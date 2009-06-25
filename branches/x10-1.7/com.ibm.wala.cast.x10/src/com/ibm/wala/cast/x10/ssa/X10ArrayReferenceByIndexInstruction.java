package com.ibm.wala.cast.x10.ssa;

import com.ibm.wala.ssa.SymbolTable;
import com.ibm.wala.types.TypeReference;
import com.ibm.wala.util.debug.Assertions;

public abstract class X10ArrayReferenceByIndexInstruction extends X10ArrayReferenceInstruction {
    private final int[] indices;

    public X10ArrayReferenceByIndexInstruction(int arrayRef, int[] indices, TypeReference declaredType) {
	super(arrayRef, declaredType);
	this.indices = indices;
	if (Assertions.verifyAssertions)
	    Assertions._assert(indices.length > 0, "Must have > 0 indices for array reference operation");
    }

    public int[] getIndices() {
	return indices;
    }

    @Override
    public int getNumberOfUses() {
	return 1 + indices.length;
    }

    @Override
    public int getUse(int j) throws UnsupportedOperationException {
	if (Assertions.verifyAssertions)
	    Assertions._assert(j <= 1 + indices.length);
	return (j == 0) ? arrayRef : indices[j-1];
    }

    @Override
    public int hashCode() {
	int result= 3643 + 1993 * super.hashCode();
	for(int i= 0; i < indices.length; i++) {
	    result = 5683 * result + 7027 * indices[i];
	}
	return result;
    }

    protected StringBuffer getIndexString(SymbolTable symbolTable) {
        StringBuffer indexBuff= new StringBuffer();
        int[] indices= getIndices();
        for(int i= 0; i < indices.length; i++) {
            if (i > 0) indexBuff.append(',');
            indexBuff.append(getValueString(symbolTable, indices[i]));
        }
        return indexBuff;
    }
}
