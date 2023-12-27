package dev.safronau.micromova.gaebackend.controllers;

import static com.google.common.truth.Truth.assertThat;

import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

@MicronautTest
public class ApplicationTest {
  @Inject EmbeddedApplication application;

  @Test
  void testItWorks() {
    assertThat(application.isRunning()).isTrue();
  }
}
