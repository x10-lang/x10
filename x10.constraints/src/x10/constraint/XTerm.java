package x10.constraint;

import java.util.List;

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
	 * but has all occurrences of t1 replaced by t2. The resulting 
	 * XTerm will have no occurrences of t1. 
	 *  
	 * @param t1 term to substitute out 
	 * @param t2 term to substitute with 
	 * @return
	 */
	XTerm<T> subst(XTerm<T> t1, XTerm<T> t2);
	
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
	
	String toString();
	
	boolean equals(Object o);
	
	int hashCode();
	/**
	 * Return true if it is either a field/method access, a lit or a variable. 
	 * FIXME: rename to something more normal
	 * @return
	 */
	boolean isProjection(); 
	
	boolean okAsNestedTerm();

	List<XTerm<T>> vars();
	
	<U extends XTerm<T>> U copy(); 
}
	