package org.jetbrains.testProxy;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.net.*;

public class Client {
  public static void main(String[] args) throws Exception {
    final ClassLoader classLoader = new ClassLoader(Thread.currentThread().getContextClassLoader()) {
    };

    Thread.currentThread().setContextClassLoader(classLoader);

    Authenticator.setDefault(new Authenticator() {
      @Override
      protected PasswordAuthentication getPasswordAuthentication() {
        Runnable runnable = new Runnable() {
          @Override
          public void run() {
            Thread.currentThread().setContextClassLoader(classLoader);

            try {
              classLoader.loadClass("org.jetbrains.testProxy.TestClass");
            }
            catch (ClassNotFoundException e) {
              e.printStackTrace();
            }
          }
        };

        if (SwingUtilities.isEventDispatchThread()) {
          runnable.run();
        }
        else {
          try {
            SwingUtilities.invokeAndWait(runnable);
          }
          catch (InterruptedException e) {
            e.printStackTrace();
          }
          catch (InvocationTargetException e) {
            e.printStackTrace();
          }
        }
        return null;
      }
    });
    new URL("http://jetbrains.com").openConnection(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("localhost", 8060))).getInputStream();
  }
}