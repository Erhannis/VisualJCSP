/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.connections.vjcsp;

import com.erhannis.connections.base.Terminal;

/**
 *
 * @author erhannis
 */
public class PlainOutputTerminal extends PlainTerminal {
  public PlainOutputTerminal(IntOrEventualClass type) {
    super(type);
  }
  
  @Override
  public boolean canConnectTo(Terminal t) {
    if (t instanceof PlainInputTerminal && ((PlainInputTerminal)t).type.equals(this.type)) {
      return true;
    } else {
      return false;
    }
  }

  @Override
  public boolean canConnectFrom(Terminal t) {
    return false;
  }
}
