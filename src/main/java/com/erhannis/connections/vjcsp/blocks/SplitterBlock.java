/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.connections.vjcsp.blocks;

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
public class SplitterBlock extends ProcessBlock {

  public SplitterBlock(String label, TransformChain transformChain, IntOrEventualClass type, int outs) {
    super(label, transformChain);
    this.terminals.add(new PlainInputTerminal("in", new TransformChain(null, transformChain), type));
    for (int i = 0; i < outs; i++) {
      //TODO Label: [0,outs), or [1,outs]?
      this.terminals.add(new PlainOutputTerminal("out " + i, new TransformChain(null, transformChain), type));
    }
  }
}
