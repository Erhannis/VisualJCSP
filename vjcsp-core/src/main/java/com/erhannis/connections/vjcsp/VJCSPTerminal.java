/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.connections.vjcsp;

import com.erhannis.connections.base.Named;
import com.erhannis.connections.base.Terminal;

/**
 *
 * @author erhannis
 */
public abstract class VJCSPTerminal implements Terminal {
  protected String name;
  
  public VJCSPTerminal(String name) {
    this.name = name;
  }

  @Override
  public String getName() {
    return name;
  }
}
