/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.connections.vjcsp.blocks;

import com.erhannis.connections.base.Block;
import com.erhannis.connections.base.BlockArchetype;
import com.erhannis.connections.base.TransformChain;
import com.erhannis.connections.vjcsp.IntOrEventualClass;
import com.erhannis.connections.vjcsp.PlainInputTerminal;
import com.erhannis.connections.vjcsp.PlainOutputTerminal;
import com.erhannis.connections.vjcsp.ProcessBlock;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;

/**
 *
 * @author erhannis
 */
public class UDPTransmitterBlock extends ProcessBlock implements BlockArchetype {

  public UDPTransmitterBlock(String label, TransformChain transformChain, String hostname, int port) {
    super(label, transformChain);
    //TODO Make port/host a channel, or part of the message, or something?
    this.terminals.add(new PlainInputTerminal("in", new TransformChain(null, transformChain), new IntOrEventualClass(String.class)));
  }

  @Override
  public Block create() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }
}
