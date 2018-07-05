/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.connections.base;

import java.util.HashSet;
import java.util.LinkedHashSet;

/**
 *
 * @author erhannis
 */
public class Project {
  public LinkedHashSet<Network> networks = new LinkedHashSet<>();
  public Network mainNetwork;
  
  /**
   * Compiles the project to <i>source code</i>, not to a binary.
   */
  public void compile() {
    //TODO Do
    //TODO Check file changes
    throw new RuntimeException("Not yet implemented");
  }
}
