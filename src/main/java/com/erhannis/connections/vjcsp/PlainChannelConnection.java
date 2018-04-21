/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.connections.vjcsp;

import com.erhannis.connections.base.Connection;
import com.erhannis.connections.base.Drawable;
import com.erhannis.connections.base.Terminal;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
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
    asdf;
  }

  @Override
  public Point2D.Double getCenter() {
    asdf;
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
}
