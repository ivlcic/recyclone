package com.dropchop.recyclone.rest.jaxrs.server;

import com.dropchop.recyclone.rest.jaxrs.api.MediaType;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.ext.Provider;

/**
 * @author Nikola Ivačič <nikola.ivacic@dropchop.org> on 16. 01. 22.
 */
@Provider
@PreMatching
public class DefaultContentTypeFilter implements ContainerRequestFilter {

  @Override
  public void filter(ContainerRequestContext ctx) {
    String ctp = ctx.getHeaderString("Accept");
    if (ctp == null) {
      ctx.getHeaders().putSingle("Accept", "application/json; charset=UTF-8");
    }
    if (MediaType.WILDCARD.equals(ctp)) {
      ctx.getHeaders().putSingle("Accept", "application/json; charset=UTF-8");
    }
  }
}
