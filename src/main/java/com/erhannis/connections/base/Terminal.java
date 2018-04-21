/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.connections.base;

/**
 *
 * @author erhannis
 */
public interface Terminal {
  //TODO Could the kind of connection make a difference?
  /**
   * Can this terminal connect to terminal t?
   * 
   * SHOULD call t.canConnectFrom(this) as part of its checks.
   * 
   * @param t
   * @return 
   */
  public boolean canConnectTo(Terminal t);
  
  /**
   * Can this terminal accept a connection from t?
   * 
   * SHOULD NOT call t.canConnectTo(this) as part of its checks.
   * Doing so could cause an infinite loop, and stack overflow.
   * 
   * This may be written under the assumption that t.canConnectTo(this) would/has otherwise returned true.
   * In other words, unless you have particular need to check something extra, you can probably return true.
   * 
   * @param t
   * @return 
   */
  public boolean canConnectFrom(Terminal t);
}
