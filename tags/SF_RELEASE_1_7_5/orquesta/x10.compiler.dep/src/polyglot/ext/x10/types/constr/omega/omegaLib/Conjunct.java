package polyglot.ext.x10.types.constr.omega.omegaLib;

import java.util.Iterator;
import java.util.*;

/**
 * Conjunct.
 * <p>
 * $Id: Conjunct.java,v 1.1 2008-03-06 18:02:23 nystrom Exp $
 * <p>
 * Copyright 2006 by the <a href="http://spa-www.cs.umass.edu/">Scale Compiler Group</a>,<br>
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
 * About variables in Conjunct:
 * <ul>
 * <li> All varaibles appear in exactly one declaration.
 * <li> All variables used in Conjunct are referenced in mappedVars.
 * <li> Wildcard variables are referenced both in mappedVars and in myLocals, 
 *   since they are declared in the conjunct.
 * <li> All other variables are declared at the levels above.
 * </ul>
 * Column number is: 
 * <ul>
 * <li> in forwardingAddress in Problem if variablesInitialized is set, 
 * <li> equal to position of VarDecl in mappedVars list otherwise.
 * </ul>
 */
public final class Conjunct extends FDeclaration
{
  private static int createdCount = 0; /* A count of all the instances of this class that were created. */

  public static int created()
  {
    return createdCount;
  }

  public static final int CantBeNegated = Integer.MAX_VALUE - 10;
  public static final int AvoidNegating = Integer.MAX_VALUE - 11;

  public static final int MERGE_REGULAR = 0;
  public static final int MERGE_COMPOSE = 1;
  public static final int MERGE_GIST    = 2;

  public static final char[] dirGlyphs = {'-', '?', '+'};

  public static final int EVAC_TRIVIAL       = 0;
  public static final int EVAC_OFFSET        = 1;
  public static final int EVAC_SUBSEQ        = 2;
  public static final int EVAC_OFFSET_subseq = 3;
  public static final int EVAC_AFFINE        = 4;
  public static final int EVAC_NASTY         = 5;

  public static final int ANY_NEGATION      = 0;
  public static final int ONE_GEQ_OR_EQ     = 1;
  public static final int ONE_GEQ_OR_STRIDE = 2;

  private static final String[] evacNames = {"trivial", "offset", "subseq", "off_sub", "affine", "nasty"};

  private Problem problem;
  private Vector  mappedVars;   /* List of all variables. */
  private boolean areColumnsOrdered;
  private boolean simplified;
  private boolean verified;
  private boolean exact;
  private int     numOpenConstraints;
  private int     guaranteed_leading_0s; // -1 if unknown
  private int     possible_leading_0s;   // -1 if unknown
  private int     leading_dir;           // 0 if unknown, else +/- 1
  public  int     id;

  public Conjunct(OmegaLib omegaLib)
  {
    this(omegaLib, null, null);
  }

  public Conjunct(OmegaLib omegaLib, Formula f, RelBody r)
  {
    super(omegaLib, f, r);

    this.mappedVars             = new Vector();
    this.myLocals               = new Vector();
    this.numOpenConstraints     = 0;
    this.areColumnsOrdered      = false;
    this.simplified             = false;
    this.verified               = false;
    this.guaranteed_leading_0s  = -1;
    this.possible_leading_0s    = -1;
    this.leading_dir            = 0;
    this.exact                  = true;
    this.problem                = new Problem(omegaLib, 0, 0, this);
    this.id                     = createdCount++;
  }

  public Conjunct(Conjunct conj)
  {
    super(conj.omegaLib, conj.parent(), conj.relation());

    if (conj.problem == null)
      throw new polyglot.util.InternalCompilerError("problem is null");

    this.myLocals               = VarDecl.copyVarDecls(conj.myLocals);
    this.mappedVars             = (Vector) conj.mappedVars.clone();
    this.areColumnsOrdered      = conj.areColumnsOrdered;
    this.numOpenConstraints     = conj.numOpenConstraints;
    this.exact                  = conj.exact;
    this.simplified             = conj.simplified;
    this.verified               = conj.verified;
    this.guaranteed_leading_0s  = conj.guaranteed_leading_0s;
    this.possible_leading_0s    = conj.possible_leading_0s;
    this.leading_dir            = conj.leading_dir;
    this.problem                = new Problem(conj.problem, this); 
    this.remap();

    VarDecl.resetRemapField(myLocals);
    this.id = createdCount++;
  }

  public Formula copy(Formula parent, RelBody rel_body)
  {
    if (problem == null)
      throw new polyglot.util.InternalCompilerError("problem is null");

    Conjunct new_conj = new Conjunct(omegaLib, parent, rel_body);

    new_conj.myLocals               = VarDecl.copyVarDecls(myLocals);
    new_conj.mappedVars             = (Vector) mappedVars.clone();
    new_conj.areColumnsOrdered      = areColumnsOrdered;
    new_conj.numOpenConstraints     = numOpenConstraints;
    new_conj.exact                  = exact;
    new_conj.simplified             = simplified;
    new_conj.verified               = verified;
    new_conj.guaranteed_leading_0s  = guaranteed_leading_0s;
    new_conj.possible_leading_0s    = possible_leading_0s;
    new_conj.leading_dir            = leading_dir;
    new_conj.problem                = new Problem(problem, new_conj); 
    new_conj.remap();

    VarDecl.resetRemapField(myLocals);

    return new_conj;
  }

  public void delete()
  {
  }

  public String toString()
  {
    StringBuffer buf = new StringBuffer("(Conjunct ");
    buf.append(id);
    buf.append(" s=");
    buf.append(simplified);
    buf.append(" v=");
    buf.append(verified);
    buf.append(' ');
    buf.append(guaranteed_leading_0s);
    buf.append(' ');
    buf.append(possible_leading_0s);
    buf.append(' ');
    buf.append(leading_dir);
    buf.append(')');
    return buf.toString();
  }

  private int max(int a, int b)
  {
    if (a > b)
      return a;
    return b;
  }

  private int min(int a, int b)
  {
    if (a < b)
      return a;
    return b;
  }

  public boolean leadingDirValidAndKnown()
  {
    if (relation().isSet())
      return false;

    // If we know leading dir, we can rule out extra possible 0's.

    if ((leading_dir != 0) && (possible_leading_0s != guaranteed_leading_0s))
      throw new polyglot.util.InternalCompilerError("leadingDirValidAndKnown " + leading_dir + " " + possible_leading_0s + " " + guaranteed_leading_0s);

    return ((possible_leading_0s == guaranteed_leading_0s) &&
            (possible_leading_0s >= 0) &&
            (possible_leading_0s < min(relation().numberInput(), relation().numberOutput())) &&
            (leading_dir != 0));
  }

  public Vector variables()
  {
    return mappedVars;
  }

  public int numVars()
  {
    return problem.numVars();
  }

  public Equation[] getEQs()
  {
    return problem.getEQs();
  }

  public int getNumEQs()
  {
    return problem.getNumEQs();
  }

  public Equation[] getGEQs()
  {
    return problem.getGEQs();
  }

  public int getNumGEQs()
  {
    return problem.getNumGEQs();
  }

  public Problem getProblem()
  {
    return problem;
  }

  public boolean hasWildcards(Equation eq)
  {
    int l = mappedVars.size();

    for (int i = 1; i <= l; i++) {
      if (eq.isZero(i))
        continue;
      VarDecl v = (VarDecl) mappedVars.elementAt(i - 1);
      if (v.kind() == VarDecl.WILDCARD_VAR)
        return true;
    }

    return false;
  }

  public int maxTuplePosition(Equation eq)
  {
    int m = 0;
    int l = mappedVars.size();

    for (int i = 1; i <= l; i++) {
      if (eq.isZero(i))
        continue;
      VarDecl v = (VarDecl) mappedVars.elementAt(i - 1);
      switch (v.kind()) {
      case VarDecl.INPUT_VAR: 
      case VarDecl.OUTPUT_VAR: 
        int pos = v.getPosition();
        if (m < pos)
          m = pos;
      default: 
        ;
      }
    }

    return m;
  }

  public boolean varIsConstant(VarDecl v, Equation eq)
  {
    int l = mappedVars.size();

    for (int i = 1; i <= l; i++) {
      int ci = eq.getCoefficient(i);
      if (ci == 0)
        continue;
      VarDecl vc = (VarDecl) mappedVars.elementAt(i - 1);
      boolean is_const = (vc == v);
      if (!is_const)
        return false;
    }

    return true;
  }

  public void promiseThatUbSolutionsExist()
  {
    verified = true;
  }

  public int maxUfsArityOfSet() 
  {
    int ma = 0;
    int l  = mappedVars.size();
    for (int i = 1; i <= l; i++) {
      VarDecl v = (VarDecl) mappedVars.elementAt(i - 1);
      if ((v.kind() == VarDecl.GLOBAL_VAR) && (v.functionOf() == VarDecl.SET_TUPLE) && problem.anyNonZeroCoef(i)) {
        int a = v.getGlobalVar().arity();
        if (a > ma)
          ma = a;
      }
    }
    return ma;
  }

  public int maxUfsArityOfIn() 
  {
    int ma = 0;
    int l  = mappedVars.size();
    for (int i = 1; i <= l; i++) {
      VarDecl v = (VarDecl) mappedVars.elementAt(i - 1);
      if ((v.kind() == VarDecl.GLOBAL_VAR) && (v.functionOf() == VarDecl.INPUT_TUPLE) && problem.anyNonZeroCoef(i)) {
        int a = v.getGlobalVar().arity();
        if (a > ma)
          ma = a;
      }
    }
    return ma;
  }

  public int maxUfsArityOfOut() 
  {
    int ma = 0;
    int l  = mappedVars.size();
    for (int i = 1; i <= l; i++) {
      VarDecl v = (VarDecl) mappedVars.elementAt(i - 1);
      if ((v.kind() == VarDecl.GLOBAL_VAR) &&  (v.functionOf() == VarDecl.OUTPUT_TUPLE) && problem.anyNonZeroCoef(i)) {
        int a = v.getGlobalVar().arity();
        if (a > ma)
          ma = a;
      }
    }
    return ma;
  }

  public int nodeType()
  {
    return OP_CONJUNCT;
  }

  public boolean isTrue()
  {
    return problem.isEmpty() && exact;
  }

  public boolean isEmpty()
  {
    return problem.isEmpty();
  }

  public void clearSubs()
  {
    problem.clearSubs();
  }

  public boolean queryDifference(VarDecl v1, VarDecl v2, int[] bounds)
  {
    int c1 = getColumn(v1);
    int c2 = getColumn(v2);
    olAssert((c1 != 0) && (c2 != 0));
    return problem.queryDifference(c1, c2, bounds);
  }

  public void queryVariableBounds(VarDecl v, int[] bounds)
  {
    int c = getColumn(v);
    olAssert(c != 0);
    problem.queryVariableBounds(c, bounds);
  }

  public int difficulty() 
  {
    return problem.difficulty();
  }

  public int queryGuaranteedLeadingZeros()
  {
    countLeadingZeros();
    return guaranteed_leading_0s;
  }

  public int getGuaranteedLeading0s()
  {
    countLeadingZeros();
    return guaranteed_leading_0s;
  }

  public int queryPossibleLeadingZeros()
  {
    countLeadingZeros();
    return possible_leading_0s;
  }

