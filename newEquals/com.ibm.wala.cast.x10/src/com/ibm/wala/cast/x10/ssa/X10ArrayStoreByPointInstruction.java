package com.ibm.wala.cast.x10.ssa;

import java.util.Collection;

import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.ssa.SSAInstructionFactory;
import com.ibm.wala.ssa.SymbolTable;
import com.ibm.wala.types.TypeReference;

public class X10ArrayStoreByPointInstruction extends X10ArrayReferenceByPointInstruction {
    private final int value;

    public X10ArrayStoreByPointInstruction(int arrayRef, int pointIndex, int value, TypeReference declaredType) {
	super(arrayRef, pointIndex, declaredType);
	this.value= value;
    }

    public int getStoreValue() {
	return value;
    }

    @Override
    public SSAInstruction copyForSSA(SSAInstructionFactory insts, int[] defs, int[] uses) {
	return ((X10InstructionFactory)insts).ArrayStoreByPoint(
		uses == null ? arrayRef : uses[0],
		uses == null ? pointIndex : uses[1],
		uses == null ? value : uses[2],
		declaredType);
    }

    @Override
    public Collection<TypeReference> getExceptionTypes() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public String toString(SymbolTable symbolTable) {
	return "x10arrayStoreByPoint " + getValueString(symbolTable, getArrayRef()) + "[" + getValueString(symbolTable, getPointIndex()) + "] = " + getValueString(symbolTable, value);
    }

    @Override
    public void visit(IVisitor v) {
	((AstX10InstructionVisitor) v).visitArrayStoreByPoint(this);
    }

    @Override
    public int hashCode() {
        return 4273 + 6007 * value + 7369 * super.hashCode();
    }
}
