package dev.safronau.micromova.gaebackend.services;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.datastore.v1.Datastore;
import com.google.api.services.datastore.v1.model.CommitRequest;
import com.google.api.services.datastore.v1.model.Entity;
import com.google.api.services.datastore.v1.model.EntityResult;
import com.google.api.services.datastore.v1.model.Key;
import com.google.api.services.datastore.v1.model.LookupRequest;
import com.google.api.services.datastore.v1.model.LookupResponse;
import com.google.api.services.datastore.v1.model.Mutation;
import com.google.api.services.datastore.v1.model.PathElement;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.common.collect.MoreCollectors;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Property;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.ReflectiveAccess;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Optional;

@Singleton
@Bean
@Requires(notEnv = "test")
public class DataStoreImpl implements DataStore {

  @Property(name = "mova.google_project_id")
  @ReflectiveAccess
  private String projectId;

  private final Datastore datastore;

  @Inject
  DataStoreImpl() throws GeneralSecurityException, IOException {
    datastore =
        new Datastore.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(GoogleCredentials.getApplicationDefault()))
            .setApplicationName(projectId)
            .build();
  }

  @Override
  public Optional<Entity> get(String kind, String name) {
    try {
      return lookup(new PathElement().setKind(kind).setName(name));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Optional<Entity> get(String kind, long id) {
    try {
      return lookup(new PathElement().setKind(kind).setId(id));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void put(String kind, long id, Entity entity) {
    entity.setKey(new Key().setPath(List.of(new PathElement().setKind(kind).setId(id))));
    try {
      datastore
          .projects()
          .commit(
              projectId,
              new CommitRequest()
                  .setMode("NON_TRANSACTIONAL")
                  .setMutations(List.of(new Mutation().setUpsert(entity))))
          .execute();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private Optional<Entity> lookup(PathElement path) throws IOException {
    LookupResponse response =
        datastore
            .projects()
            .lookup(
                projectId, new LookupRequest().setKeys(List.of(new Key().setPath(List.of(path)))))
            .execute();
    if (response.getFound() == null) {
      return Optional.empty();
    }
    return response.getFound().stream()
        .map(EntityResult::getEntity)
        .collect(MoreCollectors.toOptional());
  }
}
