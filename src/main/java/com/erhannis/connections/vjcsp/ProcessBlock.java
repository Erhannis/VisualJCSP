/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.connections.vjcsp;

import com.erhannis.connections.ConnectionsPanel;
import com.erhannis.connections.base.BlockWireform;
import com.erhannis.connections.base.Drawable;
import com.erhannis.connections.base.Labeled;
import com.erhannis.connections.base.Terminal;
import com.erhannis.connections.base.TransformChain;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;

/**
 *
 * @author erhannis
 */
public abstract class ProcessBlock implements BlockWireform, Drawable, Labeled {
  protected final LinkedHashSet<VJCSPTerminal> terminals = new LinkedHashSet<>();

  protected String label;
  protected TransformChain transformChain;
  
  public ProcessBlock(String label, TransformChain transformChain) {
    this.label = label;
    this.transformChain = transformChain;
  }
  
  public void draw0(Graphics2D g, Color colorOverride) {
    draw1(g, colorOverride);

    //TODO Make holes for the terminals?
    drawTerminals(g, colorOverride);
  }

  protected void drawTerminals(Graphics2D g, Color colorOverride) {
    LinkedHashSet<PlainInputTerminal> inputTerminals = new LinkedHashSet<>();
    LinkedHashSet<PlainOutputTerminal> outputTerminals = new LinkedHashSet<>();
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
      t.draw(g, colorOverride);
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
      t.draw(g, colorOverride);
      g.setTransform(base);
      i++;
    }
  }
  
  @Override
  public TransformChain getTransformChain() {
    return transformChain;
  }

  @Override
  public String getLabel() {
    return label;
  }

  @Override
  public Color getColor() {
    return Color.ORANGE; //TODO Parameterize?
  }

  @Override
  public Collection<Terminal> getTerminals() {
    return new LinkedHashSet<Terminal>(terminals);
  }
  
  //TODO The following two methods, I'd made abstract here and implemented in a subclass - possibly revert to that
  //protected abstract void draw1(Graphics2D g, Color colorOverride);
  protected void draw1(Graphics2D g, Color colorOverride) {
    g.setColor(colorOverride != null ? colorOverride : getColor());
    g.drawRect(LEFT, TOP, WIDTH, HEIGHT);
  }

  public Point2D.Double getCenter() {
    //TODO Static or something?
    return new Point2D.Double(0, 0);
  }
}
