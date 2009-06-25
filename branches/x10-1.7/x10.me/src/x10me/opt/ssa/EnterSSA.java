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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;


import x10me.opt.DefUse;
import x10me.opt.bc2ir.BCconstants;
import x10me.opt.controlflow.BasicBlock;
import x10me.opt.controlflow.BasicBlockEnumeration;
import x10me.opt.controlflow.DominanceFrontier;
import x10me.opt.controlflow.DominatorTreeNode;
import x10me.opt.driver.CompilerPhase;
import x10me.opt.driver.OptOptions;
import x10me.opt.ir.*;
import x10me.opt.ir.operand.BasicBlockOperand;
import x10me.opt.ir.operand.HeapOperand;
import x10me.opt.ir.operand.Operand;
import x10me.opt.ir.operand.RegisterOperand;
import x10me.opt.ir.operand.UnreachableOperand;
import x10me.opt.util.TreeNode;
import x10me.types.KnownTypes;
import x10me.types.Type;
import x10me.util.BitVector;
import x10me.util.Pair;

/**
 * This compiler phase constructs SSA form.
 *
 * <p> This module constructs SSA according to the SSA properties defined
 * in </code> IR.desiredSSAOptions </code>.  See <code> SSAOptions
 * </code> for more details on supported options for SSA construction.
 *
 * <p>The SSA construction algorithm is the classic dominance frontier
 * based algorithm from Cytron et al.'s 1991 TOPLAS paper.
 *
 * <p> See our SAS 2000 paper
 * <a href="http://www.research.ibm.com/jalapeno/publication.html#sas00">
 *  Unified Analysis of Arrays and Object References in Strongly Typed
 *  Languages </a> for an overview of Array SSA form.  More implementation
 *  details are documented in {@link SSA <code> SSA.java</code>}.
 *
 * @see SSA
 * @see SSAOptions
 * @see x10me.opt.controlflow.LTDominators
 */
public class EnterSSA extends CompilerPhase {

  /**
   * The governing IR
   */
  private IR ir;

  /**
   * A set of registers determined to span basic blocks
   */
  private HashSet<Register> nonLocalRegisters;

  /**
   * The set of scalar phi functions inserted
   */
  private final HashSet<Instruction> scalarPhis = new HashSet<Instruction>();

  /**
   * For each basic block, the number of predecessors that have been
   * processed.
   */
  private int[] numPredProcessed;

  /**
   * Constructor for this compiler phase
   */  
  public EnterSSA (IR ir) {
    super(ir);
  }

  /**
   * Return a string identifying this compiler phase.
   * @return "Enter SSA"
   */
  public final String getName() {
    return "Enter SSA";
  }

  /**
   * Construct SSA form to satisfy the desired options in ir.desiredSSAOptions.
   * This module is lazy; if the actual SSA options satisfy the desired options,
   * then do nothing.
   *
   * @param ir the governing IR
   */
  public final void perform() {
     if (ir.dumpFile.current != null) {
      ir.printInstructions();
    }
    computeSSA();
  }
 
  /**
   * Calculate SSA form for an IR.  This routine holds the guts of the
   * transformation.
   *
   * @param ir the governing IR
   */
  private void computeSSA() {

    // 1. re-compute the flow-insensitive isSSA flag for each register
    DefUse.computeDU(ir);
    DefUse.recomputeSSA(ir);

    // 2. set up a mapping from symbolic register number to the
    //  register.  !!TODO: factor this out and make it more
    //  useful.
    Register[] symbolicRegisters = getSymbolicRegisters();

    // 3. walk through the IR, and set up BitVectors representing the defs
    //    for each symbolic register (more efficient than using register
    //  lists)
    if (ir.dumpFile.current != null) ir.dumpFile.current.println("Find defs for each register...");
    BitVector[] defSets = getDefSets();

    // 4. Insert phi functions for scalars
    if (ir.dumpFile.current != null) ir.dumpFile.current.println("Insert phi functions...");
    insertPhiFunctions(defSets, symbolicRegisters);

    // 5. Insert heap variables into the Array SSA form
    insertHeapVariables();

    if (ir.dumpFile.current != null) {
      ir.dumpFile.current.println("Before renaming...");
      ir.printInstructions();
      ir.dumpFile.current.println("Renaming...");
    }

    renameSymbolicRegisters(symbolicRegisters);
    renameHeapVariables();
    
    if (ir.dumpFile.current != null) {
      ir.dumpFile.current.println("SSA done.");
      ir.printInstructions();
    }
  }

