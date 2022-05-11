package com.dropchop.recyclone.model.api.attr;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * @author Nikola Ivačič <nikola.ivacic@dropchop.org> on 20. 11. 21.
 */
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public abstract class AttributeBase<T> implements Attribute<T> {
  @NonNull
  private String name;
}
