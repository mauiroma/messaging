package it.mauiroma.messaging.amq.rest;


import it.mauiroma.messaging.amq.producer.AMQProducerEJB;

import javax.ejb.EJB;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

@Path("amq")
public class ActiveMQSender {

    @EJB
    private AMQProducerEJB amqProducerEJB;

    @GET
    @Path("/send")
    public String send(@QueryParam("text")String text){
        return amqProducerEJB.sendQueue(text);
    }
}
