package polyglot.ext.x10.types.constr.omega.omegaLib;

import java.util.*;

/**
 * Problem.
 * <p>
 * $Id: Problem.java,v 1.1 2008-03-06 18:02:23 nystrom Exp $
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
 */
public final class Problem
{
  private static int createdCount = 0; /* A count of all the instances of this class that were created. */

  public static int created()
  {
    return createdCount;
  }

  public static final int noRed = 0;
  public static final int redFalse = 1;
  public static final int redConstraints = 2;

  public static final int RED   = 1;
  public static final int BLACK = 0;

  private static final int apparentlyEqual = 0;
  private static final int mightNotBeEqual = 1;
  private static final int NotEqual        = 2;

  private static final int normalizeFalse     = 0;
  private static final int normalizeUncoupled = 1;
  private static final int normalizeCoupled   = 2;

  private static final int NONE = 0;
  private static final int LE   = 1;
  private static final int LT   = 2;

  private static final int SOLVE_F        = 0;
  private static final int SOLVE_T        = 1;
  private static final int SOLVE_UNKNOWN  = 2;
  private static final int SOLVE_SIMPLIFY = 3;

  private static final String[] solveDisp = {"False", "True", "Unknown", "Simplify"};

  private static final int maxmaxGEQs    = 512;
  private static final int maxGEQs       = 70;
  private static final int allocInc      = 10;
  private static final int maxWildcards  = 18;

  private static final String[] wildName = {"???",     "__alpha", "__beta",  "__gamma", 
                                           "__delta", "__tau",   "__sigma", "__chi",
                                           "__omega", "__pi",    "__ni",    "__Alpha",
                                           "__Beta",  "__Gamma", "__Delta", "__Tau",
                                           "__Sigma", "__Chi",   "__Omega", "__Pi"};

  private static final String connector = " && ";

  private static int nextWildcard = 0;

  private Problem  full_answer;
  private Problem  context;
  private Problem  redProblem;
  private int      mayBeRed;
  private int      varsOfInterest;
  private int      nVars;
  private int[]    var;
  private int[]    forwardingAddress;
  private int      hashVersion;
  private int      outerColor;
  private int      solveDepth;
  private int      safeVars;
  private int      darkConstraints;
  private int      unit;
  private int      parallelSplinters;
  private int      disjointSplinters;
  private int      lbSplinters;
  private int      ubSplinters;
  private int      parallelLB;
  private int      nEQs;
  private int      nGEQs;
  private int      nSUBs;
  private Vector   redMemory;
  private boolean  variablesInitialized;
  private boolean  variablesFreed;
  private boolean  darkShadowFeasible;

  private Conjunct   varNameSource;
  private Equation[] EQs;
  private Equation[] GEQs;
  private Equation[] SUBs;
  private OmegaLib   omegaLib;

  /* Allocation parameters and functions */

  public Problem(OmegaLib omegaLib, int in_eqs, int in_geqs, Conjunct varNameSource)
  {
    this.omegaLib             = omegaLib;
    this.GEQs                 = new Equation[in_geqs];
    this.EQs                  = new Equation[in_eqs];
    this.SUBs                 = null;
    this.nEQs                 = 0;
    this.nGEQs                = 0;
    this.nSUBs                = 0;
    this.nVars                = 0;
    this.solveDepth           = 0;
    this.hashVersion          = omegaLib.getProtoHash();
    this.variablesInitialized = false;
    this.variablesFreed       = false;
    this.varsOfInterest       = 0;
    this.safeVars             = 0;
    this.redMemory            = new Vector();
    this.forwardingAddress    = new int[OmegaLib.maxVars + 1];
    this.var                  = new int[OmegaLib.maxVars + 1];
    this.varNameSource        = varNameSource;
    createdCount++;
  }

  public Problem(OmegaLib omegaLib, int in_eqs, int in_geqs)
  {
    this(omegaLib, in_eqs, in_geqs, null);
  }

  public Problem(Problem p2, Conjunct varNameSource)
  {
    this(p2.omegaLib, p2.nEQs, p2.nGEQs, null);

    this.nVars                = p2.nVars;
    this.hashVersion          = p2.hashVersion;
    this.variablesInitialized = p2.variablesInitialized;
    this.variablesFreed       = p2.variablesFreed;
    this.varsOfInterest       = p2.varsOfInterest;
    this.safeVars             = p2.safeVars;
    this.var                  = (int[]) p2.var.clone();
    this.forwardingAddress    = (int[]) p2.forwardingAddress.clone();
    this.varNameSource        = varNameSource;

    this.nEQs = p2.nEQs;
    for (int e = p2.nEQs - 1; e >= 0; e--)
      this.EQs[e] = p2.EQs[e].copy();

    this.nGEQs = p2.nGEQs;
    for (int e = p2.nGEQs - 1; e >= 0; e--)
      this.GEQs[e] = p2.GEQs[e].copy();
  }

  public Problem(Problem p2)
  {
    this(p2, p2.varNameSource);
  }

  public String toString()
  {
    return "(Problem 0x" + Integer.toHexString(hashCode()) + ")";
  }

  /**
   * Return true if there are no equations.
   */
  public boolean isEmpty()
  {
    return (nEQs == 0) && (nGEQs == 0);
  }

  /**
   * Return true if there are no substitutions.
   */
  public boolean isNoSubs()
  {
    return (nSUBs == 0);
  }

  public Equation[] getGEQs()
  {
    return GEQs;
  }

  public int getNumGEQs()
  {
    return nGEQs;
  }

  public Equation[] getEQs()
  {
    return EQs;
  }

