package com.gigaspaces.notifyclient;

import org.openspaces.core.GigaSpace;
import org.openspaces.core.GigaSpaceConfigurer;
import org.openspaces.core.space.SpaceProxyConfigurer;

public class CartService {

  private static GigaSpace gigaSpace;

  public static boolean createCart(long id, String user) {
    Cart cart = new Cart();
    cart.setId(id);
    cart.setUser(user);
    gigaSpace.write(cart);
    return true;
  }

  public static void main(String[] args) {
    SpaceProxyConfigurer spaceProxyConfigurer = new SpaceProxyConfigurer("testspace");
    gigaSpace = new GigaSpaceConfigurer(spaceProxyConfigurer).gigaSpace();
    for (int i = 1; i <= 100; i++) {
      String user = String.format("User %03d", i);
      createCart(i, user);
    }
  }

}
