package org.treblereel.chat.server;

import java.util.Enumeration;
import java.util.Properties;

import javax.inject.Inject;
import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jboss.errai.bus.server.annotations.Service;
import org.jboss.errai.jms.JMSBindingProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
// @JmsService
public class ChatService {
  Logger logger = LoggerFactory.getLogger(ChatService.class);
  private static final String JNDI_HOST = "http-remoting://127.0.0.1:8080";
  private static final String INBOUND_TOPIC = "jms/topic/inboundTopic";
  private static final String OUTBOUND_TOPIC = "jms/topic/outboundTopic";
  private static final String DEFAULT_USERNAME = "quickstartUser";
  private static final String DEFAULT_PASSWORD = "quickstartPwd1!";
  private static final String DEFAULT_CONNECTION_FACTORY = "jms/RemoteConnectionFactory";
  private ConnectionFactory connectionFactory = null;
  private JMSContext jmsContext = null;

  @Inject
  JMSBindingProvider jMSBindingProvider;

  @Inject
  public void init() {
    logger.info("On init");

    initEchoService();
  }

  private void initEchoService() {

    Properties props = new Properties();
    props.put(Context.PROVIDER_URL, JNDI_HOST);
    props.put(Context.INITIAL_CONTEXT_FACTORY,
        "org.jboss.naming.remote.client.InitialContextFactory");
    props.put(Context.SECURITY_PRINCIPAL, DEFAULT_USERNAME);
    props.put(Context.SECURITY_CREDENTIALS, DEFAULT_PASSWORD);

    InitialContext jndiContext;
    try {
      jndiContext = new InitialContext(props);
      connectionFactory = (ConnectionFactory) jndiContext.lookup(DEFAULT_CONNECTION_FACTORY);

      Topic inTopic = (Topic) jndiContext.lookup(INBOUND_TOPIC);
      Topic outTopic = (Topic) jndiContext.lookup(OUTBOUND_TOPIC);

      jmsContext = connectionFactory.createContext();
      createReciever(inTopic, outTopic);
    } catch (NamingException e) {
      e.printStackTrace();
    }



  }

  private void createReciever(final Topic inTopic, Topic outTopic) {
    JMSConsumer jmsReciever = jmsContext.createSharedConsumer(outTopic, "server_id2");
    jmsReciever.setMessageListener(new MessageListener() {
      @SuppressWarnings("rawtypes")
      @Override
      public void onMessage(Message message) {
        if (message != null) {
          try {
            message.acknowledge();
            MapMessage mapMessage = (MapMessage) message;
            MapMessage newMessage = connectionFactory.createContext().createMapMessage();
            Enumeration e = mapMessage.getMapNames();
            while (e.hasMoreElements()) {
              String item = (String) e.nextElement();
              Object obj = mapMessage.getObject(item);
              if (item.equals("ToSubject")) {
                newMessage.setString("ToSubject", "jms:" + INBOUND_TOPIC);
              } else {
                newMessage.setObject(item, obj);
              }
            }
            shiftMessageToTopic(inTopic, newMessage);
          } catch (JMSException e) {
            logger.error("Can't resend message  " + e.getMessage());
          }
        }
      }
    });
  }

  private void shiftMessageToTopic(Topic inTopic, MapMessage map) {
    if (map != null) {
      JMSProducer jmsProducer = jmsContext.createProducer();
      jmsProducer.send(inTopic, map);
    }
  }
}
