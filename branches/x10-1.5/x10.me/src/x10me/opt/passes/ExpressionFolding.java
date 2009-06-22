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

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;


import x10me.opt.DefUse;
import x10me.opt.ir.*;
import x10me.opt.ir.operand.*;
import x10me.types.*;

/**
 * This class simplifies expressions globally, if in SSA form, or locally within
 * a basic block if not.
 */
public class ExpressionFolding {
  /**
   * Only fold operations when the result of the 1st operation becomes dead
   * after folding
   * TODO: doesn't apply to local folding
   */
  private static final boolean RESTRICT_TO_DEAD_EXPRESSIONS = false;

  /**
   * Fold across uninterruptible regions
   */
  private static final boolean FOLD_OVER_UNINTERRUPTIBLE = false;
  /**
   * Fold operations on ints
   */
  private static final boolean FOLD_INTS = true;
  /**
   * Fold operations on word like things
   */
  private static final boolean FOLD_REFS = true;
  /**
   * Fold operations on longs
   */
  private static final boolean FOLD_LONGS = true;
  /**
   * Fold operations on floats
   */
  private static final boolean FOLD_FLOATS = true;
  /**
   * Fold operations on doubles
   */
  private static final boolean FOLD_DOUBLES = true;

  /**
   * Fold binary SUB operations
   */
  private static final boolean FOLD_SUBS = true;

  /**
   * Fold binary ADD operations
   */
  private static final boolean FOLD_ADDS = true;

  /**
   * Fold binary multiply operations
   */
  private static final boolean FOLD_MULTS = true;

  /**
   * Fold binary divide operations
   */
  private static final boolean FOLD_DIVS = true;

  /**
   * Fold binary shift left operations
   */
  private static final boolean FOLD_SHIFTLS = true;

  /**
   * Fold binary shift right operations
   */
  private static final boolean FOLD_SHIFTRS = true;

  /**
   * Fold binary CMP operations
   */
  private static final boolean FOLD_CMPS = true;

  /**
   * Fold binary XOR operations
   */
  private static final boolean FOLD_XORS = true;

  /**
   * Fold binary OR operations
   */
  private static final boolean FOLD_ORS = true;

  /**
   * Fold binary AND operations
   */
  private static final boolean FOLD_ANDS = true;

  /**
   * Fold unary NEG operations
   */
  private static final boolean FOLD_NEGS = true;

  /**
   * Fold unary NOT operations
   */
  private static final boolean FOLD_NOTS = true;

  /**
   * Fold operations that create a constant on the LHS? This may produce less
   * optimal code on 2 address architectures where the constant would need
   * loading into a register prior to the operation.
   */
  private static final boolean FOLD_CONSTANTS_TO_LHS = true;

  /**
   * Fold IFCMP operations
   */
  private static final boolean FOLD_IFCMPS = true;

  /**
   * Fold COND_MOVE operations
   */
  private static final boolean FOLD_CONDMOVES = true;

  /**
   * Fold xxx_2xxx where the precision is increased then decreased achieving a
   * nop effect
   */
  private static final boolean FOLD_2CONVERSION = true;

  /**
   * Fold ZeroCheck where we're testing whether a value is 0 or not
   */
  private static final boolean FOLD_CHECKS = true;

  /**
   * Perform expression folding on individual basic blocks
   */
  public static boolean performLocal(IR ir) {
    Instruction outer = ir.cfg.entry().firstRealInstruction();
    if (ir.dumpFile.current != null) {
      ir.dumpFile.current.println("Start of expression folding for: " + ir.method.toString());
    }
    boolean didSomething = false;
    // Outer loop: walk over every instruction of basic block looking for candidates
    while (outer != null) {
      Register outerDef = isCandidateExpression(outer, false);
      if (outerDef != null) {
	if (ir.dumpFile.current != null) {
	  ir.dumpFile.current.println("Found outer candidate of: " + outer.toString());
	}
	Instruction inner = outer.nextInstructionInCodeOrder();
	// Inner loop: walk over instructions in block, after outer instruction,
	// checking whether inner and outer could be folded together.
	// if not check for a dependency that means there are potential hazards
	// to stop the search
	loop_over_inner_instructions:
	  while ((inner != null) && (inner instanceof BBend) && !inner.isGCPoint()) {
	    if (!FOLD_OVER_UNINTERRUPTIBLE &&
		((inner instanceof UnintBegin) || (inner instanceof UnintEnd))) {
	      break loop_over_inner_instructions;
	    }
	    Register innerDef = isCandidateExpression(inner, false);
	    // 1. check for true dependence (does inner use outer's def?)
	    if (innerDef != null) {
	      if (ir.dumpFile.current != null) {
		ir.dumpFile.current.println("Found inner candidate of: " + inner.toString());
	      }
	      RegisterOperand use = getUseFromCandidate(inner);
	      if ((use != null) && (use.getRegister() == outerDef)) {
		// Optimization case
		Instruction newInner;
		if (ir.dumpFile.current != null) {
		  ir.dumpFile.current.println("Trying to fold:" + outer.toString());
		  ir.dumpFile.current.println("          with:" + inner.toString());
		}
		newInner = transform(inner, outer);

		if (newInner != null) {
		  if (ir.dumpFile.current != null) {
		    ir.dumpFile.current.println("Replacing:" + inner.toString());
		    ir.dumpFile.current.println("     with:" + newInner.toString());
		  }
		  DefUse.replaceInstructionAndUpdateDU(ir, inner, Instruction.CPOS(inner,newInner));
		  inner = newInner;
		  didSomething = true;
		}
	      }
	    }
	    // 2. check for output dependence (does inner kill outer's def?)
	    if (innerDef == outerDef) {
	      if (ir.dumpFile.current != null) {
		ir.dumpFile.current.println("Stopping search as innerDef == outerDef " + innerDef.toString());
	      }
	      break loop_over_inner_instructions;
	    }
	    if (innerDef == null) {
	      OperandEnumeration defs = inner.getDefs();
	      while(defs.hasMoreElements()) {
		Operand def = defs.next();
		if (def.isRegister()) {
		  Register defReg = def.asRegister().getRegister();
		  if (defReg == outerDef) {
		    if (ir.dumpFile.current != null) {
		      ir.dumpFile.current.println("Stopping search as innerDef == outerDef " + defReg.toString());
		    }
		    break loop_over_inner_instructions;
		  }
		}
	      }
	    }
	    // 3. check for anti dependence (do we define something that outer uses?)
	    if (innerDef != null) {
	      OperandEnumeration uses = outer.getUses();
	      while(uses.hasMoreElements()) {
		Operand use = uses.next();
		if (use.isRegister() && (use.asRegister().getRegister() == innerDef)) {
		  if (ir.dumpFile.current != null) {
		    ir.dumpFile.current.println("Stopping search as anti-dependence " + use.toString());
		  }
		  break loop_over_inner_instructions;
		}
	      }
	    } else {
	      OperandEnumeration defs = inner.getDefs();
	      while(defs.hasMoreElements()) {
		Operand def = defs.next();
		if (def.isRegister()) {
		  OperandEnumeration uses = outer.getUses();
		  while(uses.hasMoreElements()) {
		    Operand use = uses.next();
		    if (use.similar(def)) {
		      if (ir.dumpFile.current != null) {
			ir.dumpFile.current.println("Stopping search as anti-dependence " + use.toString());
		      }
		      break loop_over_inner_instructions;
		    }
		  }
		}
	      }
	    }
	    inner = inner.nextInstructionInCodeOrder();
	  } // loop over inner instructions
      }
      outer = outer.nextInstructionInCodeOrder();
    } // loop over outer instructions
    return didSomething;
  }

  /**
   * Get the register that's used by the candidate instruction
   * @param s the instruction
   * @return register used by candidate or null if this isn't a candidate
   */
  private static RegisterOperand getUseFromCandidate(Instruction s) {
    if (s instanceof Binary) {
      return ((Binary)s).getVal1().asRegister();
    } else if (s instanceof GuardedBinary) {
      return ((GuardedBinary)s).getVal1().asRegister();
    } else if (s instanceof Unary) {
      return ((Unary)s).getVal().asRegister();
    } else if (s instanceof GuardedUnary) {
      return ((GuardedUnary)s).getVal().asRegister();
    } else if (s instanceof BooleanCmp) {
      return ((BooleanCmp)s).getVal1().asRegister();
    } else if (s instanceof IfCmp) {
      return ((IfCmp)s).getVal1().asRegister();
    } else if (s instanceof IfCmp2) {
      return ((IfCmp2)s).getVal1().asRegister();
    } else if (s instanceof CondMove) {
      return ((CondMove)s).getVal1().asRegister();
    } else if (s instanceof ZeroCheck) {
      return ((ZeroCheck)s).getValue().asRegister();
    } else if (s instanceof BoundsCheck) {
      return ((BoundsCheck)s).getRef().asRegister();
    } else if (s instanceof NullCheck) {
      return ((NullCheck)s).getRef().asRegister();
    } else if (s instanceof InstanceOf) {
      return ((InstanceOf)s).getRef().asRegister();


    } else if (s instanceof NewArrayParent || s instanceof New) {
      return null;
    } else {
      throw new Error();
    }
  }

  /**
   * Get the register that's defined by the candidate instruction
   * @param first is this the first instruction?
   * @param s the instruction
   * @return register used by candidate or null if this isn't a candidate
   */
  private static RegisterOperand getDefFromCandidate(Instruction s, boolean first) {
    if (s instanceof Binary) {
      return ((Binary)s).getResult().asRegister();
    } else if (s instanceof GuardedBinary) {
      return ((GuardedBinary)s).getResult().asRegister();
    } else if (s instanceof Unary) {
      return ((Unary)s).getResult().asRegister();
    } else if (s instanceof GuardedUnary) {
      return ((GuardedUnary)s).getResult().asRegister();
    } else if (s instanceof BooleanCmp) {
      return ((BooleanCmp)s).getResult().asRegister();
    } else if (s instanceof IfCmp) {
      if (first) {
	return null;
      } else {
	return ((IfCmp)s).getGuardResult();
      }
    } else if (s instanceof IfCmp2) {
      if (first) {
	return null;
      } else {
	return ((IfCmp2)s).getGuardResult();
      }
    } else if (s instanceof CondMove) {
      if (first) {
	return null;
      } else {
	return ((CondMove)s).getResult().asRegister();
      }
    } else if (s instanceof ZeroCheck) {
      return ((ZeroCheck)s).getGuardResult();
    } else if (s instanceof BoundsCheck) {
      return ((BoundsCheck)s).getGuardResult();
    } else if (s instanceof NullCheck) {
      return ((NullCheck)s).getGuardResult();
    } else if (s instanceof InstanceOf) {
      return ((InstanceOf)s).getResult().asRegister();
    } else if (s instanceof NewArrayParent) {
      if (first) {
	return ((NewArrayParent)s).getResult().asRegister();
      } else {
	return null;
      }
    } else if (s instanceof New) {
      if (first) {
	return ((New)s).getResult().asRegister();
      } else {
	return null;
      }
    } else {
      throw new Error ();
    }
  }
  /**
   * Perform the transformation.
   *
   * If we have, in SSA form,
   *
   * <pre>
   *    x = a op1 c1
   *    y = x op2 c2
   * </pre>
   *
   * where c1 and c2 are constants, replace the def of y by
   *
   * <pre>
   * y = a op1 (c1 op3 c2)
   * </pre>
   *
   * Where op1, op2 and op3 are add, subtract, multiply, and, or, xor and
   * compare. Repeatedly apply transformation until all expressions are folded.
   *
   * <p>
   * PRECONDITIONS: SSA form, register lists computed
   *
   * @param ir
   *          the governing IR
   */
  public static void perform(IR ir) {
    // Create a set of potential computations to fold.
    HashSet<Register> candidates = new HashSet<Register>(20);

    for (Enumeration<Instruction> e = ir.forwardInstrEnumerator(); e.hasMoreElements();) {
      Instruction s = e.nextElement();
      // Check if s is a candidate for expression folding
      Register r = isCandidateExpression(s, true);
      if (r != null) {
	candidates.add(r);
      }
    }

    if (RESTRICT_TO_DEAD_EXPRESSIONS) {
      pruneCandidates(candidates);
    }

    boolean didSomething = true;
    while (didSomething) {
      didSomething = false;

      iterate_over_candidates:
	for (Iterator<Register> it = candidates.iterator(); it.hasNext();) {
	  Register r = it.next();
	  Instruction s = r.getFirstDef();
	  Operand val1 = getUseFromCandidate(s);
	  if (val1 == null) continue; // operator that's not being optimized
	  if (ir.dumpFile.current != null) {
	    ir.dumpFile.current.println("Found candidate instruction: " + s.toString());
	  }
	  Instruction def = val1.asRegister().getRegister().getFirstDef();
	  // filter out moves to get the real defining instruction
	  while (def instanceof Move) {
	    Operand op = ((Move)def).getVal();
	    if (op.isRegister()) {
	      def = op.asRegister().getRegister().getFirstDef();
	    } else {
	      // The non-constant operand of the candidate expression is the
	      // result of moving a constant. Remove as a candidate and leave
	      // for constant propagation and simplification.
	      it.remove();
	      continue iterate_over_candidates;
	    }
	  }
	  if (candidates.contains(val1.asRegister().getRegister())) {
	    if (ir.dumpFile.current != null) {
	      ir.dumpFile.current.println(" Found candidate definition: " + def.toString());
	    }
	    // check if the defining instruction has not mutated yet
	    if (isCandidateExpression(def, true) == null) {
	      if (ir.dumpFile.current != null) {
		ir.dumpFile.current.println(" Ignoring definition that is no longer a candidate");
	      }
	      continue;
	    }

	    Instruction newS = transform(s, def);
	    if (newS != null) {
	      if (ir.dumpFile.current != null) {
		ir.dumpFile.current.println(" Replacing: " + s.toString() + "\n with:" + newS.toString());
	      }
	      // check if this expression is still an optimisation candidate
	      if (isCandidateExpression(newS, true) == null) {
		it.remove();
	      }
	      DefUse.replaceInstructionAndUpdateDU(ir, s, Instruction.CPOS(s,newS));
	      didSomething = true;
	    }
	  }
	}
    }
  }

  /**
   * Prune the candidate set; restrict candidates to only allow transformations
   * that result in dead code to be eliminated
   */
  private static void pruneCandidates(HashSet<Register> candidates) {
    for (Iterator<Register> i = candidates.iterator(); i.hasNext();) {
      Register r = i.next();
      Instruction s = r.getFirstDef();
      Operand val1 = getUseFromCandidate(s);
      if (val1 == null) continue; // operator that's not being optimized


      assert val1.isRegister(): "Error with val1 of " + s;

      Register v1 = val1.asRegister().getRegister();
      if (candidates.contains(v1)) {
	Enumeration<RegisterOperand> uses = DefUse.uses(v1);
	while (uses.hasMoreElements()) {
	  RegisterOperand op = uses.nextElement();
	  Instruction u = op.instruction;
	  if ((isCandidateExpression(u, true) == null) && !(u instanceof Move)) {
	    i.remove();
	    break;
	  }
	}
      }
    }
  }

