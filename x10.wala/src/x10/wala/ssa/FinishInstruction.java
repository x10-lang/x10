/*
 * Created on Oct 25, 2005
 */
package x10.wala.ssa;

import java.util.Collection;
import java.util.Collections;
import com.ibm.wala.util.debug.Assertions;
import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.ssa.SSAInstructionFactory;
import com.ibm.wala.ssa.SymbolTable;

public class FinishInstruction extends SSAInstruction {
    private final boolean isEnter;
    public FinishInstruction(boolean isEnter) {
      super();
      this.isEnter = isEnter;
    }
    public SSAInstruction copyForSSA(SSAInstructionFactory insts, int[] defs, int[] uses) {
      return ((AstX10InstructionFactory)insts).Finish(isEnter);
    }

    public String toString(SymbolTable symbolTable) {
      return isEnter ? "finish enter" : "finish exit";
    }

    /**
     * @see com.ibm.domo.ssa.SSAInstruction#visit(IVisitor)
     */
    public void visit(IVisitor v) {
	((AstX10InstructionVisitor) v).visitFinish(this);
    }
    /**
     * @see com.ibm.domo.ssa.SSAInstruction#getNumberOfUses()
     */
    public int getNumberOfUses() {
      return 0;
    }

    /**
     * @see com.ibm.domo.ssa.SSAInstruction#getUse(int)
     */
    public int getUse(int j) {
	Assertions.UNREACHABLE();
	return -1;
    }

    public int hashCode() {
      return (isEnter ? 6173 ^ 4423 : 13);
    }
    /* (non-Javadoc)
     * @see com.ibm.domo.ssa.Instruction#isPEI()
     */
    public boolean isPEI() {
      return false;
    }
    /* (non-Javadoc)
     * @see com.ibm.domo.ssa.Instruction#isFallThrough()
     */
    public boolean isFallThrough() {
      return true;
    }
    /* (non-Javadoc)
     * @see com.ibm.domo.ssa.Instruction#getExceptionTypes()
     */
    public Collection getExceptionTypes() {
	return Collections.EMPTY_SET;
    }

    public boolean isFinishEnter() {
      return isEnter;
    }
}
