package com.awesome.ftp;

import org.springframework.integration.channel.interceptor.WireTap;
import org.springframework.integration.config.GlobalChannelInterceptor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

@GlobalChannelInterceptor
public class AwesomeChannelInterceptor extends WireTap {

    public AwesomeChannelInterceptor(MessageChannel channel) {
        super(channel);
    }

    @Override
    public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
        super.postSend(message, channel, sent);


        String content = null;
        try {
            content = new Scanner((File) message.getPayload()).useDelimiter("\\Z").next();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println("---------------------------------------------------------");
        System.out.println(content);
    }
}