  public int queryLeadingDir()
  {
    countLeadingZeros();
    return leading_dir;
  }

  private int rank()
  {
    Conjunct conj = new Conjunct(this);

    conj.reorder();
    conj.orderedElimination(conj.relation().getGlobalDecls().size());

    int rank = 0;
    int l    = conj.mappedVars.size();

    for (int i = 0; i < l; i++) {
      VarDecl v = (VarDecl) conj.mappedVars.elementAt(i);
      if (conj.findColumn(v) > 0)
        rank++;
    }

    conj.delete();
    return rank;
  }

  public boolean isUnknown()
  { 
    olAssert(problem != null);
    return (!exact && (problem != null) && problem.isEmpty());
  }

  public boolean isExact() 
  { 
    return exact;
  }

  public boolean isInexact()  
  { 
    return !exact;
  }

  public void makeInexact()  
  {
    exact = false;
  }

  /**
   * Simplify Conjunct.
   * Return true if there are solutions.
   */
  public boolean simplifyConjunct(boolean ver_sim, int simplificationEffort, int color)
  {
    if (verified 
        && (simplificationEffort <= 0) 
        && (simplified  || (simplificationEffort < 0))
        && (color == Problem.BLACK)) {
      if (omegaLib.trace) {
        System.out.print("$$$ Redundant simplifyConjunct ignored (");
        System.out.print(ver_sim);
        System.out.print(",");
        System.out.print(simplificationEffort);
        System.out.print(",");
        System.out.print(color);
        System.out.println(")");
        prefixPrint(true);
      }
      return true;
    }

    if (simplificationEffort < 0)
      simplificationEffort = 0;

    moveUfsToInput();
    reorder();

    Problem p = problem;

    omegaLib.useUglyNames++;

    p.touchAllGEQs();
    p.touchAllEQs();

    if (omegaLib.trace) {
      System.out.print("$$$ simplifyConjunct (");
      System.out.print(ver_sim);
      System.out.print(",");
      System.out.print(simplificationEffort);
      System.out.print(",");
      System.out.print(color);
      System.out.println(")[");
      prefixPrint(true);
    }

    olAssert(areColumnsOrdered);

    boolean ret_code;
    if (color == Problem.BLACK) {
      ret_code = simplifyProblem(ver_sim && !verified, simplificationEffort);
    } else {
      int r = redSimplifyProblem(simplificationEffort, true);
      ret_code = (r != Problem.redFalse);
    }
    olAssert(p.isNoSubs());

    if (!ret_code) {
      if (omegaLib.trace)
        System.out.println("] $$$ simplifyConjunct : false\n");
      delete();
      omegaLib.useUglyNames--;
      return false;
    }

    // mappedVars is mapping from columns to VarDecls.
    // Recompute mappedVars for problem returned from ip.c

    recomputeMappedVars();
    problem.setVariablesInitialized(true);

    if (omegaLib.trace) {
      System.out.println("] $$$ simplifyConjunct");
      prefixPrint(true);
      System.out.println("");
    }

    omegaLib.useUglyNames--;
    simplified = true;
    setupAnonymousWildcardNames();

    return true;
  }

  public void recomputeMappedVars()
  {
    int    numSafe    = problem.numSafeVars();
    int    numVars    = problem.numVars();
    Vector new_mapped = new Vector(numVars);  // This is expanded by "append"

    for (int i = 1; i <= numSafe; i++) {
      // what is now in column i used to be in column p.var[i]
      VarDecl v = (VarDecl) mappedVars.elementAt(problem.varMappedAt(i) - 1);
      if (v.kind() == VarDecl.WILDCARD_VAR)
        throw new polyglot.util.InternalCompilerError("found wild card " + v);
      new_mapped.addElement(v);
    }

    myLocals.clear();
    mappedVars = new_mapped;

    for (int i = numSafe + 1; i <= numVars; i++) {
      VarDecl v = declare();
      mappedVars.addElement(v);
    }

    problem.resetVarAndForwarding(); // Reset var and forwarding address.
  }

  public void assertLeadingInfo()
  {
    RelBody rel = relation();

    if (rel.isSet())
      return;

    int d = min(rel.numberInput(), rel.numberOutput());

    if ((guaranteed_leading_0s == -1) && (guaranteed_leading_0s == possible_leading_0s))
      olAssert(leading_dir == 0);
 
    if ((leading_dir != 0) && (possible_leading_0s != guaranteed_leading_0s)) {
      omegaLib.useUglyNames++;
      prefixPrint(true);
      omegaLib.useUglyNames--;
    }

    if (((leading_dir != 0) && (possible_leading_0s != guaranteed_leading_0s)) ||
        ((possible_leading_0s > d) || (guaranteed_leading_0s > d)) ||
        ((possible_leading_0s != -1) && (guaranteed_leading_0s > possible_leading_0s)))
      throw new polyglot.util.InternalCompilerError("assertLeadingInfo " + leading_dir + " " + possible_leading_0s + " " + guaranteed_leading_0s + " " + d);

    int[] bounds = new int[2];

    // check that there must be "guaranteed_leading_0s" 0s
    for (int carried_level = 1; carried_level <= guaranteed_leading_0s; carried_level++) {
      VarDecl in   = rel.inputVar(carried_level);
      VarDecl out  = rel.outputVar(carried_level);
      boolean guar = queryDifference(out, in, bounds);
      int     lb   = bounds[0];
      int     ub   = bounds[1];

      if ((lb == 0) || (ub == 0))
        continue;

      // Probably "queryDifference" is just approximate.
      // Add the negation of leading_dir and assert that
      // the result is unsatisfiable;
      // add in > out (in-out-1>=0) and assert unsatisfiable.

      Conjunct test = new Conjunct(this);
      test.problem.turnRedBlack();
      omegaLib.skipFinalizationCheck++;
            
      GEQHandle g = test.addGEQ(false);
      g.updateCoefDuringSimplify(in, -1);
      g.updateCoefDuringSimplify(out, 1);
      g.updateConstantDuringSimplify(-1);

      if (test.simplifyConjunct(true, 0, 0))
        throw new polyglot.util.InternalCompilerError("assertLeadingInfo ");

      // test was deleted by simplifyConjunct, as it was FALSE

      test = new Conjunct(this);
      test.problem.turnRedBlack();
      g = test.addGEQ(false);
      g.updateCoefDuringSimplify(in, 1);
      g.updateCoefDuringSimplify(out, -1);
      g.updateConstantDuringSimplify(-1);

      if (test.simplifyConjunct(true, 0, 0))
        throw new polyglot.util.InternalCompilerError("assertLeadingInfo ");

      // test was deleted by simplifyConjunct, as it was FALSE

      omegaLib.skipFinalizationCheck--;
    }

    int carried_level = possible_leading_0s + 1;

    // check that there can't be another
    if ((guaranteed_leading_0s == possible_leading_0s) && (possible_leading_0s >= 0) && (carried_level <= min(rel.numberInput(), rel.numberOutput()))) {
      VarDecl in   = rel.inputVar(carried_level);
      VarDecl out  = rel.outputVar(carried_level);
      boolean guar = queryDifference(out, in, bounds);
      int     lb   = bounds[0];
      int     ub   = bounds[1];

      if ((lb <= 0) &&  (ub >= 0)) {
        // probably "queryDifference" is just approximate
        // add a 0 and see if its satisfiable

        Conjunct test = new Conjunct(this);
        test.problem.turnRedBlack();
        omegaLib.skipFinalizationCheck++;

        EQHandle e = test.addEQ(false);
        e.updateCoefDuringSimplify(in, -1);
        e.updateCoefDuringSimplify(out, 1);

        if (test.simplifyConjunct(true, 0, 0))
          throw new polyglot.util.InternalCompilerError("assertLeadingInfo ");

        // test was deleted by simplifyConjunct, as it was FALSE

        omegaLib.skipFinalizationCheck--;
      }
    }

    // check leading direction info
    if (leadingDirValidAndKnown()) {
      VarDecl in   = rel.inputVar(guaranteed_leading_0s + 1);
      VarDecl out  = rel.outputVar(guaranteed_leading_0s + 1);
      boolean guar = queryDifference(out, in, bounds);
      int     lb   = bounds[0];
      int     ub   = bounds[1];

      if (((leading_dir < 0) && (ub >= 0)) ||
          ((leading_dir > 0) && (lb <= 0))) {
        // probably "queryDifference" is just approximate
        // add the negation of leading_dir and assert that
        // the result is unsatisfiable;
        // eg for leading_dir = +1 (in must be < out),
        // add in >= out (in-out>=0) and assert unsatisfiable.

        Conjunct test = new Conjunct(this);
        test.problem.turnRedBlack();
        omegaLib.skipFinalizationCheck++;
            
        GEQHandle g = test.addGEQ(false);
        g.updateCoefDuringSimplify(in, leading_dir);
        g.updateCoefDuringSimplify(out, -leading_dir);

        if (test.simplifyConjunct(true, 0, 0))
          throw new polyglot.util.InternalCompilerError("assertLeadingInfo ");

        // test was deleted by simplifyConjunct, as it was FALSE

        omegaLib.skipFinalizationCheck--;
      }
    }
  }

  public String tryToPrintVarToStringWithDiv(VarDecl v)
  {
    int col = findColumn(v);
    if (col == 0)
      return "";

    StringBuffer s    = new StringBuffer("");
    boolean      seen = false;
    int          l    = mappedVars.size();

    // This assumes that there is one EQ involving v, that v cannot be 
    // substituted and hence has a non-unit coefficient.

    Equation[] eqs  = getEQs();
    int        neqs = getNumEQs();
    for (int e = 0; e < neqs; e++) {
      Equation eq     = eqs[e];
      int      v_coef = eq.getCoefficient(col);

      if (v_coef == 0)
        continue;

      olAssert(!seen);  // This asserts just one EQ with v 

      int v_sign   = ((v_coef > 0) ? 1 : -1);
      int sign_adj = -v_sign;

      v_coef *= v_sign;

      s.append("intDiv(");

      boolean first = true;

      for (int i = 1; i <= l; i++) {
        int cc = eq.getCoefficient(i);
        if (cc == 0)
          continue;

        VarDecl cv = (VarDecl) mappedVars.elementAt(i - 1);
        if (cv == v)
          continue;

        int this_coef = sign_adj * cc;
        if (!first && (this_coef > 0))
          s.append("+");

        if (this_coef == 1)
          s.append(cv.name(omegaLib));
        else if (this_coef == -1) {
          s.append("-");
          s.append(cv.name(omegaLib));
        } else {
          s.append(this_coef);
          s.append("*");
          s.append(cv.name(omegaLib));
        }

        first = false;
      }

      int the_const = eq.getConstant() * sign_adj;
      if ((the_const > 0) && !first)
        s.append("+");

      if (the_const != 0)
        s.append(the_const);

      s.append(",");
      s.append(v_coef);
      s.append(")");
      seen = true;
    }

    return s.toString();
  }

  public void print()
  {
    String s = printToString(true);
    System.out.print(s);
  }

  public void prefixPrint()
  {
    prefixPrint(true);
  }

