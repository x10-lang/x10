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

import x10me.opt.bc2ir.BCconstants;
import x10me.opt.controlflow.BasicBlock;
import x10me.opt.driver.OptOptions;
import x10me.opt.inlining.InlineSequence;
import x10me.opt.ir.operand.*;

/**
 * Instructions are the basic atomic unit of the IR.
 * An instruction contains an {@link Operator operator} and
 * (optionally) some {@link Operand operands}.
 * In addition, an instruction may (or may not) have
 * valid {@link #bcIndex} and{@link #position} fields that
 * together encode a description of the bytecode that it came from.
 * <p>
 * Although we use a single class, <code>Instruction</code>,
 * to implement all IR instructions, there are logically a number
 * of different kinds of instructions.
 * For example, binary operators, array loads, calls,
 * and null_checks all have different number of operands with differing
 * semantics.  To manage this in an abstract, somewhat object-oriented,
 * but still highly efficient fashion we have the notion of an
 * <em>Instruction Format</em>. An Instruction Format is a class
 * external to Instruction (defined in the instructionFormat package)
 * that provides static methods to create instructions and symbolically
 * access their operands.  Every instance of <code>Operator</code>
 * is assigned to exactly one Instruction Format.  Thus, the instruction's
 * operator implies which Instruction Format class can be used to
 * access the instruction's operands.
 * <p>
 * There are some common logical operands (eg Result, Location) that
 * appear in a large number of Instruction Formats.  In addition to the
 * basic Instruction Format classes, we provided additional classes
 * (eg ResultCarrier, LocationCarrier) that allow manipulation of all
 * instructions that contain a common operands.
 * <p>
 * A configuration (OptOptVIFcopyingGC) is defined in which all methods of
 * all Instruction Format classes verify that the operator of the instruction
 * being manipulated actually belongs to the appropriate Instruction Format.
 * This configuration is quite slow, but is an important sanity check to make
 * sure that Instruction Formats are being used in a consistent fashion.
 * <p>
 * The instruction's operator also has a number of traits.  Methods on
 * <code>Instruction</code> are provided to query these operator traits.
 * In general, clients should use the methods of Instruction to query
 * traits, since a particular instruction may override the operator-provided
 * default in some cases. For example, {@link #isMove()}, {@link #isBranch()},
 * {@link #isPEI()}, and {@link #isCall()} are some of the trait queries.
 * <p>
 * Unfortunately, the combination of operators, operator traits, and
 * Instruction Formats often leads to a tricky decision of which of three
 * roughly equivalent idioms one should use when writing code that
 * needs to manipulate instructions and their operands.
 * For example,
 * <pre>
 * if (Call.conforms(instr)) {
 *    return Call.getResult(instr);
 * }
 * </pre>
 * and
 * <pre>
 * if (instr.operator() == CALL) {
 *    return Call.getResult(instr);
 * }
 * </pre>
 * and
 * <pre>
 * if (instr.isCall()) {
 *    return ResultCarrier.getResult(instr);
 * }
 * </pre>
 * are more or less the same.
 * In some cases, picking an idiom is simply a matter of taste,
 * but in others making the wrong choice can lead to code that is less
 * robust or maintainable as operators and/or instruction formats are added
 * and removed from the IR. One should always think carefully about which
 * idiom is the most concise, maintainable, robust and efficient means of
 * accomplishing a given task.
 * Some general rules of thumb (or at least one person's opinion):
 * <ul>
 * <li> Tests against operator traits should be preferred
 *      to use of the conforms method of an Instruction Format class if
 *      both are possible.  This is definitely true if the code in question
 *      does not need to access specific operands of the instruction.
 *      Things are murkier if the code needs to manipulate specific
 *      (non-common) operands of the instruction.
 * <li> If you find yourself writing long if-then-else constructs using
 *      either Instruction Format conforms or operator traits then you ought to
 *      at least consider writing a switch statement on the opcode of the
 *      operator.  It should be more efficient and, depending on what your
 *      desired default behavior is, may be more robust/maintainable as well.
 * <li> Instruction Format classes are really intended to represent the
 *      "syntactic form" of an instruction, not the semantics of its operator.
 *      Using "conforms" when no specific operands are being manipulated
 *      is almost always not the right way to do things.
 * </ul>
 *
 * @see Operator
 * @see Operand
 * @see BasicBlock
 */
abstract public class Instruction {

