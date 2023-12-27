package dev.safronau.micromova.gaebackend.services;

import com.google.api.services.datastore.v1.model.Entity;
import java.util.Optional;

public interface DataStore {
  Optional<Entity> get(String kind, String name);

  Optional<Entity> get(String kind, long id);

  void put(String kind, long id, Entity entity);
}
