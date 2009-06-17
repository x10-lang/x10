package polyglot.ext.x10.types.constr.omega.omegaLib;

import java.util.*;

/**
 * FDeclaration.
 * <p>
 * $Id: FDeclaration.java,v 1.1 2008-03-06 18:02:23 nystrom Exp $
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
 * <blockquote cite="http://www.cs.umd.edu/projects/omega">
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
 */
public abstract class FDeclaration extends Formula
{
  protected Vector myLocals;

  public FDeclaration(OmegaLib omegaLib, Formula f, RelBody r)
  {
    super(omegaLib, f, r);
    myLocals = new Vector();
  }

  public FDeclaration(OmegaLib omegaLib, Formula f, RelBody r, Vector vid)
  {
    super(omegaLib, f, r);
    myLocals = (Vector) vid.clone();
  }

  public VarDecl declare(String s)
  {
    olAssert(false);  // must be declared in forall, exists, or conjunct
    return null;
  }

  public VarDecl doDeclare(String s, int var_kind)
  {
    olAssert(var_kind != VarDecl.GLOBAL_VAR);

    VarDecl v;

    if (s != null) {
      v = new VarDecl(s, var_kind, 0);
    } else {
      v = new VarDecl(var_kind, 0);
    }

    myLocals.addElement(v);

    return v;
  }

  public abstract VarDecl declare();

  public abstract VarDecl declare(VarDecl v);

  public Vector declareTuple(int size)
  {
    int first = myLocals.size() + 1;

    for (int i = 1 ; i <= size; i++)
      declare();

    Vector n = new Vector();
    for (int i = first; i < first + size; i++)
     n.addElement(myLocals.elementAt(i));

    return n;
  }

  public Vector locals()
  {
    return myLocals;
  }

  public void setupNames()
  {
    if (false && omegaLib.trace)
      System.out.println("Setting up names for Declaration 0x" + Integer.toHexString(hashCode()));

    // Allow re-use of wc names in other scopes
    int wcin = omegaLib.wildCardInstanceNumber;

    int l = myLocals.size();
    for (int i = 0; i < l; i++) {
      VarDecl v = (VarDecl) myLocals.elementAt(i);
      if (v.hasName())
        omegaLib.increment(v);
      else
        v.setInstance(wcin++);
    }

    int cl = myChildren.size();
    for (int i = 0; i < cl; i++) {
      Formula c = (Formula) myChildren.elementAt(i);
      c.setupNames();
    }

    for (int i = 0; i < l; i++)
      omegaLib.decrement((VarDecl) myLocals.elementAt(i));

  }

  protected void setupAnonymousWildcardNames()
  {
    int l = myLocals.size();
    for (int i = 0; i < l; i++) {
      VarDecl v = (VarDecl) myLocals.elementAt(i);
      if (!v.hasName())
        v.setInstance(omegaLib.wildCardInstanceNumber++);
    }
  }

  public boolean canAddChild()
  {
    return numberOfChildren() < 1;
  }

  public int priority()
  {
    return 3;
  }

  public void print()
  {
    StringBuffer s = new StringBuffer("");
    int l = myLocals.size();
    for (int i = 0; i < l; i++) {
      VarDecl v = (VarDecl) myLocals.elementAt(i);
      if (i > 0)
        s.append(',');
      s.append(v.name(omegaLib));
    }
    System.out.println(" ( " + s + " : ");
    super.print();
    System.out.println(")");
  }

  public void prefixPrint(boolean debug)
  {
    StringBuffer s = new StringBuffer("[");
    int l = myLocals.size();
    for (int i = 0; i < l; i++) {
      VarDecl v = (VarDecl) myLocals.elementAt(i);
      if (i > 0)
        s.append(',');
      s.append(v.name(omegaLib));
//        v.printVarAddrs(s);
    }
    s.append("]");
    System.out.println(s);
    super.prefixPrint(debug);
  }
}
