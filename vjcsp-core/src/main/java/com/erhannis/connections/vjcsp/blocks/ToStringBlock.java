/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.connections.vjcsp.blocks;

import com.erhannis.connections.base.BlockArchetype;
import com.erhannis.connections.base.BlockWireform;
import com.erhannis.connections.base.Terminal;
import com.erhannis.connections.base.TransformChain;
import com.erhannis.connections.vjcsp.IntOrEventualClass;
import com.erhannis.connections.vjcsp.PlainInputTerminal;
import com.erhannis.connections.vjcsp.PlainOutputTerminal;
import com.erhannis.connections.vjcsp.ProcessBlock;
import com.squareup.javapoet.CodeBlock;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import jcsp.lang.CSProcess;
import jcsp.lang.ChannelInput;
import jcsp.lang.ChannelOutput;
import jcsp.lang.Parallel;
import jcsp.plugNplay.Deparaplex;
import jcsp.plugNplay.ProcessWrite;

/**
 * Reads an input object x, outputs Objects.toString(x).
 *
 * @author erhannis
 */
public class ToStringBlock implements CSProcess { //TODO Do same for int channel
  public static class Wireform extends ProcessBlock {
    public static class Archetype implements BlockArchetype {
      @Override
      public HashMap<String, Class> getParameters() {
        HashMap<String, Class> parameters = new HashMap<>();
        return parameters;
      }

      @Override
      public Wireform createWireform(HashMap<String, Object> params, String name, TransformChain transformChain) {
        params = (params != null ? params : new HashMap<String, Object>());
        Wireform wireform = new Wireform(name, transformChain);
        wireform.terminals.add(new PlainInputTerminal("in", new TransformChain(null, transformChain), new IntOrEventualClass(Object.class)));
        wireform.terminals.add(new PlainOutputTerminal("out", new TransformChain(null, transformChain), new IntOrEventualClass(String.class)));
        return wireform;
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

    private HashMap<String, Object> params = new HashMap<>();

    public Wireform(String name, TransformChain transformChain) {
      super(name, transformChain);
    }

    @Override
    public void compile(File root) throws CompilationException {
      new Archetype().compile(root);
      //TODO Do
      System.err.println("Implement (ToStringBlock.Wireform).compile()");
    }

    @Override
    public CodeBlock getConstructor(Map<String, String> paramToChannelname, Map<Terminal, String> terminalToChannelname) {
      String inCname = ERROR_NAME;
      String outCname = ERROR_NAME;
      for (Terminal t : getTerminals()) {
        if (t instanceof PlainInputTerminal) {
          inCname = terminalToChannelname.get(t);
        } else {
          outCname = terminalToChannelname.get(t);
        }
      }
      ArrayList<Object> formatArgs = new ArrayList<>();
      formatArgs.add(getRunformClass());
      formatArgs.add(inCname);
      formatArgs.add(outCname);
      return CodeBlock.builder().add("new $T($L, $L)", formatArgs.toArray()).build();
    }
    
    @Override
    public HashMap<String, Object> getParameters() {
      return params;
    }
    
    @Override
    public Archetype getArchetype() {
      //TODO Could probably singleton
      return new Archetype();
    }
  }

  private final ChannelInput in;
  private final ChannelOutput out;

  /**
   * Construct a new SplitterBlockImpl process with the input Channel in and the
   * output Channels out. The ordering of the Channels in the out array make no
   * difference to the functionality of this process.
   *
   * @param in the input channel
   * @param out the output Channels
   */
  public ToStringBlock(final ChannelInput in, final ChannelOutput out) {
    this.in = in;
    this.out = out;
  }

  @Override
  public void run() {
    while (true) {
      Object data = in.read();
      out.write(Objects.toString(data));
    }
  }
}