  /**
   * BITFIELD used to encode {@link #operatorInfo}.
   * NB: OI_INVALID must be default value!
   */
  @SuppressWarnings("unused")
  // FIXME use it or lose it!
  private static final byte OI_INVALID = 0x00;
  /** BITFIELD used to encode {@link #operatorInfo}. */
  private static final byte OI_PEI_VALID = 0x01;
  /** BITFIELD used to encode {@link #operatorInfo}. */
  private static final byte OI_PEI = 0x02;
  /** BITFIELD used to encode {@link #operatorInfo}. */
  private static final byte OI_GC_VALID = 0x04;
  /** BITFIELD used to encode {@link #operatorInfo}. */
  private static final byte OI_GC = 0x08;
  /** BITFIELD used to encode {@link #operatorInfo}. */
  private static final byte MARK1 = 0x20;
  /** BITFIELD used to encode {@link #operatorInfo}. */
  private static final byte MARK2 = 0x40;

  /**
   * The int to hold the above information.
   */
  private int operatorInfo;

  
  /*
   * NOTE: There are currently two free bits: 0x10 and 0x80.
   */

  /**
   * The index of the bytecode that this instruction came from.
   * In combination with the {@link #position}, the bcIndex field
   * uniquely identifies the source postion of the bytecode that
   * this instruction came from.
   */
  public int bcIndex = BCconstants.UNKNOWN_BCI;

  /**
   * A description of the tree of inlined methods that contains the bytecode
   * that this instruction came from.
   * In combination with the {@link #bcIndex}, the position field
   * uniquely identifies the source postion of the bytecode that
   * this instruction came from.
   * A single postion operator can be shared by many instruction objects.
   *
   * @see InlineSequence
   * @see org.jikesrvm.compilers.opt.runtimesupport.OptEncodedCallSiteTree
   */
  public InlineSequence position;

  /**
   * A scratch word to be used as needed by analyses/optimizations to store
   * information during an optimization.
   * Cannot be used to comunicate information between compiler phases since
   * any phase is allowed to mutate it.
   * Cannot safely be assumed to have a particular value at the start of
   * a phase.
   * Typical uses:  scratch bits to encode true/false or numbering
   *                store an index into a lookaside array of other information.
   */
  public int scratch;

  /**
   * A scratch object to be used as needed by analyses/optimizations to store
   * information during an optimization.
   * Cannot be used to comunicate information between compiler phases since
   * any phase is allowed to mutate it.
   * Cannot safely be assumed to have a particular value at the start of
   * a phase.
   * To be used when more than one word of information is needed and
   * lookaside arrays are not desirable.
   * Typical uses:  attribute objects or links to shared data
   */
  public Object scratchObject;

  /**
   * The next instruction in the intra-basic-block list of instructions,
   * will be null if no such instruction exists.
   */
  private Instruction next;

  /**
   * The previous instruction in the intra-basic-block list of instructions,
   * will be null if no such instruction exists.
   */
  private Instruction prev;
  
  /**
   * INTERNAL IR USE ONLY: create a new instruction. with the specified number
   * of operands. 
   */
  Instruction() {
  }

  public String toString() {
    StringBuilder result = new StringBuilder("    ");
    if (isPEI()) {
      result.setCharAt(0, 'E');
    }
    if (isGCPoint()) {
      result.setCharAt(1, 'G');
    }

    if (isBbFirst()) {
      result.append("LABEL").append(getBasicBlock().getNumber());
      if (getBasicBlock().getInfrequent()) result.append(" (Infrequent)");
      return result.toString();
    }

    result.append(nameOf());
    Operand op;
    int N = getNumberOfOperands();
    int numDefs = getNumberOfDefs();

    // print explicit defs
    int defsPrinted = 0;
    for (int i = 0; i < numDefs; i++) {
      op = getOperand(i);
      if (op != null) {
        if (defsPrinted > 0) result.append(", ");
        if (defsPrinted % 10 == 9) result.append('\n');
        result.append(op);
        defsPrinted++;
      }
    }

    // print separator
    if (defsPrinted > 0) {
      result.append(" = ");
    }

    // print explicit uses
    int usesPrinted = 0;
    for (int i = numDefs; i < N; i++) {
      op = getOperand(i);
      if (usesPrinted > 0) result.append(", ");
      if ((defsPrinted + usesPrinted) % 10 == 9) result.append('\n');
      usesPrinted++;
      if (op != null) {
        result.append(op);
      } else {
        result.append("<unused>");
      }
    }

    return result.toString();
  }

