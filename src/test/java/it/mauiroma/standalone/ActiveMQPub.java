package it.mauiroma.standalone;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.junit.Test;

import javax.jms.*;


public class ActiveMQPub {

    @Test
    public void testNOXA(){
        //350000 = 5 minuti
        send("RH.queue", "sleep=30000", 35);
    }

    @Test
    public void testXA(){
        sendTopic("RH.topic", "text", 1);
    }


    @Test
    public void testMassive(){
        send("QueueXA", "error", 1);
        send("QueueXA", "sleepo", 10000);

    }



    private void send(String queue, String text, int messages){
        try {

            ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("tcp://localhost:61616?trace=true");

            Connection connection = factory.createConnection("jboss","password");
            connection.start();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination dest = new ActiveMQQueue(queue);
            MessageProducer producer = session.createProducer(dest);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

            for( int i=1; i <= messages; i ++) {
                //String body = i +"-Test MESSAGE";
                String body = text;
                TextMessage msg = session.createTextMessage(body);
                msg.setIntProperty("sleepTime", 1);
                msg.setIntProperty("seqNumber", i);
                producer.send(msg);
                System.out.println(msg.getJMSMessageID());
                if( (i % 1000) == 0) {
                    System.out.println(String.format("Sent %d messages", i));
                }
            }
            connection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }


    private void sendTopic(String queue, String text, int messages){
        try {

            ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("tcp://localhost:61616?trace=true");

            Connection connection = factory.createConnection("jboss","password");
            connection.setClientID("XXXXXXXXX");
            connection.start();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            Destination dest = new ActiveMQTopic(queue);
            MessageProducer producer = session.createProducer(dest);

            producer.setDeliveryMode(DeliveryMode.PERSISTENT);

            for( int i=1; i <= messages; i ++) {
                //String body = i +"-Test MESSAGE";
                String body = text;
                TextMessage msg = session.createTextMessage(body);
                msg.setIntProperty("sleepTime", 1);
                producer.send(msg);
                System.out.println(msg.getJMSMessageID());
                if( (i % 1000) == 0) {
                    System.out.println(String.format("Sent %d messages", i));
                }
            }
            connection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
