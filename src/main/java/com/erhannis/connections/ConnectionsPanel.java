/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.connections;

import com.erhannis.connections.base.Block;
import com.erhannis.connections.vjcsp.VJCSPNetwork;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
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
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author erhannis
 */
public class ConnectionsPanel extends javax.swing.JPanel {
  protected static enum Mode {
    MOVE
  }

  private static final double SCALE_INCREMENT = 1.1;
  private static final double VIEW_PRESCALE = 1;

  private static final Color COLOR_BACKGROUND = Color.BLACK;
  private static final Color COLOR_NORMAL = Color.LIGHT_GRAY;
  private static final Color COLOR_HIGHLIGHT = Color.CYAN;
  private static final Color COLOR_HIGH = Color.GREEN;
  private static final Color COLOR_LOW = Color.BLACK;

  //TODO This doesn't seem very generic
  protected VJCSPNetwork network;
  public HashSet<Block> selectedBlocks = new HashSet<Block>();

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

    //TODO Move to MainFrame?
    ConnectionsPanel pd = this;
    pd.addMouseListener(new MouseListener() {
      @Override
      public void mouseClicked(MouseEvent e) {
        Point2D m = pd.ati.transform(new Point2D.Double(e.getX(), e.getY()), null);
        if (radioMove.isSelected()) {
        } else if (radioConnect.isSelected()) {
          outer:
          for (Unit u : pd.rootUnits) {
            for (Terminal t : u.getTerminals()) {
              double dist = m.distance(t.getViewX(), t.getViewY());
              if (dist < t.getViewSocketRadius()) {
                if (pd.selectedTerminals.isEmpty() || pd.selectedTerminals.size() > 1) {
                  pd.selectedTerminals.clear();
                  pd.selectedTerminals.add(t);
                } else {
                  Terminal st = pd.selectedTerminals.iterator().next();
                  if (st instanceof InputTerminal && t instanceof OutputTerminal) {
                    GDC.addConnection(((OutputTerminal) t), ((InputTerminal) st));
                  } else if (st instanceof OutputTerminal && t instanceof InputTerminal) {
                    GDC.addConnection(((OutputTerminal) st), ((InputTerminal) t));
                  } else {
                  }
                  if (!e.isShiftDown()) {
                    pd.selectedTerminals.clear();
                  }
                }
                break outer;
              }
            }
          }
          doRepaint();
        } else if (radioDisconnect.isSelected()) {
          outer:
          for (Unit u : pd.rootUnits) {
            for (Terminal t : u.getTerminals()) {
              double dist = m.distance(t.getViewX(), t.getViewY());
              if (dist < t.getViewSocketRadius()) {
                //TODO Yeah, the following is slightly cheating.
                if (t instanceof InputTerminal) {
                  if (((InputTerminal) t).getConnection() != null) {
                    ((InputTerminal) t).getConnection().removeOutput(((InputTerminal) t));
                  }
                } else if (t instanceof OutputTerminal) {
                  if (((OutputTerminal) t).getConnection() != null) {
                    ((OutputTerminal) t).getConnection().severConnection();
                  }
                } else {
                  //TODO Dunno.
                }
              }
            }
          }
          doRepaint();
        } else if (radioTransfer.isSelected()) {
          outer:
          for (Unit u : pd.rootUnits) {
            for (Terminal t : u.getTerminals()) {
              double dist = m.distance(t.getViewX(), t.getViewY());
              if (dist < t.getViewSocketRadius()) {
                if (pd.selectedTerminals.isEmpty() || pd.selectedTerminals.size() > 1) {
                  pd.selectedTerminals.clear();
                  pd.selectedTerminals.add(t);
                } else {
                  Terminal st = pd.selectedTerminals.iterator().next();
                  if (e.isShiftDown()) {
                    t.setName(st.getName());
                    st.setName("");
                  } else {
                    if (st instanceof InputTerminal && t instanceof InputTerminal) {
                      ((InputTerminal) st).getConnection().replaceOutput((InputTerminal) st, (InputTerminal) t);
                    } else if (st instanceof OutputTerminal && t instanceof OutputTerminal) {
                      ((OutputTerminal) st).getConnection().replaceInput((OutputTerminal) t);
                    } else {
                    }
                  }
                  pd.selectedTerminals.clear();
                }
                break outer;
              }
            }
          }
          doRepaint();
        } else if (radioLabelTerminal.isSelected()) {
          for (Unit u : unit.allUnits) {
            for (Terminal t : u.getTerminals()) {
              double dist = m.distance(t.getViewX(), t.getViewY());
              if (dist < t.getViewSocketRadius()) {
                t.setName(textTerminalLabel.getText());
                break;
              }
            }
          }
          doRepaint();
        } else if (radioRenameUnit.isSelected()) {
          double closestDist2 = Double.POSITIVE_INFINITY;
          Unit closest = null;
          for (Unit u : pd.rootUnits) {
            double dist2 = m.distanceSq(u.getViewLeft(), u.getViewTop());
            if (dist2 < closestDist2 && dist2 <= MeMath.sqr((u.getViewHeight() + u.getViewHeight()) / 2.0) && u != unit.internalMetaUnit) {
              // We want to be at least PRETTY close, and not delete the internalMetaUnit.
              closest = u;
              closestDist2 = dist2;
            }
          }
          if (closest != null) {
            closest.setName(textNewUnitName.getText());
          }
          doRepaint();
        } else if (radioRemove.isSelected()) {
          double closestDist2 = Double.POSITIVE_INFINITY;
          Unit closest = null;
          for (Unit u : pd.rootUnits) {
            double dist2 = m.distanceSq(u.getViewLeft(), u.getViewTop());
            if (dist2 < closestDist2 && dist2 <= MeMath.sqr((u.getViewHeight() + u.getViewHeight()) / 2.0) && u != unit.internalMetaUnit) {
              // We want to be at least PRETTY close, and not delete the internalMetaUnit.
              closest = u;
              closestDist2 = dist2;
            }
          }
          if (closest != null) {
            if (closest instanceof DirectedUnit) {
              //TODO Again, cheating
              unit.removeUnit((DirectedUnit) closest);
            }
          }
          pd.selectedTerminals.clear();
          pd.selectedUnits.clear();
          doRepaint();
        } else if (radioReplace.isSelected()) {
          double closestDist2 = Double.POSITIVE_INFINITY;
          Unit closest = null;
          for (Unit u : pd.rootUnits) {
            double dist2 = m.distanceSq(u.getViewLeft(), u.getViewTop());
            if (dist2 < closestDist2 && dist2 <= MeMath.sqr((u.getViewHeight() + u.getViewHeight()) / 2.0) && u != unit.internalMetaUnit) {
              // We want to be at least PRETTY close, and not replace the internalMetaUnit.
              closest = u;
              closestDist2 = dist2;
            }
          }
          if (closest != null) {
            if (closest instanceof DirectedUnit) {
              //TODO Again, cheating

              // Copied from radioPlace block
              Unit newUnitArchetype = (Unit) listUnitTypes.getSelectedValue();
              if (newUnitArchetype == null) {
                return;
              } else if (newUnitArchetype == unitToReplace) {
                JOptionPane.showMessageDialog(FrameEditDirectedCompositeUnit.this, "This is...a bad idea.  And not yet implemented.  I may do so later, if I'm feeling dangerous.");
                return;
              } else if (!(newUnitArchetype instanceof DirectedUnit)) {
                JOptionPane.showMessageDialog(FrameEditDirectedCompositeUnit.this, "Sorry, not a DirectedUnit.");
              }
              DirectedUnit newUnit = null;
              try {
                newUnit = (DirectedUnit) newUnitArchetype.copy();
              } catch (IOException ex) {
                Logger.getLogger(FrameEditDirectedCompositeUnit.class.getName()).log(Level.SEVERE, null, ex);
                return;
              } catch (ClassNotFoundException ex) {
                Logger.getLogger(FrameEditDirectedCompositeUnit.class.getName()).log(Level.SEVERE, null, ex);
                return;
              }
              unit.addUnit(newUnit);

              DirectedUnit duc = (DirectedUnit) closest;
              int ins = Math.min(duc.getInputs().size(), newUnit.getInputs().size());
              int outs = Math.min(duc.getOutputs().size(), newUnit.getOutputs().size());
              for (int i = 0; i < ins; i++) {
                if (duc.in(i).getConnection() != null) {
                  GDC.addConnection(duc.in(i).getConnection().getInput(), newUnit.in(i));
                }
              }
              for (int i = 0; i < outs; i++) {
                if (duc.out(i).getConnection() != null) {
                  GDC.addConnection(newUnit.out(i), duc.out(i).getConnection().getOutputs().toArray(new InputTerminal[]{}));
                }
              }
              newUnit.setViewDims(closest.getViewWidth(), closest.getViewHeight());
              newUnit.setViewTopLeft(closest.getViewTop(), closest.getViewLeft());
              newUnit.setViewFontSize(closest.getViewFontSize());
              newUnit.recalcView();
              unit.removeUnit((DirectedUnit) closest);
              pd.selectedTerminals.clear();
              pd.selectedUnits.clear();
              doRepaint();
            }
          }
          pd.selectedTerminals.clear();
          pd.selectedUnits.clear();
          doRepaint();
        } else if (radioPlace.isSelected()) {
          Unit newUnitArchetype = (Unit) listUnitTypes.getSelectedValue();
          if (newUnitArchetype == null) {
            return;
          } else if (newUnitArchetype == unitToReplace) {
            JOptionPane.showMessageDialog(FrameEditDirectedCompositeUnit.this, "This is...a bad idea.  And not yet implemented.  I may do so later, if I'm feeling dangerous.");
            return;
          } else if (!(newUnitArchetype instanceof DirectedUnit)) {
            JOptionPane.showMessageDialog(FrameEditDirectedCompositeUnit.this, "Sorry, not a DirectedUnit.");
          }
          DirectedUnit newUnit = null;
          try {
            newUnit = (DirectedUnit) newUnitArchetype.copy();
          } catch (IOException ex) {
            Logger.getLogger(FrameEditDirectedCompositeUnit.class.getName()).log(Level.SEVERE, null, ex);
            return;
          } catch (ClassNotFoundException ex) {
            Logger.getLogger(FrameEditDirectedCompositeUnit.class.getName()).log(Level.SEVERE, null, ex);
            return;
          }
          unit.addUnit(newUnit);
          if (cbAutosource.isSelected() && newUnit.getInputs().size() >= 2) {
            if (unit.internalMetaUnit.getOutputs().size() >= 2) {
              GDC.addConnection(unit.internalMetaUnit.out(0), newUnit.in(0));
              GDC.addConnection(unit.internalMetaUnit.out(1), newUnit.in(1));
            } else {
              boolean foundHigh = false;
              boolean foundLow = false;
              for (DirectedUnit du : unit.allUnits) {
                if (du instanceof SourceHigh) {
                  foundHigh = true;
                  GDC.addConnection(du.out(0), newUnit.in(0));
                } else if (du instanceof SourceLow) {
                  foundLow = true;
                  GDC.addConnection(du.out(0), newUnit.in(1));
                }
                if (foundLow && foundHigh) {
                  break;
                }
              }
            }
          }
          newUnit.setViewLeft(m.getX());
          newUnit.setViewTop(m.getY());
          double scale = (double) spinInitialScale.getValue();
          newUnit.setViewWidth(newUnit.getViewWidth() * scale);
          newUnit.setViewHeight(newUnit.getViewHeight() * scale);
          newUnit.setViewFontSize((float) (newUnit.getViewFontSize() * scale));
          newUnit.recalcView();
          doRepaint();
        }
      }

      public boolean hadFocus = false;
      private Point2D startPoint = null;

      @Override
      public void mousePressed(MouseEvent e) {
        changed = true;
        Point2D m = pd.ati.transform(new Point2D.Double(e.getX(), e.getY()), null);
        startPoint = m;
        if (radioMove.isSelected()) {
          double closestDist2 = Double.POSITIVE_INFINITY;
          Unit closest = null;
          for (Unit u : pd.rootUnits) {
            double dist2 = m.distanceSq(u.getViewLeft(), u.getViewTop());
            if (dist2 < closestDist2) {
              closest = u;
              closestDist2 = dist2;
            }
          }
          pd.selectedUnits.clear();
          pd.selectedUnits.add(closest);
          doRepaint();
        } else if (radioConnect.isSelected()) {
        } else if (radioDisconnect.isSelected()) {
        } else if (radioLabelTerminal.isSelected()) {
        } else if (radioRemove.isSelected()) {
        } else if (radioReplace.isSelected()) {
        } else if (radioPlace.isSelected()) {
        }
      }

      @Override
      public void mouseReleased(MouseEvent e) {
        Point2D m = pd.ati.transform(new Point2D.Double(e.getX(), e.getY()), null);
        pd.selectedUnits.clear();
        IfChain:
        if (radioMove.isSelected()) {
        } else if (radioConnect.isSelected()) {
          if (m.equals(startPoint)) {
            break IfChain;
          }
          Rectangle2D r = new Rectangle2D.Double(startPoint.getX(), startPoint.getY(), m.getX() - startPoint.getX(), m.getY() - startPoint.getY());
          MeUtils.fixRect2DIP(r);
          if (pd.selectedTerminals.isEmpty()) {
            // Whoaaaa, what is this crazy exoticism
            unit.allUnits.stream().forEach((du) -> {
              du.getTerminals().stream().filter((t) -> (r.contains(new Point2D.Double(t.getViewX(), t.getViewY())))).forEach((t) -> {
                pd.selectedTerminals.add(t);
              });
            });
          } else {
            // It's weeeiiiirrrd
            HashSet<Terminal> terms = new HashSet<Terminal>();
            unit.allUnits.stream().forEach((du) -> {
              du.getTerminals().stream().filter((t) -> (r.contains(new Point2D.Double(t.getViewX(), t.getViewY())))).forEach((t) -> {
                terms.add(t);
              });
            });
            ArrayList<Terminal> a = new ArrayList<Terminal>(pd.selectedTerminals);
            ArrayList<Terminal> b = new ArrayList<Terminal>(terms);
            a.sort(new Comparator<Terminal>() {
              @Override
              public int compare(Terminal o1, Terminal o2) {
                return Double.compare(o1.getViewY(), o2.getViewY());
              }
            });
            b.sort(new Comparator<Terminal>() {
              @Override
              public int compare(Terminal o1, Terminal o2) {
                return Double.compare(o1.getViewY(), o2.getViewY());
              }
            });
            if (b.size() == 1 && a.size() > 1) {
              ArrayList<Terminal> bucket = a;
              a = b;
              b = bucket;
            }
            if (a.size() == 1 && b.size() > 1 && a.get(0) instanceof OutputTerminal) {
              for (int i = 0; i < b.size(); i++) {
                if (b.get(i) instanceof InputTerminal) {
                  GDC.addConnection(((OutputTerminal) a.get(0)), ((InputTerminal) b.get(i)));
                }
              }
            } else {
              for (int i = 0; i < a.size() && i < b.size(); i++) {
                if (a.get(i) instanceof OutputTerminal) {
                  if (b.get(i) instanceof InputTerminal) {
                    GDC.addConnection(((OutputTerminal) a.get(i)), ((InputTerminal) b.get(i)));
                  } else {
                    // Dunno
                  }
                } else if (a.get(i) instanceof InputTerminal) {
                  if (b.get(i) instanceof OutputTerminal) {
                    GDC.addConnection(((OutputTerminal) b.get(i)), ((InputTerminal) a.get(i)));
                  } else {
                    // Dunno
                  }
                } else {
                  //TODO Dunno; could support plain terminals, eventually
                }
              }
            }
            pd.selectedTerminals.clear();
          }
        } else if (radioDisconnect.isSelected()) {
          if (m.equals(startPoint)) {
            break IfChain;
          }
          Rectangle2D r = new Rectangle2D.Double(startPoint.getX(), startPoint.getY(), m.getX() - startPoint.getX(), m.getY() - startPoint.getY());
          MeUtils.fixRect2DIP(r);
          // Whoaaaa, what is this crazy exoticism
          unit.allUnits.stream().forEach((du) -> {
            du.getTerminals().stream().filter((t) -> (r.contains(new Point2D.Double(t.getViewX(), t.getViewY())))).forEach((t) -> {
              if (t instanceof InputTerminal) {
                ((InputTerminal) t).breakConnection();
              } else if (t instanceof OutputTerminal) {
                ((OutputTerminal) t).breakConnection();
              }
            });
          });
        } else if (radioTransfer.isSelected()) {
          if (m.equals(startPoint)) {
            break IfChain;
          }
          Rectangle2D r = new Rectangle2D.Double(startPoint.getX(), startPoint.getY(), m.getX() - startPoint.getX(), m.getY() - startPoint.getY());
          MeUtils.fixRect2DIP(r);
          if (pd.selectedTerminals.isEmpty()) {
            // Whoaaaa, what is this crazy exoticism
            unit.allUnits.stream().forEach((du) -> {
              du.getTerminals().stream().filter((t) -> (r.contains(new Point2D.Double(t.getViewX(), t.getViewY())))).forEach((t) -> {
                pd.selectedTerminals.add(t);
              });
            });
          } else {
            // It's weeeiiiirrrd
            HashSet<Terminal> terms = new HashSet<Terminal>();
            unit.allUnits.stream().forEach((du) -> {
              du.getTerminals().stream().filter((t) -> (r.contains(new Point2D.Double(t.getViewX(), t.getViewY())))).forEach((t) -> {
                terms.add(t);
              });
            });
            ArrayList<Terminal> a = new ArrayList<Terminal>(pd.selectedTerminals);
            ArrayList<Terminal> b = new ArrayList<Terminal>(terms);
            a.sort(new Comparator<Terminal>() {
              @Override
              public int compare(Terminal o1, Terminal o2) {
                return Double.compare(o1.getViewY(), o2.getViewY());
              }
            });
            b.sort(new Comparator<Terminal>() {
              @Override
              public int compare(Terminal o1, Terminal o2) {
                return Double.compare(o1.getViewY(), o2.getViewY());
              }
            });
            if (b.size() == 1 && a.size() > 1) {
              ArrayList<Terminal> bucket = a;
              a = b;
              b = bucket;
            }
            if (a.size() == 1 && b.size() > 1 && a.get(0) instanceof OutputTerminal) {
              for (int i = 0; i < b.size(); i++) {
                if (e.isShiftDown()) {
                  b.get(i).setName(a.get(i).getName());
                  a.get(i).setName("");
                } else {
                  if (b.get(i) instanceof OutputTerminal && ((OutputTerminal) b.get(i)).getConnection() != null) {
                    GDC.addConnection(((OutputTerminal) a.get(0)), new ArrayList<InputTerminal>(((OutputTerminal) b.get(i)).getConnection().getOutputs()).toArray(new InputTerminal[]{}));
                  }
                }
              }
            } else {
              if (a.size() > 0 && b.size() > 0) {
                if (a.get(0).getViewY() < b.get(0).getViewY()) {
                  // This should make shift-down transfers work, but might make inconsistent the outcome of selecting more terminals on one side than another
                  Collections.reverse(a);
                  Collections.reverse(b);
                }
              }
              for (int i = 0; i < a.size() && i < b.size(); i++) {
                if (e.isShiftDown()) {
                  b.get(i).setName(a.get(i).getName());
                  a.get(i).setName("");
                } else {
                  if (a.get(i) instanceof OutputTerminal) {
                    if (b.get(i) instanceof OutputTerminal && a.get(i).getConnection() != null) {
                      ((OutputTerminal) a.get(i)).getConnection().replaceInput(((OutputTerminal) b.get(i)));
                    } else {
                      // Dunno
                    }
                  } else if (a.get(i) instanceof InputTerminal) {
                    if (b.get(i) instanceof InputTerminal && a.get(i).getConnection() != null) {
                      ((InputTerminal) a.get(i)).getConnection().replaceOutput(((InputTerminal) a.get(i)), ((InputTerminal) b.get(i)));
                    } else {
                      // Dunno
                    }
                  } else {
                    //TODO Dunno; could support plain terminals, eventually
                  }
                }
              }
            }
            pd.selectedTerminals.clear();
          }
        } else if (radioLabelTerminal.isSelected()) {
          if (m.equals(startPoint)) {
            break IfChain;
          }
          String baseLabel = textTerminalLabel.getText();
          Rectangle2D r = new Rectangle2D.Double(startPoint.getX(), startPoint.getY(), m.getX() - startPoint.getX(), m.getY() - startPoint.getY());
          MeUtils.fixRect2DIP(r);
          HashSet<Terminal> terms = new HashSet<Terminal>();
          unit.allUnits.stream().forEach((du) -> {
            du.getTerminals().stream().filter((t) -> (r.contains(new Point2D.Double(t.getViewX(), t.getViewY())))).forEach((t) -> {
              terms.add(t);
            });
          });
          ArrayList<Terminal> a = new ArrayList<Terminal>(terms);
          a.sort(new Comparator<Terminal>() {
            @Override
            public int compare(Terminal o1, Terminal o2) {
              return -Double.compare(o1.getViewY(), o2.getViewY());
            }
          });
          if (e.isShiftDown()) {
            for (int i = 0; i < a.size(); i++) {
              a.get(i).setName(baseLabel + i);
            }
          } else {
            for (Terminal t : a) {
              t.setName(baseLabel);
            }
          }
        } else if (radioRemove.isSelected()) {
        } else if (radioReplace.isSelected()) {
        } else if (radioPlace.isSelected()) {
        }
        startPoint = null;
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
        if (radioMove.isSelected()) {
          if (!pd.selectedUnits.isEmpty()) {
            Point2D m = pd.ati.transform(new Point2D.Double(e.getX(), e.getY()), null);
            for (Unit u : pd.selectedUnits) {
              u.setViewLeft(m.getX());
              u.setViewTop(m.getY());
              u.recalcView();
            }
            changed = true;
            doRepaint();
          }
        } else if (radioConnect.isSelected()) {
        } else if (radioDisconnect.isSelected()) {
        } else if (radioRemove.isSelected()) {
        } else if (radioReplace.isSelected()) {
        } else if (radioPlace.isSelected()) {
          //TODO Resize
        }
      }

      @Override
      public void mouseMoved(MouseEvent e) {
      }
    });
    pd.addMouseWheelListener(new MouseWheelListener() {
      @Override
      public void mouseWheelMoved(MouseWheelEvent e) {
        if (radioMove.isSelected()) {
          if (!pd.selectedUnits.isEmpty()) {
            double scale = Math.pow(1.1, e.getPreciseWheelRotation());
            for (Unit u : pd.selectedUnits) {
              u.setViewWidth(u.getViewWidth() * scale);
              u.setViewHeight(u.getViewHeight() * scale);
              u.setViewFontSize((float) (u.getViewFontSize() * scale));
              u.recalcView();
            }
            changed = true;
            doRepaint();
          }
        } else if (radioConnect.isSelected()) {
        } else if (radioDisconnect.isSelected()) {
        } else if (radioRemove.isSelected()) {
        } else if (radioReplace.isSelected()) {
        } else if (radioPlace.isSelected()) {
        }
      }
    });

  }

  protected static Mode getMode(InputEvent e) {
    //TODO Do
    System.err.println("Implement getMode");
    return Mode.MOVE;
  }
  
  public void setNetwork(VJCSPNetwork network) {
    this.network = network;
    this.repaint();
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
      network.draw(g);
    }

    g.setTransform(saveAT);
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
    if (selectedBlocks.isEmpty()) { // Otherwise we're resizing an element
      double scale = Math.pow(SCALE_INCREMENT, -evt.getPreciseWheelRotation());
      at.preConcatenate(AffineTransform.getTranslateInstance(-evt.getX(), -evt.getY()));
      at.preConcatenate(AffineTransform.getScaleInstance(scale, scale));
      at.preConcatenate(AffineTransform.getTranslateInstance(evt.getX(), evt.getY()));
      try {
        ati = at.createInverse();
      } catch (NoninvertibleTransformException ex) {
        Logger.getLogger(ConnectionsPanel.class.getName()).log(Level.SEVERE, null, ex);
      }
      repaint();
    }
  }//GEN-LAST:event_formMouseWheelMoved


  // Variables declaration - do not modify//GEN-BEGIN:variables
  // End of variables declaration//GEN-END:variables
}
