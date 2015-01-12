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

package polyglot.ast;


import polyglot.types.Flags;
import polyglot.util.*;
import polyglot.visit.PrettyPrinter;

public class FlagsNode_c extends Node_c implements FlagsNode
{
  protected Flags flags;

  public FlagsNode_c(Position pos, Flags flags) {
    super(pos);
    assert(flags != null);
    this.flags = flags;
  }

  // Override to make Name.equals(Flags) a compile-time error
  public final void equals(Flags s) { }

  /** Get the name of the expression. */
  public Flags flags() {
    return this.flags;
  }

  /** Set the name of the expression. */
  public FlagsNode flags(Flags flags) {
    FlagsNode_c n = (FlagsNode_c) copy();
    n.flags = flags;
    return n;
  }

  /** Write the name to an output file. */
  public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
    w.write(flags.translateJava());
  }

  public String toString() {
    return flags.toString();
  }

  public void dump(CodeWriter w) {
    w.write("(Flags \"" + flags + "\")");
  }
  

}
