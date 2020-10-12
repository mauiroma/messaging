package it.mauiroma.messaging.amq.producer;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.jms.*;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.logging.Logger;

@TransactionManagement(value= TransactionManagementType.CONTAINER)
@Stateless
public class AMQProducerEJB{

    @Resource(lookup = "java:/jms/queue/RH/JBOSS/queue")
    private Queue queue;


    @Resource(lookup = "java:/RemoteJmsXA")
    private ConnectionFactory connectionFactory;

    private final static Logger LOGGER = Logger.getLogger(AMQProducerEJB.class.toString());

    public String sendQueue(String text){
        LOGGER.info("SEND Message to destination ["+getQueueName()+"]");
        return produceMessage(text, queue);
    }

    private String produceMessage(String text,  Destination destination) {
        Instant start = Instant.now();
        String result = "ok";
        String messageText = Objects.isNull(text) ? ""+System.currentTimeMillis():text;
        Connection connection = null;
        try {
            connection = connectionFactory.createConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Message message = session.createTextMessage(messageText);
            MessageProducer producer = session.createProducer(destination);
            //long startSend = System.currentTimeMillis();
            Instant startSend = Instant.now();
            producer.send(message);
            Instant end = Instant.now();
            LOGGER.info("Time elapsed to send message ["+Duration.between(startSend, end).toMillis()+"]");
            LOGGER.info("Global Time elapsed to send message included obtain connection ["+Duration.between(start, end)+"]");
            result = message.getJMSMessageID();
        } catch (JMSException e) {
            e.printStackTrace();
            result = e.getMessage();
        }finally{
            LOGGER.info("RESULT ["+result+"]");
            try {
                connection.close();
            } catch (JMSException e) {
                LOGGER.warning("Error when try to close connection");
                //ignored
            }
        }
        return result;
    }

    public String getQueueName(){
        try {
            return queue.getQueueName();
        } catch (Exception e) {
            return "ERROR";
        }
    }


}