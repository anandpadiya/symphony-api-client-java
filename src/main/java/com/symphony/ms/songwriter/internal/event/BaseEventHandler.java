package com.symphony.ms.songwriter.internal.event;

import com.symphony.ms.songwriter.internal.event.model.BaseEvent;

/**
 * Base interface for all EventHandlers
 *
 * @author Marcus Secato
 *
 * @param <E> - class of the event to handle
 */
public interface BaseEventHandler<E extends BaseEvent> {

  /**
   * Callback for when the event is received
   *
   * @param event
   */
  void onEvent(E event);
}
