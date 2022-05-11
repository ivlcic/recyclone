package com.dropchop.recyclone.model.api.aspect;

import com.dropchop.recyclone.model.api.marker.HasUuidV1;
import com.dropchop.recyclone.model.api.marker.state.HasCreated;
import com.dropchop.recyclone.model.api.utils.Uuid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * @author Nikola Ivačič <nikola.ivacic@dropchop.org> on 7. 01. 22.
 */
public interface TimebasedUuidWeaver {

  @java.lang.SuppressWarnings("all")
  Logger log = LoggerFactory.getLogger(TimebasedUuidWeaver.class);

  default void changeClassWithCreated(Object oModel, ZonedDateTime created) {
    HasUuidV1 model = (HasUuidV1)oModel;
    UUID oldUuid = model.getUuid();
    if (oldUuid == null || oldUuid.version() != 1) {
      UUID newUuid = Uuid.fromTimeAndName(created.toInstant(), Uuid.getRandom());
      log.trace("Will change uuid {} with {} based on created {}", model.getUuid(), newUuid, created);
      model.setUuid(newUuid);
    } else {
      log.trace("Will not change uuid {} based on created {}", model.getUuid(), created);
    }
  }

  default void changeClassWithUuid(Object oModel, UUID uuid) {
    HasUuidV1 model = (HasUuidV1)oModel;
    if (uuid.version() == 1) {
      Instant instant = Uuid.toInstant(uuid);
      ZonedDateTime created = instant.atZone(ZoneId.systemDefault());
      log.trace("Will change created {} with {} based on uuid {}", model.getUuid(), created, uuid);
      ((HasCreated)model).setCreated(created);
    }
  }
}
