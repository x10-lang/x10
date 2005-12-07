package com.ibm.domo.ast.x10.ssa;

import com.ibm.capa.impl.debug.Assertions;
import com.ibm.domo.ssa.SSAInstruction;
import com.ibm.domo.ssa.SymbolTable;
import com.ibm.domo.ssa.ValueDecorator;
import com.ibm.domo.types.TypeReference;

public class SSARegionIterNextInstruction extends SSAAbstractUnaryInstruction {

    public SSARegionIterNextInstruction(int nextValue, int regionIter) {
	super(nextValue, regionIter);
    }

    public SSAInstruction copyForSSA(int[] defs, int[] uses) {
	return new SSARegionIterNextInstruction((defs != null ? defs[0] : getDef(0)), (uses != null ? uses[0] : getUse(0)));
    }

    public String toString(SymbolTable symbolTable, ValueDecorator d) {
	return getValueString(symbolTable, d, nextValue) + " = regionIterNext(" + getValueString(symbolTable, d, regionIter) + ")";
    }

    public void visit(Visitor v) {
	((AstX10InstructionVisitor) v).visitRegionIterNext(this);
    }

    public TypeReference[] getExceptionTypes() {
	return null;
    }
}
