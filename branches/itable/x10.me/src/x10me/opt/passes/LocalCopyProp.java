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
import java.util.HashSet;
import java.util.Map;

import x10me.opt.controlflow.BasicBlock;
import x10me.opt.driver.CompilerPhase;
import x10me.opt.driver.OptOptions;
import x10me.opt.ir.IR;
import x10me.opt.ir.Instruction;
import x10me.opt.ir.Move;
import x10me.opt.ir.Register;
import x10me.opt.ir.operand.Operand;
import x10me.opt.ir.operand.OperandEnumeration;
import x10me.opt.ir.operand.RegisterOperand;

/**
 * Perform local copy propagation for a factored basic block.
 * Orthogonal to the copy propagation performed in Simple
 * since here we use flow-sensitive analysis within a basic block.
 *
 * TODO: factor out common functionality in the various local propagation
 * phases?
 */
public class LocalCopyProp extends CompilerPhase {

  public LocalCopyProp(IR ir) {
    super(ir);
  } 
  
  @Override
  public final boolean shouldPerform(OptOptions options) {
    return options.LOCAL_COPY_PROP;
  }

  @Override
  public final String getName() {
    return "Local CopyProp";
  }

  @Override
  public void perform() {
    HashMap<Register, Operand> info = new HashMap<Register, Operand>();
    for (BasicBlock bb = ir.firstBasicBlockInCodeOrder(); bb != null; bb = bb.nextBasicBlockInCodeOrder()) {
      if (bb.isEmpty()) continue;
      if (bb.getInfrequent() && ir.options.FREQ_FOCUS_EFFORT) continue;

      // iterate over all instructions in the basic block
      for (Instruction s = bb.firstRealInstruction(),
          sentinel = bb.lastInstruction(); s != sentinel; s = s.nextInstructionInCodeOrder()) {

        if (!info.isEmpty()) {
          // PROPAGATE COPIES
          int numUses = s.getNumberOfUses();
          if (numUses > 0) {
            boolean didSomething = false;
            for (OperandEnumeration e = s.getUses(); e.hasMoreElements();) {
              Operand use = e.next();
              if (use instanceof RegisterOperand) {
                RegisterOperand rUse = (RegisterOperand) use;
                Operand value = info.get(rUse.getRegister());
                if (value != null) {
                  didSomething = true;
                  value = value.copy();
                  if (value instanceof RegisterOperand) {
                    // preserve program point specific typing!
                    ((RegisterOperand) value).copyType(rUse);
                  }
                  s.replaceOperand(use, value);
                }
              }
            }
            if (didSomething) {
              Simplifier.simplify(ir.regpool, ir.options, s);
            }
          }
 
          for (OperandEnumeration e = s.getDefs(); e.hasMoreElements();) {
            Operand def = e.next();
            if (def != null && def.isRegister()) {
              Register r = def.asRegister().getRegister();
              info.remove(r);
              // also must kill any registers mapped to r
              // TODO: use a better data structure for efficiency.
              // I'm being lazy for now in the name of avoiding
              // premature optimization.
              HashSet<Register> toRemove = new HashSet<Register>();
              for (Map.Entry<Register, Operand> entry : info.entrySet()) {
                Register eR = ((RegisterOperand) entry.getValue()).getRegister();
                if (eR == r) {
                  // delay the removal to avoid ConcurrentModification
                  // with iterator.
                  toRemove.add(entry.getKey());
                }
              }
              // Now perform the removals.
              for (final Register register : toRemove) {
                info.remove(register);
              }
            }
          }
        }
        // GEN
        if (s instanceof Move) {
          Move m = (Move)s;
          Operand val = m.getVal();
          if (val.isRegister()) {
            RegisterOperand rhs = val.asRegister();
            if (!rhs.getRegister().isPhysical()) {
              RegisterOperand lhs = (RegisterOperand)m.getResult();
              /* Only gen if the move instruction does not represent a Magic <==> non-Magic coercion */
              if (lhs.getType().isReferenceType() == rhs.getType().isReferenceType()) {
                info.put(lhs.getRegister(), val);
              }
            }
          }
        }
      }
      info.clear();
    }
  }
}
