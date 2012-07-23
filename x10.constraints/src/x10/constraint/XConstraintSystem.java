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
	
	// convenience methods for some useful XExpr
	public XExpr<T> makeEquals(XTerm<T> left, XTerm<T> right);
	public XExpr<T> makeAnd(XTerm<T> left, XTerm<T> right);
	public XExpr<T> makeNot(XTerm<T> arg);
	
	/**
	 * Construct an empty XConstraint. 
	 * @return
	 */
	public XConstraint<T> makeConstraint(); 

}
