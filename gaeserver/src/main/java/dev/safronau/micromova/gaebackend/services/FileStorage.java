package dev.safronau.micromova.gaebackend.services;

import com.google.common.collect.ImmutableList;
import com.google.common.net.MediaType;

public interface FileStorage {
  /**
   * Lists objects in the file storage.
   *
   * @param prefix of the path to list.
   * @return file names.
   */
  ImmutableList<String> list(String prefix);

  byte[] read(String path);

  String write(byte[] contents, String path, MediaType contentType, boolean isPublic);
}
