/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.connections.vjcsp;

import com.erhannis.connections.base.Terminal;
import com.erhannis.connections.base.TransformChain;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;

/**
 *
 * @author erhannis
 */
public class PlainOutputTerminal extends PlainTerminal {
  public PlainOutputTerminal(String label, TransformChain transformChain, IntOrEventualClass type) {
    super(label, transformChain, type);
  }
  
  @Override
  public boolean canConnectTo(Terminal t) {
    //TODO Allow sub/superclassing, whichever makes sense
    if (t instanceof PlainInputTerminal && ((PlainInputTerminal)t).type.equals(this.type)) {
      return t.canConnectFrom(this);
    } else {
      return false;
    }
  }

  @Override
  public boolean canConnectFrom(Terminal t) {
    return false;
  }

  @Override
  protected void draw0(Graphics2D g) {
    g.setColor(type.getColor());
    g.drawArc(LEFT, TOP, WIDTH, HEIGHT, 180, -180);
  }
}
