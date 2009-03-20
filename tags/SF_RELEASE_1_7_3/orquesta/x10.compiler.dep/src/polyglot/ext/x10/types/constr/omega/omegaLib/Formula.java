package polyglot.ext.x10.types.constr.omega.omegaLib;

import java.util.*;

/**
 * Presburger Formula base class.
 * <p>
 * $Id: Formula.java,v 1.1 2008-03-06 18:02:23 nystrom Exp $
 * <p>
 * Copyright 2005 by the <a href="http://spa-www.cs.umass.edu/">Scale Compiler Group</a>,<br>
 * <a href="http://www.cs.umass.edu/">Department of Computer Science</a><br>
 * <a href="http://www.umass.edu/">University of Massachusetts</a>,<br>
 * Amherst MA. 01003, USA<br>
 * All Rights Reserved.<br>
 * <p>
 * This version of the Omega Libray is a translation from C++ to Java
 * of the Omega Library developed at the University of Maryland.
 * <blockquote cite="http://www.cs.umd.edu/projects/omega">
 * Copyright (C) 1994-1996 by the Omega Project
 * <p>
 * All rights reserved.
 * <p>
 * NOTICE:  This software is provided ``as is'', without any
 * warranty, including any implied warranty for merchantability or
 * fitness for a particular purpose.  Under no circumstances shall
 * the Omega Project or its agents be liable for any use of, misuse
 * of, or inability to use this software, including incidental and
 * consequential damages.
 * <p>
 * License is hereby given to use, modify, and redistribute this
 * software, in whole or in part, for any purpose, commercial or
 * non-commercial, provided that the user agrees to the terms of this
 * copyright notice, including disclaimer of warranty, and provided
 * that this copyright notice, including disclaimer of warranty, is
 * preserved in the source code and documentation of anything derived
 * from this software.  Any redistributor of this software or
 * anything derived from this software assumes responsibility for
 * ensuring that any parties to whom such a redistribution is made
 * are fully aware of the terms of this license and disclaimer.
 * <p>
 * The Omega project can be contacted at
 * <a href="mailto:omega@cs.umd.edu">omega@cs.umd.edu</a>
 * or <a href="http://www.cs.umd.edu/projects/omega">http://www.cs.umd.edu/projects/omega</a>
 * </blockquote>
 * Presburger formulas are those formulas that can be constructed
 * by combining affine constraints on integer variables with the logical operations
 * <i>not</i>, <i>and</i> and <i>or</i>, and the quantifiers <i>forall</i> and 
 * <i>there-exists</i>.
 * The affine constraints can be either equality constraints or inequality
 * constraints (abbreviated as EQs and GEQs respectively).
 * There are a number of algorithms for testing the satisfiability of arbitrary
 * Presburger formulas.
 * <p>
 * The best known upper bound on the performance of an algorithm for
 * verifying Presburger formulas is 2<sup>2<sup>2<sup>n</sup></sup></sup>.
 * and we have no reason to believe that the omegaLib provides better 
 * worst-case performance.  However, it may be more efficient for the many 
 * simple cases that arise in compiler applications.
 */
public abstract class Formula
{
  public static final int OP_RELATION = 0;
  public static final int OP_NOT      = 1;
  public static final int OP_AND      = 2;
  public static final int OP_OR       = 3;
  public static final int OP_CONJUNCT = 4;
  public static final int OP_FORALL   = 5;
  public static final int OP_EXISTS   = 6;

  protected OmegaLib omegaLib;
  protected Vector   myChildren;
  protected Formula  myParent;
  protected RelBody  myRelation;

  protected Formula(OmegaLib omegaLib, Formula p, RelBody r)
  {
    this.omegaLib   = omegaLib;
    this.myParent   = p;
    this.myRelation = r;
    this.myChildren = new Vector();
  }

  protected Formula(OmegaLib omegaLib)
  {
    this(omegaLib, null, null);
  }

