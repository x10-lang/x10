package x10.constraint.xnative;



import x10.constraint.XConstraint;
import x10.constraint.XConstraintSystem;
import x10.constraint.XEQV;
import x10.constraint.XField;
import x10.constraint.XFormula;
import x10.constraint.XLocal;
import x10.constraint.XTerm;
import x10.constraint.XUQV;
import x10.constraint.XVar;



public class XNativeConstraintSystem implements XConstraintSystem {

	public XConstraint mkConstraint() {
		return new XNativeConstraint();
	}
	
	public XConstraint makeTrueConstraint() {
		return new XNativeConstraint(); 
	}
	
	private static final XNativeLit ZERO_FLOAT = new XNativeLit(new Float(0.0f));
	private static final XNativeLit ZERO_DOUBLE = new XNativeLit(new Double(0.0));
	private static final XNativeLit ZERO_INT = new XNativeLit(0);
	private static final XNativeLit ZERO_LONG = new XNativeLit(new Long(0));
	private static final XNativeLit ZERO_CHAR = new XNativeLit(Character.valueOf('\0'));
	private static final XNativeLit NULL = new XNativeLit(null);
	private static final XNativeLit TRUE = new XNativeLit(true);
	private static final XNativeLit FALSE = new XNativeLit(false);
    public static boolean isBoolean(XTerm x) { return x==TRUE || x==FALSE; } // because we intern
	
    // used in generating a new name or variable
	static int nextId = 0;
	
	public XNativeLit xtrue() {return TRUE;} 
	public XNativeLit xfalse() {return FALSE;}
	public XNativeLit zeroFloat() {return ZERO_FLOAT; }
	public XNativeLit zeroDouble() {return ZERO_DOUBLE; }
	public XNativeLit zeroInt() {return ZERO_INT; }
	public XNativeLit zeroLong() {return ZERO_LONG; }
	public XNativeLit zeroChar() {return ZERO_CHAR; }
	public XNativeLit xnull() {return NULL; }
	
	/**
	 * Make a fresh EQV with a system chosen name. 
	 * @return
	 */
	public XEQV makeEQV() {return new XNativeEQV(nextId++);}
	/**
	 * Make a fresh UQV with a system chosen name. 
	 * @return
	 */
	public XUQV makeUQV() {return new XNativeUQV(nextId++);}

	/**
	 * Make a fresh UQV whose name starts with prefix.
	 * @param prefix -- a prefix of the name for the returned UQV
	 * @return
	 */
	public XUQV makeUQV(String prefix) {return new XNativeUQV(prefix, nextId++);}

	/**
	 * Make and return <code>receiver.field</code>.
	 * @param receiver
	 * @param field
	 * @return
	 */
	public <T> XField<T> makeField(XVar receiver, T field) {
		return new XNativeField<T>((XNativeVar)receiver, field);
	}
	public XField<Object> makeFakeField(XVar receiver, Object field) {
		return new XNativeField<Object>((XNativeVar)receiver, field, true);
	}
	
    /** Make and return a literal containing o. null, true and false are
     * interned.
     */
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
	public XFormula<Object> makeAtom(Object op, XTerm... terms) {
		return makeAtom(op, true, terms);
	}

    /**
       Make and return left == right.
     */
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
	public XTerm makeAnd(XTerm left, XTerm right) {
		assert left != null;
		assert right != null;
		return new XAnd((XNativeTerm)left, (XNativeTerm)right);
	}
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
