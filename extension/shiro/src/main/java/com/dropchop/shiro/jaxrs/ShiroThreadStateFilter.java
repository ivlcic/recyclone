package com.dropchop.shiro.jaxrs;

import com.dropchop.shiro.cdi.ShiroAuthorizationService;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;

/**
 * @author Nikola Ivačič <nikola.ivacic@dropchop.org> on 29. 12. 21.
 */
@Slf4j
public class ShiroThreadStateFilter implements ContainerRequestFilter, ContainerResponseFilter {

  private final ShiroAuthorizationService authorizationService;

  public ShiroThreadStateFilter(ShiroAuthorizationService authorizationService) {
    this.authorizationService = authorizationService;
    log.debug("Constructed {}.", this.getClass().getName());
  }

  @Override
  public void filter(ContainerRequestContext requestContext) {
    this.authorizationService.bindSubjectToThreadStateInRequestContext(requestContext);
  }

  @Override
  public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
    this.authorizationService.unbindSubjectFromThreadStateInRequestContext(requestContext);
  }
}
