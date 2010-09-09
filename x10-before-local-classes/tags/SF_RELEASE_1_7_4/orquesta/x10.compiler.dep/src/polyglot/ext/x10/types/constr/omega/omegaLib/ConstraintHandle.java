package polyglot.ext.x10.types.constr.omega.omegaLib;

import java.util.*;

/**
 * ConstraintHandle.
 * <p>
 * $Id: ConstraintHandle.java,v 1.1 2008-03-06 18:02:23 nystrom Exp $
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
public abstract class ConstraintHandle
{
  private Conjunct c;
  private Equation eqns;

  public ConstraintHandle(Conjunct c, Equation eqns)
  {
    this.c    = c;
    this.eqns = eqns;
  }

  public abstract String printToString();

  public final String printGEQtoString()
  {
    c.relation().setupNames();
    return c.printGEQtoString(eqns);
  }

  public final String printEQtoString()
  {
    c.relation().setupNames();
    return c.printEQtoString(eqns);
  }

  public final String printTermToString()
  {
    c.relation().setupNames();
    return c.printTermToString(eqns);
  }

  public final void updateCoefficient(VarDecl v, int delta)
  {
    if (c.relation().isSimplified())
      throw new polyglot.util.InternalCompilerError("Relation is simplified.");

    int col = c.getColumn(v);
    eqns.addToCoef(col, delta);
  }

  public final void updateConstant(int delta)
  {
    if (c.relation().isSimplified())
      throw new polyglot.util.InternalCompilerError("Relation is simplified.");

    eqns.addToCoef(0, delta);
  }

  public final int getCoefficient(VarDecl v)
  {
    if (!c.relation().isSimplified())
      throw new polyglot.util.InternalCompilerError("Relation is not simplified.");

    int col = c.findColumn(v);

    if (col == 0)
      return 0;

    return eqns.getCoefficient(col);
  }

  public final int getConstant()
  {
    if (!c.relation().isSimplified())
      throw new polyglot.util.InternalCompilerError("Relation is not simplified.");

    return eqns.getConstant();
  }

  public final boolean hasWildcards()
  {
    return c.hasWildcards(eqns);
  }

  public final int maxTuplePosition()
  {
    return c.maxTuplePosition(eqns);
  }

  public final boolean isConstant(VarDecl v)
  {
    return c.varIsConstant(v, eqns);
  }

  public final VarDecl getLocal(GlobalVarDecl G)
  {
    return c.relation().getLocal(G);
  }

  public final VarDecl getLocal(GlobalVarDecl G, int of)
  {
    return c.relation().getLocal(G, of);
  }

  public final void multiply(int multiplier)
  {
    if (c.relation().isSimplified())
      throw new polyglot.util.InternalCompilerError("Relation is simplified.");

    eqns.multCoefs(c.numVars(), multiplier);
  }

  public final RelBody relation()
  {
    return c.relation();
  }

  public final void updateCoefDuringSimplify(VarDecl v, int delta)
  {
    int col = c.getColumn(v);
    eqns.addToCoef(col, delta);
  }

  public final void updateConstantDuringSimplify(int delta)
  {
    eqns.addToCoef(0, delta);
  }

  public final int getConstant_during_simplify()
  {
    return eqns.getConstant();
  }

  public final void copyConstraint(ConstraintHandle from) 
  {
    copyConstraint(from.c, from.eqns);
  }

  public final void copyConstraint(Conjunct from, Equation eq) 
  {
    c.copyConstraint(eqns, from, eq);
  }
}
