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


import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Stack;



import x10me.opt.controlflow.BasicBlock;
import x10me.opt.controlflow.BasicBlockEnumeration;
import x10me.opt.controlflow.ControlFlowGraph;
import x10me.opt.controlflow.DominatorTree;
import x10me.opt.controlflow.LSTGraph;
import x10me.opt.driver.CompilerPhase;
import x10me.opt.driver.DumpFile;
import x10me.opt.driver.OptOptions;
import x10me.opt.inlining.InlineOracle;
import x10me.opt.inlining.InlineSequence;
import x10me.opt.ir.operand.HeapOperand;
import x10me.opt.ir.operand.Operand;
import x10me.opt.ir.operand.OperandEnumeration;
import x10me.opt.ssa.HeapVariable;
import x10me.opt.ssa.SSADictionary;
import x10me.types.Method;
import x10me.types.Type;
import x10me.util.BitVector;

/**
 * An <code>IR</code> object (IR is short for Intermediate Representation)
 * contains all the per-compilation information associated with
 * a method that is being compiled.
 * <p>
 * <code>IR</code> objects are intended to be transitory.
 * They are created to compile a particular method under a
 * given {@link CompilationPlan compilation plan}
 * and are discarded once the compilation plan has been completed.
 * <p>
 * The primary component of the IR is the
 * {@link ControlFlowGraph <em>FCFG</em>} (factored control flow graph)
 * The FCFG contains
 * {@link Instruction intermediate language instructions}
 * grouped into {@link BasicBlock factored basic blocks}.
 * In addition to the FCFG, an <code>IR</code> object also
 * contains a variety of other supporting and derived data structures.
 * <p>
 *
 * @see ControlFlowGraph
 * @see BasicBlock
 * @see Instruction
 * @see Operator
 * @see Operand
 */
public final class IR {

  /**
   * The {@link NormalMethod} object corresponding to the
   * method being compiled. Other methods may have been inlined into
   * the IR during compilation, so method really only represents the
   * primary or outermost method being compiled.
   */
  public final Method method;

  /**
   * The specialized parameters to be used in place of those defined
   * in the NormalMethod.
   */
  public final Type[] params;

  /**
   * @return The {@link NormalMethod} object corresponding to the
   * method being compiled. Other methods may have been inlined into
   * the IR during compilation, so method really only represents the
   * primary or outermost method being compiled.
   */
  public Method getMethod() {
    return method;
  }

  public DumpFile dumpFile;
  
  /**
   * The compiler {@link OptOptions options} that apply
   * to the current compilation.
   */
  public final OptOptions options;

  /**
   * The root {@link GenerationContext generation context}
   * for the current compilation.
   */
  public InlineSequence inlineSequence;

  /**
   * The {@link InlineOracle inlining oracle} to be used for the
   * current compilation.
   * TODO: It would make more sense to have the inlining oracle be
   * a component of the generation context, but as things currently
   * stand the IR is created before the generation context.  We might be
   * able to restructure things such that the generation context is
   * created in the IR constructor and then eliminate this field,
   * replacing all uses with gc.inlinePlan instead.
   */
  public final InlineOracle inlinePlan;

  /**
   * The {@link ControlFlowGraph FCFG} (Factored Control Flow Graph)
   */
  public ControlFlowGraph cfg;

  /**
   *  The dominatorTree if it exists;
   */
  public DominatorTree dominatorTree; 

  /**
   *
   */
  public DominatorTree postDominatorTree;
  
  /**
   * The {@link RegisterPool Register pool}
   */
  public RegisterPool regpool;

  /**
   * Backing store for {@link #getBasicBlock(int)}.
   */
  private BasicBlock[] basicBlockMap;

  /**
   * <code>true</code> if exception handlers exist 
   * and there is code that can reach them. 
   */
  private boolean hasReachableExceptionHandlers;

  public LSTGraph loopStructureTree;

  public SSADictionary dictionary;
  
   /**
   * @param m    The method to compile
   * @param ip   The inlining oracle to use for the compilation
   * @param opts The options to use for the compilation
   */
  public IR(Method m, InlineOracle ip, OptOptions opts) {
    method = m;
    params = null;
    options = opts;
    inlinePlan = ip;
   }

  /**
   * Print the instructions in this IR to System.out.
   */
  public void printInstructions() {
    for (InstructionEnumeration e = forwardInstrEnumerator(); e.hasMoreElements();) {
      Instruction i = e.next();
      System.out.print(i.bcIndex + "\t" + i);

      // Print block frequency with the label instruction
      if (i instanceof Label) {
        BasicBlock bb = i.getBasicBlock();
        System.out.print("   Frequency:  " + bb.getExecutionFrequency());
      }

      System.out.println();
    }
  }

  /**
   * Return the first instruction with respect to
   * the current code linearization order.
   *
   * @return the first instruction in the code order
   */
  public Instruction firstInstructionInCodeOrder() {
    return firstBasicBlockInCodeOrder().firstInstruction();
  }