  /**
   *  Return the name of the instruction.
   *  @return the name of the instruction
   */
  public abstract String nameOf();

  /**
   * Create a copy of this instruction.
   * The copy has the same operator and operands, but is not linked into
   * an instruction list.
   * Subtypes may override this set or clear other fields.
   *
   * @return the copy
   */
  public Instruction copyWithoutLinks() {
    Instruction copy = null;
    try {
      copy = (Instruction) this.clone ();
    } catch (CloneNotSupportedException v) {}
    copy.prev = null;
    copy.next = null;
    return copy;
  }

  /** 
   * Clear the instruction information from tmp.
   * @param tmp gets it instruction nulled
   */
  void clearOperand(Operand tmp) {
	tmp.instruction = null;
  }
 /**
   * Return the next instruction with respect to the current
   * code linearization order.
   *
   * @return the next insturction in the code order or
   *         <code>null</code> if no such instruction exists
   */
  public Instruction nextInstructionInCodeOrder() {
    return next;
  }

  /**
   * Return the previous instruction with respect to the current
   * code linearization order.
   *
   * @return the previous insturction in the code order or
   *         <code>null</code> if no such instruction exists
   */
  public Instruction prevInstructionInCodeOrder() {
    return prev;
  }

  /**
   * @return has this instruction been linked with a previous instruction? ie
   *         will calls to insertBefore succeed?
   */
  public final boolean hasPrev() {
    return prev != null;
  }

  /**
   * Get the basic block that contains this instruction.
   * Note: this instruction takes O(1) time for LABEL and BBEND
   * instructions, but will take O(# of instrs in the block)
   * for all other instructions. Therefore, although it can be used
   * on any instruction, care must be taken when using it to avoid
   * doing silly O(N^2) work for what could be done in O(N) work.
   */
  public BasicBlock getBasicBlock() {
    // Find basic block by going forwards to BBEND instruction
    Instruction instr = null; // Set = null to avoid compiler warning.
    for (instr = getNext(); !instr.isBbLast(); instr = instr.getNext()) ;
    return instr.getBasicBlock();

  }

  /**
   * Set the source position description ({@link #bcIndex},
   * {@link #position}) for this instruction to be the same as the
   * source instruction's source position description.
   *
   * @param source the instruction to copy the source position from
   */
  public final void copyPosition(Instruction source) {
    bcIndex = source.bcIndex;
    position = source.position;
  }

  /**
   * Set the source position description ({@link #bcIndex},
   * {@link #position}) for this instruction to be the same as the
   * source instruction's source position description if the position 
   * for this instruction were not set.
   *
   * @param source the instruction to copy the source position from
   */
  public final void conditionalCopyPosition(Instruction source) {
    if (this.position == null)
      this.copyPosition(source);
  }

  /**
   * Copy the position information from the source instruction to
   * the destination instruction, returning the source instruction.
   * To be used in passthrough expressions like
   * <pre>
   *    instr.insertBack(CPOS(instr, Load.create(...)));
   * </pre>
   *
   * @param src the instruction to copy position information from
   * @param dst the instruction to copy position information to
   * @return dest
   */
  public static Instruction CPOS(Instruction src, Instruction dst) {
    dst.copyPosition(src);
    return dst;
  }

  /**
   * Get the {@link #bcIndex bytecode index} of the instruction.
   *
   * @return the bytecode index of the instruction
   */
  public final int getBytecodeIndex() {
    return bcIndex;
  }

  /**
   * Set the {@link #bcIndex bytecode index} of the instruction.
   *
   * @param bci the new bytecode index
   */
  public final void setBytecodeIndex(int bci) {
    bcIndex = bci;
  }

  /**
   * Return the opcode of the instruction's operator
   * (a unique id suitable for use in switches); see
   * {@link Operators}.
   *
   * @return the operator's opcode
   */
  abstract public char getOpcode();
  
 /*
  * Functions dealing with the instruction's operands.  The top level
  * versions of these functions assume that the defs are ordered
  * before the uses.  In instructions were this may not be true, the
  * iterators and the instantiation of the iterators may need to be
  * changed.
  */

  /**
   * Get the number of operands in this instruction.
   *
   * @return number of operands
   */
  public abstract int getNumberOfOperands();
  
  /**
   * Returns the number of operands that are defs.
   *
   * @return number of operands that are defs
   */
  public abstract int getNumberOfDefs();

