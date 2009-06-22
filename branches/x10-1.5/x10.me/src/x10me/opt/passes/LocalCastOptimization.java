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
import x10me.opt.ir.*;
import x10me.opt.ir.operand.NullConstantOperand;
import x10me.opt.ir.operand.RegisterOperand;

/**
 * Perform simple peephole optimizations to reduce the overhead of
 * checking casts.  This code was inspired by some special cases in
 * handling checkcast in HIR2LIR, but the actual code is all different.
 *
 * <p> There are currently the following optimizations:
 * <ul>
 * <li> 1.  If a checkcast is just before a nullcheck, invert them and
 * convert the checkcast into a checkcast_not_null
 * <li> 2.  If a checkcast is followed by a branch based on a null test of
 * the same variable, then push the cast below the conditional on
 * the path where the obejct is known not to be null.  And convert
 * it to a checkcast_not_null
 * </ul>
 */
public final class LocalCastOptimization extends CompilerPhase {

  public String getName() {
    return "Local Cast Optimizations";
  }

  /**
   * Return this instance of this phase. This phase contains no
   * per-compilation instance fields.
   * @param ir not used
   * @return this
   */
  public LocalCastOptimization(IR ir) {
    super(ir);
  }

  @Override
  public void perform() {
    // loop over all basic blocks ...
    for (BasicBlockEnumeration e = ir.getBasicBlocks(); e.hasMoreElements();) {
      BasicBlock bb = e.next();
      if (bb.isEmpty()) continue;
      if (bb.getInfrequent() && ir.options.FREQ_FOCUS_EFFORT) continue;
  
      // visit each instruction in the basic block
      for (InstructionEnumeration ie = bb.forwardInstrEnumerator(); ie.hasMoreElements();) {
        Instruction s = ie.next();
        if (s instanceof TypeCheck && (invertNullAndTypeChecks(s) || pushTypeCheckBelowIf(s, ir))) {
          // hack: we may have modified the instructions; start over
          ie = bb.forwardInstrEnumerator();
        }
      }
    }
  }

  /**
   * If there's a checkcast followed by a null check, move the checkcast
   * after the null check, since the null pointer exception must be thrown
   * anyway.
   * @param s the potential checkcast instruction
   * @return true iff the transformation happened
   */
  private boolean invertNullAndTypeChecks(Instruction s) {
    if (s instanceof Checkcast) {
      Checkcast cc = (Checkcast) s;
      Register r = cc.getRef().asRegister().getRegister();
      Instruction n = s.nextInstructionInCodeOrder();
      while (n instanceof RefMove
	  && ((Move) n).getVal() instanceof RegisterOperand
	  && ((Move) n).getVal().asRegister().getRegister() == r) {
	r = ((Move) n).getResult().asRegister().getRegister();
	n = n.nextInstructionInCodeOrder();
      }
      if (n instanceof NullCheck
	  && cc.getRef().asRegister().getRegister() 
	  == ((NullCheck) n).getRef().asRegister().getRegister()) {
	s.remove();
	n.insertAfter(new CheckcastNotnull(cc.getClearResult(), cc.getClearRef(), 
	    cc.getClearType(), ((NullCheck) n).getGuardResult()
	    .copy()));
	return true;
      }
    }
    return false;
  }

  /**
   * Where legal, move a type check below an if instruction.
   * @param s the potential typecheck instruction
   * @param ir the governing IR
   */
  private boolean pushTypeCheckBelowIf(Instruction s, IR ir) {
    if (s instanceof Checkcast) {
      Checkcast cc = (Checkcast)s;
      Register r = cc.getRef().asRegister().getRegister();
      Instruction n = s.nextInstructionInCodeOrder();
      /* find moves of the checked value, so that we can also
         optimize cases where the checked value is moved before
         it is used
      */
      while (n instanceof RefMove &&
             ((Move)n).getVal() instanceof RegisterOperand &&
             ((Move)n).getVal().asRegister().getRegister() == r) {
        r = ((Move)n).getResult().asRegister().getRegister();
        n = n.nextInstructionInCodeOrder();
      }
      if (n instanceof RefIfcmp) {
	RefIfcmp ric = (RefIfcmp)n;
	if (ric.getVal2() instanceof NullConstantOperand &&
	    ric.getVal1() instanceof RegisterOperand &&
	    r == ric.getVal1().asRegister().getRegister()) {
	  BasicBlock newBlock, patchBlock;
	  BasicBlock myBlock = n.getBasicBlock();
	  Instruction after = n.nextInstructionInCodeOrder();
	  if (ric.getCond().isEQUAL())
	    /*  We fall through on non-NULL values, so the
              checkcast must be on the not-taken path
              from the branch.  There are 3 cases:
              1. n is the last instruction in its basic block,
              in which case control falls through to the next
              block in code order.  This case is if the
              instruction after n is a BBEND
	     */ {
	    if (after instanceof BBend) {
	      patchBlock = myBlock.nextBasicBlockInCodeOrder();
	    } else if (after instanceof Goto) {
	      /* 2. n is followed by an unconditional goto.  In
               this case control jumps to the target of the
               goto.
	       */
	      patchBlock = ((Goto)after).getBranchTarget();
	    } else if (after instanceof RefIfcmp) {
	      /* 3. n is followed by another conditional branch. In
               this case, we will split the basic block to make
               n the last instruction in the block, and then
               we have the fall through case again.
	       */
	      patchBlock = myBlock.splitNodeAt(n, ir);
	      myBlock.insertOut(patchBlock);
	      ir.cfg.linkInCodeOrder(myBlock, patchBlock);
	    } else {
	      /* this is a bad thing */
	      return false;
	    }
	  } else
	    /* We branch on not-NULL values, so the checkcast
             must be spliced in before the branch target
	     */ {
	    patchBlock = ((HasTarget)n).getBranchTarget();
	  }
	  /* add block between branch and appropriate successor */

	  newBlock = BasicBlock.makeBlockOnEdge(myBlock, patchBlock, ir);

	  /* put check in new block */
	  s.remove();
	  
	  newBlock.prependInstruction(new CheckcastNotnull(cc.getClearResult(),
	      cc.getClearRef(), cc.getClearType(), ric.getGuardResult().copyRO()));
	  return true;
	}
      }
    }
    return false;
  }
}
