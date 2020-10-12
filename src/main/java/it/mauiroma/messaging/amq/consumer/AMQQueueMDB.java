package it.mauiroma.messaging.amq.consumer;


import javax.ejb.*;
import javax.jms.Message;
import javax.jms.MessageListener;

@MessageDriven(name = "AMQQueueMDB", activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "jms/queue/RH/JBOSS/queue"),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge")}
)


//@DeliveryActive("${property.mdb.delivery.group}")
//@DeliveryGroup("${property.mdb.delivery.group}")
public class AMQQueueMDB implements MessageListener {


    @EJB
    private AMQConsumerEJB amqConsumerEJB;

    public void onMessage(Message message) {
        try {
            amqConsumerEJB.manageMessage(message);
        } catch (Exception e) {
            throw new RuntimeException("Generic error");
        }
    }
}
