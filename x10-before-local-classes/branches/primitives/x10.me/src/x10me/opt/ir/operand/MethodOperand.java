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

import x10me.types.Field;
import x10me.types.Method;
import x10me.types.Type;

/**
 * Refers to a method. Used for method call instructions.
 * Contains a Method (which may or may not have been resolved yet.)
 *
 * TODO: Create subclasses of MethodOperand for internal & specialized
 * targets.
 *
 * @see Operand
 * @see Method
 */
public final class MethodOperand extends LocationOperand {

  /* Enumeration of types of invokes */
  private static final byte UNKNOWN = -1; 
  private static final byte STATIC = 0;
  private static final byte SPECIAL = 1;
  private static final byte VIRTUAL = 2;
  private static final byte INTERFACE = 3;

  /**
   * Target Method of invocation.
   */
  Method target;
  
  /**
   * Precise target of invocation.  This is non null if we are able 
   * to precisely determine the target of the call.
   * This is always set for statics, but may also be set for the 
   * other types if we are able to determine the precise 
   * type of the object making the call.
   */

  Method preciseTarget;

  /**
   * Is this the operand of a call that never returns?
   */
  boolean isNonReturningCall;

  /**
   * Is this the operand of a call that is the off-branch of a guarded inline?
   */
  boolean isGuardedInlineOffBranch;

  /**
   * The type of the invoke (STATIC, SPECIAL, VIRTUAL, INTERFACE)
   */
  byte type = UNKNOWN;

  /**
   * @param ref MemberReference of method to call
   * @param tar the Method to call (may be null)
   * @param t the type of invoke used to call it (STATIC, SPECIAL, VIRTUAL, INTERFACE)
   */
  private MethodOperand(Method tar, Method ptar, byte t) {
    target = tar;
    preciseTarget = ptar;
    type = t;
    setPreciseTarget();
  }

  private void setPreciseTarget() {
    if (isVirtual()) {
      if (target.isFinal() || target.getDeclaringClass().isFinal())
	preciseTarget = target;
    }
  }
      
  /**
   * create a method operand for an INVOKE_SPECIAL bytecode
   *
   * @param general target of method to call
   * @param precise target the method to call (may be null)
   * @return the newly created method operand
   */
  public static MethodOperand SPECIAL(Method target, Method preciseTarget) {
    return new MethodOperand(target, preciseTarget, SPECIAL);
  }

  /**
   * create a method operand for an INVOKE_STATIC bytecode
   *
   * @param target Member of method to call
   * @return the newly created method operand
   */
  public static MethodOperand STATIC(Method target) {
    return new MethodOperand(target, target, STATIC);
  }

  /**
   * create a method operand for an INVOKE_VIRTUAL bytecode
   *
   * @param general target of method to call
   * @param precise target the method to call (may be null)
   * @return the newly created method operand
   */
  public static MethodOperand VIRTUAL(Method target, Method preciseTarget) {
    return new MethodOperand(target, preciseTarget, VIRTUAL);
  }

  /**
   * create a method operand for an INVOKE_INTERFACE bytecode
   *
   * @param general target of method to call
   * @param precise target the method to call (may be null)
   * @return the newly created method operand
   */
  public static MethodOperand INTERFACE(Method target, Method preciseTarget) {
    return new MethodOperand(target, preciseTarget, INTERFACE);
  }

  public boolean isStatic() {
    return type == STATIC;
  }

  public boolean isVirtual() {
    return type == VIRTUAL;
  }

  public boolean isSpecial() {
    return type == SPECIAL;
  }

  public boolean isInterface() {
    return type == INTERFACE;
  }

  public boolean hasPreciseTarget() {
    return preciseTarget != null;
  }

  public Method getTarget() {
    return target;
  }

  public Method getPreciseTarget() {
    return preciseTarget;
  }

  /**
   * Get whether this operand represents a method call that never
   * returns (such as a call to athrow());
   *
   * @return Does this op represent a call that never returns?
   */
  public boolean isNonReturningCall() {
    return isNonReturningCall;
  }

  /**
   * Record whether this operand represents a method call that never
   * returns (such as a call to athrow());
   */
  public void setIsNonReturningCall(boolean neverReturns) {
    isNonReturningCall = neverReturns;
  }

  /**
   * Return whether this operand is the off branch of a guarded inline
   */
  public boolean isGuardedInlineOffBranch() {
    return isGuardedInlineOffBranch;
  }

  /**
   * Record that this operand is the off branch of a guarded inline
   */
  public void setIsGuardedInlineOffBranch(boolean f) {
    isGuardedInlineOffBranch = f;
  }

  /**
   * Refine the target information. Used to reduce the set of
   * targets for an invokevirtual.
   */
  public void refine(Method ptar) {
    this.preciseTarget = ptar;
    setPreciseTarget();
  }

  /**
   * Return a new operand that is semantically equivalent to <code>this</code>.
   *
   * @return a copy of <code>this</code>
   */
  public Operand copy() {
    MethodOperand mo = new MethodOperand(target, preciseTarget, type);
    mo.isNonReturningCall = isNonReturningCall;
    mo.isGuardedInlineOffBranch = isGuardedInlineOffBranch;
    return mo;
  }

  @Override
  public boolean mayBeTypeBasedAliased(LocationOperand other) {
    throw new Error();
  }

  @Override
  public boolean mustBeTypeBasedAliased(LocationOperand other) {
    throw new Error();
  }

  /**
   * Are two operands semantically equivalent?
   *
   * @param op other operand
   * @return   <code>true</code> if <code>this</code> and <code>op</code>
   *           are semantically equivalent or <code>false</code>
   *           if they are not.
   */
  public boolean similar(Operand op) {
    if (op instanceof MethodOperand) {
      MethodOperand mop = (MethodOperand) op;
      return target == mop.target && preciseTarget == mop.preciseTarget;
    } else {
      return false;
    }
  }

  /**
   * Returns the string representation of this operand.
   *
   * @return a string representation of this operand.
   */
  public String toString() {
    String s = "";
    switch (type) {
      case STATIC:
        s += "static";
        break;
      case SPECIAL:
        s += "special";
        break;
      case VIRTUAL:
        s += "virtual";
        break;
      case INTERFACE:
        s += "interface";
        break;
    }
    if (preciseTarget != null && (type != STATIC)) {
      s += "_exact";
    }

    if (target != null) {
      return s + "\"" + target + "\"";
    } else {
      return s + "<" + preciseTarget + ">";
    }
  }
}
