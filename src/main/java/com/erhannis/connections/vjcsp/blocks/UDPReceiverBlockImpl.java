/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.connections.vjcsp.blocks;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import jcsp.lang.CSProcess;
import jcsp.lang.ChannelInput;
import jcsp.lang.ChannelOutput;
import jcsp.lang.Parallel;
import jcsp.plugNplay.Deparaplex;
import jcsp.plugNplay.ProcessWrite;

/**
 * Reads UDP packets onto a channel.  Outputs strings.
 *
 * http://www.java2s.com/Code/Java/Network-Protocol/ReceiveUDPpockets.htm
 *
 * @author erhannis
 */
public class UDPReceiverBlockImpl implements CSProcess {

  private final ChannelInput portIn;
  private final ChannelOutput out;

  public UDPReceiverBlockImpl(ChannelInput portIn, final ChannelOutput out) {
    this.portIn = portIn;
    this.out = out;
  }

  @Override
  public void run() {
    Integer port = (Integer)portIn.read();
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
      System.err.println("UDPReceiverBlockImpl going down!");
      e.printStackTrace();
    }
  }
}
