package com.dropchop.recyclone.service.api.security;

import com.dropchop.recyclone.model.api.security.Constants;
import com.dropchop.recyclone.model.dto.security.Action;
import com.dropchop.recyclone.service.api.CrudService;

/**
 * @author Nikola Ivačič <nikola.ivacic@dropchop.org> on 20. 12. 21.
 */
public interface ActionService extends CrudService<Action> {
  @Override
  default String getSecurityDomain() {
    return Constants.Domains.Security.ACTION;
  }
}
