package dev.safronau.micromova.gaebackend.errors;

/** Catch all server error */
public final class UnknownError extends RuntimeException {
  public UnknownError(String message, Throwable cause) {
    super(message, cause);
  }
}
