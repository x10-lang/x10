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

package x10me.opt.ir.operand;


import x10me.opt.bc2ir.IRGenOptions;
import x10me.opt.ir.Instruction;
import x10me.opt.ir.Register;
import x10me.types.KnownTypes;
import x10me.types.Type;

/**
 * An <code>Operand</code> identifies an operand for an
 * {@link Instruction}. A single Operand object should
 * not be shared between instructions (or even be used twice in
 * the same instruction).  Operands should not be shared between
 * instructions because we use the
 * {@link #instruction reference to the operand's containing instruction}
 * to construct use/def chains. We also store program-point specific
 * information about an {@link Register symbolic register}
 * in the {@link RegisterOperand RegisterOperands} that
 * {@link RegisterOperand#register refer} to the
 * <code>Register</code>.
 * <p>
 * Operands are divided into several primary categories
 * <ul>
 * <li> {@link RegisterOperand} represent symbolic and
 *      and physical registers.
 * <li> The subclasses of {@link ConstantOperand}
 *      represent various kinds of constant operands.
 * <li> {@link MethodOperand} represents the targets of CALL instructions.
 * <li> {@link BranchOperand}, {@link BasicBlockOperand},
 *      and {@link BranchOperand} are used to encode CFG
 *      information in LABEL, BBEND, and branch instructions.
 * <li> {@link ConditionOperand} and {@link TrapCodeOperand}
 *      encode the conditions tested by conditional branches and
 *      trap instructions.
 * <li> {@link LocationOperand} represents the memory location
 *      accessed by a load or store operation.
 * <li> {@link TypeOperand} encodes a {@link org.jikesrvm.classloader.RVMType} for use
 *      in instructions such as NEW or INSTANCEOF that operate on the
 *      type hierarchy.
 * </ul>
 *
 * @see Instruction
 * @see BasicBlockOperand
 * @see BranchOperand
 * @see ConditionOperand
 * @see ConstantOperand
 * @see DoubleConstantOperand
 * @see FloatConstantOperand
 * @see IntConstantOperand
 * @see LocationOperand
 * @see LongConstantOperand
 * @see MethodOperand
 * @see NullConstantOperand
 * @see RegisterOperand
 * @see StringConstantOperand
 * @see TrapCodeOperand
 * @see TrueGuardOperand
 * @see TypeOperand
 */
public abstract class Operand {

  /**
   * Handle back to containing instruction.
   */
  public Instruction instruction;


  /**
   * Is the operand an {@link AddressConstantOperand}?
   *
   * @return <code>true</code> if <code>this</code> is an 
   *         <code>instanceof</code> an {@link AddressConstantOperand},
   *         or <code>false</code> if it is not.
   */  
  public boolean isAddressConstant() {
    return this instanceof AddressConstantOperand;
  }
  
  /**
   * Is the operand an {@link ALengthOperand}?
   *
   * @return <code>true</code> if <code>this</code> is an 
   *         <code>instanceof</code> an {@link ALengthOperand},
   *         or <code>false</code> if it is not.
   */
  public boolean isALength() {
    // default to false and then override in subclasses
    return false;
  }

  /**
   * Is the operand an {@link ArrayOperand}?
   *
   * @return <code>true</code> if <code>this</code> is an 
   *         <code>instanceof</code> an {@link ArrayOperand},
   *         or <code>false</code> if it is not.
   */
  public boolean isArray() {
    // default to false and then override in subclasses
    return false;
  }
  /**
   * Is the operand an {@link BranchProfileOperand}?
   *
   * @return <code>true</code> if <code>this</code> is an 
   *         <code>instanceof</code> an {@link BranchProfileOperand},
   *         or <code>false</code> if it is not.
   */
  public boolean isBranchProfile() {
    // default to false and then override in subclasses
    return this instanceof BranchProfileOperand;
  }

  /**
   * Is the operand an {@link ConditionOperand}?
   *
   * @return <code>true</code> if <code>this</code> is an 
   *         <code>instanceof</code> an {@link ConditionOperand},
   *         or <code>false</code> if it is not.
   */
  public boolean isCondition() {
    // default to false and then override in subclasses
    return this instanceof ConditionOperand;
  }

