package polyglot.ext.x10.types.constr.omega.omegaLib;

import java.util.Iterator;
import java.util.Enumeration;
import java.util.*;

/**
 * RelBody.
 * <p>
 * $Id: RelBody.java,v 1.1 2008-03-06 18:02:23 nystrom Exp $
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
public final class RelBody extends Formula
{
  private static int createdCount = 0; /* A count of all the instances of this class that were created. */

  public static int created()
  {
    return createdCount;
  }

  private static final int Basic_Farkas                = 0;
  private static final int Decoupled_Farkas            = 1;
  private static final int Linear_Combination_Farkas   = 2;
  private static final int Positive_Combination_Farkas = 3;
  private static final int Affine_Combination_Farkas   = 4;
  private static final int Convex_Combination_Farkas   = 5;

  private static final boolean       checkMaybeSubset             = true;
  private static final GlobalVarDecl coefficient_of_constant_term = new GlobalVarDecl("constantTerm");

  private int     farkasDifficulty;
  private int     number_input;
  private int     number_output;
  private int     ref_count;
  private int     r_conjs;              // are redundant conjuncts eliminated?
  private Vector  inNames;
  private Vector  outNames;
  private Vector  symbolic;
  private DNF     simplifiedDNF;
  private boolean finalized;
  private boolean isSet;

  /**
   * @param number_input is the number of input variables
   * @param number_output is the number of output variables
   */
  public RelBody(OmegaLib omegaLib, int number_input, int number_output)
  {
    super(omegaLib);

    this.myRelation     = this;
    this.ref_count      = 0;
    this.number_input   = number_input;
    this.number_output  = number_output; 
    this.simplifiedDNF  = null;
    this.r_conjs        = 0; 
    this.finalized      = false;
    this.isSet          = false;
 
    this.inNames    = new Vector(number_input + 1);
    for (int i = 0; i <= number_input; i++) {
      this.inNames.addElement(omegaLib.newCName(""));
      omegaLib.newInputVar(i);
    }

    this.outNames    = new Vector(number_output + 1);
    for (int i = 0; i <= number_output; i++) {
      outNames.addElement(omegaLib.newCName(""));
      omegaLib.newOutputVar(i);
    }

    this.symbolic = new Vector();

    if (omegaLib.trace)
      System.out.println("+++ Create Rel_Body::Rel_Body(" + number_input + ", " + number_output + ") +++");

    createdCount++;
    if (omegaLib.trace)
      dumpInNames(inNames, number_input);
  }

  public RelBody(RelBody r)
  {
    super(r.omegaLib);

    this.myRelation     = this;
    this.number_input   = r.number_input;
    this.number_output  = r.number_output;
    this.ref_count      = 0;
    this.inNames        = (Vector) r.inNames.clone();
    this.outNames       = (Vector) r.outNames.clone();
    this.r_conjs        = r.r_conjs; 
    this.finalized      = r.finalized;
    this.isSet          = r.isSet;
    this.symbolic       = VarDecl.copyVarDecls(r.symbolic);
    this.simplifiedDNF = null;

    if (omegaLib.trace) {
      System.out.print("+++ Copy ");
      System.out.print(r);
      System.out.print(" => ");
      System.out.print(this);
      System.out.println(" +++");
      prefixPrint();
    }

    if ((r.numberOfChildren() != 0) && (r.simplifiedDNF == null)) {
      Formula f = r.getFirstChild().copy(this, this);
      f.remap();
      addChild(f);
    } else if ((r.numberOfChildren() == 0) && (r.simplifiedDNF != null)) {
      simplifiedDNF = r.simplifiedDNF.copy(omegaLib, this);
      simplifiedDNF.remap();
    }

    VarDecl.resetRemapField(r.symbolic);

    if (omegaLib.trace)
      dumpInNames(inNames, number_input);
    createdCount++;
  }

  public RelBody(RelBody r, Conjunct c)
  {
    super(r.omegaLib);

    this.myRelation     = this;
    this.number_input   = r.number_input;
    this.number_output  = r.number_output;
    this.ref_count      = 0;
    this.inNames        = (Vector) r.inNames.clone();
    this.outNames       = (Vector) r.outNames.clone();
    this.r_conjs        = 0;
    this.finalized      = r.finalized;
    this.isSet          = r.isSet;
    this.symbolic       = VarDecl.copyVarDecls(r.symbolic);
    this.simplifiedDNF  = new DNF(omegaLib);

    if (omegaLib.trace)
      dumpInNames(inNames, number_input);

    // assert that r has as many variables as c requires, or that c is from r
    olAssert(r == c.relation());
    olAssert(r.simplifiedDNF != null);

    simplifiedDNF.addConjunct((Conjunct) c.copy(this, this));

    getSingleConjunct().remap();

    VarDecl.resetRemapField(r.symbolic);

    if (omegaLib.trace) {
      System.out.print("+++ Copy ");
      System.out.print(r);
      System.out.print(", Conjunct ");
      System.out.print(c.id);
      System.out.print(" => ");
      System.out.print(this);
      System.out.println(" +++");
    }
    createdCount++;
  }

  public void delete()
  {
    ref_count--;
    if (ref_count > 0)
      return;

    super.delete();
    if (simplifiedDNF == null)
      return;

    simplifiedDNF.delete();
    simplifiedDNF = null;
  }

  public void incrementRefCount()
  {
    ref_count++;
  }

  public void decrementRefCount()
  {
    ref_count--;
  }

  public int getRefCount()
  {
    return ref_count;
  }

  public void setFinalized()
  {
    finalized = true;
    super.setFinalized();
  }

  public DNF simplifiedDNF()
  {
    return simplifiedDNF;
  }

  /**
   * Return x such that for all conjuncts c, c has <code>&gt;= x</code> leading 0s.
   * For a set, return -1.
   */
  public int queryGuaranteedLeadingZeros()
  {
    return queryDNF().queryGuaranteedLeadingZeros(isSet() ? -1 : 0);
  }

  /**
   * Return x such that for all conjuncts c, c has <code>&lt;= x</code> leading 0s.
   * If no conjuncts, return min of input and output tuple sizes, or -1 if relation is a set.
   */
  public int queryPossibleLeadingZeros()
  {
    int x = isSet() ? -1 : min(number_input, number_output);
    return queryDNF().queryPossibleLeadingZeros(x);
  }

  /**
   * Return +-1 according to sign of leading dir, or 0 if we don't know.
   */
  public int queryLeadingDir()
  {
    return queryDNF().queryLeadingDir();
  }

  public int numberOfConjuncts()
  {
    return queryDNF().length();
  }

  private void dumpInNames(Vector names, int n)
  {
    System.out.println("Dump InNames");
    for (int i = 1; i <= n; i++) {
      CName  cs = (CName) names.elementAt(i);
      String na = cs.name();
      if (na.equals(""))
        System.out.print("null");
      else
        System.out.print(na);
      System.out.print(" ");
    }
    System.out.println("");
    omegaLib.dumpInputVars(n);
  }

  public String toString()
  {
    StringBuffer buf = new StringBuffer("(RelBody 0x");
    buf.append(Integer.toHexString(hashCode()));
    buf.append(" f=");
    buf.append(finalized);
    buf.append(" s=");
    buf.append(isSet);
    buf.append(" rc=");
    buf.append(ref_count);
    buf.append(" (");
    buf.append(number_input);
    buf.append(',');
    buf.append(number_output);
    buf.append("))");
    return buf.toString();
  }

  public Formula copy(Formula f, RelBody b)
  {
    throw new polyglot.util.InternalCompilerError("RelBody.copy");
  }

  public int nodeType()
  {
    return OP_RELATION;
  }

  public boolean isSet()
  { 
    return isSet;
  }

  public void setIsSet(boolean flg)
  {
    isSet = flg;
  }

  public int numberInput()
  {
    return number_input;
  }

  public int numberOutput()
  {
    return number_output;
  }

  public int numberSet()
  {
    olAssert(isSet());
    return number_input;
  }

  public Vector getGlobalDecls()
  {
    return symbolic;
  }

  public void clearGlobalDecls()
  {
    symbolic.clear();
  }

  /**
   * Return true if the variable was removed from the list of globals.
   */
  public boolean removeGlobalDecl(VarDecl v)
  {
    return symbolic.remove(v);
  }

  private int maxUfsArity()
  {
    int ma  = 0;
    int gvl = symbolic.size();
    for (int i = 0; i < gvl; i++) {
      VarDecl v = (VarDecl) symbolic.elementAt(i);
      int a = v.getGlobalVar().arity();
      if (a > ma)
        ma = a;
    }
    return ma;
  }

  private int maxSharedUfsArity()
  {
    int ma  = 0;
    int gvl = symbolic.size();
    for (int i = 0; i < gvl; i++) {
      VarDecl v = (VarDecl) symbolic.elementAt(i);
      for (int j = 0; j < gvl; j++) {
        VarDecl v2 = (VarDecl) symbolic.elementAt(j);
        if ((v != v2) && (v.getGlobalVar() == v2.getGlobalVar()) && (v.functionOf() != v2.functionOf())) {
          int a = v.getGlobalVar().arity();
          if (a > ma)
            ma = a;
        }
      }
    }
    return ma;
  }

  private int maxUfsArityOfSet()
  {
    int ma  = 0;
    int gvl = symbolic.size();
    for (int i = 0; i < gvl; i++) {
      VarDecl v = (VarDecl) symbolic.elementAt(i);
      if (v.functionOf() == VarDecl.SET_TUPLE) {
        int a = v.getGlobalVar().arity();
        if (a > ma)
          ma = a;
      }
    }
    return ma;
  }

  private int maxUfsArityOfIn()
  {
    int ma  = 0;
    int gvl = symbolic.size();
    for (int i = 0; i < gvl; i++) {
      VarDecl v = (VarDecl) symbolic.elementAt(i);
      if (v.functionOf() == VarDecl.INPUT_TUPLE) {
        int a = v.getGlobalVar().arity();
        if (a > ma)
          ma = a;
      }
    }
    return ma;
  }

  private int maxUfsArityOfOut()
  {
    int ma  = 0;
    int gvl = symbolic.size();
    for (int i = 0; i < gvl; i++) {
      VarDecl v = (VarDecl) symbolic.elementAt(i);
      if (v.functionOf() == VarDecl.OUTPUT_TUPLE) {
        int a = v.getGlobalVar().arity();
        if (a > ma)
          ma = a;
      }
    }
    return ma;
  }

  public VarDecl inputVar(int nth)
  {
    olAssert(!isSet() || (omegaLib.skipSetChecks > 0));
    olAssert((1 <= nth) && (nth <= number_input));
    CName   c  = (CName) inNames.elementAt(nth);
    VarDecl in = omegaLib.newInputVar(nth);
    in.nameVariable(c.name());
    return in;
  }

  public VarDecl outputVar(int nth)
  {
    olAssert(!isSet() || (omegaLib.skipSetChecks > 0));
    olAssert((1 <= nth) && (nth <= number_output));
    CName   c   = (CName) outNames.elementAt(nth);
    VarDecl out = omegaLib.newOutputVar(nth);
    out.nameVariable(c.name());
    return out;
  }

  public VarDecl setVar(int nth)
  {
    olAssert(isSet() || (omegaLib.skipSetChecks > 0));
    olAssert((1 <= nth) && (nth <= number_input));
    CName   c  = (CName) inNames.elementAt(nth);
    VarDecl in = omegaLib.newInputVar(nth);
    in.nameVariable(c.name());
    return in;
  }

  public VarDecl getLocal(VarDecl v)
  {
    if (v.kind() == VarDecl.GLOBAL_VAR) {
      GlobalVarDecl g = v.getGlobalVar();
      if (g.arity() != 0)
        return getLocal(g,v.functionOf());
      return getLocal(g);
    }
    if (isSet())
      return setVar(v.getPosition());
    if (v.kind() == VarDecl.INPUT_VAR)
      return inputVar(v.getPosition());
    if (v.kind() == VarDecl.OUTPUT_VAR)
      return outputVar(v.getPosition());

    throw new polyglot.util.InternalCompilerError("Can only get local for variable with global scope");
  }

  public VarDecl getLocal(GlobalVarDecl G)
  {
    olAssert(G.arity() == 0);
    int gvl = symbolic.size();
    for (int i = 0; i < gvl; i++) {
      VarDecl v = (VarDecl) symbolic.elementAt(i);

      if (v.getGlobalVar() == G)
        return v;
    }

    VarDecl v = G.getLocal();
    symbolic.addElement(v);
    return v;
  }

  public VarDecl getLocal(GlobalVarDecl G, int of)
  {
    olAssert((G.arity() == 0) || (of == VarDecl.INPUT_TUPLE) || (of == VarDecl.OUTPUT_TUPLE));

    int gvl = symbolic.size();
    for (int i = 0; i < gvl; i++) {
      VarDecl v = (VarDecl) symbolic.elementAt(i);
      if ((v.getGlobalVar() == G) && ((G.arity() == 0) || (of == v.functionOf())))
        return v;
    }

    VarDecl V = G.getLocal(of);
    symbolic.addElement(V);
    return V;
  }

  public boolean hasLocal(GlobalVarDecl G)
  {
    olAssert(G.arity() == 0);

    int gvl = symbolic.size();
    for (int i = 0; i < gvl; i++) {
      VarDecl v = (VarDecl) symbolic.elementAt(i);
      if (v.getGlobalVar() == G)
        return true;
    }

    return false;
  }

  public boolean hasLocal(GlobalVarDecl G, int of)
  {
    olAssert((G.arity() == 0) || (of == VarDecl.INPUT_TUPLE) || (of == VarDecl.OUTPUT_TUPLE));

    int gvl = symbolic.size();
    for (int i = 0; i < gvl; i++) {
      VarDecl v = (VarDecl) symbolic.elementAt(i);
      if (v.getGlobalVar() == G)
        return true;
    }

    return false;
  }

  public void nameInputVar(int nth, String s)
  {
    if ((nth < 1) || (nth > number_input) || (isSet() && (omegaLib.skipSetChecks <= 0)))
      throw new polyglot.util.InternalCompilerError("Invalid input variable " + nth);

    CName c = omegaLib.newCName(s);
    inNames.setElementAt(c, nth);
  }

  public void nameOutputVar(int nth, String s)
  {
    if ((nth < 1) || (nth > number_output) || (isSet() && (omegaLib.skipSetChecks <= 0)))
      throw new polyglot.util.InternalCompilerError("Invalid output variable " + nth);

    CName c = omegaLib.newCName(s);
    outNames.setElementAt(c, nth);
  }

  public void addInputVar(String name)
  {
    if (name == null)
      throw new polyglot.util.InternalCompilerError("addInputVar");

    CName c = omegaLib.newCName(name);
    number_input++;
    inNames.addElement(c);

    if (number_input <= number_output)
      invalidateLeadingInfo(number_input);
  }

  public void addSetVar(String name)
  {
    if (name == null)
      throw new polyglot.util.InternalCompilerError("addSetVar");

    CName c = omegaLib.newCName(name);
    number_input++;
    inNames.addElement(c);
  }

  public void addOutputVar(String name)
  {
    if (name == null)
      throw new polyglot.util.InternalCompilerError("addOutputVar");

    CName c = omegaLib.newCName(name);
    number_output++;
    outNames.addElement(c);

    if (number_output <= number_input)
      invalidateLeadingInfo(number_output);
  }

  public void nameSetVar(int nth, String s)
  {
    nameInputVar(nth, s);
  }

  public FAnd andWithAnd()
  {
    if (isSimplified())
      DNFtoFormula();
    relation().finalized = false;
    Formula f = removeFirstChild();
    FAnd a = addAnd();
    a.checkAndAddChild(f);
    return a;
  }

  public EQHandle andWithEQ()
  {
    if (isSimplified())
      DNFtoFormula();
    olAssert(!isShared());  // The relation has been split.
    relation().finalized = false;
    return findAvailableConjunct().addEQ(false);
  }

  public EQHandle andWithEQ(Conjunct c, Equation eq)
  {
    olAssert(c.relation().isSimplified());
    EQHandle H = andWithEQ();
    H.copyConstraint(c, eq);
    return H;
  }

  public GEQHandle andWithGEQ()
  {
    if (isSimplified())
      DNFtoFormula();
    olAssert(!isShared());  // The relation has been split.
    relation().finalized = false;  // We are giving out a handle.
                                // We should evantually implement finalization
                                // of subtrees, so the existing formula cannot
                                // be modified.
    return findAvailableConjunct().addGEQ(false);
  }

  public GEQHandle andWithGEQ(Conjunct c, Equation eq)
  {
    olAssert(c.relation().isSimplified());
    GEQHandle H = andWithGEQ();
    H.copyConstraint(c, eq);
    return H;
  }

  public void print()
  {
    System.out.print(" not ");
    super.print();
  }

  private void print(boolean printSym)
  {
    setupNames();

    System.out.println("{");

    String s = printVariablesToString(printSym);
    System.out.println(s);

    if (simplifiedDNF == null) {
      super.print();
    } else {
      olAssert(numberOfChildren() == 0);
      simplifiedDNF.print();
    }

    System.out.println(" }\n");
  }

  private String printVariablesToString(boolean printSym)
  {
    StringBuffer s = new StringBuffer("");

    if (symbolic.size() != 0) {
      if (printSym)
        s.append(" Sym=[");

      int gvl = symbolic.size();
      for (int i = 0; i < gvl; i++) {
        VarDecl v = (VarDecl) symbolic.elementAt(i);
        if (printSym && (i > 0))
          s.append(",");
        if (printSym)
          s.append(v.name(omegaLib));
//      v.printVarAddrs(s);
      }

      if (printSym)
        s.append("] ");
    }

    if (number_input > 0) {
      s.append("[");
      for (int i = 1; i <= number_input; i++) {
        VarDecl v = omegaLib.newInputVar(i);
        if (i > 1)
          s.append(",");
        s.append(v.name(omegaLib));
//      v.printVarAddrs(s);
      }
      s.append("] ");
    }

    if (number_output > 0) {
      s.append("=> [");
      for (int i = 1; i <= number_output; i++) {
        VarDecl v = omegaLib.newOutputVar(i);
        if (i > 1)
          s.append(",");
        s.append(v.name(omegaLib));
//      v.printVarAddrs(s);
      }
      s.append("] ");
    }

    return s.toString();  
  }

  public void printWithSubs(boolean printSym, boolean newline)
  {
    String s = printWithSubsToString(printSym, newline);
    System.out.println(s);
  }

  public void printWithSubs()
  {
    printWithSubs(false, true);
  }

  public String printWithSubsToString(boolean printSym, boolean newline)
  {
    StringBuffer s = new StringBuffer("");

    if (omegaLib.trace) {
      System.out.println("printWithSubsToString:");
      prefixPrint();
    }

    boolean anythingPrinted = false;
    boolean firstRelation   = true;

    RelBody R   = new RelBody(this);
    Vector  cl  = R.queryDNF().getConjList();
    int     cll = cl.size();
    for (int icl = 0; icl < cll; icl++) {
      Conjunct c = (Conjunct) cl.elementAt(icl);
      RelBody S = new RelBody(R, c);
      Conjunct C = S.getSingleConjunct();

      if (!C.simplifyConjunct(true, 4, Problem.BLACK))
        continue;

      S.setupNames();

      if (! firstRelation) {
        s.append(" union");
        if (newline)
          s.append("\n ");
      } else
        firstRelation = false;

      anythingPrinted = true;

      C.reorderForPrint(false, C.maxUfsArityOfIn(), C.maxUfsArityOfOut(), false);
      C.orderedElimination(S.symbolic.size() + C.maxUfsArityOfIn() + C.maxUfsArityOfOut());

      if (omegaLib.trace)
        S.prefixPrint();

      s.append(S.printSubsToString(C, printSym));
    }

    if (!anythingPrinted) {
      R.setupNames();
      s.setLength(0);
      s.append("{");
      s.append(R.printVariablesToString(printSym));
      if ((R.number_input != 0) || (R.number_output != 0))
        s.append(" :");
      s.append(" FALSE }");
    }

    if (newline)
      s.append("\n");

    return s.toString();
  }

  public String printSubsToString(Conjunct conj, boolean printSym)
  {
    StringBuffer s = new StringBuffer("(");
    /* Do actual printing of Conjunct C as a relation */

    if (printSym) {
      if (symbolic.size() != 0) {
        s.append("Sym = [");

        int sl = symbolic.size();
        for (int j = 0; j < sl; j++) {
          VarDecl v = (VarDecl) symbolic.elementAt(j);
          s.append(v.name(omegaLib));
          if (j < sl - 1)
            s.append(",");
        }
        s.append("] ");
      }
    }

    if (number_input != 0) {
      s.append("[");
      // Print input names with substitutions in them
      for (int i = 1; i <= number_input; i++) {
        VarDecl v   = inputVar(i);
        int     col = conj.findColumn(v);
        if (col != 0)
          s.append(conj.printSubToString(col));
        else
          s.append(v.name(omegaLib));
        if (i < number_input)
          s.append(",");
      }
      s.append("]");
    }
        
    if (!isSet())
      s.append(" . ");

    if (number_output != 0) {
      s.append("[");
        
      // Print output names with substitutions in them
      for (int i = 1; i <= number_output; i++) {
        VarDecl v   = outputVar(i);
        int     col = conj.findColumn(v);
        if (col != 0)
          s.append(conj.printSubToString(col));
        else
          s.append(v.name(omegaLib));
        if (i < number_output)
          s.append(",");
      }
      s.append("] ");
    }
        
    if (isUnknown()) {
      if ((number_input != 0) || (number_output != 0))
        s.append(":");
      s.append(" UNKNOWN");
    } else {

      // Empty conj means TRUE, so don't print colon
      if (!conj.isEmpty()) {
        conj.clearSubs();
        if ((number_input != 0) || (number_output != 0))
          s.append(":");
        s.append(" ");
        s.append(conj.printToString(false));
      } else {
        if ((number_input == 0) && (number_output == 0))
          s.append(" TRUE ");
      }
    }

    s.append("}");

    return s.toString();
  }

  public String printOutputsWithSubsToString()
  {
    RelBody S = new RelBody(this);
    RelBody Q = new RelBody(this);

    S.setupNames();

    Conjunct     C = S.getSingleConjunct();
    Conjunct     D = Q.getSingleConjunct(); // orderedElimination futzes with conj
    StringBuffer s = new StringBuffer("");

    C.reorderForPrint(false, 0, 0, false);
    C.orderedElimination(S.symbolic.size());

    // Print output names with substitutions in them.

    for (int i = 1; i <= S.number_output; i++) {
      VarDecl v   = outputVar(i);
      int     col = C.findColumn(v);
      String  t   = C.printSubToString(col);

      if (col != 0)
        s.append(t);

      if (col == 0 || t.equals(v.name(omegaLib))) // no sub found
        // Assume you can't get a unit coefficient on v, must use div
        s.append(D.tryToPrintVarToStringWithDiv(outputVar(i)));

      if (i < S.number_output)
        s.append(",");
    }

    return s.toString();
  }

  public String printOutputsWithSubsToString(int i)
  {
    RelBody S = new RelBody(this);
    RelBody Q = new RelBody(this);

    S.setupNames();

    Conjunct C = S.getSingleConjunct();
    Conjunct D = Q.getSingleConjunct(); // orderedElimination futzes with conj

    C.reorderForPrint(false, 0, 0, false);
    C.orderedElimination(S.symbolic.size());

    // Print output names with substitutions in them

    VarDecl v   = outputVar(i);
    int     col = C.findColumn(v);
    String  t   = C.printSubToString(col);

    if (col != 0)
      return t;

    if (col == 0 || t.equals(v.name(omegaLib))) // no sub found?
      return D.tryToPrintVarToStringWithDiv(v);

    // should check for failure
    return "";
  }

  public String printFormulaToString()
  {
    setupNames();
    return queryDNF().printToString();
  }

  public void prefixPrint()
  {
    prefixPrint(true);
  }

  public void prefixPrint(boolean debug)
  {
    int old_useUglyNames = omegaLib.useUglyNames;
    omegaLib.useUglyNames = 0;

    setupNames();

    omegaLib.setPrintLevel(0);

//      System.out.print("(0x");
//      System.out.print(Integer.toHexString(hashCode()));
//      System.out.print(")");
    System.out.print(isSet() ? "SET: " : "RELATION: ");
    System.out.println(printVariablesToString(true));

    if (simplifiedDNF == null) {
      super.prefixPrint(debug);
    } else {
      olAssert(numberOfChildren() == 0);
      simplifiedDNF.prefixPrint(debug, true);
    }

    System.out.println("");
    omegaLib.useUglyNames = old_useUglyNames;
  }

  public boolean isSatisfiable()
  {
    boolean ubs = isUpperBoundSatisfiable();
    olAssert(ubs == isUpperBoundSatisfiable());
    return ubs;
  }

  public boolean isNotSatisfiable()
  {
    if (!isLowerBoundSatisfiable())
      return true;
    return isUpperBoundDefinitelyNotSatisfiable();
  }

  /**
   * Check if there exist any exact conjuncts in the solution.
   * Interpret UNKNOWN as false, then check satisfiability.
   */
  public boolean isLowerBoundSatisfiable()
  {
    int tmp = omegaLib.sRdtConstraints;
    omegaLib.sRdtConstraints = -1;
    simplify();
    omegaLib.sRdtConstraints = tmp;

    return simplifiedDNF.isLowerBoundSatisfiable();
  }

  /**
   * Check if the formula simplifies to FALSE, since the library
   * will never say that if the *known* constraints are unsatisfiable by 
   * themselves.  Interpret UNKNOWN as true, then check satisfiability.
  */
  public boolean isUpperBoundSatisfiable()
  {
    int tmp = omegaLib.sRdtConstraints;
    omegaLib.sRdtConstraints = -1;
    simplify();
    omegaLib.sRdtConstraints = tmp;
    return !simplifiedDNF.isDefinitelyFalse();
  }

  /**
   * Check if the formula simplifies to FALSE.
   */
  public boolean isUpperBoundDefinitelyNotSatisfiable()
  {
    int tmp = omegaLib.sRdtConstraints;
    omegaLib.sRdtConstraints = -1;
    simplify();
    omegaLib.sRdtConstraints = tmp;
    return simplifiedDNF.isDefinitelyFalse();
  }

  /**
   * Check if we can easily determine if the formula evaluates to true.
   */
  public boolean isObviousTautology()
  {
    int tmp = omegaLib.sRdtConstraints;
    omegaLib.sRdtConstraints = 0;
    simplify();
    omegaLib.sRdtConstraints = tmp;
    return simplifiedDNF.isDefinitelyTrue();
  }

  public boolean isUnknown()
  {
    simplify();
    return (hasSingleConjunct() && getSingleConjunct().isUnknown());
  }

  public DNF queryDNF()
  {
    return queryDNF(0, 0);
  }

  public DNF queryDNF(int rdt_conjs, int rdt_constrs)
  {
    simplify(rdt_conjs, rdt_constrs);
    return simplifiedDNF;
  }

  public void simplify()
  {
    simplify(0, 0);
  }

  public void simplify(int rdt_conjs, int rdt_constrs)
  {
   if (simplifiedDNF == null) {
      finalized = true;
      if (numberOfChildren() == 0) {
        simplifiedDNF = new DNF(omegaLib);
      } else {
        olAssert(numberOfChildren() == 1);
        if (omegaLib.trace) {
          System.out.print("=== Rel_Body::simplify(");
          System.out.print(rdt_conjs);
          System.out.print(", ");
          System.out.print(rdt_constrs);
          System.out.print(") Input tree (");
          System.out.print(r_conjs);
          System.out.println(") ===");
          prefixPrint();
        }

        verifytree();

        beautify();
        verifytree();

        rearrange();
        verifytree();

        beautify();
        verifytree();

        omegaLib.sRdtConstraints = rdt_constrs;
        if (omegaLib.trace) {
          System.out.println("\n=== In simplify, before DNFize ===");
          prefixPrint();
        }
        DNFize();
        if (omegaLib.trace) {
          System.out.println("\n=== In simplify, after DNFize ===");
          prefixPrint();
        }
        verifytree();

        simplifiedDNF.rmRedundantInexactConjs();
        verifytree();

        if ((rdt_conjs > 0) && !simplifiedDNF.isDefinitelyFalse() && (simplifiedDNF.length() > 1)) {
          simplifiedDNF.rmRedundantConjs(rdt_conjs - 1);
          verifytree();
        }

        if (omegaLib.trace) {
          System.out.println ( "\n=== Resulting Relation ===");
          prefixPrint();
        }
      }
    } else {
      /* Reprocess DNF to get rid of redundant stuff */

      if (rdt_constrs < 0)
        return;

      simplifiedDNF.rmRedundantInexactConjs();

      if (rdt_conjs > r_conjs) {
        if (omegaLib.trace) 
          System.out.println("=== simplify() redundant CONJUNCTS ===");
        simplifiedDNF.rmRedundantConjs(rdt_conjs - 1);
      }

      if (rdt_constrs > 0) {
        if (omegaLib.trace) 
          System.out.println("=== simplify() redundant CONSTR-S ===");
        omegaLib.sRdtConstraints = rdt_constrs;
        simplifiedDNF.simplify(rdt_constrs);
      }
    }
 
    r_conjs = rdt_conjs;

    simplifiedDNF.setParentRel(this);
  }

  public boolean isFinalized()
  {
    return finalized;
  }

  public boolean isShared()
  {
    return ref_count > 1;
  }

  public boolean queryDifference(VarDecl v1, VarDecl v2, int[] bounds)
  {
    simplify();
    return simplifiedDNF.queryDifference(v1, v2, bounds);
  }

  public void queryVariableBounds(VarDecl v, int[] bounds)
  {
    simplify();
    simplifiedDNF.queryVariableBounds(v, bounds);
  }

  public void copyNames(RelBody r)
  {
    if (isSet()) {
      for (int t = 1; t <= r.numberSet(); t++) 
        nameSetVar(t, r.setVar(t).baseName());
    } else {
      for (int t = 1; t <= r.numberInput(); t++) 
        nameInputVar(t, r.inputVar(t).baseName());
      for (int t = 1; t <= r.numberOutput(); t++) 
        nameOutputVar(t, r.outputVar(t).baseName());
    }
  }

  /**
   * If direction==0, move all conjuncts with >= level leading 0's to return
   *            else  move all conjuncts with level-1 0's followed by
   *                the appropriate signed difference to returned Relation.
   */
  public RelBody extractDNFByCarriedLevel(int level, int direction)
  {
    RelBody r = this;
    if (r.ref_count > 1)
      r = new RelBody(r);

    if (r.simplifiedDNF == null)
      r.DNFize();

    olAssert((r.simplifiedDNF != null) && (r.numberOfChildren() == 0));

    r.simplifiedDNF.makeLevelCarriedTo(level);

    RelBody extracted = new RelBody(omegaLib, r.numberInput(), r.numberOutput());

    extracted.copyNames(r);

    olAssert(extracted.numberOfChildren() == 0);
    olAssert(extracted.simplifiedDNF == null);

    extracted.simplifiedDNF = new DNF(omegaLib);
    extracted.symbolic = (Vector) r.symbolic.clone();

    DNF remaining = new DNF(omegaLib);

    for (Conjunct curr = r.simplifiedDNF.removeFirstConjunct();
         curr != null;
         curr = r.simplifiedDNF.removeFirstConjunct()) {

      if (!curr.checkLeading0s(level))
        throw new polyglot.util.InternalCompilerError("leading 0s");

      if (omegaLib.trace)
        curr.assertLeadingInfo();

      if (curr.checkLeading0s(direction, level)) {
        extracted.simplifiedDNF.addConjunct(curr);
      } else {
        remaining.addConjunct(curr);
      }
    }

    r.simplifiedDNF = remaining;

    if (omegaLib.trace) {
      simplifiedDNF.assertLeadingInfo();
      extracted.simplifiedDNF.assertLeadingInfo();
    }

    r.setFinalized();
    extracted.setFinalized();
 
    return extracted;
  }

  public RelBody makeLevelCarriedTo(int level)
  {
    RelBody r = this;
    if (ref_count > 1)
      r = new RelBody(this);

    if (r.simplifiedDNF == null)
      r.DNFize();

    olAssert((r.simplifiedDNF != null) && (r.numberOfChildren() == 0));

    r.simplifiedDNF.makeLevelCarriedTo(level);
    return r;
  }

  public void setupNames()
  {
    if (omegaLib.useUglyNames != 0)
      return;

    if (false && omegaLib.trace)
      System.out.println("Setting up names for " +  this);

    for (int i = 1; i <= numberInput(); i++)
      omegaLib.newInputVar(i).nameVariable(((CName) inNames.elementAt(i)).name());

    for (int i = 1; i <= numberOutput(); i++)
      omegaLib.newOutputVar(i).nameVariable(((CName) outNames.elementAt(i)).name());

    HashSet gbls = new HashSet(11);

    omegaLib.wildCardInstanceNumber = 0;

    for (int i = 0; i < symbolic.size(); i++) {
      VarDecl s = (VarDecl) symbolic.elementAt(i);
      gbls.add(s.getGlobalVar());
    }

    Iterator eg = gbls.iterator();
    while (eg.hasNext()) {
      GlobalVarDecl g  = (GlobalVarDecl) eg.next();
      String        bn = g.getBaseName();

      if (bn == null)
        continue;
 
      CName c = omegaLib.newCName(bn);
      g.setInstance(c.increment());
    }

    for (int i = 1; i <= numberInput(); i++)
      omegaLib.increment(inputVar(i));

    for (int i = 1; i <= numberOutput(); i++)
      omegaLib.increment(outputVar(i));

    if (simplifiedDNF != null)  // It is simplified
      simplifiedDNF.setupNames();
    else                         // not simplified
      super.setupNames();

    for (int i = 1; i <= numberOutput(); i++)
      omegaLib.decrement(outputVar(i));

    for (int i = 1; i <= numberInput(); i++)
      omegaLib.decrement(inputVar(i));

    Iterator it = gbls.iterator();
    while (it.hasNext()) {
      GlobalVarDecl g = (GlobalVarDecl) it.next();
      omegaLib.decrement(g.getBaseName());
    }
  }

  public int unknownUses()
  {
    if (!isSimplified())
      simplify();
    
    return simplifiedDNF.localStatus();
  }

  public boolean isExact()
  {
    if (!isSimplified())
      simplify();
    
    return simplifiedDNF.isExact();
  }

  public boolean isSimplified()
  {
    return ((simplifiedDNF != null) && (numberOfChildren() == 0));
  }

  public Conjunct removeFirstConjunct()
  {
    simplify();
    return simplifiedDNF.removeFirstConjunct();
  }

  public Conjunct getSingleConjunct()
  {
    simplify();
    return simplifiedDNF.getSingleConjunct();
  }

  public boolean hasSingleConjunct()
  {
    simplify();
    return simplifiedDNF.hasSingleConjunct();
  }

  public void beautify()
  {
    if (numberOfChildren() != 1)
      throw new polyglot.util.InternalCompilerError("Length of children");

    setParent(null, this);

    omegaLib.skipFinalizationCheck++;
    getFirstChild().beautify();

    Formula child = getFirstChild();
    if ((child.nodeType() == OP_AND) && (child.numberOfChildren() == 0)) {
      removeChild(child);
      child.delete();
      addConjunct();
    }

    omegaLib.skipFinalizationCheck--;

    if (omegaLib.trace) {
      System.out.println("\n=== Beautified TREE ===");
      prefixPrint();
    }

    if (numberOfChildren() != 1)
      throw new polyglot.util.InternalCompilerError("Length of children 2");
  }

  public void rearrange()
  {
    olAssert(numberOfChildren() == 1);

    omegaLib.skipFinalizationCheck++;
    getFirstChild().rearrange();
    omegaLib.skipFinalizationCheck--;

    if (omegaLib.trace) {
      System.out.println("\n=== Rearranged TREE ===");
      prefixPrint();
    }
  }

  public void interpretUnknownAsTrue()
  {
    simplify();
    simplifiedDNF.interpretUnknownAsTrue();
  }

  public void interpretUnknownAsFalse()
  {
    simplify();
    simplifiedDNF.removeInexactConjunct();  
  }

  public boolean canAddChild()
  {
    return numberOfChildren() < 1;
  }

  public void reverseLeadingDirInfo()
  {
    if (isSimplified()) {
      simplifiedDNF.reverseLeadingDirInfo();
      return;
    }

    if ((simplifiedDNF != null) || (numberOfChildren() != 1))
        throw new polyglot.util.InternalCompilerError("Num children = " + numberOfChildren());

    ((Formula) children().elementAt(0)).reverseLeadingDirInfo();
  }

  public void invalidateLeadingInfo(int changed) 
  {
    super.invalidateLeadingInfo(changed);
  }

  public void enforceLeadingInfo(int guaranteed, int possible, int dir)
  { 
    super.enforceLeadingInfo(guaranteed, possible, dir); 
  }
  // re-declare this so that Relation (a friend) can call it

  public DNF DNFize()
  {
    if (simplifiedDNF == null) {
      simplifiedDNF = ((Formula) children().remove(0)).DNFize();

      int mua = maxSharedUfsArity();
      if (mua > 0) {
        if (omegaLib.trace) {
          System.out.println("\n=== In DNFize, before LCDNF ===");
          prefixPrint();
        }

        simplifiedDNF.makeLevelCarriedTo(mua);
      }

      if (omegaLib.trace) {
        System.out.println("\n=== In DNFize, before verify ===");
        prefixPrint();
      }

      simplifiedDNF.simplify(omegaLib.sRdtConstraints);
    }

    olAssert(numberOfChildren() == 0);

    return simplifiedDNF;
  }

  public void DNFtoFormula()
  {
    if (simplifiedDNF != null) {
      simplifiedDNF.DNFtoFormula(this);
      simplifiedDNF = null;
    }
  }
    
  public Conjunct findAvailableConjunct()
  {
    if (numberOfChildren() == 0)
      return addConjunct();

    olAssert(numberOfChildren() == 1);

    Formula  kid = (Formula) children().elementAt(0);  // RelBodies have only one child
    Conjunct c   = kid.findAvailableConjunct();

    if (c == null) {
      removeChild(kid);
      FAnd a = addAnd();
      a.checkAndAddChild(kid);
      c = a.addConjunct();
    }

    return c;
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

  /**
   * Build lists of variables that need to be replaced in the given 
   * Formula.  Declare globals in new relation.  Then call
   * map_vars to do the replacements.
   * <p>
   * Obnoxiously many arguments here:
   * Relation arguments contain declarations of symbolic and in/out vars.
   * FExists argument is where needed existentially quant. vars can be decl.
   * <p>
   * Mapping specifies how in/out vars are mapped
   * Two lists are required to be able to map in/out variables from the first
   * and second relations to the same existentially quantified variable.
   */
  public boolean align(RelBody newr, FExists fe, Formula f, Mapping mapping)
  {
    boolean newrIsSet       = false;
    boolean input_remapped  = false;
    boolean output_remapped = false;
    boolean sym_remapped    = false;

    f.setRelation(newr);  // Might not need to do this anymore, if bugs were fixed
    omegaLib.skipSetChecks++;

    // MAP old input variables by setting their remap fields.

    for (int i = 1; i <= numberInput(); i++) { 
      VarDecl this_var  = inputVar(i);
      String  this_name = ((CName) inNames.elementAt(i)).name();

      switch (mapping.getMapInKind(i)) {
      case VarDecl.INPUT_VAR:
      case VarDecl.SET_VAR:
        if (mapping.getMapInKind(i) == VarDecl.SET_VAR)
          newrIsSet = true;  // Don't mark it just yet; we still need to refer to its "input" vars internally.

        olAssert((newrIsSet && mapping.getMapInKind(i) == VarDecl.SET_VAR) || ((!newrIsSet &&mapping.getMapInKind(i) == VarDecl.INPUT_VAR)));

        int     new_pos = mapping.getMapInPos(i);
        VarDecl new_var = newr.inputVar(new_pos);

        if (this_var != new_var) {
          input_remapped = true;
          this_var.resetRemapField(new_var);
        }

        String new_name = ((CName) newr.inNames.elementAt(new_pos)).name();
        if (this_name != null) {             // should we name this?
          if (new_name != null) {            // already named, anonymize
            if (!new_name.equals(this_name))
              newr.nameInputVar(new_pos, null);
          } else
            newr.nameInputVar(new_pos, this_name);
        }
        break;

      case VarDecl.OUTPUT_VAR:
        olAssert(!newr.isSet());
        input_remapped = true;
        int     new_poso  = mapping.getMapInPos(i);
        VarDecl new_varo  = newr.outputVar(new_poso);
        String  new_nameo = ((CName) newr.outNames.elementAt(new_poso)).name();
        this_var.resetRemapField(new_varo);
        if (this_name != null) {
          if (new_nameo != null) { // already named, anonymize
            if (!new_nameo.equals(this_name))
              newr.nameOutputVar(new_poso, null);
          } else
            newr.nameOutputVar(new_poso, this_name);
        }
        break;

      case VarDecl.EXISTS_VAR:
        input_remapped = true;
        int cur_ex = omegaLib.getPosIDS(mapping.getMapInPos(i));
        // check if we have declared it, use that if so.
        // create it if not.  
        if ((mapping.getMapInPos(i) <= 0) || (cur_ex == 0)) {
          VarDecl New_E;
          if (this_name != null)
            New_E = fe.declare(this_name);
          else
            New_E = fe.declare();
          this_var.resetRemapField(New_E);
          if (mapping.getMapInPos(i) > 0) {
            omegaLib.appendIDS(New_E, mapping.getMapInPos(i));
          }
        } else {
          VarDecl new_vare = omegaLib.getVarIDS(cur_ex);
          this_var.resetRemapField(new_vare);
          if (this_name != null) { // Have we already assigned a name?
            String bn = new_vare.baseName();
            if (bn != null) {
              if (!bn.equals(this_name))
                new_vare.nameVariable(null);
            } else {
              new_vare.nameVariable(this_name);
              olAssert(this_name != null);
            }
          }
        }
        break;
      default:
        throw new polyglot.util.InternalCompilerError("Unsupported var type in align");
      }
    }

    //  MAP old output variables.

    for (int i = 1; i <= number_output; i++) {   
      VarDecl this_var  = outputVar(i);
      String  this_name = ((CName) outNames.elementAt(i)).name();

      switch (mapping.getMapOutKind(i)) {
      case VarDecl.INPUT_VAR:
      case VarDecl.SET_VAR:
        if (mapping.getMapOutKind(i) == VarDecl.SET_VAR)
          newrIsSet = true;  // Don't mark it just yet; we still need to 
        // refer to its "input" vars internally
                
        olAssert((newrIsSet && (mapping.getMapOutKind(i) == VarDecl.SET_VAR)) || ((!newrIsSet && (mapping.getMapOutKind(i) == VarDecl.INPUT_VAR))));

        output_remapped = true;
        int     new_pos  = mapping.getMapOutPos(i);
        VarDecl new_var  = newr.inputVar(new_pos);
        String  new_name = ((CName) newr.inNames.elementAt(new_pos)).name();
        this_var.resetRemapField(new_var);
        if (this_name != null) {
          if (new_name != null) {    // already named, anonymize
            if (!new_name.equals(this_name))
              newr.nameInputVar(new_pos, null);
          } else
            newr.nameInputVar(new_pos, this_name);
        }
        break;

      case VarDecl.OUTPUT_VAR:
        olAssert(!newr.isSet());
        int     new_poso = mapping.getMapOutPos(i);
        VarDecl new_varo = newr.outputVar(new_poso);
        if (new_varo != this_var) {
          output_remapped = true;
          this_var.resetRemapField(new_varo);
        }
        String new_nameo = ((CName) newr.outNames.elementAt(new_poso)).name();
        if (this_name != null) {
          if (new_nameo != null) {    // already named, anonymize
            if (!new_nameo.equals(this_name))
              newr.nameOutputVar(new_poso, null);
          } else
            newr.nameOutputVar(new_poso, this_name);
        }
        break;

      case VarDecl.EXISTS_VAR:
        // check if we have declared it, create it if not.  
        output_remapped = true;
        int cur_ex = omegaLib.getPosIDS(mapping.getMapOutPos(i));
        if ((mapping.getMapOutPos(i) <= 0) || (cur_ex == 0)) {   // Declare it.
          VarDecl New_E = fe.declare(this_name);
          this_var.resetRemapField(New_E);
          if (mapping.getMapOutPos(i) > 0)
            omegaLib.appendIDS(New_E, mapping.getMapOutPos(i));
        } else {
          VarDecl new_vare = omegaLib.getVarIDS(cur_ex);
          this_var.resetRemapField(new_vare);
          if (this_name != null) {
            String bn = new_vare.baseName();
            if (bn != null) {
              if (!bn.equals(this_name))
                new_vare.nameVariable(null); 
            } else {
              new_vare.nameVariable(this_name);
            }
          }
        }
        break;
      default:
        throw new polyglot.util.InternalCompilerError("Unsupported var type in align");
      }
    }

    Vector oldSym = symbolic;
    int    l      = oldSym.size();
    for (int i = 1; i <= l; i++) {
      VarDecl v = (VarDecl) oldSym.elementAt(i - 1);

      olAssert(v.kind() == VarDecl.GLOBAL_VAR);

      GlobalVarDecl gv = v.getGlobalVar();
      int gva = gv.arity();
      if (gva > 0) {
        int new_of = v.functionOf();

        if (!omegaLib.leavePufsUntouched) 
          new_of = mapping.getTupleFate(new_of, gva);

        if (new_of == VarDecl.UNKNOWN_TUPLE) {
          // hopefully v is not really used
          // if we get here, f should have been in DNF,
          //                 now an OR node with conjuncts below
          // we just need to check that no conjunct uses v

          if ((f.nodeType() == Formula.OP_CONJUNCT) && (f.reallyConjunct().variables().indexOf(v) != 0))
            throw new polyglot.util.InternalCompilerError("v unused");

          // Since its not really used, don't bother adding it to
          // the the global_vars list of the new relation.

          continue;
        }

        if (v.functionOf() != new_of) {
          VarDecl new_v = newr.getLocal(gv, new_of);
          olAssert(v != new_v);
          v.resetRemapField(new_v);
          sym_remapped = true;
        } else { // add symbolic to symbolic list
          VarDecl new_v = newr.getLocal(gv, v.functionOf());

          olAssert(v == new_v);
        }
      } else { // add symbolic to symbolic list
        VarDecl new_v = newr.getLocal(gv);
        olAssert(v == new_v);
      }
    }

    if (sym_remapped || input_remapped || output_remapped) {
      f.remap();

      // If 2 vars mapped to same variable, combine them.
      // There's a column to combine only when there are two equal remap fields.

      HashSet vt      = new HashSet(11);
      boolean combine = false;
      for (int i = 1; !combine && i <= numberInput(); i++) {
        VarDecl t = (VarDecl) inputVar(i);
        if (!vt.add(t.getRemap()))
          combine = true;
      }

      for (int i = 1; !combine && i <= number_output; i++) {
        VarDecl t2 = (VarDecl) outputVar(i);
        if (!vt.add(t2.getRemap()))
          combine = true;
      }

      if (combine)
        f.combineColumns(); 

      if (sym_remapped) 
        VarDecl.resetRemapField(symbolic);
      if (input_remapped) 
        VarDecl.resetRemapField(omegaLib.inputVars(), numberInput());
      if (output_remapped) 
        VarDecl.resetRemapField(omegaLib.outputVars(), numberOutput());
    }

    omegaLib.skipSetChecks--;

    if (fe != null) {
      int fel = fe.myLocals.size();
      for (int i = 0; i < fel; i++) {
        VarDecl v = (VarDecl) fe.myLocals.elementAt(i);
        olAssert(v == v.getRemap());
      }
    }

    return newrIsSet;
  }

  public RelBody approximate(boolean strides_allowed)
  {
    RelBody r = this;
    if (r.ref_count > 1)
      r = new RelBody(r);

    r.simplify(-1, -1);

    if (omegaLib.trace) {
      System.out.println("Computing approximation ");
      if (strides_allowed)
        System.out.println("with strides allowed ");
      System.out.println("[ ");
      r.prefixPrint();
    }

    omegaLib.useUglyNames++; 
    r.simplifiedDNF.reorderAndSimplify(r, strides_allowed);
 
    if (omegaLib.trace) 
      System.out.println("] done Computing approximation");

    omegaLib.useUglyNames--; 

    return r;
  }

  /**
   * Variables in DNF of map_rel reference declarations of map_rel (or not).
   * remapDNFVars makes them to reference declarations of ref_rel.
   * Ref_rel can get new global variable declarations in the process.
   */
  public void remapDNFVars(RelBody ref_rel)
  {
    olAssert(simplifiedDNF != null);
    olAssert(ref_rel.simplifiedDNF != null);

    omegaLib.skipSetChecks++;

    simplifiedDNF.remapDNFVars(ref_rel);

    omegaLib.skipSetChecks--;
  }

  /**
   * Compute (gist r1 given r2).
   * Currently we assume that r2 has only one conjunct.
   * r2 may have zero input and output OR may have # in/out vars equal to r1.
   */
  private boolean gistSingleConjunctSub(RelBody r2, int effort)
  {
    //  The merged conjunct has to have the variables of either r1 or r2, but
    //  not both. Use r1's, since it'll be cheaper to remap r2's single conj.

    RelBody r1 = this;

    omegaLib.skipSetChecks++;

    r2.remapDNFVars(r1);
    if (!r2.isUpperBoundSatisfiable())
      throw new polyglot.util.InternalCompilerError("Gist: second operand is FALSE");

    omegaLib.skipSetChecks--;

    Conjunct known = r2.getSingleConjunct();
    if (known == null)
      throw new polyglot.util.InternalCompilerError("Gist: second operand has more than 1 conjunct");

    DNF new_dnf = r1.simplifiedDNF.gistSingleConjunct(known, r1, effort);

    if (new_dnf == null)
      return false;

    r1.simplifiedDNF = new_dnf;

    return true;
  }

  private RelBody MapRel1(Mapping map, boolean isAndNot, int number_input, int number_output, boolean invalidate_resulting_leading_info, boolean finalize)
  {
    map.getRelationArityFromOneMapping();

    RelBody outputRelBody = new RelBody(omegaLib, (number_input == -1) ? map.getInReq() : number_input, (number_output == -1) ? map.getOutReq() : number_output);

    DNFtoFormula();

    Formula f1 = removeFirstChild();
    FExists fe = null;
    Formula f  = outputRelBody;

    if (map.hasExistentials()) {
      fe = outputRelBody.addExists();
      f = fe;
    }

    omegaLib.and_below_exists = null;

    if (!finalize) {
      f = f.addAnd();
      omegaLib.and_below_exists = (FAnd) f;
    }

    if (isAndNot)
      f = f.addNot();

    f.checkAndAddChild(f1);

    omegaLib.clearIDS();

    boolean returnAsSet = align(outputRelBody, fe, f1, map);
    if (returnAsSet || (isSet() && outputRelBody.number_output == 0)) {
      outputRelBody.setIsSet(true);
      outputRelBody.invalidateLeadingInfo(-1);
    }

    if (finalize)
      outputRelBody.setFinalized();

    if (invalidate_resulting_leading_info)
      outputRelBody.invalidateLeadingInfo(-1);

    return outputRelBody;
  }

  public RelBody after(int carried_by, int new_output, int dir)
  {
    RelBody r = new RelBody(this);
    olAssert(!r.isSet());

    int a                   = r.maxUfsArityOfOut();
    int preserved_positions = min(carried_by - 1, new_output);

    if (a >= preserved_positions) { // UFS's must evacuate from the output tuple
      r.simplify();
      DNF d = r.DNFize();
      d.countLeadingZeros();

      // Any conjucts with leading_0s == -1 must have >= "a" leading 0s
      // What a gross way to do  Ferd

      d.after(r, preserved_positions);
    }

    Mapping m1 = new Mapping(r.numberInput(), r.number_output);
    for (int i = 1; i <= r.numberInput(); i++)
      m1.setMap_in (i, VarDecl.INPUT_VAR, i);

    if (carried_by > new_output) {
      int preserve = min(new_output, r.number_output);
      for (int i = 1; i <= preserve; i++)
        m1.setMap_out(i, VarDecl.OUTPUT_VAR, i);
      for (int i = preserve + 1; i <= r.number_output; i++)
        m1.setMap_out(i, VarDecl.EXISTS_VAR, -1);

      r.MapRel1(m1, false, -1, -1, true, true);

      if (new_output > preserve) {
        for (int i = 1; i <= new_output - r.number_output; i++)
          r.addOutputVar("");
      }
      return r;
    }

    for (int i = 1; i < carried_by; i++)
      m1.setMap_out(i, VarDecl.OUTPUT_VAR, i);

    m1.setMap_out(carried_by, VarDecl.EXISTS_VAR, 1);

    for (int i = carried_by + 1; i <= r.number_output; i++)
      m1.setMap_out(i, VarDecl.EXISTS_VAR,-1);

    r = r.MapRel1(m1, false, -1, -1, true, false);

    GEQHandle h = omegaLib.and_below_exists.addGEQ(false);
    olAssert(carried_by < 128);
    h.updateCoefficient((VarDecl) omegaLib.getVarIDS(1), -dir);
    h.updateCoefficient(r.outputVar(carried_by), dir);
    h.updateConstant(-1);

    if (new_output > r.number_output)
      r.addOutputVar("");

    return r;
  }

  /**
   * Project away all input and output variables.
   */
  public RelBody projectOnSym(RelBody input_context)
  {
    omegaLib.skipSetChecks++;
    boolean slpu = omegaLib.leavePufsUntouched;
    omegaLib.leavePufsUntouched = true;

    RelBody r         = this;
    int     in_arity  = r.maxUfsArityOfIn();
    int     out_arity = r.maxUfsArityOfOut();
    int     no_inp    = r.number_input;
    int     no_out    = r.number_output;
    Mapping M         = new Mapping(no_inp, no_out);

    for (int i = 1; i <= no_inp; i++) // project out input variables
      M.setMap(VarDecl.INPUT_VAR, i, VarDecl.EXISTS_VAR, i);

    for (int i = 1; i <= no_out; i++) // project out output variables
      M.setMap(VarDecl.OUTPUT_VAR, i, VarDecl.EXISTS_VAR, no_inp + i);

    r = r.MapRel1(M, false, 0, 0, true, true);

    for (int i = 1; i <= in_arity; i++)
      r.addInputVar("");

    for (int i = 1; i <= out_arity; i++)
      r.addOutputVar("");

    int d = min(in_arity, out_arity);
    if ((d != 0) && (input_context != null)) {
      RelBody context = input_context;
      int     g       = min(d, context.queryGuaranteedLeadingZeros());
      int     p       = min(d, context.queryPossibleLeadingZeros());
      int     dir     = context.queryLeadingDir();
      r.enforceLeadingInfo(g, p, dir);
    }

    omegaLib.leavePufsUntouched = slpu;
    omegaLib.skipSetChecks--;

    if (omegaLib.trace) {
      System.out.print("\nProjecting onto symbolic (");
      System.out.print(in_arity);
      System.out.print(",");
      System.out.print(out_arity);
      System.out.println("):");
      r.prefixPrint();
    }

    return r;
  }

  /**
   * Domain and Range.
   * Make output (input) variables wildcards and simplify.
   * Move all UFS's to have have the remaining tuple as an argument,
   *   and maprel will move them to the set tuple
   * RESET all leading 0's
   */
  public RelBody domain()
  {
    olAssert(!isSet());

    RelBody r = this;
    if (r.ref_count > 1)
      r = new RelBody(r);

    omegaLib.skipSetChecks++;

    Mapping m1 = new Mapping(number_input, number_output);
    for (int i = 1; i <= number_input; i++)
      m1.setMap_in(i, VarDecl.SET_VAR, i);
    for (int i = 1; i <= number_output; i++)
      m1.setMap_out(i, VarDecl.EXISTS_VAR, i);

    omegaLib.skipSetChecks--;

    int a = r.maxUfsArityOfOut();
    if (a > 0) {
      // UFS's must evacuate from the output tuple

      r.simplify();

      DNF d = r.DNFize();
      d.countLeadingZeros();

      // Any conjucts with leading_0s == -1 must have >= "a" leading 0s
      // What a gross way to do  Ferd

      d.domain(r, a);
    }

    r = r.MapRel1(m1, false, -1, -1, true, true); //  this invalidates leading0s
    olAssert(r.isSet() || (m1.numberInput() == 0));  // MapRel can't tell to make a set
    r.setIsSet(true);
    r.invalidateLeadingInfo(-1);

    omegaLib.skipSetChecks++;
    olAssert((r.queryGuaranteedLeadingZeros() == -1) && (r.queryPossibleLeadingZeros() == -1));
    omegaLib.skipSetChecks--;

    return r;
  }

  public RelBody range()
  {
    RelBody r = this;
    if (r.ref_count > 1)
      r = new RelBody(r);

    omegaLib.skipSetChecks++;

    Mapping m1 = new Mapping(number_input, number_output);
    for (int i = 1; i <= number_input; i++)
      m1.setMap_in (i, VarDecl.EXISTS_VAR, i);
    for (int i = 1; i <= number_output; i++)
      m1.setMap_out(i, VarDecl.SET_VAR, i);
    omegaLib.skipSetChecks--;

    int a = r.maxUfsArityOfIn();
    if (a > 0) {
      // UFS's must evacuate from the input tuple

      r.simplify();
      DNF d = r.DNFize();
      d.countLeadingZeros();
      // Any conjucts with leading_0s == -1 must have >= "a" leading 0s
      // What a gross way to do  Ferd

      d.range(r, a);
    }

    r = r.MapRel1(m1, false, -1, -1, true, true); // this invalidates leading0s
    olAssert(r.isSet() || m1.numberOutput() == 0); // MapRel can't tell to make a set
    r.setIsSet(true);                        // if there were no outputs.
    r.invalidateLeadingInfo(-1);

    omegaLib.skipSetChecks++;
    olAssert((r.queryGuaranteedLeadingZeros() == -1) && (r.queryPossibleLeadingZeros() == -1));
    omegaLib.skipSetChecks--;

    return r;
  }

  /**
   * Inverse F -reverse the input and output tuples.
   */
  public RelBody inverse()
  {
    RelBody r = this;
    if (r.ref_count > 1)
      r = new RelBody(r);

    olAssert(!r.isSet());
 
    Mapping m1 = new Mapping(number_input, number_output);
    for (int i = 1; i <= number_input; i++)
      m1.setMap_in(i, VarDecl.OUTPUT_VAR, i);
    for (int i = 1; i <= number_output; i++)
      m1.setMap_out(i, VarDecl.INPUT_VAR, i);

    r = r.MapRel1(m1, false, -1, -1, false, true);

    r.reverseLeadingDirInfo();

    return r;
  }

  /**
   * Deltas(F)
   *   Return a set such that the ith variable is old Out_i - In_i
   *   Delta variables are created as input variables.
   *   Then input and output variables are projected out.
   */
  public RelBody deltas()
  {
    olAssert(number_input == number_output);
    return deltas(number_input);
  }

  public RelBody deltas(int eq_no)
  {
    RelBody r = this;
    if (r.ref_count > 1)
      r = new RelBody(r);

    omegaLib.skipSetChecks++;

    olAssert(eq_no <= number_input);
    olAssert(eq_no <= number_output);

    int no_inp = number_input;
    int no_out = number_output;

    if (omegaLib.trace) {
      System.out.println("Computing Deltas:");
      r.prefixPrint();
    }

    int a = r.maxUfsArity();
    if (a > 0) {
      // UFS's must evacuate from all tuples - we need to go to DNF
      // to enumerate the variables, I think...
      r.simplify();
      if (omegaLib.trace) {
        System.out.println("Relation simplified:");
        r.prefixPrint();
      }

      DNF    d   = r.DNFize();
      d.deltas();
    }

    for (int i = 1; i <= eq_no; i++)
      r.addInputVar("");

    Mapping M = new Mapping(no_inp + eq_no, no_out);

    for (int i = 1; i <= eq_no; i++) {          // Set up Deltas equalities
      EQHandle E = r.andWithEQ();
      /* delta_i - w_i + r_i = 0 */
      E.updateCoefficient(r.inputVar(i), 1);
      E.updateCoefficient(r.outputVar(i), -1);
      E.updateCoefficient(r.inputVar(no_inp+i), 1);
      M.setMap(VarDecl.INPUT_VAR, no_inp+i, VarDecl.SET_VAR, i);  // Result will be a set
    }

    for (int i = 1; i <= no_inp; i++) // project out input variables
      M.setMap(VarDecl.INPUT_VAR, i, VarDecl.EXISTS_VAR, i);

    for (int i = 1; i <= no_out; i++) // project out output variables
      M.setMap(VarDecl.OUTPUT_VAR, i, VarDecl.EXISTS_VAR, no_inp+i);

    r = r.MapRel1(M, false, eq_no, 0, true, true);

    if (omegaLib.trace) {
      System.out.println("Computing deltas:");
      r.prefixPrint();
    };

    olAssert(r.isSet());  // Should be since we map things to VarDecl.SET_VAR
    olAssert(r.numberSet() == eq_no);
    omegaLib.skipSetChecks--;

    return r;
  }

  public RelBody deltasToRelation(int n_inputs, int n_outputs)
  {
    RelBody r = this;
    if (r.ref_count > 1)
      r = new RelBody(r);

    omegaLib.skipSetChecks++;

    r.setIsSet(false);

    int common = numberInput();

    olAssert(common <= n_inputs);
    olAssert(common <= n_outputs);

    if (r.maxUfsArity() > 0)
      throw new polyglot.util.InternalCompilerError("\"Deltas\" not ready for UFS yet"); // FERD

    for (int i = 1; i <= n_inputs; i++)
      r.addInputVar("");

    for (int i = 1; i <= n_outputs; i++)
      r.addOutputVar("");

    Mapping M = new Mapping(common + n_inputs, n_outputs);

    for (int i = 1; i <= common; i++) {          // Set up Deltas equalities
      EQHandle E = r.andWithEQ();
      /* delta_i - w_i + r_i = 0 */
      E.updateCoefficient(r.inputVar(i), 1);
      E.updateCoefficient(r.outputVar(i), -1);
      E.updateCoefficient(r.inputVar(common + i), 1);
      M.setMap(VarDecl.INPUT_VAR, i, VarDecl.EXISTS_VAR, i);  // Result will be a set
    }

    for (int i = 1; i <= n_inputs; i++) // project out input variables
      M.setMap(VarDecl.INPUT_VAR, common+i, VarDecl.INPUT_VAR, i);

    for (int i = 1; i<=n_outputs; i++) // project out output variables
      M.setMap(VarDecl.OUTPUT_VAR, i, VarDecl.OUTPUT_VAR, i);

    r = r.MapRel1(M, false, n_inputs, n_outputs, true, true);

    if (omegaLib.trace) {
      System.out.println("Computed DeltasToRelation:");
      r.prefixPrint();
    }

    olAssert(!r.isSet());
    omegaLib.skipSetChecks--;

    return r;
  }

  /**
   *    complement.
   *            not F
   */
  public RelBody complement()
  {
    RelBody r = this;
    if (r.ref_count > 1)
      r = new RelBody(r);

    omegaLib.skipSetChecks++;  
    Mapping m = new Mapping(number_input, number_output);
    for (int i = 1; i <= number_input; i++)
      m.setMap_in (i, VarDecl.INPUT_VAR, i);
    for (int i = 1; i <= number_output; i++)
      m.setMap_out(i, VarDecl.OUTPUT_VAR, i);
    omegaLib.skipSetChecks--;

    r = r.MapRel1(m, true, -1, -1, false, true);
    return r;
  }

  public RelBody projectOntoJust(VarDecl v)
  {
    RelBody r = this;
    if (r.ref_count > 1)
      r = new RelBody(r);

    omegaLib.skipSetChecks++;
    
    int ivars    = number_input;
    int ovars    = number_output;
    int ex_ivars = 0;
    int ex_ovars = 0;
    
    olAssert((v.kind() == VarDecl.INPUT_VAR) || (v.kind() == VarDecl.OUTPUT_VAR));
    if (v.kind() == VarDecl.INPUT_VAR) {
      ex_ivars = 1;
      r.addInputVar("");
    } else {
      ex_ovars = 1;
      r.addOutputVar("");
    }

    // Project everything except v
    Mapping m = new Mapping(ivars + ex_ivars, ovars + ex_ovars);
    for (int j = 1; j <= ivars + ex_ivars; j++)
      m.setMap_in(j, VarDecl.EXISTS_VAR, j);
    for (int j = 1; j <= ovars + ex_ovars; j++)
      m.setMap_out(j, VarDecl.EXISTS_VAR, j+ivars+ex_ivars);
    m.setMap(v.kind(), v.getPosition(), v.kind(), v.getPosition());

    r = r.MapRel1(m, false, -1, -1, true, true);
    omegaLib.skipSetChecks--;

    return r;
  }

  private RelBody MapAndCombineRel2(RelBody r2, Mapping mapping1, Mapping mapping2, boolean isOr, boolean comp, int number_input, int number_output)
  {
    mapping1.getRelationArityFromMappings(mapping2);

    RelBody r3 = new RelBody(omegaLib, (number_input == -1) ? mapping1.getInReq() : number_input, (number_output == -1) ? mapping1.getOutReq() : number_output);

    /* permit the add_{exists,and} below, reset after they are done.*/
    omegaLib.skipFinalizationCheck++;
    
    FExists fe = null;
    Formula f  = r3;
    if (mapping1.hasExistentials() || mapping2.hasExistentials()) {
      fe = r3.addExists();
      f = fe;
    }   

    DNFtoFormula();
    Formula f1 = removeFirstChild();
    r2.DNFtoFormula();
    Formula f2 = r2.removeFirstChild();

    // align: change r1 vars to r3 vars in formula f1 via map mapping1,
    //        declaring needed exists vars in FExists *fe
    // Also maps symbolic variables appropriately, sets relation ptrs in f1.
    // In order to map variables of both relations to the same variables,
    // we keep a list of new existentially quantified vars between calls.
    // returnAsSet means mark r3 as set before return.  Don't mark it yet,
    // because internally we need to refer to "inputVars" of a set, and that
    // would blow assertions.

    omegaLib.clearIDS();

    boolean returnAsSet = align(r3, fe, f1, mapping1);

    // align: change r2 vars to r3 vars in formula f2 via map mapping2

    returnAsSet = r2.align(r3, fe, f2, mapping2);

    if (isOr) {
      if (f1.nodeType() == Formula.OP_OR) {
        f.checkAndAddChild(f1); 
        f = f1;
      } else {
        f = f.addOr();
        f.checkAndAddChild(f1); 
      }
    } else {
      if (f1.nodeType() == Formula.OP_AND) {
        f.checkAndAddChild(f1); 
        f = f1;
      } else {
        f = f.addAnd();
        f.checkAndAddChild(f1); 
      }
    }

    Formula c2 = f;
    if (comp)
      c2 = f.addNot();

    c2.checkAndAddChild(f2);

    omegaLib.skipFinalizationCheck--;     /* Set this back for return */

    if (returnAsSet || (isSet() && r2.isSet() && (r3.number_input >= 0) && (r3.number_output == 0))){
      r3.setIsSet(true);
      r3.invalidateLeadingInfo(-1);
    }

    return r3;
  }

  /**
   *      r1 Union r2.
   *            align the input tuples (if any) for F and G
   *            align the output tuples (if any) for F and G
   *            match named variables in F and G
   *            formula is f | g
   */
  public RelBody union(RelBody r2)
  {
    RelBody r1  = this;
    int     in  = r1.number_input;
    int     out = r1.number_output;

    if ((in != r2.number_input) || (out != r2.number_output))
      throw new polyglot.util.InternalCompilerError("in & out");

    return r1.MapAndCombineRel2(r2, Mapping.Identity(in, out), Mapping.Identity(in, out), true, false, -1, -1);
  }

  /**
   * F intersection G.
   *            align the input tuples (if any) for F and G
   *            align the output tuples (if any) for F and G
   *            match named variables in F and G
   *            formula is f & g
   */
  public RelBody intersection(RelBody r2)
  {
    RelBody r1  = this;
    int     in  = r1.number_input;
    int     out = r1.number_output;

    if ((in != r2.number_input) || (out != r2.number_output))
      throw new polyglot.util.InternalCompilerError("in & out");

    return r1.MapAndCombineRel2(r2, Mapping.Identity(in, out), Mapping.Identity(in, out), false, false, -1, -1);
  }

  /**
   *    F \ G (the relation F restricted to domain G).
   *            align the input tuples for F and G
   *            match named variables in F and G
   *            formula is f & g
   */
  public RelBody restrictDomain(RelBody r2)
  {
    RelBody r1  = this;
    int     in  = r1.number_input;
    int     out = r1.number_output;

    if (!r2.isSet() || (in != r2.numberSet()))
      throw new polyglot.util.InternalCompilerError("set");

    Mapping m2 = new Mapping(r2.numberSet());
    for (int i = 1; i <= r2.numberSet(); i++)
      m2.setMap_set(i, VarDecl.INPUT_VAR, i);

    omegaLib.skipSetChecks++;
    olAssert((r2.queryGuaranteedLeadingZeros() == -1) && (r2.queryPossibleLeadingZeros() == -1));
    omegaLib.skipSetChecks--;

    // FERD -- update leading 0's - the may close up?
    //result.invalidateLeadingInfo();  // could do better
    return r1.MapAndCombineRel2(r2, Mapping.Identity(in,out), m2, false, false, -1, -1);
  }

  /**
   *    F / G (the relation F restricted to range G)
   *            align the output tuples for F and G
   *            match named variables in F and G
   *            formula is f & g
   */
  public RelBody restrictRange(RelBody r2)
  {
    RelBody r1  = this;
    int     in  = r1.number_input;
    int     out = r1.number_output;

    if (!r2.isSet() || (out != r2.numberSet()))
      throw new polyglot.util.InternalCompilerError("set");

    Mapping m2 = new Mapping(r2.numberSet());
    for (int i = 1; i <= r2.numberSet(); i++)
      m2.setMap_set(i, VarDecl.OUTPUT_VAR, i);

    omegaLib.skipSetChecks++;
    olAssert((r2.queryGuaranteedLeadingZeros() == -1) && (r2.queryPossibleLeadingZeros() == -1));
    omegaLib.skipSetChecks--;

    // FERD -- update leading 0's - the may close up?
    // result.invalidateLeadingInfo();  // could do better
    return r1.MapAndCombineRel2(r2, Mapping.Identity(in, out), m2, false, false, -1, -1);
  }

  /**
   * Cross Product.  Give two sets, A and B, create a RelBody whose
   * domain is A and whose range is B.
   */
  public RelBody crossProduct(RelBody r2)
  {
    RelBody r1 = this;

    olAssert(r1.isSet());
    olAssert(r2.isSet());

    omegaLib.skipSetChecks++;
    olAssert((r1.queryGuaranteedLeadingZeros() == -1) && (r1.queryPossibleLeadingZeros() == -1));
    olAssert((r2.queryGuaranteedLeadingZeros() == -1) && (r2.queryPossibleLeadingZeros() == -1));
    omegaLib.skipSetChecks--;

    Mapping mr2 = new Mapping(r2.numberSet()); 
    for (int i = 1; i <= r2.numberSet(); i++)
      mr2.setMap_set(i, VarDecl.OUTPUT_VAR, i);

    Mapping mr1 = new Mapping(r1.numberSet());
    for (int i = 1; i <= r1.numberSet(); i++)
      mr1.setMap_set(i, VarDecl.INPUT_VAR, i);

    return r1.MapAndCombineRel2(r2, mr1, mr2, false, false, -1, -1);
  }

  /**
   * Composition(F, G) = F o G, where F o G (x) = F(G(x))
   *      That is, if F = { [i] . [j] : ... }
   *              and G = { [x] . [y] : ... }
   *             then Composition(F, G) = { [x] . [j] : ... }
   *
   *    align the output tuple for G and the input tuple for F,
   *            these become existensially quantified variables
   *    use the output tuple from F and the input tuple from G for the result
   *    match named variables in G and F
   *    formula is g & f
   *
   * If there are function symbols of arity > 0, we call special case
   * code to handle them.  This is not set up for the r2.isSet case yet.
   */
  public RelBody composition(RelBody r2)
  {
    RelBody r1 = this;
    if (r1.ref_count > 1)
      r1 = new RelBody(r1);

    if (r2.ref_count > 1)
      r2 = new RelBody(r2);

    if (r2.isSet()) {
      int a1 = r1.maxUfsArityOfIn();
      int a2 = r2.maxUfsArityOfSet();
      if (r2.numberSet() != r1.number_input) {
        System.out.println("Illegal composition/application, arities don't match");
        System.out.println("Trying to compute r1(r2)");
        System.out.println("arity of r2 must match input arity of r1");
        System.out.print("r1: ");
        r1.printWithSubs();
        System.out.print("r2: ");
        r2.printWithSubs();
        System.out.println("");
        olAssert(r2.numberSet() == r1.number_input);
        throw new polyglot.util.InternalCompilerError("composition");
      }

      omegaLib.skipSetChecks++;

      if ((a1 == 0) && (a2 == 0)) {
        int     x  = r1.number_output;

        Mapping m1 = new Mapping(r1.number_input, r1.number_output);
        for (int i = 1; i <= r1.number_output; i++)
          m1.setMap_out(i, VarDecl.SET_VAR, i);

        for (int i = 1; i <= r1.number_input; i++)
          m1.setMap_in (i, VarDecl.EXISTS_VAR, i);

        Mapping m2 = new Mapping(r2.numberSet());
        for (int i = 1; i <= r2.numberSet(); i++)
          m2.setMap_set(i, VarDecl.EXISTS_VAR, i);

        RelBody r3 = r2.MapAndCombineRel2(r1, m2, m1, false, false, -1, -1);

        omegaLib.skipSetChecks--;
        if (x == 0) {
          r3.setIsSet(true);
          r3.invalidateLeadingInfo(-1);
        }
        return r3;
      }

      throw new polyglot.util.InternalCompilerError("Can't compose RelBody and set with function symbols");
    }

    if (r2.number_output != r1.number_input) {
      System.out.println("Illegal composition, arities don't match");
      System.out.println("Trying to compute r1 compose r2");
      System.out.println("Output arity of r2 must match input arity of r1");
      System.out.print("r1: ");
      r1.printWithSubs();
      System.out.print("r2: ");
      r2.printWithSubs();
      System.out.println("");
      olAssert(r2.number_output == r1.number_input);
      throw new polyglot.util.InternalCompilerError("Composition2");
    }

    int a1 = r1.maxUfsArityOfIn();
    int a2 = r2.maxUfsArityOfOut();
    int a  = max(a1, a2);

    if ((a1 == 0) && (a2 == 0) && false /* FERD - leading 0's go wrong here */) {
      // If no real UFS's, we can just use the general code:
      Mapping m1 = new Mapping(r1.number_input, r1.number_output);
      for (int i = 1; i <= r1.number_input; i++)
        m1.setMap_in (i, VarDecl.EXISTS_VAR, i);

      for (int i = 1; i <= r1.number_output; i++)
        m1.setMap_out(i, VarDecl.OUTPUT_VAR, i);

      Mapping m2 = new Mapping(r2.number_input, r2.number_output);
      for (int i = 1; i <= r2.number_input; i++)
        m2.setMap_in (i, VarDecl.INPUT_VAR, i);

      for (int i = 1; i <= r2.number_output; i++)
        m2.setMap_out(i, VarDecl.EXISTS_VAR, i);

      return r2.MapAndCombineRel2(r1, m2, m1, false, false, -1, -1);
    }

    RelBody result   = new RelBody(omegaLib, r2.number_input, r1.number_output);
    int     mid_size = r2.number_output;

    for (int i = 1; i <= r2.number_input; i++)
      result.nameInputVar(i, r2.inputVar(i).baseName());
    for (int i = 1; i <= r1.number_output; i++)
      result.nameOutputVar(i, r1.outputVar(i).baseName());

    r1.simplify();
    r2.simplify();

    DNF d1 = r1.DNFize();
    DNF d2 = r2.DNFize();
      
    d1.countLeadingZeros();
    d2.countLeadingZeros();

    // Any conjucts with leading_0s == -1 must have >= max_arity leading 0s
    // What a gross way to do   Ferd

    FExists exists         = result.addExists();
    Vector  middle_tuple   = exists.declareTuple(mid_size);
    HashMap lost_functions = new HashMap(11); // Map from global variable to variable.
    FOr     result_conjs   = exists.addOr();
    Vector  cl1            = d1.getConjList();
    int     cll1           = cl1.size();

    for (int icl1 = 0; icl1 < cll1; icl1++) {
      Conjunct conj1 = (Conjunct) cl1.elementAt(icl1);
      Vector   cl2   = d2.getConjList();
      int      cll2  = cl2.size();

      for (int icl2 = 0; icl2 < cll2; icl2++) {
        Conjunct conj2 = (Conjunct) cl2.elementAt(icl2);

//      conj1.studyEvacuation(conj2, a);

        /**
         * Combine conj1 and conj2.
         *   conj2's in becomes result's in; conj1's out becomes out
         *   conj2's out and conj1's in get merged and exist. quant.
         *   conj2's f(in) and conj1's f(out) become f(in) and f(out)
         *   conj2's f(out) and conj1's f(in) get merged, evacuate:
         *     if conj1 has f.arity leading 0s, they become f(out),
         *     if conj2 has f.arity leading 0s, they become f(in)
         *     if neither has enough 0s, they become a wildcard
         *                               and the result is inexact
         *   old wildcards stay wildcards
         */

        Conjunct copy2    = new Conjunct(conj2);
        Conjunct copy1    = new Conjunct(conj1);
        Vector   remapped = new Vector();
        int      c1L0     = copy1.getGuaranteedLeading0s();
        int      c2L0     = copy2.getGuaranteedLeading0s();
        boolean  inexact  = false;

        // get rid of conj2's f(out)

        Vector vars2 = copy2.variables();
        int    l2    = vars2.size();
        for (int vi = 0; vi < l2; vi++) {
          VarDecl func = (VarDecl) vars2.elementAt(vi);
          if (func.kind() != VarDecl.GLOBAL_VAR)
            continue;

          GlobalVarDecl f = func.getGlobalVar();
          if ((f.arity() > 0) && (func.functionOf() == VarDecl.OUTPUT_TUPLE)) {
            if (c2L0 >= f.arity()) {
              func.resetRemapField(r2.getLocal(f, VarDecl.INPUT_TUPLE));
              remapped.addElement(func);
            } else if (c1L0 >= f.arity()) {
                                // f.resetRemapField(copy1.getLocal(f, VarDecl.OUTPUT_TUPLE));
                                // this should work with the current impl.
                                // SHOULD BE A NO-OP?
              olAssert(func == r1.getLocal(f, VarDecl.OUTPUT_TUPLE));
            } else {
              VarDecl f_quantified = (VarDecl) lost_functions.get(f);
              if (f_quantified == null) {
                f_quantified = exists.declare();
                lost_functions.put(f, f_quantified);
              }
              inexact = true;
              func.resetRemapField(f_quantified);
              remapped.addElement(func);
            }
          }
        }

        // remap copy2's out
        for (int i = 1; i <= mid_size; i++)
          r2.outputVar(i).resetRemapField((VarDecl) middle_tuple.elementAt(i));

        // do remapping for conj2, then reset everything so
        // we can go on with conj1

        copy2.remap();
        VarDecl.resetRemapField(remapped);
        VarDecl.resetRemapField(omegaLib.outputVars(), mid_size);

        remapped.clear();

        // get rid of conj1's f(in)

        Vector vars1 = copy1.variables();
        int    l1    = vars1.size();
        for (int vi = 0; vi < l1; vi++) {
          VarDecl func = (VarDecl) vars1.elementAt(vi);
          if (func.kind() != VarDecl.GLOBAL_VAR)
            continue;

          GlobalVarDecl f = func.getGlobalVar();
          if ((f.arity() > 0) && (func.functionOf() == VarDecl.INPUT_TUPLE)) {
            if (c1L0 >= f.arity()) {
              func.resetRemapField(r1.getLocal(f, VarDecl.OUTPUT_TUPLE));
              remapped.addElement(func);
            } else if (c2L0 >= f.arity()) {
                                // f.resetRemapField(copy2.getLocal(f, VarDecl.INPUT_TUPLE));
                                // this should work with the current impl.
                                // SHOULD BE A NO-OP?
              olAssert(func == r2.getLocal(f, VarDecl.INPUT_TUPLE));
            } else {
              VarDecl f_quantified = (VarDecl) lost_functions.get(f);
              if (f_quantified == null) {
                f_quantified = exists.declare();
                lost_functions.put(f, f_quantified);
              }
              inexact = true;
              func.resetRemapField(f_quantified);
              remapped.addElement(func);
            }
          }
        }                   

        // merge copy1's in with the already remapped copy2's out
        for (int i = 1; i <= mid_size; i++)
          r1.inputVar(i).resetRemapField((VarDecl) middle_tuple.elementAt(i));

        copy1.remap();
        VarDecl.resetRemapField(remapped);
        VarDecl.resetRemapField(omegaLib.inputVars(), mid_size);

        Conjunct conj3 = copy1.mergeConjuncts(copy2, Conjunct.MERGE_COMPOSE, exists.relation());

        if (inexact)
          conj3.makeInexact();

        // Make sure all variables used in the conjunct
        // are listed in the "result" 

        Vector vars = conj3.variables();
        int    l3   = vars.size();
        for (int vi = 0; vi < l3; vi++) {
          VarDecl func = (VarDecl) vars.elementAt(vi);
          if (func.kind() != VarDecl.GLOBAL_VAR)
            continue;

          GlobalVarDecl f = func.getGlobalVar();
          if (f.arity() > 0)
            result.getLocal(f, func.functionOf());
          else
            result.getLocal(f);
        }

        result_conjs.checkAndAddChild(conj3);
      }
    }

    return result;
  }

  /**
   * doSubsetCheck really implements mustBeSubset anyway (due to 
   * correct handling of inexactness in the negation code), but
   * still take upper and lower bounds here
   */
  private boolean mustBeSubset(RelBody r2)
  {
    RelBody s1 = this;
    if (s1.ref_count > 1)
      s1 = new RelBody(s1);
    s1.interpretUnknownAsTrue();
    
    RelBody s2 = r2;
    if (s2.ref_count > 1)
      s2 = new RelBody(s2);
    s2.interpretUnknownAsFalse();

    return s1.doSubsetCheck(s2);
  }

  /**
   *    F minus G.
   */
  public RelBody difference(RelBody r2)
  {
    RelBody r1 = this;

    omegaLib.skipSetChecks++;

    olAssert(r1.number_input == r2.number_input);
    olAssert(r1.number_output == r2.number_output);

    Mapping m1 = new Mapping(r1.number_input, r1.number_output);
    for (int i = 1; i <= r1.number_input; i++)
      m1.setMap_in (i, VarDecl.INPUT_VAR, i);
    for (int i = 1; i <= r1.number_output; i++)
      m1.setMap_out(i, VarDecl.OUTPUT_VAR, i);

    Mapping m2 = new Mapping(r2.number_input, r2.number_output);
    for (int i = 1; i <= r2.number_input; i++)
      m2.setMap_in (i, VarDecl.INPUT_VAR, i);
    for (int i = 1; i <= r2.number_output; i++)
      m2.setMap_out(i, VarDecl.OUTPUT_VAR, i);

    omegaLib.skipSetChecks--;

    return r1.MapAndCombineRel2(r2, m1, m2, false, true, -1, -1);
  }

  public boolean doSubsetCheck(RelBody r2)
  {
    RelBody r1 = this;
    if (r1.ref_count > 1)
      r1 = new RelBody(r1);

    if (r2.ref_count > 1)
      r2 = new RelBody(r2);

    omegaLib.skipSetChecks++;
    olAssert(number_input == r2.number_input);
    olAssert(number_output == r2.number_output);
    omegaLib.skipSetChecks--;

    r1.simplify(1, 0);
    r2.simplify(2, 2);

    if (omegaLib.trace)
      System.out.println("\n$$$ mustBeSubset IN $$$");

    boolean c = true;

    // Check each conjunct separately
    Vector cl1  = r1.queryDNF().getConjList();
    int    cll1 = cl1.size();
    for (int icl1 = 0; icl1 < cll1; icl1++) {
      Conjunct pd  = (Conjunct) cl1.elementAt(icl1);
      RelBody tmp = new RelBody(this, pd);
      if (checkMaybeSubset) {
        c = !tmp.difference(r2).isUpperBoundSatisfiable();
      } else {
        RelBody d = tmp.difference(r2);
        c = !d.isUpperBoundSatisfiable();
        if (!c && !d.isExact()) { // negation-induced inexactness
          if (omegaLib.trace) {
            System.out.println("\n===== r1 is maybe a mustBeSubset of r2 ========");
            System.out.println("------. r1:");
            tmp.printWithSubs();
            System.out.println("------. r2:");
            r2.printWithSubs();
            System.out.println("------. r1-r2:");
            d.printWithSubs();
          }
        }
      }
      tmp.delete();
    }

    if (omegaLib.trace)
      System.out.println("$$$ mustBeSubset OUT $$$");

    return c;
  }
  /**
   * Project out global variable g from RelBody r.
   */
  public RelBody project(GlobalVarDecl g)
  {
    RelBody r = this;
    if (r.ref_count > 1)
      r = new RelBody(r);

    r.DNFtoFormula();
    Formula  f  = r.removeFirstChild();
    FExists ex = r.addExists();
    ex.checkAndAddChild(f);
    
    if (g.arity() == 0) {
      if (!r.hasLocal(g))
        throw new polyglot.util.InternalCompilerError("Project: RelBody doesn't contain variable to be projected");

      VarDecl v = r.getLocal(g);
        
      boolean rmd = r.removeGlobalDecl(v);
      olAssert(rmd, "Project: Variable to be projected doesn't exist");
        
      v.resetRemapField(ex.declare(v.baseName()));
      f.remap();
      v.resetRemapField(v);
    } else {
      boolean hi = r.hasLocal(g, VarDecl.INPUT_TUPLE);
      boolean ho = r.hasLocal(g, VarDecl.OUTPUT_TUPLE);
      if (!hi && !ho)
        throw new polyglot.util.InternalCompilerError("Project: RelBody doesn't contain variable to be projected");

      if (hi) {
        VarDecl v = r.getLocal(g, VarDecl.INPUT_TUPLE);
        
        if (!r.removeGlobalDecl(v))
          throw new polyglot.util.InternalCompilerError("Project: Variable to be projected doesn't exist");
        
        v.resetRemapField(ex.declare(v.baseName()));
        f.remap();
        v.resetRemapField(v);
      }
      if (ho) {
        VarDecl v = r.getLocal(g, VarDecl.OUTPUT_TUPLE);
        
        if (!r.removeGlobalDecl(v))
          throw new polyglot.util.InternalCompilerError("Project: Variable to be projected doesn't exist");
        
        v.resetRemapField(ex.declare(v.baseName()));
        f.remap();
        v.resetRemapField(v);
      }
    }

    omegaLib.skipFinalizationCheck--;
    
    return r;
  }

  /**
   * Project all symbolic variables from RelBody r.
   */
  public RelBody projectSym()
  {
    RelBody r = this;
    if (r.ref_count > 1)
      r = new RelBody(r);

    r.DNFtoFormula();

    Formula f = r.removeFirstChild();

    omegaLib.skipFinalizationCheck++;

    FExists ex   = r.addExists();
    Vector  gbls = r.getGlobalDecls();
    int     l    = gbls.size();
    for (int i = 0; i < l; i++) {
      VarDecl v = (VarDecl) gbls.elementAt(i);
      v.resetRemapField(ex.declare(v.baseName()));
    }

    ex.checkAndAddChild(f);

    omegaLib.skipFinalizationCheck--;

    f.remap();

    VarDecl.resetRemapField(gbls);
    r.clearGlobalDecls();

    return r;
  }

  /**
   * Project an input, output or set variable, leaving a variable in that position
   * with no constraints.
   */
  public RelBody project(int pos, int vkind)
  {
    RelBody r = this;
    if (r.ref_count > 1)
      r = new RelBody(r);

    VarDecl v = null;

    switch (vkind) {
    case VarDecl.INPUT_VAR:
      v = omegaLib.newInputVar(pos);
      break;
    case VarDecl.OUTPUT_VAR:
      v = omegaLib.newOutputVar(pos);
      break;
    case VarDecl.SET_VAR:
      v = omegaLib.newInputVar(pos);
      break;
    default:
      throw new polyglot.util.InternalCompilerError("Project variable kind");
    }
    
    omegaLib.skipFinalizationCheck++;

    r.DNFtoFormula();
    Formula f  = r.removeFirstChild();
    FExists ex = r.addExists();
    ex.checkAndAddChild(f);

    v.resetRemapField(ex.declare(v.baseName()));
    f.remap();
    v.resetRemapField(v);

    omegaLib.skipFinalizationCheck--;
    
    r.setFinalized();
    return r;
  }

  public RelBody project(Vector s)
  {
    RelBody r = this;
    if (r.ref_count > 1)
      r = new RelBody(r);

    // This is difficult to do with mappings.  This cheats, since it is
    // much easier and more straightforward.

    r.DNFtoFormula();

    Formula f = r.removeFirstChild();

    omegaLib.skipFinalizationCheck++;

    FExists ex = r.addExists();

    for (int i = 1; i <= s.size(); i++) {
      VarDecl v = (VarDecl) s.elementAt(i);
      v.resetRemapField(ex.declare(v.baseName()));
    }

    ex.checkAndAddChild(f);

    omegaLib.skipFinalizationCheck--;

    f.remap();

    VarDecl.resetRemapField(s);

    return r;
  }

  public void calculateDimensions(Conjunct c, int[] dims)
  {
    RelBody rc = new RelBody(this, c);

    if (omegaLib.trace) {
      System.out.println("{{{\nIn Conjunct::calculateDimensions:");
      rc.prefixPrint();
    }

    RelBody x = rc.approximate(false);
    rc.delete();
    rc = x;

    RelBody rd = new RelBody(rc);

    if (omegaLib.trace) {
      System.out.println("Conjunct::calculateDimensions: Approximated as:");
      rc.prefixPrint();
    }

    omegaLib.skipSetChecks++;

    Conjunct rc_conj  = rc.getSingleConjunct();
    int      ndim_all = rc.numberInput() + rc.number_output;
    int      neqs     = rc_conj.getNumEQs();

    ndim_all -= neqs;

    rc = rc.projectOnSym(null);
    rc.simplify();

    if (omegaLib.trace) {
      System.out.println("Conjunct::calculateDimensions: after project_on_sym");
      rc.prefixPrint();
    }

    int n_eq_sym = rc.queryDNF().minNumEQs();

    ndim_all += n_eq_sym;
    omegaLib.skipSetChecks--;

    int ndim_domain = ndim_all;
    if (!isSet()) { /* get dimensions for the domain (broadcasting) */
      rd = rd.domain();
      rd.simplify();

      if (omegaLib.trace) {
        System.out.println("Domain is:");
        rd.prefixPrint();
      }

      rc_conj = rd.getSingleConjunct();
      ndim_domain = rd.numberSet() - neqs + n_eq_sym;
    }

    if (omegaLib.trace) {
      System.out.println("n_eq_sym=" + n_eq_sym);
      System.out.print("Dimensions: all=");
      System.out.print(ndim_all);
      System.out.print(" domain=");
      System.out.print(ndim_domain);
      System.out.println("}}}");
    }

    dims[0] = ndim_domain;
    dims[1] = ndim_all;

    rc.delete();
    rd.delete();
  }

  private void printRs(Vector Rs)
  {
    System.out.println("Rs:");
    int rl = Rs.size();
    for (int i = 0; i < rl; i++) {
      RelBody r = (RelBody) Rs.elementAt(i);
      System.out.println("#");
      System.out.print(i);
      System.out.print(" : ");
      System.out.println(r.printWithSubsToString(false, true));
    }
  }

  private boolean isObviousSubset(RelBody r2) 
  {
    RelBody r1 = this;
    if (r1.ref_count > 1)
      r1 = new RelBody(r1);

    if (r2.ref_count > 1)
      r2 = new RelBody(r2);

    r1.simplify();
    r2.simplify();
    omegaLib.useUglyNames++;

    r2.remapDNFVars(r1);

    boolean flg = r1.queryDNF().redSimplifyProblem(r2.queryDNF());

    omegaLib.useUglyNames--;

    return flg;
  }

  public RelBody trueRelation()
  {
    if (isSet())
      return trueRelation(omegaLib, numberSet());

    return trueRelation(omegaLib, number_input, number_output);
  }

  public static RelBody trueRelation(OmegaLib omegaLib, int setvars)
  {
    RelBody r = new RelBody(omegaLib, setvars, setvars);
    r.addAnd();
    return r;
  }

  public static RelBody trueRelation(OmegaLib omegaLib, int in, int out)
  {
    RelBody r = new RelBody(omegaLib, in, out);
    r.addAnd();
    return r;
  }

  public RelBody falseRelation()
  {
    if (isSet())
      return falseRelation(omegaLib, numberSet());

    return falseRelation(omegaLib, number_input, number_output);
  }

  public static RelBody falseRelation(OmegaLib omegaLib, int setvars)
  {
    RelBody r = new RelBody(omegaLib, setvars, setvars);
    r.addOr();
    return r;
  }

  public static RelBody falseRelation(OmegaLib omegaLib, int in, int out)
  {
    RelBody r = new RelBody(omegaLib, in, out);
    r.addOr();
    return r;
  }

  public RelBody emptyRelation()
  {
    if (isSet())
      return new RelBody(omegaLib, numberSet(), numberSet());

    return new RelBody(omegaLib, number_input, number_output);
  }

  public RelBody unknownRelation()
  {
    if (this.isSet())
      return unknownRelation(omegaLib, numberSet());

    return unknownRelation(omegaLib, number_input, number_output);
  }

  public static RelBody unknownRelation(OmegaLib omegaLib, int setvars)
  {
    RelBody R = new RelBody(omegaLib, setvars, setvars);
    R.addAnd();
    R.simplify();
    Conjunct c = R.getSingleConjunct();
    c.makeInexact();
    return R;
  }

  public static RelBody unknownRelation(OmegaLib omegaLib, int in, int out)
  {
    RelBody R = new RelBody(omegaLib, in, out);
    R.addAnd();
    R.simplify();
    Conjunct c = R.getSingleConjunct();
    c.makeInexact();
    return R;
  }

  /**
   * <pre>
   * forall x1, .., xn s.t. a10 + a11 x1 + ... + a1n xn >= 0 and
   *                                     ...
   *                      am0 + am1 x1 + ... + amn xn >= 0
   *
   * b0 + b1 x1 + ... + bn xn >= 0 
   *
   *            iff
   *
   * exists lambda_0, ..., lambda_m >= 0 s.t.
   *     forall x1, .., xn 
   *       lambda_0 +
   *       lambda_1 ( a10 + a11 x1 + ... + a1n xn) +
   *                                ...
   *       lambda_m ( am0 + am1 x1 + ... + amn xn) =
   *
   *                   b0 +  b1 x1 + ... +  bn xn
   *
   *            iff
   *
   * exists lambda_0, ..., lambda_m >= 0 s.t.
   *   lambda_0 + sum_i ( lambda_i a_i0) = b_0
   *   for j in 1..n
   *       sum_i ( a_ij lambda_i) = b_j
   *
   *            iff
   *
   * exists lambda0, ..., lambda_m s.t.
   *        lambda1, ..., lambda_m >= 0 
   *        lambda0 >= 0 
   *   lambda_0 = b_0 - sum_i ( lambda_i a_i0)
   *   for j in 1..n
   *       sum_i ( a_ij lambda_i) = b_j
   *            iff
   *
   * exists lambda1, ..., lambda_m s.t.
   *        lambda1, ..., lambda_m >= 0 
   *   b_0 - sum_i ( lambda_i a_i0) >= 0
   *   for j in 1..n
   *       sum_i ( a_ij lambda_i) = b_j
   *
   * a_ij come from relation rel
   *
   * x_1, ..., x_n are input and output variables from rel.
   *
   * b_0, ..., b_m are input and output arrays of coef_vars
   * </pre>
   * Given a Relation/Set R
   * Compute A, B, C such that
   * Ax+By + C >= 0 is true for all x, y in R
   * iff [A, B] : constantTerm=C is in AffineClosure(R)
   * Note: constantTerm is a special global variable
   * If constantTerm appears in the incoming relation
   * then set it's coefficient to be 1 in the result
   * <p>
   * # For example, given
   * R := {[i, j] : 1 <= i <= 10 && 1 <= j <= n}
   * # the farkas closure of R is:
   * # ac := approximate {[i, j] : exists (lambda0, lambda1, lambda2, lambda3, lambda4 :
   * #       0 <= lambda1, lambda2, lambda3, lambda4
   * #       && constantTerm - (-lambda1+ 10 lambda2 - lambda3) >= 0
   * #       && i = lambda1-lambda2
   * #       && j = lambda3-lambda4
   * #       && n = lambda4)}
   * # 
   * # ac;
   *  <p>
   * {[i, j]: 0 <= n && 0 <= n+constantTerm+i+j 
   *                    && 0 <= n+constantTerm+10i+j && 0 <= n+j}
   *  <p>
   * The ConvexCombination of ac is:
   * <pre>
   *# approximate {[i, j] : exists (lambda1, lambda2, lambda3, lambda4 :
   *#       0 <= lambda1, lambda2, lambda3, lambda4
   *#       && 1 = lambda2+lambda3
   *#       && i = lambda2+10lambda3
   *#       && j = lambda2+lambda3+lambda4
   *#       && n = lambda1+lambda2+lambda3+lambda4
   *#   )}
   *{[i, j]: 1 <= i <= 10 && 1 <= j <= n}
   * </pre>
   */

  private void handleVariable(Conjunct conj, FAnd and_node, HashMap gMap, HashMap eMap, VarDecl v)
  {
    omegaLib.useUglyNames++;
    if (omegaLib.trace)
      System.out.println("Building equality for " + v.name(omegaLib));

    EQHandle e = and_node.addEQ(false);

    if ((v.kind() == VarDecl.GLOBAL_VAR)  && (v.getGlobalVar() == coefficient_of_constant_term)) 
      e.updateConstant(-1);
    else
      e.updateCoefficient(getLocal(v), -1);

    Equation[] eqs   = conj.getEQs();
    int        neqs  = conj.getNumEQs();
    Equation[] geqs  = conj.getGEQs();
    int        ngeqs = conj.getNumGEQs();

    for (int j = 0; j < ngeqs; j++) {
      Equation eqc  = geqs[j];
      VarDecl v1 = (VarDecl) gMap.get(eqc);
      if (v1 != null) {
        int col = conj.findColumn(v1);
        e.updateCoefficient(v1, eqc.getCoefficient(col));
      }
    }

    for (int j = 0; j < neqs; j++) {
      Equation eqc = eqs[j];
      VarDecl  v1  = (VarDecl) eMap.get(eqc);
      if (v1 != null) {
        int col = conj.findColumn(v1);
        e.updateCoefficient(v1, eqc.getCoefficient(col));
      }
    }

    if (omegaLib.trace)
      System.out.println("Constraint is " + e.printToString());

    omegaLib.useUglyNames--;
  }

  private RelBody farkas(int op)
  {
    omegaLib.useUglyNames++;
    farkasDifficulty = 0;

    RelBody R = this;
    if (R.ref_count > 1)
      R = new RelBody(R);

    R = R.approximate(false);
    R.simplify(0, 2);

    RelBody result = R.falseRelation();

    if (omegaLib.trace) {
      System.out.println("Computing farka of: [");
      R.prefixPrint();
    }

    Vector vars = (Vector) R.getGlobalDecls().clone();
    if (R.isSet()) 
      for (int i = 1; i <= R.numberSet(); i++)
        vars.addElement(R.setVar(i));
    else {
      for (int i = 1; i <= R.number_input; i++)
        vars.addElement(R.inputVar(i));
      for (int i = 1; i <= R.number_output; i++)
        vars.addElement(R.outputVar(i));
    }
 
    Vector owners = new Vector();

    //    Map<VarDecl, VarDecl_Set> connectedVariables(empty);

    HashMap connectedVariables = new HashMap(11);

    if (op == Decoupled_Farkas) {
      Vector gl  = R.getGlobalDecls();
      int    gll = gl.size();
      for (int gli = 0; gli < gll; gli++) {
        VarDecl v = (VarDecl) gl.elementAt(gli);
        if (v.kind() != VarDecl.GLOBAL_VAR)
          continue;

        GlobalVarDecl g = v.getGlobalVar();
        if (g.arity() <= 0)
          continue;

        if (R.isSet()) 
          for (int i = 1; i <= g.arity(); i++) 
            v.UFUnion(R.setVar(i));
        else if (v.functionOf() == VarDecl.INPUT_TUPLE)
          for (int i = 1; i <= g.arity(); i++) 
            v.UFUnion(R.inputVar(i));
        else
          for (int i = 1; i <= g.arity(); i++) 
            v.UFUnion(R.outputVar(i));
      }

      R.queryDNF().farkas();

      int vvl = vars.size();
      for (int i = 0; i < vvl; i++) {
        VarDecl v3    = (VarDecl) vars.elementAt(i);
        VarDecl owner = v3.UFOwner();
        HashSet hs    = (HashSet) connectedVariables.get(owner);
        if (hs == null) {
          hs = new HashSet(3);
          connectedVariables.put(owner, hs);
        }
        hs.add(v3);
      }

      Iterator ek = connectedVariables.keySet().iterator();
      while (ek.hasNext()) {
        VarDecl v  = (VarDecl) ek.next();
        HashSet  hs = (HashSet) connectedVariables.get(v);
        owners.addElement(v);
        if (omegaLib.trace) {
          System.out.print(v.displayName(omegaLib));
          System.out.print(":");
          Iterator it = hs.iterator();
          while (it.hasNext()) {
            VarDecl v2 = (VarDecl) it.next();
            System.out.print(" ");
            System.out.print(v2.displayName(omegaLib));
          }
          System.out.println("");
        }
      }
    }

    int     ol            = owners.size();
    int     oli           = 0;
    int     lambda_cnt    = 1;
    boolean firstGroup    = true;
    RelBody partialResult = null;

    while (((op == Decoupled_Farkas) && (oli < ol)) || ((op != Decoupled_Farkas) && firstGroup)) {
      VarDecl owner = oli < ol ? (VarDecl) owners.elementAt(oli) : null;
      if (omegaLib.trace && (op == Decoupled_Farkas)) {
        System.out.print("[Computing decoupled farkas for:");
        HashSet  hs = (HashSet) connectedVariables.get(owner);
        Iterator it = hs.iterator();
        while (it.hasNext()) {
          VarDecl v2 = (VarDecl) it.next();
          System.out.print(" ");
          System.out.print(v2.displayName(omegaLib));
        }
        System.out.println("");
      }

      firstGroup = false;
      partialResult = R.trueRelation();

      int    difficulty = 0;
      Vector cl         = R.queryDNF().getConjList();
      int    cll        = cl.size();
      for (int cli = 0; cli < cll; cli++) {
        Conjunct s = (Conjunct) cl.elementAt(cli);

        difficulty = s.difficulty();

        if (omegaLib.trace) {
          System.out.println("Computing farka of conjunct: ");
          s.prefixPrint();
          System.out.println("Difficulty is " + difficulty);
        }

        if (difficulty >= 500)  {
          farkasDifficulty = difficulty;
          if (omegaLib.trace)
            System.out.println("Too ugly, returning dull result");
          omegaLib.useUglyNames--;
          if ((op == Basic_Farkas) || (op == Decoupled_Farkas))
            return partialResult.falseRelation();

          return partialResult.trueRelation();
        }

        RelBody farkas = R.emptyRelation();

        farkas.copyNames(R);

        FExists    exist    = farkas.addExists();
        FAnd       and_node = exist.addAnd();
        HashMap    gMap     = new HashMap(11);
        HashMap    eMap     = new HashMap(11);
        Equation[] eqs      = s.getEQs();
        int        neqs     = s.getNumEQs();
        Equation[] geqs     = s.getGEQs();
        int        ngeqs    = s.getNumGEQs();

        for (int j = 0; j < neqs; j++) {
          Equation eh = eqs[j];
          if (op == Decoupled_Farkas) {
            boolean shouldConsider = true;
            Vector  vl             = s.variables();
            int     vll            = vl.size();
            for (int vli = 0; vli < vll; vli++) {
              VarDecl v   = (VarDecl) vl.elementAt(vli);
              int      col = s.findColumn(v);
              if ((eh.getCoefficient(col) != 0) && (v.UFOwner() != owner))  {
                shouldConsider = false;
                break;
              }
            }
            if (!shouldConsider)
              continue;
          }

          eMap.put(eh, exist.declare("lambda" + lambda_cnt++));
          olAssert((op == Basic_Farkas) || (op == Decoupled_Farkas) || (eh.getConstant() == 0));
        }
        for (int j = 0; j < ngeqs; j++) {
          Equation eh = geqs[j];
          if (op == Decoupled_Farkas) {
            boolean shouldConsider = true;
            Vector  vl             = s.variables();
            int     vll            = vl.size();
            for (int vli = 0; vli < vll; vli++) {
              VarDecl v   = (VarDecl) vl.elementAt(vli);
              int      col = s.findColumn(v);
              if ((eh.getCoefficient(col) != 0) && (v.UFOwner() != owner))  {
                shouldConsider = false;
                break;
              }
            }
            if (!shouldConsider)
              continue;
          }

          VarDecl lambda = exist.declare("lambda" + lambda_cnt);
          switch (op) {
          case Positive_Combination_Farkas:
          case Convex_Combination_Farkas:
          case Basic_Farkas: 
          case Decoupled_Farkas: 
            GEQHandle positive = and_node.addGEQ(false);
            positive.updateCoefficient(lambda, 1);
            break;
          case Linear_Combination_Farkas:
          case Affine_Combination_Farkas: 
            break;
          }
          gMap.put(eh, lambda);
          olAssert((op == Basic_Farkas) || (op == Decoupled_Farkas) || (eh.getConstant() == 0));
        }

        int vvl = vars.size();
        for (int i = 0; i < vvl; i++) {
          VarDecl v  = (VarDecl) vars.elementAt(i);
          olAssert(v.kind() != VarDecl.WILDCARD_VAR);
          if ((v.kind() == VarDecl.GLOBAL_VAR) && (v.getGlobalVar() == coefficient_of_constant_term))  {
            olAssert((op != Basic_Farkas) && (op != Decoupled_Farkas));
            if (op == Linear_Combination_Farkas)
              continue;
            if (op == Positive_Combination_Farkas)
              continue;
          }
          if ((op == Decoupled_Farkas) && (v.UFOwner() != owner)) {
            EQHandle e = and_node.addEQ(false);
            e.updateCoefficient(farkas.getLocal(v), -1);
            continue;
          }
          farkas.handleVariable(s, and_node, gMap, eMap, v);
        }

        if ((op == Basic_Farkas) || (op == Decoupled_Farkas)) {
          GEQHandle e = and_node.addGEQ(false);
          e.updateCoefficient(farkas.getLocal(coefficient_of_constant_term), 1);
          for (int j = 0; j < ngeqs; j++) {
            Equation eh = geqs[j];
            VarDecl  v1 = (VarDecl) gMap.get(eh);
            if (v1 != null)
              e.updateCoefficient(v1, -eh.getConstant());
          }
          for (int j = 0; j < neqs; j++) {
            Equation eh = eqs[j];
            VarDecl  v1 = (VarDecl) eMap.get(eh);
            if (v1 != null)
              e.updateCoefficient(v1, -eh.getConstant());
          }
        }

        farkas.simplify(-1, -1);
        difficulty = farkas.getSingleConjunct().difficulty();

        if (omegaLib.trace) {
          System.out.println("farka has difficulty " + difficulty);
          farkas.prefixPrint();
        }

        if (difficulty >= 500)  {
          farkasDifficulty = difficulty;
          if (omegaLib.trace)
            System.out.println("Too ugly, returning dull result");

          omegaLib.useUglyNames--;
          if ((op == Basic_Farkas) || (op == Decoupled_Farkas))
            return  partialResult.falseRelation();

          return partialResult.trueRelation();
        }

        farkas = farkas.approximate(false);
        if (omegaLib.trace) {
          System.out.println("simplified:");
          farkas.prefixPrint();
        }

        partialResult = partialResult.intersection(farkas).approximate(false);

        if (omegaLib.trace) {
          System.out.println("combined:");
          partialResult.prefixPrint();
        }

        if (partialResult.hasSingleConjunct()) {
          difficulty = partialResult.getSingleConjunct().difficulty();
        } else 
          difficulty = 1000;

        if (difficulty >= 500)  {
          farkasDifficulty = difficulty;
          if (omegaLib.trace)
            System.out.println("Too ugly, returning dull result");

          omegaLib.useUglyNames--;
          if ((op == Basic_Farkas) || (op == Decoupled_Farkas)) 
            return partialResult.falseRelation();

          return partialResult.trueRelation();
        }
      }

      farkasDifficulty += difficulty;

      if (omegaLib.trace) {
        System.out.println("] done computing farkas");
        partialResult.prefixPrint();
      }

      if (op == Decoupled_Farkas) {
        result = result.union(partialResult);
        oli++;
      }
    }

    if (true || (op == Decoupled_Farkas))
      VarDecl.resetRemapField(vars);

    omegaLib.useUglyNames--;
    if (op != Decoupled_Farkas)
      return partialResult;

    if (omegaLib.trace) {
      System.out.println("] decoupled result:");
      result.prefixPrint();
    }

    return result;
  }

  private RelBody fastTightHull(RelBody input_H)
  {
    RelBody R = approximate(false);
    RelBody H = input_H.approximate(false);

    if (omegaLib.trace) {
      System.out.println("[ Computing FastTightHull of:");
      R.prefixPrint();
      System.out.println("given known hull of:");
      H.prefixPrint();
    }

    if (!H.hasSingleConjunct())  {
      if (omegaLib.trace) 
        System.out.println("] bailing out of FastTightHull, known hull not convex");
      return H;
    }
    if (!H.isObviousTautology()) {
      R = R.gist(H, 0);
      R.simplify(1, 0);
    }
    if (R.hasSingleConjunct()) {
      R = R.intersection(H);
      if (omegaLib.trace)  {
        System.out.println("] quick easy answer to FastTightHull");
        R.prefixPrint();
      }
      return R;
    }

    if (R.hasLocal(coefficient_of_constant_term)) {
      if (omegaLib.trace)
        System.out.println("] Can't handle recursive application of Farkas lemma");

      return H;
    }
                
    if (omegaLib.trace) {
      System.out.println("Gist of R given H is:");
      R.prefixPrint();
    }

    HashSet vars = R.queryDNF().fastTightHull();
     
    // We now know which variables appear in R.

    if (omegaLib.trace) {
      System.out.print("Variables we need a better hull on are: ");
      Iterator it = vars.iterator();
      while (it.hasNext()) {
        VarDecl v = (VarDecl) it.next();
        System.out.println(" " + v.displayName(omegaLib));
      }
    }

    Conjunct   c      = H.getSingleConjunct();
    Equation[] eqs    = c.getEQs();
    int        neqs   = c.getNumEQs();
    Equation[] geqs   = c.getGEQs();
    int        ngeqs  = c.getNumGEQs();
    int        total  = 0;
    int        copied = 0;

    for (int i = 0; i < neqs; i++) {
      Equation eq = eqs[i];

      total++;

      Iterator  it = vars.iterator();
      while (it.hasNext()) {
        VarDecl v   = (VarDecl) it.next();
        int      col = c.findColumn(v);
        if (eq.getCoefficient(col) == 0)
          continue;

        R.andWithEQ(c, eq);
        copied++;
        break;
      }
    }

    for (int i = 0; i < ngeqs; i++) {
      Equation eq = geqs[i];

      total++;

      Iterator   it = vars.iterator();
      while (it.hasNext()) {
        VarDecl v = (VarDecl) it.next();
        int      col = c.findColumn(v);
        if (eq.getCoefficient(col) == 0)
          continue;

        R.andWithGEQ(c, eq);
        copied++;
        break;
      }
    }

    if (copied < total) {
      R = R.approximate(false);

      if (omegaLib.trace) {     
        System.out.println("Decomposed relation, copied only " + copied + " of " + total + " constraints");
        System.out.println("Original R:");
        R.prefixPrint();
        System.out.println("Known hull:");
        H.prefixPrint();
        System.out.println("New R:");
        R.prefixPrint();
      }
    }

    RelBody F = R.farkas(Basic_Farkas);
    if (omegaLib.trace)  
      System.out.println("Farkas Difficulty = " + R.farkasDifficulty);

    if (R.farkasDifficulty > 260) {
      if (omegaLib.trace)  {
        System.out.println("] bailing out, farkas is way too complex");
        System.out.println("Farkas:");
        F.prefixPrint();
      }
      return H;
    }

    if (R.farkasDifficulty > 130) {
      // Bail out
      if (omegaLib.trace) {
        System.out.print(farkasDifficulty);
        System.out.println(" non-zeros in original farkas");
      }

      RelBody tmp = R.farkas(Decoupled_Farkas);
           
      if (omegaLib.trace)  {
        System.out.print(R.farkasDifficulty);
        System.out.println(" non-zeros in decoupled farkas");
      }

      if (R.farkasDifficulty > 260)  {
        if (omegaLib.trace)  {
          System.out.println("] bailing out, farkas is way too complex");
          System.out.println("Farkas:");
          F.prefixPrint();
        }
        return H;
      }

      if (R.farkasDifficulty > 130) 
        R = H.intersection(tmp.farkas(Affine_Combination_Farkas));
      else
        R = H.intersection(tmp.farkas(Convex_Combination_Farkas).intersection(F.farkas(Affine_Combination_Farkas)));

      if (omegaLib.trace)  {
        System.out.println("] bailing out, farkas is too complex, using affine hull");
        System.out.println("Farkas:");
        F.prefixPrint();
        System.out.println("Affine hull:");
        R.prefixPrint();
      }
      return R;
    }
        
    R = H.intersection(F.farkas(Convex_Combination_Farkas));
    if (omegaLib.trace)  {
      System.out.println("] Result of FastTightHull:\n");
      R.prefixPrint();
    }
    return R;
  }

  private RelBody quickHull(Vector Rs)
  {
    olAssert(0 != Rs.size());

    RelBody R1 = (RelBody) Rs.elementAt(0);
    if (Rs.size() == 1)
      return R1;

    RelBody result = R1.trueRelation();

    result.copyNames(R1);

    omegaLib.useUglyNames++; 

    RelBody R  = R1;
    int     rl = Rs.size();
    for (int i = 1; i < rl; i++) {
      RelBody r = (RelBody) Rs.elementAt(i);
      R = R.union(r);
    }
        
    if (omegaLib.trace) 
      R1.prefixPrint();

    olAssert(R1.isSimplified());

    omegaLib.useUglyNames++;

    R1.queryDNF().convertEQstoGEQs();

    omegaLib.useUglyNames--;

    Rs.setElementAt(R1, 1);

    if (omegaLib.trace) 
      for (int i = 0; i < rl; i++) {
        RelBody r = (RelBody) Rs.elementAt(i);
        System.out.println("#" + i);
        r.prefixPrint();
      }

    Conjunct   first  = R1.getSingleConjunct();
    Equation[] fgeqs  = first.getGEQs();
    int        fngeqs = first.getNumGEQs();

    for (int i1 = 0; i1 < fngeqs; i1++) {
      Equation ehc             = fgeqs[i1];
      int      maxConstantTerm = ehc.getConstant();
      boolean  found           = true;

      if (omegaLib.trace)
        System.out.println("searching for bound on:\n " + first.printGEQtoString(ehc));

      for (int i = 1; i < rl; i++) {
        RelBody    r           = (RelBody) Rs.elementAt(i);
        Conjunct   c           = r.getSingleConjunct();
        Equation[] eqs         = c.getEQs();
        int        neqs        = c.getNumEQs();
        Equation[] geqs        = c.getGEQs();
        int        ngeqs       = c.getNumGEQs();
        boolean    found_for_i = false;

        for (int i2 = 0; i2 < ngeqs; i2++) {
          Equation eht = geqs[i2];

          if (omegaLib.trace) {
            System.out.println("candidate:\n " + first.printGEQtoString(ehc));
            System.out.println("target:\n " + c.printGEQtoString(eht));
          }

          if (!first.parallel(ehc, c, eht))
            continue;

          if (omegaLib.trace)
            System.out.println("Found bound:\n " + c.printGEQtoString(eht));

          maxConstantTerm = max(maxConstantTerm, eht.getConstant());
          found_for_i = true;

          break;
        }

        if (found_for_i)
          continue;

        for (int i2 = 0; i2 < neqs; i2++) {
          Equation ehte = eqs[i2];
          int      h    = c.hull(ehte, first, ehc);

          if (omegaLib.trace) {
            System.out.println("Hull of:");
            System.out.print(" ");
            System.out.println(c.printGEQtoString(ehte));
            System.out.print(" ");
            System.out.println(first.printGEQtoString(ehc));
            System.out.println("is " + h);
          }

          if (h != Integer.MIN_VALUE) {
            if (omegaLib.trace)
              System.out.println("Found bound of " + h + ":\n " + c.printGEQtoString(ehte));
            maxConstantTerm = max(maxConstantTerm, h);
            found_for_i = true;
            break;
          }
        }

        if (found_for_i)
          continue;

        if (omegaLib.trace) {
          System.out.println("No bound found in:");
          System.out.println(r.printWithSubsToString(false, true));
        }

        // if nothing found 

        found = false;
        break;
      }
          
      if (found) {
        GEQHandle h = result.andWithGEQ();
        h.copyConstraint(first, ehc);
        if (omegaLib.trace)
          System.out.println("Setting constant term to " + maxConstantTerm + " in\n " + h.printToString());
        h.updateConstant(maxConstantTerm - ehc.getConstant());
        if (omegaLib.trace)
          System.out.println("Updated constraint is\n " + h.printToString());
      }
    }

    Equation[] feqs  = first.getEQs();
    int        fneqs = first.getNumEQs();

    for (int i1 = 0; i1 < fneqs; i1++) {
      Equation ehc   = feqs[i1];
      boolean  found = true;
      for (int i = 1; i < rl; i++) {
        Relation   r           = (Relation) Rs.elementAt(i);
        Conjunct   c           = r.getSingleConjunct();
        Equation[] eqs         = c.getEQs();
        int        neqs        = c.getNumEQs();
        boolean    found_for_i = false;

        for (int i2 = 0; i2 < neqs; i2++) {
          Equation eht = eqs[i2];
          if (first.constraintIsEqual(ehc, c, eht)) {
            found_for_i = true;
            break;
          }
        }
        if (found_for_i)
          continue;

        //if nothing found 
        found = false;
        break;
      }
          
      if (found) {
        EQHandle h = result.andWithEQ();
        h.copyConstraint(first, ehc);
        if (omegaLib.trace)
          System.out.println("Adding eq constraint: " + h.printToString());
      }
    }

    omegaLib.useUglyNames--;
    if (omegaLib.trace) {
      System.out.print("quick hull is of:");
      result.printWithSubs();
    }
    return result;
  }

  private RelBody betterHull(RelBody knownHull, Vector Rs, boolean stridesAllowed, boolean checkSubsets)
  {
    if ((knownHull != null) && (knownHull.ref_count > 1))
      knownHull = new RelBody(knownHull);

    int rl = Rs.size();
    olAssert(rl > 0);
    RelBody r = (RelBody) Rs.elementAt(0);

    if (rl == 1) {
      if (stridesAllowed)
        return r;

      return r.approximate(false);
    }

    if (checkSubsets) {
      boolean[] live = new boolean[rl];

      if (omegaLib.trace) {
        System.out.println("Checking subsets in hull computation:");
        printRs(Rs);
      }

      for (int i = 0; i < rl; i++)
        live[i] = true;

      for (int i = 0; i < rl; i++) {
        RelBody ri = (RelBody) Rs.elementAt(i);
        for (int j = 0; j < rl; j++) {
          if (i == j)
            continue;

          if (!live[j])
            continue;

          RelBody rj = (RelBody) Rs.elementAt(j);
          if (omegaLib.trace)
            System.out.println("checking " + i + " Is_Obvious_Subset " + j);

          if (!ri.isObviousSubset(rj))
            continue;

          if (omegaLib.trace)
            System.out.println("yes...");

          live[i] = false;
          break;
        }
      }

      for (int i = 0; i < rl; i++) {
        if (live[i])
          continue;

        if (i < rl - 1) {
          RelBody ri = (RelBody) Rs.elementAt(rl - 1);
          Rs.setElementAt(ri, i);
          live[i] = live[rl - 1];
        }

        Rs.removeElementAt(rl - 1);
        i--;
        rl--;
      }
    }

    if (omegaLib.trace) {
      System.out.println("Better Hull:");
      printRs(Rs);
      System.out.print("known hull: ");
      if (knownHull != null)
        System.out.print(knownHull.printWithSubsToString(false, true));
      System.out.println("");
    }

    RelBody hull;
    if (knownHull == null)
      hull = quickHull(Rs);
    else
      hull = quickHull(Rs).intersection(knownHull);

    hull.simplify();
    if (omegaLib.trace)
      System.out.println("quick hull: " + hull.printWithSubsToString(false, true));

    RelBody orig = r.falseRelation();
    for (int i = 0; i < rl; i++) {
      RelBody ri = (RelBody) Rs.elementAt(i);
      orig = orig.union(ri);
    }

    orig.simplify();

    for (int i = 0; i <= rl; i++) {
      RelBody ri = (RelBody) Rs.elementAt(i);
      if (!hull.isObviousTautology()) {
        ri = ri.gist(hull, 0);
        Rs.setElementAt(ri, i);
      }

      r.simplify();

      if (r.isObviousTautology())
        return hull;

      if (!r.hasSingleConjunct())
        continue;

      olAssert(r.isSimplified());

      omegaLib.useUglyNames++;

      r.queryDNF().convertEQstoGEQs();

      omegaLib.useUglyNames--;

      Rs.setElementAt(r, i);

      if (omegaLib.trace)
        System.out.println("Checking for hull constraints in:\n  " + r.printWithSubsToString(false, true));

      Conjunct   c     = r.getSingleConjunct();
      Equation[] eqs   = c.getEQs();
      int        neqs  = c.getNumEQs();
      Equation[] geqs  = c.getGEQs();
      int        ngeqs = c.getNumGEQs();

      for (int j = 0; j < ngeqs; j++) {
        Equation eh  = geqs[j];
        RelBody tmp = r.trueRelation();
        tmp.andWithGEQ(c, eh);
        if (!orig.difference(tmp).isUpperBoundSatisfiable()) 
          hull.andWithGEQ(c, eh);
      }

      for (int j = 0; j < neqs; j++) {
        Equation eh  = eqs[j];
        RelBody tmp = r.trueRelation();
        tmp.andWithEQ(c, eh);
        if (!orig.difference(tmp).isUpperBoundSatisfiable()) 
          hull.andWithEQ(c, eh);
      }
    }

    hull = orig.fastTightHull(hull);

    olAssert(hull.hasSingleConjunct());

    if (stridesAllowed)
      return hull;

    return hull.approximate(false);
  }

  private void genRelationsFromConjuncts(Vector rs)
  {
    Vector cl  = queryDNF().getConjList();
    int    cll = cl.size();
    for (int cli = 0; cli < cll; cli++) {
      Conjunct c = (Conjunct) cl.elementAt(cli);
      rs.addElement(new RelBody(this, c));
    }
  }

  public RelBody hull(boolean stridesAllowed, int effort, RelBody knownHull)
  {
    RelBody r = this;
    if (r.ref_count > 1)
      r = new RelBody(r);

    r.simplify(1, 0);

    if (!r.isUpperBoundSatisfiable())
      return r;

    Vector Rs = new Vector();
    r.genRelationsFromConjuncts(Rs);

    if (effort == 1)
      return betterHull(knownHull, Rs, stridesAllowed, false);

    return quickHull(Rs);
  }

  private RelBody hull(Vector Rs, boolean[] validMask, int effort, boolean stridesAllowed, RelBody knownHull)
  {
    // Use RelBody of index i only when validMask[i] != 0
    Vector Rs2 = new Vector();
    int    rl  = Rs.size();
    for (int i = 0; i < rl; i++) {
      if (!validMask[i])
        continue;

      RelBody r = (RelBody) Rs.elementAt(i);

      r.simplify();
      r.genRelationsFromConjuncts(Rs2);
    }

    olAssert((effort == 0) || (effort == 1));
    if (effort == 1)
      return betterHull(knownHull, Rs2, stridesAllowed, true);

    return quickHull(Rs2);
  }

  public RelBody checkForConvexPairs()
  {
    RelBody r = this;
    if (r.ref_count > 1)
      r = new RelBody(r);

    RelBody hull = r.fastTightHull(r.trueRelation());

    r.simplify(1, 0);

    if (!r.isUpperBoundSatisfiable() || (r.numberOfConjuncts() < 2))
      return r;

    Vector Rs = new Vector();
    r.genRelationsFromConjuncts(Rs);

    int       rl   = Rs.size();
    boolean[] dead = new boolean[rl];

    for (int i = 0; i < rl; i++) {
      if (dead[i]) 
        continue;

      RelBody ri = (RelBody) Rs.elementAt(i);
      for (int j = i + 1; j < rl; j++) {
        if (dead[j])
          continue;

        if (omegaLib.trace)
          System.out.println("Comparing " + i + " and " + j);

        RelBody rj   = (RelBody) Rs.elementAt(j);
        RelBody U    = ri.union(rj);
        RelBody H_ij = U.fastTightHull(hull);

        if (!H_ij.difference(U).isUpperBoundSatisfiable()) {
          ri = H_ij;
          Rs.setElementAt(ri, i);
          dead[j] = true;
          if (omegaLib.trace)
            System.out.println("Combined them");
        }
      }
    }

    int i = 0;
    while ((i < rl) && dead[i]) i++;
    olAssert(i < rl);
    r = (RelBody) Rs.elementAt(i);
    i++;
    for (; i < rl; i++) {
      if (dead[i]) 
        continue;

      RelBody ri = (RelBody) Rs.elementAt(i);
      r = r.union(ri);
    }

    return r;
  }
                
  private RelBody vennDiagramForm(Vector Rs, int next, boolean anyPositives, int weight)
  {
    RelBody context = this;
    if (context.ref_count > 1)
      context = new RelBody(context);

    if (omegaLib.trace) {
      System.out.print("[VennDiagramForm, next = ");
      System.out.print(next);
      System.out.print(", anyPositives = ");
      System.out.print(anyPositives);
      System.out.print(", weight = ");
      System.out.println(weight);
      System.out.println("context:");
      context.prefixPrint();
    }

    if (anyPositives && (weight > 3)) {
      context.simplify();
      if (!context.isUpperBoundSatisfiable())  {
        if (omegaLib.trace) 
          System.out.println("] not satisfiable");
        return context;
      }
      weight = 0;
    }

    if (next > Rs.size()) {
      if (!anyPositives) {
        if (omegaLib.trace) 
          System.out.println("] no positives");
        return context.falseRelation();
      }
      context.simplify();
      if (omegaLib.trace)  {
        System.out.println("] answer is:");
        context.prefixPrint();
      }
      return context;
    }

    RelBody rn  = (RelBody) Rs.elementAt(next);
    RelBody Pos = context.intersection(rn).vennDiagramForm(Rs, next + 1, true, weight + 2);
    RelBody Neg = context.difference(rn).vennDiagramForm(Rs, next + 1, anyPositives, weight + 1);

    if (omegaLib.trace)  {
      System.out.println("] VennDiagramForm");
      System.out.println("pos part:");
      Pos.prefixPrint();
      System.out.println("neg part:");
      Neg.prefixPrint();
    }

    return Pos.union(Neg);
  }
        
  private RelBody vennDiagramForm(RelBody context, Vector Rs)
  {
    if (context == null)
      context = trueRelation();
    else if (context.ref_count > 1)
      context = new RelBody(context);

    RelBody r1 = (RelBody) Rs.elementAt(0);

    if (omegaLib.trace) {
      System.out.println("Starting computation of VennDiagramForm");
      System.out.println("context:");
      context.prefixPrint();
      for (int i = 0; i < Rs.size(); i++) {
        RelBody ri = (RelBody) Rs.elementAt(i);
        System.out.print("#" + i + ":");
        ri.prefixPrint();
      }
    }

    return context.vennDiagramForm(Rs, 1, false, 0);
  }
        
  public RelBody vennDiagramForm(RelBody context)
  {
    RelBody r = this;
    if (r.ref_count > 1)
      r = new RelBody(r);

    if (context.ref_count > 1)
      context = new RelBody(context);

    Vector Rs = new Vector();

    r.genRelationsFromConjuncts(Rs);

    return vennDiagramForm(context, Rs);
  }


  public RelBody checkForConvexRepresentation()
  {
    RelBody r = this;
    RelBody h = r.hull(false, 1, null);
    if (!h.difference(r).isUpperBoundSatisfiable())
      return h;

    return r;
  }

  /**
   * Compute (gist r1 given r2).
   * Currently we assume that r2 has only one conjunct.
   * r2 may have zero input and output OR may have # in/out vars equal to r1.
   */
  public RelBody gistSingleConjunct(RelBody R2, int effort)
  {
    RelBody R1 = this;
    if (R1.ref_count > 1)
      R1 = new RelBody(R1);

    if (R2.ref_count > 1)
      R2 = new RelBody(R2);

    omegaLib.skipSetChecks++;

    olAssert(((R1.number_input == R2.number_input) && (R1.number_output == R2.number_output)) || ((R2.number_input == 0) && (R2.number_output == 0)));
    R1.simplify();
    R2.simplify();

    if (omegaLib.trace) {
      System.out.println("\n### GIST computation start ### [");
      R1.prefixPrint();
      R2.prefixPrint();
      System.out.println("### ###");
    }

    omegaLib.skipSetChecks--;

    boolean res = R1.gistSingleConjunctSub(R2, effort);

    if (!res) {
      omegaLib.skipSetChecks++;
      RelBody retval = trueRelation(omegaLib, R1.number_input, R2.number_output);
      if (R1.isSet() && R2.isSet()) {
        retval.setIsSet(true);
        retval.invalidateLeadingInfo(-1);
      }
      omegaLib.skipSetChecks--;
      return retval;
    }

    if (omegaLib.trace) {
      System.out.println("] ### GIST computation end ###");
      R1.prefixPrint();
      System.out.println("### ###");
    }

    return R1;
  }

  /**
   * Compute <code>gist(r1)</code> given r2. 
   * Relation <code>r2</code> can have multiple conjuncts.
   */
  public RelBody gist(RelBody R2, int effort)
  {
    RelBody R1 = this;
    if (R1.ref_count > 1)
      R1 = new RelBody(R1);

    if (R2.ref_count > 1)
      R2 = new RelBody(R2);

    omegaLib.skipSetChecks++;

    olAssert(((R1.number_input == R2.number_input) && (R1.number_output == R2.number_output)) ||
           ((R2.number_input == 0) && (R2.number_output == 0)));
    omegaLib.skipSetChecks--;
    R2.simplify();

    if (omegaLib.trace) {
      System.out.println("\n### multi-GIST computation start ### [");
      R1.prefixPrint();
      R2.prefixPrint();
      System.out.println ( "### ###");
    }

    if (!R2.isUpperBoundSatisfiable())
      return R1.trueRelation();

    if (R2.isObviousTautology()) {
      R1.simplify();
      return R1;
    }

    if (!R1.intersection(R2).isUpperBoundSatisfiable())
      return R1.falseRelation();

    int nconj1 = R1.simplifiedDNF.length();
    int nconj2 = R2.simplifiedDNF.length();

    if (omegaLib.trace && (nconj1 + nconj2 > 50)) {
      System.out.println("WOW!!!! - Gist (" + nconj1 + "%d conjuncts, " + nconj2 + " conjuncts)!!!");
      System.out.print("Base:");
      R1.prefixPrint();
      System.out.print("Context:");
      R2.prefixPrint();
    }

    if (nconj2 == 1)
      return R1.gistSingleConjunct(R2, effort);

    R1.simplify(0, 1);
    R2.simplify(0, 1);

    RelBody G    = R1.trueRelation(); 
    Vector  cl2  = R2.simplifiedDNF.getConjList();
    int     cll2 = cl2.size();
    Vector  cl1  = R1.simplifiedDNF.getConjList();
    int     cll1 = cl1.size();

    for (int icl2 = 0; icl2 < cll2; icl2++) {
      Conjunct c2   = (Conjunct) cl2.elementAt(icl2);
      RelBody  G2   = R1.falseRelation(); 

      for (int icl1 = 0; icl1 < cll1; icl1++) {
        Conjunct c1 = (Conjunct) cl1.elementAt(icl1);
        RelBody  G1 = (new RelBody(R1, c1)).gistSingleConjunct(new RelBody(R2, c2), effort);
        
        if (G1.isObviousTautology()) {
          G2.delete();
          G2 = G1;
          break;
        }

        if (!G1.isUpperBoundSatisfiable() || !G1.isExact()) {
          if (omegaLib.trace) {
            System.out.println("gist A given B is unsatisfiable");
            System.out.println ( "A:");
            (new RelBody(R1,c1)).prefixPrint();
            System.out.println ( "B:");
            (new RelBody(R2,c2)).prefixPrint();
            System.out.println ( "");
          }
          //G1 = RelBody(R1,c1);
          G1.delete();
          return R1;
        }

        if (false && G1.isExact() && !(new RelBody(R1, c1)).mustBeSubset(G1)) {
          System.out.println("Unexpected non-mustBeSubset gist result!");
          System.out.println("base: ");
          (new RelBody(R1, c1)).prefixPrint();
          System.out.println("context: ");
          (new RelBody(R2,c2)).prefixPrint();
          System.out.println("result: ");
          G1.prefixPrint();
          System.out.println("base not subseteq result: ");
          olAssert(!G1.isExact() || (new RelBody(R1, c1)).mustBeSubset(G1));
        }

        RelBody x = G2;
        G2 = G2.union(G1);
        x.delete();
      }

      G2.simplify(0, 1);
        RelBody x = G;
      G = G.intersection(G2);
        x.delete();
      G.simplify(0, 1);

      if (omegaLib.trace) {
        System.out.println("result so far is:");
        G.prefixPrint();
      }
    }
                      
    if (omegaLib.trace) {
      System.out.println("\n### end multi-GIST computation ### ]");
      System.out.println("G is:");
      G.prefixPrint();
      System.out.println("### ###");
    }

    RelBody S1 = R1.intersection(R2);
    RelBody S2 = G.intersection(R2);
    R1.delete();
    R2.delete();

    if (omegaLib.trace) {
      System.out.println("\n---.[Checking validity of the GIST result");
      System.out.println("for G=gist R1 given R2:");
      System.out.println("R1 intersect R2 is:");
      S1.printWithSubs();
      System.out.println("\nG intersect R2 is:");
      S2.printWithSubs();
      System.out.println("---.]");
    }

    olAssert(!S1.isExact() || !S2.isExact() || S1.mustBeSubset(S2) && S2.mustBeSubset(S1));

    S1.delete();
    S2.delete();

    return G;
  }

  /**
   * get a D form of the RelBody  (single conjunct).
   *  D = {[ i_1, i_2, ..., i_m] . [j_1, j_2, ..., j_m ] :
   *         (forall p, 1<= p <= m) L_p <= j_p - i_p <= U_p && 
   *      j_p - i_p == M_p alpha_p};
   *  Right now only wildcards that are in stride constraints are treated.
   */
  private RelBody getDForm()
  {
    RelBody D = new RelBody(omegaLib, number_input, number_output);
      
    makeLevelCarriedTo(number_input);
    olAssert(hasSingleConjunct());
    queryDNF().queryGuaranteedLeadingZeros(-1);
      
    RelBody diff = deltas(); 

    if (omegaLib.trace) {
      System.out.println("The RelBody projected onto differencies is:");
      diff.printWithSubs();
    }

    /* now form D */

    FAnd  N      = D.addAnd();
    int[] bounds = new int[2];
    for (int i = 1; i <= diff.numberSet(); i++) {
      diff.queryVariableBounds(diff.setVar(i), bounds);
      int l = bounds[0];
      int u = bounds[1];
      if (l != OmegaLib.negInfinity) {
        GEQHandle g = N.addGEQ(false);
        g.updateCoefficient(D.inputVar(i), -1);
        g.updateCoefficient(D.outputVar(i), 1);
        g.updateConstant(-l);
      }
      if (u != OmegaLib.posInfinity) {
        GEQHandle g = N.addGEQ(false);
        g.updateCoefficient(D.inputVar(i), 1);
        g.updateCoefficient(D.outputVar(i), -1);
        g.updateConstant(u);
      }
    }

    /* add all stride constrains if they do exist */

    Conjunct c = diff.getSingleConjunct();

    if (c.locals().size() > 0) { // there are local variables

      // now go through all the equalities
     
      int        coef = 0;
      int        pos  = 0;
      Equation[] eqs  = c.getEQs();
      int        neqs = c.getNumEQs();
      Vector     vars = c.variables();
      int        l    = vars.size();

      for (int k = 0; k < neqs; k++) {
        Equation eq = eqs[k];

        // Constraint is in stride form if it has 2 vars, 
        // one of which is wildcard. Count number if vars and wildcard vars.

        int nwild = 0;
        int nvar = 0;
        
        for (int i = 1; i < l; i++) {
          int c1 = eq.getCoefficient(i);
          if (c1 == 0)
            continue;

          VarDecl vc = (VarDecl) vars.elementAt(i);
          if (vc.kind() == VarDecl.WILDCARD_VAR) {
            coef = c1;
            nwild++;
          } else
            pos = vc.getPosition();

          nvar++;
        }

        if ((nvar == 2) && (nwild == 1)) { //stride constraint
          EQHandle e = N.addStride(coef, false);
          e.updateCoefficient(D.inputVar(pos), -1);
          e.updateCoefficient(D.outputVar(pos), 1);
        }
      }
    }
   
    D.setFinalized();
    D.simplify();
    return D;
  }

  /**
   * Get RelBody A x A describing a region of domain and range.
   *   A=Hull(Domain(R), Range(R)) intersection IterationSpace
   *   returns cross product A x A
   */
  private RelBody formRegion(RelBody iterationSpace)
  {
    RelBody H = domain().union(range());

    H.simplify(1, 1);

    omegaLib.useUglyNames++;

    H.queryDNF().convertEQstoGEQs();

    omegaLib.useUglyNames--;

    H = H.hull(false, 1, null);

    RelBody A  = H.intersection(iterationSpace);
    RelBody A1 = new RelBody(A);

    return A.crossProduct(A1);
  }

  private RelBody formRegion1(RelBody iterationSpace)
  {
    RelBody dom = domain().intersection(iterationSpace);
    RelBody ran = range().intersection(iterationSpace);
    return dom.crossProduct(ran);
  }

  /**
   * Check if we can use D instead of R.
   *  i.e.  D intersection (A cross A) is mustBeSubset of R
   */
  public boolean isDOK(RelBody D, RelBody AxA)
  {
    RelBody r = D.intersection(AxA);
    r.simplify();

    if (omegaLib.trace) {
      System.out.println("Intersection of D and AxA is:");
      r.printWithSubs();
    }  
    olAssert(mustBeSubset(r));

    return r.mustBeSubset(this);
  }

  /**
   * Check if the constraint is in the form i_k' - i_k comp_op  c.
   * Return v - the number of the var and the type of the comp_op:
   *  1 - >,  -1 - <, 0  - not in the right form
   * If this is equality constraint in the right form any 1 or -1 can be
   * returned.
   */
  private int isConstraintInDForm(Conjunct c, Equation eq)
  {
    int v     = -1;
    int c_out = 0;

    for (int i = 1; i <= number_input; i++) {
      int coli = c.findColumn(inputVar(i));
      int c_in = eq.getCoefficient(coli);
      if (c_in == 0)
        continue;

      if (v != -1)
        return 0;

      v = i;
      int colo = c.findColumn(outputVar(i));
      c_out = eq.getCoefficient(colo);
      if (c_in * c_out != -1)
        return 0;
    }

    if (c_out == 0)
      return 0;

    return (v * 2) + ((c_out > 0) ? 1 : 0);
  }

  /**
   * Check if RelBody is in the D form.
   *  D = {[ i_1, i_2, ..., i_m] . [j_1, j_2, ..., j_m ] :
   *         (forall p, 1<= p <= m) L_p <= j_p - i_p <= U_p && 
   *      j_p - i_p == M_p alpha_p};
   *  Right now we do not check for multiple stride constraints for one var.
   *  Probably they cannot exist in simplified conjunct
   *  This function will be used in assertions
   */
  private boolean isInD_DForm()
  {
    if (!hasSingleConjunct())
      return false;

    Conjunct c = getSingleConjunct();

    if (getGlobalDecls().size() != 0) // there are symbolic vars
      return false;

    if (number_input != number_output)
      return false;

    int   n  = number_input;
    int[] bl = new int[n];
    int[] bu = new int[n];

    for (int i = 1; i <=  n; i++) {
      bl[i] = 0;
      bu[i] = 0;
    }

    Equation[] eqs  = c.getEQs();
    int        neqs = c.getNumEQs();
    Vector     vars = c.variables();
    int        l    = vars.size();
    for (int k = 0; k < neqs; k++) {
      Equation eq = eqs[k];

      int res = isConstraintInDForm(c, eq);
      if (0 == res)
        return false;

      int v      = res / 2;
      int n_wild = 0;

      /**
       * Check if the constraint is a stride one. Here we say that an equality
       * constraint is a stride constraint if it has exatly one wildcard.
       * The function returns number of the wildcards in the constraint.
       * So if we know that constraint is from the RelBody in D form, then
       * it cannot have more than 1 wildcard variables, and the result of
       * this functions can be treated as bool.
       */
   
      for (int i = 1; i < l; i++) {
        int c1 = eq.getCoefficient(i);
        if (c1 == 0)
          continue;

        VarDecl vc = (VarDecl) vars.elementAt(i);
        if (vc.kind() == VarDecl.WILDCARD_VAR)
          n_wild++;
      }

      if (n_wild >= 2)
        return false;

      if (n_wild == 0) { // not stride constraint
        if ((bl[v] != 0) || (bu[v] != 0))
          return false;
        bl[v] = 1;
        bu[v] = 1;
      }
    }

    Equation[] geqs  = c.getGEQs();
    int        ngeqs = c.getNumGEQs();
    for (int k = 0; k < ngeqs; k++) {
      Equation eq  = geqs[k];
      int      res = isConstraintInDForm(c, eq);

      if (res == 0)
        return false;

      int     v   = res / 2;
      boolean pos = ((res & 1) ==  1);
      if ((pos && (bl[v] != 0)) || (!pos && (bu[v] != 0)))
        return false;

      if (pos)
        bl[v] = 1;
      else
        bu[v] = 1;
    }
   
    return true;
  }
          
  private RelBody getDPlusForm()
  {
    return getDClosure(1);
  }

  private RelBody getDStarForm()
  {
    return getDClosure(0);
  }

  /**
   * Get D+ or D* from the RelBody that is in D form
   * To get D+ calculate:
   *    D+= {[i1, i2 .. i_m] . {j1, j2, ..., j_m]:
   *     exists s s.t. s>=1 and 
   *         (forall p, 1<= p <= m) L_p * s<= j_p - i_p <= U_p*s && 
   *      j_p - i_p == M_p alpha_p};
   * To get D* calculate almost the same RelBody but s>=0.
   * Parameter n is 1 for getting D+ and 0 for  D*
   */
  private RelBody getDClosure(int n)
  {
    olAssert(isInD_DForm());
    olAssert((n == 0) || (n == 1));
   
    Conjunct c = getSingleConjunct();
    RelBody  R = new RelBody(omegaLib, number_input, number_output);

    FExists ex = R.addExists();
    VarDecl s  = ex.declare("s");
    FAnd    N  = ex.addAnd();

    /* add s>=1 or s>=0 */

    GEQHandle geqx = N.addGEQ(false);
    geqx.updateCoefficient(s, 1);
    geqx.updateConstant(-n);
   
    /* copy and modify all the EQs */

    Equation[] eqs  = c.getEQs();
    int        neqs = c.getNumEQs();
    Vector     vars = c.variables();
    int        l    = vars.size();
    for (int k = 0; k < neqs; k++) {
      Equation  eqj = eqs[k];
      EQHandle eq  = N.addEQ(false);

      eq.copyConstraint(c, eqj);

      // if it's stride constraint do not change it 
      int n_wild = 0;

      /**
       * Check if the constraint is a stride one. Here we say that an equality
       * constraint is a stride constraint if it has exatly one wildcard.
       * The function returns number of the wildcards in the constraint.
       * So if we know that constraint is from the RelBody in D form, then
       * it cannot have more than 1 wildcard variables, and the result of
       * this functions can be treated as bool.
       */
   
      for (int i = 1; i < l; i++) {
        int c1 = eqj.getCoefficient(i);
        if (c1 == 0)
          continue;

        VarDecl vc = (VarDecl) vars.elementAt(i);
        if (vc.kind() == VarDecl.WILDCARD_VAR)
          n_wild++;
      }
       
      if (0 == n_wild) { /* eq is j_k -i_k = c, replace c buy s*c */
        eq.updateCoefficient(s, eqj.getConstant());
        eq.updateConstant(-eqj.getConstant());
      }
    }

    /* copy and modify all the GEQs */

    Equation[] geqs  = c.getGEQs();
    int        ngeqs = c.getNumGEQs();
    for (int k = 0; k < ngeqs; k++) {
      Equation  eqgi = geqs[k];
      GEQHandle geq  = N.addGEQ(false);
      geq.copyConstraint(c, eqgi);
      
      /* geq is j_k -i_k >=c or i_k-j_k >=c, replace c buy s*c */

      geq.updateCoefficient(s, eqgi.getConstant());
      geq.updateConstant(-eqgi.getConstant());
    }

    R.setFinalized();

    if (omegaLib.trace) {
      System.out.println("Simplified D" + (n == 1 ? '+' : '*') + " is:\n");
      R.printWithSubs();
    }

    return R;
  }

  /**
   * Identity.
   */
  public static RelBody identity(OmegaLib omegaLib, int numberInputp)
  {
    RelBody rr = new RelBody(omegaLib, numberInputp, numberInputp);
    FAnd    f  = rr.addAnd();

    for (int i = 1; i <= numberInputp; i++) {
      EQHandle e = f.addEQ(false);
      e.updateCoefficient(rr.inputVar(i), -1);
      e.updateCoefficient(rr.outputVar(i), 1);
    }

    return rr;
  }

  public RelBody identity()
  {
     RelBody r = this;
     if (r.ref_count > 1)
       r = new RelBody(r);
    return identity(omegaLib, r.numberSet()).restrictDomain(r);
  }

  /**
   * Check whether the RelBody intersect with identity or not.
   */
  private boolean doesIntersectWithIdentity()
  {
    olAssert(number_input == number_output);

    RelBody I = identity(omegaLib, number_input);
    RelBody C = I.intersection(this);
    return C.isUpperBoundSatisfiable();
  }

  private boolean doesIncludeIdentity()
   {
     RelBody r = identity(omegaLib, number_input);
     return r.mustBeSubset(this);
  }

  private final class Closure
  {
    public RelBody rStar = null;
    public RelBody rPlus = null;
    public boolean done  = false;

    public Closure(RelBody rPlus, RelBody rStar, boolean done)
    {
      this.rStar = rStar;
      this.rPlus = rPlus;
      this.done   = done;
    }
  }

  /**
   * Bill's closure: check if it is possible to calculate transitive closure
   * of the RelBody using the Bill's algorithm.
   * Return the transitive closure RelBody if it is possible and null RelBody
   * otherwise.
   */
  private Closure billClosure(RelBody iterationSpace)
  {
    if (doesIncludeIdentity())
      return new Closure(null, null, false);

    if (omegaLib.trace)
      System.out.println("\nApplying Bill's method to calculate transitive closure");
   
    // get D and AxA
    RelBody D = getDForm();

    if (omegaLib.trace) {
      System.out.println("\n D form for the RelBody:");
      D.printWithSubs();
    }

    RelBody AxA = formRegion1(iterationSpace);

    if (omegaLib.trace) { 
      System.out.println("\n AxA for the RelBody:");
      AxA.printWithSubs();
    }

    // compute R_+  

    RelBody rPlus = D.getDPlusForm().intersection(AxA);

    if (omegaLib.trace) {
      System.out.println("\nR_+= D+ intersection AxA is:");
      rPlus.printWithSubs();
    }

    // compute R_*

    RelBody rStar = D.getDStarForm().intersection(formRegion(iterationSpace));

    if (omegaLib.trace) {
      System.out.println("\nR_*= D* intersection AxA is:");
      rStar.printWithSubs();
    }

    /*
              Check that R_+ is acyclic. 
            Given the way we constructed R_+, R_+=(R_+)+.
            As a result it's enough to verify that R_+ intersection I = 0, 
            to prove that R_+ is acyclic.
    */

  
    if (rPlus.doesIntersectWithIdentity()) {
      if (omegaLib.trace)
        System.out.println("R_+ is not acyclic.");
      return new Closure(rPlus, rStar, false);
    }

    // Check R_+ - R is mustBeSubset of R o R_+

    if (!rPlus.difference(this).mustBeSubset(composition(rPlus)))
      return new Closure(rPlus, rStar, false);

    if (omegaLib.trace)
      System.out.println("R_+ -R is a mustBeSubset of R o R_+ - good");

    // If we are here than all tests worked, and R_+ is transitive closure of R.

    return new Closure(rPlus, rStar, true);
  }

  /**
   * print the RelBody given the bounds on the iteration space
   * If the bounds are unknown (Bounds is Null), then just print RelBody
   * itself
   */
  private void printGivenBounds(RelBody bounds)
  {
    RelBody r;
    if (bounds == null)
      r = new RelBody(this);
    else 
      r = gist(bounds, 1);
    r.printWithSubs();
  }

  /**
   * Investigate closure:
   * checks if the copmuted approximation on the Transitive closure
   * is upper and lower bound. If it's both - it's exact.
   * This function doesn't return any value. It's just prints a lot
   * of debug output
   * INPUT:
   *    r  - RelBody
   *    r_closure - approximation on r+.
   *    F - iteration space
   */
  private void investigateClosure(RelBody r_closure, RelBody F)
  {
    if (F != null)
      F = F.crossProduct(F);

    System.out.println("\n\n--.investigating the closure of the RelBody:");
    printGivenBounds(F);

    System.out.println("\nComputed closure is:");
    r_closure.printGivenBounds(F);

  
    RelBody r3 = composition(r_closure);
    r3.simplify(1, 1);

    r3 = r3.union(r_closure.composition(this));
    r3.simplify(1, 1);

    r3  = r3.union(this);
    r3.simplify(1, 1);

  
    RelBody remainder = r3.difference(r_closure);

    if (F != null)
      r3 = r3.gist(F, 1);

    r3.simplify(1, 1);

    if (F != null)
      r_closure = r_closure.gist(F, 1);

    r_closure.simplify(1, 1);

    boolean LB_res = r_closure.mustBeSubset(r3);
    boolean UB_res = r3.mustBeSubset(r_closure); 

    System.out.println("\nThe results of checking closure (gist) are:");
    System.out.println("LB - " + (LB_res ? "YES" : "NO") + ", UB - " + (UB_res ? "YES" : "NO"));

    if (!UB_res) {
      remainder.simplify(2, 2);
      System.out.println("Dependences not included include:");
      remainder.printGivenBounds(F);
    }
  }  

  private RelBody composeN(int n)
  {
    RelBody r = this;
    if (n == 1)
      return r;

    return r.composition(r.composeN(n - 1));
  }

  private RelBody approxClosure(int n)
  {
    RelBody r_closure = new RelBody(this); 

    for (int i = 2; i <= n; i++)
      r_closure = r_closure.union(composeN(n));

    r_closure = r_closure.union(r_closure.unknownRelation());
      
    return r_closure;
  }

  private boolean isClosureItself()
  {
    return  composition(this).mustBeSubset(this);
  }

  private void dimensions(int[] dims)
  {
    simplify(2, 2);
    queryDNF().dimensions(this, dims);
  }

  /**
   * Transitive closure of the RelBody containing single conjunct
   */
  private Closure conjunctTransitiveClosure(RelBody iterationSpace)
  {
    RelBody R = this;
    if (R.ref_count > 1)
      R = new RelBody(R);

    olAssert(R.hasSingleConjunct());

    if (omegaLib.trace) {
      System.out.println("\nTaking closure of the single conjunct: [");
      R.printWithSubs();
    }

    if (R.isClosureItself()) {
      int[] dims = new int[2];
      R.dimensions(dims);
      int ndim_all = dims[0];
      int ndim_domain = dims[1];

      if (ndim_all == ndim_domain + 1) {
        RelBody ispace = R.domain().crossProduct(R.range());
        RelBody R_zero = ispace.intersection(identity(omegaLib, R.number_input));
        RelBody rStar = R.union(R_zero).hull(true, 1, ispace);
        RelBody rPlus = R;

        if (omegaLib.trace) {
        System.out.println("\n] For this RelBody R+=R");
        System.out.println("R*:");
        rStar.printWithSubs();
        }

        return new Closure(rPlus, rStar, true);
      }

      if (omegaLib.trace)
        System.out.println("\n] For this RelBody R+=R, not appropriate for R*");

      return new Closure(null, null, false);
    }

    if (iterationSpace != null) {
      // Bill's closure requires the information about Iteration Space. 
      // So if IterationSpace is NULL, i.e. unknown( e.g. when calling from parser, 
      // we do not do Bill's closure 
             
      Closure closure = R.billClosure(iterationSpace);
      if (omegaLib.trace) {
        if (!closure.done)
          System.out.println("Bill's closure is not applicable");
        else {
          System.out.println("Bill's closure is applicable");
          System.out.println(" For R:");
          R.printWithSubs();
          System.out.println("R+ is:");
          closure.rPlus.printWithSubs();
          System.out.println("R* is:");
          closure.rStar.printWithSubs();
          System.out.println("");
          R.investigateClosure(closure.rPlus, iterationSpace);
        }       
      }

      if (closure.done) {
        if (omegaLib.trace)
          System.out.println("]");
        return closure;
      }
    } 

    // Do and check approximate closure (several compositions).

    RelBody rPlus  = R.approxClosure(2);

    if (omegaLib.trace) {
      System.out.println("Doing approximate closure");
      R.investigateClosure(rPlus, iterationSpace);
      System.out.println("]");
    }

    return new Closure(rPlus, null, false);
  }

  /**
   * Try to get conjunct transitive closure.
   * it we can get it easy get it, return true.
   * if not - return false
   */
  public RelBody tryConjunctTransitiveClosure(RelBody iterationSpace, RelBody rPlus)
  {
    RelBody R = this;
    if (R.ref_count > 1)
      R = new RelBody(R);

    olAssert(R.hasSingleConjunct());

    if (omegaLib.trace) {
      System.out.println("\nTrying to take closure of the single conjunct: [");
      R.printWithSubs();
    }

    if (R.isClosureItself()) { 
      if (omegaLib.trace)
        System.out.println("\n ]The RelBody is closure itself. Leave it alone");
      return null;
    }

    olAssert(iterationSpace != null);
    Closure closure = R.billClosure(iterationSpace);
    RelBody rStar = closure.rStar;

    if (omegaLib.trace) {
      if (!closure.done)
        System.out.println("]Bill's closure is not applicable");
      else {
        System.out.println("]Bill's closure is applicable");
        System.out.println(" For R:");
        R.printWithSubs();
        System.out.println("R+ is:");
        closure.rPlus.printWithSubs();
        System.out.println("R* is:");
        rStar.printWithSubs();
        System.out.println("");
        R.investigateClosure(rPlus, iterationSpace);
      } 
    }

    return rStar; 
  }

  public boolean equal(RelBody r2)
  {
    boolean res = mustBeSubset(r2);
    if (!res)
      return false;
    return r2.mustBeSubset(this);
  }

  private void appendClausesToList(Vector L) 
  {
    makeLevelCarriedTo(number_input);
    simplify(2, 2);
    for (int depth = number_input; depth >= -1; depth--)
      queryDNF().appendClausesToList(L, depth, this);
  }

  private void printRelBodyList(Vector L) 
  {
    int l = L.size();
    for (int i = 0; i < l; i++) {
      RelBody r = (RelBody) L.elementAt(i);
      r.printWithSubs();
    }
  }

  public RelBody decoupledConvexHull()
  {
    RelBody r = approximate(false);
    if (r.hasSingleConjunct())
      return r;
    return r.farkas(Decoupled_Farkas).farkas(Convex_Combination_Farkas);
  }

  public RelBody convexHull()
  {
    RelBody r = approximate(false);
    if (r.hasSingleConjunct())
      return r;
    return r.farkas(Basic_Farkas).farkas(Convex_Combination_Farkas);
  }

  public RelBody affineHull()
  {
    return farkas(Basic_Farkas).farkas(Affine_Combination_Farkas);
  }

  public RelBody linearHull()
  {
    return farkas(Basic_Farkas).farkas(Linear_Combination_Farkas);
  }

  public RelBody conicHull()
  {
    return farkas(Basic_Farkas).farkas(Positive_Combination_Farkas);
  }

  public RelBody conicClosure()
  {
    olAssert(number_input == number_output);

    return deltas().conicHull().deltasToRelation(number_input, number_input);
  }

  public RelBody join(RelBody G)
  {
    return composition(G);
  }

  /**
   * Transitive closure of the RelBody containing multiple conjuncts.
   */
  public RelBody transitiveClosure0(int maxExpansion, RelBody iterationSpace)
  {
    RelBody r = this;
    if (r.ref_count > 1)
      r = new RelBody(r);

    if (iterationSpace.ref_count > 1)
      iterationSpace = new RelBody(iterationSpace);

    if (omegaLib.trace) 
      System.out.println("\n\n[Transitive closure\n");

    olAssert(r.number_input == r.number_output);

    if (r.maxUfsArity() > 0)
      throw new polyglot.util.InternalCompilerError("Can't take transitive closure with UFS yet.");

    r.simplify(2, 2);
    if (!r.isUpperBoundSatisfiable()) {
      if (omegaLib.trace) 
        System.out.println("]TC : RelBody is false");
      return r;
    }

    iterationSpace = r.domain().union(r.range()).hull(true, 1, iterationSpace);

    if (omegaLib.trace) {
      System.out.println("r is:");
      r.printWithSubs();
      System.out.println("IS is:");
      iterationSpace.printWithSubs();
    }

    RelBody dom = r.domain();
    dom.simplify(2, 1);
    RelBody rng = r.range(); 
    rng.simplify(2, 1);
    RelBody AC = r.restrictDomain(rng).restrictRange(dom).conicClosure();
    RelBody UB = r.union(r.join(AC.join(r)));
    UB.simplify(2, 1);
      
    if (omegaLib.trace) {
      System.out.println("UB is:");
      UB.printWithSubs();
    }

    RelBody result       = r.falseRelation();
    Vector  firstChoice  = new Vector();
    Vector  secondChoice = new Vector();

    r.simplify(2, 2);

    RelBody test = r.difference(r.composition(r));
    test.simplify(2, 2);

    if (r.numberOfConjuncts() > test.numberOfConjuncts())  {
      RelBody test2 = test.union(test.composition(test));
      test2.simplify(2, 2);

      if (r.mustBeSubset(test2))
        r = test;
      else if (omegaLib.trace) {
        System.out.println("Transitive reduction not possible:");
        System.out.println("R is:");
        r.printWithSubs();
        System.out.println("test2 is:");
        test2.printWithSubs();
      }
    }

    r.makeLevelCarriedTo(r.number_input);

    if (omegaLib.trace) {
      System.out.println("r is:");
      r.printWithSubs();
    }

    for (int depth = r.number_input; depth >= -1; depth--)
      r.queryDNF().appendClausesToList(firstChoice, depth, r);

    boolean first_conj = true;
    int     sll        = firstChoice.size();
    for (int i = 0; i < sll; i++) {
      RelBody sli = (RelBody) firstChoice.elementAt(i);
      if (first_conj)
        first_conj = false;
      else {
        RelBody C_plus = sli.tryConjunctTransitiveClosure(iterationSpace, null);
        if (C_plus != null)
          firstChoice.setElementAt(C_plus, i);
      }
    }
        
    // compute closure

    int     maxClauses      = 3 + firstChoice.size() * (1 + maxExpansion);
    int     resultConjuncts = 0;
    int     numFails        = 0;
    boolean resultInexact   = false;

    while ((0 != firstChoice.size()) || (0 != secondChoice.size())) {
      if (omegaLib.trace) {
        System.out.println("Main loop of TC:");
        if (0 != firstChoice.size()) {
        System.out.println("First choice:");
        printRelBodyList(firstChoice);
        }
        if (0 != secondChoice.size()) {
        System.out.println("Second choice:");
        printRelBodyList(secondChoice);
        }
      }
    
      RelBody R;
      if (0 != firstChoice.size()) 
        R = (RelBody) firstChoice.remove(0);
      else
        R = (RelBody) secondChoice.remove(0);

      if (omegaLib.trace) {
        System.out.println("Working with conjunct:");
        R.printWithSubs();
      }

      Closure closure = R.conjunctTransitiveClosure(iterationSpace);
      boolean known   = closure.done;
      RelBody rPlus  = closure.rPlus;
      RelBody rStar  = closure.rStar;

      if ((rPlus == null) && (numFails < firstChoice.size())) {
        numFails++;
        firstChoice.addElement(R);
        if (omegaLib.trace) {
        System.out.println("\nTry another conjunct, R is not suitable");
        R.printWithSubs();
        }
        continue;
      }

      if (omegaLib.trace) {
        System.out.println("\nR+ is:");
        if (rPlus != null) {
          rPlus.printWithSubs();
          System.out.println("Known R? is :");
          rStar.printWithSubs();
        } else
          System.out.println("The R* for this RelBody is not calculated");
      }
          
      RelBody R_z;
      if (rPlus != null) {
        R_z = rStar.difference(rPlus);
        known = R_z.isUpperBoundSatisfiable();
        if (known) {
          int d = R.getSingleConjunct().queryGuaranteedLeadingZeros();
          R_z.makeLevelCarriedTo(min(R.number_input, d + 1));

          if (R_z.queryDNF().length() > 1)
            known = false;

          if (omegaLib.trace)
            System.out.println("\nForced R_Z to be level carried at level " + min(R.number_input, d + 1));
        }
        if (omegaLib.trace) {
          if (known) {
            System.out.println("\nDifference between R? and R+ is:");
            R_z.printWithSubs();
          } else
            System.out.println("\nR_z is unusable");
        }
      } else
        R_z = r.falseRelation();

      if (!known)
        numFails++;
      else
        numFails = 0;

      if (!known && (numFails <= firstChoice.size())) {
        firstChoice.addElement(R);
        if (omegaLib.trace) {
        System.out.println("\nTry another conjunct, Rz is avaiable for R:");
        R.printWithSubs();
        }
        continue;
      }

      //make N empty list
      RelBody N = r.falseRelation();
   
      //append R+ to T
      result = result.union(rPlus);
      resultConjuncts++;

      int expansion = maxClauses - (resultConjuncts + 2 * firstChoice.size() + secondChoice.size());
      if (expansion < 0)
        expansion = 0;

      if (omegaLib.trace) {
        System.out.println("Max clauses = " + maxClauses);
        System.out.println("result conjuncts =  " + resultConjuncts);
        System.out.println("firstChoice's =  " + firstChoice.size());
        System.out.println("secondChoice's =  " + secondChoice.size());
        System.out.println("Allowed expansion is " + expansion);
      }

      if (!known && (expansion == 0)) {
        if (omegaLib.trace) {
          System.out.println("Expansion = 0, R? unknown, skipping composition");
          if (!resultInexact)
            System.out.println("RESULT BECOMES INEXACT 1");
        }
        resultInexact = true;
      } else {
        Vector list1 = firstChoice;
        Vector list2 = secondChoice;
        int    sl    = list1.size();
        for (int sli = 0; true; sli++) {
          if (sli >= sl) {
            list1 = list2;
            if (list1 == null)
              break;
            list2 = null;
            sli = 0;
            sl = list1.size();
        }

          RelBody C = (RelBody) list1.elementAt(sli);

        if (omegaLib.trace) {
          System.out.println("\nComposing chosen conjunct with C:");
          C.printWithSubs();
        }

        if (!known) {
          if (omegaLib.trace)
            System.out.println("\nR? is unknown! No debug info here yet");

          RelBody C1 = C.composition(rPlus);

          if (omegaLib.trace) {
            System.out.println("\nGenerating");
            C1.printWithSubs();
          }

          C1.simplify();
          RelBody newStuff = C1.difference(C).difference(rPlus);
          newStuff.simplify();

          if (omegaLib.trace) {
            System.out.println("New Stuff:");
            newStuff.printWithSubs();
          }

          boolean C1_contains_new_stuff = newStuff.isUpperBoundSatisfiable();

          if (C1_contains_new_stuff) {
            if (newStuff.hasSingleConjunct())
              C1 = newStuff;
            if (expansion != 0) {
              N = N.union(C1);
              expansion--;
            } else {
              if (!resultInexact && omegaLib.trace)
                System.out.println("RESULT BECOMES INEXACT 2");
              resultInexact = true;
              break;
            }
          } else
            C1 = C1.falseRelation();

          RelBody C2 = new RelBody(rPlus.composition(C));

          if (omegaLib.trace) {
            System.out.println("\nGenerating");
            C2.printWithSubs();
          }

          newStuff = C2.difference(C).difference(C1).difference(rPlus);
          newStuff.simplify();

          if (omegaLib.trace) {
            System.out.println("New Stuff:");
            newStuff.printWithSubs();
          }

          if (newStuff.isUpperBoundSatisfiable())  {
            if (newStuff.hasSingleConjunct())
              C2 = new RelBody(newStuff);
            if (expansion != 0) {
              N = N.union(C2);
              expansion--;
            } else {
              if (!resultInexact && omegaLib.trace)
                System.out.println("RESULT BECOMES INEXACT 3");
              resultInexact = true;
              break;
            }
          } else
            C2 = C2.falseRelation();
                           
          if (!C1_contains_new_stuff)
            continue;

          RelBody C3 = new RelBody(rPlus.composition(C1));
          if (omegaLib.trace) {
            System.out.println("\nGenerating");
            C3.printWithSubs();
          }

          newStuff = C3.difference(C).difference(C1).difference(C2).difference(rPlus);
          newStuff.simplify();

          if (omegaLib.trace) {
            System.out.println("New Stuff:");
            newStuff.printWithSubs();
          }

          if (!newStuff.isUpperBoundSatisfiable())
            continue;

          if (newStuff.hasSingleConjunct())
            C3 = new RelBody(newStuff);

          if (expansion != 0) {
            N = N.union(C3);
            expansion--;
              continue;
          }

          if (!resultInexact && omegaLib.trace)
            System.out.println("RESULT BECOMES INEXACT 4");

          resultInexact = true;
          break;
        }

        RelBody C_Rz = new RelBody(C.composition(R_z));
        if (omegaLib.trace) {
          System.out.println("C o Rz is:");
          C_Rz.printWithSubs();
        }

        RelBody Rz_C_Rz = new RelBody(R_z.composition(C_Rz));
        if (omegaLib.trace) {
          System.out.println("\nRz o C o Rz is:");
          Rz_C_Rz.printWithSubs();
        } 
          
        if (C.equal(Rz_C_Rz)) {
          RelBody tmp = C.composition(rStar);
          tmp.simplify();
          RelBody tmp2 = rStar.composition(tmp);
          tmp2.simplify();

            RelBody s = tmp2;
          if (tmp2.mustBeSubset(tmp))
            s = tmp;
            list1.setElementAt(s, sli);

          if (omegaLib.trace) {
            System.out.println("\nC is equal to Rz o C o Rz so  R? o C o R? replaces C");
            System.out.println("R? o C o R? is:");
            s.printWithSubs();
          }
          continue;
        }

        if (C.equal(C_Rz)) {
          RelBody s = C.composition(rStar);
          list1.setElementAt(s, sli);

          RelBody  p = new RelBody(rPlus.composition(s));
          p.simplify();

          if (omegaLib.trace) {
            System.out.println("\nC is equal to C o Rz, so C o Rz replaces C");
            System.out.println("C o R? is:");
            s.printWithSubs();
            System.out.println("R+ o C o R? is added to list N. It's :");
            p.printWithSubs();
          }

          if (!p.isObviousSubset(rPlus) && !p.isObviousSubset(C)) {
            if (expansion != 0)  {
              p.simplify(2, 2); 
              expansion--;
            } else {
              if (!resultInexact && omegaLib.trace)
                System.out.println("RESULT BECOMES INEXACT 5");
              resultInexact = true;
              break;
            }
          }
        }

        RelBody Rz_C = new RelBody(R_z.composition(C));

        if (C.equal(Rz_C)) {
          RelBody s = rStar.composition(C);
          list1.setElementAt(s, sli);
          RelBody Rstar_C_Rplus = new RelBody(s.composition(rPlus));   
          Rstar_C_Rplus.simplify();

          if (omegaLib.trace) {
            System.out.println("\nC is equal to Rz o C , so R? o C replaces C");
            System.out.println("R? o C is:");
            s.printWithSubs();
            System.out.println("R+ o C is added to list N. It's :");
            Rstar_C_Rplus.printWithSubs();
          }

          if (!Rstar_C_Rplus.isObviousSubset(rPlus)
              && !Rstar_C_Rplus.isObviousSubset(C))
            if (expansion != 0) 
              N = N.union(Rstar_C_Rplus);
            else {
              if (!resultInexact && omegaLib.trace)
                System.out.println("RESULT BECOMES INEXACT 6");
              resultInexact = true;
              break;
            }
          continue;
        }

        if (omegaLib.trace)
          System.out.println("\nHave to handle it the hard way");

        RelBody C1 = C.composition(rPlus);
        C1.simplify();

        if (!C1.isObviousSubset(rPlus) && !C1.isObviousSubset(C)) {
          if (expansion != 0) {
            N = N.union(C1);
            expansion--;
          } else {
            if (!resultInexact && omegaLib.trace)
              System.out.println("RESULT BECOMES INEXACT 7");
            resultInexact = true;
            break;
          }
        }

        RelBody C2 = new RelBody(rPlus.composition(C));
        C2.simplify();
        if (!C2.isObviousSubset(rPlus) && !C2.isObviousSubset(C)) {
          if (expansion != 0) {
            N = N.union(C2);
            expansion--;
          } else {
            if (!resultInexact && omegaLib.trace)  {
              System.out.println("RESULT BECOMES INEXACT 8");
              System.out.println("Have to discard:");
              C2.printWithSubs();
            }
            resultInexact = true;
            break;
          }
        }

        RelBody C3 = new RelBody(rPlus.composition(C1));
        C3.simplify();

        if (C3.isObviousSubset(rPlus) || C3.isObviousSubset(C))
          continue;

        if (expansion != 0) {
          N = N.union(C3);
          expansion--;
            continue;
        }

        if (!resultInexact && omegaLib.trace)
          System.out.println("RESULT BECOMES INEXACT 9");

        resultInexact = true;
        break;
        }
      }

      // now we processed the first conjunct.

      if (omegaLib.trace) {
        N.simplify(2, 2);
        System.out.println("\nNew conjuncts:");
        N.printWithSubs();
      }
          
      N.simplify(2, 2);
      N.appendClausesToList(secondChoice);
    }
    
    // Did we do all conjuncts? If not, make T be inexact

    result.copyNames(r);

    result.simplify(2, 2);

    if (!result.isExact()) {
      result.interpretUnknownAsFalse();
      resultInexact = true;
    }

    if (resultInexact) {
      test = new RelBody(result.composition(result));
      test.simplify(2, 2);

      if (omegaLib.trace) {
        System.out.println("\nResult is:");
        result.printWithSubs();
        System.out.println("\nResult composed with itself is:");
        test.printWithSubs();
      }

      if (!test.mustBeSubset(result))
        result = result.union(UB.intersection(result.unknownRelation()));
    }

    if (omegaLib.trace)  {
      System.out.println("\nThe transitive closure is :");
      result.printWithSubs();
      System.out.println("\n\n] END Transitive closure\n");
    }

    return result;
  }

  public RelBody transitiveClosure(int maxExpansion, RelBody iterationSpace)
  {
    RelBody r = this;
    if (r.ref_count > 1)
      r = new RelBody(r);

    if (iterationSpace.ref_count > 1)
      iterationSpace = new RelBody(iterationSpace);

  
    if (omegaLib.trace) {
      System.out.println("\nComputing Transitive closure of:");
      r.printWithSubs();
      System.out.println("\nIteration space is:");
      iterationSpace.printWithSubs();
    }

    if (!r.isUpperBoundSatisfiable()) {
      if (omegaLib.trace) 
        System.out.println("]TC : RelBody is false");
      return r;
    }

    RelBody UB = r.deltas().projectSym().conicHull().deltasToRelation(r.number_input, r.number_output);
    UB.simplify();

    if (omegaLib.trace) {
      System.out.println("UB is:");
      UB.printWithSubs();
    }

    RelBody conditions = UB.composition(r.domain());

    if (omegaLib.trace) {
      System.out.println("Forward reachable is:");
      conditions.printWithSubs();
    }

    conditions = UB.inverse().composition(conditions);

    if (omegaLib.trace) {
      System.out.println("Backward/forward reachable is:");
      conditions.printWithSubs();
    }

    conditions.simplify();
    conditions = conditions.vennDiagramForm(null);
    conditions.simplify();

    if (conditions.isObviousTautology())
      return r.transitiveClosure0(maxExpansion, iterationSpace);

    if (omegaLib.trace) {
      System.out.println("Condition regions are:");
      conditions.printWithSubs();
    }

    RelBody answer = r.falseRelation();
    answer.copyNames(r);

    Vector cl  = conditions.queryDNF().getConjList();
    int    cll = cl.size();
    for (int i = 0; i < cll; i++) {
      Conjunct c   = (Conjunct) cl.elementAt(i);
      RelBody  tmp = new RelBody(conditions, c);

      if (omegaLib.trace) {
        System.out.println("\nComputing Transitive closure:");
        System.out.println("\nRegion:");
        tmp.prefixPrint();
      }

      RelBody tmp3 = r.restrictDomain(tmp);

      tmp3.simplify(2, 2);

      if (omegaLib.trace) {
        System.out.println("\nRelBody:");
        tmp3.prefixPrint();
      }

      answer = answer.union(tmp3.transitiveClosure0(maxExpansion, iterationSpace));
    }

    return answer;
  }
}
