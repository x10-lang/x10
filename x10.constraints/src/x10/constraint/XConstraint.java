package x10.constraint;

import java.util.List;
import java.util.Set;
/**
 * Representation of a constraint associated to a type. Constraints are not immutable and
 * can be built incrementally by adding new terms (which must be of type Boolean). 
 * The constraint essentially represents the conjunction of all the terms added in 
 * the constraint. Note that the added terms can be either atoms, or have an arbitrary 
 * Boolean structure (i.e. there is no guarantee that the conjunctions are flatten).
 *  
 * @author lshadare
 *
 * @param <T> type of XTerms
 */
public interface XConstraint <T extends XType> {
	/**
	 * Check if the constraint is currently consistent. 
	 * @return true if consistent. 
	 */
    public boolean consistent(); 
    /**
     * Check if the constraint is valid.
     * @return true if valid. 
     */
    public boolean valid(); 
     /**
     * Strengthen the constraint by asserting the term t. The term t
     * should always have Boolean type.   
     * @param t
     * @throws XFailure
     */
    public void addTerm(XTerm<T> t) throws XFailure;

    /**
     * Strengthen the constraint by adding in the equality
     * left == right. 
     * @param left
     * @param right
     */
    public void addEquality(XTerm<T> left, XTerm<T> right);
    /**
     * Strengthen the constraint by adding in the disequality
     * left != right
     * @param left
     * @param right
     */
    public void addDisEquality(XTerm<T> left, XTerm<T> right);
    
    /**
     * Check if the current constraint entails the other constraint. 
     * @param other
     * @return true if the entailment holds
     */
    public boolean entails(XConstraint<T> other);
    /**
     * Check if the current constraint entails the term.
     * @param term
     * @return true if the entailment holds. 
     */
    public boolean entails(XTerm<T> term);
    /**
     * Check if the current constraint entails the disequality
     * left != right 
     * @param left
     * @param right
     * @return true if the disequality is entailed 
     */
    public boolean entailsDisEquality(XTerm<T> left, XTerm<T> right);
    /**
     * Check if the current constraint entails the equality
     * left != right 
     * @param left
     * @param right
     * @return true if the equality is entailed 
     */
    public boolean entailsEquality(XTerm<T>left, XTerm<T> right);
    
    /**
	 * Return the least upper bound of this and other. That is, the resulting 
	 * constraint has precisely the constraints entailed by both this and other.
	 * (Note: An inconsistent constraint entails every constraint, and 
	 * a valid constraint entails only those constraints such as x=x that 
	 * every constraint entails.)
	 * @param other
	 * @return
	 */
    public XConstraint<T> leastUpperBound(XConstraint<T> other);
    
     /**
     * Return those subset of constraints in the base set of other that are 
     * <em>not</em> implied by this. That is, return the residue
     * r such that (r and this) implies other.
     * @param other -- must be checked for consistency before call is made
     * @return
     */
    public XConstraint<T> residue(XConstraint<T> other);
    /**
     * Returns a list of XTerms representing the conjuncts in the constraint. 
     * This is not necessarily flattened. 
     * @return
     */
    public List<? extends XTerm<T>> terms();	
 
   	/**
	 * Collects all the XVar and XFields occurring in the current constraint. 
	 * @return
	 */
	public Set<? extends XTerm<T>> getVarsAndFields();

    /**
     * Create a copy of the current constraint. 
     * @return a copy of the current constraint
     */
	public XConstraint<T> copy();
	/**
	 * Return a term v is equal to in the constraint, and null if such
	 * a term could not be "easily" computed. 
	 * 
	 * @param v
	 * @return t such that this |- t = v
	 */
	public XTerm<T> bindingForVar(XTerm<T> v);
	/**
	 * Mark the constraint as being inconsistent. 
	 */
	public void setInconsistent();
	/**
	 * Return the externally visible terms i.e. a list of atoms in the current constraint ignoring
	 * those that have equalities involving EQV variables. 
     * @return
     */
	public List<? extends XTerm<T>> extTerms();
	/**
	 * Return the externally visible terms. i.e. a list of atoms in the current constraint ignoring
	 * those that have equalities involving EQV variables and fake fields. 
     * @return
     */
	public List<? extends XTerm<T>> extTermsHideFake();
	
	public String toString();
}
