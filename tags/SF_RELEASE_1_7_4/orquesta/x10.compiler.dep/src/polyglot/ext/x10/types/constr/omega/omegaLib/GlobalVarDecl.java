package polyglot.ext.x10.types.constr.omega.omegaLib;

import java.util.*;
/**
 * GlobalVarDecl.
 * <p>
 * $Id: GlobalVarDecl.java,v 1.1 2008-03-06 18:02:23 nystrom Exp $
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
 * GlobalVarDecl is an ADT with the following operations:
 * <ul>
 * <li> create global variable,
 * <li> find the arity of the variable, (default = 0, for symbolic consts)
 * <li> get the name of global variable, 
 * <li> tell if two variables are the same (if they are the same object)
 * </ul>
 */

public class GlobalVarDecl
{
  private static int createdCount = 0; /* A count of all the instances of this class that were created. */

  public static int created()
  {
    return createdCount;
  }

  public static final int FREE_VAR   = 0;
  public static final int COEF_VAR   = 1;

  // local representative, there is just 1 for every 0-ary global variable
  private VarDecl loc_rep1; // arity == 0, or arity > 0 and of == In
  private VarDecl loc_rep2; // arity > 0 and of == Out
  private int     instance;
  public  int     id;

  public GlobalVarDecl(String baseName)
  {
    this.loc_rep1 = new VarDecl(baseName, this, VarDecl.INPUT_TUPLE);
    this.loc_rep2 = new VarDecl(baseName, this, VarDecl.OUTPUT_TUPLE);
    id = createdCount++;
  }

  public void setInstance(int instance)
  {
    this.instance = instance;
  }

  public int getInstance()
  {
    return instance;
  }

  public String getBaseName()
  {
    return loc_rep1.baseName();
  }

  public void setBaseName(String newName)
  {
    loc_rep1.nameVariable(newName);
    loc_rep2.nameVariable(newName);
  }

  public int arity()
  {
    return 0;   // default compatible with old symbolic constant stuff
  }

  public CoefVarDecl reallyCoefVar()
  {
    throw new polyglot.util.InternalCompilerError("reallyCoefVar");
//      return null;
  }

  public int kind() 
  {
    throw new polyglot.util.InternalCompilerError("kind");
//      return COEF_VAR;
  }

  public VarDecl getLocal()
  {
    if (arity() != 0)
      throw new polyglot.util.InternalCompilerError("arity is " + arity());

    return loc_rep1;
  }

  public VarDecl getLocal(int of)
  {
    if ((arity() != 0) && (of != VarDecl.INPUT_TUPLE) && (of != VarDecl.OUTPUT_TUPLE))
      throw new polyglot.util.InternalCompilerError("arity");

    return (((arity() == 0) || (of == VarDecl.INPUT_TUPLE)) ? loc_rep1 : loc_rep2);
  }
}
