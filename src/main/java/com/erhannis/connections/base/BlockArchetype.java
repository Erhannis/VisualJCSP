/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.connections.base;

import java.util.HashMap;

/**
 *
 * @author erhannis
 */
public interface BlockArchetype {
  /**
   * Returns the possible parameters for `(BlockArchetype).create()`.
   * For example, {"port":java.lang.Integer, "host":java.lang.String}.
   * 
   * //TODO Allow params to be marked as "required", etc.?
   * //TODO Allow params to be ordered?
   * 
   * @return 
   */
  public HashMap<String, Class> getParameters();
  
  /**
   * Creates the diagram form of a block, given a list of parameters.
   * Note that you may not need to give all parameters returned by
   * `getParameters()` - it is strongly recommended that implementations do one of the
   * following, when a parameter is omitted, in decreasing order of preference: <br/>
   * 1. Add a channel for the omitted parameter.  The code form of the block will
   *      require one message on each such channel, after which it will begin normal
   *      operation.<br/>
   * 2. Provide a default for the parameter.
   * 3. Throw an IllegalArgumentException.  This is discouraged.
   * 
   * //TODO Make helper class to aid #1?
   * 
   * @param params
   * @return 
   */
  public Block createWireform(HashMap<String, Object> params);
  
  //TODO `getBlockType` or something?
}
