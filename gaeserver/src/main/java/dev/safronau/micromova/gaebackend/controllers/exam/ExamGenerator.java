package dev.safronau.micromova.gaebackend.controllers.exam;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import dev.safronau.micromova.proto.Collection;
import dev.safronau.micromova.proto.Exam;
import dev.safronau.micromova.proto.Format;
import dev.safronau.micromova.proto.Phrase;
import dev.safronau.micromova.proto.Recording;
import dev.safronau.micromova.proto.SelectTranslationTest;
import dev.safronau.micromova.proto.SelectedOptions;
import dev.safronau.micromova.proto.SpellTest;
import dev.safronau.micromova.proto.TestType;
import dev.safronau.micromova.proto.Translation;
import dev.safronau.micromova.proto.TypeSourceTest;
import dev.safronau.micromova.proto.TypeTranslationTest;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class ExamGenerator {
  private static final int CORRECT_ANSWER_SCORE = 1000;
  private static final int MAX_PHRASE_SCORE = CORRECT_ANSWER_SCORE * 100;
  private static final Duration ONE_DAY = Duration.ofDays(1);

  public static Exam generate(
      Collection collection, Instant now, int maxTasks, String storageBucket) {
    long nowSeconds = now.getEpochSecond();
    ImmutableList<Phrase> selectedPhrases =
        getSelectablePhrases(collection).stream()
            .sorted(
                (w1, w2) -> {
                  long w1Days = (nowSeconds - w1.getSuccessTimeSeconds()) / ONE_DAY.toSeconds();
                  long w2Days = (nowSeconds - w2.getSuccessTimeSeconds()) / ONE_DAY.toSeconds();
                  double w1Score = w1.getScore() / (1 + Math.sqrt(w1Days) / 7);
                  double w2Score = w2.getScore() / (1 + Math.sqrt(w2Days) / 7);
                  return Double.compare(w1Score, w2Score);
                })
            .collect(ImmutableList.toImmutableList());
    ImmutableList<String> sourceStrings =
        collection.getPhraseList().stream()
            .map(Phrase::getNormalizedText)
            .distinct()
            .collect(ImmutableList.toImmutableList());
    ImmutableList<String> translationStrings =
        collection.getPhraseList().stream()
            .flatMap(phrase -> phrase.getTranslationList().stream())
            .map(Translation::getText)
            .distinct()
            .collect(ImmutableList.toImmutableList());
    int numTasks = Math.min(maxTasks, selectedPhrases.size());
    List<Integer> taskNumbers = IntStream.range(0, numTasks).boxed().collect(Collectors.toList());
    Collections.shuffle(taskNumbers);
    Exam.Builder builder =
        Exam.newBuilder()
            .addAllOrder(taskNumbers)
            .addAllSourcePhrases(sourceStrings)
            .addAllTranslationPhrases(translationStrings);
    for (int i = 0; i < numTasks; i++) {
      Phrase phrase = selectedPhrases.get(i);
      switch (getTestType(phrase)) {
        case SELECT_TRANSLATION_SHOW_PHRASE -> builder.addSelectTranslationTests(
            getSelectTranslationTest(phrase, true, selectedPhrases, storageBucket));
        case SELECT_TRANSLATION_HIDE_PHRASE -> builder.addSelectTranslationTests(
            getSelectTranslationTest(phrase, false, selectedPhrases, storageBucket));
        case SPELL -> builder.addSpellTests(getSpellTest(phrase, storageBucket));
        case TYPE_TRANSLATION_SHOW_PHRASE -> builder.addTypeTranslationTests(
            getTypeTranslationTest(phrase, true, storageBucket));
        case TYPE_TRANSLATION_HIDE_PHRASE -> builder.addTypeTranslationTests(
            getTypeTranslationTest(phrase, false, storageBucket));
        case TYPE_SOURCE -> builder.addTypeSourceTests(getTypeSourceTest(phrase, selectedPhrases));
        default -> throw new IllegalStateException("unreachable");
      }
    }
    return builder.build();
  }

  public static ImmutableList<Phrase> getSelectablePhrases(Collection collection) {
    return collection.getPhraseList().stream()
        .filter(Predicate.not(Phrase::getIsDiscoverable))
        .collect(ImmutableList.toImmutableList());
  }

  private static TestType getTestType(Phrase phrase) {
    if (phrase.getCorrectAnswersDelta() < 3) {
      return TestType.SELECT_TRANSLATION_SHOW_PHRASE;
    } else if (phrase.getScore() > 20 * CORRECT_ANSWER_SCORE) {
      double random = rand();
      if (random <= 0.2) {
        return TestType.TYPE_TRANSLATION_SHOW_PHRASE;
      } else if (random <= 0.4) {
        return TestType.TYPE_TRANSLATION_HIDE_PHRASE;
      } else {
        return TestType.TYPE_SOURCE;
      }
    }
    double magic = rand() / Math.max(1.0, 1.0 / (0.0001 * phrase.getScore()));
    int numCorrectAnswers = phrase.getScore() / CORRECT_ANSWER_SCORE;
    if (numCorrectAnswers > 10) {
      magic *= Math.log10(numCorrectAnswers);
    }
    if (magic > 0.7) {
      double anotherMagic = rand();
      if (anotherMagic <= 0.33) {
        return TestType.TYPE_TRANSLATION_SHOW_PHRASE;
      } else if (anotherMagic <= 0.66) {
        return TestType.TYPE_TRANSLATION_HIDE_PHRASE;
      } else {
        return TestType.TYPE_SOURCE;
      }
    } else if (magic > 0.5) {
      return TestType.SELECT_TRANSLATION_HIDE_PHRASE;
    } else if (magic > 0.2) {
      return TestType.SPELL;
    } else if (magic > 0.1) {
      return TestType.SELECT_TRANSLATION_HIDE_PHRASE;
    }
    return TestType.SELECT_TRANSLATION_SHOW_PHRASE;
  }

  public static int getStars(int score) {
    if (score == 0) {
      return 0;
    }
    double n = Math.pow(score / 1000.0, 0.5) / 2;
    return (int) Math.min(Math.round(n * 1000), 3000);
  }

  private static TypeSourceTest getTypeSourceTest(Phrase phrase, List<Phrase> selectablePhrases) {
    int userScore = (int) (0.8 * CORRECT_ANSWER_SCORE + rand() * CORRECT_ANSWER_SCORE / 5);
    String translation = selectCorrectOption(phrase);
    Set<String> answers = new HashSet<>();
    for (Phrase p : selectablePhrases) {
      if (ImmutableSet.copyOf(getTranslationStrings(p)).contains(translation)) {
        answers.add(p.getNormalizedText());
      }
    }
    return TypeSourceTest.newBuilder()
        .setId(phrase.getId())
        .setTranslation(translation)
        .addAllCorrectSources(shuffle(answers))
        .setCorrectScoreUser(getUserPoints(25, phrase.getScore()))
        .setWrongScoreUser(-10)
        .setCorrectScorePhrase(Math.min(userScore + phrase.getScore(), MAX_PHRASE_SCORE))
        .setWrongScorePhrase((int) (phrase.getScore() / 1.25))
        .setExample(phrase.getExample())
        .setMp3Url("")
        .build();
  }

  private static TypeTranslationTest getTypeTranslationTest(
      Phrase phrase, boolean isShow, String storageBucket) {
    int userScore = (int) (0.8 * CORRECT_ANSWER_SCORE + rand() * CORRECT_ANSWER_SCORE / 5);
    int taskPoints = isShow ? 15 : 20;
    return TypeTranslationTest.newBuilder()
        .setId(phrase.getId())
        .setPhrase(phrase.getNormalizedText())
        .setIsPhraseHidden(!isShow)
        .addAllCorrectTranslations(shuffle(getTranslationStrings(phrase)))
        .setCorrectScoreUser(getUserPoints(taskPoints, phrase.getScore()))
        .setWrongScoreUser(-10)
        .setCorrectScorePhrase(Math.min(userScore + phrase.getScore(), MAX_PHRASE_SCORE))
        .setWrongScorePhrase((int) (phrase.getScore() / 1.25))
        .setExample(phrase.getExample())
        .setMp3Url(getMp3Url(phrase, storageBucket))
        .build();
  }

  private static <T> List<T> shuffle(java.util.Collection<T> items) {
    List<T> copy = new ArrayList<>(items);
    Collections.shuffle(copy);
    return copy;
  }

  private static SpellTest getSpellTest(Phrase phrase, String storageBucket) {
    int userScore = (int) (0.8 * CORRECT_ANSWER_SCORE + rand() * CORRECT_ANSWER_SCORE / 5);
    List<String> translations = getTranslationStrings(phrase);
    return SpellTest.newBuilder()
        .setId(phrase.getId())
        .setPhrase(phrase.getNormalizedText())
        .setCorrectScoreUser(getUserPoints(15, phrase.getScore()))
        .setWrongScoreUser(-10)
        .setCorrectScorePhrase(Math.min(userScore + phrase.getScore(), MAX_PHRASE_SCORE))
        .setWrongScorePhrase((int) (phrase.getScore() / 1.25))
        .setExample(phrase.getExample())
        .setTranslation(translations.get(ThreadLocalRandom.current().nextInt(translations.size())))
        .setMp3Url(getMp3Url(phrase, storageBucket))
        .build();
  }

  private static SelectTranslationTest getSelectTranslationTest(
      Phrase phrase, boolean isShow, ImmutableList<Phrase> phrases, String storageBucket) {
    String correctTranslation = selectCorrectOption(phrase);
    ImmutableSet<String> correctOptions = ImmutableSet.copyOf(getTranslationStrings(phrase));
    Map<String, Boolean> options = new HashMap<>(Map.of(correctTranslation, true));
    List<Phrase> shuffledPhrases =
        phrases.stream().filter(p -> p.getId() != phrase.getId()).collect(Collectors.toList());
    Collections.shuffle(shuffledPhrases);
    List<String> translations = new ArrayList<>(List.of(correctTranslation));
    for (int i = 0; i < shuffledPhrases.size() && options.size() < 5; i++) {
      Phrase candidate = shuffledPhrases.get(i);
      String translation =
          candidate
              .getTranslation(ThreadLocalRandom.current().nextInt(candidate.getTranslationCount()))
              .getText();
      if (!options.containsKey(translation)) {
        options.put(translation, correctOptions.contains(translation));
        translations.add(translation);
      }
    }
    int userScore = (int) (0.8 * CORRECT_ANSWER_SCORE + rand() * CORRECT_ANSWER_SCORE / 5);
    int taskPoints = isShow ? 10 : 12;
    return SelectTranslationTest.newBuilder()
        .setId(phrase.getId())
        .setPhrase(phrase.getNormalizedText())
        .setIsPhraseHidden(!isShow)
        .setIsNewPhrase(phrase.getCorrectAnswersDelta() < 3)
        .addAllOptions(buildSelectedOptions(options, translations))
        .setCorrectScoreUser(getUserPoints(taskPoints, phrase.getScore()))
        .setWrongScoreUser(-10)
        .setCorrectScorePhrase(Math.min(userScore + phrase.getScore(), MAX_PHRASE_SCORE))
        .setWrongScorePhrase((int) (phrase.getScore() / 1.25))
        .setExample(phrase.getExample())
        .setMp3Url(getMp3Url(phrase, storageBucket))
        .build();
  }

  private static List<SelectedOptions> buildSelectedOptions(
      Map<String, Boolean> options, List<String> translations) {
    List<SelectedOptions> selectedOptions =
        translations.stream()
            .map(
                translation ->
                    SelectedOptions.newBuilder()
                        .setTranslation(translation)
                        .setIsCorrect(options.get(translation))
                        .build())
            .collect(Collectors.toList());
    Collections.shuffle(selectedOptions);
    return selectedOptions;
  }

  private static int getUserPoints(int taskPoints, int taskScore) {
    return (int) Math.round(taskPoints * (18000.0 / Math.max(taskScore, 18000)));
  }

  private static String selectCorrectOption(Phrase phrase) {
    ImmutableList<String> translations = getTranslationStrings(phrase);
    for (String option : translations) {
      if (rand() < 0.67) {
        return option;
      }
    }
    return translations.get(translations.size() - 1);
  }

  private static ImmutableList<String> getTranslationStrings(Phrase phrase) {
    return phrase.getTranslationList().stream()
        .map(Translation::getText)
        .collect(ImmutableList.toImmutableList());
  }

  private static String getMp3Url(Phrase phrase, String storageBucket) {
    List<String> urls =
        phrase.getRecordingList().stream()
            .filter(recording -> recording.getFormat().equals(Format.MP3))
            .map(Recording::getUri)
            .toList();
    return storageBucket + "/" + urls.get(ThreadLocalRandom.current().nextInt(urls.size()));
  }

  private static double rand() {
    return ThreadLocalRandom.current().nextDouble();
  }

  private ExamGenerator() {}
}
