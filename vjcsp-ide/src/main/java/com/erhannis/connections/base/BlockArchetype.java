/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.connections.base;

import com.erhannis.visualjcsp.Settings;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

/**
 *
 * @author erhannis
 */
public interface BlockArchetype extends Compilable {
  /**
   * Returns the name of the archetype.
   * Frequently this corresponds to the simple classname of the runform.
   * 
   * If that is true, and your Runform class contains the Wireform class contains the Archetype class,
   * then the default implementation of this method will return the correct name.
   * 
   * @return 
   */
  public default String getName() {
    return this.getClass().getEnclosingClass().getEnclosingClass().getSimpleName();
  }
  
  /**
   * Returns the possible parameters for `new (BlockArchetype)()`.
   * For example, {"port":java.lang.Integer, "host":java.lang.String}.
   * 
   * //TODO Allow params to be marked as "required", etc.?
   * //TODO Allow/require params to be ordered?
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
   * A null `params` is taken to mean "no parameters given". //TODO Is this sufficiently useful?
   * 
   * //TODO Make helper class to aid #1?
   * 
   * @param params
   * @return 
   */
  public BlockWireform createWireform(HashMap<String, Object> params, String name, TransformChain transformChain); //TODO Should the second two params be here?
  
  //TODO `getBlockType` or something?
  
  /**
   * Returns the fully qualified class of the runform.
   * When (Project).compile() is called, the class must be accessible as one of
   * the following:
   * 1.  A java source file, in (root)/vjcsp/src/(classtree)
   * 2.  A java class file, in (root)/vjcsp/class/(classtree)
   * 3.  A java class in a jar, in (root)/vjcsp/libs/(jarfile)
   * 
   * If your Runform class contains the Wireform class contains the Archetype class,
   * then the default implementation of this method will return the correct class name.
   * 
   * @return 
   */
  public default String getRunformClassname() {
    return this.getClass().getEnclosingClass().getEnclosingClass().getCanonicalName();
  }

  @Override
  public default void compile(File root) throws CompilationException {
    try {
      ClassPool cp = ClassPool.getDefault();
      CtClass clazz = cp.get(getRunformClassname());
      File libs = new File(root, Settings.CLASSES_TARGET_FOLDER);
      libs.mkdirs();
      clazz.writeFile(libs.getCanonicalPath());
    } catch (Exception ex) {
      throw new CompilationException(ex);
    }
  }

  @Override
  public default Set<BlockArchetype> getArchetypes() {
    return new HashSet<BlockArchetype>(Arrays.asList(this));
  }
}
