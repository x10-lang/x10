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


import static x10me.opt.ir.Operators.*;


import x10me.opt.driver.OptConstants;
import x10me.opt.driver.OptOptions;
import x10me.opt.driver.SizeConstants;
import x10me.opt.inlining.InlineSequence;
import x10me.opt.ir.*;
import x10me.opt.ir.operand.*;
import x10me.types.Address;
import x10me.types.Field;
import x10me.types.KnownTypes;
import x10me.types.Method;
import x10me.types.Offset;
import x10me.types.Type;
import x10me.types.Word;


/**
 * A constant folder, strength reducer and axiomatic simplifier.
 *
 * <p> This module performs no analysis, it simply attempts to
 * simplify the instruction as is. The intent is that
 * analysis modules can call this transformation engine, allowing us to
 * share the tedious simplification code among multiple analysis modules.
 *
 * <p> NOTE: For maintainability purposes, I've intentionally avoided being
 * clever about combining 'similar' operators together into a combined case
 * of the main switch switch statement. Also, operators are in sorted ordered
 * within each major grouping.  Please maintain this coding style.
 * I'd rather have this module be 2000 lines of obviously correct code than
 * 500 lines of clever code.
 */
public abstract class Simplifier {
  /**
   * Given an instruction, attempt to simplify it.
   * The instruction will be mutated in place.
   *
   * <p> We don't deal with branching operations here --
   * doing peephole optimizations of branches
   * is the job of a separate module.
   *
   * @param regpool register pool in case simplification requires a temporary register
   * @param opts options for this compilation
   * @param s the instruction to simplify
   * @return one of UNCHANGED, MOVEFOLDED, MOVEREDUCED, TRAPREDUCED, REDUCED
   */
  public static Instruction simplify(RegisterPool regpool, OptOptions opts, Instruction s) {
    Instruction result;
    char opcode = s.getOpcode();
    switch (opcode) {
    ////////////////////
    // GUARD operations
    ////////////////////
    case Operators.GuardCombine:
      result = guardCombine(s, opts);
      break;
      ////////////////////
      // TRAP operations
      ////////////////////
    case Operators.NullCheck:
      result = nullCheck(s, opts);
      break;
    case Operators.IntZeroCheck:
      result = intZeroCheck(s, opts);
      break;
    case Operators.LongZeroCheck:
      result = longZeroCheck(s, opts);
      break;
    case Operators.Checkcast:
      result = checkcast(s, opts);
      break;
    case Operators.CheckcastUnresolved:
      result = checkcast(s, opts);
      break;
    case Operators.CheckcastNotnull:
      result = checkcastNotNull(s, opts);
      break;
    case Operators.InstanceOf:
      result = instanceOf(s, opts);
      break;
    case Operators.InstanceOfNotnull:
      result = instanceOfNotNull(s, opts);
      break;
    case Operators.ObjArrayStoreCheck:
      result = objarrayStoreCheck(s, opts);
      break;
    case Operators.ObjArrayStoreCheckNotnull:
      result = objarrayStoreCheckNotNull(s, opts);
      break;
    case Operators.MustImplementInterface:
      result = mustImplementInterface(s, opts);
      break;
      ////////////////////
      // Conditional moves
      ////////////////////
    case Operators.IntCondMove:
      result = intCondMove(s, opts);
      break;
    case Operators.LongCondMove:
      result = longCondMove(s, opts);
      break;
    case Operators.FloatCondMove:
      result = floatCondMove(s, opts);
      break;
    case Operators.DoubleCondMove:
      result = doubleCondMove(s, opts);
      break;
    case Operators.RefCondMove:
      result = refCondMove(s, opts);
      break;
    case Operators.GuardCondMove:
      result = guardCondMove(s, opts);
      break;
      ////////////////////
      // INT ALU operations
      ////////////////////
    case Operators.BooleanNot:
      result = booleanNot(s, opts);
      break;
    case Operators.BooleanCmpInt:
      result = booleanCmpInt(s, opts);
      break;
    case Operators.BooleanCmpAddr:
      result = booleanCmpAddr(s, opts);
      break;
    case Operators.IntAdd:
      result = intAdd(s, opts);
      break;
    case Operators.IntAnd:
      result = intAnd(s, opts);
      break;
    case Operators.IntDiv:
      result = intDiv(regpool, s, opts);
      break;
    case Operators.IntMul:
      result = intMul(regpool, s, opts);
      break;
    case Operators.IntNeg:
      result = intNeg(s, opts);
      break;
    case Operators.IntNot:
      result = intNot(s, opts);
      break;
    case Operators.IntOr:
      result = intOr(s, opts);
      break;
    case Operators.IntRem:
      result = intRem(s, opts);
      break;
    case Operators.IntShl:
      result = intShl(s, opts);
      break;
    case Operators.IntShr:
      result = intShr(s, opts);
      break;
    case Operators.IntSub:
      result = intSub(s, opts);
      break;
    case Operators.IntUshr:
      result = intUshr(s, opts);
      break;
    case Operators.IntXor:
      result = intXor(s, opts);
      break;
      ////////////////////
      // WORD ALU operations
      ////////////////////
    case Operators.RefAdd:
      result = refAdd(s, opts);
      break;
    case Operators.RefAnd:
      result = refAnd(s, opts);
      break;
    case Operators.RefShl:
      result = refShl(s, opts);
      break;
    case Operators.RefShr:
      result = refShr(s, opts);
      break;
    case Operators.RefNeg:
      result = refNeg(s, opts);
      break;
    case Operators.RefNot:
      result = refNot(s, opts);
      break;
    case Operators.RefOr:
      result = refOr(s, opts);
      break;
    case Operators.RefSub:
      result = refSub(s, opts);
      break;
    case Operators.RefUshr:
      result = refUshr(s, opts);
      break;
    case Operators.RefXor:
      result = refXor(s, opts);
      break;
      ////////////////////
      // LONG ALU operations
      ////////////////////
    case Operators.LongAdd:
      result = longAdd(s, opts);
      break;
    case Operators.LongAnd:
      result = longAnd(s, opts);
      break;
    case Operators.LongCmp:
      result = longCmp(s, opts);
      break;
    case Operators.LongDiv:
      result = longDiv(s, opts);
      break;
    case Operators.LongMul:
      result = longMul(regpool, s, opts);
      break;
    case Operators.LongNeg:
      result = longNeg(s, opts);
      break;
    case Operators.LongNot:
      result = longNot(s, opts);
      break;
    case Operators.LongOr:
      result = longOr(s, opts);
      break;
    case Operators.LongRem:
      result = longRem(s, opts);
      break;
    case Operators.LongShl:
      result = longShl(s, opts);
      break;
    case Operators.LongShr:
      result = longShr(s, opts);
      break;
    case Operators.LongSub:
      result = longSub(s, opts);
      break;
    case Operators.LongUshr:
      result = longUshr(s, opts);
      break;
    case Operators.LongXor:
      result = longXor(s, opts);
      break;
      ////////////////////
      // FLOAT ALU operations
      ////////////////////
    case Operators.FloatAdd:
      result = floatAdd(s, opts);
      break;
    case Operators.FloatCmpg:
      result = floatCmpg(s, opts);
      break;
    case Operators.FloatCmpl:
      result = floatCmpl(s, opts);
      break;
    case Operators.FloatDiv:
      result = floatDiv(s, opts);
      break;
    case Operators.FloatMul:
      result = floatMul(s, opts);
      break;
    case Operators.FloatNeg:
      result = floatNeg(s, opts);
      break;
    case Operators.FloatRem:
      result = floatRem(s, opts);
      break;
    case Operators.FloatSub:
      result = floatSub(s, opts);
      break;
    case Operators.FloatSqrt:
      result = floatSqrt(s, opts);
      break;
      ////////////////////
      // DOUBLE ALU operations
      ////////////////////
    case Operators.DoubleAdd:
      result = doubleAdd(s, opts);
      break;
    case Operators.DoubleCmpg:
      result = doubleCmpg(s, opts);
      break;
    case Operators.DoubleCmpl:
      result = doubleCmpl(s, opts);
      break;
    case Operators.DoubleDiv:
      result = doubleDiv(s, opts);
      break;
    case Operators.DoubleMul:
      result = doubleMul(s, opts);
      break;
    case Operators.DoubleNeg:
      result = doubleNeg(s, opts);
      break;
    case Operators.DoubleRem:
      result = doubleRem(s, opts);
      break;
    case Operators.DoubleSub:
      result = doubleSub(s, opts);
      break;
    case Operators.DoubleSqrt:
      result = doubleSqrt(s, opts);
      break;
      ////////////////////
      // CONVERSION operations
      ////////////////////
    case Operators.Double2float:
      result = double2Float(s, opts);
      break;
    case Operators.Double2int:
      result = double2Int(s, opts);
      break;
    case Operators.Double2long:
      result = double2Long(s, opts);
      break;
    case Operators.DoubleAsLongBits:
      result = doubleAsLongBits(s, opts);
      break;
    case Operators.Int2double:
      result = int2Double(s, opts);
      break;
    case Operators.Int2byte:
      result = int2Byte(s, opts);
      break;
    case Operators.Int2ushort:
      result = int2UShort(s, opts);
      break;
    case Operators.Int2float:
      result = int2Float(s, opts);
      break;
    case Operators.Int2long:
      result = int2Long(s, opts);
      break;
    case Operators.Int2addrsigext:
      result = int2AddrSigExt(s, opts);
      break;
    case Operators.Int2addrzerext:
      result = int2AddrZerExt(s, opts);
      break;
    case Operators.Long2addr:
      result = long2Addr(s, opts);
      break;
    case Operators.Int2short:
      result = int2Short(s, opts);
      break;
    case Operators.IntBitsAsFloat:
      result = intBitsAsFloat(s, opts);
      break;
    case Operators.Addr2int:
      result = addr2Int(s, opts);
      break;
    case Operators.Addr2long:
      result = addr2Long(s, opts);
      break;
    case Operators.Float2double:
      result = float2Double(s, opts);
      break;
    case Operators.Float2int:
      result = float2Int(s, opts);
      break;
    case Operators.Float2long:
      result = float2Long(s, opts);
      break;
    case Operators.FloatAsIntBits:
      result = floatAsIntBits(s, opts);
      break;
    case Operators.Long2float:
      result = long2Float(s, opts);
      break;
    case Operators.Long2int:
      result = long2Int(s, opts);
      break;
    case Operators.Long2double:
      result = long2Double(s, opts);
      break;
    case Operators.LongBitsAsDouble:
      result = longBitsAsDouble(s, opts);
      break;
      ////////////////////
      // Field operations
      ////////////////////
    case Operators.Arraylength:
      result = arrayLength(s, opts);
      break;
    case Operators.BoundsCheck:
      result = boundsCheck(s, opts);
      break;
    case Operators.Call:
      result = call(regpool, s, opts);
      break;
    case Operators.GetField:
      result = getField(s, opts);
      break;
    case Operators.RefLoad:
      result = null;
      break;
    default:
      result = null;
    }
    return result;
  }

  private static Instruction guardCombine(Instruction s, OptOptions opts) {
    Operand op1 = ((Binary)s).getVal1();
    Operand op2 = ((Binary)s).getVal2();
    if (op1.similar(op2) || (op2 instanceof TrueGuardOperand)) {
      return s.replace(new GuardMove(((Binary)s).getClearResult(), op1));
    } else if (op1 instanceof TrueGuardOperand) {
      // ONLY OP1 IS TrueGuard: MOVE REDUCE
      return s.replace(new GuardMove(((Binary)s).getClearResult(), op2));
    } else {
      return null;
    }
  }

  private static Instruction nullCheck(Instruction s, OptOptions opts) {
    Operand ref = ((NullCheck)s).getRef();
    if (ref.isNullConstant() || (ref.isAddressConstant() && ref.asAddressConstant().value.isZero())) {
      return s.replace(new Trap(new TypeOperand (KnownTypes.NULL_POINTER_EXCEPTION_TYPE)));
    } else if (ref.isConstant()) {
      // object, string, class or non-null address constant

      // Make the slightly suspect assumption that all non-zero address
      // constants are actually valid pointers. Not necessarily true,
      // but unclear what else we can do.
      return s.replace(new GuardMove(((NullCheck)s).getClearGuardResult(), new TrueGuardOperand()));
    } else {
      return null;
    }
  }

  private static Instruction intZeroCheck(Instruction s, OptOptions opts) {
    {
      Operand op = ((ZeroCheck)s).getValue();
      if (op.isIntConstant()) {
	int val = op.asIntConstant().value;
	if (val == 0) {
	  return s.replace(new Trap(new TypeOperand(KnownTypes.ZERO_DIVIDE_EXCEPTION_TYPE)));
	} else {
	  return s.replace(new GuardMove(((ZeroCheck)s).getClearGuardResult(), new TrueGuardOperand()));
	}
      }
    }
    return null;
  }

  private static Instruction longZeroCheck(Instruction s, OptOptions opts) {
    {
      Operand op = ((ZeroCheck)s).getValue();
      if (op.isLongConstant()) {
	long val = op.asLongConstant().value;
	if (val == 0L) {
	  return s.replace(new Trap(new TypeOperand(KnownTypes.ZERO_DIVIDE_EXCEPTION_TYPE)));
	} else {
	  return s.replace(new GuardMove(((ZeroCheck)s).getClearGuardResult(), new TrueGuardOperand()));
	}
      }
    }
    return null;
  }
  
  private static Instruction checkcast(Instruction s, OptOptions opts) {
    Operand ref = ((TypeCheck)s).getRef();
    if (ref.isNullConstant()) {
      return s.replace(new RefMove(((TypeCheck)s).getResult(), ref));
    } else if (ref.isConstant()) {
      Checkcast cc = (Checkcast)s;
      Instruction newS = s.replace(new CheckcastNotnull(cc.getResult(), cc.getRef(), cc.getType(), cc.getGuard()));
      Instruction newnewS = checkcastNotNull(newS, opts);
      // Return the result of checkcastNotNull if it replaced anything.
      if (newnewS == null) {
	return newS;
      } else {
        return newnewS;
      }
    } else {
      Type lhsType = ((Checkcast)s).getType().getType();
      Type rhsType = ref.getType();
      if (Type.includesType(lhsType, rhsType)) {
	return s.replace(new RefMove(((TypeCheck)s).getResult(), ref));
      } else {
	// NOTE: Constants.NO can't help us because (T)null always succeeds
	return null;
      }
    }
  }

