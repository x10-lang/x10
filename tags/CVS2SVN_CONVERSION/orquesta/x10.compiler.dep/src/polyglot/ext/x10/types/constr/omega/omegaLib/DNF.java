package polyglot.ext.x10.types.constr.omega.omegaLib;

import java.util.*;

/**
 * DNF.
 * <p>
 * $Id: DNF.java,v 1.1 2008-03-06 18:02:23 nystrom Exp $
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
public final class DNF
{
  private static int createdCount = 0; /* A count of all the instances of this class that were created. */
  private static int maxSize = 0; /* The maximum number of Conjuncts in the list. */

  public static int created()
  {
    return createdCount;
  }

  public static int maxSize()
  {
    return maxSize;
  }

  private static final int no_u  = 1;
  private static final int and_u = 2;
  private static final int or_u  = 4;

  private OmegaLib omegaLib;
  private Vector   conjList; // List of Conjunct.
  public  int      id;

  public DNF(OmegaLib omegaLib)
  {
    this.omegaLib = omegaLib;
    this.conjList = new Vector();
    this.id       = createdCount++;
  }

  public DNF copy(OmegaLib omegaLib, RelBody rel_body)
  {
    DNF new_dnf = new DNF(omegaLib);
    int l       = conjList.size();
    for (int i = 0; i < l; i++) {
      Conjunct conj = (Conjunct) conjList.elementAt(i);
      new_dnf.addConjunct((Conjunct) conj.copy(rel_body, rel_body));
    }
    return new_dnf;
  }

  public void delete()
  {
  }

  public String toString()
  {
    StringBuffer buf = new StringBuffer("(DNF ");
    buf.append(id);
    buf.append(' ');
    buf.append(conjList);
    buf.append(')');
    return buf.toString();
  }

  public void print()
  {
    int     l    = conjList.size();
    boolean live = false;

    for (int i = 0; i < l; i++) {
      Conjunct p = (Conjunct) conjList.elementAt(i);
      if (live)
        System.out.println(" OR ");
      p.print();
      live = true;
    }

    if (!live)
      System.out.println("FALSE");
  }

  public String printToString()
  {
    StringBuffer s = new StringBuffer("");
    int     l    = conjList.size();

    for (int i = 0; i < l; i++) {
      Conjunct c = (Conjunct) conjList.elementAt(i);
      if (i > 0)
        s.append(" && ");
      s.append(c.printToString(true));
    }
    return s.toString();
  }

  public void prefixPrint()
  {
    prefixPrint(true, false);
  }

  public void prefixPrint(boolean debug, boolean parent_names_setup)
  {
    int l = conjList.size();
    omegaLib.wildCardInstanceNumber = 0;
    Vector all_vars = new Vector();
    if ((0 == omegaLib.useUglyNames) && !parent_names_setup) {
      // We need to manually set up all of the input,output, and symbolic 
      // variables, since during simplification, a dnf's conjuncts may not 
      // be listed as part of a relation, or perhaps as part of different
      // relations (?) (grr).
      for (int i = 0; i < l; i++) {
        Conjunct conj = (Conjunct) conjList.elementAt(i);
        conj.extractNonWildVars(all_vars);
        conj.setupNames();
      }

      int al = all_vars.size();
      for (int k = 0; k < al; k++) {
        VarDecl v = (VarDecl) all_vars.elementAt(k);
        if (v.hasName())
          omegaLib.increment(v);
        else
          v.setInstance(omegaLib.wildCardInstanceNumber++);
      }
    }

    omegaLib.setPrintLevel(0);
    for (int j = 0; j < l; j++) {
      Conjunct conj = (Conjunct) conjList.elementAt(j);
      System.out.print("#");
      System.out.print(j + 1);
      System.out.print(" ");
      conj.prefixPrint(debug);
    }

    int al = all_vars.size();
    for (int k = 0; k < al; k++)
      omegaLib.decrement((VarDecl) all_vars.elementAt(k));
    
    System.out.println("");
  }

  public boolean isDefinitelyFalse()
  {
    return conjList.size() == 0;
  }

  public boolean isDefinitelyTrue()
  {
    return (hasSingleConjunct() && getSingleConjunct().isTrue());
  }

  public int length()
  {
    return conjList.size();
  }

  public Vector getConjList()
  {
    return conjList;
  }

  public Conjunct getSingleConjunct()
  {
    olAssert(conjList.size() == 1);
    return (Conjunct) conjList.elementAt(0);
  }

  public boolean hasSingleConjunct()
  {
    return (conjList.size() == 1);
  }

  public Conjunct removeFirstConjunct()
  {
    if (conjList.size() == 0)
      return null;

    return (Conjunct) conjList.remove(0);
  }

  public void clear()
  {
    conjList.clear();
  }

  /**
   * Return x such that for all conjuncts c, c has <code>&gt;= x</code> leading 0s.
   * If it is a set, always returns -1. The argument specifies if it is a set or relation.
   */
  public int queryGuaranteedLeadingZeros(int what_to_return_for_empty_dnf)
  {
    countLeadingZeros();
    int     result = what_to_return_for_empty_dnf; // if set, -1; if rel, 0
    boolean first = true;

    int l = conjList.size();
    for (int i = 0; i < l; i++) {
      Conjunct conj = (Conjunct) conjList.elementAt(i);
      int      tmp  = conj.queryGuaranteedLeadingZeros();

      olAssert((tmp >= 0) || (conj.relation().isSet() && (tmp == -1)));

      if (first || (tmp < result))
        result = tmp;
      first = false;
    }
    
    return result;
  }

  /**
   * Return x such that for all conjuncts c, c has <code>&lt;= x</code> leading 0s.
   * If no conjuncts, return the argument.
   */
  public int queryPossibleLeadingZeros(int numberInputput_and_output)
  {
    countLeadingZeros();
    int result = numberInputput_and_output;
    boolean first = true;

    int l = conjList.size();
    for (int i = 0; i < l; i++) {
      Conjunct conj = (Conjunct) conjList.elementAt(i);
      int      tmp  = conj.queryPossibleLeadingZeros();

      olAssert((tmp >= 0) || ((tmp == -1) && conj.relation().isSet()));

      if (first || (tmp > result))
        result = tmp;
      first = false;
      }

    return result;
  }

  public int queryLeadingDir()
  {
    countLeadingZeros();
    int result = 0;
    boolean first = true;

    int l = conjList.size();
    for (int i = 0; i < l; i++) {
      Conjunct conj = (Conjunct) conjList.elementAt(i);
      int      glz  = conj.queryGuaranteedLeadingZeros();
      int      plz  = conj.queryPossibleLeadingZeros();
      int      rlz  = 0;

      if (glz != plz)
        return 0;

      if (first) {
        rlz = glz;
        result = conj.queryLeadingDir();
        first = false;
      } else
        if ((glz != rlz) || (result != conj.queryLeadingDir()))
          return 0;
    }

    return result;
  }

  /**
   * Simplify all conjuncts in a DNF
   */
  public void simplify(int sRdtConstraints)
  {
    int l = conjList.size();
    for (int i = 0; i < l; i++) {
      Conjunct conj = (Conjunct) conjList.elementAt(i);
      System.out.println("conj:");
      conj.prefixPrint();
      if ((sRdtConstraints >= 0) && !conj.simplifyConjunct(true, sRdtConstraints, Problem.BLACK)) {
        removeConjunct(conj);
        l--;
        i--;
      }
    }
  }

  /**
   * Add level-carried DNF form out to level "level".
   */
  public void makeLevelCarriedTo(int level)
  {
    countLeadingZeros();

    if (length() > 0) {
      RelBody bodys = ((Conjunct) conjList.elementAt(0)).relation();

      if (!bodys.isSet()) { // LCDNF makes no sense otherwise
        DNF newstuff     = new DNF(omegaLib);
        int shared_depth = min(bodys.numberInput(), bodys.numberOutput());
        int split_to     = level >= 0 ? min(shared_depth, level) : shared_depth;

        omegaLib.skipFinalizationCheck++;

        int l = conjList.size();
        for (int i = 0; i < l; i++) {
          Conjunct conj = (Conjunct) conjList.elementAt(i);
          conj.makeLevelCarriedTo(newstuff, split_to, level);
        }

        omegaLib.skipFinalizationCheck--;
        joinDNF(newstuff);
      }
    }

    if (omegaLib.trace) {
      int l = conjList.size();
      for (int i = 0; i < l; i++) {
        Conjunct c = (Conjunct) conjList.elementAt(i);
        c.assertLeadingInfo();
      }
    }

    simplify(omegaLib.sRdtConstraints);
  }

  public void countLeadingZeros()
  {
    int l = conjList.size();
    for (int i = 0; i < l; i++) {
      Conjunct conj = (Conjunct) conjList.elementAt(i);
      conj.countLeadingZeros();
    }
  }

  public void addConjunct(Conjunct conj)
  {
    conjList.addElement(conj);
    if (conjList.size() > maxSize)
      maxSize = conjList.size();
  }

  public void joinDNF(DNF dnf)
  {
    conjList.addAll(dnf.conjList);
    if (conjList.size() > maxSize)
      maxSize = conjList.size();
    dnf.delete();
  }

  public void removeConjunct(Conjunct c)
  {
    boolean res = conjList.removeElement(c);
    if (res)
      return;

    throw new polyglot.util.InternalCompilerError("DNF.removeConjunct: no such conjunct " + c);
  }

  /**
   * Remove redundant conjuncts from given DNF.
   * If (C1 => C2), remove C1.
   * C1 => C2 is TRUE: when problem where C1 is Black and C2 is Red 
   * Blk   Red       : has no red constraints.
   * It means that C1 is a subset of C2 and therefore C1 is redundant.
   *
   * Exception: C1 => UNKNOWN - leave them as they are
   */
  public void rmRedundantConjs(int effort)
  {
    if (isDefinitelyFalse() || hasSingleConjunct())
      return;

    omegaLib.useUglyNames++;
    omegaLib.skipSetChecks++;

    int count = 0;
    int l = conjList.size();
    for (int i = 0; i < l; i++) {
      Conjunct p = (Conjunct) conjList.elementAt(i);
      if (p != null)
        count++;
    }

    if (omegaLib.trace) {
      System.out.print("@@@ rmRedundantConjs IN @@@[");
      prefixPrint();
      for (int i = 0; i < l; i++) {
        Conjunct p = (Conjunct) conjList.elementAt(i);
        System.out.print("#");
        System.out.print(i++);
        System.out.print(" = ");
        System.out.println(p);
      }
    }

    for (int i = l - 1; i >= 0; i--) {
      Conjunct cdel                  = (Conjunct) conjList.elementAt(i);
      int      del_min_leading_zeros = cdel.queryGuaranteedLeadingZeros();
      int      del_max_leading_zeros = cdel.queryPossibleLeadingZeros();

      for (int j = 0; j < l; j++) {
        Conjunct c = (Conjunct) conjList.elementAt(j);
        if (c == cdel)
          continue;

        int c_min_leading_zeros = cdel.queryGuaranteedLeadingZeros();
        int c_max_leading_zeros = cdel.queryPossibleLeadingZeros();

        if (omegaLib.trace) {
          System.out.print("@@@ rmRedundantConjs ");
          System.out.print(cdel);
          System.out.print(" => ");
          System.out.print(c);
          System.out.println("[");
        }

        if (c.isInexact() && cdel.isExact()) {
          if (omegaLib.trace)
            System.out.println("]@@@ rmRedundantConjs @@@ Exact Conj => Inexact Conj is not tested\n");
          continue;
        }

        if ((del_min_leading_zeros  >=0 && c_min_leading_zeros >= 0) &&
            (c_max_leading_zeros >= 0 && del_max_leading_zeros >=0) &&
            ((del_min_leading_zeros  > c_max_leading_zeros) || (c_min_leading_zeros > del_max_leading_zeros))) {
          if (omegaLib.trace)
            System.out.println("]@@@ not redundant due to leading zero info");
          continue;
        }

        Conjunct cgist = cdel.mergeConjuncts(c, Conjunct.MERGE_GIST, null);

        if (0 == cgist.redSimplifyProblem(effort, false)) {
          if (omegaLib.trace) {
            System.out.println("]@@@ rmRedundantConjs @@@ IMPLICATION TRUE " +  cdel);
            cdel.prefixPrint();
            System.out.println("=>");
            c.prefixPrint();
          }
          conjList.removeElementAt(i);
          l--;
          cdel.delete();
          cgist.delete();
          break;
        }

        if (omegaLib.trace) {
          System.out.println("]@@@ rmRedundantConjs @@@ IMPLICATION FALSE " + cdel);
          cgist.prefixPrint();
        }
        cgist.delete();
      }
    }

    if (omegaLib.trace) {
      System.out.println("]@@@ rmRedundantConjs OUT @@@");
      prefixPrint();
    }

    omegaLib.skipSetChecks--;
    omegaLib.useUglyNames--;
  }

  /**
   * Remove inexact conjuncts from given DNF if it contains UNKNOWN
   * conjunct.
   */
  public void rmRedundantInexactConjs()
  {
    if (isDefinitelyFalse() || hasSingleConjunct())
      return;

    boolean has_unknown = false;
    boolean has_inexact = false;

    Conjunct c_unknown = null;
    int      l = conjList.size();
    for (int i = 0; i < l; i++) {
      Conjunct p = (Conjunct) conjList.elementAt(i);

      if (!p.isInexact())
        continue;

      if (p.isUnknown()) {
        has_unknown = true;
        c_unknown = p;
      } else
        has_inexact = true;
    }

    if (!has_unknown || !has_inexact)
      return;

    omegaLib.useUglyNames++;
    omegaLib.skipSetChecks++;  

    for (int i = l - 1; i >= 0; i--) {
      Conjunct cdel = (Conjunct) conjList.elementAt(i);

      if (cdel.isInexact() && (cdel != c_unknown)) {
        conjList.removeElementAt(i);
        cdel.delete();
      }
    }

    omegaLib.useUglyNames--;
    omegaLib.skipSetChecks--;
  }

  /**
   * Convert DNF to Formula and add it root.
   */
  public void DNFtoFormula(Formula root)
  {
    Formula new_or = root;

    int l = conjList.size();
    if (l != 1) {
      omegaLib.skipFinalizationCheck++;
      new_or = root.addOr();
      omegaLib.skipFinalizationCheck--;
    }

    int l2 = conjList.size();
    for (int i = 0; i < l2; i++) {
      Conjunct conj = (Conjunct) conjList.elementAt(i);
      new_or.checkAndAddChild(conj);
    }

    conjList.clear();
    delete();
  }

  public void remap()
  {
    int l = conjList.size();
    for (int i = 0; i < l; i++) {
      Conjunct conj = (Conjunct) conjList.elementAt(i);
      conj.remap();
    }
  }

  public void setupNames()
  {
    if (false && omegaLib.trace)
      System.out.println("Setting up names for DNF " + this);

    int l = conjList.size();
    for (int i = 0; i < l; i++) {
      Conjunct conj = (Conjunct) conjList.elementAt(i);
      conj.setupNames();
    }
  }

  public void removeInexactConjunct()
  {
    int l = conjList.size();
    for (int i = l - 1; i >= 0; i--) {
      Conjunct conj = (Conjunct) conjList.elementAt(i);
      if (conj.isExact())
        continue;

      // remove it from the list

      conj.delete();
      conjList.remove(i);
    }
  }

  /**
   * DNF1 & DNF2 => DNF.
   */
  public DNF DNFandDNF(DNF dnf2)
  {
    DNF new_dnf = new DNF(omegaLib);
    int l = dnf2.conjList.size();
    for (int i = 0; i < l; i++) {
      Conjunct p = (Conjunct) dnf2.conjList.elementAt(i);
      new_dnf.joinDNF(DNFAndConjunct(p));
    }

    delete();
    dnf2.delete();

    if (new_dnf.length() > 1)
      new_dnf.simplify(omegaLib.sRdtConstraints);

    if (omegaLib.trace) {
      System.out.println("+++ DNFandDNF OUT +++");
      new_dnf.prefixPrint();
    }

    return new_dnf;
  }

  /**
   * DNF & CONJ => new DNF.
   * Don't touch arguments.
   */
  private DNF DNFAndConjunct(Conjunct conj)
  {
    DNF new_dnf = new DNF(omegaLib);
    int l = conjList.size();
    for (int i = 0; i < l; i++) {
      Conjunct p        = (Conjunct) conjList.elementAt(i);
      Conjunct new_conj = p.mergeConjuncts(conj, Conjunct.MERGE_REGULAR, null);
      new_dnf.addConjunct(new_conj);
    }

    if (new_dnf.length() > 1)
      new_dnf.simplify(omegaLib.sRdtConstraints);

    return new_dnf;
  }

  /**
   * Compute C0 and not (C1 or C2 or ... CN).
   * Reuse/delete its arguments.
   */
  public DNF conjAndNotDnf(Conjunct positive_conjunct, boolean weak)
  {
    omegaLib.useUglyNames++;

    if (omegaLib.trace) {
      System.out.println("conjAndNotDnf [");
      System.out.println("positive_conjunct:");
      positive_conjunct.prefixPrint();
      System.out.println("neg_conjs:");
      this.prefixPrint();
      System.out.println("\n");
    }

    DNF ret_dnf = conjAndNotDnfSub(positive_conjunct, weak);
    positive_conjunct.delete();

    if (omegaLib.trace) {
      System.out.println("] conjAndNotDnf RETURN:");
      ret_dnf.prefixPrint();
      System.out.println("\n");
    }

    omegaLib.useUglyNames--;

    return ret_dnf;
  }

  private DNF conjAndNotDnfSub(Conjunct positive_conjunct, boolean weak)
  {
    DNF ret_dnf = new DNF(omegaLib);

    if (positive_conjunct.simplifyConjunct(true, 0, Problem.BLACK) == false) {
      positive_conjunct = null;
      return ret_dnf;
    }

    /* Compute gists of negative conjuncts given positive conjunct */

    boolean c0_updated = true;
    while (c0_updated) {
      c0_updated = false;
      int l = conjList.size();
      for (int i = 0; i < l; i++) {
        Conjunct neg_conj = (Conjunct) conjList.elementAt(i);

        if (!positive_conjunct.isExact() && !neg_conj.isExact()) {
          // C1 and unknown & ~(C2 and unknown) = C1 and unknown
          conjList.remove(i);
          neg_conj.delete();
          i--;
          continue;
        }

        Conjunct cgist = positive_conjunct.mergeConjuncts(neg_conj, Conjunct.MERGE_GIST, null);

        if (cgist.simplifyConjunct(false, 1, Problem.RED) == false) {
          // C1 & ~FALSE = C1
          conjList.remove(i);
          neg_conj.delete();
          i--;
          continue;
        }

        cgist.removeColorConstraints();
        if (cgist.isTrue()) { // C1 & ~TRUE = FALSE
          cgist.delete();
          return ret_dnf;
        }

        if (cgist.cost() == 1) { // single inequality
          DNF      neg_dnf = cgist.negateConjunct();
          cgist.delete();
          Conjunct conj    = positive_conjunct.mergeConjuncts(neg_dnf.getSingleConjunct(), Conjunct.MERGE_REGULAR, null);
          positive_conjunct.delete();
          neg_dnf.delete();
          positive_conjunct = conj;
          conjList.remove(i);
          neg_conj.delete();
          i--;

          if (!positive_conjunct.simplifyConjunct(false, 0, Problem.BLACK)) {
            positive_conjunct = null;
            return ret_dnf;
          }

          c0_updated = true;
          continue;
        }

        conjList.setElementAt(cgist, i);
        neg_conj.delete();
      }
    }

    if (omegaLib.trace) {
      System.out.println("--- conjAndNotDnf positive_conjunct NEW:");
      positive_conjunct.prefixPrint();
      System.out.println("--- conjAndNotDnf this GISTS:");
      this.prefixPrint();
      System.out.println("--- conjAndNotDnf ---\n");
    }

    /* Find minimal negative conjunct */

    Conjunct min_conj   = null;
    int      min_cost   = Integer.MAX_VALUE;
    int      min_p      = 0;
    int      live_count = 0;
    int      l          = conjList.size();

    for (int i = 0; i < l; i++) {
      Conjunct neg_conj = (Conjunct) conjList.elementAt(i);

      live_count++;

      if (neg_conj.cost() < min_cost) {
        min_conj = neg_conj;
        min_cost = neg_conj.cost();
        min_p = i;
      }
    }

    /* Negate minimal conjunct, AND result with positive conjunct */
    if (weak || min_conj == null) {
      ret_dnf.addConjunct(positive_conjunct);
      positive_conjunct = null;
      return ret_dnf;
    }

    if (min_cost == Conjunct.CantBeNegated) {
      if (omegaLib.trace)
        System.out.println("** Ignoring negative clause that can't be negated and generating inexact result");

      positive_conjunct.makeInexact();
      ret_dnf.addConjunct(positive_conjunct);
      positive_conjunct = null;
      if (omegaLib.trace) 
        System.out.println("Ignoring negative clause that can't be negated and generating inexact upper bound\n");
      return ret_dnf;
    }

    DNF neg_dnf = min_conj.negateConjunct();
    min_conj.delete();
    conjList.remove(min_p);
    DNF new_pos = neg_dnf.DNFAndConjunct(positive_conjunct);
    neg_dnf.delete();
    positive_conjunct.delete();
    positive_conjunct = null;

    // new_dnf.rmRedundantConjs(2);

    if (live_count > 1) {
      Vector cl  = new_pos.conjList;
      int    cll = cl.size();
      for (int i = 0; i < cll; i++) {
        Conjunct conj = (Conjunct) cl.elementAt(i);
        ret_dnf.joinDNF(this.copy(omegaLib, conj.relation()).conjAndNotDnf(conj, false));
      }
      cl.clear();
      new_pos.delete();
    } else {
      ret_dnf.joinDNF(new_pos);
    }

    return ret_dnf;
  }

  public void dimensions(RelBody r, int[] dims)
  {
    int ndim_all    = 0;
    int ndim_domain = 0;
    int[] ndims     = new int[2];
    int cll         = conjList.size();

    for (int i = 0; i < cll; i++) {
      Conjunct s = (Conjunct) conjList.elementAt(i);

      r.calculateDimensions(s, ndims);
      if (ndims[0] > ndim_domain)
        ndim_domain = ndims[0];
      if (ndims[1] > ndim_all)
        ndim_all = ndims[1];
    }

    dims[0] = ndim_domain;
    dims[1] = ndim_all;
  }

  public void reverseLeadingDirInfo()
  {
    int    cll = conjList.size();
    for (int i = 0; i < cll; i++) {
      Conjunct c = (Conjunct) conjList.elementAt(i);
      c.reverseLeadingDirInfo();
    }
  }

  public void interpretUnknownAsTrue()
  {
    int    cll = conjList.size();
    for (int i = 0; i < cll; i++) {
      Conjunct c = (Conjunct) conjList.elementAt(i);
      c.interpretUnknownAsTrue();
    }
  }

  public void setParentRel(RelBody rel)
  {
    int    cll = conjList.size();
    for (int i = 0; i < cll; i++) {
      Conjunct c = (Conjunct) conjList.elementAt(i);
      c.setRelation(rel);
      c.setParent(rel);
    }
  }

  /**
   * Check if there exist any exact conjuncts in the solution.
   * Interpret UNKNOWN as false, then check satisfiability.
   */
  public boolean isLowerBoundSatisfiable()
  {
    int cll = conjList.size();
    for (int i = 0; i < cll; i++) {
      Conjunct c = (Conjunct) conjList.elementAt(i);
      if (c.isExact())
        return true;
    }
    return false;
  }

  public boolean queryDifference(VarDecl v1, VarDecl v2, int[] bounds)
  {
    int[]     nBounds  = new int[2];
    boolean first      = true;
    int     lowerBound = OmegaLib.negInfinity;  // default values if no DNF's
    int     upperBound = OmegaLib.posInfinity;
    boolean guaranteed = false;
    int     cll        = conjList.size();

    for (int i = 0; i < cll; i++) {
      Conjunct c = (Conjunct) conjList.elementAt(i);
      boolean  g = c.queryDifference(v1, v2, nBounds);
      if (first) {
        lowerBound = nBounds[0];
        upperBound = nBounds[1];
        guaranteed = g;
        first = false;
      } else {
        guaranteed = guaranteed && g;
        lowerBound = min(lowerBound, nBounds[0]);
        upperBound = max(upperBound, nBounds[1]);
      }
    }
    bounds[0] = lowerBound;
    bounds[1] = upperBound;
    return guaranteed;
  }

  public void queryVariableBounds(VarDecl v, int[] bounds)
  {

    int[] nBounds = new int[2];
    boolean first = true;
    int lowerBound = OmegaLib.negInfinity;  // default values if no DNF's
    int upperBound = OmegaLib.posInfinity;

    int cll = conjList.size();
    for (int i = 0; i < cll; i++) {
      Conjunct c = (Conjunct) conjList.elementAt(i);
      if (c == null)
        continue;

      c.queryVariableBounds(v, nBounds);
      if (first) {
        lowerBound = nBounds[0];
        upperBound = nBounds[1];
        first = false;
      } else {
        lowerBound = min(lowerBound, nBounds[0]);
        upperBound = max(upperBound, nBounds[1]);
      }
    }

    bounds[0] = lowerBound;
    bounds[1] = upperBound;
  }

  public void assertLeadingInfo()
  {
    int cll = conjList.size();
    for (int i = 0; i < cll; i++) {
      Conjunct c = (Conjunct) conjList.elementAt(i);
      c.assertLeadingInfo();
    }
  }

  public void domain(RelBody r, int a)
  {
    int cll = conjList.size();
    for (int i = 0; i < cll; i++) {
      Conjunct c = (Conjunct) conjList.elementAt(i);
      c.domain(r, a);
    }
  }

  public void range(RelBody r, int a)
  {
    int cll = conjList.size();
    for (int i = 0; i < cll; i++) {
      Conjunct c = (Conjunct) conjList.elementAt(i);
      c.range(r, a);
    }
  }

  public void reorderAndSimplify(RelBody r, boolean strides_allowed)
  {
    int cll = conjList.size();
    for (int i = 0; i < cll; i++) {
      Conjunct c = (Conjunct) conjList.elementAt(i);
      c.reorderAndSimplify(r, strides_allowed);
    }
  }

  public boolean redSimplifyProblem(DNF dnf2)
  {
    int cll1 = conjList.size();
    for (int i1 = 0; i1 < cll1; i1++) {
      Conjunct c1   = (Conjunct) conjList.elementAt(i1);
      int      cll2 = dnf2.conjList.size();
      boolean  found = false;
      for (int i2 = 0; i2 < cll2; i2++) {
        Conjunct c2 = (Conjunct) dnf2.conjList.elementAt(i2);
        if (!c2.isExact())
          continue;
    
        Conjunct cgist = c1.mergeConjuncts(c2, Conjunct.MERGE_GIST, c2.relation());

        cgist.setupNames();

        if (cgist.redSimplifyProblem(2, false) == Problem.noRed) {
          found = true;
          break;
        }
      }
      if (!found)
        return false;
    }
    return true;
  }

  public HashSet fastTightHull()
  {
    HashSet vars = new HashSet(11);
    int     cll  = conjList.size();
    for (int i = 0; i < cll; i++) {
      Conjunct c = (Conjunct) conjList.elementAt(i);
      c.fastTightHull(vars);
    }
    return vars;
  }

  public void deltas()
  {
    int cll = conjList.size();
    for (int i = 0; i < cll; i++) {
      Conjunct c = (Conjunct) conjList.elementAt(i);
      c.deltas();
    }
  }

  public void farkas()
  {
    int cll = conjList.size();
    for (int i = 0; i < cll; i++) {
      Conjunct c = (Conjunct) conjList.elementAt(i);
      c.farkas();
    }
  }

  public void appendClausesToList(Vector l, int depth, RelBody rel)
  {
    int cll = conjList.size();
    for (int i = 0; i < cll; i++) {
      Conjunct c = (Conjunct) conjList.elementAt(i);
      if (c.queryGuaranteedLeadingZeros() == depth)
        l.addElement(new Relation(rel, c));
    }
  }

  public void convertEQstoGEQs()
  {
    int cll = conjList.size();
    for (int i = 0; i < cll; i++) {
      Conjunct c = (Conjunct) conjList.elementAt(i);
      c.convertEQstoGEQs();
    }
  }

  public int minNumEQs()
  {
    int n_eq_sym = 1000;
    int cll = conjList.size();
    for (int i = 0; i < cll; i++) {
      Conjunct c = (Conjunct) conjList.elementAt(i);
      int x = c.getNumEQs();
      if (x < n_eq_sym)
        n_eq_sym = x;
    }
    return n_eq_sym;
  }

  public void DNFizeH(Vector myLocals)
  {
    int cll = conjList.size();
    for (int i = 0; i < cll; i++) {
      Conjunct c           = (Conjunct) conjList.elementAt(i);
      Vector   locals_copy = new Vector(myLocals.size());

      locals_copy = VarDecl.copyVarDecls(myLocals);
      c.DNFizeH(locals_copy);
    }
  }

  public DNF realNegConjs()
  {
    DNF realNegConjs = new DNF(omegaLib);
    int cll            = conjList.size();
    for (int i = 0; i < cll; i++) {
      Conjunct c = (Conjunct) conjList.elementAt(i);
      if (c.simplifyConjunct(true, 0, Problem.BLACK) != false)
        realNegConjs.addConjunct(c);
    }
    return realNegConjs;
  }

  public void join_conjAndNotDnf(DNF pdnf, DNF neg_conjs)
  {
    Vector clp  = pdnf.conjList;
    int    cllp = clp.size();
    for (int clip = 0; clip < cllp; clip++) {
      Conjunct pc = (Conjunct) clp.elementAt(clip);
      joinDNF(neg_conjs.copy(omegaLib, pc.relation()).conjAndNotDnf(pc, false));
    }
    pdnf.conjList.setSize(1);
  }

  public void remapDNFVars(RelBody ref_rel)
  {
    int cll = conjList.size();
    for (int i = 0; i < cll; i++) {
      Conjunct c = (Conjunct) conjList.elementAt(i);
      c.remapDNFVars(ref_rel);
    }
  }

  public DNF gistSingleConjunct(Conjunct known, RelBody r1, int effort)
  {
    DNF new_dnf = new DNF(omegaLib);
    int cll     = conjList.size();
    for (int i = 0; i < cll; i++) {
      Conjunct conj = (Conjunct) conjList.elementAt(i);
      Conjunct cgist = known.mergeConjuncts(conj, Conjunct.MERGE_GIST, conj.relation()); // Uses r1's vars

      cgist.setRelation(r1);   // Thinks it's part of r1 now, for var. purposes
      if (!cgist.simplifyConjunct(true, effort + 1, Problem.RED))
        continue;

      /* Throw out BLACK constraints, turn red constraints into BLACK */

      cgist.removeColorConstraints();

      if (cgist.isTrue())
        return null;

      new_dnf.addConjunct(cgist);
    }
    return new_dnf;
  }

  public void after(RelBody r, int preserved_positions)
  {
    int cll = conjList.size();
    for (int i = 0; i < cll; i++) {
      Conjunct c = (Conjunct) conjList.elementAt(i);
      c.after(r, preserved_positions);
    }
  }

  public boolean isExact()
  {
    return (0 == (localStatus() & (and_u |or_u)));
  }

  public int localStatus()
  {
    int cll = conjList.size();
    if (cll == 0)
      return no_u;

    int localStatus = 0;

    for (int i = 0; i < cll; i++) {
      Conjunct c = (Conjunct) conjList.elementAt(i);
      if (c.isExact())
        localStatus |= no_u;
      else if (c.isUnknown())
        localStatus |= or_u;
      else
        localStatus |= and_u;
    }
    olAssert(localStatus != 0);

    int impossible = (and_u | or_u);
    olAssert((localStatus & impossible) != impossible);

    return localStatus;
  }

  private void olAssert(boolean t)
  {
    if (t)
      return;
    throw new polyglot.util.InternalCompilerError("DNF");
  }

  private int min(int a, int b)
  {
    if (a < b)
      return a;
    return b;
  }

  private int max(int a, int b)
  {
    if (a > b)
      return a;
    return b;
  }
}
