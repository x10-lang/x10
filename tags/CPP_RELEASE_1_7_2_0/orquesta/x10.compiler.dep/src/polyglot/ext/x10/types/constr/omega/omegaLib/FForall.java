package polyglot.ext.x10.types.constr.omega.omegaLib;

import java.util.*;

/**
 * FForall.
 * <p>
 * $Id: FForall.java,v 1.1 2008-03-06 18:02:23 nystrom Exp $
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
public class FForall extends FDeclaration
{
  public FForall(OmegaLib omegaLib, Formula f, RelBody b)
  {
    super(omegaLib, f, b);
  }

  public int nodeType()
  {
    return OP_FORALL;
  }

  public VarDecl declare(String s)
  {
    return doDeclare(s, VarDecl.FORALL_VAR);
  }

  public VarDecl declare()
  {
    return doDeclare(null, VarDecl.FORALL_VAR);
  }

  public VarDecl declare(VarDecl v)
  {
    return doDeclare(v.baseName(), VarDecl.FORALL_VAR);
  }

  public Formula copy(Formula parent, RelBody reln)
  {
    FForall f = new FForall(omegaLib, parent, reln);
    int cl = myChildren.size();
    for (int i = 0; i < cl; i++) {
      Formula c = (Formula) myChildren.elementAt(i);
      f.addChild(c.copy(f, reln));
    }

    f.myLocals = VarDecl.copyVarDecls(myLocals);
    VarDecl.resetRemapField(myLocals);

    return f;
  }

  public Conjunct findAvailableConjunct()
  {
    return null;
  }

  public void beautify()
  {
    super.beautify();
    olAssert(myChildren.size() == 1);

    if (myLocals.size() == 0) {
      parent().removeChild(this);
      parent().checkAndAddChild((Formula) myChildren.remove(0));
      delete();
    }
  }

  /**
   * Convert a universal quantifier to "not exists not".
   * Forall v: f = ~ (Exists v: ~ f)
   */
  public void rearrange()
  {
    Formula p = parent();
    p.removeChild(this);
  
    FNot topnot = p.addNot();
    FExists exist = topnot.addExists();
    int l = myLocals.size();
    for (int i = 0; i < l; i++) {
      VarDecl v = (VarDecl) myLocals.elementAt(i);

      v.setKind(VarDecl.EXISTS_VAR);
    }
    exist.myLocals.addAll(myLocals);

    FNot botnot = exist.addNot();
    Formula f = (Formula) myChildren.remove(0);
    botnot.checkAndAddChild(f);

    delete();

    botnot.rearrange();
  }

  public DNF DNFize()
  {
    olAssert(false);
    return(null);
  }

  public void print()
  {
    System.out.print("forall ");
    super.print();
  }

  public void prefixPrint(boolean debug)
  {
    omegaLib.incrementPrintLevel();
    omegaLib.printHead();
    System.out.print("FORALL [");
    super.prefixPrint(debug);
    omegaLib.decrementPrintLevel();
  }
}