  private static Instruction checkcastNotNull(Instruction s, OptOptions opts) {
    Operand ref = ((TypeCheck)s).getRef();
    Type lhsType = ((TypeCheck)s).getType().getType();
    Type rhsType = ref.getType();
    if (Type.includesType(lhsType, rhsType)) {
      return s.replace(new RefMove(((TypeCheck)s).getResult(), ref));
    } else {
      if (rhsType != null && rhsType.isClassType() && rhsType.isFinal()) {
	// only final (or precise) rhs types can be optimized since rhsType may be conservative
	return s.replace(new Trap(new TypeOperand(KnownTypes.CLASS_CAST_EXCEPTION_TYPE)));
      } else {
	return null;
      }
    }
  }
  private static Instruction instanceOf(Instruction s, OptOptions opts) {
    Operand ref = ((InstanceOf)s).getRef();
    if (ref.isNullConstant()) {
      return s.replace(new IntMove(((InstanceOf)s).getClearResult(), new IntConstantOperand(0)));
    } else if (ref.isConstant()) {
      InstanceOf io = (InstanceOf)s;
      Instruction newS = s.replace(new InstanceOfNotnull(io.getResult(), io.getType(), io.getRef(), io.getGuard()));
      Instruction newnewS = instanceOfNotNull(s, opts);
      if (newnewS == null) {
	return newS;
      } else {
	return newnewS;
      }
    } else {
      Type lhsType = ((InstanceOf)s).getType().getType();
      Type rhsType = ref.getType();
      // NOTE: Constants.YES doesn't help because ref may be null and null instanceof T is false
      if (Type.includesType(lhsType, rhsType)) { 
	return null;
      } else {
	if (rhsType != null && rhsType.isClassType() && rhsType.isFinal()) {
	  // only final (or precise) rhs types can be optimized since rhsType may be conservative
	  return s.replace(new IntMove(((InstanceOf)s).getClearResult(), new IntConstantOperand(0)));
	} else {
	  return null;
	}
      } 
    }
  }

  private static Instruction instanceOfNotNull(Instruction s, OptOptions opts) {
    {
      Operand ref = ((InstanceOf)s).getRef();
      Type lhsType = ((InstanceOf)s).getType().getType();
      Type rhsType = ref.getType();
      if (Type.includesType(lhsType, rhsType)) {
	return s.replace(new IntMove(((InstanceOf)s).getClearResult(), new IntConstantOperand(1)));
      } else {
	if (rhsType != null && rhsType.isClassType() && rhsType.isFinal()) {
	  // only final (or precise) rhs types can be optimized since rhsType may be conservative
	  return s.replace(new IntMove(((InstanceOf)s).getClearResult(), new IntConstantOperand(0)));
	}
      }
    }
    return null;
  }

  private static Instruction objarrayStoreCheck(Instruction s, OptOptions opts) {
    Operand val = ((StoreCheck)s).getVal();
    if (val.isNullConstant()) {
      // Writing null into an array is trivially safe
      return s.replace(new GuardMove(((StoreCheck)s).getClearGuardResult(), ((StoreCheck)s).getClearGuard()));
    } else {
      Operand ref = ((StoreCheck)s).getRef();
      Type arrayTypeRef = ref.getType();
      if (!arrayTypeRef.isArrayType()) {
	// Caused by inlining new and type propogation
	return null;
      }
      Type typeOfIMElem = arrayTypeRef.getInnermostElementType();
      if (typeOfIMElem != null) {
	Type typeOfVal = val.getType();
	if ((typeOfIMElem == typeOfVal) &&
	    (typeOfIMElem.isPrimitiveType() || typeOfIMElem.isUnboxedType() || typeOfIMElem.isFinal())) {
	  // Writing something of a final type to an array of that
	  // final type is safe
	  return s.replace(new GuardMove(((StoreCheck)s).getClearGuardResult(), ((StoreCheck)s).getClearGuard()));
	}
      }
      final boolean refIsPrecise = ref.isConstant() || (ref.isRegister() && ref.asRegister().isPreciseType());
      if ((arrayTypeRef == KnownTypes.OBJECT_ARRAY_TYPE) && refIsPrecise) {
	// We know this to be an array of objects so any store must
	// be safe
	return s.replace(new GuardMove(((StoreCheck)s).getClearGuardResult(), ((StoreCheck)s).getClearGuard()));
      }
      final boolean valIsPrecise = val.isConstant() || (val.isRegister() && val.asRegister().isPreciseType());
      if (refIsPrecise && valIsPrecise) {
	// writing a known type of value into a known type of array
	if (Type.includesType(arrayTypeRef.getArrayElementType(), val.getType())) {
	  // all stores should succeed
	  return s.replace(new GuardMove(((StoreCheck)s).getClearGuardResult(), ((StoreCheck)s).getClearGuard()));
	} else {
	  // all stores will fail
	  return s.replace(new Trap(new TypeOperand(KnownTypes.ARRAY_STORE_EXCEPTION_TYPE)));
	}
      }
      return null;
    }
  }

  private static Instruction objarrayStoreCheckNotNull(Instruction s, OptOptions opts) {
    Operand val = ((StoreCheck)s).getVal();
    Operand ref = ((StoreCheck)s).getRef();
    Type arrayTypeRef = ref.getType();
    if (!arrayTypeRef.isArrayType()) {
      // Caused by inlining new and type propogation
      return null;
    }
    Type typeOfIMElem = arrayTypeRef.getInnermostElementType();
    if (typeOfIMElem != null) {
      Type typeOfVal = val.getType();
      if ((typeOfIMElem == typeOfVal) &&
	  (typeOfIMElem.isPrimitiveType() || typeOfIMElem.isUnboxedType() || typeOfIMElem.isFinal())) {
	// Writing something of a final type to an array of that
	// final type is safe
	return s.replace(new GuardMove(((StoreCheck)s).getClearGuardResult(), ((StoreCheck)s).getClearGuard()));
      }
    }
    final boolean refIsPrecise = ref.isConstant() || (ref.isRegister() && ref.asRegister().isPreciseType());
    if ((arrayTypeRef == KnownTypes.OBJECT_ARRAY_TYPE) && refIsPrecise) {
      // We know this to be an array of objects so any store must
      // be safe
      return s.replace(new GuardMove(((StoreCheck)s).getClearGuardResult(), ((StoreCheck)s).getClearGuard()));
    }
    final boolean valIsPrecise = val.isConstant() || (val.isRegister() && val.asRegister().isPreciseType());
    if (refIsPrecise && valIsPrecise) {
      // writing a known type of value into a known type of array
      if (Type.includesType(arrayTypeRef.getArrayElementType(), val.getType())) {
	// all stores should succeed
	return s.replace(new GuardMove(((StoreCheck)s).getClearGuardResult(), ((StoreCheck)s).getClearGuard()));
      } else {
	// all stores will fail
	return s.replace(new Trap(new TypeOperand(KnownTypes.ARRAY_STORE_EXCEPTION_TYPE)));
      }
    }
    return null;
  }

  private static Instruction mustImplementInterface(Instruction s, OptOptions opts) {
    Operand ref = ((TypeCheck)s).getRef();
    if (ref.isNullConstant()) {
      // Possible situation from constant propagation. This operation
      // is really a nop as a null_check should have happened already
      return s.replace(new Trap(new TypeOperand(KnownTypes.NULL_POINTER_EXCEPTION_TYPE)));
    } else {
      Type lhsType = ((TypeCheck)s).getType().getType(); // the interface that must be implemented
      Type rhsType = ref.getType();                     // our type
      if (Type.includesType(lhsType, rhsType)) {
	if (rhsType != null) {
	  if (rhsType.isClassType() && rhsType.isInterface()) {
	    /* This is exactly the kind of typing that could require us to raise an IncompatibleClassChangeError */
	    return null;
	  }
	  return s.replace(new RefMove(((TypeCheck)s).getResult(), ref));
	} else {
	  return null;
	}
      } else {
	if (rhsType != null && rhsType.isClassType() && rhsType.isFinal()) {
	  // only final (or precise) rhs types can be optimized since rhsType may be conservative
	  return s.replace(new Trap(new TypeOperand(KnownTypes.MUST_IMPLEMENT_INTERFACE_EXCEPTION)));
	}
      }
      return null;
    }
  }

  private static Instruction intCondMove(Instruction s, OptOptions opts) {
    {
      Operand val1 = ((CondMove)s).getVal1();
      Operand val2 = ((CondMove)s).getVal2();
      int cond = ((CondMove)s).getCond().evaluate(val1, val2);
      if (cond != ConditionOperand.UNKNOWN) {
	// BOTH CONSTANTS OR SIMILAR: FOLD
	Operand val =
	  (cond == ConditionOperand.TRUE) ? ((CondMove)s).getClearTrueValue() : ((CondMove)s).getClearFalseValue();
	  return s.replace(new IntMove(((CondMove)s).getClearResult(), val));
      }
      if (val1.isConstant() && !val2.isConstant()) {
	// Canonicalize by switching operands and fliping code.
	Operand tmp = ((CondMove)s).getClearVal1();
	((CondMove)s).setVal1(((CondMove)s).getClearVal2());
	((CondMove)s).setVal2(tmp);
	((CondMove)s).getCond().flipOperands();
      }
      Operand tv = ((CondMove)s).getTrueValue();
      Operand fv = ((CondMove)s).getFalseValue();
      if (tv.similar(fv)) {
	return s.replace(new IntMove(((CondMove)s).getClearResult(), tv));
      }
      if (tv.isIntConstant() && fv.isIntConstant() && !((CondMove)s).getCond().isFLOATINGPOINT()) {
	int itv = tv.asIntConstant().value;
	int ifv = fv.asIntConstant().value;
	if (itv == 1 && ifv == 0) {
	  return s.replace(BooleanCmp.create(val1.getType(),
		  ((CondMove)s).getClearResult(),
		  ((CondMove)s).getClearVal1(),
		  ((CondMove)s).getClearVal2(),
		  ((CondMove)s).getClearCond(),
		  new BranchProfileOperand()));
	}
	if (itv == 0 && ifv == 1) {
	  return s.replace(BooleanCmp.create(val1.getType(),
		  ((CondMove)s).getClearResult(),
		  ((CondMove)s).getClearVal1(),
		  ((CondMove)s).getClearVal2(),
		  ((CondMove)s).getClearCond().flipCode(),
		  new BranchProfileOperand()));
	}
      }
    }
    return null;
  }

  private static Instruction longCondMove(Instruction s, OptOptions opts) {
    {
      Operand val1 = ((CondMove)s).getVal1();
      Operand val2 = ((CondMove)s).getVal2();
      int cond = ((CondMove)s).getCond().evaluate(val1, val2);
      if (cond != ConditionOperand.UNKNOWN) {
	// BOTH CONSTANTS OR SIMILAR: FOLD
	Operand val =
	  (cond == ConditionOperand.TRUE) ? ((CondMove)s).getClearTrueValue() : ((CondMove)s).getClearFalseValue();
	  return s.replace(new LongMove(((CondMove)s).getClearResult(), val));
      }
      if (val1.isConstant() && !val2.isConstant()) {
	// Canonicalize by switching operands and fliping code.
	Operand tmp = ((CondMove)s).getClearVal1();
	((CondMove)s).setVal1(((CondMove)s).getClearVal2());
	((CondMove)s).setVal2(tmp);
	((CondMove)s).getCond().flipOperands();
      }
      Operand tv = ((CondMove)s).getTrueValue();
      Operand fv = ((CondMove)s).getFalseValue();
      if (tv.similar(fv)) {
	return s.replace(new LongMove(((CondMove)s).getClearResult(), tv));
      }
      if (tv.isLongConstant() && fv.isLongConstant() && !((CondMove)s).getCond().isFLOATINGPOINT()) {
	long itv = tv.asLongConstant().value;
	long ifv = fv.asLongConstant().value;
	if (itv == 1 && ifv == 0) {
	  return s.replace(BooleanCmp.create(val1.getType(),
		  ((CondMove)s).getClearResult(),
		  ((CondMove)s).getClearVal1(),
		  ((CondMove)s).getClearVal2(),
		  ((CondMove)s).getClearCond(),
		  new BranchProfileOperand()));
	}
	if (itv == 0 && ifv == 1) {
	  return s.replace(BooleanCmp.create(val1.getType(),
		  ((CondMove)s).getClearResult(),
		  ((CondMove)s).getClearVal1(),
		  ((CondMove)s).getClearVal2(),
		  ((CondMove)s).getClearCond().flipCode(),
		  new BranchProfileOperand()));
	}
      }
    }
    return null;
  }

  private static Instruction floatCondMove(Instruction s, OptOptions opts) {
    {
      Operand val1 = ((CondMove)s).getVal1();
      Operand val2 = ((CondMove)s).getVal2();
      int cond = ((CondMove)s).getCond().evaluate(val1, val2);
      if (cond != ConditionOperand.UNKNOWN) {
	// BOTH CONSTANTS OR SIMILAR: FOLD
	Operand val =
	  (cond == ConditionOperand.TRUE) ? ((CondMove)s).getClearTrueValue() : ((CondMove)s).getClearFalseValue();
	  return s.replace(new FloatMove(((CondMove)s).getClearResult(), val));
      }
      if (val1.isConstant() && !val2.isConstant()) {
	// Canonicalize by switching operands and fliping code.
	Operand tmp = ((CondMove)s).getClearVal1();
	((CondMove)s).setVal1(((CondMove)s).getClearVal2());
	((CondMove)s).setVal2(tmp);
	((CondMove)s).getCond().flipOperands();
      }
      Operand tv = ((CondMove)s).getTrueValue();
      Operand fv = ((CondMove)s).getFalseValue();
      if (tv.similar(fv)) {
	return s.replace(new FloatMove(((CondMove)s).getClearResult(), tv));
      }
    }
    return null;
  }

  private static Instruction doubleCondMove(Instruction s, OptOptions opts) {
    {
      Operand val1 = ((CondMove)s).getVal1();
      Operand val2 = ((CondMove)s).getVal2();
      int cond = ((CondMove)s).getCond().evaluate(val1, val2);
      if (cond != ConditionOperand.UNKNOWN) {
	// BOTH CONSTANTS OR SIMILAR: FOLD
	Operand val =
	  (cond == ConditionOperand.TRUE) ? ((CondMove)s).getClearTrueValue() : ((CondMove)s).getClearFalseValue();
	  return s.replace(new DoubleMove(((CondMove)s).getClearResult(), val));
      }
      if (val1.isConstant() && !val2.isConstant()) {
	// Canonicalize by switching operands and fliping code.
	Operand tmp = ((CondMove)s).getClearVal1();
	((CondMove)s).setVal1(((CondMove)s).getClearVal2());
	((CondMove)s).setVal2(tmp);
	((CondMove)s).getCond().flipOperands();
      }
      Operand tv = ((CondMove)s).getTrueValue();
      Operand fv = ((CondMove)s).getFalseValue();
      if (tv.similar(fv)) {
	return s.replace(new DoubleMove(((CondMove)s).getClearResult(), tv));
      }
    }
    return null;
  }

