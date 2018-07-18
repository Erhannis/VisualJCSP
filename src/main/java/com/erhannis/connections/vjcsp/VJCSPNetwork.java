/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.connections.vjcsp;

import com.erhannis.connections.base.BlockArchetype;
import com.erhannis.connections.base.BlockWireform;
import com.erhannis.connections.base.Compilable;
import com.erhannis.connections.base.Connection;
import com.erhannis.connections.base.Drawable;
import static com.erhannis.connections.base.Drawable.TOP;
import com.erhannis.connections.base.Network;
import com.erhannis.connections.base.Terminal;
import com.erhannis.connections.base.TransformChain;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Represents a network of blocks.
 *
 * I feel like there ought to be a Network interface, but I'm not sure what it
 * would do.
 *
 * @author erhannis
 */
public class VJCSPNetwork implements Drawable, Network {
  public HashSet<ProcessBlock> blocks = new HashSet<>();
  public HashSet<PlainChannelConnection> connections = new HashSet<>();
  public transient HashMap<Terminal, PlainChannelConnection> t2c = new HashMap<>();

  protected TransformChain transformChain = new TransformChain(new AffineTransform(), null);

  @Override
  public void draw0(Graphics2D g, Color colorOverride) {
    AffineTransform prevTransform = g.getTransform();
    for (ProcessBlock block : blocks) {
      block.draw(g, colorOverride);
      g.setTransform(prevTransform);
    }
    for (PlainChannelConnection connection : connections) {
      connection.draw(g, colorOverride);
    }
    //TODO Draw border or something?
  }

  @Override
  public Point2D.Double getCenter() {
    return new Point2D.Double(0, 0);
  }

  @Override
  public TransformChain getTransformChain() {
    return transformChain;
  }

  /**
   * Currently implemented: PlainOutputTerminal -> PlainInputTerminal
   *
   * Assumes that both terminals are descendants of this network. System
   * behavior is undefined otherwise.
   *
   * //TODO Extract to...uh, where, actually?
   *
   * @param a
   * @param b
   */
  public void connect(Terminal a, Terminal b) {
    if (a instanceof PlainOutputTerminal && b instanceof PlainInputTerminal) {
      if (t2c.containsKey(a)) {
        if (t2c.containsKey(b)) {
          if (t2c.get(a) != t2c.get(b)) {
            // Error; can't (currently?) merge two connections
            throw new IllegalArgumentException("Terminals are already connected to different connections");
          } else {
            // Nothing; connecting two connected terminals is a NOP
          }
        } else {
          // Add B to A's Connection
          PlainChannelConnection connection = t2c.get(a);
          connection.addToTerminal(b);
          t2c.put(b, connection);
        }
      } else if (t2c.containsKey(b)) {
        // Add A to B's Connection
        PlainChannelConnection connection = t2c.get(b);
        connection.addFromTerminal(a);
        t2c.put(a, connection);
      } else {
        // Create connection with both A and B
        PlainChannelConnection connection = new PlainChannelConnection((PlainOutputTerminal) a, (PlainInputTerminal) b);
        connections.add(connection);
        t2c.put(a, connection);
        t2c.put(b, connection);
      }
    } else {
      throw new IllegalArgumentException("Unhandled terminal type-pair (" + a + ", " + b + ")");
    }
  }

  /**
   * //TODO Extract?
   *
   * @param t
   */
  public void disconnect(Terminal t) {
    Iterator<PlainChannelConnection> connIter = connections.iterator();
    while (connIter.hasNext()) {
      Connection c = connIter.next();
      if (c.removeTerminal(t)) {
        // Delete connection
        Iterator<Entry<Terminal, PlainChannelConnection>> entryIter = t2c.entrySet().iterator();
        while (entryIter.hasNext()) {
          Entry<Terminal, PlainChannelConnection> entry = entryIter.next();
          if (c.equals(entry.getValue())) {
            entryIter.remove();
          }
        }
        connIter.remove();
      }
    }
    t2c.remove(t);
  }

  @Override
  public Color getColor() {
    return Color.YELLOW; //TODO Parameterize?
  }

  @Override
  public void compile(File root) throws CompilationException {
    for (Compilable compilable : blocks) {
      compilable.compile(root);
    }
    //TODO Do
    //System.err.println("Implement (VJCSPNetwork).compile() properly");
    throw new RuntimeException("Implement (VJCSPNetwork).compile()");
  }

  @Override
  public Set<BlockArchetype> getArchetypes() {
    return blocks.stream().flatMap(n -> n.getArchetypes().stream()).collect(Collectors.toSet());
  }
}
