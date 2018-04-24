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
  public PlainInputTerminal(TransformChain transformChain, IntOrEventualClass type) {
    super(transformChain, type);
  }
  
  @Override
  public boolean canConnectTo(Terminal t) {
    // It's an input terminal; it can only be connected to, not from.
    return false;
  }

  @Override
  public boolean canConnectFrom(Terminal t) {
    return true;
  }

  @Override
  protected void draw0(Graphics2D g) {
    g.setColor(type.getColor());
    g.drawArc(LEFT, TOP, WIDTH, HEIGHT, 180, 180);
  }
}
