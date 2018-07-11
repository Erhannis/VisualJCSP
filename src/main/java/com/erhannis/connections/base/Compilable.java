/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.connections.base;

import java.io.File;

/**
 *
 * @author erhannis
 */
public interface Compilable {
  public static class CompilationException extends Exception {
    CompilationException(Exception ex) {
      super(ex);
    }
  }
  
  public void compile(File root) throws CompilationException;
}
