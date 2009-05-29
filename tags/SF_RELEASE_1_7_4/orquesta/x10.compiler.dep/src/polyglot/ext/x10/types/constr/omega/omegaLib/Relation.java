package polyglot.ext.x10.types.constr.omega.omegaLib;

import java.util.Iterator;
import java.util.*;

/**
 * Relation.
 * <p>
 * $Id: Relation.java,v 1.1 2008-03-06 18:02:23 nystrom Exp $
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
public final class Relation
{
  private static int createdCount = 0; /* A count of all the instances of this class that were created. */

  public static int created()
  {
    return createdCount;
  }

  /**
   * Keep the count of instances up-to-date.
   * @exception Throwable [needs description]
   */
  protected void finalize() throws Throwable
  {
    super.finalize();
    if (rel_body != null)
      rel_body.delete();
    rel_body = null;
  }

  private RelBody rel_body;

  /**
   * The Omega tuple data structure which holds the global variables
   * used in the equations constructed for the Omega test.
   */
  private HashMap freeVars;

  private OmegaLib omegaLib;

  public Relation(OmegaLib omegaLib)
  {
    this.omegaLib = omegaLib;
    this.rel_body = null;
    createdCount++;
  }

  /**
   * Create a relation. Its will be built later.
   */
  public Relation(OmegaLib omegaLib, int numberInput, int numberOutput)
  {
    this.omegaLib = omegaLib;
    this.rel_body = new RelBody(omegaLib, numberInput, numberOutput);
    this.rel_body.incrementRefCount();
    createdCount++;
  }

  public Relation(OmegaLib omegaLib, int nci)
  {
    this(omegaLib, nci, 0);
    rel_body.setIsSet(true);
    rel_body.invalidateLeadingInfo(-1);
  }

  public Relation(Relation r)
  {
    if (r.rel_body.isFinalized()) {
      rel_body = r.rel_body;
    } else {
      if (r.rel_body.isShared())
        throw new polyglot.util.InternalCompilerError("Body is shared.");
      rel_body = new RelBody(r.rel_body);
    }

    rel_body.incrementRefCount();

    createdCount++;
  }

  public Relation(Relation r, Conjunct c)
  {
    rel_body = new RelBody(r.rel_body, c);
    rel_body.incrementRefCount();
    createdCount++;
  }

  public Relation(RelBody r, Conjunct c)
  {
    rel_body = new RelBody(r, c);
    rel_body.incrementRefCount();
    createdCount++;
  }

  public Relation(RelBody r)
  {
    rel_body = r;
    rel_body.incrementRefCount();
    createdCount++;
  }

  public void delete()
  {
    if (rel_body == null)
      return;

    rel_body.delete();

    rel_body = null;
  }

  public String toString()
  {
    StringBuffer buf = new StringBuffer("(Relation 0x");
    buf.append(Integer.toHexString(hashCode()));
    buf.append(' ');
    buf.append(rel_body);
    buf.append(')');
    return buf.toString();
  }

  public void setFinalized()
  {
    if (rel_body != null)
      rel_body.setFinalized();
    Runtime.getRuntime().runFinalization();
  }

  public void putVar(String name, Object var)
  {
    if (freeVars == null)
      freeVars = new HashMap(23);
    freeVars.put(name, var);
  }

  public Object getVar(String name)
  {
    if (freeVars == null)
      return null;
    return freeVars.get(name);
  }

  public HashMap getVars()
  {
    return freeVars;
  }

  public RelBody getRelBody()
  {
    return rel_body;
  }

  public FForall addForall()
  {
    return rel_body.addForall();
  }

  public FExists addExists()
  {
    return rel_body.addExists();
  }

  public FAnd addAnd()
  {
    return rel_body.addAnd();
  }

  public FAnd andWith()
  {
    return rel_body.andWith();
  }

  public FOr addOr()
  {
    return rel_body.addOr();
  }

  public FNot addNot()
  {
    return rel_body.addNot();
  }

  public boolean isSet()
  {
    return rel_body.isSet();
  }  

  public int numberInput()
  {
    return rel_body.numberInput();
  }

  public int numberOutput()
  {
    return rel_body.numberOutput();
  }

  public int numberSet()
  {
    return rel_body.numberSet();
  }

  public VarDecl inputVar(int nth)
  {
    return rel_body.inputVar(nth);
  }

  public VarDecl outputVar(int nth)
  {
    return rel_body.outputVar(nth);
  }

  public VarDecl setVar(int nth)
  {
    return rel_body.setVar(nth);
  }

  public VarDecl getLocal(VarDecl v)
  {
    return rel_body.getLocal(v);
  }

  public VarDecl getLocal(GlobalVarDecl G)
  {
    return rel_body.getLocal(G);
  }

  public VarDecl getLocal(GlobalVarDecl G, int of)
  {
    return rel_body.getLocal(G, of);
  }

  public void nameInputVar(int nth, String S)
  {
    rel_body.nameInputVar(nth, S);
  }

  public void nameOutputVar(int nth, String S)
  {
    rel_body.nameOutputVar(nth, S);
  }

  public void nameSetVar(int nth, String S)
  {
    rel_body.nameSetVar(nth, S);
  }

  public FAnd andWithAnd()
  {
    return rel_body.andWithAnd();
  }

  private EQHandle andWithEQ()
  {
    return rel_body.andWithEQ();
  }

  private EQHandle andWithEQ(Conjunct c, Equation eq)
  {
    return rel_body.andWithEQ(c, eq);
  }

  public void prefixPrint()
  {
    rel_body.prefixPrint();
  }

  public void prefixPrint(boolean debug)
  {
    rel_body.prefixPrint(debug);
  }

  public void printWithSubs()
  {
    rel_body.printWithSubs();
  }

  public boolean isLowerBoundSatisfiable()
  {
    return rel_body.isLowerBoundSatisfiable();
  }

  public boolean isUpperBoundDefinitelyNotSatisfiable()
  {
    return rel_body.isUpperBoundDefinitelyNotSatisfiable();
  }

  public boolean isUpperBoundSatisfiable()
  {
    return rel_body.isUpperBoundSatisfiable();
  }

  public boolean isSatisfiable()
  {
    return rel_body.isSatisfiable();
  }

  public boolean isNotSatisfiable()
  {
    return rel_body.isNotSatisfiable();
  }

  public boolean isObviousTautology()
  {
    return rel_body.isObviousTautology();
  }

  public int numberOfConjuncts()
  {
    return rel_body.numberOfConjuncts();
  }

  public void simplify()
  {
    rel_body.simplify();
  }

  public Conjunct removeFirstConjunct()
  {
    return rel_body.removeFirstConjunct();
  }

  public Conjunct getSingleConjunct()
  {
    return rel_body.getSingleConjunct();
  }

  public boolean hasSingleConjunct()
  {
    return rel_body.hasSingleConjunct();
  }

  public boolean queryDifference(VarDecl v1, VarDecl v2, int[] bounds)
  {
    return rel_body.queryDifference(v1, v2, bounds);
  }

  public void queryVariableBounds(VarDecl v, int[] bounds)
  {
    rel_body.queryVariableBounds(v, bounds);
  }

  public Relation makeLevelCarriedTo(int level)
  {
    return new Relation(rel_body.makeLevelCarriedTo(level));
  }

  public Relation extractDNFByCarriedLevel(int level, int direction)
  {
    return new Relation(rel_body.extractDNFByCarriedLevel(level, direction));
  }

  public void calculateDimensions(Conjunct c, int[] dims)
  {
    rel_body.calculateDimensions(c, dims);
  }

  public Relation approximate()
  {
    return new Relation(rel_body.approximate(false));
  }

  public Relation approximate(boolean strides_allowed)
  {
    return new Relation(rel_body.approximate(strides_allowed));
  }

  /**
   * Project away all input and output variables.
   */
  public Relation projectOnSym(Relation input_context)
  {
    return new Relation(rel_body.projectOnSym(input_context.rel_body));
  }

  /**
   * Project out global variable g from relation r.
   */
  public Relation project(GlobalVarDecl g)
  {
    return new Relation(rel_body.project(g));
  }

  /**
   * Project all symbolic variables from relation r.
   */
  public Relation projectSym()
  {
    return new Relation(rel_body.projectSym());
  }

  /**
   * Project an input, output or set variable, leaving a variable in that position
   * with no constraints.
   */
  public Relation project(int pos, int vkind)
  {
    return new Relation(rel_body.project(pos, vkind));
  }

  public Relation project(Vector s)
  {
    return new Relation(rel_body.project(s));
  }

  /**
   *      r1 Union r2.
   *            align the input tuples (if any) for F and G
   *            align the output tuples (if any) for F and G
   *            match named variables in F and G
   *            formula is f | g
   */
  public Relation union(Relation input_r2)
  {
    return new Relation(rel_body.union(input_r2.rel_body));
  }

  /**
   * F intersection G.
   *            align the input tuples (if any) for F and G
   *            align the output tuples (if any) for F and G
   *            match named variables in F and G
   *            formula is f & g
   */
  public Relation intersection(Relation input_r2)
  {
    return new Relation(rel_body.intersection(input_r2.rel_body));
  }

  /**
   *    F \ G (the relation F restricted to domain G).
   *            align the input tuples for F and G
   *            match named variables in F and G
   *            formula is f & g
   */
  public Relation restrictDomain(Relation input_r2)
  {
    return new Relation(rel_body.restrictDomain(input_r2.rel_body));
  }

  /**
   *    F / G (the relation F restricted to range G)
   *            align the output tuples for F and G
   *            match named variables in F and G
   *            formula is f & g
   */
  public Relation restrictRange(Relation input_r2)
  {
    return new Relation(rel_body.restrictRange(input_r2.rel_body));
  }

  /**
   * Domain and Range.
   * Make output (input) variables wildcards and simplify.
   * Move all UFS's to have have the remaining tuple as an argument,
   *   and maprel will move them to the set tuple
   * RESET all leading 0's
   */
  public Relation domain()
  {
    return new Relation(rel_body.domain());
  }

  public Relation range()
  {
    return new Relation(rel_body.range());
  }

  /**
   * Cross Product.  Give two sets, A and B, create a relation whose
   * domain is A and whose range is B.
   */
  public Relation crossProduct(Relation input_B)
  {
    return new Relation(rel_body.crossProduct(input_B.rel_body));
  }

  /**
   *    inverse F -reverse the input and output tuples.
   */
  public Relation inverse()
  {
    return new Relation(rel_body.inverse());
  }

  public Relation after(int carried_by, int new_output, int dir)
  {
    return new Relation(rel_body.after(carried_by, new_output, dir));
  }

  /**
   * Deltas(F)
   *   Return a set such that the ith variable is old Out_i - In_i
   *   Delta variables are created as input variables.
   *   Then input and output variables are projected out.
   */
  public Relation deltas()
  {
    return new Relation(rel_body.deltas());
  }

  public Relation deltas(int eq_no)
  {
    return new Relation(rel_body.deltas(eq_no));
  }

  public Relation deltasToRelation(int numberInputs, int numberOutputs)
  {
    return new Relation(rel_body.deltasToRelation(numberInputs, numberOutputs));
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
  public Relation composition(Relation r2)
  {
    return new Relation(rel_body.composition(r2.rel_body));
  }

  /**
   *    F minus G.
   */
  public Relation difference(Relation input_r2)
  {
    return new Relation(rel_body.difference(input_r2.rel_body));
  }

  /**
   *    complement.
   *            not F
   */
  public Relation complement()
  {
    return new Relation(rel_body.complement());
  }

  /**
   * Compute (gist r1 given r2).
   * Currently we assume that r2 has only one conjunct.
   * r2 may have zero input and output OR may have # in/out vars equal to r1.
   */
  public Relation gistSingleConjunct(Relation input_R2, int effort)
  {
    return new Relation(rel_body.gistSingleConjunct(input_R2.rel_body, effort));
  }

  /**
   * Compute <code>gist(r1)</code> given r2. 
   * Relation <code>r2</code> can have multiple conjuncts.
   */
  public Relation gist(Relation r2, int effort)
  {
    return new Relation(rel_body.gist(r2.rel_body, effort));
  }

  public Relation projectOntoJust(VarDecl v)
  {
    return new Relation(rel_body.projectOntoJust(v));
  }

  public Relation decoupledConvexHull()
  {
    return new Relation(rel_body.decoupledConvexHull());
  }

  public Relation convexHull()
  {
    return new Relation(rel_body.convexHull());
  }

  public Relation affineHull()
  {
    return new Relation(rel_body.affineHull());
  }

  public Relation linearHull()
  {
    return new Relation(rel_body.linearHull());
  }

  public Relation conicHull()
  {
    return new Relation(rel_body.conicHull());
  }

  public Relation conicClosure()
  {
    return new Relation(rel_body.conicClosure());
  }

  public Relation hull(boolean stridesAllowed, int effort, Relation knownHull)
  {
    return new Relation(rel_body.hull(stridesAllowed, effort, knownHull.rel_body));
  }

  public Relation checkForConvexPairs()
  {
    return new Relation(rel_body.checkForConvexPairs());
  }
                
  public Relation vennDiagramForm(Relation Context_In)
  {
    return new Relation(rel_body.vennDiagramForm(Context_In.rel_body));
  }

  public Relation checkForConvexRepresentation()
  {
    return new Relation(rel_body.checkForConvexRepresentation());
  }

  /**
   * Check if we can use D instead of R.
   *  i.e.  D intersection (A cross A) is Must_Be_Subset of R
   */
  public boolean isDOK(Relation D, Relation AxA)
  {
    return rel_body.isDOK(D.rel_body, AxA.rel_body);
  }

  /**
   * Try to get conjunct transitive closure.
   * it we can get it easy get it, return true.
   * if not - return false
   */
  public Relation tryConjunctTransitiveClosure(Relation IterationSpace, Relation rPlus)
  {
    return new Relation(rel_body.tryConjunctTransitiveClosure(IterationSpace.rel_body, rPlus.rel_body));
  }

  public boolean equal(Relation r2)
  {
    return rel_body.equal(r2.rel_body);
  }

  /**
   * Transitive closure of the relation containing multiple conjuncts.
   */
  public Relation transitiveClosure0(int maxExpansion, Relation iterationSpace)
  {
    return new Relation(rel_body.transitiveClosure0(maxExpansion, iterationSpace.rel_body));
  }
}
