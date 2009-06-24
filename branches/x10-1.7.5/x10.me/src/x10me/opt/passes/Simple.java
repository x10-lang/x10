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

import static x10me.opt.driver.OptConstants.YES;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Enumeration;


import x10me.opt.DefUse;
import x10me.opt.controlflow.BasicBlock;
import x10me.opt.controlflow.BasicBlockEnumeration;
import x10me.opt.driver.CompilerPhase;
import x10me.opt.driver.OptOptions;
import x10me.opt.ir.*;
import x10me.opt.ir.operand.IntConstantOperand;
import x10me.opt.ir.operand.Operand;
import x10me.opt.ir.operand.OperandEnumeration;
import x10me.opt.ir.operand.RegisterOperand;
import x10me.opt.ir.operand.TrueGuardOperand;
import x10me.types.Type;

/*
 * Simple flow-insensitive optimizations.
 *
 * <p> Except for the "CompilerPhase" methods, all fields and methods in
 * this class are declared static.
 */
public final class Simple extends CompilerPhase {

  private final BranchOptimizations branchOpts = new BranchOptimizations(ir, -1, false, false, false);

  /**
   * At what optimization level should this phase be run?
   */
  private final int level;

  /**
   * Perform type propagation?
   */
  private final boolean typeProp;

  /**
   * Attempt to eliminate bounds and cast checks?
   */
 
  private final boolean foldChecks;
  /**
   * Fold conditional branches with constant operands?
   */
 
  private final boolean foldBranches;
  /**
   * Sort registers used by commutative operators
   */
  private final boolean sortRegisters;

  @Override
  public boolean shouldPerform(OptOptions options) {
    return options.getOptLevel() >= level;
  }

  @Override
  public String getName() {
    return "Simple Opts";
  }

  /**
   * The constructor is used to specify what pieces of Simple will
   * be enabled for this instance.  Some pieces are always enabled.
   * Customizing can be useful because some of the optimizations are not
   * valid/useful on LIR or even on "late stage" HIR.
   *
   * @param the IR.
   * @param level at what optimization level should the phase be enabled?
   * @param typeProp should type propagation be peformed?
   * @param foldChecks should we attempt to eliminate boundscheck?
   * @param foldBranches should we attempt to constant fold conditional
   * @param sortRegisters should we sort use operands?
   * branches?
   */
  public Simple(IR ir, int level, boolean typeProp, boolean foldChecks, boolean foldBranches, boolean sortRegisters) {
    super(ir);
    this.level = level;
    this.typeProp = typeProp;
    this.foldChecks = foldChecks;
    this.foldBranches = foldBranches;
    this.sortRegisters = sortRegisters;
  }

  /**
   * Main driver for the simple optimizations
   */
  public void perform() {
    // Compute defList, useList, useCount fields for each register.
    DefUse.computeDU(ir);
    // Recompute isSSA flags
    DefUse.recomputeSSA(ir);
    // Simple copy propagation.
    // This pass incrementally updates the register list.
    copyPropagation(ir);
    // Simple type propagation.
    // This pass uses the register list, but doesn't modify it.
    if (typeProp) {
      typePropagation(ir);
    }
    // Perform simple bounds-check and arraylength elimination.
    // This pass incrementally updates the register list
    if (foldChecks) {
      arrayPropagation(ir);
    }
    // Simple dead code elimination.
    // This pass incrementally updates the register list
    eliminateDeadInstructions(ir);
    // constant folding
    // This pass usually doesn't modify the DU, but
    // if it does it will recompute it.
    foldConstants(ir);
    // Simple local expression folding respecting DU
    if (ir.options.LOCAL_EXPRESSION_FOLDING && ExpressionFolding.performLocal(ir)) {
      // constant folding again
      foldConstants(ir);
    }
    // Try to remove conditional branches with constant operands
    // If it actually constant folds a branch,
    // this pass will recompute the DU
    if (foldBranches) {
      simplifyConstantBranches(ir);
    }
    // Should we sort commutative use operands
    if (sortRegisters) {
      sortCommutativeRegisterUses(ir);
    }
  }

