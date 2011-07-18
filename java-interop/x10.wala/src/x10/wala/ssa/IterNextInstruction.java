package x10.wala.ssa;

import java.util.Collection;
import java.util.Collections;
import com.ibm.wala.ssa.SSAAbstractUnaryInstruction;
import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.ssa.SSAInstructionFactory;
import com.ibm.wala.ssa.SymbolTable;

public class IterNextInstruction extends SSAAbstractUnaryInstruction {

    public IterNextInstruction(int nextValue, int regionIter) {
	super(nextValue, regionIter);
    }

    public SSAInstruction copyForSSA(SSAInstructionFactory insts, int[] defs, int[] uses) {
	return ((AstX10InstructionFactory)insts).IterNext((defs != null ? defs[0] : getDef(0)), (uses != null ? uses[0] : getUse(0)));
    }

    public String toString(SymbolTable symbolTable) {
	return getValueString(symbolTable, getDef(0)) + " = regionIterNext(" + getValueString(symbolTable, getUse(0)) + ")";
    }

    public void visit(IVisitor v) {
	((AstX10InstructionVisitor) v).visitIterNext(this);
    }

    public Collection getExceptionTypes() {
	return Collections.EMPTY_SET;
    }
}
