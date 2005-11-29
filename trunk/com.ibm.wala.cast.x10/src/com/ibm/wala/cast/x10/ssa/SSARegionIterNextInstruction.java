package com.ibm.domo.ast.x10.ssa;

import com.ibm.capa.impl.debug.Assertions;
import com.ibm.domo.ssa.SSAInstruction;
import com.ibm.domo.ssa.SymbolTable;
import com.ibm.domo.ssa.ValueDecorator;
import com.ibm.domo.types.TypeReference;

public class SSARegionIterNextInstruction extends SSAInstruction {

    private final int regionIter;
    private final int nextValue;

    public SSARegionIterNextInstruction(int hasNextValue, int regionIter) {
	this.regionIter= regionIter;
	this.nextValue= hasNextValue;
    }

    public SSAInstruction copyForSSA(int[] defs, int[] uses) {
	return new SSARegionIterNextInstruction((defs != null ? defs[0] : nextValue), (uses != null ? uses[0] : regionIter));
    }

    public String toString(SymbolTable symbolTable, ValueDecorator d) {
	return getValueString(symbolTable, d, nextValue) + " = regionIterNext(" + getValueString(symbolTable, d, regionIter) + ")";
    }

    public void visit(Visitor v) {
	((AstX10InstructionVisitor) v).visitRegionIterNext(this);
    }

    public int hashCode() {
	return (49 + (nextValue * 13)) * 11 + regionIter;
    }

    public TypeReference[] getExceptionTypes() {
	return null;
    }

    public boolean isFallThrough() {
	return true;
    }

    public int getDef() {
        return nextValue;
    }

    public int getDef(int i) {
	Assertions._assert(i == 0, "invalid def index for regionIteratorNext instruction");
        return nextValue;
    }

    public int getNumberOfDefs() {
        return 1;
    }

    public boolean hasDef() {
        return true;
    }

    public int getUse(int j) {
	Assertions._assert(j == 0, "invalid use index for regionIteratorNext instruction");
	return regionIter;
    }

    public int getNumberOfUses() {
        return 1;
    }
}