  /**
   * Insert heap variables needed for Array SSA form.
   *
   * @param ir the governing IR
   * @param backwards if this is true, every statement that can leave the
   *                   procedure <em> uses </em> every heap variable.
   *                   This option is useful for backwards analyses
   */
  private void insertHeapVariables() {
    // insert dphi functions where needed
    registerHeapVariables();

    // insert heap defs and uses for CALL instructions
    registerCalls();

    // insert phi funcions where needed
    insertHeapPhiFunctions();
  }

  /**
   * Register every CALL instruction in this method with the
   * implicit heap array SSA look aside structure.
   * Namely, mark that this instruction defs and uses <em> every </em>
   * type of heap variable in the IR's SSA dictionary.
   *
   * @param ir the governing IR
   */
  private void registerCalls() {
    SSADictionary dictionary = ir.dictionary;
    for (BasicBlockEnumeration bbe = ir.getBasicBlocks(); bbe.hasMoreElements();) {
      BasicBlock b = bbe.next();
      for (InstructionEnumeration e = b.forwardInstrEnumerator(); e.hasMoreElements();) {
        Instruction s = e.next();
        if (s instanceof ReadCeiling ||
            s instanceof WriteFloor ||
            s instanceof Call ||
            s instanceof MonitorOp ||
            s instanceof Prepare ||
            s instanceof Attempt ||
            s instanceof CacheOp ||
            s.isDynamicLinkPoint()) {
          dictionary.registerUnknown(s, b);
        }
      }
    }
  }

  /**
   * Register every instruction in this method with the
   * implicit heap array SSA lookaside structure.
   *
   * @param ir the governing IR
   */
  private void registerHeapVariables() {
    SSADictionary dictionary = ir.dictionary;
    for (BasicBlockEnumeration bbe = ir.getBasicBlocks(); bbe.hasMoreElements();) {
      BasicBlock b = bbe.next();
      for (InstructionEnumeration e = b.forwardInstrEnumerator(); e.hasMoreElements();) {
        Instruction s = e.next();
        if (s.isImplicitLoad() ||
            s.isImplicitStore() ||
            s.isAlloc() ||
            s instanceof Phi ||
            s.isPEI() ||
            s instanceof Label ||
            s instanceof BBend ||
            s instanceof UnintBegin ||
            s instanceof UnintEnd) {
          dictionary.registerInstruction(s, b);
        }
      }
    }
  }

  /**
   * Insert phi functions for heap array SSA heap variables.
   *
   * @param ir the governing IR
   */
  private void insertHeapPhiFunctions() {
    Iterator<HeapVariable<Object>> e = ir.dictionary.getHeapVariables();
    while (e.hasNext()) {
      HeapVariable<Object> H = e.next();

      if (ir.dumpFile.current != null) ir.dumpFile.current.println("Inserting phis for Heap " + H);
      if (ir.dumpFile.current != null) ir.dumpFile.current.println("Start iterated frontier...");

      BitVector defH = H.getDefBlocks();
      if (ir.dumpFile.current != null) ir.dumpFile.current.println(H + " DEFINED IN " + defH);

      BitVector needsPhi = DominanceFrontier.
          getIteratedDominanceFrontier(ir, defH);
      if (ir.dumpFile.current != null) ir.dumpFile.current.println(H + " NEEDS PHI " + needsPhi);

      if (ir.dumpFile.current != null) ir.dumpFile.current.println("Done.");
      for (int b = 0; b < needsPhi.length(); b++) {
        if (needsPhi.get(b)) {
          BasicBlock bb = ir.getBasicBlock(b);
          ir.dictionary.createHeapPhiInstruction(bb, H);
        }
      }
    }
  }

