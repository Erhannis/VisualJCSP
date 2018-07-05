/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.connections.vjcsp;

import com.erhannis.connections.base.Terminal;
import com.erhannis.connections.base.TransformChain;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;

/**
 *
 * @author erhannis
 */
public class PlainInputTerminal extends PlainTerminal {
  public PlainInputTerminal(String label, TransformChain transformChain, IntOrEventualClass type) {
    super(label, transformChain, type);
  }
  
  @Override
  public boolean canConnectTo(Terminal t) {
    // It's an input terminal; it can only be connected to, not from.
    return false;
  }

  @Override
  public boolean canConnectFrom(Terminal t) {
    return (t instanceof PlainOutputTerminal); //TODO Is this correct?  Also, should this, too, check types?
  }

  @Override
  public void draw0(Graphics2D g, Color colorOverride) {
    g.setColor(colorOverride != null ? colorOverride : type.getColor());
    g.drawArc(LEFT, TOP, WIDTH, HEIGHT, 180, 180);
  }
}
