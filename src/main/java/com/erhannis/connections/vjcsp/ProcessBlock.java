/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.connections.vjcsp;

import com.erhannis.connections.base.Block;
import com.erhannis.connections.base.Drawable;
import com.erhannis.connections.base.Labeled;
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
public abstract class ProcessBlock implements Block, Drawable, Labeled {
  protected final HashSet<VJCSPTerminal> terminals = new HashSet<>();

  protected String label;
  protected TransformChain transformChain;
  
  public ProcessBlock(String label, TransformChain transformChain) {
    this.label = label;
    this.transformChain = transformChain;
  }
  
  @Override
  public void draw(Graphics2D g) {
    Color prevColor = g.getColor();
    AffineTransform prevTransform = g.getTransform();
    // Blehhhh.  As much as I didn't want to put this here, here's where it makes most sense.
    g.transform(getTransformChain().transform);
   
    //TODO Make this kind of thing default for Drawable?
    draw0(g);

    //TODO Make holes for the terminals?
    drawTerminals(g);
    
    g.setColor(prevColor);
    g.setTransform(prevTransform);
  }
  
  protected void drawTerminals(Graphics2D g) {
    HashSet<PlainInputTerminal> inputTerminals = new HashSet<>();
    HashSet<PlainOutputTerminal> outputTerminals = new HashSet<>();
    //TODO I'm not sure whether to be uneasy about the class-specificity, here
    for (Terminal t : terminals) {
      if (t instanceof PlainInputTerminal) {
        inputTerminals.add((PlainInputTerminal)t);
      } else if (t instanceof PlainOutputTerminal) {
        outputTerminals.add((PlainOutputTerminal)t);
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
    scale = 1.0 / (2 * Math.max(1, count));
    i = 0;
    for (PlainInputTerminal t : inputTerminals) {
      double ty = TOP;
      double tx = LEFT + ((i + 1) * (WIDTH / (count + 1.0)));
      //TODO Is it bad I rely on the in-place nature of getTransformChain?
      t.getTransformChain().transform = new AffineTransform(scale, 0, 0, scale, tx, ty);
      t.draw(g);
      g.setTransform(base);
      i++;
    }
    count = outputTerminals.size();
    scale = 1.0 / (2 * Math.max(1, count));
    i = 0;
    for (PlainOutputTerminal t : outputTerminals) {
      double ty = TOP + HEIGHT;
      double tx = LEFT + ((i + 1) * (WIDTH / (count + 1.0)));
      //TODO Is it bad I rely on the in-place nature of getTransformChain?
      t.getTransformChain().transform = new AffineTransform(scale, 0, 0, scale, tx, ty);
      t.draw(g);
      g.setTransform(base);
      i++;
    }
  }
  
  protected abstract void draw0(Graphics2D g);
  
  @Override
  public TransformChain getTransformChain() {
    return transformChain;
  }

  @Override
  public String getLabel() {
    return label;
  }
}