  public void prefixPrint(boolean debug)
  {
    omegaLib.incrementPrintLevel();

    if (debug) {
      omegaLib.printHead();
//        System.out.print("(");
//        System.out.print(id);
//        System.out.print(")");
      System.out.print(exact ? "EXACT" : "INEXACT");
      System.out.print(" CONJUNCT " + id + ", ");

      if (simplified)
        System.out.print("simplified, ");

      if (verified)
        System.out.print("verified, ");

      if ((possible_leading_0s != -1) && (guaranteed_leading_0s != -1))
        olAssert(guaranteed_leading_0s <= possible_leading_0s);

      if ((guaranteed_leading_0s != -1) && (guaranteed_leading_0s == possible_leading_0s))
        System.out.print("# leading 0's = " + possible_leading_0s + ",");

      else if (possible_leading_0s != -1 || guaranteed_leading_0s != -1) {
        if (guaranteed_leading_0s != -1)
          System.out.print("" + guaranteed_leading_0s + " <= ");
        System.out.print("#O's");
        if (possible_leading_0s != -1)
          System.out.print(" <= " + possible_leading_0s);
        System.out.print(", ");
      }

      if (dirGlyphs[leading_dir + 1] != '?') {
        System.out.print(" first = " + dirGlyphs[leading_dir + 1]);
        System.out.print(", ");
      }

      StringBuffer s = new StringBuffer("myLocals = [");
      int          l = myLocals.size();
      for (int i = 0; i < l; i++) {
        VarDecl v = (VarDecl) myLocals.elementAt(i);
        if (i > 0)
          s.append(",");
        olAssert(v.kind() == VarDecl.WILDCARD_VAR);
        s.append(v.name(omegaLib));
//          v.printVarAddrs(s);
      }

      s.append("] mappedVars = [");

      int ml = mappedVars.size();
      for (int i = 0; i < ml; i++) {
        VarDecl v = (VarDecl) mappedVars.elementAt(i);
        if (i > 0)
          s.append(",");
        s.append(v.name(omegaLib));
//          v.printVarAddrs(s);
      }
      s.append(']');
      System.out.println(s.toString());
    }

    omegaLib.incrementPrintLevel();
    problem.printProblem(debug);
    omegaLib.decrementPrintLevel();
 
    int l = myChildren.size();
    for (int i = 0; i < l; i++) {
      Formula c = (Formula) myChildren.elementAt(i);
      c.prefixPrint(debug);
    }

    omegaLib.decrementPrintLevel();
  }

  public String printToString(boolean true_printed)
  {
    StringBuffer s   = new StringBuffer(""); 
    int          num = myLocals.size();

    if (num > 0) {
      s.append("Exists  ( ");
      for (int i = 0; i < num; i++) {
        VarDecl v = (VarDecl) myLocals.elementAt(i);
        s.append(v.displayName(omegaLib));
        if (i < num)
          s.append(",");
      }
      if (true_printed || !isTrue())
        s.append(" : ");
    }

    if (isTrue()) {
      s.append(true_printed ? "TRUE" : "");
    } else { 
      if (isUnknown()) 
        s.append("UNKNOWN");
      else {
        s.append(problem.prettyPrintProblemToString());
        if (!exact)
          s.append(" && UNKNOWN");
      } 
    }

    if (num > 0)
      s.append(")");

    return s.toString();
  }

  public String printEQtoString(Equation e)
  {
    return problem.printEQtoString(e);
  }

  public String printGEQtoString(Equation e)
  {
    return problem.printGEQtoString(e);
  }

  public String printTermToString(Equation e)
  { 
    return problem.printTermToString(e, 1);
  }

  public String printSubToString(int col)
  {
    return problem.printSubToString(col);
  }

  public void interpretUnknownAsTrue() 
  {
    exact = true;
  }

  public Conjunct reallyConjunct()
  {
    return this;
  }

  /**
   * Create new constraints with all co-efficients 0.
   * These are public in FAnd, use them from there.
   */
  public EQHandle addStride(int step, boolean preserves_level)
  {
    assertNotFinalized();

    VarDecl wild = declare();

    simplified = false;
    verified = false;

    if (!preserves_level) {
      if (leading_dir == 0)
        possible_leading_0s = -1;
      // otherwise we must still have leading_dir, and thus no more 0's
    }

    numOpenConstraints++;

    EQHandle h = new EQHandle(this, problem.addEQ());

    h.updateCoefficient(wild, step);

    return h;
  }

  public EQHandle addEQ(boolean preserves_level)
  {
    assertNotFinalized();
    simplified = false;
    verified = false;

    if ((!preserves_level) && (leading_dir == 0))
      possible_leading_0s = -1; // otherwise we must still have leading_dir, and thus no more 0's

    numOpenConstraints++;
    return new EQHandle(this, problem.addEQ());
  }

  public GEQHandle addGEQ(boolean preserves_level)
  {
    assertNotFinalized();
    simplified = false;
    verified = false;

    if ((!preserves_level) && (leading_dir == 0))
      possible_leading_0s = -1; // otherwise we must still have leading_dir, and thus no more 0's

    numOpenConstraints++;
    return new GEQHandle(this, problem.addGEQ());
  }

  // This should only be used to copy constraints from simplified relations, 
  // i.e. there are no quantified variables in c except wildcards.
  private  EQHandle addEQ(ConstraintHandle c)
  {
    return addEQ(c, false);
  }

  public EQHandle addEQ(ConstraintHandle c, boolean preserves_level)
  {
    EQHandle e = addEQ(preserves_level);
    e.copyConstraint(c);
    return e;
  }

  // This should only be used to copy constraints from simplified relations, 
  // i.e. there are no quantified variables in c except wildcards.
  private GEQHandle addGEQ(ConstraintHandle c)
  { 
    return addGEQ(c, false);
  }

  public GEQHandle addGEQ(ConstraintHandle c, boolean preserves_level)
  { 
    GEQHandle g = addGEQ(preserves_level);
    g.copyConstraint(g);
    return g;
  }

  public void copyConstraint(Equation eqns, Conjunct from, Equation eq) 
  {
    omegaLib.skipSetChecks++;

    RelBody this_rel = relation();
    Vector  vars     = from.mappedVars;
    int     l        = vars.size();
    
    eqns.addToCoef(0, eq.getConstant());
    if (this_rel == from.relation()) {
      for (int i = 1; i <= l; i++) {
        int c1 = eq.getCoefficient(i);
        if (c1 == 0)
          continue;

        VarDecl vc  = (VarDecl) vars.elementAt(i - 1);
        int     vk = vc.kind();
        olAssert((vk != VarDecl.FORALL_VAR) && (vk != VarDecl.EXISTS_VAR));
        if (vk == VarDecl.WILDCARD_VAR) {
          // Must add a new wildcard,
          // since they can't be used outside local Conjunct
          vc = declare();
        }
        int col = getColumn(vc);
        eqns.addToCoef(col, c1);
      }
      omegaLib.skipSetChecks--;
      return;
    }

    for (int i = 1; i <= l; i++) {
      int c1 = eq.getCoefficient(i);
      if (c1 == 0)
        continue;

      VarDecl vc = (VarDecl) vars.elementAt(i - 1);
      int     vk = vc.kind();

      switch (vk) {
      case VarDecl.FORALL_VAR:
      case VarDecl.EXISTS_VAR:
        throw new polyglot.util.InternalCompilerError("Can't copy quantified constraints across relations");
      case VarDecl.WILDCARD_VAR:
        // For each wildcard var we see, create a new wildcard
        // will lead to lots of wildcards, but Wayne likes it that way.

        vc = declare();
        break;

      case VarDecl.INPUT_VAR: // use VarDecl of corresponding position
        int posi = vc.getPosition();
        olAssert(this_rel.numberInput() >= posi);
        vc = this_rel.inputVar(posi);
        break;

      case VarDecl.OUTPUT_VAR:  // use VarDecl of corresponding position
        int poso = vc.getPosition();
        olAssert(this_rel.numberOutput() >= poso);
        vc = this_rel.outputVar(poso);
        break;

      case VarDecl.GLOBAL_VAR:  // get this Global's Var_ID in this relation
        GlobalVarDecl G = vc.getGlobalVar();
        if (G.arity() == 0)
          vc = this_rel.getLocal(G);
        else 
          vc = this_rel.getLocal(G, vc.functionOf());
        break;

      default:
        throw new polyglot.util.InternalCompilerError("copyConstraint: variable of impossible type");
      }

      int col = getColumn(vc);
      eqns.addToCoef(col, c1);
    }

    omegaLib.skipSetChecks--;
  }

  public boolean canAddChild()
  {
    return false;
  }

  public void remap()
  {
    int l = mappedVars.size();
    for (int i = 0; i < l; i++) {
      VarDecl v = (VarDecl) mappedVars.elementAt(i);
      mappedVars.setElementAt(v.getRemap(), i);
    }
    areColumnsOrdered = false;
  }

  public void beautify()
  {
    if (isTrue()) {
      parent().removeChild(this);
      parent().addAnd();
      delete();
    }
  }

  public DNF DNFize()
  {
    DNF results = new DNF(omegaLib);

    if (isTrue()) {
      simplified = true;
      verified = true;
      results.addConjunct(this);
    } else {
      results.addConjunct(this);
    }

    return results;
  }

  public void DNFizeH(Vector locals_copy)
  {
    // can simply call localize_vars for DNF with a single conjunct
    pushExists(locals_copy);
    remap();
    VarDecl.resetRemapField(myLocals);

    simplified = false; // who knows
    areColumnsOrdered = false;
  }

  public int priority()
  {
    return 1;
  }

  public Conjunct findAvailableConjunct()
  {
    return this;
  }

  public String getVarName(int col)
  {
    if (col == 0)
      return "";

    VarDecl v = (VarDecl) mappedVars.elementAt(col - 1);
    olAssert(v != null);

    StringBuffer buf = new StringBuffer(v.displayName(omegaLib));
//      buf.append('(');
//      buf.append(col);
//      buf.append(')');

    return buf.toString();
  }

  public VarDecl declare(String s)
  {
    return doDeclare(s, VarDecl.WILDCARD_VAR);
  }

  public VarDecl declare()
  {
    return doDeclare("", VarDecl.WILDCARD_VAR);
  }

  public VarDecl declare(VarDecl v)
  {
    return doDeclare(v.baseName(), VarDecl.WILDCARD_VAR);
  }

  /**
   * Add given list of wildcards S to this Conjunct.
   * Clears argument. (That's very important, otherwise those var_id's get freed)
   * Push_exists takes responsibility for reusing or deleting Var_ID's;
   * here we reuse them.  Must also empty out the Tuple when finished (joins).
   */
  protected void pushExists(Vector S)
  {
    int l = S.size();
    for (int i = 0; i < l; i++) {
      VarDecl v = (VarDecl) S.elementAt(i);
      v.changeKind(VarDecl.WILDCARD_VAR);
      myLocals.addElement(v);
    }
    areColumnsOrdered = false;
    simplified = false;
  }

  /**
   * Return column for D in conjunct.
   */
  public int getColumn(VarDecl D)
  {
    int col = findColumn(D);
    if (col == 0)    // if it does not already have a column assigned
      col = mapToColumn(D);
    if (col <= 0)
      throw new polyglot.util.InternalCompilerError("getColumn " + col); // Not substituted
    return col;
  }

  public int findColumn(VarDecl D)
  {
    olAssert(D != null);
    return problem.findColumn(1 + mappedVars.indexOf(D));
  }

