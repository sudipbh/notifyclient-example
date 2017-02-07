package com.gigaspaces.notifyclient;

import com.gigaspaces.events.DataEventSession;
import com.gigaspaces.events.EventSessionConfig;
import com.gigaspaces.events.EventSessionFactory;
import com.gigaspaces.events.NotifyActionType;
import com.j_spaces.core.client.EntryArrivedRemoteEvent;
import net.jini.core.event.EventRegistration;
import net.jini.core.event.RemoteEvent;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.event.UnknownEventException;
import net.jini.core.lease.Lease;
import net.jini.core.lease.UnknownLeaseException;
import org.openspaces.core.GigaSpace;
import org.openspaces.core.GigaSpaceConfigurer;
import org.openspaces.core.space.SpaceProxyConfigurer;

import java.rmi.RemoteException;
import java.util.concurrent.TimeUnit;

public class CartClient implements RemoteEventListener {

  private static GigaSpace gigaSpace;

  private DataEventSession session;
  private EventRegistration registration;

  public CartClient() throws RemoteException {
    gigaSpace = new GigaSpaceConfigurer(new SpaceProxyConfigurer("testspace")).gigaSpace();
    EventSessionFactory factory = EventSessionFactory.getFactory(gigaSpace.getSpace());
    EventSessionConfig config = new EventSessionConfig();
    session = factory.newDataEventSession(config);
    registration = session.addListener(new Cart(),this, Lease.FOREVER,null,null, NotifyActionType.NOTIFY_ALL);
  }

  public void cleanUp() throws RemoteException, UnknownLeaseException {
    session.removeListener(registration);
    session.close();
  }

  @Override
  public void notify(RemoteEvent remoteEvent) throws UnknownEventException, RemoteException {
    try {
      EntryArrivedRemoteEvent arrivedRemoteEvent =(EntryArrivedRemoteEvent) remoteEvent;
      Cart cart = (Cart) arrivedRemoteEvent.getObject();
      System.out.printf("Cart[ id=%d, user=%s ]\n", cart.getId(), cart.getUser());
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public static void main(String[] args) {
    try {
      CartClient cartClient = new CartClient();
      TimeUnit.MINUTES.sleep(15);
      cartClient.cleanUp();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }
}
