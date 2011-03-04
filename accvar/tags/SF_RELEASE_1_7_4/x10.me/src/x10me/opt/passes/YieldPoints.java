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

package x10me.opt.passes;

import x10me.opt.bc2ir.BCconstants;
import x10me.opt.controlflow.BasicBlock;
import x10me.opt.controlflow.BasicBlockEnumeration;
import x10me.opt.controlflow.LSTGraph;
import x10me.opt.controlflow.LSTNode;
import x10me.opt.driver.CompilerPhase;
import x10me.opt.driver.OptOptions;
import x10me.opt.inlining.InlineSequence;
import x10me.opt.ir.*;

/**
 * This class inserts yield points in
 *  1) a method's prologue
 *  2) loop headers
 *  3) (optionally) method exits (epilogue, athrow)
 */
public class YieldPoints extends CompilerPhase {

  public YieldPoints(IR ir) {
    super(ir);
  }
  
  /**
   * Return the name of this phase
   * @return "Yield Point Insertion"
   */
  public final String getName() {
    return "Yield Point Insertion";
  }

 
  @Override
  public final void perform() {
    // (1) Insert prologue yieldpoint unconditionally.
    //     As part of prologue/epilogue insertion we'll remove
    //     the yieldpoints in trival methods that otherwise wouldn't need
    //     a stackframe.
    prependYield(ir.cfg.entry(), Operators.YieldpointPrologue, 0, ir.inlineSequence);

    // (2) If using epilogue yieldpoints scan basic blocks, looking for returns or throws
    for (BasicBlockEnumeration e = ir.getBasicBlocks(); e.hasMoreElements();) {
      BasicBlock block = e.next();
      if (block.hasReturn() || block.hasAthrowInst()) {
	prependYield(block, Operators.YieldpointEpilogue, BCconstants.INSTRUMENTATION_BCI, ir.inlineSequence);
      }
    }

    // (3) Insert yieldpoints in loop heads based on the LST.
    LSTGraph lst = ir.loopStructureTree;
    if (lst != null) {
      for (java.util.Enumeration<LSTNode> e = lst.getRoot().getChildren(); e.hasMoreElements();) {
	processLoopNest(e.nextElement());
      }
    }
  }

  /**
   * Process all loop heads in a loop nest by inserting a backedge yieldpoint in each of them.
   */
  private void processLoopNest(LSTNode n) {
    for (java.util.Enumeration<LSTNode> e = n.getChildren(); e.hasMoreElements();) {
      processLoopNest(e.nextElement());
    }
    Instruction dest = n.header.firstInstruction();
    prependYield(n.header, Operators.YieldpointBackedge, dest.bcIndex, dest.position);
  }

  /**
   * Add a YIELD instruction to the appropriate place for the basic
   * block passed.
   *
   * @param bb the basic block
   * @param yp the yieldpoint operator to insert
   * @param bcIndex the bcIndex of the yieldpoint
   * @param position the source position of the yieldpoint
   */
  private void prependYield(BasicBlock bb, int yp, int bcIndex, InlineSequence position) {
    Instruction insertionPoint = null;
    Instruction s = null;
    
    if (bb.isEmpty()) {
      insertionPoint = bb.lastInstruction();
    } else {
      insertionPoint = bb.firstRealInstruction();
    }

    if (yp == Operators.YieldpointPrologue) {
        assert((insertionPoint != null) && (insertionPoint instanceof IrPrologue));
      // put it after the prologue
      insertionPoint = insertionPoint.nextInstructionInCodeOrder();
      s = new YieldpointEpilogue();
    } else if (yp == Operators.YieldpointEpilogue) {
      // epilogues go before the return or athrow (at end of block)
      insertionPoint = bb.lastRealInstruction();
      s = new YieldpointEpilogue();
    } else {
      s = new YieldpointBackedge();
    }
    
    insertionPoint.insertBefore(s);
    s.position = position;
    s.bcIndex = bcIndex;
  }
}