  /**
   * Return the last instruction with respect to
   * the current code linearization order.
   *
   * @return the last instruction in the code order
   */
  public Instruction lastInstructionInCodeOrder() {
    return lastBasicBlockInCodeOrder().lastInstruction();
  }

  /**
   * Return the first basic block with respect to
   * the current code linearization order.
   *
   * @return the first basic block in the code order
   */
  public BasicBlock firstBasicBlockInCodeOrder() {
    return cfg.firstInCodeOrder();
  }

  /**
   * Return the last basic block with respect to
   * the current code linearization order.
   *
   * @return the last basic block in the code order
   */
  public BasicBlock lastBasicBlockInCodeOrder() {
    return cfg.lastInCodeOrder();
  }

  /**
   * Forward (with respect to the current code linearization order)
   * iteration over all the instructions in this IR.
   * The IR must <em>not</em> be modified during the iteration.
   *
   * @return an InstructionEnumeration that enumerates the
   *         instructions in forward code order.
   */
  public InstructionEnumeration forwardInstrEnumerator() {
    return IREnumeration.forwardGlobalIE(this);
  }

  /**
   * Reverse (with respect to the current code linearization order)
   * iteration over all the instructions in this IR.
   * The IR must <em>not</em> be modified during the iteration.
   *
   * @return an InstructionEnumeration that enumerates the
   *         instructions in reverse code order.
   */
  public InstructionEnumeration reverseInstrEnumerator() {
    return IREnumeration.reverseGlobalIE(this);
  }

  /**
   * Enumerate the basic blocks in the IR in an arbitrary order.
   *
   * @return an BasicBlockEnumeration that enumerates the
   *         basic blocks in an arbitrary order.
   */
  public BasicBlockEnumeration getBasicBlocks() {
    return IREnumeration.forwardBE(this);
  }

  /**
   * Forward (with respect to the current code linearization order)
   * iteration overal all the basic blocks in the IR.
   *
   * @return an BasicBlockEnumeration that enumerates the
   *         basic blocks in forward code order.
   */
  public BasicBlockEnumeration forwardBlockEnumerator() {
    return IREnumeration.forwardBE(this);
  }

  /**
   * Reverse (with respect to the current code linearization order)
   * iteration overal all the basic blocks in the IR.
   *
   * @return an BasicBlockEnumeration that enumerates the
   *         basic blocks in reverse code order.
   */
  public BasicBlockEnumeration reverseBlockEnumerator() {
    return IREnumeration.reverseBE(this);
  }

  /**
   * Return an enumeration of the parameters to the IR
   * Warning: Only valid before register allocation (see CallingConvention)
   *
   * @return the parameters of the IR.
   */
  public OperandEnumeration getParameters() {
    for (Instruction s = firstInstructionInCodeOrder(); true; s = s.nextInstructionInCodeOrder()) {
      if (s instanceof IrPrologue) {
        return s.getDefs();
      }
    }
  }

  /**
   * Is the operand a parameter of the IR?
   * Warning: Only valid before register allocation (see CallingConvention)
   *
   * @param op the operand to check
   * @return true if the op is a parameter to the IR, false otherwise
   */
  public boolean isParameter(Operand op) {
    for (OperandEnumeration e = getParameters(); e.hasMoreElements();) {
      if (e.next().similar(op)) return true;
    }
    return false;
  }

  /**
   * How many bytes of parameters does this method take?
   */
  public int incomingParameterBytes() {
    int nWords = method.getParameterWords();
    // getParameterWords() does not include the implicit 'this' parameter.
    if (!method.isStatic()) nWords++;
    return nWords << 2;
  }

  /**
   * Recompute the basic block map, so can use getBasicBlock(int)
   * to index into the basic blocks quickly.
   * TODO: think about possibly keeping the basic block map up-to-date
   *       automatically (Use a hashtable, perhaps?).
   */
  public void resetBasicBlockMap() {
    basicBlockMap = new BasicBlock[getMaxBasicBlockNumber() + 1];
    for (Enumeration<BasicBlock> bbEnum = cfg.basicBlocks(); bbEnum.hasMoreElements();) {
      BasicBlock block = bbEnum.nextElement();
      basicBlockMap[block.getNumber()] = block;
    }
  }

  /**
   * Get the basic block with a given number.
   * PRECONDITION: {@link #resetBasicBlockMap} has been called
   * before calling this function, but after making any changes to
   * the set of basic blocks in the IR.
   *
   * @param number the number of the basic block to retrieve
   * @return that requested block
   */
  public BasicBlock getBasicBlock(int number) {
    assert basicBlockMap != null;
    return basicBlockMap[number];
  }

  /**
   * Get an enumeration of all the basic blocks whose numbers
   * appear in the given BitSet.
   * PRECONDITION: {@link #resetBasicBlockMap} has been called
   * before calling this function, but after making any changes to
   * the set of basic blocks in the IR.
   *
   * @param bits The BitSet that defines which basic blocks to
   *             enumerate.
   * @return an enumeration of said blocks.
   */
  public BasicBlockEnumeration getBasicBlocks(BitVector bits) {
    return new BitSetBBEnum(this, bits);
  }

