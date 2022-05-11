package com.dropchop.shiro.cdi;

import com.dropchop.shiro.filter.AccessControlFilter;
import com.dropchop.shiro.filter.BasicHttpAuthenticationFilter;
import com.dropchop.shiro.filter.BearerHttpAuthenticationFilter;
import com.dropchop.shiro.realm.ShiroMapRealm;
import org.apache.shiro.authz.permission.PermissionResolver;
import org.apache.shiro.authz.permission.RolePermissionResolver;
import org.apache.shiro.authz.permission.WildcardPermissionResolver;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.event.EventBus;
import org.apache.shiro.event.support.DefaultEventBus;
import org.apache.shiro.realm.Realm;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import java.util.Collections;
import java.util.List;

/**
 * Modeled and copied from Shiro Spring.
 *
 * @author Nikola Ivačič <nikola.ivacic@dropchop.org> on 6. 01. 22.
 */
@Alternative
@ApplicationScoped
public class ShiroEnvironmentProvider {
  @Produces
  public CacheManager getCacheManager() {
    return null;
  }

  @Produces
  public RolePermissionResolver getRolePermissionResolver() {
    return null;
  }

  @Produces
  public PermissionResolver getPermissionResolver() {
    return new WildcardPermissionResolver();
  }

  @Produces
  public EventBus getEventBus() {
    return new DefaultEventBus();
  }

  @Produces
  public List<Realm> getRealms() {
    return List.of(new ShiroMapRealm(Collections.emptyMap(), Collections.emptyMap()));
  }

  @Produces
  public List<AccessControlFilter> getFilters() {
    return List.of(
      new BasicHttpAuthenticationFilter(),
      new BearerHttpAuthenticationFilter()
    );
  }
}