  public void delete()
  {
    int l = myChildren.size();
    for(int j = 0; j < l; j++) {
      Formula c = (Formula) myChildren.elementAt(j);
      c.delete();
    }
    myChildren = null;
  }

  public void setFinalized()
  {
    if (myChildren.size() == 0)
      return;
    Formula f = (Formula) myChildren.elementAt(0);
    f.setFinalized();
  }

  /**
   * Convert the class name of this node to a string representation.
   */
  public final String toStringClass()
  {
    String c = getClass().toString();
    return c.substring(c.lastIndexOf('.') + 1);
  }

  protected void olAssert(boolean t)
  {
    if (t)
      return;
    throw new polyglot.util.InternalCompilerError(this.getClass().getName());
  }

  protected void olAssert(boolean t, String msg)
  {
    if (t)
      return;
    throw new polyglot.util.InternalCompilerError(this.getClass().getName() + ": " + msg);
  }

  public boolean canAddChild()
  {
    return true;
  }

  public abstract int nodeType();

  public FForall addForall()
  {
    assertNotFinalized();
    if (!canAddChild())
      throw new polyglot.util.InternalCompilerError("acan't add child!");

    FForall f = new FForall(omegaLib, this, myRelation);
    myChildren.addElement(f);
    return f;
  }

  public FExists addExists()
  {
    assertNotFinalized();
    if (!canAddChild())
      throw new polyglot.util.InternalCompilerError("acan't add child!");

    FExists f = new FExists(omegaLib, this, myRelation);
    myChildren.addElement(f);
    return f;
  }

  public FExists addExists(Vector S)
  {
    assertNotFinalized();
    if (!canAddChild())
      throw new polyglot.util.InternalCompilerError("acan't add child!");

    FExists f = new FExists(omegaLib, this, myRelation, S);
    myChildren.addElement(f);
    return f;
  }

  public FAnd andWith()
  {
    return addAnd();
  }

  public FAnd addAnd()
  {
    assertNotFinalized();
    if (!canAddChild())
      throw new polyglot.util.InternalCompilerError("acan't add child!");

    FAnd f = new FAnd(omegaLib, this, myRelation);
    myChildren.addElement(f);
    return f;
  }

  public FOr addOr()
  {
    assertNotFinalized();
    if (!canAddChild())
      throw new polyglot.util.InternalCompilerError("acan't add child!");

    FOr f = new FOr(omegaLib, this, myRelation);
    myChildren.addElement(f);
    return f;
  }

  public FNot addNot()
  {
    assertNotFinalized();
    if (!canAddChild())
      throw new polyglot.util.InternalCompilerError("acan't add child!");

    FNot f = new FNot(omegaLib, this, myRelation);
    myChildren.addElement(f);
    return f;
  }

  public RelBody relation()
  {
    return myRelation;
  }

  protected Formula removeFirstChild()
  {
    return (Formula) myChildren.remove(0);
  }

  protected Formula getFirstChild()
  {
    return (Formula) myChildren.elementAt(0);
  }

  protected void removeChild(Formula kid)
  {
    if (kid.myParent != this)
      throw new polyglot.util.InternalCompilerError("Incorrect Formula structure.");

    if (myChildren.elementAt(0) == kid) {
      myChildren.removeElementAt(0);
      return;
    }

    int l = myChildren.size();
    int k = -1;
    for (int j = 0; (j < l) && (myChildren.elementAt(j) != kid); k = j, j++) ;
        
    if (k >= 0) {
      myChildren.setSize(k);
      return;
    }

    throw new polyglot.util.InternalCompilerError("Child to be removed not found in child list");
  }

  private void replaceChild(Formula child, Formula new_child)
  {
    if (child.myParent != this)
      throw new polyglot.util.InternalCompilerError("Incorrect Formula structure.");

    int l = myChildren.size();
    for(int j = 0; j < l; j++) {
      Formula c = (Formula) myChildren.elementAt(j);
      if (c == child) {
        myChildren.setElementAt(new_child, j);
        new_child.myParent = this;
        new_child.myRelation = this.relation();
        break;
      }
    }
  }

