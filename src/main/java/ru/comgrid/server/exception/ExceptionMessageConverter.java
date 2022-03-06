package ru.comgrid.server.exception;

import org.jetbrains.annotations.NotNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.support.MessageBuilder;

import java.time.LocalDateTime;

public class ExceptionMessageConverter implements MessageConverter{
	@Override
	public Object fromMessage(Message<?> message, Class<?> targetClass){
		return null;
	}

	@Override
	public Message<?> toMessage(@NotNull Object payload, MessageHeaders headers){
		if(!(payload instanceof WrongRequestException exception)){
			return null;
		}
		return MessageBuilder
			.withPayload("{\"timestamp\": \"" + LocalDateTime.now() + "\", \"status\": 500, \"reason\": \"" + exception.getMessage() + "\"}")
			.copyHeaders(headers)
			.build();
	}
}
