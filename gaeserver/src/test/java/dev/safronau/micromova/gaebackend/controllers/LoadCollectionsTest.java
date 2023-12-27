package dev.safronau.micromova.gaebackend.controllers;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import dev.safronau.micromova.gaebackend.services.StorePaths;
import dev.safronau.micromova.proto.Collection;
import dev.safronau.micromova.proto.LoadCollectionsRequest;
import dev.safronau.micromova.proto.LoadCollectionsResponse;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

@MicronautTest
public class LoadCollectionsTest extends CommonControllerTest {

  private static final String URL = "/api/collection/loadall";
  private static final LoadCollectionsRequest REQUEST = LoadCollectionsRequest.getDefaultInstance();

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
    HttpResponse<LoadCollectionsResponse> response =
        client.toBlocking().exchange(buildRequest(URL, REQUEST), LoadCollectionsResponse.class);
    assertThat(response.getBody().isEmpty()).isTrue();
    assertThat(response.getStatus()).isEqualTo(HttpStatus.OK);
  }

  @Test
  void collectionsExist_success() {
    Collection collection = TestUtils.parseProto("TestCollection.textproto", Collection.class);
    fakeFileStorage.insertFile(
        collection.toByteArray(),
        StorePaths.buildCollectionPath(FAKE_USER_ID, collection.getName()));
    fakeFileStorage.insertFile(
        collection.toBuilder().setUserId("another-fake-user").build().toByteArray(),
        StorePaths.buildCollectionPath("another-fake-user", collection.getName()));

    LoadCollectionsResponse response =
        client.toBlocking().retrieve(buildRequest(URL, REQUEST), LoadCollectionsResponse.class);

    assertThat(response)
        .isEqualTo(LoadCollectionsResponse.newBuilder().addNames(collection.getName()).build());
  }
}
