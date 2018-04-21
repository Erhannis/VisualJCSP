/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.connections.vjcsp;

import com.erhannis.connections.base.Block;
import com.erhannis.connections.base.Drawable;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 *
 * @author erhannis
 */
public abstract class ProcessBlock implements Block, Drawable {
  @Override
  public void draw(Graphics2D g) {
    Color prevColor = g.getColor();
   
    //TODO Make this kind of thing default for Drawable?
    draw0(g);
    
    drawTerminals();
    
    g.setColor(prevColor);
  }
  
  protected abstract void draw0(Graphics2D g);
}
