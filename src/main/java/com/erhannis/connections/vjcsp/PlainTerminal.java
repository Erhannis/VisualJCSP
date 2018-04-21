/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.connections.vjcsp;

/**
 *
 * @author erhannis
 */
public abstract class PlainTerminal extends VJCSPTerminal {
  protected final IntOrEventualClass type;
  
  protected PlainTerminal(IntOrEventualClass type) {
    this.type = type;
  }
}
