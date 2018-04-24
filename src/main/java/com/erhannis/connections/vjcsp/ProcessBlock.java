/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.connections.vjcsp;

import com.erhannis.connections.base.Block;
import com.erhannis.connections.base.Drawable;
import com.erhannis.connections.base.Terminal;
import com.erhannis.connections.base.TransformChain;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.HashSet;

/**
 *
 * @author erhannis
 */
public abstract class ProcessBlock implements Block, Drawable {
  protected final HashSet<VJCSPTerminal> terminals = new HashSet<>();

  protected TransformChain transformChain;
  
  @Override
  public void draw(Graphics2D g) {
    Color prevColor = g.getColor();
    AffineTransform prevTransform = g.getTransform();
   
    //TODO Make this kind of thing default for Drawable?
    draw0(g);

    //TODO Make holes for the terminals?
    drawTerminals(g);
    
    g.setColor(prevColor);
    g.setTransform(prevTransform);
  }
  
  protected void drawTerminals(Graphics2D g) {
    HashSet<Terminal> inputTerminals = new HashSet<>();
    HashSet<Terminal> outputTerminals = new HashSet<>();
    for (Terminal t : terminals) {
      if (t instanceof PlainInputTerminal) {
        inputTerminals.add(t);
      } else if (t instanceof PlainOutputTerminal) {
        outputTerminals.add(t);
      } else {
        throw new RuntimeException("Unhandled terminal type: " + t.getClass());
      }
    }
    //TODO Allow manually place terminals?  Allow ORDER terminals?
    AffineTransform base = g.getTransform();
    int count;
    double scale;
    int i;
    count = inputTerminals.size();
    scale = 1 / (2 * count);
    i = 0;
    for (Terminal t : inputTerminals) {
      i++;
      double ty = TOP;
      double tx = LEFT + ((i + 1) * (WIDTH / (count + 1)));
      g.transform(new AffineTransform(scale, 0, 0, scale, tx, ty));
      ((Drawable)t).draw(g);
      g.setTransform(base);
    }
    count = inputTerminals.size();
    scale = 1 / (2 * count);
    i = 0;
    for (Terminal t : outputTerminals) {
      i++;
      double ty = TOP + HEIGHT;
      double tx = LEFT + ((i + 1) * (WIDTH / (count + 1)));
      g.transform(new AffineTransform(scale, 0, 0, scale, tx, ty));
      ((Drawable)t).draw(g);
      g.setTransform(base);
    }
  }
  
  protected abstract void draw0(Graphics2D g);
  
  @Override
  public TransformChain getTransformChain() {
    return transformChain;
  }
}
