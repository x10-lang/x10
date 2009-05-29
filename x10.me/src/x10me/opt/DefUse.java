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

package x10me.opt;


import x10me.opt.controlflow.BasicBlock;
import x10me.opt.ir.*;
import x10me.opt.ir.operand.*;
import x10me.types.KnownTypes;


/**
 * This class computes du-lists and associated information.
 *
 * <P> Note: DU operands are stored on the USE lists, but not the DEF
 * lists.
 */
public final class DefUse {

  /**
   * Clear defList, useList for an IR.
   *
   * @param ir the IR in question
   */
  public static void clearDU(IR ir) {
    for (Register reg = ir.regpool.getFirstSymbolicRegister(); reg != null; reg = reg.getNext()) {
      reg.defList = null;
      reg.useList = null;
      reg.scratch = -1;
      reg.clearSeenUse();
    }

    if (ir.dumpFile.current != null) {
      ir.dumpFile.current.println("Cleared DU");
    }
  }

  /**
   * Compute the register list and def-use lists for a method.
   *
   * @param ir the IR in question
   */
  public static void computeDU(IR ir) {
    // Clear old register list (if any)
    clearDU(ir);
    // Create register defList and useList
    for (Instruction instr = ir.firstInstructionInCodeOrder(); instr != null; instr =
        instr.nextInstructionInCodeOrder()) {

      OperandEnumeration defs = instr.getDefs();
      OperandEnumeration uses = instr.getUses();

      while (defs.hasMoreElements()) {
        Operand op = defs.next();
        if (op instanceof RegisterOperand) {
          RegisterOperand rop = (RegisterOperand) op;
          recordDef(rop);
        }
      }         // for ( defs = ... )

      while (uses.hasMoreElements()) {
        Operand op = uses.next();
        if (op instanceof RegisterOperand) {
          RegisterOperand rop = (RegisterOperand) op;
          recordUse(rop);
        }
      }         // for ( uses = ... )
    }           // for ( instr = ... )
    // Remove any symbloic registers with no uses/defs from
    // the register pool.  We'll waste analysis time keeping them around.
    Register next;
    for (Register reg = ir.regpool.getFirstSymbolicRegister(); reg != null; reg = next) {
      next = reg.getNext();
      if (reg.defList == null && reg.useList == null) {
        if (ir.dumpFile.current != null) {
          ir.dumpFile.current.println("Removing " + reg + " from the register pool");
        }
        ir.regpool.removeRegister(reg);
      }
    }
  }

  /**
   * Record a use of a register
   * @param regOp the operand that uses the register
   */
  public static void recordUse(RegisterOperand regOp) {
    Register reg = regOp.getRegister();
    regOp.append(reg.useList);
    reg.useList = regOp;
    reg.useCount++;
  }

  /**
   * Record a def/use of a register
   * TODO: For now we just pretend this is a use!!!!
   *
   * @param regOp the operand that uses the register
   */
  public static void recordDefUse(RegisterOperand regOp) {
    Register reg = regOp.getRegister();
    regOp.append(reg.useList);
    reg.useList = regOp;
  }

  /**
   * Record a def of a register
   * @param regOp the operand that uses the register
   */
  public static void recordDef(RegisterOperand regOp) {
    Register reg = regOp.getRegister();
    regOp.append(reg.defList);
    reg.defList = regOp;
  }

  /**
   * Record that a use of a register no longer applies
   * @param regOp the operand that uses the register
   */
  public static void removeUse(IR ir, RegisterOperand regOp) {
    Register reg = regOp.getRegister();
    if (regOp == reg.useList) {
      reg.useList = reg.useList.getNext();
    } else {
      RegisterOperand prev = reg.useList;
      RegisterOperand curr = prev.getNext();
      while (curr != regOp) {
        prev = curr;
        curr = curr.getNext();
      }
      prev.setNext(curr.getNext());
    }
    reg.useCount--;
    if (ir.dumpFile.current != null) {
      ir.dumpFile.current.println("removed a use " + regOp.instruction);
      printUses(ir, reg);
    }
  }

  /**
   * Record that a def of a register no longer applies
   * @param regOp the operand that uses the register
   */
  public static void removeDef(IR ir, RegisterOperand regOp) {
    Register reg = regOp.getRegister();
    if (regOp == reg.defList) {
      reg.defList = reg.defList.getNext();
    } else {
      RegisterOperand prev = reg.defList;
      RegisterOperand curr = prev.getNext();
      while (curr != regOp) {
        prev = curr;
        curr = curr.getNext();
      }
      prev.setNext(curr.getNext());
    }
    if (ir.dumpFile.current != null) {
      ir.dumpFile.current.println("removed a def " + regOp.instruction);
      printDefs(ir, reg);
    }
  }

