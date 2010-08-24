/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2007 Polyglot project group, Cornell University
 * Copyright (c) 2006-2007 IBM Corporation
 * 
 */

package polyglot.ast;

import java.util.ArrayList;
import java.util.List;

import polyglot.types.*;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.visit.*;

/**
 * A <code>ArrayAccessAssign_c</code> represents a Java assignment expression
 * to an array element.  For instance, <code>A[3] = e</code>.
 * 
 * The class of the <code>Expr</code> returned by
 * <code>ArrayAccessAssign_c.left()</code>is guaranteed to be an
 * <code>ArrayAccess</code>.
 */
public class ArrayAccessAssign_c extends Assign_c implements ArrayAccessAssign
{
    Expr array;
    Expr index;
    
  public ArrayAccessAssign_c(Position pos, Expr array, Expr index, Operator op, Expr right) {
    super(pos, op, right);
    this.array = array;
    this.index = index;
  }

  public Expr array() {
      return array;
  }

  public Expr index() {
      return index;
  }

  public ArrayAccessAssign array(Expr array) {
      ArrayAccessAssign_c n = (ArrayAccessAssign_c) copy();
      n.array = array;
      return n;
  }

  public ArrayAccessAssign index(Expr index) {
      ArrayAccessAssign_c n = (ArrayAccessAssign_c) copy();
      n.index = index;
      return n;
  }

  @Override
  public Assign typeCheckLeft(ContextVisitor tc) throws SemanticException {
      Type at = array.type();
      if (!at.isArray())
	  throw new SemanticException("Target of array assignment is not an array element.", array.position());
      Type it = index.type();
      if (!it.isInt())
	  throw new SemanticException("Array element must be indexed by an int.", index.position());
      return this;
  }


  public Type leftType() {
      Type at = array.type();
      if (at.isArray())
	  return at.toArray().base();
      return null;
  }
  
  public Expr left(NodeFactory nf) {
      ArrayAccess aa = nf.ArrayAccess(position(), array, index);
      if (type != null)
            return aa.type(type);
      else
            return aa;
  }

  @Override
  public Assign visitLeft(NodeVisitor v) {
      Expr array = (Expr) visitChild(this.array, v);
      Expr index = (Expr) visitChild(this.index, v);
      return reconstruct(array, index);
  }

  protected Assign reconstruct(Expr array, Expr index) {
      if (array != this.array || index != this.index) {
	  ArrayAccessAssign_c n = (ArrayAccessAssign_c) copy();
	  n.array = array;
	  n.index = index;
	  return n;
      }
      return this;
  }

  public Term firstChild() {
	  return array;
  }
  
  protected void acceptCFGAssign(CFGBuilder v) {
      //    a[i] = e: visit a -> i -> e -> (a[i] = e)
      v.visitCFG(array, index, ENTRY);
      v.visitCFG(index, right(), ENTRY);
      v.visitCFG(right(), this, EXIT);
  }
  protected void acceptCFGOpAssign(CFGBuilder v) {
      v.visitCFG(array, index, ENTRY);
      v.visitCFG(index, right(), ENTRY);
      v.visitCFG(right(), this, EXIT);
  }

  public List<Type> throwTypes(TypeSystem ts) {
      List<Type> l = new ArrayList<Type>(super.throwTypes(ts));
      
      if (throwsArrayStoreException()) {
          l.add(ts.ArrayStoreException());
      }
      
      l.add(ts.NullPointerException());
      l.add(ts.OutOfBoundsException());
      
      return l;
  }
  
  /** Get the throwsArrayStoreException of the expression. */
  public boolean throwsArrayStoreException() {
    return op == ASSIGN && array.type().isReference();
  }
}
