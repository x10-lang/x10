/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file was derived from code developed by the
 *  Jikes RVM project (http://jikesrvm.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  See the COPYRIGHT.txt file distributed with this work for information
 *  regarding copyright ownership.
 */

package x10me.opt.driver;

public class OptOptions {

  public boolean dumpBefore; 

  public boolean dumpAfter;

  public boolean dumpTrace;
  
  private int optLevel;
  
  public int getOptLevel() {
    return optLevel;
  }

  /**
   * Control for IR invariant checking.
   */
  // TODO these all need to be properly set.
  public final static int NOVERIFY = 0; 
  public final static int SOMEVERIFY = 1; 
  public final static int PARANOID = 11; 
   
  public final static int verifyAssertions = NOVERIFY;
  public final static int verifyIR = NOVERIFY;

  public static final int PROFILE_DUMB_FREQ = 0;

  public final boolean REORDER_CODE_PH = false;

  public final boolean REORDER_CODE = false;

  public final boolean SSA_LOOP_VERSIONING = false;

  public final int CONTROL_UNROLL_LOG = 0;

  public final int PROFILE_FREQUENCY_STRATEGY = 0;

  public final float PROFILE_INFREQUENT_THRESHOLD = 0;

  public final boolean CONTROL_TURN_WHILES_INTO_UNTILS = false;

  public final int CONTROL_COND_MOVE_CUTOFF = 0;

  public final boolean PRINT_DOMINATORS = false;
  public final boolean PRINT_POST_DOMINATORS = false;

  public final boolean LOCAL_EXPRESSION_FOLDING = false;

  public final boolean LOCAL_CSE = false;

  public final boolean FREQ_FOCUS_EFFORT = false;

  public final boolean LOCAL_COPY_PROP = false;

  public final double CONTROL_WELL_PREDICTED_CUTOFF = 0;

  public boolean SIMPLIFY_LONG_OPS = false;

  public boolean SIMPLIFY_FLOAT_OPS = false;

  public boolean SIMPLIFY_INTEGER_OPS = false;

  public boolean SIMPLIFY_DOUBLE_OPS = false;

  public boolean SIMPLIFY_REF_OPS = false;

  public boolean SIMPLIFY_FIELD_OPS = false;

  public boolean SIMPLIFY_CHASE_FINAL_FIELDS = false;

  public boolean SANITY_CHECK = false;
  public boolean PARANOID_CHECK = false;

  public boolean CONTROL_STATIC_SPLITTING;

  public int CONTROL_STATIC_SPLITTING_MAX_COST;

  public boolean LOCAL_CONSTANT_PROP;

  public boolean READS_KILL; 
}