  /**
   * Create new column in conjunct.
   */
  private int mapToColumn(VarDecl D)
  {
    olAssert(D != null);
    // This heavy-duty assertion says that if you are trying to use a global
    // var's local representative in a relation, that you have first told the 
    // relation that you are using it here.  PUFS requires that we know
    // all the function symbols that might be used in a relation.
    // If one wanted to be less strict, one could just tell the relation 
    // that the global variable was being used.

    if ((D.kind() == VarDecl.GLOBAL_VAR) && !relation().hasLocal(D.getGlobalVar(), D.functionOf()))
      throw new polyglot.util.InternalCompilerError("Attempt to update global var without a local variable ID");

    areColumnsOrdered = false; // extremely important
    mappedVars.addElement(D);
    return problem.mapToColumn();
  }

  protected void combineColumns()
  {
    int nvars = mappedVars.size();

    for (int i = 1; i <= nvars; i++) {
      for (int j = i + 1; j <= nvars; j++) {
        // If they are the same, copy into the higher numbered column.
        // That way we won't have problems with already-merged columns later

        if (mappedVars.elementAt(i - 1) != mappedVars.elementAt(j - 1))
          continue;

        if (omegaLib.trace) {
          System.out.print("combine_col:Actually combined ");
          System.out.print(j);
          System.out.print(",");
          System.out.println(i);
        }

        problem.combineColumns(i, j);

        // Create a wildcard with no constraints.  This is a temporary measure 
        // so we don't have to shuffle columns.

        VarDecl zero_var = declare();
        mappedVars.setElementAt(zero_var, i - 1);
        break;
      }
    }
  }

  /**
   * Reorder variables by swapping.
   * areColumnsOrdered is just a hint that thorough check needs to be done.
   */
  private void reorder()
  {
    if (areColumnsOrdered)
      return;

    int var_no     = mappedVars.size();
    int first_wild = 1;
    int last_prot  = var_no;
    while (first_wild < last_prot) {
      for (; first_wild <= var_no; first_wild++) {
        VarDecl v = (VarDecl) mappedVars.elementAt(first_wild - 1);
        if (v.kind() == VarDecl.WILDCARD_VAR)
          break;
      }

      for (; last_prot >= 1; last_prot--) {
        VarDecl v = (VarDecl) mappedVars.elementAt(last_prot - 1);
        if (v.kind() != VarDecl.WILDCARD_VAR)
          break;
      }

      if (first_wild >= last_prot)
        continue;

      problem.swapVars(first_wild, last_prot);
      problem.setVariablesInitialized(false);

      VarDecl t = (VarDecl) mappedVars.elementAt(first_wild - 1);
      mappedVars.setElementAt(mappedVars.elementAt(last_prot - 1), first_wild - 1);
      mappedVars.setElementAt(t, last_prot - 1);

      if (omegaLib.trace) {
        System.out.print("<<<OrderConjCols>>>: swapped var-s ");
        System.out.print(first_wild);
        System.out.print(" and ");
        System.out.println(last_prot);
      }
    }

    int safe_vars;
    for (safe_vars = 0; safe_vars < var_no; safe_vars++) {
      VarDecl v = (VarDecl) mappedVars.elementAt(safe_vars);
      if (v.kind() == VarDecl.WILDCARD_VAR)
        break;
    }

    for (int s = safe_vars ; s < var_no ; s++) {
      VarDecl v = (VarDecl) mappedVars.elementAt(s);
      if (v.kind() != VarDecl.WILDCARD_VAR)
        throw new polyglot.util.InternalCompilerError("Not a wildcard " + v);
    }

    problem.setNumSafeVars(safe_vars);
    areColumnsOrdered = true;
  }

  private void sortByBaseName(Vector gvars)
  {
    int gvl = gvars.size();
    for (int i = 0; i < gvl; i++) {
      for (int j = 0; j < gvl - 1; j++) {
        VarDecl vj  = (VarDecl) gvars.elementAt(j);
        VarDecl vj1 = (VarDecl) gvars.elementAt(j + 1);
        if (vj.getGlobalVar().getBaseName().compareTo(vj1.getGlobalVar().getBaseName()) < 0) {
          gvars.setElementAt(vj1, j);
          gvars.setElementAt(vj, j + 1);
        }
      }
    }
  }

  public void reorderForPrint(boolean reverseOrder, int first_pass_input, int first_pass_output, boolean sort)
  {
    Conjunct C2     = new Conjunct(this);
    Vector   newpos = new Vector();
    Vector   wcvars = new Vector();
    Vector   gvars  = new Vector();

    // We reorder the original Variable_ID's into the newpos list; later, we 
    // copy from their original column (using findColumn) to the new one.

    int n = mappedVars.size();

    // there may be more inp/outp vars than maxVars; must do dynamically
    omegaLib.skipSetChecks++;

    boolean[] input_used  = new boolean[myRelation.numberInput() + 1];
    boolean[] output_used = new boolean[myRelation.numberOutput() + 1];

    for (int i = 1; i <= n; i++) {
      VarDecl v = (VarDecl) mappedVars.elementAt(i - 1);
      int     k = v.kind();
      if (k == VarDecl.INPUT_VAR)
        input_used[v.getPosition()] = true;     
      else if (k == VarDecl.OUTPUT_VAR)
        output_used[v.getPosition()] = true;    
      else if(k == VarDecl.GLOBAL_VAR)
        gvars.addElement(v);
    }

    if (sort)
      sortByBaseName(gvars);

    newpos.addAll(gvars);

    if (!reverseOrder) {
      for (int i = 1; i <= min(myRelation.numberInput(), first_pass_input); i++)
        if (input_used[i])
          newpos.addElement(myRelation.inputVar(i));
      for (int i = 1; i <= min(myRelation.numberOutput(), first_pass_output); i++)
        if (output_used[i])
          newpos.addElement(myRelation.outputVar(i));
      for (int i = max(1, first_pass_input + 1); i <= myRelation.numberInput(); i++)
        if (input_used[i])
          newpos.addElement(myRelation.inputVar(i));
      for (int i = max(1, first_pass_output + 1); i <= myRelation.numberOutput(); i++)
        if (output_used[i])
          newpos.addElement(myRelation.outputVar(i));
    } else {
      for (int i = 1; i <= min(myRelation.numberOutput(), first_pass_output); i++)
        if (output_used[i])
          newpos.addElement(myRelation.outputVar(i));
      for (int i = 1; i <= min(myRelation.numberInput(),first_pass_input); i++)
        if (input_used[i])
          newpos.addElement(myRelation.inputVar(i));
      for (int i = max(1, first_pass_output + 1); i <= myRelation.numberOutput(); i++)
        if (output_used[i])
          newpos.addElement(myRelation.outputVar(i));
      for (int i = max(1, first_pass_input + 1); i <= myRelation.numberInput(); i++)
        if (input_used[i])
          newpos.addElement(myRelation.inputVar(i));
    }


    for (int i = 1; i <= n; i++) {
      VarDecl v = (VarDecl) mappedVars.elementAt(i - 1);
      if (v.kind() == VarDecl.WILDCARD_VAR)
        wcvars.addElement(v);
    }

    if (sort)
      sortByBaseName(gvars);

    newpos.addAll(wcvars);

    int numVars = problem.numVars();

    olAssert(numVars == newpos.size()); // i.e. no other variable types

    // Copy coef columns into new order.  Constant column is unchanged.

    problem.touchAllGEQs();
        
    for (int i = 1; i <= numVars; i++) {
      int col = findColumn((VarDecl) newpos.elementAt(i - 1)); // Find column in original conj.
      olAssert(col != 0);
      problem.copyColumn(i, C2.problem, col, 0, 0); // Copy it from orig. column in the copy.
    }

    problem.resetVarAndForwarding();

    mappedVars = newpos;
    omegaLib.skipSetChecks--;
    C2.delete();
  }

  /**
   * Wherever possible, move function symbols to input tuple.
   * This ensures that if in == out, red F(in) = x is redundant
   * with BLACK F(out) = x
   */
  private void moveUfsToInput()
  {  
    if (guaranteed_leading_0s <= 0)
      return;

    HashSet already_done = new HashSet(11);
    boolean remapped     = false;

    omegaLib.skipFinalizationCheck++;

    RelBody body = relation();

    olAssert(body != null);

    Vector v  = body.getGlobalDecls();
    int    vl = v.size();
    for (int vi = 0; vi < vl; vi++) {
      VarDecl       func = (VarDecl) v.elementAt(vi);
      GlobalVarDecl f    = func.getGlobalVar();

      if ((f.arity() <= guaranteed_leading_0s) &&
          !already_done.contains(f) &&
          body.hasLocal(f, VarDecl.INPUT_TUPLE) &&
          body.hasLocal(f, VarDecl.OUTPUT_TUPLE)) {

        already_done.add(f);

        // equatE f(in) = f(out)
        VarDecl f_in  = body.getLocal(f, VarDecl.INPUT_TUPLE);
        VarDecl f_out = body.getLocal(f, VarDecl.OUTPUT_TUPLE);

        if (f_in == f_out)
          continue;

        EQHandle e = addEQ(true);
                
        e.updateCoefDuringSimplify(f_in, -1);
        e.updateCoefDuringSimplify(f_out, 1);
                
        f_out.resetRemapField(f_in);
        remapped = true;
      }
    }

    if (remapped) {
      remap();
      combineColumns();
      VarDecl.resetRemapField(body.getGlobalDecls());
      remapped = false;
    }
        
    omegaLib.skipFinalizationCheck--;
  }

  public void countLeadingZeros()
  {
    RelBody body      = relation();
    int     max_depth = min(body.numberInput(), body.numberOutput());

    if (body.isSet()) {
      if ((guaranteed_leading_0s != -1) || (possible_leading_0s != -1))
        throw new polyglot.util.InternalCompilerError("countLeadingZeros");
      //        guaranteed_leading_0s = possible_leading_0s = -1;
      leading_dir = 0;
      return;
    }

    if (omegaLib.trace)
      assertLeadingInfo();

    if (guaranteed_leading_0s >= 0)
      return;

    int[] bounds = new int[2];
    int L;
    for (L = 1; L <= max_depth; L++) {
      VarDecl in         = body.inputVar(L);
      VarDecl out        = body.outputVar(L);
      boolean guaranteed = queryDifference(out, in, bounds);
      int     min        = bounds[0];
      int     max        = bounds[1];

      if ((min >= 0) && (max <= 0))
        continue;

      if ((min <= 0) && (max >= 0))
        break;

      // We know guaranteed & possible.

      guaranteed_leading_0s = L - 1;
      possible_leading_0s   = L - 1;

      if (min > 0)      // We know its 0,..,0,+
        leading_dir = 1;
      else              // We know its 0,..,0,-
        leading_dir = -1;

      return;
    }

    guaranteed_leading_0s = L - 1;

    for ( ; L <= max_depth; L++) {
      VarDecl in         = body.inputVar(L);
      VarDecl out        = body.outputVar(L);
      boolean guaranteed = queryDifference(out, in, bounds);
      int     min        = bounds[0];
      int     max        = bounds[1];
            
      if ((min > 0) || (max < 0))
        break;
    }

    possible_leading_0s = L - 1;

    if (omegaLib.trace)
      assertLeadingInfo();
  }