  /**
   * Calculate the set of blocks that contain defs for each
   *    symbolic register in an IR.  <em> Note: </em> This routine skips
   *    registers marked  already having a single static
   *    definition, physical registers, and guard registeres.
   *
   * @return an array of BitVectors, where element <em>i</em> represents the
   *    basic blocks that contain defs for symbolic register <em>i</em>
   */
  private BitVector[] getDefSets() {
    int nBlocks = ir.getMaxBasicBlockNumber();
    BitVector[] result = new BitVector[ir.getNumberOfSymbolicRegisters()];

    for (int i = 0; i < result.length; i++) {
      result[i] = new BitVector(nBlocks + 1);
    }

    // loop over each basic block
    for (BasicBlockEnumeration e = ir.getBasicBlocks(); e.hasMoreElements();) {
      BasicBlock bb = e.next();
      int bbNumber = bb.getNumber();
      // visit each instruction in the basic block
      for (InstructionEnumeration ie = bb.forwardInstrEnumerator(); ie.hasMoreElements();) {
        Instruction s = ie.next();
        // record each def in the instruction
        // skip SSA defs
        for (int j = 0; j < s.getNumberOfDefs(); j++) {
          Operand operand = s.getOperand(j);
          if (operand == null) continue;
          if (!operand.isRegister()) continue;
          if (operand.asRegister().getRegister().isSSA()) continue;
          if (operand.asRegister().getRegister().isPhysical()) continue;

          int reg = operand.asRegister().getRegister().getNumber();
          result[reg].set(bbNumber);
        }
      }
    }
    return result;
  }

  /**
   * Insert the necessary phi functions into an IR.
   * <p> Algorithm:
   * <p>For register r, let S be the set of all blocks that
   *    contain defs of r.  Let D be the iterated dominance frontier
   *    of S.  Each block in D needs a phi-function for r.
   *
   * <p> Special Java case: if node N dominates all defs of r, then N
   *                      does not need a phi-function for r
   *
   * @param ir the governing IR
   * @param defs defs[i] represents the basic blocks that define
   *            symbolic register i.
   * @param symbolics symbolics[i] is symbolic register number i
   */
  private void insertPhiFunctions(BitVector[] defs, Register[] symbolics) {
    for (int r = 0; r < defs.length; r++) {
      if (symbolics[r] == null) continue;
      if (symbolics[r].isSSA()) continue;
      if (ir.dumpFile.current != null) ir.dumpFile.current.println("Inserting phis for register " + r);
      if (ir.dumpFile.current != null) ir.dumpFile.current.println("Start iterated frontier...");
      BitVector needsPhi = DominanceFrontier.getIteratedDominanceFrontier(ir, defs[r]);
      removePhisThatDominateAllDefs(needsPhi, defs[r]);
      if (ir.dumpFile.current != null) ir.dumpFile.current.println("Done.");

      for (int b = 0; b < needsPhi.length(); b++) {
        if (needsPhi.get(b)) {
          BasicBlock bb = ir.getBasicBlock(b);
            insertPhi(bb, symbolics[r]);
        }
      }
    }
  }

  /**
   * If node N dominates all defs of a register r, then N does
   * not need a phi function for r; this function removes such
   * nodes N from a Bit Set.
   *
   * @param needsPhi representation of set of nodes that
   *                need phi functions for a register r
   * @param ir the governing IR
   * @param defs set of nodes that define register r
   */
  private void removePhisThatDominateAllDefs(BitVector needsPhi, BitVector defs) {
    for (int i = 0; i < needsPhi.length(); i++) {
      if (!needsPhi.get(i)) {
        continue;
      }
      if (ir.dominatorTree.dominates(i, defs)) {
        needsPhi.clear(i);
      }
    }
  }

  /**
   * Insert a phi function for a symbolic register at the head
   * of a basic block.
   *
   * @param bb the basic block
   * @param r the symbolic register that needs a phi function
   */
  private void insertPhi(BasicBlock bb, Register r) {
    Instruction s = makePhiInstruction(r, bb);
    bb.firstInstruction().insertAfter(s);
    scalarPhis.add(s);
  }

