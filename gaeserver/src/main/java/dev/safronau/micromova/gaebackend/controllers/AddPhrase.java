package dev.safronau.micromova.gaebackend.controllers;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.flogger.FluentLogger;
import dev.safronau.micromova.gaebackend.auth.Annotations.CurrentUserId;
import dev.safronau.micromova.gaebackend.converters.Converters;
import dev.safronau.micromova.gaebackend.services.Constants;
import dev.safronau.micromova.gaebackend.services.GoogleDatastore;
import dev.safronau.micromova.gaebackend.services.GoogleStorage;
import dev.safronau.micromova.gaebackend.services.TextToSpeech;
import dev.safronau.micromova.proto.AddPhraseRequest;
import dev.safronau.micromova.proto.AddPhraseResponse;
import dev.safronau.micromova.proto.Collection;
import dev.safronau.micromova.proto.Format;
import dev.safronau.micromova.proto.Language;
import dev.safronau.micromova.proto.Phrase;
import dev.safronau.micromova.proto.Recording;
import dev.safronau.micromova.proto.Type;
import dev.safronau.micromova.proto.VoiceGender;
import dev.safronau.micromova.proto.VoiceType;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.protobuf.codec.ProtobufferCodec;
import io.micronaut.runtime.http.scope.RequestScope;
import jakarta.inject.Inject;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RequestScope
@Controller("/api/collection/addphrase")
public class AddPhrase {

  private static final FluentLogger logger = FluentLogger.forEnclosingClass();

  private final GoogleDatastore googleDatastore;
  private final GoogleStorage googleStorage;
  private final TextToSpeech textToSpeech;
  private final String userId;

  @Inject
  AddPhrase(
      GoogleStorage googleStorage,
      GoogleDatastore googleDatastore,
      TextToSpeech textToSpeech,
      @CurrentUserId String userId) {
    this.googleDatastore = googleDatastore;
    this.googleStorage = googleStorage;
    this.textToSpeech = textToSpeech;
    this.userId = userId;
  }

  @Post(consumes = ProtobufferCodec.PROTOBUFFER_ENCODED, produces = MediaType.TEXT_PLAIN)
  Mono<byte[]> execute(@Body AddPhraseRequest request) {
    Preconditions.checkArgument(
        !request.getPhrase().getTranslationList().isEmpty(), "missing translations");
    Map<VoiceGender, String> overrides = ImmutableMap.of();
    if (request.getVoiceType().equals(VoiceType.JOURNEY)) {
      Language language = request.getPhrase().getSourceLanguage();
      overrides =
          ImmutableMap.of(
              VoiceGender.FEMALE,
              Objects.requireNonNull(
                  Constants.JOURNEY_VOICE_NAME.get(language, VoiceGender.FEMALE)),
              VoiceGender.MALE,
              Objects.requireNonNull(Constants.JOURNEY_VOICE_NAME.get(language, VoiceGender.MALE)));
    }
    return Mono.zip(
            getOrCreatePhrase(request.getPhrase(), overrides),
            readCollection(request.getCollectionName()),
            ((phrase, collection) -> {
              logger.atInfo().log("Got both phrase and collection.");
              Collection.Builder builder = collection.toBuilder();
              Optional<Phrase.Builder> pb =
                  builder.getPhraseBuilderList().stream()
                      .filter(p -> p.getId() == phrase.getId())
                      .findFirst();
              if (pb.isPresent()) {
                logger.atInfo().log(
                    "Phrase %s is present in the collection %s.",
                    phrase.getId(), collection.getName());
                pb.get()
                    .setExample(phrase.getExample())
                    .clearTranslation()
                    .addAllTranslation(phrase.getTranslationList())
                    .clearRecording()
                    .addAllRecording(phrase.getRecordingList())
                    .setIsDiscoverable(phrase.getIsDiscoverable());
              } else {
                builder.addPhrase(phrase);
              }
              return AddPhraseResponse.newBuilder()
                  .setCollection(
                      Converters.from(googleStorage.writeCollectionFile(builder.build())))
                  .build()
                  .toByteArray();
            }))
        .subscribeOn(Schedulers.parallel());
  }

  private Mono<Phrase> getOrCreatePhrase(Phrase phrase, Map<VoiceGender, String> overrides) {
    return createPhrase(phrase, overrides).subscribeOn(Schedulers.parallel());
  }

  private Mono<Phrase> createPhrase(Phrase phrase, Map<VoiceGender, String> overrides) {
    logger.atInfo().log("Creating a phrase %s", phrase);
    return Mono.zip(
            ImmutableList.of(
                synthesizeSpeechForGender(
                    phrase, VoiceGender.FEMALE, Constants.MP3_64_ENCODING, Map.of()),
                synthesizeSpeechForGender(
                    phrase, VoiceGender.MALE, Constants.MP3_64_ENCODING, Map.of()),
                synthesizeSpeechForGender(
                    phrase, VoiceGender.FEMALE, Constants.OGG_ENCODING, overrides),
                synthesizeSpeechForGender(
                    phrase, VoiceGender.MALE, Constants.OGG_ENCODING, overrides)),
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
      Phrase phrase, VoiceGender voiceGender, String encoding, Map<VoiceGender, String> overrides) {
    return Mono.fromCallable(
            () ->
                textToSpeech.synthesizeSpeech(
                    phrase.getNormalizedText(),
                    phrase.getSourceLanguage(),
                    voiceGender,
                    encoding,
                    overrides))
        .map(
            bytes ->
                googleStorage.writeAudioFile(
                    bytes,
                    phrase.getSourceLanguage(),
                    voiceGender,
                    phrase.getNormalizedText(),
                    encoding))
        .subscribeOn(Schedulers.parallel());
  }

  private Mono<Collection> readCollection(String name) {
    return Mono.just(googleStorage.readCollectionFile(/* userId= */ userId, /* name= */ name));
  }

  /* private static String cleanText(String text) {
    return text.trim().replaceAll("\\s+", " ");
  } */
}
