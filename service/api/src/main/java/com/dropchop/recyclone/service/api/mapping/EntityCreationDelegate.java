package com.dropchop.recyclone.service.api.mapping;

import com.dropchop.recyclone.model.api.base.Dto;
import com.dropchop.recyclone.model.api.base.Entity;
import com.dropchop.recyclone.model.api.invoke.Params;
import com.dropchop.recyclone.service.api.EntityByIdService;

/**
 * @author Nikola Ivačič <nikola.ivacic@dropchop.org> on 31. 05. 22.
 */
public class EntityCreationDelegate<D extends Dto, E extends Entity, ID, P extends Params>
  extends EntityLoadDelegate<D, E, ID, P> implements CreateEntityListener<D, E, P> {

  public EntityCreationDelegate(EntityByIdService<D, E, ID> service) {
    super(service);
  }

  @Override
  public EntityCreationDelegate<D, E, ID, P> forActionOnly(String action) {
    super.forActionOnly(action);
    return this;
  }

  @Override
  public EntityCreationDelegate<D, E, ID, P> failIfMissing(boolean failIfMissing) {
    super.failIfMissing(failIfMissing);
    return this;
  }

  @Override
  public EntityCreationDelegate<D, E, ID, P> failIfPresent(boolean failIfPresent) {
    super.failIfPresent(failIfPresent);
    return this;
  }

  @Override
  public E create(D dto, MappingContext<P> context) {
    return this.load(dto, context);
  }
}
