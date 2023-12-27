package dev.safronau.micromova.gaebackend.auth;

import dev.safronau.micromova.gaebackend.auth.Annotations.CurrentUserId;
import dev.safronau.micromova.gaebackend.auth.Annotations.CurrentUserKey;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.context.ServerRequestContext;
import io.micronaut.http.cookie.Cookie;
import io.micronaut.http.cookie.Cookies;
import io.micronaut.runtime.http.scope.RequestScope;
import java.util.Optional;

@RequestScope
public class User {

  public static final String USER_ID_HEADER = "X-Mova-User-Id";
  public static final String USER_KEY_HEADER = "X-Mova-User-Key";

  @Factory
  static final class UserFactory {
    @CurrentUserId
    @Bean
    String provideCurrentUserId() {
      return getCookies()
          .flatMap(cookies -> cookies.findCookie(USER_ID_HEADER).map(Cookie::getValue))
          .orElse("");
    }

    @CurrentUserKey
    @Bean
    String provideCurrentUserKey() {
      return getCookies()
          .flatMap(cookies -> cookies.findCookie(USER_KEY_HEADER).map(Cookie::getValue))
          .orElse("");
    }

    private static Optional<Cookies> getCookies() {
      return ServerRequestContext.currentRequest().map(HttpRequest::getCookies);
    }
  }
}
