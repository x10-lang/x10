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

import java.util.ArrayList;

import x10me.opt.controlflow.BasicBlock;
import x10me.opt.driver.CompilerPhase;
import x10me.opt.driver.OptOptions;
import x10me.opt.ir.*;
import x10me.opt.ir.operand.FieldOperand;
import x10me.opt.ir.operand.IntConstantOperand;
import x10me.opt.ir.operand.LocationOperand;
import x10me.opt.ir.operand.Operand;
import x10me.opt.ir.operand.OperandEnumeration;
import x10me.opt.ir.operand.RegisterOperand;

/**
 * Perform local common-subexpression elimination for a factored basic
 * block.
 * <ul>
 *   <li> Note: this module also performs scalar replacement of loads
 *   <li> Note: this module also performs elimination of redundant
 *         nullchecks, boundchecks, and zero checks.
 * </ul>
 * Algorithm: Muchnick pp.379-385
 */
public class LocalCSE extends CompilerPhase {

  /**
   * Constructor
   */
  public LocalCSE(IR ir) {
    super(ir);
  }

  @Override
  public final boolean shouldPerform(OptOptions options) {
    return options.LOCAL_CSE;
  }

  public final String getName() {
    return "Local CSE";
  }

  /**
   * Perform Local CSE for a method.
   *
   * @param ir the IR to optimize
   */
  public final void perform() {
    // iterate over each basic block
    for (BasicBlock bb = ir.firstBasicBlockInCodeOrder(); bb != null; bb = bb.nextBasicBlockInCodeOrder()) {
      if (bb.isEmpty()) continue;
      if (bb.getInfrequent() && ir.options.FREQ_FOCUS_EFFORT) continue;
      optimizeBasicBlock(bb);
    }
  }

  /**
   * Perform Local CSE for a basic block in HIR.
   *
   * @param ir the method's ir
   * @param bb the basic block
   */
  private void optimizeBasicBlock(BasicBlock bb) {
    AvExCache cache = new AvExCache(ir.options, true);
    // iterate over all instructions in the basic block
    for (Instruction inst = bb.firstRealInstruction(),
	sentinel = bb.lastInstruction(),
	nextInstr = null; inst != sentinel; inst = nextInstr) {
      nextInstr = inst.nextInstructionInCodeOrder(); // cache before we
      // mutate prev/next links
      // 1. try and replace this instruction according to
      // available expressions in the cache, and update cache
      // accordingly.
      if (isLoadInstruction(inst)) {
	loadHelper(ir, cache, inst);
      } else if (isStoreInstruction(inst)) {
	storeHelper(cache, inst);
      } else if (isExpression(inst)) {
	expressionHelper(ir, cache, inst);
      } else if (isCheck(inst)) {
	checkHelper(ir, cache, inst);
      } else if (isTypeCheck(inst)) {
	typeCheckHelper(ir, cache, (TypeCheck)inst);
      }

      // 2. update the cache according to which expressions this
      // instruction kills
      cache.eliminate(inst);
      // Non-pure CALL instructions and synchronizations KILL all memory locations!
      if (inst.isNonPureCall()) {
	cache.invalidateAllLoads();
      } else if (isSynchronizing(inst)) {
	cache.invalidateAllLoads();
      }
    }
  }

  /**
   * Is a given instruction a CSE-able load?
   */
  public static boolean isLoadInstruction(Instruction s) {
    return s instanceof GetField || s instanceof GetStatic;
  }

  /**
   * Is a given instruction a CSE-able store?
   */
  public static boolean isStoreInstruction(Instruction s) {
    return s instanceof PutField || s instanceof PutStatic;
  }

  /**
   * Does the instruction compute some expression?
   *
   * @param inst the instruction in question
   * @return true or false, as appropriate
   */
  private boolean isExpression(Instruction inst) {
    if (inst instanceof Unary || inst instanceof GuardedUnary ||
	inst instanceof Binary || inst instanceof GuardedBinary ||
	inst instanceof InstanceOfParent)
      return true;

    if (inst instanceof Call)
      return ((Call)inst).isPureCall();

    return false;
  }