  /**
   * Is the operand an {@link FieldOperand}?
   *
   * @return <code>true</code> if <code>this</code> is an 
   *         <code>instanceof</code> an {@link FieldOperand},
   *         or <code>false</code> if it is not.
   */
  public boolean isField() {
    // default to false and then override in subclasses
    return false;
  }

  /**
   * Is the operand an {@link RegisterOperand}?
   *
   * @return <code>true</code> if <code>this</code> is an
   *         <code>instanceof</code> an {@link RegisterOperand}
   *         or <code>false</code> if it is not.
   */
  public final boolean isRegister() {
    return this instanceof RegisterOperand;
  }

  /**
   * Is the operand an {@link ConstantOperand}?
   *
   * @return <code>true</code> if <code>this</code> is an
   *         <code>instanceof</code> an {@link ConstantOperand}
   *         or <code>false</code> if it is not.
   */
  public final boolean isConstant() {
    return this instanceof ConstantOperand;
  }

  /**
   * Is the operand an {@link IntConstantOperand}?
   *
   * @return <code>true</code> if <code>this</code> is an
   *         <code>instanceof</code> an {@link IntConstantOperand}
   *         or <code>false</code> if it is not.
   */
  public final boolean isIntConstant() {
    return this instanceof IntConstantOperand;
  }

  /**
   * Is the operand an {@link FloatConstantOperand}?
   *
   * @return <code>true</code> if <code>this</code> is an
   *         <code>instanceof</code> an {@link FloatConstantOperand}
   *         or <code>false</code> if it is not.
   */
  public final boolean isFloatConstant() {
    return this instanceof FloatConstantOperand;
  }

  /**
   * Is the operand an {@link LongConstantOperand}?
   *
   * @return <code>true</code> if <code>this</code> is an
   *         <code>instanceof</code> an {@link LongConstantOperand}
   *         or <code>false</code> if it is not.
   */
  public final boolean isLongConstant() {
    return this instanceof LongConstantOperand;
  }

  /**
   * Is the operand an {@link DoubleConstantOperand}?
   *
   * @return <code>true</code> if <code>this</code> is an
   *         <code>instanceof</code> an {@link DoubleConstantOperand}
   *         or <code>false</code> if it is not.
   */
  public final boolean isDoubleConstant() {
    return this instanceof DoubleConstantOperand;
  }

  /**
   * Is the operand an {@link StringConstantOperand}?
   *
   * @return <code>true</code> if <code>this</code> is an
   *         <code>instanceof</code> an {@link StringConstantOperand}
   *         or <code>false</code> if it is not.
   */
  public final boolean isStringConstant() {
    return this instanceof StringConstantOperand;
  }

  /**
   * Is the operand an {@link ObjectConstantOperand}?
   *
   * @return <code>true</code> if <code>this</code> is an
   *         <code>instanceof</code> an {@link ObjectConstantOperand}
   *         or <code>false</code> if it is not.
   */
  public final boolean isObjectConstant() {
    return this instanceof ObjectConstantOperand;
  }

  /**
   * Is the operand a movable {@link ObjectConstantOperand}?
   *
   * @return false
   */
  public boolean isMovableObjectConstant() {
    return false;
  }

  /**
   * Is the operand an {@link NullConstantOperand}?
   *
   * @return <code>true</code> if <code>this</code> is an
   *         <code>instanceof</code> an {@link NullConstantOperand}
   *         or <code>false</code> if it is not.
   */
  public final boolean isNullConstant() {
    return this instanceof NullConstantOperand;
  }

  /**
   * Is the operand an {@link TrueGuardOperand}?
   *
   * @return <code>true</code> if <code>this</code> is an
   *         <code>instanceof</code> an {@link TrueGuardOperand}
   *         or <code>false</code> if it is not.
   */
  public final boolean isTrueGuard() {
    return this instanceof TrueGuardOperand;
  }

  /**
   * Is the operand an {@link BranchOperand}?
   *
   * @return <code>true</code> if <code>this</code> is an
   *         <code>instanceof</code> an {@link BranchOperand}
   *         or <code>false</code> if it is not.
   */
  public final boolean isBranch() {
    return this instanceof BranchOperand;
  }

