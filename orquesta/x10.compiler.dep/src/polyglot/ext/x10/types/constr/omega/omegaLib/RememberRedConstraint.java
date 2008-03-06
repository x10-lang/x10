package polyglot.ext.x10.types.constr.omega.omegaLib;

import java.util.*;

/**
 * RememberRedConstraint.
 * <p>
 * $Id: RememberRedConstraint.java,v 1.1 2008-03-06 18:02:23 nystrom Exp $
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
public final class RememberRedConstraint
{
  public static final int notRed    = 0;
  public static final int redEQ     = 1;
  public static final int redGEQ    = 2;
  public static final int redLEQ    = 3;
  public static final int redStride = 4;

  private int length;
  private int stride;
  private int kind;
  private int[] coef;
  private int[] var;

  public RememberRedConstraint(int kind, int[] coefs, int stride)
  {
    this.kind    = kind;
    this.stride  = stride;
    this.coef    = new int[coefs.length];
    this.var     = new int[coefs.length];
    this.coef[0] = coefs[0];

    this.length  = 1;
    for (int i = 1; i <= coefs.length; i++) {
      if (coef[i] == 0)
        continue;

      int j = this.length++;
      this.coef[j] = coef[i];
      this.var[j] = i;
    }
  }

  public void recall(Problem p, int[] forwardingAddress, int[] varr)
  {
    Equation e;

    switch (kind) {
    default:
    case notRed:
    case redLEQ:
      throw new polyglot.util.InternalCompilerError("recall");
    case redGEQ:
      e = p.getNewGEQ();
      e.setTouched(true);
      break;
    case redEQ:
      e = p.getNewEQ();
      break;
    case redStride:
      e = p.getNewEQ();
      int i = p.addNewUnprotectedWildcard();
      e.setCoef(i, -stride);
      break;
    }

    e.turnRed();
    e.setConstant(coef[0]);
    for (int i = 1; i < length; i++) {
      int v = var[i];
      int f = forwardingAddress[v];
      if (varr[f] != v)
        throw new polyglot.util.InternalCompilerError("recall variable error");
      e.setCoef(f, coef[i]);
    }
  }

  public String toString(Problem p)
  {
    StringBuffer buf = new StringBuffer("(");
    switch (kind) {
    case notRed:    buf.append("notRed: ");      break;
    case redGEQ:    buf.append("Red: 0 <= ");    break;
    case redLEQ:    buf.append("Red ??: 0 >= "); break;
    case redEQ:     buf.append("Red: 0 == ");    break;
    case redStride: buf.append("Red stride ");
                    buf.append(stride);
                    buf.append(": ");            break;
    }

    buf.append(coef[0]);
    buf.append(' ');

    for (int i = 1; i < length; i++) {
      if (coef[i] >= 0) {
        buf.append("+");
        buf.append(coef[i]);
      } else {
        buf.append("-");
        buf.append(-coef[i]);
      }
      buf.append(p.orgVariable(var[i]));
    }

    buf.append(')');

    return buf.toString();
  }
}
