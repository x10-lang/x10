/*
 * Created on Oct 25, 2005
 */
package com.ibm.wala.cast.x10.ssa;

import java.util.Collection;
import java.util.Collections;
import com.ibm.wala.util.debug.Assertions;
import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.ssa.SSAInstructionFactory;
import com.ibm.wala.ssa.SymbolTable;

public class SSAAtStmtInstruction extends SSAInstruction {
  public int line;
  public int column;
  public SSAAtStmtInstruction(final boolean isEnter, int l, int c) {
	this.line = l;
	this.column = c;
    this.fIsEnter = isEnter;
  }
  
  // --- SSAInstruction's abstract methods implementation

  public SSAInstruction copyForSSA(final SSAInstructionFactory instrFactory, final int[] defs, final int[] uses) {
    return ((X10InstructionFactory) instrFactory).AtStmt(this.fIsEnter,this.line,this.column);
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
