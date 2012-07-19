package x10.constraint.redesign;

public interface XConstraintSystem<T extends XType> {
	
	public XLit<T, Boolean> xtrue(); 
	public XLit<T, Boolean> xfalse(); 
	public XLit<T, Object> xnull(); 
	
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
	public <D extends XDef<T>> XLocal<T, D> makeLocal(T type, D def);
	/**
	 * Make an expression with the operation op and children terms
	 * @param op
	 * @param terms
	 * @return
	 */
	public XExpr<T> makeExpr(XOp<T> op, XTerm<T>... terms);

	public XTerm<T> makeEquals(XTerm<T> left, XTerm<T> right);
	public XTerm<T> makeAnd(XTerm<T> left, XTerm<T> right);
	public XTerm<T> makeNot(XTerm<T> arg);

	public XExpr<T> makeProjection(XTerm<T> receiver, XDef<T> label);
	public XExpr<T> makeFakeProjection(XTerm<T> receiver, XDef<T> label);
	/**
	 * Returns the Boolean type. 
	 * @return
	 */
	public T Boolean(); 
}
