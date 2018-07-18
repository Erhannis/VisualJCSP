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
    this.color = new Color(0xFF000000 | this.hashCode(), true);
  }

  public IntOrEventualClass(Class clazz) {
    this.type = Type.CLASS;
    this.clazz = clazz;
    this.clazzString = null;
    this.color = new Color(0xFF000000 | this.hashCode(), true);
  }

  public IntOrEventualClass(String clazzString) {
    this.type = Type.CLASS_STRING;
    this.clazz = null;
    this.clazzString = clazzString;
    this.color = new Color(0xFF000000 | this.hashCode(), true);
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof IntOrEventualClass)) {
      return false;
    }
    IntOrEventualClass o = (IntOrEventualClass) obj;
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

  /**
   * Returns a comparison of two IntOrEventualClass, in a manner similar to
   * `Double.compare()`.<br/>
   * If two classes cannot be ordered, this will return NaN.<br/>
   * A superclass is less than its descendant. For example:<br/>
   * Object &lt; Number &lt; Double<br/>
   * and also<br/>
   * Comparable&lt;Double&gt; &lt; Double<br/>
   * (I think `Comparable` alone might also work. I use
   * `.isAssignableFrom`.)<br/>
   * compare([Object], [Double]) would return -1.<br/>
   * compare([Double], [Object]) would return 1.<br/>
   * compare([Double], [Double]) would return 0.<br/>
   * compare([Double], [Integer]) would return NaN.<br/>
   * (If it's possible for two classes that are not equal to both be assignable
   * to each other, the system will likely return -1 regardless of the order of
   * the classes. Hopefully this situation is not possible.)<br/>
   * //TODO Check and fix? <br/>
   * INTs are only equal to each other, and fit in no hierarchy. <br/>
   * CLASS_STRINGs are not yet really implemented, but will pass if they are
   * equal.
   *
   * @param a
   * @param b
   * @return
   */
  public static double compare(IntOrEventualClass a, IntOrEventualClass b) {
    // My, what a...lovely switch statement.
    switch (a.type) {
      case INT:
        switch (b.type) {
          case INT:
            return 0;
          case CLASS:
            //TODO Technically it might be possible for b to be int.class, but I think even then it counts as different...maybe.
            return Double.NaN;
          case CLASS_STRING:
            //TODO Technically it might be possible for b to be int.class, but I think even then it counts as different...maybe.
            return Double.NaN;
          default:
            throw new RuntimeException("Unhandled type: " + b.type);
        }
      case CLASS:
        switch (b.type) {
          case INT:
            //TODO Technically it might be possible for a to be int.class, but I think even then it counts as different...maybe.
            return Double.NaN;
          case CLASS:
            if (a.clazz.equals(b.clazz)) {
              return 0;
            } else if (a.clazz.isAssignableFrom(b.clazz)) {
              return -1;
            } else if (b.clazz.isAssignableFrom(a.clazz)) {
              return 1;
            } else {
              return Double.NaN;
            }
          case CLASS_STRING:
            //TODO Implement
            throw new RuntimeException("CLASS_STRING not yet implemented");
          default:
            throw new RuntimeException("Unhandled type: " + b.type);
        }
      case CLASS_STRING:
        switch (b.type) {
          case INT:
            //TODO Technically it might be possible for a to be int.class, but I think even then it counts as different...maybe.
            return Double.NaN;
          case CLASS:
            //TODO Implement
            throw new RuntimeException("CLASS_STRING not yet implemented");
          case CLASS_STRING:
            if (a.clazzString.equals(b.clazzString)) {
              return 0;
            } else {
              //TODO Implement
              throw new RuntimeException("CLASS_STRING not yet implemented");
            }
          default:
            throw new RuntimeException("Unhandled type: " + b.type);
        }
      default:
        throw new RuntimeException("Unhandled type: " + a.type);
    }
  }

  /**
   * Can type `b` be assignable to `this` type?
   * @param b
   * @return 
   */
  public boolean isAssignableFrom(IntOrEventualClass b) {
    return IntOrEventualClass.compare(this, b) <= 0;
  }
  
  @Override
  public String toString() {
    switch (type) {
      case INT:
        return "[INT]"; //TODO Use different brackets?
      case CLASS:
        return "[" + clazz + "]";
      case CLASS_STRING:
        return "[\"" + clazzString + "\"]";
      default:
        throw new RuntimeException("Unhandled type: " + type);
    }
  }
}
