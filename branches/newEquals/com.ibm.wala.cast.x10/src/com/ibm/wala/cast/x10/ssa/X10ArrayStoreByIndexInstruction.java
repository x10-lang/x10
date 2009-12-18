package com.ibm.wala.cast.x10.ssa;

import java.util.Collection;

import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.ssa.SSAInstructionFactory;
import com.ibm.wala.ssa.SymbolTable;
import com.ibm.wala.types.TypeReference;

public class X10ArrayStoreByIndexInstruction extends X10ArrayReferenceByIndexInstruction {
    private final int value;

    public X10ArrayStoreByIndexInstruction(int arrayRef, int[] indices, int value, TypeReference declaredType) {
	super(arrayRef, indices, declaredType);
	this.value = value;
    }

    public int getStoreValue() {
	return value;
    }

    @Override
    public SSAInstruction copyForSSA(SSAInstructionFactory insts, int[] defs, int[] uses) {
	int newArray = uses == null ? getArrayRef() : uses[0];
	int[] newIndices = uses == null ? getIndices() : new int[uses.length - 2];
	int newValue = uses == null ? getStoreValue() : uses[uses.length - 1];

	for(int i= 0; i < newIndices.length; i++) {
	    newIndices[i]= uses[i+1];
	}
	return ((X10InstructionFactory)insts).ArrayStoreByIndex(newArray, newIndices, newValue, declaredType);
    }

    @Override
    public Collection<TypeReference> getExceptionTypes() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public String toString(SymbolTable symbolTable) {
	return "x10arrayStoreByIndex " + getValueString(symbolTable, getArrayRef()) + "[" + getIndexString(symbolTable) + "] = " + getValueString(symbolTable, value);
    }

    @Override
    public void visit(IVisitor v) {
	((AstX10InstructionVisitor) v).visitArrayStoreByIndex(this);
    }

    @Override
    public int hashCode() {
        return 5779 + 7411 * value + 2819 * super.hashCode();
    }

    @Override
    public int getNumberOfUses() {
    	return 1 + super.getNumberOfUses();
    }

    @Override
    public int getUse(int j) throws UnsupportedOperationException {
    	if (j == super.getNumberOfUses()) {
    		return getStoreValue();
    	} else {
    		return super.getUse(j);
    	}
    }
}
