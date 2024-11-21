package dev.safronau.micromova.gaebackend.services;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.google.common.collect.ImmutableList;
import com.google.common.flogger.FluentLogger;
import com.google.common.net.MediaType;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import dev.safronau.micromova.gaebackend.errors.UnknownError;
import dev.safronau.micromova.proto.Collection;
import dev.safronau.micromova.proto.Language;
import dev.safronau.micromova.proto.VoiceGender;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.Base64;
import java.util.Optional;

@Singleton
public class GoogleStorage {

  private static final FluentLogger logger = FluentLogger.forEnclosingClass();

  private static final String AUDIO_DIRECTORY = "texttospeech";

  private static final Base64.Decoder BASE64_DECODER = Base64.getDecoder();
  private static final Base64.Encoder BASE64_ENCODER = Base64.getEncoder();

  private final FileStorage storage;

  @Inject
  public GoogleStorage(FileStorage storage) {
    this.storage = storage;
  }

  public Collection readCollectionFile(String userId, String name) {
    try {
      Optional<byte[]> contents =
          Optional.ofNullable(storage.read(StorePaths.buildCollectionPath(userId, name)));
      if (contents.isPresent()) {
        return Collection.parseFrom(contents.get());
      }
      return Collection.getDefaultInstance();
    } catch (InvalidProtocolBufferException e) {
      throw new UnknownError("bad collection binary data", e);
    }
  }

  public Collection writeCollectionFile(Collection collection) {
    Collection updatedCollection =
        collection.toBuilder().setVersion(collection.getVersion() + 1).build();
    storage.write(
        updatedCollection.toByteArray(),
        StorePaths.buildCollectionPath(collection.getUserId(), collection.getName()),
        MediaType.OCTET_STREAM,
        /* isPublic= */ false);
    return updatedCollection;
  }

  public String writeAudioFile(
      ByteString audio, Language language, VoiceGender voiceGender, String text, String encoding) {
    MediaType type =
        switch (encoding) {
          case Constants.MP3_64_ENCODING -> MediaType.MPEG_AUDIO;
          case Constants.OGG_ENCODING -> MediaType.OGG_AUDIO;
          default -> throw new IllegalArgumentException("invalid encoding " + encoding);
        };
    String fileName =
        storage.write(
            audio.toByteArray(),
            buildAudioPath(language, voiceGender, text, encoding),
            type,
            /* isPublic= */ true);
    logger.atInfo().log("Wrote an audio file: %s", fileName);
    return fileName;
  }

  public ImmutableList<String> listCollections(String userId) {
    return storage.list(String.format("%s/%s/", StorePaths.COLLECTION_DIRECTORY, userId)).stream()
        .map(e -> e.substring(e.lastIndexOf("/") + 1))
        .map(BASE64_DECODER::decode)
        .map(e -> new String(e, UTF_8))
        .collect(ImmutableList.toImmutableList());
  }

  private static String buildAudioPath(
      Language language, VoiceGender voiceGender, String text, String encoding) {
    String encodedText = BASE64_ENCODER.encodeToString(text.getBytes(UTF_8));
    String ext =
        switch (encoding) {
          case Constants.MP3_64_ENCODING -> "mp3";
          case Constants.OGG_ENCODING -> "ogg";
          default -> throw new IllegalArgumentException("invalid encoding " + encoding);
        };
    return String.format(
        "%s/%s/%s/%s.%s",
        AUDIO_DIRECTORY, language.getNumber(), voiceGender.getNumber(), encodedText, ext);
  }
}
