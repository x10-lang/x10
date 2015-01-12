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
 * Attribute is an abstract class for an attribute defined for a method,
 * field, or class.  An attribute consists of its name (represented as an
 * index into the constant pool) and its length.  Attribute is extended
 * to represent a constant value, code, exceptions, etc.
 *
 * @see polyglot.types.reflect ConstantValue
 * @see polyglot.types.reflect Exceptions
 *
 * @author Nate Nystrom
 *         (<a href="mailto:nystrom@cs.purdue.edu">nystrom@cs.purdue.edu</a>)
 */
public abstract class Attribute {
  protected int nameIndex;
  protected int length;

  /**
   * Constructor.
   *
   * @param nameIndex
   *        The index into the constant pool of the name of the attribute.
   * @param length
   *        The length of the attribute, excluding the header.
   */
  public Attribute(int nameIndex, int length) {
    this.nameIndex = nameIndex;
    this.length = length;
  }
  
  public int getName() {
      return nameIndex;
  }
}