  /**
   *  This code changes the use in <code>origRegOp</code> to use
   *    the use in <code>newRegOp</code>.
   *
   *  <p> If the type of <code>origRegOp</code> is not a reference, but the
   *  type of <code>newRegOp</code> is a reference, we need to update
   *  <code>origRegOp</code> to be a reference.
   *  Otherwise, the GC map code will be incorrect.   -- Mike Hind
   *  @param origRegOp the register operand to change
   *  @param newRegOp the register operand to use for the change
   */
  public static void transferUse(IR ir, RegisterOperand origRegOp, RegisterOperand newRegOp) {
    assert origRegOp.getRegister().getType() == newRegOp.getRegister().getType();
    Instruction inst = origRegOp.instruction;
    if (ir.dumpFile.current != null) {
      ir.dumpFile.current.println("Transfering a use of " + origRegOp + " in " + inst + " to " + newRegOp);
    }
    removeUse(ir, origRegOp);
    // check to see if the regOp type is NOT a ref, but the newRegOp type
    // is a reference.   This can occur because of magic calls.
    if (!origRegOp.getType().isReferenceType() && newRegOp.getType().isReferenceType()) {
      // clone the newRegOp object and use it to replace the regOp object
      RegisterOperand copiedRegOp = (RegisterOperand) newRegOp.copy();
      inst.replaceOperand(origRegOp, copiedRegOp);
      recordUse(copiedRegOp);
    } else {
      // just copy the register
      origRegOp.setRegister(newRegOp.getRegister());
      if (newRegOp.getType() != KnownTypes.OBJECT_TYPE &&
          !newRegOp.getType().isUnboxedType() && !origRegOp.isPreciseType()) {
        // copy type information from new to orig unless its an unboxed type
        // (we don't want to copy type information for unboxed types as it is
        // likely the result of inlining new) or the type of the original is
        // precise
        origRegOp.copyType(newRegOp);
      }
      recordUse(origRegOp);
    }
    if (ir.dumpFile.current != null) {
      printUses(ir, origRegOp.getRegister());
      printUses(ir, newRegOp.getRegister());
    }
  }

  /**
   * Remove an instruction and update register lists.
   */
  public static void removeInstructionAndUpdateDU(IR ir, Instruction s) {
    for (OperandEnumeration e = s.getDefs(); e.hasMoreElements();) {
      Operand op = e.next();
      if (op instanceof RegisterOperand) {
        removeDef(ir, (RegisterOperand) op);
      }
    }
    for (OperandEnumeration e = s.getUses(); e.hasMoreElements();) {
      Operand op = e.next();
      if (op instanceof RegisterOperand) {
        removeUse(ir, (RegisterOperand) op);
      }
    }
    s.remove();
  }

  /**
   * Update register lists to account for the effect of a new
   * instruction s
   */
  public static void updateDUForNewInstruction(Instruction s) {
    for (OperandEnumeration e = s.getDefs(); e.hasMoreElements();) {
      Operand op = e.next();
      if (op instanceof RegisterOperand) {
        recordDef((RegisterOperand) op);
      }
    }
    for (OperandEnumeration e = s.getUses(); e.hasMoreElements();) {
      Operand op = e.next();
      if (op instanceof RegisterOperand) {
        recordUse((RegisterOperand) op);
      }
    }
  }

  /**
   * Replace an instruction and update register lists.
   */
  public static void replaceInstructionAndUpdateDU(IR ir, Instruction oldI, Instruction newI) {
    oldI.insertBefore(newI);
    removeInstructionAndUpdateDU(ir, oldI);
    updateDUForNewInstruction(newI);
  }

  /**
   * Enumerate all operands that use a given register.
   */
  public static RegisterOperandEnumeration uses(Register reg) {
    return new RegOpListWalker(reg.useList);
  }

  /**
   * Enumerate all operands that def a given register.
   */
  public static RegisterOperandEnumeration defs(Register reg) {
    return new RegOpListWalker(reg.defList);
  }

  /**
   * Does a given register have exactly one use?
   */
  static boolean exactlyOneUse(Register reg) {
    return (reg.useList != null) && (reg.useList.getNext() == null);
  }

  /**
   * Print all the instructions that def a register.
   * @param reg
   */
  static void printDefs(IR ir, Register reg) {
    ir.dumpFile.current.println("Definitions of " + reg);
    for (RegisterOperandEnumeration e = defs(reg); e.hasMoreElements();) {
      ir.dumpFile.current.println("\t" + e.next().instruction);
    }
  }

  /**
   * Print all the instructions that usea register.
   * @param reg
   */
  static void printUses(IR ir, Register reg) {
    ir.dumpFile.current.println("Uses of " + reg);
    for (RegisterOperandEnumeration e = uses(reg); e.hasMoreElements();) {
      ir.dumpFile.current.println("\t" + e.next().instruction);
    }
  }

