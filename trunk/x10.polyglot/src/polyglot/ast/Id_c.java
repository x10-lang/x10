/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2006-2007 Polyglot project group, Cornell University
 * Copyright (c) 2006-2007 IBM Corporation
 * 
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
