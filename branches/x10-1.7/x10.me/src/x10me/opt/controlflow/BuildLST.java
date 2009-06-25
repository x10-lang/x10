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

package x10me.opt.controlflow;

import x10me.opt.driver.CompilerPhase;
import x10me.opt.ir.IR;

/**
 * A compiler phase to construct the loop structure tree (LST).
 * The steps are (1) construct approximate dominators (ie blocks are
 * not unfactored) and (2) build the LST.
 *
 * @see LTDominators
 * @see LSTGraph
 */
public class BuildLST extends CompilerPhase {
  public BuildLST(IR ir) {
    super(ir);
  }
  
  @Override
  public String getName() {
    return "Build LST";
  }

 
  @Override
  public void perform() {
    ir.cfg.compactNodeNumbering();
    LTDominators.approximate(ir, true);
    DominatorTree.perform(ir, true);
    LSTGraph.perform(ir);
  }
}
