package polyglot.ext.x10.types.constr.omega.omegaLib;

import java.util.*;

/**
 * Variable declaration.
 * <p>
 * $Id: VarDecl.java,v 1.1 2008-03-06 18:02:23 nystrom Exp $
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
 * <p>
 * Variables are free or quantified.
 * Free variables are classified as input, output and global.
 * Quantified variables are classified as forall, exists and wildcard.
 * All global variables are functions symbols of (possibly 0) arguments
 * Local variables that correspond to >0-ary functions are identified
 * as functions of a prefix of the input, output, or both tuples
 * </blockquote>
 */
public final class VarDecl
{
  private static int createdCount = 0; /* A count of all the instances of this class that were created. */

  public static int created()
  {
    return createdCount;
  }

  public static final int INPUT_VAR    = 0;
  public static final int OUTPUT_VAR   = 1;
  public static final int SET_VAR      = 2;
  public static final int GLOBAL_VAR   = 3;
  public static final int FORALL_VAR   = 4;
  public static final int EXISTS_VAR   = 5;
  public static final int WILDCARD_VAR = 6;

  public static final String[] varTypes = {
    "INPUT_VAR",
    "OUTPUT_VAR",
    "SET_VAR",
    "GLOBAL_VAR",
    "FORALL_VAR",
    "EXISTS_VAR",
    "WILDCARD_VAR"
  };

  public static final int UNKNOWN_TUPLE = 0;
  public static final int INPUT_TUPLE   = 1;
  public static final int OUTPUT_TUPLE  = 2;
  public static final int SET_TUPLE     = INPUT_TUPLE;

  public static final String[] tupleTypes = {"Unknown", "Input", "Output"};

  public static final String[] greekLetters = {
    "alpha", "beta", "gamma", "delta", "tau", "sigma", "chi",
    "omega", "pi", "ni", "Alpha", "Beta", "Gamma", "Delta",
    "Tau", "Sigma", "Chi", "Omega", "Pi" 
  };

  public static final String[] argNames = {"???", "In", "Out", "In == Out"};

  private int           var_kind;
  private int           position;   // only for INPUT_VAR, OUTPUT_VAR
  private int           instance;
  private String        base_name;
  private VarDecl       remap;      // pointer to new copy of this node
  private GlobalVarDecl global_var; // only for GLOBAL_VAR
  private int           of;         // only for GLOBAL_VAR
  public  int           id;

  public VarDecl(String name, int vkind, int pos)
  {
    this.base_name  = name;
    this.instance   = 999;
    this.remap      = this;
    this.var_kind   = vkind;
    this.position   = pos;
    this.global_var = null;
    this.of         = 0;
    id = createdCount++;
  }

  public VarDecl(int vkind, int pos)
  {
    this(null, vkind, pos);
  }

  public VarDecl(VarDecl v)
  {
    this(v.base_name, v.var_kind, v.position);
  }

  public VarDecl(String name, GlobalVarDecl v)
  {
    this(name, GLOBAL_VAR, 0);
    this.global_var = v;
    olAssert(v.arity() == 0);
  }

  public VarDecl(String name, GlobalVarDecl v, int functionOf)
  {
    this(name, GLOBAL_VAR, 0);
    this.global_var = v;
    this.of = functionOf;
  }

  public String toString()
  {
    StringBuffer buf = new StringBuffer("(");
    buf.append(varTypes[var_kind]);
    buf.append(" ");
    buf.append(id);
    buf.append(' ');
    buf.append(base_name);
    buf.append(' ');
    buf.append('(');
    buf.append(instance);
    buf.append(") ");
    buf.append(position);
    buf.append(')');
    return buf.toString();
  }

  public void setInstance(int instance)
  {
    this.instance = instance;
  }

  public int getInstance()
  {
    return instance;
  }

  public VarDecl getRemap()
  {
    return remap;
  }

  public void changeKind(int newKind)
  {
    var_kind = newKind;
  }

  public int kind()
  {
    return var_kind;
  }

  public int getPosition()
  {
    if ((var_kind != INPUT_VAR) && (var_kind != OUTPUT_VAR))
      throw new polyglot.util.InternalCompilerError("VarDecl.getPosition: bad var_kind " + varTypes[var_kind]);
    return position;
  }

  public GlobalVarDecl getGlobalVar()
  {
    if (var_kind != GLOBAL_VAR)
      throw new polyglot.util.InternalCompilerError("VarDecl::getGlobalVar: bad var_kind");
    return global_var;
  }

  public int functionOf()
  {
    olAssert(var_kind == GLOBAL_VAR);
    return of;
  }

  public CoefVarDecl reallyCoefVar()
  {
    throw new polyglot.util.InternalCompilerError("reallyCoefVar");
//      return null;
  }

  public String name(OmegaLib omegaLib)
  {
    return displayName(omegaLib);
  }

