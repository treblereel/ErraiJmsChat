Errai-JMS plugin for 3.1.1

You see a simple demo, just put any text into the textbox and press the Send button, your message will be sent to the topic/outboundTopic. Afterwards the system will catch that message and put it in to topic/inboundTopic, you will see that message on the right textbox, because we are also subscribed to that topic to show you that ability. You also can open several browser windows to determine that all clients can receive your message.

That plugin allows users to subscribe to the Jms Topic and send messages to any topic. From the client side it works like the original Errai MessageBus messaging, you only need to subscribe to the specific subject or send to some subject, for instance, if you want to send a message to “topic/inboundTopic” you should send a message to this subject, and vise versa. One thing to note, at this time Errai-Jms only supports Topics, not Queues, but if someone needs, i can add this functionality. All necessary properties are kept in ErraiApp.properties. To add Jms support you only need to

```java
@Inject
JMSBindingProvider jMSBindingProvider;
```
to your application.

This settings depends on your App server setup:
```java
jndi.naming.provider.url=http-remoting://127.0.0.1:8080
jndi.naming.security.principal=quickstartUser
jndi.naming.security.credentials=quickstartPwd1!
jndi.naming.factory.initial=org.jboss.naming.remote.client.InitialContextFactory
jndi.connection.factory=jms/RemoteConnectionFactory
```
Outbound query:
```java
jms.outboundExample.topic=jms/topic/outboundTopic
jms.outboundExample.send=true
```
Inbound query:
```java
jms.inboundExample.topic=jms/topic/inboundTopic
jms.inboundExample.send=false
```

