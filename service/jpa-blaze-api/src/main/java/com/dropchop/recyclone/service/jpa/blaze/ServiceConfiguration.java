package com.dropchop.recyclone.service.jpa.blaze;

import com.dropchop.recyclone.model.api.base.Dto;
import com.dropchop.recyclone.model.api.base.Entity;
import com.dropchop.recyclone.repo.api.CrudRepository;
import com.dropchop.recyclone.repo.jpa.blaze.*;
import com.dropchop.recyclone.service.api.mapping.ToDtoMapper;
import com.dropchop.recyclone.service.api.mapping.ToEntityMapper;

import java.util.List;

/**
 * @author Nikola Ivačič <nikola.ivacic@dropchop.org> on 2. 05. 22.
 */
public class ServiceConfiguration<D extends Dto, E extends Entity, ID> {

  final CrudRepository<E, ID> repository;
  final ToDtoMapper<D, E> toDtoMapper;
  final ToEntityMapper<D, E> toEntityMapper;
  final Iterable<BlazeCriteriaDecorator<E>> criteriaDecorators;

  public ServiceConfiguration(CrudRepository<E, ID> repository,
                              ToDtoMapper<D, E> toDtoMapper,
                              ToEntityMapper<D, E> toEntityMapper,
                              Iterable<BlazeCriteriaDecorator<E>> criteriaDecorators) {
    this.repository = repository;
    this.toDtoMapper = toDtoMapper;
    this.toEntityMapper = toEntityMapper;
    this.criteriaDecorators = criteriaDecorators;
  }

  public ServiceConfiguration(CrudRepository<E, ID> repository,
                              ToDtoMapper<D, E> toDtoMapper,
                              ToEntityMapper<D, E> toEntityMapper) {
    this(repository, toDtoMapper, toEntityMapper, List.of(
      new LikeIdentifierCriteriaDecorator<>(),
      new InlinedStatesCriteriaDecorator<>(),
      new SortCriteriaDecorator<>(),
      new PageCriteriaDecorator<>()
    ));
  }

  public CrudRepository<E, ID> getRepository() {
    return repository;
  }

  public ToDtoMapper<D, E> getToDtoMapper() {
    return toDtoMapper;
  }

  public ToEntityMapper<D, E> getToEntityMapper() {
    return toEntityMapper;
  }

  public Iterable<BlazeCriteriaDecorator<E>> getCriteriaDecorators() {
    return criteriaDecorators;
  }
}
