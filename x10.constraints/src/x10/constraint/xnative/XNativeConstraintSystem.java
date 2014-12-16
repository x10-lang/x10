package x10.constraint.xnative;



import java.util.List;

import x10.constraint.XConstraint;
import x10.constraint.XConstraintSystem;
import x10.constraint.XEQV;
import x10.constraint.XField;
import x10.constraint.XFormula;
import x10.constraint.XLocal;
import x10.constraint.XTerm;
import x10.constraint.XUQV;
import x10.constraint.XVar;


/**
 * Factory class producing XNativeTerms to be used with XNativeConstraints. 
 * @author lshadare
 *
 */
public class XNativeConstraintSystem implements XConstraintSystem {
	@Override
	public XConstraint makeConstraint() {
		return new XNativeConstraint();
	}
	@Override
	public XConstraint makeTrueConstraint() {
		return new XNativeConstraint(); 
	}
	
	private static final XNativeLit NULL = new XNativeLit(null);
	private static final XNativeLit TRUE = new XNativeLit(true);
	private static final XNativeLit FALSE = new XNativeLit(false);
    public static boolean isBoolean(XTerm x) { return x==TRUE || x==FALSE; } // because we intern
	
    // used in generating a new name or variable
	static int nextId = 0;
	@Override
	public XNativeLit xtrue() {return TRUE;} 
	@Override
	public XNativeLit xfalse() {return FALSE;}
	@Override
	public XNativeLit xnull() {return NULL; }
	
	/**
	 * Make a fresh EQV with a system chosen name. 
	 * @return
	 */
	@Override
	public XEQV makeEQV() {return new XNativeEQV(nextId++);}
	/**
	 * Make a fresh UQV with a system chosen name. 
	 * @return
	 */
	@Override
	public XUQV makeUQV() {return new XNativeUQV(nextId++);}

	/**
	 * Make a fresh UQV whose name starts with prefix.
	 * @param prefix -- a prefix of the name for the returned UQV
	 * @return
	 */
	@Override
	public XUQV makeUQV(String prefix) {return new XNativeUQV(prefix, nextId++);}

	/**
	 * Make and return <code>receiver.field</code>.
	 * @param receiver
	 * @param field
	 * @return
	 */
	@Override
	public <T> XField<T> makeField(XVar receiver, T field) {
		return new XNativeField<T>((XNativeVar)receiver, field);
	}
	@Override
	public XField<Object> makeFakeField(XVar receiver, Object field) {
		return new XNativeField<Object>((XNativeVar)receiver, field, true);
	}
	
    /** Make and return a literal containing o. null, true and false are
     * interned.
     */
	@Override
	public XNativeLit makeLit(Object o) {
		if (o == null) return NULL;
		if (o.equals(true)) return TRUE;
		if (o.equals(false)) return FALSE;
		return new XNativeLit(o);
	}
	
	/**
    Make and return op(terms1,..., termsn) -- an atomic formula
    with operator op and terms terms. Uses varargs.
	 */
	@Override
	public XFormula<Object> makeAtom(Object op, XTerm... terms) {
		return makeAtom(op, true, terms);
	}

    /**
       Make and return left == right.
     */
	@Override
	public XTerm makeEquals(XTerm left, XTerm right) {
		assert left != null;
		assert right != null;
		if (left instanceof XNativeLit && right instanceof XNativeLit) {
		        return(left.equals(right))? xtrue(): xfalse();
		}
		return new XEquals((XNativeTerm)left, (XNativeTerm)right);
	}
	
	/**
	 * Make and return left != right.
	 * @param left
	 * @param right
	 * @return
	 */
	@Override
	public XTerm makeDisEquals(XTerm left, XTerm right) {
		assert left != null;
		assert right != null;
		if (left instanceof XNativeLit && right instanceof XNativeLit) {
		    return (left.equals(right))?xfalse():xtrue();
		}
		return new XDisEquals((XNativeTerm)left, (XNativeTerm)right);
	}

    /**
       Make and return left,right -- the logical conjunction.
       left and right should be boolean terms. (not checked.)
     */
	@Override
	public XTerm makeAnd(XTerm left, XTerm right) {
		assert left != null;
		assert right != null;
		return new XAnd((XNativeTerm)left, (XNativeTerm)right);
	}
	@Override
	public XTerm makeNot(XTerm arg) {
		assert arg != null;
		return new XNot((XNativeTerm)arg);
	}

	/**
	 * Return the constraint true.
	 * @return
	 */
	//public static XConstraint makeTrueConstraint() {return new XNativeConstraint();}
	
	//*************************************** Implementation
	/**
    Make and return op(terms1,..., termsn) -- an expression 
    with operator op and arguments terms. If atomicFormula is true
    then this is marked as an atomicFormula, else it is considered a term 
    (a function application term).
	 */

	public XFormula<Object> makeAtom(Object op, boolean isAtomicFormula, XTerm... terms) {
		assert op != null;
		assert terms != null;
		XNativeFormula<Object> f = new XNativeFormula<Object>(op, op, isAtomicFormula, terms);
		return f;
	}
	
	public <T> XLocal<T> makeLocal(T name) {
		return new XNativeLocal<T>(name);
	}
}
