/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.connections;

import com.erhannis.connections.base.BlockWireform;
import com.erhannis.connections.base.BlockArchetype;
import com.erhannis.connections.base.Connection;
import com.erhannis.connections.base.Drawable;
import com.erhannis.connections.base.Terminal;
import com.erhannis.connections.base.TransformChain;
import com.erhannis.connections.vjcsp.FileProcessBlock;
import com.erhannis.connections.vjcsp.IntOrEventualClass;
import com.erhannis.connections.vjcsp.PlainChannelConnection;
import com.erhannis.connections.vjcsp.ProcessBlock;
import com.erhannis.connections.vjcsp.VJCSPNetwork;
import com.erhannis.connections.vjcsp.blocks.SplitterBlock;
import com.erhannis.mathnstuff.Holder;
import com.erhannis.mathnstuff.MeMath;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dialog.ModalityType;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;

/**
 *
 * @author erhannis
 */
public class ConnectionsPanel extends javax.swing.JPanel {

  protected static enum Mode {

    NOTHING, SHIFT_ONLY, CTRL_ONLY, OTHER;
  }

  public static final DataFlavor ARCHETYPE_FLAVOR = new DataFlavor(BlockArchetype.class, "BlockArchetype");

  private static final double SCALE_INCREMENT = 1.1;
  private static final double VIEW_PRESCALE = 1;

  private static final Color COLOR_BACKGROUND = Color.BLACK;
  private static final Color COLOR_NORMAL = Color.LIGHT_GRAY;
  private static final Color COLOR_HIGHLIGHT = Color.CYAN;
  private static final Color COLOR_HIGH = Color.GREEN;
  private static final Color COLOR_LOW = Color.BLACK;

  //TODO This doesn't seem very generic
  protected VJCSPNetwork network;
  public LinkedHashSet<BlockWireform> selectedBlocks = new LinkedHashSet<>();
  protected boolean changed = false;

  public AffineTransform at = new AffineTransform(VIEW_PRESCALE, 0, 0, VIEW_PRESCALE, 0, 0);
  public AffineTransform ati;
  public static final Font FONT = new Font("Monospaced", 0, 14);

