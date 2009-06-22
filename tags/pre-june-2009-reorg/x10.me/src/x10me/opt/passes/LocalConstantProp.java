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

import java.util.HashMap;

import x10me.opt.controlflow.BasicBlock;
import x10me.opt.driver.CompilerPhase;
import x10me.opt.driver.OptOptions;
import x10me.opt.ir.IR;
import x10me.opt.ir.Instruction;
import x10me.opt.ir.Move;
import x10me.opt.ir.Register;
import x10me.opt.ir.operand.ConstantOperand;
import x10me.opt.ir.operand.Operand;
import x10me.opt.ir.operand.OperandEnumeration;
import x10me.opt.ir.operand.RegisterOperand;

/**
 * Perform local constant propagation for a factored basic block.
 * Orthogonal to the constant propagation performed in Simple
 * since here we use flow-sensitive analysis within a basic block.
 */
public class LocalConstantProp extends CompilerPhase {

  public LocalConstantProp(IR ir) {
    super(ir);
  }
  
  @Override
  public final boolean shouldPerform(OptOptions options) {
    return options.LOCAL_CONSTANT_PROP;
  }

  public final String getName() {
    return "Local ConstantProp";
  }

  /**
   * Perform Local Constant propagation for a method.
   *
   * @param ir the IR to optimize
   */
  public void perform() {
    // info is a mapping from Register to ConstantOperand.
    HashMap<Register, ConstantOperand> info = new HashMap<Register, ConstantOperand>();
    boolean runBranchOpts = false;

    /* Visit each basic block and apply the optimization */
    for (BasicBlock bb = ir.firstBasicBlockInCodeOrder(); bb != null; bb = bb.nextBasicBlockInCodeOrder()) {
      if (bb.isEmpty()) continue; /* skip over trivial blocks */
      if (bb.getInfrequent() && ir.options.FREQ_FOCUS_EFFORT) continue;

      /* Iterate over all instructions in the basic block */
      for (Instruction s = bb.firstRealInstruction(), next, sentinel = bb.lastInstruction(); s != sentinel; s = next) {
        next = s.nextInstructionInCodeOrder();

        /* Do we known anything ? */
        if (!info.isEmpty()) {
          /* Transform: attempt to propagate constants */
          int numUses = s.getNumberOfUses();
          if (numUses > 0) {
            boolean didSomething = false;
            int numDefs = s.getNumberOfDefs();
            for (int idx = numDefs; idx < numUses + numDefs; idx++) {
              Operand use = s.getOperand(idx);
              if (use instanceof RegisterOperand) {
                RegisterOperand rUse = (RegisterOperand)use;
                Operand value = info.get(rUse.getRegister());
                if (value != null) {
                  didSomething = true;
                  s.putOperand(idx, value.copy());
                }
              }
            }
            if (didSomething) {
              Simplifier.simplify(ir.regpool, ir.options, s);
            }
          }

          /* KILL: Remove bindings for all registers defined by this instruction */
          for (OperandEnumeration e = s.getDefs(); e.hasMoreElements();) {
            Operand def = e.next();
            if (def != null) {
              /* Don't bother special casing the case where we are defining another constant; GEN will handle that */
              /* Don't attempt to remove redundant assignments; let dead code elimination handle that */
              Register defReg = ((RegisterOperand)def).getRegister();
              info.remove(defReg);
            }
          }
        }

        /* GEN: If this is a move operation with a constant RHS, then it defines a constant */
        if (s instanceof Move) {
          Move m = (Move)s;
          if (m.getVal().isConstant()) {
            info.put(((RegisterOperand)m.getResult()).getRegister(), (ConstantOperand)m.getVal());
          }
        }	
      }

      /* End of basic block; clean up and prepare for next block */
      info.clear();
      runBranchOpts |= BranchSimplifier.simplify(bb, ir);
    }

    /* End of IR.  If we simplified a branch instruction, then run branch optimizations */
    if (runBranchOpts) {
      new BranchOptimizations(ir, 0, true, false, false).perform();
    }

  }
}
