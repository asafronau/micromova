package dev.safronau.micromova.gaebackend.controllers;

import dev.safronau.micromova.gaebackend.auth.Annotations.CurrentUserId;
import dev.safronau.micromova.gaebackend.controllers.exam.ExamResponseBuilder;
import dev.safronau.micromova.gaebackend.services.GoogleStorage;
import dev.safronau.micromova.proto.ApplyExamResultRequest;
import dev.safronau.micromova.proto.Collection;
import dev.safronau.micromova.proto.Phrase;
import dev.safronau.micromova.proto.RecentScores;
import dev.safronau.micromova.proto.TaskResult;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.protobuf.codec.ProtobufferCodec;
import io.micronaut.runtime.http.scope.RequestScope;
import jakarta.inject.Inject;
import java.time.Clock;
import java.util.HashMap;
import java.util.Map;

@RequestScope
@Controller("/api/exam/apply")
public class ApplyExam {
  private final GoogleStorage googleStorage;
  private final String userId;
  private final Clock clock;
  private final ExamResponseBuilder examResponseBuilder;

  @Inject
  ApplyExam(
      GoogleStorage googleStorage,
      @CurrentUserId String userId,
      Clock clock,
      ExamResponseBuilder examResponseBuilder) {
    this.googleStorage = googleStorage;
    this.userId = userId;
    this.clock = clock;
    this.examResponseBuilder = examResponseBuilder;
  }

  @Post(consumes = ProtobufferCodec.PROTOBUFFER_ENCODED, produces = MediaType.TEXT_PLAIN)
  byte[] execute(@Body ApplyExamResultRequest request) {
    Collection.Builder collectionBuilder =
        googleStorage.readCollectionFile(userId, request.getCollectionName()).toBuilder();

    Map<Long, TaskResult> results = new HashMap<>();
    boolean allCorrect = true;

    for (TaskResult result : request.getTaskResultsList()) {
      results.put(result.getId(), result);
      allCorrect = allCorrect && result.getIsCorrectAnswer();
    }

    int userScore = 0;
    int currentSec = (int) clock.instant().getEpochSecond();
    for (Phrase.Builder builder : collectionBuilder.getPhraseBuilderList()) {
      if (results.containsKey(builder.getId())) {
        TaskResult result = results.get(builder.getId());
        builder.setScore(result.getPhraseScore());
        userScore += result.getUserScore();
        if (result.getIsCorrectAnswer()) {
          builder.setCorrectAnswersDelta(builder.getCorrectAnswersDelta() + 1);
          builder.setSuccessTimeSeconds(currentSec);
        } else {
          builder.setCorrectAnswersDelta(builder.getCorrectAnswersDelta() - 1);
        }
      }
    }
    if (allCorrect) {
      userScore = (int) (userScore * 1.1);
    }
    collectionBuilder.addRecentScoreEventTime(
        RecentScores.newBuilder().setScore(userScore).setTimestamp(currentSec));

    if (collectionBuilder.getIsDiscoverEnabled() && collectionBuilder.getDiscoverPoints() > 0) {
      collectionBuilder.setCurrentDiscoverPoints(
          collectionBuilder.getCurrentDiscoverPoints() + userScore);
      if (collectionBuilder.getCurrentDiscoverPoints() >= collectionBuilder.getDiscoverPoints()) {
        collectionBuilder.setCurrentDiscoverPoints(
            collectionBuilder.getCurrentDiscoverPoints() % collectionBuilder.getDiscoverPoints());
        for (Phrase.Builder builder : collectionBuilder.getPhraseBuilderList()) {
          if (builder.getIsDiscoverable()) {
            builder.setIsDiscoverable(false);
            collectionBuilder.addRecentDiscoverPhraseEventTime(currentSec);
            break;
          }
        }
      }
    }
    Collection collection = googleStorage.writeCollectionFile(collectionBuilder.build());
    return examResponseBuilder
        .build(collection, clock, request.getTimezone(), request.getIsOggCapable())
        .toByteArray();
  }
}
