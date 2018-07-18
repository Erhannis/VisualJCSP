/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.visualjcsp;

import com.erhannis.connections.ConnectionsPanel;
import com.erhannis.connections.base.BlockWireform;
import com.erhannis.connections.base.BlockArchetype;
import com.erhannis.connections.base.Compilable;
import com.erhannis.connections.base.Connection;
import com.erhannis.connections.base.Project;
import com.erhannis.connections.base.Terminal;
import com.erhannis.connections.base.TransformChain;
import com.erhannis.connections.vjcsp.IntOrEventualClass;
import com.erhannis.connections.vjcsp.PlainInputTerminal;
import com.erhannis.connections.vjcsp.PlainOutputTerminal;
import com.erhannis.connections.vjcsp.ProcessBlock;
import com.erhannis.connections.vjcsp.VJCSPNetwork;
import com.erhannis.connections.vjcsp.blocks.SplitterBlock;
import com.erhannis.connections.vjcsp.blocks.UDPReceiverBlock;
import com.erhannis.connections.vjcsp.blocks.UDPTransmitterBlock;
import com.erhannis.mathnstuff.FactoryHashMap;
import com.erhannis.mathnstuff.Pair;
import com.erhannis.vjcsp.core.Generate;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javassist.CannotCompileException;
import javassist.NotFoundException;
import javax.lang.model.element.Modifier;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import jcsp.lang.AltingChannelInput;
import jcsp.lang.Any2AnyChannel;
import jcsp.lang.Any2OneChannel;
import jcsp.lang.CSProcess;
import jcsp.lang.CSTimer;
import jcsp.lang.Channel;
import jcsp.lang.ChannelOutput;
import jcsp.lang.One2AnyChannel;
import jcsp.lang.One2OneChannel;
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

  protected File mProjectFile;
  protected Project mProject;

  protected JFileChooser mChooser = new JFileChooser();

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
        return new ArchetypeTransferable((BlockArchetype) listBlocks.getSelectedValue());//new DataHandler(listBlocks.getSelectedValue(), "application/x-java-object");
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
    keyPressedChannelOut.write(new KeyEvent(this, 0, 0, 0, KeyEvent.VK_ESCAPE, (char) KeyEvent.VK_ESCAPE));

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
              save();
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

    if (mChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
      mProjectFile = mChooser.getSelectedFile();
      //TODO Load/create new
      if (mProjectFile.exists()) {
        // Load
        mProject = new Project();
      } else {
        // Create
        mProject = new Project();
      }
    } else {
      //TODO This isn't the best.
      JOptionPane.showMessageDialog(this, "Project must have a project file, to know where to put its data.  Application will now quit.");
      System.exit(0);
    }

    {
      // Example

      VJCSPNetwork network = new VJCSPNetwork();

      mProject.networks.add(network);
      mProject.mainNetwork = network;

//      double scale = 20;
//
//      FileProcessBlock generate1 = new FileProcessBlock("Generate 1", new TransformChain(AffineTransform.getTranslateInstance(4 * scale, 4 * scale), network.getTransformChain()));
//      PlainOutputTerminal g1o = generate1.addPlainOutputTerminal("StrOut", new IntOrEventualClass(String.class));
//
//      FileProcessBlock generate2 = new FileProcessBlock("Generate 2", new TransformChain(AffineTransform.getTranslateInstance(12 * scale, 4 * scale), network.getTransformChain()));
//      PlainOutputTerminal g2o = generate2.addPlainOutputTerminal("StrOut", new IntOrEventualClass(String.class));
//
//      FileProcessBlock concat = new FileProcessBlock("Concat", new TransformChain(AffineTransform.getTranslateInstance(8 * scale, 8 * scale), network.getTransformChain()));
//      PlainInputTerminal ci1 = concat.addPlainInputTerminal("StrIn 1", new IntOrEventualClass(String.class));
//      PlainInputTerminal ci2 = concat.addPlainInputTerminal("StrIn 2", new IntOrEventualClass(String.class));
//      PlainOutputTerminal co = concat.addPlainOutputTerminal("StrOut", new IntOrEventualClass(String.class));
//
//      Random r = new Random();
//      for (int i = 0; i < 20; i++) {
//        FileProcessBlock fpb = new FileProcessBlock("test " + i, new TransformChain(AffineTransform.getTranslateInstance(r.nextDouble() * 20 * scale, r.nextDouble() * 20 * scale), network.getTransformChain()));
//        int top = r.nextInt(4);
//        for (int j = 0; j < top; j++) {
//          fpb.addPlainInputTerminal(i + "i" + j, new IntOrEventualClass(String.class));
//        }
//        top = r.nextInt(4);
//        for (int j = 0; j < top; j++) {
//          fpb.addPlainOutputTerminal(i + "o" + j, new IntOrEventualClass(String.class));
//        }
//        //network.blocks.add(fpb);
//      }
//
//      FileProcessBlock sysout = new FileProcessBlock("sysout", new TransformChain(AffineTransform.getTranslateInstance(8 * scale, 12 * scale), network.getTransformChain()));
//      PlainInputTerminal si = sysout.addPlainInputTerminal("StrIn", new IntOrEventualClass(String.class));
//
//      network.blocks.add(generate1);
//      network.blocks.add(generate2);
//      network.blocks.add(concat);
//      network.blocks.add(sysout);
//      network.connect(g1o, ci1);
//      network.connect(g2o, ci2);
//      network.connect(co, si);
      panel.setNetwork(network);
      if (1 == 1) {
        return;
      }
    }
  }

  private boolean hasChanged() {
    //TODO Do
    System.err.println("Implement hasChanged");
    return true;
  }

  private List<BlockArchetype> getArchetypes() {
    //TODO Make dynamic
    //TODO Add refresh button
    //TODO FileProcessBlock
    return Arrays.asList(new SplitterBlock.Wireform.Archetype(), new UDPReceiverBlock.Wireform.Archetype(), new UDPTransmitterBlock.Wireform.Archetype());
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
    btnCompile = new javax.swing.JButton();
    jPanel2 = new javax.swing.JPanel();
    jMenuBar1 = new javax.swing.JMenuBar();
    jMenu1 = new javax.swing.JMenu();
    jMenuItem2 = new javax.swing.JMenuItem();
    jMenuItem3 = new javax.swing.JMenuItem();
    jMenu2 = new javax.swing.JMenu();
    jMenu3 = new javax.swing.JMenu();
    jMenuItem1 = new javax.swing.JMenuItem();

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

    btnCompile.setText("Compile");
    btnCompile.setFocusable(false);
    btnCompile.setRequestFocusEnabled(false);
    btnCompile.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnCompileActionPerformed(evt);
      }
    });

    javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
    jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(lFilter)
        .addContainerGap(443, Short.MAX_VALUE))
      .addComponent(jScrollPane1)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addComponent(btnCompile)
        .addContainerGap())
    );
    jPanel1Layout.setVerticalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(lFilter)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 570, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
        .addComponent(btnCompile)
        .addContainerGap())
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

    jMenu1.setText("File");

    jMenuItem2.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
    jMenuItem2.setText("Save");
    jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItem2ActionPerformed(evt);
      }
    });
    jMenu1.add(jMenuItem2);

    jMenuItem3.setText("Exit");
    jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItem3ActionPerformed(evt);
      }
    });
    jMenu1.add(jMenuItem3);

    jMenuBar1.add(jMenu1);

    jMenu2.setText("Edit");
    jMenuBar1.add(jMenu2);

    jMenu3.setText("Help");

    jMenuItem1.setText("About...");
    jMenu3.add(jMenuItem1);

    jMenuBar1.add(jMenu3);

    setJMenuBar(jMenuBar1);

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

  private void btnCompileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCompileActionPerformed
    try {
      compile();
    } catch (IOException ex) { //TODO Show dialog?
      Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
    } catch (NotFoundException ex) {
      Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
    } catch (CannotCompileException ex) {
      Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
    } catch (Compilable.CompilationException ex) {
      Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
    }
  }//GEN-LAST:event_btnCompileActionPerformed

  private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
    save();
  }//GEN-LAST:event_jMenuItem2ActionPerformed

  private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
    exit();
  }//GEN-LAST:event_jMenuItem3ActionPerformed

  private void compile() throws IOException, NotFoundException, CannotCompileException, Compilable.CompilationException {
    //TODO Strongly recommend the user put java processes in a package path including "generated"
    File root = mProjectFile.getParentFile();
    root.mkdirs();
    File libs = new File(root, Settings.LIBS_FOLDER);
    libs.mkdirs();
    File classFolder = new File(root, Settings.CLASSES_TARGET_FOLDER);
    classFolder.mkdirs();

    //TODO Clean old classes?  Label things "generated", etc.?
    //TODO Check for code changes
    //mProject.compile(root);
    mockCompilation(root);

    // https://stackoverflow.com/a/1281295/513038
    Manifest manifest = new Manifest();
    manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
    JarOutputStream target = new JarOutputStream(new FileOutputStream(new File(libs, Settings.GENERATED_JAR_FILENAME)), manifest);
    addDirContentsToJar(classFolder, target);
    target.close();
    System.err.println("Don't forget to add jar to pom.xml");
    //TODO Add jar to pom.xml - or actually, figure out how to include them in the FINAL build, for dependency transitivity
  }

  //TODO Move somewhere else?
  // Modified from https://stackoverflow.com/a/1281295/513038
  private static void addDirContentsToJar(File dir, JarOutputStream target) throws IOException {
    if (dir.isDirectory()) {
      for (File nestedFile : dir.listFiles()) {
        addToJar("", nestedFile, target);
      }
    } else {
      throw new IOException("Expected dir; was not: " + dir);
    }
  }

  // Modified from https://stackoverflow.com/a/1281295/513038
  private static void addToJar(String parents, File source, JarOutputStream target) throws IOException {
    BufferedInputStream in = null;
    try {
      if (source.isDirectory()) {
        String name = (parents + source.getName()).replace("\\", "/");
        if (!name.isEmpty()) {
          if (!name.endsWith("/")) {
            name += "/";
          }
          JarEntry entry = new JarEntry(name);
          entry.setTime(source.lastModified());
          target.putNextEntry(entry);
          target.closeEntry();
        }
        for (File nestedFile : source.listFiles()) {
          addToJar(name, nestedFile, target);
        }
        return;
      }

      JarEntry entry = new JarEntry((parents + source.getName()).replace("\\", "/"));
      entry.setTime(source.lastModified());
      target.putNextEntry(entry);
      in = new BufferedInputStream(new FileInputStream(source));

      byte[] buffer = new byte[1024];
      while (true) {
        int count = in.read(buffer);
        if (count == -1) {
          break;
        }
        target.write(buffer, 0, count);
      }
      target.closeEntry();
    } finally {
      if (in != null) {
        in.close();
      }
    }
  }

  private void mockCompilation(File root) throws Compilable.CompilationException {
    // First, setting up the mock network.  It won't be directly used, yet, but it's important to know what we're dealing with.
    double scale = 20;

    Project project = new Project();
    VJCSPNetwork network = new VJCSPNetwork();
    project.networks.add(network);
    project.mainNetwork = network;

    HashMap<String, Object> params;

    params = new HashMap<String, Object>();
    params.put("port", 1234);
    //TODO This cast is dumb, and possibly dangerous
    ProcessBlock receiver = (ProcessBlock) new UDPReceiverBlock.Wireform.Archetype().createWireform(params, "UDPReceiverBlock", new TransformChain(AffineTransform.getTranslateInstance(4 * scale, 4 * scale), network.getTransformChain()));
    PlainOutputTerminal r1o = (PlainOutputTerminal) receiver.getTerminals().stream().filter(t -> t instanceof PlainOutputTerminal).findFirst().get();

    params = new HashMap<String, Object>();
    params.put("type", new IntOrEventualClass(String.class));
    params.put("count", 2);
    //TODO This cast is dumb, and possibly dangerous
    ProcessBlock splitter = (ProcessBlock) new SplitterBlock.Wireform.Archetype().createWireform(params, "SplitterBlock", new TransformChain(AffineTransform.getTranslateInstance(4 * scale, 4 * scale), network.getTransformChain()));
    PlainInputTerminal s1i = (PlainInputTerminal) splitter.getTerminals().stream().filter(t -> t instanceof PlainInputTerminal).findFirst().get();
    Object[] s1os = splitter.getTerminals().stream().filter(t -> t instanceof PlainOutputTerminal).toArray();
    PlainOutputTerminal s1o1 = (PlainOutputTerminal) s1os[0];
    PlainOutputTerminal s1o2 = (PlainOutputTerminal) s1os[1];
    params.remove("type"); //TODO A hack
    
    params = new HashMap<String, Object>();
    params.put("port", 1235);
    params.put("hostname", "localhost");
    //TODO This cast is dumb, and possibly dangerous
    ProcessBlock transmitter1 = (ProcessBlock) new UDPTransmitterBlock.Wireform.Archetype().createWireform(params, "UDPTransmitterBlock", new TransformChain(AffineTransform.getTranslateInstance(4 * scale, 0 * scale), network.getTransformChain()));
    PlainInputTerminal t1i = (PlainInputTerminal) transmitter1.getTerminals().iterator().next();

    params = new HashMap<String, Object>();
    params.put("port", 1236);
    params.put("hostname", "localhost");
    //TODO This cast is dumb, and possibly dangerous
    ProcessBlock transmitter2 = (ProcessBlock) new UDPTransmitterBlock.Wireform.Archetype().createWireform(params, "UDPTransmitterBlock", new TransformChain(AffineTransform.getTranslateInstance(6 * scale, 0 * scale), network.getTransformChain()));
    PlainInputTerminal t2i = (PlainInputTerminal) transmitter2.getTerminals().iterator().next();
    
    network.blocks.add(receiver);
    //network.blocks.add(splitter);
    network.blocks.add(transmitter1);
    network.blocks.add(transmitter2);
    //network.connect(r1o, s1i);
    //network.connect(s1o1, t1i);
    //network.connect(s1o2, t2i);
    network.connect(r1o, t1i);
    network.connect(r1o, t2i);
    
    project = this.mProject;
    network = (VJCSPNetwork)this.mProject.mainNetwork;

    // Next, performing all the steps compilation would need to take.
    // First, copying over existing classes.
    for (BlockArchetype arch : network.getArchetypes()) {
      arch.compile(root);
    }

    File srcDir = new File(root, "src/main/java");
    srcDir.mkdirs();

    //TODO Add JCSP and any core runtime VJSCP library
    // Next, create tie-together code
    // Have to start from the details out, I think maybe
    // Network init class
    ClassName networkClass; //TODO Gotta deal with unique names
    {
      Function<String, String> uniqueName = new Function<String, String>() {
        private HashSet<String> names = new HashSet<>();

        @Override
        public String apply(String base) {
          String result = base;
          int i = 2;
          while (names.contains(result)) {
            result = base + "_" + i;
            i++;
          }
          names.add(result);
          return result;
        }
      };
            
      //HashMap<BlockWireform, HashMap<String, String>> paramToChannelname = new HashMap<>();
      FactoryHashMap<BlockWireform, HashMap<String, Pair<String, String>>> wireformToParamToChannelnames = new FactoryHashMap<BlockWireform, HashMap<String, Pair<String, String>>>((block) -> new HashMap<String, Pair<String, String>>());
      HashMap<Terminal, String> terminalToChannelname = new HashMap<>();
      
      boolean addedLines;

      HashSet<Terminal> terminalsAccountedFor = new HashSet<Terminal>();
      
      // Connections
      // Things we need out of this: unique chan = Channel.one2one();, channelIn name, channelOut name.
      CodeBlock.Builder connectionsCode = CodeBlock.builder();
      connectionsCode.add("// Connections code\n");
      addedLines = false;
      for (Connection con : network.connections) {
        int froms = con.getFromTerminals().size();
        int tos = con.getToTerminals().size();
        if (froms == 0 || tos == 0) {
          throw new Compilable.CompilationException("Channel has i/o (" + froms + "/" + tos + "), invalid");
        }
        terminalsAccountedFor.addAll(con.getFromTerminals());
        terminalsAccountedFor.addAll(con.getToTerminals());
        CodeBlock.Builder codeBlock = CodeBlock.builder();
        // "receiver" + "_" + "Port" + "_Channel"
        String fromTerms = String.join("", con.getFromTerminals().stream().map(t -> t.getName()).collect(Collectors.toList()));
        String toTerms = String.join("", con.getToTerminals().stream().map(t -> t.getName()).collect(Collectors.toList()));
        String conBaseName = uniqueName.apply(fromTerms + "_" + toTerms);
        String conChanName = uniqueName.apply(conBaseName + "_Channel");
        String conOutName = uniqueName.apply(conBaseName + "_Out");
        String conInName = uniqueName.apply(conBaseName + "_In");
        if (froms > 1) {
          if (tos > 1) {
            codeBlock.addStatement("$T $L = $T.any2any()", Any2AnyChannel.class, conChanName, Channel.class);
            codeBlock.addStatement("$T $L = $L.out()", SharedChannelOutput.class, conOutName, conChanName);
            codeBlock.addStatement("$T $L = $L.in()", SharedChannelInput.class, conInName, conChanName);
          } else {
            codeBlock.addStatement("$T $L = $T.any2one()", Any2OneChannel.class, conChanName, Channel.class);
            codeBlock.addStatement("$T $L = $L.out()", SharedChannelOutput.class, conOutName, conChanName);
            codeBlock.addStatement("$T $L = $L.in()", AltingChannelInput.class, conInName, conChanName);
          }
        } else {
          if (tos > 1) {
            codeBlock.addStatement("$T $L = $T.one2any()", One2AnyChannel.class, conChanName, Channel.class);
            codeBlock.addStatement("$T $L = $L.out()", ChannelOutput.class, conOutName, conChanName);
            codeBlock.addStatement("$T $L = $L.in()", SharedChannelInput.class, conInName, conChanName);
          } else {
            codeBlock.addStatement("$T $L = $T.one2one()", One2OneChannel.class, conChanName, Channel.class);
            codeBlock.addStatement("$T $L = $L.out()", ChannelOutput.class, conOutName, conChanName);
            codeBlock.addStatement("$T $L = $L.in()", AltingChannelInput.class, conInName, conChanName);
          }
        }
        codeBlock.add("\n");
        connectionsCode.add(codeBlock.build());
        
        for (Terminal t : con.getFromTerminals()) {
          terminalToChannelname.put(t, conOutName);
        }
        for (Terminal t : con.getToTerminals()) {
          terminalToChannelname.put(t, conInName);
        }
        
        addedLines = true;
      }
      if (!addedLines) {
        connectionsCode.add("\n");
      }

      // Static param channels
      // Things we need out of this: unique chan = Channel.one2one();, channelIn name, channelOut name.
      CodeBlock.Builder staticParamChannelsCode = CodeBlock.builder();
      staticParamChannelsCode.add("// Static param channels code\n");
      addedLines = false;
      for (ProcessBlock block : network.blocks) {
        HashMap<String, Pair<String, String>> paramToChannelnames = wireformToParamToChannelnames.get(block);
        CodeBlock.Builder codeBlock = CodeBlock.builder();
        //TODO Check if any terminal names overlap?
        Map<String, Terminal> s2t = block.getTerminals().stream().collect(Collectors.toMap(t -> t.getName(), t -> t));
        for (String param : block.getArchetype().getParameters().keySet()) {
          Terminal t = s2t.get(param);
          // Do the terminals that don't have connections already
          //TODO Check for and error/warning on unconnected terminals, maybe?
          //TODO Check this code; it's not run in this test
          if (t != null && !terminalsAccountedFor.contains(t)) {
            terminalsAccountedFor.add(t);
          }
            
          String conBaseName = uniqueName.apply(block.getName() + "_" + param);
          String conChanName = uniqueName.apply(conBaseName + "_Channel");
          String conOutName = uniqueName.apply(conBaseName + "_Out");
          String conInName = uniqueName.apply(conBaseName + "_In");

          codeBlock.addStatement("$T $L = $T.one2one()", One2OneChannel.class, conChanName, Channel.class);
          codeBlock.addStatement("$T $L = $L.out()", ChannelOutput.class, conOutName, conChanName);
          codeBlock.addStatement("$T $L = $L.in()", AltingChannelInput.class, conInName, conChanName);
          codeBlock.add("\n");
          
          paramToChannelnames.put(param, new Pair<String, String>(conOutName, conInName));

          addedLines = true;
        }
        staticParamChannelsCode.add(codeBlock.build());
      }
      if (!addedLines) {
        staticParamChannelsCode.add("\n");
      }

      // Process booting
      CodeBlock.Builder processBootCode = CodeBlock.builder();
      {
        processBootCode.add("// Process boot code\n");
        //TODO Dynamic
        processBootCode.add("$[new $T(new $T[]{\n", Parallel.class, CSProcess.class);

        // Static params
        for (BlockWireform block : network.blocks) {
          HashMap<String, Pair<String, String>> paramToChannelnames = wireformToParamToChannelnames.get(block);
          for (Entry<String, Object> entry : block.getParameters().entrySet()) {
            // "UDPReceiverBlock_port_Out"
            String conOutName = paramToChannelnames.get(entry.getKey()).a;
            if (entry.getValue() instanceof String) {
              processBootCode.add("new $T($L, $S),\n", Generate.class, conOutName, entry.getValue());
            } else if (entry.getValue() instanceof Number) {
              processBootCode.add("new $T($L, $L),\n", Generate.class, conOutName, entry.getValue());
            } else {
              //TODO Provide some way of providing a generator?
              throw new Compilable.CompilationException("Type code-building not yet implemented: " + entry.getValue().getClass());
            }
          }
        }

        // Blocks
        for (BlockWireform block : network.blocks) {
          processBootCode.add(block.getConstructor(wireformToParamToChannelnames.get(block).entrySet().stream().collect(Collectors.toMap(e -> e.getKey(), e-> e.getValue().b)), terminalToChannelname));
          processBootCode.add(",\n");
        }
//        processBootCode.add("new $T($L, $L),\n", UDPReceiverBlock.class, "UDPReceiverBlock_port_In", "msg_msg_Out");
//        processBootCode.add("new $T($L, $L, $L),\n", UDPTransmitterBlock.class, "UDPTransmitterBlock_hostname_In", "UDPTransmitterBlock_port_In", "msg_msg_In");

        processBootCode.add("}).run();$]\n");
      }

      //TODO Hmm.  Ok, I need to iterate through connections/blocks, and also I need to be able to map terminals to constructor arguments.
      //TODO ...OR I need to request code blocks from wireforms.  ...If that works.
      MethodSpec run = MethodSpec.methodBuilder("run")
              .addAnnotation(Override.class)
              .addModifiers(Modifier.PUBLIC)
              .returns(void.class)
              .addCode(connectionsCode.build())
              .addCode(staticParamChannelsCode.build())
              .addCode(processBootCode.build())
              .build();

      TypeSpec networkInitSpec = TypeSpec.classBuilder("NetworkNameHere")
              .addSuperinterface(TypeName.get(CSProcess.class))
              .addModifiers(Modifier.PUBLIC)
              .addMethod(run)
              .build();

      networkClass = ClassName.get("com.vjcsp.network.classpath", networkInitSpec.name);
      JavaFile javaFile = JavaFile.builder(networkClass.packageName(), networkInitSpec)
              .build();
      try {
        javaFile.writeTo(srcDir);
      } catch (IOException ex) {
        throw new Compilable.CompilationException(ex);
      }
    }

    // Main class
    {
      MethodSpec main = MethodSpec.methodBuilder("main")
              .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
              .returns(void.class)
              .addParameter(String[].class, "args")
              .addStatement("new $T().run()", networkClass)
              .build();
      //TODO Hang on, networks kindof have to have an Archetype or something, too - they're their own class.
      TypeSpec mainClass = TypeSpec.classBuilder("NetworkNameHere_Main")
              .addModifiers(Modifier.PUBLIC)
              .addMethod(main)
              .build();

      JavaFile javaFile = JavaFile.builder("com.vjcsp.network.classpath", mainClass)
              .build();
      try {
        javaFile.writeTo(srcDir);
      } catch (IOException ex) {
        throw new Compilable.CompilationException(ex);
      }
    }
  }

  private void save() {
    //TODO Do
    System.err.println("Implement save");
  }

  private void exit() {
    if (hasChanged()) {
      switch (JOptionPane.showConfirmDialog(this, "Exit without saving?", "Exit?", JOptionPane.YES_NO_CANCEL_OPTION)) {
        case JOptionPane.YES_OPTION:
          this.dispose();
          break;
        case JOptionPane.NO_OPTION:
          save();
          this.dispose();
          break;
        case JOptionPane.CANCEL_OPTION:
          break;
      }
    } else {
      this.dispose();
    }
  }

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
  private javax.swing.JButton btnCompile;
  private javax.swing.JMenu jMenu1;
  private javax.swing.JMenu jMenu2;
  private javax.swing.JMenu jMenu3;
  private javax.swing.JMenuBar jMenuBar1;
  private javax.swing.JMenuItem jMenuItem1;
  private javax.swing.JMenuItem jMenuItem2;
  private javax.swing.JMenuItem jMenuItem3;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JPanel jPanel2;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JSplitPane jSplitPane1;
  private javax.swing.JLabel lFilter;
  private javax.swing.JList listBlocks;
  // End of variables declaration//GEN-END:variables
}
