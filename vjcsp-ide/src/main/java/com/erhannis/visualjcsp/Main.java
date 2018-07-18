/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.visualjcsp;

import com.erhannis.connections.vjcsp.IntOrEventualClass;
import jcsp.util.ChannelDataStore;

/**
 *
 * @author erhannis
 */
public class Main {

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    MainFrame mf = new MainFrame();
    mf.setVisible(true);
  }

  private static void testIntOrEventualClassComparison() {
    IntOrEventualClass object = new IntOrEventualClass(Object.class);
    IntOrEventualClass number = new IntOrEventualClass(Number.class);
    IntOrEventualClass dbl = new IntOrEventualClass(Double.class);
    IntOrEventualClass intint = new IntOrEventualClass();
    IntOrEventualClass nt = new IntOrEventualClass(Integer.class);
    IntOrEventualClass sNumber = new IntOrEventualClass("java.lang.Number");
    IntOrEventualClass sDouble = new IntOrEventualClass("java.lang.Double");
    IntOrEventualClass[] options = {object, number, dbl, intint, nt, sNumber, sDouble};
    for (IntOrEventualClass a : options) {
      for (IntOrEventualClass b : options) {
        String result = "ERROR";
        try {
          result = "" + IntOrEventualClass.compare(a, b);
        } catch (Throwable t) {
        }
        System.out.println(a + " ? " + b + " : " + result);
      }
    }
  }
}
