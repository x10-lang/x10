package com.ibm.wala.cast.x10.ssa;

import java.util.Collection;
import java.util.Collections;
import com.ibm.wala.ssa.SSAAbstractUnaryInstruction;
import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.ssa.SSAInstructionFactory;
import com.ibm.wala.ssa.SymbolTable;

public class SSARegionIterHasNextInstruction extends SSAAbstractUnaryInstruction {

    public SSARegionIterHasNextInstruction(int hasNextValue, int regionIter) {
	super(hasNextValue, regionIter);
    }

    public SSAInstruction copyForSSA(SSAInstructionFactory insts, int[] defs, int[] uses) {
	return ((X10InstructionFactory)insts).RegionIterHasNext((defs != null ? defs[0] : getDef(0)), (uses != null ? uses[0] : getUse(0)));
    }

    public String toString(SymbolTable symbolTable) {
	return getValueString(symbolTable, getDef(0)) + " = regionIterHasNext(" + getValueString(symbolTable, getUse(0)) + ")";
    }

    public void visit(IVisitor v) {
	((AstX10InstructionVisitor) v).visitRegionIterHasNext(this);
    }

    public Collection getExceptionTypes() {
	return Collections.EMPTY_SET;
    }
}
