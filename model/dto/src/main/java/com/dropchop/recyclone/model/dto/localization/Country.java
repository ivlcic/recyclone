package com.dropchop.recyclone.model.dto.localization;

import com.dropchop.recyclone.model.dto.base.DtoCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.Set;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

/**
 * Country with ISO 3166 2-letter code.
 *
 * @author Nikola Ivačič <nikola.ivacic@dropchop.org> on 17. 12. 21.
 */
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@JsonInclude(NON_NULL)
public class Country extends DtoCode
  implements com.dropchop.recyclone.model.api.localization.Country<TitleTranslation> {

  @NonNull
  private String code;

  private String title;

  private String lang;

  private Set<TitleTranslation> translations;
}
