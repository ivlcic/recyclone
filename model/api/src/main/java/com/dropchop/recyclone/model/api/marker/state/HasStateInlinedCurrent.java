package com.dropchop.recyclone.model.api.marker.state;

import com.dropchop.recyclone.model.api.State;

import java.time.ZonedDateTime;

/**
 * @author Nikola Ivačič <nikola.ivacic@dropchop.org> on 6. 03. 22.
 */
public interface HasStateInlinedCurrent extends HasStateInlined {

  String getCurrentStateCodeAsString();

  default State.Code getCurrentStateCode() {
    String codeName = getCurrentStateCodeAsString();
    if (codeName == null || codeName.isBlank()) {
      return null;
    }
    return allStateCodesMap().get(codeName);
  }

  void setCurrentStateAsString(String stateCode);

  default void setCurrentState(State.Code code) {
    this.setCurrentStateAsString(code.toString());
  }

  ZonedDateTime getCurrentStateAt();
}
