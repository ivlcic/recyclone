package com.dropchop.recyclone.rest.jaxrs.api.intern.security;

import com.dropchop.recyclone.model.api.rest.Constants.Paths;
import com.dropchop.recyclone.model.api.rest.Constants.Tags;
import com.dropchop.recyclone.model.dto.invoke.CodeParams;
import com.dropchop.recyclone.model.dto.rest.Result;
import com.dropchop.recyclone.model.dto.security.Domain;
import com.dropchop.recyclone.rest.jaxrs.api.ClassicRestResource;
import com.dropchop.recyclone.rest.jaxrs.api.DynamicExecContext;
import com.dropchop.recyclone.rest.jaxrs.api.MediaType;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.ws.rs.*;
import java.util.List;

/**
 * @author Nikola Ivačič <nikola.ivacic@dropchop.org> on 20. 01. 22.
 */
@Path(Paths.Security.DOMAIN)
@DynamicExecContext(CodeParams.class)
public interface DomainResource extends ClassicRestResource<Domain> {

  @GET
  @Path("")
  @Tag(name = Tags.SECURITY)
  @Tag(name = Tags.DynamicContext.INTERNAL)
  @Tag(name = Tags.DYNAMIC_PARAMS + Tags.DYNAMIC_DELIM + "com.dropchop.recyclone.model.dto.invoke.CodeParams")
  @Produces(MediaType.APPLICATION_JSON_DROPCHOP_RESULT)
  Result<Domain> get();

  @GET
  @Path("")
  @Tag(name = Tags.SECURITY)
  @Tag(name = Tags.DynamicContext.INTERNAL)
  @Produces(MediaType.APPLICATION_JSON)
  @Tag(name = Tags.DYNAMIC_PARAMS + Tags.DYNAMIC_DELIM + "com.dropchop.recyclone.model.dto.invoke.CodeParams")
  default List<Domain> getRest() {
    return unwrap(get());
  }

  @GET
  @Path("{code : [a-z_\\-.]{3,255}}")
  @Tag(name = Tags.SECURITY)
  @Tag(name = Tags.DynamicContext.INTERNAL)
  @Tag(name = Tags.DYNAMIC_PARAMS + Tags.DYNAMIC_DELIM + "com.dropchop.recyclone.model.dto.invoke.CodeParams")
  @Produces(MediaType.APPLICATION_JSON_DROPCHOP_RESULT)
  Result<Domain> getByCode(@PathParam("code") String code);

  @GET
  @Path("{code : [a-z_\\-.]{3,255}}")
  @Tag(name = Tags.SECURITY)
  @Tag(name = Tags.DynamicContext.INTERNAL)
  @Tag(name = Tags.DYNAMIC_PARAMS + Tags.DYNAMIC_DELIM + "com.dropchop.recyclone.model.dto.invoke.CodeParams")
  @Produces(MediaType.APPLICATION_JSON)
  default List<Domain> getByCodeRest(@PathParam("code") String code) {
    return unwrap(getByCode(code));
  }

  @POST
  @Path(Paths.SEARCH)
  @Tag(name = Tags.SECURITY)
  @Tag(name = Tags.DynamicContext.INTERNAL)
  @Produces(MediaType.APPLICATION_JSON_DROPCHOP_RESULT)
  Result<Domain> search(CodeParams params);

  @POST
  @Path(Paths.SEARCH)
  @Tag(name = Tags.SECURITY)
  @Tag(name = Tags.DynamicContext.INTERNAL)
  @Produces(MediaType.APPLICATION_JSON)
  default List<Domain> searchRest(CodeParams params) {
    return unwrap(search(params));
  }

  @POST
  @Tag(name = Tags.SECURITY)
  @Tag(name = Tags.DynamicContext.INTERNAL)
  @Produces(MediaType.APPLICATION_JSON_DROPCHOP_RESULT)
  Result<Domain> create(List<Domain> objects);

  @POST
  @Tag(name = Tags.SECURITY)
  @Tag(name = Tags.DynamicContext.INTERNAL)
  @Produces(MediaType.APPLICATION_JSON)
  default List<Domain> createRest(List<Domain> domains) {
    return unwrap(create(domains));
  }

  @PUT
  @Tag(name = Tags.SECURITY)
  @Tag(name = Tags.DynamicContext.INTERNAL)
  @Produces(MediaType.APPLICATION_JSON_DROPCHOP_RESULT)
  Result<Domain> update(List<Domain> objects);

  @PUT
  @Tag(name = Tags.SECURITY)
  @Tag(name = Tags.DynamicContext.INTERNAL)
  @Produces(MediaType.APPLICATION_JSON)
  default List<Domain> updateRest(List<Domain> domains) {
    return unwrap(update(domains));
  }

  @DELETE
  @Tag(name = Tags.SECURITY)
  @Tag(name = Tags.DynamicContext.INTERNAL)
  @Produces(MediaType.APPLICATION_JSON_DROPCHOP_RESULT)
  Result<Domain> delete(List<Domain> objects);

  @DELETE
  @Tag(name = Tags.SECURITY)
  @Tag(name = Tags.DynamicContext.INTERNAL)
  @Produces(MediaType.APPLICATION_JSON)
  default List<Domain> deleteRest(List<Domain> domains) {
    return unwrap(delete(domains));
  }
}