  /**
   * Create a phi-function instruction
   *
   * @param r the symbolic register
   * @param bb the basic block holding the new phi function
   * @return the instruction r = PHI null,null,..,null
   */
  private Instruction makePhiInstruction(Register r, BasicBlock bb) {
    int n = bb.getNumberOfIn();
    BasicBlockEnumeration in = bb.getIn();
    Type type = null;
    Phi phi = new Phi(new RegisterOperand(r, type), n);
    for (int i = 0; i < n; i++) {
      RegisterOperand junk = new RegisterOperand(r, type);
      phi.setValue(i, junk);
      BasicBlock pred = in.next();
      phi.setPred(i, new BasicBlockOperand(pred));
    }
    phi.position = ir.inlineSequence;
    phi.bcIndex = BCconstants.SSA_SYNTH_BCI;
    return phi;
  }

  /**
   * Set up a mapping from symbolic register number to the register.
   * <p> TODO: put this functionality elsewhere.
   *
   * @return a mapping
   */
  private Register[] getSymbolicRegisters() {
    Register[] map = new Register[ir.getNumberOfSymbolicRegisters()];
    for (Register reg = ir.regpool.getFirstSymbolicRegister(); reg != null; reg = reg.getNext()) {
      int number = reg.getNumber();
      map[number] = reg;
    }
    return map;
  }

  /**
   * Rename the symbolic registers so that each register has only one
   * definition.
   *
   * <p><em> Note </em>: call this after phi functions have been inserted.
   * <p> <b> Algorithm:</b> from Cytron et. al 91
   * <pre>
   *  call search(entry)
   *
   *  search(X):
   *  for each statement A in X do
   *     if A is not-phi
   *       for each r in RHS(A) do
   *            if !r.isSSA, replace r with TOP(S(r))
   *       done
   *     fi
   *    for each r in LHS(A) do
   *            if !r.isSSA
   *                r2 = new temp register
   *                push r2 onto S(r)
   *                replace r in A by r2
   *            fi
   *    done
   *  done (end of first loop)
   *  for each Y in succ(X) do
   *      j <- whichPred(Y,X)
   *      for each phi-function F in Y do
   *       replace the j-th operand (r) in RHS(F) with TOP(S(r))
   *     done
   *  done (end of second loop)
   *  for each Y in Children(X) do
   *    call search(Y)
   *  done (end of third loop)
   *  for each assignment A in X do
   *     for each r in LHS(A) do
   *      pop(S(r))
   *   done
   *  done (end of fourth loop)
   *  end
   * <pre>
   *
   * @param symbolicRegisters mapping from integer to symbolic registers
   */
  private void renameSymbolicRegisters(Register[] symbolicRegisters) {
    int n = ir.getNumberOfSymbolicRegisters();
    @SuppressWarnings("unchecked") // the old covariant array-type problem
        Stack<RegisterOperand>[] S = new Stack[n + 1];
    for (int i = 0; i < S.length; i++) {
      S[i] = new Stack<RegisterOperand>();
      // populate the Stacks with initial names for
      // each parameter, and push "null" for other symbolic registers
      if (i >= symbolicRegisters.length) continue;
      //Register r = symbolicRegisters[i];
      // If a register's name is "null", that means the
      // register has not yet been defined.
      S[i].push(null);
    }
    BasicBlock entry = ir.cfg.entry();
    DefUse.clearDU(ir);
    numPredProcessed = new int[ir.getMaxBasicBlockNumber()];
    search(entry, S);
    DefUse.recomputeSSA(ir);
    rectifyPhiTypes();
  }

