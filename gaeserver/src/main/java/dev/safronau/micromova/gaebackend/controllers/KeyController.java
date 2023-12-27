package dev.safronau.micromova.gaebackend.controllers;

import dev.safronau.micromova.gaebackend.auth.User;
import dev.safronau.micromova.gaebackend.services.GoogleDatastore;
import io.micronaut.context.annotation.Property;
import io.micronaut.core.annotation.ReflectiveAccess;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.cookie.Cookie;
import io.micronaut.runtime.http.scope.RequestScope;
import jakarta.inject.Inject;
import java.net.URI;
import java.time.Duration;

@RequestScope
@Controller("/key/{keyId}")
public class KeyController {

  private static final Duration COOKIE_TTL = Duration.ofDays(30);

  @Property(name = "mova.domain")
  @ReflectiveAccess
  private String domain;

  private final GoogleDatastore googleDatastore;

  @Inject
  KeyController(GoogleDatastore googleDatastore) {
    this.googleDatastore = googleDatastore;
  }

  @Get
  HttpResponse execute(String keyId) {
    String userId = googleDatastore.getUserId(keyId);
    if (userId.isEmpty()) {
      return HttpResponse.unauthorized();
    }
    return HttpResponse.redirect(URI.create("/#/lists"))
        .cookie(buildCookie(User.USER_ID_HEADER, userId))
        .cookie(buildCookie(User.USER_KEY_HEADER, keyId));
  }

  private Cookie buildCookie(String name, String value) {
    return Cookie.of(name, value)
        .httpOnly(false)
        .secure(true)
        .path("/")
        .domain(domain)
        .maxAge(COOKIE_TTL);
  }
}