  /**
   * Creates new form ConnectionsPanel
   */
  public ConnectionsPanel() {
    try {
      ati = at.createInverse();
    } catch (NoninvertibleTransformException ex) {
      Logger.getLogger(ConnectionsPanel.class.getName()).log(Level.SEVERE, null, ex);
    }

    initComponents();
    setBackground(COLOR_BACKGROUND);

    /**/
    //TODO Move to MainFrame?
    ConnectionsPanel pd = this;
    Holder<Point2D> dragStartPoint = new Holder<>(null);
    Holder<Point2D> dragLastPoint = new Holder<>(null);
    Holder<Mode> dragMode = new Holder<>(null);
    Holder<Terminal> dragFromTerminal = new Holder<>(null);
    pd.addMouseListener(new MouseListener() {
      @Override
      public void mouseClicked(MouseEvent e) {
        Point2D m = pd.ati.transform(new Point2D.Double(e.getX(), e.getY()), null);
        switch (getMode(e)) {
          case NOTHING: // Select
          {
            BlockWireform picked = pickBlock(m);
            if (picked == null) {
              selectedBlocks.clear();
            } else {
              if (selectedBlocks.contains(picked) && selectedBlocks.size() == 1) {
                selectedBlocks.clear();
              } else {
                selectedBlocks.clear();
                selectedBlocks.add(picked);
              }
            }
            break;
          }
          case SHIFT_ONLY: // Disconnect terminal
          {
            Terminal picked = pickTerminal(m);

            if (picked != null) {
              changed = true;
              network.disconnect(picked);
            }
            break;
          }
          case CTRL_ONLY: // +- Selection
          {
            BlockWireform picked = pickBlock(m);
            if (selectedBlocks.contains(picked)) {
              selectedBlocks.remove(picked);
            } else {
              selectedBlocks.add(picked);
            }
            break;
          }
          default:
          // TODO Log or something?
        }
        doRepaint();
      }

      public boolean hadFocus = false;

      @Override
      public void mousePressed(MouseEvent e) {
        changed = true;
        Point2D m = pd.ati.transform(new Point2D.Double(e.getX(), e.getY()), null);
        dragStartPoint.value = m;
        dragLastPoint.value = m;
        dragMode.value = getMode(e);
        switch (dragMode.value) {
          case NOTHING: //???
          {
            //TODO Might be nice to allow immediate press-drag
            break;
          }
          case SHIFT_ONLY: //???
          {
            dragFromTerminal.value = pickTerminal(m);
            //TODO Highlight acceptable targets
            break;
          }
          case CTRL_ONLY: //???
          {
            break;
          }
          default:
          // TODO Log or something?
        }
        doRepaint();
      }

      @Override
      public void mouseReleased(MouseEvent e) {
        Point2D m = pd.ati.transform(new Point2D.Double(e.getX(), e.getY()), null);
        //selectedBlocks.clear();

        if (dragMode.value != null) {
          switch (dragMode.value) {
            case NOTHING: //???
            {
              break;
            }
            case SHIFT_ONLY: // Connect terminal
            {
              Terminal picked = pickTerminal(m);
              if (dragFromTerminal.value != null && picked != null) {
                changed = true;
                //TODO Currently doesn't check if has existing conflicting connections
                try {
                  if (dragFromTerminal.value.canConnectTo(picked)) {
                    network.connect(dragFromTerminal.value, picked);
                  }
                } catch (IllegalArgumentException ex) {
                  ex.printStackTrace();
                }
              }
              break;
            }
            case CTRL_ONLY: //???
            {
              break;
            }
            default:
            // TODO Log or something?
          }
        }

        dragStartPoint.value = null;
        dragLastPoint.value = null;
        dragMode.value = null;
        dragFromTerminal.value = null;

        doRepaint();
      }

      @Override
      public void mouseEntered(MouseEvent e) {
      }

      @Override
      public void mouseExited(MouseEvent e) {
      }
    });

    pd.addMouseMotionListener(new MouseMotionListener() {
      @Override
      public void mouseDragged(MouseEvent e) {
        if (dragMode.value == null) {
          return;
        }

        changed = true;
        Point2D m = pd.ati.transform(new Point2D.Double(e.getX(), e.getY()), null);

        switch (dragMode.value) {
          case NOTHING: // Move
          {
            if (dragLastPoint.value != null) {
              for (BlockWireform b : selectedBlocks) {
                if (b instanceof Drawable) {
                  Drawable d = (Drawable) b;
                  //TODO May not take into account parent transforms properly
                  AffineTransform worldTransform = d.getTransformChain().computeWorldTransform();
                  d.getTransformChain().transform.translate((m.getX() - dragLastPoint.value.getX()) / worldTransform.getScaleX(), (m.getY() - dragLastPoint.value.getY()) / worldTransform.getScaleY());
                }
              }
            }
            break;
          }
          case SHIFT_ONLY: // Connect terminal
          {
//            Terminal picked = pickTerminal(m);
//
//            //network.connections.stream().filter(c -> c.)
//            changed = true;
//            //TODO DO
//            throw new RuntimeException("Not yet implemented");
            break;
          }
          case CTRL_ONLY: // +- Rectangular selection
          {
            //TODO DO
            throw new RuntimeException("Not yet implemented");
            //break;
          }
          default:
          // TODO Log or something?
        }

        dragLastPoint.value = m;
        doRepaint();
      }

      @Override
      public void mouseMoved(MouseEvent e) {
      }
    });

    pd.addMouseWheelListener(new MouseWheelListener() {
              @Override
              public void mouseWheelMoved(MouseWheelEvent evt
              ) {
                switch (getMode(evt)) {
                  case NOTHING: // Select
                  {
                    double scale = Math.pow(SCALE_INCREMENT, -evt.getPreciseWheelRotation());
                    at.preConcatenate(AffineTransform.getTranslateInstance(-evt.getX(), -evt.getY()));
                    at.preConcatenate(AffineTransform.getScaleInstance(scale, scale));
                    at.preConcatenate(AffineTransform.getTranslateInstance(evt.getX(), evt.getY()));
                    try {
                      ati = at.createInverse();
                    } catch (NoninvertibleTransformException ex) {
                      Logger.getLogger(ConnectionsPanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    break;
                  }
                  case SHIFT_ONLY: // ???
                  {
                    break;
                  }
                  case CTRL_ONLY: // Resize selection
                  {
                    if (!selectedBlocks.isEmpty()) {
                      //TODO Scale blockgroup, not just blocks?  I.e., scale distances?
                      double scale = Math.pow(SCALE_INCREMENT, evt.getPreciseWheelRotation());
                      for (BlockWireform b : selectedBlocks) {
                        if (b instanceof Drawable) {
                          Drawable d = (Drawable) b;
                          d.getTransformChain().transform.scale(scale, scale);
                        }
                      }
                      changed = true;
                    }
                    break;
                  }
                  default:
                  // TODO Log or something?
                }
                doRepaint();
              }
            }
    );

    /**/
    this.setTransferHandler(new TransferHandler() {
      public boolean canImport(TransferHandler.TransferSupport support) {
        if (!support.isDataFlavorSupported(ARCHETYPE_FLAVOR)) {
          return false;
        }
        return true;
      }

      public boolean importData(TransferHandler.TransferSupport support) {
        if (!canImport(support)) {
          return false;
        }

        Transferable transferable = support.getTransferable();
        BlockArchetype data;
        try {
          data = (BlockArchetype) transferable.getTransferData(ARCHETYPE_FLAVOR);
        } catch (Exception e) {
          return false;
        }

        TransferHandler.DropLocation dl = support.getDropLocation();
        Point p = dl.getDropPoint();
        Point2D m = pd.ati.transform(new Point2D.Double(p.getX(), p.getY()), null);

        System.out.println(data);

        HashMap<String, Object> params = new HashMap<>();
        if (data.getParameters().size() > 0) {
          // Note that params is expected to be mutated
          //TODO Make name a param?  Make TransformChain an invisible param?
          WireformParamsDialog frame = new WireformParamsDialog(SwingUtilities.getWindowAncestor(ConnectionsPanel.this), "Params for " + data, ModalityType.APPLICATION_MODAL, data.getParameters(), params);
          frame.setVisible(true);
        }

        //TODO This cast is dumb, and possibly dangerous
        try {
          ProcessBlock block = (ProcessBlock) data.createWireform(params, data.getName(), new TransformChain(AffineTransform.getTranslateInstance(m.getX(), m.getY()), network.getTransformChain()));
          network.blocks.add(block);
        } catch (Exception e) {
          e.printStackTrace();
          //TODO Show error to user
        }

        doRepaint();

        return true;
      }
    });
  }

  protected static Mode getMode(InputEvent e) {
    if (!(e.isControlDown() || e.isAltDown() || e.isAltGraphDown() || e.isMetaDown() || e.isShiftDown())) {
      return Mode.NOTHING;
    } else if (e.isControlDown() && !(e.isAltDown() || e.isAltGraphDown() || e.isMetaDown() || e.isShiftDown())) {
      return Mode.CTRL_ONLY;
    } else if (e.isShiftDown() && !(e.isControlDown() || e.isAltDown() || e.isAltGraphDown() || e.isMetaDown())) {
      return Mode.SHIFT_ONLY;
    } else {
      return Mode.OTHER;
    }
  }

  protected BlockWireform pickBlock(Point2D p) {
    double closestDist2 = Double.POSITIVE_INFINITY;
    BlockWireform closest = null;
    for (BlockWireform b : network.blocks) {
      if (b instanceof Drawable) { // This seems like it ought to be a given....
        Drawable d = (Drawable) b;
        //TODO Could optimize, and/or extract
        AffineTransform t = d.getTransformChain().computeWorldTransform();
        Point2D center = t.transform(d.getCenter(), null);
        double width = Drawable.WIDTH * t.getScaleX(); //TODO Not necessarily right
        double height = Drawable.HEIGHT * t.getScaleY(); //TODO Not necessarily right
        double dist2 = p.distanceSq(center);
        if (dist2 < closestDist2 && dist2 <= MeMath.sqr((width + height) / 2.0)) {
          closest = b;
          closestDist2 = dist2;
        }
      }
    }
    return closest;
  }

  protected Terminal pickTerminal(Point2D p) {
    double closestDist2 = Double.POSITIVE_INFINITY;
    Terminal closest = null;
    for (BlockWireform b : network.blocks) {
      for (Terminal t : b.getTerminals()) {
        if (t instanceof Drawable) { // Also seems like it ought to be given....
          Drawable d = (Drawable) t;
          //TODO Could optimize, and/or extract
          AffineTransform transform = d.getTransformChain().computeWorldTransform();
          Point2D center = transform.transform(d.getCenter(), null);
          double width = Drawable.WIDTH * transform.getScaleX(); //TODO Not necessarily right
          double height = Drawable.HEIGHT * transform.getScaleY(); //TODO Not necessarily right
          double dist2 = p.distanceSq(center);
          if (dist2 < closestDist2 && dist2 <= MeMath.sqr((width + height) / 2.0)) {
            closest = t;
            closestDist2 = dist2;
          }
        }
      }
    }
    return closest;
  }

  public void setNetwork(VJCSPNetwork network) {
    this.network = network;
    this.doRepaint();
  }

  public VJCSPNetwork getNetwork() {
    return network;
  }

  @Override
  protected void paintComponent(Graphics g0) {
    super.paintComponent(g0); //To change body of generated methods, choose Tools | Templates.
    Graphics2D g = (Graphics2D) g0;

    AffineTransform saveAT = g.getTransform();

    g.transform(at);
    //g.transform(t);
    g.setStroke(new BasicStroke(0));

    // Do stuff
    if (network != null) {
      network.draw(g, null);

      // Draw selected
      g.transform(network.getTransformChain().transform);
      AffineTransform prevTransform = g.getTransform();
      for (BlockWireform b : selectedBlocks) {
        if (b instanceof Drawable) {
          Drawable d = (Drawable) b;
          d.draw(g, COLOR_HIGHLIGHT);
          g.setTransform(prevTransform);
        }
      }
    }

    g.setTransform(saveAT);
  }

  private void doRepaint() {
    //TODO Check some flag or something?
    repaint();
  }

  protected boolean hasChanged() {
    return changed;
  }

  /**
   * This method is called from within the constructor to initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    addMouseWheelListener(new java.awt.event.MouseWheelListener() {
      public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
        formMouseWheelMoved(evt);
      }
    });

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 400, Short.MAX_VALUE)
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 300, Short.MAX_VALUE)
    );
  }// </editor-fold>//GEN-END:initComponents

  private void formMouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_formMouseWheelMoved
  }//GEN-LAST:event_formMouseWheelMoved


  // Variables declaration - do not modify//GEN-BEGIN:variables
  // End of variables declaration//GEN-END:variables
}
