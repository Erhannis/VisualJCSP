/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.connections.vjcsp;

import com.erhannis.connections.base.BlockArchetype;
import com.erhannis.connections.base.Terminal;
import com.erhannis.connections.base.TransformChain;
import com.squareup.javapoet.CodeBlock;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author erhannis
 */
public class FileProcessWireform extends ProcessBlock {
  public static class Archetype implements BlockArchetype {
    protected final String filepath;
    protected final String runformClassname;
    protected final String name;

    public Archetype(String filepath, String runformClassname, String name) {
      this.filepath = filepath;
      this.runformClassname = runformClassname;
      this.name = name; //TODO Parse from classname?
    }

    @Override
    public String getRunformClassname() {
      return runformClassname;
    }

    @Override
    public String getName() {
      return name;
    }

    @Override
    public HashMap<String, Class> getParameters() {
      HashMap<String, Class> parameters = new HashMap<>();
      throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public FileProcessWireform createWireform(HashMap<String, Object> params, String name, TransformChain transformChain) {
      params = (params != null ? params : new HashMap<String, Object>());
//      asdf;
//      FileProcessWireform wireform = new FileProcessWireform(params, name, transformChain);
      //TODO Add terminals
      throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    // It's a little obnoxious that this code has to be repeated in every Archetype
    @Override
    public boolean equals(Object obj) {
      if (obj == null) {
        return false;
      }
      return (this.getClass() == obj.getClass());
    }

    // Ditto
    @Override
    public int hashCode() {
      return this.getClass().hashCode();
    }
  }
  //TODO Track file

  public FileProcessWireform(String name, TransformChain transformChain) {
    super(name, transformChain);
  }

  public PlainInputTerminal addPlainInputTerminal(String name, IntOrEventualClass type) {
    // If the AffineTransform doesn't get set before the terminal is drawn, an error will be thrown - as it should.
    return addTerminal(new PlainInputTerminal(name, new TransformChain(null, transformChain), type));
  }

  public PlainOutputTerminal addPlainOutputTerminal(String name, IntOrEventualClass type) {
    // If the AffineTransform doesn't get set before the terminal is drawn, an error will be thrown - as it should.
    return addTerminal(new PlainOutputTerminal(name, new TransformChain(null, transformChain), type));
  }

  //TODO Extract to Block?
  //TODO Is the generics overkill?
  public <T extends VJCSPTerminal> T addTerminal(T terminal) {
    this.terminals.add(terminal);
    return terminal;
  }

  @Override
  public BlockArchetype getArchetype() {
    //TODO Do
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void compile(File root) throws CompilationException {
    //TODO Do
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public CodeBlock getConstructor(Map<String, String> paramToChannelname, Map<Terminal, String> terminalToChannelname) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public HashMap<String, Object> getParameters() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public Class getRunformClass() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }
}
