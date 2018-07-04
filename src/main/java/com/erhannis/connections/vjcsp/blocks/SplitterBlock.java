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
public class SplitterBlock extends ProcessBlock {
  public static class Archetype implements BlockArchetype {
    @Override
    public String getName() {
      return "SplitterBlock";
    }    
    
    @Override
    public HashMap<String, Class> getParameters() {
      HashMap<String, Class> parameters = new HashMap<>();
      //TODO Generics, rather than type?
      parameters.put("type", IntOrEventualClass.class);
      parameters.put("count", Integer.class);
      return parameters;
    }

    @Override
    public Block createWireform(HashMap<String, Object> params, String label, TransformChain transformChain) {
      params = (params != null ? params : new HashMap<String, Object>());
      SplitterBlock block = new SplitterBlock(false, params, label, transformChain);
      IntOrEventualClass type = (IntOrEventualClass) params.getOrDefault("type", new IntOrEventualClass(Object.class));
      int count = (Integer) params.getOrDefault("count", 2);
      block.terminals.add(new PlainInputTerminal("in", new TransformChain(null, transformChain), type));
      for (int i = 0; i < count; i++) {
        //TODO Label: [0,outs), or [1,outs]?
        block.terminals.add(new PlainOutputTerminal("out " + i, new TransformChain(null, transformChain), type));
      }
      return block;
    }
  }

  private HashMap<String, Object> params;

  public SplitterBlock(boolean isArchetype, HashMap<String, Object> params, String label, TransformChain transformChain) {
    super(label, transformChain);
    this.params = params;
  }
}