  /**
   * Sort commutative use operands so that those defined most are on the lhs
   *
   * @param ir the IR to work on
   */
  private static void sortCommutativeRegisterUses(IR ir) {
    // Pass over instructions
    for (Enumeration<Instruction> e = ir.forwardInstrEnumerator(); e.hasMoreElements();) {
      Instruction s = e.nextElement();
      // Sort most frequently defined operands onto lhs
      if (s instanceof Binary) {
	Binary b = (Binary)s;
	if (b.isCommutative() &&
	    b.getVal1().isRegister() && b.getVal2().isRegister()) {
	  RegisterOperand rop1 = b.getVal1().asRegister();
	  RegisterOperand rop2 = b.getVal2().asRegister();
	  // Simple SSA based test
	  if (rop1.register.isSSA()) {
	    if(rop2.register.isSSA()) {
	      // ordering is arbitrary, ignore
	    } else {
	      // swap
	      b.setVal1(rop2);
	      b.setVal2(rop1);
	    }
	  } else if (rop2.register.isSSA()) {
	    // already have prefered ordering
	  } else {
	    // neither registers are SSA so place registers used more on the RHS
	    // (we don't have easy access to a count of the number of definitions)
	    if (rop1.register.useCount > rop2.register.useCount) {
	      // swap
	      b.setVal1(rop2);
	      b.setVal2(rop1);
	    }
	  }
	}
      }
    }
  }

  /**
   * Perform flow-insensitive copy and constant propagation using
   * register list information.
   *
   * <ul>
   * <li> Note: register list MUST be initialized BEFORE calling this routine.
   * <li> Note: this function incrementally maintains the register list.
   * </ul>
   *
   * @param ir the IR in question
   */
  public static void copyPropagation(IR ir) {
    // Use register list to enumerate register objects
    Register elemNext;
    boolean reiterate = true;
    while (reiterate) {         // /MT/ better think about proper ordering.
      reiterate = false;
      for (Register reg = ir.regpool.getFirstSymbolicRegister(); reg != null; reg = elemNext) {
        elemNext = reg.getNext(); // we may remove reg, so get elemNext up front
        if (reg.useList == null ||   // Copy propagation not possible if reg
            // has no uses
            reg.defList == null ||   // Copy propagation not possible if reg
            // has no defs
            !reg.isSSA()) {           // Flow-insensitive copy prop only possible
          // for SSA registers.
          continue;
        }
        // isSSA => reg has exactly one definition, reg.defList.
        RegisterOperand lhs = reg.defList;
        Instruction defInstr = lhs.instruction;
        Operand rhs;
        // Copy/constant propagation only possible when defInstr is a move
        if (defInstr.isMove()) {
          rhs = ((Move)defInstr).getVal();
        } else if (defInstr instanceof Phi) {
          Operand phiVal = equivalentValforPHI(defInstr);
          if (phiVal == null) continue;
          rhs = phiVal;
        } else {
          continue;
        }

        if (rhs.isRegister()) {
          Register rrhs = rhs.asRegister().getRegister();
          // If rhs is a non-SSA register, then we can't propagate it
          // because we can't be sure that the same definition reaches
          // all uses.
          if (!rrhs.isSSA()) continue;

          // If rhs is a physical register, then we can't safely propagate
          // it to uses of lhs because we don't understand the implicit
          // uses/defs of physical registers well enough to do so safely.
          if (rrhs.isPhysical()) continue;
        }

        reiterate = ir.options.getOptLevel() > 1;
        // Now substitute rhs for all uses of lhs, updating the
        // register list as we go.
        if (rhs.isRegister()) {
          RegisterOperand nextUse;
          RegisterOperand rhsRegOp = rhs.asRegister();
          for (RegisterOperand use = reg.useList; use != null; use = nextUse) {
            nextUse = use.getNext(); // get early before LOCAL_EXPRESSION_FOLDINGreg's useList is updated.
            assert rhsRegOp.getRegister().getType() == use.getRegister().getType();
            DefUse.transferUse(ir, use, rhsRegOp);
          }
        } else if (rhs.isConstant()) {
          // NOTE: no need to incrementally update use's register list since we are going
          //       to blow it all away as soon as this loop is done.
          for (RegisterOperand use = reg.useList; use != null; use = use.getNext()) {
            int index = use.getIndexInInstruction();
            use.instruction.putOperand(index, rhs.copy());
          }
        } else {
          throw new Error("Simple.copyPropagation: unexpected operand type");
        }
        // defInstr is now dead. Remove it.
        defInstr.remove();
        if (rhs.isRegister()) {
          DefUse.removeUse(ir, rhs.asRegister());
        }
        ir.regpool.removeRegister(lhs.getRegister());
      }
    }
  }