  // TODO: It would be easy to avoid creating the Stack if we switch to
  //       the "advance" pattern used in BasicBlock.BBEnum.
  // TODO: Make this an anonymous local class.
  private static final class BitSetBBEnum implements BasicBlockEnumeration {
    private final Stack<BasicBlock> stack;

    BitSetBBEnum(IR ir, BitVector bits) {
      stack = new Stack<BasicBlock>();
      int size = bits.length();
      Enumeration<BasicBlock> bbEnum = ir.getBasicBlocks();
      while (bbEnum.hasMoreElements()) {
        BasicBlock block = bbEnum.nextElement();
        int number = block.getNumber();
        if (number < size && bits.get(number)) stack.push(block);
      }
    }

    public boolean hasMoreElements() { return !stack.empty(); }

    public BasicBlock nextElement() { return stack.pop(); }

    public BasicBlock next() {return stack.pop(); }
  }

  /**
   * Densely number all the instructions currently in this IR
   * from 0...numInstr-1.
   * Returns the number of instructions in the IR.
   * Intended style of use:
   * <pre>
   *    passInfo = new passInfoObjects[ir.numberInstructions()];
   *    ...do analysis using passInfo as a look aside
   *            array holding pass specific info...
   * </pre>
   *
   * @return the number of instructions
   */
  public int numberInstructions() {
    int num = 0;
    for (Instruction instr = firstInstructionInCodeOrder(); instr != null; instr =
        instr.nextInstructionInCodeOrder(), num++) {
      instr.scratch = num;
    }
    return num;
  }

  /**
   * Set the scratch word on all instructions currently in this
   * IR to a given value.
   *
   * @param value value to store in all instruction scratch words
   */
  public void setInstructionScratchWord(int value) {
    for (Instruction instr = firstInstructionInCodeOrder(); instr != null; instr =
        instr.nextInstructionInCodeOrder()) {
      instr.scratch = value;
    }
  }

  /**
   * Clear (set to zero) the scratch word on all
   * instructions currently in this IR.
   */
  public void clearInstructionScratchWord() {
    setInstructionScratchWord(0);
  }

  /**
   * Clear (set to null) the scratch object on
   * all instructions currently in this IR.
   */
  public void clearInstructionScratchObject() {
    for (Instruction instr = firstInstructionInCodeOrder(); instr != null; instr =
        instr.nextInstructionInCodeOrder()) {
      instr.scratchObject = null;
    }
  }

  /**
   * Clear (set to null) the scratch object on
   * all basic blocks currently in this IR.
   */
  public void clearBasicBlockScratchObject() {
    BasicBlockEnumeration e = getBasicBlocks();
    while (e.hasMoreElements()) {
      e.next().scratchObject = null;
    }
  }

  /**
   * Return the number of symbolic registers for this IR
   */
  public int getNumberOfSymbolicRegisters() {
    return regpool.getNumberOfActiveRegisters();
  }

  /**
   * @return the largest basic block number assigned to
   *         a block in the IR. Will return -1 if no
   *         block numbers have been assigned.
   */
  public int getMaxBasicBlockNumber() {
    if (cfg == null) {
      return -1;
    } else {
      return cfg.numberOfNodes();
    }
  }

  /**
   * Prune the exceptional out edges for each basic block in the IR.
   */
  public void pruneExceptionalOut() {
    if (hasReachableExceptionHandlers()) {
      for (Enumeration<BasicBlock> e = getBasicBlocks(); e.hasMoreElements();) {
        BasicBlock bb = e.nextElement();
        bb.pruneExceptionalOut(this);
      }
    }
  }

  /**
   * @return <code>true</code> if it is possible that the IR contains
   *         an exception handler, <code>false</code> if it is not.
   *         Note this method may conservatively return <code>true</code>
   *         even if the IR does not actually contain a reachable
   *         exception handler.
   */
  public boolean hasReachableExceptionHandlers() {
    return hasReachableExceptionHandlers;
  }

  /**
   * Partially convert the FCFG into a more traditional
   * CFG by splitting all nodes that contain PEIs and that
   * have reachable exception handlers into multiple basic
   * blocks such that the instructions in the block have
   * the expected post-dominance relationship. Note, we do
   * not bother to unfactor basic blocks that do not have reachable
   * exception handlers because the fact that the post-dominance
   * relationship between instructions does not hold in these blocks
   * does not matter (at least for intraprocedural analyses).
   * For more information {@link BasicBlock see}.
   */
  public void unfactor() {
    BasicBlockEnumeration e = getBasicBlocks();
    while (e.hasMoreElements()) {
      BasicBlock b = e.next();
      b.unfactor(this);
    }
  }

