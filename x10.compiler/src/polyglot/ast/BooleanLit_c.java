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

package polyglot.ast;

import polyglot.types.Context;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import polyglot.visit.PrettyPrinter;
import x10.constraint.XFailure;
import x10.constraint.XTerm;
import x10.errors.Errors;
import x10.errors.Errors.IllegalConstraint;
import x10.types.XTypeTranslator;
import x10.types.constants.ConstantValue;
import x10.types.constraints.CConstraint;
import x10.types.constraints.ConstraintManager;

/**
 * A <code>BooleanLit</code> represents a boolean literal expression.
 */
public class BooleanLit_c extends Lit_c implements BooleanLit
{
  protected boolean value;

  public BooleanLit_c(Position pos, boolean value) {
    super(pos);
    this.value = value;
  }

  /** Get the value of the expression. */
  public boolean value() {
    return this.value;
  }

  /** Set the value of the expression. */
  public BooleanLit value(boolean value) {
    BooleanLit_c n = (BooleanLit_c) copy();
    n.value = value;
    return n;
  }

  /** Type check the expression. */
  public Node typeCheck(ContextVisitor tc) {
      TypeSystem xts =  tc.typeSystem();
	  Type Boolean =  xts.Boolean();
	 
	  CConstraint c = ConstraintManager.getConstraintSystem().makeCConstraint();
	  try {
		  XTerm term = xts.xtypeTranslator().translate(c, this.type(Boolean),  tc.context());
		  c.addSelfBinding(term);
	  } catch (IllegalConstraint z) {
		  Errors.issue(tc.job(), z);
	  }
	  Type newType = Types.xclause(Boolean, c);
	  return type(newType);
  }

  public String toString() {
    return String.valueOf(value);
  }

  /** Write the expression to an output file. */
  public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
    w.write(String.valueOf(value));
  }

  /** Dumps the AST. */
  public void dump(CodeWriter w) {
    super.dump(w);

    w.allowBreak(4, " ");
    w.begin(0);
    w.write("(value " + value + ")");
    w.end();
  }

  public ConstantValue constantValue() {
      return ConstantValue.makeBoolean(value);
  }

}
