package com.ibm.domo.ast.x10.ssa;

import com.ibm.capa.impl.debug.Assertions;
import com.ibm.domo.ssa.SSAInstruction;
import com.ibm.domo.ssa.SymbolTable;
import com.ibm.domo.ssa.ValueDecorator;
import com.ibm.domo.types.TypeReference;

public class SSAHereInstruction extends SSAInstruction {
    private final int retValue;

    public SSAHereInstruction(int retValue) {
	this.retValue= retValue;
    }

    public SSAInstruction copyForSSA(int[] defs, int[] uses) {
	return new SSAHereInstruction(defs != null ? defs[0] : retValue);
    }

    public String toString(SymbolTable symbolTable, ValueDecorator d) {
	return getValueString(symbolTable, d, getDef()) + " = here()";
    }

    public void visit(Visitor v) {
	((AstX10InstructionVisitor) v).visitHere(this);
    }

    public int hashCode() {
	return 413771901 * retValue;
    }

    public TypeReference[] getExceptionTypes() {
	return null;
    }

    public boolean isFallThrough() {
	return true;
    }

    public int getNumberOfDefs() {
        return 1;
    }

    public int getDef() {
        return retValue;
    }

    public int getDef(int i) {
	Assertions._assert(i == 0);
        return retValue;
    }
}
