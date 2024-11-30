package dev.safronau.micromova.gaebackend.controllers;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.flogger.FluentLogger;
import dev.safronau.micromova.gaebackend.auth.Annotations.CurrentUserId;
import dev.safronau.micromova.gaebackend.services.Constants;
import dev.safronau.micromova.gaebackend.services.GoogleDatastore;
import dev.safronau.micromova.gaebackend.services.GoogleStorage;
import dev.safronau.micromova.gaebackend.services.TextToSpeech;
import dev.safronau.micromova.proto.Collection;
import dev.safronau.micromova.proto.Format;
import dev.safronau.micromova.proto.Language;
import dev.safronau.micromova.proto.Phrase;
import dev.safronau.micromova.proto.Recording;
import dev.safronau.micromova.proto.Type;
import dev.safronau.micromova.proto.UpdateAudioInCollectionRequest;
import dev.safronau.micromova.proto.UpdateAudioInCollectionResponse;
import dev.safronau.micromova.proto.VoiceGender;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.exceptions.HttpStatusException;
import io.micronaut.protobuf.codec.ProtobufferCodec;
import io.micronaut.runtime.http.scope.RequestScope;
import jakarta.inject.Inject;
import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RequestScope
@Controller("/api/collection/audioupdate")
public class UpdateAudioInCollection {

  private static final FluentLogger logger = FluentLogger.forEnclosingClass();

  private final GoogleDatastore googleDatastore;
  private final GoogleStorage googleStorage;
  private final TextToSpeech textToSpeech;
  private final String userId;

  @Inject
  UpdateAudioInCollection(
      GoogleDatastore googleDatastore,
      GoogleStorage googleStorage,
      TextToSpeech textToSpeech,
      @CurrentUserId String userId) {
    this.googleDatastore = googleDatastore;
    this.googleStorage = googleStorage;
    this.textToSpeech = textToSpeech;
    this.userId = userId;
  }

  @Post(consumes = ProtobufferCodec.PROTOBUFFER_ENCODED, produces = MediaType.TEXT_PLAIN)
  Mono<byte[]> execute(@Body UpdateAudioInCollectionRequest request) {
    Collection collection =
        googleStorage.readCollectionFile(/* userId= */ userId, /* name= */ request.getName());
    if (collection.equals(Collection.getDefaultInstance())) {
      throw new HttpStatusException(HttpStatus.BAD_REQUEST, "collection is missing");
    }
    Collection.Builder builder = collection.toBuilder();

    var sources = collection.getPhraseList().stream()
        .filter(phrase -> phrase.getNormalizedText().split(" ").length >= 5)
        .skip(100)
        .toList();
    /*var updatesPhrases =
    collection.getPhraseList().stream()
        .filter(phrase -> phrase.getNormalizedText().split(" ").length > 6)
        .map(this::getOrCreatePhrase)
        .toList();*/

    System.out.printf("============ %s\n", sources.size());

    Mono<Phrase> monos = Mono.empty();
    for (var s : sources) {
      monos =
          monos.then(Mono.delay(Duration.ofSeconds(3))).then(
              getOrCreatePhrase(s)
                  .map(
                      pp -> {
                        Optional<Phrase.Builder> pb =
                            builder.getPhraseBuilderList().stream()
                                .filter(p -> p.getId() == pp.getId())
                                .findFirst();
                        pb.get()
                            .setExample(pp.getExample())
                            .clearTranslation()
                            .addAllTranslation(pp.getTranslationList())
                            .clearRecording()
                            .addAllRecording(pp.getRecordingList())
                            .setIsDiscoverable(pp.getIsDiscoverable());
                        return pp;
                      }));
    }
    return monos
        .subscribeOn(Schedulers.single())
        .map(
            ee -> {
              googleStorage.writeCollectionFile(builder.build());
              return UpdateAudioInCollectionResponse.getDefaultInstance().toByteArray();
            });

    /*return
        Mono.zip(
                updatesPhrases,
                (Object[] responses) -> {
                  for (var e : responses) {
                    Phrase pp = (Phrase) e;
                    Optional<Phrase.Builder> pb =
                        builder.getPhraseBuilderList().stream()
                            .filter(p -> p.getId() == pp.getId())
                            .findFirst();
                    pb.get()
                        .setExample(pp.getExample())
                        .clearTranslation()
                        .addAllTranslation(pp.getTranslationList())
                        .clearRecording()
                        .addAllRecording(pp.getRecordingList())
                        .setIsDiscoverable(pp.getIsDiscoverable());
                  }
                  return builder.build();
                })
            .subscribeOn(Schedulers.single()).map(e -> {
              googleStorage.writeCollectionFile(e);
              return UpdateAudioInCollectionResponse.getDefaultInstance().toByteArray();
            });*/

    // System.out.println(col);
    // googleStorage.writeCollectionFile(builder.build());
  }

