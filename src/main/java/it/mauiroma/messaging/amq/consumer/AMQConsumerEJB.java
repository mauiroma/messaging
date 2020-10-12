package it.mauiroma.messaging.amq.consumer;

import it.mauiroma.messaging.amq.beans.OBJMessage;

import javax.ejb.*;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@TransactionManagement(value= TransactionManagementType.CONTAINER)
@Stateless
public class AMQConsumerEJB {

    private final static Logger LOGGER = Logger.getLogger(AMQConsumerEJB.class.toString());

    private static Map<String,Integer> redeliveryMap = new HashMap<String, Integer>();


    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @Transactional(rollbackOn = Exception.class)
    public void manageMessage(Message message) throws Exception {
        //TransactionManager tm = (TransactionManager) new InitialContext().lookup("java:jboss/TransactionManager");
        LOGGER.info("[" + message.getJMSMessageID() + "] received from[" + message.getJMSDestination().toString() + "]");
        try {
            if (message instanceof TextMessage) {
                LOGGER.info("Received Test Message");
                LOGGER.info("text[" + ((TextMessage) message).getText() + "]");
                redeliveryCount(message.getJMSMessageID());
                manageTextMessage((TextMessage) message);
            }else if(message instanceof ObjectMessage){
                LOGGER.info("Received Object Message");
                LOGGER.info(((OBJMessage)((ObjectMessage)message).getObject()).getMessage());
            }else {
                LOGGER.warning("Message of wrong type: " + message.getClass().getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
            //throw new RuntimeException(e);
            throw e;
        }
    }

    private void manageTextMessage(TextMessage msg) throws Exception {
        String message = msg.getText();
        switch (message.split("=")[0]) {
            case "error":
                LOGGER.info("["+msg.getJMSMessageID()+"] EXCEPTION");
                throw new Exception("Messagge contains \"error\", EXCEPTION!!!");
            case "sleep":
                try {
                    String text = msg.getText().toLowerCase();
                    int sleepTime = Integer.parseInt(text.split("=")[1]);
                    LOGGER.info("["+msg.getJMSMessageID()+"] SLEEP ["+sleepTime+"]");
                    Thread.sleep(sleepTime);
                    LOGGER.info("["+msg.getJMSMessageID()+"] AWAKE");
                } catch (Exception e) {
                    LOGGER.warning("["+msg.getJMSMessageID()+"] SLEEP ERROR");
                }
                break;
            default:
                LOGGER.warning("["+msg.getJMSMessageID()+"] NORMAL");
                break;
        }

    }

    private synchronized void redeliveryCount(String jmsMessageID) {
        if(redeliveryMap.containsKey(jmsMessageID)){
            int redelivery = redeliveryMap.get(jmsMessageID)+1;
            redeliveryMap.put(jmsMessageID,redelivery);
            LOGGER.info("["+jmsMessageID+"] REDELIVERY ["+redelivery+"]");
        }else{
            LOGGER.info("["+jmsMessageID+"] REDELIVERY [1]");
            redeliveryMap.put(jmsMessageID,1);
        }
    }
}
