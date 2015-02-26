package org.treblereel.chat.client.local;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import org.jboss.errai.bus.client.ErraiBus;
import org.jboss.errai.bus.client.api.base.MessageBuilder;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.bus.client.api.messaging.MessageBus;
import org.jboss.errai.bus.client.api.messaging.MessageCallback;
import org.jboss.errai.bus.server.annotations.Service;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

@EntryPoint
@Service
@ApplicationScoped
public class ChatClient extends Composite {
  private MessageBus bus = ErraiBus.get();
  private static ChatClientUiBinder uiBinder = GWT.create(ChatClientUiBinder.class);
  final Logger logger = LoggerFactory.getLogger(ChatClient.class.getName());
  private Label label;
  

  @UiTemplate("ChatClient.ui.xml")
  interface ChatClientUiBinder extends UiBinder<Widget, ChatClient> {
  }


  @PostConstruct
  public void buildUI() {
    initWidget(uiBinder.createAndBindUi(this));
    VerticalPanel verticalPanel = new VerticalPanel(); 
    HorizontalPanel horizontalPanell = new  HorizontalPanel();
    verticalPanel.add(horizontalPanell);
    
    final TextBox  textBox = new TextBox();
    textBox.setMaxLength(50);
    horizontalPanell.add(textBox);
    
    Button b = new Button("SEND");
    b.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        if(textBox.getValue().length() > 0)
        sendMessage(textBox.getValue());
      }
    });
    horizontalPanell.add(b);
    
    label = new Label();
    horizontalPanell.add(label);
    HTMLPanel htmlPanel = new HTMLPanel(text);
    verticalPanel.add(htmlPanel);

    RootPanel.get().add(verticalPanel);
    initSubscribers();
  }

  private void initSubscribers() {

    bus.subscribe("jms:jms/topic/inboundTopic", new MessageCallback() {
      @Override
      public void callback(Message message) {
        String answer = (String) message.get(Object.class, "text");
        label.setText(answer);
      }
    });
  }

  private void sendMessage(String s) {
    MessageBuilder.createMessage("jms:jms/topic/outboundTopic").signalling()
        .with("text",s).noErrorHandling().sendNowWith(bus);
  }
  
  private final static String text = "Errai-JMS plugin for 3.1.1-SNAPSHOT <br><br>" 
  
    
    + " <br>You see a simple demo, just put any text into the textbox and press the Send button, your message<br>"
    + " will be sent to the topic/outboundTopic. Afterwards the system will catch that message and put it in to<br>"
    + " topic/inboundTopic, you will see that message on the right textbox, because we are also subscribed to <br>"
    + "that topic to show you that ability. You also can open several browser windows to determine that all <br>"
    + "clients can receive your message.<br>"

    + "That plugin allows users to subscribe to the Jms Topic and send messages to any topic. From the client side<br>"
    + " it works like the original Errai MessageBus messaging, you only need to subscribe to the specific subject <br>"
    + "or send to some subject, for instance, if you want to send a message to “topic/inboundTopic” you should send <br>"
    + "a message to this subject, and vise versa. One thing to note, at this time Errai-Jms only supports Topics, <br>"
    + "not Queues, but if someone needs, i can add this functionality. All necessary properties are kept in <br>"
    + "ErraiApp.properties. To add Jms support you only need to<br><br>" +  
  
  

  "@Inject"+
  "JMSBindingProvider jMSBindingProvider;"+

  "<br>to your application.<br><br>"+

 " This settings depends on your App server setup: <br><ul>"+
 " <li>jndi.naming.provider.url=http-remoting://127.0.0.1:8080 </li>"+
 " <li>jndi.naming.security.principal=quickstartUser</li>"+
 " <li>jndi.naming.security.credentials=quickstartPwd1!</li>"+
 " <li>jndi.naming.factory.initial=org.jboss.naming.remote.client.InitialContextFactory</li>"+
 " <li>jndi.connection.factory=jms/RemoteConnectionFactory</li>"+

  "</ul>"+
  "#Outbound query<br><ul>"+
  "<li>jms.outboundExample.topic=jms/topic/outboundTopic</li>"+
  "<li>jms.outboundExample.send=true</li></ul>"+
  "#Inbound query<br><br><ul>"+
  "<li>jms.inboundExample.topic=jms/topic/inboundTopic</li>"+
  "<li>jms.inboundExample.send=false</li>"+

  "</ul><br><br>Dmitry Tikhomirov<br>"+
  "<a href='https://twitter.com/treblereel'>@treblereel</a><br>"+
  "<a href='https://github.com/treblereel'>GitHub</a>";
}
