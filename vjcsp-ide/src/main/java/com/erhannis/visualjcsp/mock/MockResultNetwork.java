/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.visualjcsp.mock;

import com.erhannis.vjcsp.core.Generate;
import com.erhannis.connections.vjcsp.blocks.UDPReceiverBlock;
import com.erhannis.connections.vjcsp.blocks.UDPTransmitterBlock;
import jcsp.lang.AltingChannelInput;
import jcsp.lang.CSProcess;
import jcsp.lang.Channel;
import jcsp.lang.ChannelOutput;
import jcsp.lang.One2OneChannel;
import jcsp.lang.Parallel;

/**
 *
 * @author erhannis
 */
public class MockResultNetwork implements CSProcess {
  @Override
  public void run() {
    //TODO How to name things?
    One2OneChannel receiverPortChannel = Channel.one2one();
    AltingChannelInput receiverPortChannelIn = receiverPortChannel.in();
    ChannelOutput receiverPortChannelOut = receiverPortChannel.out();
    
    One2OneChannel msgChannel = Channel.one2one();
    AltingChannelInput msgChannelIn = msgChannel.in();
    ChannelOutput msgChannelOut = msgChannel.out();

    One2OneChannel transmitterPortChannel = Channel.one2one();
    AltingChannelInput transmitterPortChannelIn = transmitterPortChannel.in();
    ChannelOutput transmitterPortChannelOut = transmitterPortChannel.out();

    One2OneChannel transmitterHostnameChannel = Channel.one2one();
    AltingChannelInput transmitterHostnameChannelIn = transmitterHostnameChannel.in();
    ChannelOutput transmitterHostnameChannelOut = transmitterHostnameChannel.out();
    
    new Parallel(new CSProcess[] {
      new Generate(receiverPortChannelOut, 1234),
      new Generate(transmitterPortChannelOut, 1235),
      new Generate(transmitterHostnameChannelOut, "localhost"),
      new UDPReceiverBlock(receiverPortChannelIn, msgChannelOut),
      new UDPTransmitterBlock(transmitterHostnameChannelIn, transmitterPortChannelIn, msgChannelIn)
    }).run();
  }
}

