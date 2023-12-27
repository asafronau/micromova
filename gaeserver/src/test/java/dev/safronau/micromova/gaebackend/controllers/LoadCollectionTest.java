package dev.safronau.micromova.gaebackend.controllers;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import dev.safronau.micromova.gaebackend.converters.Converters;
import dev.safronau.micromova.gaebackend.services.StorePaths;
import dev.safronau.micromova.proto.Collection;
import dev.safronau.micromova.proto.LoadCollectionRequest;
import dev.safronau.micromova.proto.LoadCollectionResponse;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

@MicronautTest
public class LoadCollectionTest extends CommonControllerTest {
  private static final String URL = "/api/collection/load";

  private static final LoadCollectionRequest REQUEST =
      LoadCollectionRequest.newBuilder().setName("Fäke cöllëctîön").build();

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
  void collectionsMissing_success() {
    HttpClientResponseException exception =
        assertThrows(
            HttpClientResponseException.class,
            () -> client.toBlocking().retrieve(buildRequest(URL, REQUEST)));
    assertThat(exception.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  void collectionExist_success() throws Exception {
    Collection collection = TestUtils.parseProto("TestCollection.textproto", Collection.class);
    fakeFileStorage.insertFile(
        collection.toByteArray(),
        StorePaths.buildCollectionPath(FAKE_USER_ID, collection.getName()));

    byte[] httpResponse = client.toBlocking().retrieve(buildRequest(URL, REQUEST), byte[].class);

    LoadCollectionResponse response = LoadCollectionResponse.parseFrom(httpResponse);
    assertThat(response.getCollection()).isEqualTo(Converters.from(collection));
  }

  @Test
  void brokenCollectionDataExist_error() {
    fakeFileStorage.insertFile(
        new byte[] {0, 0, 0}, StorePaths.buildCollectionPath(FAKE_USER_ID, REQUEST.getName()));

    HttpClientResponseException exception =
        assertThrows(
            HttpClientResponseException.class,
            () -> client.toBlocking().retrieve(buildRequest(URL, REQUEST)));
    assertThat(exception.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
