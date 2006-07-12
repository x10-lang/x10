package com.ibm.domo.ast.x10.ssa;

import java.util.Collection;
import java.util.Collections;
import com.ibm.domo.ssa.SSAAbstractUnaryInstruction;
import com.ibm.domo.ssa.SSAInstruction;
import com.ibm.domo.ssa.SymbolTable;
import com.ibm.domo.ssa.ValueDecorator;

public class SSARegionIterInitInstruction extends SSAAbstractUnaryInstruction {

    public SSARegionIterInitInstruction(int iterVal, int regionVal) {
      super(iterVal, regionVal);
    }

    public SSAInstruction copyForSSA(int[] defs, int[] uses) {
	return new SSARegionIterInitInstruction((defs != null ? defs[0] : getDef(0)), (uses != null ? uses[0] : getUse(0)));
    }

    public String toString(SymbolTable symbolTable, ValueDecorator d) {
	return getValueString(symbolTable, d, getDef(0)) + " = regionIter(" + getValueString(symbolTable, d, getUse(0)) + ")";
    }

    public void visit(IVisitor v) {
	((AstX10InstructionVisitor) v).visitRegionIterInit(this);
    }

    public Collection getExceptionTypes() {
	return Collections.EMPTY_SET;
    }
}
