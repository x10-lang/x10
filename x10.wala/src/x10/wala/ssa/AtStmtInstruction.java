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

public class AtStmtInstruction extends SSAInstruction {
  
  public AtStmtInstruction(final boolean isEnter) {
    this.fIsEnter = isEnter;
  }
    
  // --- SSAInstruction's abstract methods implementation

  public SSAInstruction copyForSSA(final SSAInstructionFactory instrFactory, final int[] defs, final int[] uses) {
    return ((AstX10InstructionFactory) instrFactory).AtStmt(this.fIsEnter);
  }
  
  public int hashCode() {
    return this.fIsEnter ? 345345 : -56534;
  }
  
  public boolean isFallThrough() {
    return true;
  }

  public String toString(final SymbolTable symbolTable) {
    return this.fIsEnter ? "atStmt enter" : "atStmt exit";
  }

  public void visit(final IVisitor v) {
    ((AstX10InstructionVisitor) v).visitAtStmt(this);
  }
  
  // --- Overridden methods

  public int getNumberOfUses() {
    return 0;
  }

  public int getUse(final int j) {
    Assertions.UNREACHABLE();
    return -1;
  }

  public boolean isPEI() {
    return false;
  }

  public Collection getExceptionTypes() {
    return Collections.EMPTY_SET;
  }
  
  // --- Fields
  
  private final boolean fIsEnter;
  
}
