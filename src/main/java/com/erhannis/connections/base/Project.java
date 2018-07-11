/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.connections.base;

import java.io.File;
import java.util.HashSet;
import java.util.LinkedHashSet;

/**
 *
 * @author erhannis
 */
public class Project implements Compilable {
  public LinkedHashSet<Network> networks = new LinkedHashSet<>();
  public Network mainNetwork;
  
  /**
   * Compiles the project to <i>source code</i>, not to a binary.
   */
  @Override
  public void compile(File root) throws CompilationException {
    //TODO Do
    //TODO Check file changes
    for (Network network : networks) {
      network.compile(root);
    }
  }
}