  /**
   * This routine is the guts of the SSA construction phase for scalars.  See
   * renameSymbolicRegisters for more details.
   *
   * @param X basic block to search dominator tree from
   * @param S stack of names for each register
   */
  private void search(BasicBlock X, Stack<RegisterOperand>[] S) {
    if (ir.dumpFile.current != null) ir.dumpFile.current.println("SEARCH " + X);
    for (InstructionEnumeration ie = X.forwardInstrEnumerator(); ie.hasMoreElements();) {
      Instruction A = ie.next();
      if (!(A instanceof Phi)) {
        // replace each use
        for (int u = A.getNumberOfDefs(); u < A.getNumberOfOperands(); u++) {
          Operand op = A.getOperand(u);
          if (op instanceof RegisterOperand) {
            RegisterOperand rop = (RegisterOperand) op;
            Register r1 = rop.getRegister();
            if (r1.isSSA()) continue;
            if (r1.isPhysical()) continue;
            RegisterOperand r2 = S[r1.getNumber()].peek();
            if (ir.dumpFile.current != null) ir.dumpFile.current.println("REPLACE NORMAL USE " + r1 + " with " + r2);
            if (r2 != null) {
              rop.setRegister(r2.getRegister());
              DefUse.recordUse(rop);
            }
          }
        }
      }
      // replace each def
      for (int d = 0; d < A.getNumberOfDefs(); d++) {
        Operand op = A.getOperand(d);
        if (op instanceof RegisterOperand) {
          RegisterOperand rop = (RegisterOperand) op;
          Register r1 = rop.getRegister();
          if (r1.isSSA()) continue;
          if (r1.isPhysical()) continue;
          Register r2 = ir.regpool.getReg(r1);
          if (ir.dumpFile.current != null) ir.dumpFile.current.println("PUSH " + r2 + " FOR " + r1 + " BECAUSE " + A);
          S[r1.getNumber()].push(new RegisterOperand(r2, rop.getType()));
          rop.setRegister(r2);
          r2.scratchObject = r1;
        }
      }
    } // end of first loop

    if (ir.dumpFile.current != null) ir.dumpFile.current.println("SEARCH (second loop) " + X);
    for (BasicBlockEnumeration y = X.getOut(); y.hasMoreElements();) {
      BasicBlock Y = y.next();
      if (ir.dumpFile.current != null) ir.dumpFile.current.println(" Successor: " + Y);
      int j = numPredProcessed[Y.getNumber()]++;
      if (Y.isExit()) continue;
      Instruction s = Y.firstRealInstruction();
      if (s == null) continue;
      // replace use USE in each PHI instruction
      if (ir.dumpFile.current != null) ir.dumpFile.current.println(" Predecessor: " + j);
      while (s instanceof Phi) {
	Phi phi = (Phi)s;
        Operand val = phi.getValue(j);
        if (val.isRegister()) {
          Register r1 = ((RegisterOperand) phi.getValue(j)).getRegister();
          // ignore registers already marked SSA by a previous pass
          if (!r1.isSSA()) {
            RegisterOperand r2 = S[r1.getNumber()].peek();
            if (r2 == null) {
              // in this case, the register is never defined along
              // this particular control flow path into the basic
              // block.
              phi.setValue(j, new UnreachableOperand());
            } else {
              RegisterOperand rop = r2.copyRO();
              phi.setValue(j, rop);
              DefUse.recordUse(rop);
            }
            phi.setPred(j, new BasicBlockOperand(X));
          }
        }
        s = s.nextInstructionInCodeOrder();
      }
    } // end of second loop

    if (ir.dumpFile.current != null) ir.dumpFile.current.println("SEARCH (third loop) " + X);
    for (Enumeration<TreeNode> c = ir.dominatorTree.getChildren(X); c.hasMoreElements();) {
      DominatorTreeNode v = (DominatorTreeNode) c.nextElement();
      search(v.getBlock(), S);
    } // end of third loop

    if (ir.dumpFile.current != null) ir.dumpFile.current.println("SEARCH (fourth loop) " + X);
    for (InstructionEnumeration a = X.forwardInstrEnumerator(); a.hasMoreElements();) {
      Instruction A = a.next();
      // loop over each def
      for (int d = 0; d < A.getNumberOfDefs(); d++) {
        Operand newOp = A.getOperand(d);
        if (newOp == null) continue;
        if (!newOp.isRegister()) continue;
        Register newReg = newOp.asRegister().getRegister();
        if (newReg.isSSA()) continue;
        if (newReg.isPhysical()) continue;
        Register r1 = (Register) newReg.scratchObject;
        S[r1.getNumber()].pop();
        if (ir.dumpFile.current != null) ir.dumpFile.current.println("POP " + r1);
      }
    } // end of fourth loop
    if (ir.dumpFile.current != null) ir.dumpFile.current.println("FINISHED SEARCH " + X);
  }