  /**
   * Perform the transformation on the instruction
   *
   * @param s
   *          the instruction to transform of the form y = x op c1
   * @param def
   *          the definition of x, the defining instruction is of the form x = a
   *          op c2
   * @return the new instruction to replace s;
   */
  private static Instruction transform(Instruction s, Instruction def) {
    // x = a op1 c1  <-- def
    // y = x op2 c2  <-- s
    final RegisterOperand a = getUseFromCandidate(def);
    final RegisterOperand x = getDefFromCandidate(def, true);
    if (x == null) {
      return null;
    }
    final RegisterOperand y = getDefFromCandidate(s, false);
    if (y == null) {
      return null;
    }

    assert x.similar(getUseFromCandidate(s)): 
      "x not similar to x2 " + x + " : " + getUseFromCandidate(s);


    switch (s.getOpcode()) {
    // Foldable operators
    case Operators.IntAdd: {
      if (FOLD_INTS && FOLD_ADDS) {
	int c2 = getIntValue(((Binary)s).getVal2());
	if (def instanceof IntAdd) {
	  int c1 = getIntValue(((Binary)def).getVal2());
	  // x = a + c1; y = x + c2
	  return new IntAdd(y.copyRO(), a.copyRO(), new IntConstantOperand(c1 + c2));
	} else if (def instanceof IntSub) {
	  int c1 = getIntValue(((Binary)def).getVal2());
	  // x = a - c1; y = x + c2
	  return new IntAdd(y.copyRO(), a.copyRO(), new IntConstantOperand(c2 - c1));
	} else if (def instanceof IntNeg && FOLD_CONSTANTS_TO_LHS) {
	  // x = -a; y = x + c2;
	  return new IntSub(y.copyRO(), new IntConstantOperand(c2), a.copyRO());
	}
      }
      return null;
    }
    case Operators.RefAdd: {
      if (FOLD_REFS && FOLD_ADDS) {
	Address c2 = getAddressValue(((Binary)s).getVal2());
	if (def instanceof RefAdd) {
	  Address c1 = getAddressValue(((Binary)def).getVal2());
	  // x = a + c1; y = x + c2
	  return new RefAdd(y.copyRO(), a.copyRO(), new AddressConstantOperand(c1.toWord().plus(c2.toWord()).toAddress()));
	} else if (def instanceof RefSub) {
	  Address c1 = getAddressValue(((Binary)def).getVal2());
	  // x = a - c1; y = x + c2
	  return new RefAdd(y.copyRO(), a.copyRO(), new AddressConstantOperand(c2.toWord().minus(c1.toWord()).toAddress()));
	} else if (def instanceof RefNeg && FOLD_CONSTANTS_TO_LHS) {
	  // x = -a; y = x + c2;
	  return new RefSub(y.copyRO(), new AddressConstantOperand(c2), a.copyRO());
	}
      }
      return null;
    }
    case Operators.LongAdd: {
      if (FOLD_LONGS && FOLD_ADDS) {
	long c2 = getLongValue(((Binary)s).getVal2());
	if (def instanceof LongAdd) {
	  long c1 = getLongValue(((Binary)def).getVal2());
	  // x = a + c1; y = x + c2
	  return new LongAdd(y.copyRO(), a.copyRO(), new LongConstantOperand(c1 + c2));
	} else if (def instanceof LongSub) {
	  long c1 = getLongValue(((Binary)def).getVal2());
	  // x = a - c1; y = x + c2
	  return new LongAdd(y.copyRO(), a.copyRO(), new LongConstantOperand(c2 - c1));
	} else if (def instanceof LongNeg && FOLD_CONSTANTS_TO_LHS) {
	  // x = -a; y = x + c2;
	  return new LongSub(y.copyRO(), new LongConstantOperand(c2), a.copyRO());
	}
      }
      return null;
    }
    case Operators.FloatAdd: {
      if (FOLD_FLOATS && FOLD_ADDS) {
	float c2 = getFloatValue(((Binary)s).getVal2());
	if (def instanceof FloatAdd) {
	  float c1 = getFloatValue(((Binary)def).getVal2());
	  // x = a + c1; y = x + c2
	  return new FloatAdd(y.copyRO(), a.copyRO(), new FloatConstantOperand(c1 + c2));
	} else if (def instanceof FloatSub) {
	  float c1 = getFloatValue(((Binary)def).getVal2());
	  // x = a - c1; y = x + c2
	  return new FloatAdd(y.copyRO(), a.copyRO(), new FloatConstantOperand(c2 - c1));
	} else if (def instanceof FloatNeg && FOLD_CONSTANTS_TO_LHS) {
	  // x = -a; y = x + c2;
	  return new FloatSub(y.copyRO(), new FloatConstantOperand(c2), a.copyRO());
	}
      }
      return null;
    }
    case Operators.DoubleAdd: {
      if (FOLD_DOUBLES && FOLD_ADDS) {
	double c2 = getDoubleValue(((Binary)s).getVal2());
	if (def instanceof DoubleAdd) {
	  double c1 = getDoubleValue(((Binary)def).getVal2());
	  // x = a + c1; y = x + c2
	  return new DoubleAdd(y.copyRO(), a.copyRO(), new DoubleConstantOperand(c1 + c2));
	} else if (def instanceof DoubleSub) {
	  double c1 = getDoubleValue(((Binary)def).getVal2());
	  // x = a - c1; y = x + c2
	  return new DoubleAdd(y.copyRO(), a.copyRO(), new DoubleConstantOperand(c2 - c1));
	} else if (def instanceof DoubleNeg && FOLD_CONSTANTS_TO_LHS) {
	  // x = -a; y = x + c2;
	  return new DoubleSub(y.copyRO(), new DoubleConstantOperand(c2), a.copyRO());
	}
      }
      return null;
    }
    case Operators.IntSub: {
      if (FOLD_INTS && FOLD_SUBS) {
	int c2 = getIntValue(((Binary)s).getVal2());
	if (def instanceof IntAdd) {
	  int c1 = getIntValue(((Binary)def).getVal2());
	  // x = a + c1; y = x - c2
	  return new IntAdd(y.copyRO(), a.copyRO(), new IntConstantOperand(c1 - c2));
	} else if (def instanceof IntSub) {
	  int c1 = getIntValue(((Binary)def).getVal2());
	  // x = a - c1; y = x - c2
	  return new IntAdd(y.copyRO(), a.copyRO(), new IntConstantOperand(-c1 - c2));
	} else if (def instanceof IntNeg && FOLD_CONSTANTS_TO_LHS) {
	  // x = -a; y = x - c2;
	  return new IntSub(y.copyRO(), new IntConstantOperand(-c2), a.copyRO());
	}
      }
      return null;
    }
    case Operators.RefSub: {
      if (FOLD_REFS && FOLD_SUBS) {
	Address c2 = getAddressValue(((Binary)s).getVal2());
	if (def instanceof RefAdd) {
	  Address c1 = getAddressValue(((Binary)def).getVal2());
	  // x = a + c1; y = x - c2
	  return new RefAdd(y.copyRO(), a.copyRO(), new AddressConstantOperand(c1.toWord().minus(c2.toWord()).toAddress()));
	} else if (def instanceof RefSub) {
	  Address c1 = getAddressValue(((Binary)def).getVal2());
	  // x = a - c1; y = x - c2
	  return new RefAdd(
	      y.copyRO(),
	      a.copyRO(),
	      new AddressConstantOperand(Word.zero().minus(c1.toWord()).minus(c2.toWord()).toAddress()));
	} else if (def instanceof RefNeg && FOLD_CONSTANTS_TO_LHS) {
	  // x = -a; y = x - c2;
	  return new RefSub(y.copyRO(), new AddressConstantOperand(Word.zero().minus(c2.toWord()).toAddress()), a.copyRO());
	}
      }
      return null;
    }
    case Operators.LongSub: {
      if (FOLD_LONGS && FOLD_SUBS) {
	long c2 = getLongValue(((Binary)s).getVal2());
	if (def instanceof LongAdd) {
	  long c1 = getLongValue(((Binary)def).getVal2());
	  // x = a + c1; y = x - c2
	  return new LongAdd(y.copyRO(), a.copyRO(), new LongConstantOperand(c1 - c2));
	} else if (def instanceof LongSub) {
	  long c1 = getLongValue(((Binary)def).getVal2());
	  // x = a - c1; y = x - c2
	  return new LongAdd(y.copyRO(), a.copyRO(), new LongConstantOperand(-c1 - c2));
	} else if (def instanceof LongNeg && FOLD_CONSTANTS_TO_LHS) {
	  // x = -a; y = x - c2;
	  return new LongSub(y.copyRO(), new LongConstantOperand(-c2), a.copyRO());
	}
      }
      return null;
    }
    case Operators.FloatSub: {
      if (FOLD_FLOATS && FOLD_SUBS) {
	float c2 = getFloatValue(((Binary)s).getVal2());
	if (def instanceof FloatAdd) {
	  float c1 = getFloatValue(((Binary)def).getVal2());
	  // x = a + c1; y = x - c2
	  return new FloatAdd(y.copyRO(), a.copyRO(), new FloatConstantOperand(c1 - c2));
	} else if (def instanceof FloatSub) {
	  float c1 = getFloatValue(((Binary)def).getVal2());
	  // x = a - c1; y = x - c2
	  return new FloatAdd(y.copyRO(), a.copyRO(), new FloatConstantOperand(-c1 - c2));
	} else if (def instanceof FloatNeg && FOLD_CONSTANTS_TO_LHS) {
	  // x = -a; y = x - c2;
	  return new FloatSub(y.copyRO(), new FloatConstantOperand(-c2), a.copyRO());
	}
      }
      return null;
    }
    case Operators.DoubleSub: {
      if (FOLD_DOUBLES && FOLD_SUBS) {
	double c2 = getDoubleValue(((Binary)s).getVal2());
	if (def instanceof FloatAdd) {
	  double c1 = getDoubleValue(((Binary)def).getVal2());
	  // x = a + c1; y = x - c2
	  return new DoubleAdd(y.copyRO(), a.copyRO(), new DoubleConstantOperand(c1 - c2));
	} else if (def instanceof DoubleSub) {
	  double c1 = getDoubleValue(((Binary)def).getVal2());
	  // x = a - c1; y = x + c2
	  return new DoubleAdd(y.copyRO(), a.copyRO(), new DoubleConstantOperand(-c1 - c2));
	} else if (def instanceof DoubleNeg && FOLD_CONSTANTS_TO_LHS) {
	  // x = -a; y = x - c2;
	  return new DoubleSub(y.copyRO(), new DoubleConstantOperand(-c2), a.copyRO());
	}
      }
      return null;
    }
    case Operators.IntMul: {
      if (FOLD_INTS && FOLD_MULTS) {
	int c2 = getIntValue(((Binary)s).getVal2());
	if (def instanceof IntMul) {
	  int c1 = getIntValue(((Binary)def).getVal2());
	  // x = a * c1; y = x * c2
	  return new IntMul(y.copyRO(), a.copyRO(), new IntConstantOperand(c1 * c2));
	} else if (def instanceof IntNeg) {
	  // x = -a; y = x * c2;
	  return new IntMul(y.copyRO(), a.copyRO(), new IntConstantOperand(-c2));
	}
      }
      return null;
    }
    case Operators.LongMul: {
      if (FOLD_LONGS && FOLD_MULTS) {
	long c2 = getLongValue(((Binary)s).getVal2());
	if (def instanceof LongMul) {
	  long c1 = getLongValue(((Binary)def).getVal2());
	  // x = a * c1; y = x * c2
	  return new LongMul(y.copyRO(), a.copyRO(), new LongConstantOperand(c1 * c2));
	} else if (def instanceof LongNeg) {
	  // x = -a; y = x * c2;
	  return new LongMul(y.copyRO(), a.copyRO(), new LongConstantOperand(-c2));
	}
      }
      return null;
    }
    case Operators.FloatMul: {
      if (FOLD_FLOATS && FOLD_MULTS) {
	float c2 = getFloatValue(((Binary)s).getVal2());
	if (def instanceof FloatMul) {
	  float c1 = getFloatValue(((Binary)def).getVal2());
	  // x = a * c1; y = x * c2
	  return new FloatMul(y.copyRO(), a.copyRO(), new FloatConstantOperand(c1 * c2));
	} else if (def instanceof FloatNeg) {
	  // x = -a; y = x * c2;
	  return new FloatMul(y.copyRO(), a.copyRO(), new FloatConstantOperand(-c2));
	}
      }
      return null;
    }
    case Operators.DoubleMul: {
      if (FOLD_DOUBLES && FOLD_MULTS) {
	double c2 = getDoubleValue(((Binary)s).getVal2());
	if (def instanceof DoubleMul) {
	  double c1 = getDoubleValue(((Binary)def).getVal2());
	  // x = a * c1; y = x * c2
	  return new DoubleMul(y.copyRO(), a.copyRO(), new DoubleConstantOperand(c1 * c2));
	} else if (def instanceof DoubleNeg) {
	  // x = -a; y = x * c2;
	  return new DoubleMul(y.copyRO(), a.copyRO(), new DoubleConstantOperand(-c2));
	}
      }
      return null;
    }
    case Operators.IntDiv: {
      if (FOLD_INTS && FOLD_DIVS) {
	int c2 = getIntValue(((GuardedBinary)s).getVal2());
	if (def instanceof IntDiv) {
	  int c1 = getIntValue(((GuardedBinary)def).getVal2());
	  Operand guard = ((GuardedBinary)def).getGuard();
	  // x = a / c1; y = x / c2
	  return new IntDiv(y.copyRO(), a.copyRO(), new IntConstantOperand(c1 * c2), guard);
	} else if (def instanceof IntNeg) {
	  Operand guard = ((GuardedBinary)s).getGuard();
	  // x = -a; y = x / c2;
	  return new IntDiv(y.copyRO(), a.copyRO(), new IntConstantOperand(-c2), guard);
	}
      }
      return null;
    }
    case Operators.LongDiv: {
      if (FOLD_LONGS && FOLD_DIVS) {
	long c2 = getLongValue(((GuardedBinary)s).getVal2());
	if (def instanceof LongDiv) {
	  long c1 = getLongValue(((GuardedBinary)def).getVal2());
	  Operand guard = ((GuardedBinary)def).getGuard();
	  // x = a / c1; y = x / c2
	  return new LongDiv(y.copyRO(), a.copyRO(), new LongConstantOperand(c1 * c2), guard);
	} else if (def instanceof LongNeg) {
	  Operand guard = ((GuardedBinary)s).getGuard();
	  // x = -a; y = x / c2;
	  return new LongDiv(y.copyRO(), a.copyRO(), new LongConstantOperand(-c2), guard);
	}
      }
      return null;
    }
    case Operators.FloatDiv: {
      if (FOLD_FLOATS && FOLD_DIVS) {
	float c2 = getFloatValue(((Binary)s).getVal2());
	if (def instanceof FloatDiv) {
	  float c1 = getFloatValue(((Binary)def).getVal2());
	  // x = a / c1; y = x / c2
	  return new FloatDiv(y.copyRO(), a.copyRO(), new FloatConstantOperand(c1 * c2));
	} else if (def instanceof FloatNeg) {
	  // x = -a; y = x / c2;
	  return new FloatDiv(y.copyRO(), a.copyRO(), new FloatConstantOperand(-c2));
	}
      }
      return null;
    }
    case Operators.DoubleDiv: {
      if (FOLD_DOUBLES && FOLD_DIVS) {
	double c2 = getDoubleValue(((Binary)s).getVal2());
	if (def instanceof DoubleDiv) {
	  double c1 = getDoubleValue(((Binary)def).getVal2());
	  // x = a / c1; y = x / c2
	  return new DoubleDiv(y.copyRO(), a.copyRO(), new DoubleConstantOperand(c1 * c2));
	} else if (def instanceof DoubleNeg) {
	  // x = -a; y = x / c2;
	  return new DoubleDiv(y.copyRO(), a.copyRO(), new DoubleConstantOperand(-c2));
	}
      }
      return null;
    }
    case Operators.IntShl: {
      if (FOLD_INTS && FOLD_SHIFTLS) {
	int c2 = getIntValue(((Binary)s).getVal2());
	if (def instanceof IntShl) {
	  int c1 = getIntValue(((Binary)def).getVal2());
	  // x = a << c1; y = x << c2
	  return new IntShl(y.copyRO(), a.copyRO(), new IntConstantOperand(c1 + c2));
	} else if ((def instanceof IntShr) || (def instanceof IntUshr)) {
	  int c1 = getIntValue(((Binary)def).getVal2());
	  if (c1 == c2) {
	    // x = a >> c1; y = x << c1
	    return new IntAnd(y.copyRO(), a.copyRO(), new IntConstantOperand(-1 << c1));
	  }
	} else if (def instanceof IntAnd) {
	  int c1 = getIntValue(((Binary)def).getVal2());
	  // x = a & c1; y = << c2
	  if ((c1 << c2) == (-1 << c2)) {
	    // the first mask is redundant
	    return new IntShl(y.copyRO(), a.copyRO(), new IntConstantOperand(c2));
	  }
	} else if ((def instanceof IntOr)||(def instanceof IntXor)) {
	  int c1 = getIntValue(((Binary)def).getVal2());
	  // x = a | c1; y = << c2
	  if ((c1 << c2) == 0) {
	    // the first mask is redundant
	    return new IntShl(y.copyRO(), a.copyRO(), new IntConstantOperand(c2));
	  }
	}
      }
      return null;
    }
    case Operators.RefShl: {
      if (FOLD_REFS && FOLD_SHIFTLS) {
	int c2 = getIntValue(((Binary)s).getVal2());
	if (def instanceof RefShl) {
	  int c1 = getIntValue(((Binary)def).getVal2());
	  // x = a << c1; y = x << c2
	  return new RefShl(y.copyRO(), a.copyRO(), new IntConstantOperand(c1 + c2));
	} else if ((def instanceof RefShr) || (def instanceof RefUshr)) {
	  int c1 = getIntValue(((Binary)def).getVal2());
	  if (c1 == c2) {
	    // x = a >> c1; y = x << c1
	    return new RefAnd(
		y.copyRO(),
		a.copyRO(),
		new AddressConstantOperand(Word.zero().minus(Word.one()).lsh(c1).toAddress()));
	  }
	} else if (def instanceof RefAnd) {
	  Address c1 = getAddressValue(((Binary)def).getVal2());
	  // x = a & c1; y = x << c2
	  if (c1.toWord().lsh(c2).EQ(Word.fromIntSignExtend(-1).lsh(c2))) {
	    // the first mask is redundant
	    return new RefShl(y.copyRO(), a.copyRO(), new IntConstantOperand(c2));
	  }
	} else if ((def instanceof RefOr)||(def instanceof RefXor)) {
	  Address c1 = getAddressValue(((Binary)def).getVal2());
	  // x = a | c1; y = x << c2
	  if (c1.toWord().lsh(c2).EQ(Word.zero())) {
	    // the first mask is redundant
	    return new RefShl(y.copyRO(), a.copyRO(), new IntConstantOperand(c2));
	  }
	}
      }
      return null;
    }
    case Operators.LongShl: {
      if (FOLD_LONGS && FOLD_SHIFTLS) {
	int c2 = getIntValue(((Binary)s).getVal2());
	if (def instanceof LongShl) {
	  int c1 = getIntValue(((Binary)def).getVal2());
	  // x = a << c1; y = x << c2
	  return new LongShl(y.copyRO(), a.copyRO(), new IntConstantOperand(c1 + c2));
	} else if ((def instanceof LongShr) || (def instanceof LongUshr)) {
	  int c1 = getIntValue(((Binary)def).getVal2());
	  if (c1 == c2) {
	    // x = a >> c1; y = x << c1
	    return new LongAnd(y.copyRO(), a.copyRO(), new LongConstantOperand(-1L << c1));
	  }
	} else if (def instanceof LongAnd) {
	  long c1 = getLongValue(((Binary)def).getVal2());
	  // x = a & c1; y = << c2
	  if ((c1 << c2) == (-1L << c2)) {
	    // the first mask is redundant
	    return new LongShl(y.copyRO(), a.copyRO(), new IntConstantOperand(c2));
	  }
	} else if ((def instanceof LongOr)||(def instanceof LongXor)) {
	  long c1 = getLongValue(((Binary)def).getVal2());
	  // x = a | c1; y = << c2
	  if ((c1 << c2) == 0L) {
	    // the first mask is redundant
	    return new LongShl(y.copyRO(), a.copyRO(), new IntConstantOperand(c2));
	  }
	}
      }
      return null;
    }
    case Operators.IntShr: {
      if (FOLD_INTS && FOLD_SHIFTRS) {
	int c2 = getIntValue(((Binary)s).getVal2());
	if (def instanceof IntShr) {
	  int c1 = getIntValue(((Binary)def).getVal2());
	  // x = a >> c1; y = x >> c2
	  return new IntShr(y.copyRO(), a.copyRO(), new IntConstantOperand(c1 + c2));
	} else if (def instanceof IntShl) {
	  int c1 = getIntValue(((Binary)def).getVal2());
	  if (c1 == c2) {
	    if (c1 == 24) {
	      // x = a << 24; y = x >> 24
	      return new Int2byte(y.copyRO(), a.copyRO());
	    } else if (c1 == 16) {
	      // x = a << 16; y = x >> 16
	      return new Int2short(y.copyRO(), a.copyRO());
	    }
	  }
	} else if (def instanceof IntAnd) {
	  int c1 = getIntValue(((Binary)def).getVal2());
	  // x = a & c1; y = >> c2
	  if ((c1 >> c2) == -1) {
	    // the first mask is redundant
	    return new IntShr(y.copyRO(), a.copyRO(), new IntConstantOperand(c2));
	  }
	} else if ((def instanceof IntOr)||(def instanceof IntXor)) {
	  int c1 = getIntValue(((Binary)def).getVal2());
	  // x = a | c1; y = >> c2
	  if ((c1 >>> c2) == 0) {
	    // the first mask is redundant
	    return new IntShr(y.copyRO(), a.copyRO(), new IntConstantOperand(c2));
	  }
	}
      }
      return null;
    }
    case Operators.RefShr: {
      if (FOLD_REFS && FOLD_SHIFTRS) {
	int c2 = getIntValue(((Binary)s).getVal2());
	if (def instanceof RefShr) {
	  int c1 = getIntValue(((Binary)def).getVal2());
	  // x = a >> c1; y = x >> c2
	  return new RefShr(y.copyRO(), a.copyRO(), new IntConstantOperand(c1 + c2));
	} else if (def instanceof RefAnd) {
	  Address c1 = getAddressValue(((Binary)def).getVal2());
	  // x = a & c1; y = x >> c2
	  if (c1.toWord().rsha(c2).EQ(Word.zero().minus(Word.one()))) {
	    // the first mask is redundant
	    return new RefShr(y.copyRO(), a.copyRO(), new IntConstantOperand(c2));
	  }
	} else if ((def instanceof RefOr)||(def instanceof RefXor)) {
	  Address c1 = getAddressValue(((Binary)def).getVal2());
	  // x = a | c1; y = x >> c2
	  if (c1.toWord().rshl(c2).EQ(Word.zero())) {
	    // the first mask is redundant
	    return new RefShr(y.copyRO(), a.copyRO(), new IntConstantOperand(c2));
	  }
	}
      }
      return null;
    }
    case Operators.LongShr: {
      if (FOLD_LONGS && FOLD_SHIFTRS) {
	int c2 = getIntValue(((Binary)s).getVal2());
	if (def instanceof LongShr) {
	  int c1 = getIntValue(((Binary)def).getVal2());
	  // x = a >> c1; y = x >> c2
	  return new LongShr(y.copyRO(), a.copyRO(), new IntConstantOperand(c1 + c2));
	} else if (def instanceof LongAnd) {
	  long c1 = getLongValue(((Binary)def).getVal2());
	  // x = a & c1; y = >> c2
	  if ((c1 >> c2) == -1L) {
	    // the first mask is redundant
	    return new LongShr(y.copyRO(), a.copyRO(), new IntConstantOperand(c2));
	  }
	} else if ((def instanceof LongOr)||(def instanceof LongXor)) {
	  long c1 = getLongValue(((Binary)def).getVal2());
	  // x = a & c1; y = >> c2
	  if ((c1 >>> c2) == 0L) {
	    // the first mask is redundant
	    return new LongShr(y.copyRO(), a.copyRO(), new IntConstantOperand(c2));
	  }
	}
      }
      return null;
    }
    case Operators.IntUshr: {
      if (FOLD_INTS && FOLD_SHIFTRS) {
	int c2 = getIntValue(((Binary)s).getVal2());
	if (def instanceof IntUshr) {
	  int c1 = getIntValue(((Binary)def).getVal2());
	  // x = a >>> c1; y = x >>> c2
	  return new IntUshr(y.copyRO(), a.copyRO(), new IntConstantOperand(c1 + c2));
	} else if (def instanceof IntShl) {
	  int c1 = getIntValue(((Binary)def).getVal2());
	  if (c1 == c2) {
	    // x = a << c1; y = x >>> c1
	    return new IntAnd(y.copyRO(), a.copyRO(), new IntConstantOperand(-1 >>> c1));
	  }
	} else if (def instanceof IntAnd) {
	  int c1 = getIntValue(((Binary)def).getVal2());
	  // x = a & c1; y = >>> c2
	  if ((c1 >> c2) == -1L) {
	    // the first mask is redundant
	    return new IntUshr(y.copyRO(), a.copyRO(), new IntConstantOperand(c2));
	  }
	} else if ((def instanceof IntOr)||(def instanceof IntXor)) {
	  int c1 = getIntValue(((Binary)def).getVal2());
	  // x = a | c1; y = >>> c2
	  if ((c1 >>> c2) == 0) {
	    // the first mask is redundant
	    return new IntUshr(y.copyRO(), a.copyRO(), new IntConstantOperand(c2));
	  }
	}
      }
      return null;
    }
    case Operators.RefUshr: {
      if (FOLD_REFS && FOLD_SHIFTRS) {
	int c2 = getIntValue(((Binary)s).getVal2());
	if (def instanceof RefUshr) {
	  int c1 = getIntValue(((Binary)def).getVal2());
	  // x = a >>> c1; y = x >>> c2
	  return new RefUshr(y.copyRO(), a.copyRO(), new IntConstantOperand(c1 + c2));
	} else if (def instanceof RefShl) {
	  int c1 = getIntValue(((Binary)def).getVal2());
	  if (c1 == c2) {
	    // x = a << c1; y = x >>> c1
	    return new RefAnd(
		y.copyRO(),
		a.copyRO(),
		new AddressConstantOperand(Word.zero().minus(Word.one()).rshl(c1).toAddress()));
	  }
	} else if (def instanceof RefAnd) { //IAN!!!
	  Address c1 = getAddressValue(((Binary)def).getVal2());
	  // x = a & c1; y = x >>> c2
	  if (c1.toWord().rsha(c2).EQ(Word.zero().minus(Word.one()))) {
	    // the first mask is redundant
	    return new RefUshr(y.copyRO(), a.copyRO(), new IntConstantOperand(c2));
	  }
	} else if (false) { //(def instanceof RefOr)||(def instanceof RefXor)) {
	  Address c1 = getAddressValue(((Binary)def).getVal2());
	  // x = a | c1; y = x >>> c2
	  if (c1.toWord().rshl(c2).EQ(Word.zero())) {
	    // the first mask is redundant
	    return new RefUshr(y.copyRO(), a.copyRO(), new IntConstantOperand(c2));
	  }
	}
      }
      return null;
    }
    case Operators.LongUshr: {
      if (FOLD_LONGS && FOLD_SHIFTRS) {
	int c2 = getIntValue(((Binary)s).getVal2());
	if (def instanceof LongUshr) {
	  int c1 = getIntValue(((Binary)def).getVal2());
	  // x = a >>> c1; y = x >>> c2
	  return new LongUshr(y.copyRO(), a.copyRO(), new IntConstantOperand(c1 + c2));
	} else if (def instanceof LongShl) {
	  int c1 = getIntValue(((Binary)def).getVal2());
	  if (c1 == c2) {
	    // x = a << c1; y = x >>> c1
	    return new LongAnd(y.copyRO(), a.copyRO(), new LongConstantOperand(-1L >>> c1));
	  }
	} else if (def instanceof LongAnd) {
	  long c1 = getLongValue(((Binary)def).getVal2());
	  // x = a & c1; y = >>> c2
	  if ((c1 >> c2) == -1L) {
	    // the first mask is redundant
	    return new LongUshr(y.copyRO(), a.copyRO(), new IntConstantOperand(c2));
	  }
	} else if ((def instanceof LongOr)||(def instanceof LongXor)) {
	  long c1 = getLongValue(((Binary)def).getVal2());
	  // x = a & c1; y = >>> c2
	  if ((c1 >>> c2) == 0L) {
	    // the first mask is redundant
	    return new LongUshr(y.copyRO(), a.copyRO(), new IntConstantOperand(c2));
	  }
	}
      }
      return null;
    }
    case Operators.IntAnd: {
      if (FOLD_INTS && FOLD_ANDS) {
	int c2 = getIntValue(((Binary)s).getVal2());
	if (def instanceof IntAnd) {
	  int c1 = getIntValue(((Binary)def).getVal2());
	  // x = a & c1; y = x & c2
	  return new IntAnd(y.copyRO(), a.copyRO(), new IntConstantOperand(c1 & c2));
	} else if (def instanceof IntOr) {
	  int c1 = getIntValue(((Binary)def).getVal2());
	  // x = a | c1; y = x & c2
	  if ((c1 & c2) == 0) {
	    return new IntAnd(y.copyRO(), a.copyRO(), new IntConstantOperand(c2));
	  }
	} else if (def instanceof IntXor) {
	  int c1 = getIntValue(((Binary)def).getVal2());
	  // x = a ^ c1; y = x & c2
	  if ((c1 & c2) == 0) {
	    return new IntAnd(y.copyRO(), a.copyRO(), new IntConstantOperand(c2));
	  }
	} else if (def instanceof IntShr) {
	  int c1 = getIntValue(((Binary)def).getVal2());
	  // x = a >> c1; y = x & c2
	  if ((-1 >>> c1) == c2) {
	    // turn arithmetic shifts into logical shifts if possible
	    return new IntUshr(y.copyRO(), a.copyRO(), new IntConstantOperand(c1));
	  }
	} else if (def instanceof IntShl) {
	  int c1 = getIntValue(((Binary)def).getVal2());
	  // x = a << c1; y = x & c2
	  if (((-1 << c1) & c2) == (-1 << c1)) {
	    // does the mask zero bits already cleared by the shift?
	    return new IntShl(y.copyRO(), a.copyRO(), new IntConstantOperand(c1));
	  }
	} else if (def instanceof IntUshr) {
	  int c1 = getIntValue(((Binary)def).getVal2());
	  // x = a >>> c1; y = x & c2
	  if (((-1 >>> c1) & c2) == (-1 >>> c1)) {
	    // does the mask zero bits already cleared by the shift?
	    return new IntUshr(y.copyRO(), a.copyRO(), new IntConstantOperand(c1));
	  }
	}
      }
      return null;
    }
    case Operators.RefAnd: {
      if (FOLD_REFS && FOLD_ANDS) {
	Address c2 = getAddressValue(((Binary)s).getVal2());
	if (def instanceof RefAnd) {
	  Address c1 = getAddressValue(((Binary)def).getVal2());
	  // x = a & c1; y = x & c2
	  return new RefAnd(y.copyRO(), a.copyRO(), new AddressConstantOperand(c1.toWord().and(c2.toWord()).toAddress()));
	} else if (def instanceof RefOr) {
	  Address c1 = getAddressValue(((Binary)def).getVal2());
	  // x = a | c1; y = x & c2
	  if (c1.toWord().and(c2.toWord()).EQ(Word.zero())) {
	    return new RefAnd(y.copyRO(), a.copyRO(), new AddressConstantOperand(c2));
	  }
	} else if (def instanceof RefXor) {
	  Address c1 = getAddressValue(((Binary)def).getVal2());
	  // x = a ^ c1; y = x & c2
	  if (c1.toWord().and(c2.toWord()).EQ(Word.zero())) {
	    return new RefAnd(y.copyRO(), a.copyRO(), new AddressConstantOperand(c2));
	  }
	} else if (def instanceof RefShr) {
	  int c1 = getIntValue(((Binary)def).getVal2());
	  // x = a >> c1; y = x & c2
	  if (Word.zero().minus(Word.one()).rshl(c1).toAddress().EQ(c2)) {
	    // turn arithmetic shifts into logical ones if possible
	    return new RefUshr(y.copyRO(), a.copyRO(), new IntConstantOperand(c1));
	  }
	} else if (def instanceof RefShl) {
	  int c1 = getIntValue(((Binary)def).getVal2());
	  // x = a << c1; y = x & c2
	  if (Word.zero().minus(Word.one()).lsh(c1).and(c2.toWord()).EQ(Word.zero().minus(Word.one()).lsh(c1))) {
	    // does the mask zero bits already cleared by the shift?
	    return new RefShl(y.copyRO(), a.copyRO(), new IntConstantOperand(c1));
	  }
	} else if (def instanceof RefUshr) {
	  int c1 = getIntValue(((Binary)def).getVal2());
	  // x = a >>> c1; y = x & c2
	  if (Word.zero().minus(Word.one()).rshl(c1).and(c2.toWord()).EQ(Word.zero().minus(Word.one()).rshl(c1))) {
	    // does the mask zero bits already cleared by the shift?
	    return new RefUshr(y.copyRO(), a.copyRO(), new IntConstantOperand(c1));
	  }
	}
      }
      return null;
    }
    case Operators.LongAnd: {
      if (FOLD_LONGS && FOLD_ANDS) {
	long c2 = getLongValue(((Binary)s).getVal2());
	if (def instanceof LongAnd) {
	  long c1 = getLongValue(((Binary)def).getVal2());
	  // x = a & c1; y = x & c2
	  return new LongAnd(y.copyRO(), a.copyRO(), new LongConstantOperand(c1 & c2));
	} else if (def instanceof LongOr) {
	  long c1 = getLongValue(((Binary)def).getVal2());
	  // x = a | c1; y = x & c2
	  if ((c1 & c2) == 0) {
	    return new LongAnd(y.copyRO(), a.copyRO(), new LongConstantOperand(c2));
	  }
	} else if (def instanceof LongXor) {
	  long c1 = getLongValue(((Binary)def).getVal2());
	  // x = a ^ c1; y = x & c2
	  if ((c1 & c2) == 0) {
	    return new LongAnd(y.copyRO(), a.copyRO(), new LongConstantOperand(c2));
	  }
	} else if (def instanceof LongShr) {
	  int c1 = getIntValue(((Binary)def).getVal2());
	  // x = a >> c1; y = x & c2
	  if ((-1L >>> c1) == c2) {
	    // turn arithmetic shifts into logical ones if possible
	    return new LongUshr(y.copyRO(), a.copyRO(), new IntConstantOperand(c1));
	  }
	} else if (def instanceof LongShl) {
	  int c1 = getIntValue(((Binary)def).getVal2());
	  // x = a << c1; y = x & c2
	  if (((-1L << c1) & c2) == (-1L << c1)) {
	    // does the mask zero bits already cleared by the shift?
	    return new LongShl(y.copyRO(), a.copyRO(), new IntConstantOperand(c1));
	  }
	} else if (def instanceof LongUshr) {
	  int c1 = getIntValue(((Binary)def).getVal2());
	  // x = a >>> c1; y = x & c2
	  if (((-1L >>> c1) & c2) == (-1L >>> c1)) {
	    // does the mask zero bits already cleared by the shift?
	    return new LongUshr(y.copyRO(), a.copyRO(), new IntConstantOperand(c1));
	  }
	}
      }
      return null;
    }
    case Operators.IntOr: {
      if (FOLD_INTS && FOLD_ORS) {
	int c2 = getIntValue(((Binary)s).getVal2());
	if (def instanceof IntOr) {
	  int c1 = getIntValue(((Binary)def).getVal2());
	  // x = a | c1; y = x | c2
	  return new IntOr(y.copyRO(), a.copyRO(), new IntConstantOperand(c1 | c2));
	} else if (def instanceof IntAnd) {
	  int c1 = getIntValue(((Binary)def).getVal2());
	  // x = a & c1; y = x | c2
	  if ((~c1 | c2) == c2) {
	    return new IntOr(y.copyRO(), a.copyRO(), new IntConstantOperand(c2));
	  }
	} else if (def instanceof IntXor) {
	  int c1 = getIntValue(((Binary)def).getVal2());
	  // x = a ^ c1; y = x | c2
	  if ((c1 | c2) == c2) {
	    return new IntOr(y.copyRO(), a.copyRO(), new IntConstantOperand(c2));
	  }
	}
      }
      return null;
    }
    case Operators.RefOr: {
      if (FOLD_REFS && FOLD_ORS) {
	Address c2 = getAddressValue(((Binary)s).getVal2());
	if (def instanceof RefOr) {
	  Address c1 = getAddressValue(((Binary)def).getVal2());
	  // x = a | c1; y = x | c2
	  return new RefOr(y.copyRO(), a.copyRO(), new AddressConstantOperand(c1.toWord().or(c2.toWord()).toAddress()));
	} else if (def instanceof RefAnd) {
	  Address c1 = getAddressValue(((Binary)def).getVal2());
	  // x = a & c1; y = x | c2
	  if (c1.toWord().not().or(c2.toWord()).EQ(c2.toWord())) {
	    return new RefOr(y.copyRO(), a.copyRO(), new AddressConstantOperand(c2));
	  }
	} else if (def instanceof RefXor) {
	  Address c1 = getAddressValue(((Binary)def).getVal2());
	  // x = a ^ c1; y = x | c2
	  if (c1.toWord().or(c2.toWord()).EQ(c2.toWord())) {
	    return new RefOr(y.copyRO(), a.copyRO(), new AddressConstantOperand(c2));
	  }
	}
      }
      return null;
    }
    case Operators.LongOr: {
      if (FOLD_LONGS && FOLD_ORS) {
	long c2 = getLongValue(((Binary)s).getVal2());
	if (def instanceof LongOr) {
	  long c1 = getLongValue(((Binary)def).getVal2());
	  // x = a | c1; y = x | c2
	  return new LongOr(y.copyRO(), a.copyRO(), new LongConstantOperand(c1 | c2));
	} else if (def instanceof LongAnd) {
	  long c1 = getLongValue(((Binary)def).getVal2());
	  // x = a & c1; y = x | c2
	  if ((~c1 | c2) == c2) {
	    return new LongOr(y.copyRO(), a.copyRO(), new LongConstantOperand(c2));
	  }
	} else if (def instanceof LongXor) {
	  long c1 = getLongValue(((Binary)def).getVal2());
	  // x = a ^ c1; y = x | c2
	  if ((c1 | c2) == c2) {
	    return new LongOr(y.copyRO(), a.copyRO(), new LongConstantOperand(c2));
	  }
	}
      }
      return null;
    }
    case Operators.IntXor: {
      if (FOLD_INTS && FOLD_XORS) {
	int c2 = getIntValue(((Binary)s).getVal2());
	if (def instanceof IntXor) {
	  int c1 = getIntValue(((Binary)def).getVal2());
	  // x = a ^ c1; y = x ^ c2
	  return new IntXor(y.copyRO(), a.copyRO(), new IntConstantOperand(c1 ^ c2));
	} else if (def instanceof IntNot) {
	  // x = ~a; y = x ^ c2
	  return new IntXor(y.copyRO(), a.copyRO(), new IntConstantOperand(~c2));
	} else if (def instanceof BooleanNot) {
	  // x = !a; y = x ^ c2
	  return new IntXor(y.copyRO(), a.copyRO(), new IntConstantOperand(c2 ^ 1));
	}
      }
      return null;
    }
    case Operators.RefXor: {
      if (FOLD_REFS && FOLD_XORS) {
	Address c2 = getAddressValue(((Binary)s).getVal2());
	if (def instanceof RefXor) {
	  Address c1 = getAddressValue(((Binary)def).getVal2());
	  // x = a ^ c1; y = x ^ c2
	  return new RefXor(y.copyRO(), a.copyRO(), new AddressConstantOperand(c1.toWord().xor(c2.toWord()).toAddress()));
	} else if (def instanceof RefNot) {
	  // x = ~a; y = x ^ c2
	  return new RefXor(y.copyRO(), a.copyRO(), new AddressConstantOperand(c2.toWord().not().toAddress()));
	}
      }
      return null;
    }
    case Operators.LongXor: {
      if (FOLD_LONGS && FOLD_XORS) {
	long c2 = getLongValue(((Binary)s).getVal2());
	if (def instanceof LongXor) {
	  long c1 = getLongValue(((Binary)def).getVal2());
	  // x = a ^ c1; y = x ^ c2
	  return new LongXor(y.copyRO(), a.copyRO(), new LongConstantOperand(c1 ^ c2));
	} else if (def instanceof LongNot) {
	  // x = ~a; y = x ^ c2
	  return new LongXor(y.copyRO(), a.copyRO(), new LongConstantOperand(~c2));
	}
      }
      return null;
    }
    case Operators.LongCmp: {
      if (FOLD_LONGS && FOLD_CMPS) {
	long c2 = getLongValue(((Binary)s).getVal2());
	if (def instanceof LongNeg) {
	  // x = -a; y = x cmp c2
	  return new LongCmp(y.copyRO(), new LongConstantOperand(-c2), a.copyRO());
	}
      }
      return null;
    }
    case Operators.FloatCmpl: {
      if (FOLD_FLOATS && FOLD_CMPS) {
	float c2 = getFloatValue(((Binary)s).getVal2());
	if (def instanceof FloatAdd) {
	  float c1 = getFloatValue(((Binary)def).getVal2());
	  // x = a + c1; y = x cmp c2
	  return new FloatCmpl(y.copyRO(), a.copyRO(), new FloatConstantOperand(c2 - c1));
	} else if (def instanceof FloatSub) {
	  float c1 = getFloatValue(((Binary)def).getVal2());
	  // x = a - c1; y = x cmp c2
	  return new FloatCmpl(y.copyRO(), a.copyRO(), new FloatConstantOperand(c1 + c2));
	} else if (def instanceof FloatNeg) {
	  // x = -a; y = x cmp c2
	  return new FloatCmpl(y.copyRO(), new FloatConstantOperand(-c2), a.copyRO());
	}
      }
      return null;
    }
    case Operators.FloatCmpg: {
      if (FOLD_FLOATS && FOLD_CMPS) {
	float c2 = getFloatValue(((Binary)s).getVal2());
	if (def instanceof FloatAdd) {
	  float c1 = getFloatValue(((Binary)def).getVal2());
	  // x = a + c1; y = x cmp c2
	  return new FloatCmpg(y.copyRO(), a.copyRO(), new FloatConstantOperand(c2 - c1));
	} else if (def instanceof FloatSub) {
	  float c1 = getFloatValue(((Binary)def).getVal2());
	  // x = a - c1; y = x cmp c2
	  return new FloatCmpg(y.copyRO(), a.copyRO(), new FloatConstantOperand(c1 + c2));
	} else if (def instanceof FloatNeg) {
	  // x = -a; y = x cmp c2
	  return new FloatCmpg(y.copyRO(), new FloatConstantOperand(-c2), a.copyRO());
	}
      }
      return null;
    }
    case Operators.DoubleCmpl: {
      if (FOLD_DOUBLES && FOLD_CMPS) {
	double c2 = getDoubleValue(((Binary)s).getVal2());
	if (def instanceof DoubleAdd) {
	  double c1 = getDoubleValue(((Binary)def).getVal2());
	  // x = a + c1; y = x cmp c2
	  return new DoubleCmpl(y.copyRO(), a.copyRO(), new DoubleConstantOperand(c2 - c1));
	} else if (def instanceof DoubleSub) {
	  double c1 = getDoubleValue(((Binary)def).getVal2());
	  // x = a - c1; y = x cmp c2
	  return new DoubleCmpl(y.copyRO(), a.copyRO(), new DoubleConstantOperand(c1 + c2));
	} else if (def instanceof DoubleNeg) {
	  // x = -a; y = x cmp c2
	  return new DoubleCmpl(y.copyRO(), new DoubleConstantOperand(-c2), a.copyRO());
	}
      }
      return null;
    }
    case Operators.DoubleCmpg: {
      if (FOLD_DOUBLES && FOLD_CMPS) {
	double c2 = getDoubleValue(((Binary)s).getVal2());
	if (def instanceof DoubleAdd) {
	  double c1 = getDoubleValue(((Binary)def).getVal2());
	  // x = a + c1; y = x cmp c2
	  return new DoubleCmpg(y.copyRO(), a.copyRO(), new DoubleConstantOperand(c2 - c1));
	} else if (def instanceof DoubleSub) {
	  double c1 = getDoubleValue(((Binary)def).getVal2());
	  // x = a - c1; y = x cmp c2
	  return new DoubleCmpg(y.copyRO(), a.copyRO(), new DoubleConstantOperand(c1 + c2));
	} else if (def instanceof DoubleNeg) {
	  // x = -a; y = x cmp c2
	  return new DoubleCmpg(y.copyRO(), new DoubleConstantOperand(-c2), a.copyRO());
	}
      }
      return null;
    }
    case Operators.BooleanCmpInt: {
      if (FOLD_INTS && FOLD_CMPS) {
	int c2 = getIntValue(((BooleanCmp)s).getVal2());
	ConditionOperand cond = (ConditionOperand) ((BooleanCmp)s).getCond().copy();
	BranchProfileOperand prof = (BranchProfileOperand) ((BooleanCmp)s).getBranchProfile().copy();
	if (cond.isEQUAL() || cond.isNOT_EQUAL()) {
	  if (def instanceof IntAdd) {
	    int c1 = getIntValue(((Binary)def).getVal2());
	    // x = a + c1; y = x cmp c2
	    return new BooleanCmpInt(y.copyRO(), a.copyRO(), new IntConstantOperand(c2 - c1), cond, prof);
	  } else if (def instanceof IntSub) {
	    int c1 = getIntValue(((Binary)def).getVal2());
	    // x = a - c1; y = x cmp c2
	    return new BooleanCmpInt(y.copyRO(), a.copyRO(), new IntConstantOperand(c1 + c2), cond, prof);
	  } else if (def instanceof IntNeg) {
	    // x = -a; y = x cmp c2
	    return new BooleanCmpInt(y.copyRO(), a.copyRO(), new IntConstantOperand(-c2), cond.flipOperands(), prof);
	  } else if (def instanceof BooleanCmpInt) {
	    int c1 = getIntValue(((BooleanCmp)def).getVal2());
	    ConditionOperand cond2 = ((BooleanCmp)def).getCond().copy().asCondition();
	    // x = a cmp c1 ? true : false; y = x cmp c2 ? true : false
	    if ((cond.isEQUAL() && c2 == 1)||
		(cond.isNOT_EQUAL() && c2 == 0)) {
	      // Fold away redundancy booleancmp
	      return new BooleanCmpInt(y.copyRO(), a.copyRO(), new IntConstantOperand(c1), cond2, prof);
	    } else if ((cond.isEQUAL() && c2 == 0)||
		(cond.isNOT_EQUAL() && c2 == 1)) {
	      // Fold away redundancy booleancmp
	      return new BooleanCmpInt(y.copyRO(), a.copyRO(), new IntConstantOperand(c1), cond2.flipCode(), prof);
	    }
	  } else if (def instanceof BooleanCmpLong) {
	    long c1 = getLongValue(((BooleanCmp)def).getVal2());
	    ConditionOperand cond2 = ((BooleanCmp)def).getCond().copy().asCondition();
	    // x = a cmp c1 ? true : false; y = x cmp c2 ? true : false
	    if ((cond.isEQUAL() && c2 == 1)||
		(cond.isNOT_EQUAL() && c2 == 0)) {
	      // Fold away redundancy booleancmp
	      return new BooleanCmpLong(y.copyRO(), a.copyRO(), new LongConstantOperand(c1), cond2, prof);
	    } else if ((cond.isEQUAL() && c2 == 0)||
		(cond.isNOT_EQUAL() && c2 == 1)) {
	      // Fold away redundancy booleancmp
	      return new BooleanCmpLong(y.copyRO(), a.copyRO(), new LongConstantOperand(c1), cond2.flipCode(), prof);
	    }
	  } else if (def instanceof BooleanCmpAddr) {
	    Address c1 = getAddressValue(((BooleanCmp)def).getVal2());
	    ConditionOperand cond2 = ((BooleanCmp)def).getCond().copy().asCondition();
	    // x = a cmp c1 ? true : false; y = x cmp c2 ? true : false
	    if ((cond.isEQUAL() && c2 == 1)||
		(cond.isNOT_EQUAL() && c2 == 0)) {
	      // Fold away redundancy booleancmp
	      return new BooleanCmpAddr(y.copyRO(), a.copyRO(), new AddressConstantOperand(c1), cond2, prof);
	    } else if ((cond.isEQUAL() && c2 == 0)||
		(cond.isNOT_EQUAL() && c2 == 1)) {
	      // Fold away redundancy booleancmp
	      return new BooleanCmpAddr(y.copyRO(), a.copyRO(), new AddressConstantOperand(c1), cond2.flipCode(), prof);
	    }
	  } else if (def instanceof BooleanCmpFloat) {
	    float c1 = getFloatValue(((BooleanCmp)def).getVal2());
	    ConditionOperand cond2 = ((BooleanCmp)def).getCond().copy().asCondition();
	    // x = a cmp c1 ? true : false; y = x cmp c2 ? true : false
	    if ((cond.isEQUAL() && c2 == 1)||
		(cond.isNOT_EQUAL() && c2 == 0)) {
	      // Fold away redundancy booleancmp
	      return new BooleanCmpFloat(y.copyRO(), a.copyRO(), new FloatConstantOperand(c1), cond2, prof);
	    } else if ((cond.isEQUAL() && c2 == 0)||
		(cond.isNOT_EQUAL() && c2 == 1)) {
	      // Fold away redundancy booleancmp
	      return new BooleanCmpFloat(y.copyRO(), a.copyRO(), new FloatConstantOperand(c1), cond2.flipCode(), prof);
	    }
	  } else if (def instanceof BooleanCmpDouble) {
	    double c1 = getDoubleValue(((BooleanCmp)def).getVal2());
	    ConditionOperand cond2 = ((BooleanCmp)def).getCond().copy().asCondition();
	    // x = a cmp c1 ? true : false; y = x cmp c2 ? true : false
	    if ((cond.isEQUAL() && c2 == 1)||
		(cond.isNOT_EQUAL() && c2 == 0)) {
	      // Fold away redundancy booleancmp
	      return new BooleanCmpDouble(y.copyRO(), a.copyRO(), new DoubleConstantOperand(c1), cond2, prof);
	    } else if ((cond.isEQUAL() && c2 == 0)||
		(cond.isNOT_EQUAL() && c2 == 1)) {
	      // Fold away redundancy booleancmp
	      return new BooleanCmpDouble(y.copyRO(), a.copyRO(), new DoubleConstantOperand(c1), cond2.flipCode(), prof);
	    }
	  } else if (def instanceof LongCmp) {
	    long c1 = getLongValue(((Binary)def).getVal2());
	    // x = a lcmp c1; y = y = x cmp c2 ? true : false
	    if (cond.isEQUAL() && c2 == 0) {
	      return new BooleanCmpLong(y.copyRO(), a.copyRO(), new LongConstantOperand(c1),
		  ConditionOperand.EQUAL(), prof);
	    } else if (cond.isNOT_EQUAL() && c2 == 0) {
	      return new BooleanCmpLong(y.copyRO(), a.copyRO(), new LongConstantOperand(c1),
		  ConditionOperand.NOT_EQUAL(), prof);
	    } else if ((cond.isEQUAL() && c2 == 1)||(cond.isGREATER() && c2 == 0)){
	      return new BooleanCmpLong(y.copyRO(), a.copyRO(), new LongConstantOperand(c1),
		  ConditionOperand.GREATER(), prof);
	    } else if (cond.isGREATER_EQUAL() && c2 == 0){
	      return new BooleanCmpLong(y.copyRO(), a.copyRO(), new LongConstantOperand(c1),
		  ConditionOperand.GREATER_EQUAL(), prof);
	    } else if ((cond.isEQUAL() && c2 == -1)||(cond.isLESS() && c2 == 0)) {
	      return new BooleanCmpLong(y.copyRO(), a.copyRO(), new LongConstantOperand(c1),
		  ConditionOperand.LESS(), prof);
	    } else if (cond.isLESS_EQUAL() && c2 == 0) {
	      return new BooleanCmpLong(y.copyRO(), a.copyRO(), new LongConstantOperand(c1),
		  ConditionOperand.LESS_EQUAL(), prof);
	    }
	  }
	}
      }
      return null;
    }
    case Operators.BooleanCmpLong: {
      if (FOLD_LONGS && FOLD_CMPS) {
	long c2 = getLongValue(((BooleanCmp)s).getVal2());
	ConditionOperand cond = (ConditionOperand) ((BooleanCmp)s).getCond().copy();
	BranchProfileOperand prof = (BranchProfileOperand) ((BooleanCmp)s).getBranchProfile().copy();
	if (cond.isEQUAL() || cond.isNOT_EQUAL()) {
	  if (def instanceof LongAdd) {
	    long c1 = getLongValue(((Binary)def).getVal2());
	    // x = a + c1; y = x cmp c2
	    return new BooleanCmpLong(y.copyRO(), a.copyRO(), new LongConstantOperand(c2 - c1), cond, prof);
	  } else if (def instanceof LongSub) {
	    long c1 = getLongValue(((Binary)def).getVal2());
	    // x = a - c1; y = x cmp c2
	    return new BooleanCmpLong(y.copyRO(), a.copyRO(), new LongConstantOperand(c1 + c2), cond, prof);
	  } else if (def instanceof LongNeg) {
	    // x = -a; y = x cmp c2
	    return new BooleanCmpInt(y.copyRO(), a.copyRO(), new LongConstantOperand(-c2), cond.flipOperands(), prof);
	  }
	}
      }
      return null;
    }
    case Operators.BooleanCmpAddr: {
      if (FOLD_REFS && FOLD_CMPS) {
	Address c2 = getAddressValue(((BooleanCmp)s).getVal2());
	ConditionOperand cond = (ConditionOperand) ((BooleanCmp)s).getCond().copy();
	BranchProfileOperand prof = (BranchProfileOperand) ((BooleanCmp)s).getBranchProfile().copy();
	if (cond.isEQUAL() || cond.isNOT_EQUAL()) {
	  if (def instanceof RefAdd) {
	    Address c1 = getAddressValue(((Binary)def).getVal2());
	    // x = a + c1; y = x cmp c2
	    return new BooleanCmpAddr(
		y.copyRO(),
		a.copyRO(),
		new AddressConstantOperand(c2.toWord().minus(c1.toWord()).toAddress()),
		cond,
		prof);
	  } else if (def instanceof RefSub) {
	    Address c1 = getAddressValue(((Binary)def).getVal2());
	    // x = a - c1; y = x cmp c2
	    return new BooleanCmpAddr(
		y.copyRO(),
		a.copyRO(),
		new AddressConstantOperand(c1.toWord().plus(c2.toWord()).toAddress()),
		cond,
		prof);
	  } else if (def instanceof RefNeg) {
	    // x = -a; y = x cmp c2
	    return new BooleanCmpAddr(
		y.copyRO(),
		a.copyRO(),
		new AddressConstantOperand(Word.zero().minus(c2.toWord()).toAddress()),
		cond.flipOperands(),
		prof);
	  }
	}
      }
      return null;
    }
    case Operators.IntIfcmp: {
      if (FOLD_INTS && FOLD_IFCMPS) {
	int c2 = getIntValue(((IfCmp)s).getVal2());
	ConditionOperand cond = (ConditionOperand) ((IfCmp)s).getCond().copy();
	BranchOperand target = (BranchOperand) ((IfCmp)s).getTarget().copy();
	BranchProfileOperand prof = (BranchProfileOperand) ((IfCmp)s).getBranchProfile().copy();
	if (cond.isEQUAL() || cond.isNOT_EQUAL()) {
	  if (def instanceof IntAdd) {
	    int c1 = getIntValue(((Binary)def).getVal2());
	    // x = a + c1; y = x cmp c2
	    return new IntIfcmp(y.copyRO(), a.copyRO(), new IntConstantOperand(c2 - c1), cond, target, prof);
	  } else if (def instanceof IntSub) {
	    int c1 = getIntValue(((Binary)def).getVal2());
	    // x = a - c1; y = x cmp c2
	    return new IntIfcmp(y.copyRO(), a.copyRO(), new IntConstantOperand(c1 + c2), cond, target, prof);
	  } else if (def instanceof IntNeg) {
	    // x = -a; y = x cmp c2
	    return new IntIfcmp(y.copyRO(), a.copyRO(), new IntConstantOperand(-c2), cond.flipOperands(), target, prof);
	  } else if (def instanceof BooleanCmpInt) {
	    int c1 = getIntValue(((BooleanCmp)def).getVal2());
	    ConditionOperand cond2 = ((BooleanCmp)def).getCond().copy().asCondition();
	    // x = a cmp<cond2> c1 ? true : false; y = x cmp<cond> c2
	    if ((cond.isEQUAL() && c2 == 1)||
		(cond.isNOT_EQUAL() && c2 == 0)) {
	      // Fold away redundant booleancmp
	      // x = a cmp<cond2> c1; y = x == 1  ==> y = a cmp<cond2> c1
	      return new IntIfcmp(y.copyRO(), a.copyRO(), new IntConstantOperand(c1), cond2, target, prof);
	    } else if ((cond.isEQUAL() && c2 == 0)||
		(cond.isNOT_EQUAL() && c2 == 1)) {
	      // Fold away redundant booleancmp
	      // x = a cmp<cond2> c1; y = x == 0  ==> y = a cmp<!cond2> c1
	      return new IntIfcmp(y.copyRO(), a.copyRO(), new IntConstantOperand(c1), cond2.flipCode(), target, prof);
	    }
	  } else if (def instanceof BooleanCmpLong) {
	    long c1 = getLongValue(((BooleanCmp)def).getVal2());
	    ConditionOperand cond2 = ((BooleanCmp)def).getCond().copy().asCondition();
	    // x = a cmp c1 ? true : false; y = x cmp c2
	    if ((cond.isEQUAL() && c2 == 1)||
		(cond.isNOT_EQUAL() && c2 == 0)) {
	      // Fold away redundant booleancmp
	      return new LongIfcmp(y.copyRO(), a.copyRO(), new LongConstantOperand(c1), cond2, target, prof);
	    } else if ((cond.isEQUAL() && c2 == 0)||
		(cond.isNOT_EQUAL() && c2 == 1)) {
	      // Fold away redundant booleancmp
	      return new LongIfcmp(y.copyRO(), a.copyRO(), new LongConstantOperand(c1), cond2.flipCode(), target, prof);
	    }
	  } else if (def instanceof BooleanCmpAddr) {
	    Address c1 = getAddressValue(((BooleanCmp)def).getVal2());
	    ConditionOperand cond2 = ((BooleanCmp)def).getCond().copy().asCondition();
	    // x = a cmp c1 ? true : false; y = x cmp c2
	    if ((cond.isEQUAL() && c2 == 1)||
		(cond.isNOT_EQUAL() && c2 == 0)) {
	      // Fold away redundant booleancmp
	      return new RefIfcmp(y.copyRO(), a.copyRO(), new AddressConstantOperand(c1), cond2, target, prof);
	    } else if ((cond.isEQUAL() && c2 == 0)||
		(cond.isNOT_EQUAL() && c2 == 1)) {
	      // Fold away redundant booleancmp
	      return new RefIfcmp(y.copyRO(), a.copyRO(), new AddressConstantOperand(c1), cond2.flipCode(), target, prof);
	    }
	  } else if (def instanceof BooleanCmpFloat) {
	    float c1 = getFloatValue(((BooleanCmp)def).getVal2());
	    ConditionOperand cond2 = ((BooleanCmp)def).getCond().copy().asCondition();
	    // x = a cmp c1 ? true : false; y = x cmp c2
	    if ((cond.isEQUAL() && c2 == 1)||
		(cond.isNOT_EQUAL() && c2 == 0)) {
	      // Fold away redundant booleancmp
	      return new FloatIfcmp(y.copyRO(), a.copyRO(), new FloatConstantOperand(c1), cond2, target, prof);
	    } else if ((cond.isEQUAL() && c2 == 0)||
		(cond.isNOT_EQUAL() && c2 == 1)) {
	      // Fold away redundant booleancmp
	      return new FloatIfcmp(y.copyRO(), a.copyRO(), new FloatConstantOperand(c1), cond2.flipCode(), target, prof);
	    }
	  } else if (def instanceof BooleanCmpDouble) {
	    double c1 = getDoubleValue(((BooleanCmp)def).getVal2());
	    ConditionOperand cond2 = ((BooleanCmp)def).getCond().copy().asCondition();
	    // x = a cmp c1 ? true : false; y = x cmp c2
	    if ((cond.isEQUAL() && c2 == 1)||
		(cond.isNOT_EQUAL() && c2 == 0)) {
	      // Fold away redundant booleancmp
	      return new DoubleIfcmp(y.copyRO(), a.copyRO(), new DoubleConstantOperand(c1), cond2, target, prof);
	    } else if ((cond.isEQUAL() && c2 == 0)||
		(cond.isNOT_EQUAL() && c2 == 1)) {
	      // Fold away redundant booleancmp
	      return new DoubleIfcmp(y.copyRO(), a.copyRO(), new DoubleConstantOperand(c1), cond2.flipCode(), target, prof);
	    }
	  } else if (def instanceof LongCmp) {
	    long c1 = getLongValue(((Binary)def).getVal2());
	    // x = a lcmp c1; y = y = x cmp c2
	    if (cond.isEQUAL() && c2 == 0) {
	      return new LongIfcmp(y.copyRO(), a.copyRO(), new LongConstantOperand(c1),
		  ConditionOperand.EQUAL(), target, prof);
	    } else if (cond.isNOT_EQUAL() && c2 == 0) {
	      return new LongIfcmp(y.copyRO(), a.copyRO(), new LongConstantOperand(c1),
		  ConditionOperand.NOT_EQUAL(), target, prof);
	    } else if ((cond.isEQUAL() && c2 == 1)||(cond.isGREATER() && c2 == 0)){
	      return new LongIfcmp(y.copyRO(), a.copyRO(), new LongConstantOperand(c1),
		  ConditionOperand.GREATER(), target, prof);
	    } else if (cond.isGREATER_EQUAL() && c2 == 0){
	      return new LongIfcmp(y.copyRO(), a.copyRO(), new LongConstantOperand(c1),
		  ConditionOperand.GREATER_EQUAL(), target, prof);
	    } else if ((cond.isEQUAL() && c2 == -1)||(cond.isLESS() && c2 == 0)) {
	      return new LongIfcmp(y.copyRO(), a.copyRO(), new LongConstantOperand(c1),
		  ConditionOperand.LESS(), target, prof);
	    } else if (cond.isLESS_EQUAL() && c2 == 0) {
	      return new LongIfcmp(y.copyRO(), a.copyRO(), new LongConstantOperand(c1),
		  ConditionOperand.LESS_EQUAL(), target, prof);
	    }
	  }
	}
      }
      return null;
    }
    case Operators.LongIfcmp: {
      if (FOLD_LONGS && FOLD_IFCMPS) {
	long c2 = getLongValue(((IfCmp)s).getVal2());
	ConditionOperand cond = (ConditionOperand) ((IfCmp)s).getCond().copy();
	BranchOperand target = (BranchOperand) ((IfCmp)s).getTarget().copy();
	BranchProfileOperand prof = (BranchProfileOperand) ((IfCmp)s).getBranchProfile().copy();
	if (cond.isEQUAL() || cond.isNOT_EQUAL()) {
	  if (def instanceof LongAdd) {
	    long c1 = getLongValue(((Binary)def).getVal2());
	    // x = a + c1; y = x cmp c2
	    return new LongIfcmp(y.copyRO(), a.copyRO(), new LongConstantOperand(c2 - c1), cond, target, prof);
	  } else if (def instanceof LongSub) {
	    long c1 = getLongValue(((Binary)def).getVal2());
	    // x = a - c1; y = x cmp c2
	    return new LongIfcmp(y.copyRO(), a.copyRO(), new LongConstantOperand(c1 + c2), cond, target, prof);
	  } else if (def instanceof LongNeg) {
	    // x = -a; y = x cmp c2
	    return new LongIfcmp(y.copyRO(), a.copyRO(), new LongConstantOperand(-c2), cond.flipOperands(), target, prof);
	  }
	}
      }
      return null;
    }
    case Operators.FloatIfcmp: {
      if (FOLD_FLOATS && FOLD_IFCMPS) {
	float c2 = getFloatValue(((IfCmp)s).getVal2());
	ConditionOperand cond = (ConditionOperand) ((IfCmp)s).getCond().copy();
	BranchOperand target = (BranchOperand) ((IfCmp)s).getTarget().copy();
	BranchProfileOperand prof = (BranchProfileOperand) ((IfCmp)s).getBranchProfile().copy();
	if (def instanceof FloatAdd) {
	  float c1 = getFloatValue(((Binary)def).getVal2());
	  // x = a + c1; y = x cmp c2
	  return new FloatIfcmp(y.copyRO(), a.copyRO(), new FloatConstantOperand(c2 - c1), cond, target, prof);
	} else if (def instanceof FloatSub) {
	  float c1 = getFloatValue(((Binary)def).getVal2());
	  // x = a - c1; y = x cmp c2
	  return new FloatIfcmp(y.copyRO(), a.copyRO(), new FloatConstantOperand(c1 + c2), cond, target, prof);
	} else if (def instanceof FloatNeg) {
	  // x = -a; y = x cmp c2
	  return new FloatIfcmp(y.copyRO(), a.copyRO(), new FloatConstantOperand(-c2), cond.flipOperands(), target, prof);
	}
      }
      return null;
    }
    case Operators.DoubleIfcmp: {
      if (FOLD_DOUBLES && FOLD_IFCMPS) {
	double c2 = getDoubleValue(((IfCmp)s).getVal2());
	ConditionOperand cond = (ConditionOperand) ((IfCmp)s).getCond().copy();
	BranchOperand target = (BranchOperand) ((IfCmp)s).getTarget().copy();
	BranchProfileOperand prof = (BranchProfileOperand) ((IfCmp)s).getBranchProfile().copy();
	if (def instanceof DoubleAdd) {
	  double c1 = getDoubleValue(((Binary)def).getVal2());
	  // x = a + c1; y = x cmp c2
	  return new DoubleIfcmp(y.copyRO(), a.copyRO(), new DoubleConstantOperand(c2 - c1), cond, target, prof);
	} else if (def instanceof DoubleSub) {
	  double c1 = getDoubleValue(((Binary)def).getVal2());
	  // x = a - c1; y = x cmp c2
	  return new DoubleIfcmp(y.copyRO(), a.copyRO(), new DoubleConstantOperand(c1 + c2), cond, target, prof);
	} else if (def instanceof DoubleNeg) {
	  // x = -a; y = x cmp c2
	  return new DoubleIfcmp(y.copyRO(), a.copyRO(), new DoubleConstantOperand(-c2), cond.flipOperands(), target, prof);
	}
      }
      return null;
    }
    case Operators.RefIfcmp: {
      if (FOLD_REFS && FOLD_IFCMPS) {
	Address c2 = getAddressValue(((IfCmp)s).getVal2());
	ConditionOperand cond = (ConditionOperand) ((IfCmp)s).getCond().copy();
	BranchOperand target = (BranchOperand) ((IfCmp)s).getTarget().copy();
	BranchProfileOperand prof = (BranchProfileOperand) ((IfCmp)s).getBranchProfile().copy();
	if (cond.isEQUAL() || cond.isNOT_EQUAL()) {
	  if ((def instanceof New || def instanceof Newarray) && c2.EQ(Address.zero())) {
	    // x = new ... ; y = x cmp null
	    return new RefIfcmp(
		y.copyRO(),
		new AddressConstantOperand(Address.zero()),
		new AddressConstantOperand(Address.zero()),
		cond.flipCode(),
		target,
		prof);
	  } else if (def instanceof RefAdd) {
	    Address c1 = getAddressValue(((Binary)def).getVal2());
	    // x = a + c1; y = x cmp c2
	    return new RefIfcmp(
		y.copyRO(),
		a.copyRO(),
		new AddressConstantOperand(c2.toWord().minus(c1.toWord()).toAddress()),
		cond,
		target,
		prof);
	  } else if (def instanceof RefSub) {
	    Address c1 = getAddressValue(((Binary)def).getVal2());
	    // x = a - c1; y = x cmp c2
	    return new RefIfcmp(
		y.copyRO(),
		a.copyRO(),
		new AddressConstantOperand(c1.toWord().plus(c2.toWord()).toAddress()),
		cond,
		target,
		prof);
	  } else if (def instanceof RefNeg) {
	    // x = -a; y = x cmp c2
	    return new RefIfcmp(
		y.copyRO(),
		a.copyRO(),
		new AddressConstantOperand(Word.zero().minus(c2.toWord()).toAddress()),
		cond.flipOperands(),
		target,
		prof);
	  }
	}
      }
      return null;
    }
    case Operators.IntIfcmp2: {
      if (FOLD_INTS && FOLD_IFCMPS) {
	int c2 = getIntValue(((IfCmp2)s).getVal2());
	ConditionOperand cond1 = (ConditionOperand) ((IfCmp2)s).getCond1().copy();
	ConditionOperand cond2 = (ConditionOperand) ((IfCmp2)s).getCond2().copy();
	BranchOperand target1 = (BranchOperand) ((IfCmp2)s).getTarget1().copy();
	BranchOperand target2 = (BranchOperand) ((IfCmp2)s).getTarget2().copy();
	BranchProfileOperand prof1 = (BranchProfileOperand) ((IfCmp2)s).getBranchProfile1().copy();
	BranchProfileOperand prof2 = (BranchProfileOperand) ((IfCmp2)s).getBranchProfile2().copy();
	if ((cond1.isEQUAL() || cond1.isNOT_EQUAL())&&(cond2.isEQUAL() || cond2.isNOT_EQUAL())) {
	  if (def instanceof IntAdd) {
	    int c1 = getIntValue(((Binary)def).getVal2());
	    // x = a + c1; y = x cmp c2
	    return new IntIfcmp2(
		y.copyRO(),
		a.copyRO(),
		new IntConstantOperand(c2 - c1),
		cond1,
		target1,
		prof1,
		cond2,
		target2,
		prof2);
	  } else if (def instanceof IntSub) {
	    int c1 = getIntValue(((Binary)def).getVal2());
	    // x = a - c1; y = x cmp c2
	    return new IntIfcmp2(
		y.copyRO(),
		a.copyRO(),
		new IntConstantOperand(c1 + c2),
		cond1,
		target1,
		prof1,
		cond2,
		target2,
		prof2);
	  } else if (def instanceof IntNeg) {
	    // x = -a; y = x cmp c2
	    return new IntIfcmp2(
		y.copyRO(),
		a.copyRO(),
		new IntConstantOperand(-c2),
		cond1.flipOperands(),
		target1,
		prof1,
		cond2.flipOperands(),
		target2,
		prof2);
	  }
	}
      }
      return null;
    }

    case Operators.IntCondMove:
    case Operators.LongCondMove:
    case Operators.RefCondMove:
    case Operators.FloatCondMove:
    case Operators.DoubleCondMove:
    case Operators.GuardCondMove: {
      if (FOLD_INTS && FOLD_CONDMOVES) {
	Operand trueValue = ((CondMove)s).getTrueValue();
	Operand falseValue = ((CondMove)s).getFalseValue();
	ConditionOperand cond = (ConditionOperand) ((CondMove)s).getCond().copy();
	boolean isEqualityTest = cond.isEQUAL() || cond.isNOT_EQUAL();
	switch (def.getOpcode()) {
	case Operators.IntAdd:
	  if (isEqualityTest) {
	    int c1 = getIntValue(((Binary)def).getVal2());
	    int c2 = getIntValue(((CondMove)s).getVal2());
	    // x = a + c1; y = x cmp c2 ? trueValue : falseValue
		return new IntCondMove(y.copyRO(), a.copyRO(), new IntConstantOperand(c2 - c1), cond, trueValue, falseValue);
	  }
	  break;
	case Operators.LongAdd:
	  if (isEqualityTest) {
	    long c1 = getLongValue(((Binary)def).getVal2());
	    long c2 = getLongValue(((CondMove)s).getVal2());
	    // x = a + c1; y = x cmp c2 ? trueValue : falseValue
	    return new LongCondMove(y.copyRO(), a.copyRO(), new LongConstantOperand(c2 - c1), cond, trueValue, falseValue);
	  }
	  break;
	case Operators.RefAdd:
	  if (isEqualityTest) {
	    Address c1 = getAddressValue(((Binary)def).getVal2());
	    Address c2 = getAddressValue(((CondMove)s).getVal2());
	    // x = a + c1; y = x cmp c2 ? trueValue : falseValue
	    return new RefCondMove(y.copyRO(),
		a.copyRO(),
		new AddressConstantOperand(c2.toWord().minus(c1.toWord()).toAddress()),
		cond,
		trueValue,
		falseValue);
	  }
	  break;
	case Operators.FloatAdd: {
	  float c1 = getFloatValue(((Binary)def).getVal2());
	  float c2 = getFloatValue(((CondMove)s).getVal2());
	  // x = a + c1; y = x cmp c2 ? trueValue : falseValue
	  return new FloatCondMove(y.copyRO(), a.copyRO(), new FloatConstantOperand(c2 - c1), cond, trueValue, falseValue);
	}
	case Operators.DoubleAdd: {
	  double c1 = getDoubleValue(((Binary)def).getVal2());
	  double c2 = getDoubleValue(((CondMove)s).getVal2());
	  // x = a + c1; y = x cmp c2 ? trueValue : falseValue
	  return new DoubleCondMove(y.copyRO(), a.copyRO(), new DoubleConstantOperand(c2 - c1), cond, trueValue, falseValue);
	}
	case Operators.IntSub:
	  if (isEqualityTest) {
	    int c1 = getIntValue(((Binary)def).getVal2());
	    int c2 = getIntValue(((CondMove)s).getVal2());
	    // x = a - c1; y = x cmp c2 ? trueValue : falseValue
	    return new IntCondMove(y.copyRO(), a.copyRO(), new IntConstantOperand(c1 + c2), cond, trueValue, falseValue);
	  }
	  break;
	case Operators.LongSub:
	  if (isEqualityTest) {

	    long c1 = getLongValue(((Binary)def).getVal2());
	    long c2 = getLongValue(((CondMove)s).getVal2());
	    // x = a - c1; y = x cmp c2 ? trueValue : falseValue
	    return new LongCondMove(y.copyRO(), a.copyRO(), new LongConstantOperand(c1 + c2), cond, trueValue, falseValue);
	  }
	  break;
	case Operators.RefSub:
	  if (isEqualityTest) {
	    Address c1 = getAddressValue(((Binary)def).getVal2());
	    Address c2 = getAddressValue(((CondMove)s).getVal2());
	    // x = a - c1; y = x cmp c2 ? trueValue : falseValue
	    return new RefCondMove(y.copyRO(),
		a.copyRO(),
		new AddressConstantOperand(c1.toWord().plus(c2.toWord()).toAddress()),
		cond,
		trueValue,
		falseValue);
	  }
	  break;
	case Operators.FloatSub: {
	  float c1 = getFloatValue(((Binary)def).getVal2());
	  float c2 = getFloatValue(((CondMove)s).getVal2());
	  // x = a - c1; y = x cmp c2 ? trueValue : falseValue
	  return new FloatCondMove(y.copyRO(), a.copyRO(), new FloatConstantOperand(c1 + c2), cond, trueValue, falseValue);
	}
	case Operators.DoubleSub: {
	  double c1 = getDoubleValue(((Binary)def).getVal2());
	  double c2 = getDoubleValue(((CondMove)s).getVal2());
	  // x = a - c1; y = x cmp c2 ? trueValue : falseValue
	  return new DoubleCondMove(y.copyRO(), a.copyRO(), new DoubleConstantOperand(c1 + c2), cond, trueValue, falseValue);
	}
	case Operators.IntNeg:
	  if (isEqualityTest) {
	    int c2 = getIntValue(((CondMove)s).getVal2());
	    // x = -a; y = x cmp c2 ? trueValue : falseValue
	    return new IntCondMove(y.copyRO(),
		a.copyRO(),
		new IntConstantOperand(-c2),
		cond.flipOperands(),
		trueValue,
		falseValue);
	  }
	  break;
	case Operators.LongNeg:
	  if (isEqualityTest) {
	    long c2 = getLongValue(((CondMove)s).getVal2());
	    // x = -a; y = x cmp c2 ? trueValue : falseValue
	    return new LongCondMove(y.copyRO(),
		a.copyRO(),
		new LongConstantOperand(-c2),
		cond.flipOperands(),
		trueValue,
		falseValue);
	  }
	  break;
	case Operators.RefNeg:
	  if (isEqualityTest) {
	    Address c2 = getAddressValue(((CondMove)s).getVal2());
	    // x = -a; y = x cmp c2 ? trueValue : falseValue
	    return new RefCondMove(y.copyRO(),
		a.copyRO(),
		new AddressConstantOperand(Word.zero().minus(c2.toWord()).toAddress()),
		cond.flipOperands(),
		trueValue,
		falseValue);
	  }
	  break;
	case Operators.FloatNeg: {
	  float c2 = getFloatValue(((CondMove)s).getVal2());
	  // x = -a; y = x cmp c2 ? trueValue : falseValue
	  return new FloatCondMove(y.copyRO(),
	      a.copyRO(),
	      new FloatConstantOperand(-c2),
	      cond.flipOperands(),
	      trueValue,
	      falseValue);
	}
	case Operators.DoubleNeg: {
	  double c2 = getDoubleValue(((CondMove)s).getVal2());
	  // x = -a; y = x cmp c2 ? trueValue : falseValue
	  return new DoubleCondMove(y.copyRO(),
	      a.copyRO(),
	      new DoubleConstantOperand(-c2),
	      cond.flipOperands(),
	      trueValue,
	      falseValue);
	}
	case Operators.BooleanCmpInt: {
	  int c1 = getIntValue(((BooleanCmp)def).getVal2());
	  int c2 = getIntValue(((CondMove)s).getVal2());
	  // x = a cmp c1 ? true : false; y = x cmp c2 ? trueValue : falseValue
	  if ((cond.isEQUAL() && c2 == 1)||
	      (cond.isNOT_EQUAL() && c2 == 0)) {
	    return new IntCondMove(y.copyRO(),
		a.copyRO(),
		new IntConstantOperand(c1),
		((BooleanCmp)def).getCond().copy().asCondition(),
		trueValue,
		falseValue);
	  } else if ((cond.isEQUAL() && c2 == 0)||
	      (cond.isNOT_EQUAL() && c2 == 1)) {
	    return new IntCondMove(y.copyRO(),
		a.copyRO(),
		new IntConstantOperand(c1),
		((BooleanCmp)def).getCond().copy().asCondition().flipCode(),
		trueValue,
		falseValue);
	  }
	  break;
	}
	case Operators.BooleanCmpAddr: {
	  Address c1 = getAddressValue(((BooleanCmp)def).getVal2());
	  int c2 = getIntValue(((CondMove)s).getVal2());
	  // x = a cmp c1 ? true : false; y = x cmp c2 ? trueValue : falseValue
	  if ((cond.isEQUAL() && c2 == 1)||
	      (cond.isNOT_EQUAL() && c2 == 0)) {
	    return new RefCondMove(y.copyRO(), a.copyRO(), new AddressConstantOperand(c1), 
		((BooleanCmp)def).getCond().copy().asCondition(),
		trueValue,
		falseValue);
	  } else if ((cond.isEQUAL() && c2 == 0)||
	      (cond.isNOT_EQUAL() && c2 == 1)) {
	    return new RefCondMove(y.copyRO(), a.copyRO(), new AddressConstantOperand(c1),
		((BooleanCmp)def).getCond().flipCode(),
		trueValue,
		falseValue);
	  }
	  break;
	}
	case Operators.BooleanCmpLong: {
	  long c1 = getLongValue(((BooleanCmp)def).getVal2());
	  int c2 = getIntValue(((CondMove)s).getVal2());
	  // x = a cmp c1 ? true : false; y = x cmp c2 ? trueValue : falseValue
	  if ((cond.isEQUAL() && c2 == 1)||
	      (cond.isNOT_EQUAL() && c2 == 0)) {
	    return new LongCondMove(y.copyRO(), a.copyRO(), new LongConstantOperand(c1),
		((BooleanCmp)def).getCond().copy().asCondition(),
		trueValue,
		falseValue);
	  } else if ((cond.isEQUAL() && c2 == 0)||
	      (cond.isNOT_EQUAL() && c2 == 1)) {
	    return new LongCondMove(y.copyRO(), a.copyRO(),
		new LongConstantOperand(c1),
		((BooleanCmp)def).getCond().copy().asCondition().flipCode(),
		trueValue,
		falseValue);
	  } else {
	    return null;
	  }
	}
	case Operators.BooleanCmpDouble: {
	  double c1 = getDoubleValue(((BooleanCmp)def).getVal2());
	  int c2 = getIntValue(((CondMove)s).getVal2());
	  // x = a cmp c1 ? true : false; y = x cmp c2 ? trueValue : falseValue
	  if ((cond.isEQUAL() && c2 == 1)||
	      (cond.isNOT_EQUAL() && c2 == 0)) {
	    return new DoubleCondMove(y.copyRO(), a.copyRO(),
		new DoubleConstantOperand(c1),
		((BooleanCmp)def).getCond().copy().asCondition(),
		trueValue,
		falseValue);
	  } else if ((cond.isEQUAL() && c2 == 0)||
	      (cond.isNOT_EQUAL() && c2 == 1)) {
	    return new DoubleCondMove(y.copyRO(), a.copyRO(),
		new DoubleConstantOperand(c1),
		((BooleanCmp)def).getCond().copy().asCondition().flipCode(),
		trueValue,
		falseValue);
	  }
	  break;
	}
	case Operators.BooleanCmpFloat: {
	  float c1 = getFloatValue(((BooleanCmp)def).getVal2());
	  int c2 = getIntValue(((CondMove)s).getVal2());
	  // x = a cmp c1 ? true : false; y = x cmp c2 ? trueValue : falseValue
	  if ((cond.isEQUAL() && c2 == 1)||
	      (cond.isNOT_EQUAL() && c2 == 0)) {
	    return new FloatCondMove(y.copyRO(), a.copyRO(),
		new FloatConstantOperand(c1),
		((BooleanCmp)def).getCond().copy().asCondition(),
		trueValue,
		falseValue);
	  } else if ((cond.isEQUAL() && c2 == 0)||
	      (cond.isNOT_EQUAL() && c2 == 1)) {
	    return new FloatCondMove(y.copyRO(), a.copyRO(),
		new FloatConstantOperand(c1),
		((BooleanCmp)def).getCond().copy().asCondition().flipCode(),
		trueValue,
		falseValue);
	  }
	  break;
	}
	case Operators.LongCmp: {
	  long c1 = getLongValue(((Binary)def).getVal2());
	  int c2 = getIntValue(((CondMove)s).getVal2());
	  // x = a lcmp c1; y = y = x cmp c2 ? trueValue : falseValue
	  if (cond.isEQUAL() && c2 == 0) {
	    return new LongCondMove(y.copyRO(), a.copyRO(),
		new LongConstantOperand(c1),
		ConditionOperand.EQUAL(),
		trueValue,
		falseValue);
	  } else if (cond.isNOT_EQUAL() && c2 == 0) {
	    return new LongCondMove(y.copyRO(), a.copyRO(),
		new LongConstantOperand(c1),
		ConditionOperand.NOT_EQUAL(),
		trueValue,
		falseValue);
	  } else if ((cond.isEQUAL() && c2 == 1)||(cond.isGREATER() && c2 == 0)){
	    return new LongCondMove(y.copyRO(), a.copyRO(),
		new LongConstantOperand(c1),
		ConditionOperand.GREATER(),
		trueValue,
		falseValue);
	  } else if (cond.isGREATER_EQUAL() && c2 == 0){
	    return new LongCondMove(y.copyRO(), a.copyRO(),
		new LongConstantOperand(c1),
		ConditionOperand.GREATER_EQUAL(),
		trueValue,
		falseValue);
	  } else if ((cond.isEQUAL() && c2 == -1)||(cond.isLESS() && c2 == 0)) {
	    return new LongCondMove(y.copyRO(), a.copyRO(),
		new LongConstantOperand(c1),
		ConditionOperand.LESS(),
		trueValue,
		falseValue);
	  } else if (cond.isLESS_EQUAL() && c2 == 0) {
	    return new LongCondMove(y.copyRO(), a.copyRO(),
		new LongConstantOperand(c1),
		ConditionOperand.LESS_EQUAL(),
		trueValue,
		falseValue);
	  }
	  break;
	}
	default:
	}
      }
      return null;
    }

    case Operators.IntNeg: {
      if (FOLD_INTS && FOLD_NEGS) {
	if (def instanceof IntNeg) {
	  // x = -z; y = -x;
	  return new IntMove(y.copyRO(), ((Unary)def).getVal().copy());
	} else if (def instanceof IntMul) {
	  int c1 = getIntValue(((Binary)def).getVal2());
	  // x = a * c1; y = -x;
	  return new IntMul(y.copyRO(), a.copyRO(), new IntConstantOperand(-c1));
	} else if (def instanceof IntDiv) {
	  int c1 = getIntValue(((GuardedBinary)def).getVal2());
	  Operand guard = ((GuardedBinary)def).getGuard();
	  // x = a / c1; y = -x;
	  return new IntDiv(y.copyRO(), a.copyRO(), new IntConstantOperand(-c1), guard.copy());
	} else if (FOLD_CONSTANTS_TO_LHS && (def instanceof IntAdd)) {
	  int c1 = getIntValue(((Binary)def).getVal2());
	  // x = a + c1; y = -x;
	  return new IntSub(y.copyRO(), new IntConstantOperand(-c1), a.copyRO());
	} else if (FOLD_CONSTANTS_TO_LHS && (def instanceof IntSub)) {
	  int c1 = getIntValue(((Binary)def).getVal2());
	  // x = a - c1; y = -x;
	  return new IntSub(y.copyRO(), new IntConstantOperand(c1), a.copyRO());
	}
      }
      return null;
    }

    case Operators.RefNeg: {
      if (FOLD_REFS && FOLD_NEGS) {
	if (def instanceof RefNeg) {
	  // x = -z; y = -x;
	  return new RefMove(y.copyRO(), ((Unary)def).getVal().copy());
	} else if (FOLD_CONSTANTS_TO_LHS && (def instanceof RefAdd)) {
	  Address c1 = getAddressValue(((Binary)def).getVal2());
	  // x = a + c1; y = -x;
	  return new RefSub(y.copyRO(), new AddressConstantOperand(Word.zero().minus(c1.toWord()).toAddress()), a.copyRO());
	} else if (FOLD_CONSTANTS_TO_LHS && (def instanceof RefSub)) {
	  Address c1 = getAddressValue(((Binary)def).getVal2());
	  // x = a - c1; y = -x;
	  return new RefSub(y.copyRO(), new AddressConstantOperand(c1), a.copyRO());
	}
      }
      return null;
    }

    case Operators.LongNeg: {
      if (FOLD_LONGS && FOLD_NEGS) {
	if (def instanceof LongNeg) {
	  // x = -z; y = -x;
	  return new LongMove(y.copyRO(), ((Unary)def).getVal().copy());
	} else if (def instanceof LongMul) {
	  long c1 = getLongValue(((Binary)def).getVal2());
	  // x = a * c1; y = -x;
	  return new LongMul(y.copyRO(), a.copyRO(), new LongConstantOperand(-c1));
	} else if (def instanceof LongDiv) {
	  long c1 = getLongValue(((GuardedBinary)def).getVal2());
	  Operand guard = ((GuardedBinary)def).getGuard();
	  // x = a / c1; y = -x;
	  return new LongDiv(y.copyRO(), a.copyRO(), new LongConstantOperand(-c1), guard.copy());
	} else if (FOLD_CONSTANTS_TO_LHS && (def instanceof LongAdd)) {
	  long c1 = getLongValue(((Binary)def).getVal2());
	  // x = a + c1; y = -x;
	  return new LongSub(y.copyRO(), new LongConstantOperand(-c1), a.copyRO());
	} else if (FOLD_CONSTANTS_TO_LHS && (def instanceof LongSub)) {
	  long c1 = getLongValue(((Binary)def).getVal2());
	  // x = a - c1; y = -x;
	  return new LongSub(y.copyRO(), new LongConstantOperand(c1), a.copyRO());
	}
      }
      return null;
    }

    case Operators.FloatNeg: {
      if (FOLD_FLOATS && FOLD_NEGS) {
	if (def instanceof FloatNeg) {
	  // x = -z; y = -x;
	  return new FloatMove(y.copyRO(), ((Unary)def).getVal().copy());
	} else if (def instanceof FloatMul) {
	  float c1 = getFloatValue(((Binary)def).getVal2());
	  // x = a * c1; y = -x;
	  return new FloatMul(y.copyRO(), a.copyRO(), new FloatConstantOperand(-c1));
	} else if (def instanceof FloatDiv) {
	  float c1 = getFloatValue(((Binary)def).getVal2());
	  // x = a / c1; y = -x;
	  return new FloatDiv(y.copyRO(), a.copyRO(), new FloatConstantOperand(-c1));
	} else if (FOLD_CONSTANTS_TO_LHS && (def instanceof FloatAdd)) {
	  float c1 = getFloatValue(((Binary)def).getVal2());
	  // x = a + c1; y = -x;
	  return new FloatSub(y.copyRO(), new FloatConstantOperand(-c1), a.copyRO());
	} else if (FOLD_CONSTANTS_TO_LHS && (def instanceof FloatSub)) {
	  float c1 = getFloatValue(((Binary)def).getVal2());
	  // x = a - c1; y = -x;
	  return new FloatSub(y.copyRO(), new FloatConstantOperand(c1), a.copyRO());
	}
      }
      return null;
    }

    case Operators.DoubleNeg: {
      if (FOLD_DOUBLES && FOLD_NEGS) {
	if (def instanceof DoubleNeg) {
	  // x = -z; y = -x;
	  return new DoubleMove(y.copyRO(), ((Unary)def).getVal().copy());
	} else if (def instanceof DoubleMul) {
	  double c1 = getDoubleValue(((Binary)def).getVal2());
	  // x = a * c1; y = -x;
	  return new DoubleMul(y.copyRO(), a.copyRO(), new DoubleConstantOperand(-c1));
	} else if (def instanceof DoubleDiv) {
	  double c1 = getDoubleValue(((Binary)def).getVal2());
	  // x = a / c1; y = -x;
	  return new DoubleDiv(y.copyRO(), a.copyRO(), new DoubleConstantOperand(-c1));
	} else if (FOLD_CONSTANTS_TO_LHS && (def instanceof DoubleAdd)) {
	  double c1 = getDoubleValue(((Binary)def).getVal2());
	  // x = a + c1; y = -x;
	  return new DoubleSub(y.copyRO(), new DoubleConstantOperand(-c1), a.copyRO());
	} else if (FOLD_CONSTANTS_TO_LHS && (def instanceof DoubleSub)) {
	  double c1 = getDoubleValue(((Binary)def).getVal2());
	  // x = a - c1; y = -x;
	  return new DoubleSub(y.copyRO(), new DoubleConstantOperand(c1), a.copyRO());
	}
      }
      return null;
    }

    case Operators.BooleanNot: {
      if (FOLD_INTS && FOLD_NOTS) {
	if (def instanceof BooleanNot) {
	  // x = 1 ^ a; y = 1 ^ x;
	  return new IntMove(y.copyRO(), ((Unary)def).getVal().copy());
	} else if (def instanceof BooleanCmp) {
	  // x = a cmp b; y = !x
	  return BooleanCmp.create(((BooleanCmp)def).getVal1().getType(),
	      y.copyRO(),
	      ((BooleanCmp)def).getVal1().copy(),
	      ((BooleanCmp)def).getVal2().copy(),
	      ((ConditionOperand) ((BooleanCmp)def).getCond().copy()).flipCode(),
	      ((BranchProfileOperand) ((BooleanCmp)def).getBranchProfile().copy()));
	}
      }
      return null;
    }

    case Operators.IntNot: {
      if (FOLD_INTS && FOLD_NOTS) {
	if (def instanceof IntNot) {
	  // x = -1 ^ a; y = -1 ^ x;
	  return new IntMove(y.copyRO(), a.copy());
	}
      }
      return null;
    }

    case Operators.RefNot: {
      if (FOLD_REFS && FOLD_NOTS) {
	if (def instanceof RefNot) {
	  // x = -1 ^ a; y = -1 ^ x;
	  return new RefMove(y.copyRO(), a.copy());
	}
      }
      return null;
    }

    case Operators.LongNot: {
      if (FOLD_LONGS && FOLD_NOTS) {
	if (def instanceof LongNot) {
	  // x = -1 ^ a; y = -1 ^ x;
	  return new LongMove(y.copyRO(), a.copy());
	}
      }
      return null;
    }

    case Operators.Int2byte: {
      if (FOLD_INTS && FOLD_2CONVERSION) {
	if ((def instanceof Int2byte) || (def instanceof Int2short)) {
	  // x = (short)a; y = (byte)x;
	  return new Int2byte(y.copyRO(), a.copy());
	} else if (def instanceof Int2ushort) {
	  // x = (char)a; y = (byte)x;
	  return new IntAnd(y.copyRO(), a.copy(), new IntConstantOperand(0xFF));
	}
      }
      return null;
    }
    case Operators.Int2short: {
      if (FOLD_INTS && FOLD_2CONVERSION) {
	if (def instanceof Int2byte) {
	  // x = (byte)a; y = (short)x;
	  return new Int2byte(y.copyRO(), a.copy());
	} else if (def instanceof Int2short) {
	  // x = (short)a; y = (short)x;
	  return new Int2short(y.copyRO(), a.copy());
	} else if (def instanceof Int2ushort) {
	  // x = (char)a; y = (short)x;
	  return new Int2ushort(y.copyRO(), a.copy());
	}
      }
      return null;
    }
    case Operators.Int2ushort: {
      if (FOLD_INTS && FOLD_2CONVERSION) {
	if ((def instanceof Int2short) || (def instanceof Int2ushort)) {
	  // x = (short)a; y = (char)x;
	  return new Int2ushort(y.copyRO(), a.copy());
	}
      }
      return null;
    }

    case Operators.Long2int: {
      if (FOLD_LONGS && FOLD_2CONVERSION) {
	if (def instanceof Int2long) {
	  // x = (long)a; y = (int)x;
	  return new IntMove(y.copyRO(), a.copy());
	}
      }
      return null;
    }
    case Operators.Int2long:
      // unused
      return null;

    case Operators.Double2float: {
      if (FOLD_DOUBLES && FOLD_2CONVERSION) {
	if (def instanceof Float2double) {
	  // x = (double)a; y = (float)x;
	  return new FloatMove(y.copyRO(), a.copy());
	}
      }
      return null;
    }

    case Operators.Float2double:
      // unused
      return null;
    case Operators.IntZeroCheck: {
      if (FOLD_INTS && FOLD_CHECKS) {
	if (def instanceof IntNeg) {
	  // x = -z; y = zerocheck x;
	  return new IntZeroCheck(y.copyRO(), ((Unary)def).getVal().copy());
	}
      }
      return null;
    }
    case Operators.LongZeroCheck: {
      if (FOLD_INTS && FOLD_CHECKS) {
	if (def instanceof IntNeg) {
	  // x = -z; y = zerocheck x;
	  return new IntZeroCheck(y.copyRO(), ((Unary)def).getVal().copy());
	}
      }
      return null;
    }
    case Operators.Newarray:
      // unused
      return null;
    case Operators.BoundsCheck: {
      if (FOLD_CHECKS) {
	if (def instanceof Newarray) {
	  // x = newarray xxx[c1]; y = boundscheck x, c2;
	  int c1 = getIntValue(((NewArrayParent)def).getSize());
	  int c2 = getIntValue(((BoundsCheck)s).getIndex());
	  if (c2 >= 0 && c2 < c1) {
	    return new GuardMove(y.copyRO(), ((BoundsCheck)def).getGuard().copy());
	  }
	}
      }
      return null;
    }
    case Operators.NullCheck: {
      if (FOLD_CHECKS) {
	if ((def instanceof Newarray) || (def instanceof New)) {
	  // x = new xxx; y = nullcheck x;
	  return new GuardMove(y.copyRO(), new TrueGuardOperand());
	}
      }
      return null;
    }
    case Operators.InstanceOf: {
      if (FOLD_CHECKS) {
	Type newType;
	if (def instanceof New) {
	  // x = new xxx; y = instanceof x, zzz;
	  newType = ((New)def).getType().getType();
	} else if (def instanceof Newarray) {
	  // x = newarray xxx; y = instanceof x, zzz;
	  newType = ((NewArrayParent)def).getType().getType();
	} else {
	  return null;
	}
	Type instanceofType = ((InstanceOf)s).getType().getType();
	if (newType == instanceofType) {
	  return new IntMove(y.copyRO(), new IntConstantOperand(1));
	} else {
	  return new IntMove(y.copyRO(), new IntConstantOperand(Type.includesType(instanceofType, newType) ? 1 : 0));
	}
      }
      return null;
    }
    case Operators.Arraylength: {
      if (FOLD_CHECKS) {
	if (def instanceof Newarray) {
	  // x = newarray xxx[c1]; y = arraylength x;
	  return new IntMove(y.copyRO(), ((NewArrayParent)def).getSize().copy());
	}
      }
      return null;
    }
    default:
      throw new Error();
    }
  }

