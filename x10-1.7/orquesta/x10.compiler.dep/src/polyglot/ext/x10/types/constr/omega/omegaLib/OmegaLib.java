package polyglot.ext.x10.types.constr.omega.omegaLib;

import java.util.*;

/**
 * This class is defined as the basis for using the Omega Library.
 * It's primary purpose is to hold the variables that were static
 * before.
 * <p>
 * $Id: OmegaLib.java,v 1.1 2008-03-06 18:02:23 nystrom Exp $
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
 * The Omega project can be contacted at <a
 * href="mailto:omega@cs.umd.edu">omega@cs.umd.edu</a> or <a
 * href="http://www.cs.umd.edu/projects/omega">http://www.cs.umd.edu/projects/omega</a>
 * </blockquote>
 */
public final class OmegaLib
{
  /**
   * True if traces are to be performed.
   */
  public static boolean classTrace = false;

  private static final int hashTableSize = 2500;

  public static final int posInfinity = Integer.MAX_VALUE;
  public static final int negInfinity = Integer.MIN_VALUE;
  public static final int maxKeys     = 4096;
  public static final int maxVars     = 40;

  public boolean pleaseNoEqualitiesInSimplifiedProblems = false;
  public boolean printInCodeGenStyle                    = false;
  public boolean leavePufsUntouched                     = false;
  public boolean addingOuterEqualities                  = false;
  public boolean inApproximateMode                      = false;
  public boolean inStridesAllowedMode                   = false;
  public boolean trace                                  = false;

  public int sRdtConstraints        = 0;
  public int skipSetChecks          = 0;
  public int skipFinalizationCheck  = 0;
  public int presLegalNegations     = Conjunct.ANY_NEGATION;
  public int wildCardInstanceNumber = 0;
  public int useUglyNames           = 0;
  public int solveGEQDepth          = 0;
  public int conservative           = 0;
  public int newVar                 = -1;

  public FAnd and_below_exists = null;

  private int        printLevel       = 0;
  private int        ghashVersion     = 0;
  private int        nextKey          = maxVars + 1;
  private Problem    originalProblem  = null;
  private Equation[] hashMaster       = new Equation[hashTableSize];
  private HashMap    nameHashTable    = new HashMap(203);
  private Vector     inputVars        = new Vector();
  private Vector     outputVars       = new Vector();
  private VarDecl[]  exists_ids       = new VarDecl[10];
  private int[]      exists_numbers   = new int[10];
  private int        exists_next      = 0;

  public OmegaLib(boolean trace)
  {
    this.trace = trace && classTrace;
  }

  public void initialize()
  {
    pleaseNoEqualitiesInSimplifiedProblems = false;
    printInCodeGenStyle                    = false;
    leavePufsUntouched                     = false;
    addingOuterEqualities                  = false;
    inApproximateMode                      = false;
    inStridesAllowedMode                   = false;
    trace                                  = false;

    sRdtConstraints        = 0;
    skipSetChecks          = 0;
    skipFinalizationCheck  = 0;
    presLegalNegations     = Conjunct.ANY_NEGATION;
    wildCardInstanceNumber = 0;
    useUglyNames           = 0;
    solveGEQDepth          = 0;
    conservative           = 0;
    newVar                 = -1;

    and_below_exists = null;

    printLevel       = 0;
    ghashVersion     = 0;
    nextKey          = maxVars + 1;
    originalProblem  = null;

    if (hashMaster != null)
      hashMaster = new Equation[hashTableSize];
    else {
      for (int i = 0; i < hashMaster.length; i++)
        hashMaster[i] = null;
    }

    if (nameHashTable == null)
      nameHashTable = new HashMap(203);
    else
      nameHashTable.clear();

    if (inputVars == null)
      inputVars = new Vector();
    else
      inputVars.clear();

    if (outputVars == null)
      outputVars = new Vector();
    else
      outputVars.clear();

    if (exists_ids != null)
      exists_ids = new VarDecl[10];
    else {
      for (int i = 0; i < exists_ids.length; i++)
        exists_ids[i] = null;
    }

    if (exists_numbers != null)
      exists_numbers = new int[10];
    else {
      for (int i = 0; i < exists_numbers.length; i++)
        exists_numbers[i] = 0;
    }

    exists_next = 0;
  }

  public void setPrintLevel(int printLevel)
  {
    this.printLevel = printLevel;
  }

  public void incrementPrintLevel()
  {
    printLevel++;
  }

  public void decrementPrintLevel()
  {
    printLevel--;
  }

  public void printHead()
  {
    for (int i = 0; i < printLevel; i++) {
      System.out.print(". ");
    }
  }

  public VarDecl newInputVar(int i)
  {
    if (i < inputVars.size())
      return (VarDecl) inputVars.elementAt(i);

    while (i >= inputVars.size())
      inputVars.addElement(new VarDecl("", VarDecl.INPUT_VAR, i));

    return (VarDecl) inputVars.elementAt(i);
  }