  /**
   * Is the given instruction a check instruction?
   *
   * @param inst the instruction in question
   * @return true or false, as appropriate
   */
  private boolean isCheck(Instruction inst) {
    switch (inst.getOpcode()) {
    case Operators.NullCheck:
    case Operators.BoundsCheck:
    case Operators.IntZeroCheck:
    case Operators.LongZeroCheck:
      return true;
    default:
      return false;
    }
  }

  private boolean isTypeCheck(Instruction inst) {
    return inst instanceof TypeCheck;
  }

  /**
   * Process a load instruction
   *
   * @param ir the containing IR object.
   * @param cache the cache of available expressions
   * @param inst the instruction begin processed
   */
  private void loadHelper(IR ir, AvExCache cache, Instruction inst) {
    Operand loc = ((Load)inst).getAddress();
    if (loc.mayBeVolatile()) return; // don't optimize volatile fields

    // look up the expression in the cache
    AvailableExpression ae = cache.find(inst);
    if (ae != null) {
      Operand dest = ((HasResult)inst).getClearResult();
      if (ae.tmp == null) {
	// (1) generate a new temporary, and store in the AE cache
	RegisterOperand newRes = ir.regpool.makeTemp(dest.getType());
	ae.tmp = newRes.getRegister();
	// (2) get the CSE value into newRes
	if (ae.isLoad()) {
	  // the first appearance was a load.
	  // Modify the first load to assign its result to a new temporary
	  // and then insert a move from the new temporary to the old result
	  // after the mutated first load.
	  Operand res = ((HasResult)ae.inst).getClearResult();
	  ((HasResult)ae.inst).setResult(newRes);
	  ae.inst.insertAfter(Move.create(res.getType(), res, newRes.copyD2U()));
	} else {
	  // the first appearance was a store.
	  // Insert a move that assigns the value to newRes before
	  // the store instruction.
	  Operand value;
	  if (ae.inst instanceof PutStatic) {
	    value = ((PutStatic)ae.inst).getValue();
	  } else {
	    value = ((PutField)ae.inst).getValue();
	  }
	  ae.inst.insertBefore(Move.create(newRes.getType(), newRes, value.copy()));
	}
	// (3) replace second load with a move from the new temporary
	inst.replace(Move.create(dest.getType(), dest, newRes.copyD2U()));
      } else {
	// already have a temp. replace the load with a move
	RegisterOperand newRes = new RegisterOperand(ae.tmp, dest.getType());
	inst.replace(Move.create(dest.getType(), dest, newRes));
      }
    } else {
      // did not find a match: insert new entry in cache
      cache.insert(inst);
    }
  }

  /**
   * Process a store instruction
   *
   * @param cache the cache of available expressions
   * @param inst the instruction begin processed
   */
  private void storeHelper(AvExCache cache, Instruction inst) {
    FieldOperand loc = ((HasField)inst).getField();
    if (loc.mayBeVolatile()) return; // don't optimize volatile fields

    // look up the expression in the cache
    AvailableExpression ae = cache.find(inst);
    if (ae == null) {
      // did not find a match: insert new entry in cache
      cache.insert(inst);
    }
  }

  /**
   * Process a unary or binary expression.
   *
   * @param ir the containing IR object
   * @param cache the cache of available expressions
   * @param inst the instruction begin processed
   */
  private void expressionHelper(IR ir, AvExCache cache, Instruction inst) {
    // look up the expression in the cache
    AvailableExpression ae = cache.find(inst);
    if (ae != null) {
      Operand dest = ((HasResult)inst).getClearResult();
      if (ae.tmp == null) {
	// (1) generate a new temporary, and store in the AE cache
	RegisterOperand newRes = ir.regpool.makeTemp(dest.getType());
	ae.tmp = newRes.getRegister();
	// (2) Modify ae.inst to assign its result to the new temporary
	// and then insert a move from the new temporary to the old result
	// of ae.inst after ae.inst.
	Operand res = ((HasResult)ae.inst).getClearResult();
	((HasResult)ae.inst).setResult(newRes);
	ae.inst.insertAfter(Move.create(res.getType(), res, newRes.copyD2U()));
	// (3) replace inst with a move from the new temporary
	inst.replace(Move.create(dest.getType(), dest, newRes.copyD2U()));
      } else {
	// already have a temp. replace inst with a move
	RegisterOperand newRes = new RegisterOperand(ae.tmp, dest.getType());
	inst.replace(Move.create(dest.getType(), dest, newRes));
      }
    } else {
      // did not find a match: insert new entry in cache
      cache.insert(inst);
    }
  }

