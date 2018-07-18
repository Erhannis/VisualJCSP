/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.connections.base;

import com.squareup.javapoet.CodeBlock;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author erhannis
 */
public interface BlockWireform extends Compilable {
  public static final String ERROR_NAME = "===ERROR==="; // I don't believe this would ever be a valid identifier
  
  //TODO `addTerminal`?...hmm, maybe not?  Some block classes may have a hardcoded terminal configuration.  getTerminals, then?
  /**
   * NOTE: All terminals must have names unique over the block, for the purpose of matching terminals to code.
   * @return 
   */
  public Collection<Terminal> getTerminals();
  
  /**
   * Returns the parameters that have been set.
   * For example, {"port":1234, "host":"localhost"}.
   * 
   * //TODO Allow params to be ordered?
   * 
   * @return 
   */
  public HashMap<String, Object> getParameters();

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
  
  /**
   * Returns the class of the runform.
   * 
   * If your Runform class contains the Wireform class,
   * then the default implementation of this method will return the correct class.
   * 
   * @return 
   */
  public default Class getRunformClass() { //TODO Not sure about this one
    return this.getClass().getEnclosingClass();
  }

  public CodeBlock getConstructor(Map<String, String> paramToChannelname, Map<Terminal, String> terminalToChannelname);
}
