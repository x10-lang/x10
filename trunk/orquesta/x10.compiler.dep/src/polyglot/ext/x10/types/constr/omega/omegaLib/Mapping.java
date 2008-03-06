package polyglot.ext.x10.types.constr.omega.omegaLib;

import java.util.*;

/**
 * Mapping for relations.
 * <p>
 * $Id: Mapping.java,v 1.1 2008-03-06 18:02:23 nystrom Exp $
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
 * href="mailto:omega@cs.umd.edu">omega@cs.umd.edu</a> or
 * <a href="http://www.cs.umd.edu/projects/omega">http://www.cs.umd.edu/projects/omega</a>
 * </blockquote>
 * When a relation operation needs to re-arrange the variables,
 * it describes the re-arragement with a mapping, and then calls
 * align to re-arrange them.
 * <p>
 * In a mapping, map_in (map_out/map_set) gives the new type and
 *  position of each of the old input (output/set) variables.
 * For variables being mapped to Input, Output, or Set variables,
 *  the position is the new position in the tuple.
 * For variables being mapped to EXISTS_VAR, FORALL_VAR, or
 *  WILDCARD_VAR, the positions can be used to map multiple
 *  variables to the same quantified variable, by providing
 *  the same position.  Each variable with a negative position
 *  is given a unique quantified variable that is NOT listed
 *  in the seen_exists_ids list.
 * We are not sure what the positions mean for GLOBAL_VARs - perhaps
 *  they are ignored?
 * <p>
 * Currently, align supports only mapping to Set, Input,
 * Output, and Exists vars.
 */
public final class Mapping
{
  private int   n_input;
  private int   n_output;
  private int[] map_in_kind;
  private int[] map_in_pos;
  private int[] map_out_kind;
  private int[] map_out_pos;
  private int   in_req = 0;
  private int   out_req = 0;


  public Mapping(int n_input, int n_output)
  {
    this.n_input      = n_input;
    this.n_output     = n_output;
    this.map_in_kind  = new int[n_input + 1];
    this.map_in_pos   = new int[n_input + 1];
    this.map_out_kind = new int[n_output + 1];
    this.map_out_pos  = new int[n_output + 1];
  }

  public Mapping(int no_set)
  {
    this(no_set, 0);
  }

  public Mapping(Mapping m)
  {
    this.n_input      = m.n_input;
    this.n_output     = m.n_output;

    this.map_in_kind  = (int[]) m.map_in_kind.clone();
    this.map_in_pos   = (int[]) m.map_in_pos.clone();
    this.map_out_kind = (int[]) m.map_out_kind.clone();
    this.map_out_pos  = (int[]) m.map_out_pos.clone();
  }

  public void setMap(int in_kind, int pos, int type, int map)
  {
    if (in_kind == VarDecl.INPUT_VAR)
      setMap_in(pos, type, map);
    else if (in_kind == VarDecl.SET_VAR)
      setMap_in(pos, type, map);
    else if (in_kind == VarDecl.OUTPUT_VAR)
      setMap_out(pos, type, map);
    else
      throw new polyglot.util.InternalCompilerError("setMap");
  }

  public void setMap_in(int pos, int type, int map)
  {
    if ((pos < 1) || (pos > n_input))
      throw new polyglot.util.InternalCompilerError("Mapping: setMap_in " + pos + " " + (n_input - 1));
    map_in_kind[pos] = type;
    map_in_pos[pos] = map;
  }

  public void setMap_set(int pos, int type, int map)
  {
    if ((pos < 1) || (pos > n_input))
      throw new polyglot.util.InternalCompilerError("Mapping: setMap_set");
    map_in_kind[pos] = type;
    map_in_pos[pos] = map;
  }

  public void setMap_out(int pos, int type, int map)
  {
    if ((pos < 1) || (pos > n_output))
      throw new polyglot.util.InternalCompilerError("Mapping: setMap_out");
    map_out_kind[pos] = type;
    map_out_pos[pos] = map;
  }

  public int getMapInKind(int pos)
  {
    if ((pos < 1) || (pos > n_input))
      throw new polyglot.util.InternalCompilerError("Mapping: getMapInKind");
    return map_in_kind[pos];
  }

  public int getMapInPos(int pos)
  {
    if ((pos < 1) || (pos > n_input))
      throw new polyglot.util.InternalCompilerError("Mapping: getMapInPos");
    return map_in_pos[pos];
  }

  public int getMapOutKind(int pos)
  {
    if ((pos < 1) || (pos > n_output))
      throw new polyglot.util.InternalCompilerError("Mapping: getMapOutKind");
    return map_out_kind[pos];
  }

  public int getMapOutPos(int pos)
  {
    if ((pos < 1) || (pos > n_output))
      throw new polyglot.util.InternalCompilerError("Mapping: getMapOutPos");
    return map_out_pos[pos];
  }

  public int numberInput()
  {
    return n_input;
  }

  public int numberOutput()
  {
    return n_output;
  }

