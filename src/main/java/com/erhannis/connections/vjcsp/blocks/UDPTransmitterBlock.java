/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.connections.vjcsp.blocks;

import com.erhannis.connections.base.BlockWireform;
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
public class UDPTransmitterBlock extends ProcessBlock {
  public static class Archetype implements BlockArchetype {
    @Override
    public String getName() {
      return "UDPTransmitterBlock";
    }    
    
    @Override
    public HashMap<String, Class> getParameters() {
      HashMap<String, Class> parameters = new HashMap<>();
      parameters.put("hostname", String.class);
      parameters.put("port", Integer.class);
      return parameters;
    }

    @Override
    public BlockWireform createWireform(HashMap<String, Object> params, String label, TransformChain transformChain) {
      params = (params != null ? params : new HashMap<String, Object>());
      UDPTransmitterBlock block = new UDPTransmitterBlock(false, params, label, transformChain);
      //TODO I feel like this could be consolidated.
      if (!params.containsKey("hostname")) {
        block.terminals.add(new PlainInputTerminal("hostname", new TransformChain(null, transformChain), new IntOrEventualClass(String.class)));
      }
      if (!params.containsKey("port")) {
        block.terminals.add(new PlainInputTerminal("port", new TransformChain(null, transformChain), new IntOrEventualClass(Integer.class)));
      }
      block.terminals.add(new PlainInputTerminal("msg", new TransformChain(null, transformChain), new IntOrEventualClass(String.class)));
      return block;
    }
  }

  private HashMap<String, Object> params;
  
  public UDPTransmitterBlock(boolean isArchetype, HashMap<String, Object> params, String label, TransformChain transformChain) {
    super(label, transformChain);
    this.params = params;
  }
}
