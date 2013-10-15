/**
 * 
 */
package x10.constraint.visitors;

import x10.constraint.XAnd;
import x10.constraint.XEquals;
import x10.constraint.XNot;
import x10.constraint.XTerm;
import x10.constraint.XVar;

/**
 * @author Louis Mandel
 *
 */
public class SearchValueVisitor extends XGraphVisitor {

	private final XVar x;
	private XTerm result = null;

	public XTerm result() {
		return this.result;
	}

	/**
	 * @param hideEQV
	 * @param hideFake
	 */
	public SearchValueVisitor(boolean hideEQV, boolean hideFake, XVar x) {
		super(hideEQV, hideFake);
		this.x = x;
	}

	@Override
	protected boolean visitAtomicFormula(XTerm t) {
		if ( !t.hasVar(x) ) { return true; }
		return this.searchValueInTerm(t);
	}

	@Override
	protected boolean visitEquals(XTerm t1, XTerm t2) {
		if ( t1.hasVar(x) || t2.hasVar(x) ) {
			return this.unif(t1, t2);
		}
		return true;
	}

	@Override
	protected boolean visitDisEquals(XTerm t1, XTerm t2) {
		if ( t1.hasVar(x) || t2.hasVar(x) ) {
			// TODO there is maybe some thing to do here
			return true;
		}
		return true;
	}

	/**
	 * Search the value of the variable this.x in t. If it finds a value it store
	 * this value in this.result and return false. Otherwise, it returns true.
	 *
	 * @param t
	 * @return
	 */
	public boolean searchValueInTerm(XTerm t) {
		if (t instanceof XAnd) {
			XAnd and = (XAnd) t;
			return searchValueInTerm(and.left()) && searchValueInTerm(and.right());
		}
		else if (t instanceof XEquals) {
			XEquals eq = (XEquals) t;
			return unif(eq.left(), eq.right());
		}
		return true;
	}


	private boolean unif(XTerm t1, XTerm t2) {
		if ( is_x(t1) ) {
			if (is_x(t2) ) {
				// no information: x == x
				return true;
			}
			if ( t2.hasVar(x) ) { 
				// occur check: raise an error ?
				return true;
			}
			result = t2;
			return false;
		}
		if ( is_x(t2) ) {
			if (is_x(t1) ) {
				// no information: x == x
				return true;
			}
			if ( t1.hasVar(x) ) { 
				// occur check: raise an error ?
				return true;
			}
			result = t1;
			return false;
		}
		if ( t1 instanceof XNot && t2 instanceof XNot) {
			XNot n1 = (XNot) t1;
			XNot n2 = (XNot) t2;
			return unif(n1.unaryArg(), n2.unaryArg());
		}
		return true;
	}

	private boolean is_x(XTerm t) {
		if ( t instanceof XVar ) {
			XVar y = (XVar) t;
			return x.equals(y);
		}
		return false;
	}

}
