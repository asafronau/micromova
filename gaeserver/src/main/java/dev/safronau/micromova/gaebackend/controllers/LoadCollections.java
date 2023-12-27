package dev.safronau.micromova.gaebackend.controllers;

import dev.safronau.micromova.gaebackend.auth.Annotations.CurrentUserId;
import dev.safronau.micromova.gaebackend.services.GoogleStorage;
import dev.safronau.micromova.proto.LoadCollectionsResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.protobuf.codec.ProtobufferCodec;
import io.micronaut.runtime.http.scope.RequestScope;
import jakarta.inject.Inject;

@RequestScope
@Controller("/api/collection/loadall")
public class LoadCollections {

  private final GoogleStorage googleStorage;
  private final String userId;

  @Inject
  LoadCollections(GoogleStorage googleStorage, @CurrentUserId String userId) {
    this.googleStorage = googleStorage;
    this.userId = userId;
  }

  @Post(processes = ProtobufferCodec.PROTOBUFFER_ENCODED)
  LoadCollectionsResponse execute() {
    return LoadCollectionsResponse.newBuilder()
        .addAllNames(googleStorage.listCollections(userId))
        .build();
  }
}
