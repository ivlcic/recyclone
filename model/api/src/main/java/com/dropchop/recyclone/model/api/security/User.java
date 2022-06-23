package com.dropchop.recyclone.model.api.security;

import com.dropchop.recyclone.model.api.base.Model;
import com.dropchop.recyclone.model.api.common.Person;
import com.dropchop.recyclone.model.api.localization.Country;
import com.dropchop.recyclone.model.api.localization.Language;
import com.dropchop.recyclone.model.api.localization.TitleTranslation;

import java.util.List;
import java.util.SortedSet;

/**
 * @author Nikola Ivačič <nikola.ivacic@dropchop.org> on 9. 01. 22.
 */
@SuppressWarnings("unused")
public interface User<
    UA extends UserAccount,
    T extends TitleTranslation,
    A extends Action<T>,
    D extends Domain<T, A>,
    P extends Permission<T, A, D>,
    R extends Role<T, A, D, P>,
    O extends Model,
    C extends Country<T>,
    L extends Language<T>
    >
    extends Person<C, L, T>, PermissionBearer {

  SortedSet<R> getRoles();

  void setRoles(SortedSet<R> roles);

  SortedSet<P> getPermissions();

  void setPermissions(SortedSet<P> permissions);

  List<? extends UA> getAccounts();

  void setAccounts(List<? extends UA> accounts);

  O getOwner();

  void setOwner(O owner);
}
