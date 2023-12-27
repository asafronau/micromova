package dev.safronau.micromova.gaebackend.controllers;

import com.google.protobuf.Message;
import com.google.protobuf.TextFormat;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

final class TestUtils {

  static <T extends Message> T parseProto(String filename, Class<T> klass) {
    try {
      return TextFormat.parse(
          Files.readString(Paths.get("src", "test", "resources", filename)), klass);
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  private TestUtils() {}
}
