/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.connections.vjcsp;

import com.erhannis.connections.base.BlockArchetype;
import com.erhannis.connections.base.TransformChain;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.io.File;

/**
 *
 * @author erhannis
 */
public class FileProcessBlock extends ProcessBlock {
  //TODO Track file
  
  public FileProcessBlock(String name, TransformChain transformChain) {
    super(name, transformChain);
  }
  
  public PlainInputTerminal addPlainInputTerminal(String name, IntOrEventualClass type) {
    // If the AffineTransform doesn't get set before the terminal is drawn, an error will be thrown - as it should.
    return addTerminal(new PlainInputTerminal(name, new TransformChain(null, transformChain), type));
  }

  public PlainOutputTerminal addPlainOutputTerminal(String name, IntOrEventualClass type) {
    // If the AffineTransform doesn't get set before the terminal is drawn, an error will be thrown - as it should.
    return addTerminal(new PlainOutputTerminal(name, new TransformChain(null, transformChain), type));
  }
  
  //TODO Extract to Block?
  //TODO Is the generics overkill?
  public <T extends VJCSPTerminal> T addTerminal(T terminal) {
    this.terminals.add(terminal);
    return terminal;
  }

  @Override
  public BlockArchetype getArchetype() {
    //TODO Do
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void compile(File root) throws CompilationException {
    //TODO Do
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }
}
