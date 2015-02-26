package org.treblereel.chat.client.local;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.EntryPoint;

/**
 * This class provides a target for injecting parts of the application that the
 * test cases need access to. Think of it as your test suite's window into the
 * CDI container. Test cases can access the injected members using the following
 * code:
 * 
 * <pre>
 *   ErraiIocTestHelper.instance.<i>injectedFieldName</i>
 * </pre>
 * <p>
 * You can also set up CDI event producers and observers here if your test needs
 * to fire events or assert that a particular event was fired.
 * <p>
 * Note that this "CDI Test Helper" pattern is just a workaround. If there were
 * something like the BeanManager available in the client, it would be
 * preferable for the tests to create and destroy managed beans on demand.
 * <p>
 * As an alternative workaround, you could dispense with this class altogether
 * and have your main Entry Point class keep a static reference to itself.
 * However, this would pollute the API with an unwanted singleton pattern: there
 * would be the possibility of classes referring to the entry point class through
 * its singleton rather than allowing it to be injected.
 * 
 * @author Jonathan Fuerth <jfuerth@gmail.com>
 */
@EntryPoint
public class ErraiIocTestHelper {

  static ErraiIocTestHelper instance;

  static boolean busInitialized = false;
  
  @Inject ChatClient client;
  
  @PostConstruct
  void saveStaticReference() {
    instance = this;
  }

}
