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
import polyglot.util.*;
import polyglot.visit.*;

/**
 * A <code>FieldAssign_c</code> represents a Java assignment expression to
 * a field.  For instance, <code>this.x = e</code>.
 * 
 * The class of the <code>Expr</code> returned by
 * <code>FieldAssign_c.left()</code>is guaranteed to be a <code>Field</code>.
 */
public abstract class FieldAssign_c extends Assign_c implements FieldAssign
{
    boolean targetImplicit;
    Receiver target;
    Id name;
    protected FieldInstance fi;
    
  public FieldAssign_c(NodeFactory nf, Position pos, Receiver target, Id name, Operator op, Expr right) {
    super(nf, pos, op, right);
    assert name != null;
    this.target = target;
    this.name = name;
  }

  @Override
  public Assign visitLeft(NodeVisitor v) {
      Id name = (Id) visitChild(this.name, v);
      Receiver target = (Receiver) visitChild(this.target, v);
      return reconstruct(target, name);
  }

protected Assign reconstruct(Receiver target, Id name) {
    if (name != this.name || target != this.target) {
	  FieldAssign_c n = (FieldAssign_c) copy();
	  n.target = target;
	  n.name = name;
	  return n;
      }
      return this;
}

public boolean targetImplicit() {
    return targetImplicit;
}

    public FieldAssign targetImplicit(boolean f) {
	FieldAssign_c n = (FieldAssign_c) copy();
	n.targetImplicit = f;
	return n;
    }
  
  public Expr left() {
      Field f = nf.Field(position(), target, name);
      if (fi != null) f = f.fieldInstance(fi);
      f = f.targetImplicit(targetImplicit);
      if (type != null && fi != null) f = (Field) f.type(fi.type());
      return f;
  }
  
  public Type leftType() {
      if (fi == null) return null;
      return fi.type();
  }

  public Receiver target() {
      return target;
  }

  public FieldAssign target(Receiver target) {
	FieldAssign_c n = (FieldAssign_c) copy();
	n.target = target;
	return n;
  }

  public Id name() {
      return name;
  }
  
  public FieldAssign name(Id name) {
      FieldAssign_c n = (FieldAssign_c) copy();
      n.name = name;
      return n;
  }

  @Override
  public Assign typeCheckLeft(ContextVisitor tc) {
      Field left = (Field) left();
      left = (Field) left.del().typeCheck(tc);
      FieldAssign_c n = (FieldAssign_c) reconstruct(left.target(), left.name());
      return n.fieldInstance(left.fieldInstance());
  }

  public FieldInstance fieldInstance() {
      return fi;
  }  
    
  public FieldAssign fieldInstance(FieldInstance fi) {
      FieldAssign_c n = (FieldAssign_c) copy();
      n.fi = fi;
      return n;
  }

  public Term firstChild() {
      if (target instanceof Term)
	  return (Term) target;
      else
	  return right;
  }

  protected void acceptCFGAssign(CFGBuilder v) {
      //     o.f = e: visit o -> e -> (o.f = e)
      if (target instanceof Term)
	  v.visitCFG((Term) target, right(), ENTRY);              
      v.visitCFG(right(), this, EXIT);
  }
  protected void acceptCFGOpAssign(CFGBuilder v) {
      if (target instanceof Term)
	  v.visitCFG((Term) target, right(), ENTRY);              
      v.visitCFG(right(), this, EXIT);
  }

  public List<Type> throwTypes(TypeSystem ts) {
      List<Type> l = new ArrayList<Type>(super.throwTypes(ts));

      if (target instanceof Expr) {
          l.add(ts.NullPointerException());
      }

      return l;
  }

  public String toString() {
	  return target + "." + name + " " + op + " " + right;
  }
 
}
