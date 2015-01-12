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
 *  (C) Copyright IBM Corporation 2007-2015.
 */

package polyglot.types.reflect;

import java.util.*;
import java.io.*;

/**
 * Signature describes the signature of the class, method, or field with
 * generic information.
 * The Signature attribute stores an index into the constant pool of the
 * generic signature of the class, method, or field.
 *
 * @see polyglot.types.reflect Method
 * @see polyglot.types.reflect Field
 *
 * @author Igor Peshansky
 */
public class Signature extends Attribute {
  private int signature;
  private ClassFile clazz;

  /**
   * Constructor for creating a <code>Signature</code> from scratch.
   *
   * @param nameIndex
   *        The index of the UTF8 string "Signature" in the class's
   *        constant pool
   * @param signature
   *        The index of the UTF8 string in the class's constant pool
   *        that represents the generic signature of the entity
   */
  public Signature(ClassFile clazz, int nameIndex, int signature) {
    super(nameIndex, 2 + 2);
    this.clazz = clazz;
    this.signature = signature;
  }

  /**
   * Constructor.  Create a Signature attribute from a data stream.
   *
   * @param in
   *        The data stream of the class file.
   * @param nameIndex
   *        The index into the constant pool of the name of the attribute.
   * @param length
   *        The length of the attribute, excluding the header.
   * @exception IOException
   *        If an error occurs while reading.
   */
  public Signature(ClassFile clazz, DataInputStream in,
                   int nameIndex, int length) throws IOException
  {
    super(nameIndex, length);

    this.clazz = clazz;

    signature = in.readUnsignedShort();
  }
  public ClassFile getClazz() {
      return clazz;
  }
  public int getSignature() {
      return signature;
  }
}
