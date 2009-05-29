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

import java.lang.reflect.Constructor;

import x10me.opt.controlflow.BasicBlock;
import x10me.opt.driver.CompilerPhase;
import x10me.opt.driver.OptOptions;
import x10me.opt.ir.*;
import x10me.opt.ir.operand.MethodOperand;
import x10me.opt.ir.operand.Operand;
import x10me.opt.ir.operand.RegisterOperand;

/**
 * Transform tail recursive calls into loops.
 * <p>
 * NOTES:
 * <ul>
 * <li> This pass does not attempt to optimize all tail calls, just those
 *      that are directly recursive.
 * <li> Even the small optimization we are doing here destroys the ability
 *      to accurately support stack frame inspection.
 * <li> This phase assumes that is run before Yieldpoints and thus
 *      does not need to insert a yieldpoint in the newly created loop header.
 * </ul>
 */
public final class TailRecursionElimination extends CompilerPhase {

  public TailRecursionElimination(IR ir) {
    super(ir);
  }
  
  private final BranchOptimizations branchOpts = new BranchOptimizations(ir, -1, true, false);

  public boolean shouldPerform(OptOptions options) {
    return options.getOptLevel() >= 1;
  }

  public String getName() { return "Tail Recursion Elimination"; }

  /**
   * Perform tail recursion elimination.
   *
   * @param ir the IR to optimize
   */
  public void perform() {
    BasicBlock target = null;
    Instruction prologue = null;
    boolean didSomething = false;

    for (Instruction instr = ir.firstInstructionInCodeOrder(),
        nextInstr = null; instr != null; instr = nextInstr) {
      nextInstr = instr.nextInstructionInCodeOrder();

      switch (instr.getOpcode()) {
        case Operators.IrPrologue:
          prologue = instr;
          break;
        case Operators.Call:
          Call c = (Call)instr;
          if (isTailRecursion(c, ir)) {
            if (target == null) {
              target = prologue.getBasicBlock().splitNodeWithLinksAt(prologue, ir);
            }
            if (ir.dumpFile.current != null) dumpIR(ir, "Before transformation of " + instr);
            nextInstr = transform(c, prologue, target);
            if (ir.dumpFile.current != null) dumpIR(ir, "After transformation of " + instr);
            didSomething = true;
          }
          break;
        default:
          break;
      }
    }

    if (didSomething) {
      branchOpts.perform(true);
      if (ir.dumpFile.current != null) dumpIR(ir, "After cleanup");
      if (ir.dumpFile.current != null) {
        ir.dumpFile.current.println("Eliminated tail calls in " + ir.method);
      }
    }
  }

  /**
   * Is the argument call instruction a tail recursive call?
   *
   * @param call the call in question
   * @param ir the enclosing IR
   * @return <code>true</code> if call is tail recursive and
   *         <code>false</code> if it is not.
   */
  boolean isTailRecursion(Call call, IR ir) {
    if (!call.hasMethod()) return false;
    MethodOperand methOp = call.getMethod();
    if (!methOp.hasPreciseTarget()) return false;
    if (methOp.getTarget() != ir.method) return false;
    RegisterOperand result = (RegisterOperand)call.getResult();
    Instruction s = call.nextInstructionInCodeOrder();
    while (true) {
      if (s.isMove()) {
	Move m = (Move)s;
        if (m.getVal().similar(result)) {
          result = (RegisterOperand)m.getResult();
          if (ir.dumpFile.current != null) ir.dumpFile.current.println("Updating result to " + result);
        } else {
          return false; // move of a value that isn't the result blocks us
        }
      } else
      if (s instanceof Label || s instanceof BBend || s instanceof UnintBegin || s instanceof UnintEnd) {
        if (ir.dumpFile.current != null) ir.dumpFile.current.println("Falling through " + s);
        // skip over housekeeping instructions and follow the code order.
      } else if (s instanceof Goto) {
        // follow the unconditional branch to its target LABEL
        s = ((Goto)s).getBranchTarget().firstInstruction();
        if (ir.dumpFile.current != null) ir.dumpFile.current.println("Following goto to " + s);
      } else if (s.isReturn()) {
        Operand methodResult = ((Return)s).getVal();
        if (ir.dumpFile.current != null) ir.dumpFile.current.println("Found return " + s);
        return methodResult == null || methodResult.similar(result);
      } else {
        // any other instruction blocks us
        return false;
      }
      s = s.nextInstructionInCodeOrder();
    }
  }

  /**
   * Transform the tail recursive call into a loop.
   *
   * @param call     The recursive call
   * @param prologue The IR_Prologue instruction
   * @param target   The loop head
   * @param ir       the containing IR
   */
  Instruction transform(Call call, Instruction prologue, BasicBlock target) {
    // (1) insert move instructions to assign fresh temporaries
    //     the actuals of the call.
    int numParams = call.getNumberOf();
    RegisterOperand[] temps = new RegisterOperand[numParams];
    for (int i = 0; i < numParams; i++) {
      Operand actual = call.getClearParam(i);
      temps[i] = ir.regpool.makeTemp(actual);
      Instruction move = Move.create(temps[i].getType(), temps[i], actual);
      move.copyPosition(call);
      call.insertBefore(move);
    }

    // (2) insert move instructions to assign the formal parameters
    //     the corresponding fresh temporary
    for (int i = 0; i < numParams; i++) {
      RegisterOperand formal = ((Prologue)prologue).getFormal(i).copyD2D();
      Instruction move = Move.create(formal.getType(), formal, temps[i].copyD2U());
      move.copyPosition(call);
      call.insertBefore(move);
    }

    // (3) Blow away all instructions below the call in the basic block
    //     (should only be moves and other housekeeping instructions
    //      skipped over in isTailRecursion loop above)
    BasicBlock myBlock = call.getBasicBlock();
    Instruction dead = myBlock.lastRealInstruction();
    while (dead != call) {
      dead = dead.remove();
    }

    // (4) Insert a goto to jump from the call to the loop head
    call.insertAfter(new Goto(target.makeJumpTarget()));

    // (5) Remove the call instruction
    call.remove();

    // (6) Update the CFG
    myBlock.deleteNormalOut();
    myBlock.insertOut(target);

    return myBlock.lastInstruction();
  }
}
