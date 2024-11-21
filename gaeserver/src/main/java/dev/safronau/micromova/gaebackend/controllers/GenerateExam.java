package dev.safronau.micromova.gaebackend.controllers;

import dev.safronau.micromova.gaebackend.auth.Annotations.CurrentUserId;
import dev.safronau.micromova.gaebackend.controllers.exam.ExamResponseBuilder;
import dev.safronau.micromova.gaebackend.services.GoogleStorage;
import dev.safronau.micromova.proto.Collection;
import dev.safronau.micromova.proto.GenerateExamRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.exceptions.HttpStatusException;
import io.micronaut.protobuf.codec.ProtobufferCodec;
import io.micronaut.runtime.http.scope.RequestScope;
import jakarta.inject.Inject;
import java.time.Clock;

@RequestScope
@Controller("/api/exam/generate")
public class GenerateExam {
  private final GoogleStorage googleStorage;
  private final String userId;
  private final Clock clock;
  private final ExamResponseBuilder examResponseBuilder;

  @Inject
  GenerateExam(
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
  byte[] execute(@Body GenerateExamRequest request) {
    Collection collection = googleStorage.readCollectionFile(userId, request.getCollectionName());
    if (collection.equals(Collection.getDefaultInstance())) {
      throw new HttpStatusException(HttpStatus.BAD_REQUEST, "collection is missing");
    }
    return examResponseBuilder
        .build(collection, clock, request.getTimezone(), request.getIsOggCapable())
        .toByteArray();
  }
}
