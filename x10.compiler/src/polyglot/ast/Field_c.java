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

import java.util.Collections;
import java.util.List;

import polyglot.types.*;
import polyglot.util.*;
import polyglot.visit.*;
import x10.types.constants.ConstantValue;

/**
 * A <code>Field</code> is an immutable representation of a Java field
 * access.  It consists of field name and may also have either a 
 * <code>Type</code> or an <code>Expr</code> containing the field being 
 * accessed.
 */
public abstract class Field_c extends Expr_c implements Field
{
  protected Receiver target;
  protected Id name;
  protected FieldInstance fi;
  protected boolean targetImplicit;
  
  public Field_c(Position pos, Receiver target, Id name) {
    super(pos);
    assert(target != null && name != null);
    this.target = target;
    this.name = name;
    this.targetImplicit = false;
e = new Exception();
  }
  Exception e;

  /** Get the precedence of the field. */
  public Precedence precedence() { 
    return Precedence.LITERAL;
  }

  /** Get the target of the field. */
  public Receiver target() {
    return this.target;
  }

  /** Set the target of the field. */
  public Field target(Receiver target) {
    Field_c n = (Field_c) copy();
    n.target = target;
    return n;
  }
  
  /** Get the name of the field. */
  public Id name() {
      return this.name;
  }
  
  /** Set the name of the field. */
  public Field name(Id name) {
      Field_c n = (Field_c) copy();
      n.name = name;
      return n;
  }

  /** Return the access flags of the variable. */
  public Flags flags() {
    return fi.flags();
  }

  /** Get the field instance of the field. */
  public FieldInstance varInstance() {
    return fi;
  }

  /** Get the field instance of the field. */
  public FieldInstance fieldInstance() {
    return fi;
  }

  /** Set the field instance of the field. */
  public Field fieldInstance(FieldInstance fi) {
    if (fi == this.fi) return this;
    Field_c n = (Field_c) copy();
    assert fi != null;
    n.fi = fi;
    return n;
  }

  public boolean isTargetImplicit() {
      return this.targetImplicit;
  }

  public Field targetImplicit(boolean implicit) {
      Field_c n = (Field_c) copy();
      n.targetImplicit = implicit;
      return n;
  }

  /** Reconstruct the field. */
  protected Field_c reconstruct(Receiver target, Id name) {
    if (target != this.target || name != this.name) {
      Field_c n = (Field_c) copy();
      n.target = target;
      n.name = name;
      return n;
    }

    return this;
  }

  /** Visit the children of the field. */
  public Node visitChildren(NodeVisitor v) {
    Receiver target = (Receiver) visitChild(this.target, v);
    Id name = (Id) visitChild(this.name, v);
    return reconstruct(target, name);
  }

  public Node buildTypes(TypeBuilder tb) {
      Field_c n = (Field_c) super.buildTypes(tb);

      TypeSystem ts = tb.typeSystem();

      FieldInstance fi = ts.createFieldInstance(position(),
              new ErrorRef_c<FieldDef>(ts, position(), "Cannot get FieldDef before type-checking field access.") {
          private static final long serialVersionUID = 7823901508583320051L;
          public String toString() { e.printStackTrace(); return super.toString(); } });
      return n.fieldInstance(fi);
  }
  
  /** Type check the field. */
  public abstract Node typeCheck(ContextVisitor tc);
  
  public Node checkConstants(ContextVisitor tc) {
      // Just check if the field is constant to force a dependency to be
      // created.
      isConstant();
      return this;
  }
  
  /** Write the field to an output file. */
  public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
    w.begin(0);
    if (!targetImplicit) {
        // explicit target.
        if (target instanceof Expr) {
          printSubExpr((Expr) target, w, tr);
        }
        else if (target instanceof TypeNode || target instanceof AmbReceiver) {
          print(target, w, tr);
        }
    
        w.write(".");
	w.allowBreak(2, 3, "", 0);
    }
    tr.print(this, name, w);
    w.end();
  }

  public void dump(CodeWriter w) {
    super.dump(w);

    if (fi != null) {                       
        w.allowBreak(4, " ");
        w.begin(0);
        w.write("(instance " + fi + ")");   
        w.end();                            
    }                                       

    w.allowBreak(4, " ");
    w.begin(0);
    w.write("(name \"" + name + "\")");
    w.end();
  }

  public Term firstChild() {
      if (target instanceof Term) {
          return (Term) target;
      }
      
      return null;
  }

  public <S> List<S> acceptCFG(CFGBuilder v, List<S> succs) {
      if (target instanceof Term) {
          v.visitCFG((Term) target, this, EXIT);
      }
      
      return succs;
  }


  public String toString() {
    return (target != null ? target + "." : "") + name;
  }


  public List<Type> throwTypes(TypeSystem ts) {
      if (target instanceof Expr && ! (target instanceof Special)) {
          return Collections.<Type>singletonList(ts.NullPointerException());
      }

      return Collections.<Type>emptyList();
  }

  public boolean isConstant() {
      if (fi != null &&
              (target instanceof TypeNode ||
                      (target instanceof Special && targetImplicit))) {
          return fi.isConstant();
      }

      return false;
  }

  public ConstantValue constantValue() {
    if (isConstant()) {
      return fi.constantValue();
    }

    return null;
  }
  
  /**
   * Check the consistency of the implicit target inserted by the compiler by
   * asserting that the FieldInstance in the Context for this field's name is
   * the same as the FieldInstance we assigned to this field.
   */
  protected void checkConsistency(Context c) {
      if (targetImplicit) {
          VarInstance<?> vi = c.findVariableSilent(name.id());
          if (vi instanceof FieldInstance) {
              FieldInstance rfi = (FieldInstance) vi;
              // Compare the original (declaration) fis, not the actuals.
              // We do this because some extensions that do type substitutions
              // perform the substitution
              // on the fi after lookup and some extensions modify lookup
              // itself to do the substitution.
              if (c.typeSystem().equals(rfi.def(), fi.def())) {
                  // all is OK.
                  return;
              }
              System.out.println("(found) rfi is " + rfi.def());
              System.out.println("(actual) fi is " + fi.def());
          }
          throw new InternalCompilerError("Field " + this + " has an " +
               "implicit target, but the name " + name.id() + " resolves to " +
               vi + " instead of " + target, position());
      }      
  }


}
