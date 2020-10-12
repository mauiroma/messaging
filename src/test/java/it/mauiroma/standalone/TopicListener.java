package it.mauiroma.standalone;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.util.Date;

public class TopicListener implements MessageListener ,Runnable{
    @Override
    public void onMessage(Message message) {
        try {

            if (message instanceof TextMessage) {
                TextMessage text = (TextMessage) message;
                Date date = new Date (text.getJMSTimestamp());
                System.out.println(date);
                System.out.println(" - Consuming text msg: " + text.getText());
            } else if (message instanceof ObjectMessage) {
                ObjectMessage objmsg = (ObjectMessage) message;
                Object obj = objmsg.getObject();
                System.out.println(" - Consuming object msg: " + obj);
            } else {
                System.out.println(
                        " - Unrecognized Message type " + message.getClass());
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        System.out.println("RUN");
        try {
            ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("mromani", "password", "tcp://localhost:61616");
            Connection connection = factory.createConnection();
            // Setting unique client id for durable subscriber
            connection.setClientID("Listener");
            // Create 3 new consumers for the topic
            Session session = connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
            Topic destination = session.createTopic("RH.topic");
            MessageConsumer consumer = session.createDurableSubscriber(destination, "MAUI");
            consumer.setMessageListener(this);
            connection.start();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
