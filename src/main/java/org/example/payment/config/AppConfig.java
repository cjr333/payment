package org.example.payment.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Hooks;

import javax.annotation.PostConstruct;

@Configuration
@Slf4j
public class AppConfig {
  @Component
  @Profile("local")
  public static class HooksConfig {
    @PostConstruct
    public void init() {
      Hooks.onOperatorDebug();
    }
  }
}
