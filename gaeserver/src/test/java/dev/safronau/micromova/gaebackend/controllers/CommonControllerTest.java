package dev.safronau.micromova.gaebackend.controllers;

import com.google.api.services.datastore.v1.model.Entity;
import com.google.api.services.datastore.v1.model.Value;
import com.google.protobuf.Message;
import dev.safronau.micromova.gaebackend.controllers.fakes.FakeDataStore;
import dev.safronau.micromova.gaebackend.controllers.fakes.FakeFileStorage;
import dev.safronau.micromova.gaebackend.services.Constants;
import io.micronaut.context.ApplicationContext;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.cookie.Cookie;
import io.micronaut.runtime.server.EmbeddedServer;
import jakarta.inject.Inject;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

abstract class CommonControllerTest {
  static final String FAKE_USER_ID = "fake-user-id";
  static final String FAKE_USER_KEY = "fake-user-key";

  static EmbeddedServer server;

  @Inject FakeDataStore fakeDataStore;
  @Inject FakeFileStorage fakeFileStorage;

  @Inject
  @Client("/")
  HttpClient client;

  @BeforeEach
  public void setUp() {
    fakeDataStore.put(
        Constants.USER_KEY_KIND,
        FAKE_USER_KEY,
        new Entity().setProperties(Map.of("userName", new Value().setStringValue(FAKE_USER_ID))));
    server = ApplicationContext.run(EmbeddedServer.class);
  }

  @AfterEach
  public void stopServer() {
    fakeDataStore.clear();
    fakeFileStorage.clear();
    server.stop();
  }

  <T extends Message> HttpRequest<byte[]> buildRequest(String url, T message) {
    return HttpRequest.POST(url, message.toByteArray())
        .header("Content-Type", "application/x-protobuf")
        .cookie(Cookie.of("X-Mova-User-Id", FAKE_USER_ID))
        .cookie(Cookie.of("X-Mova-User-Key", FAKE_USER_KEY));
  }
}
