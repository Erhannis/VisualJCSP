/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.connections.vjcsp.blocks.io;

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
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;
import jcsp.lang.CSProcess;
import jcsp.lang.ChannelInput;
import jcsp.lang.ChannelOutput;
import jcsp.lang.Parallel;
import jcsp.plugNplay.Deparaplex;
import jcsp.plugNplay.ProcessWrite;

/**
 * Reads from stdin onto a channel. Outputs strings.
 *
 * @author erhannis
 */
public class StdinBlock implements CSProcess {
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
        Wireform wireform = new Wireform(params, name, transformChain);
        wireform.terminals.add(new PlainOutputTerminal("readln", new TransformChain(null, transformChain), new IntOrEventualClass(String.class)));
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

    private HashMap<String, Object> params; //TODO Move this and getter into superclass?

    public Wireform(HashMap<String, Object> params, String name, TransformChain transformChain) {
      super(name, transformChain);
      this.params = params;
    }

    @Override
    public void compile(File root) throws CompilationException {
      new Archetype().compile(root);
      //TODO Do
      System.err.println("Implement (StdinBlock.Wireform).compile()");
    }

    @Override
    public CodeBlock getConstructor(Map<String, String> paramToChannelname, Map<Terminal, String> terminalToChannelname) {
      String readlnOutCname = terminalToChannelname.getOrDefault(getTerminals().stream().filter(t -> "readln".equals(t.getName())).findFirst().orElse(null), ERROR_NAME);
      ArrayList<Object> formatArgs = new ArrayList<>();
      formatArgs.add(getRunformClass());
      formatArgs.add(readlnOutCname);
      return CodeBlock.builder().add("new $T($L)", formatArgs.toArray()).build();
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

  private final ChannelOutput readlnOut;

  public StdinBlock(final ChannelOutput readlnOut) {
    this.readlnOut = readlnOut;
  }

  @Override
  public void run() {
    Scanner scan = new Scanner(System.in);
    while (true) {
      String msg = scan.nextLine();
      readlnOut.write(msg);
    }
  }
}
