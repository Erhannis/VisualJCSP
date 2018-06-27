/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.connections.vjcsp.blocks;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
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
public class UDPTransmitterBlockImpl implements CSProcess {

  private final String hostname;
  private final int port;
  private final ChannelInput in;

  public UDPTransmitterBlockImpl(String hostname, int port, final ChannelInput in) {
    this.hostname = hostname;
    this.port = port;
    this.in = in;
  }

  @Override
  public void run() {
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
      System.err.println("UDPTransmitterBlockImpl going down!");
      e.printStackTrace();
    }
  }
}
