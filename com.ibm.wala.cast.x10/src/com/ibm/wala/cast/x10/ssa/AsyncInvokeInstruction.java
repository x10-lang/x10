package com.ibm.wala.cast.x10.ssa;

import com.ibm.wala.cast.ir.ssa.AstLexicalAccess.Access;
import com.ibm.wala.cast.java.ssa.AstJavaInvokeInstruction;
import com.ibm.wala.classLoader.CallSiteReference;
import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.ssa.SymbolTable;

public class AsyncInvokeInstruction extends AstJavaInvokeInstruction {
    private int placeExpr;

    public AsyncInvokeInstruction(int result, int[] params, int exception, CallSiteReference site, int placeExpr) {
	super(result, params, exception, site);
	this.placeExpr= placeExpr;
    }

    public AsyncInvokeInstruction(int[] params, int exception, CallSiteReference site, int placeExpr) {
	super(params, exception, site);
	this.placeExpr= placeExpr;
    }

    protected AsyncInvokeInstruction(int[] results, int[] params, int exception, Access[] lexicalReads, Access[] lexicalWrites, CallSiteReference csr) {
	super(results, params, exception, csr, lexicalReads, lexicalWrites);
    }

    public int getPlaceExpr() {
        return placeExpr;
    }

    @Override
    public SSAInstruction copyForSSA(int[] defs, int[] uses) {
	// Use super to copy all lexical uses/defs; there should be one slot left
	// at the end for our place expr.
	AsyncInvokeInstruction copy = (AsyncInvokeInstruction) super.copyForSSA(defs, uses);
	if (uses != null) {
	    copy.placeExpr = uses[super.getNumberOfUses()];
	} else {
	    copy.placeExpr = placeExpr;
	}
	return copy;
	
    }

    @Override
    public int getUse(int j) {
	if (j < super.getNumberOfUses())
	    return super.getUse(j);
	return placeExpr;
    }

    @Override
    public int getNumberOfUses() {
        return super.getNumberOfUses() + 1;
    }

    @Override
  protected SSAInstruction copyInstruction(int results[], int[] params, int exception, Access[] lexicalReads, Access[] lexicalWrites) {
      return new AsyncInvokeInstruction(results, params, exception, lexicalReads, lexicalWrites, getCallSite());
    }

    @Override
    public String toString(SymbolTable symbolTable) {
	String sts = super.toString(symbolTable);

	return sts + "(place " + getValueString(symbolTable, placeExpr) + ")";
    }
}
