package dev.safronau.micromova.gaebackend.services;

import com.google.protobuf.ByteString;
import dev.safronau.micromova.proto.Language;
import dev.safronau.micromova.proto.VoiceGender;

public interface TextToSpeech {
  ByteString synthesizeSpeech(String text, Language language, VoiceGender voiceGender);
}
