package polyglot.ext.x10.types.constr.omega.omegaLib;

import java.util.*;

/**
 * CoefVarDecl.
 * <p>
 * $Id: CoefVarDecl.java,v 1.1 2008-03-06 18:02:23 nystrom Exp $
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
public class CoefVarDecl extends GlobalVarDecl
{
  private int id;
  private int var;

  public CoefVarDecl(int id, int var)
  {
    super("c_" + id + "_" + var);
    this.id = id;
    this.var = var;
  }

  public int getId()
  {
    return id;
  }

  public int var() 
  {
    return var;
  }

  public int kind() 
  {
    return COEF_VAR;
  }

  public CoefVarDecl reallyCoefVar()
  {
    return this;
  }
}
