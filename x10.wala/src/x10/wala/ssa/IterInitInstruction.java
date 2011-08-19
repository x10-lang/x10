package x10.wala.ssa;

import java.util.Collection;
import java.util.Collections;
import com.ibm.wala.ssa.SSAAbstractUnaryInstruction;
import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.ssa.SSAInstructionFactory;
import com.ibm.wala.ssa.SymbolTable;

public class IterInitInstruction extends SSAAbstractUnaryInstruction {

    public IterInitInstruction(int iterVal, int regionVal) {
      super(iterVal, regionVal);
    }

    public SSAInstruction copyForSSA(SSAInstructionFactory insts, int[] defs, int[] uses) {
	return ((AstX10InstructionFactory)insts).IterInit((defs != null ? defs[0] : getDef(0)), (uses != null ? uses[0] : getUse(0)));
    }

    public String toString(SymbolTable symbolTable) {
	return getValueString(symbolTable, getDef(0)) + " = regionIter(" + getValueString(symbolTable, getUse(0)) + ")";
    }

    public void visit(IVisitor v) {
	((AstX10InstructionVisitor) v).visitIterInit(this);
    }

    public Collection getExceptionTypes() {
	return Collections.EMPTY_SET;
    }
}