  /**
   * Verify that the IR is well-formed.
   * NB: this is expensive -- be sure to guard invocations with
   * debugging flags.
   *
   * @param where phrase identifying invoking  compilation phase
   */
  public void verify(String where) {
    verify(where, true);
  }

  /**
   * Verify that the IR is well-formed.
   * NB: this is expensive -- be sure to guard invocations with
   * debugging flags.
   *
   * @param where    phrase identifying invoking  compilation phase
   * @param checkCFG should the CFG invariants be checked
   *                 (they can become invalid in "late" MIR).
   */
  public void verify(String where, boolean checkCFG) {
    // Check basic block and the containing instruction construction
    verifyBBConstruction(where);

    if (checkCFG) {
      // Check CFG invariants
      verifyCFG(where);
    }

    // Verify registers aren't in use for 2 different types
    verifyRegisterTypes(where);

    if (options.verifyIR == OptOptions.PARANOID) {
      // Follow CFG checking use follows def (ultra-expensive)
      verifyUseFollowsDef(where);

      // Simple sanity checks on instructions
      verifyInstructions(where);
    }

    // Make sure CFG is in fit state for dominators
    // TODO: Enable this check; currently finds some broken IR
    //       that we need to fix.
    // verifyAllBlocksAreReachable(where);
  }

  /**
   * Verify basic block construction from the basic block and
   * instruction information.
   *
   * @param where    phrase identifying invoking  compilation phase
   */
  private void verifyBBConstruction(String where) {
    // First, verify that the basic blocks are properly chained together
    // and that each basic block's instruction list is properly constructed.
    BasicBlock cur = cfg.firstInCodeOrder();
    BasicBlock prev = null;
    while (cur != null) {
      if (cur.getPrev() != prev) {
        verror(where, "Prev link of " + cur + " does not point to " + prev);
      }

      // Verify cur's start and end instructions
      Instruction s = cur.firstInstruction();
      Instruction e = cur.lastInstruction();
      if (s == null) {
        verror(where, "Bblock " + cur + " has null start instruction");
      }
      if (e == null) {
        verror(where, "Bblock " + cur + " has null end instruction");
      }

      // cur has start and end instructions,
      // make sure that they are locally ok.
      if (!s.isBbFirst()) {
        verror(where, "Instr " + s + " is first instr of " + cur + " but is not BB_FIRST");
      }
      if (s.getBasicBlock() != cur) {
        verror(where, "Instr " + s + " is first instr of " + cur + " but points to BBlock " + s.getBasicBlock());
      }
      if (!e.isBbLast()) {
        verror(where, "Instr " + e + " is last instr of " + cur + " but is not BB_LAST");
      }
      if (e.getBasicBlock() != cur) {
        verror(where, "Instr " + e + " is last instr of " + cur + " but points to BBlock " + e.getBasicBlock());
      }

      // Now check the integrity of the block's instruction list
      if (s.getPrev() != null) {
        verror(where, "Instr " + s + " is the first instr of " + cur + " but has a predecessor " + s.getPrev());
      }
      if (e.getNext() != null) {
        verror(where, "Instr " + s + " is the last instr of " + cur + " but has a successor " + e.getNext());
      }
      Instruction pp = s;
      Instruction p = s.getNext();
      boolean foundBranch = false;
      while (p != e) {
        if (p == null) {
          verror(where, "Fell off the instruction list in " + cur + " before finding " + e);
        }
        if (p.getPrev() != pp) {
          verror(where, "Instr " + pp + " has next " + p + " but " + p + " has prev " + p.getPrev());
        }
        if (!p.isBbInside()) {
          verror(where, "Instr " + p + " should be inside " + cur + " but is not BBInside");
        }
        if (foundBranch && !p.isBranch()) {
          printInstructions();
          verror(where, "Non branch " + p + " after branch " + pp + " in " + cur);
        }
        if (p.isBranch() && !(p instanceof LowTableSwitch)) {
          foundBranch = true;
          if (p.isUnconditionalBranch() && p.getNext() != e) {
            printInstructions();
            verror(where, "Unconditional branch " + p + " does not end its basic block " + cur);
          }
        }
        pp = p;
        p = p.getNext();
      }
      if (p.getPrev() != pp) {
        verror(where, "Instr " + pp + " has next " + p + " but " + p + " has prev " + p.getPrev());
      }

      // initialize the mark bit for the bblist test below
      cur.scratch = 0;

      prev = cur;
      cur = (BasicBlock) cur.getNext();
    }
  }

