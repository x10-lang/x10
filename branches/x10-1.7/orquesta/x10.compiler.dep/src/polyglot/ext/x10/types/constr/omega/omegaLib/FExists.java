package polyglot.ext.x10.types.constr.omega.omegaLib;

import java.util.*;

/**
 * FExists.
 * <p>
 * $Id: FExists.java,v 1.1 2008-03-06 18:02:23 nystrom Exp $
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
 */
public class FExists extends FDeclaration
{
  public FExists(OmegaLib omegaLib, Formula f, RelBody b)
  {
    super(omegaLib, f, b);
  }

  public FExists(OmegaLib omegaLib, Formula f, RelBody b, Vector vid)
  {
    super(omegaLib, f, b, vid);
  }

  public int nodeType()
  {
    return OP_EXISTS;
  }

  public VarDecl declare(String s)
  {
    return doDeclare(s, VarDecl.EXISTS_VAR);
  }

  public VarDecl declare()
  {
    return doDeclare(null, VarDecl.EXISTS_VAR);
  }

  public VarDecl declare(VarDecl v)
  {
    return doDeclare(v.baseName(), VarDecl.EXISTS_VAR);
  }

  protected void pushExists(Vector S)
  {
    myLocals.addAll(S);
  }

  public Formula copy(Formula parent, RelBody reln)
  {
    FExists f = new FExists(omegaLib, parent, reln);
    f.myLocals = VarDecl.copyVarDecls(myLocals);
    int cl = myChildren.size();
    for (int i = 0; i < cl; i++) {
      Formula c = (Formula) myChildren.elementAt(i);
      f.addChild(c.copy(f, reln));
    }
    VarDecl.resetRemapField(myLocals);
    return f;
  }

  public Conjunct findAvailableConjunct()
  {
    olAssert((myChildren.size() == 1) || (myChildren.size() == 0));
    if (myChildren.size() == 0)
      return null;

    return ((Formula) myChildren.elementAt(0)).findAvailableConjunct();
  }

  public void beautify()
  {
    super.beautify();
    olAssert(myChildren.size() == 1);

    if (myLocals.size() == 0) {
      parent().removeChild(this);
      parent().checkAndAddChild((Formula) myChildren.remove(0));
      delete();
    } else {
      Formula child = (Formula) myChildren.elementAt(0);
      if ((child.nodeType() == OP_CONJUNCT) || (child.nodeType() == OP_EXISTS)) {
        child.pushExists(myLocals);
        parent().removeChild(this);
        parent().checkAndAddChild(child);
        myChildren.remove(0);
        delete();
      }
    }
  }

  /**
   * Exists <code>v: (f1 | &hellip; | fn) = (Exists v: f1) | &hellip; | (Exists v: fn)</code>.
   */
  public void rearrange()
  {
    Formula child = (Formula) myChildren.elementAt(0);
    switch(child.nodeType()) {
    case OP_OR:
    case OP_CONJUNCT:
    case OP_EXISTS:
      child.pushExists(myLocals);
      parent().removeChild(this);
      parent().checkAndAddChild(child);
      myChildren.remove(0);
      delete();
      break;
    default:
      break;
    }
  }

  /**
   * exists <code>x : (c1 v c2 v &hellip;) -. (exists x : c1) v (exists x : c2) v &hellip;</code>.
   */
  public DNF DNFize()
  {
    DNF dnf = ((Formula) myChildren.remove(0)).DNFize();

    dnf.DNFizeH(myLocals);
    delete();

    if (omegaLib.trace) {
      System.out.println("=== F_EXISTS::DNFize() OUT ===");
      dnf.prefixPrint();
    }
    return dnf;
  }

  /**
   * Exists <code>v: (f1 | &hellip; | fn) = (Exists v: f1) | &hellip; | (Exists v: fn)</code>.
   */
  public void print()
  {
    System.out.println("exists ");
    super.print();
  }

  public void prefixPrint(boolean debug)
  {
    omegaLib.incrementPrintLevel();
    omegaLib.printHead();
    System.out.println("EXISTS [");
    super.prefixPrint(debug);
    if (omegaLib.trace) {
      int l = myLocals.size();
      for (int i = 0; i < l; i++) {
        VarDecl v = (VarDecl) myLocals.elementAt(i);
        olAssert(v.kind() == VarDecl.EXISTS_VAR);
      }
    }
    omegaLib.decrementPrintLevel();
  }
}
