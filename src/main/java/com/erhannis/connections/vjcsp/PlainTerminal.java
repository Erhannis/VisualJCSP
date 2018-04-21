/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.connections.vjcsp;

import com.erhannis.connections.base.Drawable;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;

/**
 *
 * @author erhannis
 */
public abstract class PlainTerminal extends VJCSPTerminal implements Drawable {
  protected final IntOrEventualClass type;
  
  protected PlainTerminal(IntOrEventualClass type) {
    this.type = type;
  }

  @Override
  public void draw(Graphics2D g) {
    Color prevColor = g.getColor();
    g.setColor(type.getColor());
   
    //TODO Make this kind of thing default for Drawable?
    draw0(g);
    
    g.setColor(prevColor);
  }
  
  protected abstract void draw0(Graphics2D g);
  
  @Override
  public Point2D.Double getCenter() {
    //TODO Should be a static thing?
    return new Point2D.Double(0, 0);
  }
}