  private static Instruction refCondMove(Instruction s, OptOptions opts) {
    {
      Operand val1 = ((CondMove)s).getVal1();
      if (val1.isConstant()) {
	Operand val2 = ((CondMove)s).getVal2();
	if (val2.isConstant()) {
	  // BOTH CONSTANTS: FOLD
	  int cond = ((CondMove)s).getCond().evaluate(val1, val2);
	  if (cond != ConditionOperand.UNKNOWN) {
	    Operand val =
	      (cond == ConditionOperand.TRUE) ? ((CondMove)s).getClearTrueValue() : ((CondMove)s).getClearFalseValue();
	      return s.replace(new RefMove(((CondMove)s).getClearResult(), val));
	  }
	} else {
	  // Canonicalize by switching operands and fliping code.
	  Operand tmp = ((CondMove)s).getClearVal1();
	  ((CondMove)s).setVal1(((CondMove)s).getClearVal2());
	  ((CondMove)s).setVal2(tmp);
	  ((CondMove)s).getCond().flipOperands();
	}
      }
      if (((CondMove)s).getTrueValue().similar(((CondMove)s).getFalseValue())) {
	Operand val = ((CondMove)s).getClearTrueValue();
	return s.replace(new RefMove(((CondMove)s).getClearResult(), val));
      }
    }
    return null;
  }

  private static Instruction guardCondMove(Instruction s, OptOptions opts) {
    {
      Operand val1 = ((CondMove)s).getVal1();
      if (val1.isConstant()) {
	Operand val2 = ((CondMove)s).getVal2();
	if (val2.isConstant()) {
	  // BOTH CONSTANTS: FOLD
	  int cond = ((CondMove)s).getCond().evaluate(val1, val2);
	  if (cond == ConditionOperand.UNKNOWN) {
	    Operand val =
	      (cond == ConditionOperand.TRUE) ? ((CondMove)s).getClearTrueValue() : ((CondMove)s).getClearFalseValue();
	      return s.replace(new GuardMove(((CondMove)s).getClearResult(), val));
	  }
	} else {
	  // Canonicalize by switching operands and fliping code.
	  Operand tmp = ((CondMove)s).getClearVal1();
	  ((CondMove)s).setVal1(((CondMove)s).getClearVal2());
	  ((CondMove)s).setVal2(tmp);
	  ((CondMove)s).getCond().flipOperands();
	}
      }
      if (((CondMove)s).getTrueValue().similar(((CondMove)s).getFalseValue())) {
	Operand val = ((CondMove)s).getClearTrueValue();
	return s.replace(new GuardMove(((CondMove)s).getClearResult(), val));
      }
    }
    return null;
  }

  private static Instruction booleanNot(Instruction s, OptOptions opts) {
    if (opts.SIMPLIFY_INTEGER_OPS) {
      Operand op = ((Unary)s).getVal();
      if (op.isIntConstant()) {
	// CONSTANT: FOLD
	int val = op.asIntConstant().value;
	if (val == 0) {
	  return s.replace(new IntMove(((Unary)s).getClearResult(), new IntConstantOperand(1)));
	} else {
	  return s.replace(new IntMove(((Unary)s).getClearResult(), new IntConstantOperand(0)));
	}
      }
    }
    return null;
  }

  private static Instruction booleanCmpInt(Instruction s, OptOptions opts) {
    if (opts.SIMPLIFY_INTEGER_OPS) {
      Operand op1 = ((BooleanCmp)s).getVal1();
      Operand op2 = ((BooleanCmp)s).getVal2();
      if (op1.isConstant()) {
	if (op2.isConstant()) {
	  // BOTH CONSTANTS: FOLD
	  int cond = ((BooleanCmp)s).getCond().evaluate(op1, op2);
	  if (cond != ConditionOperand.UNKNOWN) {
	    return s.replace(new IntMove(((BooleanCmp)s).getResult(), (cond == ConditionOperand.TRUE) ? new IntConstantOperand(1) : new IntConstantOperand(0)));
	  }
	} else {
	  // Canonicalize by switching operands and fliping code.
	  ((BooleanCmp)s).setVal1(op2);
	  ((BooleanCmp)s).setVal2(op1);
	  ((BooleanCmp)s).getCond().flipOperands();
	  op2 = op1;
	  op1 = ((BooleanCmp)s).getVal1();
	}
      }
      // try to fold boolean compares involving one boolean constant
      // e.g.: x = (y == true)  ? true : false ==> x = y
      // or:   x = (y == false) ? true : false ==> x = !y
      if (op1.getType().isBooleanType() && op2.isConstant()) {
	ConditionOperand cond = ((BooleanCmp)s).getCond();
	int op2value = op2.asIntConstant().value;
	if ((cond.isNOT_EQUAL() && (op2value == 0)) || (cond.isEQUAL() && (op2value == 1))) {
	  return s.replace(new IntMove(((BooleanCmp)s).getResult(), op1));
	} else if ((cond.isEQUAL() && (op2value == 0)) || (cond.isNOT_EQUAL() && (op2value == 1))) {
	  return s.replace(new BooleanNot(((BooleanCmp)s).getResult(), op1));
	}
      }
    }
    return null;
  }

  private static Instruction booleanCmpAddr(Instruction s, OptOptions opts) {
    if (opts.SIMPLIFY_REF_OPS) {
      Operand op1 = ((BooleanCmp)s).getVal1();
      Operand op2 = ((BooleanCmp)s).getVal2();
      if (op1.isConstant()) {
	if (op2.isConstant()) {
	  // BOTH CONSTANTS: FOLD
	  int cond = ((BooleanCmp)s).getCond().evaluate(op1, op2);
	  if (cond != ConditionOperand.UNKNOWN) {
	    return s.replace(new RefMove(((BooleanCmp)s).getResult(), (cond == ConditionOperand.TRUE) ? new IntConstantOperand(1) : new IntConstantOperand(0)));
	  }
	} else {
	  // Canonicalize by switching operands and fliping code.
	  Operand tmp = ((BooleanCmp)s).getClearVal1();
	  ((BooleanCmp)s).setVal1(((BooleanCmp)s).getClearVal2());
	  ((BooleanCmp)s).setVal2(tmp);
	  ((BooleanCmp)s).getCond().flipOperands();
	}
      }
    }
    return null;
  }

  private static Instruction intAdd(Instruction s, OptOptions opts) {
    if (opts.SIMPLIFY_INTEGER_OPS) {
      canonicalizeCommutativeOperator(s);
      Operand op2 = ((Binary)s).getVal2();
      if (op2.isIntConstant()) {
	int val2 = op2.asIntConstant().value;
	Operand op1 = ((Binary)s).getVal1();
	if (op1.isIntConstant()) {
	  // BOTH CONSTANTS: FOLD
	  int val1 = op1.asIntConstant().value;
	  return s.replace(new IntMove(((Binary)s).getClearResult(), new IntConstantOperand(val1 + val2)));
	} else {
	  // ONLY OP2 IS CONSTANT: ATTEMPT TO APPLY AXIOMS
	  if (val2 == 0) {
	    return s.replace(new IntMove(((Binary)s).getClearResult(), ((Binary)s).getClearVal1()));
	  }
	}
      } else {
	Operand op1 = ((Binary)s).getVal1();
	if (op1.similar(op2)) {
	  // Adding something to itself is the same as a multiply by 2 so
	  // canonicalize as a shift left
	  return s.replace(new IntShl(((Binary)s).getClearResult(), op1, new IntConstantOperand(1)));
	}
      }
    }
    return null;
  }

  private static Instruction intAnd(Instruction s, OptOptions opts) {
    if (opts.SIMPLIFY_INTEGER_OPS) {
      canonicalizeCommutativeOperator(s);
      Operand op1 = ((Binary)s).getVal1();
      Operand op2 = ((Binary)s).getVal2();
      if (op1.similar(op2)) {
	// THE SAME OPERAND: x & x == x
	return s.replace(new IntMove(((Binary)s).getClearResult(), ((Binary)s).getClearVal1()));
      }
      if (op2.isIntConstant()) {
	int val2 = op2.asIntConstant().value;
	if (op1.isIntConstant()) {
	  // BOTH CONSTANTS: FOLD
	  int val1 = op1.asIntConstant().value;
	  return s.replace(new IntMove(((Binary)s).getClearResult(), new IntConstantOperand(val1 & val2)));
	} else {
	  // ONLY OP2 IS CONSTANT: ATTEMPT TO APPLY AXIOMS
	  if (val2 == 0) {                  // x & 0 == 0
	    return s.replace(new IntMove(((Binary)s).getClearResult(), new IntConstantOperand(0)));
	  }
	  if (val2 == -1) {                 // x & -1 == x & 0xffffffff == x
	    return s.replace(new IntMove(((Binary)s).getClearResult(), ((Binary)s).getClearVal1()));
	  }
	}
      }
    }
    return null;
  }

  private static Instruction intDiv(RegisterPool regpool, Instruction s, OptOptions opts) {
    if (opts.SIMPLIFY_INTEGER_OPS) {
      Operand op1 = ((GuardedBinary)s).getVal1();
      Operand op2 = ((GuardedBinary)s).getVal2();
      if (op1.similar(op2)) {
	// THE SAME OPERAND: x / x == 1
	return s.replace(new IntMove(((GuardedBinary)s).getClearResult(), new IntConstantOperand(1)));
      }
      if (op2.isIntConstant()) {
	int val2 = op2.asIntConstant().value;
	if (val2 == 0) {
	  // TODO: This instruction is actually unreachable.
	  // There will be an INT_ZERO_CHECK
	  // guarding this instruction that will result in an
	  // ArithmeticException.  We
	  // should probabbly just remove the INT_DIV as dead code.
	  return null;
	}
	if (op1.isIntConstant()) {
	  // BOTH CONSTANTS: FOLD
	  int val1 = op1.asIntConstant().value;
	  return s.replace(new IntMove(((GuardedBinary)s).getClearResult(), new IntConstantOperand(val1 / val2)));
	} else {
	  // ONLY OP2 IS CONSTANT: ATTEMPT TO APPLY AXIOMS
	  if (val2 == 1) {                  // x / 1 == x;
	    return s.replace(new IntMove(((GuardedBinary)s).getClearResult(), ((GuardedBinary)s).getClearVal1()));
	  }
	  // x / c == (x + (((1 << c) - 1) & (x >> 31))) >> c .. if c is power of 2
	  if (s.hasPrev()) {
	    int power = PowerOf2(val2);
	    if (power != -1) {
	      RegisterOperand tempInt1 = regpool.makeTempInt();
	      RegisterOperand tempInt2 = regpool.makeTempInt();
	      RegisterOperand tempInt3 = regpool.makeTempInt();
	      s.insertBefore(new IntShr(tempInt1, ((GuardedBinary)s).getVal1().copy(), new IntConstantOperand(31)));
	      s.insertBefore(new IntAnd(tempInt2, tempInt1.copyRO(), new IntConstantOperand((1 << power)-1)));
	      s.insertBefore(new IntAdd(tempInt3, tempInt2.copyRO(), ((GuardedBinary)s).getClearVal1()));
	      return s.replace(new IntShr(((GuardedBinary)s).getClearResult(), tempInt3.copyRO(), new IntConstantOperand(power)));
	    }
	  }
	}
      }
    }
    return null;
  }

  private static Instruction intMul(RegisterPool regpool, Instruction s, OptOptions opts) {
    if (opts.SIMPLIFY_INTEGER_OPS) {
      canonicalizeCommutativeOperator(s);
      Operand op2 = ((Binary)s).getVal2();
      if (op2.isIntConstant()) {
	Operand op1 = ((Binary)s).getVal1();
	if (op1.isIntConstant()) {
	  // BOTH CONSTANTS: FOLD
	  int val1 = op1.asIntConstant().value;
	  int val2 = op2.asIntConstant().value;
	  return s.replace(new IntMove(((Binary)s).getClearResult(), new IntConstantOperand(val1 * val2)));
	} else {
	  // ONLY OP2 IS CONSTANT
	  return multiplyByConstant(regpool, s, op1, op2, opts);
	}
      }
    }
    return null;
  }

  private static Instruction multiplyByConstant(RegisterPool regpool, Instruction s, Operand op1, Operand op2, OptOptions opts) {
    ConstantOperand zero;
    long val2;
    int numBits;
    if (op2.isIntConstant()) {
      val2 = op2.asIntConstant().value;
      zero = IntConstantOperand.zero;
      numBits = 32;
    } else {
      val2 = op2.asLongConstant().value;
      zero = LongConstantOperand.zero;
      numBits = 64;
    }
    // ATTEMPT TO APPLY AXIOMS
    if (val2 == 0) {                  // x * 0 == 0
      return s.replace(Move.create(op2.getType(), ((Binary)s).getClearResult(), zero.copy()));
    } else if (numBits == 32 && ((int)val2 == ((int)-val2))) { // x * MIN_INT == x << 31
      return s.replace(new IntShl(((Binary)s).getClearResult(), op1, new IntConstantOperand(31)));
    } else if (numBits == 64 && val2 == -val2) { // x * MIN_LONG == x << 63
      return s.replace(new LongShl(((Binary)s).getClearResult(), op1, new IntConstantOperand(63)));
    }
    // Try to reduce x*c into shift and adds, but only if cost is cheap
    if (s.hasPrev()) {
      // don't attempt to reduce if this instruction isn't
      // part of a well-formed sequence

      // Cost of shift and add replacement
      int cost = 0;
      boolean negative = val2 < 0;
      if (negative) {
	val2 = -val2;
	cost++;
      }
      if (numBits > SizeConstants.BITS_IN_ADDRESS) {
	for (int i = 1; i < SizeConstants.BITS_IN_ADDRESS; i++) {
	  if ((val2 & (1L << i)) != 0) {
	    // each 1 requires a shift and add
	    cost+=2;
	  }
	}
	for (int i = SizeConstants.BITS_IN_ADDRESS; i < numBits; i++) {
	  if ((val2 & (1L << i)) != 0) {
	    // when the shift is > than the bits in the address we can just 0
	    // the bottom word, make the cost cheaper
	    cost++;
	  }
	}
      } else {
	for (int i = 1; i < numBits; i++) {
	  if ((val2 & (1L << i)) != 0) {
	    // each 1 requires a shift and add
	    cost+=2;
	  }
	}
      }
      int targetCost;
      targetCost = 2;
    
      if (cost <= targetCost) {
	// generate shift and adds
	RegisterOperand val1Operand = op1.asRegister();
	RegisterOperand resultOperand = numBits == 32 ? regpool.makeTempInt() : regpool.makeTempLong();
	Instruction move;
	if ((val2 & 1) == 1) {
	  // result = val1 * 1
	  move = Move.create(op1.getType(), resultOperand, val1Operand);
	} else {
	  // result = 0
	  move = Move.create(op1.getType(), resultOperand, zero.copy());
	}
	s.insertBefore(move);
	int lastShift = 0;
	RegisterOperand lastShiftResult = null;
	boolean lastShiftWasShort = false;
	for (int i = 1; i < numBits; i++) {
	  if ((val2 & (1L << i)) != 0) {
	    Instruction shift;
	    RegisterOperand shiftResult = numBits == 32 ? regpool.makeTempInt() : regpool.makeTempLong();
	    lastShiftWasShort = false;
	    s.insertBefore(Shl.create(op1.getType(), shiftResult, val1Operand.copyRO(), new IntConstantOperand(i)));
	    lastShiftResult = shiftResult;
	    lastShift = i;
	    RegisterOperand addResult = numBits == 32 ? regpool.makeTempInt() : regpool.makeTempLong();
	    s.insertBefore(Add.create(op1.getType(), addResult, resultOperand.copyRO(), shiftResult.copyRO()));
	    resultOperand = addResult;
	  }
	}
	if (negative) {
	  return s.replace(Neg.create(op1.getType(), ((Binary)s).getClearResult(), resultOperand.copyRO()));
	} else {
	  return s.replace(Move.create(op1.getType(),((Binary)s).getClearResult(), resultOperand.copyRO()));
	}
      }
    }
    return null;
  }