  /**
   * Rename the implicit heap variables in the SSA form so that
   * each heap variable has only one definition.
   *
   * <p> Algorithm: Cytron et. al 91  (see renameSymbolicRegisters)
   *
   * @param ir the governing IR
   */
  private void renameHeapVariables() {
    int n = ir.dictionary.getNumberOfHeapVariables();
    if (n == 0) {
      return;
    }
    // we maintain a stack of names for each type of heap variable
    // stacks implements a mapping from type to Stack.
    // Example: to get the stack of names for HEAP<int> variables,
    // use stacks.get(ClassLoaderProxy.IntType);
    HashMap<Object, Stack<HeapOperand<Object>>> stacks = new HashMap<Object, Stack<HeapOperand<Object>>>(n);
    // populate the stacks variable with the initial heap variable
    // names, currently stored in the SSADictionary
    for (Iterator<HeapVariable<Object>> e = ir.dictionary.getHeapVariables(); e.hasNext();) {
      HeapVariable<Object> H = e.next();
      Stack<HeapOperand<Object>> S = new Stack<HeapOperand<Object>>();
      S.push(new HeapOperand<Object>(H));
      Object heapType = H.getHeapType();
      stacks.put(heapType, S);
    }
    BasicBlock entry = ir.cfg.entry();
    numPredProcessed = new int[ir.getMaxBasicBlockNumber()];
    search2(entry, stacks);
    // registerRenamedHeapPhis(ir);
  }

  /**
   * This routine is the guts of the SSA construction phase for heap array
   * SSA.  The renaming algorithm is analogous to the algorithm for
   * scalars See <code> renameSymbolicRegisters </code> for more details.
   *
   * @param X the current basic block being traversed
   * @param stacks a structure holding the current names for each heap
   * variable
   * used and defined by each instruction.
   */
  private void search2(BasicBlock X, HashMap<Object, Stack<HeapOperand<Object>>> stacks) {
    if (ir.dumpFile.current != null) ir.dumpFile.current.println("SEARCH2 " + X);
    SSADictionary dictionary = ir.dictionary;
    for (Enumeration<Instruction> ie = dictionary.getAllInstructions(X); ie.hasMoreElements();) {
      Instruction A = ie.nextElement();
      if (!dictionary.usesHeapVariable(A) && !dictionary.defsHeapVariable(A)) continue;
      if (!(A instanceof Phi)) {
        // replace the Heap variables USED by this instruction
        HeapOperand<Object>[] uses = dictionary.getHeapUses(A);
        if (uses != null) {
          @SuppressWarnings("unchecked")  // Generic array problem
              HeapOperand<Object>[] newUses = new HeapOperand[uses.length];
          for (int i = 0; i < uses.length; i++) {
            Stack<HeapOperand<Object>> S = stacks.get(uses[i].getHeapType());
            newUses[i] = S.peek().copy();
            if (ir.dumpFile.current != null) {
              ir.dumpFile.current.println("NORMAL USE PEEK " + newUses[i]);
            }
          }
          dictionary.replaceUses(A, newUses);
        }
      }
      // replace any Heap variable DEF
      if (!(A instanceof Phi)) {
        HeapOperand<Object>[] defs = dictionary.getHeapDefs(A);
        if (defs != null) {
          for (HeapOperand<Object> operand : dictionary.replaceDefs(A, X)) {
            Stack<HeapOperand<Object>> S = stacks.get(operand.getHeapType());
            S.push(operand);
            if (ir.dumpFile.current != null) ir.dumpFile.current.println("PUSH " + operand + " FOR " + operand.getHeapType());
          }
        }
      } else {
        HeapOperand<Object>[] r = dictionary.replaceDefs(A, X);
        Stack<HeapOperand<Object>> S = stacks.get(r[0].getHeapType());
        S.push(r[0]);
        if (ir.dumpFile.current != null) ir.dumpFile.current.println("PUSH " + r[0] + " FOR " + r[0].getHeapType());
      }
    } // end of first loop

    for (BasicBlockEnumeration y = X.getOut(); y.hasMoreElements();) {
      BasicBlock Y = y.next();
      if (Y.isExit()) continue;
      int j = numPredProcessed[Y.getNumber()]++;
      // replace each USE in each HEAP-PHI function for Y
      for (Iterator<Instruction> hp = dictionary.getHeapPhiInstructions(Y); hp.hasNext();) {
        Phi phi = (Phi)hp.next();
        @SuppressWarnings("unchecked") // Down-cast to a generic type
            HeapOperand<Object> H1 = (HeapOperand) phi.getResult();
        Stack<HeapOperand<Object>> S = stacks.get(H1.getHeapType());
        HeapOperand<Object> H2 = S.peek();
        phi.setValue(j, new HeapOperand<Object>(H2.getHeapVariable()));
        phi.setPred(j, new BasicBlockOperand(X));
      }
    } // end of second loop

    for (Enumeration<TreeNode> c = ir.dominatorTree.getChildren(X); c.hasMoreElements();) {
      DominatorTreeNode v = (DominatorTreeNode) c.nextElement();
      search2(v.getBlock(), stacks);
    } // end of third loop

    for (Enumeration<Instruction> a = dictionary.getAllInstructions(X); a.hasMoreElements();) {
      Instruction A = a.nextElement();
      if (!dictionary.usesHeapVariable(A) && !dictionary.defsHeapVariable(A)) continue;
      // retrieve the Heap Variables defined by A
      if (!(A instanceof Phi)) {
        HeapOperand<Object>[] defs = dictionary.getHeapDefs(A);
        if (defs != null) {
          for (HeapOperand<Object> def : defs) {
            Stack<HeapOperand<Object>> S = stacks.get(def.getHeapType());
            S.pop();
            if (ir.dumpFile.current != null) ir.dumpFile.current.println("POP " + def.getHeapType());
          }
        }
      } else {
	Phi phi = (Phi)A;
        @SuppressWarnings("unchecked") // Down-cast to a generic type
            HeapOperand<Object> H = (HeapOperand) phi.getResult();
        Stack<HeapOperand<Object>> S = stacks.get(H.getHeapType());
        S.pop();
        if (ir.dumpFile.current != null) ir.dumpFile.current.println("POP " + H.getHeapType());
      }
    } // end of fourth loop
    if (ir.dumpFile.current != null) ir.dumpFile.current.println("END SEARCH2 " + X);
  }

