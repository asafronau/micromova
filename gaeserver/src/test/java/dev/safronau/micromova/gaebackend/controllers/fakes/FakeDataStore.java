package dev.safronau.micromova.gaebackend.controllers.fakes;

import com.google.api.services.datastore.v1.model.Entity;
import com.google.api.services.datastore.v1.model.Key;
import com.google.api.services.datastore.v1.model.PathElement;
import dev.safronau.micromova.gaebackend.services.DataStore;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Requires;
import jakarta.inject.Singleton;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
@Bean
@Requires(env = "test")
public final class FakeDataStore implements DataStore {

  private final Map<String, Entity> store = new ConcurrentHashMap<>();

  @Override
  public Optional<Entity> get(String kind, String name) {
    return Optional.ofNullable(store.get(buildKey(kind, name)));
  }

  @Override
  public Optional<Entity> get(String kind, long id) {
    return Optional.ofNullable(store.get(buildKey(kind, id)));
  }

  @Override
  public void put(String kind, long id, Entity entity) {
    entity.setKey(new Key().setPath(List.of(new PathElement().setKind(kind).setId(id))));
    store.put(buildKey(kind, id), entity);
  }

  public void put(String kind, String name, Entity entity) {
    entity.setKey(new Key().setPath(List.of(new PathElement().setKind(kind).setName(name))));
    store.put(buildKey(kind, name), entity);
  }

  public void clear() {
    store.clear();
  }

  private String buildKey(String kind, String name) {
    return kind + ":" + name;
  }

  private String buildKey(String kind, long id) {
    return kind + ":" + id;
  }
}