  /**
   * Process a check instruction
   *
   * @param cache the cache of available expressions
   * @param inst the instruction begin processed
   */
  private void checkHelper(IR ir, AvExCache cache, Instruction inst) {
    // look up the check in the cache
    AvailableExpression ae = cache.find(inst);
    if (ae != null) {
      Operand dest = ((HasGuardResult)inst).getClearGuardResult();
      if (ae.tmp == null) {
	// generate a new temporary, and store in the AE cache
	RegisterOperand newRes = ir.regpool.makeTemp(dest.getType());
	ae.tmp = newRes.getRegister();
	// (2) Modify ae.inst to assign its guard result to the new temporary
	// and then insert a guard move from the new temporary to the
	// old guard result of ae.inst after ae.inst.
	Operand res = ((HasGuardResult)ae.inst).getClearGuardResult();
	((HasGuardResult)ae.inst).setGuardResult(newRes);
	Instruction t = new GuardMove(res, newRes.copyD2U());
	t.copyPosition(ae.inst);
	ae.inst.insertAfter(t);
	// (3) replace inst with a move from the new temporary
	inst.replace(new GuardMove(dest, newRes.copyD2U()));
      } else {
	// already have a temp. replace inst with a guard move
	RegisterOperand newRes = new RegisterOperand(ae.tmp, dest.getType());
	inst.replace(new GuardMove(dest, newRes));
      }
    } else {
      // did not find a match: insert new entry in cache
      cache.insert(inst);
    }
  }

  /**
   * Process a type check instruction
   *
   * @param ir     Unused
   * @param cache  The cache of available expressions.
   * @param inst   The instruction being processed
   */
  private static void typeCheckHelper(IR ir, AvExCache cache, TypeCheck inst) {
    // look up the check in the cache
    AvailableExpression ae = cache.find(inst);
    if (ae != null) {
      // it's a duplicate; blow it away.
      inst.replace(new RefMove(inst.getClearResult(), inst.getClearRef()));
    } else {
      // did not find a match: insert new entry in cache
      cache.insert(inst);
    }
  }

  /**
   * Is this a synchronizing instruction?
   *
   * @param inst the instruction in question
   */
  private static boolean isSynchronizing(Instruction inst) {
    switch (inst.getOpcode()) {
    case Operators.MonitorEnter:
    case Operators.MonitorExit:
    case Operators.ReadCeiling:
    case Operators.WriteFloor:
      return true;
    default:
      return false;
    }
  }

  /**
   * Implements a cache of Available Expressions
   */
  protected static final class AvExCache {
    /** Implementation of the cache */
    private final ArrayList<AvailableExpression> cache = new ArrayList<AvailableExpression>(3);

    private final OptOptions options;
    private final boolean doMemory;

    AvExCache(OptOptions opts, boolean doMem) {
      options = opts;
      doMemory = doMem;
    }