  public VarDecl newOutputVar(int i)
  {
    if (i < outputVars.size())
      return (VarDecl) outputVars.elementAt(i);

    while (i >= outputVars.size())
      outputVars.addElement(new VarDecl("", VarDecl.OUTPUT_VAR, i));

    return (VarDecl) outputVars.elementAt(i);
  }

  public Vector inputVars()
  {
    return inputVars;
  }

  public Vector outputVars()
  {
    return outputVars;
  }

  public void dumpInputVars(int n)
  {
    System.out.println("Dump inputVars");
    for (int i = 1; i <= n; i++) {
      VarDecl cs = (VarDecl) inputVars.elementAt(i);
      String   s  = cs.baseName();
      if (s.equals(""))
        System.out.print("null");       
      else
        System.out.print(s);
      System.out.print(" ");
    }
    System.out.println("");
  }

  public void increment(VarDecl v)
  {
    String name = v.baseName();

    if (name == null)
      return;

    if (name.equals(""))
      return;

    CName c = (CName) nameHashTable.get(v.baseName());
 
    v.setInstance(c.increment());
  }

  public void decrement(VarDecl v)
  {
    String name = v.baseName();

    if (name == null)
      return;

    if (name.equals(""))
      return;

    CName c = (CName) nameHashTable.get(v.baseName());

    v.setInstance(c.decrement());
  }

  public void decrement(String name)
  {
    CName c = (CName) nameHashTable.get(name);
    c.decrement();
  }

  public CName newCName(String s)
  {
    if (s == null)
      s = "";

    CName c = (CName) nameHashTable.get(s);

    if (c == null) {
      c = new CName(s);
      nameHashTable.put(s, c);
    }

    return c;
  }

  public void appendIDS(VarDecl v, int pos)
  {
    if (exists_next >= exists_numbers.length) {
      VarDecl[] nv = new VarDecl[exists_next + 10];
      System.arraycopy(exists_ids, 0, nv, 0, exists_ids.length);
      exists_ids = nv;
      int[] ni = new int[exists_next + 10];
      System.arraycopy(exists_numbers, 0, ni, 0, exists_numbers.length);
      exists_numbers = ni;
    }

    exists_ids[exists_next] = v;
    exists_numbers[exists_next] = pos;
    exists_next++;
  }

  public void clearIDS()
  {
    exists_next = 0;
  }

  public VarDecl getVarIDS(int i)
  {
    return exists_ids[i];
  }

  public int getPosIDS(int i)
  {
    return exists_numbers[i];
  }

  public void putIDS(VarDecl v, int pos, int index)
  {
    exists_ids[index] = v;
    exists_numbers[index] = pos;
  }

  public void usePrototype(int hashCode, Equation eqe, int[] packing, int topVar, int nVars)
  {
    int mult = 1;
    int g2   = hashCode;

    if (hashCode < 0) {
      mult = -1;
      g2 = -hashCode;
    }

  top:
    for (int j = g2 % hashTableSize; true; j = (j + 1) % hashTableSize) {
      Equation proto = hashMaster[j];

      if (proto == null) {
        proto = new Equation(nextKey, Problem.BLACK, nVars, 0);
        hashMaster[j] = proto;

        nextKey++;
        if (proto.getKey() > maxKeys)
          throw new polyglot.util.InternalCompilerError("too many hash keys generated ");

        eqe.setKey(mult * proto.getKey());

        for (int i0 = topVar; i0 >= 0; i0--) {
          int i = packing[i0];
          proto.setCoef(i, mult * eqe.getCoefficient(i));
        }

        proto.setConstant(topVar);
        proto.setColor(g2);

        if (trace)
          System.out.println(" constraint key = " + (nextKey - 1));

        return;
      }

      if (proto.getColor() != g2)
        continue;

      if (proto.getConstant() != topVar)
        continue;

      for (int i0 = topVar; i0 >= 0; i0--) {
        int i = packing[i0];
        if (eqe.getCoefficient(i) != (mult * proto.getCoefficient(i)))
          continue top;
      }

      eqe.setKey(mult * proto.getKey());
      return;
    }
  }

  public int getProtoHash()
  {
    return ghashVersion;
  }

  public int resetPrototypes(int hashVersion, Problem prob)
  {
    if (nextKey * 3 > maxKeys) {
      ghashVersion++;
      nextKey = maxVars + 1;

      prob.touchAllGEQs();
      for (int i = 0; i < hashTableSize; i++)
        hashMaster[i].setTouched(false);

      return ghashVersion;
    }

    if (hashVersion != ghashVersion) {
      prob.touchAllGEQs();
      return ghashVersion;
    }

    return hashVersion;
  }

  public Problem getOriginalProblem()
  {
    return originalProblem;
  }

  public void setOriginalProblem(Problem prob)
  {
    this.originalProblem = prob;
  }
}
