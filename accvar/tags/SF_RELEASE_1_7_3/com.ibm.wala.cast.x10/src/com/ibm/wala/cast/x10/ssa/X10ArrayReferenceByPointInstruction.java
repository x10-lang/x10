package com.ibm.wala.cast.x10.ssa;

import com.ibm.wala.types.TypeReference;
import com.ibm.wala.util.debug.Assertions;

public abstract class X10ArrayReferenceByPointInstruction extends X10ArrayReferenceInstruction {
    protected final int pointIndex;

    public X10ArrayReferenceByPointInstruction(int arrayRef, int pointIndex, TypeReference declaredType) {
	super(arrayRef, declaredType);
	this.pointIndex= pointIndex;
    }

    public int getPointIndex() {
	return pointIndex;
    }

    @Override
    public int getNumberOfUses() {
	return 2;
    }

    @Override
    public int getUse(int j) throws UnsupportedOperationException {
	if (Assertions.verifyAssertions)
	    Assertions._assert(j <= 2);
	return (j == 0) ? arrayRef : pointIndex;
    }

    @Override
    public int hashCode() {
        return 5087 + 1801 * pointIndex + 6221 * super.hashCode();
    }
}