  /**
   * If a tuple as a whole becomes the new Input or Output tuple,
   *  return the Tuple if they will become (Input, Output).
   * Return UNKNOWN_TUPLE otherwise.
   */
  public int getTupleFate(int t, int prefix)
  {
    switch (t) {
      // case VarDecl.SET_TUPLE: return getInputFate(prefix);
      case VarDecl.INPUT_TUPLE:  return getInputFate(prefix);
      case VarDecl.OUTPUT_TUPLE: return getOutputFate(prefix);
      default:                    return VarDecl.UNKNOWN_TUPLE;
    }
  }

  public int getSetFate(int prefix) 
  {
    return getInputFate(prefix);
  }

  public int getInputFate(int prefix) 
  {
    if (prefix < 0)
      prefix = n_input;

    if (n_input < prefix)
      throw new polyglot.util.InternalCompilerError("Mapping: getInputFate: " + n_output + " < " + prefix);

    if (n_input < prefix)
      return VarDecl.UNKNOWN_TUPLE;

    int vf = map_in_kind[1];
    for (int i = 1; i <= prefix; i++)
      if ((map_in_pos[i] != i) || (map_in_kind[i] != vf))
        return VarDecl.UNKNOWN_TUPLE;

    switch (vf) {
      case VarDecl.INPUT_VAR:  return VarDecl.INPUT_TUPLE;
      case VarDecl.SET_VAR:    return VarDecl.SET_TUPLE;
      case VarDecl.OUTPUT_VAR: return VarDecl.OUTPUT_TUPLE;
      default:                  return VarDecl.UNKNOWN_TUPLE;
    }
  }

  public int getOutputFate(int prefix /* = -1 */)
  {
    if (prefix < 0)
      prefix = n_output;

    if (n_output < prefix)
      throw new polyglot.util.InternalCompilerError("Mapping: getOutputFate");

    if (n_output < 1)
      return VarDecl.UNKNOWN_TUPLE;

    int vf = map_out_kind[1];
    for (int i = 1; i <= prefix; i++)
      if ((map_out_pos[i] != i) || (map_out_kind[i] != vf))
        return VarDecl.UNKNOWN_TUPLE;

    switch (vf) {
      case VarDecl.INPUT_VAR:  return VarDecl.INPUT_TUPLE;
      case VarDecl.SET_VAR:    return VarDecl.SET_TUPLE;
      case VarDecl.OUTPUT_VAR: return VarDecl.OUTPUT_TUPLE;
      default:                  return VarDecl.UNKNOWN_TUPLE;
    }
  }

  public static Mapping Identity(int inp, int outp) 
  {
    Mapping m = new Mapping(inp, outp);

    for (int i = 1; i <= m.n_input; i++)
      m.setMap(VarDecl.INPUT_VAR, i, VarDecl.INPUT_VAR, i);

    for (int i = 1; i <= m.n_output; i++)
      m.setMap(VarDecl.OUTPUT_VAR, i, VarDecl.OUTPUT_VAR, i);

    return m;
  }

  public static Mapping Identity(int setvars) 
  {
    Mapping m = new Mapping(setvars);

    for (int i = 1; i <= setvars; i++)
      m.setMap(VarDecl.SET_VAR, i, VarDecl.SET_VAR, i);

    return m;
  }

  /**
   * Determine if a mapping requires an f_exists node.
   */
  public boolean hasExistentials() 
  { 
    for(int i = 1; i <= this.numberInput(); i++) 
      if (this.getMapInKind(i) == VarDecl.EXISTS_VAR)
        return true;
    for(int j = 1; j <= this.numberOutput(); j++) 
      if (this.getMapOutKind(j) == VarDecl.EXISTS_VAR)
        return true;
    return false;
  }

  public void getRelationArityFromOneMapping()
  {
    in_req = 0; 
    out_req = 0;
    for (int i = 1;  i <= this.numberInput();  i++) {
      int j = this.getMapInPos(i);
      switch (this.getMapInKind(i)) {
      case VarDecl.INPUT_VAR:  in_req  = max(in_req, j);  break;
      case VarDecl.SET_VAR:    in_req  = max(in_req, j);  break;
      case VarDecl.OUTPUT_VAR: out_req = max(out_req, j); break;
      default: break;
      }
    }

    for (int i = 1;  i <= this.numberOutput();  i++) {
      int j = this.getMapOutPos(i);
      switch (this.getMapOutKind(i)) {
      case VarDecl.INPUT_VAR:  in_req  = max(in_req, j);  break;
      case VarDecl.SET_VAR:    in_req  = max(in_req, j);  break;
      case VarDecl.OUTPUT_VAR: out_req = max(out_req, j); break;
      default: break;
      }
    }
  }

  /**
   * Scan mappings to see how many input and output variables they require. 
   */
  public void getRelationArityFromMappings(Mapping m2)
  {
    getRelationArityFromOneMapping();

    int inreq1 = in_req;
    int outreq1 = out_req;

    m2.getRelationArityFromOneMapping();
    in_req = max(inreq1, in_req);
    out_req = max(outreq1, out_req);
  }

  public int getOutReq()
  {
    return out_req;
  }

  public int getInReq()
  {
    return in_req;
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
}
