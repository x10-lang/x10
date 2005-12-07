package com.ibm.domo.ast.x10.ssa;

import com.ibm.capa.impl.debug.Assertions;
import com.ibm.domo.ssa.*;
import com.ibm.domo.types.TypeReference;

public class SSARegionIterHasNextInstruction extends SSAAbstractUnaryInstruction {

    public SSARegionIterHasNextInstruction(int hasNextValue, int regionIter) {
	super(hasNextValue, regionIter);
    }

    public SSAInstruction copyForSSA(int[] defs, int[] uses) {
	return new SSARegionIterHasNextInstruction((defs != null ? defs[0] : getDef(0)), (uses != null ? uses[0] : getUse(0)));
    }

    public String toString(SymbolTable symbolTable, ValueDecorator d) {
	return getValueString(symbolTable, d, getDef(0)) + " = regionIterHasNext(" + getValueString(symbolTable, d, getUse(0)) + ")";
    }

    public void visit(Visitor v) {
	((AstX10InstructionVisitor) v).visitRegionIterHasNext(this);
    }

    public TypeReference[] getExceptionTypes() {
	return null;
    }
}
