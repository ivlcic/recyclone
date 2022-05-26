package com.dropchop.recyclone.service.jpa.blaze;

import com.dropchop.recyclone.model.api.base.Dto;
import com.dropchop.recyclone.model.api.base.Entity;
import com.dropchop.recyclone.model.api.invoke.Params;
import com.dropchop.recyclone.service.api.EntityByIdService;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Nikola Ivačič <nikola.ivacic@dropchop.org> on 26. 05. 22.
 */
public class RelatedAllPreloadedEntityLoader<D extends Dto, E extends Entity, ID, P extends Params>
  extends RelatedEntityLoader<D, E, ID, P> {

  private final Map<String, E> preloaded;

  public RelatedAllPreloadedEntityLoader(EntityByIdService<D, E, ID> service) {
    super(service);
    preloaded = service.findAll()
      .stream()
      .collect(Collectors.toMap(E::identifier, Function.identity()));
  }

  @Override
  protected Optional<E> findById(D dto) {
    return Optional.of(preloaded.get(dto.identifier()));
  }
}
