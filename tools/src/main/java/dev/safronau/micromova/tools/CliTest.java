package dev.safronau.micromova.tools;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import dev.safronau.micromova.proto.AddPhraseRequest;
import dev.safronau.micromova.proto.AddPhraseResponse;
import dev.safronau.micromova.proto.CreateCollectionRequest;
import dev.safronau.micromova.proto.CreateCollectionResponse;
import dev.safronau.micromova.proto.GenerateExamRequest;
import dev.safronau.micromova.proto.GenerateExamResponse;
import dev.safronau.micromova.proto.Language;
import dev.safronau.micromova.proto.LoadCollectionRequest;
import dev.safronau.micromova.proto.LoadCollectionResponse;
import dev.safronau.micromova.proto.LoadCollectionsResponse;
import dev.safronau.micromova.proto.Phrase;
import dev.safronau.micromova.proto.Translation;
import dev.safronau.micromova.proto.UpdateAudioInCollectionResponse;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

public final class CliTest {

  private static final String COOKIE = System.getenv("TEST_MOVA_COOKIE");

  public static void main(String[] args) {
    //createTestCollection();
    //loadTestCollections();
    //loadTestCollection();
    // for (int i = 0; i < 10; i++) generateExam();
    // addPhrase();
    migrate();
  }

  private static long getPhraseKey(String text) {
    return fnv1((text + Language.EN_US).getBytes(UTF_8));
  }

  private static long fnv1(byte[] data) {
    long hash = 0xcbf29ce484222325L;
    for (byte datum : data) {
      hash *= 0x100000001b3L;
      hash ^= datum & 0xff;
    }
    return hash;
  }

  private static void addPhrase() {
    AddPhraseRequest req =
        AddPhraseRequest.newBuilder()
            .setCollectionName("andrei test 1")
            .setPhrase(
                Phrase.newBuilder()
                    .setSourceLanguage(Language.DE)
                    .addTranslation(
                        Translation.newBuilder().setText("Тест").setLanguage(Language.RU))
                    .setNormalizedText("Tëßt"))
            .build();
    HttpClient client = HttpClient.newHttpClient();
    HttpRequest request =
        HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8080/api/collection/addphrase"))
            .header("Content-Type", "application/x-protobuf")
            .header("Cookie", COOKIE)
            .POST(BodyPublishers.ofByteArray(req.toByteArray()))
            .build();
    System.out.println(
        client
            .sendAsync(request, BodyHandlers.ofByteArray())
            .thenApply(HttpResponse::body)
            .thenApply((response) -> parseProto(response, AddPhraseResponse.getDefaultInstance()))
            .join());
  }

  private static void generateExam() {
    GenerateExamRequest req =
        GenerateExamRequest.newBuilder()
            .setCollectionName("andrei test 1")
            .setTimezone("America/Los_Angeles")
            .build();
    HttpClient client = HttpClient.newHttpClient();
    HttpRequest request =
        HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8080/api/exam/generate"))
            .header("Content-Type", "application/x-protobuf")
            .header("Cookie", COOKIE)
            .POST(BodyPublishers.ofByteArray(req.toByteArray()))
            .build();
    System.out.println(
        client
            .sendAsync(request, BodyHandlers.ofByteArray())
            .thenApply(HttpResponse::body)
            .thenApply(
                (response) -> parseProto(response, GenerateExamResponse.getDefaultInstance()))
            .join());
  }

  private static void migrate() {
    LoadCollectionRequest req = LoadCollectionRequest.newBuilder().setName("deutsch 2021").build();
    HttpClient client = HttpClient.newHttpClient();
    HttpRequest request =
        HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8080/api/collection/audioupdate"))
            .header("Content-Type", "application/x-protobuf")
            .header("Cookie", COOKIE)
            .POST(BodyPublishers.ofByteArray(req.toByteArray()))
            .build();
    System.out.println(
        client
            .sendAsync(request, BodyHandlers.ofByteArray())
            .thenApply(HttpResponse::body)
            .thenApply(
                (response) ->
                    parseProto(response, UpdateAudioInCollectionResponse.getDefaultInstance()))
            .join());
  }

  private static void loadTestCollection() {
    LoadCollectionRequest req = LoadCollectionRequest.newBuilder().setName("andrei test 1").build();
    HttpClient client = HttpClient.newHttpClient();
    HttpRequest request =
        HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8080/api/collection/load"))
            .header("Content-Type", "application/x-protobuf")
            .header("Cookie", COOKIE)
            .POST(BodyPublishers.ofByteArray(req.toByteArray()))
            .build();
    System.out.println(
        client
            .sendAsync(request, BodyHandlers.ofByteArray())
            .thenApply(HttpResponse::body)
            .thenApply(
                (response) -> parseProto(response, LoadCollectionResponse.getDefaultInstance()))
            .join());
  }

  private static void loadTestCollections() {
    HttpClient client = HttpClient.newHttpClient();
    HttpRequest request =
        HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8080/api/collection/loadall"))
            .header("Content-Type", "application/x-protobuf")
            .header("Cookie", COOKIE)
            .POST(BodyPublishers.noBody())
            .build();
    System.out.println(
        client
            .sendAsync(request, BodyHandlers.ofByteArray())
            .thenApply(HttpResponse::body)
            .thenApply(
                (response) -> parseProto(response, LoadCollectionsResponse.getDefaultInstance()))
            .join());
  }

  private static void createTestCollection() {
    CreateCollectionRequest req =
        CreateCollectionRequest.newBuilder()
            .setName("test andrei 123")
            .setDiscoverPoints(400)
            .setSourceLanguage(Language.EN_US)
            .setTranslationLanguage(Language.RU)
            .build();
    HttpClient client = HttpClient.newHttpClient();
    HttpRequest request =
        HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8080/api/collection/create"))
            .header("Content-Type", "application/x-protobuf")
            .header("Cookie", COOKIE)
            .POST(BodyPublishers.ofByteArray(req.toByteArray()))
            .build();
    System.out.println(
        client
            .sendAsync(request, BodyHandlers.ofByteArray())
            .thenApply(HttpResponse::body)
            .thenApply(
                (response) -> parseProto(response, CreateCollectionResponse.getDefaultInstance()))
            .join());
  }

  @SuppressWarnings("unchecked")
  private static <T extends Message> T parseProto(byte[] response, T prototype) {
    try {
      return (T) prototype.getParserForType().parseFrom(response);
    } catch (InvalidProtocolBufferException e) {
      throw new IllegalArgumentException(e);
    }
  }
}