    /**
     * Find and return a matching available expression.
     *
     * @param inst the instruction to match
     * @return the matching AE if found, null otherwise
     */
    public AvailableExpression find(Instruction inst) {
      Operand[] ops = null;
      FieldOperand location = null;
      if (inst instanceof GetField) {
	GetField s = (GetField)inst;
	assert(doMemory);
	ops = new Operand[]{s.getRef()};
	location = s.getField();
      } else if (inst instanceof GetStatic) {
	GetStatic s = (GetStatic)inst;
	assert(doMemory);
	location = s.getField();
      } else if (inst instanceof PutField) {
	PutField s = (PutField)inst;
	assert(doMemory);
	ops = new Operand[]{s.getRef()};
	location = s.getField();
      } else if (inst instanceof PutStatic) {
	PutStatic s = (PutStatic)inst;
	assert(doMemory);
	location = s.getField();
      } else if (inst instanceof Unary) {
	Unary s = (Unary)inst;
	ops = new Operand[]{s.getVal()};
      } else if (inst instanceof GuardedUnary) {
	GuardedUnary s = (GuardedUnary)inst;
	ops = new Operand[]{s.getVal()};
      } else if (inst instanceof Binary) {
	Binary s = (Binary)inst;
	ops = new Operand[]{s.getVal1(), s.getVal2()};
      } else if (inst instanceof GuardedBinary) {
	GuardedBinary s = (GuardedBinary)inst;
	ops = new Operand[]{s.getVal1(), s.getVal2()};
      } else if (inst instanceof Move) {
	Move s = (Move)inst;
	ops = new Operand[]{s.getVal()};
      } else if (inst instanceof NullCheck) {
	NullCheck s = (NullCheck)inst;
	ops = new Operand[]{s.getRef()};
      } else if (inst instanceof ZeroCheck) {
	ZeroCheck s = (ZeroCheck)inst;
	ops = new Operand[]{s.getValue()};
      } else if (inst instanceof BoundsCheck) {
	BoundsCheck s = (BoundsCheck)inst;
	ops = new Operand[]{s.getRef(), s.getIndex()};
      } else if (inst instanceof TypeCheck) {
	TypeCheck s = (TypeCheck)inst;
	ops = new Operand[]{s.getRef(), s.getType()};
      } else if (inst instanceof InstanceOf) {
	InstanceOf s = (InstanceOf)inst;
	ops = new Operand[]{s.getRef(), s.getType()};
      } else if (inst instanceof Call) {
	Call s = (Call)inst;
	int numParams = s.getNumberOf();
	ops = new Operand[numParams+2];
	ops[0] = s.getAddress();
	ops[1] = s.getMethod();
	for (int i=0; i < numParams; i++) {
	  ops[i+2] = s.getParam(i);
	}
      } else {
	throw new Error("Unsupported type " + inst);
      }

      AvailableExpression ae = new AvailableExpression(inst, ops, location, null);
      int index = cache.indexOf(ae);
      if (index == -1) {
	return null;
      }
      return cache.get(index);
    }