  /**
   * If this formula can have children, add the formula as a child of this formula. Abort oterwise.
   * The parent of the child is set to this formula and the relation of the child is set to the relation of this formula.
   */
  protected void checkAndAddChild(Formula kid)
  {
    if (!canAddChild())
      throw new polyglot.util.InternalCompilerError("Can't add child to " + this);

    myChildren.addElement(kid);
    kid.myParent = this;
    kid.myRelation = this.relation();
  }

  /**
   * Add the formula as a child of this formula.
   * The parent and the relation of the child are assumed to be set already.
   */
  protected void addChild(Formula kid)
  {
    myChildren.addElement(kid);
  }

  protected Conjunct addConjunct()
  {
    assertNotFinalized();
    if (!canAddChild())
      throw new polyglot.util.InternalCompilerError("Can't add child to " + this);

    Conjunct f = new Conjunct(omegaLib, this, myRelation);
    myChildren.addElement(f);
    return f;
  }

  public abstract Conjunct findAvailableConjunct();

  /**
   * Push exists takes responsibility for the VarDecls.
   * It should: Re-use them, or Delete them.
   */
  protected void pushExists(Vector vid)
  {
    throw new polyglot.util.InternalCompilerError("pushExists");
  }

  public Vector children()
  {
    return myChildren;
  }

  public int numberOfChildren()
  {
    return myChildren.size();
  }

  public Vector getChildren()
  {
    return myChildren;
  }

  public Formula parent()
  {
    return myParent;
  }

  public void setParent(Formula p)
  {
    myParent = p;
  }

  public void verifytree() // should be const
  {
    int l = myChildren.size();
    for (int i = 0; i < l; i++) {
      Formula c = (Formula) myChildren.elementAt(i);
      if (c.myParent != this)
        throw new polyglot.util.InternalCompilerError("Child not child of parent.");
      if (c.myRelation != this.myRelation)
        throw new polyglot.util.InternalCompilerError("Child relation differs.");
      c.verifytree();
    }
  }

  public void reverseLeadingDirInfo()
  {
    int l = myChildren.size();
    for (int i = 0; i < l; i++) {
      Formula c = (Formula) myChildren.elementAt(i);
      c.reverseLeadingDirInfo();
    }
  }

  public void invalidateLeadingInfo(int changed)
  {
    int l = myChildren.size();
    for (int i = 0; i < l; i++) {
      Formula c = (Formula) myChildren.elementAt(i);
      c.invalidateLeadingInfo(changed);
    }
  }

  public void enforceLeadingInfo(int guaranteed, int possible, int dir)
  {
    int l = myChildren.size();
    for (int i = 0; i < l; i++) {
      Formula c = (Formula) myChildren.elementAt(i);
      c.enforceLeadingInfo(guaranteed, possible, dir);
    }
  }

  /**
   * Remap mappedVars in all conjuncts of formula.
   * Uses the remap field of the VarDecl.
   */
  public void remap()
  {
    int l = myChildren.size();
    for (int i = 0; i < l; i++) {
      Formula c = (Formula) myChildren.elementAt(i);
      c.remap();
    }
  }

  public abstract DNF DNFize();
  public abstract Formula copy(Formula f, RelBody b);

  public void beautify()
  {
    // copy list of children, as they may be removed as we work
    int    l  = myChildren.size();
    Vector cs = (Vector) myChildren.clone();
    for (int i = 0; i < l; i++) {
      Formula c = (Formula) cs.elementAt(i);
      c.beautify();
    }
  }

  public void rearrange()
  {
    // copy list of children, as they may be removed as we work
    int l = myChildren.size();
    for (int i = 0; i < l; i++) {
      Formula c = (Formula) myChildren.elementAt(i);
      c.rearrange();
    }
  }

