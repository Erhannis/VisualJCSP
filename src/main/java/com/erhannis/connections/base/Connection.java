/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.connections.base;

/**
 *
 * @author erhannis
 */
public interface Connection {
  //TODO Hmm.  We're already checking that kindof a thing, on the terminals....  Redundant/inconsistent?  Remove?
//  public boolean canAddFromTerminal(Terminal t);
//  public boolean canAddToTerminal(Terminal t);
  
  public void addFromTerminal(Terminal t);
  public void addToTerminal(Terminal t);
}
