package com.bossien.adminservice.activemq;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

@Component
public class ResponseConsumer {

//    @JmsListener(destination = "test.queue")
//    @SendTo("out.queue")
//    public String receiveQueue(String text) {
//        System.out.println("ResponseConsumer收到的消息为:"+text);
//        return "return message"+text;
//    }
}