  /**
   * After performing renaming on heap phi functions, this
   * routines notifies the SSA dictionary of the new names.
   *
   * FIXME - this was commented out: delete it ??  RJG
   *
   * @param ir the governing IR
   */
  @SuppressWarnings({"unused", "unchecked"})
  // HeapOperand requires casts to a generic type
  private void registerRenamedHeapPhis(IR ir) {
    SSADictionary ssa = ir.dictionary;
    for (BasicBlockEnumeration e1 = ir.getBasicBlocks(); e1.hasMoreElements();) {
      BasicBlock bb = e1.nextElement();
      for (Enumeration<Instruction> e2 = ssa.getAllInstructions(bb); e2.hasMoreElements();) {
        Instruction s = e2.nextElement();
        if (s instanceof Phi) {
          Phi phi = (Phi)s;
          if (ssa.defsHeapVariable(s)) {
            int n = phi.getNumberOf();
            HeapOperand<Object>[] uses = new HeapOperand[n];
            for (int i = 0; i < n; i++) {
              uses[i] = (HeapOperand) phi.getValue(i);
            }
            ssa.replaceUses(s, uses);
          }
        }
      }
    }
  }

  /**
   * Store a copy of the Heap variables each instruction defs.
   *
   * @param ir governing IR
   * @param store place to store copies
   */
  @SuppressWarnings("unused")
  private void copyHeapDefs(HashMap<Instruction, HeapOperand<?>[]> store) {
    SSADictionary dictionary = ir.dictionary;
    for (BasicBlockEnumeration be = ir.forwardBlockEnumerator(); be.hasMoreElements();) {
      BasicBlock bb = be.next();
      for (Enumeration<Instruction> e = dictionary.getAllInstructions(bb); e.hasMoreElements();) {
        Instruction s = e.nextElement();
        store.put(s, ir.dictionary.getHeapDefs(s));
      }
    }
  }

  /**
   * Compute type information for operands in each phi instruction.
   *
   * PRECONDITION: Def-use chains computed.
   * SIDE EFFECT: empties the scalarPhis set
   * SIDE EFFECT: bashes the Instruction scratch field.
   */
  private static final int NO_NULL_TYPE = 0;