  /**
   * Try to find an operand that is equivalent to the result of a
   * given phi instruction.
   *
   * @param phi the instruction to be simplified
   * @return one of the phi's operands that is equivalent to the phi's result,
   * or null if the phi can not be simplified.
   */
  static Operand equivalentValforPHI(Instruction s) {
    if (!(s instanceof Phi)) return null;
    Phi phi = (Phi)s;
    // search for the first input that is different from the result
    Operand result = phi.getResult(), equiv = result;
    int i = 0, n = phi.getNumberOf();
    while (i < n) {
      equiv = phi.getValue(i++);
      if (!equiv.similar(result)) break;
    }
    // no luck if result and equiv aren't the only distinct inputs
    while (i < n) {
      Operand opi = phi.getValue(i++);
      if (!opi.similar(equiv) && !opi.similar(result)) return null;
    }
    return equiv;
  }

  /**
   * Perform flow-insensitive type propagation using register list
   * information. Note: register list MUST be initialized BEFORE
   * calling this routine.
   *
   * <p> Kept separate from copyPropagation loop to enable clients
   * more flexibility.
   *
   * @param ir the IR in question
   */
  static void typePropagation(IR ir) {
    // Use register list to enumerate register objects (FAST)
    Register elemNext;
    for (Register reg = ir.regpool.getFirstSymbolicRegister(); reg != null; reg = elemNext) {
      elemNext = reg.getNext();
      // Type propagation not possible if reg has no uses
      if (reg.useList == null) {
        continue;
      }
      // Type propagation not possible if reg has no defs
      if (reg.defList == null) {
        continue;
      }
      // Do not attempt type propagation if reg has multiple defs
      if (!reg.isSSA()) {
        continue;
      }
      // Now reg has exactly one definition
      RegisterOperand lhs = reg.defList;
      Instruction instr = lhs.instruction;

      // Type propagation not possible if lhs is not in a move instr
      if (!instr.isMove()) {
        continue;
      }
      Move m = (Move)instr;
      Operand rhsOp = m.getVal();
      // Do not attempt type propagation if RHS is not a register
      if (!(rhsOp instanceof RegisterOperand)) {
        continue;
      }
      RegisterOperand rhs = (RegisterOperand) rhsOp;
      // Propagate the type in the def
      lhs.copyType(rhs);

      // Now propagate lhs into all uses; substitute rhs.type for lhs.type
      for (RegisterOperand use = reg.useList; use != null; use = use.getNext()) {
        // if rhs.type is a supertype of use.type, don't do it
        // because use.type has more detailed information
        if (Type.includesType(rhs.getType(), use.getType())) {
          continue;
        }
        // If Magic has been employed to convert an int to a reference,
        // don't undo the effects!
        if (rhs.getType().isPrimitiveType() && !use.getType().isPrimitiveType()) {
          continue;
        }
        use.copyType(rhs);
      }
    }
  }

