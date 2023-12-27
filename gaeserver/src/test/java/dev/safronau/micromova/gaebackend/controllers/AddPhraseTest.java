package dev.safronau.micromova.gaebackend.controllers;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import dev.safronau.micromova.gaebackend.converters.Converters;
import dev.safronau.micromova.gaebackend.services.Constants;
import dev.safronau.micromova.gaebackend.services.StorePaths;
import dev.safronau.micromova.proto.AddPhraseRequest;
import dev.safronau.micromova.proto.AddPhraseResponse;
import dev.safronau.micromova.proto.Collection;
import dev.safronau.micromova.proto.CollectionView;
import dev.safronau.micromova.proto.Language;
import dev.safronau.micromova.proto.Phrase;
import dev.safronau.micromova.proto.PhraseHeadline;
import dev.safronau.micromova.proto.Translation;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

@MicronautTest
public class AddPhraseTest extends CommonControllerTest {
  private static final String URL = "/api/collection/addphrase";

  private static final Collection COLLECTION =
      TestUtils.parseProto("TestCollection.textproto", Collection.class);

  private static final String COLLECTION_PATH =
      StorePaths.buildCollectionPath(FAKE_USER_ID, COLLECTION.getName());

  private static final Phrase NEW_PHRASE =
      Phrase.newBuilder()
          .setSourceLanguage(Language.DE)
          .setNormalizedText("Schmelzkäse ist mir suspekt")
          .addTranslation(
              Translation.newBuilder()
                  .setText("I am suspicious of processed cheese")
                  .setLanguage(Language.RU))
          .build();

  @Test
  void missingAuthCookie_error() {
    HttpRequest<byte[]> request =
        HttpRequest.POST(URL, AddPhraseRequest.getDefaultInstance().toByteArray())
            .header("Content-Type", "application/x-protobuf");
    HttpClientResponseException exception =
        assertThrows(
            HttpClientResponseException.class, () -> client.toBlocking().retrieve(request));
    assertThat(exception.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }

  @Test
  void missingPhrase_addsNew_success() throws Exception {
    fakeFileStorage.insertFile(COLLECTION.toByteArray(), COLLECTION_PATH);

    AddPhraseResponse response =
        AddPhraseResponse.parseFrom(
            client.toBlocking().retrieve(buildRequest(NEW_PHRASE), byte[].class));

    assertThat(response.getCollection())
        .isEqualTo(
            Converters.from(COLLECTION).toBuilder()
                .addPhrase(
                    0,
                    PhraseHeadline.newBuilder()
                        .setId(-9216743620397136613L)
                        .setNormalizedText(NEW_PHRASE.getNormalizedText()))
                .setVersion(1)
                .build());
    assertThat(fakeFileStorage.list("texttospeech/3/"))
        .containsExactly(
            "texttospeech/3/2/U2NobWVsemvDpHNlIGlzdCBtaXIgc3VzcGVrdA==.mp3",
            "texttospeech/3/1/U2NobWVsemvDpHNlIGlzdCBtaXIgc3VzcGVrdA==.mp3");
    assertThat(fakeDataStore.get(Constants.PHRASE_KEY_KIND, -9216743620397136613L).isPresent())
        .isTrue();
  }

  @Test
  void existingPhrase_updates_success() throws Exception {
    fakeFileStorage.insertFile(COLLECTION.toByteArray(), COLLECTION_PATH);
    Phrase.Builder builder = COLLECTION.toBuilder().getPhraseBuilder(0);
    CollectionView.Builder collectionView = Converters.from(COLLECTION).toBuilder();

    AddPhraseResponse response1 =
        AddPhraseResponse.parseFrom(
            client
                .toBlocking()
                .retrieve(buildRequest(builder.setComment("comment #1").build()), byte[].class));
    assertThat(response1.getCollection()).isEqualTo(collectionView.setVersion(1).build());

    AddPhraseResponse response2 =
        AddPhraseResponse.parseFrom(
            client
                .toBlocking()
                .retrieve(buildRequest(builder.setComment("comment #2").build()), byte[].class));
    assertThat(response2.getCollection()).isEqualTo(collectionView.setVersion(2).build());

    assertThat(fakeFileStorage.list("texttospeech/3/"))
        .containsExactly(
            "texttospeech/3/1/RsOka2UgRGXDvHRzY2ggTWXDn8OkZ2U=.mp3",
            "texttospeech/3/2/RsOka2UgRGXDvHRzY2ggTWXDn8OkZ2U=.mp3");
    assertThat(fakeDataStore.get(Constants.PHRASE_KEY_KIND, 4548451716323669398L).isPresent())
        .isTrue();
  }

  private HttpRequest<byte[]> buildRequest(Phrase newPhrase) {
    return buildRequest(
        URL,
        AddPhraseRequest.newBuilder()
            .setCollectionName(COLLECTION.getName())
            .setPhrase(newPhrase)
            .build());
  }
}