  public void enforceLeadingInfo(int guaranteed, int possible, int dir)
  {
    omegaLib.skipFinalizationCheck++;
    guaranteed_leading_0s = guaranteed;

    int d = min(relation().numberInput(),relation().numberOutput());

    olAssert(0 <= guaranteed);
    olAssert(guaranteed <= possible);
    olAssert(possible <= d);

    for (int i = 1; i <= guaranteed; i++) {
      EQHandle e = addEQ(false);
      e.updateCoefDuringSimplify(myRelation.inputVar(i), -1);
      e.updateCoefDuringSimplify(myRelation.outputVar(i), 1);
    }

    if ((guaranteed == possible) && (guaranteed >= 0) && (possible + 1 <= d) && (dir != 0)) {
      GEQHandle g = addGEQ(false);
      if (dir > 0) {
        g.updateCoefDuringSimplify(myRelation.inputVar(possible + 1), -1);
        g.updateCoefDuringSimplify(myRelation.outputVar(possible + 1), 1);
      } else {
        g.updateCoefDuringSimplify(myRelation.inputVar(possible + 1), 1);
        g.updateCoefDuringSimplify(myRelation.outputVar(possible + 1), -1);
      }
      g.updateConstantDuringSimplify(-1);
      possible_leading_0s = possible;
 
      leading_dir = dir;
    } else {
      possible_leading_0s = d;
      leading_dir = 0;
    }

    omegaLib.skipFinalizationCheck--;

    if (omegaLib.trace)
      assertLeadingInfo();
  }

  public void invalidateLeadingInfo(int changed)
  {
    if (changed == -1) {
      guaranteed_leading_0s = -1;
      possible_leading_0s   = -1;
      leading_dir           =  0;
    } else {
      int d = min(relation().numberInput(), relation().numberOutput());
      olAssert((1 <= changed) && (changed <= d));

      if (possible_leading_0s == changed - 1)
        possible_leading_0s  = d;

      guaranteed_leading_0s = min(guaranteed_leading_0s,changed-1);
    }

    if (omegaLib.trace)
      assertLeadingInfo();
  }

  public void reverseLeadingDirInfo()
  {
    leading_dir *= -1;
  }

  /*
   * Remove BLACK constraints from the problem.
   * Make all the remaining red constraints BLACK.
   */
  public void removeColorConstraints()
  {
    possible_leading_0s   = -1;
    guaranteed_leading_0s = -1;
    leading_dir           = 0;

    problem.removeColorConstraints();
  }

  public void orderedElimination(int symLen)
  {
    problem.orderedElimination(symLen);
  }

  public void convertEQstoGEQs()
  { 
    simplifyConjunct(true, 1, Problem.BLACK);
    problem.convertEQstoGEQs(false);
  }

  /**
   * Cost = # of terms in DNF when negated.
   * Cost = CantBeNegated if too bad (i.e. bad wildcards).
   * Cost =  AvoidNegating if it would be inexact.
   *
   * Also check presLegalNegations --
   * <ul>
   * <li>If set to ANY_NEGATION, just return the number
   * <li>If set to ONE_GEQ_OR_STRIDE, return CantBeNegated if c > 1
   * <li>If set to ONE_GEQ_OR_EQ, return CantBeNegated if not a single geq or eq
   * </ul>
   */
  public int cost()
  {
    int        wc_j    = 0;
    int        numVars = problem.numVars();
    boolean[]  isWild  = new boolean[numVars + 1];
    Equation[] geqs    = problem.getGEQs();
    int        ngeqs   = problem.getNumGEQs();
    Equation[] eqs     = problem.getEQs();
    int        neqs    = problem.getNumEQs();
    int        c       = ngeqs;

    // cost 1 per GEQ, and if 1 GEQ has wildcards, +2 for each of them

    for (int j = 1; j <= numVars; j++) {
      VarDecl v = (VarDecl) mappedVars.elementAt(j - 1);
      isWild[j] = (v.kind() == VarDecl.WILDCARD_VAR);
    }

    for (int i = 0;  i < ngeqs; i++) {
      int      wc_no = 0;
      Equation eq    = geqs[i];

      for (int j = 1; j <= numVars; j++) {
        if (eq.isZero(j))
          continue;

        if (!isWild[j])
          continue;

        wc_no++;
        c += 2;
        wc_j = j;
      }

      if (wc_no > 1)
        return CantBeNegated;
    }

    for (int i = 0; i < neqs; i++) {
      int      wc_no = 0;
      Equation eq    = eqs[i];
      for (int j = 1; j <= numVars; j++) {
        if (eq.isZero(j))
          continue;

        if (isWild[j]) {
          wc_no++;
          wc_j = j;
        }
      }

      if (wc_no == 0) { // no wildcards
        c += 2;
        continue;
      }

      if (wc_no == 1) { // one wildcard - maybe we can negate it
        if (problem.isNotEQZero(i, wc_j)) // We are not ready to handle  
          return CantBeNegated;

        // Stride constraint

        c++;
        continue;
      }

      // Multiple wildcards

      return CantBeNegated;
    }

    if (!exact)
      return AvoidNegating; 

    if (omegaLib.presLegalNegations == ANY_NEGATION)
      return c;

      // single GEQ ok either way as long as no wildcards
      // (we might be able to handle wildcards, but I haven't thought about it)

    if ((neqs == 0) && (ngeqs <= 1)) {
      if (c > 1) { // the GEQ had a wildcard -- I'm not ready to go here.
        if (omegaLib.trace)
          System.out.println("Refusing to negate a GEQ with wildcard(s) under restricted_negation; it may be possible to fix this.");

        return CantBeNegated;
      }
      return c;
    }

    if ((neqs == 1) && (ngeqs == 0)) {
      olAssert((c == 1) || (c == 2));

      if (omegaLib.presLegalNegations == ONE_GEQ_OR_STRIDE) {
        if (c == 1)
          return c; // stride constraint is ok

        if (omegaLib.trace)
          System.out.println("Refusing to negate a non-stride EQ under current presLegalNegations.");

        return CantBeNegated;

      }
      olAssert(omegaLib.presLegalNegations == ONE_GEQ_OR_EQ);
      return c;
    }

    if (omegaLib.trace)
      System.out.println("Refusing to negate multiple constraints under current presLegalNegations.");

    return CantBeNegated;
  }
  
  public boolean simplifyProblem(boolean verify, int redundantElimination)
  {
    if (verified)
      verify = false;

    boolean result = problem.simplifyProblem(verify, redundantElimination);

    if (!result && !exact)
      exact = true;

    olAssert(!(verified && verify && !result));

    if (verify && result)
      verified = true;
    else if (!result)
      verified = false;

    return result;
  }

  // not as confident about this one as the previous:
  public int redSimplifyProblem(int effort, boolean computeGist)
  {
    int result = problem.redSimplifyProblem(effort, computeGist);
    if ((result == Problem.redFalse) && !exact)
      exact = false;
    return result;
  }

  /*
   * Make a new wildcard variable, return WC number.
   */
  private int newWC(Problem p)
  {
    VarDecl wc    = declare();
    int     wc_no = mapToColumn(wc);
    return wc_no;
  }

  /**
   * Merge CONJ1 & CONJ2 => CONJ.
   * Action:
   * <ul>
   * <li> MERGE_REGULAR or MERGE_COMPOSE: regular merge.
   * <li> MERGE_GIST    make constraints from conj2 red, i.e.
   *                        Gist Conj2 given Conj1 (T.S. comment). 
   * </ul>
   * Reorder columns as we go.
   * Merge the columns for identical variables.  
   * We assume we know nothing about the ordering of conj1, conj2.
   * <p>
   * Does not consume its arguments
   * <p>
   * Optional 4th argument gives the relation for the result - if
   *  null, conj1 and conj2 must have the same relation, which will
   *  be used for the result
   * <p>
   * The only members of conj1 and conj2 that are used are: problem,
   *  mappedVars and declare(), and the leading_0s/leading_dir members
   *  and exact.
   * <p>
   * NOTE: variables that are shared between conjuncts are necessarily 
   * declared above, not here; so we can simply create columns for the 
   * locals of each conj after doing the protected vars.  
   */
  public Conjunct mergeConjuncts(Conjunct conj2, int action, RelBody body)
  {
    // body must be set unless both conjuncts are from the same relation
    olAssert((body != null) || (relation() == conj2.relation()));

    if ((body == relation()) && (body == conj2.relation()))
      body = null;  // we test this later to see if there is a new body

    Conjunct conj3 = new Conjunct(omegaLib, null, (body != null) ? body : conj2.relation());
    Problem  p1    = problem;
    Problem  p2    = conj2.problem;
    Problem  p3    = conj3.problem;

    if (action != MERGE_COMPOSE) {
      assertLeadingInfo();
      conj2.assertLeadingInfo();
    }

    if (omegaLib.trace) {
      omegaLib.useUglyNames++;
      System.out.print(">>> Merge conjuncts: Merging");
      System.out.print((action == MERGE_GIST ? " for gist" : (action == MERGE_COMPOSE ? " for composition" : "")));
      System.out.print(":");
      prefixPrint(true);
      conj2.prefixPrint(true);
      System.out.println("");
      omegaLib.useUglyNames--;
    }

    if (action == MERGE_GIST)
      conj3.exact = conj2.exact;
    else
      conj3.exact = exact && conj2.exact;

    if (action == MERGE_COMPOSE) {
      conj3.guaranteed_leading_0s = min(guaranteed_leading_0s, conj2.guaranteed_leading_0s);
      conj3.possible_leading_0s   = min(possible_leading_0s, conj2.possible_leading_0s);

      if (conj3.guaranteed_leading_0s > conj3.possible_leading_0s)
        throw new polyglot.util.InternalCompilerError("mergeConjuncts " + conj3.guaranteed_leading_0s + " > " + conj3.possible_leading_0s);

      // investigate leading_dir - not well tested code
      if ((guaranteed_leading_0s < 0) || (conj2.guaranteed_leading_0s < 0)) {
        conj3.leading_dir = 0;
      } else if (guaranteed_leading_0s == conj2.guaranteed_leading_0s) {
        if (leading_dir == conj2.leading_dir)
          conj3.leading_dir = leading_dir;
        else
          conj3.leading_dir = 0;
      } else if (guaranteed_leading_0s < conj2.guaranteed_leading_0s) {
        conj3.leading_dir = leading_dir;
      } else { // (guaranteed_leading_0s > conj2.guaranteed_leading_0s)
        conj3.leading_dir = conj2.leading_dir;
      }

      if (conj3.leading_dir == 0)
        conj3.possible_leading_0s = min(conj3.relation().numberInput(), conj3.relation().numberOutput());

      if ((conj3.guaranteed_leading_0s > conj3.possible_leading_0s) || ((conj3.guaranteed_leading_0s != conj3.possible_leading_0s) && (0 != conj3.leading_dir)))
        throw new polyglot.util.InternalCompilerError("mergeConjuncts " + conj3.guaranteed_leading_0s + " > " + conj3.possible_leading_0s);

    } else if (body == null) { // if body is set, who knows what leading 0's mean?
      olAssert((action == MERGE_REGULAR) || (action == MERGE_GIST));

      boolean feasable = true;

      int redAndBlackGuarLeadingZeros = max(guaranteed_leading_0s, conj2.guaranteed_leading_0s);
      if (action == MERGE_REGULAR) 
        conj3.guaranteed_leading_0s = redAndBlackGuarLeadingZeros;
      else
        conj3.guaranteed_leading_0s = guaranteed_leading_0s;

      conj3.possible_leading_0s = min(possible_leading_0s, conj2.possible_leading_0s);

      if (conj3.possible_leading_0s < redAndBlackGuarLeadingZeros)
        feasable = false;
      else if ((conj3.guaranteed_leading_0s == -1) || (conj3.possible_leading_0s > redAndBlackGuarLeadingZeros))
        conj3.leading_dir = 0;
      else {
        if (guaranteed_leading_0s == conj2.guaranteed_leading_0s) {
          if (!leadingDirValidAndKnown())
            conj3.leading_dir = conj2.leading_dir;
          else if (!conj2.leadingDirValidAndKnown())
            conj3.leading_dir = leading_dir;
          else if (leading_dir * conj2.leading_dir > 0)
            conj3.leading_dir = leading_dir; // 1,2 same dir
          else
            feasable = false;  // 1 and 2 go in opposite directions
        } else if (conj3.possible_leading_0s != conj3.guaranteed_leading_0s)
          conj3.leading_dir = 0;
        else if (guaranteed_leading_0s<conj2.guaranteed_leading_0s) {
          if (leadingDirValidAndKnown())
            throw new polyglot.util.InternalCompilerError("leading_dir is valid and known");
          conj3.leading_dir = conj2.leading_dir;
        } else {
          if (conj2.leadingDirValidAndKnown())
            throw new polyglot.util.InternalCompilerError("conj2.leading_dir is valid and known");
          conj3.leading_dir = leading_dir;
        }
      }

      if (!feasable) {
        if (omegaLib.trace)
          System.out.println(">>> Merge conjuncts: quick check proves FALSE.");

        // return 0 = 1

        Equation eq = p3.getNewEQ();
        eq.set(Problem.BLACK, 0, true);
        eq.setConstant(1);

        // Make sure these don't blow later assertions
        conj3.possible_leading_0s = -1;
        conj3.guaranteed_leading_0s = -1;
        conj3.leading_dir = 0;

        return conj3;
      }
    } else { // provided "body" argument but not composing, leading 0s meaningless
      conj3.guaranteed_leading_0s = -1;
      conj3.possible_leading_0s = -1;
      conj3.leading_dir = 0;
    }

    // initialize omega stuff

    p3.merge(action, this, conj2, conj3, conj3.mappedVars);

    conj3.areColumnsOrdered = true;
    conj3.simplified        = false;
    conj3.verified          = false;
    
    if (omegaLib.trace) {
      omegaLib.useUglyNames++;
      System.out.print(">>> Merge conjuncts: result is:");
      conj3.prefixPrint(true);
      omegaLib.useUglyNames--;

      conj3.assertLeadingInfo();
    }

    return conj3;
  }