    /**
     * Insert a new available expression in the cache
     *
     * @param inst the instruction that defines the AE
     */
    public void insert(Instruction inst) {
      Operand[] ops = null;
      FieldOperand location = null;

      if (inst instanceof GetField) {
	GetField s = (GetField)inst;
	assert(doMemory);
	ops = new Operand[]{s.getRef()};
	location = s.getField();
      } else if (inst instanceof GetStatic) {
	GetStatic s = (GetStatic)inst;
	assert(doMemory);
	location = s.getField();
      } else if (inst instanceof PutField) {
	PutField s = (PutField)inst;
	assert(doMemory);
	ops = new Operand[]{s.getRef()};
	location = s.getField();
      } else if (inst instanceof PutStatic) {
	PutStatic s = (PutStatic)inst;
	assert(doMemory);
	location = s.getField();
      } else if (inst instanceof Unary) {
	Unary s = (Unary)inst;
	ops = new Operand[]{s.getVal()};
      } else if (inst instanceof GuardedUnary) {
	GuardedUnary s = (GuardedUnary)inst;
	ops = new Operand[]{s.getVal()};
      } else if (inst instanceof Binary) {
	Binary s = (Binary)inst;
	ops = new Operand[]{s.getVal1(), s.getVal2()};
      } else if (inst instanceof GuardedBinary) {
	GuardedBinary s = (GuardedBinary)inst;
	ops = new Operand[]{s.getVal1(), s.getVal2()};
      } else if (inst instanceof Move) {
	Move s = (Move)inst;
	ops = new Operand[]{s.getVal()};
      } else if (inst instanceof NullCheck) {
	NullCheck s = (NullCheck)inst;
	ops = new Operand[]{s.getRef()};
      } else if (inst instanceof ZeroCheck) {
	ZeroCheck s = (ZeroCheck)inst;
	ops = new Operand[]{s.getValue()};
      } else if (inst instanceof BoundsCheck) {
	BoundsCheck s = (BoundsCheck)inst;
	ops = new Operand[]{s.getRef(), s.getIndex()};
      } else if (inst instanceof TypeCheck) {
	TypeCheck s = (TypeCheck)inst;
	ops = new Operand[]{s.getRef(), s.getType()};
      } else if (inst instanceof InstanceOf) {
	InstanceOf s = (InstanceOf)inst;
	ops = new Operand[]{s.getRef(), s.getType()};
      } else if (inst instanceof Call) {
	Call s = (Call)inst;
	int numParams = s.getNumberOf();
	ops = new Operand[numParams+2];
	ops[0] = s.getAddress();
	ops[1] = s.getMethod();
	for (int i=0; i < numParams; i++) {
	  ops[i+2] = s.getParam(i);
	}
      } else {
	throw new Error("Unsupported type " + inst);
      }

      AvailableExpression ae = new AvailableExpression(inst, ops, location, null);
      cache.add(ae);
    }

    /**
     * Eliminate all AE tuples that contain a given operand
     *
     * @param op the operand in question
     */
    private void eliminate(RegisterOperand op) {
      int i = 0;
      loop_over_expressions:
	while (i < cache.size()) {
	  AvailableExpression ae = cache.get(i);
	  if (ae.ops != null) {
	    for (Operand opx : ae.ops) {
	      if (opx instanceof RegisterOperand && ((RegisterOperand) opx).getRegister() == op.getRegister()) {
		cache.remove(i);
		continue loop_over_expressions; // don't increment i, since we removed
	      }
	    }
	  }
	  i++;
	}
    }

    /**
     * Eliminate all AE tuples that are killed by a given instruction
     *
     * @param s the store instruction
     */
    public void eliminate(Instruction s) {
      int i = 0;
      // first kill all registers that this instruction defs
      for (OperandEnumeration defs = s.getDefs(); defs.hasMoreElements();) {
	// first KILL any registers this instruction DEFS
	Operand def = defs.next();
	if (def instanceof RegisterOperand) {
	  eliminate((RegisterOperand) def);
	}
      }
      if (doMemory) {
	// eliminate all memory locations killed by stores
	if (LocalCSE.isStoreInstruction(s) || (options.READS_KILL && LocalCSE.isLoadInstruction(s))) {
	  // sLocation holds the location killed by this instruction
	  FieldOperand sLocation = ((HasField)s).getField();
	  // walk through the cache and invalidate any killed locations
	  while (i < cache.size()) {
	    AvailableExpression ae = cache.get(i);
	    if (ae.inst != s) {   // a store instruction doesn't kill itself
	      boolean killIt = false;
	      if (ae.isLoadOrStore()) {
		if ((sLocation == null) && (ae.location == null)) {
		  // !TODO: is this too conservative??
		  killIt = true;
		} else if ((sLocation != null) && (ae.location != null)) {
		  killIt = sLocation.mayBeAliased(ae.location);
		}
	      }
	      if (killIt) {
		cache.remove(i);
		continue;         // don't increment i, since we removed
	      }
	    }
	    i++;
	  }
	}
      }
    }

    /**
     * Eliminate all AE tuples that cache ANY memory location.
     */
    public void invalidateAllLoads() {
      if (!doMemory) return;
      int i = 0;
      while (i < cache.size()) {
	AvailableExpression ae = cache.get(i);
	if (ae.isLoadOrStore()) {
	  cache.remove(i);
	  continue;               // don't increment i, since we removed
	}
	i++;
      }
    }
  }

