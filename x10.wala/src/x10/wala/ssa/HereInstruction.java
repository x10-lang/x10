package x10.wala.ssa;

import java.util.Collection;
import java.util.Collections;
import com.ibm.wala.util.debug.Assertions;
import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.ssa.SSAInstructionFactory;
import com.ibm.wala.ssa.SymbolTable;

public class HereInstruction extends SSAInstruction {
    private final int retValue;

    public HereInstruction(int retValue) {
	this.retValue= retValue;
    }

    public SSAInstruction copyForSSA(SSAInstructionFactory insts, int[] defs, int[] uses) {
	return ((AstX10InstructionFactory)insts).Here(defs != null ? defs[0] : retValue);
    }

    public String toString(SymbolTable symbolTable) {
	return getValueString(symbolTable, getDef()) + " = here()";
    }

    public void visit(IVisitor v) {
	((AstX10InstructionVisitor) v).visitHere(this);
    }

    public int hashCode() {
	return 413771901 * retValue;
    }

    public Collection getExceptionTypes() {
	return Collections.EMPTY_SET;
    }

    public boolean isFallThrough() {
	return true;
    }

    public int getNumberOfDefs() {
        return 1;
    }

    public int getDef() {
        return retValue;
    }

    public int getDef(int i) {
	Assertions.productionAssertion(i == 0);
        return retValue;
    }
}