  private Mono<Phrase> getOrCreatePhrase(Phrase phrase) {
    return buildAndStorePhrase(phrase).single();
  }

  private Mono<Phrase> buildAndStorePhrase(Phrase phrase) {
    return Mono.zip(
            ImmutableList.of(
                synthesizeSpeechForGender(phrase, VoiceGender.FEMALE, Constants.MP3_64_ENCODING),
                synthesizeSpeechForGender(phrase, VoiceGender.MALE, Constants.MP3_64_ENCODING),
                synthesizeSpeechForGender(phrase, VoiceGender.FEMALE, Constants.OGG_ENCODING),
                synthesizeSpeechForGender(phrase, VoiceGender.MALE, Constants.OGG_ENCODING)),
            (responses) ->
                produceResponse(
                    phrase,
                    (String) responses[0],
                    (String) responses[1],
                    (String) responses[2],
                    (String) responses[3]))
        .subscribeOn(Schedulers.single());
  }

  private Phrase produceResponse(
      Phrase phrase,
      String femaleMp3AudioPath,
      String maleMp3AudioPath,
      String femaleOggAudioPath,
      String maleOggAudioPath) {
    Phrase.Builder newPhrase = Phrase.newBuilder().mergeFrom(phrase).clearRecording();
    long id =
        googleDatastore.putPhrase(
            phrase.getNormalizedText(),
            phrase.getSourceLanguage(),
            femaleMp3AudioPath,
            maleMp3AudioPath,
            femaleOggAudioPath,
            maleOggAudioPath);
    return newPhrase
        .setId(id)
        .addRecording(
            Recording.newBuilder()
                .setType(Type.GOOGLE_TEXT_TO_SPEECH)
                .setUri(femaleMp3AudioPath)
                .setFormat(Format.MP3)
                .setVoiceGender(VoiceGender.FEMALE))
        .addRecording(
            Recording.newBuilder()
                .setType(Type.GOOGLE_TEXT_TO_SPEECH)
                .setUri(maleMp3AudioPath)
                .setFormat(Format.MP3)
                .setVoiceGender(VoiceGender.MALE))
        .addRecording(
            Recording.newBuilder()
                .setType(Type.GOOGLE_TEXT_TO_SPEECH)
                .setUri(femaleOggAudioPath)
                .setFormat(Format.OGG)
                .setVoiceGender(VoiceGender.FEMALE))
        .addRecording(
            Recording.newBuilder()
                .setType(Type.GOOGLE_TEXT_TO_SPEECH)
                .setUri(maleOggAudioPath)
                .setFormat(Format.OGG)
                .setVoiceGender(VoiceGender.MALE))
        .build();
  }

  private Mono<String> synthesizeSpeechForGender(
      Phrase phrase, VoiceGender voiceGender, String encoding) {
    Map<VoiceGender, String> map;
    if (encoding.equals(Constants.OGG_ENCODING)) {
      Language language = phrase.getSourceLanguage();
      map =
          ImmutableMap.of(
              VoiceGender.FEMALE,
              Objects.requireNonNull(
                  Constants.JOURNEY_VOICE_NAME.get(language, VoiceGender.FEMALE)),
              VoiceGender.MALE,
              Objects.requireNonNull(Constants.JOURNEY_VOICE_NAME.get(language, VoiceGender.MALE)));
    } else {
      map = Map.of();
    }
    return Mono.fromCallable(
            () ->
                textToSpeech.synthesizeSpeech(
                    phrase.getNormalizedText(),
                    phrase.getSourceLanguage(),
                    voiceGender,
                    encoding,
                    map))
        .map(
            bytes ->
                googleStorage.writeAudioFile(
                    bytes,
                    phrase.getSourceLanguage(),
                    voiceGender,
                    phrase.getNormalizedText(),
                    encoding))
        .single();
  }
}