  /**
   * Does instruction s compute a register r = candidate expression?
   *
   * @param s the instruction
   * @param ssa are we in SSA form?
   * @return the computed register, or null
   */
  private static Register isCandidateExpression(Instruction s, boolean ssa) {

    switch (s.getOpcode()) {
    // Foldable operators
    case Operators.BooleanNot:
    case Operators.IntNot:
    case Operators.RefNot:
    case Operators.LongNot:

    case Operators.IntNeg:
    case Operators.RefNeg:
    case Operators.LongNeg:
    case Operators.FloatNeg:
    case Operators.DoubleNeg:

    case Operators.Int2byte:
    case Operators.Int2short:
    case Operators.Int2ushort:
    case Operators.Int2long:
    case Operators.Long2int:
    case Operators.Float2double:
    case Operators.Double2float: {
      Operand val1 = ((Unary)s).getVal();
      // if val1 is constant too, this should've been constant folded
      // beforehand. Give up.
      if (val1.isConstant()) {
	return null;
      }
      Register result = ((Unary)s).getResult().asRegister().getRegister();
      if (ssa) {
	return result;
      } else if (val1.asRegister().getRegister() != result) {
	return result;
      } else {
	return null;
      }
    }

    case Operators.Arraylength: {
      Operand val1 = ((GuardedUnary)s).getVal();
      // if val1 is constant too, this should've been constant folded
      // beforehand. Give up.
      if (val1.isConstant()) {
	return null;
      }
      Register result = ((GuardedUnary)s).getResult().asRegister().getRegister();
      // don't worry about the input and output bring the same as their types differ
      return result;
    }

    case Operators.IntAdd:
    case Operators.RefAdd:
    case Operators.LongAdd:
    case Operators.FloatAdd:
    case Operators.DoubleAdd:

    case Operators.IntSub:
    case Operators.RefSub:
    case Operators.LongSub:
    case Operators.FloatSub:
    case Operators.DoubleSub:

    case Operators.IntMul:
    case Operators.LongMul:
    case Operators.FloatMul:
    case Operators.DoubleMul:

    case Operators.FloatDiv:
    case Operators.DoubleDiv:

    case Operators.IntShl:
    case Operators.RefShl:
    case Operators.LongShl:

    case Operators.IntShr:
    case Operators.RefShr:
    case Operators.LongShr:

    case Operators.IntUshr:
    case Operators.RefUshr:
    case Operators.LongUshr:

    case Operators.IntAnd:
    case Operators.RefAnd:
    case Operators.LongAnd:

    case Operators.IntOr:
    case Operators.RefOr:
    case Operators.LongOr:

    case Operators.IntXor:
    case Operators.RefXor:
    case Operators.LongXor:

    case Operators.LongCmp:
    case Operators.FloatCmpl:
    case Operators.DoubleCmpl:
    case Operators.FloatCmpg:
    case Operators.DoubleCmpg: {

      Operand val2 = ((Binary)s).getVal2();
      if (!val2.isObjectConstant()) {
	if (val2.isConstant()) {
	  Operand val1 = ((Binary)s).getVal1();
	  // if val1 is constant too, this should've been constant folded
	  // beforehand. Give up.
	  if (val1.isConstant()) {
	    return null;
	  }

	  Register result = ((Binary)s).getResult().asRegister().getRegister();
	  if (ssa) {
	    return result;
	  } else if (val1.asRegister().getRegister() != result) {
	    return result;
	  } else {
	    return null;
	  }
	} else {
	  assert val2.isRegister();

	  Operand val1 = ((Binary)s).getVal1();
	  if (s.isCommutative() && val1.isConstant() && !val1.isMovableObjectConstant()) {
	    ((Binary)s).setVal1(((Binary)s).getClearVal2());
	    ((Binary)s).setVal2(val1);
	    Register result = ((Binary)s).getResult().asRegister().getRegister();
	    if (ssa) {
	      return result;
	    } else if (val2.asRegister().getRegister() != result) {
	      return result;
	    } else {
	      return null;
	    }
	  }
	}
      }
      return null;
    }

    case Operators.IntDiv:
    case Operators.LongDiv: {
      Operand val2 = ((GuardedBinary)s).getVal2();
      if (val2.isConstant()) {
	Operand val1 = ((GuardedBinary)s).getVal1();
	// if val1 is constant too, this should've been constant folded
	// beforehand. Give up.
	if (val1.isConstant()) {
	  return null;
	}
	Register result = ((GuardedBinary)s).getResult().asRegister().getRegister();
	if (ssa) {
	  return result;
	} else if (val1.asRegister().getRegister() != result) {
	  return result;
	}
      }
      return null;
    }

    case Operators.BooleanCmpInt:
    case Operators.BooleanCmpLong:
    case Operators.BooleanCmpAddr: {
      Operand val2 = ((BooleanCmp)s).getVal2();
      if (val2.isConstant() && !val2.isMovableObjectConstant()) {
	Operand val1 = ((BooleanCmp)s).getVal1();
	// if val1 is constant too, this should've been constant folded
	// beforehand. Give up.
	if (val1.isConstant()) {
	  return null;
	}
	Register result = ((BooleanCmp)s).getResult().asRegister().getRegister();
	if (ssa) {
	  return result;
	} else if (val1.asRegister().getRegister() != result) {
	  return result;
	}
      } else if (val2.isRegister()) {
	Operand val1 = ((BooleanCmp)s).getVal1();
	if (val1.isConstant() && !val1.isMovableObjectConstant()) {
	  ((BooleanCmp)s).setVal1(((BooleanCmp)s).getClearVal2());
	  ((BooleanCmp)s).setVal2(val1);
	  ((BooleanCmp)s).getCond().flipOperands();
	  Register result = ((BooleanCmp)s).getResult().asRegister().getRegister();
	  if (ssa) {
	    return result;
	  } else if (val2.asRegister().getRegister() != result) {
	    return result;
	  }
	}
      }
      return null;
    }
    case Operators.IntIfcmp:
    case Operators.LongIfcmp:
    case Operators.FloatIfcmp:
    case Operators.DoubleIfcmp:
    case Operators.RefIfcmp: {
      Operand val2 = ((IfCmp)s).getVal2();
      if (!val2.isObjectConstant()) {
	if (val2.isConstant()) {
	  Operand val1 = ((IfCmp)s).getVal1();
	  // if val1 is constant too, this should've been constant folded
	  // beforehand. Give up.
	  if (val1.isConstant()) {
	    return null;
	  }

	  Register result = ((IfCmp)s).getGuardResult().asRegister().getRegister();
	  if (ssa) {
	    return result;
	  } else if (val1.asRegister().getRegister() != result) {
	    return result;
	  }
	} else {
	  assert val2.isRegister();
	  
	  Operand val1 = ((IfCmp)s).getVal1();
	  if (val1.isConstant() && !val1.isMovableObjectConstant()) {
	    ((IfCmp)s).setVal1(((IfCmp)s).getClearVal2());
	    ((IfCmp)s).setVal2(val1);
	    ((IfCmp)s).getCond().flipOperands();
	    Register result = ((IfCmp)s).getGuardResult().asRegister().getRegister();
	    if (ssa) {
	      return result;
	    } else if (val2.asRegister().getRegister() != result) {
	      return result;
	    }
	  }
	}
      }
      return null;
    }
    case Operators.IntIfcmp2: {
      Operand val2 = ((IfCmp2)s).getVal2();
      if (!val2.isObjectConstant()) {
	if (val2.isConstant()) {
	  Operand val1 = ((IfCmp2)s).getVal1();
	  // if val1 is constant too, this should've been constant folded
	  // beforehand. Give up.
	  if (val1.isConstant()) {
	    return null;
	  }

	  Register result = ((IfCmp2)s).getGuardResult().asRegister().getRegister();
	  if (ssa) {
	    return result;
	  } else if (val1.asRegister().getRegister() != result) {
	    return result;
	  }
	} else {
	  assert val2.isRegister();
	  
	  Operand val1 = ((IfCmp2)s).getVal1();
	  if (val1.isConstant() && !val1.isMovableObjectConstant()) {
	    ((IfCmp2)s).setVal1(((IfCmp2)s).getClearVal2());
	    ((IfCmp2)s).setVal2(val1);
	    ((IfCmp2)s).getCond1().flipOperands();
	    ((IfCmp2)s).getCond2().flipOperands();
	    Register result = ((IfCmp2)s).getGuardResult().asRegister().getRegister();
	    if (ssa) {
	      return result;
	    } else if (val2.asRegister().getRegister() != result) {
	      return result;
	    }
	  }
	}
      }
      return null;
    }
    case Operators.IntCondMove:
    case Operators.LongCondMove:
    case Operators.RefCondMove:
    case Operators.FloatCondMove:
    case Operators.DoubleCondMove:
    case Operators.GuardCondMove: {
      Operand val2 = ((CondMove)s).getVal2();
      if (!val2.isObjectConstant()) {
	if (val2.isConstant()) {
	  Operand val1 = ((CondMove)s).getVal1();
	  // if val1 is constant too, this should've been constant folded
	  // beforehand. Give up.
	  if (val1.isConstant()) {
	    return null;
	  }
	  Register result = ((CondMove)s).getResult().asRegister().getRegister();
	  if (ssa) {
	    return result;
	  } else if (val1.asRegister().getRegister() != result) {
	    return result;
	  }
	} else {
	  assert val2.isRegister();

	  Operand val1 = ((CondMove)s).getVal1();
	  if (val1.isConstant() && !val1.isMovableObjectConstant()) {
	    ((CondMove)s).setVal1(((CondMove)s).getClearVal2());
	    ((CondMove)s).setVal2(val1);
	    ((CondMove)s).getCond().flipOperands();
	    Register result = ((CondMove)s).getResult().asRegister().getRegister();
	    if (ssa) {
	      return result;
	    } else if (val2.asRegister().getRegister() != result) {
	      return result;
	    }
	  }
	}
      }
      return null;
    }
    case Operators.BoundsCheck: {
      Operand ref = ((BoundsCheck)s).getRef();
      Operand index = ((BoundsCheck)s).getIndex();
      if (index.isConstant()) {
	if (ref.isConstant()) {
	  // this should have been constant folded. Give up.
	  return null;
	}
	// don't worry about the input and output bring the same as their types differ
	return ((BoundsCheck)s).getGuardResult().asRegister().getRegister();
      }
      return null;
    }
    case Operators.NullCheck: {
      Operand ref = ((NullCheck)s).getRef();
      if (ref.isConstant()) {
	// this should have been constant folded. Give up.
	return null;
      }
      // don't worry about the input and output bring the same as their types differ
      return ((NullCheck)s).getGuardResult().asRegister().getRegister();
    }
    case Operators.InstanceOf: {
      Operand ref = ((InstanceOf)s).getRef();
      if (ref.isConstant()) {
	// this should have been constant folded. Give up.
	return null;
      }
      // don't worry about the input and output bring the same as their types differ
      return ((InstanceOf)s).getResult().asRegister().getRegister();
    }
    case Operators.Newarray: {
      Operand size = ((NewArrayParent)s).getSize();
      if (size.isConstant()) {
	// don't worry about the input and output bring the same as their types differ
	return ((NewArrayParent)s).getResult().asRegister().getRegister();
      }
      return null;
    }
    case Operators.New: {
      return ((New)s).getResult().asRegister().getRegister();
    }
    case Operators.IntZeroCheck:
    case Operators.LongZeroCheck:  {
      Operand val1 = ((ZeroCheck)s).getValue();
      // if val1 is constant, this should've been constant folded
      // beforehand. Give up.
      if (val1.isConstant()) {
	return null;
      }
      // don't worry about the input and output bring the same as their types differ
      return ((ZeroCheck)s).getGuardResult().asRegister().getRegister();
    }
    default:
      // Operator can't be folded
      return null;
    }
  }

