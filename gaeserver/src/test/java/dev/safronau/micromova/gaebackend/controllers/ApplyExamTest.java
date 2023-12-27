package dev.safronau.micromova.gaebackend.controllers;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import dev.safronau.micromova.gaebackend.services.StorePaths;
import dev.safronau.micromova.proto.ApplyExamResultRequest;
import dev.safronau.micromova.proto.Collection;
import dev.safronau.micromova.proto.Exam;
import dev.safronau.micromova.proto.Format;
import dev.safronau.micromova.proto.GenerateExamResponse;
import dev.safronau.micromova.proto.Language;
import dev.safronau.micromova.proto.Phrase;
import dev.safronau.micromova.proto.RecentScores;
import dev.safronau.micromova.proto.Recording;
import dev.safronau.micromova.proto.SelectTranslationTest;
import dev.safronau.micromova.proto.SpellTest;
import dev.safronau.micromova.proto.TaskResult;
import dev.safronau.micromova.proto.Translation;
import dev.safronau.micromova.proto.Type;
import dev.safronau.micromova.proto.TypeSourceTest;
import dev.safronau.micromova.proto.TypeTranslationTest;
import dev.safronau.micromova.proto.VoiceGender;
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
public class ApplyExamTest extends CommonControllerTest {
  private static final String URL = "/api/exam/apply";

  private static final String COLLECTION_NAME = "large collection";

  private static final ApplyExamResultRequest REQUEST =
      ApplyExamResultRequest.newBuilder()
          .setCollectionName(COLLECTION_NAME)
          .setTimezone("America/Los_Angeles")
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
  void allResponsesWrong() throws Exception {
    Collection collection = createLargeCollection();

    ApplyExamResultRequest.Builder request = REQUEST.toBuilder();
    for (int i = 0; i < 50; i++) {
      request.addTaskResults(
          TaskResult.newBuilder()
              .setId(i + 1)
              .setPhraseScore(1000)
              .setUserScore(-10)
              .setIsCorrectAnswer(false));
    }

    GenerateExamResponse response =
        GenerateExamResponse.parseFrom(
            client.toBlocking().retrieve(buildRequest(URL, request.build()), byte[].class));

    Exam exam = response.getExam();
    assertThat(response.getStarScoreMillis()).isEqualTo(500);
    assertThat(response.getTotalOpenedPhrases()).isEqualTo(50);
    assertThat(response.getTodayScore()).isEqualTo(-400);
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
    assertThat(testIds).containsExactlyElementsIn(LongStream.rangeClosed(11, 50).boxed().toList());
    assertThat(exam.getOrderList())
        .containsExactlyElementsIn(IntStream.range(0, 40).boxed().toList());
    assertThat(exam.getSourcePhrasesCount()).isEqualTo(51);
    assertThat(exam.getTranslationPhrasesCount()).isEqualTo(51);

    Collection updatedCollection =
        Collection.parseFrom(
            fakeFileStorage.read(StorePaths.buildCollectionPath(FAKE_USER_ID, COLLECTION_NAME)));
    for (int i = 0; i < 50; i++) {
      Phrase oldPhrase = collection.getPhrase(i);
      Phrase updatedPhrase = updatedCollection.getPhrase(i);

      assertThat(oldPhrase.getSuccessTimeSeconds())
          .isEqualTo(updatedPhrase.getSuccessTimeSeconds());
      assertThat(oldPhrase.getCorrectAnswersDelta())
          .isEqualTo(updatedPhrase.getCorrectAnswersDelta() + 1);
      assertThat(updatedPhrase.getScore()).isEqualTo(1000);
    }
    assertThat(collection.getPhrase(50).getIsDiscoverable()).isTrue();
  }

  @Test
  void allResponsesCorrect() throws Exception {
    Collection collection = createLargeCollection();

    ApplyExamResultRequest.Builder request = REQUEST.toBuilder();
    for (int i = 0; i < 50; i++) {
      Phrase phrase = collection.getPhrase(i);
      request.addTaskResults(
          TaskResult.newBuilder()
              .setId(phrase.getId())
              .setPhraseScore(phrase.getScore() + 1000)
              .setUserScore(5)
              .setIsCorrectAnswer(true));
    }

    GenerateExamResponse response =
        GenerateExamResponse.parseFrom(
            client.toBlocking().retrieve(buildRequest(URL, request.build()), byte[].class));

    Exam exam = response.getExam();
    assertThat(response.getStarScoreMillis()).isEqualTo(2475);
    assertThat(response.getTotalOpenedPhrases()).isEqualTo(51);
    assertThat(response.getTodayScore()).isEqualTo(375);
    assertThat(response.getTodayDiscoveredPhrases()).isEqualTo(2);
    assertThat(response.getWeekDiscoveredPhrases()).isEqualTo(8);
    assertThat(response.getUniquePhrasesToday()).isEqualTo(50);
    assertThat(response.getUniquePhrasesWeek()).isEqualTo(50);

    List<Long> testIds =
        Stream.of(
                exam.getSelectTranslationTestsList().stream().map(SelectTranslationTest::getId),
                exam.getSpellTestsList().stream().map(SpellTest::getId),
                exam.getTypeTranslationTestsList().stream().map(TypeTranslationTest::getId),
                exam.getTypeSourceTestsList().stream().map(TypeSourceTest::getId))
            .flatMap(Function.identity())
            .toList();
    assertThat(testIds)
        .containsExactlyElementsIn(
            Stream.concat(LongStream.rangeClosed(1, 39).boxed(), Stream.of(123456L)).toList());
    assertThat(exam.getOrderList())
        .containsExactlyElementsIn(IntStream.range(0, 40).boxed().toList());
    assertThat(exam.getSourcePhrasesCount()).isEqualTo(51);
    assertThat(exam.getTranslationPhrasesCount()).isEqualTo(51);

    Collection updatedCollection =
        Collection.parseFrom(
            fakeFileStorage.read(StorePaths.buildCollectionPath(FAKE_USER_ID, COLLECTION_NAME)));
    for (int i = 0; i < 50; i++) {
      Phrase oldPhrase = collection.getPhrase(i);
      Phrase updatedPhrase = updatedCollection.getPhrase(i);

      assertThat(updatedPhrase.getSuccessTimeSeconds())
          .isEqualTo(mockClock.instant().getEpochSecond());
      assertThat(oldPhrase.getCorrectAnswersDelta())
          .isEqualTo(updatedPhrase.getCorrectAnswersDelta() - 1);
      assertThat(updatedPhrase.getScore()).isEqualTo(oldPhrase.getScore() + 1000);
    }
    assertThat(updatedCollection.getCurrentDiscoverPoints()).isEqualTo(5);
    assertThat(updatedCollection.getPhrase(50).getIsDiscoverable()).isFalse();
  }

  private Collection createLargeCollection() {
    Collection.Builder builder =
        Collection.newBuilder()
            .setName(COLLECTION_NAME)
            .setDiscoverPoints(270)
            .setIsDiscoverEnabled(true)
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
    builder.addPhrase(
        Phrase.newBuilder()
            .setId(123456)
            .setNormalizedText("to be discovered")
            .addTranslation(
                Translation.newBuilder().setLanguage(Language.EN_US).setText("tobediscovered"))
            .setIsDiscoverable(true)
            .addRecording(
                Recording.newBuilder()
                    .setVoiceGender(VoiceGender.FEMALE)
                    .setFormat(Format.MP3)
                    .setUri("tobediscovered.mp3")
                    .setType(Type.GOOGLE_TEXT_TO_SPEECH))
            .build());
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
    return collection;
  }
}
