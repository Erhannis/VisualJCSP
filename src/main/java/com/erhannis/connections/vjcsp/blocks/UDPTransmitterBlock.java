/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.connections.vjcsp.blocks;

import com.erhannis.connections.base.BlockArchetype;
import com.erhannis.connections.base.BlockWireform;
import com.erhannis.connections.base.TransformChain;
import com.erhannis.connections.vjcsp.IntOrEventualClass;
import com.erhannis.connections.vjcsp.PlainInputTerminal;
import com.erhannis.connections.vjcsp.ProcessBlock;
import java.io.File;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import jcsp.lang.AltingChannelInput;
import jcsp.lang.CSProcess;
import jcsp.lang.ChannelInput;
import jcsp.lang.ChannelOutput;
import jcsp.lang.Parallel;
import jcsp.plugNplay.Deparaplex;
import jcsp.plugNplay.ProcessWrite;

/**
 * Reads UDP packets onto a channel. Outputs strings.
 *
 * http://www.java2s.com/Code/Java/Network-Protocol/ReceiveUDPpockets.htm
 *
 * @author erhannis
 */
public class UDPTransmitterBlock implements CSProcess {
  public static class Wireform extends ProcessBlock {
    public static class Archetype implements BlockArchetype {
      @Override
      public String getName() {
        return "UDPTransmitterBlock";
      }

      @Override
      public HashMap<String, Class> getParameters() {
        HashMap<String, Class> parameters = new HashMap<>();
        parameters.put("hostname", String.class);
        parameters.put("port", Integer.class);
        return parameters;
      }

      @Override
      public Wireform createWireform(HashMap<String, Object> params, String label, TransformChain transformChain) {
        params = (params != null ? params : new HashMap<String, Object>());
        Wireform wireform = new Wireform(false, params, label, transformChain);
        //TODO I feel like this could be consolidated.
        if (!params.containsKey("hostname")) {
          wireform.terminals.add(new PlainInputTerminal("hostname", new TransformChain(null, transformChain), new IntOrEventualClass(String.class)));
        }
        if (!params.containsKey("port")) {
          wireform.terminals.add(new PlainInputTerminal("port", new TransformChain(null, transformChain), new IntOrEventualClass(Integer.class)));
        }
        wireform.terminals.add(new PlainInputTerminal("msg", new TransformChain(null, transformChain), new IntOrEventualClass(String.class)));
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

    private HashMap<String, Object> params;

    public Wireform(boolean isArchetype, HashMap<String, Object> params, String label, TransformChain transformChain) {
      super(label, transformChain);
      this.params = params;
    }

    @Override
    public void compile(File root) throws CompilationException {
      new Archetype().compile(root);
      //TODO Do
      System.err.println("Implement (UDPTransmitterBlock.Wireform).compile()");
    }

    @Override
    public Archetype getArchetype() {
      //TODO Could probably singleton
      return new Archetype();
    }
  }

  private final AltingChannelInput hostnameIn;
  private final AltingChannelInput portIn;
  private final ChannelInput in;

  public UDPTransmitterBlock(AltingChannelInput hostnameIn, AltingChannelInput portIn, final ChannelInput in) {
    this.hostnameIn = hostnameIn;
    this.portIn = portIn;
    this.in = in;
  }

  @Override
  public void run() {
    Integer port = (Integer) portIn.read();
    String hostname = (String) hostnameIn.read();
    //TODO Make more robust?
    try (DatagramSocket dsocket = new DatagramSocket()) {
      // Get the internet address of the specified host
      InetAddress address = InetAddress.getByName(hostname);

      while (true) {
        byte[] message = ((String) in.read()).getBytes();

        // Initialize a datagram packet with data and address
        DatagramPacket packet = new DatagramPacket(message, message.length, address, port);

        dsocket.send(packet);
      }
    } catch (Exception e) {
      System.err.println("UDPTransmitterBlock going down!");
      e.printStackTrace();
    }
  }
}
