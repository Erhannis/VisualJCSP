/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.connections.vjcsp;

import com.erhannis.connections.base.Block;
import com.erhannis.connections.base.Connection;
import com.erhannis.connections.base.Drawable;
import static com.erhannis.connections.base.Drawable.TOP;
import com.erhannis.connections.base.TransformChain;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

/**
 * Represents a network of blocks.
 * 
 * I feel like there ought to be a Network interface, but I'm not sure what it would do.
 * 
 * @author erhannis
 */
public class VJCSPNetwork implements Drawable {
  public HashSet<ProcessBlock> blocks;
  public HashSet<PlainChannelConnection> connections;

  protected TransformChain transformChain = new TransformChain(new AffineTransform(), null);
  
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
    AffineTransform prevTransform = g.getTransform();
    for (ProcessBlock block : blocks) {
      g.transform(block.getTransformChain().transform);
      block.draw(g);
      g.setTransform(prevTransform);
    }
    for (PlainChannelConnection connection : connections) {
      connection.draw(g);
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
}