  /**
   * Returns the number of operands that are uses.
   *
   * @return how many operands are uses
   */
  public abstract int getNumberOfUses();

  
  /**
   * Enumerate all operands of an instruction.
   *
   * @return an enumeration of the instruction's operands.
   */
  public final OperandEnumeration getOperands() {
    return new OperandEnumeration (this, 0, this.getNumberOfOperands ());
  }

  /**
   * Enumerate all defs of an instruction.
   *
   * @return an enumeration of the instruction's defs.
   */
  public final OperandEnumeration getDefs() {
    return new OperandEnumeration (this, 0, this.getNumberOfDefs ());
  }
  
  /**
   * Enumerate all uses of an instruction (includes def/use).
   *
   * @return an enumeration of the instruction's uses.
   */
  public final OperandEnumeration getUses() {
    return new OperandEnumeration (this, this.getNumberOfDefs (), this.getNumberOfUses ());
  }

  /**
   * The only approved direct use of getOperand is in a loop over
   * some subset of an instructions operands (all of them, all uses, all defs).
   *
   * @param i which operand to return
   * @return the ith operand
   */
  abstract public Operand getOperand(int i);

  /**
   * The only approved direct use of getOperand is in a loop over
   * some subset of an instructions operands (all of them, all uses, all defs).
   *
   * @param i which operand to return
   * @return the ith operand detatching it from the instruction
   */
  public final Operand getClearOperand(int i) {
    Operand o = getOperand(i);
    if (o != null) {
      o.instruction = null;
      putOperand (i, null);
    }
    return o;
  }

  /**
   * The only approved direct use of getOperand is in a loop over
   * some subset of an instruction's operands (all of them, all uses, all defs).
   *
   * @param i which operand to set
   * @param op the operand to set it to
   */
  public abstract void putOperand(int i, Operand op);

  /**
   * Replace all occurances of the first operand with the second.
   *
   * @param oldOp   The operand to replace
   * @param newOp   The new one to replace it with
   */
  public final void replaceOperand(Operand oldOp, Operand newOp) {
    for (int i = 0; i < getNumberOfOperands(); i++) {
      if (getOperand(i) == oldOp) {
        putOperand(i, newOp);
      }
    }
  }

  /**
   * Replace any operands that are similar to the first operand
   * with a copy of the second operand.
   *
   * @param oldOp   The operand whose similar operands should be replaced
   * @param newOp   The new one to replace it with
   */
  public final void replaceSimilarOperands(Operand oldOp, Operand newOp) {
    for (int i = 0; i < getNumberOfOperands(); i++) {
      if (oldOp.similar(getOperand(i))) {
        putOperand(i, newOp.copy());
      }
    }
  }

  /**
   * Replace all occurances of register r with register n
   *
   * @param r the old register
   * @param n the new register
   */
  public void replaceRegister(Register r, Register n) {
    for (OperandEnumeration u = getUses(); u.hasMoreElements();) {
      Operand use = u.next();
      if (use.isRegister()) {
        if (use.asRegister().getRegister() == r) {
          use.asRegister().setRegister(n);
        }
      }
    }
    for (OperandEnumeration d = getDefs(); d.hasMoreElements();) {
      Operand def = d.next();
      if (def.isRegister()) {
        if (def.asRegister().getRegister() == r) {
          def.asRegister().setRegister(n);
        }
      }
    }
  }

  /*
   * Methods dealing with the instruction's operator.
   */

  /**
   * Is the instruction an "acquire" (have the memory model semantics of a lock acquire)
   * @return <code>true</code> if the instruction is an "acquire" (have the memory model semantics of a lock acquire)
   *          or <code>false</code> if it is not.
   */
  public boolean isAcquire() {
	return false;
  }
 
  /**
   * Is the instruction an actual memory allocation instruction
   * (NEW, NEWARRAY, etc)?
   *
   * @return <code>true</code> if the instruction is an allocation
   *         or <code>false</code> if it is not.
   */
  public boolean isAlloc() {
    return false;
  }

  /**
   * Is the instruction an intraprocedural branch?  A branch that is not a 
   * call or return but it may be a conditional branch.
   *
   * @return <code>true</code> if the instruction is am
   *         intraprocedural branch or <code>false</code> if it is not.
   */
  public boolean isBranch() {
    return false;
  }

  /**
   * Is the instruction a call (one kind of interprocedural branch)?
   *
   * @return <code>true</code> if the instruction is a call
   *         or <code>false</code> if it is not.
   */
  public boolean isCall() {
    return false;
  }