  private static Instruction intNeg(Instruction s, OptOptions opts) {
    if (opts.SIMPLIFY_INTEGER_OPS) {
      Operand op = ((Unary)s).getVal();
      if (op.isIntConstant()) {
	// CONSTANT: FOLD
	int val = op.asIntConstant().value;
	return s.replace(new IntMove(((Unary)s).getClearResult(), new IntConstantOperand(-val)));
      }
    }
    return null;
  }

  private static Instruction intNot(Instruction s, OptOptions opts) {
    if (opts.SIMPLIFY_INTEGER_OPS) {
      Operand op = ((Unary)s).getVal();
      if (op.isIntConstant()) {
	// CONSTANT: FOLD
	int val = op.asIntConstant().value;
	return s.replace(new IntMove(((Unary)s).getClearResult(), new IntConstantOperand(~val)));
      }
    }
    return null;
  }

  private static Instruction intOr(Instruction s, OptOptions opts) {
    if (opts.SIMPLIFY_INTEGER_OPS) {
      canonicalizeCommutativeOperator(s);
      Operand op1 = ((Binary)s).getVal1();
      Operand op2 = ((Binary)s).getVal2();
      if (op1.similar(op2)) {
	// THE SAME OPERAND: x | x == x
	return s.replace(new IntMove(((Binary)s).getClearResult(), ((Binary)s).getClearVal1()));
      }
      if (op2.isIntConstant()) {
	int val2 = op2.asIntConstant().value;
	if (op1.isIntConstant()) {
	  // BOTH CONSTANTS: FOLD
	  int val1 = op1.asIntConstant().value;
	  return s.replace(new IntMove(((Binary)s).getClearResult(), new IntConstantOperand(val1 | val2)));
	} else {
	  // ONLY OP2 IS CONSTANT: ATTEMPT TO APPLY AXIOMS
	  if (val2 == -1) { // x | -1 == x | 0xffffffff == 0xffffffff == -1
	    return s.replace(new IntMove(((Binary)s).getClearResult(), new IntConstantOperand(-1)));
	  }
	  if (val2 == 0) {                  // x | 0 == x
	    return s.replace(new IntMove(((Binary)s).getClearResult(), ((Binary)s).getClearVal1()));
	  }
	}
      }
    }
    return null;
  }

  private static Instruction intRem(Instruction s, OptOptions opts) {
    if (opts.SIMPLIFY_INTEGER_OPS) {
      Operand op1 = ((GuardedBinary)s).getVal1();
      Operand op2 = ((GuardedBinary)s).getVal2();
      if (op1.similar(op2)) {
	// THE SAME OPERAND: x % x == 0
	return s.replace(new IntMove(((GuardedBinary)s).getClearResult(), new IntConstantOperand(0)));
      }
      if (op2.isIntConstant()) {
	int val2 = op2.asIntConstant().value;
	if (val2 == 0) {
	  // TODO: This instruction is actually unreachable.
	  // There will be an INT_ZERO_CHECK
	  // guarding this instruction that will result in an
	  // ArithmeticException.  We
	  // should probabbly just remove the INT_REM as dead code.
	  return null;
	}
	if (op1.isIntConstant()) {
	  // BOTH CONSTANTS: FOLD
	  int val1 = op1.asIntConstant().value;
	  return s.replace(new IntMove(((GuardedBinary)s).getClearResult(), new IntConstantOperand(val1 % val2)));
	} else {
	  // ONLY OP2 IS CONSTANT: ATTEMPT TO APPLY AXIOMS
	  if ((val2 == 1) || (val2 == -1)) {             // x % 1 == 0
	    return s.replace(new IntMove(((GuardedBinary)s).getClearResult(), new IntConstantOperand(0)));
	  }
	}
      }
    }
    return null;
  }

  private static Instruction intShl(Instruction s, OptOptions opts) {
    if (opts.SIMPLIFY_INTEGER_OPS) {
      Operand op2 = ((Binary)s).getVal2();
      Operand op1 = ((Binary)s).getVal1();
      if (op2.isIntConstant()) {
	int val2 = op2.asIntConstant().value;
	if (op1.isIntConstant()) {
	  // BOTH CONSTANTS: FOLD
	  int val1 = op1.asIntConstant().value;
	  return s.replace(new IntMove(((Binary)s).getClearResult(), new IntConstantOperand(val1 << val2)));
	} else {
	  // ONLY OP2 IS CONSTANT: ATTEMPT TO APPLY AXIOMS
	  if (val2 == 0) {                  // x << 0 == x
	    return s.replace(new IntMove(((Binary)s).getClearResult(), ((Binary)s).getClearVal1()));
	  }
	  if ((val2 >= SizeConstants.BITS_IN_INT) || (val2 < 0)) {  // x << 32 == 0
	    return s.replace(new IntMove(((Binary)s).getClearResult(), new IntConstantOperand(0)));
	  }
	}
      } else if (op1.isIntConstant()) {
	int val1 = op1.asIntConstant().value;
	// ONLY OP1 IS CONSTANT: ATTEMPT TO APPLY AXIOMS
	if (val1 == 0) {                  // 0 << x == 0
	  return s.replace(new IntMove(((Binary)s).getClearResult(), new IntConstantOperand(0)));
	}
      }
    }
    return null;
  }

  private static Instruction intShr(Instruction s, OptOptions opts) {
    if (opts.SIMPLIFY_INTEGER_OPS) {
      Operand op1 = ((Binary)s).getVal1();
      Operand op2 = ((Binary)s).getVal2();
      if (op2.isIntConstant()) {
	int val2 = op2.asIntConstant().value;
	if (op1.isIntConstant()) {
	  // BOTH CONSTANTS: FOLD
	  int val1 = op1.asIntConstant().value;
	  return s.replace(new IntMove(((Binary)s).getClearResult(), new IntConstantOperand(val1 >> val2)));
	} else {
	  // ONLY OP2 IS CONSTANT: ATTEMPT TO APPLY AXIOMS
	  if (val2 == 0) {                  // x >> 0 == x
	    return s.replace(new IntMove(((Binary)s).getClearResult(), ((Binary)s).getClearVal1()));
	  }
	  if ((val2 >= SizeConstants.BITS_IN_INT) || (val2 < 0)) { // x >> 32 == x >> 31
	    ((Binary)s).setVal2(new IntConstantOperand(31));
	    return null;
	  }
	}
      } else if (op1.isIntConstant()) {
	int val1 = op1.asIntConstant().value;
	// ONLY OP1 IS CONSTANT: ATTEMPT TO APPLY AXIOMS
	if ((val1 == -1) || (val1 == 0)) { // -1 >> x == -1,0 >> x == 0
	  return s.replace(new IntMove(((Binary)s).getClearResult(), op1));
	}
      }
    }
    return null;
  }

  private static Instruction intSub(Instruction s, OptOptions opts) {
    if (opts.SIMPLIFY_INTEGER_OPS) {
      Operand op1 = ((Binary)s).getVal1();
      Operand op2 = ((Binary)s).getVal2();
      if (op1.similar(op2)) {
	// THE SAME OPERAND: x - x == 0
	return s.replace(new IntMove(((Binary)s).getClearResult(), new IntConstantOperand(0)));
      }
      if (op2.isIntConstant()) {
	int val2 = op2.asIntConstant().value;
	if (op1.isIntConstant()) {
	  // BOTH CONSTANTS: FOLD
	  int val1 = op1.asIntConstant().value;
	  return s.replace(new IntMove(((Binary)s).getClearResult(), new IntConstantOperand(val1 - val2)));
	} else {
	  // ONLY OP2 IS CONSTANT: ATTEMPT TO APPLY AXIOMS
	  if (val2 == 0) {                  // x - 0 == x
	    return s.replace(new IntMove(((Binary)s).getClearResult(), ((Binary)s).getClearVal1()));
	  }
	  // x - c = x + -c
	  // prefer adds, since some architectures have addi but not subi, also
	  // add is commutative and gives greater flexibility to LIR/MIR phases
	  // without possibly introducing temporary variables
	  return s.replace(new IntAdd(((Binary)s).getClearResult(), ((Binary)s).getClearVal1(), new IntConstantOperand(-val2)));
	}
      } else if (op1.isIntConstant() && (op1.asIntConstant().value == 0)) {
	return s.replace(new IntNeg(((Binary)s).getClearResult(), ((Binary)s).getClearVal2()));
      }
    }
    return null;
  }

  private static Instruction intUshr(Instruction s, OptOptions opts) {
    if (opts.SIMPLIFY_INTEGER_OPS) {
      Operand op2 = ((Binary)s).getVal2();
      Operand op1 = ((Binary)s).getVal1();
      if (op2.isIntConstant()) {
	int val2 = op2.asIntConstant().value;
	if (op1.isIntConstant()) {
	  // BOTH CONSTANTS: FOLD
	  int val1 = op1.asIntConstant().value;
	  return s.replace(new IntMove(((Binary)s).getClearResult(), new IntConstantOperand(val1 >>> val2)));
	} else {
	  // ONLY OP2 IS CONSTANT: ATTEMPT TO APPLY AXIOMS
	  if (val2 == 0) {                  // x >>> 0 == x
	    return s.replace(new IntMove(((Binary)s).getClearResult(), ((Binary)s).getClearVal1()));
	  }
	  if ((val2 >= SizeConstants.BITS_IN_INT) || (val2 < 0)) { // x >>> 32 == 0
	    return s.replace(new IntMove(((Binary)s).getClearResult(), new IntConstantOperand(0)));
	  }
	}
      } else if (op1.isIntConstant()) {
	int val1 = op1.asIntConstant().value;
	// ONLY OP1 IS CONSTANT: ATTEMPT TO APPLY AXIOMS
	if (val1 == 0) {                  // 0 >>> x == 0
	  return s.replace(new IntMove(((Binary)s).getClearResult(), new IntConstantOperand(0)));
	}
      }
    }
    return null;
  }

  private static Instruction intXor(Instruction s, OptOptions opts) {
    if (opts.SIMPLIFY_INTEGER_OPS) {
      canonicalizeCommutativeOperator(s);
      Operand op1 = ((Binary)s).getVal1();
      Operand op2 = ((Binary)s).getVal2();
      if (op1.similar(op2)) {
	// THE SAME OPERAND: x ^ x == 0
	return s.replace(new IntMove(((Binary)s).getClearResult(), new IntConstantOperand(0)));
      }
      if (op2.isIntConstant()) {
	int val2 = op2.asIntConstant().value;

	if (op1.isIntConstant()) {
	  // BOTH CONSTANTS: FOLD
	  int val1 = op1.asIntConstant().value;
	  return s.replace(new IntMove(((Binary)s).getClearResult(), new IntConstantOperand(val1 ^ val2)));
	} else {
	  // ONLY OP2 IS CONSTANT: ATTEMPT TO APPLY AXIOMS
	  if (val2 == -1) {                 // x ^ -1 == x ^ 0xffffffff = ~x
	    return s.replace(new IntNot(((Binary)s).getClearResult(), ((Binary)s).getClearVal1()));
	  }
	  if (val2 == 0) {                  // x ^ 0 == x
	    return s.replace(new IntMove(((Binary)s).getClearResult(), ((Binary)s).getClearVal1()));
	  }
	}
      }
    }
    return null;
  }

  private static Instruction refAdd(Instruction s, OptOptions opts) {
    if (opts.SIMPLIFY_REF_OPS) {
      canonicalizeCommutativeOperator(s);
      Operand op2 = ((Binary)s).getVal2();
      if (op2.isConstant() && !op2.isMovableObjectConstant()) {
	Address val2 = getAddressValue(op2);
	Operand op1 = ((Binary)s).getVal1();
	if (op1.isConstant() && !op1.isMovableObjectConstant()) {
	  // BOTH CONSTANTS: FOLD
	  Address val1 = getAddressValue(op1);
	  return s.replace(new RefMove(((Binary)s).getClearResult(), new AddressConstantOperand(val1.plus(val2.toWord().toOffset()))));
	} else {
	  // ONLY OP2 IS CONSTANT: ATTEMPT TO APPLY AXIOMS
	  if (val2.isZero()) {                 // x + 0 == x
	    if (op1.isIntLike()) {
	      return s.replace(new Int2addrsigext(((Binary)s).getClearResult(), ((Binary)s).getClearVal1()));
	    } else {
	      return s.replace(new RefMove(((Binary)s).getClearResult(), ((Binary)s).getClearVal1()));
	    }
	  }
	}
      } else {
	Operand op1 = ((Binary)s).getVal1();
	if (op1.similar(op2)) {
	  // Adding something to itself is the same as a multiply by 2 so
	  // canonicalize as a shift left
	  return s.replace(new RefShl(((Binary)s).getClearResult(), op1, new IntConstantOperand(1)));
	}
      }
    }
    return null;
  }

  private static Instruction refAnd(Instruction s, OptOptions opts) {
    if (opts.SIMPLIFY_REF_OPS) {
      canonicalizeCommutativeOperator(s);
      Operand op1 = ((Binary)s).getVal1();
      Operand op2 = ((Binary)s).getVal2();
      if (op1.similar(op2)) {
	// THE SAME OPERAND: x & x == x
	return s.replace(new RefMove(((Binary)s).getClearResult(), ((Binary)s).getClearVal1()));
      }
      if (op2.isConstant() && !op2.isMovableObjectConstant()) {
	Word val2 = getAddressValue(op2).toWord();
	if (op1.isConstant() && !op1.isMovableObjectConstant()) {
	  // BOTH CONSTANTS: FOLD
	  Word val1 = getAddressValue(op1).toWord();
	  return s.replace(new RefMove(((Binary)s).getClearResult(), new AddressConstantOperand(val1.and(val2).toAddress())));
	} else {
	  // ONLY OP2 IS CONSTANT: ATTEMPT TO APPLY AXIOMS
	  if (val2.isZero()) {                  // x & 0 == 0
	    return s.replace(new RefMove(((Binary)s).getClearResult(), new AddressConstantOperand(Address.zero())));
	  }
	  if (val2.isMax()) {                 // x & -1 == x & 0xffffffff == x
	    return s.replace(new RefMove(((Binary)s).getClearResult(), ((Binary)s).getClearVal1()));
	  }
	}
      }
    }
    return null;
  }

