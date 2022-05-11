package com.dropchop.recyclone.model.api.invoke;

import com.dropchop.recyclone.model.api.Dto;
import com.dropchop.recyclone.model.api.invoke.ExecContext.Listener;

import java.util.List;

/**
 * @author Nikola Ivačič <nikola.ivacic@dropchop.org> on 20. 03. 22.
 */
public interface DataExecContext<D extends Dto, L extends Listener> extends ExecContext<L> {
  List<D> getData();
  void setData(List<D> data);
}
