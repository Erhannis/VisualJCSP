/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.connections.base;

import java.awt.geom.AffineTransform;

/**
 *
 * @author erhannis
 */
public class TransformChain {
  public AffineTransform transform;
  public TransformChain parent;
  
  public TransformChain(AffineTransform transform, TransformChain parent) {
    this.transform = transform;
    this.parent = parent;
  }
  
  //TODO Cache?
  public AffineTransform computeWorldTransform() {
    /*/
    if (parent != null) {
      //TODO Not sure which way around this goes
      AffineTransform result = new AffineTransform(transform);
      result.concatenate(parent.computeWorldTransform());
      return result;
    } else {
      return transform;
    }
    /*/
    if (parent != null) {
      //TODO Not sure which way around this goes
      AffineTransform result = parent.computeWorldTransform();
      result.concatenate(transform);
      return result;
    } else {
      return new AffineTransform(transform);
    }
    /**/
  }
}
