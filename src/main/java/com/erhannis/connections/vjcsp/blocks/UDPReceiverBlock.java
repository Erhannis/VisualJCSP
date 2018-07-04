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
public class UDPReceiverBlock extends ProcessBlock {
  public static class Archetype implements BlockArchetype {
    @Override
    public String getName() {
      return "UDPReceiverBlock";
    }    
    
    @Override
    public HashMap<String, Class> getParameters() {
      HashMap<String, Class> parameters = new HashMap<>();
      parameters.put("port", Integer.class);
      return parameters;
    }

    @Override
    public Block createWireform(HashMap<String, Object> params, String label, TransformChain transformChain) {
      params = (params != null ? params : new HashMap<String, Object>());
      UDPReceiverBlock block = new UDPReceiverBlock(false, params, label, transformChain);
      //TODO I feel like this could be consolidated.
      if (!params.containsKey("port")) {
        block.terminals.add(new PlainInputTerminal("port", new TransformChain(null, transformChain), new IntOrEventualClass(Integer.class)));
      }
      block.terminals.add(new PlainOutputTerminal("msg", new TransformChain(null, transformChain), new IntOrEventualClass(String.class)));
      return block;
    }
  }

  private HashMap<String, Object> params;
  
  public UDPReceiverBlock(boolean isArchetype, HashMap<String, Object> params, String label, TransformChain transformChain) {
    super(label, transformChain);
    this.params = params;
  }
}
