/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.connections.base;

import java.awt.Graphics2D;

/**
 *
 * @author erhannis
 */
public interface Drawable {
  /**
   * Draw the component.
   * 
   * REALLY SHOULD return with g's transform as you found it.
   * @param g 
   */
  public void draw(Graphics2D g);
}