  /**
   * Does the instruction represent a commutative operation?
   *
   * @return <code>true</code> if the instruction is a commutative operation
   *         or <code>false</code> if it is not.
   */
  public boolean isCommutative() {
    return false;
  }

  /**
   * Is the instruction a compare (val,val) => condition?
   *
   * @return <code>true</code> if the instruction is a compare
   *         or <code>false</code> if it is not.
   */
  public boolean isCompare() {
    return false;
  }

  /**
   * Is the instruction a conditional intraprocedural branch? 
   *
   * @return <code>true</code> if the instruction is a conditional
   *         intraprocedural branch or <code>false</code> if it is not.
   */
  public boolean isConditionalBranch() {
    return false;
  }

  /**
   * Does the instruction represent a dynamic linking point?
   *
   * @return <code>true</code> if the instruction is a dynamic linking point
   *         or <code>false</code> if it is not.
   */
  public boolean isDynamicLinkPoint() {
    return false;
  }

  /**
   * Is the instruction an explicit load of a finite set of values from
   * a finite set of memory locations (load, load multiple, _not_ call)?
   *
   * @return <code>true</code> if the instruction is an explicit load
   *         or <code>false</code> if it is not.
   */
  public boolean isExplicitLoad() {
    return false;
  }

  /**
   * Is the instruction an explicit store of a finite set of values to
   * a finite set of memory locations (store, store multiple, _not_ call)?
   *
   * @return <code>true</code> if the instruction is an explicit store
   *         or <code>false</code> if it is not.
   */
  public boolean isExplicitStore() {
    return false;
  }

  /**
   * Is the instruction a potential GC point?
   *
   * @return <code>true</code> if the instruction is a potential
   *         GC point or <code>false</code> if it is not.
   */
  public boolean isGCPoint() {
    return false;
  }

  /**
   * Should the instruction be treated as a load from some unknown location(s)
   * for the purposes of scheduling and/or modeling the memory subsystem?
   *
   * @return <code>true</code> if the instruction is an implicit load
   *         or <code>false</code> if it is not.
   */
  public boolean isImplicitLoad() {
    return false;
  }

  /**
   * Should the instruction be treated as a store to some unknown location(s)
   * for the purposes of scheduling and/or modeling the memory subsystem?
   *
   * @return <code>true</code> if the instruction is an implicit store
   *         or <code>false</code> if it is not.
   */
  public boolean isImplicitStore() {
    return false;
  }

  /**
   * Does the instruction represent a getField or getStatic?
   *
   * @return <code>true</code> if the instruction is a getField or getStatic
   *         or <code>false</code> if it is not.
   */
  public boolean isGet() {
    return false;
  }

  /**
   * Does the instruction represent a simple move (the value is unchanged)
   * from one "register" location to another "register" location?
   *
   * @return <code>true</code> if the instruction is a simple move
   *         or <code>false</code> if it is not.
   */
  public boolean isMove() {
    return false;
  }

  /**
   * Is the instruction a call but not a pure call (one kind of interprocedural branch)?
   *
   * @return <code>true</code> if the instruction is a nonpure call
   *         or <code>false</code> if it is not.
   */
  public boolean isNonPureCall() {
    return false;
  }

  /**
   * Is the instruction a pure call (one kind of interprocedural branch)?
   *
   * @return <code>true</code> if the instruction is a pure call
   *         or <code>false</code> if it is not.
   */
  public boolean isPureCall() {
    return false;
  }

  /**
   * Does the instruction represent a putField or putStatic?
   *
   * @return <code>true</code> if the instruction is a getField or getStatic  or <code>false</code> if it is not.
   */
  public boolean isPut() {
    return false;
  }

  /**
   * Is the instruction a "release" (have the memory model semantics of a lock release)
   * @return <code>true</code> if the instruction is a "release" (have the memory model semantics of a lock release)
   *          or <code>false</code> if it is not.
   */
  public boolean isRelease() {
	return false;
  }

  /**
   * Is the instruction a return (interprocedural branch)?
   *
   * @return <code>true</code> if the instruction is a return
   *         or <code>false</code> if it is not.
   */
  public boolean isReturn() {
    return false;
  }

  /**
   * Is the instruction a throw of a Java exception?
   *
   * @return <code>true</code> if the instruction is a throw
   *         or <code>false</code> if it is not.
   */
  public boolean isThrow() {
    return false;
  }

