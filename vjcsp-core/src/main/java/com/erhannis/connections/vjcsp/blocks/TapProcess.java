/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.connections.vjcsp.blocks;

import jcsp.lang.CSProcess;
import jcsp.lang.ChannelInput;
import jcsp.lang.ChannelOutput;

/**
 *
 * @author erhannis
 */
public class TapProcess implements CSProcess {
  private ChannelInput in;
  private ChannelOutput out;
  
  public TapProcess(ChannelInput in, ChannelOutput out) {
    this.in = in;
    this.out = out;
  }
  
  @Override
  public void run() {
    while (true) {
      Object o = in.read();
      System.out.println("TapProcess: " + o);
      out.write(o);
    }
  }
}
