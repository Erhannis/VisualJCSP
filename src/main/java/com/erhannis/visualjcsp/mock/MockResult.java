/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.visualjcsp.mock;

/**
 * Let's say that I had a Project, with a Network, which had a UDPReceiver
 * connected to a UDPTransmitter.  What would the compiled output look like?
 * 
 * @author erhannis
 */
public class MockResult {
  public static void main(String[] args) {
    //NOTE If I made non-JCSP networks, their run method would probably go here.
    new MockResultNetwork().run();
  }
}