  /**
   * Is this instruction a branch that has that has only two possible
   * successors?
   *
   * @return <code>true</code> if the instruction is an
   * interprocedural conditional branch with only two possible
   * outcomes (taken or not taken).
   */
  public boolean isTwoWayBranch() {
    return false;
  }

  /**
   * Is the instruction an unconditional intraprocedural branch?
   * We consider various forms of switches to be unconditional
   * intraprocedural branches, even though they are multi-way branches
   * and we may not no exactly which target will be taken.
   * This turns out to be the right thing to do, since some
   * arm of the switch will always be taken (unlike conditional branches).
   *
   * @return <code>true</code> if the instruction is an unconditional
   *         intraprocedural branch or <code>false</code> if it is not.
   */
  public boolean isUnconditionalBranch() {
    return false;
  }

  /**
   * Is the instruction a yield point?
   *
   * @return <code>true</code> if the instruction is a yield point
   *          or <code>false</code> if it is not.
   */
  public boolean isYieldPoint() {
    return false;
  }

  /**
   * Is the instruction a PEI (Potentially Excepting Instruction)?
   *
   * @return <code>true</code> if the instruction is a PEI
   *         or <code>false</code> if it is not.
   */
  public boolean isPEI() {
    return false;
  }

  /**
   * Has the instruction been explictly marked as a a PEI (Potentially Excepting Instruction)?
   *
   * @return <code>true</code> if the instruction is explicitly marked as a PEI
   *         or <code>false</code> if it is not.
   */
  public boolean isMarkedAsPEI() {
    return false;
  }
 
  /**
   * Record that this instruction is not a PEI.
   * Leave GCPoint status (if any) unchanged.
   */
  public final void markAsNonPEI() {
    operatorInfo &= ~OI_PEI;
    operatorInfo |= OI_PEI_VALID;
  }

  /**
   * NOTE: ONLY FOR USE ON MIR INSTRUCTIONS!!!!
   * Record that this instruction is a PEI.
   * Note that marking as a PEI implies marking as GCpoint.
   */
  public final void markAsPEI() {
    operatorInfo |= (OI_PEI_VALID | OI_PEI | OI_GC_VALID | OI_GC);
  }

  /**
   * NOTE: ONLY FOR USE ON MIR INSTRUCTIONS!!!!
   * Record that this instruction does not represent a potential GC point.
   * Leave exception state (if any) unchanged.
   */
  public final void markAsNonGCPoint() {
    operatorInfo &= ~OI_GC;
    operatorInfo |= OI_GC_VALID;
  }

  /**
   * NOTE: ONLY FOR USE ON MIR INSTRUCTIONS!!!!
   * Record that this instruction is a potential GC point.
   * Leave PEI status (if any) unchanged.
   */
  public final void markAsGCPoint() {
    operatorInfo |= (OI_GC_VALID | OI_GC);
  }

  /**
   * NOTE: ONLY FOR USE ON MIR INSTRUCTIONS!!!!
   * Mark this instruction as being neither an exception or GC point.
   */
  public final void markAsNonPEINonGCPoint() {
    operatorInfo &= ~(OI_PEI | OI_GC);
    operatorInfo |= (OI_PEI_VALID | OI_GC_VALID);
  }

  /**
   * Is the first mark bit of the instruction set?
   *
   * @return <code>true</code> if the first mark bit is set
   *         or <code>false</code> if it is not.
   */
  final boolean isMarked1() {
    return (operatorInfo & MARK1) != 0;
  }

  /**
   * Is the second mark bit of the instruction set?
   *
   * @return <code>true</code> if the first mark bit is set
   *         or <code>false</code> if it is not.
   */
  final boolean isMarked2() {
    return (operatorInfo & MARK2) != 0;
  }

  /**
   * Set the first mark bit of the instruction.
   */
  final void setMark1() {
    operatorInfo |= MARK1;
  }

  /**
   * Set the second mark bit of the instruction.
   */
  final void setMark2() {
    operatorInfo |= MARK2;
  }

  /**
   * Clear the first mark bit of the instruction.
   */
  final void clearMark1() {
    operatorInfo &= ~MARK1;
  }

  /**
   * Clear the second mark bit of the instruction.
   */
  final void clearMark2() {
    operatorInfo &= ~MARK2;
  }
 
