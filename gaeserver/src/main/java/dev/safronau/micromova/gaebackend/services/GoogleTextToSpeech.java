package dev.safronau.micromova.gaebackend.services;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.texttospeech.v1beta1.Texttospeech;
import com.google.api.services.texttospeech.v1beta1.model.AudioConfig;
import com.google.api.services.texttospeech.v1beta1.model.SynthesisInput;
import com.google.api.services.texttospeech.v1beta1.model.SynthesizeSpeechRequest;
import com.google.api.services.texttospeech.v1beta1.model.VoiceSelectionParams;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableTable;
import com.google.common.flogger.FluentLogger;
import com.google.protobuf.ByteString;
import dev.safronau.micromova.proto.Language;
import dev.safronau.micromova.proto.VoiceGender;
import io.micronaut.context.annotation.Property;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.ReflectiveAccess;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Optional;

@Singleton
@Requires(notEnv = "test")
public class GoogleTextToSpeech implements TextToSpeech {

  @Property(name = "mova.google_project_id")
  @ReflectiveAccess
  private String projectId;

  private static final FluentLogger logger = FluentLogger.forEnclosingClass();

  private static final ImmutableMap<Language, String> LANGUAGE_TO_NAME =
      ImmutableMap.of(
          Language.EN_US,
          "en-US",
          Language.DE,
          "de",
          Language.FR,
          "fr",
          Language.PL,
          "pl",
          Language.IT,
          "it",
          Language.NL,
          "nl");

  private static final ImmutableMap<VoiceGender, String> VOICE_GENDER_TO_SSML =
      ImmutableMap.of(VoiceGender.FEMALE, "FEMALE", VoiceGender.MALE, "MALE");

  private static final ImmutableTable<Language, VoiceGender, String> TTS_VOICE_NAME =
      ImmutableTable.<Language, VoiceGender, String>builder()
          .put(Language.DE, VoiceGender.FEMALE, "de-DE-Wavenet-A")
          .put(Language.DE, VoiceGender.MALE, "de-DE-Wavenet-B")
          .put(Language.EN_US, VoiceGender.FEMALE, "en-US-Wavenet-B")
          .put(Language.EN_US, VoiceGender.MALE, "en-US-Wavenet-F")
          .put(Language.FR, VoiceGender.FEMALE, "fr-FR-Wavenet-A")
          .put(Language.FR, VoiceGender.MALE, "fr-FR-Wavenet-B")
          .put(Language.PL, VoiceGender.FEMALE, "pl-PL-Wavenet-A")
          .put(Language.PL, VoiceGender.MALE, "pl-PL-Wavenet-B")
          .put(Language.IT, VoiceGender.FEMALE, "it-IT-Wavenet-A")
          .put(Language.IT, VoiceGender.MALE, "it-IT-Wavenet-D")
          .put(Language.NL, VoiceGender.FEMALE, "nl-NL-Wavenet-D")
          .put(Language.NL, VoiceGender.MALE, "nl-NL-Wavenet-B")
          .build();

  @Inject
  public GoogleTextToSpeech() {}

  private static String getSsmlVoiceGender(VoiceGender voiceGender) {
    return Optional.ofNullable(VOICE_GENDER_TO_SSML.get(voiceGender))
        .orElseThrow(
            () ->
                new IllegalArgumentException(
                    String.format("Unsupported voice gender: %s", voiceGender)));
  }

  private static String getLanguageCode(Language language) {
    return Optional.ofNullable(LANGUAGE_TO_NAME.get(language))
        .orElseThrow(
            () ->
                new IllegalArgumentException(String.format("Unsupported language: %s", language)));
  }

  @Override
  public ByteString synthesizeSpeech(String text, Language language, VoiceGender voiceGender) {
    logger.atInfo().log("Synthesizing %s %s %s", text, language, voiceGender);
    try {
      Texttospeech client =
          new Texttospeech.Builder(
                  GoogleNetHttpTransport.newTrustedTransport(),
                  GsonFactory.getDefaultInstance(),
                  new HttpCredentialsAdapter(GoogleCredentials.getApplicationDefault()))
              .setApplicationName(projectId)
              .build();
      SynthesizeSpeechRequest request =
          new SynthesizeSpeechRequest()
              .setInput(new SynthesisInput().setText(text))
              .setVoice(
                  new VoiceSelectionParams()
                      .setLanguageCode(getLanguageCode(language))
                      .setName(TTS_VOICE_NAME.get(language, voiceGender))
                      .setSsmlGender(getSsmlVoiceGender(voiceGender)))
              .setAudioConfig(new AudioConfig().setAudioEncoding("MP3_64_KBPS"));
      return ByteString.copyFrom(client.text().synthesize(request).execute().decodeAudioContent());
    } catch (IOException | GeneralSecurityException e) {
      throw new IllegalStateException(e);
    }
  }
}
