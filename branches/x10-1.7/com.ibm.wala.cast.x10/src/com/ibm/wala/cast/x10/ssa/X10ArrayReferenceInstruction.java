package com.ibm.wala.cast.x10.ssa;

import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.types.TypeReference;

public abstract class X10ArrayReferenceInstruction extends SSAInstruction {
    protected final int arrayRef;

    protected final TypeReference declaredType;

    X10ArrayReferenceInstruction(int arrayRef, TypeReference declaredType) {
	this.arrayRef = arrayRef;
	this.declaredType = declaredType;
    }

    /**
     * Return the value number of the array reference.
     */
    public int getArrayRef() {
      return arrayRef;
    }

    public TypeReference getDeclaredType() {
	return declaredType;
    }

    /**
     * @return true iff this represents an aload of a primitive type element
     */
    public boolean typeIsPrimitive() {
	return declaredType.isPrimitiveType();
    }

    @Override
    public boolean isPEI() {
	return true;
    }

    @Override
    public boolean isFallThrough() {
	return true;
    }

    @Override
    public int hashCode() {
	return 7103 + 7877 * arrayRef + 6553 * declaredType.hashCode();
    }
}