  /**
   * Verify control flow graph construction
   *
   * @param where    phrase identifying invoking  compilation phase
   */
  private void verifyCFG(String where) {
    // Check that the CFG links are well formed
    final int inBBListMarker = 999;  // actual number is insignificant
    final boolean VERIFY_CFG_EDGES = false;
    HashSet<BasicBlock> origOutSet = null;
    if (VERIFY_CFG_EDGES) origOutSet = new HashSet<BasicBlock>();

    for (BasicBlock cur = cfg.firstInCodeOrder(); cur != null; cur = (BasicBlock) cur.getNext()) {

      // Check incoming edges
      for (BasicBlockEnumeration e = cur.getIn(); e.hasMoreElements();) {
        BasicBlock pred = e.next();
        if (!pred.pointsOut(cur)) {
          verror(where, pred + " is an inEdge of " + cur + " but " + cur + " is not an outEdge of " + pred);
        }
      }

      // Check outgoing edges
      for (BasicBlockEnumeration e = cur.getOut(); e.hasMoreElements();) {
        BasicBlock succ = e.next();
        if (!succ.pointsIn(cur)) {
          verror(where, succ + " is an outEdge of " + cur + " but " + cur + " is not an inEdge of " + succ);
        }
        // Remember the original out edges for CFG edge verification
        if (VERIFY_CFG_EDGES) origOutSet.add(succ);
      }

      if (VERIFY_CFG_EDGES) {
        // Next, check that the CFG links are semantically correct
        // (ie that the CFG links and branch instructions agree)
        // This done by calling recomputeNormalOut() and confirming
        // that nothing changes.
        cur.recomputeNormalOut(this);

        // Confirm outgoing edges didn't change
        for (BasicBlockEnumeration e = cur.getOut(); e.hasMoreElements();) {
          BasicBlock succ = e.next();
          if (!origOutSet.contains(succ) && !succ.isExit() // Sometimes recomput is conservative in adding edge to exit
            // because it relies soley on the mayThrowUncaughtException
            // flag.
              ) {
            cur.printExtended();
            verror(where,
                   "An edge in the cfg was incorrect.  " +
                   succ +
                   " was not originally an out edge of " +
                   cur +
                   " but it was after calling recomputeNormalOut()");
          }
          origOutSet.remove(succ); // we saw it, so remove it
        }
        // See if there were any edges that we didn't see the second
        // time around
        if (!origOutSet.isEmpty()) {
          BasicBlock missing = origOutSet.iterator().next();

          cur.printExtended();
          verror(where,
                 "An edge in the cfg was incorrect.  " +
                 missing +
                 " was originally an out edge of " +
                 cur +
                 " but not after calling recomputeNormalOut()");
        }
      }

      // mark this block because it is the bblist
      cur.scratch = inBBListMarker;
    }

    // Check to make sure that all blocks connected
    // (via a CFG edge) to a block
    // that is in the bblist are also in the bblist
    for (BasicBlock cur = cfg.firstInCodeOrder(); cur != null; cur = (BasicBlock) cur.getNext()) {
      for (BasicBlockEnumeration e = cur.getIn(); e.hasMoreElements();) {
        BasicBlock pred = e.next();
        if (pred.scratch != inBBListMarker) {
          verror(where,
                 "In Method " +
                 method.getName() +
                 ", " +
                 pred +
                 " is an inEdge of " +
                 cur +
                 " but it is not in the CFG!");
        }
      }
      for (BasicBlockEnumeration e = cur.getOut(); e.hasMoreElements();) {
        BasicBlock succ = e.next();
        if (succ.scratch != inBBListMarker) {
          // the EXIT block is never in the BB list
          if (succ != cfg.exit()) {
            verror(where,
                   "In Method " +
                   method.getName() +
                   ", " +
                   succ +
                   " is an outEdge of " +
                   cur +
                   " but it is not in the CFG!");
          }
        }
      }
    }
  }

