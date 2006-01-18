/*
 * Created on Oct 25, 2005
 */
package com.ibm.domo.ast.x10.ssa;

import java.util.Collection;
import java.util.Collections;
import com.ibm.capa.impl.debug.Assertions;
import com.ibm.domo.ssa.SSAInstruction;
import com.ibm.domo.ssa.SymbolTable;
import com.ibm.domo.ssa.ValueDecorator;

public class SSAAtomicInstruction extends SSAInstruction {
    private final boolean isEnter;
    public SSAAtomicInstruction(boolean isEnter) {
      super();
      this.isEnter = isEnter;
    }
    public SSAInstruction copyForSSA(int[] defs, int[] uses) {
      return
        new SSAAtomicInstruction(isEnter);
    }

    public String toString(SymbolTable symbolTable, ValueDecorator d) {
      return isEnter ? "atomic enter" : "atomic exit";
    }
    /**
     * @see com.ibm.domo.ssa.SSAInstruction#visit(Visitor)
     */
    public void visit(Visitor v) {
	((AstX10InstructionVisitor) v).visitAtomic(this);
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

    public boolean isAtomicEnter() {
      return isEnter;
    }
}
