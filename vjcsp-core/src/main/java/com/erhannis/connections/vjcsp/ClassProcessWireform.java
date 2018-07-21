/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.connections.vjcsp;

import com.erhannis.connections.base.BlockArchetype;
import static com.erhannis.connections.base.BlockWireform.ERROR_NAME;
import com.erhannis.connections.base.Terminal;
import com.erhannis.connections.base.TransformChain;
import com.squareup.javapoet.CodeBlock;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;
import jcsp.lang.CSProcess;
import jcsp.lang.Channel;
import jcsp.lang.ChannelInput;
import jcsp.lang.ChannelOutput;

/**
 *
 * @author erhannis
 */
public class ClassProcessWireform extends ProcessBlock {
  public static class Archetype implements BlockArchetype {
    protected final Class<? extends CSProcess> clazz;
    protected final Constructor<? extends CSProcess> ctor;
    protected final LinkedHashMap<String, Class> params;
    protected final LinkedHashMap<String, Class> terminalsIn;
    protected final LinkedHashMap<String, Class> terminalsOut;

    public Archetype(Class<? extends CSProcess> clazz) throws NoSuchMethodException {
      this.clazz = clazz;
      Constructor<?>[] ctors = clazz.getConstructors();
      if (ctors.length == 0) {
        throw new NoSuchMethodException("Can't find constructor for class: " + clazz);
      }
      //TODO Could pick a particular constructor
      ctor = (Constructor<? extends CSProcess>) ctors[0];
      LinkedHashMap<String, Class> params = new LinkedHashMap<>();
      LinkedHashMap<String, Class> terminalsIn = new LinkedHashMap<>();
      LinkedHashMap<String, Class> terminalsOut = new LinkedHashMap<>();
      Parameter[] ctorParams = ctor.getParameters();
      for (Parameter ctorParam : ctorParams) {
        Class<?> paramClazz = ctorParam.getType();
        if (ChannelInput.class.isAssignableFrom(paramClazz)) {
          //TODO When have channel generics, use that
          params.put(ctorParam.getName(), Object.class);
        } else if (ChannelOutput.class.isAssignableFrom(paramClazz)) {
          //TODO When have channel generics, use that
          terminalsOut.put(ctorParam.getName(), Object.class);
        } else {
          //TODO We should be able to handle this
          throw new NoSuchMethodException("Class's first found ctor is invalid: " + clazz);
        }
      }
      this.params = params;
      this.terminalsIn = terminalsIn;
      this.terminalsOut = terminalsOut;
    }

    @Override
    public String getRunformClassname() {
      return clazz.getCanonicalName();
    }

    @Override
    public String getName() {
      return clazz.getSimpleName();
    }

    @Override
    public HashMap<String, Class> getParameters() {
      return params;
    }

    @Override
    public ClassProcessWireform createWireform(HashMap<String, Object> params, String name, TransformChain transformChain) {
      params = (params != null ? params : new HashMap<String, Object>());
      ClassProcessWireform wireform = new ClassProcessWireform(this, params, name, transformChain);
      for (Entry<String, Class> entry : this.params.entrySet()) {
        if (!params.containsKey(entry.getKey())) {
          wireform.terminals.add(new PlainInputTerminal(entry.getKey(), new TransformChain(null, transformChain), new IntOrEventualClass(entry.getValue())));
        }
      }
      for (Entry<String, Class> entry : terminalsIn.entrySet()) {
        wireform.terminals.add(new PlainInputTerminal(entry.getKey(), new TransformChain(null, transformChain), new IntOrEventualClass(entry.getValue())));
      }
      for (Entry<String, Class> entry : terminalsOut.entrySet()) {
        wireform.terminals.add(new PlainOutputTerminal(entry.getKey(), new TransformChain(null, transformChain), new IntOrEventualClass(entry.getValue())));
      }
      return wireform;
    }

    @Override
    public boolean equals(Object obj) {
      if (obj == null) {
        return false;
      }
      boolean result = true;
      result &= (this.getClass() == obj.getClass());
      result &= (this.clazz == ((Archetype) obj).clazz);

      return result;
    }

    @Override
    public int hashCode() {
      return Objects.hash(this.getClass(), clazz);
    }
  }

  //TODO What happens if the class definition changes between save/load?
  protected final HashMap<String, Object> params;
  protected final Archetype mArchetype;

  public ClassProcessWireform(Archetype archetype, HashMap<String, Object> params, String name, TransformChain transformChain) {
    super(name, transformChain);
    this.params = params;
    this.mArchetype = archetype;
  }

  @Override
  public BlockArchetype getArchetype() {
    return mArchetype;
  }

  @Override
  public void compile(File root) throws CompilationException {
    mArchetype.compile(root);
    //TODO Do
    System.err.println("Implement (ClassProcessWireform).compile()");
  }

  @Override
  public CodeBlock getConstructor(Map<String, String> paramToChannelname, Map<Terminal, String> terminalToChannelname) {
    //TODO Eventually allow normal ctor args, not just channels
    ArrayList<Object> formatArgs = new ArrayList<>();
    formatArgs.add(getRunformClass());
    for (Parameter p : mArchetype.ctor.getParameters()) {
      String name = p.getName();
      if (params.containsKey(name)) {
        formatArgs.add(paramToChannelname.getOrDefault(name, ERROR_NAME));
      } else {
        formatArgs.add(terminalToChannelname.getOrDefault(getTerminals().stream().filter(t -> name.equals(t.getName())).findFirst().orElse(null), ERROR_NAME));
      }
    }
    
    String chanFormat = String.join(", ", Arrays.asList(mArchetype.ctor.getParameters()).stream().map(s -> "$L").collect(Collectors.toList()));
    return CodeBlock.builder().add("new $T(" + chanFormat + ")", formatArgs.toArray()).build();
  }

  @Override
  public HashMap<String, Object> getParameters() {
    return params;
  }

  @Override
  public Class getRunformClass() {
    return mArchetype.clazz;
  }
}
