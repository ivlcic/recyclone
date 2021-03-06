package com.dropchop.recyclone.service.api.mapping;

import com.dropchop.recyclone.model.api.base.Dto;
import com.dropchop.recyclone.model.api.base.Entity;
import com.dropchop.recyclone.model.dto.rest.Result;
import com.dropchop.recyclone.model.dto.rest.ResultCode;
import com.dropchop.recyclone.model.dto.rest.ResultStatus;
import com.dropchop.recyclone.service.api.invoke.FilteringDtoContext;
import com.dropchop.recyclone.service.api.invoke.MappingContext;
import org.mapstruct.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

/**
 * @author Nikola Ivačič <nikola.ivacic@dropchop.org> on 29. 04. 22.
 */
public interface ToDtoMapper<D extends Dto, E extends Entity> {

  Logger log = LoggerFactory.getLogger(ToDtoMapper.class);

  D toDto(E entity, @Context MappingContext context);

  default List<D> toDtos(Collection<E> entities, MappingContext context) {
    List<D> dtos = new ArrayList<>(entities.size());
    for (E entity : entities) {
      dtos.add(toDto(entity, context));
    }
    return dtos;
  }

  default Result<D> toDtosResult(Collection<E> entities, MappingContext context, Supplier<ResultStatus> statusSupplier) {
    List<D> dtos = toDtos(entities, context);
    Result<D> objResult = new Result<>();
    if (dtos != null) {
      objResult.getData().addAll(dtos);
    }

    if (context.getTotalCount() <= 0) {
      context.setTotalCount(entities.size());
    }

    ResultStatus status = new ResultStatus(ResultCode.success, 0, context.getTotalCount(), null, null, null);
    if (statusSupplier != null) {
      ResultStatus tmp = statusSupplier.get();
      if (tmp != null) {
        status = tmp;
      }
    }

    objResult.setStatus(status);
    return objResult;
  }

  default Result<D> toDtosResult(Collection<E> entities, MappingContext context) {
    return toDtosResult(entities, context, null);
  }

  @BeforeMapping
  default void before(Object source, @MappingTarget Object target, @Context MappingContext context) {
    if (context instanceof FilteringDtoContext) {
      ((FilteringDtoContext) context).before(source, target);
    }
    if (source instanceof Entity && target instanceof Dto) {
      for (MappingListener listener : context.listeners()) {
        if (listener instanceof BeforeToDtoListener) {
          ((BeforeToDtoListener) listener).before((Entity) source, (Dto) target, context);
        }
      }
    }
  }

  @AfterMapping
  default void after(Object source, @MappingTarget Object target, @Context MappingContext context) {
    if (source instanceof Entity && target instanceof Dto) {
      for (MappingListener listener : context.listeners()) {
        if (listener instanceof AfterToDtoListener) {
          ((AfterToDtoListener) listener).after((Entity) source, (Dto) target, context);
        }
      }
    }
    if (context instanceof FilteringDtoContext) {
      ((FilteringDtoContext) context).after(source, target);
    }
  }

  @Condition
  default boolean filter(@TargetPropertyName String propName, @Context MappingContext context) {
    if (context instanceof FilteringDtoContext) {
      return ((FilteringDtoContext) context).filter(propName);
    }
    return true;
  }
}
