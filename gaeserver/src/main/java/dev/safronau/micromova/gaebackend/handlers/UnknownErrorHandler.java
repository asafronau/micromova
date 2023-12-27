package dev.safronau.micromova.gaebackend.handlers;

import dev.safronau.micromova.gaebackend.errors.UnknownError;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import jakarta.inject.Singleton;

@Produces
@Singleton
@Requires(classes = {UnknownError.class, ExceptionHandler.class})
public class UnknownErrorHandler implements ExceptionHandler<UnknownError, HttpResponse> {

  @Override
  public HttpResponse handle(HttpRequest request, UnknownError exception) {
    return HttpResponse.serverError(exception.toString());
  }
}