  /**
   * Is the operand an {@link BasicBlockOperand}?
   *
   * @return <code>true</code> if <code>this</code> is an
   *         <code>instanceof</code> an {@link BasicBlockOperand}
   *         or <code>false</code> if it is not.
   */
  public final boolean isBlock() {
    return this instanceof BasicBlockOperand;
  }

  /**
   * Is the operand an {@link MethodOperand}?
   *
   * @return <code>true</code> if <code>this</code> is an
   *         <code>instanceof</code> an {@link MethodOperand}
   *         or <code>false</code> if it is not.
   */
  public final boolean isMethod() {
    return this instanceof MethodOperand;
  }

  /**
   * Is the operand an {@link TypeOperand}?
   *
   * @return <code>true</code> if <code>this</code> is an
   *         <code>instanceof</code> an {@link TypeOperand}
   *         or <code>false</code> if it is not.
   */
  public final boolean isType() {
    return this instanceof TypeOperand;
  }

  /**
   * Cast to an {@link AddressConstantOperand}.
   *
   * @return <code>this</code> cast as an {@link AddressConstantOperand}
   */  
  public final AddressConstantOperand asAddressConstant() {
    return (AddressConstantOperand) this;
  }
  
  /**
   * Cast to an {@link RegisterOperand}.
   *
   * @return <code>this</code> cast as an {@link RegisterOperand}
   */
  public final RegisterOperand asRegister() {
    return (RegisterOperand) this;
  }

  /**
   * Cast to an {@link IntConstantOperand}.
   *
   * @return <code>this</code> cast as an {@link IntConstantOperand}
   */
  public final IntConstantOperand asIntConstant() {
    return (IntConstantOperand) this;
  }

 /**
   * Cast to an {@link FloatConstantOperand}.
   *
   * @return <code>this</code> cast as an {@link FloatConstantOperand}
   */
  public final FloatConstantOperand asFloatConstant() {
    return (FloatConstantOperand) this;
  }

  /**
   * Cast to an {@link LongConstantOperand}.
   *
   * @return <code>this</code> cast as an {@link LongConstantOperand}
   */
  public final LongConstantOperand asLongConstant() {
    return (LongConstantOperand) this;
  }

  /**
   * Cast to an {@link DoubleConstantOperand}.
   *
   * @return <code>this</code> cast as an {@link DoubleConstantOperand}
   */
  public final DoubleConstantOperand asDoubleConstant() {
    return (DoubleConstantOperand) this;
  }

  /**
   * Cast to an {@link StringConstantOperand}.
   *
   * @return <code>this</code> cast as an {@link StringConstantOperand}
   */
  public final StringConstantOperand asStringConstant() {
    return (StringConstantOperand) this;
  }

  /**
   * Cast to an {@link ObjectConstantOperand}.
   *
   * @return <code>this</code> cast as an {@link ObjectConstantOperand}
   */
  public final ObjectConstantOperand asObjectConstant() {
    return (ObjectConstantOperand) this;
  }

  /**
   * Cast to an {@link NullConstantOperand}.
   *
   * @return <code>this</code> cast as an {@link NullConstantOperand}
   */
  public final NullConstantOperand asNullConstant() {
    return (NullConstantOperand) this;
  }

  /**
   * Cast to an {@link BranchOperand}.
   *
   * @return <code>this</code> cast as an {@link BranchOperand}
   */
  public final BranchOperand asBranch() {
    return (BranchOperand) this;
  }

  /**
   * Cast to an {@link BasicBlockOperand}.
   *
   * @return <code>this</code> cast as an {@link BasicBlockOperand}
   */
  public final BasicBlockOperand asBlock() {
    return (BasicBlockOperand) this;
  }

  /**
   * Cast to an {@link MethodOperand}.
   *
   * @return <code>this</code> cast as an {@link MethodOperand}
   */
  public final MethodOperand asMethod() {
    return (MethodOperand) this;
  }

