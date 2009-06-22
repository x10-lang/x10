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

package x10me.opt.ssa;

import java.util.Enumeration;

import x10me.opt.bc2ir.BCconstants;
import x10me.opt.controlflow.BasicBlock;
import x10me.opt.ir.*;
import x10me.opt.ir.operand.BasicBlockOperand;
import x10me.opt.ir.operand.ConstantOperand;
import x10me.opt.ir.operand.HeapOperand;
import x10me.opt.ir.operand.Operand;
import x10me.opt.ir.operand.OperandEnumeration;
import x10me.opt.ir.operand.RegisterOperand;
import x10me.types.Type;

/**
 * This module holds utility functions for SSA form.
 *
 * Our SSA form is <em> Heap Array SSA Form </em>, an extension of
 * SSA that allows analysis of scalars, arrays, and object fields
 * in a unified framework.  See our SAS 2000 paper
 * <a href="http://www.research.ibm.com/jalapeno/publication.html#sas00">
 *  Unified Analysis of Arrays and Object References in Strongly Typed
 *  Languages </a>
 * <p> Details about our current implementation include:
 * <ul>
 *  <li> We explicitly place phi-functions as instructions in the IR.
 *  <li> Scalar registers are explicitly renamed in the IR.
 *  <li> Heap variables are represented implicitly. Each instruction
 *       that reads or writes from the heap implicitly uses a Heap variable.
 *       The particular heap variable for each instruction is cached
 *       in {@link SSADictionary <code> ir.HIRInfo.dictionary </code>}.
 *       dphi functions do <em> not </em>
 *       explicitly appear in the IR.
 *  <p>
 *  For example, consider the code:
 *  <p>
 *  <pre>
 *              a.x = z;
 *              b[100] = 5;
 *              y = a.x;
 *  </pre>
 *
 *  <p>Logically, we translate to Array SSA form (before renumbering) as:
 *  <pre>
 *              HEAP_x[a] = z
 *              HEAP_x = dphi(HEAP_x,HEAP_x)
 *              HEAP_I[] = { < b,100,5 > }
 *              HEAP_I[] = dphi(HEAP_I[], HEAP_I[])
 *              y = HEAP_x[a]
 *  </pre>
 *
 *  <p> However, the implementation does not actually modify the instruction
 *      stream. Instead, we keep track of the following information with
 *  <code> ir.HIRInfo.dictionary </code>:
 *  <pre>
 *              a.x = z  (implicit: reads HEAP_x, writes HEAP_x)
 *              b[100] =5 (implicit: reads HEAP_I[], writes HEAP_I[])
 *              y = a.x   (implicit: reads HEAP_x)
 *  </pre>
 *
 *  <p>Similarly, phi functions for the implicit heap
 *  variables <em> will not </em>
 *  appear explicitly in the instruction stream. Instead, the
 *  SSADictionary data structure keeps the heap control phi
 *  functions for each basic block in a lookaside table.
 *  </ul>
 *
 * @see EnterSSA
 * @see LeaveSSA
 * @see SSADictionary
 * @see org.jikesrvm.compilers.opt.ir.HIRInfo
 */
class SSA {

  /**
   * Add a move instruction at the end of a basic block, renaming
   * with a temporary register if needed to protect conditional branches
   * at the end of the block.
   *
   * @param ir governing IR
   * @param bb the basic block
   * @param c  the move instruction to insert
   * @param exp whether or not to respect exception control flow at the
   *            end of the block
   */
  static void addAtEnd(IR ir, BasicBlock bb, Instruction c, boolean exp) {
    if (exp) {
      bb.appendInstructionRespectingTerminalBranchOrPEI(c);
    } else {
      bb.appendInstructionRespectingTerminalBranch(c);
    }
    RegisterOperand aux = null;
    Move mv = (Move)c;
    
    RegisterOperand lhs = (RegisterOperand)mv.getResult();
    Instruction i = mv.nextInstructionInCodeOrder();
    while (!(i instanceof BBend)) {
      OperandEnumeration os = i.getUses();
      while (os.hasMoreElements()) {
        Operand op = os.next();
        if (lhs.similar(op)) {
          if (aux == null) {
            aux = ir.regpool.makeTemp(lhs);
            c.insertBefore(makeMoveInstruction(ir, aux.getRegister(), lhs.getRegister(), lhs.getType()));
          }
          op.asRegister().setRegister(aux.getRegister());
        }
      }
      i = i.nextInstructionInCodeOrder();
    }
  }