  /**
   * Recompute <code> isSSA </code> for all registers by traversing register
   * list.
   * NOTE: the DU MUST be computed BEFORE calling this function
   *
   * @param ir the IR in question
   */
  public static void recomputeSSA(IR ir) {
    // Use register /ist to enumerate register objects (FAST)
    for (Register reg = ir.regpool.getFirstSymbolicRegister(); reg != null; reg = reg.getNext()) {
      // Set isSSA = true iff reg has exactly one static definition.
      reg.putSSA((reg.defList != null && reg.defList.getNext() == null));
    }
  }

  /**
   * Merge register reg2 into register reg1.
   * Remove reg2 from the DU information
   */
  public static void mergeRegisters(IR ir, Register reg1, Register reg2) {
    RegisterOperand lastOperand;
    if (reg1 == reg2) {
      return;
    }
    if (ir.dumpFile.current != null) {
      ir.dumpFile.current.println("Merging " + reg2 + " into " + reg1);
      printDefs(ir, reg2);
      printUses(ir, reg2);
      printDefs(ir, reg1);
      printUses(ir, reg1);
    }
    // first loop through defs of reg2 (currently, there will only be one def)
    lastOperand = null;
    for (RegisterOperand def = reg2.defList; def != null; lastOperand = def, def = def.getNext()) {
      // Change def to refer to reg1 instead
      def.setRegister(reg1);
      // Track lastOperand
      lastOperand = def;
    }
    if (lastOperand != null) {
      // Set reg1.defList = concat(reg2.defList, reg1.deflist)
      lastOperand.setNext(reg1.defList);
      reg1.defList = reg2.defList;
    }
    // now loop through uses
    lastOperand = null;
    for (RegisterOperand use = reg2.useList; use != null; use = use.getNext()) {
      // Change use to refer to reg1 instead
      use.setRegister(reg1);
      // Track lastOperand
      lastOperand = use;
    }
    if (lastOperand != null) {
      // Set reg1.useList = concat(reg2.useList, reg1.uselist)
      lastOperand.setNext(reg1.useList);
      reg1.useList = reg2.useList;
    }
    // Remove reg2 from RegisterPool
    ir.regpool.removeRegister(reg2);
    if (ir.dumpFile.current != null) {
      ir.dumpFile.current.println("Merge complete");
      printDefs(ir, reg1);
      printUses(ir, reg1);
    }
  }

  /**
   * Recompute spansBasicBlock flags for all registers.
   *
   * @param ir the IR in question
   */
  public static void recomputeSpansBasicBlock(IR ir) {
    // clear fields
    for (Register reg = ir.regpool.getFirstSymbolicRegister(); reg != null; reg = reg.getNext()) {
      reg.scratch = -1;
      reg.clearSpansBasicBlock();
    }
    // iterate over the basic blocks
    for (BasicBlock bb = ir.firstBasicBlockInCodeOrder(); bb != null; bb = bb.nextBasicBlockInCodeOrder()) {
      int bbNum = bb.getNumber();
      // enumerate the instructions in the basic block
      for (InstructionEnumeration e = bb.forwardRealInstrEnumerator(); e.hasMoreElements();) {
        Instruction inst = e.next();
        // check each Operand in the instruction
        for (OperandEnumeration ops = inst.getOperands(); ops.hasMoreElements();) {
          Operand op = ops.next();
          if (op instanceof RegisterOperand) {
            Register reg = ((RegisterOperand) op).getRegister();
            if (reg.isPhysical()) {
              continue;
            }
            if (reg.spansBasicBlock()) {
              continue;
            }
            if (seenInDifferentBlock(reg, bbNum)) {
              reg.setSpansBasicBlock();
              continue;
            }
            if (inst instanceof Phi) {
              reg.setSpansBasicBlock();
              continue;
            }
            logAppearance(reg, bbNum);
          }
        }
      }
    }
  }

  /**
   * Mark that we have seen a register in a particular
   * basic block, and whether we saw a use
   *
   * @param reg the register
   * @param bbNum the number of the basic block
   */
  private static void logAppearance(Register reg, int bbNum) {
    reg.scratch = bbNum;
  }

  /**
   * Have we seen this register in a different basic block?
   *
   * @param reg the register
   * @param bbNum the number of the basic block
   */
  private static boolean seenInDifferentBlock(Register reg, int bbNum) {
    int bb = reg.scratch;
    return (bb != -1) && (bb != bbNum);
  }

  /**
   * Utility class to encapsulate walking a use/def list.
   */
  private static final class RegOpListWalker implements RegisterOperandEnumeration {

    private RegisterOperand current;

    RegOpListWalker(RegisterOperand start) {
      current = start;
    }

    public boolean hasMoreElements() {
      return current != null;
    }

    public RegisterOperand nextElement() {
      return next();
    }

    public RegisterOperand next() {
      if (current == null) raiseNoSuchElementException();
      RegisterOperand tmp = current;
      current = current.getNext();
      return tmp;
    }

    private static void raiseNoSuchElementException() {
      throw new java.util.NoSuchElementException("RegOpListWalker");
    }
  }
}
