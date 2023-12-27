package dev.safronau.micromova.gaebackend.controllers;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import dev.safronau.micromova.gaebackend.services.StorePaths;
import dev.safronau.micromova.proto.Collection;
import dev.safronau.micromova.proto.CollectionView;
import dev.safronau.micromova.proto.CreateCollectionRequest;
import dev.safronau.micromova.proto.CreateCollectionResponse;
import dev.safronau.micromova.proto.Language;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

@MicronautTest
public class CreateCollectionTest extends CommonControllerTest {
  private static final String URL = "/api/collection/create";

  private static final Collection NEW_COLLECTION =
      Collection.newBuilder()
          .setName("Test collection")
          .setUserId(FAKE_USER_ID)
          .setSourceLanguage(Language.DE)
          .setTranslationLanguage(Language.EN_US)
          .setDiscoverPoints(1234)
          .setIsDiscoverEnabled(true)
          .setVersion(1)
          .build();
  private static final CreateCollectionRequest REQUEST =
      CreateCollectionRequest.newBuilder()
          .setName(NEW_COLLECTION.getName())
          .setSourceLanguage(NEW_COLLECTION.getSourceLanguage())
          .setTranslationLanguage(NEW_COLLECTION.getTranslationLanguage())
          .setDiscoverPoints(1234)
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
  void newCollectionMissing_success() throws Exception {
    CreateCollectionResponse response =
        client.toBlocking().retrieve(buildRequest(URL, REQUEST), CreateCollectionResponse.class);

    assertThat(response)
        .isEqualTo(
            CreateCollectionResponse.newBuilder()
                .setCollection(
                    CollectionView.newBuilder()
                        .setName(NEW_COLLECTION.getName())
                        .setUserId(FAKE_USER_ID)
                        .setSourceLanguage(NEW_COLLECTION.getSourceLanguage())
                        .setTranslationLanguage(NEW_COLLECTION.getTranslationLanguage())
                        .setDiscoverPoints(NEW_COLLECTION.getDiscoverPoints())
                        .setIsDiscoverEnabled(true)
                        .setVersion(1))
                .build());
    assertThat(
            Collection.parseFrom(
                fakeFileStorage.read(
                    StorePaths.buildCollectionPath(FAKE_USER_ID, NEW_COLLECTION.getName()))))
        .isEqualTo(NEW_COLLECTION);
  }

  @Test
  void collectionAlreadyExit_error() {
    fakeFileStorage.insertFile(
        NEW_COLLECTION.toByteArray(),
        StorePaths.buildCollectionPath(FAKE_USER_ID, NEW_COLLECTION.getName()));

    HttpClientResponseException exception =
        assertThrows(
            HttpClientResponseException.class,
            () -> client.toBlocking().retrieve(buildRequest(URL, REQUEST)));
    assertThat(exception.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
  }
}
