/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.connections.vjcsp;

import com.erhannis.connections.base.TransformChain;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;

/**
 *
 * @author erhannis
 */
public class FileProcessBlock extends ProcessBlock {
  public static final Color COLOR = Color.CYAN; // Guess this one is cyan
  
  //TODO Track file
  
  public FileProcessBlock(String label, TransformChain transformChain) {
    super(label, transformChain);
  }
  
  @Override
  protected void draw0(Graphics2D g) {
    g.setColor(COLOR);
    g.drawRect(LEFT, TOP, WIDTH, HEIGHT);
  }

  @Override
  public Point2D.Double getCenter() {
    //TODO Static or something?
    return new Point2D.Double(0, 0);
  }

  public PlainInputTerminal addPlainInputTerminal(String label, IntOrEventualClass type) {
    // If the AffineTransform doesn't get set before the terminal is drawn, an error will be thrown - as it should.
    return addTerminal(new PlainInputTerminal(label, new TransformChain(null, transformChain), type));
  }

  public PlainOutputTerminal addPlainOutputTerminal(String label, IntOrEventualClass type) {
    // If the AffineTransform doesn't get set before the terminal is drawn, an error will be thrown - as it should.
    return addTerminal(new PlainOutputTerminal(label, new TransformChain(null, transformChain), type));
  }
  
  //TODO Extract to Block?
  //TODO Is the generics overkill?
  public <T extends VJCSPTerminal> T addTerminal(T terminal) {
    this.terminals.add(terminal);
    return terminal;
  }
}