  /**
   * A tuple to record an Available Expression
   */
  private static final class AvailableExpression {
    /**
     * the instruction which makes this expression available
     */
    final Instruction inst;
     /**
     * operands
     */
    final Operand[] ops;
    /**
     * location operand for memory (load/store) expressions
     */
    final LocationOperand location;
    /**
     * temporary register holding the result of the available
     * expression
     */
    Register tmp;

    /**
     * @param i the instruction which makes this expression available
     * @param ops the operands
     * @param loc location operand for memory (load/store) expressions
     * @param t temporary register holding the result of the available
     * expression
     */
    AvailableExpression(Instruction i, Operand[] ops, FieldOperand loc, Register t) {
      this.inst = i;
      this.ops = ops;
      this.location = loc;
      this.tmp = t;
    }

    /**
     * Two AEs are "equal" iff
     *  <ul>
     *   <li> for unary, binary and ternary expressions:
     *     the operator and the operands match
     *    <li> for loads and stores: if the 2 operands and the location match
     *  </ul>
     */
    public boolean equals(Object o) {
      if (!(o instanceof AvailableExpression)) {
	return false;
      }
      AvailableExpression ae = (AvailableExpression) o;
      if (isLoadOrStore()) {
	if (!ae.isLoadOrStore()) {
	  return false;
	}
	boolean result = location.mayBeAliased(ae.location);
	if (ops == null || ae.ops == null){
	  return result && ops == ae.ops;
	}
	result = result && ops[0].similar(ae.ops[0]);
	if (ops.length > 1) {
	  result = result && ops[1].similar(ae.ops[1]);
	} else {
	  /* ops[1] isn't present, so ae.ops[1] must also not be present */
	  if (ae.ops.length > 1) {
	    return false;
	  }
	}
	return result;
      } else if (isBoundsCheck()) {
	// Augment equality with BC(ref,C1) ==> BC(ref,C2)
	// when C1>0, C2>=0, and C1>C2
	if (inst.getOpcode() != (ae.inst.getOpcode())) {
	  return false;
	}
	if (!ops[0].similar(ae.ops[0])) {
	  return false;
	}
	if (ops[1].similar(ae.ops[1])) {
	  return true;
	}
	if (ops[1] instanceof IntConstantOperand && ae.ops[1] instanceof IntConstantOperand) {
	  int C1 = ((IntConstantOperand) ops[1]).value;
	  int C2 = ((IntConstantOperand) ae.ops[1]).value;
	  return C1 > 0 && C2 >= 0 && C1 > C2;
	} else {
	  return false;
	}
      } else {
	if (inst.getOpcode() != ae.inst.getOpcode()) {
	  return false;
	}
	if (ops.length != ae.ops.length) {
	  return false;
	} else {
	  if (ops.length == 2) {
	    return (ops[0].similar(ae.ops[0]) && ops[1].similar(ae.ops[1])) ||
	    (isCommutative() && ops[0].similar(ae.ops[1]) && ops[1].similar(ae.ops[0]));
	  } else {
	    for (int i=0; i < ops.length; i++) {
	      if (!ops[i].similar(ae.ops[i])) {
		return false;
	      }
	    }
	    return true;
	  }
	}
      }
    }

    /**
     * Does this expression represent the result of a load or store?
     */
    public boolean isLoadOrStore() {
      return inst.isGet() || inst.isPut();
    }

    /**
     * Does this expression represent the result of a load?
     */
    public boolean isLoad() {
      return inst.isGet();
    }

    /**
     * Does this expression represent the result of a store?
     */
    public boolean isStore() {
      return inst.isPut();
    }

    /**
     * Does this expression represent the result of a bounds check?
     */
    private boolean isBoundsCheck() {
      return inst instanceof BoundsCheck;
    }

    /**
     * Is this expression commutative?
     */
    private boolean isCommutative() {
      return inst.isCommutative();
    }
  }
}
