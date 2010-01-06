package com.ibm.wala.cast.x10.ssa;

import java.util.Collection;
import java.util.Collections;

import com.ibm.wala.ssa.SSAAbstractUnaryInstruction;
import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.ssa.SSAInstructionFactory;
import com.ibm.wala.ssa.SymbolTable;
import com.ibm.wala.types.TypeReference;

public class SSAForceInstruction extends SSAAbstractUnaryInstruction {

    private final TypeReference valueType;

    public SSAForceInstruction(int retValue, int targetValue, TypeReference valueType) {
	super(retValue, targetValue);
	this.valueType= valueType;
    }

    public SSAInstruction copyForSSA(SSAInstructionFactory insts, int[] defs, int[] uses) {
	return ((X10InstructionFactory)insts).Force((defs != null) ? defs[0] : getDef(0), (uses != null) ? uses[0] : getUse(0), valueType);
    }

    public String toString(SymbolTable symbolTable) {
	return getValueString(symbolTable, getDef()) + " = force(" + getValueString(symbolTable, getUse(0)) + ")";
    }

    public void visit(IVisitor v) {
	((AstX10InstructionVisitor) v).visitForce(this);
    }

    public Collection getExceptionTypes() {
	return Collections.EMPTY_SET;
    }

    public TypeReference getValueType() {
        return valueType;
    }
}
