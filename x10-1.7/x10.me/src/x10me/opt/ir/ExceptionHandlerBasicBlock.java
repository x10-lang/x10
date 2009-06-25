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

package x10me.opt.ir;

import static x10me.opt.driver.OptConstants.MAYBE;
import static x10me.opt.driver.OptConstants.NO;
import static x10me.opt.driver.OptConstants.YES;

import java.util.Enumeration;


import x10me.opt.controlflow.BasicBlock;
import x10me.opt.controlflow.BasicBlockEnumeration;
import x10me.opt.controlflow.ControlFlowGraph;
import x10me.opt.inlining.InlineSequence;
import x10me.opt.ir.operand.TypeOperand;
import x10me.types.Type;

/**
 * A basic block that marks the start of an exception handler.
 * Exception Handler Basic Block; acronym EHBB.
 */
public final class ExceptionHandlerBasicBlock extends BasicBlock {

  /**
   * The RVMType(s) of the exception(s) caught by this block.
   */
  private TypeOperand[] exceptionTypes;

   /**
   * Creates a new exception handler basic block at the specified location,
   * which catches the specified type of exception.
   *
   * @param loc   Bytecode index to create basic block at
   * @param position  The inline context for this basic block
   * @param type  The exception type
   * @param cfg   The ControlFlowGraph that will contain the basic block
   */
  public ExceptionHandlerBasicBlock(int loc, InlineSequence position, TypeOperand type, ControlFlowGraph cfg) {
    super(loc, position, cfg);
    exceptionTypes = new TypeOperand[1];
    exceptionTypes[0] = type;
    setExceptionHandlerBasicBlock();
  }

  /**
   * Add a new exception type to an extant exception handler block.
   * Do filtering of duplicates internally for efficiency.
   * NOTE: this routine is only intended to be called by
   * {@link org.jikesrvm.compilers.opt.bc2ir.BC2IR}.
   *
   * @param et the exception type to be added
   */
  public void addCaughtException(TypeOperand et) {
    for (TypeOperand exceptionType : exceptionTypes) {
      if (exceptionType.similar(et)) return;
    }
    TypeOperand[] newets = new TypeOperand[exceptionTypes.length + 1];
    for (int i = 0; i < exceptionTypes.length; i++) {
      newets[i] = exceptionTypes[i];
    }
    newets[exceptionTypes.length] = et;
    exceptionTypes = newets;
  }

  /**
   * Return YES/NO/MAYBE values that answer the question is it possible for
   * this handler block to catch an exception of the type et.
   *
   * @param cand the TypeReference of the exception in question.
   * @return YES, NO, MAYBE
   */
  public byte mayCatchException(Type cand) {
    for (TypeOperand exceptionType : exceptionTypes) {
      if (Type.includesType(exceptionType.getType(), cand))
	return YES;
      if (Type.includesType(cand, exceptionType.getType()))
        return YES;
    }
    return NO;
  }

  /**
   * Return YES/NO/MAYBE values that answer the question is it guarenteed that
   * this handler block will catch an exception of type <code>cand</code>
   *
   * @param cand  the TypeReference of the exception in question.
   * @return YES, NO, MAYBE
   */
  public byte mustCatchException(Type cand) {
    for (TypeOperand exceptionType : exceptionTypes) {
      if (Type.includesType(exceptionType.getType(), cand))
	return YES;
    }
    return NO;
  }

  /**
   * Return an Enumeration of the caught exception types.
   * Mainly intended for creation of exception tables during
   * final assembly. Most other clients shouldn't care about this
   * level of detail.
   */
  public Enumeration<TypeOperand> getExceptionTypes() {
    return new Enumeration<TypeOperand>() {
      private int idx = 0;

      public boolean hasMoreElements() {
        return idx != exceptionTypes.length;
      }

      public TypeOperand nextElement() {
        try {
          return exceptionTypes[idx++];
        } catch (ArrayIndexOutOfBoundsException e) {
          throw new java.util.NoSuchElementException("ExceptionHandlerBasicBlock.getExceptionTypes");
        }
      }
    };
  }

  /**
   * Get how many table entires this EHBB needs.
   * Really only of interest during final assembly.
   *
   * @see org.jikesrvm.compilers.opt.runtimesupport.OptExceptionTable
   *
   * @return the number of table entries for this basic block
   */
  public int getNumberOfExceptionTableEntries() {
    return exceptionTypes.length;
  }

  /**
   * Return a string representation of the basic block
   * (augment {@link BasicBlock#toString} with
   * the exceptions caught by this handler block).
   *
   * @return a string representation of the block
   */
  public String toString() {
    String exmsg = " (catches ";
    for (int i = 0; i < exceptionTypes.length - 1; i++) {
      exmsg = exmsg + exceptionTypes[i].toString() + ", ";
    }
    exmsg = exmsg + exceptionTypes[exceptionTypes.length - 1].toString();
    exmsg = exmsg + " for";
    BasicBlockEnumeration in = getIn();
    while (in.hasMoreElements()) {
      exmsg = exmsg + " " + in.next().toString();
    }
    exmsg = exmsg + ")";

    return super.toString() + exmsg;
  }
}
