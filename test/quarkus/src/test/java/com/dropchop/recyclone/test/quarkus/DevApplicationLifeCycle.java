package com.dropchop.recyclone.test.quarkus;

import io.quarkus.arc.profile.IfBuildProfile;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import io.quarkus.runtime.configuration.ProfileManager;
import lombok.extern.slf4j.Slf4j;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.ext.ScriptUtils;
import org.testcontainers.jdbc.JdbcDatabaseDelegate;
import org.testcontainers.shaded.org.apache.commons.io.IOUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * @author Nikola Ivačič <nikola.ivacic@dropchop.org> on 29. 01. 22.
 */
@Slf4j
@ApplicationScoped
@IfBuildProfile("dev")
public class DevApplicationLifeCycle {

  //PostgreSQLContainer<?> postgresqlContainer = null;

  void onStart(@Observes StartupEvent ev) {
    /*log.info("The application is starting with profile " + ProfileManager.getActiveProfile());
    postgresqlContainer = new PostgreSQLContainer<>("postgres:13.2")
      .withDatabaseName("last_dev_test")
      .withUsername("last_dev_test")
      .withPassword("last_dev_test")
    //.withInitScript("dev-import.sql")
    ;
    postgresqlContainer.start();
    ClassLoader cl = Thread.currentThread().getContextClassLoader();
    try {
      URL url = cl.getResource("import-dev.sql");
      if (url != null) {
        log.warn("[{}]", url);
        String scripts = IOUtils.toString(url, StandardCharsets.UTF_8);
        ScriptUtils.executeDatabaseScript(new JdbcDatabaseDelegate(postgresqlContainer, ""), url.getPath(), scripts);
      }
    } catch (Exception e) {
      log.warn("", e);
    }
    System.setProperty("quarkus.datasource.jdbc.url", "jdbc:postgresql://"
      + postgresqlContainer.getHost() + ":" + postgresqlContainer.getMappedPort(5432) + "/" + postgresqlContainer.getDatabaseName());*/
  }

  void onStop(@Observes ShutdownEvent ev) {
    //postgresqlContainer.stop();
  }
}