  /**
   * Create a move instruction r1 := r2.
   *
   * TODO: This utility function should be moved elsewhere.
   *
   * @param ir the governing ir
   * @param r1 the destination
   * @param r2 the source
   * @param t the type of r1 and r2.
   */
  static Instruction makeMoveInstruction(IR ir, Register r1, Register r2, Type t) {
    RegisterOperand o1 = new RegisterOperand(r1, t);
    RegisterOperand o2 = new RegisterOperand(r2, t);
    Instruction s = Move.create(t, o1, o2);
    s.position = ir.inlineSequence;
    s.bcIndex = BCconstants.SSA_SYNTH_BCI;
    return s;
  }

  /**
   * Create a move instruction r1 := c.
   *
   * !!TODO: put this functionality elsewhere.
   *
   * @param ir the governing ir
   * @param r1 the destination
   * @param c the source
   */
  static Instruction makeMoveInstruction(IR ir, Register r1, ConstantOperand c) {
    Type t = c.getType ();
    RegisterOperand o1 = new RegisterOperand(r1, t);
    Operand o2 = c.copy();
    Instruction s = Move.create(t, o1, o2);
    s.position = ir.inlineSequence;
    s.bcIndex = BCconstants.SSA_SYNTH_BCI;
    return s;
  }

  /**
   * Fix up any PHI instructions in the given target block to reflect that
   * the given source block is no longer a predecessor of target.
   * The basic algorithm is to erase the PHI operands related to the edge
   * from source to target by sliding the other PHI operands down as required.
   *
   * @param source the source block to remove from PHIs in target
   * @param target the target block that may contain PHIs to update.
   */
  static void purgeBlockFromPHIs(BasicBlock source, BasicBlock target) {
    for (InstructionEnumeration e = target.forwardRealInstrEnumerator(); e.hasMoreElements();) {
      Instruction s = e.next();
      if (!(s instanceof Phi)) return; // all done (assume PHIs are first!)
      Phi phi = (Phi)s;
      int numPairs = phi.getNumberOf();
      int dst = 0;
      for (int src = 0; src < numPairs; src++) {
        BasicBlockOperand bbop = phi.getPred(src);
        if (bbop.block == source) {
          phi.setValue(src, null);
          phi.setPred(src, null);
        } else {
          if (src != dst) {
            phi.setValue(dst, phi.getClearValue(src));
            phi.setPred(dst, phi.getClearPred(src));
          }
          dst++;
        }
      }
      for (int i = dst; i < numPairs; i++) {
        phi.setValue(i, null);
        phi.setPred(i, null);
      }
    }
  }

  /**
   * Update PHI instructions in the target block so that any PHIs that
   * come from basic block B1, now come from basic block B2.
   *
   * @param target the target block that may contain PHIs to update.
   * @param B1 the block to replace in the phi instructions
   * @param B2 the replacement block for B1
   */
  static void replaceBlockInPhis(BasicBlock target, BasicBlock B1, BasicBlock B2) {
    for (InstructionEnumeration e = target.forwardRealInstrEnumerator(); e.hasMoreElements();) {
      Instruction s = e.next();
      if (!(s instanceof Phi)) return; // all done (assume PHIs are first!)
      Phi phi = (Phi)s;
      int numPairs = phi.getNumberOf();
      for (int src = 0; src < numPairs; src++) {
        BasicBlockOperand bbop = phi.getPred(src);
        if (bbop.block == B1) {
          phi.setPred(src, new BasicBlockOperand(B2));
        }
      }
    }
  }
}
