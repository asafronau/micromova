package dev.safronau.micromova.gaebackend.services;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.storage.Storage;
import com.google.api.services.storage.model.ObjectAccessControl;
import com.google.api.services.storage.model.Objects;
import com.google.api.services.storage.model.StorageObject;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.common.collect.ImmutableList;
import com.google.common.net.MediaType;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Property;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.ReflectiveAccess;
import io.micronaut.http.HttpStatus;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

@Singleton
@Bean
@Requires(notEnv = "test")
public class GoogleCloudStorage implements FileStorage {

  @Property(name = "mova.google_project_id")
  @ReflectiveAccess
  private String projectId;

  @Property(name = "mova.storage_bucket")
  @ReflectiveAccess
  private String storageBucket;

  private final Storage storage;

  @Inject
  GoogleCloudStorage() throws IOException, GeneralSecurityException {
    storage =
        new Storage.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(GoogleCredentials.getApplicationDefault()))
            .setApplicationName(projectId)
            .build();
  }

  @Override
  public ImmutableList<String> list(String prefix) {
    try {
      Storage.Objects.List listRequest = storage.objects().list(storageBucket).setPrefix(prefix);
      ImmutableList.Builder<String> results = ImmutableList.builder();
      Objects objects;
      do {
        objects = listRequest.execute();
        results.addAll(objects.getItems().stream().map(StorageObject::getName).toList());
        listRequest.setPageToken(objects.getNextPageToken());
      } while (objects.getNextPageToken() != null);
      return results.build();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public byte[] read(String path) {
    try {
      Storage.Objects.Get getRequest = storage.objects().get(storageBucket, path);
      return getRequest.executeMedia().getContent().readAllBytes();
    } catch (IOException e) {
      if (e instanceof GoogleJsonResponseException googleException) {
        if (googleException.getStatusCode() == HttpStatus.NOT_FOUND.getCode()) {
          return null;
        }
      }
      throw new RuntimeException(e);
    }
  }

  @Override
  public String write(byte[] contents, String path, MediaType contentType, boolean isPublic) {
    StorageObject metadata = new StorageObject().setName(path);
    if (isPublic) {
      metadata.setAcl(List.of(new ObjectAccessControl().setEntity("allUsers").setRole("READER")));
    }
    try {
      Storage.Objects.Insert insertRequest =
          storage
              .objects()
              .insert(
                  storageBucket,
                  metadata,
                  new InputStreamContent(
                      contentType.toString(), new ByteArrayInputStream(contents)));
      return insertRequest.execute().getName();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
