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

package x10me.types;

public class Type {

  public boolean isFinal() {
	// TODO Auto-generated method stub
    return false;
  }

  public String getName() {
	// TODO Auto-generated method stub
     return null;
  }

  public boolean isArrayType() {
    return false;
  }

  public Type getArrayElementType() {
    // TODO Auto-generated method stub
    return null;
  }
  
  public boolean isPrimitiveType () {
    return false;
  }

  public boolean isClassType() {
    // TODO Auto-generated method stub
    return false;
  }

  public boolean isInterface() {
    // TODO Auto-generated method stub
    return false;
  }

  public boolean isAbstract() {
    // TODO Auto-generated method stub
    return false;
  }

  public boolean isIntLikeType() {
    // TODO Auto-generated method stub
    return false;
  }
  
  public boolean isIntType() {
    return false;
  }
  
  public boolean isLongType() {
    return false;
  }
  
  public boolean isFloatType() {
    return false;
  }
  
  public boolean isDoubleType() {
    return false;
  }
  
  public boolean isReferenceType() {
    return false;
  }
  
  public boolean isWordType() {
    return false;
  }

  public boolean isBooleanType() {
    // TODO Auto-generated method stub
    return false;
  }

  public boolean isByteType() {
    // TODO Auto-generated method stub
    return false;
  }

  public boolean isShortType() {
    // TODO Auto-generated method stub
    return false;
  }
  
  public boolean isAssignableTo(Type other) {
    return false;
  }
  
  /**
   * Returns <code>true</code> iff an array with element type this can be 
   * assigned to an array with element type other. 
   * @param other the array element type to compared with. 
   * @return <code>true</code> iff an array with element type this can be 
   * assigned to an array with element type other. 
   */
  public boolean isArrayAssignableTo(Type other) {
    return false;
  }
  
  /**
   * Return true if two primitive types are compatible.
   * 
   * @param type1 a primitive type
   * @param type2 a primitive type
   * @return <code>true</code> if primitive type1 is compatible with primitive 
   * type2 or <code>false</code> if it does not.
   */
  public static boolean compatiblePrimitives(Type type1, Type type2) {
    // TODO Shouldn't float and double be compatible?
    if (type1.isIntLikeType() && type2.isIntLikeType()) {
      if (type1 == type2) {
	return true;
      }
      if (type1.isIntType()) {
        return type2.isBooleanType() || type2.isByteType() || type2.isShortType() || type2.isIntType();
      }
      if (type1.isShortType()) {
        return type2.isBooleanType() || type2.isByteType() || type2.isShortType();
      }
      if (type1.isByteType()) {
        return type2.isBooleanType() || type2.isByteType();
      }
      if (type1.isBooleanType()) {
        return type2.isBooleanType();
      }
    }
    return false;
  }

  public static Type findCommonSuper(Type type, Type type2) {
    // TODO Auto-generated method stub
    return null;
  }

  public static boolean includesType(Type type1, Type type2) {
    // TODO Auto-generated method stub
    return false;
  }

  public boolean isUnboxedType() {
    // TODO Auto-generated method stub
    return false;
  }

  public boolean isAddressType() {
    // TODO Auto-generated method stub
    return false;
  }

  public Type getInnermostElementType() {
    // TODO Auto-generated method stub
    return null;
  }

  public boolean isCharType() {
    // TODO Auto-generated method stub
    return false;
  }

  public Method getDeclaredMethod(String string, Type[] argTypes) {
    // TODO Auto-generated method stub
    return null;
  }

  public boolean isVoidType() {
    // TODO Auto-generated method stub
    return false;
  }
}