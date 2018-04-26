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
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.HashSet;

/**
 *
 * @author erhannis
 */
public class PlainChannelConnection implements Connection, Drawable {
  protected static final int CLMODE_SQUARE = 0;
  protected static final int CLMODE_DIRECT = 1;
  
  protected static final int CONNECTION_LINE_MODE = CLMODE_DIRECT;
  
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
  public void draw0(Graphics2D g) {
    Point2D.Double center = getCenter();
    Point2D.Double pt = new Point2D.Double();
    Color color = getColor();
    for (PlainOutputTerminal pot : outputTerminals) {
      g.setColor(color);
      pot.transformChain.computeWorldTransform().transform(pot.getCenter(), pt);
      g.draw(getConnectionPath(pt, center));
    }
    color = color.brighter();
    for (PlainInputTerminal pit : inputTerminals) {
      g.setColor(color);
      pit.transformChain.computeWorldTransform().transform(pit.getCenter(), pt);
      g.draw(getConnectionPath(center, pt));
    }
    //TODO Draw buffer, etc.
  }

  @Override
  public Point2D.Double getCenter() {
    Point2D.Double result = new Point2D.Double();
    for (PlainInputTerminal pit : inputTerminals) {
      Point2D.Double pt = new Point2D.Double();
      pit.transformChain.computeWorldTransform().transform(pit.getCenter(), pt);
      result.x += pt.x;
      result.y += pt.y;
    }
    for (PlainOutputTerminal pot : outputTerminals) {
      Point2D.Double pt = new Point2D.Double();
      pot.transformChain.computeWorldTransform().transform(pot.getCenter(), pt);
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

  @Override
  public Color getColor() {
    IntOrEventualClass min = null;
    //TODO Check for bad connections?  Turn red?
    for (PlainOutputTerminal pot : outputTerminals) {
      if (min == null || (IntOrEventualClass.compare(min, pot.getType()) < 0)) {
        min = pot.getType();
      }
    }
    for (PlainInputTerminal pit : inputTerminals) {
      if (min == null || (IntOrEventualClass.compare(min, pit.getType()) < 0)) {
        min = pit.getType();
      }
    }
    if (min != null) {
      return min.getColor();
    } else {
      //TODO Exception?  Log?
      return Color.RED;
    }
  }
}