  /**
   * Verify that every instruction:
   * <ul>
   * <li>1) has operands that back reference it</li>
   * <li>2) is valid for its position in the basic block</li>
   * <li>3) if we are MIR, has no guard operands</li>
   * </ul>
   *
   * @param where phrase identifying invoking  compilation phase
   */
  private void verifyInstructions(String where) {
    Enumeration<BasicBlock> bbEnum = cfg.basicBlocks();
    while (bbEnum.hasMoreElements()) {
      BasicBlock block = bbEnum.nextElement();
      IREnumeration.AllInstructionsEnum instructions 
      	= new IREnumeration.AllInstructionsEnum(this, block);
      boolean startingInstructionsPassed = false;
      while (instructions.hasMoreElements()) {
        Instruction instruction = instructions.next();
        // Perform (1) and (3)
        OperandEnumeration useOperands = instruction.getUses();
        while (useOperands.hasMoreElements()) {
          Operand use = useOperands.next();
          if (use.instruction != instruction) {
            verror(where,
                   "In block " +
                   block +
                   " for instruction " +
                   instruction +
                   " the back link in the use of operand " +
                   use +
                   " is invalid and references " +
                   use
                       .instruction);
          }
          if ((use.isRegister()) && (use.asRegister().getRegister().isValidation())) {
            verror(where,
                   "In block " +
                   block +
                   " for instruction " +
                   instruction +
                   " the use operand " +
                   use +
                   " is invalid as it is a validation register and this IR is in MIR form");
          }
        }
        OperandEnumeration defOperands = instruction.getDefs();
        while (defOperands.hasMoreElements()) {
          Operand def = defOperands.next();
          if (def.instruction != instruction) {
            verror(where,
                   "In block " +
                   block +
                   " for instruction " +
                   instruction +
                   " the back link in the def of operand " +
                   def +
                   " is invalid and references " +
                   def
                       .instruction);
          }
          if ((def.isRegister()) && (def.asRegister().getRegister().isValidation())) {
            verror(where,
                   "In block " +
                   block +
                   " for instruction " +
                   instruction +
                   " the def operand " +
                   def +
                   " is invalid as it is a validation register and this IR is in MIR form");
          }
        }
        // Perform (2)
        // test for starting instructions
        if (!startingInstructionsPassed) {
          if (instruction instanceof Label) {
            continue;
          }
         startingInstructionsPassed = true;
        }
        // main instruction location test
        switch (instruction.getOpcode()) {
          // Label and phi nodes must be at the start of a BB
          case Operators.Phi:
          case Operators.Label:
            verror(where, "Unexpected instruction in the middle of a basic block " + instruction);
            // BBend, Goto, IfCmp, TableSwitch, Return, Trap and Athrow
            // must all appear at the end of a basic block
          case Operators.IntIfcmp:
          case Operators.IntIfcmp2:
          case Operators.LongIfcmp:
          case Operators.FloatIfcmp:
          case Operators.DoubleIfcmp:
          case Operators.RefIfcmp:
            instruction = instructions.next();
            if (!(instruction instanceof Goto) && 
            	!(instruction instanceof BBend)) {
              verror(where, "Unexpected instruction after IFCMP " + instruction);
            }
            if (instruction instanceof Goto) {
              instruction = instructions.next();
              if (!(instruction instanceof BBend)) {
                verror(where, "Unexpected instruction after GOTO/MIR_BRANCH " + instruction);
              }
            }
            if (instructions.hasMoreElements()) {
              verror(where, "Unexpected instructions after BBEND " + instructions.next());
            }
            break;
          case Operators.TableSwitch:
          case Operators.LookupSwitch:
          case Operators.Athrow:
          case Operators.Return:
            // TODO: Traps should be at the end of basic blocks but
            // Simplify reduces instructions to traps not respecting
            // this. Uses of Simplify should eliminate unreachable
            // instructions when an instruction not at the end of a
            // basic block is reduced into a trap. When this happens
            // please uncomment the next line:
            //case Operators.Trap:
          case Operators.Goto:
            Instruction next = instructions.next();
            if (!(next instanceof BBend)) {
              verror(where, "Unexpected instruction after " + instruction + "\n" + next);
            }
            if (instructions.hasMoreElements()) {
              verror(where, "Unexpected instructions after BBEND " + instructions.next());
            }
            break;
          case Operators.BBend:
            if (instructions.hasMoreElements()) {
              verror(where, "Unexpected instructions after BBEND " + instructions.next());
            }
            break;
          default:
        }
      }
    }
  }

  /**
   * Verify that every block in the CFG is reachable as failing to do
   * so will cause EnterSSA.insertPhiFunctions to possibly access
   * elements in DominanceFrontier.getIteratedDominanceFrontier
   * and then DominanceFrontier.getDominanceFrontier that aren't
   * defined. Also verify that blocks reached over an exception out
   * edge are not also reachable on normal out edges as this will
   * confuse liveness analysis.
   *
   * @param where    phrase identifying invoking  compilation phase
   */
  @SuppressWarnings("unused")
  // used when needed for debugging
  private void verifyAllBlocksAreReachable(String where) {
    BitVector reachableNormalBlocks = new BitVector(cfg.numberOfNodes());
    BitVector reachableExceptionBlocks = new BitVector(cfg.numberOfNodes());
    resetBasicBlockMap();
    verifyAllBlocksAreReachable(where, cfg.entry(), reachableNormalBlocks, reachableExceptionBlocks, false);
    boolean hasUnreachableBlocks = false;
    StringBuffer unreachablesString = new StringBuffer();
    for (int j = 0; j < cfg.numberOfNodes(); j++) {
      if (!reachableNormalBlocks.get(j) && !reachableExceptionBlocks.get(j)) {
        hasUnreachableBlocks = true;
        if (basicBlockMap[j] != null) {
          basicBlockMap[j].printExtended();
        }
        unreachablesString.append(" BB").append(j);
      }
    }
    if (hasUnreachableBlocks) {
      verror(where, "Unreachable blocks in the CFG which will confuse dominators:" + unreachablesString);
    }
  }

