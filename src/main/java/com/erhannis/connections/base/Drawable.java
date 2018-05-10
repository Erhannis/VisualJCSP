/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.connections.base;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

/**
 *
 * @author erhannis
 */
public interface Drawable {
  public static final int TOP = -25;
  public static final int LEFT = -25;
  public static final int HEIGHT = 50;
  public static final int WIDTH = 50;
  //TODO Ehhh, could put center here, too....
  
  /**
   * Draw the component.
   * 
   * SHOULD PROBABLY be contained within (and possibly fill) the square (-1,-1) to (1,1).
   * I may rescind that for, say Connection.
   * 
   * REALLY SHOULD return with g's transform and color as you found it.
   * 
   * @param g 
   * @param colorOverride  null if no override
   */
  
  default public void draw(Graphics2D g, Color colorOverride) {
    Color prevColor = g.getColor();
    AffineTransform prevTransform = g.getTransform();
    // Blehhhh.  As much as I didn't want to put this here, here's where it makes most sense.
    g.transform(getTransformChain().transform);

    if (colorOverride != null) {
      g.setColor(colorOverride);
    } else {
      g.setColor(getColor());
    }
    if (this instanceof Labeled) {
      g.drawString(((Labeled)this).getLabel(), LEFT, 0);
    }
    
    draw0(g, colorOverride);
    
    g.setColor(prevColor);
    g.setTransform(prevTransform);
  };

  public void draw0(Graphics2D g, Color colorOverride);
  
  /**
   * Returns the center of the component.
   * This does not necessarily follow any particular rules, like the geometric center
   * of the convex hull, or anything, but is more "the most important, central point of the object".
   * 
   * //TODO In absolute frame, or relative?
   * 
   * @return 
   */
  public Point2D.Double getCenter();
  
  public TransformChain getTransformChain();
  
  public Color getColor();
}
