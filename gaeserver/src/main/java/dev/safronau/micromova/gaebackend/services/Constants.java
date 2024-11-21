package dev.safronau.micromova.gaebackend.services;

import com.google.common.collect.ImmutableTable;
import dev.safronau.micromova.proto.Language;
import dev.safronau.micromova.proto.VoiceGender;

public final class Constants {

  public static final String USER_KEY_KIND = "User";
  public static final String PHRASE_KEY_KIND = "PhraseNew";

  public static final String MP3_64_ENCODING = "MP3_64_KBPS";
  public static final String OGG_ENCODING = "OGG_OPUS";

  public static final ImmutableTable<Language, VoiceGender, String> JOURNEY_VOICE_NAME =
      ImmutableTable.<Language, VoiceGender, String>builder()
          .put(Language.DE, VoiceGender.FEMALE, "de-DE-Journey-F")
          .put(Language.DE, VoiceGender.MALE, "de-DE-Journey-D")
          .put(Language.EN_US, VoiceGender.FEMALE, "en-US-Journey-F")
          .put(Language.EN_US, VoiceGender.MALE, "en-US-Journey-D")
          .put(Language.FR, VoiceGender.FEMALE, "fr-FR-Journey-F")
          .put(Language.FR, VoiceGender.MALE, "fr-FR-Journey-D")
          .put(Language.IT, VoiceGender.FEMALE, "it-IT-Journey-F")
          .put(Language.IT, VoiceGender.MALE, "it-IT-Journey-D")
          .build();

  private Constants() {}
}
