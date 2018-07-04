/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.visualjcsp;

import com.erhannis.connections.ConnectionsPanel;
import com.erhannis.connections.base.Block;
import com.erhannis.connections.base.BlockArchetype;
import com.erhannis.connections.base.TransformChain;
import com.erhannis.connections.vjcsp.FileProcessBlock;
import com.erhannis.connections.vjcsp.IntOrEventualClass;
import com.erhannis.connections.vjcsp.PlainInputTerminal;
import com.erhannis.connections.vjcsp.PlainOutputTerminal;
import com.erhannis.connections.vjcsp.ProcessBlock;
import com.erhannis.connections.vjcsp.VJCSPNetwork;
import com.erhannis.connections.vjcsp.blocks.SplitterBlock;
import com.erhannis.connections.vjcsp.blocks.UDPReceiverBlock;
import com.erhannis.connections.vjcsp.blocks.UDPTransmitterBlock;
import java.awt.Component;
import java.awt.Container;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.activation.DataHandler;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import jcsp.lang.AltingChannelInput;
import jcsp.lang.Any2OneChannel;
import jcsp.lang.CSProcess;
import jcsp.lang.CSTimer;
import jcsp.lang.Channel;
import jcsp.lang.ChannelOutput;
import jcsp.lang.One2AnyChannel;
import jcsp.lang.Parallel;
import jcsp.lang.ProcessManager;
import jcsp.lang.SharedChannelInput;
import jcsp.lang.SharedChannelOutput;

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

    DefaultListModel<BlockArchetype> modelBlock = new DefaultListModel<>();
    listBlocks.setModel(modelBlock);
    listBlocks.setTransferHandler(new TransferHandler() {
      class ArchetypeTransferable implements Transferable {
        public final BlockArchetype archetype;
        
        public ArchetypeTransferable(BlockArchetype archetype) {
          this.archetype = archetype;
        }
        
        @Override
        public DataFlavor[] getTransferDataFlavors() {
          return new DataFlavor[]{ConnectionsPanel.ARCHETYPE_FLAVOR};
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
          if (ConnectionsPanel.ARCHETYPE_FLAVOR.equals(flavor) || DataFlavor.stringFlavor.equals(flavor)) {
            return true;
          } else {
            return false;
          }
        }

        @Override
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
          if (ConnectionsPanel.ARCHETYPE_FLAVOR.equals(flavor)) {
            return archetype;
          } else {
            throw new UnsupportedFlavorException(flavor);
          }
        }
      }

      @Override
      public int getSourceActions(JComponent c) {
        return TransferHandler.COPY;
      }
      
      @Override
      protected Transferable createTransferable(JComponent c) {
        return new ArchetypeTransferable((BlockArchetype)listBlocks.getSelectedValue());//new DataHandler(listBlocks.getSelectedValue(), "application/x-java-object");
      }

      @Override
      protected void exportDone(JComponent source, Transferable data, int action) {
        super.exportDone(source, data, action); //To change body of generated methods, choose Tools | Templates.
      }
    });

    Any2OneChannel keyPressedChannel = Channel.any2one(); //TODO Buffered?
    One2AnyChannel filterUpdateChannel = Channel.one2any();
    AltingChannelInput keyPressedChannelIn = keyPressedChannel.in();
    SharedChannelOutput keyPressedChannelOut = keyPressedChannel.out();
    SharedChannelInput filterUpdateChannelIn = filterUpdateChannel.in();
    ChannelOutput filterUpdateChannelOut = filterUpdateChannel.out();

    /**
     * Keyboard -> block filter<b/>
     * Keypresses build up, creating a filter that is passed on.<b/>
     * If a keypress comes in more than FILTER_KEYBOARD_TIMEOUT after the last,
     * the filter is cleared before adding the keypress.<b/>
     * ESC clears filter.<b/>
     */
    CSProcess keyboardProcess = new CSProcess() {
      @Override
      public void run() {
        CSTimer timer = new CSTimer();
        long lastPress = -1;
        StringBuilder sb = new StringBuilder();
        while (true) {
          KeyEvent ke = (KeyEvent) keyPressedChannelIn.read();
          if (timer.read() - lastPress > Settings.FILTER_KEYBOARD_TIMEOUT) {
            sb.setLength(0);
          }
          switch (ke.getKeyChar()) {
            case KeyEvent.VK_ESCAPE: //TODO SETTING
              sb.setLength(0);
              break;
            case KeyEvent.VK_BACK_SPACE:
              if (sb.length() > 0) {
                sb.setLength(sb.length() - 1);
              }
              break;
            default:
              char c = ke.getKeyChar();
              if (c != KeyEvent.CHAR_UNDEFINED) {
                sb.append(c);
              }
              break;
          }
          filterUpdateChannelOut.write(sb.toString()); //TODO This could mess up the key timings, if blocked.
          lastPress = timer.read();
        }
      }
    };
    ProcessManager pm = new ProcessManager(new Parallel(new CSProcess[]{
      keyboardProcess,
      () -> {
        while (true) {
          String filter = (String) filterUpdateChannelIn.read();
          List<BlockArchetype> archetypes = getArchetypes();
          List<BlockArchetype> filteredArchetypes;
          if (filter.startsWith("/")) {
            // Regex
            //TODO Make case insensitive?  Optionize?
            filteredArchetypes = archetypes.stream().filter(c -> c.getName().matches(".*" + filter.substring(1) + ".*")).collect(Collectors.toList());
          } else {
            if (filter.toLowerCase().equals(filter)) {
              filteredArchetypes = archetypes.stream().filter(c -> c.getName().toLowerCase().contains(filter)).collect(Collectors.toList());
            } else {
              filteredArchetypes = archetypes.stream().filter(c -> c.getName().contains(filter)).collect(Collectors.toList());
            }
          }
          System.out.println("filter: " + filter + " -> " + filteredArchetypes.size());

          //TODO invokeAndWait?
          SwingUtilities.invokeLater(() -> {
            lFilter.setText(filter);
            modelBlock.clear();
            for (BlockArchetype archetype : filteredArchetypes) {
              modelBlock.addElement(archetype);
            }
          });
        }
      }
    }));
    pm.start();

    panel = new ConnectionsPanel();
    jSplitPane1.setRightComponent(panel);

    this.addKeyListener(new KeyListener() {
      @Override
      public void keyTyped(KeyEvent e) {
        keyPressedChannelOut.write(e);
      }

      @Override
      public void keyPressed(KeyEvent e) {
      }

      @Override
      public void keyReleased(KeyEvent e) {
      }
    });

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

  private List<BlockArchetype> getArchetypes() {
    //TODO Make dynamic
    //TODO FileProcessBlock
    return Arrays.asList(new SplitterBlock.Archetype(), new UDPReceiverBlock.Archetype(), new UDPTransmitterBlock.Archetype());
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
    lFilter = new javax.swing.JLabel();
    jScrollPane1 = new javax.swing.JScrollPane();
    listBlocks = new javax.swing.JList();
    jPanel2 = new javax.swing.JPanel();

    setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

    jSplitPane1.setDividerLocation(500);

    lFilter.setText("(Filter)");
    lFilter.setToolTipText("Block filter.  Start with \"/\" to use regex.");

    listBlocks.setModel(new javax.swing.AbstractListModel() {
      String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
      public int getSize() { return strings.length; }
      public Object getElementAt(int i) { return strings[i]; }
    });
    listBlocks.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
    listBlocks.setDragEnabled(true);
    listBlocks.setFocusable(false);
    listBlocks.setRequestFocusEnabled(false);
    jScrollPane1.setViewportView(listBlocks);

    javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
    jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(lFilter)
        .addContainerGap(443, Short.MAX_VALUE))
      .addComponent(jScrollPane1)
    );
    jPanel1Layout.setVerticalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(lFilter)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 619, Short.MAX_VALUE))
    );

    jSplitPane1.setLeftComponent(jPanel1);

    javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
    jPanel2.setLayout(jPanel2Layout);
    jPanel2Layout.setHorizontalGroup(
      jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 544, Short.MAX_VALUE)
    );
    jPanel2Layout.setVerticalGroup(
      jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 658, Short.MAX_VALUE)
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
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JSplitPane jSplitPane1;
  private javax.swing.JLabel lFilter;
  private javax.swing.JList listBlocks;
  // End of variables declaration//GEN-END:variables
}
