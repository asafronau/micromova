package dev.safronau.micromova.gaebackend.controllers;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.flogger.FluentLogger;
import dev.safronau.micromova.gaebackend.auth.Annotations.CurrentUserId;
import dev.safronau.micromova.gaebackend.converters.Converters;
import dev.safronau.micromova.gaebackend.services.GoogleDatastore;
import dev.safronau.micromova.gaebackend.services.GoogleStorage;
import dev.safronau.micromova.gaebackend.services.TextToSpeech;
import dev.safronau.micromova.proto.AddPhraseRequest;
import dev.safronau.micromova.proto.AddPhraseResponse;
import dev.safronau.micromova.proto.Collection;
import dev.safronau.micromova.proto.Format;
import dev.safronau.micromova.proto.Phrase;
import dev.safronau.micromova.proto.Recording;
import dev.safronau.micromova.proto.Type;
import dev.safronau.micromova.proto.VoiceGender;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.protobuf.codec.ProtobufferCodec;
import io.micronaut.runtime.http.scope.RequestScope;
import jakarta.inject.Inject;
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
    return Mono.zip(
            getOrCreatePhrase(request.getPhrase()),
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

  private Mono<Phrase> getOrCreatePhrase(Phrase phrase) {
    return Mono.just(
            googleDatastore
                .getPhrase(phrase.getNormalizedText(), phrase.getSourceLanguage())
                .map(
                    entity ->
                        phrase.toBuilder()
                            .setId(Iterables.getOnlyElement(entity.getKey().getPath()).getId())
                            .setNormalizedText(entity.getProperties().get("text").getStringValue())
                            .addRecording(
                                Recording.newBuilder()
                                    .setVoiceGender(VoiceGender.FEMALE)
                                    .setFormat(Format.MP3)
                                    .setType(Type.GOOGLE_TEXT_TO_SPEECH)
                                    .setUri(
                                        entity
                                            .getProperties()
                                            .get("female_mp3_path")
                                            .getStringValue()))
                            .addRecording(
                                Recording.newBuilder()
                                    .setVoiceGender(VoiceGender.MALE)
                                    .setFormat(Format.MP3)
                                    .setType(Type.GOOGLE_TEXT_TO_SPEECH)
                                    .setUri(
                                        entity
                                            .getProperties()
                                            .get("male_mp3_path")
                                            .getStringValue()))
                            .build()))
        .flatMap(pp -> pp.map(Mono::just).orElseGet(() -> createPhrase(phrase)))
        .subscribeOn(Schedulers.parallel());
  }

  private Mono<Phrase> createPhrase(Phrase phrase) {
    return buildAndStorePhrase(phrase);
  }

  private Mono<Phrase> buildAndStorePhrase(Phrase phrase) {
    logger.atInfo().log("Creating a phrase %s", phrase);
    return Mono.zip(
            synthesizeSpeechForGender(phrase, VoiceGender.FEMALE),
            synthesizeSpeechForGender(phrase, VoiceGender.MALE),
            (femaleAudio, maleAudio) -> produceResponse(phrase, femaleAudio, maleAudio))
        .subscribeOn(Schedulers.parallel());
  }

  private Phrase produceResponse(Phrase phrase, String femaleAudioPath, String maleAudioPath) {
    Phrase.Builder newPhrase = Phrase.newBuilder().mergeFrom(phrase);
    long id =
        googleDatastore.putPhrase(
            phrase.getNormalizedText(), phrase.getSourceLanguage(), femaleAudioPath, maleAudioPath);
    return newPhrase
        .setId(id)
        .addRecording(
            Recording.newBuilder()
                .setType(Type.GOOGLE_TEXT_TO_SPEECH)
                .setUri(femaleAudioPath)
                .setFormat(Format.MP3)
                .setVoiceGender(VoiceGender.FEMALE))
        .addRecording(
            Recording.newBuilder()
                .setType(Type.GOOGLE_TEXT_TO_SPEECH)
                .setUri(maleAudioPath)
                .setFormat(Format.MP3)
                .setVoiceGender(VoiceGender.MALE))
        .build();
  }

  private Mono<String> synthesizeSpeechForGender(Phrase phrase, VoiceGender voiceGender) {
    return Mono.fromCallable(
            () ->
                textToSpeech.synthesizeSpeech(
                    phrase.getNormalizedText(), phrase.getSourceLanguage(), voiceGender))
        .map(
            bytes ->
                googleStorage.writeMp3AudioFile(
                    bytes, phrase.getSourceLanguage(), voiceGender, phrase.getNormalizedText()))
        .subscribeOn(Schedulers.parallel());
  }

  private Mono<Collection> readCollection(String name) {
    return Mono.just(googleStorage.readCollectionFile(/* userId= */ userId, /* name= */ name));
  }

  /* private static String cleanText(String text) {
    return text.trim().replaceAll("\\s+", " ");
  } */
}
