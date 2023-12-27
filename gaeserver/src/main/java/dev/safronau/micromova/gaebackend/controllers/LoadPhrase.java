package dev.safronau.micromova.gaebackend.controllers;

import com.google.common.collect.MoreCollectors;
import dev.safronau.micromova.gaebackend.auth.Annotations.CurrentUserId;
import dev.safronau.micromova.gaebackend.services.GoogleStorage;
import dev.safronau.micromova.proto.Collection;
import dev.safronau.micromova.proto.LoadPhraseRequest;
import dev.safronau.micromova.proto.LoadPhraseResponse;
import dev.safronau.micromova.proto.Phrase;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.exceptions.HttpStatusException;
import io.micronaut.protobuf.codec.ProtobufferCodec;
import io.micronaut.runtime.http.scope.RequestScope;
import jakarta.inject.Inject;

@RequestScope
@Controller("/api/collection/loadphrase")
public class LoadPhrase {

  private final GoogleStorage googleStorage;
  private final String userId;

  @Inject
  LoadPhrase(GoogleStorage googleStorage, @CurrentUserId String userId) {
    this.googleStorage = googleStorage;
    this.userId = userId;
  }

  @Post(processes = ProtobufferCodec.PROTOBUFFER_ENCODED)
  LoadPhraseResponse execute(@Body LoadPhraseRequest request) {
    Collection collection =
        googleStorage.readCollectionFile(
            /* userId= */ userId, /* name= */ request.getCollectionName());
    if (collection.equals(Collection.getDefaultInstance())) {
      throw new HttpStatusException(HttpStatus.BAD_REQUEST, "collection is missing");
    }
    Phrase phrase =
        collection.getPhraseList().stream()
            .filter(p -> p.getId() == request.getId())
            .collect(MoreCollectors.onlyElement());
    return LoadPhraseResponse.newBuilder().setPhrase(phrase).build();
  }
}