  private void rectifyPhiTypes() {
    if (ir.dumpFile.current != null) ir.dumpFile.current.println("Rectify phi types.");
    removeAllUnreachablePhis(scalarPhis);
    while (!scalarPhis.isEmpty()) {
      boolean didSomething = false;
      for (Iterator<Instruction> i = scalarPhis.iterator(); i.hasNext();) {
        Phi phi = (Phi)i.next();
        phi.scratch = NO_NULL_TYPE;
        if (ir.dumpFile.current != null) ir.dumpFile.current.println("PHI: " + phi);
        Type meet = meetPhiType(phi);
        if (ir.dumpFile.current != null) ir.dumpFile.current.println("MEET: " + meet);
        if (meet != null) {
          didSomething = true;
          if (phi.scratch == NO_NULL_TYPE) i.remove();
          RegisterOperand result = (RegisterOperand) phi.getResult();
          result.setType(meet);
          for (Enumeration<RegisterOperand> e = DefUse.uses(result.getRegister()); e.hasMoreElements();) {
            RegisterOperand rop = e.nextElement();
            if (rop.getType() != meet) {
              rop.clearPreciseType();
              rop.setType(meet);
            }
          }
        }
      }
      if (!didSomething) {
        // iteration has bottomed out.
        return;
      }
    }
  }

  /**
   * Remove all phis that are unreachable
   */
  private void removeAllUnreachablePhis(HashSet<Instruction> scalarPhis) {
    boolean iterateAgain = false;
    do {
      iterateAgain = false;
      outer:
      for (Iterator<Instruction> i = scalarPhis.iterator(); i.hasNext();) {
        Phi phi = (Phi)i.next();
        for (int j = 0; j < phi.getNumberOf(); j++) {
          Operand op = phi.getValue(j);
          if (!(op instanceof UnreachableOperand)) {
            continue outer;
          }
        }
        RegisterOperand result = (RegisterOperand)phi.getResult();
        i.remove();
        for (Enumeration<RegisterOperand> e = DefUse.uses(result.getRegister()); e.hasMoreElements();) {
          RegisterOperand use = e.nextElement();
          Instruction s = use.instruction;
          if (s instanceof Phi) {
            for (int k = 0; k < phi.getNumberOf(); k++) {
              Operand op = phi.getValue(k);
              if (op != null && op.similar(result)) {
                phi.setValue(k, new UnreachableOperand());
                iterateAgain = true;
              }
            }
          }
        }
      }
    } while (iterateAgain);
  }

  /**
   * Remove all unreachable operands from scalar phi functions
   *
   * NOT CURRENTLY USED
   */
  @SuppressWarnings("unused")
  private void removeUnreachableOperands(HashSet<Instruction> scalarPhis) {
    for (Instruction s : scalarPhis) {
      Phi phi = (Phi)s;
      int i = 0;
      int j = 0;
      while (j < phi.getNumberOf()) {
	Operand v = phi.getValue(j);
	if (v instanceof UnreachableOperand) {
	  j++;
	  // rewrite the phi instruction to remove the unreachable
	  // operand
	  if (i != j) {
	    phi.setValue(i, phi.getClearValue(j));
	    phi.setPred(i, phi.getClearPred(j));
	  }
	}
	i++;
	j++;
      }
      if (i != j)
	phi.resize(i);
    }
  }

  /**
   * Return the meet of the types on the rhs of a phi instruction
   *
   * @param s phi instruction
   */
  private static Type meetPhiType(Phi phi) {
    Type result = null;
    int i = 0;
    // Find first operand that is not unreachable.
    while (i < phi.getNumberOf()) {
      Operand val = phi.getValue(i);
      if (!(val instanceof UnreachableOperand)) {
	result = val.getType();
	break;
      }
      i++;
    }
	      
    // Meet the first with the rest of the operands.
    for (int j = i + 1; j < phi.getNumberOf(); j++) {
      Operand val = phi.getValue(j);
      if (val instanceof UnreachableOperand) continue;
      Type t = val.getType();
      result = Type.findCommonSuper(result, t);
    }
     
    assert result != null;

    return result;
  }

  /**
   * Find a parameter type.
   *
   * <p> Given a register that holds a parameter, look at the register's
   * use chain to find the type of the parameter
   */
  @SuppressWarnings("unused")
  private Type findParameterType(Register p) {
    RegisterOperand firstUse = p.useList;
    if (firstUse == null) {
      return null;             // parameter has no uses
    }
    return firstUse.getType();
  }
}



