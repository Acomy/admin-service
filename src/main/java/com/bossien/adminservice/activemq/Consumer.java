package com.bossien.adminservice.activemq;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class Consumer {

    @JmsListener(destination = "test.queue")
    public void apTxCourseType(String text) {

    }
}