  /**
   * Verify that every block in the CFG is reachable as failing to do
   * so will cause EnterSSA.insertPhiFunctions to possibly access
   * elements in DominanceFrontier.getIteratedDominanceFrontier
   * and then DominanceFrontier.getDominanceFrontier that aren't
   * defined. Also verify that blocks reached over an exception out
   * edge are not also reachable on normal out edges as this will
   * confuse liveness analysis.
   *
   * @param where location of verify in compilation
   * @param curBB the current BB to work on
   * @param visitedNormalBBs the blocks already visited (to avoid cycles) on normal out edges
   * @param visitedExceptionalBBs the blocks already visited (to avoid cycles) on exceptional out edges
   * @param fromExceptionEdge should paths from exceptions be validated?
   */
  private void verifyAllBlocksAreReachable(String where, BasicBlock curBB, BitVector visitedNormalBBs,
                                           BitVector visitedExceptionalBBs, boolean fromExceptionEdge) {
    // Set visited information
    if (fromExceptionEdge) {
      visitedExceptionalBBs.set(curBB.getNumber());
    } else {
      visitedNormalBBs.set(curBB.getNumber());
    }

    // Recurse to next BBs
    BasicBlockEnumeration outBlocks = curBB.getNormalOut();
    while (outBlocks.hasMoreElements()) {
      BasicBlock out = outBlocks.next();
      if (!visitedNormalBBs.get(out.getNumber())) {
        verifyAllBlocksAreReachable(where, out, visitedNormalBBs, visitedExceptionalBBs, false);
      }
    }
    outBlocks = curBB.getExceptionalOut();
    while (outBlocks.hasMoreElements()) {
      BasicBlock out = outBlocks.next();
      if (!visitedExceptionalBBs.get(out.getNumber())) {
        verifyAllBlocksAreReachable(where, out, visitedNormalBBs, visitedExceptionalBBs, true);
      }
      if (visitedNormalBBs.get(out.getNumber())) {
        curBB.printExtended();
        out.printExtended();
        verror(where,
               "Basic block " +
               curBB +
               " reaches " +
               out +
               " by normal and exceptional out edges thereby breaking a liveness analysis assumption.");
      }
    }
    if (curBB.mayThrowUncaughtException()) {
      visitedExceptionalBBs.set(cfg.exit().getNumber());
      if (!cfg.exit().isExit()) {
        cfg.exit().printExtended();
        verror(where, "The exit block is reachable by an exception edge and contains instructions.");
      }
    }
  }

  /**
   * Verify that no register is used as a long type and an int type
   * PRECONDITION: register lists computed
   *
   * @param where    phrase identifying invoking  compilation phase
   */
  private void verifyRegisterTypes(String where) {
    for (Register r = regpool.getFirstSymbolicRegister(); r != null; r = r.getNext()) {
      // don't worry about physical registers
      if (r.isPhysical()) continue;

      int types = 0;
      if (r.isLong()) types++;
      if (r.isDouble()) types++;
      if (r.isInteger()) types++;
      if (r.isAddress()) types++;
      if (r.isFloat()) types++;
      if (types > 1) {
        verror(where, "Register " + r + " has incompatible types.");
      }
    }
  }

  /**
   * Check whether uses follow definitions and that in SSA form
   * variables aren't multiply defined
   */
  private void verifyUseFollowsDef(String where) {
    // Create set of defined variables and add registers that will be
    // defined before entry to the IR
    HashSet<Object> definedVariables = new HashSet<Object>();
    // NB the last two args determine how thorough we're going to test
    // things
    verifyUseFollowsDef(where,
                        definedVariables,
                        cfg.entry(),
                        new BitVector(cfg.numberOfNodes()),
                        new ArrayList<BasicBlock>(),
                        5,
                        // <-- maximum number of basic blocks followed
                        true
                        // <-- follow exception as well as normal out edges?
    );
  }

