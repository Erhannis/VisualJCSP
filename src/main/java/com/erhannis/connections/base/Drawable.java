/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.connections.base;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;

/**
 *
 * @author erhannis
 */
public interface Drawable {
  public static final int TOP = -1;
  public static final int LEFT = -1;
  public static final int HEIGHT = 2;
  public static final int WIDTH = 2;
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
   */
  public void draw(Graphics2D g);
  
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
  //TODO getTransform, instead?
}
