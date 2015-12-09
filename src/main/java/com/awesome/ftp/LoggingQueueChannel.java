package com.awesome.ftp;

import org.springframework.integration.channel.QueueChannel;
import org.springframework.messaging.Message;

public class LoggingQueueChannel extends QueueChannel {

    @Override
    protected Message<?> doReceive(long timeout) {
        Message<?> message = super.doReceive(timeout);
        Object payload = message.getPayload();

        System.out.println("payload = " + payload);

        return message;
    }
}
