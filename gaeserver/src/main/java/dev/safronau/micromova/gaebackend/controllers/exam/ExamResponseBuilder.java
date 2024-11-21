package dev.safronau.micromova.gaebackend.controllers.exam;

import dev.safronau.micromova.proto.Collection;
import dev.safronau.micromova.proto.Exam;
import dev.safronau.micromova.proto.GenerateExamResponse;
import dev.safronau.micromova.proto.Phrase;
import dev.safronau.micromova.proto.RecentScores;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Property;
import io.micronaut.core.annotation.ReflectiveAccess;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Bean
public class ExamResponseBuilder {
  @Property(name = "mova.max_tasks")
  @ReflectiveAccess
  private int maxTasks;

  @Property(name = "mova.storage_bucket")
  @ReflectiveAccess
  private String storageBucket;

  public GenerateExamResponse build(
      Collection collection, Clock clock, String timezone, boolean isOggCapable) {
    Exam exam =
        ExamGenerator.generate(collection, clock.instant(), maxTasks, storageBucket, isOggCapable);
    LocalDate localDate = LocalDate.now(clock.withZone(ZoneId.of(timezone)));
    ZonedDateTime todayTime = localDate.atStartOfDay(ZoneId.of(timezone));
    ZonedDateTime weekTime = todayTime.minusDays(6);
    int todayDiscoveredPhrases = 0;
    int weekDiscoveredPhrases = 0;
    for (int i = collection.getRecentDiscoverPhraseEventTimeCount() - 1; i >= 0; i--) {
      int timestamp = collection.getRecentDiscoverPhraseEventTimeList().get(i);
      Instant eventTime = Instant.ofEpochSecond(timestamp);
      if (eventTime.isAfter(todayTime.toInstant())) {
        todayDiscoveredPhrases++;
      }
      if (eventTime.isAfter(weekTime.toInstant())) {
        weekDiscoveredPhrases++;
      } else {
        break;
      }
    }
    int todayScore = 0;
    for (int i = collection.getRecentScoreEventTimeCount() - 1; i >= 0; i--) {
      RecentScores score = collection.getRecentScoreEventTime(i);
      Instant eventTime = Instant.ofEpochSecond(score.getTimestamp());
      if (eventTime.isAfter(todayTime.toInstant())) {
        todayScore += score.getScore();
      }
    }
    int uniquePhrasesToday = 0;
    int uniquePhrasesWeek = 0;
    List<Integer> scores = new ArrayList<>();
    for (Phrase phrase : ExamGenerator.getSelectablePhrases(collection)) {
      scores.add(phrase.getScore());
      if (Instant.ofEpochSecond(phrase.getSuccessTimeSeconds()).isAfter(todayTime.toInstant())) {
        uniquePhrasesToday++;
      }
      if (Instant.ofEpochSecond(phrase.getSuccessTimeSeconds()).isAfter(weekTime.toInstant())) {
        uniquePhrasesWeek++;
      }
    }
    Collections.sort(scores);
    int recentScoreCount = Math.min(scores.size(), 50);
    int totalUndiscoveredPhrases = 0;
    int totalOpenedPhrases = 0;
    for (Phrase phrase : collection.getPhraseList()) {
      if (phrase.getIsDiscoverable()) {
        totalUndiscoveredPhrases++;
      } else {
        totalOpenedPhrases++;
      }
    }
    return GenerateExamResponse.newBuilder()
        .setExam(exam)
        .setTodayScore(todayScore)
        .setTodayDiscoveredPhrases(todayDiscoveredPhrases)
        .setWeekDiscoveredPhrases(weekDiscoveredPhrases)
        .setUniquePhrasesToday(uniquePhrasesToday)
        .setUniquePhrasesWeek(uniquePhrasesWeek)
        .setTotalUndiscoveredPhrases(totalUndiscoveredPhrases)
        .setTotalOpenedPhrases(totalOpenedPhrases)
        .setStarScoreMillis(
            ExamGenerator.getStars(
                scores.stream().limit(recentScoreCount).mapToInt(Integer::intValue).sum()
                    / recentScoreCount))
        .build();
  }
}
