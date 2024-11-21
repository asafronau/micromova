package dev.safronau.micromova.gaebackend.controllers.fakes;

import com.google.protobuf.ByteString;
import dev.safronau.micromova.gaebackend.services.TextToSpeech;
import dev.safronau.micromova.proto.Language;
import dev.safronau.micromova.proto.VoiceGender;
import io.micronaut.context.annotation.Requires;
import jakarta.inject.Singleton;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Singleton
@Requires(env = "test")
final class FakeTextToSpeech implements TextToSpeech {

  @Override
  public ByteString synthesizeSpeech(
      String text,
      Language language,
      VoiceGender voiceGender,
      String encoding,
      Map<VoiceGender, String> overrides) {
    return ByteString.copyFrom(
        String.join(":", text, language.toString(), voiceGender.toString(), encoding),
        StandardCharsets.UTF_8);
  }
}
