package polyglot.ext.x10.types.constr.omega.omegaLib;

import java.util.*;

/**
 * FNot.
 * <p>
 * $Id: FNot.java,v 1.1 2008-03-06 18:02:23 nystrom Exp $
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
public class FNot extends Formula
{
  public int nodeType()
  {
    return OP_NOT;
  }

  public FNot(OmegaLib omegaLib, Formula f, RelBody r)
  {
    super(omegaLib, f, r);
  }

  public Formula copy(Formula parent, RelBody reln)
  {
    FNot f = new FNot(omegaLib, parent, reln);
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

  public boolean canAddChild()
  {
    return numberOfChildren() < 1;
  }

  public void beautify()
  {
    super.beautify();
    olAssert(myChildren.size() == 1);
    Formula child = (Formula) myChildren.elementAt(0);

    if ((child.nodeType() == OP_AND) && (child.myChildren.size() == 0)) {
      // Not TRUE = FALSE
      parent().removeChild(this);
      parent().addOr();
      delete();
    } else if ((child.nodeType() == OP_OR) && (child.myChildren.size() == 0)) {
      // Not FALSE = TRUE
      parent().removeChild(this);
      parent().addAnd();
      delete();
    }
  }

  /*
   * Push nots down the tree until quantifier or conjunct, rearrange kids
   */
  public void rearrange()
  {
    Formula child = (Formula) myChildren.elementAt(0);
    Formula new_child;
    Formula f;

    switch (child.nodeType()) {
    case OP_OR:
      parent().removeChild(this);
      new_child = parent().addAnd();
      while (child.myChildren.size() != 0) {
        f = (Formula) child.myChildren.remove(0); 
        FNot new_not = new_child.addNot(); 
        new_not.checkAndAddChild(f); 
      }
      delete();
      break;
      //case OP_AND:
      //  parent().removeChild(this);
      //  new_child = parent().addOr();
      //  while (!child.myChildren.empty()) {
      //    f = child.myChildren.remove(0); 
      //    FNot *new_not = new_child.addNot(); 
      //    new_not.checkAndAddChild(f); 
      //  }
      //  delete this;
      //  break;
    case OP_NOT:
      parent().removeChild(this);
      f = (Formula) child.myChildren.remove(0); 
      parent().checkAndAddChild(f);
      delete();
      f.rearrange();
      return;
    default:
      new_child = child;
      break;
    }

    new_child.rearrange();
  }

  public int priority()
  {
    return 2;
  }

  public DNF DNFize()
  {
    Conjunct positive_conjunct = new Conjunct(omegaLib, null, relation());  
    DNF      neg_conjs = ((Formula) myChildren.remove(0)).DNFize();

    delete();

    DNF new_dnf = neg_conjs.conjAndNotDnf(positive_conjunct, false);

    if (omegaLib.trace) {
      System.out.println("=== F_NOT::DNFize() OUT ===");
      new_dnf.prefixPrint();
    }
    return new_dnf;
  }

  public void print()
  {
    System.out.print(" not ");
    super.print();
  }

  public void prefixPrint(boolean debug)
  {
    omegaLib.incrementPrintLevel();
    omegaLib.printHead();
    System.out.print("NOT");
    super.prefixPrint(debug);
    omegaLib.decrementPrintLevel();
  }
}
