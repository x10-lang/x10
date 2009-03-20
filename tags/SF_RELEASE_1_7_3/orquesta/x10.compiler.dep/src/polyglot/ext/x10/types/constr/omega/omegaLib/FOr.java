package polyglot.ext.x10.types.constr.omega.omegaLib;

import java.util.Stack;
import java.util.*;

/**
 * FOr.
 * <p>
 * $Id: FOr.java,v 1.1 2008-03-06 18:02:23 nystrom Exp $
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
public class FOr extends Formula
{
  public int nodeType()
  {
    return OP_OR;
  }

  public FOr(OmegaLib omegaLib, Formula f, RelBody b)
  {
    super(omegaLib, f, b);
  }

  public Formula copy(Formula parent, RelBody reln)
  {
    FOr f = new FOr(omegaLib, parent, reln);
    int cl = myChildren.size();
    for (int i = 0; i < cl; i++) {
      Formula c = (Formula) myChildren.elementAt(i);
      f.addChild(c.copy(f, reln));
    }
    return f;
  }

  public Conjunct findAvailableConjunct()
  {
    return null;
  }

  /**
   * The Pix-free versions of beautify for And and Or are a bit
   * less efficient  than the previous code,  as we keep moving
   * things from one list to another, but they do not depend on
   * knowing that a Pix is valid after the list is updated, and
   * they can always be optimized later if necessary.
   */
  public void beautify() 
  {
    super.beautify();

    Stack uglies   = new Stack();
    Stack beauties = new Stack();

    Vector ch = myChildren;
    while (true) {
      int l = ch.size();
      if (l == 0)
        break;
      uglies.push(ch.remove(l - 1));
    }
    olAssert(myChildren.size() == 0);

    int l = uglies.size();
    for (int i = 0; i < l; i++) {
      Formula c = (Formula) uglies.elementAt(i);
      c.setParent(null);
    }

    while (!uglies.empty()) {
      Formula f    = (Formula) uglies.pop();
      int     type = f.nodeType();

      if ((f.nodeType() == OP_AND) && (f.myChildren.size() == 0)) {
        // smth | true = true
        parent().removeChild(this);
        parent().addAnd();
        f.delete();
        delete();
        return;
      }

      if (f.nodeType() == OP_OR) {
        // OR(f[1-m], OR(c[1-n])) = OR(f[1-m], c[1-n])

        Vector v  = f.myChildren;
        int    cl = v.size();
        for (int i = 0; i < cl; i++) {
          Formula c = (Formula) v.elementAt(i);
          c.setParent(null);
          uglies.push(c);
        }
        f.delete();
        continue;
      }

      beauties.push(f);
    }

    if (beauties.size() == 1) {
      Formula c = (Formula) beauties.pop();
      c.setParent(parent());
      parent().removeChild(this);
      parent().checkAndAddChild(c);
      delete();
      return;
    }

    olAssert(myChildren.size() == 0);
    while (!beauties.empty()) {
      Formula c = (Formula) beauties.pop();
      c.setParent(this);
      c.setRelation(relation());
      myChildren.addElement(c);
    }
  }

  public int priority()
  {
    return 0;
  }

  /**
   * or is almost in DNF already.
   */
  public DNF DNFize()
  {
    DNF new_dnf = new DNF(omegaLib);
    boolean  empty_or = true;

    while (myChildren.size() != 0) {
      DNF c_dnf = ((Formula) myChildren.remove(0)).DNFize();
      new_dnf.joinDNF(c_dnf);
      empty_or = false; 
    }

    delete();

    if (omegaLib.trace) {
      System.out.println("=== F_OR::DNFize() OUT ===");
      new_dnf.prefixPrint();
    }
    return(new_dnf);
  }

  protected void pushExists(Vector S)
  {
    Vector mc = new Vector();
    mc.addAll(myChildren);

    while (mc.size() > 0) {
      Formula f = (Formula) mc.remove(0);
      FExists e = addExists();

      e.myLocals = VarDecl.copyVarDecls(S);
      f.remap();
      VarDecl.resetRemapField(S);

      e.checkAndAddChild(f);
    }

    // Since these are not reused, they have to be deleted

    int l = S.size();
    for (int i = 0; i < l; i++) {
      VarDecl v = (VarDecl) S.elementAt(i);
      olAssert(v.kind() == VarDecl.EXISTS_VAR);
    }
    S.clear();
  }

  public void printSeparator()
  {
    System.out.print(" or ");
  }

  public void prefixPrint(boolean debug)
  {
    omegaLib.incrementPrintLevel();
    omegaLib.printHead();
    System.out.print("OR");
    super.prefixPrint(debug);
    omegaLib.decrementPrintLevel();
  }
}