  /**
   * Perform flow-insensitive propagation to eliminate bounds checks
   * and arraylength for arrays with static lengths. Only useful on the HIR
   * (because BOUNDS_CHECK is expanded in LIR into multiple instrs)
   *
   * <p> Note: this function incrementally maintains the register list.
   *
   * @param ir the IR in question
   */
  static void arrayPropagation(IR ir) {
    // Use register list to enumerate register objects (FAST)
    Register elemNext;
    for (Register reg = ir.regpool.getFirstSymbolicRegister(); reg != null; reg = elemNext) {
      elemNext = reg.getNext();
      if (reg.useList == null) {
        continue;
      }
      if (reg.defList == null) {
        continue;
      }
      if (!reg.isSSA()) {
        continue;
      }
      // Now reg has exactly one definition
      RegisterOperand lhs = reg.defList;
      Instruction instr = lhs.instruction;

      if (!(instr instanceof Newarray || instr instanceof NewarrayUnresolved)) {
        continue;
      }
      NewArrayParent na = (NewArrayParent)instr;
      Operand sizeOp = na.getSize();
      // check for an array whose length is a compile-time constant
      // or an SSA register
      boolean boundsCheckOK = false;
      boolean arraylengthOK = false;
      int size = -1;
      if (sizeOp instanceof IntConstantOperand) {
        size = ((IntConstantOperand) sizeOp).value;
        boundsCheckOK = true;
        arraylengthOK = true;
      } else if (sizeOp instanceof RegisterOperand) {
        if (sizeOp.asRegister().getRegister().isSSA()) {
          arraylengthOK = true;
        }
      }
      // Now propagate
      for (RegisterOperand use = reg.useList; use != null; use = use.getNext()) {
        Instruction i = use.instruction;
        // bounds-check elimination
        if (boundsCheckOK && i instanceof BoundsCheck) {
          BoundsCheck bc = (BoundsCheck)i;
          Operand indexOp = bc.getIndex();
          if (indexOp instanceof IntConstantOperand) {
            if (((IntConstantOperand) indexOp).value <= size) {
              Instruction s = new GuardMove(bc.getGuardResult().copyD2D(), new TrueGuardOperand());
              i.insertAfter(s);
              DefUse.updateDUForNewInstruction(s);
              DefUse.removeInstructionAndUpdateDU(ir, i);
            }
          }
        } else if (arraylengthOK && i instanceof Arraylength) {
          Operand newSizeOp = sizeOp.copy();
          RegisterOperand result = (RegisterOperand) ((GuardedUnary)i).getResult().copy();
          Instruction s = new IntMove(result, newSizeOp);
          i.insertAfter(s);
          DefUse.updateDUForNewInstruction(s);
          DefUse.removeInstructionAndUpdateDU(ir, i);
        }
      }
    }
  }

  /**
   * Simple conservative dead code elimination.
   * An instruction is eliminated if:
   * <ul>
   *  <li> 1. it is not a PEI, store or call
   *  <li> 2. it DEFs only registers
   *  <li> 3. all registers it DEFS are dead
   * </ul>
   *
   * <p> Note: this function incrementally maintains the register list.
   *
   * @param ir the IR to optimize
   */
  static void eliminateDeadInstructions(IR ir) {
    eliminateDeadInstructions(ir, false);
  }

