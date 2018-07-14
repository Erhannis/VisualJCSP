/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.connections.base;

import java.util.LinkedHashSet;
import java.util.Set;

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

  /**
   * Remove terminal from connection.  Returns true iff this erased the connection
   * (such as being the last input terminal or last output terminal).
   * @param t
   * @return
   */
  public boolean removeTerminal(Terminal t); //TODO Could possibly cause problems?
  
  public Set<Terminal> getFromTerminals();
  public Set<Terminal> getToTerminals();
}