  private static Instruction refShl(Instruction s, OptOptions opts) {
    if (opts.SIMPLIFY_REF_OPS) {
      Operand op2 = ((Binary)s).getVal2();
      Operand op1 = ((Binary)s).getVal1();
      if (op2.isIntConstant()) {
	int val2 = op2.asIntConstant().value;
	if (op1.isConstant() && !op1.isMovableObjectConstant()) {
	  // BOTH CONSTANTS: FOLD
	  Word val1 = getAddressValue(op1).toWord();
	  return s.replace(new RefMove(((Binary)s).getClearResult(), new AddressConstantOperand(val1.lsh(val2).toAddress())));
	} else {
	  // ONLY OP2 IS CONSTANT: ATTEMPT TO APPLY AXIOMS
	  if (val2 == 0) {                  // x << 0 == x
	    return s.replace(new RefMove(((Binary)s).getClearResult(), ((Binary)s).getClearVal1()));
	  }
	  if ((val2 >= SizeConstants.BITS_IN_ADDRESS) || (val2 < 0)) { // x << 32 == 0
	    return s.replace(new RefMove(((Binary)s).getClearResult(), new IntConstantOperand(0)));
	  }
	}
      } else if (op1.isConstant() && !op1.isMovableObjectConstant()) {
	Word val1 = getAddressValue(op1).toWord();
	// ONLY OP1 IS CONSTANT: ATTEMPT TO APPLY AXIOMS
	if (val1.isZero()) {                  // 0 << x == 0
	  return s.replace(new RefMove(((Binary)s).getClearResult(), new AddressConstantOperand(Address.zero())));
	}
      }
    }
    return null;
  }

  private static Instruction refShr(Instruction s, OptOptions opts) {
    if (opts.SIMPLIFY_REF_OPS) {
      Operand op1 = ((Binary)s).getVal1();
      Operand op2 = ((Binary)s).getVal2();
      if (op2.isIntConstant()) {
	int val2 = op2.asIntConstant().value;
	if (op1.isConstant() && !op1.isMovableObjectConstant()) {
	  // BOTH CONSTANTS: FOLD
	  Word val1 = getAddressValue(op1).toWord();
	  return s.replace(new RefMove(((Binary)s).getClearResult(), new AddressConstantOperand(val1.rsha(val2).toAddress())));
	} else {
	  // ONLY OP2 IS CONSTANT: ATTEMPT TO APPLY AXIOMS
	  if (val2 == 0) {                  // x >> 0 == x
	    return s.replace(new RefMove(((Binary)s).getClearResult(), ((Binary)s).getClearVal1()));
	  }
	  if ((val2 >= SizeConstants.BITS_IN_ADDRESS) || (val2 < 0)) { // x >> 32 == x >> 31
	    ((Binary)s).setVal2(new IntConstantOperand(SizeConstants.BITS_IN_ADDRESS - 1));
	    return null;
	  }
	}
      } else if (op1.isConstant() && !op1.isMovableObjectConstant()) {
	Word val1 = getAddressValue(op1).toWord();
	// ONLY OP1 IS CONSTANT: ATTEMPT TO APPLY AXIOMS
	// -1 >> x == -1, 0 >> x == 0
	if (val1.EQ(Word.zero()) || val1.EQ(Word.zero().minus(Word.one()))) {
	  return s.replace(new RefMove(((Binary)s).getClearResult(), op1));
	}
      }
    }
    return null;
  }

  private static Instruction refNeg(Instruction s, OptOptions opts) {
    if (opts.SIMPLIFY_REF_OPS) {
      Operand op = ((Unary)s).getVal();
      if (op.isConstant() && !op.isMovableObjectConstant()) {
	// CONSTANT: FOLD
	Word val = getAddressValue(op).toWord();
	Word negVal = Word.zero().minus(val);
	return s.replace(new RefMove(((Unary)s).getClearResult(), new AddressConstantOperand(negVal.toAddress())));
      }
    }
    return null;
  }

  private static Instruction refNot(Instruction s, OptOptions opts) {
    if (opts.SIMPLIFY_REF_OPS) {
      Operand op = ((Unary)s).getVal();
      if (op.isConstant() && !op.isMovableObjectConstant()) {
	// CONSTANT: FOLD
	Word val = getAddressValue(op).toWord();
	return s.replace(new RefMove(((Unary)s).getClearResult(), new AddressConstantOperand(val.not().toAddress())));
      }
    }
    return null;
  }

  private static Instruction refOr(Instruction s, OptOptions opts) {
    if (opts.SIMPLIFY_REF_OPS) {
      canonicalizeCommutativeOperator(s);
      Operand op1 = ((Binary)s).getVal1();
      Operand op2 = ((Binary)s).getVal2();
      if (op1.similar(op2)) {
	// THE SAME OPERAND: x | x == x
	return s.replace(new RefMove(((Binary)s).getClearResult(), ((Binary)s).getClearVal1()));
      }
      if (op2.isConstant() && !op2.isMovableObjectConstant()) {
	Word val2 = getAddressValue(op2).toWord();
	if (op1.isAddressConstant()) {
	  // BOTH CONSTANTS: FOLD
	  Word val1 = getAddressValue(op1).toWord();
	  return s.replace(new RefMove(((Binary)s).getClearResult(), new AddressConstantOperand(val1.or(val2).toAddress())));
	} else {
	  // ONLY OP2 IS CONSTANT: ATTEMPT TO APPLY AXIOMS
	  if (val2.isMax()) { // x | -1 == x | 0xffffffff == 0xffffffff == -1
	    return s.replace(new RefMove(((Binary)s).getClearResult(), new AddressConstantOperand(Address.max())));
	  }
	  if (val2.isZero()) {                  // x | 0 == x
	    return s.replace(new RefMove(((Binary)s).getClearResult(), ((Binary)s).getClearVal1()));
	  }
	}
      }
    }
    return null;
  }

  private static Instruction refSub(Instruction s, OptOptions opts) {
    if (opts.SIMPLIFY_REF_OPS) {
      Operand op1 = ((Binary)s).getVal1();
      Operand op2 = ((Binary)s).getVal2();
      if (op1.similar(op2)) {
	// THE SAME OPERAND: x - x == 0
	return s.replace(new RefMove(((Binary)s).getClearResult(), new IntConstantOperand(0)));
      }
      if (op2.isConstant() && !op2.isMovableObjectConstant()) {
	Address val2 = getAddressValue(op2);
	if (op1.isConstant() && !op1.isMovableObjectConstant()) {
	  // BOTH CONSTANTS: FOLD
	  Address val1 = getAddressValue(op1);
	  return s.replace(new RefMove(((Binary)s).getClearResult(), new AddressConstantOperand(val1.minus(val2.toWord().toOffset()))));
	} else {
	  // ONLY OP2 IS CONSTANT: ATTEMPT TO APPLY AXIOMS
	  if (val2.isZero()) {                 // x - 0 == x
	    if (op1.isIntLike()) {
	      return s.replace(new Int2addrsigext(((Binary)s).getClearResult(), ((Binary)s).getClearVal1()));
	    } else {
	      return s.replace(new RefMove(((Binary)s).getClearResult(), ((Binary)s).getClearVal1()));
	    }
	  }
	  // x - c = x + -c
	  // prefer adds, since some architectures have addi but not subi
	  return s.replace(new RefAdd(
		  ((Binary)s).getClearResult(),
		  ((Binary)s).getClearVal1(),
		  new AddressConstantOperand(Address.zero().minus(val2.toWord().toOffset()))));
	}
      } else if (op1.isConstant() && !op1.isMovableObjectConstant()) {
	Address val1 = getAddressValue(op1);
	if (val1.EQ(Address.zero())) {
	  return s.replace(new RefNeg(((Binary)s).getClearResult(), ((Binary)s).getClearVal2()));
	}
      }
    }
    return null;
  }

  private static Instruction refUshr(Instruction s, OptOptions opts) {
    if (opts.SIMPLIFY_REF_OPS) {
      Operand op2 = ((Binary)s).getVal2();
      Operand op1 = ((Binary)s).getVal1();
      if (op2.isIntConstant()) {
	int val2 = op2.asIntConstant().value;
	if (op1.isConstant() && !op1.isMovableObjectConstant()) {
	  // BOTH CONSTANTS: FOLD
	  Word val1 = getAddressValue(op1).toWord();
	  return s.replace(new RefMove(((Binary)s).getClearResult(), new AddressConstantOperand(val1.rshl(val2).toAddress())));
	} else {
	  // ONLY OP2 IS CONSTANT: ATTEMPT TO APPLY AXIOMS
	  if (val2 == 0) {                  // x >>> 0 == x
	    return s.replace(new RefMove(((Binary)s).getClearResult(), ((Binary)s).getClearVal1()));
	  }
	  if ((val2 >= SizeConstants.BITS_IN_ADDRESS) || (val2 < 0)) { // x >>> 32 == 0
	    return s.replace(new RefMove(((Binary)s).getClearResult(), new IntConstantOperand(0)));
	  }
	}
      } else if (op1.isConstant() && !op1.isMovableObjectConstant()) {
	Word val1 = getAddressValue(op1).toWord();
	// ONLY OP1 IS CONSTANT: ATTEMPT TO APPLY AXIOMS
	if (val1.EQ(Word.zero())) {                  // 0 >>> x == 0
	  return s.replace(new RefMove(((Binary)s).getClearResult(), new AddressConstantOperand(Address.zero())));
	}
      }
    }
    return null;
  }

  private static Instruction refXor(Instruction s, OptOptions opts) {
    if (opts.SIMPLIFY_REF_OPS) {
      canonicalizeCommutativeOperator(s);
      Operand op1 = ((Binary)s).getVal1();
      Operand op2 = ((Binary)s).getVal2();
      if (op1.similar(op2)) {
	// THE SAME OPERAND: x ^ x == 0
	return s.replace(new RefMove(((Binary)s).getClearResult(), new IntConstantOperand(0)));
      }
      if (op2.isConstant() && !op2.isMovableObjectConstant()) {
	Word val2 = getAddressValue(op2).toWord();
	if (op1.isConstant() && !op1.isMovableObjectConstant()) {
	  // BOTH CONSTANTS: FOLD
	  Word val1 = getAddressValue(op1).toWord();
	  return s.replace(new RefMove(((Binary)s).getClearResult(), new AddressConstantOperand(val1.xor(val2).toAddress())));
	} else {
	  // ONLY OP2 IS CONSTANT: ATTEMPT TO APPLY AXIOMS
	  if (val2.isMax()) {                 // x ^ -1 == x ^ 0xffffffff = ~x
	    return s.replace(new RefNot(((Binary)s).getClearResult(), ((Binary)s).getClearVal1()));
	  }
	  if (val2.isZero()) {                  // x ^ 0 == x
	    return s.replace(new RefMove(((Binary)s).getClearResult(), ((Binary)s).getClearVal1()));
	  }
	}
      }
    }
    return null;
  }

  private static Instruction longAdd(Instruction s, OptOptions opts) {
    if (opts.SIMPLIFY_LONG_OPS) {
      canonicalizeCommutativeOperator(s);
      Operand op2 = ((Binary)s).getVal2();
      if (op2.isLongConstant()) {
	long val2 = op2.asLongConstant().value;
	Operand op1 = ((Binary)s).getVal1();
	if (op1.isLongConstant()) {
	  // BOTH CONSTANTS: FOLD
	  long val1 = op1.asLongConstant().value;
	  return s.replace(new LongMove(((Binary)s).getClearResult(), new LongConstantOperand(val1 + val2)));
	} else {
	  // ONLY OP2 IS CONSTANT: ATTEMPT TO APPLY AXIOMS
	  if (val2 == 0L) {                 // x + 0 == x
	    return s.replace(new LongMove(((Binary)s).getClearResult(), ((Binary)s).getClearVal1()));
	  }
	}
      } else {
	Operand op1 = ((Binary)s).getVal1();
	if (op1.similar(op2)) {
	  // Adding something to itself is the same as a multiply by 2 so
	  // canonicalize as a shift left
	  return s.replace(new LongShl(((Binary)s).getClearResult(), op1, new IntConstantOperand(1)));
	}
      }
    }
    return null;
  }

  private static Instruction longAnd(Instruction s, OptOptions opts) {
    if (opts.SIMPLIFY_LONG_OPS) {
      canonicalizeCommutativeOperator(s);
      Operand op1 = ((Binary)s).getVal1();
      Operand op2 = ((Binary)s).getVal2();
      if (op1.similar(op2)) {
	// THE SAME OPERAND: x & x == x
	return s.replace(new LongMove(((Binary)s).getClearResult(), ((Binary)s).getClearVal1()));
      }
      if (op2.isLongConstant()) {
	long val2 = op2.asLongConstant().value;
	if (op1.isLongConstant()) {
	  // BOTH CONSTANTS: FOLD
	  long val1 = op1.asLongConstant().value;
	  return s.replace(new LongMove(((Binary)s).getClearResult(), new LongConstantOperand(val1 & val2)));
	} else {
	  // ONLY OP2 IS CONSTANT: ATTEMPT TO APPLY AXIOMS
	  if (val2 == 0L) {                 // x & 0L == 0L
	    return s.replace(new LongMove(((Binary)s).getClearResult(), new LongConstantOperand(0L)));
	  }
	  if (val2 == -1) {                 // x & -1L == x & 0xff...ff == x
	    return s.replace(new LongMove(((Binary)s).getClearResult(), ((Binary)s).getClearVal1()));
	  }
	}
      }
    }
    return null;
  }

  private static Instruction longCmp(Instruction s, OptOptions opts) {
    if (opts.SIMPLIFY_LONG_OPS) {
      Operand op1 = ((Binary)s).getVal1();
      Operand op2 = ((Binary)s).getVal2();
      if (op1.similar(op2)) {
	// THE SAME OPERAND: op1 == op2
	return s.replace(new IntMove(((Binary)s).getClearResult(), new IntConstantOperand(0)));
      }
      if (op2.isLongConstant()) {
	if (op1.isLongConstant()) {
	  // BOTH CONSTANTS: FOLD
	  long val1 = op1.asLongConstant().value;
	  long val2 = op2.asLongConstant().value;
	  int result = (val1 > val2) ? 1 : ((val1 == val2) ? 0 : -1);
	  return s.replace(new IntMove(((Binary)s).getClearResult(), new IntConstantOperand(result)));
	}
      }
    }
    return null;
  }

  private static Instruction longDiv(Instruction s, OptOptions opts) {
    if (opts.SIMPLIFY_LONG_OPS) {
      Operand op1 = ((GuardedBinary)s).getVal1();
      Operand op2 = ((GuardedBinary)s).getVal2();
      if (op1.similar(op2)) {
	// THE SAME OPERAND: x / x == 1
	return s.replace(new LongMove(((GuardedBinary)s).getClearResult(), new LongConstantOperand(1)));
      }
      if (op2.isLongConstant()) {
	long val2 = op2.asLongConstant().value;
	if (val2 == 0L) {
	  // TODO: This instruction is actually unreachable.
	  // There will be a LONG_ZERO_CHECK
	  // guarding this instruction that will result in an
	  // ArithmeticException.  We
	  // should probably just remove the LONG_DIV as dead code.
	  return null;
	}
	if (op1.isLongConstant()) {
	  // BOTH CONSTANTS: FOLD
	  long val1 = op1.asLongConstant().value;
	  return s.replace(new LongMove(((GuardedBinary)s).getClearResult(), new LongConstantOperand(val1 / val2)));
	} else {
	  // ONLY OP2 IS CONSTANT: ATTEMPT TO APPLY AXIOMS
	  if (val2 == 1L) {                 // x / 1L == x
	    return s.replace(new LongMove(((GuardedBinary)s).getClearResult(), ((GuardedBinary)s).getClearVal1()));
	  }
	}
      }
    }
    return null;
  }
  private static Instruction longMul(RegisterPool regpool, Instruction s, OptOptions opts) {
    if (opts.SIMPLIFY_LONG_OPS) {
      canonicalizeCommutativeOperator(s);
      Operand op2 = ((Binary)s).getVal2();
      if (op2.isLongConstant()) {
	Operand op1 = ((Binary)s).getVal1();
	if (op1.isLongConstant()) {
	  // BOTH CONSTANTS: FOLD
	  long val1 = op1.asLongConstant().value;
	  long val2 = op2.asLongConstant().value;
	  return s.replace(new LongMove(((Binary)s).getClearResult(), new LongConstantOperand(val1 * val2)));
	} else {
	  // ONLY OP2 IS CONSTANT
	  return multiplyByConstant(regpool, s, op1, op2, opts);
	}
      }
    }
    return null;
  }

