package com.dropchop.recyclone.rest.jaxrs.api.intern.localization;

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
 * @author Nikola Ivačič <nikola.ivacic@dropchop.org> on 22. 01. 22.
 */
@Path(Paths.Localization.COUNTRY)
@DynamicExecContext(value = CodeParams.class, dataClass = Country.class)
@RequiresPermissions(Domains.Localization.COUNTRY + PERM_DELIM + Actions.VIEW)
public interface CountryResource extends ClassicRestResource<Country> {

  @POST
  @Tag(name = Tags.LOCALIZATION)
  @Tag(name = Tags.DynamicContext.INTERNAL)
  @Produces(MediaType.APPLICATION_JSON_DROPCHOP_RESULT)
  @RequiresPermissions(Domains.Localization.COUNTRY + PERM_DELIM + Actions.CREATE)
  Result<Country> create(List<Country> objects);

  @POST
  @Tag(name = Tags.LOCALIZATION)
  @Tag(name = Tags.DynamicContext.INTERNAL)
  @Produces(MediaType.APPLICATION_JSON)
  @RequiresPermissions(Domains.Localization.COUNTRY + PERM_DELIM + Actions.CREATE)
  default List<Country> createRest(List<Country> languages) {
    return unwrap(create(languages));
  }

  @PUT
  @Tag(name = Tags.LOCALIZATION)
  @Tag(name = Tags.DynamicContext.INTERNAL)
  @Produces(MediaType.APPLICATION_JSON_DROPCHOP_RESULT)
  @RequiresPermissions(Domains.Localization.COUNTRY + PERM_DELIM + Actions.UPDATE)
  Result<Country> update(List<Country> objects);

  @PUT
  @Tag(name = Tags.LOCALIZATION)
  @Tag(name = Tags.DynamicContext.INTERNAL)
  @Produces(MediaType.APPLICATION_JSON)
  @RequiresPermissions(Domains.Localization.COUNTRY + PERM_DELIM + Actions.UPDATE)
  default List<Country> updateRest(List<Country> languages) {
    return unwrap(update(languages));
  }

  @DELETE
  @Tag(name = Tags.LOCALIZATION)
  @Tag(name = Tags.DynamicContext.INTERNAL)
  @Produces(MediaType.APPLICATION_JSON_DROPCHOP_RESULT)
  @RequiresPermissions(Domains.Localization.COUNTRY + PERM_DELIM + Actions.DELETE)
  Result<Country> delete(List<Country> objects);

  @DELETE
  @Tag(name = Tags.LOCALIZATION)
  @Tag(name = Tags.DynamicContext.INTERNAL)
  @Produces(MediaType.APPLICATION_JSON)
  @RequiresPermissions(Domains.Localization.COUNTRY + PERM_DELIM + Actions.DELETE)
  default List<Country> deleteRest(List<Country> languages) {
    return unwrap(delete(languages));
  }
}
