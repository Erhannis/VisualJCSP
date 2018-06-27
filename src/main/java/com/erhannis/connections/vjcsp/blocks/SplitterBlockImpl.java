/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.connections.vjcsp.blocks;

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
public class SplitterBlockImpl implements CSProcess {

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
  public SplitterBlockImpl(final ChannelInput in, final ChannelOutput[] out) {
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