  private static Instruction longNeg(Instruction s, OptOptions opts) {
    if (opts.SIMPLIFY_LONG_OPS) {
      Operand op = ((Unary)s).getVal();
      if (op.isLongConstant()) {
	// CONSTANT: FOLD
	long val = op.asLongConstant().value;
	return s.replace(new LongMove(((Unary)s).getClearResult(), new LongConstantOperand(-val)));
      }
    }
    return null;
  }

  private static Instruction longNot(Instruction s, OptOptions opts) {
    if (opts.SIMPLIFY_LONG_OPS) {
      Operand op = ((Unary)s).getVal();
      if (op.isLongConstant()) {
	long val = op.asLongConstant().value;
	// CONSTANT: FOLD
	return s.replace(new LongMove(((Unary)s).getClearResult(), new LongConstantOperand(~val)));
      }
    }
    return null;
  }

  private static Instruction longOr(Instruction s, OptOptions opts) {
    if (opts.SIMPLIFY_LONG_OPS) {
      canonicalizeCommutativeOperator(s);
      Operand op1 = ((Binary)s).getVal1();
      Operand op2 = ((Binary)s).getVal2();
      if (op1.similar(op2)) {
	// THE SAME OPERAND: x | x == x
	return s.replace(new LongMove(((Binary)s).getClearResult(), ((Binary)s).getClearVal1()));
      }
      if (op2.isLongConstant()) {
	long val2 = op2.asLongConstant().value;
	if (op1.isLongConstant()) {
	  // BOTH CONSTANTS: FOLD
	  long val1 = op1.asLongConstant().value;
	  return s.replace(new LongMove(((Binary)s).getClearResult(), new LongConstantOperand(val1 | val2)));
	} else {
	  // ONLY OP2 IS CONSTANT: ATTEMPT TO APPLY AXIOMS
	  if (val2 == 0L) {                 // x | 0L == x
	    return s.replace(new LongMove(((Binary)s).getClearResult(), ((Binary)s).getClearVal1()));
	  }
	  if (val2 == -1L) { // x | -1L == x | 0xff..ff == 0xff..ff == -1L
	    return s.replace(new LongMove(((Binary)s).getClearResult(), new LongConstantOperand(-1L)));
	  }
	}
      }
    }
    return null;
  }

  private static Instruction longRem(Instruction s, OptOptions opts) {
    if (opts.SIMPLIFY_LONG_OPS) {
      Operand op1 = ((GuardedBinary)s).getVal1();
      Operand op2 = ((GuardedBinary)s).getVal2();
      if (op1.similar(op2)) {
	// THE SAME OPERAND: x % x == 0
	return s.replace(new LongMove(((GuardedBinary)s).getClearResult(), new LongConstantOperand(0)));
      }
      if (op2.isLongConstant()) {
	long val2 = op2.asLongConstant().value;
	if (val2 == 0L) {
	  // TODO: This instruction is actually unreachable.
	  // There will be a LONG_ZERO_CHECK
	  // guarding this instruction that will result in an
	  // ArithmeticException.  We
	  // should probably just remove the LONG_REM as dead code.
	  return null;
	}
	if (op1.isLongConstant()) {
	  // BOTH CONSTANTS: FOLD
	  long val1 = op1.asLongConstant().value;
	  return s.replace(new LongMove(((GuardedBinary)s).getClearResult(), new LongConstantOperand(val1 % val2)));
	} else {
	  // ONLY OP2 IS CONSTANT: ATTEMPT TO APPLY AXIOMS
	  if ((val2 == 1L)||(val2 == -1L)) {                 // x % 1L == 0
	    return s.replace(new LongMove(((GuardedBinary)s).getClearResult(), new LongConstantOperand(0)));
	  }
	}
      }
    }
    return null;
  }

  private static Instruction longShl(Instruction s, OptOptions opts) {
    if (opts.SIMPLIFY_LONG_OPS) {
      Operand op2 = ((Binary)s).getVal2();
      Operand op1 = ((Binary)s).getVal1();
      if (op2.isIntConstant()) {
	int val2 = op2.asIntConstant().value;
	if (op1.isLongConstant()) {
	  // BOTH CONSTANTS: FOLD
	  long val1 = op1.asLongConstant().value;
	  return s.replace(new LongMove(((Binary)s).getClearResult(), new LongConstantOperand(val1 << val2)));
	} else {
	  // ONLY OP2 IS CONSTANT: ATTEMPT TO APPLY AXIOMS
	  if (val2 == 0) {                  // x << 0 == x
	    return s.replace(new LongMove(((Binary)s).getClearResult(), ((Binary)s).getClearVal1()));
	  }
	  if ((val2 >= SizeConstants.BITS_IN_LONG) || (val2 < 0)) { // x << 64 == 0
	    return s.replace(new IntMove(((Binary)s).getClearResult(), new LongConstantOperand(0)));
	  }
	}
      } else if (op1.isLongConstant()) {
	long val1 = op1.asLongConstant().value;
	// ONLY OP1 IS CONSTANT: ATTEMPT TO APPLY AXIOMS
	if (val1 == 0L) {                  // 0 << x == 0
	  return s.replace(new LongMove(((Binary)s).getClearResult(), op1));
	}
      }
    }
    return null;
  }

  private static Instruction longShr(Instruction s, OptOptions opts) {
    if (opts.SIMPLIFY_LONG_OPS) {
      Operand op1 = ((Binary)s).getVal1();
      Operand op2 = ((Binary)s).getVal2();
      if (op2.isIntConstant()) {
	int val2 = op2.asIntConstant().value;
	if (op1.isLongConstant()) {
	  // BOTH CONSTANTS: FOLD
	  long val1 = op1.asLongConstant().value;
	  return s.replace(new LongMove(((Binary)s).getClearResult(), new LongConstantOperand(val1 >> val2)));
	} else {
	  // ONLY OP2 IS CONSTANT: ATTEMPT TO APPLY AXIOMS
	  if (val2 == 0) {                  // x >> 0L == x
	    return s.replace(new LongMove(((Binary)s).getClearResult(), ((Binary)s).getClearVal1()));
	  }
	  if ((val2 >= SizeConstants.BITS_IN_LONG) || (val2 < 0)) { // x >> 64 == x >> 63
	    ((Binary)s).setVal2(new LongConstantOperand(63));
	    return null;
	  }
	}
      } else if (op1.isLongConstant()) {
	long val1 = op1.asLongConstant().value;
	// ONLY OP1 IS CONSTANT: ATTEMPT TO APPLY AXIOMS
	if ((val1 == -1L) || (val1 == 0L)) {  // -1 >> x == -1, 0 >> x == 0
	  return s.replace(new LongMove(((Binary)s).getClearResult(), op1));
	}
      }
    }
    return null;
  }

  private static Instruction longSub(Instruction s, OptOptions opts) {
    if (opts.SIMPLIFY_LONG_OPS) {
      Operand op1 = ((Binary)s).getVal1();
      Operand op2 = ((Binary)s).getVal2();
      if (op1.similar(op2)) {
	// THE SAME OPERAND: x - x == 0
	return s.replace(new LongMove(((Binary)s).getClearResult(), new LongConstantOperand(0)));
      }
      if (op2.isLongConstant()) {
	long val2 = op2.asLongConstant().value;
	if (op1.isLongConstant()) {
	  // BOTH CONSTANTS: FOLD
	  long val1 = op1.asLongConstant().value;
	  return s.replace(new LongMove(((Binary)s).getClearResult(), new LongConstantOperand(val1 - val2)));
	} else {
	  // ONLY OP2 IS CONSTANT: ATTEMPT TO APPLY AXIOMS
	  if (val2 == 0L) {                 // x - 0 == x
	    return s.replace(new LongMove(((Binary)s).getClearResult(), ((Binary)s).getClearVal1()));
	  }
	  // x - c = x + -c
	  // prefer adds, since some architectures have addi but not subi, also
	  // add is commutative and gives greater flexibility to LIR/MIR phases
	  // without possibly introducing temporary variables
	  return s.replace(new LongAdd(((Binary)s).getClearResult(),
	      ((Binary)s).getClearVal1(), new LongConstantOperand(-val2)));
	}
      } else if (op1.isLongConstant() && (op1.asLongConstant().value == 0)) {
	return s.replace(new LongNeg(((Binary)s).getClearResult(), ((Binary)s).getClearVal2()));
      }
    }
    return null;
  }

  private static Instruction longUshr(Instruction s, OptOptions opts) {
    if (opts.SIMPLIFY_LONG_OPS) {
      Operand op2 = ((Binary)s).getVal2();
      Operand op1 = ((Binary)s).getVal1();
      if (op2.isIntConstant()) {
	int val2 = op2.asIntConstant().value;
	if (op1.isLongConstant()) {
	  // BOTH CONSTANTS: FOLD
	  long val1 = op1.asLongConstant().value;
	  return s.replace(new LongMove(((Binary)s).getClearResult(), new LongConstantOperand(val1 >>> val2)));
	} else {
	  // ONLY OP2 IS CONSTANT: ATTEMPT TO APPLY AXIOMS
	  if (val2 == 0) {                  // x >>> 0L == x
	    return s.replace(new LongMove(((Binary)s).getClearResult(), ((Binary)s).getClearVal1()));
	  }
	  if ((val2 >= SizeConstants.BITS_IN_LONG) || (val2 < 0)) {  // x >>> 64 == 0
	    return s.replace(new LongMove(((Binary)s).getClearResult(), new LongConstantOperand(0)));
	  }
	}
      } else if (op1.isLongConstant()) {
	long val1 = op1.asLongConstant().value;
	// ONLY OP1 IS CONSTANT: ATTEMPT TO APPLY AXIOMS
	if (val1 == 0L) {                  // 0 >>> x == 0
	  return s.replace(new LongMove(((Binary)s).getClearResult(), op1));
	}
      }
    }
    return null;
  }

  private static Instruction longXor(Instruction s, OptOptions opts) {
    if (opts.SIMPLIFY_LONG_OPS) {
      canonicalizeCommutativeOperator(s);
      Operand op1 = ((Binary)s).getVal1();
      Operand op2 = ((Binary)s).getVal2();
      if (op1.similar(op2)) {
	// THE SAME OPERAND: x ^ x == 0
	return s.replace(new LongMove(((Binary)s).getClearResult(), new LongConstantOperand(0)));
      }
      if (op2.isLongConstant()) {
	long val2 = op2.asLongConstant().value;
	if (op1.isLongConstant()) {
	  // BOTH CONSTANTS: FOLD
	  long val1 = op1.asLongConstant().value;
	  return s.replace(new LongMove(((Binary)s).getClearResult(), new LongConstantOperand(val1 ^ val2)));
	} else {
	  // ONLY OP2 IS CONSTANT: ATTEMPT TO APPLY AXIOMS
	  if (val2 == -1L) {                // x ^ -1L == x ^ 0xff..ff = ~x
	    return s.replace(new LongNot(((Binary)s).getClearResult(), ((Binary)s).getClearVal1()));
	  }
	  if (val2 == 0L) {                 // x ^ 0L == x
	    return s.replace(new LongMove(((Binary)s).getClearResult(), ((Binary)s).getClearVal1()));
	  }
	}
      }
    }
    return null;
  }

  private static Instruction floatAdd(Instruction s, OptOptions opts) {
    if (opts.SIMPLIFY_FLOAT_OPS) {
      canonicalizeCommutativeOperator(s);
      Operand op2 = ((Binary)s).getVal2();
      if (op2.isFloatConstant()) {
	float val2 = op2.asFloatConstant().value;
	Operand op1 = ((Binary)s).getVal1();
	if (op1.isFloatConstant()) {
	  // BOTH CONSTANTS: FOLD
	  float val1 = op1.asFloatConstant().value;
	  return s.replace(new FloatMove(((Binary)s).getClearResult(), new FloatConstantOperand(val1 + val2)));
	}
	if (val2 == 0.0f) {
	  // x + 0.0 is x (even is x is a Nan).
	  return s.replace(new FloatMove(((Binary)s).getClearResult(), ((Binary)s).getClearVal1()));
	}
      }
    }
    return null;
  }

  private static Instruction floatCmpg(Instruction s, OptOptions opts) {
    if (opts.SIMPLIFY_INTEGER_OPS) {
      Operand op2 = ((Binary)s).getVal2();
      if (op2.isFloatConstant()) {
	Operand op1 = ((Binary)s).getVal1();
	if (op1.isFloatConstant()) {
	  // BOTH CONSTANTS: FOLD
	  float val1 = op1.asFloatConstant().value;
	  float val2 = op2.asFloatConstant().value;
	  int result = (val1 < val2) ? -1 : ((val1 == val2) ? 0 : 1);
	  return s.replace(new IntMove(((Binary)s).getClearResult(), new IntConstantOperand(result)));
	}
      }
    }
    return null;
  }

  private static Instruction floatCmpl(Instruction s, OptOptions opts) {
    if (opts.SIMPLIFY_INTEGER_OPS) {
      Operand op2 = ((Binary)s).getVal2();
      if (op2.isFloatConstant()) {
	Operand op1 = ((Binary)s).getVal1();
	if (op1.isFloatConstant()) {
	  // BOTH CONSTANTS: FOLD
	  float val1 = op1.asFloatConstant().value;
	  float val2 = op2.asFloatConstant().value;
	  int result = (val1 > val2) ? 1 : ((val1 == val2) ? 0 : -1);
	  return s.replace(new IntMove(((Binary)s).getClearResult(), new IntConstantOperand(result)));
	}
      }
    }
    return null;
  }

  private static Instruction floatDiv(Instruction s, OptOptions opts) {
    if (opts.SIMPLIFY_FLOAT_OPS) {
      Operand op2 = ((Binary)s).getVal2();
      if (op2.isFloatConstant()) {
	Operand op1 = ((Binary)s).getVal1();
	if (op1.isFloatConstant()) {
	  // BOTH CONSTANTS: FOLD
	  float val1 = op1.asFloatConstant().value;
	  float val2 = op2.asFloatConstant().value;
	  return s.replace(new FloatMove(((Binary)s).getClearResult(), new FloatConstantOperand(val1 / val2)));
	}
      }
    }
    return null;
  }

