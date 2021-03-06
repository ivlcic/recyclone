package com.dropchop.recyclone.rest.jaxrs.server.tagging;

import com.dropchop.recyclone.model.api.invoke.ErrorCode;
import com.dropchop.recyclone.model.api.invoke.Params;
import com.dropchop.recyclone.model.api.invoke.ServiceException;
import com.dropchop.recyclone.model.api.rest.Constants;
import com.dropchop.recyclone.model.dto.invoke.IdentifierParams;
import com.dropchop.recyclone.model.dto.invoke.TagParams;
import com.dropchop.recyclone.model.dto.localization.TitleTranslation;
import com.dropchop.recyclone.model.dto.rest.Result;
import com.dropchop.recyclone.model.dto.tagging.Tag;
import com.dropchop.recyclone.service.api.ServiceSelector;
import com.dropchop.recyclone.model.dto.invoke.CommonExecContext;
import com.dropchop.recyclone.service.api.tagging.TagService;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Path;
import java.util.List;
import java.util.UUID;

/**
 * @author Nikola Ivačič <nikola.ivacic@dropchop.org> on 16. 06. 22.
 */
@Slf4j
@RequestScoped
@Path(Constants.Paths.PUBLIC_SEGMENT + Constants.Paths.Tagging.TAG)
public class TagResource implements com.dropchop.recyclone.rest.jaxrs.api.tagging.TagResource<Tag<TitleTranslation>> {

  @Inject
  ServiceSelector selector;

  @Inject
  @SuppressWarnings("CdiInjectionPointsInspection")
  CommonExecContext<Tag<TitleTranslation>> ctx;

  @Override
  public Result<Tag<TitleTranslation>> get() {
    return selector.select(TagService.class).search();
  }

  @Override
  public Result<Tag<TitleTranslation>> getById(UUID id) {
    Params params = ctx.getParams();
    if (!(params instanceof TagParams tagParams)) {
      throw new ServiceException(ErrorCode.parameter_validation_error,
        String.format("Invalid parameter type: should be [%s]", IdentifierParams.class));
    }
    tagParams.setIdentifiers(List.of(id.toString()));
    return selector.select(TagService.class).search();
  }

  @Override
  public Result<Tag<TitleTranslation>> getByType(String type) {
    Params params = ctx.getParams();
    if (!(params instanceof TagParams tagParams)) {
      throw new ServiceException(ErrorCode.parameter_validation_error,
        String.format("Invalid parameter type: should be [%s]", IdentifierParams.class));
    }
    tagParams.setTypes(List.of(type));
    return selector.select(TagService.class).search();
  }

  @Override
  public Result<Tag<TitleTranslation>> search(TagParams parameters) {
    return selector.select(TagService.class).search();
  }
}