  /**
   * Check whether uses follow definitions and in SSA form that
   * variables aren't multiply defined
   *
   * @param where location of verify in compilation
   * @param definedVariables variables already defined on this path
   * @param curBB the current BB to work on
   * @param visitedBBs the blocks already visited (to avoid cycles)
   * @param path a record of the path taken to reach this basic block
   * @param traceExceptionEdges    should paths from exceptions be validated?
   */
  private void verifyUseFollowsDef(String where, HashSet<Object> definedVariables, BasicBlock curBB,
                                   BitVector visitedBBs, ArrayList<BasicBlock> path, int maxPathLength,
                                   boolean traceExceptionEdges) {
    if (path.size() > maxPathLength) {
      return;
    }
    path.add(curBB);
    // Process instructions in block
    IREnumeration.AllInstructionsEnum instructions = new IREnumeration.AllInstructionsEnum(this, curBB);
    while (instructions.hasMoreElements()) {
      Instruction instruction = instructions.next();
      // Special phi handling case
      if (instruction instanceof Phi) {
	Phi phi = (Phi)instruction;
        // Find predecessors that we have already visited
        for (int i = 0; i < phi.getNumberOf(); i++) {
          BasicBlock phi_pred = phi.getPred(i).block;
          if (phi_pred.getNumber() > basicBlockMap.length) {
            verror(where, "Phi predecessor not a valid basic block " + phi_pred);
          }
          if ((curBB != phi_pred) && path.contains(phi_pred)) {
            // This predecessor has been visited on this path so the
            // variable should be defined
            Object variable = getVariableUse(where, phi.getValue(i));
            if ((variable != null) && (!definedVariables.contains(variable))) {
              StringBuffer pathString = new StringBuffer();
              for (int j = 0; j < path.size(); j++) {
                pathString.append(path.get(j).getNumber());
                if (j < (path.size() - 1)) {
                  pathString.append("->");
                }
              }
              verror(where, "Use of " + variable + " before definition: " + instruction + "\npath: " + pathString);
            }
          }
        }
      } else {
        // General use follows def test
        OperandEnumeration useOperands = instruction.getUses();
        while (useOperands.hasMoreElements()) {
          Object variable = getVariableUse(where, useOperands.next());
          if ((variable != null) && (!definedVariables.contains(variable))) {
            if (instruction.nameOf().indexOf("Xor") != -1)
              continue;
            StringBuffer pathString = new StringBuffer();
            for (int i = 0; i < path.size(); i++) {
              pathString.append(path.get(i).getNumber());
              if (i < (path.size() - 1)) {
                pathString.append("->");
              }
            }
            verror(where, "Use of " + variable + " before definition: " + instruction + "\npath: " + pathString);
          }
        }
      }
      // Add definitions to defined variables
      OperandEnumeration defOperands = instruction.getDefs();
      while (defOperands.hasMoreElements()) {
        Object variable = getVariableDef(where, defOperands.next());
        // Check that a variable isn't defined twice when we believe we're in SSA form
        if (variable != null) {
          if (definedVariables.contains(variable)) {
            verror(where, "Single assignment broken - multiple definitions of " + variable);
          }
          definedVariables.add(variable);
        }
      }
    }
    // Recurse to next BBs
    visitedBBs.set(curBB.getNumber());
    BasicBlockEnumeration outBlocks;
    if (traceExceptionEdges) {
      outBlocks = curBB.getOut(); // <-- very slow
    } else {
      outBlocks = curBB.getNormalOut();
    }
    while (outBlocks.hasMoreElements()) {
      BasicBlock out = outBlocks.next();
      if (!visitedBBs.get(out.getNumber())) {
        verifyUseFollowsDef(where,
                            new HashSet<Object>(definedVariables),
                            out,
                            new BitVector(visitedBBs),
                            new ArrayList<BasicBlock>(path),
                            maxPathLength,
                            traceExceptionEdges);
        visitedBBs.set(out.getNumber());
      }
    }
  }

  /**
   * Get the variable used by this operand
   *
   * @param where the verification location
   * @param operand the operand to pull a variable from
   * @return null if the variable should be ignored otherwise the variable
   */
  private Object getVariableUse(String where, Operand operand) {
    if (operand.isConstant() ||
        operand.isStringConstant() ||
        operand.isType() ||
        operand.isMethod() ||
        operand.isBranch() ||
        operand.isBranchProfile() ||
        operand.isField() || operand.isALength() || operand.isArray() || 
        operand.isCondition()) {
      return null;
    } else if (operand.isRegister()) {
      Register register = operand.asRegister().getRegister();
      // ignore physical registers
      return (register.isPhysical()) ? null : register;
    } else if (operand.isBlock()) {
      Enumeration<BasicBlock> blocks = cfg.basicBlocks();
      while (blocks.hasMoreElements()) {
        if (operand.asBlock().block == blocks.nextElement()) {
          return null;
        }
      }
      verror(where, "Basic block not found in CFG for BasicBlockOperand: " + operand);
      return null; // keep jikes quiet
    } else if (operand instanceof HeapOperand) {
      HeapVariable<?> variable = ((HeapOperand<?>) operand).getHeapVariable();
      if (variable.getNumber() > 0) {
        return variable;
      } else {
        // definition 0 comes in from outside the IR
        return null;
      }
    } else {
      verror(where, "Unknown variable of " + operand.getClass() + " with operand: " + operand);
      return null; // keep jikes quiet
    }
  }

  /**
   * Get the variable defined by this operand
   *
   * @param where the verification location
   * @param operand the operand to pull a variable from
   * @return null if the variable should be ignored otherwise the variable
   */
  private Object getVariableDef(String where, Operand operand) {
    if (operand.isRegister()) {
      Register register = operand.asRegister().getRegister();
      // ignore physical registers
      return (register.isPhysical()) ? null : register;
    } else if (operand instanceof HeapOperand) {
      return ((HeapOperand<?>) operand).getHeapVariable();
    } else {
      verror(where, "Unknown variable of " + operand.getClass() + " with operand: " + operand);
      return null; // keep jikes quiet
    }
  }

  /**
   * Generate error
   *
   * @param where    phrase identifying invoking  compilation phase
   * @param msg      error message
   */
  private void verror(String where, String msg) {
    CompilerPhase.dumpIR(this, "Verify: " + where + ": " + method, true);
    dumpFile.current.println("VERIFY: " + where + " " + msg);
    throw new Error();
  }
}
