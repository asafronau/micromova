package dev.safronau.micromova.gaebackend.filter;

import dev.safronau.micromova.gaebackend.auth.Annotations.CurrentUserId;
import dev.safronau.micromova.gaebackend.auth.Annotations.CurrentUserKey;
import dev.safronau.micromova.gaebackend.services.GoogleDatastore;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Filter;
import io.micronaut.http.filter.HttpServerFilter;
import io.micronaut.http.filter.ServerFilterChain;
import io.micronaut.runtime.http.scope.RequestScope;
import jakarta.inject.Provider;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

@RequestScope
@Filter("/**")
public class UserIdValidationFilter implements HttpServerFilter {

  private final GoogleDatastore googleDatastore;
  private final Provider<String> userId;
  private final Provider<String> userKey;

  public UserIdValidationFilter(
      GoogleDatastore googleDatastore,
      @CurrentUserId Provider<String> userId,
      @CurrentUserKey Provider<String> userKey) {
    this.googleDatastore = googleDatastore;
    this.userId = userId;
    this.userKey = userKey;
  }

  @Override
  public Publisher<MutableHttpResponse<?>> doFilter(
      HttpRequest<?> request, ServerFilterChain chain) {
    if (request.getPath().startsWith("/key/")
        || request.getPath().startsWith("/_ah/")
        || googleDatastore.isUserValid(userKey.get(), userId.get())) {
      return chain.proceed(request);
    }
    return Mono.just(HttpResponse.unauthorized());
  }
}
