package com.dropchop.recyclone.service.api.mapping;

import com.dropchop.recyclone.model.api.Dto;
import com.dropchop.recyclone.model.api.Entity;
import com.dropchop.recyclone.model.api.invoke.Params;

/**
 * @author Nikola Ivačič <nikola.ivacic@dropchop.org> on 29. 04. 22.
 */
public interface AfterToEntityListener<P extends Params>
  extends MappingListener<P> {
  void after(Dto dto, Entity entity, MappingContext<P> context);
}
