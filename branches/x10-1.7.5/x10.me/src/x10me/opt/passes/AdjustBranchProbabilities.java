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


import x10me.opt.controlflow.BasicBlock;
import x10me.opt.controlflow.BasicBlockEnumeration;
import x10me.opt.driver.CompilerPhase;
import x10me.opt.ir.Athrow;
import x10me.opt.ir.Call;
import x10me.opt.ir.IR;
import x10me.opt.ir.IfCmp;
import x10me.opt.ir.Instruction;
import x10me.opt.ir.InstructionEnumeration;
import x10me.opt.ir.operand.BranchProfileOperand;
import x10me.opt.ir.operand.MethodOperand;
import x10me.types.Method;

/**
 * This pass adjusts branch probabilities derived from static estimates
 * to account for blocks that are statically guessed to be infrequent.
 */
public class AdjustBranchProbabilities extends CompilerPhase {

  public final String getName() {
    return "Adjust Branch Probabilities";
  }

  public AdjustBranchProbabilities(IR ir) {
    super(ir);
  }

  /**
   * Simplistic adjustment of branch probabilities.
   * The main target of this pass is to detect idioms like
   *   if (P) { infrequent block }
   *   if (P) { } else { infrequent block }
   * that are introduced by ExpandRuntimeServices.
   *
   * Key idea: If a block is infrequent then make sure that
   *           any conditional branch that targets/avoids the block
   *           does not have 0.5 as its branch probability.
   *
   * @param ir the governing IR
   */
  public final void perform() {
    for (BasicBlockEnumeration e = ir.getBasicBlocks(); e.hasMoreElements();) {
      BasicBlock target = e.next();
      if (findInfrequentInstruction(target)) {
        blockLoop:
        for (BasicBlockEnumeration sources = target.getIn(); sources.hasMoreElements();) {
          BasicBlock source = sources.next();
          // Found an edge to an infrequent block.
          // Look to see if there is a conditional branch that we need to adjust
          IfCmp condBranch = null;
          for (InstructionEnumeration ie = source.enumerateBranchInstructions(); ie.hasMoreElements();) {
            Instruction s = ie.next();
            if (s instanceof IfCmp && ((IfCmp)s).getBranchProfile().takenProbability == 0.5f) {
              if (condBranch == null) {
                condBranch = (IfCmp)s;
              } else {
                continue blockLoop; // branching is too complicated.
              }
            }
          }
          if (condBranch != null) {
            BasicBlock notTaken = source.getNotTakenNextBlock();
            if (notTaken == target) {
              // The not taken branch is the unlikely one, make the branch be taken always.
              condBranch.setBranchProfile(BranchProfileOperand.always());
            } else {
              // The taken branch is the unlikely one,
              condBranch.setBranchProfile(BranchProfileOperand.never());
            }
          }
        }
      }
    }
  }

  private boolean findInfrequentInstruction(BasicBlock bb) {
    for (InstructionEnumeration e2 = bb.forwardRealInstrEnumerator(); e2.hasMoreElements();) {
      Instruction s = e2.next();
      if (s instanceof Call) {
	Call c = (Call)s;
        MethodOperand op = c.getMethod();
        if (op != null) {
          Method target = op.getTarget();
          if (target != null && target.hasNoInlinePragma()) {
            return true;
          }
        }
      } else if (s instanceof Athrow) {
        return true;
      }
    }
    return false;
  }
}
