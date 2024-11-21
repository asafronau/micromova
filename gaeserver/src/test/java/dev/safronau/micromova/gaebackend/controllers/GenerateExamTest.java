package dev.safronau.micromova.gaebackend.controllers;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.google.common.collect.MoreCollectors;
import dev.safronau.micromova.gaebackend.services.StorePaths;
import dev.safronau.micromova.proto.Collection;
import dev.safronau.micromova.proto.Exam;
import dev.safronau.micromova.proto.Format;
import dev.safronau.micromova.proto.GenerateExamRequest;
import dev.safronau.micromova.proto.GenerateExamResponse;
import dev.safronau.micromova.proto.Language;
import dev.safronau.micromova.proto.Phrase;
import dev.safronau.micromova.proto.RecentScores;
import dev.safronau.micromova.proto.Recording;
import dev.safronau.micromova.proto.SelectTranslationTest;
import dev.safronau.micromova.proto.SelectedOptions;
import dev.safronau.micromova.proto.SpellTest;
import dev.safronau.micromova.proto.Translation;
import dev.safronau.micromova.proto.Type;
import dev.safronau.micromova.proto.TypeSourceTest;
import dev.safronau.micromova.proto.TypeTranslationTest;
import dev.safronau.micromova.proto.VoiceGender;
import io.micronaut.context.annotation.Property;
import io.micronaut.core.annotation.ReflectiveAccess;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

@MicronautTest
public class GenerateExamTest extends CommonControllerTest {
  @Property(name = "mova.storage_bucket")
  @ReflectiveAccess
  private String storageBucket;

  private static final String URL = "/api/exam/generate";

  private static final String COLLECTION_NAME = "large collection";

  private static final GenerateExamRequest REQUEST =
      GenerateExamRequest.newBuilder()
          .setCollectionName(COLLECTION_NAME)
          .setTimezone("America/Los_Angeles")
          .setIsOggCapable(true)
          .build();

  @Inject Clock mockClock;

  @MockBean(Clock.class)
  Clock provideClock() {
    return Clock.fixed(Instant.parse("2021-02-01T00:01:00Z"), ZoneId.of("America/Los_Angeles"));
  }