  /**
   * Return true if this instruction is the first instruction in a
   * basic block.  By convention (construction) every basic block starts
   * with a label instruction and a label instruction only appears at
   * the start of a basic block
   *
   * @return <code>true</code> if the instruction is the first instruction
   *         in its basic block or <code>false</code> if it is not.
   */
  public final boolean isBbFirst() {
    return this.prev == null;
  }

  /**
   * Return true if this instruction is the last instruction in a
   * basic block.  By convention (construction) every basic block ends
   * with a BBEND instruction and a BBEND instruction only appears at the
   * end of a basic block
   *
   * @return <code>true</code> if the instruction is the last instruction
   *         in its basic block or <code>false</code> if it is not.
   */
  public final boolean isBbLast() {
    return this.next == null;
  }

  /**
   * Mainly intended for assertion checking;  returns true if the instruction
   * is expected to appear on the "inside" of a basic block, false otherwise.
   *
   * @return <code>true</code> if the instruction is expected to appear
   *         on the inside (not first or last) of its basic block
   *         or <code>false</code> if it is expected to be a first/last
   *         instruction.
   */
  public final boolean isBbInside() {
    return (this.prev != null) && (this.next != null);
  }

  /**
   * Primitive Instruction List manipulation routines.
   * All of these operations assume that the IR invariants
   * (mostly well-formedness of the data structures) are true
   * when they are invoked.
   * Effectively, the IR invariants are defined by IR.verify().
   * These primitive functions will locally check their invariants
   * when IR.PARANOID is true.
   * If the precondition is met, then the IR invariants will be true when
   * the operation completes.
   */
  
  /**
   * Insertion: Insert newInstr immediately after this in the
   * instruction stream.
   * Can't insert after a BBEND instruction, since it must be the last
   * instruction in its basic block.
   *
   * @param newInstr the instruction to insert, must not be in an
   *                 instruction list already.
   */
  public final void insertAfter(Instruction newInstr) {
    if (OptOptions.verifyIR > OptOptions.NOVERIFY) {
      isForwardLinked();
      newInstr.isNotLinked();
      assert !isBbLast(): "cannot insert after last instruction of block";
    }

    // set position unless someone else has
    newInstr.conditionalCopyPosition(this);

    // Splice newInstr into the doubly linked list of instructions
    Instruction old_next = next;
    next = newInstr;
    newInstr.prev = this;
    newInstr.next = old_next;
    old_next.prev = newInstr;
  }

  /**
   * Insertion: Insert newInstr immediately before this in the
   * instruction stream.
   * Can't insert before a LABEL instruction, since it must be the last
   * instruction in its basic block.
   *
   * @param newInstr the instruction to insert, must not be in
   *                 an instruction list already.
   */
  public final void insertBefore(Instruction newInstr) {
    if (OptOptions.verifyIR > OptOptions.NOVERIFY) {
      isBackwardLinked();
      newInstr.isNotLinked();
      assert !isBbFirst(): "Cannot insert before first instruction of block";
    }

    // set position unless someone else has
    newInstr.conditionalCopyPosition(this);

    // Splice newInstr into the doubly linked list of instructions
    Instruction old_prev = prev;
    prev = newInstr;
    newInstr.next = this;
    newInstr.prev = old_prev;
    old_prev.next = newInstr;
  }

  /**
   * Replacement: Replace this with newInstr.
   * We could allow replacement of first & last instrs in the basic block,
   * but it would be a fair amount of work to update everything, and probably
   * isn't useful, so we'll simply disallow it for now.
   *
   * @param newInstr  the replacement instruction must not be in an
   *                  instruction list already and must not be a
   *                  LABEL or BBEND instruction.
   */
  public final Instruction replace(Instruction newInstr) {
    if (OptOptions.verifyIR > OptOptions.NOVERIFY) {
      isLinked();
      newInstr.isNotLinked();
      assert isBbInside(): "Can only replace BbInside instructions";
    }

    Instruction old_prev = prev;
    Instruction old_next = next;

    // Splice newInstr into the doubly linked list of instructions
    newInstr.prev = old_prev;
    old_prev.next = newInstr;
    newInstr.next = old_next;
    old_next.prev = newInstr;
    next = null;
    prev = null;

    // Use the existing instruction's position if the new instruction 
    // does not have one.
    newInstr.conditionalCopyPosition(this);

    return newInstr;
  }

