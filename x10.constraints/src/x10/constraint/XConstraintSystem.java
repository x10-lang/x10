package x10.constraint;

import java.util.List;

public interface XConstraintSystem<T extends XType> {
	
	public XLit<T, Boolean> xtrue(XTypeSystem<T> ts); 
	public XLit<T, Boolean> xfalse(XTypeSystem<T> ts); 
	public XLit<T, Object>  xnull(XTypeSystem<T> ts); 
	
	/**
	 * Make a new literal containing value v. 
	 * @param v
	 * @return
	 */
	public <V> XLit<T, V> makeLit(T type, V v);
	
	/**
	 * Make a variable with the given type and name
	 * @param type
	 * @param name
	 * @return
	 */
	public XVar<T> makeVar(T type, String name);
	
	/**
	 * Make a fresh EQV with a system chosen name. 
	 * @return
	 */
	public XEQV<T> makeEQV(T type);
	
	/**
	 * Make a fresh UQV with a system chosen name. 
	 * @return
	 */
	public XUQV<T> makeUQV(T type);

	/**
	 * Make a fresh UQV whose name starts with prefix.
	 * @param prefix -- a prefix of the name for the returned UQV
	 * @return
	 */
	public XUQV<T> makeUQV(T type, String prefix);
	
	/**
	 * Make a local variable with its associated definition
	 * @param def the definition of the variable
	 * @return
	 */
	public <D extends XDef<T>> XLocal<T, D> makeLocal(D def);
	
	/**
	 * Make a projection operation i.e. field/method dereference a.f
	 * @param receiver the container
	 * @param label the field 
	 * @return
	 */
	public XExpr<T> makeProjection(XTerm<T> receiver, XDef<T> label);
	/**
	 * Make a projection operation i.e. field/method dereference a.f
	 * that is hidden (visible only to the compiler such as the here
	 * field)
	 * @param receiver the container
	 * @param label the field 
	 * @return
	 */
	public XExpr<T> makeFakeProjection(XTerm<T> receiver, XDef<T> label);
	/**
	 * Make an expression with the operation op and children terms
	 * @param op
	 * @param terms
	 * @return
	 */
	XExpr<T> makeExpr(XOp<T> op, List<? extends XTerm<T>> terms);
	XExpr<T> makeExpr(XOp<T> op, XTerm<T> t1, XTerm<T> t2);
	XExpr<T> makeExpr(XOp<T> op, XTerm<T> t);

	// convenience methods for some useful XExpr
	public XExpr<T> makeEquals(XTerm<T> left, XTerm<T> right);
	public XExpr<T> makeDisEquals(XTerm<T> left, XTerm<T> right);
	public XExpr<T> makeAnd(XTerm<T> left, XTerm<T> right);
	public XExpr<T> makeNot(XTerm<T> arg);
	/**
	 * Copy factory method that creates a fresh copy of an XTerm. 
	 * @param term
	 * @return
	 */
	public <U extends XTerm<T>> U copy(U term);
	
	/**
	 * Construct an empty XConstraint. 
	 * @return
	 */
	public XConstraint<T> makeConstraint(); 

}
