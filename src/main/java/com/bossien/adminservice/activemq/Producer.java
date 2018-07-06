package com.bossien.adminservice.activemq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.jms.Destination;

@Service("producer")
public class Producer {

//    @Autowired
//    private JmsMessagingTemplate jmsMessagingTemplate;
//
//    public void send(Destination destination, final String message) {
//        this.jmsMessagingTemplate.convertAndSend(destination, message);
//    }

//    @JmsListener(destination="out.queue")
//    public void consumerMessage(String text){
//        System.out.println("从out.queue队列收到的回复报文为:"+text);
//    }
}
