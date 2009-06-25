package polyglot.ext.x10.types.constr.omega.omegaLib;

import java.util.Stack;
import java.util.*;

/**
 * FAnd.
 * <p>
 * $Id: FAnd.java,v 1.1 2008-03-06 18:02:23 nystrom Exp $
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
public class FAnd extends Formula
{
  private Conjunct pos_conj;

  public FAnd(OmegaLib omegaLib, Formula p, RelBody r)
  {
    super(omegaLib, p, r);
    pos_conj = null;
  }

  public int nodeType()
  {
    return OP_AND;
  }

  /**
   * "preserves level" should be false unless we know this will not
   * change the "level" of the constraints - ie the number of
   * leading corresponding in,out variables known to be equal
   */
  public GEQHandle addGEQ(boolean preserves_level)
  {
    assertNotFinalized();
    if (pos_conj == null) {
      int l = myChildren.size();
      for (int i = 0; i < l; i++) {
        Formula c = (Formula) myChildren.elementAt(i);
        if (c.nodeType() == OP_CONJUNCT) {
          pos_conj = c.reallyConjunct();
          break;
        }
      }
      if (pos_conj == null)
        pos_conj = addConjunct(); // FERD -- set level if preserved?
    }
    return pos_conj.addGEQ(preserves_level);
  }

  public EQHandle addEQ(boolean preserves_level)
  {
    assertNotFinalized();
    if (pos_conj == null) {
      int l = myChildren.size();
      for (int i = 0; i < l; i++) {
        Formula c = (Formula) myChildren.elementAt(i);
        if (c.nodeType() == OP_CONJUNCT) {
          pos_conj = c.reallyConjunct();
          break;
        }
      }
      if (pos_conj == null)
        pos_conj = addConjunct(); //FERD-set level info if preserved?
    }
    return pos_conj.addEQ(preserves_level);
  }

  public EQHandle addStride(int step, boolean preserves_level)
  {
    assertNotFinalized();
    if (pos_conj == null) {
      int l = myChildren.size();
      for (int i = 0; i < l; i++) {
        Formula c = (Formula) myChildren.elementAt(i);
        if (c.nodeType() == OP_CONJUNCT) {
          pos_conj = c.reallyConjunct();
          break;
        }
      }
      if (pos_conj == null)
        pos_conj = addConjunct();  // FERD -set level if preserved?
    }
    return pos_conj.addStride(step, preserves_level);
  }

  public EQHandle addEQ(ConstraintHandle constraint, boolean preserves_level)
  {
    assertNotFinalized();
    if (pos_conj == null) {
      int l = myChildren.size();
      for (int i = 0; i < l; i++) {
        Formula c = (Formula) myChildren.elementAt(i);
        if (c.nodeType() == OP_CONJUNCT) {
          pos_conj = c.reallyConjunct();
          break;
        }
      }
      if (pos_conj == null)
        pos_conj = addConjunct();//FERD-set level info if preserved?
    }
    return pos_conj.addEQ(constraint, preserves_level);
  }

  public GEQHandle addGEQ(ConstraintHandle constraint, boolean preserves_level)
  {
    assertNotFinalized();
    if (pos_conj == null) {
      int l = myChildren.size();
      for (int i = 0; i < l; i++) {
        Formula c = (Formula) myChildren.elementAt(i);
        if (c.nodeType() == OP_CONJUNCT) {
          pos_conj = c.reallyConjunct();
          break;
        }
      }
      if (pos_conj == null)
        pos_conj = addConjunct();// FERD -- set level if preserved?
    }
    return pos_conj.addGEQ(constraint, preserves_level);
  }

  public FAnd andWith()
  {
    assertNotFinalized();
    olAssert(canAddChild());
    return this;
  }

  public void addUnknown()
  {
    assertNotFinalized();
    if (pos_conj == null) {
      int l = myChildren.size();
      for (int i = 0; i < l; i++) {
        Formula c = (Formula) myChildren.elementAt(i);
        if (c.nodeType()==OP_CONJUNCT) {
          pos_conj = c.reallyConjunct();
          break;
        }
      }
      if (pos_conj == null)
        pos_conj = addConjunct(); // FERD - set level if preseved?
    }
    pos_conj.makeInexact();
  }

  public Formula copy(Formula parent, RelBody reln)
  {
    FAnd f = new FAnd(omegaLib, parent, reln);
    int  l = myChildren.size();
    for (int i = 0; i < l; i++) {
      Formula c = (Formula) myChildren.elementAt(i);
      f.addChild(c.copy(f, reln));
    }
    return f;
  }

  public Conjunct findAvailableConjunct()
  {
    int l = myChildren.size();
    for (int i = 0; i < l; i++) {
      Formula  child = (Formula) myChildren.elementAt(i);
      Conjunct c     = child.findAvailableConjunct();
      if (c != null)
        return c;
    }
    return null;
  }

  public int priority()
  {
    return 1;
  }

  public void beautify()
  {
    super.beautify();

    Conjunct conj = null;

    Stack uglies   = new Stack();
    Stack beauties = new Stack();

    Vector ch = children();
    while (true) {
      int l = ch.size();
      if (l == 0)
        break;
      uglies.push(ch.remove(l - 1));
    }
    olAssert(children().size() == 0);

    while (!uglies.empty()) {
      Formula f    = (Formula) uglies.pop();
      int     type = f.nodeType();

      if (type == OP_CONJUNCT) {
        if (conj == null)
          conj = f.reallyConjunct();
        else {
          Conjunct conj1 = conj.mergeConjuncts(f.reallyConjunct(), Conjunct.MERGE_REGULAR, null);
          conj.delete();
          f.delete();
          conj = conj1;
          
        }
        continue;
      }

      if (type == OP_AND) {
        //  AND(f[1-m], AND(c[1-n])) = AND(f[1-m], c[1-n])
        Vector fc  = f.children();
        int    fcl = fc.size();
        for (int i = 0; i < fcl; i++)
          uglies.push(fc.elementAt(i));
        f.delete();
        continue;
      }

      if ((type == OP_OR) && (f.children().size() == 0)) {
        // smth & false = false
        parent().removeChild(this);
        parent().addOr();
        f.delete();
        conj.delete();
        delete();
        return;
      }

      beauties.push(f);
    }

    if (conj != null)
      beauties.push(conj);

    if (beauties.size() == 1) {
      Formula c = (Formula) beauties.pop();
      c.setParent(parent());
      parent().removeChild(this);
      parent().checkAndAddChild(c);
      delete();
      return;
    }

    olAssert(children().size() == 0);
    while (!beauties.empty()) {
      Formula c = (Formula) beauties.pop();
      c.setParent(this);
      c.setRelation(relation());
      children().addElement(c);
    }
  }

  public void printSeparator()
  {
    System.out.print(" and ");
  }

  /**
   * Try to separate positive and negative clauses below the AND,
   * letting us use the techniques described in Pugh & Wonnacott:
   * "An Exact Method for Value-Based Dependence Analysis"
   */
  public DNF DNFize()
  {
    Conjunct positive_conjunct = null;
    DNF      neg_conjs         = new DNF(omegaLib);
    Vector   pos_dnfs          = new Vector();
    DNF      new_dnf           = new DNF(omegaLib);
    boolean  JustReturnDNF     = false;

    omegaLib.useUglyNames++;

    if (omegaLib.trace) {
      System.out.println("\nFAnd:: DNFize [");
      prefixPrint(true);
    }

    if (myChildren.size() == 0) {
      Conjunct c = new Conjunct(omegaLib, null, relation());
      new_dnf.addConjunct(c);
    } else  {
      while (myChildren.size() != 0) {
        Formula carg = (Formula) myChildren.remove(0);
        if (carg.nodeType() == OP_NOT) {
          // DNF1 & ~DNF2 . DNF
          DNF dnf = ((Formula) carg.myChildren.remove(0)).DNFize();
          neg_conjs.joinDNF(dnf);       // negative conjunct
          carg.delete();
        } else {
          // DNF1 & DNF2 . DNF
          DNF dnf = carg.DNFize();
          int dl  = dnf.length();
          if (dl == 0) {
            // DNF & false . false
            delete();
            JustReturnDNF = true;
            break;
          } else if (dl == 1) {
            // positive conjunct
            Conjunct conj = dnf.removeFirstConjunct();
            dnf.delete();
            if (positive_conjunct == null) {
              positive_conjunct = conj;
            } else {
              Conjunct new_conj = positive_conjunct.mergeConjuncts(conj, Conjunct.MERGE_REGULAR, null);
              conj.delete();
              positive_conjunct.delete();
              positive_conjunct = new_conj;
            }
          } else { // positive DNF
            pos_dnfs.addElement(dnf);
          }
        }
      }

      if (!JustReturnDNF) {
        RelBody my_relation = relation();
        delete();

        // If we have a positive_conjunct, it can serve as the 1st arg to
        // conjAndNotDnf.  Otherwise, if pos_dnfs has one DNF,
        // use each conjunct there for this purpose.
        // Only pass "true" here if there is nothing else to try,
        // as long as TRY_TO_AVOID_TRUE_AND_NOT_DNF is set.
        //
        // Perhaps we should even try to and multiple DNF's?

        if ((positive_conjunct == null) && (pos_dnfs.size() == 1)) {
          if (omegaLib.trace) {
            System.out.println("--- F_AND::DNFize() Single pos_dnf:");
            ((DNF) pos_dnfs.elementAt(0)).prefixPrint();
            System.out.println("--- F_AND::DNFize() vs neg_conjs:");
            neg_conjs.prefixPrint();
          }

          DNF realNegConjs = neg_conjs.realNegConjs();
          neg_conjs.delete();
          neg_conjs = realNegConjs;

          new_dnf.join_conjAndNotDnf((DNF) pos_dnfs.elementAt(0), neg_conjs);
        } else if ((positive_conjunct == null) && neg_conjs.isDefinitelyFalse()) {
          new_dnf = (DNF) pos_dnfs.elementAt(0);
          pos_dnfs.setElementAt(null, 0);
          int pll = pos_dnfs.size();
          for (int j = 0; j < pll; j++) {
            DNF pos_dnf = (DNF) pos_dnfs.elementAt(j);
            new_dnf = new_dnf.DNFandDNF(pos_dnf);
            pos_dnfs.setElementAt(null, j);
          }
        } else {
          if (positive_conjunct == null) {              
            if (omegaLib.trace) {
              System.out.println("Uh-oh: F_AND::DNFize() resorting to TRUE and not DNF");
              System.out.println("--- F_AND::DNFize() neg_conjs");
              neg_conjs.prefixPrint();
              System.out.println("--- F_AND::DNFize() pos_dnfs:");
              int pll = pos_dnfs.size();
              for (int j = 0; j < pll; j++) {
                DNF pos_dnf = (DNF) pos_dnfs.elementAt(j);
                pos_dnf.prefixPrint();
                System.out.println("---- --");
              }
            }
            positive_conjunct = new Conjunct(omegaLib, null, my_relation);
          }
        
          if (!neg_conjs.isDefinitelyFalse()) {
            new_dnf.joinDNF(neg_conjs.conjAndNotDnf(positive_conjunct, false));
            neg_conjs = null;
          } else {
            new_dnf.addConjunct(positive_conjunct);
          }

          positive_conjunct = null;

          //
          // AND it with positive DNFs
          //
          if (omegaLib.trace) {
            System.out.println("--- F_AND::DNFize() pos_dnfs:");
            int pll = pos_dnfs.size();
            for (int j = 0; j < pll; j++) {
              DNF pos_dnf = (DNF) pos_dnfs.elementAt(j);
              pos_dnf.prefixPrint();
            }
          }
          int pll = pos_dnfs.size();
          for (int j = 0; j < pll; j++) {
            DNF pos_dnf = (DNF) pos_dnfs.elementAt(j);
            new_dnf = new_dnf.DNFandDNF(pos_dnf);
System.out.println("--- F_AND::DNFize() new_dnf merging:"); new_dnf.prefixPrint();
            pos_dnfs.setElementAt(null, j);
          }
        }
      }
    }

    if (positive_conjunct != null)
        positive_conjunct.delete();
    if (neg_conjs != null)
        neg_conjs.delete();
    int pll = pos_dnfs.size();
    for (int j = 0; j < pll; j++) {
      DNF pos_dnf = (DNF) pos_dnfs.elementAt(j);
      if (pos_dnf != null)
        pos_dnf.delete();
    }

    if (omegaLib.trace) {
      System.out.println("] F_AND::DNFize() OUT ");
      new_dnf.prefixPrint();
    }

    omegaLib.useUglyNames--;

    return new_dnf;
  }

  public void prefixPrint(boolean debug)
  {
    omegaLib.incrementPrintLevel();
    omegaLib.printHead();
    System.out.println("AND");
    super.prefixPrint(debug);
    omegaLib.decrementPrintLevel();
  }
}
