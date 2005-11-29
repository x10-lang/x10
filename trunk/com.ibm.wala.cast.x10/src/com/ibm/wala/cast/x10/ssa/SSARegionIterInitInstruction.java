package com.ibm.domo.ast.x10.ssa;

import com.ibm.capa.impl.debug.Assertions;
import com.ibm.domo.ssa.SSAInstruction;
import com.ibm.domo.ssa.SymbolTable;
import com.ibm.domo.ssa.ValueDecorator;
import com.ibm.domo.types.TypeReference;

public class SSARegionIterInitInstruction extends SSAInstruction {
    private final int regionVal;
    private final int iterVal;

    public SSARegionIterInitInstruction(int iterVal, int regionVal) {
	this.regionVal= regionVal;
	this.iterVal= iterVal;
    }

    public SSAInstruction copyForSSA(int[] defs, int[] uses) {
	return new SSARegionIterInitInstruction((defs != null ? defs[0] : iterVal), (uses != null ? uses[0] : regionVal));
    }

    public String toString(SymbolTable symbolTable, ValueDecorator d) {
	return getValueString(symbolTable, d, iterVal) + " = regionIter(" + getValueString(symbolTable, d, regionVal) + ")";
    }

    public void visit(Visitor v) {
	((AstX10InstructionVisitor) v).visitRegionIterInit(this);
    }

    public int hashCode() {
	return (49 + (iterVal * 13)) * 11 + regionVal;
    }

    public TypeReference[] getExceptionTypes() {
	return null;
    }

    public boolean isFallThrough() {
	return true;
    }

    public int getDef() {
        return iterVal;
    }

    public int getDef(int i) {
	Assertions._assert(i == 0, "invalid def index for regionIterator instruction");
        return iterVal;
    }

    public int getNumberOfDefs() {
        return 1;
    }

    public boolean hasDef() {
        return true;
    }

    public int getUse(int j) {
	Assertions._assert(j == 0, "invalid use index for regionIterator instruction");
	return regionVal;
    }

    public int getNumberOfUses() {
        return 1;
    }
}
