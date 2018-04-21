/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.connections.vjcsp;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;

/**
 *
 * @author erhannis
 */
public class FileProcessBlock extends ProcessBlock {
  public static final Color COLOR = Color.CYAN; // Guess this one is cyan
  
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
}
