package com.dropchop.recyclone.rest.jaxrs.server;

import com.dropchop.recyclone.model.api.Dto;
import com.dropchop.recyclone.service.api.CommonExecContext;
import com.dropchop.recyclone.service.api.CommonExecContextConsumer;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.ReaderInterceptorContext;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This is to intercept methods with Dto data to add them to thread local CommonExecContext var.
 *
 * @author Nikola Ivačič <nikola.ivacic@dropchop.org> on 19. 01. 22.
 */
@Slf4j
public class DtoDataInterceptor implements ReaderInterceptor {

  private final Class<? extends Dto> dtoClass;

  public <D extends Dto> DtoDataInterceptor(Class<D> dtoClass) {
    log.debug("Construct [{}] [{}].", this.getClass().getSimpleName(), dtoClass);
    this.dtoClass = dtoClass;
  }

  @Override
  public Object aroundReadFrom(ReaderInterceptorContext context) throws IOException, WebApplicationException {
    Object o = context.proceed();
    if (o != null && this.dtoClass.isAssignableFrom(o.getClass())) {
      log.debug("Intercept [{}].", o);
      CommonExecContextConsumer.provider.setData(List.of((Dto)o));
    }
    if (o != null && List.class.isAssignableFrom(o.getClass())) {
      if (((List<?>) o).iterator().hasNext()) {
        Object item = ((List<?>) o).iterator().next();
        if (item instanceof Dto) {
          //noinspection unchecked
          CommonExecContextConsumer.provider.setData((List<? extends Dto>) o);
          log.debug("Intercept added collection of [{}].", item.getClass().getSimpleName());
        } else {
          CommonExecContextConsumer.provider.setData(new ArrayList<>());
          log.warn("Skip add data to [{}] since data does not contain [{}] instance!",
            CommonExecContext.class.getSimpleName(), Dto.class.getSimpleName());
        }
      } else {
        CommonExecContextConsumer.provider.setData(new ArrayList<>());
      }
    }
    return o;
  }
}
