package org.mardep.ssrs.dns;

import org.springframework.messaging.MessagingException;

public interface IMessagingExceptionService<T> {
	public T handleMessagingException(MessagingException e);
}
