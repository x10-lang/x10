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

import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.util.*;
import polyglot.visit.*;
import x10.errors.Errors;
import x10.types.X10LocalInstance;

/**
 * An <code>AmbExpr</code> is an ambiguous AST node composed of a single
 * identifier that must resolve to an expression.
 */
public class AmbExpr_c extends Expr_c implements AmbExpr
{
  protected Id name;

  public AmbExpr_c(Position pos, Id name) {
    super(pos);
    assert(name != null);
    this.name = name;
  }

  /** Get the precedence of the field. */
  public Precedence precedence() {
    return Precedence.LITERAL;
  }
  
  /** Get the name of the expression. */
  public Id name() {
      return this.name;
  }
  
  /** Set the name of the expression. */
  public AmbExpr name(Id id) {
      AmbExpr_c n = (AmbExpr_c) copy();
      n.name = id;
      return n;
  }

  /** Reconstruct the expression. */
  protected AmbExpr_c reconstruct(Id name) {
      if (name != this.name) {
          AmbExpr_c n = (AmbExpr_c) copy();
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

  /** Disambiguate the expression. */
  public Node disambiguate(ContextVisitor ar) {
      try {
          return superDisambiguate(ar);
      } catch (SemanticException e) {
          Errors.issue(ar.job(), e, this);
          TypeSystem xts =  ar.typeSystem();
          X10LocalInstance li = xts.createFakeLocal(name.id(), e);
          return ar.nodeFactory().Local(position(), name).localInstance(li).type(li.type());
      }
  }
  
  private Node superDisambiguate(ContextVisitor ar) throws SemanticException {
    Position pos = position();
    Disamb disamb = ar.nodeFactory().disamb();
    Node n = disamb.disambiguate(this, ar, pos, null, name);

    if (n instanceof Expr) {
        return n;
    }

    throw new SemanticException("Could not find field or local variable \"" + name + "\".", pos);
  }

  public Node typeCheck(ContextVisitor tc) {
      assert false;
      // Didn't finish disambiguation; just return.
      return this;
  }

  /** Check exceptions thrown by the expression. */
  public Node exceptionCheck(ExceptionChecker ec) {
    throw new InternalCompilerError(position(),
                                    "Cannot exception check ambiguous node "
                                    + this + ".");
  } 

  /** Write the expression to an output file. */
  public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
      tr.print(this, name, w);
  }

  public String toString() {
    return name.toString() + "{amb}";
  }

  /**
   * Return the first (sub)term performed when evaluating this
   * term.
   */
  public Term firstChild() {
      return null;
  }

  /**
   * Visit this term in evaluation order.
   */
  public <S> List<S> acceptCFG(CFGBuilder v, List<S> succs) {
      return succs;
  }
}
