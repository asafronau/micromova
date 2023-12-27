package dev.safronau.micromova.gaebackend.controllers.fakes;

import com.google.common.collect.ImmutableList;
import com.google.common.net.MediaType;
import dev.safronau.micromova.gaebackend.services.FileStorage;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Requires;
import jakarta.inject.Singleton;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
@Bean
@Requires(env = "test")
public final class FakeFileStorage implements FileStorage {

  private final Map<String, byte[]> store = new ConcurrentHashMap<>();

  @Override
  public ImmutableList<String> list(String prefix) {
    return store.keySet().stream()
        .filter(path -> path.startsWith(prefix))
        .collect(ImmutableList.toImmutableList());
  }

  @Override
  public byte[] read(String path) {
    return store.get(path);
  }

  @Override
  public String write(byte[] contents, String path, MediaType contentType, boolean isPublic) {
    store.put(path, contents);
    return path;
  }

  public void insertFile(byte[] contents, String path) {
    store.put(path, contents);
  }

  public void clear() {
    store.clear();
  }
}
