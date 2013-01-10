package x10.constraint;


/** Holds code common to SMT and Native term classes.
 *
 * @author dcunnin
 *
 * @param <T>
 */
public abstract class XCommonTerm<T extends XType> implements XTerm<T> {
	
	private final T type;

	public XCommonTerm (T type) {
		assert type != null;
		this.type = type;
	}
	
	@Override
	public T type() {
		return type;
	}

	@Override
	public abstract XTerm<T> subst(XConstraintSystem<T> sys, XTerm<T> t1, XTerm<T> t2);

	@Override
	public abstract boolean hasVar(XVar<T> var);

	@Override
	public abstract boolean isProjection();


    /**
     * Given a visitor, we traverse the entire term (which is like a tree).
     * @param visitor
     * @return If the visitor didn't return any new child, then we return "this"
     *  (otherwise we create a clone with the new children)
     */
	@Override
	public XTerm<T> accept(XTerm.TermVisitor<T> visitor) {
        // The default implementation for "leave" terms (that do not have any children)
        XTerm<T> res = visitor.visit(this);
        if (res!=null) return res;
        return this;
	}

	@Override
	public boolean isNot() {
		if (this instanceof XExpr) {
			@SuppressWarnings("unchecked")
			XExpr<T> exp = (XExpr<T>) this; 
			if (exp.op().getKind().equals(XOp.Kind.NOT))
				return true; 
		}
		return false; 
	} 
	
	@Override
	public boolean isEquals() {
		if (this instanceof XExpr) {
			@SuppressWarnings("unchecked")
			XExpr<T> exp = (XExpr<T>) this; 
			if (exp.op().getKind().equals(XOp.Kind.EQ))
				return true; 
		}
		return false; 
	}

	@Override
	public boolean isDisEquals() {
		if (this instanceof XExpr) {
			@SuppressWarnings("unchecked")
			XExpr<T> exp = (XExpr<T>) this; 
			if (exp.op().getKind().equals(XOp.Kind.NEQ))
				return true; 
		}
		return false; 
	}    

	@Override
	public boolean isAnd() {
		if (this instanceof XExpr) {
			@SuppressWarnings("unchecked")
			XExpr<T> exp = (XExpr<T>) this; 
			if (exp.op().getKind().equals(XOp.Kind.AND))
				return true; 
		}
		return false; 
	} 

	@Override
	public boolean isLit() {
		return this instanceof XLit<?,?>;
	}

	@Override
    public boolean isBooleanLit() {
    	return isLit() && type().isBoolean();
    }

	@Override
	public boolean isLiteralValue(Object v) {
		if (!(this instanceof XLit<?,?>)) return false;
		@SuppressWarnings("unchecked")
		XLit<T,Object> lit_this = (XLit<T,Object>) this; 
		return lit_this.val().equals(v);
	}
	
}
