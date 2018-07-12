/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.connections.base;

import java.util.Collection;

/**
 *
 * @author erhannis
 */
public interface BlockWireform extends Compilable {
  //TODO `addTerminal`?...hmm, maybe not?  Some block classes may have a hardcoded terminal configuration.  getTerminals, then?
  public Collection<Terminal> getTerminals();
  
  /*
  See, there are two runform-type things.
  1 is the actual object instantiated to perform an action.
  2 is the code generated to do #1.
  It may not be possible to generate 1 from the IDE.
  Note, though - I'm wondering, there's the jar file, but there's also the code
  that calls the jar file.  Not sure how that's gonna work, yet.
  */
  //TODO createRunform
  //TODO createCodeform???
  
  public BlockArchetype getArchetype();
}
