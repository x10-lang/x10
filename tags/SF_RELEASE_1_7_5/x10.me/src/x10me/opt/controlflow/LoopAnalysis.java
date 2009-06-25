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

import x10me.opt.DefUse;
import x10me.opt.driver.CompilerPhase;
import x10me.opt.driver.OptOptions;
import x10me.opt.ir.IR;

/**
 * The driver that creates an annotated {@link AnnotatedLSTGraph}.
 *
 * @see AnnotatedLSTGraph
 */
public class LoopAnalysis extends CompilerPhase {

  public LoopAnalysis(IR ir) {
    super(ir);
  }
  
  /**
   * Return a string name for this phase.
   * @return "Loop Analysis"
   */
  public final String getName() {
    return "Loop Analysis";
  }

  /**
   * Should the optimisation be performed
   */
  public boolean shouldPerform(OptOptions options) {
    return options.getOptLevel() >= 3;
  }

  @Override
  public final void perform() {
    if (!ir.hasReachableExceptionHandlers()) {
      // Build LST tree and dominator info
      new DominatorsPhase(ir, false).perform();
      DefUse.computeDU(ir);
      // Build annotated version
      ir.loopStructureTree = new AnnotatedLSTGraph(ir, ir.loopStructureTree);
    }
  }
}
