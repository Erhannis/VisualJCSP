/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.connections.vjcsp;

import com.erhannis.connections.base.Connection;
import com.erhannis.connections.base.Terminal;
import java.util.HashSet;

/**
 *
 * @author erhannis
 */
public class PlainChannelConnection implements Connection {
  protected final HashSet<PlainOutputTerminal> outputTerminals = new HashSet<>();
  protected final HashSet<PlainInputTerminal> inputTerminals = new HashSet<>();
  
  public PlainChannelConnection(PlainOutputTerminal output, PlainInputTerminal input) {
    outputTerminals.add(output);
    inputTerminals.add(input);
  }

  @Override
  public void addFromTerminal(Terminal t) {
    outputTerminals.add((PlainOutputTerminal)t);
  }
  
  @Override
  public void addToTerminal(Terminal t) {
    inputTerminals.add((PlainInputTerminal)t);
  }
}
