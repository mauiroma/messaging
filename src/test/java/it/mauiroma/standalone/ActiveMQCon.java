package it.mauiroma.standalone;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.activemq.jndi.ActiveMQInitialContextFactory;
import org.junit.Test;

import javax.jms.*;
import javax.naming.Context;
import java.util.Date;
import java.util.Hashtable;

/**
 * Created by mauiroma on 24/05/2017.
 */
public class ActiveMQCon {


    @Test
    public void testListener() {
        try {
            String user = "admin";
            String password = "admin";
            //String url = "tcp://localhost:61616?jms.rmIdFromConnectionId=true";
            String url = "tcp://localhost:61616?transporttrace=true&jms.rmIdFromConnectionId=true&jms.prefetchPolicy.queuePrefetch=0&jms.watchTopicAdvisories=false&jms.messagePrioritySupported=false";
            ActiveMQInitialContextFactory activeMQInitialContextFactory = new ActiveMQInitialContextFactory();

            String connFactoryType = "XAConnectionFactory";
//        String connFactoryType = "ConnectionFactory";

            Hashtable env = new Hashtable();
            //env.put("connectionFactoryNames","XAConnectionFactory");
            env.put("java.naming.provider.url", url);
            // env.put("queue.MyQueue","QueueTest");

            Context context = activeMQInitialContextFactory.getInitialContext(env);
            ConnectionFactory connectionFactory = (ConnectionFactory) context.lookup(connFactoryType);
            System.out.println(connectionFactory.toString());
            Destination destination = (Destination) context.lookup("dynamicQueues/QueueTest");
            for (int i = 0; i <= 2; i++) {

                //            ActiveMQXAConnectionFactory factory = new ActiveMQXAConnectionFactory(url);
                //            ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(url);
                Connection connection = connectionFactory.createConnection(user, password);
                connection.start();
                for (int consumer = 0; consumer < 20; consumer++) {
                    //new ActiveMQListener(connection, destination, System.out, "DUMMY" + i).start();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void testMessageReceiver() {
        try {
            String user = "mromani";
            String password = "password";
            ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(user, password, "tcp://localhost:61616");
            Connection connection = factory.createConnection();
            connection.start();
            Session session = connection.createSession(false,Session.CLIENT_ACKNOWLEDGE);
            ActiveMQTopic destination = new ActiveMQTopic("RH.queue");
            MessageConsumer consumer = session.createConsumer(destination);
            int counter = 1;
            while (true) {
                System.out.println("PRE RECEIVE");
                Message message = consumer.receive(2000);
                System.out.println("POST RECEIVE");
                if (message == null){
                    break;
                }
                message.acknowledge();
                System.out.println((counter++) +" "+ message);
            }
            consumer.close();
            session.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void testDurableSubsctiber() {
        try {
            String user = "mromani";
            String password = "password";
            ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(user, password, "tcp://localhost:61616");
            Connection connection = factory.createConnection();
            connection.setClientID("IDEA");
            connection.start();
            Session session = connection.createSession(false,Session.CLIENT_ACKNOWLEDGE);
            ActiveMQTopic destination = new ActiveMQTopic("RH.topic?consumer.retroactive=true");
            //MessageConsumer consumer1 = session.createDurableSubscriber(destination, "SARA1");
            //consumer1.close();
            MessageConsumer consumer = session.createDurableSubscriber(destination, "MAUI1");
            //MessageConsumer consumer = session.createConsumer(destination);
            int counter = 1;
            while (true) {
                System.out.println(new Date()+ " PRE RECEIVE");
                Message message = consumer.receive(2000);
                System.out.println(new Date()+ " POST RECEIVE");
                if (message == null){
                    break;
                }
                System.out.println(new Date()+ " PRE ACK");
                message.acknowledge();
                System.out.println(new Date()+ " AFTER ACK");
                System.out.println((counter++) +" "+ message);
            }
            consumer.close();
            session.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testThreadDurableSubsctiber() {
        try {
            String user = "mromani";
            String password = "password";
            Thread listener = new Thread(new TopicListener());
            listener.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
