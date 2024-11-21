package dev.safronau.micromova.gaebackend.controllers;

import com.google.common.collect.ImmutableList;
import com.google.common.flogger.FluentLogger;
import dev.safronau.micromova.gaebackend.auth.Annotations.CurrentUserId;
import dev.safronau.micromova.gaebackend.services.Constants;
import dev.safronau.micromova.gaebackend.services.GoogleDatastore;
import dev.safronau.micromova.gaebackend.services.GoogleStorage;
import dev.safronau.micromova.gaebackend.services.TextToSpeech;
import dev.safronau.micromova.proto.Collection;
import dev.safronau.micromova.proto.Format;
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
import java.time.temporal.TemporalUnit;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
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
    var updatesPhrases =
        collection.getPhraseList().stream()
            /*.filter(
                phrase ->
                    !phrase.getRecordingList().stream()
                        .map(Recording::getFormat)
                        .collect(Collectors.toUnmodifiableSet())
                        .contains(Format.OGG))*/
            .map(this::getOrCreatePhrase)
            .toList();
    return
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
            });

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
        .subscribeOn(Schedulers.parallel());
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
    return Mono.fromCallable(
            () ->
                textToSpeech.synthesizeSpeech(
                    phrase.getNormalizedText(),
                    phrase.getSourceLanguage(),
                    voiceGender,
                    encoding,
                    Map.of()))
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
