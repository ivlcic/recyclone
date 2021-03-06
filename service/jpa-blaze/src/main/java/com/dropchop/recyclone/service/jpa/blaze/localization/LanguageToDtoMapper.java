package com.dropchop.recyclone.service.jpa.blaze.localization;

import com.dropchop.recyclone.model.dto.localization.Language;
import com.dropchop.recyclone.model.entity.jpa.localization.ELanguage;
import com.dropchop.recyclone.service.api.mapping.ToDtoMapper;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * @author Nikola Ivačič <nikola.ivacic@dropchop.org> on 2. 02. 22.
 */
@Mapper(componentModel = "cdi", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LanguageToDtoMapper extends ToDtoMapper<Language, ELanguage> {
}
