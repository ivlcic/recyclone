package com.dropchop.recyclone.service.api.mapping;

import com.dropchop.recyclone.model.api.Dto;
import com.dropchop.recyclone.model.api.Entity;
import com.dropchop.recyclone.model.api.invoke.Params;

/**
 * @author Nikola Ivačič <nikola.ivacic@dropchop.org> on 29. 04. 22.
 */
public interface BeforeToDtoListener<P extends Params>
  extends MappingListener<P> {
  void before(Entity entity, Dto dto, MappingContext<P> context);
}