  public int getNumEQs()
  {
    return nEQs;
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

  private int abs(int x)
  {
    if (x >= 0)
      return x;
    return -x;
  }

  private int checkMultiply(long x, long y)
  {
    long p = x * y;
    int  r = (int) p;
    if (r != p)
      throw new polyglot.util.InternalCompilerError("integer multiply overflow " + x + " " + y);
    return r;
  }

  private int intDivide(int a, int b)
  {
    if (b <= 0)
      throw new polyglot.util.InternalCompilerError("integer divide error " + a + " " + b);

    if (a > 0)
      return a / b;

    return  -((-a + b - 1) / b);
  }

  private int intMod(int a, int b)
  {
    return a - b * intDivide(a, b);
  }

  private int gcd(int b, int a) /* First argument is non-negative */
  {
    if ((a < 0) || (b < 0))
      throw new polyglot.util.InternalCompilerError("gcd error " + a + " " + b);

    if (b == 1)
      return 1;

    while (b != 0) {
      int t = b;
      b = a % b;
      a = t;
    }

    return a;
  }

  private int lcm(int b, int a) /* First argument is non-negative */
  {
    if ((a < 0) || (b < 0))
      throw new polyglot.util.InternalCompilerError("lcm error " + a + " " + b);

    return a * b / gcd(a, b);
  }

  private boolean implies(long a, long b)
  {
    return (a == (a & b));
  }

  private void olAssert(boolean t)
  {
    assert t : this.getClass().getName();
  }

  private void olAssert(boolean t, String msg)
  {
    assert t : this.getClass().getName() + ": " + msg;
  }

  private int reduceProblem()
  {
    if (nVars > (nEQs + 3 * safeVars))
      freeEliminations(safeVars);

    check();
    if ((mayBeRed == 0) && (nSUBs == 0) && (safeVars == 0)) {
      int  result = solve(SOLVE_UNKNOWN);
      nGEQs = 0;
      nEQs = 0;
      nSUBs = 0;
      redMemory.clear();
      if (SOLVE_F == result) {
        Equation eq = getNewEQ();
        eq.turnBlack();
        eq.setConstant(1);
      }
      check();
      return result;
    }

    return solve(SOLVE_SIMPLIFY);
  }

  private void noSimplify()
  {
    nGEQs = 0;
    nEQs = 0;
    resurrectSubs();
    nGEQs = 0;
    nEQs = 0;
    Equation eq = getNewEQ();
    eq.turnBlack();
    eq.setConstant(1);
    redMemory.clear();
  }

  public boolean simplifyProblem(boolean verify, int redundantElimination)
  {
    setInternals();
    check();
    if (SOLVE_F == reduceProblem()) {
      noSimplify();
      return false;
    }

    if (verify) {
      boolean saoe = omegaLib.addingOuterEqualities;
      omegaLib.addingOuterEqualities = true;
      int r = verifyProblem();
      omegaLib.addingOuterEqualities = saoe;

      if (SOLVE_F == r) {
        noSimplify();
        return false;
      }

      if (nEQs > 0) { // found some equality constraints during verification
        int numRed = 0;
        if (mayBeRed != 0) 
          numRed = numNotBlack(GEQs, nGEQs);
        if ((mayBeRed != 0) && (nVars == safeVars) && (numRed == 1))
          nEQs = 0; // discard them
        else if (SOLVE_F == reduceProblem())
          throw new polyglot.util.InternalCompilerError("Added equality constraint to verified problem generates false");
      }
    }

    if (redundantElimination != 0) {
      if (redundantElimination > 1) {
        if (!expensiveEqualityCheck()) {
          noSimplify();
          return false;
        }
      }
      if (!quickKill(false, false)) {
        noSimplify();
        return false;
      }
      if (redundantElimination > 1) {
        if (!expensiveKill()) {
          noSimplify();
          return false;
        }
      }
    }

    resurrectSubs();

    if (redundantElimination != 0) 
      simplifyStrideConstraints();

    if ((redundantElimination > 2) && (safeVars < nVars)) {
      if (!quickKill(false, false))  {
        noSimplify();
        return false;
      }
      return simplifyProblem(verify, redundantElimination - 2);
    }

    setExternals();
    olAssert(redMemory.size() == 0);

    return true;
  }

  public boolean simplifyApproximate(boolean strides_allowed) 
  {
    olAssert(!omegaLib.inApproximateMode);

    omegaLib.inApproximateMode = true;
    omegaLib.inStridesAllowedMode = strides_allowed;
    if (omegaLib.trace)
      System.out.println("Entering Approximate Mode [");

    boolean result = simplifyProblem(false, 0);

    while (result && !strides_allowed && (nVars > safeVars)) {
      for (int e = nGEQs - 1; e >= 0; e--) 
        if (GEQs[e].isNotZero(nVars))
          deleteGEQ(e);
      for (int e = nEQs - 1; e >= 0; e--) 
        if (EQs[e].isNotZero(nVars))
          deleteEQ(e);
      nVars--;
      result = simplifyProblem(false, 0);
    }

    if (omegaLib.trace)
      System.out.println("] Leaving Approximate Mode");
    
    olAssert(omegaLib.inApproximateMode);
    omegaLib.inApproximateMode = false;
    omegaLib.inStridesAllowedMode = false;

    olAssert(redMemory.size() == 0);
    return result;
  }

  /*
   * Return 1 if red equations constrain the set of possible solutions. We assume that there are solutions to the BLACK
   * equations by themselves, so if there is no solution to the combined problem, we return 1.
   */
  public int redSimplifyProblem(int effort, boolean computeGist)
  {
    olAssert(mayBeRed >= 0);
    mayBeRed++;

    if (omegaLib.trace) {
      System.out.println("Checking for red equations:");
      printProblem();
    }
    setInternals();

    if (omegaLib.trace) 
      System.out.println("Set-up for gist invariant checking[");

    redProblem = this;
    redProblem.check();
    full_answer = this;
    full_answer.check();
    full_answer.turnRedBlack();
    full_answer.check();
    boolean r1 = full_answer.simplifyProblem(true, 1);
    full_answer.check();

    if (omegaLib.trace)
      System.out.println("Simplifying context [");

    context = this;     
    context.check();
    context.deleteRed();
    context.check();
    boolean r2 = context.simplifyProblem(true, 1);
    context.check();

    if (omegaLib.trace)
      System.out.println("] Simplifying context");

    if (!r2 && omegaLib.trace)
      System.out.println("WARNING: Gist context is false!");

    if (omegaLib.trace) 
      System.out.println("] Set-up for gist invariant checking done");

    if (solveEQ() == SOLVE_F) {
      if (omegaLib.trace)
        System.out.println("Gist is false");
      if (computeGist) {
        redMemory.clear();
        nGEQs = 0;
        nEQs = 0;
        resurrectSubs();
        nGEQs = 0;
        nEQs = 0;
        Equation eq = getNewEQ();
        eq.turnRed();
        eq.setConstant(1);
      }
      mayBeRed--;
      return redFalse;
    }

    if (!computeGist && (redMemory.size() > 0)) 
      return redConstraints;

    if (normalize() == normalizeFalse) {
      if (omegaLib.trace)
        System.out.println("Gist is false");
      if (computeGist) {
        nGEQs = 0;
        nEQs = 0;
        resurrectSubs();
        redMemory.clear();
        nGEQs = 0;
        nEQs = 0;
        Equation eq = getNewEQ();
        eq.turnRed();
        eq.setConstant(1);
      }
      mayBeRed--;
      return redFalse;
    }

    if (!checkRed()) {
      if (computeGist) {
        nGEQs = 0;
        nEQs = 0;
        resurrectSubs();
        nGEQs = 0;
        redMemory.clear();
        nEQs = 0;
      }
      mayBeRed--;
      return noRed;
    }

    boolean result = simplifyProblem(effort != 0, 0);

    if (!r1 && omegaLib.trace && result) 
      System.out.println("Gist is false but not detected");

    if (!result) {
      if (omegaLib.trace)
        System.out.println("Gist is false");
      if (computeGist) {
        nGEQs = 0;
        nEQs = 0;
        resurrectSubs();
        nGEQs = 0;
        nEQs = 0;
        Equation eq = getNewEQ();
        eq.turnRed();
        eq.setConstant(1);
        redMemory.clear();
      }

      mayBeRed--;
      return redFalse;
    }

    freeRedEliminations();

    if (!checkRed()) {
      if (computeGist) {
        nGEQs = 0;
        nEQs = 0;
        resurrectSubs();
        nGEQs = 0;
        redMemory.clear();
        nEQs = 0;
      }
      mayBeRed--;
      return noRed;
    }

    if ((effort != 0) && (computeGist || (redMemory.size() == 0))) {
      if (omegaLib.trace)
        System.out.println("*** Doing potentially expensive elimination tests for red equations [");
      quickRedKill(computeGist);
      checkGistInvariant();
      if (checkRed() && (effort > 1) && (computeGist || (redMemory.size() == 0))) {
        expensiveRedKill();
        result = checkRed();
      }
        
      if (!result) {
        if (omegaLib.trace)
          System.out.println("]******************** Redundant Red Equations eliminated!!");
        if (computeGist) {
          nGEQs = 0;
          nEQs = 0;
          resurrectSubs();
          nGEQs = 0;
          redMemory.clear();
          nEQs = 0;
        }
        mayBeRed--;
        return noRed;
      }
            
      if (omegaLib.trace) {
        System.out.println("]******************** Red Equations remain");
        printProblem();
      }
    }
    if (computeGist) {
      resurrectSubs();  
      cleanoutWildcards();      
      deleteBlack();
    }
    setExternals();
    mayBeRed--;
    olAssert(redMemory.size() == 0);
    return redConstraints;
  }

  public void convertEQstoGEQs(boolean excludeStrides)
  {
    if (omegaLib.trace)
      System.out.println("Converting all EQs to GEQs");

    simplifyProblem(false, 0);

    for (int e = 0; e < nEQs; e++) {
      Equation eq       = EQs[e];
      boolean  isStride = false;

      if (excludeStrides)
        isStride = eq.anyNZCoef(nVars, safeVars + 1);

      if (isStride)
        continue;

      Equation eq2 = getNewGEQ(eq);
      eq2.setTouched(true);

      Equation eq3 = getNewGEQ(eq);
      eq3.setTouched(true);
      eq3.negateCoefs(nVars, 0);
    }

    // If we have eliminated all EQs, can set nEQs to 0
    // If some strides are left, we don't know the position of them in the EQs
    // array, so decreasing nEQs might remove wrong EQs -- we just leave them
    // all in. (could sort the EQs to move strides to the front, but too hard.)

    if (!excludeStrides)
      nEQs = 0; 

    if (omegaLib.trace)
      printProblem();
  }

  private int checkEquiv(Problem p1, Problem p2) 
  {
    p1.check();
    p2.check();
    p1.resurrectSubs(); 
    p2.resurrectSubs(); 
    p1.check();
    p2.check();
    p1.putVariablesInStandardOrder(); 
    p2.putVariablesInStandardOrder(); 
    p1.check();
    p2.check();
    p1.orderedElimination(0); 
    p2.orderedElimination(0); 
    p1.check();
    p2.check();
    boolean r1 = p1.simplifyProblem(true, 0);
    boolean r2 = p2.simplifyProblem(true, 0);
    p1.check();
    p2.check();

    if (!r1 || !r2) {
      if (r1 == r2)
        return apparentlyEqual;
      return NotEqual;
    }

    if ((p1.nVars != p2.nVars) ||
        (p1.nGEQs != p2.nGEQs) ||
        (p1.nSUBs != p2.nSUBs) ||
        (p1.checkSum()  != p2.checkSum())) {

      r1 = p1.simplifyProblem(false, 1);
      r2 = p2.simplifyProblem(false, 1);
      olAssert(r1 && r2);
      p1.check();
      p2.check();

      if ((p1.nVars != p2.nVars) ||
          (p1.nGEQs != p2.nGEQs) ||
          (p1.nSUBs != p2.nSUBs) ||
          (p1.checkSum()  != p2.checkSum())) {

        r1 = p1.simplifyProblem(false, 2);
        r2 = p2.simplifyProblem(false, 2);
        p1.check();
        p2.check();

        olAssert(r1 && r2);

        if ((p1.nVars != p2.nVars) || 
            (p1.nGEQs != p2.nGEQs) ||
            (p1.nSUBs != p2.nSUBs) ||
            (p1.checkSum()  != p2.checkSum())) {

          p1.check();
          p2.check();
          p1.resurrectSubs(); 
          p2.resurrectSubs(); 
          p1.check();
          p2.check();
          p1.putVariablesInStandardOrder(); 
          p2.putVariablesInStandardOrder(); 
          p1.check();
          p2.check();
          p1.orderedElimination(0); 
          p2.orderedElimination(0); 
          p1.check();
          p2.check();
          r1 = p1.simplifyProblem(true, 0);
          r2 = p2.simplifyProblem(true, 0);
          p1.check();
          p2.check();
        }
      }
    }

    if ((p1.nVars != p2.nVars) ||
        (p1.nSUBs != p2.nSUBs) ||
        (p1.nGEQs != p2.nGEQs) ||
        (p1.nSUBs != p2.nSUBs))
      return NotEqual;

    if (p1.checkSum()  != p2.checkSum())
      return mightNotBeEqual;

    return apparentlyEqual;
  }

  private void checkGistInvariant()
  {
    check();
//      fullAnswer.check();
//      context.check();

    if (safeVars < nVars) {
      if (omegaLib.trace) {
        System.out.println("Can't check gist invariant due to wildcards");
        printProblem();
      }
      return;
    }
    if (omegaLib.trace) {
      System.out.println("Checking gist invariant on: [");
      printProblem();
    }

    Problem new_answer = this;
    new_answer.resurrectSubs();
    new_answer.cleanoutWildcards();

    if (omegaLib.trace) {
      System.out.println("which is: ");
      printProblem();
    }

    new_answer.deleteBlack();
    new_answer.turnRedBlack();

    if (omegaLib.trace) {
      System.out.println("Black version of answer: ");
      new_answer.printProblem();
    }

    problemMerge(new_answer);

    int r = checkEquiv(full_answer, new_answer);
    if (r != apparentlyEqual) {
      System.out.println("GIST INVARIANT REQUIRES MANUAL CHECK:[");
      System.out.println("Original problem:");
      redProblem.printProblem();

      System.out.println("Context:");
      context.printProblem();

      System.out.println("Computed gist:");
      printProblem();

      System.out.println("Combined answer:");
      full_answer.printProblem();

      System.out.println("Context && red constraints:");
      new_answer.printProblem();
      System.out.println("]");
    }
                
    if (omegaLib.trace) {
      System.out.println("] Done checking gist invariant on");
    }
  }

  private void simplifyStrideConstraints() 
  {
    if (omegaLib.trace)
      System.out.println("Checking for stride constraints");

    for (int i = safeVars + 1; i <= nVars; i++) {
      if (omegaLib.trace)
        System.out.println("checking " + variable(i));

      Equation ee = firstNonZeroCoef(GEQs, nGEQs, i);
      if (ee != null)
        continue;

      if (omegaLib.trace)
        System.out.println(variable(i) + " passed GEQ test");

      int e2 = -1;
      for (int e = 0; e < nEQs; e++)
        if (EQs[e].isNotZero(i)) {
          if (e2 == -1)
            e2 = e;
          else {
            e2 = -1;
            break;
          }
        }

      if (e2 < 0)
        continue;

      Equation eq = EQs[e2];
      if (omegaLib.trace) {
        System.out.println("Found stride constraint: ");
        printEQ(eq);
        System.out.println("");
      }

      // Is a stride constraint.

      eq.intModHatI(i, nVars);
    }
  }
  
  /**
   * Solve e = factor alpha for x_j and substitute
   */
  private void doMod(int factor, int e, int j)
  {
    Equation eq = EQs[e];
    if (eq.isNotBlack()) {
      rememberRedConstraint(eq, RememberRedConstraint.redEQ,0);
      eq.turnBlack();
    }

    int alpha = addNewUnprotectedWildcard();
    eq = eq.copy();
    omegaLib.newVar = alpha;

    if (omegaLib.trace) {
      System.out.println("doing moding: ");
      System.out.println("Solve ");
      printTerm(eq, 1);
      System.out.print(" = ");
      System.out.print(factor);
      System.out.print(" ");
      System.out.print(variable(alpha));
      System.out.print(" for ");
      System.out.print(variable(j));
      System.out.println(" and substitute");
    }

    eq.intModHat(0, nVars, factor);

    int nFactor = eq.getCoefficient(j);

    olAssert((nFactor == 1) || (nFactor == -1));

    eq.setCoef(alpha, factor / nFactor);

    if (omegaLib.trace) {
      System.out.println("adjusted: ");
      System.out.println("Solve ");
      printTerm(eq, 1);
      System.out.print(" = 0 for ");
      System.out.print(variable(j));
      System.out.println(" and substitute");
    }

    eq.setCoef(j, 0);
    substitute(eq, j, nFactor);
    omegaLib.newVar = -1;
    deleteVariable(j);
    EQs[e].divideCoefsEven(nVars, factor);

    if (omegaLib.trace) {
      System.out.println("Mod-ing and normalizing produces:");
      printProblem();
    }
  }

  private void substitute(Equation sub, int i, int c)
  {
    boolean recordSubstitution = (i <= safeVars) && (var[i] >= 0);

    int clr = RememberRedConstraint.notRed;
    if (sub.isNotBlack())
      clr = RememberRedConstraint.redEQ;

    olAssert(c == 1 || c == -1);

    if (omegaLib.trace) {
      if (sub.isNotBlack())
         System.out.println("RED SUBSTITUTION");
      System.out.print("substituting using ");
      System.out.print(variable(i));
      System.out.print(" := ");
      printTerm(sub, -c);
      System.out.println("");
      printVars(true);
    }

    if ((i > safeVars) && (clr != 0)) {
      boolean unsafeSub = false;
      for (int e = nEQs - 1; e >= 0; e--) {
        Equation eq = EQs[e];
        if (eq.isBlack() && eq.isZero(i)) {
          unsafeSub = true;
          break;
        }
      }

      if (!unsafeSub)
        for (int e = nGEQs - 1; e >= 0; e--) {
          Equation eq = GEQs[e];
          if (eq.isBlack() && eq.isZero(i)) {
            unsafeSub = true;
            break;
          }
        }

      if (!unsafeSub)
        unsafeSub = anyNonZeroCoef(SUBs, nSUBs, i);

      if (unsafeSub) {
        System.out.println("UNSAFE RED SUBSTITUTION");
        System.out.print("substituting using ");
        System.out.print(variable(i));
        System.out.print(" := ");
        printTerm(sub, -c);
        System.out.println("");
        printProblem();
        throw new polyglot.util.InternalCompilerError("UNSAFE RED SUBSTITUTION");
      }
    }

    for (int e = nEQs - 1; e >= 0; e--) {
      Equation eq = EQs[e];
      int      k  = eq.getCoefficient(i);
      if (k != 0) {
        k = checkMultiply(k, c); // Should be k = k/c, but same effect since abs(c) == 1
        eq.setCoef(i, 0);
        eq.multAndSub(sub, k, nVars);
      }
      if (omegaLib.trace) {
        printEQ(eq);
        System.out.println("");
      }
    }

    for (int e = nGEQs - 1; e >= 0; e--) {
      Equation eq = GEQs[e];
      int      k  = eq.getCoefficient(i);

      if (k == 0) {
        if (omegaLib.trace) {
          printGEQ(eq);
          System.out.println("");
        }
        continue;
      }

      k = checkMultiply(k, c); // Should be k = k/c, but same effect since abs(c) == 1
      eq.setTouched(true);
      eq.setCoef(i, 0);
      eq.multAndSub(sub, k, nVars);

      if ((clr == RememberRedConstraint.notRed) || !eq.isBlack()) {
        if (omegaLib.trace) {
          printGEQ(eq);
          System.out.println("");
        }
        continue;
      }
      if (eq.anyNZCoef(nVars, 1)) {
        if (omegaLib.trace) {
          printGEQ(eq);
          System.out.println("");
        }
        continue;
      }

      int z = intDivide(eq.getConstant(), abs(k));

      if (omegaLib.trace) {
        System.out.println("Black inequality matches red substitution");
        if (z < 0)
          System.out.println("System is infeasible");
        else if (z > 0)
          System.out.println("Black inequality is redundant");
        else {
          System.out.println("Black constraint partially implies red equality");
          if (k < 0) {
            System.out.println("Black constraints tell us ");
            olAssert(sub.isNotZero(i));
            sub.setCoef(i, c);
            printTerm(sub, 1);
            sub.setCoef(i, 0);
            System.out.println("<= 0");
          } else {
            System.out.println("Black constraints tell us ");
            olAssert(sub.isNotZero(i));
            sub.setCoef(i, c);
            printTerm(sub, 1);
            sub.setCoef(i, 0);
            System.out.println(" >= 0");
          }
        }
      }

      if (z == 0) {
        if (k < 0) {
          if (clr == RememberRedConstraint.redEQ)
            clr = RememberRedConstraint.redGEQ;
          else if (clr == RememberRedConstraint.redLEQ)
            clr = RememberRedConstraint.notRed;
        } else {
          if (clr == RememberRedConstraint.redEQ)
            clr = RememberRedConstraint.redLEQ;
          else if (clr == RememberRedConstraint.redGEQ)
            clr = RememberRedConstraint.notRed;
        }
      }

      if (omegaLib.trace) {
        printGEQ(eq);
        System.out.println("");
      }
    }

    if ((i <= safeVars) && (clr != 0)) {
      olAssert(sub.isZero(i));
      sub.setCoef(i, c);
      rememberRedConstraint(sub, clr, 0);
      sub.setCoef(i, 0);
    }

    if (recordSubstitution) {
      Equation eq = getNewSUB();
      eq.checkMultCoefs(sub, nVars, -c);
      eq.setKey(var[i]);
      if (omegaLib.trace) {
        System.out.print("Recording substition as: ");
        printSubstitution(eq);
        System.out.println("");
      }
    }

    if (omegaLib.trace) {
      System.out.println("Ready to update subs");
      if (sub.isNotBlack())
        System.out.println("RED SUBSTITUTION");
      System.out.print("substituting using ");
      System.out.print(variable(i));
      System.out.print(" := ");
      printTerm(sub, -c);
      System.out.println("");
      printVars(true);
    }

    for (int e = nSUBs - 1; e >= 0; e--) {
      Equation eq = SUBs[e];
      int      k  = eq.getCoefficient(i);
      if (k != 0) {
        k = checkMultiply(k, c); // Should be k = k/c, but same effect since abs(c) == 1
        eq.setCoef(i, 0);
        eq.multAndSub(sub, k, nVars);
      }

      if (omegaLib.trace) {
        System.out.print("updated sub (" + c + "): ");
        printSubstitution(eq);
        System.out.println("");
      }
    }

    if (omegaLib.trace) {
      System.out.println("---\n");
      printProblem();
      System.out.println("===\n");
    }
  }

  private void doElimination(int ee, int i)
  {
    if (omegaLib.trace)
      System.out.println("eliminating variable " +  variable(i));

    Equation sub = EQs[ee].copy();
    int      c   = sub.getCoefficient(i);
    sub.setCoef(i, 0);

    if ((c == 1) || (c == -1)) {
      substitute(sub, i, c);
      deleteVariable(i);
      return;
    }

    int a = abs(c);

    if (omegaLib.trace) {
      System.out.println("performing non-exact elimination, c = " + c);
      printProblem();
    }

    olAssert(omegaLib.inApproximateMode);

    for (int e = nEQs - 1; e >= 0; e--) {
      Equation eq = EQs[e];
      if (eq.isZero(i))
        continue;

      eq.checkMultCoefs(nVars, a);

      int k = eq.getCoefficient(i) / c;

      eq.setCoef(i, 0);
      eq.setColor(eq, sub);
      eq.multAndSub(sub, k, nVars);
    }

    for (int e = nGEQs - 1; e >= 0; e--) {
      Equation eq = GEQs[e];
      if (eq.isZero(i))
        continue;

      eq.checkMultCoefs(nVars, a);

      int k = eq.getCoefficient(i) / c;

      eq.setCoef(i, 0);
      eq.setColor(eq, sub);
      eq.multAndSub(sub, k, nVars);
      eq.setTouched(true);
    }

    for (int e = nSUBs - 1; e >= 0; e--) {
      Equation eq = SUBs[e];
      if (eq.isZero(i))
        continue;

      olAssert(false); 
      // We can't handle this since we can't multiply 
      // the coefficient of the left-hand side
      olAssert(sub.isBlack());

      eq.checkMultCoefs(nVars, a);

      int k = eq.getCoefficient(i) / c;

      eq.setCoef(i, 0);
      eq.multAndSub(sub, k, nVars);
    }

    deleteVariable(i);
  }

  private int solveEQ()
  {
    if (omegaLib.trace && (nEQs > 0)) {
      System.out.println("SolveEQ(" + mayBeRed + ")");
      printProblem();
      System.out.println("");
    }

    check();

    int[] delay = new int[nEQs];

    for (int e = 0; e < nEQs; e++) {
      Equation eq = EQs[e];
      delay[e] = 0;

      if (eq.isNotBlack())
        delay[e] += 8;

      int unitWildCards    = eq.numOneCoefs(nVars, safeVars + 1);
      int nonunitWildCards = eq.numNZCoefs(nVars, safeVars + 1) - unitWildCards;
      int unit             = eq.numOneCoefs(safeVars, 1);
      int nonUnit          = eq.numNZCoefs(safeVars, 1) - unit;

      if ((unitWildCards == 1) && (nonunitWildCards == 0))
        delay[e] += 0;
      else if ((unitWildCards >= 1) && (nonunitWildCards == 0))
        delay[e] += 1;
      else if (omegaLib.inApproximateMode && (nonunitWildCards > 0))
        delay[e] += 2;
      else if ((unit == 1) && (nonUnit == 0) && (nonunitWildCards == 0))
        delay[e] += 3;
      else if ((unit > 1) && (nonUnit == 0) && (nonunitWildCards == 0))
        delay[e] += 4;
      else if ((unit >= 1) && (nonunitWildCards <= 1))
        delay[e] += 5;
      else
        delay[e] += 6;
    }

    for (int e = 0; e < nEQs; e++) {
      int slowest = e;
      for (int e2 = e + 1; e2 < nEQs; e2++)
        if (delay[e2] > delay[slowest])
          slowest = e2;
      if (slowest != e) {
        int tmp = delay[slowest];
        delay[slowest] = delay[e];
        delay[e] = tmp;
        Equation eq = EQs[slowest];
        EQs[slowest] = EQs[e];
        EQs[e] = eq;
      }
    }

    if (omegaLib.trace && (nEQs > 0)) {
      System.out.println("SolveEQ(" + mayBeRed + ")");
      printProblem();
      System.out.println("");
    }
        
    /* Eliminate all EQ equations */
    for (int e = nEQs - 1; e >= 0; e--)  {
      Equation eq = EQs[e];

      olAssert((mayBeRed != 0) || eq.isBlack());

      check();

      if (omegaLib.trace) {
        System.out.print("considering: ");
        printEQ(eq);
        System.out.println("");
      }

      int g = eq.gcdCoefs(nVars, 1);

      if (g == 0) {
        if (eq.isNotZero(0)) {
          if (omegaLib.trace) {
            printEQ(eq);
            System.out.println("\nequations have no solution (B)");
          }
          return SOLVE_F;
        }
        nEQs--;
        continue;
      }

      if (omegaLib.inApproximateMode)
        g = gcd(abs(eq.getConstant()), g);

      if ((eq.getConstant() % g) != 0) {
        if (omegaLib.trace) {
          printEQ(eq);
          System.out.println("\nequations have no solution (A)");
        }
        return SOLVE_F;
      }

      if (g != 1)
        eq.divideCoefs(nVars, g);

      if (eq.isBlack()) {
        int     i                 = eq.lastOneCoef(nVars, safeVars + 1);
        boolean involvesWildCards = eq.anyNZCoef(nVars, safeVars + 1);
        if (!involvesWildCards || (i > safeVars)) {
          if (!involvesWildCards)
            i = eq.lastOneCoef(i, 1);
          if (i > 0) {
            nEQs--;
            doElimination(e, i);
            continue;
          }
        }
      }

      int i = eq.lastNZCoef(nVars, 1);
      int j = eq.lastNZCoef(i - 1, 1);

      if (j < 0) {
        System.out.print("\nassertion failure; i = ");
        System.out.print(i);
        System.out.print(", j = ");
        System.out.print(j);
        System.out.print(", g = ");
        System.out.println(g);
        printEQ(eq);
      } 

      olAssert(j >= 0);

      /* i == position of last non-zero coef */
      /* g == coef of i */
      /* j == position of next non-zero coef */
 
      g = eq.getCoefficient(i);
      if (g < 0)
        g = -g;

      if (g == 1) {
        nEQs--;
        // exact elimination, remember if i protected
        doElimination(e, i);
        continue;
      }

      int g2 = 0;
      if (j > safeVars) {
        for (int k = j; g != 1 && k > safeVars; k--)
          g = gcd(abs(eq.getCoefficient(k)), g);
        g2 = g;
      } else if (i > safeVars)
        g2 = g;

        // Now, gcd of all coefficients is 1.
        // Now, gcd of unprotected coefficients is g2.

      if (g2 > 1) {
        boolean e2b = anyNonZeroCoef(EQs, e, i);
        if (!e2b)
          e2b = anyNonZeroCoef(GEQs, nGEQs, i);
        if (!e2b)
          e2b = anyNonZeroCoef(SUBs, nSUBs, i);

        if (!e2b) {
          boolean change = false;
          if (omegaLib.trace) {
            System.out.println("Is a stride constraint, with " + variable(i) + " as wildcard");
            printEQ(eq);
            System.out.println(" ");
          }

          g= eq.getCoefficient(i);
          g = abs(g);
          int e2;
          for (e2 = e - 1; e2 >= 0; e2--) {
            Equation eq2 = EQs[e2];
            j = eq.lastDiffModZero(eq2, g, i, nVars, 0);
            if (j < 0)
              break;
          }

          if (false && (e2 >= 0)) {
            // Implied by e2
            if (omegaLib.trace) {
              System.out.println("Implied by ");
              printEQ(EQs[e2]);
              System.out.println(" ");
            }
            nEQs--;
            continue;
          }

          if (eq.intModHat(0, i - 1, g))
            change = true;

          if (!change) {
            if (omegaLib.trace)
              System.out.println("So what?");
          } else {
            nameWildcard(i);
            if (omegaLib.trace) {
              printEQ(eq);
              System.out.println(" ");
            }
            e++;                /* go back and try this equation again */
            continue;
          }
        }
      }

      if ((g2 > 1) && !(omegaLib.inApproximateMode && !omegaLib.inStridesAllowedMode)) {
        if (omegaLib.trace) {
          System.out.println("generating stride constraint due to gcd of " + g2 + " of unprotected coefficients");
          printEQ(eq);
          System.out.println("\n----");
          printProblem();
          System.out.println("\n----");
          System.out.println("\n----");
        }

        // Found stride constraint, remember it if red

        int k = eq.lastOneCoef(j, 1);
        
        if (omegaLib.trace)        
          System.out.println("quick stride: " + k + "," + j + "," + safeVars + "," + eq.getCoefficient(k));

        if ((j <= safeVars) && (1 <= k)) {
          if (eq.isNotBlack()) {
            int tmp = eq.getCoefficient(i);
            eq.setCoef(i, 0);
            rememberRedConstraint(eq,  RememberRedConstraint.redStride, g2);
            eq.setCoef(i,  tmp);
            eq.turnBlack();
          }
          protectWildcard(i);
          nEQs--;
          doElimination(e, k);
          if (omegaLib.trace)
            printProblem();
          continue;
        }

        i = addNewProtectedWildcard();
        Equation neq = getNewEQ(eq);
        olAssert(nEQs == (e + 2)); // we were working on highest-numbered EQ

        neq.intModHat(0, nVars, g2);

        if (!neq.isBlack()) 
          rememberRedConstraint(neq, RememberRedConstraint.redStride, g2);

        neq.setCoef(i, g2);
        neq.turnBlack();
        e += 2;                 /* go back and start on new equation */
        if (omegaLib.trace)
          printProblem();
        continue;
      }

      int sv = safeVars;
      if (g2 == 0) // there are no non-zero coefficients for unprotected variables
        sv = 0;

      // Find variable to eliminate.

      if (omegaLib.inApproximateMode && (nVars > sv)) {
        if (omegaLib.trace) {
          System.out.println("looking for non-exact elimination: ");
          printEQ(eq);
          System.out.println("");
          printProblem();
        }
        i = eq.lastNZCoef(nVars, sv + 1);
      } else
        i = eq.lastOneCoef(nVars, sv + 1);

        // we picked i
      if ((i > sv) && (!omegaLib.inApproximateMode || (i > safeVars))) {
        if (omegaLib.trace)
          System.out.println("About to do elimination; i = " + i + ", sv = " + sv + ", g2 = " + eq.getCoefficient(i));

        nEQs--;
        // exact elimination, remember if i protected
        doElimination(e, i);
        if (omegaLib.trace) {
          olAssert(omegaLib.inApproximateMode);
          System.out.println("result of non-exact elimination:");
          printProblem();
        }
        continue;
      }

      if (omegaLib.inApproximateMode) {
        if (omegaLib.trace) {
          System.out.println("Dropping equation on the floor");
          printEQ(eq);
          System.out.println("");
        }
        continue;
      }

      int factor = Integer.MAX_VALUE;
      int jj     = 0;

      for (int ii = nVars; ii > sv; ii--) {
        int coef = eq.getCoefficient(ii);
        if (coef == 0)
          continue;
        int x =  abs(coef) + 1;
        if (factor > x) {
          factor = x;
          jj = ii;
        }
      }
      olAssert(jj > sv);
      doMod(factor, e, jj);
      /* go back and try this equation again */
      e++;
    }

    nEQs = 0;
    return SOLVE_UNKNOWN;
  }

  private boolean expensiveKill()
  {
    if (omegaLib.trace) {
      System.out.println("Performing expensive kill tests: [");
      printProblem();
    }

    boolean oldRelationtrace   = omegaLib.trace;
    boolean constraintsRemoved = false;

    omegaLib.trace = false;
    omegaLib.conservative++;

    for (int e = nGEQs - 1; e >= 0; e--) {
      Equation eq = GEQs[e];
      if (eq.isEssential())
        continue;

      if (omegaLib.trace) {
        System.out.println("checking equation " + e + " to see if it is redundant: ");
        printGEQ(eq);
        System.out.println("");
      }

      Problem tmpProblem = new Problem(this);
      tmpProblem.negateGEQ(e);
      tmpProblem.varsOfInterest = 0;
      tmpProblem.nSUBs = 0;
      tmpProblem.redMemory.clear();
      tmpProblem.safeVars = 0;
      tmpProblem.variablesFreed = false;
      if (SOLVE_F == tmpProblem.solve(SOLVE_F)) {
        constraintsRemoved = true;
        deleteGEQ(e);
      }
    }

    if (constraintsRemoved && omegaLib.trace)
      System.out.println("There were " + constraintsRemoved + " Constraints removed!!");

    omegaLib.trace = oldRelationtrace;
    omegaLib.conservative--;

    if (omegaLib.trace)
      System.out.println("] expensive kill tests done");

    return true;
  }

  public boolean expensiveRedKill()
  {
    if (omegaLib.trace)
      System.out.println("Performing expensive red kill tests: [");

    boolean oldRelationtrace   = omegaLib.trace;
    boolean constraintsRemoved = false;

    omegaLib.trace = false;
    omegaLib.conservative++;

    for (int e = nGEQs - 1; e >= 0; e--) {
      Equation eq = GEQs[e];
      if (eq.isEssential() || (eq.isBlack()))
        continue;

      if (omegaLib.trace) {
        System.out.println("checking equation " + e + " to see if it is redundant: ");
        printGEQ((GEQs[e]));
        System.out.println("");
      }

      Problem tmpProblem = new Problem(this);
      tmpProblem.negateGEQ(e);
      tmpProblem.varsOfInterest = 0;
      tmpProblem.nSUBs = 0;
      tmpProblem.redMemory.clear();
      tmpProblem.safeVars = 0;
      tmpProblem.variablesFreed = false;
      tmpProblem.turnRedBlack();

      if (SOLVE_F == tmpProblem.solve(SOLVE_F)) {
        constraintsRemoved = true;
        deleteGEQ(e);
      }
    }

    if (constraintsRemoved  && omegaLib.trace)
      System.out.println("There were " + constraintsRemoved + " Constraints removed!!");

    omegaLib.trace = oldRelationtrace;
    omegaLib.conservative--;

    if (omegaLib.trace)
      System.out.println("] expensive red kill tests done");

    return true;
  }

  private boolean expensiveEqualityCheck()
  {
    return true;
  }

  private void quickRedKill(boolean computeGist) 
  {
    int       c         = 0;
    boolean[] isDead    = new boolean[nGEQs];
    int       deadCount = 0;
    long[]    P         = new long[nGEQs];
    long[]    Z         = new long[nGEQs];
    long[]    N         = new long[nGEQs];
    long      PP; /* possible Positives */
    long      PZ; /* possible zeros */
    long      PN; /* possible negatives */
    long      MZ; /* must zeros */

    if (omegaLib.trace) {
      System.out.println("in quickRedKill: [");
      printProblem();
    }

    noteEssential(false);
    boolean moreToDo = chainKill(RED, false);

    if (!moreToDo) {
      if (omegaLib.trace)
        System.out.println("] quickRedKill");
      return;
    }

    boolean equationsToKill = false;
    for (int e = nGEQs - 1; e >= 0; e--) {
      Equation eq = GEQs[e];
      int tmp = 1;
      isDead[e] = false;
      P[e] = 0;
      Z[e] = 0;
      N[e] = 0;

      if (!eq.isBlack() && !eq.isEssential())
        equationsToKill = true;

      if (!eq.isBlack() && eq.isEssential() && !computeGist && !moreToDo) {
        if (omegaLib.trace)
          System.out.println("] quickRedKill");
        return;
      }

      for (int i = nVars; i >= 1; i--) {
        int coef = eq.getCoefficient(i);
        if (coef > 0)
          P[e] |= tmp;
        else if (coef < 0)
          N[e] |= tmp;
        else
          Z[e] |= tmp;
        tmp <<= 1;
      }
    }

    if (!equationsToKill) 
      if (!moreToDo) {
        if (omegaLib.trace)
          System.out.println("] quickRedKill");
        return;
      }

    for (int e1 = nGEQs - 1; e1 >= 0; e1--) {
      if (isDead[e1])
        continue;

      Equation eqe1 = GEQs[e1];

      for (int e2 = e1 - 1; e2 >= 0; e2--) {
        if (isDead[e2])
          continue;

        Equation eqe2 = GEQs[e2];

        int     ci1   = 0;
        int     cj1   = 0;
        int     ci2   = 0;
        int     cj2   = 0;
        int     i     = -1;
        int     j     = -1;
        int     alpha = 0;
        boolean found = false;
      foundPQ:
        for (i = nVars; i > 1; i--) {
          ci1 = eqe1.getCoefficient(i);
          ci2 = eqe2.getCoefficient(i);

          for (j = i - 1; j > 0; j--) {
            cj1 = eqe1.getCoefficient(j);
            cj2 = eqe2.getCoefficient(j);

            alpha = (ci1 * cj2 - ci2 * cj1);

            if (alpha == 0)
              continue;

            found = true;
            break foundPQ;
          }
        }

        if (!found)
          continue;

        if (omegaLib.trace) {
          System.out.println("found two equations to combine, i = " +  variable(i) + ",");
          System.out.println("j = " + variable(j) + ", alpha = " + alpha);
          printGEQ((eqe1));
          System.out.println("");
          printGEQ((eqe2));
          System.out.println("");
        }

        MZ = (Z[e1] & Z[e2]);
        PZ = MZ |  (P[e1] & N[e2]) | (N[e1] & P[e2]);
        PP = P[e1] | P[e2];
        PN = N[e1] | N[e2];

        for (int e3 = nGEQs - 1; e3 >= 0; e3--) {
          Equation eqe3 = GEQs[e3];
          if (eqe3.isBlack() || eqe3.isEssential())
            continue;

          if (!implies(Z[e3], PZ) || implies(~Z[e3], MZ))
            continue;
          if (!implies(P[e3], PP) || !implies(N[e3], PN))
            continue;

          int ci3    = eqe3.getCoefficient(i);
          int cj3    = eqe3.getCoefficient(j);
          int alpha1 = cj2 * ci3 - ci2 * cj3;
          int alpha2 = -(cj1 * ci3 - ci1 * cj3);
          int alpha3 = alpha;

          if (alpha <= 0) {
            alpha1 = -alpha1;
            alpha2 = -alpha2;
            alpha3 = -alpha3;
          }
                                
          if ((alpha1 <= 0) || (alpha2 <= 0))
            continue;

          if (omegaLib.trace) {
            System.out.print("alpha1 = " + alpha1 + ", alpha2 = " + alpha2 + "; comparing against: ");
            printGEQ((eqe3));
            System.out.println("");
          }

          int k;
          int ck3 = 0;
          for (k = nVars; k >= 0; k--) {
            int ck  = eqe1.getCoefficient(k);
            int ck2 = eqe2.getCoefficient(k);

            ck3 = eqe3.getCoefficient(k);
            c = alpha1 * ck + alpha2 * ck2;

            if (omegaLib.trace) {
              System.out.print(" ");
              if (k > 0) 
                System.out.print(variable(k));
              else
                System.out.print("constant");
              System.out.print(": ");
              System.out.print(c);
              System.out.print(", ");
              System.out.println(alpha3 * ck3);
            }

            if (c != alpha3 * ck3)
              break;
          }

          if ((k < 0) || ((k == 0) && (c < alpha3 * (ck3 + 1)))) {
            if (omegaLib.trace) {
              deadCount++;
              System.out.print("red equation#");
              System.out.print(e3);
              System.out.print(" is dead (");
              System.out.print(deadCount);
              System.out.print(" dead so far, ");
              System.out.print(nGEQs - deadCount);
              System.out.print(" remain)");
              printGEQ((eqe1));
              System.out.println("");
              printGEQ((eqe2));
              System.out.println("");
              printGEQ((eqe3));
              System.out.println("");
              olAssert(moreToDo);
            }

            isDead[e3] = true;
          }
        }
      }
    }

    for (int e = nGEQs - 1; e >= 0; e--)
      if (isDead[e])
        deleteGEQ(e);

    if (omegaLib.trace) {
      System.out.println("] quickRedKill");
      printProblem();
    }
  }

  private void initializeVariables()
  {
    olAssert(!variablesInitialized);
    resetVarAndForwarding();
    variablesInitialized = true;
  }

  public void resetVarAndForwarding()
  {
    for (int i = 1; i <= nVars; i++) {
      var[i] = i;
      forwardingAddress[i] = i;
    }
  }

  public void setVariablesInitialized(boolean variablesInitialized)
  {
    this.variablesInitialized = variablesInitialized;
  }

  private void printTerm(Equation eq, int c)
  {
    String s = printTermToString(eq, c, nVars);
    System.out.print(s);
  }

  private void printSub(int v)
  {
    String s = printSubToString(v);
    System.out.println(s);
  }

  public String printSubToString(int v)
  {
    if (v > 0) 
      return variable(v);

    return printTermToString(SUBs[-v - 1], 1, nVars);
  }

  private String sprintEqn(Equation eq, boolean test, int n)
  {
    StringBuffer buf  = new StringBuffer("");
    boolean      isLT = test && (eq.getConstant() == -1);

    if (eq.isNotBlack())
      buf.append('[');

    boolean first = true;
    for (int i = isLT ? 1 : 0; i <= n; i++) {
      int ci = eq.getCoefficient(i);
      if (ci >= 0)
        continue;

      if (!first)
        buf.append("+");
      first = false;

      if (i == 0) {
        buf.append(-ci);
      } else if (ci == -1) {
        buf.append(variable(i));
      } else {
        buf.append(-ci);
        buf.append("*");
        buf.append(variable(i));
      }
    }

    if (first) {
      if (isLT) {
        buf.append('1');
        isLT = false;
      } else
        buf.append('0');
    }

    if (!test) {
      buf.append(" = ");
    } else {
      if (isLT)
        buf.append(" < ");
      else
        buf.append(" <= ");
    }

    first = true;
    for (int i = 0; i <= n; i++) {
      int ci = eq.getCoefficient(i);
      if (ci <= 0)
        continue;

      if (!first)
        buf.append('+');
      first = false;

      if (i == 0)
        buf.append(ci);
      else if (ci == 1)
        buf.append(variable(i));
      else {
        buf.append(ci);
        buf.append("*");
        buf.append(variable(i));
      }
    }

    if (first)
      buf.append('0');

    if (eq.isNotBlack())
      buf.append(']');

    return buf.toString();
  }

  public String printTermToString(Equation eq, int c)
  {
    return printTermToString(eq, c, nVars);
  }

  public String printTermToString(Equation eq, int c, int nVars)
  {
    StringBuffer s = new StringBuffer("");
    boolean first = true;
    int wentFirst = -1;

    for (int i = 1; i <= nVars; i++) {
      int x = c * eq.getCoefficient(i);
      if (x > 0) {
        first = false;
        wentFirst = i;

        if (x == 1)
          s.append(variable(i));
        else {
          s.append(x);
          s.append("*");
          s.append(variable(i)); 
        }
        break;
      }
    }

    for (int i = 1; i <= nVars; i++) {
      int ci = eq.getCoefficient(i);
      int x  = c * ci;
      if ((i != wentFirst) && (x != 0)) {
        if (!first && (x > 0))
          s.append("+");

        first = false;

        if (x == 1)
          s.append(variable(i));
        else if (x == -1) {
          s.append("-");
          s.append(variable(i));
        } else {
          s.append(x); 
          s.append("*");
          s.append(variable(i));
        }
      }
    }

    int x = c * eq.getConstant();
    if (!first && (x > 0))
      s.append("+");
    if (first || (x != 0))
      s.append(x);

    return s.toString();
  }

  public void clearSubs() 
  {
    nSUBs = 0;
    redMemory.clear();
  }

  public void setVarNameSource(Conjunct varNameSource)
  {
    this.varNameSource = varNameSource;
  }

  public String orgVariable(int i)
  {
    if (i == 0)
      return "1";

    if (i > 0)
      return varNameSource.getVarName(i);

    return wildName[-i];
  }

  private String variable(int i)
  { 
    return orgVariable(var[i]);
  }

  private void deleteGEQ(int e)
  {
    if (omegaLib.trace) {
      System.out.print("Deleting " + e + " (last:" + (nGEQs - 1) + "): "); 
      printGEQ(GEQs[e]);
      System.out.println("");
    }

    if (e < nGEQs-1) {
      Equation t = GEQs[e];
      GEQs[e] = GEQs[nGEQs - 1];
      GEQs[nGEQs - 1] = t;
    }

    nGEQs--;
  }

  private void deleteEQ(int e)
  {
    if (omegaLib.trace) {
      System.out.print("Deleting " + e + " (last:" + (nEQs - 1) + "): "); 
      printGEQ(EQs[e]);
      System.out.println("");
    }

    if (e < nEQs-1) {
      Equation t = EQs[e];
      EQs[e] = EQs[nEQs - 1];
      EQs[nEQs - 1] = t;
    }
    nEQs--;
  }

  private void deleteSUB(int e)
  {
    if (omegaLib.trace) {
      System.out.print("Deleting " + e + " (last:" + (nSUBs - 1) + "): "); 
      printSub(e);
      System.out.println("");
    }

    if (e < nSUBs-1) {
      Equation t = SUBs[e];
      SUBs[e] = SUBs[nSUBs - 1];
      SUBs[nSUBs - 1] = t;
    }
    nSUBs--;
  }

  public String printGEQtoString(Equation e)
  {
    return printEqnToString(e, true, 0);
  }

  public String printEQtoString(Equation e)
  {
    return printEqnToString(e, false, 0);
  }

  private void printGEQextra(Equation e) 
  {
    printEqn(e, true, 1);
  }

  private void printEQ(Equation e)
  {
    printEqn(e, false, 0);
  }

  private void printGEQ(Equation e) 
  {
    printEqn(e, true, 0);
  }

  private void printEqn(Equation eq, boolean test, int extra)
  {
    System.out.print(sprintEqn(eq, test, nVars + extra));
  }

  private String printEqnToString(Equation eq, boolean test, int extra)
  {
    return sprintEqn(eq, test, nVars + extra);
  }

  private void printSubstitution(Equation eq)
  {
    olAssert(eq.getKey() > 0);
    System.out.print(orgVariable(eq.getKey()));
    System.out.print(" := ");
    printTerm(eq, 1);
  }

  private void printVars(boolean debug)
  {
    System.out.print("variables = ");
    if (safeVars > 0)
      System.out.print("(");
    for (int i = 1; i <= nVars; i++) {
      System.out.print(variable(i));
      if (i == safeVars)
        System.out.print(")");
      if (i < nVars)
        System.out.print(", ");
    }
    System.out.println("");
  }

  public void printProblem()
  {
    printProblem(true);
  }

  public void printProblem(boolean debug)
  {
    if (!variablesInitialized)
      initializeVariables();

    if (debug) {
      omegaLib.printHead();
      System.out.print("#vars = ");
      System.out.print(nVars);
      System.out.print(", #EQ's = ");
      System.out.print(nEQs);
      System.out.print(", #GEQ's = ");
      System.out.print(nGEQs);
      System.out.print(", # SUB's = ");
      System.out.print(nSUBs);
      System.out.print(", ofInterest = ");
      System.out.println(varsOfInterest);
      omegaLib.printHead();
      printVars(omegaLib.trace);
    }

    for (int e = 0; e < nEQs; e++) {
      omegaLib.printHead();
      printEQ(EQs[e]);
      System.out.println("");
    }
    for (int e = 0; e < nGEQs; e++) {
      omegaLib.printHead();
      printGEQ(GEQs[e]);
      System.out.println("");
    }
    for (int e = 0; e < nSUBs; e++) {
      omegaLib.printHead();
      printSubstitution(SUBs[e]);
      System.out.println("");
    }

    int nMemories = redMemory.size();
    for (int e = 0; e < nMemories; e++) {
      RememberRedConstraint rm = (RememberRedConstraint) redMemory.elementAt(e);
      omegaLib.printHead();
      System.out.println(rm);
    }

    checkForDuplicateVariableNames();
  }

  private void checkForDuplicateVariableNames()
  {
    for(int i = 1; i <= nVars; i++) {
      String vari = variable(i);
      olAssert(vari != null);
      for(int j = 1; j < i; j++) {
        String varj = variable(j);
        olAssert(!vari.equals(varj));
      }
    }
  }

  public void printRedEquations()
  {
    if (!variablesInitialized)
      initializeVariables();
    printVars(true);
    for (int e = 0; e < nEQs; e++) {
      if (EQs[e].isNotBlack()) {
        printEQ(EQs[e]);
        System.out.println("");
      }
    }
    for (int e = 0; e < nGEQs; e++) {
      if (GEQs[e].isNotBlack()) {
        printGEQ(GEQs[e]);
        System.out.println("");
      }
    }
    for (int e = 0; e < nSUBs; e++) {
      Equation eq = SUBs[e];
      if (eq.isNotBlack()) {
        printSubstitution(eq);
        System.out.println("");
      }
    }
  }

  public int prettyPrintProblem()
  {
    String s = prettyPrintProblemToString();
    System.out.println(s);
    return 0;
  }

  public String prettyPrintProblemToString()
  {
    if (nVars <= 0)
      return "";

    StringBuffer s            = new StringBuffer("");
    boolean[]    live         = new boolean[nGEQs];
    boolean      stuffPrinted = false;

    int[][] poE         = new int[nVars+1][nVars+1];
    int[]   lastLinks   = new int[nVars+1];
    int[]   firstLinks  = new int[nVars+1];
    int[]   chainLength = new int[nVars+1];
    int[]   chain       = new int[nVars+1];
    int[]   varCount    = new int[nGEQs];
    int[][] po          = new int[nVars+1][nVars+1];

    if (!variablesInitialized)
      initializeVariables();

    for (int e = 0; e < nEQs; e++) {
      if (stuffPrinted)
        s.append(connector);
      stuffPrinted = true;
      s.append(printEQtoString(EQs[e]));
    }

    for (int e = 0; e < nGEQs; e++) {
      live[e] = true;
      varCount[e] = GEQs[e].numNZCoefs(nVars, 1);
    }

    if (!omegaLib.printInCodeGenStyle) {
      while (true) {
        for (int v = 1; v <= nVars; v++) {
          lastLinks[v] = 0;
          firstLinks[v] = 0;
          chainLength[v] = 0;
          for (int v2 = 1; v2 <= nVars; v2++)
            po[v][v2] = NONE;
        }

        for (int e = 0; e < nGEQs; e++) {
          Equation eq = GEQs[e];
          if (live[e] && (varCount[e] <= 2)) {
            for (int v = 1; v <= nVars; v++) {
              int cv = eq.getCoefficient(v);
              if (cv == 1)
                firstLinks[v]++;
              else if (cv == -1)
                lastLinks[v]++;
            }

            int v1 = eq.lastNZCoef(nVars, 1);
            int v2 = eq.lastNZCoef(v1 - 1, 1);
            int v3 = eq.lastNZCoef(v2 - 1, 1);
            int coef0 = eq.getConstant();
            if ((coef0 > 0) || (coef0 < -1) || (v2 <= 0) || (v3 > 0) ||
                (eq.getCoefficient(v1) * eq.getCoefficient(v2) != -1)) { /* Not a partial order relation */
            } else {
              if (eq.getCoefficient(v1) == 1) {
                v3 = v2;
                v2 = v1;
                v1 = v3;
              }
              /* relation is v1 <= v2 or v1 < v2 */
              po[v1][v2] = ((coef0 == 0) ? LE : LT);
              poE[v1][v2] = e;
            }
          }
        }

        for (int v = 1; v <= nVars; v++)
          chainLength[v] = lastLinks[v];

        /* Just in case nVars <= 0 */
        boolean change = false;
        for (int t = 0; t < nVars; t++) {
          change = false;
          for (int v1 = 1; v1 <= nVars; v1++)
            for (int v2 = 1; v2 <= nVars; v2++)
              if ((po[v1][v2] != NONE) && (chainLength[v1] <= chainLength[v2])) {
                chainLength[v1] = chainLength[v2] + 1;
                change = true;
              }
        }

        if (change) /* caught in cycle */
          break;

        for (int v1 = 1; v1 <= nVars; v1++)
          if (chainLength[v1] == 0)
            firstLinks[v1] = 0;

        int v = 1;
        for (int v1 = 2; v1 <= nVars; v1++)
          if (chainLength[v1] + firstLinks[v1] > chainLength[v] + firstLinks[v])
            v = v1;

        if ((chainLength[v] + firstLinks[v]) == 0)
          break;

        if (stuffPrinted)
          s.append(connector);
        stuffPrinted = true;

        /* chain starts at v */
        /* print firstLinks */

        boolean first = true;
        for (int e = 0; e < nGEQs; e++) {
          if (!live[e])
            continue;

          if (varCount[e] > 2)
            continue;

          Equation eq = GEQs[e];
          int tmp = eq.getCoefficient(v);
          if (tmp != 1)
            continue;

          if (!first)
            s.append(", ");

          eq.setCoef(v, 0);
          s.append(printTermToString(eq, -1, nVars));
          eq.setCoef(v, tmp);

          live[e] = false;
          first = false;
        }

        if (!first)
          s.append(" <= ");

        /* find chain */
        chain[0] = v;
        int m = 1;
        while (true) {
          /* print chain */
          int v2;
          for (v2 = 1; v2 <= nVars; v2++)
            if ((po[v][v2] != 0) && (chainLength[v] == (1 + chainLength[v2])))
              break;
          if (v2 > nVars)
            break;
          chain[m++] = v2;
          v = v2;
        }

        s.append(variable(chain[0]));

        boolean multiprint = false;
        for (int i = 1; i < m; i++) {
          v = chain[i - 1];
          int v2 = chain[i];
          if (po[v][v2] == LE)
            s.append(" <= ");
          else
            s.append(" < ");
          s.append(variable(v2));
          live[poE[v][v2]] = false;
          if (!multiprint && (i < (m - 1))) {
            for (int v3 = 1; v3 <= nVars; v3++) {
              if ((v == v3) || (v2 == v3))
                continue;
              if (po[v][v2] != po[v][v3])
                continue;
              if (po[v2][chain[i + 1]] != po[v3][chain[i + 1]])
                continue;
              s.append(",");
              s.append(variable(v3));
              live[poE[v][v3]] = false;
              live[poE[v3][chain[i + 1]]] = false;
              multiprint = true;
            }
          } else
            multiprint = false;
        }

        v = chain[m - 1];

        /* print lastLinks */

        first = true;
        for (int e = 0; e < nGEQs; e++) {
          Equation eq = GEQs[e];

          if (!live[e])
            continue;

          if (varCount[e] > 2)
            continue;

          int tmp = eq.getCoefficient(v);
          if (tmp != -1)
            continue;

          if (!first)
            s.append(", ");
          else
            s.append(" <= ");

          eq.setCoef(v, 0);
          s.append(printTermToString(eq, 1, nVars));
          eq.setCoef(v, tmp);
          live[e] = false;
          first = false;
        }
      }
    }

    for (int e = 0; e < nGEQs; e++) {
      if (!live[e])
        continue;

      if (stuffPrinted)
        s.append(connector);

      stuffPrinted = true;
      s.append(printGEQtoString(GEQs[e]));
    }

    for (int e = 0; e < nSUBs; e++) {
      Equation eq = SUBs[e];
      if (stuffPrinted)
        s.append(connector);
      stuffPrinted = true;
      if (eq.getKey() > 0) {
        s.append(orgVariable(eq.getKey()));
        s.append(" := ");
      } else {
        s.append("#");
        s.append(eq.getKey());
        s.append(" := ");
      }
      s.append(printTermToString(eq, 1, nVars));
    }

    return s.toString();
  }

  private int prettyPrintRedEquations()
  {
    boolean stuffPrinted = false;

    if (!variablesInitialized)
      initializeVariables();

    for (int e = 0; e < nEQs; e++) {
      Equation eq = EQs[e];
      if (eq.isNotBlack()) {
        if (stuffPrinted)
          System.out.println(connector);
        stuffPrinted = true;
        eq.turnBlack();
        printEQ(eq);
        eq.turnRed();
      }
    }
    for (int e = 0; e < nGEQs; e++) {
      Equation eq = GEQs[e];
      if (eq.isNotBlack()) {
        if (stuffPrinted)
          System.out.println(connector);
        stuffPrinted = true;
        eq.turnBlack();
        printGEQ(eq);
        eq.turnRed();
      }
    }
    for (int e = 0; e < nSUBs; e++) {
      Equation eq = SUBs[e];
      if (eq.isNotBlack()) {
        if (stuffPrinted)
          System.out.println(connector);
        stuffPrinted = true;
        printSubstitution(eq);
      }
    }

    return 0;
  }

  private void checkNumberEQs(int n)
  {
    if (n < 0)
      throw new polyglot.util.InternalCompilerError("ERROR: nEQs < 0??");
  }

  private void checkNumberGEQs(int n)
  {
    if (n < 0)
      throw new polyglot.util.InternalCompilerError("ERROR: nEQs < 0??");
  }

  private void zeroVariable(int i) 
  {
    for (int e = nGEQs - 1; e >= 0; e--)
      GEQs[e].setCoef(i, 0);
    for (int e = nEQs - 1; e >= 0; e--)
      EQs[e].setCoef(i, 0);
    for (int e = nSUBs - 1; e >= 0; e--)
      SUBs[e].setCoef(i, 0);
  }
        
  private Equation newGEQ(int color, int coef)
  {
    if (GEQs == null) {
      GEQs = new Equation[allocInc];
      Equation eq = new Equation(0, color, nVars, coef);
      GEQs[0] = eq;
      nGEQs = 1;
      return eq;
    }
    
    if (nGEQs >= GEQs.length) {
      Equation[] ne = new Equation[GEQs.length + allocInc];
      System.arraycopy(GEQs, 0, ne, 0, nGEQs);
      GEQs = ne;
    }

    Equation eq = GEQs[nGEQs];
    if (eq == null) {
      eq = new Equation(0, color, nVars, coef);
      GEQs[nGEQs] = eq;
      nGEQs++;
      return eq;
    }

    eq.reset(color, nVars, coef);
    nGEQs++;
    return eq;
  }

  /*
   * Make a new BLACK inequality in a given problem 
   */
  public Equation getNewGEQ()
  {
    return newGEQ(BLACK, 0);
  }

 public Equation getNewGEQ(Equation eq)
  {
    Equation neq = newGEQ(BLACK, 0);
    neq.eqncpy(eq);
    return neq;
  }

  public Equation addGEQ()
  {
    Equation eq = getNewGEQ();
    eq.reset(BLACK, nVars, 0);
    return eq;
  }

  private Equation newEQ(int color, int coef)
  {
    if (EQs == null) {
      EQs = new Equation[allocInc];
      Equation eq = new Equation(0, color, nVars, coef);
      EQs[0] = eq;
      nEQs = 1;
      return eq;
    }
    
    if (nEQs >= EQs.length) {
      Equation[] ne = new Equation[EQs.length + allocInc];
      System.arraycopy(EQs, 0, ne, 0, nEQs);
      EQs = ne;
    }

    Equation eq = EQs[nEQs];
    if (eq == null) {
      eq = new Equation(0, color, nVars, coef);
      EQs[nEQs] = eq;
      nEQs++;
      return eq;
    }

    eq.reset(color, nVars, coef);
    nEQs++;
    return eq;
  }

  /**
   * Make a new BLACK equation in a given problem 
   */
   public Equation getNewEQ()
   {
     return newEQ(BLACK, 0);
   }

  public Equation getNewEQ(Equation eq)
  {
    Equation neq = newEQ(BLACK, 0);
    neq.eqncpy(eq);
    return neq;
  }

  public Equation addEQ()
  {
    Equation eq = getNewEQ();
    eq.reset(BLACK, nVars, 0);
    return eq;
  }

  private Equation newSUB(int color, int coef)
  {
    if (SUBs == null) {
      SUBs = new Equation[allocInc];
      Equation eq = new Equation(0, color, nVars, coef);
      SUBs[0] = eq;
      nSUBs = 1;
      return eq;
    }
    
    if (nSUBs >= SUBs.length) {
      Equation[] ne = new Equation[SUBs.length + allocInc];
      System.arraycopy(SUBs, 0, ne, 0, nSUBs);
      SUBs = ne;
    }

    Equation eq = SUBs[nSUBs];
    if (eq == null) {
      eq = new Equation(0, color, nVars, coef);
      SUBs[nSUBs] = eq;
      nSUBs++;
      return eq;
    }

    eq.reset(color, nVars, coef);
    nSUBs++;
    return eq;
  }

  private Equation getNewSUB()
  {
    return newSUB(BLACK, 0);
  }

//    private void unprotectVariable(int v)
//    {
//      int i = forwardingAddress[v];

//      if (i < 0) {
//        i = -1 - i;
//        deleteSUB(i);
//        forwardingAddress[SUBs[i].getKey()] = -i - 1;
//      } else {
//        boolean[]  bringToLife = new boolean[nSUBs];
//        int comingBack = 0;
//        for (int e = nSUBs - 1; e >= 0; e--) {
//      bringToLife[e] = (SUBs[e].isNotZero(i));
//      if (bringToLife[e])
//        comingBack++;
//        }

//        for (int e2 = nSUBs - 1; e2 >= 0; e2--) {
//      if (!bringToLife[e2])
//        continue;

//      nVars++;
//      safeVars++;
//      if (safeVars < nVars) {
//        for (int e = nGEQs - 1; e >= 0; e--) {
//          Equation eq = GEQs[e];
//          eq.copyCoef(safeVars, nVars);
//          eq.setCoef(safeVars, 0);
//        }
//        for (int e = nEQs - 1; e >= 0; e--) {
//          Equation eq = EQs[e];
//          eq.copyCoef(safeVars, nVars);
//          eq.setCoef(safeVars, 0);
//        }
//        for (int e = nSUBs - 1; e >= 0; e--) {
//          Equation eq = SUBs[e];
//          eq.copyCoef(safeVars, nVars);
//          eq.setCoef(safeVars, 0);
//        }
//        var[nVars] = var[safeVars];
//        forwardingAddress[var[nVars]] = nVars;
//      } else  {
//        for (int e = nGEQs - 1; e >= 0; e--)
//          GEQs[e].setCoef(safeVars, 0);

//        for (int e = nEQs - 1; e >= 0; e--)
//          EQs[e].setCoef(safeVars, 0);

//        for (int e = nSUBs - 1; e >= 0; e--)
//          SUBs[e].setCoef(safeVars, 0);
//      }

//      var[safeVars] = SUBs[e2].getKey();
//      forwardingAddress[SUBs[e2].getKey()] = safeVars;

//      int neweq = newEQ(SUBs[e2]);
//      EQs[neweq].setCoef(safeVars, -1);
//          deleteSUB(e2);
//        }

//        if (i < safeVars) {
//      int j = safeVars;
//      for (int e = nSUBs - 1; e >= 0; e--) {
//            Equation eq = SUBs[e];
//        eq.swapVars(i, j);
//      }
//      for (int e = nGEQs - 1; e >= 0; e--) {
//        Equation eq = GEQs[e];
//        eq.swapVars(i, j);
//      }
//      for (int e = nEQs - 1; e >= 0; e--){
//        Equation eq = EQs[e];
//        eq.swapVars(i, j);
//      }
//      int t = var[j];
//      var[j] = var[i];
//      var[i] = t;
//      forwardingAddress[var[i]] = i;
//      forwardingAddress[var[j]] = j;
//        }
//        safeVars--;
//      }
//      chainUnprotect();
//    }

//    private void constrainVariableSign(int color, int i, int sign)
//    {
//      int nV = nVars;

//      int k = forwardingAddress[i];
//      if (k < 0) {
//        k = -1 - k;

//        if (sign != 0) {
//      Equation eq = GEQs[newGEQ(SUBs[k])];
//      eq.multCoef(sign);
//      eq.addToCoef(0, -1);
//      eq.setTouched(true);
//      eq.setColor(color);
//        } else {
//      Equation eq = EQs[newEQ(SUBs[k])];
//      eq.setColor(color);
//        }
//        return;
//      }

//      if (sign != 0) {
//        Equation eq = GEQs[newGEQ()];
//        eq.setCoef(k, sign);
//        eq.setConstant(-1);
//        eq.setTouched(true);
//        eq.setColor(color);
//        return;
//      }

//      Equation eq = EQs[newEQ()];
//      eq.setCoef(k, 1);
//      eq.setColor(color);
//    }

//    private void constrainVariableValue(int color, int i, int value)
//    {
//      Equation eq;

//      int k = forwardingAddress[i];
//      if (k < 0) {
//        k = -1 - k;

//        eq = EQs[newEQ(SUBs[k])];

//      } else {
//        eq = EQs[newEQ()];
//        eq.setCoef(k, 1);
//      }
//      eq.addToCoef(0, -value);
//      eq.setColor(color);
//    }

  public boolean queryDifference(int v1, int v2, int[] bounds) 
  {
    olAssert(nSUBs == 0); 

    int lowerBound = OmegaLib.negInfinity;
    int lb1        = OmegaLib.negInfinity;
    int lb2        = OmegaLib.negInfinity;
    int upperBound = OmegaLib.posInfinity;
    int ub1        = OmegaLib.posInfinity;
    int ub2        = OmegaLib.posInfinity;
    boolean guaranteed = true;

    for (int e = nEQs - 1; e >= 0; e--) {
      Equation eq  = EQs[e];
      int      cv1 = eq.getCoefficient(v1);
      int      cv2 = eq.getCoefficient(v2);

      if ((cv1 == 0) && (cv2 == 0))
        continue;

      int i = eq.lastNZCoef(v1, v2, nVars, 1);

      if (i != 0) {
        if (i > safeVars) {
          // check to see if this variable appears anywhere else
          int e2;

          for (e2 = nEQs - 1; e2 >= 0; e2--)
            if ((e != e2) && EQs[e2].isNotZero(i))
              break;
          if (e2 < 0)
            for (e2 = nGEQs - 1; e2 >= 0; e2--)
              if ((e != e2) && GEQs[e2].isNotZero(i))
                break;
          if (e2 < 0)
            for (e2 = nSUBs - 1; e2 >= 0; e2--)
              if ((e != e2) && SUBs[e2].isNotZero(i))
                break;
          if (e2 >= 0)
            guaranteed = false;
        } else
          guaranteed = false;
        continue;
      }

      if ((cv1 * cv2) == -1) {
        // found exact difference
        int d = - cv1 * eq.getConstant();
        bounds[0] = max(lowerBound, d);
        bounds[1] = min(upperBound, d);
        return true;
      }

      if (cv1 == 0) {
        lb2 = -eq.getConstant() / cv2;
        ub2 = lb2;
      } else if (cv2 == 0) {
        lb1 = -eq.getConstant() / cv1;
        ub1 = lb1;
      } else
        guaranteed = false;
    }

    boolean[] isDead = new boolean[nGEQs];

    for (int e = nGEQs - 1; e >= 0; e--)
      isDead[e] = false;

    for (int i = nVars; i > 0; i--) {
      if ((i == v1) || (i == v2))
        continue;

      int e;
      for (e = nGEQs - 1; e >= 0; e--) 
        if (!isDead[e] && GEQs[e].isNotZero(i))
          break;

      int e2;
      if (e < 0)
        e2 = e;
      else if (GEQs[e].getCoefficient(i) > 0) {
        for (e2 = e - 1; e2 >= 0; e2--)
          if (!isDead[e2] && GEQs[e2].getCoefficient(i) < 0)
            break;
      } else {
        for (e2 = e - 1; e2 >= 0; e2--)
          if (!isDead[e2] && GEQs[e2].getCoefficient(i) > 0)
            break;
      }

      if (e2 >= 0)
        continue;

      if (anyNonZeroCoef(SUBs, nSUBs, i))
        continue;

      if (anyNonZeroCoef(EQs, nEQs, i))
        continue;

      if (e >= 0) {
        isDead[e] = true;
        for (e--; e >= 0; e--)
          if (GEQs[e].isNotZero(i))
            isDead[e] = true;
      }
    }

    for (int e = nGEQs - 1; e >= 0; e--) {
      if (isDead[e])
        continue;

      Equation eqe = GEQs[e];
      int      cv1 = eqe.getCoefficient(v1);
      int      cv2 = eqe.getCoefficient(v2);
      int      c0  = eqe.getConstant();

      if ((cv1 == 0) && (cv2 == 0))
        continue;

      int i = eqe.lastNZCoef(v1, v2, nVars, 1);
      if (i != 0) {
        guaranteed = false;
        continue;
      }

      if ((cv1 * cv2) == -1) {
        // found relative difference
        if (cv1 == 1) { 
          // v1 - v2 + c >= 0
          lowerBound = max(lowerBound, - c0);
        } else {
          // v2 - v1 + c >= 0
          // c >= v1-v2
          upperBound = min(upperBound, c0);
        }
      } else if ((cv1 == 0) && (cv2 > 0)) 
        lb2 = -c0 / cv2;
      else if ((cv1 == 0) && (cv2 < 0))
        ub2 = -c0 / cv2;
      else if ((cv2 == 0) && (cv1 > 0)) 
        lb1 = -c0 / cv1;
      else if ((cv2 == 0) && (cv1 < 0)) 
        ub1 = -c0 / cv1;
      else
        guaranteed = false;
    }

    //   ub1-lb2 >= v1-v2 >= lb1-ub2
    
    if ((OmegaLib.negInfinity < lb2) && (ub1 < OmegaLib.posInfinity))
      upperBound = min(upperBound, ub1 - lb2);

    if ((OmegaLib.negInfinity < lb1) && (ub2 < OmegaLib.posInfinity))
      lowerBound = max(lowerBound, lb1 - ub2);

    if (lowerBound >= upperBound)
      guaranteed = false;

    bounds[0] = lowerBound;
    bounds[1] = upperBound;

    return guaranteed;
  }

  private boolean queryVariable(int i, int[] bounds)
  {
    for (int j = 1; j <= safeVars; j++)
      if (var[j] > 0) 
        olAssert(forwardingAddress[var[j]] == j);

    olAssert(i > 0);
    i = forwardingAddress[i];
    olAssert(i != 0);

    int lowerBound = OmegaLib.negInfinity;
    int upperBound = OmegaLib.posInfinity;

    if (i < 0) {
      i = -i - 1;
      boolean easy = SUBs[i].anyNZCoef(nVars, 1);
      if (easy) {
        int x = SUBs[i].getConstant();
        bounds[0] = x;
        bounds[1] = x;
        return false;
      }
      return true;
    }

    boolean coupled = anyNonZeroCoef(SUBs, nSUBs, i);

    for (int e = nEQs - 1; e >= 0; e--) {
      Equation eq = EQs[e];
      if (eq.isZero(i))
        continue;

      int j = eq.lastNZCoef(i, 0, nVars, 1);
      if (j > 0) {
        coupled = true;
        continue;
      }

      int ci = eq.getCoefficient(i);
      int c0 = eq.getConstant();
      int x = -ci * c0;
      bounds[0] = x;
      bounds[1] = x;
      return false;
    }

    for (int e = nGEQs - 1; e >= 0; e--) {
      Equation eq = GEQs[e];
      int x= eq.getCoefficient(i);
      if (x == 0)
        continue;
      if (eq.getKey() == i) {
        lowerBound = max(lowerBound, -x);
      } else if (eq.getKey() == -i) {
        upperBound = min(upperBound, x);
      } else
        coupled = true;
    }

    bounds[0] = lowerBound;
    bounds[1] = upperBound;

    return coupled;
  }

  public boolean queryVariableBounds(int i, int[] bounds)
  {
    bounds[0] = OmegaLib.negInfinity;
    bounds[1] = OmegaLib.posInfinity;
    boolean coupled = queryVariable(i, bounds);
    if (!coupled || ((nVars == 1) && (forwardingAddress[i] == 1)))
      return false;
    if ((abs(forwardingAddress[i]) == 1) && ((nVars + nSUBs) == 2) && ((nEQs + nSUBs) == 1)) {
      queryCoupledVariable(i, bounds,  (int[]) bounds.clone());
      return false;
    }
    return true;
  }

  private boolean queryCoupledVariable(int i, int[] bounds, int[] oldBounds)
  {
    Equation eqn;
    int sign;
    int v;

    int lowerBound = oldBounds[0];
    int upperBound = oldBounds[1];

    if ((abs(forwardingAddress[i]) != 1) || ((nVars + nSUBs) != 2) || ((nEQs + nSUBs) != 1))
      throw new polyglot.util.InternalCompilerError("queryCoupledVariablecalled with bad parameters " + this);

    if (forwardingAddress[i] == -1) {
      eqn = SUBs[0];
      sign = 1;
      v = 1;
    } else {
      eqn = EQs[0];
      sign = -eqn.getCoefficient(1);
      v = 2;
    }

    /* Variable i is defined in terms of variable v */

    for (int e = nGEQs - 1; e >= 0; e--) {
      Equation eqe = GEQs[e];
      int c = eqe.getCoefficient(v);
      if (c == 0)
        continue;

      if (c == 1) {
        lowerBound = max(lowerBound, -eqe.getConstant());
      } else {
        upperBound = min(upperBound, eqe.getConstant());
      }
    }

    /* lowerBound and upperBound are bounds on the value of v */

    if (lowerBound > upperBound) {
      bounds[0] = OmegaLib.posInfinity;
      bounds[1] = OmegaLib.negInfinity;
      return false;
    }

    int b1;
    int cnv = eqn.getCoefficient(v);
    int cn0 = eqn.getConstant();
    if (lowerBound == OmegaLib.negInfinity) {
      if (cnv > 0)
        b1 = sign * OmegaLib.negInfinity;
      else
        b1 = -sign * OmegaLib.negInfinity;
    } else
      b1 = sign * (cn0 + cnv * lowerBound);

    int b2;
    if (upperBound == OmegaLib.posInfinity) {
      if (cnv > 0)
        b2 = sign * OmegaLib.posInfinity;
      else
        b2 = -sign * OmegaLib.posInfinity;
    } else
      b2 = sign * (cn0 + cnv * upperBound);

    /* b1 and b2 are bounds on the value of i (don't know which is upper bound) */

    int l = bounds[0];
    int u = bounds[1];
    if (b1 <= b2) {
      l = max(l, b1);
      u = min(u, b2);
    } else {
      l = max(l, b2);
      u = min(u, b1);
    }

    bounds[0] = l;
    bounds[1] = u;
    return (l <= 0) && (0 <= u) && (intMod(cn0, abs(cnv)) == 0);
  }

  private boolean combineToTighten() 
  {
    int effort = min(12 + 5 * (nVars - safeVars), 23);

    if (omegaLib.trace) {
      System.out.println("in combineToTighten (" + effort + "," + nGEQs + ")");
      printProblem();
    }
    if (nGEQs > effort)  {
      if (omegaLib.trace)
        System.out.println("too complicated to tighten");

      return true;
    }

  doneCombining:
    for (int e = 0; e < nGEQs; e++) {
      Equation eqe = GEQs[e];
      for (int e2 = 0; e2 < e; e2++) {
        Equation eqe2 = GEQs[e2];
        int      g    = eqe.gcdSumOfProd(eqe2, 1, 1, nVars, 1);
        int      diff = eqe.getConstant() + eqe2.getConstant();

        if ((g > 1) && ((diff % g) != 0)) {
          Equation eqe3 = getNewGEQ();
          eqe3.sumAndDivide(eqe, eqe2, g, nVars);
          eqe3.setColor(eqe, eqe2);
          eqe3.setTouched(true);
          if (omegaLib.trace) {
            System.out.print ( "Combined     ");
            printGEQ(eqe);
            System.out.print("\n         and ");
            printGEQ(eqe2);
            System.out.print("\n to get #");
            System.out.print(eqe3);
            System.out.print(": ");
            printGEQ(eqe3);
            System.out.println("");
          }

          if ((nGEQs > (effort + 5)) || (nGEQs > (maxmaxGEQs - 10)))
            break doneCombining;
        }
      }
    }

    if (normalize() == normalizeFalse)
      return false;

    while (nEQs > 0) {
      if (SOLVE_F == solveEQ())
        return false;
      if (normalize() == normalizeFalse)
        return false;
    }

    return true;
  }

  private void noteEssential(boolean onlyWildcards) 
  {
    for (int e = nGEQs - 1; e >= 0; e--) {
      Equation eq = GEQs[e];
      eq.setEssential(false);
      eq.setVarCount(0);
    }

    if (onlyWildcards) {
      for (int e = nGEQs - 1; e >= 0; e--)  {
        Equation eq = GEQs[e];
        int      i  = eq.lastCoefGt1(nVars, safeVars + 1);
        eq.setEssential(i > safeVars);
      }
    }

    for (int i = nVars; i >= 1; i--) {
      int onlyLB = -1;
      int onlyUB = -1;
      for (int e = nGEQs - 1; e >= 0; e--) {
        Equation eq = GEQs[e];
        int      ci = eq.getCoefficient(i);
        if (ci > 0) {
          eq.incVarCount();
          if (onlyLB == -1)
            onlyLB = e;
          else
            onlyLB = -2;
        } else if (ci < 0) {
          eq.incVarCount();
          if (onlyUB == -1)
            onlyUB = e;
          else
            onlyUB = -2;
        }
        if (onlyUB >= 0) {
          if (omegaLib.trace) {
            System.out.println("only UB: ");
            printGEQ(GEQs[onlyUB]);
            System.out.println("");
          }
          GEQs[onlyUB].setEssential(true);
        }

        if (onlyLB >= 0) {
          if (omegaLib.trace) {
            System.out.println("only LB: ");
            printGEQ(GEQs[onlyLB]);
            System.out.println("");
          }
          GEQs[onlyLB].setEssential(true);
        }
      }
    }

    for (int e = nGEQs - 1; e >= 0; e--) {
      Equation eqe = GEQs[e];
      if (eqe.isEssential() || (eqe.getVarCount() <= 1))
        continue;

      int i1 = eqe.lastNZCoef(nVars, 1);
      int i2 = eqe.lastNZCoef(i1 - 1, 1);
      int i3 = eqe.lastNZCoef(i2 - 1, 1);

      olAssert(i2 >= 1);

      int e2;
      for (e2 = nGEQs - 1; e2 >= 0; e2--) {
        if (e == e2)
          continue;

        Equation eqe2 = GEQs[e2];
        int crossProduct = eqe.getCoefficient(i1) * eqe2.getCoefficient(i1);
        crossProduct += eqe.getCoefficient(i2) * eqe2.getCoefficient(i2);
        crossProduct += eqe2.crossProduct(i3, 1);
        if (crossProduct > 0) {
          if (omegaLib.trace) {
            System.out.print("Cross product of ");
            System.out.print(e);
            System.out.print(" and ");
            System.out.print(e2);
            System.out.print(" is ");
            System.out.print(crossProduct);
          }
          break;
        }
      }

      if (e2 < 0)
        eqe.setEssential(true);
    }

    if (omegaLib.trace) {
      System.out.println("Computed essential equations");
      System.out.println("essential equations:");
      for (int e = 0; e < nGEQs; e++) {
        Equation eqe = GEQs[e];
        if (eqe.isEssential()) {
          printGEQ(eqe);
          System.out.println("");
        }
      }

      System.out.println("potentially redundant equations:");
      for (int e = 0; e < nGEQs; e++) {
        Equation eqe = GEQs[e];
        if (!eqe.isEssential()) {
          printGEQ(eqe);
          System.out.println("");
        }
      }
    }
  }

  private final class SuccListStruct
  {
    public int   num;
    public int   notEssential;
    public int[] var;
    public int[] diff;
    public int[] eqn;

    public SuccListStruct(int nVars)
    {
      num = 0;
      notEssential = 0;
      var  = new int[nVars];
      diff = new int[nVars];
      eqn  = new int[nVars];
    }

    public void reset()
    {
      num = 0;
      notEssential = 0;
    }
  }

  private boolean chainKill(int color, boolean onlyWildcards) 
  {
    int[]     essentialPred     = new int[nVars + 1];
    int[]     inChain           = new int[nVars + 1];
    boolean[] goodStartingPoint = new boolean[nVars + 1];
    boolean[] tryToEliminate    = new boolean[nVars + 1];
    boolean[] redundant         = new boolean[nVars + 1];
    boolean   triedDoubleKill   = false;
    boolean   anyKilled         = false;
    int       canHandle         = 0;

    SuccListStruct[] succ = new SuccListStruct[nVars + 1];

    for (int v1 = 0; v1 <= nVars; v1++)
      succ[v1] = new SuccListStruct(nVars);

    while (true) {
      int anyToKill = 0;
      anyKilled = false;
      canHandle = 0;
   
      for (int v1 = 0; v1 <= nVars; v1++) {
        succ[v1].reset();
        goodStartingPoint[v1] = false;
        inChain[v1] = -1;
        essentialPred[v1] = 0;
      }

      int essentialEquations = 0;
      for (int e = 0; e < nGEQs; e++) {
        Equation eq = GEQs[e];
        redundant[e] = false;
        tryToEliminate[e] = !eq.isEssential();
        if (eq.isEssential())
          essentialEquations++;
        if ((color != BLACK) && (eq.isBlack()))
          tryToEliminate[e] = false;
      }

      if (essentialEquations == nGEQs)
        return false;

      if ((2 * essentialEquations) < nVars)
        return true;

      int v1 = -1;
      int v2 = -1;

      for (int e = 0; e < nGEQs; e++) {
        if (!tryToEliminate[e])
          continue;

        Equation eq = GEQs[e];
        if (eq.getVarCount() > 2)
          continue;

        if (!eq.findDifference(nVars))
          continue;

        v1 = eq.v1;
        v2 = eq.v2;

        olAssert((v1 == 0) || (eq.getCoefficient(v1) == 1));
        olAssert((v2 == 0) || (eq.getCoefficient(v2) == -1));

        succ[v2].notEssential++;
        int s = succ[v2].num++;
        succ[v2].eqn[s] = e;
        succ[v2].var[s] = eq.v1;
        succ[v2].diff[s] = -eq.getConstant();
        goodStartingPoint[v2] = true;
        anyToKill++;
        canHandle++;
      }

      if (anyToKill == 0)
        return canHandle < nGEQs;

      for (int e = 0; e < nGEQs; e++) {
        if (tryToEliminate[e])
          continue;

        Equation eq = GEQs[e];
        if (eq.getVarCount() > 2)
          continue;

        if (!eq.findDifference(nVars))
          continue;

        v1 = eq.v1;
        v2 = eq.v2;

        olAssert((v1 == 0) || (eq.getCoefficient(v1) == 1));
        olAssert((v2 == 0) || (eq.getCoefficient(v2) == -1));

        int s = succ[v2].num++;
        essentialPred[v1]++;
        succ[v2].eqn[s] = e;
        succ[v2].var[s] = eq.v1;
        succ[v2].diff[s] = -eq.getConstant();
        canHandle++;
      }

      if (omegaLib.trace) {
        System.out.println("In chainkill: [");
        for (int v11 = 0; v11 <= nVars; v11++) {
          System.out.print("#");
          System.out.print(essentialPred[v11]);
          System.out.print(" <= ");
          System.out.print(variable(v11));
          System.out.print(": ");
          int s;
          for (s = 0; s < succ[v11].notEssential; s++) {
            System.out.print(" ");
            System.out.print(variable(succ[v11].var[s]));
            System.out.print("(");
            System.out.print(succ[v1].diff[s]);
            System.out.print(") ");
          }
          for (; s < succ[v1].num; s++) {
            System.out.print(" ");
            System.out.print(variable(succ[v11].var[s]));
            System.out.print("(");
            System.out.print(succ[v11].diff[s]);
            System.out.print(") ");
          }
          System.out.println("");
        }
      }

      for (; v1 <= nVars; v1++)
        if ((succ[v1].num == 1) && (succ[v1].notEssential == 1)) {
          succ[v1].notEssential--;
          essentialPred[succ[v1].var[succ[v1].notEssential]]++;
        }

      if (omegaLib.trace)
        System.out.println("Trying quick double kill:");

      for (int v11 = 0; v11 <= nVars; v11++) {
        SuccListStruct success = succ[v11];
      nextVictim:
        for (int s1a = 0; s1a < success.notEssential; s1a++) {
          int v3 = success.var[s1a];
          for (int s1b = 0; s1b < success.num; s1b++) {
            if (s1a == s1b)
              continue;

            v2 = success.var[s1b];
            SuccListStruct success2 = succ[v2];
            for (int s2 = 0; s2 < success2.num; s2++) {
              if ((success2.var[s2] != v3) || ((success.diff[s1b] + success2.diff[s2]) < success.diff[s1a]))
                continue;

              if (omegaLib.trace) {
                System.out.println("quick double kill: "); 
                printGEQ(GEQs[success.eqn[s1a]]);
                System.out.println(""); 
              }

              redundant[success.eqn[s1a]] = true;
              anyKilled = true;
              anyToKill--;
              continue nextVictim;
            }
          }
        }
      }

      if (!anyKilled)
        break;

      for (int e = nGEQs - 1; e >= 0; e--) {
        if (!redundant[e])
          continue;

        if (omegaLib.trace) {
          System.out.println("Deleting ");
          printGEQ(GEQs[e]);
          System.out.println("");
        }

        deleteGEQ(e);
      }

      if (anyToKill == 0)
        return canHandle < nGEQs;

      noteEssential(onlyWildcards);
      triedDoubleKill = true;
    }

    for (int v1 = 0; v1 <= nVars; v1++)
      if ((succ[v1].num == succ[v1].notEssential) && (succ[v1].notEssential > 0)) {
        succ[v1].notEssential--;
        essentialPred[succ[v1].var[succ[v1].notEssential]]++;
      }

    while (true) { // pick a place to start
      int v1;
      for (v1 = 0; v1 <= nVars; v1++)
        if ((essentialPred[v1] == 0) && (succ[v1].num > succ[v1].notEssential))
          break;

      if (v1 > nVars) 
        for (v1 = 0; v1 <= nVars; v1++)
          if (goodStartingPoint[v1]  && (succ[v1].num > succ[v1].notEssential))
            break;

      if (v1 > nVars)
        break;

      int[] chain       = new int[nVars];
      int[] distance    = new int[nVars];
      int   chainLength = 1;

      chain[0] = v1;
      distance[0] = 0;
      inChain[v1] = 0;

      while (succ[v1].num > succ[v1].notEssential) {
        int s = succ[v1].num - 1;
        if (inChain[succ[v1].var[s]] >= 0) {
          // Found cycle, don't do anything with them yet
          break;
        }
        succ[v1].num = s;

        distance[chainLength] = distance[chainLength - 1] + succ[v1].diff[s];
        v1 = succ[v1].var[s];
        chain[chainLength] = v1;
        essentialPred[v1]--;
        olAssert(essentialPred[v1] >= 0);
        inChain[v1] = chainLength;
        chainLength++;
      }

      if (omegaLib.trace) {
        System.out.println("Found chain: ");
        for (int c = 0; c < chainLength; c++) {
          System.out.print(variable(chain[c]));
          System.out.print(":");
          System.out.print(distance[c]);
        }
        System.out.println("");
      }
        
      for (int c = 0; c < chainLength; c++) {
        SuccListStruct success = succ[chain[c]];
        for (int s = 0; s < success.notEssential; s++) {
          int df = success.diff[s];
          if (inChain[success.var[s]] < 0) {
              continue;
          }
          int dist = distance[inChain[success.var[s]]] - distance[c];
          if (omegaLib.trace) {
            System.out.print("checking for ");
            System.out.print(variable(v1));
            System.out.print(" + ");
            System.out.print(df);
            System.out.print(" <= ");
            System.out.print(variable(success.var[s]));
            System.out.print(" ");
          }
          if (inChain[success.var[s]] > (c + 1)) {
            if (omegaLib.trace)
              System.out.print("is in chain");
            if (dist >= df) {
              if (omegaLib.trace) 
                System.out.print(" is redundant");
              redundant[success.eqn[s]] = true;
            }
          }
          if (omegaLib.trace)
            System.out.println("");
        }
      }

      for (int c = 0; c < chainLength; c++) 
        inChain[chain[c]] = -1;
    }

    for (int e = nGEQs-1; e >= 0;e--)
      if (redundant[e])  {
        if (omegaLib.trace) {
          System.out.println("Deleting ");
          printGEQ(GEQs[e]);
          System.out.println("");
        }
      deleteGEQ(e);
      anyKilled = true;
    }

    if (anyKilled)
      noteEssential(onlyWildcards);

    if (anyKilled && omegaLib.trace) {
      System.out.println("Result:");
      printProblem();
    }
    if (omegaLib.trace) {
      System.out.println("] end chainkill");
      printProblem();
    }
    return canHandle < nGEQs;
  }

  private boolean quickKill(boolean onlyWildcards, boolean desperate)
  {
    boolean[] isDead           = new boolean[nGEQs];
    boolean[] involvesWildcard = new boolean[nGEQs];
    boolean   anyKilled        = false;
    int[]     killOrder        = new int[nGEQs];
    long[]    P                = new long[nGEQs];
    long[]    Z                = new long[nGEQs];
    long[]    N                = new long[nGEQs];
    long      PP; /* possible Positives */
    long      PZ; /* possible zeros */
    long      PN; /* possible negatives */

    int maxVarCount = 0;
    if (!onlyWildcards)
      if (!combineToTighten())
        return false;

    noteEssential(onlyWildcards);

    boolean moreToDo = chainKill(BLACK, onlyWildcards);

    if (!moreToDo)
      return true;

    if (!desperate && nGEQs > 60) {
      if (omegaLib.trace)
        System.out.println("too complicated to quick kill");

      return true;
    }

    if (omegaLib.trace) {
      System.out.println("in eliminate Redundant:");
      printProblem();
    }

    for (int e = nGEQs - 1; e >= 0; e--) {
      int tmp = 1;
      isDead[e] = false;
      involvesWildcard[e] = false;
      P[e] = 0;
      Z[e] = 0;
      N[e] = 0;

      Equation eqe = GEQs[e];
      int vc = eqe.getVarCount();
      if (maxVarCount < vc)
        maxVarCount = vc;

      for (int i = nVars; i >= 1; i--) {
        int coef = eqe.getCoefficient(i);
        if (coef == 0)
          Z[e] |= tmp;
        else {
          if (i > safeVars)
            involvesWildcard[e] = true;
          if (coef < 0)
            N[e] |= tmp;
          else
            P[e] |= tmp;
        }
        tmp <<= 1;
      }
    }

    int ordered = 0;
    for (int i = 1; ordered < nGEQs && i <= maxVarCount; i++) 
      for (int e = 0; e < nGEQs; e++) {
        Equation eqe = GEQs[e];
        if ((!eqe.isEssential() || involvesWildcard[e]) && (eqe.getVarCount() == i)) {
          for (int e2 = 0; e2 < ordered; e2++)
            olAssert(killOrder[e2] != e);
          killOrder[ordered++] = e;
        }
      }

    if (omegaLib.trace) {
      System.out.println("Prefered kill order:");
      for (int e3I = ordered - 1; e3I >= 0; e3I--) {
        System.out.print(nGEQs - 1 - e3I);
        System.out.print(": ");
        printGEQ(GEQs[killOrder[e3I]]);
        System.out.println("");
      }
    }

    int e3U = ordered - 1;
    while (e3U >= 0) {
      int e3L;
      for (e3L = e3U; e3L > 0; e3L--) {
       if (GEQs[killOrder[e3L - 1]].getVarCount() != GEQs[killOrder[e3U]].getVarCount())
         break;
      }

      if (omegaLib.trace) 
        System.out.println("Trying to kill " + e3U + ".." + e3L);

      for (int e1 = 0; e1 < nGEQs; e1++) {
        if (isDead[e1])
          continue;

        Equation eqe1 = GEQs[e1];

        for (int e2 = 0; e2 < nGEQs; e2++) {
          if ((e1 == e2) || isDead[e2])
            continue;

          Equation eqe2 = GEQs[e2];

          int     cp1   = 0;
          int     cp2   = 0;
          int     cq1   = 0;
          int     cq2   = 0;
          int     p     = -1;
          int     q     = -1;
          int     alpha = -1;
          boolean found = false;
        foundPQ:
          for (p = nVars; p > 1; p--) {
            cp1 = eqe1.getCoefficient(p);
            cp2 = eqe2.getCoefficient(p);

            if ((abs(cp1) > 0x7fff) || (abs(cp2) > 0x7fff))
              continue;

            for (q = p - 1; q > 0; q--) {
              cq1 = eqe1.getCoefficient(q);
              cq2 = eqe2.getCoefficient(q);

              if ((abs(cq1) > 0x7fff) || (abs(cq2) > 0x7fff))
                continue;

              alpha = checkMultiply(cp1, cq2) - checkMultiply(cp2, cq1);
              if ((alpha != 0) && (abs(alpha) <= 0x7fff)) {
                found = true;
                break foundPQ;
              }
            }
          }

          if (!found)
            continue;

          PZ = (Z[e1] & Z[e2]) | (P[e1] & N[e2]) | (N[e1] & P[e2]);
          PP = P[e1] | P[e2];
          PN = N[e1] | N[e2];

          if (omegaLib.trace) {
            System.out.print("Considering combination of ");
            printGEQ((eqe1));
            System.out.print(" and  ");
            printGEQ((eqe2));
            System.out.println("");
          }

        nextE3:
          for (int e3I = e3U; e3I >= e3L; e3I--) {
            int e3 = killOrder[e3I];
            if (isDead[e3] || (e3 == e1) || (e3 == e2))
              continue;

            if (!implies(Z[e3], PZ))
              continue;

            Equation eqe3   = GEQs[e3];
            int      cp3    = eqe3.getCoefficient(p);
            int      cq3    = eqe3.getCoefficient(q);
            int      alpha1 = checkMultiply(cq2, cp3) - checkMultiply(cp2, cq3);
            int      alpha2 = - (checkMultiply(cq1, cp3) - checkMultiply(cp1, cq3));
            int      alpha3 = alpha;

            if ((abs(alpha1) > 0x7fff) || (abs(alpha2) > 0x7fff) || ((alpha1 * alpha2) <= 0))
              continue;

            if (alpha1 < 0) {
              alpha1 = -alpha1;
              alpha2 = -alpha2;
              alpha3 = -alpha3;
            }

            int g = gcd(gcd(abs(alpha1), abs(alpha2)), abs(alpha3));
            alpha1 /= g;
            alpha2 /= g;
            alpha3 /= g;

            if (omegaLib.trace) {
              System.out.print(alpha1);
              System.out.print("e1 + ");
              System.out.print(alpha2);
              System.out.print("e2 = ");
              System.out.print(alpha3);
              System.out.print("e3: ");
              printGEQ((eqe3));
              System.out.println("");
            }

            if (alpha3 > 0) { /* Trying to prove e3 is redundant */
              if (!implies(P[e3], PP) || !implies(N[e3], PN))
                continue;

              if ((eqe3.isBlack()) && ((eqe1.isNotBlack()) || (eqe2.isNotBlack())))
                continue;

              for (int k = nVars; k >= 1; k--)
                if (checkMultiply(alpha3,  eqe3.getCoefficient(k)) != checkMultiply(alpha1, eqe1.getCoefficient(k)) + checkMultiply(alpha2, eqe2.getCoefficient(k)))
                  continue nextE3;

              int c = alpha1 * eqe1.getConstant() + alpha2 * eqe2.getConstant();
              if (c < alpha3 * (eqe3.getConstant() + 1)) {
                if (omegaLib.trace) {
                  System.out.println("found redundant inequality");
                  System.out.print("alpha1, alpha2, alpha3 = ");
                  System.out.print(alpha1);
                  System.out.print(",");
                  System.out.print(alpha2);
                  System.out.print(",");
                  System.out.print(alpha3);
                  printGEQ((eqe1));
                  System.out.println("");
                  printGEQ((eqe2));
                  System.out.println("\n=> ");
                  printGEQ((eqe3));
                  System.out.println("\n");
                  olAssert(moreToDo);
                }

                anyKilled = true;
                isDead[e3] = true;
              }
              continue;
            }

            // Trying to prove e3 <= 0 and therefore e3 = 0, or trying to prove e3 < 0, and
            // therefore the problem has no solutions.

            if (!implies(P[e3], PN) || !implies(N[e3], PP))
              continue nextE3;

            // alpha3 = alpha3;
                                /* verify alpha1*v1+alpha2*v2 = alpha3*v3 */
            for (int k = nVars; k >= 1; k--)
              if ((alpha3 * eqe3.getCoefficient(k)) != (alpha1 * eqe1.getCoefficient(k) + alpha2 * eqe2.getCoefficient(k)))
                continue nextE3;

            if (omegaLib.trace)
              System.out.println("All but constant term checked");

            int c = alpha1 * eqe1.getConstant() + alpha2 * eqe2.getConstant();
            if (omegaLib.trace) {
              System.out.println("All but constant term checked");
              System.out.print("Constant term is ");
              System.out.print(alpha3 * eqe3.getConstant());
              System.out.print(" vs ");
              System.out.println(alpha3 * (eqe3.getConstant() - 1));
            }

            if (c < (alpha3 * (eqe3.getConstant()))) { /* We just proved e3 < 0, so no solutions exist */
              if (omegaLib.trace) {
                System.out.println("found implied over tight inequality");
                System.out.print("alpha1, alpha2, alpha3 = ");
                System.out.print(alpha1);
                System.out.print(",");
                System.out.print(alpha2);
                System.out.print(",");
                System.out.print(-alpha3);
                printGEQ((eqe1));
                System.out.println("");
                printGEQ((eqe2));
                System.out.println("\n=> not ");
                printGEQ((eqe3));
                System.out.println("\n");
              }
              return false;
            }

            if (c < (alpha3 * (eqe3.getConstant() - 1))) { /* We just proved that e3 <=0, so e3 = 0 */
              if (omegaLib.trace) {
                System.out.println("found implied tight inequality");
                System.out.print("alpha1, alpha2, alpha3 = ");
                System.out.print(alpha1);
                System.out.print(",");
                System.out.print(alpha2);
                System.out.print(",");
                System.out.print(-alpha3);
                printGEQ((eqe1));
                System.out.println("");
                printGEQ((eqe2));
                System.out.println("\n=> inverse ");
                printGEQ((eqe3));
                System.out.println("\n");
              }
              Equation neweq = getNewEQ(eqe3);
              addingEqualityConstraint(neweq);
              isDead[e3] = true;
            }
          }
        }
      }
      e3U = e3L - 1;
    }

    for (int e = nGEQs - 1; e >= 0; e--)
      if (isDead[e])
        deleteGEQ(e);

    if (anyKilled && omegaLib.trace) {
      System.out.println("\nResult:");
      printProblem();
    }
    return true;
  }

//    private boolean checkIfSingleVar(Equation e, int i)
//    {
//      for (; i > 0; i--)
//        if (e.isNotZero(i)) {
//      i--;
//      break;
//        }
//      for (; i > 0; i--)
//        if (e.isNotZero(i))
//      break;
//      return (i == 0);
//    }

  private boolean singleVarGEQ(Equation e) 
  {
    int key = e.getKey();
    return (!e.isTouched() && (key != 0) && (-nVars <= key) && (key <= nVars));
  }

  public int numberNZ()
  {
    int numberNZs = 0;

    for (int e = 0; e < nGEQs; e++) {
      Equation eq = GEQs[e];
      numberNZs += eq.numNZCoefs(nVars, 1);
    }
    for (int e = 0; e < nEQs; e++) {
      Equation eq = EQs[e];
      numberNZs += eq.numNZCoefs(nVars, 1);
    }

    return numberNZs;
  }

  public int difficulty()
  {
    int numberNZs     = 0;
    int maxMinAbsCoef = 0;
    int sumMinAbsCoef = 0;

    for (int e = 0; e < nGEQs; e++) {
      Equation eq      = GEQs[e];
      int      maxCoef = 0;
      for (int i = 1; i <= nVars; i++) {
        int c = eq.getCoefficient(i);
        if (c == 0)
          continue;
        int a = abs(c);
        maxCoef = max(maxCoef, a);
        numberNZs++;
      }
      int nextCoef = 0;
      for (int i = 1; i <= nVars; i++) {
        int c = eq.getCoefficient(i);
        if (c == 0)
          continue;
        int a = abs(c);
        if (a < maxCoef)
          nextCoef = max(nextCoef, a);
        else if (a == maxCoef)
          maxCoef = Integer.MAX_VALUE;
      }
      maxMinAbsCoef = max(maxMinAbsCoef, nextCoef);
      sumMinAbsCoef += nextCoef;
    }

    for (int e = 0; e < nEQs; e++) {
      Equation eq      = EQs[e];
      int      maxCoef = 0;
      for (int i = 1; i <= nVars; i++) {
        int c = eq.getCoefficient(i);
        if (c == 0)
          continue;
        int a = abs(c);
        maxCoef = max(maxCoef, a);
        numberNZs++;
      }
      int nextCoef = 0;
      for (int i = 1; i <= nVars; i++) {
        int c = eq.getCoefficient(i);
        if (c == 0)
          continue;
        int a = abs(c);
        if (a < maxCoef)
          nextCoef = max(nextCoef, a);
        else if (a == maxCoef)
          maxCoef = Integer.MAX_VALUE;
      }
      maxMinAbsCoef = max(maxMinAbsCoef, nextCoef);
      sumMinAbsCoef += nextCoef;
    }

    return max(numberNZs, 2 * numberNZs + 2 * maxMinAbsCoef + sumMinAbsCoef);
  }

  private void deleteBlack()
  {
    boolean[] RedVar = new boolean[nVars + 1];

    olAssert(nSUBs == 0);

    for (int e = nEQs - 1; e >=0; e--) {
      Equation eq = EQs[e];
      if (eq.isNotRed()) {
        deleteEQ(e);
        continue;
      }
      eq.setTrueIfNotZero(RedVar, nVars, safeVars + 1);
    }

    for (int e = nGEQs - 1; e >= 0; e--) {
      Equation eq = GEQs[e];
      if (eq.isNotRed()) {
        deleteGEQ(e);
        continue;
      }

      eq.setTrueIfNotZero(RedVar, nVars, safeVars + 1);
    }

    olAssert(nSUBs == 0);

    for (int i = nVars; i > safeVars; i--) {
      if (!RedVar[i])
        deleteVariable(i);
    }
  }

  private void deleteRed()
  {
    boolean[] BlackVar = new boolean[nVars + 1];

    olAssert(nSUBs == 0);
    for (int e = nEQs - 1; e >= 0; e--) {
      Equation eq = EQs[e];

      if (eq.isNotBlack()) {
        deleteEQ(e);
        continue;
      }

      eq.setTrueIfNotZero(BlackVar, nVars, safeVars + 1);
    }

    for (int e = nGEQs - 1; e >= 0; e--) {
      Equation eq = GEQs[e];

      if (!eq.isBlack()) {
        deleteGEQ(e);
        continue;
      }

      eq.setTrueIfNotZero(BlackVar, nVars, safeVars + 1);
    }

    olAssert(nSUBs == 0);

    for (int i = nVars; i > safeVars; i--) {
      if (!BlackVar[i])
        deleteVariable(i);
    }
  }

  public void turnRedBlack()
  {
    for (int e = nEQs - 1; e >= 0; e--)
      EQs[e].turnBlack();
    for (int e = nGEQs - 1; e >= 0; e--)
      GEQs[e].turnBlack();
  }

  private void negateGEQ(int e)
  {
    Equation eq = GEQs[e];
    eq.negateCoefficients(nVars);
    eq.addToCoef(0, -1);
  }

  private void deleteVariable(int i)
  {
    if (i < safeVars) {
      int j = safeVars;
      for (int e = nGEQs - 1; e >= 0; e--) {
        Equation eq = GEQs[e];
        eq.setTouched(true);
        eq.copyCoef(j, i);
        eq.copyCoef(nVars, j);
      }
      for (int e = nEQs - 1; e >= 0; e--) {
        Equation eq = EQs[e];
        eq.copyCoef(j, i);
        eq.copyCoef(nVars, j);
      }
      for (int e = nSUBs - 1; e >= 0; e--) {
        Equation eq = SUBs[e];
        eq.copyCoef(j, i);
        eq.copyCoef(nVars, j);
      }
      var[i] = var[j];
      var[j] = var[nVars];
    } else if (i < nVars) {
      for (int e = nGEQs - 1; e >= 0; e--) {
        Equation eq = GEQs[e];
        if (eq.isNotZero(nVars)) {
          eq.copyCoef(nVars, i);
          eq.setTouched(true);
        }
      }
      for (int e = nEQs - 1; e >= 0; e--)
        EQs[e].copyCoef(nVars, i);
      for (int e = nSUBs - 1; e >= 0; e--)
        SUBs[e].copyCoef(nVars, i);
      var[i] = var[nVars];
    }
    if (i <= safeVars)
      safeVars--;
    nVars--;
  }

  private void setInternals() 
  {
    if (!variablesInitialized)
      initializeVariables();

    var[0] = 0;
    nextWildcard = 0;
    for (int i = 1; i <= nVars; i++)
      if (var[i] < 0) 
        var[i] = --nextWildcard;

    olAssert(nextWildcard >= -maxWildcards);

    checkForDuplicateVariableNames();

    int v = nSUBs;
    for (int i = 1; i <= safeVars; i++)
      if (var[i] > 0)
        v++;
    varsOfInterest = v;
    hashVersion = omegaLib.resetPrototypes(hashVersion, this);
  }

  private void setExternals() 
  {
    for (int i = 1; i <= safeVars; i++)
      forwardingAddress[var[i]] = i;
    for (int i = 0; i < nSUBs; i++)
      forwardingAddress[SUBs[i].getKey()] = -i - 1;
  }

  private void putVariablesInStandardOrder() 
  {
    for (int i = 1; i <= safeVars; i++) {
      int b = i;
      for (int j = i + 1; j <= safeVars; j++) {
        if (var[b] < var[j])
          b = j;
      }
      if (b != i)
        swapVars(i, b);
    }
  }

  private void nameWildcard(int i)
  {
    int j;
    do {
      --nextWildcard;
      if (nextWildcard < -maxWildcards)
        nextWildcard = -1;
      var[i] = nextWildcard;
      for (j = nVars; j > 0; j--)
        if ((i != j) && (var[j] == nextWildcard))
          break;
    } while (j != 0);
  }

  private int protectWildcard(int i) 
  {
    olAssert(i > safeVars);
    if (i != safeVars + 1)
      swapVars(i, safeVars + 1);
    safeVars++;
    nameWildcard(safeVars);
    return safeVars;
  }

  private int addNewProtectedWildcard()
  {
    int i = ++safeVars;
    nVars++;

    if (nVars != i) {
      for (int e = nGEQs - 1; e >= 0; e--) {
        Equation eq = GEQs[e];
        if (eq.isNotZero(i))
          eq.setTouched(true);
        eq.copyCoef(i, nVars);
      }

      for (int e = nEQs - 1; e >= 0; e--)
        EQs[e].copyCoef(i, nVars);

      for (int e = nSUBs - 1; e >= 0; e--)
        SUBs[e].copyCoef(i, nVars);

      var[nVars] = var[i];
    }

    zeroVariable(i);

    nameWildcard(i);

    return i;
  }

  public int addNewUnprotectedWildcard()
  {
    int i = ++nVars;
    zeroVariable(i);
    nameWildcard(i);
    return i;
  }

  private void cleanoutWildcards()
  {
    boolean renormalize = false;

    if (omegaLib.trace)
      System.out.println("Trying to cleanout wildcards");

    for (int e = nEQs - 1; e >= 0; e--) {
      Equation eqe = EQs[e];
      for (int i = nVars; i >= safeVars + 1; i--) {
        if (eqe.isZero(i))
          continue;

        int j = eqe.lastNZCoef(i - 1, safeVars + 1);

        if (j >= safeVars + 1) {
          /* a multi wild card equality*/
          if (omegaLib.inApproximateMode) {
            deleteEQ(e);
            nEQs--;
          }
          continue;
        }

        // Found a single wild card equality .

        if (omegaLib.trace) {
          System.out.print("Found a single wild card equality: ");
          printEQ(eqe);
          System.out.println("");
          printProblem();
        }

        int      c                      = eqe.getCoefficient(i);
        int      a                      = abs(c);
        Equation sub                    = eqe;
        boolean  preserveThisConstraint = true;

        for (int e2 = nEQs - 1; e2 >= 0; e2--) {
          Equation eqe2 = EQs[e2];

          if (e == e2)
            continue;

          if (eqe2.isZero(i))
            continue;

          if (eqe2.getColor() < eqe.getColor())
            continue;

          preserveThisConstraint = preserveThisConstraint && gcd(a, abs(eqe2.getCoefficient(i))) != 1;

          eqe2.multCoefs(nVars, a);

          int k = eqe2.getCoefficient(i);
          eqe2.multAndSub(sub, k / c, nVars);
          eqe2.setCoef(i, 0);
          int g = eqe2.gcdCoefs(nVars, 0);
          if (g != 0)
            eqe2.divideCoefs(nVars, g);
        }

        for (int e2 = nGEQs - 1; e2 >= 0; e2--) {
          Equation eqe2 = GEQs[e2];

          if (eqe2.isZero(i))
            continue;

          if (eqe2.getColor() < eqe.getColor())
            continue;

          eqe2.multCoefs(nVars, a);
          int k = eqe2.getCoefficient(i);
          eqe2.multAndSub(sub, k / c, nVars);
          eqe2.setCoef(i, 0);
          eqe2.setTouched(true);
          renormalize = true;
        }

        for (int e2 = nSUBs - 1; e2 >= 0; e2--) {
          Equation sube2 = SUBs[e2];
          if (sube2.isZero(i))
            continue;
          if (sube2.getColor() < eqe.getColor())
            continue;

          sube2.multCoefs(nVars, a);
          int k = sube2.getCoefficient(i);
          sube2.multAndSub(sub, k / c, nVars);
          sube2.setCoef(i, 0);
          int g = sube2.gcdCoefs(nVars, 0);
          if (g != 0)
            sube2.divideCoefs(nVars, g);
        }

        if (!preserveThisConstraint) {
          deleteEQ(e);
          nEQs--;
          deleteVariable(i);
        }

        if (omegaLib.trace) {
          System.out.print("cleaned-out wildcard: ");
          printProblem();
        }

        break;
      }
    }

    if (renormalize)
      normalize();
  }

  private void check()
  {
    int v = nSUBs;

    for (int i = 1; i <= safeVars; i++)
      if (var[i] > 0)
        v++;

    olAssert(v == varsOfInterest);

    for (int e = 0; e < nGEQs; e++)
      olAssert(GEQs[e].isTouched() || GEQs[e].getKey() != 0);

    if (mayBeRed == 0) {
      if (anyNotBlack())
        throw new polyglot.util.InternalCompilerError("check - some not BLACK");
      return;
    }

//      for (int i = safeVars + 1; i <= nVars; i++) {
//        boolean isBlack = false;
//        boolean isRed   = false;

//        for (int e = 0; e < nEQs; e++) {
//      Equation eq = EQs[e];
//      if (eq.isZero(i))
//        continue;

//      if (eq.isNotBlack())
//        isRed = true;
//      else
//        isBlack = true;
//        }

//        for (int e = 0; e < nGEQs; e++) {
//      Equation eq = GEQs[e];
//      if (eq.isZero(i))
//        continue;

//      if (eq.isNotBlack())
//        isRed = true;
//      else
//        isBlack = true;
//        }

//        if (isBlack && isRed && false) {
//      System.out.println("Mixed Red and Black variable:");
//      printProblem();
//        }
//      }
  }

  private void rememberRedConstraint(Equation e, int type, int stride) 
  {
    if ((type == RememberRedConstraint.redEQ) && (omegaLib.newVar == nVars) && (e.getCoefficient(omegaLib.newVar) != 0)) { // this is really a stride constraint
      type = RememberRedConstraint.redStride;
      stride = e.getCoefficient(omegaLib.newVar);
    } else
      if (e.anyNZCoef(nVars, safeVars + 1))
        throw new polyglot.util.InternalCompilerError("Non-zero coefficient in rememberRedConstraint");

    olAssert(type != RememberRedConstraint.notRed);
    olAssert(type == RememberRedConstraint.redStride || stride == 0);

    if (omegaLib.trace) {
      System.out.println("being asked to remember red constraint:");
      switch (type) {
      case RememberRedConstraint.notRed:    System.out.print("notRed: ");                     break;
      case RememberRedConstraint.redGEQ:    System.out.print("Red: 0 <= ");                   break;
      case RememberRedConstraint.redLEQ:    System.out.print("Red: 0 >= ");                   break;
      case RememberRedConstraint.redEQ:     System.out.print("Red: 0 == ");                   break;
      case RememberRedConstraint.redStride: System.out.print("Red stride " + stride + ": "); break;
      }
      printTerm(e, 1);
      System.out.println("");
      printProblem();
      System.out.println("----");
    }

    e = getNewEQ(e);

    if (type == RememberRedConstraint.redLEQ) {
      e.negateCoefs(safeVars, 0);
      type = RememberRedConstraint.redGEQ;
    }

    int[]   backSub = new int[safeVars + 2];
    boolean anyWild = false;

    for (int i = 1; i <= safeVars; i++)
      if (var[i] < 0)
        anyWild = true;
 
    if (anyWild) {
      for (int i = 1; i <= nVars; i++)
        backSub[i] = -1;

      for (int i = 0; i < nSUBs; i++) {
        int k = -1;
        for (int j = 1; j <= safeVars; j++) {
          if ((var[j] < 0) && (SUBs[i].getCoefficient(j) != 0)) {
            if (k == -1)
              k = j;
            else {
              k = -1;
              break;
            }
          }
        }
        if (k >= 0) {
          backSub[k] = i;
          if (omegaLib.trace) {
            System.out.print("Wildcard " + variable(k) + " handled via sub: ");
            printSubstitution(SUBs[i]);
            System.out.println("");
          }
        }
      }
      int l = 1;
      for (int i = 1; i <= safeVars; i++) {
        if ((var[i] < 0) && (e.isNotZero(i))) {
          if (backSub[i] < 0) {
            printProblem();
            throw new polyglot.util.InternalCompilerError("sophisticated back substitutions not handled ");
          }
          int j = backSub[i];
          int k = abs(SUBs[j].getCoefficient(i));
          k = k / gcd(k, abs(e.getCoefficient(i)));
          l = lcm(l,k);
        }
      }
      e.multCoefs(nVars, l);
      stride *= l;
      if (omegaLib.trace) {
        System.out.println("Expression scaled by " + l + ": ");
        printTerm(e,1);
        System.out.println("");
      }
    }

    int[] coef = new int[nVars + 2];

    coef[0] = e.getConstant();
    for (int i = 1; i <= safeVars; i++) {
      if (e.isZero(i))
        continue;

      if (omegaLib.trace)
        System.out.println("Handling " + variable(i) + ":");

      if (var[i] > 0)
        coef[var[i]] += e.getCoefficient(i);
      else {
        int m = e.getCoefficient(i);
        int s = SUBs[backSub[i]].getCoefficient(i);

        if (omegaLib.trace)
          System.out.println("m = " + m + ", s = " + s);

        olAssert(m % s == 0);

        m = m / s;

        Equation sub = SUBs[backSub[i]];

        for (int j = 1; j <= safeVars; j++) {
          if (var[j] <= 0)
            continue;

          int cs = sub.getCoefficient(j);
          if (cs == 0)
            continue;

          coef[var[j]] -= m * cs;

          if (omegaLib.trace) {
            System.out.println("Subtracting ");
            System.out.print(m);
            System.out.print(" * ");
            System.out.print(cs);
            System.out.print(" * ");
            System.out.println(variable(j));
          }
        }

        if (sub.getConstant() != 0) {
          coef[0] -= m * sub.getConstant();
          if (omegaLib.trace) {
            System.out.print("Subtracting ");
            System.out.print(m);
            System.out.print(" * ");
            System.out.println(sub.getConstant());
          }
        }
                
        coef[sub.getKey()] += m;

        if (omegaLib.trace) {
          System.out.println("Adding ");
          System.out.print(m);
          System.out.print(" * ");
          System.out.println(orgVariable(sub.getKey()));
        }
      }

      if (omegaLib.trace) {
        System.out.print("Coef = " + coef[0]);
        int k;
        for (k = nVars; k > 0; k--)
          if (coef[k] != 0)
            break;
        for (int j = 1; j <= k; j++)
          System.out.print(" " + coef[j]);
        System.out.println("");
        if (type == RememberRedConstraint.redStride)
          System.out.println("Stride = " + stride);
      }
    }

    int x = 0;
    if (type == RememberRedConstraint.redStride)
      x = stride;

    RememberRedConstraint redmem = new RememberRedConstraint(type, coef, x);
    redMemory.addElement(redmem);

    if (omegaLib.trace) {
      System.out.print("Red constraint remembered ");
      System.out.println(redmem.toString(this));
    }
  }

  private void recallRedMemories() 
  {
    int nMemories = redMemory.size();
    if (nMemories <= 0)
      return;

    if (omegaLib.trace) {
      System.out.print("Recalling red memories ");
      printProblem();
    }

    Equation e = null;

    for (int m = 0; m < nMemories; m++) {
      RememberRedConstraint redmem = (RememberRedConstraint) redMemory.elementAt(m);
      redmem.recall(this, forwardingAddress, var);
    }

    redMemory.clear();
    if (omegaLib.trace) {
      System.out.println("Red memories recalled");
      printProblem();
    }
  }

  public void swapVars(int i, int j) 
  {
    if (omegaLib.trace) {
      omegaLib.useUglyNames++;
      System.out.print("Swapping ");
      System.out.print(i);
      System.out.print(" and ");
      System.out.println(j);
      printProblem();
      omegaLib.useUglyNames--;
    }

    int tt = var[i];
    var[i] = var[j];
    var[j] = tt;

    for (int e = nGEQs - 1; e >= 0; e--) {
      Equation eq = GEQs[e];
      eq.swapVars(i, j);
      eq.setTouched(true);
    }

    for (int e = nEQs - 1; e >= 0; e--) {
      Equation eq = EQs[e];
      eq.swapVars(i, j);
    }

    for (int e = nSUBs - 1; e >= 0; e--) {
      Equation sub = SUBs[e];
      sub.swapVars(i, j);
    }

    if (omegaLib.trace) {
      omegaLib.useUglyNames++;
      System.out.println("Swapping complete ");
      printProblem();
      System.out.println("");
      omegaLib.useUglyNames--;
    }
  }

  private void addingEqualityConstraint(Equation eqe)
  {
    Problem originalProblem = omegaLib.getOriginalProblem();

    if (!omegaLib.addingOuterEqualities || 
        (originalProblem == null) ||
        (originalProblem == this) ||
        (omegaLib.conservative != 0))
      return;

    Equation eqe2 = originalProblem.getNewEQ();

    if (omegaLib.trace)
      System.out.println("adding equality constraint " + eqe2 + " to outer problem");

    for (int i = nVars; i >= 1; i--) {
      int j;
      for (j = originalProblem.nVars; j >= 1; j--)
        if (originalProblem.var[j] == var[i])
          break;

      if ((j <= 0) || ((outerColor != BLACK) && (j > originalProblem.safeVars)))        {
        if (omegaLib.trace)
          System.out.println("retracting");
        originalProblem.nEQs--;
        return;
      }
      eqe2.copyCoef(eqe, i, j);
    }

    eqe2.setConstant(eqe.getConstant());
        
    eqe2.setColor(outerColor);
    if (omegaLib.trace) {
      originalProblem.printProblem();
      printProblem();
    }
  }

  private int normalize()
  {
    boolean coupledSubscripts = false;
    int[]   fastLookup        = new int[OmegaLib.maxKeys * 2];

    check();

    int[] packing = new int[nVars + 1];

    for (int e = 0; e < nGEQs; e++) {
      Equation eqe = GEQs[e];
      if (!eqe.isTouched()) {
        if (!singleVarGEQ(eqe))
          coupledSubscripts = true;
      } else {

        if (omegaLib.trace) {
          System.out.print("Normalizing: ");
          printGEQ(eqe);
          System.out.println("");
        }

        int topVar = eqe.packNZIndexes(packing, nVars, 1) - 1;

        if (topVar == -1) {
          if (eqe.getConstant() < 0) {
            if (omegaLib.trace) {
              printGEQ(eqe);
              System.out.println("\nequations have no solution (D)");
            }
            return normalizeFalse;
          }

          deleteGEQ(e);
          e--;

          continue;
        }

        if (topVar == 0) {
          int singleVar = packing[0];
          int g = eqe.getCoefficient(singleVar);
          if (g > 0) {
            eqe.setCoef(singleVar, 1);
            eqe.setKey(singleVar);
          } else {
            g = -g;
            eqe.setCoef(singleVar, -1);
            eqe.setKey(-singleVar);
          }
          if (g > 1)
            eqe.setConstant(intDivide(eqe.getConstant(), g));
        } else {
          coupledSubscripts = true;
          int hashCode = eqe.computeHashcode(topVar, packing);
          if (omegaLib.trace) {
            System.out.print("Hash code = " + hashCode + ", Equation = ");
            printGEQ(eqe);
            System.out.println("");
          }
          omegaLib.usePrototype(hashCode, eqe, packing, topVar, nVars);
        }
      }

      eqe.setTouched(false);

      int eKey = eqe.getKey();
      if (e <= 0) {
        fastLookup[OmegaLib.maxKeys + eKey] = e;
        continue;
      }

      int e2 = fastLookup[OmegaLib.maxKeys - eKey];
      if (e2 < e) {
        Equation eq2 = GEQs[e2];
        if (eq2.getKey() == -eKey) {
          if (eq2.getConstant() < -eqe.getConstant()) {
            if (omegaLib.trace) {
              printGEQ(eqe);
              System.out.println("");
              printGEQ(eq2);
              System.out.println("\nequations have no solution (E)");
            }
            return normalizeFalse;
          }
          if (eq2.getConstant() == -eqe.getConstant()) {
            Equation neweq = getNewEQ(eqe);
            neweq.setColor(eqe, eq2);
            addingEqualityConstraint(neweq);
          }
        }
      }

      int e3 = fastLookup[OmegaLib.maxKeys + eKey];

      if (e3 >= e) {
        fastLookup[OmegaLib.maxKeys + eKey] = e;
        continue;
      }

      Equation eq3 = GEQs[e3];

      if (eq3.getKey() != eKey) {
        fastLookup[OmegaLib.maxKeys + eKey] = e;
        continue;
      }

      if ((eq3.getConstant() > eqe.getConstant()) || (eq3.getConstant() == eqe.getConstant()) && (eq3.isNotBlack())) {
        /* e3 is redundant */
        if (omegaLib.trace) {
          System.out.print("Removing Redundant Equation: ");
          printGEQ((eq3));
          System.out.println("");
          System.out.print("[a]      Made Redundant by: ");
          printGEQ((eqe));
          System.out.println("");
        }
        eq3.setConstant(eqe.getConstant());
        eq3.setColor(eqe);
        deleteGEQ(e);
        e--;
        continue;
      }

      /* e is redundant */

      if (omegaLib.trace) {
        System.out.print("Removing Redundant Equation: ");
        printGEQ((eqe));
        System.out.println("");
        System.out.print("[b]      Made Redundant by: ");
        printGEQ((eq3));
        System.out.println("");
      }

      deleteGEQ(e);
      e--;
    }

    return coupledSubscripts ? normalizeCoupled : normalizeUncoupled;
  }

  private int solve(int desiredResult)
  {
    int result;

    olAssert(nVars >= safeVars);

    if (desiredResult != SOLVE_SIMPLIFY)
      safeVars = 0;

    solveDepth++;
    if (solveDepth > 50) {
      System.out.println("Solve depth = ");
      System.out.print(solveDepth);
      System.out.print(", inApprox = ");
      System.out.print(omegaLib.inApproximateMode);
      printProblem();

      if (solveDepth > 60)
        throw new polyglot.util.InternalCompilerError("Solve depth > 60");
    }

    check();

    if (SOLVE_F == solveEQ()) {
      solveDepth--;
      return SOLVE_F;
    }

    check();

    if (0 == nGEQs) {
      result = SOLVE_T;
      nVars = safeVars;
    } else {
      result = solveGEQ(desiredResult);
    }

    check();

    solveDepth--;

    return result;
  }

  private boolean smoothWeirdEquations()
  {
    boolean result = false;

    for (int e1 = nGEQs - 1; e1 >= 0; e1--) {
      Equation eqe1 = GEQs[e1];
      if (eqe1.isNotBlack())
        continue;

      int g = eqe1.findSmallestNZ(nVars, 1);
      if (g <= 20)
        continue;

      Equation eqe3 = getNewGEQ(); // Create a scratch GEQ, not part of the problem.
      nGEQs--;

      eqe3.weirdCopyCoefs(eqe1, nVars, 1, g);
      eqe3.setConstant(9997);
      eqe3.turnBlack();
      eqe3.setTouched(true);

      if (omegaLib.trace) {
        System.out.println("Checking to see if we can derive: ");
        printGEQ(eqe3);
        System.out.println("\n from: ");
        printGEQ(eqe1);
        System.out.println("");
      }

      int e2;
    nextE2:
      for (e2 = nGEQs - 1; e2 >= 0; e2--) {
        if (e1 == e2)
        continue;

        Equation eqe2 = GEQs[e2];
        if (eqe2.isNotBlack())
          continue;

        boolean found = false;
        int     p     = -1;
        int     q     = -1;
        int     alpha = -1;
        int     cp1   = 0;
        int     cp2   = 0;
        int     cq1   = 0;
        int     cq2   = 0;
      foundPQ:
        for (p = nVars; p > 1; p--) {
          cp1 = eqe1.getCoefficient(p);
          cp2 = eqe2.getCoefficient(p);

          for (q = p - 1; q > 0; q--) {
            cq1 = eqe1.getCoefficient(q);
            cq2 = eqe2.getCoefficient(q);

            alpha = (cp1 * cq2 - cp2 * cq1);

            if (alpha == 0)
              continue;

            found = true;
            break foundPQ;
          }
        }

        if (!found)
          continue;

        int cp3    = eqe3.getCoefficient(p);
        int cq3    = eqe3.getCoefficient(q);
        int alpha1 = cq2 * cp3 - cp2 * cq3;
        int alpha2 = -(cq1 * cp3 - cp1 * cq3);
        int alpha3 = alpha;

        if ((alpha1 * alpha2) <= 0)
          continue;

        if (alpha1 < 0) {
          alpha1 = -alpha1;
          alpha2 = -alpha2;
          alpha3 = -alpha3;
        }

        if (alpha3 > 0) { /* Trying to prove e3 is redundant */
          /* verify alpha1*v1+alpha2*v2 = alpha3*v3 */
          for (int k = nVars; k >= 1; k--) {
            int ck1 = eqe1.getCoefficient(k);
            int ck2 = eqe2.getCoefficient(k);
            int ck3 = eqe3.getCoefficient(k);
            if (alpha3 * ck3 != (alpha1 * ck1 + alpha2 * ck2))
              continue nextE2;
          }

          int c = alpha1 * eqe1.getConstant() + alpha2 * eqe2.getConstant();
          if (c < alpha3 * (eqe3.getConstant() + 1))
            eqe3.setConstant(intDivide(c, alpha3));
        }
      }

      if (eqe3.getConstant() < 9997) {
        result = true;
        if (omegaLib.trace) {
          System.out.println("Smoothing wierd equations; adding:");
          printGEQ(eqe3);
          System.out.println("\nto:");
          printProblem();
          System.out.println("\n");
        }
      }
    }

    return result;
  }

  private void analyzeElimination(int v)
  {
    parallelSplinters  = Integer.MAX_VALUE;
    disjointSplinters  = 0;
    lbSplinters        = 0;
    ubSplinters        = 0;
    unit               = 0;
    darkConstraints    = 0;
    darkShadowFeasible = true;

    int     maxUBc = 0;
    int     maxLBc = 0;
    boolean exact  = true;

    for (int e = nGEQs - 1; e >= 0; e--) {
      Equation eqe = GEQs[e];
      int      c   = eqe.getCoefficient(v);

      if (c < 0) {
        maxUBc = max(maxUBc, -c);

        int Uc = -c;
        for (int e2 = nGEQs - 1; e2 >= 0; e2--) {
          Equation eqe2 = GEQs[e2];
          int      Lc   = eqe2.getCoefficient(v);

          if (Lc <= 0)
            continue;

          int g    = eqe.gcdSumOfProd(eqe2, Lc, Uc, nVars, 1);
          int grey = (Lc - 1) * (Uc - 1);
          int diff = Lc * eqe.getConstant() + Uc * eqe2.getConstant();

          if (g == 0) {
            if (diff < 0) {
              // Real shadow must be true 
              // otherwise we would have found it during
              // check for opposing constraints
              if (OmegaLib.classTrace) {
                System.out.println("Found conflicting constraints ");
                printGEQ(eqe);
                System.out.println(" and ");
                printGEQ(eqe2);
                System.out.println("\nin");
                printProblem();
              }
              throw new polyglot.util.InternalCompilerError("Found conflicting constraints ");
            }
            if (diff < grey) {
              darkShadowFeasible = false;
              if (parallelSplinters > diff + 1) {
                parallelSplinters = diff + 1;
                parallelLB = e2;
              }
            } else  {/* dark shadow is true, don't need to worry about this constraint pair */
            }
          } else {
            int splinters = intDivide(diff, g) - intDivide(diff - grey, g);
            if (splinters != 0)
              exact = false;
            disjointSplinters += splinters;
            if (g > 1)
              unit++;
            darkConstraints++;
          }
        }
      } else if (c > 0) {
        maxLBc = max(maxLBc, c);
      }
    }

    if (darkShadowFeasible) {
      disjointSplinters++;
      ubSplinters++;
      lbSplinters++;
    } else
      disjointSplinters = Integer.MAX_VALUE;


    if (!darkShadowFeasible || !exact) 
      for (int e = nGEQs - 1; e >= 0; e--) {
        int c = GEQs[e].getCoefficient(v);
        if (c < -1) {
          c = -c;
          ubSplinters += 1 +(c * maxLBc - c - maxLBc) / maxLBc;
        } else if (c > 1) {
          lbSplinters += 1 + (c * maxUBc - c - maxUBc) / maxUBc;
        }
      }

    if (omegaLib.trace) {
      System.out.print("analyzing elimination of ");
      System.out.print(variable(v));
      System.out.print("(");
      System.out.print(v);
      System.out.println(")");
      if (darkShadowFeasible)
        System.out.println("  # dark constraints = " +  darkConstraints);
      else 
        System.out.println("  dark shadow obviously unfeasible");

      System.out.print(" ");
      System.out.print(lbSplinters);
      System.out.println(" LB splinters");
      System.out.print(" ");
      System.out.print(ubSplinters);
      System.out.println(" UB splinters");
      if (disjointSplinters != Integer.MAX_VALUE) {
        System.out.print(" ");
        System.out.print(disjointSplinters);
        System.out.println(" disjoint splinters");
      }
      if (parallelSplinters != Integer.MAX_VALUE) {
        System.out.print(" ");
        System.out.print(parallelSplinters);
        System.out.println(" parallel splinters");
      }
      System.out.println("");
      System.out.print(" ");
      System.out.print(unit);
      System.out.println(" unit score ");
    }
  }

  private boolean isGoodEquation(Equation eqe1, int i, int fv)
  {
    for (int e2 = nGEQs - 1; e2 >= 0; e2--) {
      Equation eqe2 = GEQs[e2];
      int      ci2  = eqe2.getCoefficient(i);
      int      ci1  = eqe1.getCoefficient(i);

      if ((ci2 * ci1) >= 0)
        continue;

      if (eqe1.getKey() == -eqe2.getKey())
        continue;

      if (!eqe1.isGoodEquation(eqe2, ci2, nVars, fv + 1))
        return false;
    }
    return true;
  }

  private void partialElimination()
  {
    if (omegaLib.trace) {
      System.out.println("Performing Partial elimination");
      printProblem();
    }

    boolean somethingHappened = false;
    int[]   deadEqns          = new int[nGEQs];

    for (int i = nVars; i > safeVars; i--) {
      int numDead = 0;

      for (int e1 = nGEQs - 1; e1 >= 0; e1--) {
        Equation eqe1 = GEQs[e1];

        if (abs(eqe1.getCoefficient(i)) != 1)
          continue;

        if (!isGoodEquation(eqe1, i, safeVars))
          continue;

        somethingHappened = true;
        for (int e2 = nGEQs - 1; e2 >= 0; e2--) {
          Equation eqe2 = GEQs[e2];
          int      ci2  = eqe2.getCoefficient(i);
          int      ci1  = eqe1.getCoefficient(i);
          if ((ci2  * ci1) >= 0)
            continue;

          if (eqe1.getKey() != -eqe2.getKey()) {
            int Uc = abs(ci2);
            Equation eqnew;
            if (numDead == 0) {
              eqnew = getNewGEQ();
            } else {
              eqnew = GEQs[deadEqns[--numDead]];
            }

            if (omegaLib.trace) {
              System.out.println("Eliminating constraint on " + variable(i));
              System.out.println("e1 = ");
              System.out.print(e1);
              System.out.print(", e2 = ");
              System.out.print(e2);
              System.out.print(", gen = " + eqnew);
              printGEQ((eqe1));
              System.out.println("");
              printGEQ((eqe2));
              System.out.println("");
            }

            eqnew.sumOfMult(eqe2, 1, eqe1, Uc, nVars);
            eqnew.setTouched(true);
            eqnew.setColor(eqe2, eqe1);
            if (omegaLib.trace) {
              System.out.println("give ");
              printGEQ((eqnew));
              System.out.println("");
            }
            olAssert(eqnew.isZero(i));
          }
        }

        deadEqns[numDead++] = e1;
        if (omegaLib.trace)
          System.out.println("Killed " +  e1);
      }

      for (int j = 0; j < numDead; j++)
        deleteGEQ(deadEqns[j]);
    }

    if (somethingHappened && omegaLib.trace) {
      System.out.println("Result of Partial elimination");
      printProblem();
    }
  }

  private String dispVarCT(boolean isBlack, int v, int constantTerm, String mid)
  {
    StringBuffer buf = new StringBuffer(" ::=> ");
    if (!isBlack)
      buf.append("[");
    buf.append(variable(v));
    buf.append(mid);
    buf.append(constantTerm);
    if (!isBlack)
      buf.append("]");
    return buf.toString();
  }

  private int solveGEQ(int desiredResult)
  {
    boolean triedEliminatingRedundant = false;

    if (desiredResult != SOLVE_SIMPLIFY) {
      nSUBs = 0;
      redMemory.clear();
      safeVars = 0;
      varsOfInterest = 0;
    }

  solveGEQstart:
    while (true) {
      olAssert((desiredResult == SOLVE_SIMPLIFY) || (nSUBs == 0));
      if (omegaLib.trace) {
        System.out.println("\nSolveGEQ(" + solveDisp[desiredResult] + (omegaLib.pleaseNoEqualitiesInSimplifiedProblems ? ", No EQs" : "") + "):");
        printProblem();
        System.out.println("");
      }
 
      for (int e = 0; e < nSUBs; e++)
        if (SUBs[e].anyNZCoef(nVars, safeVars + 1))
          throw new polyglot.util.InternalCompilerError("non-zero wildcard coefficient.");

      check();

      if (nVars == 1) {
        int uColor     = BLACK;
        int lColor     = BLACK;
        int upperBound = OmegaLib.posInfinity;
        int lowerBound = OmegaLib.negInfinity;

        for (int e = nGEQs - 1; e >= 0; e--) {
          Equation eqe = GEQs[e];
          int      a   = eqe.getCoefficient(1);
          int      c   = eqe.getConstant();

          /* our equation is ax + c >= 0, or ax >= -c, or c >= -ax */

          if (a == 0) {
            if (c < 0) {
              if (omegaLib.trace)
                System.out.println("equations have no solution (G)");
              return SOLVE_F;
            }
          } else if (a > 0) {
            if (a != 1)
              c = intDivide(c, a);
            if ((lowerBound < -c) || (lowerBound == -c && ((desiredResult != SOLVE_SIMPLIFY) || eqe.isBlack()))) {
              lowerBound = -c;
              lColor = eqe.getColor();
            }
          } else {
            if (a != -1)
              c = intDivide(c, -a);
            if ((upperBound > c) || (upperBound == c && ((desiredResult != SOLVE_SIMPLIFY) || eqe.isBlack()))) {
              upperBound = c;
              uColor = eqe.getColor();
            }
          }
        }

        if (omegaLib.trace) {
          System.out.println("upper bound = " + upperBound);
          System.out.println("lower bound = " + lowerBound);
        }

        if (lowerBound > upperBound) {
          if (omegaLib.trace)
            System.out.println("equations have no solution (H)");
          return SOLVE_F;
        }

        if (desiredResult == SOLVE_SIMPLIFY) {
          nGEQs = 0;
          if (safeVars == 1) {
            if ((lowerBound == upperBound) && (uColor == BLACK) && (lColor == BLACK)) {
              Equation eq = getNewEQ();
              eq.setConstant(-lowerBound);
              eq.setCoef(1, 1);
              eq.setColor(lColor | uColor);
              return solve(desiredResult);
            } else {
              if (lowerBound > OmegaLib.negInfinity) {
                Equation eq = getNewGEQ();
                eq.setConstant(-lowerBound);
                eq.setCoef(1, 1);
                eq.setKey(1);
                eq.setColor(lColor);
                eq.setTouched(false);
              }
              if (upperBound < OmegaLib.posInfinity) {
                Equation eq = getNewGEQ();
                eq.setConstant(upperBound);
                eq.setCoef(1, -1);
                eq.setKey(-1);
                eq.setColor(uColor);
                eq.setTouched(false);
              }
            }
          } else
            nVars = 0;
          return SOLVE_T;
        }

        if ((omegaLib.getOriginalProblem() != null) &&
            (lColor == BLACK) &&
            (uColor == BLACK) &&
            (0 == omegaLib.conservative) &&
            (lowerBound == upperBound)) {
          Equation eq = getNewEQ();
          eq.setConstant(-lowerBound);
          eq.setCoef(1, 1);
          eq.turnBlack();
          addingEqualityConstraint(eq);
        }
        return SOLVE_T;
      }

      if (!variablesFreed) {
        variablesFreed = true;
        if (desiredResult != SOLVE_SIMPLIFY)
          freeEliminations(0);
        else
          freeEliminations(safeVars);
        if (nVars == 1)
          continue;
      }

      boolean coupledSubscripts;
      switch (normalize()) {
      case normalizeFalse:     return SOLVE_F;
      case normalizeCoupled:   coupledSubscripts = true;  break;
      case normalizeUncoupled: coupledSubscripts = false; break;
      default: throw new polyglot.util.InternalCompilerError("impossible case in SolveGEQ");
      }

      if (omegaLib.trace) {
        System.out.println("\nafter normalization:");
        printProblem();
        System.out.println("");
        for (int e = 0; e < nGEQs; e++)
          olAssert(!GEQs[e].isTouched());
        System.out.println("eliminating variable using fourier-motzkin elimination");
      }

      boolean eliminateAgain = true;
      while (eliminateAgain) {
        eliminateAgain = false;

        if (nEQs > 0)
          return solve(desiredResult);

        if (!coupledSubscripts) {
          if (safeVars == 0)
            nGEQs = 0;
          else
            for (int e = nGEQs - 1; e >= 0; e--)
              if ((GEQs[e].getKey() > safeVars) || (-safeVars > GEQs[e].getKey()))
                deleteGEQ(e);

          nVars = safeVars;
          return SOLVE_T;
        }

        int fv;
        if (desiredResult != SOLVE_SIMPLIFY)
          fv = 0;
        else
          fv = safeVars;

        if (nVars == 0 || nGEQs == 0) {
          nGEQs = 0;
          if (desiredResult == SOLVE_SIMPLIFY) 
            nVars = safeVars;
          return SOLVE_T;
        }

        if ((desiredResult == SOLVE_SIMPLIFY) && (nVars == safeVars)) {
          return SOLVE_T;
        }

        if ((nGEQs + 6) > maxGEQs || nGEQs > 2 * nVars * nVars + 4 * nVars + 10) {
          if (omegaLib.trace) {
            System.out.print("TOO MANY EQUATIONS; ");
            System.out.print(nGEQs);
            System.out.print(" equations, ");
            System.out.print(nVars);
            System.out.println(" variables, ELIMINATING REDUNDANT ONES");
          }
          if (!quickKill(false, true))
            return SOLVE_F;

          if (nEQs > 0)
            return solve(desiredResult);

          if (omegaLib.trace) {
            System.out.println("END ELIMINATION OF REDUNDANT EQUATIONS");
            printProblem();
          }
        }

        int bestScore = Integer.MAX_VALUE;
        int bestVar = -1;  // make compiler shut up

        if (desiredResult != SOLVE_SIMPLIFY)
          fv = 0;
        else
          fv = safeVars;

        if (omegaLib.trace) {   
          System.out.println("Considering elimination possibilities[ ");
          printProblem();
        }

        for (int i = nVars; i != fv; i--) {
          analyzeElimination(i);

          int score = min(min(parallelSplinters, disjointSplinters), min(lbSplinters, ubSplinters));
          boolean exact = score == 1;
          score = 10000 * (score - 1) + darkConstraints;
          score -= 3 * unit;

          if (score < bestScore) {
            bestScore = score;
            bestVar = i;
            if ((i > 4) && (score < nGEQs))
              break;
          }
        }

        olAssert(bestVar >= 0);

        boolean exact = bestScore < 10000;
        int     i     = bestVar;

        olAssert(i <= nVars);

        analyzeElimination(i);

        if (omegaLib.trace)
          System.out.println("] Choose to eliminate " + variable(i));

        int splinters = lbSplinters;
        if (splinters <= parallelSplinters) 
          parallelSplinters = Integer.MAX_VALUE;
        else
          splinters = parallelSplinters;
        if (disjointSplinters == 1)
          splinters = 1;

        exact = splinters == 1;
        if (omegaLib.inApproximateMode)
          exact = true;
                        
        if (!triedEliminatingRedundant && (darkConstraints > maxGEQs)) {
          if (omegaLib.trace) {
            System.out.print("Elimination will create too many equations; ");
            System.out.print(nGEQs);
            System.out.print(" equations, ");
            System.out.print(nVars);
            System.out.print(" variables, ");
            System.out.print(darkConstraints);
            System.out.println(" new constraints, eliminating redundant ones");
          }

          if (!quickKill(false, false))
            return SOLVE_F;

          if (nEQs > 0)
            return solve(desiredResult);

          if (omegaLib.trace) {
            System.out.println("END ELIMINATION OF REDUNDANT EQUATIONS");
            printProblem();
          }

          triedEliminatingRedundant = true;
          eliminateAgain = true;
          continue;
        }

        if (!exact && !triedEliminatingRedundant && (safeVars > 0 && desiredResult == SOLVE_SIMPLIFY)) {
          if (omegaLib.trace)
            System.out.println("Trying to produce exact elimination by finding redundant constraints [");

          if (!quickKill(true, false))
            return SOLVE_F;

          if (omegaLib.trace)
            System.out.println("]");

          triedEliminatingRedundant = true;
          eliminateAgain = true;
          continue;
        }

        triedEliminatingRedundant = false;

        if ((desiredResult == SOLVE_SIMPLIFY) && !exact) {
          partialElimination();
          switch (normalize()) {
          case normalizeFalse:
            return SOLVE_F;
          case normalizeCoupled:
          case normalizeUncoupled:
            break;
          }

          if (nEQs > 0)
            return solveEQ();
          if (omegaLib.trace)
            System.out.println("Stopping short due to non-exact elimination");
          return SOLVE_T;
        }

        if ((desiredResult == SOLVE_SIMPLIFY) && (darkConstraints > maxGEQs)) {
          if (omegaLib.trace)
            System.out.println("Stopping short due to overflow of GEQs: " + darkConstraints);
          return SOLVE_F;
        }

        if (omegaLib.trace) {
          System.out.println("going to eliminate " + variable(i) + ", (" + i + ")");
          printProblem();
          System.out.println("score = " + bestScore + "/" + splinters);
        }

        if (!exact && (desiredResult == SOLVE_SIMPLIFY) && (parallelSplinters == splinters))
          return parallelSplinter(parallelLB, parallelSplinters, desiredResult);

        boolean smoothed = false;

        if (i != nVars) {
          int j = nVars;
          swapVars(i, j);
          i = j;
        } else if (omegaLib.trace) {
          printVars(true);
          System.out.print("No swap needed before eliminating ");
          System.out.print(variable(i));
          System.out.print("(");
          System.out.print(i);
          System.out.print("/");
          System.out.print(nVars);
          System.out.println(")");
          for (int j = 1; j <= i; j++) {
            System.out.print("var #");
            System.out.print(j);
            System.out.print(" = ");
            System.out.print(variable(j));
            System.out.print("(");
            System.out.print(Integer.toHexString(var[j]));
            System.out.println(")");
          }
          printProblem();
        }

        nVars--;

        if (exact) {
          if (nVars == 1) {
            int upperBound = OmegaLib.posInfinity;
            int lowerBound = OmegaLib.negInfinity;
            int ub_color   = 0;
            int lb_color   = 0;
            int topEqn     = nGEQs - 1;
            for (int Le = topEqn; Le >= 0; Le--) {
              Equation eql = GEQs[Le];
              int      Lc = eql.getCoefficient(i);
              if (Lc == 0) {
                if (eql.getCoefficient(1) == 1) {
                  int constantTerm = -eql.getConstant();
                  if ((constantTerm > lowerBound) || ((constantTerm == lowerBound) && ((desiredResult != SOLVE_SIMPLIFY) || eql.isBlack()))) {
                    lowerBound = constantTerm;
                    lb_color = eql.getColor();
                  }
                  if (omegaLib.trace)
                    System.out.println(dispVarCT(eql.isBlack(), 1, constantTerm, " >= "));
                } else {
                  int constantTerm = eql.getConstant();
                  if ((constantTerm < upperBound) || ((constantTerm == upperBound) && ((desiredResult != SOLVE_SIMPLIFY) || eql.isBlack()))) {
                    upperBound = constantTerm;
                    ub_color = eql.getColor();
                  }
                  if (omegaLib.trace) {
                    System.out.print(" ::=> ");
                    if (eql.isNotBlack())
                      System.out.print("[");
                    System.out.print(variable(1));
                    System.out.print(" <= ");
                    System.out.print(constantTerm);
                    if (eql.isNotBlack())
                      System.out.print("]");
                    System.out.println("");
                  }
                }
              } else if (Lc > 0) {
                for (int Ue = topEqn; Ue >= 0; Ue--) {
                  Equation equ = GEQs[Ue];
                  int      ci  = equ.getCoefficient(i);
                  if (ci >= 0)
                    continue;

                  if (eql.getKey() == -equ.getKey())
                    continue;

                  int Uc = -ci;
                  int coefficient = equ.getCoefficient(1) * Lc + eql.getCoefficient(1) * Uc;
                  int constantTerm = equ.getConstant() * Lc + eql.getConstant() * Uc;

                  if (omegaLib.trace) {
                    printGEQextra(equ);
                    System.out.println("");
                    printGEQextra(eql);
                    System.out.println("");
                  }

                  if (coefficient > 0) {
                    constantTerm = -(intDivide(constantTerm, coefficient));
                    if ((constantTerm > lowerBound) ||
                        ((constantTerm == lowerBound) &&
                         ((desiredResult != SOLVE_SIMPLIFY) ||
                          ((equ.isBlack()) && (eql.isBlack()))))) {
                      lowerBound = constantTerm;
                      lb_color = equ.getColor() | eql.getColor();
                    }
                    if (omegaLib.trace)
                      System.out.println(dispVarCT((equ.isBlack()) && (eql.isBlack()), 1, constantTerm, " >= "));
                  } else if (coefficient < 0) {
                    constantTerm = (intDivide(constantTerm, -coefficient));
                    if ((constantTerm < upperBound) || ((constantTerm == upperBound) && equ.isBlack() && eql.isBlack())) {
                      upperBound = constantTerm;
                      ub_color = equ.getColor() | eql.getColor();
                    }
                    if (omegaLib.trace)
                      System.out.println(dispVarCT((equ.isBlack()) && (eql.isBlack()), 1, constantTerm, " <= "));
                  }
                }
              }
            }

            nGEQs = 0;
            if (omegaLib.trace) {
              System.out.print(" therefore, ");
              System.out.print((lb_color != BLACK) ? '[' : ' ');
              System.out.print(lowerBound);
              System.out.print(" <= ");
              System.out.print(((lb_color != BLACK) && (ub_color == BLACK)) ? ']' : ' ');
              System.out.print(variable(1));
              System.out.print(((lb_color == BLACK) && (ub_color != BLACK)) ? '[' : ' ');
              System.out.print(" <= ");
              System.out.print(upperBound);
              System.out.println((ub_color != BLACK) ? ']' : ' ');
            }

            if (lowerBound > upperBound)
              return SOLVE_F;
                    
            if (upperBound == lowerBound) {
              Equation eqg = getNewEQ();
              eqg.setCoef(1, -1);
              eqg.setConstant(upperBound);
              eqg.setColor(ub_color | lb_color);
              addingEqualityConstraint(eqg);
            } else if (safeVars == 1) {
              if (upperBound != OmegaLib.posInfinity) {
                Equation eqg = getNewGEQ();
                eqg.setCoef(1, -1);
                eqg.setConstant(upperBound);
                eqg.set(ub_color, -1, false);
              }
              if (lowerBound != OmegaLib.negInfinity) {
                Equation eqg = getNewGEQ();
                eqg.setCoef(1, 1);
                eqg.setConstant(-lowerBound);
                eqg.set(lb_color, 1, false);
              }
            }
            if (safeVars == 0) 
              nVars = 0;
            return SOLVE_T;
          }

          eliminateAgain = true;

          int[] deadEqns        = new int[nGEQs + 1];
          int   numDead         = 0;
          int   topEqn          = nGEQs - 1;
          int   lowerBoundCount = 0;

          for (int Le = topEqn; Le >= 0; Le--)
            if (GEQs[Le].getCoefficient(i) > 0)
              lowerBoundCount++;

          if (omegaLib.trace)
            System.out.println("lower bound count = " + lowerBoundCount);

          if (lowerBoundCount == 0) {
            if (desiredResult != SOLVE_SIMPLIFY)
              fv = 0;
            else
              fv = safeVars;
            nVars++;
            freeEliminations(fv);
            continue;
          }

          for (int Le = topEqn; Le >= 0; Le--) {
            Equation eql = GEQs[Le];
            int      Lc  = eql.getCoefficient(i);

            if (Lc <= 0)
              continue;

            for (int Ue = topEqn; Ue >= 0; Ue--) {
              Equation equ = GEQs[Ue];
              int      ci  = equ.getCoefficient(i);

              if (ci >= 0)
                continue;

              if (eql.getKey() != -equ.getKey()) {
                int      Uc = -ci;
                Equation eq2;

                if (numDead == 0) {
                                /* (Big kludge warning) */
                                /* this code is still using location nVars+1 */
                                /* but newGEQ, if it reallocates, only copies*/
                                /* locations up to nVars.  This fixes that.  */
                  nVars++;
                  eq2 = getNewGEQ();
                  nVars--;
                } else {
                  eq2 = GEQs[deadEqns[--numDead]];
                }

                if (omegaLib.trace) {
                  System.out.print("Le = ");
                  System.out.print(Le);
                  System.out.print(", Ue = ");
                  System.out.print(Ue);
                  System.out.print(", gen = ");
                  System.out.println(eq2);
                  printGEQextra(eql);
                  System.out.println("");
                  printGEQextra(equ);
                  System.out.println("");
                }

                eliminateAgain = false;

                int g = gcd(Lc, Uc);

                eq2.sumOfMult(equ, Lc / g, eql, Uc / g, nVars);
                eq2.setCoef(nVars + 1,  0);
                eq2.setTouched(true);
                eq2.setColor(equ, eql);

                if (omegaLib.trace) {
                  printGEQ((eq2));
                  System.out.println("");
                }
              }

              if (lowerBoundCount == 1) {
                deadEqns[numDead++] = Ue;
                if (omegaLib.trace)
                  System.out.println("Killed " +  Ue);
              }
            }

            lowerBoundCount--;
            deadEqns[numDead++] = Le;
            if (omegaLib.trace)
              System.out.println("Killed " +  Le);
          }

          while (numDead > 0) {
            nVars++;
            deleteGEQ(deadEqns[--numDead]);
            nVars--;
          }

          continue;

        }

        // if not exact

        Problem rS = new Problem(omegaLib, 0, 0);
        Problem iS = new Problem(omegaLib, 0, 0);

        iS.nVars = nVars;
        rS.nVars = nVars; // do this immed.; in case of reallocation, we
        // need to know how much to copy
        rS.varNameSource = varNameSource;
        iS.varNameSource = varNameSource;

        for (int e = 0; e < nGEQs; e++) {
          Equation eq = GEQs[e];

          if (eq.isNotZero(i))
            continue;

          rS.getNewGEQ(eq);
          iS.getNewGEQ(eq);

          if (omegaLib.trace) {
            int t;
            System.out.println("Copying (");
            System.out.print(i);
            System.out.print(", ");
            System.out.print(eq.getCoefficient(i));
            System.out.print("): ");
            printGEQextra(eq);
            System.out.println("");
            System.out.println(eq.coefsToString(nVars, 0));
          }
        }

        for (int Le = nGEQs - 1; Le >= 0; Le--) {
          Equation eql = GEQs[Le];
          if (eql.getCoefficient(i) <= 0)
            continue;

          int Lc = eql.getCoefficient(i);
          for (int Ue = nGEQs - 1; Ue >= 0; Ue--) {
            Equation equ = GEQs[Ue];
            int      ciu = equ.getCoefficient(i);

            if (ciu >= 0)
              continue;

            if (eql.getKey() == -equ.getKey())
              continue;

            int      Uc        = -ciu;
            int      g         = gcd(Lc, Uc);
            int      Lc_over_g = Lc / g;
            int      Uc_over_g = Uc / g;
            Equation re2       = rS.getNewGEQ();
            Equation ie2       = iS.getNewGEQ();

            re2.setTouched(true);
            ie2.setTouched(true);

            if (omegaLib.trace) {
              System.out.println("---");
              System.out.println("Le(Lc) = ");
              System.out.print(Le);
              System.out.print("(");
              System.out.print(Lc);
              System.out.print("), Ue(Uc) = ");
              System.out.print(Ue);
              System.out.print("(");
              System.out.print(Uc);
              System.out.print("), gen = ");
              System.out.print(ie2);
              printGEQextra(eql);
              System.out.println("");
              printGEQextra(equ);
              System.out.println("");
            }

            if (Uc == Lc) {
              ie2.sumOfCoefs(equ, eql, nVars);
              re2.copyCoefs(ie2, nVars + 1);
              ie2.addToCoef(0, - (Uc - 1));
            } else {
              ie2.sumOfMult(equ, Lc_over_g, eql, Uc_over_g, nVars);
              re2.copyCoefs(ie2, nVars + 1);
              ie2.addToCoef(0, - (Uc_over_g - 1) * (Lc_over_g - 1));
            }

            re2.setColor(equ, eql);
            ie2.setColor(re2);

            if (omegaLib.trace) {
              printGEQ(re2);
              System.out.println("");
            }
          }
        }

        iS.variablesInitialized = true;
        rS.variablesInitialized = true;
        iS.nEQs = 0;
        rS.nEQs = 0;

        olAssert(desiredResult != SOLVE_SIMPLIFY);
        olAssert(nSUBs == 0);

        iS.nSUBs =nSUBs;
        rS.nSUBs = nSUBs;
        iS.safeVars = safeVars;
        rS.safeVars = safeVars;

        for (int t = nVars; t >= 0; t--)
          rS.var[t] = var[t];
        for (int t = nVars; t >= 0; t--)
          iS.var[t] = var[t];

        nVars++;
        if (0 == desiredResult) {
          boolean t = omegaLib.trace;
          if (omegaLib.trace)
            System.out.println("\nreal solution(" + omegaLib.solveGEQDepth + "):");
          omegaLib.solveGEQDepth++;
          omegaLib.trace = false;
          int result = SOLVE_F;
          if (omegaLib.getOriginalProblem() == null) {
            omegaLib.setOriginalProblem(this);
            result = rS.solveGEQ(SOLVE_F);
            omegaLib.setOriginalProblem(null);
          } else
            result = rS.solveGEQ(SOLVE_F);
          omegaLib.trace = t;
          omegaLib.solveGEQDepth--;

          if (SOLVE_F == result) {
            return SOLVE_F;
          }

          if (nEQs > 0) { /* An equality constraint must have been found */
            return solve(desiredResult);
          }
        }

        if (desiredResult != 0) {
          if (darkShadowFeasible) {
            if (omegaLib.trace)
              System.out.println("\ninteger solution(" + omegaLib.solveGEQDepth + "):");
            omegaLib.solveGEQDepth++;
            omegaLib.conservative++;
            int result = iS.solveGEQ(desiredResult);
            omegaLib.conservative--;
            omegaLib.solveGEQDepth--;
            if (result != SOLVE_F) {
              return SOLVE_T;
            }
          }

          if (omegaLib.trace)
            System.out.println("have to do exact analysis");

          int   worstLowerBoundConstant = 1;
          int   lowerBounds             = 0;
          int[] lowerBound              = new int[nGEQs];

          omegaLib.conservative++;

          for (int e = 0; e < nGEQs; e++) {
            Equation eq = GEQs[e];
            int      ci = eq.getCoefficient(i);
            if (ci < -1) {
              worstLowerBoundConstant = max(worstLowerBoundConstant, -ci);
            } else if (ci > 1)
              lowerBound[lowerBounds++] = e;
          }

          /* sort array */
          for (int j = 0; j < lowerBounds; j++) {
            int smallest = j;
            for (int k = j + 1; k < lowerBounds; k++)
              if (GEQs[lowerBound[smallest]].getCoefficient(i) > GEQs[lowerBound[k]].getCoefficient(i))
                smallest = k;
            int t = lowerBound[smallest];
            lowerBound[smallest] = lowerBound[j];
            lowerBound[j] = t;
          }

          if (omegaLib.trace) {
            System.out.println("lower bound coeeficients = ");
            for (int j = 0; j < lowerBounds; j++)
              System.out.println(" " +  GEQs[lowerBound[j]].getCoefficient(i));
            System.out.println("");
          }

          for (int j = 0; j < lowerBounds; j++) {
            int      e       = lowerBound[j];
            Equation eqe     = GEQs[e];
            int      maxIncr = ((eqe.getCoefficient(i) - 1) * (worstLowerBoundConstant - 1) - 1) / worstLowerBoundConstant;

                                /* maxIncr += 2; */
            if (omegaLib.trace && (desiredResult == SOLVE_SIMPLIFY)) {
              System.out.println("for equation ");
              printGEQ(eqe);
              System.out.println("\ntry decrements from 0 to " +  maxIncr);
              printProblem();
            }
            if (maxIncr > 50) {
              if (!smoothed && smoothWeirdEquations()) {
                omegaLib.conservative--;
                smoothed = true;
                continue solveGEQstart;
              }
            }
            Equation neweq = getNewEQ(eqe);
            neweq.turnBlack();
            eqe.reset(BLACK, nVars, 0);
            eqe.setTouched(true);

            for (int c = maxIncr; c >= 0; c--) {
              if (omegaLib.trace) {
                System.out.println("trying next decrement of " +  (maxIncr - c));
                printProblem();
              }

              rS = new Problem(this);

              if (omegaLib.trace)
                rS.printProblem();

              int result = rS.solve(desiredResult);
              if (result != SOLVE_F) {
                omegaLib.conservative--;
                return SOLVE_T;
              }
              EQs[0].addToCoef(0, -1);
            }

            if (j + 1 < lowerBounds) {
              nEQs = 0;
              eqe.eqncpy(EQs[0]);
              eqe.setTouched(true);
              eqe.turnBlack();
              rS = new Problem(this);
              if (omegaLib.trace)
                System.out.println("exhausted lower bound, checking if still feasible ");
              int result = rS.solve(SOLVE_F);
              if (SOLVE_F == result)
                break;
            }
          }

          if (omegaLib.trace && (desiredResult == SOLVE_SIMPLIFY))
            System.out.println("fall-off the end");

          omegaLib.conservative--;
          return SOLVE_F;
        }

        return SOLVE_UNKNOWN;
      }
    }
  }

  private int parallelSplinter(int e, int diff, int desiredResult)
  {
    if (omegaLib.trace) {
      System.out.println("Using parallel splintering");
      printProblem();
    }

    Equation neweq = getNewEQ(GEQs[e]);

    olAssert(nEQs == 1);

    for (int i = 0; i <= diff; i++) {
      Problem tmpProblem = new Problem(this);
      if (omegaLib.trace) {
        System.out.println("Splinter # " + i);
        printProblem();
      }

      if (tmpProblem.solve(desiredResult) != SOLVE_F)
        return SOLVE_T;

      neweq.addToCoef(0, -1);
    }

    return SOLVE_F;
  }

  private int verifyProblem()
  {
    check();

    Problem tmpProblem = new Problem(this);

    tmpProblem.varsOfInterest  = 0;
    tmpProblem.safeVars = 0;
    tmpProblem.nSUBs = 0;
    tmpProblem.redMemory.clear();

    boolean areRed = false;

    if (mayBeRed != 0) {
      areRed = anyNotBlack();
      if (areRed)
        tmpProblem.turnRedBlack();
    }

    omegaLib.setOriginalProblem(this);
    olAssert(BLACK == outerColor);
    outerColor = areRed ? RED : BLACK;

    if (omegaLib.trace) {
      System.out.println("verifying problem: [");
      printProblem();
    }

    tmpProblem.check();
    tmpProblem.freeEliminations(0);
    int result = tmpProblem.solve(SOLVE_UNKNOWN);
    omegaLib.setOriginalProblem(null);
    outerColor = BLACK;

    if (omegaLib.trace) {
      if (result != SOLVE_F)
        System.out.println("] verified problem");
      else
        System.out.println("] disproved problem");
      printProblem();
    }

    check();

    return result;
  }

  private void freeEliminations(int fv)
  {
    boolean tryAgain = true;

    while (tryAgain) {
      tryAgain = false;
      for (int i = nVars; i > fv; i--) {
        int e;
        int ci = 0;
        for (e = nGEQs - 1; e >= 0; e--) {
          ci = GEQs[e].getCoefficient(i);
          if (ci != 0)
            break;
        }
        int e2;
        if (e < 0)
          e2 = e;
        else if (ci > 0) {
          for (e2 = e - 1; e2 >= 0; e2--)
            if (GEQs[e2].getCoefficient(i) < 0)
              break;
        } else {
          for (e2 = e - 1; e2 >= 0; e2--)
            if (GEQs[e2].getCoefficient(i) > 0)
              break;
        }

        if (e2 >= 0)
          continue;

        if (anyNonZeroCoef(SUBs, nSUBs, i))
          continue;
        if (anyNonZeroCoef(EQs, nEQs, i))
          continue;

        if (omegaLib.trace)
          System.out.println("a free elimination of " + variable(i) + " (" + e + ")");

        if (e >= 0) {
          deleteGEQ(e);
          for (e--; e >= 0; e--)
            if (GEQs[e].isNotZero(i))
              deleteGEQ(e);

          tryAgain = (i < nVars);
        }
        deleteVariable(i);
      }
    }

    if (omegaLib.trace) {
      System.out.println("\nafter free eliminations:");
      printProblem();
      System.out.println("");
    }
  }

  private void freeRedEliminations()
  {
    boolean   tryAgain  = true;
    boolean[] isRedVar  = new boolean[nVars];
    boolean[] isDeadVar = new boolean[nVars];
    boolean[] isDeadGEQ = new boolean[nGEQs];

    for (int e = nGEQs - 1; e >= 0; e--) {
      Equation eq = GEQs[e];
      isDeadGEQ[e] = false;
      if (eq.isNotBlack())
        eq.setTrueIfNotZero(isRedVar, nVars, 1);
    }

    while (tryAgain) {
      tryAgain = false;
      for (int i = nVars; i > 0; i--) {
        if (isRedVar[i] || isDeadVar[i])
          continue;

        int e;
        int ci = 0;
        for (e = nGEQs - 1; e >= 0; e--) {
          ci = GEQs[e].getCoefficient(i);
          if (!isDeadGEQ[e] && (ci != 0))
            break;
        }

        int e2;
        if (e < 0)
          e2 = e;
        else if (ci > 0) {
          for (e2 = e - 1; e2 >= 0; e2--)
            if (!isDeadGEQ[e2] && (GEQs[e2].getCoefficient(i) < 0))
              break;
        } else {
          for (e2 = e - 1; e2 >= 0; e2--)
            if (!isDeadGEQ[e2] && (GEQs[e2].getCoefficient(i) > 0))
              break;
        }

        if (e2 >= 0)
          continue;

        if (anyNonZeroCoef(SUBs, nSUBs, i))
          continue;

        if (anyNonZeroCoef(EQs, nEQs, i))
          continue;

        if (omegaLib.trace)
          System.out.println("a free red elimination of " + variable(i));

        for (; e >= 0; e--)
          if (GEQs[e].isNotZero(i))
            isDeadGEQ[e] = true;

        tryAgain = true;
        isDeadVar[i] = true;
      }
    }

    for (int e = nGEQs - 1; e >= 0; e--)
      if (isDeadGEQ[e])
        deleteGEQ(e);

    for (int i = nVars; i > safeVars; i--)
      if (isDeadVar[i])
        deleteVariable(i);

    if (omegaLib.trace) {
      System.out.println("\nafter free red eliminations:");
      printProblem();
      System.out.println("");
    }
  }

  private void problemMerge(Problem p2)
  {
    int[] newLocation = new int[p2.nVars + 1];

    resurrectSubs();
    p2.resurrectSubs();
    setExternals();
    p2.setExternals();

    olAssert(safeVars == p2.safeVars);
    if (omegaLib.trace) {
      System.out.println("Merging:");
      printProblem();
      System.out.println("and");
      p2.printProblem();
    }

    for (int i = 1; i <= p2.safeVars; i++) {
      olAssert(p2.var[i] > 0) ;
      newLocation[i] = forwardingAddress[p2.var[i]];
    }

    for (int i = p2.safeVars + 1; i <= p2.nVars; i++) {
      int j = ++nVars;
      newLocation[i] = j;
      zeroVariable(j);
      var[j] = -1;
    }

    newLocation[0] = 0;
            
    for (int e2 = p2.nEQs - 1; e2 >= 0; e2--) {
      Equation eq1 = getNewEQ();
      Equation eq2 = p2.EQs[e2];
      eq1.copyCoefsIndexed(eq2, p2.nVars, newLocation);
    }

    for (int e2 = p2.nGEQs - 1; e2 >= 0; e2--) {
      Equation eq1 = getNewGEQ();
      Equation eq2 = p2.GEQs[e2];
      eq1.setTouched(true);
      eq1.copyCoefsIndexed(eq2, p2.nVars, newLocation);
    } 

    int w = -1; 
    for (int ii = 1; ii <= nVars; ii++) 
      if (var[ii] < 0)
        var[ii] = w--;

    if (omegaLib.trace) {
      System.out.println("to get:");
      printProblem();
    }
  }

  private void chainUnprotect()
  {
    boolean[] unprotect = new boolean[safeVars + 1];
    boolean   any       = false;

    for (int i = 1; i <= safeVars; i++) {
      unprotect[i] = (var[i] < 0);
      for (int e = nSUBs - 1; e >= 0; e--)
        if (SUBs[e].isNotZero(i))
          unprotect[i] = false;
      if (unprotect[i])
        any = true;
    }

    if (!any)
      return;

    if (omegaLib.trace) {
      System.out.println("Doing chain reaction unprotection");
      printProblem();
      for (int i = 1; i <= safeVars; i++)
        if (unprotect[i])
          System.out.println("unprotecting " + variable(i));
    }

    for (int i = 1; i <= safeVars; i++) {
      if (!unprotect[i])
        continue;

      /* wild card */
      if (i < safeVars) {
        int j = safeVars;
        swapVars(i, j);
        boolean t = unprotect[i];
        unprotect[i] = unprotect[j];
        unprotect[j] = t;
        i--;
      }
      safeVars--;
    }

    if (omegaLib.trace) {
      System.out.println("After chain reactions");
      printProblem();
    }
  }

  private void resurrectSubs()
  {
    if ((nSUBs > 0) && !omegaLib.pleaseNoEqualitiesInSimplifiedProblems) {
      boolean mbr = checkRed();

      olAssert(!mbr || (mayBeRed != 0));

      if (omegaLib.trace) {
        System.out.println("problem reduced, bringing variables back to life");
        if (mbr && (mayBeRed == 0))
          System.out.println("Red equations we don't expect");
        printProblem();
        if (nEQs > 0)
          System.out.println("This is wierd: problem has equalities");  
      }

      for (int i = 1; i <= safeVars; i++)
        if (var[i] < 0) {
          /* wild card */
          if (i < safeVars) {
            int j = safeVars;
            swapVars(i, j);
            i--;
          }
          safeVars--;
        }

      int m = nSUBs;
      int n = nVars;

      if (n < safeVars + m)
        n = safeVars + m;

      for (int e = nGEQs - 1; e >= 0; e--) {
        Equation eq = GEQs[e];
        if (singleVarGEQ(eq)) {
          int i = abs(eq.getKey());
          if (i >= safeVars + 1) {
            int kk = eq.getKey();
            eq.setKey(kk + (kk > 0 ? m : -m));
          }
        } else  {
          eq.setTouched(true);
          eq.setKey(0);
        }
      }

      int num  = nVars - safeVars;
      int sv1  = safeVars + 1;
      int svm1 = safeVars + m + 1;

      System.arraycopy(var, sv1, var, svm1, num);

      for (int e = nGEQs - 1; e >= 0; e--)
        GEQs[e].copyCoef(sv1, svm1, num);
      for (int e = nEQs - 1; e >= 0; e--)
        EQs[e].copyCoef(sv1, svm1, num);
      for (int e = nSUBs - 1; e >= 0; e--)
        SUBs[e].copyCoef(sv1, svm1, num);

      for (int i = safeVars + m; i >= sv1; i--)
        zeroVariable(i);

      nVars += m;
      safeVars += m;

      for (int e = nSUBs - 1; e >= 0; e--)  
        var[safeVars -m + 1 + e] = SUBs[e].getKey();

      for (int i = 1; i <= safeVars; i++)
        forwardingAddress[var[i]] = i;

      if (omegaLib.trace) {
        System.out.println("Ready to wake substitutions");
        printProblem();
      }

      for (int e = nSUBs - 1; e >= 0; e--) {
        Equation eq = getNewEQ(SUBs[e]);
        eq.setCoef(safeVars -m + 1 + e, -1);
        eq.turnBlack();
        if (omegaLib.trace) {
          System.out.print("brought back: ");
          printEQ(eq);
          System.out.println("");
        }
      }

      nSUBs = 0;

      if (omegaLib.trace) {
        System.out.println("variables brought back to life");
        printProblem();
      }
    }

    coalesce();
    recallRedMemories();
    cleanoutWildcards();
  }

//    public void reverseProtectedVariables() 
//    {
//      for (int v1 = 1; v1 <= safeVars; v1++) {
//        int v2 = safeVars + 1 - v1;
//        if (v2 >= v1)
//      break;

//        for (int e = 0; e < nEQs; e++)
//          EQs[e].swapVars(v1, v2);

//        for (int e = 0; e < nGEQs; e++)
//          GEQs[e].swapVars(v1, v2);

//        for (int e = 0; e < nSUBs; e++)
//          SUBs[e].swapVars(v1, v2);
//      }

//      for (int i = 1; i <= safeVars; i++)
//        forwardingAddress[var[i]] = i;
//      for (int i = 0; i < nSUBs; i++)
//        forwardingAddress[SUBs[i].getKey()] = -i - 1;
//    }

  public void orderedElimination(int symbolic) 
  {
    boolean[] isDead = new boolean[nEQs];

    if (!variablesInitialized)
      initializeVariables();

    for (int i = nVars; i > symbolic; i--) {
      for (int e = 0; e < nEQs; e++) {
        Equation eq = EQs[e];
        int      ci = eq.getCoefficient(i);
        if ((ci != 1) && (ci != -1))
          continue;

        int j = eq.lastNZCoef(nVars, i + 1);

        if (i == j) {
          doElimination(e, i);
          isDead[e] = true;
          break;
        }
      }
    }

    for (int e = nEQs - 1; e >= 0; e--)
      if (isDead[e])
        deleteEQ(e);

    for (int i = 1; i <= safeVars; i++)
      forwardingAddress[var[i]] = i;
    for (int i = 0; i < nSUBs; i++)
      forwardingAddress[SUBs[i].getKey()] = -i - 1;
  }

  private int checkSum()
  {
    int cs = 0;
    for (int e = 0; e < nGEQs; e++) {
      int c = GEQs[e].getConstant();
      cs += c * c * c;
    }
    return cs;
  }

  private void coalesce()
  {
    int colors = numNotBlack(GEQs, nGEQs);

    if (colors < 2)
      return;

    boolean[] isDead         = new boolean[nGEQs];
    boolean   foundSomething = false;
    for (int e = 0; e < nGEQs; e++) {
      Equation eq1 = GEQs[e];
      if (eq1.isTouched())
        continue;

      for (int e2 = e + 1; e2 < nGEQs; e2++) {
        Equation eq2 = GEQs[e2];
        if ((!eq2.isTouched() && (eq1.getKey() == -eq2.getKey())) && (eq1.getConstant() == -eq2.getConstant())) {
          Equation neweq = getNewEQ(eq1);
          neweq.setColor(eq1, eq2);
          foundSomething = true;
          isDead[e] = true;
          isDead[e2] = true;
        }
      }
    }

    for (int e = nGEQs - 1; e >= 0; e--)
      if (isDead[e])
        deleteGEQ(e);

    if (omegaLib.trace && foundSomething) {
      System.out.println("Coalesced GEQs into " + foundSomething + " EQ's:");
      printProblem();
    }
  }

  /*
   * Remove BLACK constraints from the problem.
   * Make all the remaining red constraints BLACK.
   */
  public void removeColorConstraints()
  {
    for (int i = nGEQs - 1; i >= 0; i--) {
      if (GEQs[i].isNotBlack())
        deleteGEQ(i);
    }
  
    for (int i = nEQs - 1; i >= 0; i--) {
      if (EQs[i].isNotBlack())
        deleteEQ(i);
    }
  }

  /**
   * Return the number of equations that have non-BLACK color.
   */
  private int numNotBlack(Equation[] eqs, int l)
  {
    int nb = 0;
    for (int e = 0; e < l; e++)
      if (eqs[e].isNotBlack())
        nb++;
    return nb;
  }

  private int countRedGEQs()
  {
    return numNotBlack(GEQs, nGEQs);
  }

  /**
   * Return true if any of the equations are not BLACK.
   */
  private boolean anyNotBlack(Equation[] eqs, int l)
  {
    for (int e = l - 1; e >= 0; e--)
      if (eqs[e].isNotBlack())
        return true;
    return false;
  }

  /**
   * Return true if any of the equations are not BLACK.
   */
  private boolean anyNotBlack()
  {
    if (anyNotBlack(GEQs, nGEQs))
      return true;
    return anyNotBlack(EQs, nEQs);
  }

  private boolean checkRed()
  {
    if (redMemory.size() > 0)
      return true;
    return anyNotBlack();
  }

  /**
   * Return the last equation with a non-zero coefficient for the variable.
   */
  private Equation lastNonZeroCoef(Equation[] eqs, int l, int var)
  {
    for (int i = l - 1; i >= 0; i--) {
      Equation eq = eqs[i];
      if (eq.isNotZero(var))
        return eq;
    }
    return null;
  }

  /**
   * Return the last equation with a non-zero coefficient for the variable.
   */
  private Equation firstNonZeroCoef(Equation[] eqs, int l, int var)
  {
    for (int i = 0; i < l; i++) {
      Equation eq = eqs[i];
      if (eq.isNotZero(var))
        return eq;
    }
    return null;
  }

  /**
   * Return true if any equation has a non-zero coefficient for the variable.
   */
  private boolean anyNonZeroCoef(Equation[] eqs, int l, int var)
  {
    for (int i = 0; i < l; i++) {
      Equation eq = eqs[i];
      if (eq.isNotZero(var))
        return true;
    }
    return false;
  }

  public boolean anyNonZeroCoef(int var)
  {
    if (anyNonZeroCoef(EQs, nEQs, var))
      return true;
    if (anyNonZeroCoef(GEQs, nGEQs, var))
      return true;
    return anyNonZeroCoef(SUBs, nSUBs, var);
  }

  /**
   * Copy column fr_col of problem fp
   * to   column to_col of this problem.
   * Displacement for constraints in this problem are start_EQ and start_GEQ.
   */
  public void copyColumn(int to_col, Problem fp,  int fr_col, int start_EQ, int start_GEQ)
  {
    for (int i = 0; i < fp.nEQs; i++)
      EQs[i + start_EQ].copyColumn(fp.EQs[i], fr_col, to_col);

    for (int i = 0; i < fp.nGEQs; i++)
      GEQs[i + start_EQ].copyColumn(fp.GEQs[i], fr_col, to_col);
  }

  /**
   * Zero column to_col of this problem.
   * Displacement for constraints in to_conj are start_EQ and start_GEQ.
   * Number of constraints to zero are no_EQ and no_GEQ.
   */
  public void zeroColumn(int to_col, int start_EQ, int start_GEQ, int no_EQs, int no_GEQs)
  {
    for (int i = 0; i < no_EQs; i++) {
      Equation eq = EQs[i + start_EQ];
      eq.zeroColumn(to_col);
    }
    for (int i = 0; i < no_GEQs; i++) {
      Equation eq = GEQs[i + start_GEQ];
      eq.zeroColumn(to_col);
    }
  }

  public void merge(int action, Conjunct conj1, Conjunct conj2, Conjunct conj3, Vector newVars)
  {
    Problem  p1 = conj1.getProblem();
    Problem  p2 = conj2.getProblem();

    // initialize omega stuff

    for (int i = 0; i < p1.nGEQs + p2.nGEQs; i++) {
      Equation eq = this.getNewGEQ();
      eq.setTouched(true);
    }

    for (int i = 0; i < p1.nEQs + p2.nEQs; i++) {
      Equation eq = this.getNewEQ();
      eq.setTouched(true);
    }

    olAssert(this.nGEQs == p1.nGEQs + p2.nGEQs);
    olAssert(this.nEQs == p1.nEQs + p2.nEQs);

    // Flag constraints from second constraint as red, if necessary.

    if (action == Conjunct.MERGE_GIST) {
      for (int i = 0; i < p2.nEQs; i++)
        this.EQs[i + p1.nEQs].turnRed();

      for (int i = 0; i < p2.nGEQs; i++)
        this.GEQs[i + p1.nGEQs].turnRed();
    }

    // Copy constant column.

    this.copyColumn(0, p1, 0, 0, 0);
    this.copyColumn(0, p2, 0, p1.nEQs, p1.nGEQs);
  
    // Copy protected variables column from conj1.

    int    new_col = 1;
    Vector vars1   = conj1.variables();
    int    l1      = vars1.size();
    for (int i = 1; i <= l1; i++) {
      VarDecl v = (VarDecl) vars1.elementAt(i - 1);
      if (v.kind() == VarDecl.WILDCARD_VAR)
        continue;

      newVars.addElement(v);
      this.copyColumn(new_col, p1, i, 0, 0);
      this.zeroColumn(new_col, p1.nEQs, p1.nGEQs, p2.nEQs, p2.nGEQs);
      new_col++;
    }
  
    // Copy protected variables column from conj2,
    // checking if conj3 already has this variable from conj1.

    Vector vars2 = conj2.variables();
    int    l2    = vars2.size();
    for (int i = 1; i <= l2; i++) {
      VarDecl v = (VarDecl) vars2.elementAt(i - 1);
      if (v.kind() == VarDecl.WILDCARD_VAR)
        continue;

      int to_ix = 1 + newVars.indexOf(v);

      if (to_ix > 0) { // use old column
        this.copyColumn(to_ix, p2, i, p1.nEQs, p1.nGEQs);
        continue;
      }

      // create new column

      newVars.addElement(v);
      this.zeroColumn(new_col, 0, 0, p1.nEQs, p1.nGEQs);
      this.copyColumn(new_col, p2, i, p1.nEQs, p1.nGEQs);
      new_col++;
    }

    this.setNumSafeVars(new_col - 1);
  
    // Copy wildcards from conj1.

    for (int i = 1; i <= l1; i++) {
      VarDecl v = (VarDecl) vars1.elementAt(i - 1);
      if (v.kind() != VarDecl.WILDCARD_VAR)
        continue;

      VarDecl nv = conj3.declare(v);
      newVars.addElement(nv);
      this.copyColumn(new_col, p1, i, 0, 0);
      this.zeroColumn(new_col, p1.nEQs, p1.nGEQs, p2.nEQs, p2.nGEQs);
      new_col++;
    }
  
    // Copy wildcards from conj2.

    for (int i = 1; i <= l2; i++) {
      VarDecl v = (VarDecl) vars2.elementAt(i - 1);
      if (v.kind() != VarDecl.WILDCARD_VAR)
        continue;

      VarDecl nv = conj3.declare(v);
      newVars.addElement(nv);
      this.zeroColumn(new_col, 0, 0, p1.nEQs, p1.nGEQs);
      this.copyColumn(new_col, p2, i, p1.nEQs, p1.nGEQs);
      new_col++;
    }
 
    this.setNumVars(new_col - 1);
    this.resetVarAndForwarding();
  }

  public int varMappedAt(int i)
  {
    return var[i];
  }

  public int numSafeVars()
  {
    return safeVars;
  }

  public int numVars()
  {
    return nVars;
  }

  public void setNumSafeVars(int number)
  {
    safeVars = number;
  }

  public void setNumVars(int number)
  {
    nVars = number;
  }

  public int mapToColumn()
  {
    int col = ++nVars;
 
    forwardingAddress[col] = col;
    var[col] = col;
    zeroColumn(col, 0, 0, nEQs, nGEQs);

    return col;
  }

  public void combineColumns(int i, int j)
  {
    for (int k = 0; k < nEQs; k++)
      EQs[k].combineColumns(i, j);

    for (int k = 0; k < nGEQs; k++)
      GEQs[k].combineColumns(i, j);
  }

  public void touchAllGEQs()
  {
    for (int e = 0; e < nGEQs; e++)
      GEQs[e].setTouched(true);
  }

  public void touchAllEQs()
  {
    for (int e = 0; e < nEQs; e++)
      EQs[e].setTouched(true);
  }

  public int findColumn(int column)
  {
    // If it has been through the omega core (variablesInitialized), 
    // and it exists in the problem, check to see if it has been forwarded.
    // This will likely only be the case if substitutions have been done;
    // that won't arise in user code, only in printWithSubs and the
    // Substitutions class.

    if (variablesInitialized && (column > 0)) {
      olAssert(forwardingAddress[column] != 0); 
      column = forwardingAddress[column];
    }

    if (column > nVars)
      throw new polyglot.util.InternalCompilerError("col " + column + " > " + nVars);

    return column;
  }

  /**
   * We're going to try to describe the equalities among a set of variables.
   * We want to perform some substitutions to ensure that we don't miss
   *   v_1 = v_2 due to its expression as v_1 = v_3 && v_2 = v_3
   * We therefore try to substitute out all variables that we don't care
   *   about (e.g., v_3 in the above example).
   */
  public boolean trytoSub(int col)
  {
    if (!variablesInitialized)
      initializeVariables();

    olAssert(col <= nVars);
    olAssert(!omegaLib.inApproximateMode);

    int e;
    Equation eq = null;
    for (e = 0; e < nEQs; e++) {
      eq = EQs[e];
      int ccol = eq.getCoefficient(col);
      if ((ccol == 1) || (ccol == -1)) {
        int v = varMappedAt(col);
        doElimination(e, col);
        if (col != nVars + 1)
          forwardingAddress[var[nVars + 1]] = col;
        SUBs[nSUBs - 1].setKey(v);
        olAssert(v != 0);
        forwardingAddress[v] = -nSUBs;
        break;
      }
    }
 
    if (e == nEQs)
      return false;

    if (eq.anyNZCoef(nVars, 0))
      throw new polyglot.util.InternalCompilerError("Non-zero coefficient");

    deleteEQ(e);

    for (int i = 0; i < nSUBs; i++)
      olAssert(forwardingAddress[SUBs[i].getKey()] == -i - 1);

    return true;
  }

  public boolean findEquality(int c_to, int c_from, int initial)
  {
  main:
    for (int e = 0; e < nEQs; e++) {
      Equation eq = EQs[e];
      if (eq.isZero(c_from))
        continue;

      for (int k = initial; k < nVars; k++)
        if ((k != c_to) && (k != c_from) && eq.isNotZero(k))
          continue main;  // this EQ is not what we need
      return true;
    }
    return false;
  }

  public boolean isNotEQZero(int ignoreEQ, int var)
  {
    for (int i = 0; i < nEQs; i++)
      if ((ignoreEQ != i) && EQs[i].isNotZero(var)) // We are not ready to handle this. 
        return true;
    return false;
  }
}
