package dev.safronau.micromova.gaebackend.services;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.util.Base64;

public final class StorePaths {

  public static final String COLLECTION_DIRECTORY = "collection";

  public static String buildCollectionPath(String userId, String collectionId) {
    return String.format(
        "%s/%s/%s",
        COLLECTION_DIRECTORY,
        userId,
        Base64.getEncoder().encodeToString(collectionId.getBytes(UTF_8)));
  }
}
