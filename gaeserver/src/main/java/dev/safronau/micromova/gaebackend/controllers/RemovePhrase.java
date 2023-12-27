package dev.safronau.micromova.gaebackend.controllers;

import com.google.common.collect.ImmutableList;
import dev.safronau.micromova.gaebackend.auth.Annotations.CurrentUserId;
import dev.safronau.micromova.gaebackend.converters.Converters;
import dev.safronau.micromova.gaebackend.services.GoogleStorage;
import dev.safronau.micromova.proto.Collection;
import dev.safronau.micromova.proto.Phrase;
import dev.safronau.micromova.proto.RemovePhraseRequest;
import dev.safronau.micromova.proto.RemovePhraseResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.protobuf.codec.ProtobufferCodec;
import io.micronaut.runtime.http.scope.RequestScope;
import jakarta.inject.Inject;

@RequestScope
@Controller("/api/collection/removephrase")
public class RemovePhrase {
  private final GoogleStorage googleStorage;
  private final String userId;

  @Inject
  RemovePhrase(GoogleStorage googleStorage, @CurrentUserId String userId) {
    this.googleStorage = googleStorage;
    this.userId = userId;
  }

  @Post(consumes = ProtobufferCodec.PROTOBUFFER_ENCODED, produces = MediaType.TEXT_PLAIN)
  byte[] execute(@Body RemovePhraseRequest request) {
    Collection collection =
        googleStorage.readCollectionFile(
            /* userId= */ userId, /* name= */ request.getCollectionName());
    ImmutableList<Phrase> updatedPhrases =
        collection.getPhraseList().stream()
            .filter(phrase -> phrase.getId() != request.getId())
            .collect(ImmutableList.toImmutableList());

    return RemovePhraseResponse.newBuilder()
        .setCollection(
            Converters.from(
                googleStorage.writeCollectionFile(
                    collection.toBuilder().clearPhrase().addAllPhrase(updatedPhrases).build())))
        .build()
        .toByteArray();
  }
}
