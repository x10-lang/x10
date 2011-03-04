package x10.effects.constraints;

import x10.constraint.XConstraint;
import x10.constraint.XTerm;
import x10.constraint.XVar;
/**
 * Locs represents a set of mutable locations in the source code corresponding
 * to a single source expression -- 
 * <ol>
 * <li> a mutable local variable (singleton set),
 * <li> a mutable field of an object (singleton set), 
 * <li> an object (typically denoted by an immutable local variable) 
 *  -- stands for the set of all its mutable fields.
 *  <li> an array (typically denoted by an immutable local variable)
 *  -- stands for the set of all its elements.
 *  <li> an array element (singleton set)
 <ol>
 
 * @author vj
 *
 */
public interface Locs {
	
	/**
	 * A constraint may specify equality relations on XTerms used to 
	 * designate locations. Given such a constraint c, returns true
	 * iff other and this have no location in common.
	 * For instance, if the constraint specifies x==y, then
	 * locs(a(x)) and locs(a(y)) are not disjoint. If the constraint
	 * specifies x != y, then locs(a(x)) and locs(a(y)) are disjoint.
	 * 
	 * @param other
	 * @param constraint
	 * @return
	 */
	boolean disjointFrom(Locs other, XConstraint c);
	
	/**
	 * Return the term underlying this Locs.
	 */
	
	// XTerm term();
	/**
	 * Does the XTerm underlying this Locs contain t as a subterm.
	 * @param t
	 * @return
	 */
	//boolean hasSubterm(XTerm t);
	
	/**
	 * Let u be the term underlying this. Return a new Locs whose underlying term 
	 * is u[ t / s ], i.e. u with t replacing s.
	 * @param l
	 * @param t
	 * @return
	 */
	Locs substitute(XTerm t, XVar s);

}
