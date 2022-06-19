package com.dropchop.shiro.cdi;

import com.dropchop.recyclone.model.api.invoke.ServiceException;
import com.dropchop.recyclone.model.api.security.Constants;
import com.dropchop.recyclone.model.api.security.PermissionBearer;
import com.dropchop.recyclone.model.api.security.annotations.Logical;
import com.dropchop.recyclone.model.api.security.annotations.RequiresPermissions;
import com.dropchop.recyclone.service.api.invoke.SecurityExecContext;
import com.dropchop.recyclone.service.api.security.AuthorizationService;
import com.dropchop.shiro.filter.AccessControlFilter;
import com.dropchop.shiro.jaxrs.ShiroSecurityContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.aop.AuthorizingAnnotationHandler;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.support.SubjectThreadState;
import org.apache.shiro.util.ThreadState;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.dropchop.recyclone.model.api.invoke.ErrorCode.authentication_error;
import static com.dropchop.recyclone.model.api.invoke.ErrorCode.authorization_error;

/**
 * @author Nikola Ivačič <nikola.ivacic@dropchop.org> on 19. 06. 22.
 */
@Slf4j
@ApplicationScoped
public class ShiroAuthorizationService implements AuthorizationService {

  @Inject
  org.apache.shiro.mgt.SecurityManager securityManager;

  @Inject
  @SuppressWarnings("CdiInjectionPointsInspection")
  List<AccessControlFilter> accessControlFilters;

  public void bindSubjectToThreadStateInRequestContext(ContainerRequestContext requestContext) {
    Subject subject = new Subject.Builder(securityManager).buildSubject();
    ThreadState threadState = new SubjectThreadState(subject);
    threadState.bind();
    requestContext.setProperty("shiro.req.internal.thread.state", threadState);
    requestContext.setSecurityContext(new ShiroSecurityContext(requestContext));
  }

  @Produces
  @RequestScoped
  public Subject subject() {
    return SecurityUtils.getSubject();
  }

  public void invokeFilterChain(ContainerRequestContext requestContext) {
    try {
      for (AccessControlFilter filter: accessControlFilters) {
        boolean proceed = filter.isAccessAllowed(requestContext) || filter.onAccessDenied(requestContext);
        if (!proceed) {
          break;
        }
      }
    } catch (AuthorizationException e) {
      unbindSubjectFromThreadStateInRequestContext(requestContext);
      if (e instanceof UnauthenticatedException) {
        throw new ServiceException(authentication_error, "User is unauthenticated.");
      } else {
        throw new ServiceException(authorization_error, "User is unauthorized.");
      }
    }
  }

  public void extractRequiredPermissionsToExecContext(SecurityExecContext execContext,
                                                      Map<AuthorizingAnnotationHandler, Annotation> authzChecks) {
    String[] requiredPermissions = null;
    Logical requiredPermissionsOp = Logical.AND;
    for (Map.Entry<AuthorizingAnnotationHandler, Annotation> authzCheck : authzChecks.entrySet()) {
      AuthorizingAnnotationHandler handler = authzCheck.getKey();
      Annotation authzSpec = authzCheck.getValue();
      if (authzSpec instanceof RequiresPermissions) {
        requiredPermissions = ((RequiresPermissions) authzSpec).value();
        requiredPermissionsOp = ((RequiresPermissions) authzSpec).logical();
      }
      handler.assertAuthorized(authzSpec);
    }

    if (requiredPermissions != null && requiredPermissions.length > 0) {
      execContext.setRequiredPermissions(Arrays.asList(requiredPermissions));
      execContext.setRequiredPermissionsOp(requiredPermissionsOp);
    }
  }

  public void unbindSubjectFromThreadStateInRequestContext(ContainerRequestContext requestContext) {
    Object threadStateObj = requestContext.getProperty("shiro.req.internal.thread.state");
    if (threadStateObj instanceof ThreadState threadState) {
      threadState.clear();
    }
  }

  public boolean isSubjectPermited(Iterable<String> permissions, Logical op) {
    if (op == null) {
      op = Logical.AND;
    }
    Subject subject = SecurityUtils.getSubject();
    if (subject == null) {
      log.warn("Shiro Subject is missing!");
      return false;
    }
    for (String permission : permissions) {
      if (Logical.AND.equals(op)) {
        if (!subject.isPermitted(permission)) {
          return false;
        }
      } else if (Logical.OR.equals(op)) {
        if (subject.isPermitted(permission)) {
          return true;
        }
      }
    }
    return Logical.AND.equals(op);
  }

  @Override
  public boolean isSubjectPermited(String permission) {
    return this.isSubjectPermited(Collections.singleton(permission), Logical.AND);
  }

  @Override
  public boolean isSubjectPermited(String domain, String action) {
    return this.isSubjectPermited(Constants.Permission.compose(domain, action));
  }

  @Override
  public boolean isSubjectPermited(PermissionBearer subject, String domain, String action) {
    throw new UnsupportedOperationException("not yet implemented");
  }

  @Override
  public boolean isSubjectPermited(PermissionBearer subject, String domainAction) {
    throw new UnsupportedOperationException("not yet implemented");
  }

  @Override
  public PermissionBearer getCurrentSubject() {
    Subject subject = SecurityUtils.getSubject();
    Object principal = subject.getPrincipal();
    if (principal == null) {
      log.warn("Shiro Subject is missing its principal!");
      return null;
    }
    if (principal instanceof PermissionBearer permissionBearer) {
      return permissionBearer;
    }
    log.warn("Shiro Subject principal is not of type [{}]!", PermissionBearer.class);
    return null;
  }
}
