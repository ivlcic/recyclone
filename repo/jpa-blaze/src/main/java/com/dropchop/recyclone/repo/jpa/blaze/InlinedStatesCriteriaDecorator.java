package com.dropchop.recyclone.repo.jpa.blaze;

import com.blazebit.persistence.CriteriaBuilder;
import com.dropchop.recyclone.model.api.base.State;
import com.dropchop.recyclone.model.api.invoke.Params;
import com.dropchop.recyclone.model.api.marker.state.HasState;
import com.dropchop.recyclone.model.api.marker.state.HasStateInlined;
import com.dropchop.recyclone.model.api.marker.state.HasStateInlinedCommon;
import com.dropchop.recyclone.model.api.marker.state.HasStateInlinedCurrent;
import com.dropchop.recyclone.model.api.invoke.CommonParams;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author Nikola Ivačič <nikola.ivacic@dropchop.org> on 3. 03. 22.
 */
@Slf4j
public class InlinedStatesCriteriaDecorator extends BlazeCriteriaDecorator {

  @Override
  public void decorate() {
    Params params = getContext().getParams();
    if (!(params instanceof CommonParams parameters)) {
      log.warn("Wrong parameters instance [{}] should be [{}]", params.getClass(), CommonParams.class);
      return;
    }
    List<String> showStates = parameters.getStates();
    Collection<State.Code> hiddenStates = parameters.getHiddenStates();
    Class<?> tClass = getContext().getRootClass();
    CriteriaBuilder<?> cb = getContext().getCriteriaBuilder();
    String alias = getContext().getRootAlias();
    if (!HasStateInlined.class.isAssignableFrom(tClass)) {
      return;
    }

    Object tObject;
    try {
      tObject = tClass.getDeclaredConstructor().newInstance();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    Set<? extends State.Code> hiddenCodes = ((HasState)tObject).allStateCodes();
    //noinspection SuspiciousMethodCalls
    if (!hiddenCodes.retainAll(hiddenStates)) {
      return;
    }

    Set<? extends State.Code> showCodes = ((HasState)tObject).allStateCodes();
    showCodes.removeIf(code -> !showStates.contains(code.toString()));

    if (HasStateInlinedCommon.class.isAssignableFrom(tClass)) {
      // AND (deleted is null OR deleted is not null OR deactivated is null OR deactivated is not null)
      if (showCodes.isEmpty()) { // show default view
        for (State.Code hidden : hiddenCodes) {
          cb.where(alias + "." + hidden).isNull();
        }
      }
    }

    if (HasStateInlinedCurrent.class.isAssignableFrom(tClass)) {
      if (showCodes.isEmpty()) { // show default view
        for (State.Code hidden : hiddenCodes) {
          cb.where(alias + ".current_state").notEq(hidden.toString());
        }
      }
    }
  }
}