  @Test
  void missingAuthCookie_error() {
    HttpRequest<byte[]> request =
        HttpRequest.POST(URL, REQUEST.toByteArray())
            .header("Content-Type", "application/x-protobuf");
    HttpClientResponseException exception =
        assertThrows(
            HttpClientResponseException.class, () -> client.toBlocking().retrieve(request));
    assertThat(exception.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }

  @Test
  void largeCollection() throws Exception {
    Collection.Builder builder =
        Collection.newBuilder()
            .setName(COLLECTION_NAME)
            .setDiscoverPoints(500)
            .setSourceLanguage(Language.DE)
            .setTranslationLanguage(Language.EN_US)
            .setUserId(FAKE_USER_ID);
    for (int i = 1; i <= 50; i++) {
      Phrase phrase =
          Phrase.newBuilder()
              .setId(i)
              .setNormalizedText(String.format("phrase %02d", i))
              .setSourceLanguage(Language.DE)
              .addTranslation(
                  Translation.newBuilder()
                      .setLanguage(Language.EN_US)
                      .setText(String.format("translation %02d", i)))
              .setSuccessTimeSeconds(
                  (int) Instant.parse("2021-02-01T00:01:00Z").getEpochSecond()
                      - (i - 1) * 60 * 60 * 24)
              .setScore((i - 1) * 1000)
              .setCorrectAnswersDelta(i - 1)
              .addRecording(
                  Recording.newBuilder()
                      .setVoiceGender(VoiceGender.FEMALE)
                      .setFormat(Format.MP3)
                      .setUri("audio" + i + ".mp3")
                      .setType(Type.GOOGLE_TEXT_TO_SPEECH))
              .build();
      builder.addPhrase(phrase);
    }
    for (int i = 7; i >= 0; i--) {
      int timestamp = (int) mockClock.instant().getEpochSecond() - i * 60 * 60 * 24;
      builder.addRecentDiscoverPhraseEventTime(timestamp);
      builder.addRecentScoreEventTime(
          RecentScores.newBuilder().setTimestamp(timestamp).setScore(100));
    }
    Collection collection = builder.build();
    fakeFileStorage.insertFile(
        collection.toByteArray(),
        StorePaths.buildCollectionPath(FAKE_USER_ID, collection.getName()));

    GenerateExamResponse response =
        GenerateExamResponse.parseFrom(
            client.toBlocking().retrieve(buildRequest(URL, REQUEST), byte[].class));

    Exam exam = response.getExam();
    assertThat(response.getStarScoreMillis()).isEqualTo(2475);
    assertThat(response.getTotalOpenedPhrases()).isEqualTo(50);
    assertThat(response.getTodayScore()).isEqualTo(100);
    assertThat(response.getTodayDiscoveredPhrases()).isEqualTo(1);
    assertThat(response.getWeekDiscoveredPhrases()).isEqualTo(7);
    assertThat(response.getUniquePhrasesToday()).isEqualTo(1);
    assertThat(response.getUniquePhrasesWeek()).isEqualTo(7);

    List<Long> testIds =
        Stream.of(
                exam.getSelectTranslationTestsList().stream().map(SelectTranslationTest::getId),
                exam.getSpellTestsList().stream().map(SpellTest::getId),
                exam.getTypeTranslationTestsList().stream().map(TypeTranslationTest::getId),
                exam.getTypeSourceTestsList().stream().map(TypeSourceTest::getId))
            .flatMap(Function.identity())
            .toList();
    assertThat(testIds).containsExactlyElementsIn(LongStream.range(1, 41).boxed().toList());
    assertThat(exam.getOrderList())
        .containsExactlyElementsIn(IntStream.range(0, 40).boxed().toList());
    assertThat(exam.getSourcePhrasesCount()).isEqualTo(50);
    assertThat(exam.getTranslationPhrasesCount()).isEqualTo(50);

    exam.getSelectTranslationTestsList()
        .forEach(test -> validateSelectTranslationTest(test, collection));
    exam.getSpellTestsList().forEach(test -> validateSpellTest(test, collection));
    exam.getTypeTranslationTestsList()
        .forEach(test -> validateTypeTranslationTest(test, collection));
    exam.getTypeSourceTestsList().forEach(test -> validateTypeSourceTest(test, collection));
  }

  private void validateSelectTranslationTest(SelectTranslationTest test, Collection collection) {
    Phrase phrase = findPhrase(collection, test.getId());

    assertThat(test.getPhrase()).isEqualTo(phrase.getNormalizedText());
    assertThat(test.getMp3Url())
        .isEqualTo(String.join("/", storageBucket, phrase.getRecording(0).getUri()));
    assertThat(test.getOptionsCount()).isEqualTo(5);
    List<String> correctTranslations =
        test.getOptionsList().stream()
            .filter(SelectedOptions::getIsCorrect)
            .map(SelectedOptions::getTranslation)
            .toList();
    assertThat(correctTranslations).isNotEmpty();
    assertThat(phrase.getTranslationList().stream().map(Translation::getText).toList())
        .containsAnyIn(correctTranslations);
    assertThat(test.getWrongScoreUser()).isEqualTo(-10);
    assertThat(test.getCorrectScoreUser()).isAtMost(test.getIsPhraseHidden() ? 12 : 10);
    assertThat(test.getWrongScorePhrase()).isEqualTo((int) (phrase.getScore() / 1.25));
    assertThat(test.getCorrectScorePhrase()).isAtMost(phrase.getScore() + 1000);
    assertThat(test.getCorrectScorePhrase()).isAtLeast(phrase.getScore() + 800);
  }

  private void validateSpellTest(SpellTest test, Collection collection) {
    Phrase phrase = findPhrase(collection, test.getId());

    assertThat(test.getPhrase()).isEqualTo(phrase.getNormalizedText());
    assertThat(test.getMp3Url())
        .isEqualTo(String.join("/", storageBucket, phrase.getRecording(0).getUri()));
    assertThat(test.getTranslation())
        .isIn(phrase.getTranslationList().stream().map(Translation::getText).toList());
    assertThat(test.getWrongScoreUser()).isEqualTo(-10);
    assertThat(test.getCorrectScoreUser()).isAtMost(15);
    assertThat(test.getWrongScorePhrase()).isEqualTo((int) (phrase.getScore() / 1.25));
    assertThat(test.getCorrectScorePhrase()).isAtMost(phrase.getScore() + 1000);
    assertThat(test.getCorrectScorePhrase()).isAtLeast(phrase.getScore() + 800);
  }

  private void validateTypeTranslationTest(TypeTranslationTest test, Collection collection) {
    Phrase phrase = findPhrase(collection, test.getId());

    assertThat(test.getPhrase()).isEqualTo(phrase.getNormalizedText());
    assertThat(test.getMp3Url())
        .isEqualTo(String.join("/", storageBucket, phrase.getRecording(0).getUri()));
    assertThat(phrase.getTranslationList().stream().map(Translation::getText).toList())
        .containsAnyIn(test.getCorrectTranslationsList());
    assertThat(test.getWrongScoreUser()).isEqualTo(-10);
    assertThat(test.getCorrectScoreUser()).isAtMost(test.getIsPhraseHidden() ? 20 : 15);
    assertThat(test.getWrongScorePhrase()).isEqualTo((int) (phrase.getScore() / 1.25));
    assertThat(test.getCorrectScorePhrase()).isAtMost(phrase.getScore() + 1000);
    assertThat(test.getCorrectScorePhrase()).isAtLeast(phrase.getScore() + 800);
  }

  private void validateTypeSourceTest(TypeSourceTest test, Collection collection) {
    Phrase phrase = findPhrase(collection, test.getId());

    assertThat(
            collection.getPhraseList().stream()
                .filter(p -> getTranslations(p).contains(test.getTranslation()))
                .map(Phrase::getNormalizedText)
                .toList())
        .containsExactlyElementsIn(test.getCorrectSourcesList());
    assertThat(getTranslations(phrase)).contains(test.getTranslation());
    assertThat(test.getCorrectSourcesList()).contains(phrase.getNormalizedText());
    assertThat(test.getWrongScoreUser()).isEqualTo(-10);
    assertThat(test.getCorrectScoreUser()).isAtMost(25);
    assertThat(test.getWrongScorePhrase()).isEqualTo((int) (phrase.getScore() / 1.25));
    assertThat(test.getCorrectScorePhrase()).isAtMost(phrase.getScore() + 1000);
    assertThat(test.getCorrectScorePhrase()).isAtLeast(phrase.getScore() + 800);
  }

  private List<String> getTranslations(Phrase phrase) {
    return phrase.getTranslationList().stream().map(Translation::getText).toList();
  }

  private Phrase findPhrase(Collection collection, long id) {
    return collection.getPhraseList().stream()
        .filter(phrase -> phrase.getId() == id)
        .collect(MoreCollectors.onlyElement());
  }
}
