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
import java.util.Set;
import java.util.stream.Collectors;
import jcsp.lang.CSProcess;
import jcsp.lang.ChannelInput;
import jcsp.lang.ChannelOutput;
import jcsp.lang.Parallel;
import jcsp.plugNplay.Deparaplex;
import jcsp.plugNplay.ProcessWrite;

/**
 * Reads an input, outputs that value in parallel onto N channels. Output can
 * happen in any order, but must complete before the next value is read. Forked
 * from jcsp.plugNplay.Deparaplex.
 *
 * I'm not yet sure of the relationship between this class and SplitterBlock.
 *
 * @author erhannis
 */
public class SplitterBlock implements CSProcess {
  public static class Wireform extends ProcessBlock {
    public static class Archetype implements BlockArchetype {
      @Override
      public HashMap<String, Class> getParameters() {
        HashMap<String, Class> parameters = new HashMap<>();
        //TODO Generics, rather than type?
        parameters.put("type", IntOrEventualClass.class);
        parameters.put("count", Integer.class);
        return parameters;
      }

      @Override
      public Wireform createWireform(HashMap<String, Object> params, String name, TransformChain transformChain) {
        params = (params != null ? params : new HashMap<String, Object>());
        Wireform wireform = new Wireform(false, params, name, transformChain);
        IntOrEventualClass type = (IntOrEventualClass) params.getOrDefault("type", new IntOrEventualClass(Object.class));
        int count = (Integer) params.getOrDefault("count", 2);
        wireform.terminals.add(new PlainInputTerminal("in", new TransformChain(null, transformChain), type));
        for (int i = 0; i < count; i++) {
          //TODO Name: [0,outs), or [1,outs]?
          wireform.terminals.add(new PlainOutputTerminal("out" + i, new TransformChain(null, transformChain), type));
        }
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

    private HashMap<String, Object> params; //TODO Move this and getter into parent?

    public Wireform(boolean isArchetype, HashMap<String, Object> params, String name, TransformChain transformChain) {
      super(name, transformChain);
      this.params = params;
    }

    @Override
    public void compile(File root) throws CompilationException {
      new Archetype().compile(root);
      //TODO Do
      System.err.println("Implement (SplitterBlock.Wireform).compile()");
    }

    @Override
    public CodeBlock getConstructor(Map<String, String> paramToChannelname, Map<Terminal, String> terminalToChannelname) {
      String inCname = ERROR_NAME;
      ArrayList<String> outCnames = new ArrayList<>();
      for (Terminal t : getTerminals()) {
        if (t instanceof PlainInputTerminal) {
          inCname = terminalToChannelname.get(t);
        } else {
          outCnames.add(terminalToChannelname.get(t));
        }
      }
      ArrayList<Object> formatArgs = new ArrayList<>();
      formatArgs.add(getRunformClass());
      formatArgs.add(inCname);
      formatArgs.addAll(outCnames);
      String outCformat = String.join(", ", outCnames.stream().map(s -> "$L").collect(Collectors.toList()));
      return CodeBlock.builder().add("new $T($L, new ChannelOutput[] {" + outCformat + "})", formatArgs.toArray()).build();
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
  private final ChannelOutput[] out;

  /**
   * Construct a new SplitterBlockImpl process with the input Channel in and the
   * output Channels out. The ordering of the Channels in the out array make no
   * difference to the functionality of this process.
   *
   * @param in the input channel
   * @param out the output Channels
   */
  public SplitterBlock(final ChannelInput in, final ChannelOutput[] out) {
    this.in = in;
    this.out = out;
  }

  @Override
  public void run() {
    final ProcessWrite[] outputProcess = new ProcessWrite[out.length];
    for (int i = 0; i < out.length; i++) {
      outputProcess[i] = new ProcessWrite(out[i]);
    }
    Parallel parOutput = new Parallel(outputProcess);

    while (true) {
      Object data = in.read();
      for (int i = 0; i < outputProcess.length; i++) {
        outputProcess[i].value = data;
      }
      parOutput.run();
    }
  }
}
