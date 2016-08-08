package com.wangjunneil.schedule.activemq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import javax.jms.*;

/**
 *
 * Created by wangjun on 7/27/16.
 */
public class QueueMessageProducer {

    private JmsTemplate jmsTemplate;

    private Destination jdOrderQueue;

    public void setJdOrderQueue(Destination jdOrderQueue) {
        this.jdOrderQueue = jdOrderQueue;
    }

    public void setJmsTemplate(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void sendJDOrderMessage(String message) {
        jmsTemplate.send(jdOrderQueue, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                TextMessage textMessage = session.createTextMessage();
                textMessage.setText(message);
                return textMessage;
            }
        });
    }

}
