package com.ibm.wala.cast.x10.ssa;

import java.util.Collection;

import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.ssa.SSAInstructionFactory;
import com.ibm.wala.ssa.SymbolTable;
import com.ibm.wala.types.TypeReference;

public class X10ArrayLoadByIndexInstruction extends X10ArrayReferenceByIndexInstruction {
    private final int result;

    public X10ArrayLoadByIndexInstruction(int result, int arrayRef, int dims[], TypeReference declaredType) {
	super(arrayRef, dims, declaredType);
	this.result = result;
    }

    @Override
    public boolean hasDef() {
      return true;
    }

    @Override
    public int getDef() {
      return result;
    }

    @Override
    public int getDef(int i) {
      assert i == 0;
      return result;
    }

    @Override
    public int getNumberOfDefs() {
      return 1;
    }

    @Override
    public SSAInstruction copyForSSA(SSAInstructionFactory insts, int[] defs, int[] uses) {
	int newArray = uses == null ? getArrayRef() : uses[0];
	int[] newIndices = uses == null ? getIndices() : new int[uses.length - 1];

	if (uses != null) {
	    for(int i= 0; i < newIndices.length; i++) {
		newIndices[i]= uses[i+1];
	    }
	}
	return ((X10InstructionFactory)insts).ArrayLoadByIndex(
		      defs == null ? result : defs[0],
		      newArray,
		      newIndices,
		      getDeclaredType());
    }

    @Override
    public Collection<TypeReference> getExceptionTypes() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public String toString(SymbolTable symbolTable) {
	return getValueString(symbolTable, result) + " = x10arrayLoadByIndex " + getValueString(symbolTable, getArrayRef()) + "[" + getIndexString(symbolTable) + "]";
    }

    @Override
    public void visit(IVisitor v) {
	((AstX10InstructionVisitor) v).visitArrayLoadByIndex(this);
    }

    @Override
    public int hashCode() {
        return 4021 + 5563 * result + 4003 * super.hashCode();
    }
}
