/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.connections;

import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import jcsp.lang.Parallel;
import jcsp.lang.Sequence;

/**
 *
 * @author erhannis
 */
public class WireformParamsDialog extends javax.swing.JDialog {

  private Sequence dataEntries = new Sequence();

  /**
   * Creates new form WireformParamsDialog
   */
  public WireformParamsDialog(Window window, String title, ModalityType modality, Map<String, Class> possibleParameters, Map<String, Object> paramsMutable) {
    super(window, title, modality);
    initComponents();

    panelParams.setLayout(new GridLayout(possibleParameters.size(), 2, 10, 5));

    for (Map.Entry<String, Class> entry : possibleParameters.entrySet()) {
      JCheckBox cbUseParam = new JCheckBox(entry.getKey());
      ChangeListener onChange = new ChangeListener() {
        @Override
        public void stateChanged(ChangeEvent e) {
          cbUseParam.setSelected(true);
        }
      };
      panelParams.add(cbUseParam);
      if (Integer.class.equals(entry.getValue())) {
        SpinnerNumberModel model = new SpinnerNumberModel(0, Integer.MIN_VALUE, Integer.MAX_VALUE, 1);
        model.addChangeListener(onChange);
        if (paramsMutable.containsKey(entry.getKey())) {
          model.setValue(paramsMutable.get(entry.getKey()));
          onChange.stateChanged(null);
        }
        panelParams.add(new JSpinner(model));
        dataEntries.addProcess(() -> {
          if (cbUseParam.isSelected()) {
            paramsMutable.put(entry.getKey(), model.getNumber().intValue());
          } else {
            paramsMutable.remove(entry.getKey());
          }
        });
      } else if (Long.class.equals(entry.getValue())) {
        SpinnerNumberModel model = new SpinnerNumberModel(0L, Long.MIN_VALUE, Long.MAX_VALUE, 1L);
        model.addChangeListener(onChange);
        if (paramsMutable.containsKey(entry.getKey())) {
          model.setValue(paramsMutable.get(entry.getKey()));
          onChange.stateChanged(null);
        }
        panelParams.add(new JSpinner(model));
        dataEntries.addProcess(() -> {
          if (cbUseParam.isSelected()) {
            paramsMutable.put(entry.getKey(), model.getNumber().longValue());
          } else {
            paramsMutable.remove(entry.getKey());
          }
        });
      } else if (Double.class.equals(entry.getValue())) {
        SpinnerNumberModel model = new SpinnerNumberModel(0.0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 1.0);
        model.addChangeListener(onChange);
        if (paramsMutable.containsKey(entry.getKey())) {
          model.setValue(paramsMutable.get(entry.getKey()));
          onChange.stateChanged(null);
        }
        panelParams.add(new JSpinner(model));
        dataEntries.addProcess(() -> {
          if (cbUseParam.isSelected()) {
            paramsMutable.put(entry.getKey(), model.getNumber().doubleValue());
          } else {
            paramsMutable.remove(entry.getKey());
          }
        });
      } else if (String.class.equals(entry.getValue())) {
        JTextField tfParam = new JTextField();
        tfParam.getDocument().addDocumentListener(new DocumentListener() {
          public void changedUpdate(DocumentEvent e) {
            onChange.stateChanged(null);
          }

          public void removeUpdate(DocumentEvent e) {
            onChange.stateChanged(null);
          }

          public void insertUpdate(DocumentEvent e) {
            onChange.stateChanged(null);
          }
        });
        if (paramsMutable.containsKey(entry.getKey())) {
          tfParam.setText(Objects.toString(paramsMutable.get(entry.getKey()), ""));
          onChange.stateChanged(null);
        }
        panelParams.add(tfParam);
        dataEntries.addProcess(() -> {
          if (cbUseParam.isSelected()) {
            paramsMutable.put(entry.getKey(), tfParam.getText());
          } else {
            paramsMutable.remove(entry.getKey());
          }
        });
      } else {
        JLabel lFailure = new JLabel("No handler for class: " + entry.getValue());
        lFailure.setEnabled(false);
        panelParams.add(lFailure);
      }
    }

    //TODO "Cancel" btn?
    this.pack();
  }

  /**
   * This method is called from within the constructor to initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    btnDone = new javax.swing.JButton();
    jScrollPane1 = new javax.swing.JScrollPane();
    panelParams = new javax.swing.JPanel();

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

    btnDone.setText("Done");
    btnDone.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnDoneActionPerformed(evt);
      }
    });

    javax.swing.GroupLayout panelParamsLayout = new javax.swing.GroupLayout(panelParams);
    panelParams.setLayout(panelParamsLayout);
    panelParamsLayout.setHorizontalGroup(
      panelParamsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 398, Short.MAX_VALUE)
    );
    panelParamsLayout.setVerticalGroup(
      panelParamsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 249, Short.MAX_VALUE)
    );

    jScrollPane1.setViewportView(panelParams);

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addComponent(btnDone)
        .addContainerGap())
      .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 251, Short.MAX_VALUE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
        .addComponent(btnDone)
        .addContainerGap())
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents

  private void btnDoneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDoneActionPerformed
    dataEntries.run();
    dispose();
  }//GEN-LAST:event_btnDoneActionPerformed

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton btnDone;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JPanel panelParams;
  // End of variables declaration//GEN-END:variables
}
