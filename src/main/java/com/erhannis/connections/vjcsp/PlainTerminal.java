/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.connections.vjcsp;

import com.erhannis.connections.base.Drawable;
import com.erhannis.connections.base.TransformChain;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

/**
 *
 * @author erhannis
 */
public abstract class PlainTerminal extends VJCSPTerminal implements Drawable {
  protected final IntOrEventualClass type;
  
  protected TransformChain transformChain;
  
  protected PlainTerminal(String name, TransformChain transformChain, IntOrEventualClass type) {
    super(name);
    this.transformChain = transformChain;
    this.type = type;
  }
  
  public IntOrEventualClass getType() {
    return type;
  }
  
  @Override
  public Point2D.Double getCenter() {
    //TODO Should be a static thing?
    return new Point2D.Double(0, 0);
  }

  @Override
  public TransformChain getTransformChain() {
    return transformChain;
  }

  @Override
  public Color getColor() {
    IntOrEventualClass type = getType();
    if (type != null) {
      return type.getColor();
    } else {
      return Color.LIGHT_GRAY; //TODO Parameterize?  Throw exception?
    }
  }
}