  public String baseName()
  {
    return base_name;
  }

  public void nameVariable(String newName)
  {
    base_name = newName;
  }

  public void setKind(int var_kind)
  {
    this.var_kind = var_kind;
  }

  public void resetRemapField(VarDecl v)
  {
    remap = v;
  }

  /**
   * Operation to allow the remap field to be used for
   * union-find operations on variables.
   * Be sure to reset the remap fields afterward.
   */
  public VarDecl UFOwner()
  {
    VarDecl v = this;
    while (v.remap != v)
      v = v.remap;
    return v;
  }

  public void UFUnion(VarDecl b)
  {
    VarDecl a = this;
    while (a.remap != a) {
      VarDecl tmp = a.remap;
      a.remap = tmp.remap;
      a = tmp;
    }
    while (b.remap != a) {
      VarDecl tmp = b.remap;
      b.remap = a;
      b = tmp;
    }
  }

  public boolean hasName()
  {
    return (base_name != null) && !base_name.equals("");
  }

  public String displayName(OmegaLib omegaLib)
  {
    StringBuffer s       = new StringBuffer("");
    int          primes  = instance;
    boolean      hasName = hasName();

    if (omegaLib.useUglyNames != 0) 
      primes = 0;

    switch (var_kind) {
    case INPUT_VAR:
      if ((omegaLib.useUglyNames == 0) && hasName)
        s.append(base_name);
      else {
        primes = 0;
        s.append("In_");
        s.append(position);
      }
      break;
    case OUTPUT_VAR:
      if ((omegaLib.useUglyNames == 0) && hasName)
        s.append(base_name);
      else {
        primes = 0;
        s.append("Out_");
        s.append(position);
      }
      break;
    case GLOBAL_VAR:
      olAssert(hasName);
      if (omegaLib.useUglyNames != 0) {
        s.append(base_name);
        s.append('@');
        s.append(id);
      } else  {
        s.append(base_name);
        primes = getGlobalVar().getInstance();
      }
      break;
    default: 
      if (omegaLib.useUglyNames != 0) {
        if (hasName) {
          s.append(base_name);
          s.append('@');
          s.append(id);
        } else {
          s.append("?@");
          s.append(id);
        }
      } else if (hasName)
        s.append(base_name);
      else {
        s.append(greekLetters[instance % greekLetters.length]);
        primes = instance / greekLetters.length;
      }
      break;
    }

    if (primes > 200)
      throw new polyglot.util.InternalCompilerError("Too many primes (" + primes + ") for " + this + " " + s.toString());

    for(int i = 1; i <= primes; i++)
      s.append('\'');

    if (var_kind == GLOBAL_VAR) {
      int a = getGlobalVar().arity();
      if (a != 0) {
        if (omegaLib.useUglyNames != 0) {
          s.append("(");
          s.append(argNames[of]);
          s.append("[1#");
          s.append(a);
          s.append("])");
        } else {
          int f_of = of;
          olAssert((f_of == INPUT_TUPLE) || (f_of == OUTPUT_TUPLE));
          s.append('(');
          for(int i = 1; i <= a; i++) {
            if (f_of == INPUT_TUPLE) 
              s.append(omegaLib.newInputVar(i).displayName(omegaLib));
            else
              s.append(omegaLib.newOutputVar(i).displayName(omegaLib));
            if (i < a)
              s.append(',');
          }
          s.append(')');
        }
      }
    }

    return s.toString();
  }

  public void printVarAddrs(StringBuffer s)
  {
    s.append("(0x");
    s.append(Integer.toHexString(hashCode()));
    s.append(',');

    if (remap == null)
      s.append('0');
    else {
      s.append("0x");
      s.append(Integer.toHexString(remap.hashCode()));
    }

    s.append(')');
  }

  private void olAssert(boolean t)
  {
    if (t)
      return;
    throw new polyglot.util.InternalCompilerError("VarDecl");
  }

  public static Vector copyVarDecls(Vector vl)
  {
    int    l      = vl.size();
    Vector new_vl = new Vector(l);

    for (int p = 0; p < l; p++) {
      VarDecl v  = (VarDecl) vl.elementAt(p);
      VarDecl nv = v;
      if (v.kind() != GLOBAL_VAR)
        nv = new VarDecl(v);
      new_vl.addElement(nv);
      v.remap = nv;
    }

    return new_vl;
  }

  public static void resetRemapField(Vector c)
  {
    int l = c.size();
    for (int p = 1; p < l; p++) {
      VarDecl v = (VarDecl) c.elementAt(p);
      v.remap = v;
    }
  }  

  public static void resetRemapField(Vector c, int nVars)
  {
    for (int p = 1; p <= nVars; p++) {
      VarDecl v = (VarDecl) c.elementAt(p);
      v.remap = v;
    }
  }  
}