  /**
   *  ~CONJ => DNF
   */
  public DNF negateConjunct()
  {
    if (omegaLib.trace) {
      System.out.println("%%%%%% negateConjunct IN %%%%%%");
      prefixPrint();
      System.out.println("");
    }

    DNF        new_dnf = new DNF(omegaLib);
    Equation[] geqs    = problem.getGEQs();
    int        ngeqs   = problem.getNumGEQs();
    Equation[] eqs     = problem.getEQs();
    int        neqs    = problem.getNumEQs();

    if (!isExact())
      new_dnf.addConjunct(new Conjunct(this));

    Conjunct true_part = new Conjunct(this);
    true_part.setParent(null);
    true_part.invalidateLeadingInfo(-1);

    Problem   tp       = true_part.problem;
    int       numVars  = problem.numVars();
    int[]     wildCard = new int[ngeqs];
    boolean[] handleIt = new boolean[numVars + 1];

    for (int i = 0; i < ngeqs; i++) {
      wildCard[i] = 0;
      Equation eq = geqs[i];
      for (int j = 1; j <= numVars; j++) {
        int cj = eq.getCoefficient(j);
        if (cj == 0)
          continue;

        VarDecl v = (VarDecl) mappedVars.elementAt(j - 1);

        if (v.kind() != VarDecl.WILDCARD_VAR)
          continue;

        olAssert(wildCard[i] == 0);
        handleIt[j] = true;

        if (cj > 0)
          wildCard[i] = j;
        else
          wildCard[i] = -j;
      }
    }

    for (int i = 0; i < ngeqs; i++) {
      if (wildCard[i] != 0)
        continue;

      Equation eqp = geqs[i];

      /* ~(ax + by + c >= 0) = (-ax -by -c-1 >= 0) */

      Conjunct new_conj = new Conjunct(true_part);
      Problem  np       = new_conj.problem;

      new_conj.exact = true;

      int c0  = eqp.getConstant();

      Equation eqnp = np.getNewGEQ();
      eqnp.setConstant(-c0 - 1);
      Equation eqtp = tp.getNewGEQ();
      eqtp.setConstant(c0);

      olAssert(numVars == mappedVars.size());

      for (int j = 1; j <= numVars; j++) {
        VarDecl v  = (VarDecl) mappedVars.elementAt(j - 1);
        int     cj = eqp.getCoefficient(j);

        if ((v.kind() == VarDecl.WILDCARD_VAR) && (cj != 0))
          throw new polyglot.util.InternalCompilerError("negateConjunct: wildcard in inequality");

        eqnp.setCoef(j, -cj);
        eqtp.setCoef(j, cj);
      }

      new_dnf.addConjunct(new_conj);
    }

    for (int i = 0; i < neqs; i++) {
      int      wc_no = 0;
      int      wc_j  = 0;
      Equation eqp   = eqs[i];

      for (int j = 1; j <= numVars; j++) {
        VarDecl v = (VarDecl) mappedVars.elementAt(j - 1);
        if ((v.kind() == VarDecl.WILDCARD_VAR) && eqp.isNotZero(j)) {
          wc_no++;
          wc_j = j;
        }
      }

      if (wc_no != 0) {
        olAssert(!handleIt[wc_j]);

        if (problem.isNotEQZero(i, wc_j))
          throw new polyglot.util.InternalCompilerError("Too many non-zero coefficients");

        olAssert(wc_no == 1, "negateConjunct: more than 1 wildcard in equality");

        // === Negating equality with a wildcard for K>0 ===
        // ~(exists v st expr + K v  + C = 0) =
        //  (exists v st 1 <= - expr - K v - C <= K-1)

        Conjunct nc = new Conjunct(true_part);
        Problem  np = nc.problem;
        nc.exact = true;

        // -K alpha = expr  <==>  K alpha = expr
        int cwc_j = eqp.getCoefficient(wc_j);
        if (cwc_j < 0) {
          cwc_j = -cwc_j;
          eqp.setCoef(wc_j, cwc_j);
        }

        if (cwc_j == 2) {
          // ~(exists v st  expr +2v +C = 0) =
          //  (exists v st -expr -2v -C = 1)
          // That is (expr +2v +C+1 = 0)
          Equation eqnp = np.getNewEQ();
          eqnp.setConstant(eqp.getConstant() + 1);
          eqnp.copyCoefs(eqp, numVars, 1);
        } else {
          // -expr -Kv -C-1 >= 0

          Equation eqnp    = np.getNewGEQ();
          eqnp.setConstant(-eqp.getConstant() + 1);
          eqnp.negateCoefs(eqp, numVars, 1);

          // +expr +Kv +C+K-1 >= 0

          Equation eqnpe = np.getNewGEQ();
          eqnpe.setConstant(eqp.getConstant() + cwc_j - 1);
          eqnpe.copyCoefs(eqp, numVars, 1);
        }

        new_dnf.addConjunct(nc);

      } else {
        /* ~(ax + by + c = 0) = (-ax -by -c-1 >= 0) Or (ax + by + c -1 >= 0) */
        Conjunct nc1 = new Conjunct(true_part);
        Conjunct nc2 = new Conjunct(true_part);
        Problem  np1 = nc1.problem;
        Problem  np2 = nc2.problem;

        nc1.invalidateLeadingInfo(-1);
        nc2.invalidateLeadingInfo(-1);
        nc1.exact = true;
        nc2.exact = true;

        Equation eqe1 = np1.getNewGEQ();
        Equation eqe2 = np2.getNewGEQ();
        int      x    = eqp.getConstant();

        eqe1.setConstant(-x - 1);
        eqe2.setConstant(x - 1);

        eqe1.negateCoefs(eqp, numVars, 1);
        eqe2.copyCoefs(eqp, numVars, 1);
        new_dnf.addConjunct(nc1);
        new_dnf.addConjunct(nc2);
      }

      Equation eqtp = tp.getNewEQ();
      eqtp.copyCoefs(eqp, numVars + 1);
    }

    for (int j = 1; j <= numVars; j++) {
      if (!handleIt[j])
        continue;

      for(int i = 0; i < ngeqs; i++) {
        if (wildCard[i] != j)
          continue;

        Equation eqi = geqs[i];
        for (int k = 0; k < ngeqs; k++) {
          if (wildCard[k] != -j)
            continue;

          Equation eqk = geqs[k];

          // E_i <= c_i alpha
          //        c_k alpha <= E_k
          // c_k E_i <= c_i c_k alpha <= c_i E_k
          // c_k E_i <= c_i c_k floor (c_i E_k / c_i c_k)
          // negating:
          // c_k E_i > c_i c_k floor (c_i E_k / c_i c_k)
          // c_k E_i > c_i c_k beta > c_i E_k - c_i c_k
          // c_k E_i - 1 >= c_i c_k beta >= c_i E_k - c_i c_k + 1

          Conjunct new_conj = new Conjunct(true_part);
          Problem  np       = new_conj.problem;
          int      c_k      = -eqk.getCoefficient(j);
          int      c_i      = eqi.getCoefficient(j);

          olAssert(c_k > 0);
          olAssert(c_i > 0);

          new_conj.exact = true;

          Equation eqne = np.getNewGEQ();

          // c_k E_i - 1 >= c_i c_k beta 

          eqne.multCoefs(eqi, numVars, -c_k);
          eqne.setCoef(j, -c_i * c_k);
          eqne.addToCoef(0, -1);

          Equation eqnee = np.getNewEQ();

          // c_i c_k beta >= c_i E_k - c_i c_k + 1
          // c_i c_k beta + c_i c_k -1 >= c_i E_k 

          eqnee.multCoefs(eqk, numVars, -c_i);
          eqnee.setCoef(j, c_i * c_k);
          eqnee.addToCoef(0, c_i * c_k - 1);

          new_dnf.addConjunct(new_conj);
        }
      }
    }

    if (omegaLib.trace) {
      System.out.println("%%%%%% negateConjunct OUT %%%%%%");
      new_dnf.prefixPrint();
    }

    true_part.delete();

    return new_dnf;
  }

  private boolean checkSubseqN(Vector evac_from, Vector evac_to, int n_from, int n_to, int max_arity, int n, boolean allow_offset)
  {
    // check each position v to see if from[v] == to[v+n] (+ offset)
    
    olAssert(max_arity + n <= n_to);

    for (int v = 1; v <= max_arity; v++) {
      // first, get rid of possible interlopers:

      Conjunct d = new Conjunct(this);
      Problem prob = d.problem;
      for (int tv = 1; tv <= n_to; tv++) {
        if (tv != v + n) {
          int col = d.findColumn((VarDecl) evac_to.elementAt(tv));
          if (col > 0)
            prob.trytoSub(col);
        }
      }
      for (int fv = 1; fv <= n_from; fv++) {
        if (fv != v) {
          int col = d.findColumn((VarDecl) evac_from.elementAt(fv));
          if (col > 0)
            d.problem.trytoSub(col);
        }
      }

      int c_to = d.findColumn((VarDecl) evac_to.elementAt(v + n));
      int c_from = d.findColumn((VarDecl) evac_from.elementAt(v));

      olAssert(c_to > 0);
      olAssert(c_from > 0);
      olAssert(c_to != c_from);

      // now, just look for an equality c_to = c_from + offset

      if (!prob.findEquality(c_to, c_from, allow_offset ? 1 : 0))
        return false;  // no EQ did what we need
    }

    return true;
  }

