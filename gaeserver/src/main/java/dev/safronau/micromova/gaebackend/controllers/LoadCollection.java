package dev.safronau.micromova.gaebackend.controllers;

import dev.safronau.micromova.gaebackend.auth.Annotations.CurrentUserId;
import dev.safronau.micromova.gaebackend.converters.Converters;
import dev.safronau.micromova.gaebackend.services.GoogleStorage;
import dev.safronau.micromova.proto.Collection;
import dev.safronau.micromova.proto.LoadCollectionRequest;
import dev.safronau.micromova.proto.LoadCollectionResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.exceptions.HttpStatusException;
import io.micronaut.protobuf.codec.ProtobufferCodec;
import io.micronaut.runtime.http.scope.RequestScope;
import jakarta.inject.Inject;

@RequestScope
@Controller("/api/collection/load")
public class LoadCollection {

  private final GoogleStorage googleStorage;
  private final String userId;

  @Inject
  LoadCollection(GoogleStorage googleStorage, @CurrentUserId String userId) {
    this.googleStorage = googleStorage;
    this.userId = userId;
  }

  @Post(consumes = ProtobufferCodec.PROTOBUFFER_ENCODED, produces = MediaType.TEXT_PLAIN)
  byte[] execute(@Body LoadCollectionRequest request) {
    Collection collection =
        googleStorage.readCollectionFile(/* userId= */ userId, /* name= */ request.getName());
    if (collection.equals(Collection.getDefaultInstance())) {
      throw new HttpStatusException(HttpStatus.BAD_REQUEST, "collection is missing");
    }
    LoadCollectionResponse response =
        LoadCollectionResponse.newBuilder().setCollection(Converters.from(collection)).build();
    return response.toByteArray();
  }
}
