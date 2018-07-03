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
import java.util.HashMap;

/**
 *
 * @author erhannis
 */
public class UDPTransmitterBlock extends ProcessBlock implements BlockArchetype {

  public UDPTransmitterBlock(boolean isArchetype, String label, TransformChain transformChain) {
    super(label, transformChain);
  }

  @Override
  public HashMap<String, Class> getParameters() {
    return new HashMap<String, Class>() {{
      put("hostname", String.class);
      put("port", Integer.class);
    }};
  }

  @Override
  public Block createWireform(HashMap<String, Object> params) { //TODO Heck, "transformChain"? "label"?
    UDPTransmitterBlock block = new UDPTransmitterBlock(false, label, transformChain);
    //TODO Store params
    //TODO Make form classes?  Maybe static inner classes?
    this.terminals.add(new PlainInputTerminal("hostname", new TransformChain(null, transformChain), new IntOrEventualClass(String.class)));
    this.terminals.add(new PlainInputTerminal("port", new TransformChain(null, transformChain), new IntOrEventualClass(Integer.class)));
  }
}
