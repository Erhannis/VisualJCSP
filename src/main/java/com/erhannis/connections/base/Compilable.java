/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.connections.base;

import java.io.File;
import java.util.Set;

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
  
  /**
   * Returns the Archetypes used in the object.  This includes Archetypes
   * used <i>by</i> the Wireforms in the object, if any - this method is used
   * to enumerate the classes that need to get copied during compilation.
   * 
   * //TODO Not totally sure about this one.
   * 
   * @return 
   */
  public Set<BlockArchetype> getArchetypes();
  
  public void compile(File root) throws CompilationException;
}
