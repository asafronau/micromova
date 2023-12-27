package dev.safronau.micromova.gaebackend.controllers;

import dev.safronau.micromova.gaebackend.auth.Annotations.CurrentUserId;
import dev.safronau.micromova.gaebackend.converters.Converters;
import dev.safronau.micromova.gaebackend.services.GoogleStorage;
import dev.safronau.micromova.proto.Collection;
import dev.safronau.micromova.proto.CreateCollectionRequest;
import dev.safronau.micromova.proto.CreateCollectionResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.exceptions.HttpStatusException;
import io.micronaut.protobuf.codec.ProtobufferCodec;
import io.micronaut.runtime.http.scope.RequestScope;
import jakarta.inject.Inject;

@RequestScope
@Controller("/api/collection/create")
public class CreateCollection {

  private final GoogleStorage googleStorage;
  private final String userId;

  @Inject
  CreateCollection(GoogleStorage googleStorage, @CurrentUserId String userId) {
    this.googleStorage = googleStorage;
    this.userId = userId;
  }

  @Post(processes = ProtobufferCodec.PROTOBUFFER_ENCODED)
  CreateCollectionResponse execute(@Body CreateCollectionRequest request) {
    if (!googleStorage
        .readCollectionFile(userId, request.getName())
        .equals(Collection.getDefaultInstance())) {
      throw new HttpStatusException(HttpStatus.BAD_REQUEST, "collection already exists");
    }
    Collection collection =
        Collection.newBuilder()
            .setName(request.getName())
            .setUserId(userId)
            .setSourceLanguage(request.getSourceLanguage())
            .setTranslationLanguage(request.getTranslationLanguage())
            .setDiscoverPoints(request.getDiscoverPoints())
            .setIsDiscoverEnabled(true)
            .setCurrentDiscoverPoints(0)
            .build();
    return CreateCollectionResponse.newBuilder()
        .setCollection(Converters.from(googleStorage.writeCollectionFile(collection)))
        .build();
  }
}
