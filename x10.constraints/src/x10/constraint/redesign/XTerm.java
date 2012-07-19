package x10.constraint.redesign;
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
	 * XTerm will have no occurances of t1. 
	 *  
	 * @param t1 term to substitute out 
	 * @param t2 term to substitute with 
	 * @return
	 */
	XTerm<T> subst(XTerm<T> t1, XTerm<T> t2); 
	
	String toString();
	
	boolean equals(Object o);
	
	int hashCode();
}