  /**
   * Cast to an {@link TypeOperand}.
   *
   * @return <code>this</code> cast as an {@link TypeOperand}
   */
  public final TypeOperand asType() {
    return (TypeOperand) this;
  }

  /**
   * Cast to an {@link ConditionOperand}.
   *
   * @return <code>this</code> cast as an {@link ConditionOperand}
   */
  public final ConditionOperand asCondition() {
    return (ConditionOperand) this;
  }

  /**
   * Does the operand represent a value of an int-like data type?
   *
   * @return <code>true</code> if the data type of <code>this</code>
   *         is int-like as defined by {@link TypeReference#isIntLikeType}
   *         or <code>false</code> if it is not.
   */
  public boolean isIntLike() {
    // default to false and then override in subclasses
    return false;
  }

  /**
   * Does the operand represent a value of the int data type?
   *
   * @return <code>true</code> if the data type of <code>this</code>
   *         is an int as defined by {@link TypeReference#isIntType}
   *         or <code>false</code> if it is not.
   */
  public boolean isInt() {
    // default to false and then override in subclasses
    return false;
  }

  /**
   * Does the operand represent a value of the long data type?
   *
   * @return <code>true</code> if the data type of <code>this</code>
   *         is a long as defined by {@link TypeReference#isLongType}
   *         or <code>false</code> if it is not.
   */
  public boolean isLong() {
    // default to false and then override in subclasses
    return false;
  }

  /**
   * Does the operand represent a value of the float data type?
   *
   * @return <code>true</code> if the data type of <code>this</code>
   *         is a float as defined by {@link TypeReference#isFloatType}
   *         or <code>false</code> if it is not.
   */
  public boolean isFloat() {
    // default to false and then override in subclasses
    return false;
  }

  /**
   * Does the operand represent a value of the double data type?
   *
   * @return <code>true</code> if the data type of <code>this</code>
   *         is a double as defined by {@link TypeReference#isDoubleType}
   *         or <code>false</code> if it is not.
   */
  public boolean isDouble() {
    // default to false and then override in subclasses
    return false;
  }

  /**
   * Does the operand represent a value of the reference data type?
   *
   * @return <code>true</code> if the data type of <code>this</code>
   *         is a reference as defined by {@link TypeReference#isReferenceType}
   *         or <code>false</code> if it is not.
   */
  public boolean isRef() {
    // default to false and then override in subclasses
    return false;
  }

  /**
   * Does the operand represent a value of the address data type?
   *
   * @return <code>true</code> if the data type of <code>this</code>
   *         is an address as defined by {@link TypeReference#isWordType}
   *         or <code>false</code> if it is not.
   */
  public boolean isAddress() {
    // default to false and then override in subclasses
    return false;
  }

  /**
   * Does the operand definitely represent <code>null</code>?
   *
   * @return <code>true</code> if the operand definitely represents
   *         <code>null</code> or <code>false</code> if it does not.
   */
  public boolean isDefinitelyNull() {
    // default to false and then override in subclasses
    return false;
  }

  /**
   * Return a new operand that is semantically equivalent to <code>this</code>.
   *
   * @return a copy of <code>this</code>
   */
  public abstract Operand copy();

  /**
   * Is the accessed location possibly volatile?
   */
  public boolean mayBeVolatile() {
    return false;
  }

	  /**
   * Are two operands semantically equivalent?
   *
   * @param op other operand
   * @return   <code>true</code> if <code>this</code> and <code>op</code>
   *           are semantically equivalent or <code>false</code>
   *           if they are not.
   */
  public abstract boolean similar(Operand op);

  /**
   * Return the {@link TypeReference} of the value represented by the operand.
   *
   * @return the type of the value represented by the operand
   */
  public Type getType() {
    throw new Error("Getting the type for this operand has no defined meaning: " + this);
  }

  /**
   * Return the index of the operand in its containing instruction (SLOW).
   *
   * @return the index of the operand in its containing instruction
   */
  public int getIndexInInstruction() {
    for (int i = 0; i < instruction.getNumberOfOperands(); i++) {
      Operand op = instruction.getOperand(i);
      if (op == this) return i;
    }
    throw new Error();
  }
}