  private static int getIntValue(Operand op) {
    if (op instanceof IntConstantOperand) {
      return op.asIntConstant().value;
    }

    throw new Error(
	"Cannot getIntValue from this operand " + op +
	" of instruction " + op.instruction);
  }

  private static long getLongValue(Operand op) {
    if (op instanceof LongConstantOperand)
      return op.asLongConstant().value;

    throw new Error(
	"Cannot getLongValue from this operand " + op +
	" of instruction " + op.instruction);
  }

  private static float getFloatValue(Operand op) {
    if (op instanceof FloatConstantOperand)
      return op.asFloatConstant().value;
    throw new Error(
	"Cannot getFloatValue from this operand " + op +
	" of instruction " + op.instruction);
  }

  private static double getDoubleValue(Operand op) {
    if (op instanceof DoubleConstantOperand)
      return op.asDoubleConstant().value;
    throw new Error(
	"Cannot getDoubleValue from this operand " + op +
	" of instruction " + op.instruction);
  }

  private static Address getAddressValue(Operand op) {
    if (op instanceof NullConstantOperand) {
      return Address.zero();
    }
    if (op instanceof AddressConstantOperand) {
      return op.asAddressConstant().value;
    }
    if (op instanceof IntConstantOperand) {
      return Address.fromIntSignExtend(op.asIntConstant().value);
    }
    if (op instanceof LongConstantOperand) {
      return Address.fromLong(op.asLongConstant().value);
    }
    if (op instanceof ObjectConstantOperand) {
      assert !op.isMovableObjectConstant();
      return Address.objectAsAddress(op.asObjectConstant().value);
    }
    throw new Error(
	"Cannot getAddressValue from this operand " + op +
	" of instruction " + op.instruction);
  }
}
