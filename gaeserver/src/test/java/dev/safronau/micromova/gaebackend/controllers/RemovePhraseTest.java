package dev.safronau.micromova.gaebackend.controllers;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import dev.safronau.micromova.gaebackend.converters.Converters;
import dev.safronau.micromova.gaebackend.services.StorePaths;
import dev.safronau.micromova.proto.Collection;
import dev.safronau.micromova.proto.RemovePhraseRequest;
import dev.safronau.micromova.proto.RemovePhraseResponse;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

@MicronautTest
public class RemovePhraseTest extends CommonControllerTest {
  private static final String URL = "/api/collection/removephrase";

  private static final Collection COLLECTION =
      TestUtils.parseProto("TestCollection.textproto", Collection.class);

  private static final RemovePhraseRequest REQUEST =
      RemovePhraseRequest.newBuilder()
          .setId(COLLECTION.getPhrase(0).getId())
          .setCollectionName(COLLECTION.getName())
          .build();

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
  void phraseExists_success() throws Exception {
    fakeFileStorage.insertFile(
        COLLECTION.toByteArray(),
        StorePaths.buildCollectionPath(FAKE_USER_ID, COLLECTION.getName()));

    RemovePhraseResponse response =
        RemovePhraseResponse.parseFrom(
            client.toBlocking().retrieve(buildRequest(URL, REQUEST), byte[].class));

    assertThat(response)
        .isEqualTo(
            RemovePhraseResponse.newBuilder()
                .setCollection(
                    Converters.from(COLLECTION).toBuilder().clearPhrase().setVersion(1).build())
                .build());
  }
}
