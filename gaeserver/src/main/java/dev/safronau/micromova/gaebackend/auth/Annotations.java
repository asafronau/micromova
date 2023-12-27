package dev.safronau.micromova.gaebackend.auth;

import jakarta.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class Annotations {
  @Qualifier
  @Retention(RetentionPolicy.RUNTIME)
  public @interface CurrentUserId {}

  @Qualifier
  @Retention(RetentionPolicy.RUNTIME)
  public @interface CurrentUserKey {}
}
