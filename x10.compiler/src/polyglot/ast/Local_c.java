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

import java.util.List;

import polyglot.types.*;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.visit.*;
import x10.types.constants.ConstantValue;

/** 
 * A local variable expression.
 */
public abstract class Local_c extends Expr_c implements Local
{
  protected Id name;
  protected LocalInstance li;

  public Local_c(Position pos, Id name) {
    super(pos);
    assert(name != null);
    this.name = name;
  }

  /** Get the precedence of the local. */
  public Precedence precedence() { 
    return Precedence.LITERAL;
  }

  /** Get the name of the local. */
  public Id name() {
    return this.name;
  }
  
  /** Set the name of the local. */
  public Local name(Id name) {
      Local_c n = (Local_c) copy();
      n.name = name;
      return n;
  }
  
  /** Return the access flags of the variable. */
  public Flags flags() {
    return li.flags();
  }

  /** Get the local instance of the local. */
  public LocalInstance varInstance() {
    return li;
  }

  /** Get the local instance of the local. */
  public LocalInstance localInstance() {
    return li;
  }

  /** Set the local instance of the local. */
  public Local localInstance(LocalInstance li) {
    if (li == this.li) return this;
    Local_c n = (Local_c) copy();
    n.li = li;
    return n;
  }

  /** Reconstruct the expression. */
  protected Local_c reconstruct(Id name) {
      if (name != this.name) {
          Local_c n = (Local_c) copy();
          n.name = name;
          return n;
      }
      
      return this;
  }
  
  /** Visit the children of the constructor. */
  public Node visitChildren(NodeVisitor v) {
      Id name = (Id) visitChild(this.name, v);
      return reconstruct(name);
  }

  public Node buildTypes(TypeBuilder tb) {
      Local_c n = (Local_c) super.buildTypes(tb);

      TypeSystem ts = tb.typeSystem();

      LocalInstance li = ts.createLocalInstance(position(), new ErrorRef_c<LocalDef>(ts, position(), "Cannot get LocalDef before type-checking local variable."));
      return n.localInstance(li);
  }

  /** Type check the local. */
  public abstract Node typeCheck(ContextVisitor tc);

  public Term firstChild() {
      return null;
  }

  public <S> List<S> acceptCFG(CFGBuilder v, List<S> succs) {
      return succs;
  }

  public String toString() {
    return name.toString();
  }

  /** Write the local to an output file. */
  public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
      tr.print(this, name, w);
  }

  /** Dumps the AST. */
  public void dump(CodeWriter w) {
    super.dump(w);

    if (li != null) {
	w.allowBreak(4, " ");
	w.begin(0);
	w.write("(instance " + li + ")");
	w.end();
    }
  }
  
  public boolean isConstant() {
    return li != null && li.isConstant();
  }

  public ConstantValue constantValue() {
    if (! isConstant()) return null;
    return li.constantValue();
  }
  


}
