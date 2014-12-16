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

import java.util.*;
import java.io.*;

/**
 * The ConstantValue attribute stores an index into the constant pool
 * that represents constant value.  A class's static fields have 
 * constant value attributes.
 *
 * @see polyglot.types.reflect Field
 *
 * @author Nate Nystrom
 *         (<a href="mailto:nystrom@cs.purdue.edu">nystrom@cs.purdue.edu</a>)
 */
public class ConstantValueAttr extends Attribute
{
  private int index;

  /**
   * Constructor.  Create a ConstantValue attribute from a data stream.
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
  ConstantValueAttr(DataInputStream in, int nameIndex, int length)
    throws IOException
  {
    super(nameIndex, length);
    index = in.readUnsignedShort();
  }
  
  public int getIndex() {
      return index;
  }
}