  private static Instruction floatMul(Instruction s, OptOptions opts) {
    if (opts.SIMPLIFY_FLOAT_OPS) {
      canonicalizeCommutativeOperator(s);
      Operand op2 = ((Binary)s).getVal2();
      if (op2.isFloatConstant()) {
	float val2 = op2.asFloatConstant().value;
	Operand op1 = ((Binary)s).getVal1();
	if (op1.isFloatConstant()) {
	  // BOTH CONSTANTS: FOLD
	  float val1 = op1.asFloatConstant().value;
	  return s.replace(new FloatMove(((Binary)s).getClearResult(), new FloatConstantOperand(val1 * val2)));
	}
	if (val2 == 1.0f) {
	  // x * 1.0 is x, even if x is a NaN
	  return s.replace(new FloatMove(((Binary)s).getClearResult(), ((Binary)s).getClearVal1()));
	}
      }
    }
    return null;
  }

  private static Instruction floatNeg(Instruction s, OptOptions opts) {
    if (opts.SIMPLIFY_FLOAT_OPS) {
      Operand op = ((Unary)s).getVal();
      if (op.isFloatConstant()) {
	// CONSTANT: FOLD
	float val = op.asFloatConstant().value;
	return s.replace(new FloatMove(((Unary)s).getClearResult(), new FloatConstantOperand(-val)));
      }
    }
    return null;
  }

  private static Instruction floatRem(Instruction s, OptOptions opts) {
    if (opts.SIMPLIFY_FLOAT_OPS) {
      Operand op2 = ((Binary)s).getVal2();
      if (op2.isFloatConstant()) {
	Operand op1 = ((Binary)s).getVal1();
	if (op1.isFloatConstant()) {
	  // BOTH CONSTANTS: FOLD
	  float val1 = op1.asFloatConstant().value;
	  float val2 = op2.asFloatConstant().value;
	  return s.replace(new FloatMove(((Binary)s).getClearResult(), new FloatConstantOperand(val1 % val2)));
	}
      }
    }
    return null;
  }

  private static Instruction floatSub(Instruction s, OptOptions opts) {
    if (opts.SIMPLIFY_FLOAT_OPS) {
      Operand op1 = ((Binary)s).getVal1();
      Operand op2 = ((Binary)s).getVal2();
      if (op2.isFloatConstant()) {
	float val2 = op2.asFloatConstant().value;
	if (op1.isFloatConstant()) {
	  // BOTH CONSTANTS: FOLD
	  float val1 = op1.asFloatConstant().value;
	  return s.replace(new FloatMove(((Binary)s).getClearResult(), new FloatConstantOperand(val1 - val2)));
	}
	if (val2 == 0.0f) {
	  // x - 0.0 is x, even if x is a NaN
	  return s.replace(new FloatMove(((Binary)s).getClearResult(), ((Binary)s).getClearVal1()));
	}
      } else if (op1.isFloatConstant() && (op1.asFloatConstant().value == 0.0f)) {
	return s.replace(new FloatNeg(((Binary)s).getClearResult(), ((Binary)s).getClearVal2()));
      }
    }
    return null;
  }

  private static Instruction floatSqrt(Instruction s, OptOptions opts) {
    if (opts.SIMPLIFY_FLOAT_OPS) {
      Operand op = ((Unary)s).getVal();
      if (op.isFloatConstant()) {
	// CONSTANT: FOLD
	float val = op.asFloatConstant().value;
	return s.replace(new FloatMove(((Unary)s).getClearResult(), new FloatConstantOperand((float)Math.sqrt(val))));
      }
    }
    return null;
  }

  private static Instruction doubleAdd(Instruction s, OptOptions opts) {
    if (opts.SIMPLIFY_DOUBLE_OPS) {
      canonicalizeCommutativeOperator(s);
      Operand op2 = ((Binary)s).getVal2();
      if (op2.isDoubleConstant()) {
	double val2 = op2.asDoubleConstant().value;
	Operand op1 = ((Binary)s).getVal1();
	if (op1.isDoubleConstant()) {
	  // BOTH CONSTANTS: FOLD
	  double val1 = op1.asDoubleConstant().value;
	  return s.replace(new DoubleMove(((Binary)s).getClearResult(), new DoubleConstantOperand(val1 + val2)));
	}
	if (val2 == 0.0) {
	  // x + 0.0 is x, even if x is a NaN
	  return s.replace(new DoubleMove(((Binary)s).getClearResult(), ((Binary)s).getClearVal1()));
	}
      }
    }
    return null;
  }

  private static Instruction doubleCmpg(Instruction s, OptOptions opts) {
    if (opts.SIMPLIFY_INTEGER_OPS) {
      Operand op2 = ((Binary)s).getVal2();
      if (op2.isDoubleConstant()) {
	Operand op1 = ((Binary)s).getVal1();
	if (op1.isDoubleConstant()) {
	  // BOTH CONSTANTS: FOLD
	  double val1 = op1.asDoubleConstant().value;
	  double val2 = op2.asDoubleConstant().value;
	  int result = (val1 < val2) ? -1 : ((val1 == val2) ? 0 : 1);
	  return s.replace(new IntMove(((Binary)s).getClearResult(), new IntConstantOperand(result)));
	}
      }
    }
    return null;
  }

  private static Instruction doubleCmpl(Instruction s, OptOptions opts) {
    if (opts.SIMPLIFY_INTEGER_OPS) {
      Operand op2 = ((Binary)s).getVal2();
      if (op2.isDoubleConstant()) {
	Operand op1 = ((Binary)s).getVal1();
	if (op1.isDoubleConstant()) {
	  // BOTH CONSTANTS: FOLD
	  double val1 = op1.asDoubleConstant().value;
	  double val2 = op2.asDoubleConstant().value;
	  int result = (val1 > val2) ? 1 : ((val1 == val2) ? 0 : -1);
	  return s.replace(new DoubleMove(((Binary)s).getClearResult(), new IntConstantOperand(result)));
	}
      }
    }
    return null;
  }

  private static Instruction doubleDiv(Instruction s, OptOptions opts) {
    if (opts.SIMPLIFY_DOUBLE_OPS) {
      Operand op2 = ((Binary)s).getVal2();
      if (op2.isDoubleConstant()) {
	Operand op1 = ((Binary)s).getVal1();
	if (op1.isDoubleConstant()) {
	  // BOTH CONSTANTS: FOLD
	  double val1 = op1.asDoubleConstant().value;
	  double val2 = op2.asDoubleConstant().value;
	  return s.replace(new DoubleMove(((Binary)s).getClearResult(), new DoubleConstantOperand(val1 / val2)));
	}
      }
    }
    return null;
  }

  private static Instruction doubleMul(Instruction s, OptOptions opts) {
    if (opts.SIMPLIFY_DOUBLE_OPS) {
      canonicalizeCommutativeOperator(s);
      Operand op2 = ((Binary)s).getVal2();
      if (op2.isDoubleConstant()) {
	double val2 = op2.asDoubleConstant().value;
	Operand op1 = ((Binary)s).getVal1();
	if (op1.isDoubleConstant()) {
	  // BOTH CONSTANTS: FOLD
	  double val1 = op1.asDoubleConstant().value;
	  return s.replace(new DoubleMove(((Binary)s).getClearResult(), new DoubleConstantOperand(val1 * val2)));
	}
	if (val2 == 1.0) {
	  // x * 1.0 is x even if x is a NaN
	  return s.replace(new DoubleMove(((Binary)s).getClearResult(), ((Binary)s).getClearVal1()));
	}
      }
    }
    return null;
  }

  private static Instruction doubleNeg(Instruction s, OptOptions opts) {
    if (opts.SIMPLIFY_DOUBLE_OPS) {
      Operand op = ((Unary)s).getVal();
      if (op.isDoubleConstant()) {
	// CONSTANT: FOLD
	double val = op.asDoubleConstant().value;
	return s.replace(new DoubleMove(((Unary)s).getClearResult(), new DoubleConstantOperand(-val)));
      }
    }
    return null;
  }

  private static Instruction doubleRem(Instruction s, OptOptions opts) {
    if (opts.SIMPLIFY_DOUBLE_OPS) {
      Operand op2 = ((Binary)s).getVal2();
      if (op2.isDoubleConstant()) {
	Operand op1 = ((Binary)s).getVal1();
	if (op1.isDoubleConstant()) {
	  // BOTH CONSTANTS: FOLD
	  double val1 = op1.asDoubleConstant().value;
	  double val2 = op2.asDoubleConstant().value;
	  return s.replace(new DoubleMove(((Binary)s).getClearResult(), new DoubleConstantOperand(val1 % val2)));
	}
      }
    }
    return null;
  }

  private static Instruction doubleSub(Instruction s, OptOptions opts) {
    if (opts.SIMPLIFY_DOUBLE_OPS) {
      Operand op1 = ((Binary)s).getVal1();
      Operand op2 = ((Binary)s).getVal2();
      if (op2.isDoubleConstant()) {
	double val2 = op2.asDoubleConstant().value;
	if (op1.isDoubleConstant()) {
	  // BOTH CONSTANTS: FOLD
	  double val1 = op1.asDoubleConstant().value;
	  return s.replace(new DoubleMove(((Binary)s).getClearResult(), new DoubleConstantOperand(val1 - val2)));
	}
	if (val2 == 0.0) {
	  // x - 0.0 is x, even if x is a NaN
	  return s.replace(new DoubleMove(((Binary)s).getClearResult(), ((Binary)s).getClearVal1()));
	}
      } else if (op1.isDoubleConstant() && (op1.asDoubleConstant().value == 0.0)) {
	return s.replace(new DoubleNeg(((Binary)s).getClearResult(), ((Binary)s).getClearVal2()));
      }
    }
    return null;
  }

  private static Instruction doubleSqrt(Instruction s, OptOptions opts) {
    if (opts.SIMPLIFY_DOUBLE_OPS) {
      Operand op = ((Unary)s).getVal();
      if (op.isDoubleConstant()) {
	// CONSTANT: FOLD
	double val = op.asDoubleConstant().value;
	return s.replace(new DoubleMove(((Unary)s).getClearResult(), new DoubleConstantOperand(Math.sqrt(val))));
      }
    }
    return null;
  }

  private static Instruction double2Float(Instruction s, OptOptions opts) {
    if (opts.SIMPLIFY_FLOAT_OPS) {
      Operand op = ((Unary)s).getVal();
      if (op.isDoubleConstant()) {
	// CONSTANT: FOLD
	double val = op.asDoubleConstant().value;
	return s.replace(new FloatMove(((Unary)s).getClearResult(), new FloatConstantOperand((float) val)));
      }
    }
    return null;
  }

  private static Instruction double2Int(Instruction s, OptOptions opts) {
    if (opts.SIMPLIFY_INTEGER_OPS) {
      Operand op = ((Unary)s).getVal();
      if (op.isDoubleConstant()) {
	// CONSTANT: FOLD
	double val = op.asDoubleConstant().value;
	return s.replace(new IntMove(((Unary)s).getClearResult(), new IntConstantOperand((int) val)));
      }
    }
    return null;
  }

  private static Instruction double2Long(Instruction s, OptOptions opts) {
    if (opts.SIMPLIFY_LONG_OPS) {
      Operand op = ((Unary)s).getVal();
      if (op.isDoubleConstant()) {
	// CONSTANT: FOLD
	double val = op.asDoubleConstant().value;
	return s.replace(new LongMove(((Unary)s).getClearResult(), new LongConstantOperand((long) val)));
      }
    }
    return null;
  }

  private static Instruction doubleAsLongBits(Instruction s, OptOptions opts) {
    if (opts.SIMPLIFY_LONG_OPS) {
      Operand op = ((Unary)s).getVal();
      if (op.isDoubleConstant()) {
	// CONSTANT: FOLD
	double val = op.asDoubleConstant().value;
	return s.replace(new LongMove(((Unary)s).getClearResult(), new LongConstantOperand(Double.doubleToLongBits(val))));
      }
    }
    return null;
  }

  private static Instruction int2Double(Instruction s, OptOptions opts) {
    if (opts.SIMPLIFY_DOUBLE_OPS) {
      Operand op = ((Unary)s).getVal();
      if (op.isIntConstant()) {
	// CONSTANT: FOLD
	int val = op.asIntConstant().value;
	return s.replace(new DoubleMove(((Unary)s).getClearResult(), new DoubleConstantOperand((double) val)));
      }
    }
    return null;
  }

  private static Instruction int2Byte(Instruction s, OptOptions opts) {
    if (opts.SIMPLIFY_INTEGER_OPS) {
      Operand op = ((Unary)s).getVal();
      if (op.isIntConstant()) {
	// CONSTANT: FOLD
	int val = op.asIntConstant().value;
	return s.replace(new IntMove(((Unary)s).getClearResult(), new IntConstantOperand((byte) val)));
      }
    }
    return null;
  }

  private static Instruction int2UShort(Instruction s, OptOptions opts) {
    if (opts.SIMPLIFY_INTEGER_OPS) {
      Operand op = ((Unary)s).getVal();
      if (op.isIntConstant()) {
	// CONSTANT: FOLD
	int val = op.asIntConstant().value;
	return s.replace(new IntMove(((Unary)s).getClearResult(), new IntConstantOperand((char) val)));
      }
    }
    return null;
  }

  private static Instruction int2Float(Instruction s, OptOptions opts) {
    if (opts.SIMPLIFY_FLOAT_OPS) {
      Operand op = ((Unary)s).getVal();
      if (op.isIntConstant()) {
	// CONSTANT: FOLD
	int val = op.asIntConstant().value;
	return s.replace(new FloatMove(((Unary)s).getClearResult(), new FloatConstantOperand((float) val)));
      }
    }
    return null;
  }

  private static Instruction int2Long(Instruction s, OptOptions opts) {
    if (opts.SIMPLIFY_LONG_OPS) {
      Operand op = ((Unary)s).getVal();
      if (op.isIntConstant()) {
	// CONSTANT: FOLD
	int val = op.asIntConstant().value;
	return s.replace(new LongMove(((Unary)s).getClearResult(), new LongConstantOperand((long) val)));
      }
    }
    return null;
  }

  private static Instruction int2AddrSigExt(Instruction s, OptOptions opts) {
    if (opts.SIMPLIFY_REF_OPS) {
      Operand op = ((Unary)s).getVal();
      if (op.isIntConstant()) {
	// CONSTANT: FOLD
	int val = op.asIntConstant().value;
	return s.replace(new RefMove(((Unary)s).getClearResult(), new AddressConstantOperand(Address.fromIntSignExtend(val))));
      }
    }
    return null;
  }

  private static Instruction int2AddrZerExt(Instruction s, OptOptions opts) {
    if (opts.SIMPLIFY_REF_OPS) {
      Operand op = ((Unary)s).getVal();
      if (op.isIntConstant()) {
	// CONSTANT: FOLD
	int val = op.asIntConstant().value;
	return s.replace(new RefMove(((Unary)s).getClearResult(), new AddressConstantOperand(Address.fromIntZeroExtend(val))));
      }
    }
    return null;
  }

