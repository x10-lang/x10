
package x10.refactorings;

import polyglot.ast.Expr;
import polyglot.types.VarInstance;

import com.ibm.wala.ipa.callgraph.propagation.PointerKey;

/**
 * A class that allows for association of a WALA pointer key and a Polyglot variable instance
 * with the expression from which the variable originates.
 * 
 * @author sm053
 *
 */
class VarWithFirstUse {
	private VarInstance fVarInstance;

	private Expr fFirstUseExpr;

	private PointerKey fPtrKeyOfFirstUse;

	/**
	 * Constructs a VarWithFirstUse object given all of its components.
	 * 
	 * @param vi the variable instance
	 * @param firstUse the expression where the variable is first used
	 * @param ptrKeyOfFirstUse the pointer key initially associated with the instance
	 */
	public VarWithFirstUse(VarInstance vi, Expr firstUse,
			PointerKey ptrKeyOfFirstUse) {
		fVarInstance = vi;
		fFirstUseExpr = firstUse;
		fPtrKeyOfFirstUse = ptrKeyOfFirstUse;
	}

	/**
	 * Gets the first use expression.
	 * 
	 * @return an expression
	 */
	public Expr getFirstUse() {
		return fFirstUseExpr;
	}

	/**
	 * Gets the variable instance.
	 * 
	 * @return the variable instance
	 */
	public VarInstance getVarInstance() {
		return fVarInstance;
	}

	/**
	 * Gets the WALA pointer key.
	 * 
	 * @return a pointer key
	 */
	public PointerKey getPtrKeyOfFirstUse() {
		return fPtrKeyOfFirstUse;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o) {
		if (o == null || !(o instanceof VarWithFirstUse)) {
			return false;
		} else {
			VarWithFirstUse vo = (VarWithFirstUse) o;
			if (vo.getVarInstance() == null) {
				return (this.fVarInstance == null) 
					&& vo.getFirstUse().equals(this.fFirstUseExpr);
			} else {
				return vo.getVarInstance().equals(this.fVarInstance);
			}
		}
	}
	
	/**
	 * Creates a useful debugging string for a VarWithFirstUse
	 * 
	 * @return a debug string representation
	 */
	public String debug() {
		return "{{"+fVarInstance+","+fFirstUseExpr+","+fPtrKeyOfFirstUse+"}}";
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return ExtractAsyncRefactoring.debugOutput?debug():fFirstUseExpr.toString();
	}
}