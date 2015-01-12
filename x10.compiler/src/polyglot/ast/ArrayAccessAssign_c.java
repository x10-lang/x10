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

import java.util.ArrayList;
import java.util.List;

import polyglot.frontend.Globals;
import polyglot.types.*;
import polyglot.util.CodeWriter;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.visit.*;
import x10.errors.Errors;
import x10.types.constants.ConstantValue;

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
    
  public ArrayAccessAssign_c(NodeFactory nf, Position pos, Expr array, Expr index, Operator op, Expr right) {
    super(nf, pos, op, right);
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
  public Assign typeCheckLeft(ContextVisitor tc) {
      Type at = array.type();
      if (!at.isArray())
	  Errors.issue(tc.job(),
	          new SemanticException("Target of array assignment is not an array element.", array.position()),
	          this);
      Type it = index.type();
      if (!it.isInt())
	  Errors.issue(tc.job(),
	          new SemanticException("Array element must be indexed by an int.", index.position()),
	          this);
      return this;
  }

  /** Type check the expression. */
  public Node typeCheck(ContextVisitor tc) {
      ArrayAccessAssign_c n = (ArrayAccessAssign_c) typeCheckLeft(tc);
      
      TypeSystem ts = tc.typeSystem();
    Type t = n.leftType();
    
    if (t == null)
    t = ts.unknownType(position());

    Expr right = n.right();
    Assign.Operator op = n.operator();

    Type s = right.type();

    Context context = tc.context();
    if (op == ASSIGN) {
      if (! ts.isImplicitCastValid(s, t, context) &&
          ! ts.typeEquals(s, t, context) &&
          ! ts.numericConversionValid(t, ConstantValue.toJavaObject(right.constantValue()), context)) {

        Errors.issue(tc.job(),
                new SemanticException("Cannot assign " + s + " to " + t + ".", position()));
      }

      return n.type(t);
    }

    if (op == ADD_ASSIGN) {
      // t += s
      if (ts.typeEquals(t, ts.String(), context) && ts.canCoerceToString(s, context)) {
        return n.type(ts.String());
      }

      if (t.isNumeric() && s.isNumeric()) {
        Type r;
        try {
            r = ts.promote(t, s);
        } catch (SemanticException e) {
            r = t;
        }
        return n.type(r);
      }

      Errors.issue(tc.job(),
              new SemanticException("Operator must have numeric or String operands.", position()));
      return n.type(t);
    }

    if (op == SUB_ASSIGN || op == MUL_ASSIGN ||
        op == DIV_ASSIGN || op == MOD_ASSIGN) {
      if (t.isNumeric() && s.isNumeric()) {
        Type r;
        try {
            r = ts.promote(t, s);
        } catch (SemanticException e) {
            r = t;
        }
        return n.type(r);
      }

      Errors.issue(tc.job(),
              new SemanticException("Operator must have numeric operands.", position()));
      return n.type(t);
    }

    if (op == BIT_AND_ASSIGN || op == BIT_OR_ASSIGN || op == BIT_XOR_ASSIGN) {
      if (t.isBoolean() && s.isBoolean()) {
        return n.type(ts.Boolean());
      }

      if (ts.isImplicitCastValid(t, ts.Long(), context) &&
          ts.isImplicitCastValid(s, ts.Long(), context)) {
        Type r;
        try {
            r = ts.promote(t, s);
        } catch (SemanticException e) {
            r = t;
        }
        return n.type(r);
      }

      Errors.issue(tc.job(),
              new SemanticException("Operator must have integral or boolean operands.", position()));
      return n.type(t);
    }

    if (op == SHL_ASSIGN || op == SHR_ASSIGN || op == USHR_ASSIGN) {
      if (ts.isImplicitCastValid(t, ts.Long(), context) &&
          ts.isImplicitCastValid(s, ts.Long(), context)) {
        // Only promote the left of a shift.
        Type r;
        try {
            r = ts.promote(t);
        } catch (SemanticException e) {
            r = t;
        }
        return n.type(r);
      }

      Errors.issue(tc.job(),
              new SemanticException("Operator must have integral operands.", position()));
      return n.type(t);
    }

    throw new InternalCompilerError("Unrecognized assignment operator " +
                                    op + ".");
  }
  
  public Type leftType() {
      Type at = array.type();
      if (at.isArray())
	  return at.toArray().base();
      return null;
  }
  
  public Expr left() {
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
  public String toString() {
	    return array + "(" + index + ") " + op + " " + right;
	   }
  public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
		printSubExpr(array, w, tr);
		w.write ("[");
		printBlock(index, w, tr);
		w.write ("]");
	    w.write(" ");
	    w.write(op.toString());
	    w.allowBreak(2, 2, " ", 1); // miser mode
	    w.begin(0);
	    printSubExpr(right, false, w, tr);
	    w.end();
	  }
}