  /**
   * Simple conservative dead code elimination.
   * An instruction is eliminated if:
   * <ul>
   *  <li> 1. it is not a PEI, store or non-pure call
   *  <li> 2. it DEFs only registers
   *  <li> 3. all registers it DEFS are dead
   * </ul>
   *
   * <p> Note: this function incrementally maintains the register list.
   *
   * @param ir IR to optimize
   * @param preserveImplicitSSA if this is true, do not eliminate dead
   * instructions that have implicit operands for heap array SSA form
   */
  public static void eliminateDeadInstructions(IR ir, boolean preserveImplicitSSA) {
    // (USE BACKWARDS PASS FOR INCREASED EFFECTIVENESS)
    ArrayList<Instruction> setCaughtExceptionInstructions = null;
    int getCaughtExceptionInstructions = 0;
    for (Instruction instr = ir.lastInstructionInCodeOrder(),
        prevInstr = null; instr != null; instr = prevInstr) {
      prevInstr = instr.prevInstructionInCodeOrder(); // cache because
      // remove nulls next/prev fields
      // if instr is a PEI, store, branch, or call, then it's not dead ...
      if (instr.isPEI() || instr.isImplicitStore() || instr.isBranch() || instr.isNonPureCall()) {
        continue;
      }
      if (preserveImplicitSSA && (instr.isImplicitLoad() || instr.isAlloc() || instr instanceof Phi)) {
        continue;
      }

      if (instr instanceof SetCaughtException) {
        if (setCaughtExceptionInstructions == null) {
          setCaughtExceptionInstructions = new ArrayList<Instruction>();
        }
        setCaughtExceptionInstructions.add(instr);
      }

      // remove NOPs
      if (instr instanceof Nop) {
        DefUse.removeInstructionAndUpdateDU(ir, instr);
      }

      // remove UNINT_BEGIN/UNINT_END with nothing in between them
      if (instr instanceof UnintBegin) {
        Instruction s = instr.nextInstructionInCodeOrder();
        if (s instanceof UnintEnd) {
          DefUse.removeInstructionAndUpdateDU(ir, s);
          DefUse.removeInstructionAndUpdateDU(ir, instr);
        }
      }

      // remove trivial assignments
      if (instr instanceof Move) {
	Move m = (Move)instr;
        Register lhs = m.getResult().asRegister().getRegister();
        if (m.getVal().isRegister()) {
          Register rhs = m.getVal().asRegister().getRegister();
          if (lhs == rhs) {
            DefUse.removeInstructionAndUpdateDU(ir, instr);
            continue;
          }
        }
      }
      if (instr instanceof GetCaughtException) {
        getCaughtExceptionInstructions++;
      }

      // check that all defs are to dead registers and that
      // there is at least 1 def.
      boolean isDead = true;
      boolean foundRegisterDef = false;
      for (OperandEnumeration defs = instr.getDefs(); defs.hasMoreElements();) {
        Operand def = defs.next();
        if (!def.isRegister()) {
          isDead = false;
          break;
        }
        foundRegisterDef = true;
        RegisterOperand r = def.asRegister();
        if (r.getRegister().useList != null) {
          isDead = false;
          break;
        }
        if (r.getRegister().isPhysical()) {
          isDead = false;
          break;
        }
      }
      if (!isDead) {
        continue;
      }
      if (!foundRegisterDef) {
        continue;
      }
      if (instr instanceof GetCaughtException) {
        getCaughtExceptionInstructions--;
      }
      // There are 1 or more register defs, but all of them are dead.
      // Remove instr.
      DefUse.removeInstructionAndUpdateDU(ir, instr);
    }
    if (false && // temporarily disabled - see RVM-410
        (getCaughtExceptionInstructions == 0) &&
        (setCaughtExceptionInstructions != null)) {
      for (Instruction instr : setCaughtExceptionInstructions) {
        DefUse.removeInstructionAndUpdateDU(ir, instr);
      }
    }
  }

  /**
   * Perform constant folding.
   *
   * @param ir the IR to optimize
   */
  void foldConstants(IR ir) {
    boolean recomputeRegList = false;
    for (Instruction s = ir.firstInstructionInCodeOrder(); s != null; s = s.nextInstructionInCodeOrder()) {
      Instruction newS = Simplifier.simplify(ir.regpool, ir.options, s);
      if (newS != null) {
	recomputeRegList = true;
	s = newS;
      }
    }
    if (recomputeRegList) {
      DefUse.computeDU(ir);
      DefUse.recomputeSSA(ir);
    }
  }

  /**
   * Simplify branches whose operands are constants.
   *
   * <p> NOTE: This pass ensures that the register list is still valid after it
   * is done.
   *
   * @param ir the IR to optimize
   */
  void simplifyConstantBranches(IR ir) {
    boolean didSomething = false;
    for (BasicBlockEnumeration e = ir.forwardBlockEnumerator(); e.hasMoreElements();) {
      BasicBlock bb = e.next();
      didSomething |= BranchSimplifier.simplify(bb, ir);
    }
    if (didSomething) {
      // killed at least one branch, cleanup the CFG removing dead code.
      // Then recompute register list and isSSA info
      branchOpts.perform(true);
      DefUse.computeDU(ir);
      DefUse.recomputeSSA(ir);
    }
  }
}
