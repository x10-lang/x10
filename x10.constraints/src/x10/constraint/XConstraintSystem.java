package x10.constraint;

import java.util.List;

public interface XConstraintSystem<T extends XType> {
	
	public XLit<T, Boolean> xtrue(XTypeSystem<? extends T> ts); 
	public XLit<T, Boolean> xfalse(XTypeSystem<? extends T> ts); 
	public <U> XLit<T, U>  xnull(XTypeSystem<? extends T> ts); 
	
	/**
	 * Make a new literal containing value v with type type. 
	 * @param v
	 * @return
	 */
	//public <V> XLit<T, V> makeLit(T type, V v);
	public <V> XLit<T, V> makeLit(V v, T type);
	
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
	 * Make a local variable with its associated name and 
	 * definition
	 * @param def the definition of the variable
	 * @param name the name of the local variable
	 * @return
	 */
	public <D extends XDef<T>> XLocal<T, D> makeLocal(D def, String name);

	/**
     * Construct the XTerm corresponding to a method call. This will be represented by a 
     * field dereference followed by an function application. For example a.foo(x,y), will become 
     * (APPLY ((APPLY_LABEL foo) a) x y). 
     * @param md method definition
     * @param receiver 
     * @param t method arguments
     * @return 
     */
    public <D extends XDef<T>> XExpr<T> makeMethod(D md, XTerm<T> receiver, List<? extends XTerm<T>> t);
	
	
	/**
	 * Make a projection operation i.e. field/method dereference a.f
	 * @param receiver the container
	 * @param definition of the field/method
	 * @return
	 */
	public <D extends XDef<T>> XField<T, D> makeField(XTerm<T> receiver, D label);
	/**
	 * Make a projection operation i.e. field/method dereference a.f
	 * that is hidden (visible only to the compiler such as the here
	 * field)
	 * @param receiver the container
	 * @param definition of the field/method
	 * @return
	 */
	public <D extends XDef<T>> XField<T, D> makeFakeField(XTerm<T> receiver, D label);
	/**
	 * Make a projection operation i.e. field/method dereference when there is no 
	 * definition of the field.  
	 * @param receiver
	 * @param label field label
	 * @param type the type of the field dereference
	 * @return
	 */
	public <D> XField<T, D> makeField(XTerm<T> receiver, D label, T type);
	public <D> XField<T, D> makeFakeField(XTerm<T> receiver, D label, T type);
	
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
	public XTerm<T> makeAnd(List<? extends XTerm<T>> conjuncts);
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
