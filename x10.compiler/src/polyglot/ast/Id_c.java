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

import polyglot.types.Name;
import polyglot.util.*;
import polyglot.visit.PrettyPrinter;

/** A node that represents a simple identifier in the AST. */
public class Id_c extends Node_c implements Id
{
  protected Name id;

  public Id_c(Position pos, Name id) {
    super(pos);
    assert(id != null);
    this.id = id;
  }

  // Override to make Id.equals(String) a compile-time error
  public final void equals(String s) { }
  public final void equals(Name s) { }

  /** Get the name of the expression. */
  public Name id() {
    return this.id;
  }

  /** Set the name of the expression. */
  public Id id(Name id) {
    Id_c n = (Id_c) copy();
    n.id = id;
    return n;
  }

  /** Write the name to an output file. */
  public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
    w.write(id.toString());
  }

  public String toString() {
    return id.toString();
  }

  public void dump(CodeWriter w) {
    w.write("(Id \"" + id + "\")");
  }

}
