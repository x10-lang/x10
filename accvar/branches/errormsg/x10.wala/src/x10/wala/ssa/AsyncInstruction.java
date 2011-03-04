package x10.wala.ssa;

import com.ibm.wala.cast.ir.ssa.AstLexicalAccess.Access;
import com.ibm.wala.cast.java.ssa.AstJavaInvokeInstruction;
import com.ibm.wala.classLoader.CallSiteReference;
import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.ssa.SSAInstructionFactory;
import com.ibm.wala.ssa.SymbolTable;

public class AsyncInstruction extends AstJavaInvokeInstruction {
    private int clocks[];
    //FIXME: a hack to get whether placeExpr is HERE
    
    public AsyncInstruction(int result, int[] params, int exception, CallSiteReference site, int[] clocks) {
	super(result, params, exception, site);
	  this.clocks = clocks;
    }

    public AsyncInstruction(int[] params, int exception, CallSiteReference site, int[] clocks) {
	super(params, exception, site);
	  this.clocks = clocks;
    }

    public AsyncInstruction(int[] results, int[] params, int exception, Access[] lexicalReads, Access[] lexicalWrites, CallSiteReference csr) {
	super(results, params, exception, csr, lexicalReads, lexicalWrites);
    }

    public int[] getClocks() {
    	/*if (clocks != null)
    		for (int i = 0; i < clocks.length; i++)
    			System.out.println("Clock[" + i + "]" + clocks[i]);*/
    	return clocks;
    }
    
    @Override
    public SSAInstruction copyForSSA(SSAInstructionFactory insts, int[] defs, int[] uses) {
	// Use super to copy all lexical uses/defs; there should be one slot left
	// at the end for our place expr, and the rest are the clock expressions,
    // if any    		
	AsyncInstruction copy = (AsyncInstruction) super.copyForSSA(insts, defs, uses);

	if (clocks != null) {
		copy.clocks = new int[ clocks.length ];
		for(int i = 0; i < clocks.length; i++) {
			//copy.clocks[i] = (uses != null)? uses[super.getNumberOfUses() + i + 1]: clocks[i];
			copy.clocks[i] = (uses != null)? uses[uses.length-clocks.length+i]: clocks[i];
		}
	}

	/*if (uses != null)
	{
		for (int i = 0; i < uses.length; i++)
			System.out.println("Use [" + i + "]" + uses[i]);
	}
	if (clocks != null)
		for (int i = 0; i < clocks.length; i++)
			System.out.println("Clock[" + i + "]" + clocks[i]);
			
	*/
	return copy;
	
    }

    @Override
    public int getUse(int j) {
	if (j < super.getNumberOfUses())
	    return super.getUse(j);
	else
		return clocks[ j-super.getNumberOfUses() ];
    }

    @Override
    public int getNumberOfUses() {
        return super.getNumberOfUses() + ((clocks==null)? 0 : clocks.length);
    }

    @Override
  protected SSAInstruction copyInstruction(SSAInstructionFactory insts, int results[], int[] params, int exception, Access[] lexicalReads, Access[] lexicalWrites) {
      return ((AstX10InstructionFactory)insts).AsyncInvoke(results, params, exception, lexicalReads, lexicalWrites, getCallSite());
    }

    @Override
    public String toString(SymbolTable symbolTable) {
	  StringBuffer sts = new StringBuffer(super.toString(symbolTable));
	
	  if (clocks != null) {
		  sts.append(" (clocks");
		  for(int i = 0; i < clocks.length; i++) {
			  sts.append(" " + getValueString(symbolTable, clocks[i]));
		  }
		  sts.append(")");
	  }
    
      return sts.toString();
    }
}
