/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 * This file was originally derived from the Polyglot extensible compiler framework.
 *
 *  (C) Copyright 2000-2007 Polyglot project group, Cornell University
 *  (C) Copyright IBM Corporation 2007-2014.
 */

package polyglot.types.reflect;

import polyglot.types.*;
import java.util.*;
import java.io.*;
import java.lang.reflect.Modifier;

/**
 * Field models a field (member variable) in a class.  The Field class
 * grants access to information such as the field's modifiers, its name
 * and type descriptor (represented as indices into the constant pool),
 * and any attributes of the field.  Static fields have a ConstantValue
 * attribute.
 *
 * @see polyglot.types.reflect ConstantValue
 *
 * @author Nate Nystrom
 *         (<a href="mailto:nystrom@cs.purdue.edu">nystrom@cs.purdue.edu</a>)
 */
public class Field {
    protected DataInputStream in;
    protected ClassFile clazz; 
    protected int modifiers;
    protected int name;
    protected int type;
    protected Attribute[] attrs;
    protected ConstantValueAttr constantValue;
    protected Signature signature;
    protected boolean synthetic;

    /**
     * Constructor.  Read a field from a class file.
     *
     * @param in
     *        The data stream of the class file.
     * @param clazz
     *        The class file containing the field.
     * @exception IOException
     *        If an error occurs while reading.
     */
    Field(DataInputStream in, ClassFile clazz)
        throws IOException
    {
        this.clazz = clazz;
        this.in = in;
    }

    public void initialize() throws IOException {
        modifiers = in.readUnsignedShort();

        name = in.readUnsignedShort();
        type = in.readUnsignedShort();
        
        int numAttributes = in.readUnsignedShort();
        
        attrs = new Attribute[numAttributes];
        
        for (int i = 0; i < numAttributes; i++) {
            int nameIndex = in.readUnsignedShort();
            int length = in.readInt();
            
            Constant name = clazz.getConstants()[nameIndex];
            
            if (name != null) {
                if ("ConstantValue".equals(name.value())) {
                    constantValue = new ConstantValueAttr(in, nameIndex, length);
                    attrs[i] = constantValue;
                }
                if ("Synthetic".equals(name.value())) {
                    synthetic = true;
                }
                if ("Signature".equals(name.value())) {
                    signature = new Signature(clazz, in, nameIndex, length);
                    attrs[i] = signature;
                }
            }
            
            if (attrs[i] == null) {
                long n = in.skip(length);
                if (n != length) {
                    throw new EOFException();
                }
            }
        }
    }
    
    /**
     * Return true of t is java.lang.String.
     * We don't compare against ts.String() because ts.String() may not
     * yet be set.
     */
    public boolean isString(Type t) {
      return t.isClass()
          && t.toClass().isTopLevel()
          && t.toClass().fullName().equals(QName.make("java.lang.String"));
    }

    public boolean isSynthetic() {
      return synthetic;
    }

    public boolean isConstant() {
      return this.constantValue != null;
    }
    
    public Constant constantValue() {
      if (this.constantValue != null) {
        int index = this.constantValue.getIndex();
        return clazz.getConstants()[index];
      }

      return null;
    }

    public int getInt() throws SemanticException {
      Constant c = constantValue();

      if (c != null && c.tag() == Constant.INTEGER) {
        Integer v = (Integer) c.value();
        return v.intValue();
      }

      throw new SemanticException("Could not find expected constant " +
                                  "pool entry with tag INTEGER.");
    }

    public float getFloat() throws SemanticException {
      Constant c = constantValue();

      if (c != null && c.tag() == Constant.FLOAT) {
        Float v = (Float) c.value();
        return v.floatValue();
      }

      throw new SemanticException("Could not find expected constant " +
                                  "pool entry with tag FLOAT.");
    }

    public double getDouble() throws SemanticException {
      Constant c = constantValue();

      if (c != null && c.tag() == Constant.DOUBLE) {
        Double v = (Double) c.value();
        return v.doubleValue();
      }

      throw new SemanticException("Could not find expected constant " +
                                  "pool entry with tag DOUBLE.");
    }

    public long getLong() throws SemanticException {
      Constant c = constantValue();

      if (c != null && c.tag() == Constant.LONG) {
        Long v = (Long) c.value();
        return v.longValue();
      }

      throw new SemanticException("Could not find expected constant " +
                                  "pool entry with tag LONG.");
    }

    public String getString() throws SemanticException {
      Constant c = constantValue();

      if (c != null && c.tag() == Constant.STRING) {
        Integer i = (Integer) c.value();
        c = clazz.getConstants()[i.intValue()];

        if (c != null && c.tag() == Constant.UTF8) {
          String v = (String) c.value();
          return v;
        }
      }

      throw new SemanticException("Could not find expected constant " +
                                  "pool entry with tag STRING or UTF8.");
    }

    public Attribute[] getAttrs() {
        return attrs;
    }
    public ClassFile getClazz() {
        return clazz;
    }
    public ConstantValueAttr getConstantValue() {
        return constantValue;
    }
    public int getModifiers() {
        return modifiers;
    }
    public int getName() {
        return name;
    }
    public int getType() {
        return type;
    }
    public Signature getSignature() {
      return signature;
    }
    public String name() {
      return (String) clazz.getConstants()[this.name].value();
    }
    public String signature() {
      if (this.signature != null) {
        return (String) clazz.getConstants()[this.signature.getSignature()].value();
      }
      return (String) clazz.getConstants()[this.type].value();
    }
    public String toString() {
      return Modifier.toString(modifiers)+"("+Integer.toHexString(modifiers)+") "+name()+signature();
    }
}
