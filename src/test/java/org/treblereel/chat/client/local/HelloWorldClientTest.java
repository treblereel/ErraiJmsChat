package org.treblereel.chat.client.local;

import org.jboss.errai.ioc.client.Container;

import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.Timer;

public class HelloWorldClientTest extends GWTTestCase {

  @Override
  public String getModuleName() {
    return "org.treblereel.chat.App";
  }

  @Override
  protected void gwtSetUp() throws Exception {
    super.gwtSetUp();
    
    // We need to bootstrap the IoC container manually because GWTTestCase
    // doesn't call onModuleLoad() for us.
    new Container().onModuleLoad();
  }
  
  public void testSendMessage() throws Exception {

  }
}
