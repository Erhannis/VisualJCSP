/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.connections.base;

import java.util.Collection;

/**
 *
 * @author erhannis
 */
public interface Block {
  //TODO `addTerminal`?...hmm, maybe not?  Some block classes may have a hardcoded terminal configuration.  getTerminals, then?
  public Collection<Terminal> getTerminals();
}
