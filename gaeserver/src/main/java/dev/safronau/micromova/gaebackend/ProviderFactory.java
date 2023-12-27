package dev.safronau.micromova.gaebackend;

import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import java.time.Clock;

@Factory
public class ProviderFactory {
  @Bean
  Clock provideClock() {
    return Clock.systemDefaultZone();
  }
}
