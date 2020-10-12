Messaging: Application Using an MDB (Message-Driven Bean)
============================================================
Author: mauiroma
Technologies: JMS, EJB, MDB
Summary: The `messaging` uses *JMS* and *EJB Message-Driven Bean* (MDB) .

What is it?
-----------

The `messaging` demonstrates the use of *JMS* and *EJB Message-Driven Bean* in Red Hat JBoss Enterprise Application Platform.   
Based on jboss-eap-quickstarts/helloworld-jms/



Build and Deploy the Quickstart
-------------------------

## Cli command to add connection to remote AMQ63

```
/subsystem=messaging-activemq/server=default:remove
/subsystem=ee/service=default-bindings:undefine-attribute(name=jms-connection-factory)
/subsystem=ee:write-attribute(name=annotation-property-replacement,value=true)
/system-property=amq.url:add(value=tcp://localhost:61616)
/system-property=amq.url.connection.property:add(value="jms.rmIdFromConnectionId=true&amp&amp;jms.prefetchPolicy.queuePrefetch=2000")
```


### RESOURCE ADAPTER
```
deploy activemq-rar630310.rar --runtime-name=amq-ra.rar
/subsystem=ejb3:write-attribute(name=default-resource-adapter-name,value=amq-ra)
/subsystem=resource-adapters/resource-adapter=amq-ra:add(archive=amq-ra)
/subsystem=resource-adapters/resource-adapter=amq-ra/config-properties=ServerUrl:add(value=${amq.url}?${amq.url.connection.property})
/subsystem=resource-adapters/resource-adapter=amq-ra/config-properties=UserName:add(value=mromani)
/subsystem=resource-adapters/resource-adapter=amq-ra/config-properties=Password:add(value=password)
/subsystem=resource-adapters/resource-adapter=amq-ra:write-attribute(name=transaction-support,value=XATransaction
```

### CONNECTION FACTORY
```
/system-property=amq.cf.jndi:add(value=java:/jms/cf/ConnectionFactory)
/system-property=amq.cf.name:add(value=ConnectionFactory)
/subsystem=resource-adapters/resource-adapter=amq-ra/connection-definitions=ConnectionFactory:add(class-name=org.apache.activemq.ra.ActiveMQManagedConnectionFactory,recovery-username=mromani,recovery-password=password, jndi-name=${amq.cf.jndi})
/subsystem=resource-adapters/resource-adapter=amq-ra/connection-definitions=ConnectionFactory:write-attribute(name=min-pool-size,value=1)
/subsystem=resource-adapters/resource-adapter=amq-ra/connection-definitions=ConnectionFactory:write-attribute(name=max-pool-size,value=20)
/subsystem=resource-adapters/resource-adapter=amq-ra/connection-definitions=ConnectionFactory:write-attribute(name=pool-prefill,value=false)
/subsystem=resource-adapters/resource-adapter=amq-ra/connection-definitions=ConnectionFactory:write-attribute(name=same-rm-override,value=false)
```

### QUEUE
```
/system-property=amq.queue.jndi:add(value=java:/jms/queues/rhqueue)
/system-property=amq.queue.name:add(value=RH.queue)
/subsystem=resource-adapters/resource-adapter=amq-ra/admin-objects=RH.queue:add(class-name=org.apache.activemq.command.ActiveMQQueue, jndi-name=${amq.queue.jndi})
/subsystem=resource-adapters/resource-adapter=amq-ra/admin-objects=RH.queue/config-properties=PhysicalName:add(value=${amq.queue.name})
```

## CLI Command to add connection to remote AMQ7
```
/subsystem=ee/service=default-bindings:undefine-attribute(name=jms-connection-factory)

/subsystem=messaging-activemq/server=default:remove()

/socket-binding-group=standard-sockets/remote-destination-outbound-socket-binding=messaging-remote-broker01:add(host=amq.mauiroma.it,port=61616)
/socket-binding-group=standard-sockets/remote-destination-outbound-socket-binding=messaging-remote-broker02:add(host=amq1.mauiroma.it,port=61616)

/subsystem=messaging-activemq/remote-connector=messaging-remote-broker01-connector:add(socket-binding=messaging-remote-broker01)
/subsystem=messaging-activemq/remote-connector=messaging-remote-broker02-connector:add(socket-binding=messaging-remote-broker02)

/subsystem=messaging-activemq/pooled-connection-factory=activemq-rar.rar:add(transaction=xa,user=admin, password=password, entries=["java:/RemoteJmsXA", "java:jboss/RemoteJmsXA"],connectors=["messaging-remote-broker01-connector", "messaging-remote-broker02-connector"],ha=true, rebalance-connections=true)

/subsystem=naming/binding=java\:global\/remoteContext:add(binding-type=external-context, class=javax.naming.InitialContext, module=org.apache.activemq.artemis, environment=[java.naming.factory.initial=org.apache.activemq.artemis.jndi.ActiveMQInitialContextFactory])

/subsystem=naming/binding=java\:global\/remoteContext:map-put(key=queue.RH.JBOSS.QUEUE, name=environment, value=RH.JBOSS.QUEUE)

/subsystem=naming/binding="java:/jms/queue/RH/JBOSS/queue":add(lookup=java:global/remoteContext/RH.JBOSS.QUEUE,binding-type=lookup)
```

Access the application 
---------------------

The application will be running at the following URL: <http://localhost:8080/messaging/rest/amq/send?text=test> and will send some messages to the queue.