  /**
   * Removal: Remove this from the instruction stream.
   *
   *  We currently forbid the removal of LABEL instructions to avoid
   *  problems updating branch instructions that reference the label.
   *  We also outlaw removal of BBEND instructions.
   *  <p>
   *  NOTE: We allow the removal of branch instructions, but don't update the
   *  CFG data structure.....right now we just assume the caller knows what
   *  they are doing and takes care of it.
   *  <p>
   *  NB: execution of this method nulls out the prev & next fields of this
   *
   * @return the previous instruction in the instruction stream
   */
  public final Instruction remove() {
    if (OptOptions.verifyIR > OptOptions.NOVERIFY) {
      isLinked();
      assert !isBbFirst() && !isBbLast(): "Removal of first/last instructions in block not supported";
    }

    // Splice this out of instr list
    Instruction Prev = prev, Next = next;
    Prev.next = Next;
    Next.prev = Prev;
    next = null;
    prev = null;
    return Prev;
  }

  /*
   * Helper routines to verify instruction list invariants.
   * Invocations to these functions are guarded by IR.PARANOID and thus
   * the calls to VM.Assert don't need to be guarded by VM.VerifyAssertions.
   */
  private void isLinked() {
    assert prev.next == this: "is_linked: failure (1)";
    assert next.prev == this: "is_linked: failure (2)";
  }

  private void isBackwardLinked() {
    assert prev.next == this: "is_backward_linked: failure (1)";
    // OK if next is null (IR under construction)
    assert next == null || next.prev == this: "is_backward_linked: failure (2)";
  }

  private void isForwardLinked() {
    // OK if prev is null (IR under construction)
    assert prev == null || prev.next == this: "is_forward_linked: failure (1)";
    assert next.prev == this: "is_forward_linked (2)";
  }

  private void isNotLinked() {
    assert prev == null && next == null: "is_not_linked: failure (1)";
  }

  /**
   * For IR internal use only; general clients should use
   * {@link #nextInstructionInCodeOrder()}.
   *
   * @return the contents of {@link #next}
   */
  public final Instruction getNext() {
    return next;
  }

  /**
   * For IR internal use only;   general clients should always use higer level
   * mutation functions.
   * Set the {@link #next} field of the instruction.
   *
   * @param n the new value for next
   */
  public final void setNext(Instruction n) {
    next = n;
  }

  /**
   * For IR internal use only; General clients should use
   * {@link #prevInstructionInCodeOrder()}.
   *
   * @return the contents of {@link #prev}
   */
  public final Instruction getPrev() {
    return prev;
  }

  /**
   * For IR internal use only;   general clients should always use higer level
   * mutation functions.
   * Set the {@link #prev} field of the instruction.
   *
   * @param p the new value for prev
   */
  public final void setPrev(Instruction p) {
    prev = p;
  }

  /**
   * For IR internal use only;   general clients should always use higer level
   * mutation functions.
   * Clear the {@link #prev} and {@link #next} fields of the instruction.
   */
  final void clearLinks() {
    next = null;
    prev = null;
  }

  /**
   * Are two instructions similar, i.e. having the same operator and
   * the same number of similar operands?
   * @param similarInstr instruction to compare against
   * @return true if they are similar
   */
  public boolean similar(Instruction similarInstr) {
    if (similarInstr.getOpcode() != getOpcode()) {
      return false;
    } else {
      int num_operands = getNumberOfOperands();
      if (similarInstr.getNumberOfOperands() != num_operands) {
        return false;
      } else {
        for (int i = 0; i < num_operands; i++) {
          Operand op1 = getOperand(i);
          Operand op2 = similarInstr.getOperand(i);
          if ((op1 == null) && (op2 == null)) {
            return true;
          }
          if ((op1 == null) || (op2 == null) || !op1.similar(op2)) {
            return false;
          }
        }
        return true;
      }
    }
  }

  /**
   * For IR internal use only;   general clients should always use higer level
   * mutation functions.
   * Link this and other together by setting this's {@link #next} field to
   * point to other and other's {@link #prev} field to point to this.
   *
   * @param other the instruction to link with.
   */
  public final void linkWithNext(Instruction other) {
    next = other;
    other.prev = this;
  }

  /**
   * Might this instruction be a load from a field that is declared
   * to be volatile?
   *
   * @return <code>true</code> if the instruction might be a load
   *         from a volatile field or <code>false</code> if it
   *         cannot be a load from a volatile field
   */
  public boolean mayBeVolatileFieldLoad() {
    if (this instanceof GetFieldParent || this instanceof GetStaticParent) {
      return ((HasField)this).getField().mayBeVolatile();
    }
    return false;
  }
}