  private void assertSubbedSyms()
  {
    // where possible, symbolic constants must have been subbed out
    Vector gv = relation().getGlobalDecls();
    int    l  = gv.size();
    for (int v = 1; v <= l; v++) {
      int col = findColumn((VarDecl) gv.elementAt(v));
      if (col > 0)
        olAssert(!problem.trytoSub(col));
    }
  }

  private boolean checkOffset(Vector /* VarDecl */ evac_from, Vector evac_to, int n_from, int n_to, int max_arity)
  {
    assertSubbedSyms();

    return checkSubseqN(evac_from,evac_to,n_from, n_to,max_arity, 0, true);
  }

  private boolean checkSubseq(Vector evac_from, Vector evac_to, int n_from, int n_to, int max_arity)
  {
    assertSubbedSyms();

    for (int i = 0; i <= n_to - max_arity; i++)
      if (checkSubseqN(evac_from, evac_to, n_from, n_to,max_arity, i, false))
        return true;

    return false;
  }

  private boolean checkOffsetSubseq(Vector evac_from, Vector evac_to, int n_from, int n_to, int max_arity)
  {
    assertSubbedSyms();

    for (int i = 0; i <= n_to - max_arity; i++)
      if (checkSubseqN(evac_from, evac_to, n_from,n_to, max_arity, i, true))
        return true;

    return false;
  }

  private boolean checkAffine(Vector evac_from, Vector evac_to, int n_from, int n_to, int max_arity)
  {
    Conjunct c = new Conjunct(this);
    c.assertSubbedSyms();

    // try to find substitutions for all evac_to variables
    for (int v = 1; v <= max_arity; v++) {
      int col = c.findColumn((VarDecl) evac_to.elementAt(v));
      if (col > 0)
        c.problem.trytoSub(col);
    }
    // any that didn't have substitutions, aren't affine
    for (int v = 1; v <= max_arity; v++)
      if (c.findColumn((VarDecl) evac_to.elementAt(v)) >= 0) {
        return false;
      }

    // FERD - disallow symbolic constants?
    return true;
  }

  /**
   * @param in_out is true if studying the input => output direction
   */
  private int study(boolean in_out, RelBody rel, int max_arity)
  {
    olAssert(max_arity > 0);
    olAssert(max_arity <= relation().numberInput());
    olAssert(max_arity <= relation().numberOutput());

    Vector evac_from = omegaLib.inputVars();
    Vector evac_to   = omegaLib.inputVars();
    int    n_from    = rel.numberInput();
    int    n_to      = rel.numberOutput();

    if (!in_out) {
      Vector t = evac_from;
      evac_from = evac_to;
      evac_to = t;
      int x = n_from;
      n_from = n_to;
      n_to = x;
    }

    int ret = EVAC_NASTY;

    if (queryGuaranteedLeadingZeros() >= max_arity)
      ret = EVAC_TRIVIAL;
    else {
      Conjunct c = new Conjunct(this);
      olAssert(c.relation() == relation());

      if (omegaLib.trace) {
        System.out.print("About to study ");
        System.out.print(in_out ? "In-.Out" : "Out-.In");
        System.out.println(" evacuation for conjunct");
        omegaLib.useUglyNames++;
        prefixPrint();
        omegaLib.useUglyNames--;
      }

      boolean sat = c.simplifyConjunct(true, 4, Problem.BLACK);
      olAssert(sat);  // else c is deleted

      // Substitute out all possible symbolic constants
      olAssert(c.problem.isNoSubs());
      
      Vector gv = relation().getGlobalDecls();
      int l = gv.size();
      for (int v = 1; v <= l; v++) {
        int col = c.findColumn((VarDecl) gv.elementAt(v));
        if (col > 0)
          c.problem.trytoSub(col);
      }

      if (c.checkOffset(evac_from, evac_to, n_from, n_to, max_arity))
        ret = EVAC_OFFSET;
      else if (c.checkSubseq(evac_from, evac_to, n_from, n_to, max_arity))
        ret = EVAC_SUBSEQ;
      else if (c.checkOffsetSubseq(evac_from, evac_to, n_from, n_to, max_arity))
        ret = EVAC_OFFSET_subseq;
      else if (c.checkAffine(evac_from, evac_to, n_from, n_to, max_arity))
        ret = EVAC_AFFINE;
    }

    if (omegaLib.trace) {
      if ((ret != EVAC_TRIVIAL) && (ret != EVAC_NASTY)) {
        System.out.print("Studied ");
        System.out.print(in_out ? "In-.Out" : "Out-.In");
        System.out.println(" evacuation for conjunct");
        omegaLib.useUglyNames++;
        prefixPrint();
        omegaLib.useUglyNames--;
      }

      System.out.println("Saw evacuation type " + evacNames[ret]);
    }

    return ret;
  }

  public void reorderAndSimplify(RelBody r, boolean stridesAllowed)
  {
    problem.touchAllGEQs();

    reorder();
    if (!problem.simplifyApproximate(stridesAllowed)) 
      r.simplifiedDNF().removeConjunct(this);
    else {
      simplifyProblem(true, 1);
      recomputeMappedVars();
      problem.setVariablesInitialized(false);
    }
  }

  public void domain(RelBody r, int a)
  {
    Vector remapped = new Vector();
    int    cL0      = guaranteed_leading_0s;

    int l = mappedVars.size();
    for (int vi = 0; vi < l; vi++) {
      VarDecl func = (VarDecl) mappedVars.elementAt(vi);
      if (func.kind() != VarDecl.GLOBAL_VAR)
        continue;

      GlobalVarDecl f = func.getGlobalVar();
      if ((f.arity() > 0) && (func.functionOf() == VarDecl.OUTPUT_TUPLE)) {
        if (cL0 >= f.arity()) {
          func.resetRemapField(r.getLocal(f, VarDecl.INPUT_TUPLE));
        } else {
          func.resetRemapField(declare());
          makeInexact();
        }
        remapped.addElement(func);
      }
    }

    remap();
    VarDecl.resetRemapField(remapped);
            
    guaranteed_leading_0s = -1;
    possible_leading_0s = -1;
    leading_dir = 0;
  }

  public void range(RelBody r, int a)
  {
    Vector remapped = new Vector();
    int    cL0      = guaranteed_leading_0s;

    int l = mappedVars.size();
    for (int vi = 0; vi < l; vi++) {
      VarDecl func = (VarDecl) mappedVars.elementAt(vi);
      if (func.kind() != VarDecl.GLOBAL_VAR)
        continue;

      GlobalVarDecl f = func.getGlobalVar();
      if ((f.arity() > 0) && (func.functionOf() == VarDecl.INPUT_TUPLE)) {
        if (cL0 >= f.arity()) {
          func.resetRemapField(r.getLocal(f, VarDecl.OUTPUT_TUPLE));
        } else {
          func.resetRemapField(declare());
          makeInexact();
        }
        remapped.addElement(func);
      }
    }

    remap();
    VarDecl.resetRemapField(remapped);

    guaranteed_leading_0s = -1;
    possible_leading_0s   = -1;
    leading_dir           = 0;
  }

  public void after(RelBody r, int preservedPositions)
  {
    Vector remapped = new Vector();
    int    cL0      = guaranteed_leading_0s;

    int l = mappedVars.size();
    for (int vi = 0; vi < l; vi++) {
      VarDecl func = (VarDecl) mappedVars.elementAt(vi);
      if (func.kind() != VarDecl.GLOBAL_VAR)
        continue;

      GlobalVarDecl f = func.getGlobalVar();
      if ((f.arity() > preservedPositions) && (func.functionOf() == VarDecl.OUTPUT_TUPLE)) {
        if (cL0 >= f.arity()) {
          func.resetRemapField(r.getLocal(f, VarDecl.INPUT_TUPLE));
        } else {
          func.resetRemapField(declare());
          makeInexact();
        }
        remapped.addElement(func);
      }
    }

    remap();
    VarDecl.resetRemapField(remapped);
            
    guaranteed_leading_0s = -1;
    possible_leading_0s = -1;
    leading_dir = 0;
  }

  public void deltas()
  {
    Vector remapped = new Vector();
    int    cL0      = guaranteed_leading_0s;

    int l = mappedVars.size();
    for (int vi = 0; vi < l; vi++) {
      VarDecl func = (VarDecl) mappedVars.elementAt(vi);
      if (func.kind() != VarDecl.GLOBAL_VAR)
        continue;

      GlobalVarDecl f = func.getGlobalVar();
      if (f.arity() > 0) {
        func.resetRemapField(declare());
        makeInexact();
        remapped.addElement(func);
      }
    }

    remap();
    VarDecl.resetRemapField(remapped);
  }

  public boolean checkLeading0s(int direction, int level)
  {
    return (((direction == 0) && (guaranteed_leading_0s >= level)) ||
            ((guaranteed_leading_0s == level - 1) &&
             leadingDirValidAndKnown() &&
             (leading_dir * direction > 0)));
  }

  public boolean checkLeading0s(int level)
  {
    return ((((guaranteed_leading_0s >= level) ||
              (guaranteed_leading_0s == possible_leading_0s))) &&
            (possible_leading_0s >= 0));
  }

  public void remapDNFVars(RelBody ref_rel)
  {
    int mvl = mappedVars.size();
    for (int j = 0; j < mvl; j++) {
      VarDecl v = (VarDecl) mappedVars.elementAt(j);

      switch(v.kind()) {
      case VarDecl.INPUT_VAR:
        olAssert(ref_rel.numberInput() >= v.getPosition());
        break;
      case VarDecl.OUTPUT_VAR:
        olAssert(ref_rel.numberOutput() >= v.getPosition());
        break;
      case VarDecl.GLOBAL_VAR:
        // The assignment is a noop, but tells ref_rel that the global may be
        // used inside it, which is required.
        mappedVars.setElementAt(ref_rel.getLocal(v.getGlobalVar(), v.functionOf()), j);
        break;
      case VarDecl.WILDCARD_VAR:
        break;
      default:
        throw new polyglot.util.InternalCompilerError("bad variable kind");
      }
    }
  }

  public void extractNonWildVars(Vector allVars)
  {
    int vl = mappedVars.size();
    for (int k = 0; k < vl; k++) {
      VarDecl vi = (VarDecl) mappedVars.elementAt(k);
      if ((vi.kind() != VarDecl.WILDCARD_VAR) && !allVars.contains(vi))
        allVars.addElement(vi);
    }
  }

