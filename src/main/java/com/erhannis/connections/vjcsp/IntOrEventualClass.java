/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.connections.vjcsp;

import java.awt.Color;
import java.util.Objects;

/**
 *
 * @author erhannis
 */
public class IntOrEventualClass {
  protected static enum Type {
    INT, CLASS, CLASS_STRING;
  }
  
  protected final Type type;
  protected final Class clazz;
  protected final String clazzString;
  protected final Color color; //TODO Would ` = new Color(0xFF000000 | this.hashCode())` work, here?
  
  public IntOrEventualClass() {
    this.type = Type.INT;
    this.clazz = null;
    this.clazzString = null;
    this.color = new Color(0xFF000000 | this.hashCode());
  }
  
  public IntOrEventualClass(Class clazz) {
    this.type = Type.CLASS;
    this.clazz = clazz;
    this.clazzString = null;
    this.color = new Color(0xFF000000 | this.hashCode());
  }

  public IntOrEventualClass(String clazzString) {
    this.type = Type.CLASS_STRING;
    this.clazz = null;
    this.clazzString = clazzString;
    this.color = new Color(0xFF000000 | this.hashCode());
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof IntOrEventualClass)) {
      return false;
    }
    IntOrEventualClass o = (IntOrEventualClass)obj;
    boolean result = true;
    //NOTE This could become inaccurate, if something else changed, as some of these shouldn't matter (but currently end up the same regardless)
    result &= Objects.equals(this.type, o.type);
    result &= Objects.equals(this.clazz, o.clazz);
    result &= Objects.equals(this.clazzString, o.clazzString);
    return result;
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, clazz, clazzString);
  }
  
  //TODO This is kindof related to Drawable, rather than an inherent part of the class....
  public Color getColor() {
    return color;
  }
}