  public void setupNames()
  {
    if (false && omegaLib.trace)
      System.out.println("Setting up names for formula 0x" + Integer.toHexString(hashCode()));

    int l = myChildren.size();
    for (int i = 0; i < l; i++) {
      Formula c = (Formula) myChildren.elementAt(i);
      c.setupNames();
    }
  }

  /*
   * Function that descends to conjuncts to merge columns
   */
  protected void combineColumns()
  {
    int l = myChildren.size();
    for (int i = 0; i < l; i++) {
      Formula c = (Formula) myChildren.elementAt(i);
      c.combineColumns();
    }
  }

  public void setRelation(RelBody r)
  {
    myRelation = r;
    int l = myChildren.size();
    for (int i = 0; i < l; i++) {
      Formula c = (Formula) myChildren.elementAt(i);
      c.setRelation(r);
    }
  }

  public void setParent(Formula parent, RelBody reln)
  {
    myParent = parent;
    myRelation = reln;

    int l = myChildren.size();
    for (int i = 0; i < l; i++) {
      Formula c = (Formula) myChildren.elementAt(i);
      c.setParent(this, reln);
    }
  }

  public void assertNotFinalized()
  {
    if (omegaLib.skipFinalizationCheck != 0)
      return;

    if (relation().isFinalized())
      throw new polyglot.util.InternalCompilerError("Relation is finalized.");
    if (relation().isShared())
      throw new polyglot.util.InternalCompilerError("Relation is shared.");
  }

  public Conjunct reallyConjunct() // until we get RTTI
  {
    System.out.println("** reallyConjunct() called on something that wasn't");
    return null;
  }

  public int priority()
  {
    return 0;
  }

  public String toString()
  {
    int          nt  = nodeType();
    StringBuffer buf = new StringBuffer("(");

    buf.append(toStringClass());
    buf.append(" 0x");
    buf.append(Integer.toHexString(hashCode()));
    buf.append(' ');

    if (myChildren.size() == 0) {
      if ((nt == OP_RELATION) || (nt == OP_OR))
        buf.append("FALSE ");
      else if (nt == OP_AND)
        buf.append("TRUE ");
      else
        buf.append("??? ");
    }
    
    int l = myChildren.size();
    for (int i = 0; i < l; i++) {
      Formula c = (Formula) myChildren.elementAt(i);
      if (i > 0)
        buf.append(", ");
      if ((nt == OP_EXISTS) || (nt == OP_FORALL) || (c.priority() < priority()))
        buf.append('(');
      buf.append(c);
      if ((nt == OP_EXISTS) || (nt == OP_FORALL) || (c.priority() < priority())) 
        buf.append(')');
    }

    buf.append(')');

    return buf.toString();
  }

  public void print()
  {
    int nt = nodeType();
    if (myChildren.size() == 0) {
      if ((nt == OP_RELATION) || (nt == OP_OR))
        System.out.print("FALSE");
      else if (nt == OP_AND)
        System.out.print("TRUE");
      else
        olAssert(false);
    }

    int l = myChildren.size();
    for (int i = 0; i < l; i++) {
      Formula c = (Formula) myChildren.elementAt(i);
      if (i > 0)
        printSeparator();
      if ((nt == OP_EXISTS) || (nt == OP_FORALL) || (c.priority() < priority()))
        System.out.print(" ( ");
      c.print();
      if ((nt == OP_EXISTS) || (nt == OP_FORALL) || (c.priority() < priority()))
        System.out.print(")");
    }
  }

  public void prefixPrint(boolean debug)
  {
    int l = myChildren.size();
    for (int i = 0; i < l; i++) {
      Formula c = (Formula) myChildren.elementAt(i);
      c.prefixPrint(debug);
    }
  }

  public void printSeparator()
  {
    olAssert(false);    // should never be called, it's only for derived classes
  }
}
