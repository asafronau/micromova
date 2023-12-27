package dev.safronau.micromova.gaebackend.controllers;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import dev.safronau.micromova.gaebackend.converters.Converters;
import dev.safronau.micromova.gaebackend.services.StorePaths;
import dev.safronau.micromova.proto.Collection;
import dev.safronau.micromova.proto.UpdateCollectionRequest;
import dev.safronau.micromova.proto.UpdateCollectionResponse;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

@MicronautTest
public class UpdateCollectionTest extends CommonControllerTest {
  private static final String URL = "/api/collection/update";

  private static final Collection COLLECTION =
      TestUtils.parseProto("TestCollection.textproto", Collection.class);

  private static final UpdateCollectionRequest REQUEST =
      UpdateCollectionRequest.newBuilder()
          .setName(COLLECTION.getName())
          .setIsDiscoverEnabled(false)
          .setDiscoverPoints(1000)
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
  void collectionExist_success() throws Exception {
    Collection collection = TestUtils.parseProto("TestCollection.textproto", Collection.class);
    fakeFileStorage.insertFile(
        collection.toByteArray(),
        StorePaths.buildCollectionPath(FAKE_USER_ID, collection.getName()));

    UpdateCollectionResponse response =
        UpdateCollectionResponse.parseFrom(
            client.toBlocking().retrieve(buildRequest(URL, REQUEST), byte[].class));

    assertThat(response.getCollection())
        .isEqualTo(
            Converters.from(collection).toBuilder().setDiscoverPoints(1000).setVersion(1).build());
  }

  @Test
  void collectionMissing_error() {
    HttpClientResponseException exception =
        assertThrows(
            HttpClientResponseException.class,
            () -> client.toBlocking().retrieve(buildRequest(URL, REQUEST)));
    assertThat(exception.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
  }
}
