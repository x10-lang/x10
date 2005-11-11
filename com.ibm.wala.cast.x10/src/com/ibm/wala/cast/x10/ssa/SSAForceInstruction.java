package com.ibm.domo.ast.x10.ssa;

import com.ibm.domo.ssa.SSAInstruction;
import com.ibm.domo.ssa.SymbolTable;
import com.ibm.domo.ssa.ValueDecorator;
import com.ibm.domo.types.TypeReference;

public class SSAForceInstruction extends SSAInstruction {

    private final int retValue;
    private final int targetValue;
    private final TypeReference valueType;

    public SSAForceInstruction(int retValue, int targetValue, TypeReference valueType) {
	this.retValue= retValue;
	this.targetValue= targetValue;
	this.valueType= valueType;
    }

    public SSAInstruction copyForSSA(int[] defs, int[] uses) {
	return new SSAForceInstruction((defs != null) ? defs[0] : retValue, (uses != null) ? uses[0] : targetValue, valueType);
    }

    public String toString(SymbolTable symbolTable, ValueDecorator d) {
	return getValueString(symbolTable, d, retValue) + " = force(" + getValueString(symbolTable, d, targetValue) + ")";
    }

    public void visit(Visitor v) {
	((AstX10InstructionVisitor) v).visitForce(this);
    }

    public int hashCode() {
	return (49 + (retValue * 13)) * 11 + targetValue ;
    }

    public TypeReference[] getExceptionTypes() {
	return null;
    }

    public boolean isFallThrough() {
	return true;
    }

    public TypeReference getValueType() {
        return valueType;
    }
}
