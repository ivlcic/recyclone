package com.dropchop.recyclone.service.jpa.blaze;

import com.dropchop.recyclone.model.api.base.Dto;
import com.dropchop.recyclone.model.api.base.Entity;
import com.dropchop.recyclone.model.api.invoke.CommonParams;
import com.dropchop.recyclone.model.api.security.Constants;
import com.dropchop.recyclone.service.api.invoke.FilteringDtoContext;
import com.dropchop.recyclone.service.api.invoke.MappingContext;
import com.dropchop.recyclone.service.api.mapping.EntityDelegateFactory;
import com.dropchop.recyclone.service.api.mapping.SetDeactivated;
import com.dropchop.recyclone.service.api.mapping.SetModification;
import com.dropchop.recyclone.service.jpa.blaze.localization.LanguageService;
import com.dropchop.recyclone.service.jpa.blaze.mapping.SetLanguage;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Nikola Ivačič <nikola.ivacic@dropchop.org> on 9. 03. 22.
 */
@Slf4j
public abstract class CrudServiceImpl<D extends Dto, P extends CommonParams, E extends Entity, ID>
  extends BaseCrudService<D, P, E, ID> {

  protected MappingContext<P> constructToEntityMappingContext(ServiceConfiguration<D, P, E, ID> conf) {
    Class<?> rootClass = conf.getRepository().getRootClass();
    return new FilteringDtoContext<P>()
      .of(ctx)
      .createWith(
        new EntityDelegateFactory<D, E, ID, P>(this)
          .forActionOnly(Constants.Actions.UPDATE)
          .forActionOnly(Constants.Actions.DELETE)
      )
      .afterMapping(
        new SetModification<>(rootClass)
      )
      .afterMapping(
        new SetLanguage<>(serviceSelector.select(LanguageService.class), rootClass)
      )
      .afterMapping(
        new SetDeactivated<>(rootClass)
      );
  }
}