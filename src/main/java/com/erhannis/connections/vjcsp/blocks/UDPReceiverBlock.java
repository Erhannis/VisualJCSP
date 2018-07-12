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
import com.erhannis.connections.vjcsp.PlainOutputTerminal;
import com.erhannis.connections.vjcsp.ProcessBlock;
import java.io.File;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.HashMap;
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
public class UDPReceiverBlock implements CSProcess {
  public static class Wireform extends ProcessBlock {
    public static class Archetype implements BlockArchetype {
      @Override
      public String getName() {
        return "UDPReceiverBlock";
      }

      @Override
      public HashMap<String, Class> getParameters() {
        HashMap<String, Class> parameters = new HashMap<>();
        parameters.put("port", Integer.class);
        return parameters;
      }

      @Override
      public Wireform createWireform(HashMap<String, Object> params, String label, TransformChain transformChain) {
        params = (params != null ? params : new HashMap<String, Object>());
        Wireform wireform = new Wireform(false, params, label, transformChain);
        //TODO I feel like this could be consolidated.
        if (!params.containsKey("port")) {
          wireform.terminals.add(new PlainInputTerminal("port", new TransformChain(null, transformChain), new IntOrEventualClass(Integer.class)));
        }
        wireform.terminals.add(new PlainOutputTerminal("msg", new TransformChain(null, transformChain), new IntOrEventualClass(String.class)));
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
      System.err.println("Implement (UDPReceiverBlock.Wireform).compile()");
    }

    @Override
    public Archetype getArchetype() {
      //TODO Could probably singleton
      return new Archetype();
    }
  }

  private final ChannelInput portIn;
  private final ChannelOutput out;

  public UDPReceiverBlock(ChannelInput portIn, final ChannelOutput out) {
    this.portIn = portIn;
    this.out = out;
  }

  @Override
  public void run() {
    Integer port = (Integer) portIn.read();
    //TODO Make more robust?
    try {
      // Create a socket to listen on the port.
      DatagramSocket dsocket = new DatagramSocket(port);

      // Create a buffer to read datagrams into. If a
      // packet is larger than this buffer, the
      // excess will simply be discarded!
      //TODO Parameterize
      byte[] buffer = new byte[2048];

      // Create a packet to receive data into the buffer
      DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

      // Now loop forever, waiting to receive packets and printing them.
      while (true) {
        // Wait to receive a datagram
        dsocket.receive(packet);

        // Convert the contents to a string, and display them
        //TODO Write out byte[]?
        String msg = new String(buffer, 0, packet.getLength());
        //TODO Make source part of output?
        //System.out.println(packet.getAddress().getHostName() + ": " + msg);
        out.write(msg);

        // Reset the length of the packet before reusing it.
        packet.setLength(buffer.length);
      }
    } catch (Exception e) {
      System.err.println("UDPReceiverBlock going down!");
      e.printStackTrace();
    }
  }
}
