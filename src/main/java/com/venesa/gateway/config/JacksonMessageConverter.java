package com.venesa.gateway.config;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConversionException;

public class JacksonMessageConverter extends Jackson2JsonMessageConverter {

    public JacksonMessageConverter() {
        super();
    }

    @Override
    public Object fromMessage(Message message) throws MessageConversionException {
       message.getMessageProperties().setContentType("application/json");
        return super.fromMessage(message);
    }
}
