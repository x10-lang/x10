package com.ibm.domo.ast.x10.ssa;

import com.ibm.capa.impl.debug.Assertions;
import com.ibm.domo.ssa.SSAInstruction;
import com.ibm.domo.ssa.SymbolTable;
import com.ibm.domo.ssa.ValueDecorator;
import com.ibm.domo.types.TypeReference;

public class SSARegionIterInitInstruction extends SSAAbstractUnaryInstruction {

    public SSARegionIterInitInstruction(int iterVal, int regionVal) {
      super(iterVal, regionVal);
    }

    public SSAInstruction copyForSSA(int[] defs, int[] uses) {
	return new SSARegionIterInitInstruction((defs != null ? defs[0] : getDef(0)), (uses != null ? uses[0] : getUse(0)));
    }

    public String toString(SymbolTable symbolTable, ValueDecorator d) {
	return getValueString(symbolTable, d, iterVal) + " = regionIter(" + getValueString(symbolTable, d, regionVal) + ")";
    }

    public void visit(Visitor v) {
	((AstX10InstructionVisitor) v).visitRegionIterInit(this);
    }

    public TypeReference[] getExceptionTypes() {
	return null;
    }
}
