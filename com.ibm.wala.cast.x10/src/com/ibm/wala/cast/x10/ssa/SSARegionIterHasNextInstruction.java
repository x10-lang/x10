package com.ibm.domo.ast.x10.ssa;

import com.ibm.capa.impl.debug.Assertions;
import com.ibm.domo.ssa.SSAInstruction;
import com.ibm.domo.ssa.SymbolTable;
import com.ibm.domo.ssa.ValueDecorator;
import com.ibm.domo.types.TypeReference;

public class SSARegionIterHasNextInstruction extends SSAInstruction {
    private final int regionIter;
    private final int hasNextValue;

    public SSARegionIterHasNextInstruction(int hasNextValue, int regionIter) {
	this.regionIter= regionIter;
	this.hasNextValue= hasNextValue;
    }

    public SSAInstruction copyForSSA(int[] defs, int[] uses) {
	return new SSARegionIterHasNextInstruction((defs != null ? defs[0] : hasNextValue), (uses != null ? uses[0] : regionIter));
    }

    public String toString(SymbolTable symbolTable, ValueDecorator d) {
	return getValueString(symbolTable, d, hasNextValue) + " = regionIterHasNext(" + getValueString(symbolTable, d, regionIter) + ")";
    }

    public void visit(Visitor v) {
	((AstX10InstructionVisitor) v).visitRegionIterHasNext(this);
    }

    public int hashCode() {
	return (49 + (hasNextValue * 13)) * 11 + regionIter;
    }

    public TypeReference[] getExceptionTypes() {
	return null;
    }

    public boolean isFallThrough() {
	return true;
    }

    public int getDef() {
        return hasNextValue;
    }

    public int getDef(int i) {
	Assertions._assert(i == 0, "invalid def index for regionIteratorHasNext instruction");
        return hasNextValue;
    }

    public int getNumberOfDefs() {
        return 1;
    }

    public boolean hasDef() {
        return true;
    }

    public int getUse(int j) {
	Assertions._assert(j == 0, "invalid use index for regionIteratorHasNext instruction");
	return regionIter;
    }

    public int getNumberOfUses() {
        return 1;
    }
}
