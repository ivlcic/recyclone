package com.dropchop.recyclone.rest.jaxrs.api.localization;

import com.dropchop.recyclone.model.api.rest.Constants.Paths;
import com.dropchop.recyclone.model.api.rest.Constants.Tags;
import com.dropchop.recyclone.model.api.security.Constants.Actions;
import com.dropchop.recyclone.model.api.security.Constants.Domains;
import com.dropchop.recyclone.model.api.security.annotations.RequiresPermissions;
import com.dropchop.recyclone.model.dto.invoke.CodeParams;
import com.dropchop.recyclone.model.dto.localization.Country;
import com.dropchop.recyclone.model.dto.rest.Result;
import com.dropchop.recyclone.rest.jaxrs.api.ClassicRestResource;
import com.dropchop.recyclone.rest.jaxrs.api.DynamicExecContext;
import com.dropchop.recyclone.rest.jaxrs.api.MediaType;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.ws.rs.*;
import java.util.List;

import static com.dropchop.recyclone.model.api.security.Constants.PERM_DELIM;

/**
 * @author Nikola Ivačič <nikola.ivacic@dropchop.org> on 12. 01. 22.
 */
@Path(Paths.Localization.COUNTRY)
@DynamicExecContext(CodeParams.class)
@RequiresPermissions(Domains.Localization.COUNTRY + PERM_DELIM + Actions.VIEW)
public interface CountryResource extends ClassicRestResource<Country> {

  @GET
  @Path("")
  @Tag(name = Tags.LOCALIZATION)
  @Tag(name = Tags.DynamicContext.PUBLIC)
  @Tag(name = Tags.DYNAMIC_PARAMS + Tags.DYNAMIC_DELIM + "com.dropchop.recyclone.model.dto.invoke.CodeParams")
  @Produces(MediaType.APPLICATION_JSON_DROPCHOP_RESULT)
  Result<Country> get();

  @GET
  @Path("")
  @Tag(name = Tags.LOCALIZATION)
  @Tag(name = Tags.DynamicContext.PUBLIC)
  @Tag(name = Tags.DYNAMIC_PARAMS + Tags.DYNAMIC_DELIM + "com.dropchop.recyclone.model.dto.invoke.CodeParams")
  @Produces(MediaType.APPLICATION_JSON)
  default List<Country> getRest() {
    return unwrap(get());
  }

  @GET
  @Path("{code : [a-z]{2}(([\\-\\w])*(%2A)*)*}")
  @Tag(name = Tags.LOCALIZATION)
  @Tag(name = Tags.DynamicContext.PUBLIC)
  @Tag(name = Tags.DYNAMIC_PARAMS + Tags.DYNAMIC_DELIM + "com.dropchop.recyclone.model.dto.invoke.CodeParams")
  @Produces(MediaType.APPLICATION_JSON_DROPCHOP_RESULT)
  Result<Country> getByCode(@PathParam("code") String code);

  @GET
  @Path("{code : [a-z]{2}(([\\-\\w])*(%2A)*)*}")
  @Tag(name = Tags.LOCALIZATION)
  @Tag(name = Tags.DynamicContext.PUBLIC)
  @Tag(name = Tags.DYNAMIC_PARAMS + Tags.DYNAMIC_DELIM + "com.dropchop.recyclone.model.dto.invoke.CodeParams")
  @Produces(MediaType.APPLICATION_JSON)
  default List<Country> getByCodeRest(@PathParam("code") String code) {
    return unwrap(getByCode(code));
  }

  @POST
  @Path(Paths.SEARCH_SEGMENT)
  @Tag(name = Tags.LOCALIZATION)
  @Tag(name = Tags.DynamicContext.PUBLIC)
  @Produces(MediaType.APPLICATION_JSON_DROPCHOP_RESULT)
  Result<Country> search(CodeParams parameters);

  @POST
  @Path(Paths.SEARCH_SEGMENT)
  @Tag(name = Tags.LOCALIZATION)
  @Tag(name = Tags.DynamicContext.PUBLIC)
  @Produces(MediaType.APPLICATION_JSON)
  default List<Country> searchRest(CodeParams parameters) {
    return unwrap(search(parameters));
  }
}