  public void makeLevelCarriedTo(DNF newstuff, int split_to, int level)
  {
    RelBody body = relation();

    olAssert(body != null);

    boolean is_guaranteed = verified;

    int[] bounds = new int[2];
    for (int leading_eqs = 1; leading_eqs <= split_to; leading_eqs++) {
      if ((leading_eqs > possible_leading_0s) && leadingDirValidAndKnown())
        break;

      if (leading_eqs > guaranteed_leading_0s) {
        VarDecl in         = body.inputVar(leading_eqs);
        VarDecl out        = body.outputVar(leading_eqs);
        boolean guaranteed = queryDifference(out, in, bounds);
        int     min        = bounds[0];
        int     max        = bounds[1];

        if ((min > 0) || (max < 0))
          guaranteed = true;

        if (!guaranteed)
          is_guaranteed = false;

        boolean generateLTClause = min < 0;
        boolean generateGTClause = max > 0;
        boolean retainEQClause   = ((leading_eqs <= possible_leading_0s) && (min <= 0) && (max >= 0));

        if (!(generateLTClause || generateGTClause || retainEQClause)) { // conjunct is infeasible
          if (omegaLib.trace) {
            System.out.println("Conjunct discovered to be infeasible during makeLevelCarriedTo(");
            System.out.print(level);
            System.out.println("):");
            prefixPrint(true);
          }

          Conjunct cpy = new Conjunct(this);
          olAssert(!cpy.simplifyConjunct(true, 32767, 0));
        }

        if (generateLTClause) {
          Conjunct lt = this;
          if (generateGTClause || retainEQClause)
            lt = new Conjunct(this);

          if (max >= 0) {
            GEQHandle lh = lt.addGEQ(false); // out < in ==> in - out - 1>= 0
            lh.updateCoefDuringSimplify(in, 1);
            lh.updateCoefDuringSimplify(out, -1);
            lh.updateConstantDuringSimplify(-1);
          }

          lt.guaranteed_leading_0s = leading_eqs - 1;
          lt.possible_leading_0s   = leading_eqs - 1;
          lt.leading_dir           = -1;

          if (is_guaranteed) {
            lt.promiseThatUbSolutionsExist();
          } else if (false) {
            System.out.println("Can't guaranteed solutions to:\n");
            omegaLib.useUglyNames++;
            lt.prefixPrint();
            omegaLib.useUglyNames--;
          }

          if (generateGTClause || retainEQClause)
            newstuff.addConjunct(lt);
        }

        if (generateGTClause) {
          Conjunct gt = this;
          if (retainEQClause)
            gt = new Conjunct(this);

          if (min <= 0) {
            GEQHandle g = gt.addGEQ(false); // out>in ==> out-in-1>=0
            g.updateCoefDuringSimplify(in, -1);
            g.updateCoefDuringSimplify(out, 1);
            g.updateConstantDuringSimplify(-1);
          }

          gt.guaranteed_leading_0s = leading_eqs - 1;
          gt.possible_leading_0s   = leading_eqs - 1;
          gt.leading_dir           = 1;

          if (is_guaranteed) {
            gt.promiseThatUbSolutionsExist();
          } else if (false) {
            System.out.println("Can't guaranteed solutions to:\n");
            omegaLib.useUglyNames++;
            gt.prefixPrint();
            omegaLib.useUglyNames--;
          }

          if (retainEQClause)
            newstuff.addConjunct(gt);
        }

        if (!retainEQClause)
          break;

        olAssert((min <= 0) && (0 <= max));

        if ((min < 0) || (max > 0)) {
          EQHandle e = addEQ(true);
          e.updateCoefDuringSimplify(in, -1);
          e.updateCoefDuringSimplify(out, 1);
        }
 
        olAssert((guaranteed_leading_0s == -1) || (leading_eqs > guaranteed_leading_0s));
        olAssert((possible_leading_0s == -1) || (leading_eqs <= possible_leading_0s));

        guaranteed_leading_0s = leading_eqs;
      }

      HashSet already_done = new HashSet(11);
      boolean remapped     = false;

      olAssert((guaranteed_leading_0s == -1) || (leading_eqs <= guaranteed_leading_0s));

      Vector vb  = body.getGlobalDecls();
      int    vbl = vb.size();
      for (int fi = 0; fi < vbl; fi++) {
        VarDecl       func = (VarDecl) vb.elementAt(fi);
        GlobalVarDecl f    = func.getGlobalVar();

        if (!already_done.contains(f) &&
            body.hasLocal(f, VarDecl.INPUT_TUPLE) &&
            body.hasLocal(f, VarDecl.OUTPUT_TUPLE) &&
            (f.arity() == leading_eqs)) {
          already_done.add(f);

          // add f(in) = f(out), project one away
          EQHandle e     = addEQ(true);
          VarDecl  f_in  = body.getLocal(f, VarDecl.INPUT_TUPLE);
          VarDecl  f_out = body.getLocal(f, VarDecl.OUTPUT_TUPLE);

          e.updateCoefDuringSimplify(f_in, -1);
          e.updateCoefDuringSimplify(f_out, 1);

          f_out.resetRemapField(f_in);
          remapped      = true;
          is_guaranteed = false;
        }           
      }

      if (remapped) {
        remap();
        combineColumns();
        VarDecl.resetRemapField(body.getGlobalDecls());
        remapped = false;
      }
    }

    if (is_guaranteed) 
      promiseThatUbSolutionsExist(); 
    else if (false) {
      System.out.println("Can't guaranteed solutions to:");
      omegaLib.useUglyNames++;
      prefixPrint();
      omegaLib.useUglyNames--;
    }
  }

  public boolean parallel(Equation eq, Conjunct that, Equation eqt)
  {
    olAssert(relation().isSimplified());
    olAssert(that.relation().isSimplified());

    int    sign = 0;
    Vector vars = variables();
    int    l    = vars.size();

    for (int i = 1; i < l; i++) {
      int c1 = eq.getCoefficient(i);
      if (c1 == 0)
        continue;

      VarDecl vc  = (VarDecl) vars.elementAt(i);
      int      col = that.findColumn(vc);
      if (col == 0)
        return false;

      int c2 = eqt.getCoefficient(col);

      if (c1 != c2)
        return false;
    }

    olAssert(sign != 0);

    Vector varst = that.variables();
    int    lt    = varst.size();

    for (int i = 1; i < lt; i++) {
      int c2 = eqt.getCoefficient(i);
      if (c2 == 0)
        continue;

      VarDecl vc  = (VarDecl) varst.elementAt(i);
      int      col = findColumn(vc);
      if (col == 0)
        return false;
      int      c1 = eq.getCoefficient(col);
      if (c1 != c2)
        return false;
    }

    return true;
  }

  public int hull(Equation eq, Conjunct that, Equation eqt)
  {
    int    sign = 0;
    Vector vars = variables();
    int    l    = vars.size();

    for (int i = 1; i < l; i++) {
      int c1 = eq.getCoefficient(i);
      if (c1 == 0)
        continue;

      VarDecl vc  = (VarDecl) vars.elementAt(i);
      int      col = that.findColumn(vc);
      if (col == 0)
        return Integer.MIN_VALUE;

      int c2 = eqt.getCoefficient(col);

      if (sign == 0)
        sign = (c1 * c2 >= 0 ? 1 : -1);
      if (sign * c1 != c2)
        return Integer.MIN_VALUE;
    }

    olAssert(sign != 0);

    Vector varst = that.variables();
    int    lt    = varst.size();

    for (int i = 1; i < lt; i++) {
      int c2 = eqt.getCoefficient(i);
      if (c2 == 0)
        continue;

      VarDecl vc  = (VarDecl) varst.elementAt(i);
      int      col = findColumn(vc);
      if (col == 0)
        return Integer.MIN_VALUE;

      int c1 = eq.getCoefficient(col);
      if (sign * c1 != c2)
        return Integer.MIN_VALUE;
    }

    int x = sign * eq.getConstant();
    int y = eqt.getConstant();

    if (x >= y)
      return x;

    return y;
  }

  public boolean constraintIsEqual(Equation eq, Conjunct that, Equation eqt)
  {
    int    sign = 0;
    Vector vars = variables();
    int    l    = vars.size();

    for (int i = 1; i < l; i++) {
      int c1 = eq.getCoefficient(i);
      if (c1 == 0)
        continue;

      VarDecl vc  = (VarDecl) vars.elementAt(i);
      int      col = that.findColumn(vc);
      if (col == 0)
        return false;

      int c2 = eqt.getCoefficient(col);

      if (sign == 0)
        sign = (c1 * c2 >= 0 ? 1 : -1);
      if (sign * c1 != c2)
        return false;
    }

    olAssert(sign != 0);

    Vector varst = that.variables();
    int    lt    = varst.size();

    for (int i = 1; i < lt; i++) {
      int c2 = eqt.getCoefficient(i);
      if (c2 == 0)
        continue;

      VarDecl vc = (VarDecl) varst.elementAt(i);
      int      col = findColumn(vc);
      if (col == 0)
        return false;

      int c1 = eq.getCoefficient(col);
      if (sign * c1 != c2)
        return false;
    }

    return sign * eq.getConstant() == eqt.getConstant();
  }

  public void farkas()
  {
    Equation[] eqs   = getEQs();
    int        neqs  = getNumEQs();
    Equation[] geqs  = getGEQs();
    int        ngeqs = getNumGEQs();

    Vector vl  = variables();
    int    vll = vl.size();
    for (int vli = 0; vli < vll; vli++) {
      VarDecl v1   = (VarDecl) vl.elementAt(vli);
      int     col1 = findColumn(v1);
      if (col1 == 0)
        throw new polyglot.util.InternalCompilerError("Variable not found");

      for (int j = 0; j < neqs; j++) {
        Equation eqh = eqs[j];
        if (eqh.getCoefficient(col1) == 0)
          continue;

        for (int vli2 = 0; vli2 < vll; vli2++) {
          VarDecl v2   = (VarDecl) vl.elementAt(vli);
          int     col2 = findColumn(v2);
          if (col2 == 0)
            continue;

          if (eqh.getCoefficient(col2) == 0)
            continue;

          v1.UFUnion(v2);
        }
      }
      for (int j = 0; j < ngeqs; j++) {
        Equation eqh = geqs[j];
        if (eqh.getCoefficient(col1) == 0)
          continue;
        for (int vli2 = 0; vli2 < vll; vli2++) {
          VarDecl v2   = (VarDecl) vl.elementAt(vli);
          int     col2 = findColumn(v2);
          if (col2 == 0)
            continue;
          if (eqh.getCoefficient(col2) == 0)
            continue;

          v1.UFUnion(v2);
        }
      }
    }
  }

  public void fastTightHull(HashSet vars)
  {
    Equation[] eqs   = getEQs();
    int        neqs  = getNumEQs();
    Equation[] geqs  = getGEQs();
    int        ngeqs = getNumGEQs();
    Vector     vl    = variables();
    int        vll   = vl.size();

    for (int vli = 0; vli < vll; vli++) {
      VarDecl v     = (VarDecl) vl.elementAt(vli);
      boolean found = false;
      int     col   = findColumn(v);
      if (col == 0)
        continue;

      for (int i = 0; i < neqs; i++) {
        Equation eq = eqs[i];
        if (eq.getCoefficient(col) == 0)
          continue;
        vars.add(v);
        found = true;
        break;
      }

      if (found)
        continue;

      for (int i = 0; i < ngeqs; i++) {
        Equation eq = geqs[i];
        if (eq.getCoefficient(col) == 0)
          continue;
        if (!found)
          vars.add(v);
        found = true;
        break;
      }
    }
  }
}
