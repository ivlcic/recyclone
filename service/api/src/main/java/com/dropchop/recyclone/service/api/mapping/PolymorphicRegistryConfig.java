package com.dropchop.recyclone.service.api.mapping;

/**
 * @author Nikola Ivačič <nikola.ivacic@dropchop.org> on 17. 06. 22.
 */
public interface PolymorphicRegistryConfig extends PolymorphicRegistry {
  PolymorphicRegistryConfig registerDtoEntityMapping(Class<?> source, Class<?> target);
  PolymorphicRegistryConfig registerSerializationConfig(SerializationConfig serializationConfig);
}