  private static Instruction long2Addr(Instruction s, OptOptions opts) {
    if (opts.SIMPLIFY_REF_OPS) {
      Operand op = ((Unary)s).getVal();
      if (op.isLongConstant()) {
	// CONSTANT: FOLD
	long val = op.asLongConstant().value;
	return s.replace(new RefMove(((Unary)s).getClearResult(), new AddressConstantOperand(Address.fromLong(val))));
      }
    }
    return null;
  }

  private static Instruction int2Short(Instruction s, OptOptions opts) {
    if (opts.SIMPLIFY_INTEGER_OPS) {
      Operand op = ((Unary)s).getVal();
      if (op.isIntConstant()) {
	// CONSTANT: FOLD
	int val = op.asIntConstant().value;
	return s.replace(new IntMove(((Unary)s).getClearResult(), new IntConstantOperand((short) val)));
      }
    }
    return null;
  }

  private static Instruction intBitsAsFloat(Instruction s, OptOptions opts) {
    if (opts.SIMPLIFY_FLOAT_OPS) {
      Operand op = ((Unary)s).getVal();
      if (op.isIntConstant()) {
	// CONSTANT: FOLD
	int val = op.asIntConstant().value;
	return s.replace(new FloatMove(((Unary)s).getClearResult(), new FloatConstantOperand(Float.intBitsToFloat(val))));
      }
    }
    return null;
  }

  private static Instruction addr2Int(Instruction s, OptOptions opts) {
    if (opts.SIMPLIFY_INTEGER_OPS) {
      Operand op = ((Unary)s).getVal();
      if (op.isConstant() && !op.isMovableObjectConstant()) {
	// CONSTANT: FOLD
	Address val = getAddressValue(op);
	return s.replace(new IntMove(((Unary)s).getClearResult(), new IntConstantOperand(val.toInt())));
      }
    }
    return null;
  }

  private static Instruction addr2Long(Instruction s, OptOptions opts) {
    if (opts.SIMPLIFY_LONG_OPS) {
      Operand op = ((Unary)s).getVal();
      if (op.isConstant() && !op.isMovableObjectConstant()) {
	// CONSTANT: FOLD
	Address val = getAddressValue(op);
	return s.replace(new LongMove(((Unary)s).getClearResult(), new LongConstantOperand(val.toLong())));
      }
    }
    return null;
  }

  private static Instruction float2Double(Instruction s, OptOptions opts) {
    if (opts.SIMPLIFY_DOUBLE_OPS) {
      Operand op = ((Unary)s).getVal();
      if (op.isFloatConstant()) {
	// CONSTANT: FOLD
	float val = op.asFloatConstant().value;
	return s.replace(new DoubleMove(((Unary)s).getClearResult(), new DoubleConstantOperand((double) val)));
      }
    }
    return null;
  }

  private static Instruction float2Int(Instruction s, OptOptions opts) {
    if (opts.SIMPLIFY_INTEGER_OPS) {
      Operand op = ((Unary)s).getVal();
      if (op.isFloatConstant()) {
	// CONSTANT: FOLD
	float val = op.asFloatConstant().value;
	return s.replace(new IntMove(((Unary)s).getClearResult(), new IntConstantOperand((int) val)));
      }
    }
    return null;
  }

  private static Instruction float2Long(Instruction s, OptOptions opts) {
    if (opts.SIMPLIFY_LONG_OPS) {
      Operand op = ((Unary)s).getVal();
      if (op.isFloatConstant()) {
	// CONSTANT: FOLD
	float val = op.asFloatConstant().value;
	return s.replace(new LongMove(((Unary)s).getClearResult(), new LongConstantOperand((long) val)));
      }
    }
    return null;
  }

  private static Instruction floatAsIntBits(Instruction s, OptOptions opts) {
    if (opts.SIMPLIFY_INTEGER_OPS) {
      Operand op = ((Unary)s).getVal();
      if (op.isFloatConstant()) {
	// CONSTANT: FOLD
	float val = op.asFloatConstant().value;
	return s.replace(new IntMove(((Unary)s).getClearResult(), new IntConstantOperand(Float.floatToIntBits(val))));
      }
    }
    return null;
  }

  private static Instruction long2Float(Instruction s, OptOptions opts) {
    if (opts.SIMPLIFY_FLOAT_OPS) {
      Operand op = ((Unary)s).getVal();
      if (op.isLongConstant()) {
	// CONSTANT: FOLD
	long val = op.asLongConstant().value;
	return s.replace(new FloatMove(((Unary)s).getClearResult(), new FloatConstantOperand((float) val)));
      }
    }
    return null;
  }

  private static Instruction long2Int(Instruction s, OptOptions opts) {
    if (opts.SIMPLIFY_INTEGER_OPS) {
      Operand op = ((Unary)s).getVal();
      if (op.isLongConstant()) {
	// CONSTANT: FOLD
	long val = op.asLongConstant().value;
	return s.replace(new IntMove(((Unary)s).getClearResult(), new IntConstantOperand((int) val)));
      }
    }
    return null;
  }

  private static Instruction long2Double(Instruction s, OptOptions opts) {
    if (opts.SIMPLIFY_DOUBLE_OPS) {
      Operand op = ((Unary)s).getVal();
      if (op.isLongConstant()) {
	// CONSTANT: FOLD
	long val = op.asLongConstant().value;
	return s.replace(new DoubleMove(((Unary)s).getClearResult(), new DoubleConstantOperand((double) val)));
      }
    }
    return null;
  }

  private static Instruction longBitsAsDouble(Instruction s, OptOptions opts) {
    if (opts.SIMPLIFY_DOUBLE_OPS) {
      Operand op = ((Unary)s).getVal();
      if (op.isLongConstant()) {
	// CONSTANT: FOLD
	long val = op.asLongConstant().value;
	return s.replace(new DoubleMove(((Unary)s).getClearResult(), new DoubleConstantOperand(Double.longBitsToDouble(val))));
      }
    }
    return null;
  }

  private static Instruction arrayLength(Instruction s, OptOptions opts) {
    if (opts.SIMPLIFY_FIELD_OPS) {
      ArrayOperand op = (ArrayOperand)((Arraylength)s).getVal();
      if (op.isObjectConstant()) {
	int length = 0;
	length = op.getArrayLength();
	return s.replace(new IntMove(((GuardedUnary)s).getClearResult(), new IntConstantOperand(length)));
      } else if (op.isNullConstant()) {
	// TODO: this arraylength operation is junk so destroy
	return null;
      }
    }
    return null;
  }

  private static Instruction boundsCheck(Instruction s, OptOptions opts) {
    if (opts.SIMPLIFY_FIELD_OPS) {
      ArrayOperand ref = ((BoundsCheck)s).getRef();
      Operand index = ((BoundsCheck)s).getIndex();
      if (ref.isNullConstant()) {
	// Should already be caught by nullcheck simplification
	return null;
      } else if (index.isIntConstant()) {
	int indexAsInt = index.asIntConstant().value;
	if (indexAsInt < 0) {
	  return s.replace(new Trap(new TypeOperand(KnownTypes.INDEX_OUT_OF_BOUNDS_EXCEPTION_TYPE)));
	} else if (ref.isConstant()) {
	  if (ref.isObjectConstant()) {
	    int refLength = ref.getArrayLength();
	    if (indexAsInt < refLength) {
	      return s.replace(new GuardMove(((BoundsCheck)s).getClearGuardResult(), ((BoundsCheck)s).getClearGuard()));
	    } else {
	      return s.replace(new Trap(new TypeOperand(KnownTypes.INDEX_OUT_OF_BOUNDS_EXCEPTION_TYPE)));
	    }
	  }
	}
      }
    }
    return null;
  }

  private static Instruction call(RegisterPool regpool, Instruction s, OptOptions opts) {
    if (opts.SIMPLIFY_FIELD_OPS) {
      MethodOperand methOp = ((Call)s).getMethod();
      if (methOp == null) {
	return null;
      }
      if (methOp.isVirtual() && !methOp.hasPreciseTarget()) {
	Operand calleeThis = ((Call)s).getParam(0);
	if (calleeThis.isNullConstant()) {
	  // Should already be caught by nullcheck simplification
	  return null;
	} else if (calleeThis.isConstant() || calleeThis.asRegister().isPreciseType()) {
	    return null;
	}
      } 
      if (methOp.hasPreciseTarget() && methOp.getTarget().isRuntimePure()) {
	  return null;
      } else if (methOp.hasPreciseTarget() && methOp.getTarget().isPure()) {
	// Look for a precise method call to a pure method with all constant arguments
	Method method = methOp.getTarget();
	int n = ((Call)s).getNumberOf();
	for(int i=0; i < n; i++) {
	  Operand param = ((Call)s).getParam(i);
	  if (!param.isConstant() || param.isNullConstant()) {
	    return null;
	  }
	}
	// Pure method with all constant arguments. Perform reflective method call
	Object thisArg = null;
	Type[] paramTypes = method.getParameterTypes();
	Object[] otherArgs;
	Object result = null;
	if (!methOp.isStatic()) {
	  thisArg = boxConstantOperand((ConstantOperand)((Call)s).getParam(0), method.getDeclaringClass());
	  n--;
	  otherArgs = new Object[n];
	  for(int i=0; i < n; i++) {
	    otherArgs[i] = boxConstantOperand((ConstantOperand)((Call)s).getParam(i+1),paramTypes[i]);
	  }
	} else {
	  otherArgs = new Object[n];
	  for(int i=0; i < n; i++) {
	    otherArgs[i] = boxConstantOperand((ConstantOperand)((Call)s).getParam(i),paramTypes[i]);
	  }
	}
	Throwable t = null;
	Method m = null;
	try {
	  Type[] argTypes = new Type[n];
	  for(int i=0; i < n; i++) {
	    argTypes[i] = ((Call)s).getParam(i).getType();
	  }
	  m = method.getDeclaringClass().getDeclaredMethod(method.getName().toString(), argTypes);
	  result = m.invoke(thisArg, otherArgs);
	} catch (Throwable e) { t = e;}
	if (t != null) {
	  // Call threw exception so leave in to generate at execution time
	  return null;
	}
	if (result == null) throw new Error("Method " + m + "/" + method + " returned null");
	if(method.getReturnType().isVoidType()) {
	  return s.replace(new Nop());
	} else {
	  return s.replace(Move.create(method.getReturnType(), ((Call)s).getClearResult(),
		  boxConstantObjectAsOperand(result, method.getReturnType())));

	}
      }
    }
    return null;
  }

  /**
   * Package up a constant operand as an object
   * @param op the constant operand to package
   * @param t the type of the object (needed to differentiate primitive from numeric types..)
   * @return the object
   */
  private static Object boxConstantOperand(ConstantOperand op, Type t){
    if (op.isObjectConstant()) {
      return op.asObjectConstant().value;
    } else if (op.isLongConstant()) {
      return op.asLongConstant().value;
    } else if (op.isFloatConstant()) {
      return op.asFloatConstant().value;
    } else if (op.isDoubleConstant()) {
      return op.asDoubleConstant().value;
    } else if (t.isIntType()) {
      return op.asIntConstant().value;
    } else if (t.isBooleanType()) {
      return op.asIntConstant().value == 1;
    } else if (t.isByteType()) {
      return (byte)op.asIntConstant().value;
    } else if (t.isCharType()) {
      return (char)op.asIntConstant().value;
    } else if (t.isShortType()) {
      return (short)op.asIntConstant().value;
    } else {
      throw new Error("Trying to box an VM magic unboxed type ("+op+
	  ")for a pure method call is not possible:\n"+op.instruction+
	  "\n at "+op.instruction.position);
    }
  }
  /**
   * Package up an object as a constant operand
   * @param x the object to package
   * @param t the type of the object (needed to differentiate primitive from numeric types..)
   * @return the constant operand
   */
  private static ConstantOperand boxConstantObjectAsOperand(Object x, Type t){
    assert !t.isUnboxedType();
    if (x == null) {
      throw new Error("Field of type: " + t + " is null");
    }
    if (t.isIntType()) {
      return new IntConstantOperand((Integer)x);
    } else if (t.isBooleanType()) {
      return new IntConstantOperand((Boolean)x ? 1 : 0);
    } else if (t.isByteType()) {
      return new IntConstantOperand((Byte)x);
    } else if (t.isCharType()) {
      return new IntConstantOperand((Character)x);
    } else if (t.isShortType()) {
      return new IntConstantOperand((Short)x);
    } else if (t.isLongType()) {
      return new LongConstantOperand((Long)x);
    } else if (t.isFloatType()) {
      return new FloatConstantOperand((Float)x);
    } else if (t.isDoubleType()) {
      return new DoubleConstantOperand((Double)x);
    } else if (x instanceof String) {
      // Handle as object constant but make sure to use interned String
      x = ((String)x).intern();
      return new ObjectConstantOperand(x, KnownTypes.STRING_TYPE);
    } else if (x instanceof Class) {
      // Handle as object constant
      return new ObjectConstantOperand(x, KnownTypes.CLASS_TYPE);
    } else {
      return new ObjectConstantOperand(x, KnownTypes.OBJECT_TYPE);
    }
  }

  private static Instruction getField(Instruction s, OptOptions opts) {
    if (opts.SIMPLIFY_FIELD_OPS) {
      Operand ref = ((GetField)s).getRef();
      if (ref.isNullConstant()) {
	// Simplify to an unreachable operand, this instruction is dead code
	// guarded by a nullcheck that should already have been simplified
	RegisterOperand result = (RegisterOperand)((GetField)s).getClearResult();
	return s.replace(Move.create(result.getType(), result, new UnreachableOperand()));
      } 
    }
    return null;
  }

  /**
   * To reduce the number of conditions to consider, we
   * transform all commutative
   * operators to a canoncial form.  The following forms are considered
   * canonical:
   * <ul>
   * <li> <code> Reg = Reg <op> Reg </code>
   * <li> <code> Reg = Reg <op> Constant </code>
   * <li> <code> Reg = Constant <op> Constant </code>
   * </ul>
   */
  private static void canonicalizeCommutativeOperator(Instruction instr) {
    if (((Binary)instr).getVal1().isConstant()) {
      Operand tmp1 = ((Binary)instr).getClearVal1();
      Operand tmp2 = ((Binary)instr).getClearVal2();
      ((Binary)instr).setVal1(tmp2);
      ((Binary)instr).setVal2(tmp1);
    }
  }

  /**
   * Compute 2 raised to the power v, 0 <= v <= 31
   */
  private static int PowerOf2(int v) {
    int i = 31;
    int power = -1;
    for (; v != 0; v = v << 1, i--) {
      if (v < 0) {
	if (power == -1) {
	  power = i;
	} else {
	  return -1;
	}
      }
    }
    return power;
  }

  /**
   * Turn the given operand encoding an address constant into an Address
   */
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
      return op.asObjectConstant().toAddress();
    }
    throw new Error("Cannot getAddressValue from this operand " + op);
  }
}
