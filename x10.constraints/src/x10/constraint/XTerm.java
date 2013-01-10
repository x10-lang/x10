package x10.constraint;


/**
 * XTerms are used to represent the terms occurring in constraints. They
 * are the building blocks of Constraints. This interface is the root of
 * other XTerm interfaces. All XTerms contain type information represented 
 * by the type parameter T. Note that the type associated with the XTerm cannot
 * itself have constraints, and must be a base type. 
 *  
 * @author lshadare
 *
 * @param <T>
 */
public interface XTerm<T extends XType> {
	/**
	 * Returns the base type of this term. 
	 * @return base type of term.
	 */
	T type();
	
	/**
	 * Returns a new XTerm that is identical with the current XTerm
	 * but has all occurrences of t2 replaced by t1. The resulting 
	 * XTerm will have no occurrences of t2. 
	 *  
	 * @param t1 term to substitute with 
	 * @param t2 term to substitute out 
	 * @return
	 */
	XTerm<T> subst(XConstraintSystem<T> sys, XTerm<T> t1, XTerm<T> t2);
	
	/**
	 * Checks whether the XTerm contains the given variable.
	 * @param var
	 * @return
	 */
	boolean hasVar(XVar<T> var);
	
    public interface TermVisitor<U extends XType> {
        /**
         * Visit the term tree.
         * @param term
         * @return  A term if normal traversal is to stop, <code>null</code> if it
         * is to continue.
         */
        XTerm<U> visit(XTerm<U> term);
    }
    
    /**
     * Given a visitor, we traverse the entire term (which is like a tree).
     * @param visitor
     * @return If the visitor didn't return any new child, then we return "this"
     *  (otherwise we create a clone with the new children)
     */
    public XTerm<T> accept(TermVisitor<T> visitor) ;
	/**
	 * Return true if this is the term is a projection i.e. a field or method access
	 * 
	 * @return
	 */
	boolean isProjection(); 
	/**
	 * Check whether the term represents an equality. 
	 * @return true if equality
	 */
	boolean isEquals(); 
	/**
	 * Check whether the term represents a disequality. 
	 * @return true if disequality
	 */
	boolean isDisEquals();
	/**
	 * Check whether the term represents a conjunction 
	 * @return true if conjunction
	 */
	boolean isAnd(); 
	/**
	 * Check whether the term represents boolean negation
	 * @return true if not
	 */
	boolean isNot(); 
	/**
	 * Return true if the term can appear nested inside a constraint
	 * @return
	 */
	boolean okAsNestedTerm();
	/**
	 * Construct a copy of the given XTerm. 
	 * @return
	 */
	XTerm<T> copy();

	/** Check if we are a literal and the literal value is v.
	 */
	boolean isLiteralValue(Object v);

	/** Is this term a boolean literal (i.e. true or false).
	 */
	boolean isBooleanLit();

	/** Is this term a literal.
	 */
	boolean isLit();

}
	