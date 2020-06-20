package org.example.payment.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Profile("local")
public class H2 {
  private org.h2.tools.Server webServer;
  private final int port = 8081;

  @EventListener(org.springframework.context.event.ContextRefreshedEvent.class)
  public void start() throws java.sql.SQLException {
    log.info("starting h2 console at port {}", port);
    this.webServer = org.h2.tools.Server.createWebServer("-webPort", String.valueOf(port), "-tcpAllowOthers").start();
  }

  @EventListener(org.springframework.context.event.ContextClosedEvent.class)
  public void stop() {
    log.info("stopping h2 console at port {}", port);
    this.webServer.stop();
  }
}
