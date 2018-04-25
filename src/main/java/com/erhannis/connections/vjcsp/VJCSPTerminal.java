/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.connections.vjcsp;

import com.erhannis.connections.base.Labeled;
import com.erhannis.connections.base.Terminal;

/**
 *
 * @author erhannis
 */
public abstract class VJCSPTerminal implements Terminal, Labeled {
  protected String label;
  
  public VJCSPTerminal(String label) {
    this.label = label;
  }

  @Override
  public String getLabel() {
    return label;
  }
}
