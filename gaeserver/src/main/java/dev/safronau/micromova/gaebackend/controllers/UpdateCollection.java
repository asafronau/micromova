package dev.safronau.micromova.gaebackend.controllers;

import dev.safronau.micromova.gaebackend.auth.Annotations.CurrentUserId;
import dev.safronau.micromova.gaebackend.converters.Converters;
import dev.safronau.micromova.gaebackend.services.GoogleStorage;
import dev.safronau.micromova.proto.Collection;
import dev.safronau.micromova.proto.UpdateCollectionRequest;
import dev.safronau.micromova.proto.UpdateCollectionResponse;
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
@Controller("/api/collection/update")
public class UpdateCollection {
  private final GoogleStorage googleStorage;
  private final String userId;

  @Inject
  UpdateCollection(GoogleStorage googleStorage, @CurrentUserId String userId) {
    this.googleStorage = googleStorage;
    this.userId = userId;
  }

  @Post(consumes = ProtobufferCodec.PROTOBUFFER_ENCODED, produces = MediaType.TEXT_PLAIN)
  byte[] execute(@Body UpdateCollectionRequest request) {
    Collection collection =
        googleStorage.readCollectionFile(/* userId= */ userId, /* name= */ request.getName());
    if (collection.equals(Collection.getDefaultInstance())) {
      throw new HttpStatusException(HttpStatus.BAD_REQUEST, "collection is missing");
    }
    Collection.Builder builder = collection.toBuilder();
    builder.setIsDiscoverEnabled(request.getIsDiscoverEnabled());
    builder.setDiscoverPoints(request.getDiscoverPoints());
    return UpdateCollectionResponse.newBuilder()
        .setCollection(Converters.from(googleStorage.writeCollectionFile(builder.build())))
        .build()
        .toByteArray();
  }
}
