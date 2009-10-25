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

import java.util.Iterator;


import x10me.opt.controlflow.BasicBlock;
import x10me.opt.controlflow.BasicBlockEnumeration;
import x10me.opt.ir.operand.HeapOperand;
import x10me.opt.ir.operand.Operand;
import x10me.opt.ir.operand.RegisterOperand;
import x10me.types.Type;

/**
 * This class is not meant to be instantiated.
 * It simply serves as a place to collect the implementation of
 * primitive IR enumerations.
 * None of these functions are meant to be called directly from
 * anywhere except IR, Instruction, and BasicBlock.
 * General clients should use the higher level interfaces provided
 * by those classes
 */
public abstract class IREnumeration {

  /**
   * Forward intra basic block instruction enumerations from
   * from start...last inclusive.
   *
   * NB: start and last _must_ be in the same basic block
   *     and must be in the proper relative order.
   *     This code does _not_ check this invariant, and will
   *     simply fail by eventually thowing a NoSuchElementException
   *     if it is not met. Caller's must be sure the invariants are met.
   *
   * @param start the instruction to start with
   * @param end   the instruction to end with
   * @return an enumeration of the instructions from start to end
   */
  public static InstructionEnumeration forwardIntraBlockIE(final Instruction start, final Instruction end) {
    return new InstructionEnumeration() {
      private Instruction current = start;
      private final Instruction last = end;

      public boolean hasMoreElements() { return current != null; }

      public Instruction nextElement() { return next(); }

      public Instruction next() {
        Instruction res = current;
        if (current == last) {
          current = null;
        } else {
          try {
            current = current.getNext();
          } catch (NullPointerException e) {
            fail("forwardIntraBlockIE");
          }
        }
        return res;
      }
    };
  }

  /**
   * Reverse intra basic block instruction enumerations from
   * from start...last inclusive.
   *
   * NB: start and last _must_ be in the same basic block
   *     and must be in the proper relative order.
   *     This code does _not_ check this invariant, and will
   *     simply fail by eventually thowing a NoSuchElementException
   *     if it is not met. Caller's must be sure the invariants are met.
   *
   * @param start the instruction to start with
   * @param end   the instruction to end with
   * @return an enumeration of the instructions from start to end
   */
  public static InstructionEnumeration reverseIntraBlockIE(final Instruction start, final Instruction end) {
    return new InstructionEnumeration() {
      private Instruction current = start;
      private final Instruction last = end;

      public boolean hasMoreElements() { return current != null; }

      public Instruction nextElement() { return next(); }

      public Instruction next() {
        Instruction res = current;
        if (current == last) {
          current = null;
        } else {
          try {
            current = current.getPrev();
          } catch (NullPointerException e) {
            fail("reverseIntraBlockIE");
          }
        }
        return res;
      }
    };
  }

  /**
   * A forward enumeration of all the instructions in the IR.
   *
   * @param ir the IR to walk over
   * @return a forward enumeration of the insturctions in ir
   */
  public static InstructionEnumeration forwardGlobalIE(final IR ir) {
    return new InstructionEnumeration() {
      private Instruction current = ir.firstInstructionInCodeOrder();

      public boolean hasMoreElements() { return current != null; }

      public Instruction nextElement() { return next(); }

      public Instruction next() {
        try {
          Instruction res = current;
          current = current.nextInstructionInCodeOrder();
          return res;
        } catch (NullPointerException e) {
          fail("forwardGlobalIR");
          return null; // placate jikes
        }
      }
    };
  }

  /**
   * A reverse enumeration of all the instructions in the IR.
   *
   * @param ir the IR to walk over
   * @return a forward enumeration of the insturctions in ir
   */
  public static InstructionEnumeration reverseGlobalIE(final IR ir) {
    return new InstructionEnumeration() {
      private Instruction current = ir.lastInstructionInCodeOrder();

      public boolean hasMoreElements() { return current != null; }

      public Instruction nextElement() { return next(); }

      public Instruction next() {
        try {
          Instruction res = current;
          current = current.prevInstructionInCodeOrder();
          return res;
        } catch (NullPointerException e) {
          fail("forwardGlobalIR");
          return null; // placate jikes
        }
      }
    };
  }

  /**
   * A forward enumeration of all the basic blocks in the IR.
   *
   * @param ir the IR to walk over
   * @return a forward enumeration of the basic blocks in ir
   */
  public static BasicBlockEnumeration forwardBE(final IR ir) {
    return new BasicBlockEnumeration() {
      private BasicBlock current = ir.firstBasicBlockInCodeOrder();

      public boolean hasMoreElements() { return current != null; }

      public BasicBlock nextElement() { return next(); }

      public BasicBlock next() {
        try {
          BasicBlock res = current;
          current = current.nextBasicBlockInCodeOrder();
          return res;
        } catch (NullPointerException e) {
          fail("forwardBE");
          return null; // placate jikes
        }
      }
    };
  }

  /**
   * A reverse enumeration of all the basic blocks in the IR.
   *
   * @param ir the IR to walk over
   * @return a reverse enumeration of the basic blocks in ir
   */
  public static BasicBlockEnumeration reverseBE(final IR ir) {
    return new BasicBlockEnumeration() {
      private BasicBlock current = ir.lastBasicBlockInCodeOrder();

      public boolean hasMoreElements() { return current != null; }

      public BasicBlock nextElement() { return next(); }

      public BasicBlock next() {
        try {
          BasicBlock res = current;
          current = current.prevBasicBlockInCodeOrder();
          return res;
        } catch (NullPointerException e) {
          fail("forwardBE");
          return null; // placate jikes
        }
      }
    };
  }
 
  /**
   * This class implements an {@link InstructionEnumeration} over
   * all instructions for a basic block. This enumeration includes
   * explicit instructions in the IR and implicit phi instructions for
   * heap variables, which are stored only in this lookaside
   * structure.
   * @see x10me.opt.ssa.SSADictionary
   */
  public static final class AllInstructionsEnum implements InstructionEnumeration {
    /**
     * An enumeration of the explicit instructions in the IR for a
     * basic block
     */
    private final InstructionEnumeration explicitInstructions;

    /**
     * The label instruction for the basic block - the label is
     * special as we want it to appear in the enumeration before the
     * implicit SSA instructions
     */
    private Instruction labelInstruction;

    /**
     * Construct an enumeration for all instructions, both implicit and
     * explicit in the IR, for a given basic block
     *
     * @param block the basic block whose instructions this enumerates
     */
    public AllInstructionsEnum(IR ir, BasicBlock block) {
      explicitInstructions = block.forwardInstrEnumerator();
      labelInstruction = explicitInstructions.next();
    }

    /**
     * Are there more elements in the enumeration?
     *
     * @return true or false
     */
    public boolean hasMoreElements() {
      return explicitInstructions.hasMoreElements();
    }

    /**
     * Get the next instruction in the enumeration
     *
     * @return the next instruction
     */
    public Instruction next() {
      if (labelInstruction != null) {
        Instruction temp = labelInstruction;
        labelInstruction = null;
        return temp;
      } else {
        return explicitInstructions.next();
      }
    }

    /**
     * Get the next instruction in the enumeration
     *
     * @return the next instruction
     */
    public Instruction nextElement() {
      return next();
    }
  }

  private static void fail(String msg) throws java.util.NoSuchElementException {
    throw new java.util.NoSuchElementException(msg);
  }
}
