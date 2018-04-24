/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.connections.vjcsp;

import com.erhannis.connections.base.Connection;
import com.erhannis.connections.base.Drawable;
import com.erhannis.connections.base.Terminal;
import com.erhannis.connections.base.TransformChain;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.HashSet;

/**
 *
 * @author erhannis
 */
public class PlainChannelConnection implements Connection, Drawable {
  protected static final int CLMODE_SQUARE = 0;
  protected static final int CLMODE_DIRECT = 1;
  
  protected static final int CONNECTION_LINE_MODE = CLMODE_SQUARE;
  
  protected final HashSet<PlainOutputTerminal> outputTerminals = new HashSet<>();
  protected final HashSet<PlainInputTerminal> inputTerminals = new HashSet<>();
  
  //TODO Beware, because of Connection's dependence on world coords of other things, it may react poorly to having its transform changed or parented.
  protected TransformChain transformChain = new TransformChain(new AffineTransform(), null);

  public PlainChannelConnection(PlainOutputTerminal output, PlainInputTerminal input) {
    //TODO There's something missing somewhere, about adding the connection to the terminals themselves.
    outputTerminals.add(output);
    inputTerminals.add(input);
  }

  @Override
  public void addFromTerminal(Terminal t) {
    outputTerminals.add((PlainOutputTerminal) t);
  }

  @Override
  public void addToTerminal(Terminal t) {
    inputTerminals.add((PlainInputTerminal) t);
  }

  @Override
  public void draw(Graphics2D g) {
    Color prevColor = g.getColor();
    AffineTransform prevTransform = g.getTransform();
   
    //TODO Make this kind of thing default for Drawable?
    draw0(g);
    
    g.setColor(prevColor);
    g.setTransform(prevTransform);
  }

  protected void draw0(Graphics2D g) {
    HashMap<PlainTerminal, ProcessBlock> t2b = new HashMap<>();
    
    Point2D.Double center = getCenter();
    for (PlainOutputTerminal pot : outputTerminals) {
      g.setColor(pot.getType().getColor().brighter()); // Tweak?  Other way round?
      Point2D.Double pt = pot.getCenter(); //TODO Ooooh...heck.  This won't work.
      g.draw(getConnectionPath(pt, center));
    }
    for (PlainInputTerminal pit : inputTerminals) {
      g.setColor(pit.getType().getColor());
      Point2D.Double pt = pit.getCenter(); //TODO Ooooh...heck.  This won't work.
      g.draw(getConnectionPath(pt, center));
    }
    //TODO Draw buffer, etc.
    g.draw(getConnectionPath(TOP, TOP, TOP, TOP));
  }

  @Override
  public Point2D.Double getCenter() {
    Point2D.Double result = new Point2D.Double();
    for (PlainInputTerminal pit : inputTerminals) {
      Point2D.Double pt = pit.getCenter();
      result.x += pt.x;
      result.y += pt.y;
    }
    for (PlainOutputTerminal pot : outputTerminals) {
      Point2D.Double pt = pot.getCenter();
      result.x += pt.x;
      result.y += pt.y;
    }
    result.x /= (inputTerminals.size() + outputTerminals.size());
    result.y /= (inputTerminals.size() + outputTerminals.size());
    return result;
  }

  protected Path2D getConnectionPath(Point2D.Double a, Point2D.Double b) {
    return getConnectionPath(a.x, a.y, b.x, b.y);
  }
  
  protected Path2D getConnectionPath(double ax, double ay, double bx, double by) {
    Path2D path = new Path2D.Double();
    switch (CONNECTION_LINE_MODE) {
      case CLMODE_SQUARE:
        if (ax == bx || ay == by) {
          path.moveTo(ax, ay);
          path.lineTo(bx, by);
          return path;
        }
        double slopeFactor = Math.atan(Math.abs(by - ay) / Math.abs(bx - ax)) / (Math.PI / 2);
        double midpoint = ax + ((bx - ax) * slopeFactor);
        path.moveTo(ax, ay);
        path.lineTo(midpoint, ay);
        path.lineTo(midpoint, by);
        path.lineTo(bx, by);
        return path;
      case CLMODE_DIRECT:
      default:
        path.moveTo(ax, ay);
        path.lineTo(bx, by);
        return path;
    }
  }

  @Override
  public TransformChain getTransformChain() {
    return transformChain;
  }
}
