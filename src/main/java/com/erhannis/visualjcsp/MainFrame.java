/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.visualjcsp;

import com.erhannis.connections.ConnectionsPanel;
import com.erhannis.connections.base.TransformChain;
import com.erhannis.connections.vjcsp.FileProcessBlock;
import com.erhannis.connections.vjcsp.IntOrEventualClass;
import com.erhannis.connections.vjcsp.PlainInputTerminal;
import com.erhannis.connections.vjcsp.PlainOutputTerminal;
import com.erhannis.connections.vjcsp.ProcessBlock;
import com.erhannis.connections.vjcsp.VJCSPNetwork;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author erhannis
 */
public class MainFrame extends javax.swing.JFrame {

  protected ConnectionsPanel panel;

  /**
   * Creates new form MainFrame
   */
  public MainFrame() {
    initComponents();

    panel = new ConnectionsPanel();
    jSplitPane1.setRightComponent(panel);
    
    this.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent ev) {
        if (hasChanged()) {
          switch (JOptionPane.showConfirmDialog(MainFrame.this, "Save before closing?")) {
            case JOptionPane.YES_OPTION:
              saveNetwork();
              MainFrame.this.dispose();
              break;
            case JOptionPane.NO_OPTION:
              MainFrame.this.dispose();
              break;
            case JOptionPane.CANCEL_OPTION:
              break;
          }
        } else {
          MainFrame.this.dispose();
        }
      }
    });

    {
      // Example
      VJCSPNetwork network = new VJCSPNetwork();

      double scale = 20;

      FileProcessBlock generate1 = new FileProcessBlock("Generate 1", new TransformChain(AffineTransform.getTranslateInstance(4 * scale, 4 * scale), network.getTransformChain()));
      PlainOutputTerminal g1o = generate1.addPlainOutputTerminal("StrOut", new IntOrEventualClass(String.class));

      FileProcessBlock generate2 = new FileProcessBlock("Generate 2", new TransformChain(AffineTransform.getTranslateInstance(12 * scale, 4 * scale), network.getTransformChain()));
      PlainOutputTerminal g2o = generate2.addPlainOutputTerminal("StrOut", new IntOrEventualClass(String.class));

      FileProcessBlock concat = new FileProcessBlock("Concat", new TransformChain(AffineTransform.getTranslateInstance(8 * scale, 8 * scale), network.getTransformChain()));
      PlainInputTerminal ci1 = concat.addPlainInputTerminal("StrIn 1", new IntOrEventualClass(String.class));
      PlainInputTerminal ci2 = concat.addPlainInputTerminal("StrIn 2", new IntOrEventualClass(String.class));
      PlainOutputTerminal co = concat.addPlainOutputTerminal("StrOut", new IntOrEventualClass(String.class));

      Random r = new Random();
      for (int i = 0; i < 20; i++) {
        FileProcessBlock fpb = new FileProcessBlock("test " + i, new TransformChain(AffineTransform.getTranslateInstance(r.nextDouble() * 20 * scale, r.nextDouble() * 20 * scale), network.getTransformChain()));
        int top = r.nextInt(4);
        for (int j = 0; j < top; j++) {
          fpb.addPlainInputTerminal(i + "i" + j, new IntOrEventualClass(String.class));
        }
        top = r.nextInt(4);
        for (int j = 0; j < top; j++) {
          fpb.addPlainOutputTerminal(i + "o" + j, new IntOrEventualClass(String.class));
        }
        //network.blocks.add(fpb);
      }

      FileProcessBlock sysout = new FileProcessBlock("sysout", new TransformChain(AffineTransform.getTranslateInstance(8 * scale, 12 * scale), network.getTransformChain()));
      PlainInputTerminal si = sysout.addPlainInputTerminal("StrIn", new IntOrEventualClass(String.class));

      network.blocks.add(generate1);
      network.blocks.add(generate2);
      network.blocks.add(concat);
      network.blocks.add(sysout);
      network.connect(g1o, ci1);
      network.connect(g2o, ci2);
      network.connect(co, si);

      panel.setNetwork(network);
      if (1 == 1) {
        return;
      }
    }
  }

  private boolean hasChanged() {
    //TODO Do
    System.err.println("Implement hasChanged");
    return false;
  }
  
  private void saveNetwork() {
    //TODO Do
    System.err.println("Implement saveNetwork");
  }
  
  /**
   * This method is called from within the constructor to initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    jSplitPane1 = new javax.swing.JSplitPane();
    jPanel1 = new javax.swing.JPanel();
    jPanel2 = new javax.swing.JPanel();

    setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

    javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
    jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 100, Short.MAX_VALUE)
    );
    jPanel1Layout.setVerticalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 300, Short.MAX_VALUE)
    );

    jSplitPane1.setLeftComponent(jPanel1);

    javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
    jPanel2.setLayout(jPanel2Layout);
    jPanel2Layout.setHorizontalGroup(
      jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 291, Short.MAX_VALUE)
    );
    jPanel2Layout.setVerticalGroup(
      jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 300, Short.MAX_VALUE)
    );

    jSplitPane1.setRightComponent(jPanel2);

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(jSplitPane1)
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(jSplitPane1)
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents

  /**
   * @param args the command line arguments
   */
  public static void main(String args[]) {
    /* Set the Nimbus look and feel */
    //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
     * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
     */
    try {
      for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
        if ("Nimbus".equals(info.getName())) {
          javax.swing.UIManager.setLookAndFeel(info.getClassName());
          break;
        }
      }
    } catch (ClassNotFoundException ex) {
      java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    } catch (InstantiationException ex) {
      java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    } catch (IllegalAccessException ex) {
      java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    } catch (javax.swing.UnsupportedLookAndFeelException ex) {
      java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    }
    //</editor-fold>

    /* Create and display the form */
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        new MainFrame().setVisible(true);
      }
    });
  }

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JPanel jPanel1;
  private javax.swing.JPanel jPanel2;
  private javax.swing.JSplitPane jSplitPane1;
  // End of variables declaration//GEN-END:variables
}
