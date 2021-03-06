package com.dropchop.recyclone.rest.jaxrs.server.localization;

import com.dropchop.recyclone.model.api.invoke.ErrorCode;
import com.dropchop.recyclone.model.api.invoke.Params;
import com.dropchop.recyclone.model.api.invoke.ServiceException;
import com.dropchop.recyclone.model.api.rest.Constants.Paths;
import com.dropchop.recyclone.model.dto.invoke.CodeParams;
import com.dropchop.recyclone.model.dto.localization.Country;
import com.dropchop.recyclone.model.dto.rest.Result;
import com.dropchop.recyclone.model.dto.invoke.CommonExecContext;
import com.dropchop.recyclone.service.api.ServiceSelector;
import com.dropchop.recyclone.service.api.localization.CountryService;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Path;
import java.util.List;

/**
 * @author Nikola Ivačič <nikola.ivacic@dropchop.org> on 22. 01. 22.
 */
@Slf4j
@RequestScoped
@Path(Paths.PUBLIC_SEGMENT + Paths.Localization.COUNTRY)
public class CountryResource implements
  com.dropchop.recyclone.rest.jaxrs.api.localization.CountryResource {

  @Inject
  ServiceSelector selector;

  @Inject
  @SuppressWarnings("CdiInjectionPointsInspection")
  CommonExecContext<Country> ctx;

  @Override
  public Result<Country> get() {
    return selector.select(CountryService.class).search();
  }

  @Override
  public Result<Country> getByCode(String code) {
    Params params = ctx.getParams();
    if (!(params instanceof CodeParams codeParams)) {
      throw new ServiceException(ErrorCode.parameter_validation_error,
        String.format("Invalid parameter type: should be [%s]", CodeParams.class));
    }
    codeParams.setCodes(List.of(code));
    return selector.select(CountryService.class).search();
  }

  @Override
  public Result<Country> search(CodeParams parameters) {
    return selector.select(CountryService.class).search();
  }
}
