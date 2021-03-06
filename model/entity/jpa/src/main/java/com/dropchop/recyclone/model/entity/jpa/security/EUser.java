package com.dropchop.recyclone.model.entity.jpa.security;

import com.dropchop.recyclone.model.api.localization.Country;
import com.dropchop.recyclone.model.api.security.User;
import com.dropchop.recyclone.model.entity.jpa.common.EPerson;
import com.dropchop.recyclone.model.entity.jpa.base.EUuid;
import com.dropchop.recyclone.model.entity.jpa.localization.ECountry;
import com.dropchop.recyclone.model.entity.jpa.localization.ELanguage;
import com.dropchop.recyclone.model.entity.jpa.localization.ETitleTranslation;
import com.dropchop.recyclone.model.entity.jpa.tagging.ETag;
import lombok.*;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.SortedSet;
import java.util.UUID;

/**
 * @author Nikola Ivačič <nikola.ivacic@dropchop.org> on 7. 01. 22.
 */
@Getter
@Setter
@Entity
@Table(name = "\"user\"")
@NoArgsConstructor
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
public class EUser<O extends EUuid> extends EPerson
  implements User<EUserAccount, ETitleTranslation, EAction, EDomain, EPermission, ERole, O, ECountry, ELanguage, ETag> {

  @Transient
  List<EUserAccount> accounts;

  @Transient
  SortedSet<EPermission> permissions;

  @ManyToMany(targetEntity = ERole.class)
  @JoinTable(name = "security_user_role",
    joinColumns = {@JoinColumn(name = "fk_user_uuid", foreignKey = @ForeignKey(name = "security_user_role_fk_user_uuid"))},
    inverseJoinColumns = {@JoinColumn(name = "fk_role_code", foreignKey = @ForeignKey(name = "security_user_role_fk_role_code"))}
  )
  @OrderBy("code ASC")
  SortedSet<ERole> roles;

  @Column(name="default_email")
  private String defaultEmail;

  @Column(name="default_phone")
  private String defaultPhone;

  @Column(name="created")
  private ZonedDateTime created;

  @Column(name="modified")
  private ZonedDateTime modified;

  @Column(name="deactivated")
  private ZonedDateTime deactivated;

  @Column(name="owner_uuid")
  private UUID ownerUuid;

  @Column(name="owner_type")
  private String ownerType;

  @Transient
  private O owner;

  @Transient
  private List<ETag> tags;
}
