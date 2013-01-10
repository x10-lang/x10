package x10.constraint.xnative;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import x10.constraint.XConstraint;
import x10.constraint.XConstraintSystem;
import x10.constraint.XDef;
import x10.constraint.XExpr;
import x10.constraint.XLit;
import x10.constraint.XOp;
import x10.constraint.XTerm;
import x10.constraint.XType;
import x10.constraint.XTypeSystem;


/**
 * Factory class producing XNativeTerms to be used with XNativeConstraints. 
 * @author lshadare
 *
 */
public class XNativeConstraintSystem<T extends XType> implements XConstraintSystem<T> {


	@Override
	public XConstraint<T> makeConstraint(XTypeSystem<T> ts) {
		return new XNativeConstraint<T>(this, ts);
	}
		
    // used in generating a new name or variable
	protected static int nextId = 0;

	@Override
	public XNativeLit<T,Boolean> xtrue(XTypeSystem<? extends T> ts) {
		return new XNativeLit<T,Boolean>(true, ts.Boolean());
	}
	@Override
	public XNativeLit<T,Boolean> xfalse(XTypeSystem<? extends T> ts) {
		return new XNativeLit<T,Boolean>(false, ts.Boolean());
	}
	@Override
	public <U> XNativeLit<T,U> xnull(XTypeSystem<? extends T> ts) {
		return new XNativeLit<T,U>(null, ts.Null());
	}
	
	/**
	 * Make a fresh EQV with a system chosen name. 
	 * @return
	 */
	@Override
	public XNativeEQV<T> makeEQV(T t) {return new XNativeEQV<T>(t, nextId++);}

	/**
	 * Make a fresh UQV with a system chosen name. 
	 * @return
	 */
	@Override
	public XNativeUQV<T> makeUQV(T t) {return new XNativeUQV<T>(t, nextId++);}

	/**
	 * Make a fresh UQV whose name starts with prefix.
	 * @param prefix -- a prefix of the name for the returned UQV
	 * @return
	 */
	@Override
	public XNativeUQV<T> makeUQV(T t, String prefix) {return new XNativeUQV<T>(prefix, t, nextId++);}

	/**
	 * Make and return <code>receiver.field</code>.
	 * @param receiver
	 * @param field
	 * @return
	 */
	@Override
	public <D extends XDef<T>> XNativeField<T,D> makeField(XTerm<T> receiver, D field, boolean hidden) {
		return new XNativeField<T,D>((XNativeTerm<T>)receiver, field, hidden);
	}

	@Override
	public <D extends XDef<T>> XNativeField<T,D> makeField(XTerm<T> receiver, D field) {
		return makeField(receiver,field,false);
	}
	
    /** Make and return a literal containing o. null, true and false are
     * interned.
     */
	@Override
	public <V> XLit<T, V> makeLit(T type, V v) {
		return new XNativeLit<T,V>(v, type);
	}

	/**
       Make and return left == right.
     */
	@Override
    @SuppressWarnings("unchecked")
	public XNativeExpr<T> makeEquals(XTypeSystem<T> ts, XTerm<T> left, XTerm<T> right) {
		assert left != null;
		assert right != null;
		/* [DC] not sure if we need this optimisation
		if (left instanceof XNativeLit<T> && right instanceof XNativeLit<T>) {
		        return(left.equals(right))? xtrue(): xfalse();
		}
		*/
		return new XNativeExpr<T>(XOp.<T>EQ(ts.Boolean()), false, (XNativeTerm<T>)left, (XNativeTerm<T>)right);
	}
	
	/**
	 * Make and return left != right.
	 * @param left
	 * @param right
	 * @return
	 */
	@Override
    @SuppressWarnings("unchecked")
	public XNativeExpr<T> makeDisEquals(XTypeSystem<T> ts, XTerm<T> left, XTerm<T> right) {
		/* [DC] not sure if we need this optimisation
		if (left instanceof XNativeLit && right instanceof XNativeLit) {
		    return (left.equals(right))?xfalse():xtrue();
		}
		*/
		return new XNativeExpr<T>(XOp.<T>NEQ(ts.Boolean()), false, (XNativeTerm<T>)left, (XNativeTerm<T>)right);
	}

    /**
       Make and return left,right -- the logical conjunction.
       left and right should be boolean terms. (not checked.)
     */
	@Override
    @SuppressWarnings("unchecked")
	public XNativeExpr<T> makeAnd(XTypeSystem<T> ts, XTerm<T> left, XTerm<T> right) {
		assert left != null;
		assert right != null;
		return new XNativeExpr<T>(XOp.<T>AND(ts.Boolean()), false, (XNativeTerm<T>)left, (XNativeTerm<T>)right);
	}

	@Override
	public XNativeExpr<T> makeAnd(XTypeSystem<T> ts, List<? extends XTerm<T>> conjuncts) {
		List<XNativeTerm<T>> conjuncts2 = new ArrayList<XNativeTerm<T>>();
		for (XTerm<T> t : conjuncts) conjuncts2.add((XNativeTerm<T>)t);
		return new XNativeExpr<T>(XOp.<T>AND(ts.Boolean()), false, conjuncts2);
	}

	@Override
    @SuppressWarnings("unchecked")
	public XNativeExpr<T> makeNot(XTypeSystem<T> ts, XTerm<T> arg) {
		assert arg != null;
		return new XNativeExpr<T>(XOp.<T>NOT(ts.Boolean()), false, (XNativeTerm<T>)arg);
	}
	
	
	/**
    Make and return op(terms1,..., termsn) -- an expression 
    with operator op and arguments terms. If atomicFormula is true
    then this is marked as an atomicFormula, else it is considered a term 
    (a function application term).
	 */
	@Override
	public XNativeExpr<T> makeExpr(XOp<T> op, List<? extends XTerm<T>> terms) {
		assert op != null;
		assert terms != null;
		List<XNativeTerm<T>> terms2 = new ArrayList<XNativeTerm<T>>(terms.size());
		for (XTerm<T> t : terms) terms2.add((XNativeTerm<T>)t);
		boolean hidden = false;
		XNativeExpr<T> f = new XNativeExpr<T>(op, hidden, terms2);
		return f;
	}
	
	
//	@Override
//	public XVar<T> makeVar(T type, String name) {
//		return new XNativeLocal<T>(name, type);
//	}

	@SuppressWarnings("unchecked")
	@Override
	public XExpr<T> makeExpr(XOp<T> op, XTerm<T> t1, XTerm<T> t2) {
		return makeExpr(op,Arrays.<XTerm<T>>asList(t1,t2));
	}

	@SuppressWarnings("unchecked")
	@Override
	public XExpr<T> makeExpr(XOp<T> op, XTerm<T> t) {
		return makeExpr(op,Arrays.<XTerm<T>>asList(t));
	}

	
	/**
	 * Does this contain an existentially quantified variable?
	 * Default no; should be overridden by subclasses representing eqvs.
	 * @return true if it is, false if it isn't.
	 */
	public boolean hasEQV(XNativeTerm<T> term) {
		if (term instanceof XNativeEQV<?>) return true;
		
		if (term instanceof XNativeExpr<?>) {
			XNativeExpr<T> expr = (XNativeExpr<T>) term;
			for (XNativeTerm<T> ch : expr.children()) {
				if (hasEQV(ch)) return true;
			}
		}
		
		return false;
	}}
