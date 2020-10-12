/*
 * JBoss, Home of Professional Open Source
 * Copyright 2015, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package it.mauiroma.messaging.amq.web;


import it.mauiroma.messaging.amq.producer.AMQProducerEJB;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

@Named
@RequestScoped
public class AMQController {

    @EJB
    private AMQProducerEJB mqSender;

    private int messages = 1;

    private String textMessage = "Text Message";

    private String output;

    private String destination = "queue";

    public void sendMessages() {
        StringBuilder out = new StringBuilder();
        LocalTime start = LocalTime.now();
        out.append("Test started at " + start);
        out.append("<br>Total messages [" + getMessages() + "] to destination [" + mqSender.getQueueName() + "]");
        for (int i = 0; i < getMessages(); i++) {
            //String text = (i + 1) + " " + getTextMessage();
            try {
                out.append(mqSender.sendQueue(getTextMessage()));
            } catch (Exception e) {
                String error = "<br>&nbsp;&nbsp;Message with text [" + getTextMessage() + "] not sent";
                out.append(error);
                out.append("<br>&nbsp;&nbsp;&nbsp;&nbsp;Error Message[" + e.getMessage() + "]");
            }
        }
        LocalTime end = LocalTime.now();
        out.append("<br>Test finished at " + end);
        out.append("<br>Elapsed Millis " + ChronoUnit.MILLIS.between(start, end));
        setOutput(out.toString());
    }


    public String getQueue() {
        return mqSender.getQueueName();
    }

    public int getMessages() {
        return messages;
    }

    public void setMessages(int messages) {
        this.messages = messages;
    }

    public String getTextMessage() {
        return textMessage;
    }

    public void setTextMessage(String textMessage) {
        this.textMessage = textMessage;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getDestination() {
        return mqSender.getQueueName();
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }
}