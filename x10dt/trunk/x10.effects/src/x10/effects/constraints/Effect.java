package x10.effects.constraints;

/**
 * The implement of effect annotations.
 * 
 * <p>An effect annotation is either @fun(r,w,a) or @parfun(r,w,a), where r, w, and a are sets 
 * of (mutable) locations.
 * 
 * <p> We use an XTerm to represent a location. 
 * 
 * <p> Two distinct Effects do not share any mutable state except for XTerms that they may 
 * have in common.
 * <p> Effects are created using the Factory.makeEffect(b) method. XTerms should then be added
 * to the read, write and atomicInc sets of the effect.
 * 
 * <p> Effects are composed using the methods: 
 * <ul>
 * <li> followedBy(Effect) (for sequencing and conditionals),
 * <li> exists(XVar) (for local variable declarations), 
 * <li> forall(XVar) (for for loops),
 * <li> makeParFun() (for async), and 
 * <li> makeFun() (for finish), and clone().
 * </ul>
 * @author vj 05/13/09
 *
 */
import java.util.*;

import x10.constraint.*;

public interface Effect extends Cloneable {
	/**
	 * An effect set is associated with either an @fun annotation or an @parfun annotation. 
	 * 
	 * @return true if this effect is associated with an @fun, false if it is associated with @parfun.
	 */
	boolean isFun();
	
	
	Set<Locs> readSet();
	Set<Locs> writeSet();
	Set<Locs> atomicIncSet();
	
	/**
	 * Same as commutesWith(e, XTerms.makeTrueConstraint())
	 * @param e
	 * @return
	 */
	boolean commutesWith(Effect e);
	
	/**
	 * Does this commute with e, given constraints c? c may provide information useful to establish
	 * commutativity (or lack thereof). For instance, c may specify that x1 != x2, and this can be used
	 * to determine a(x1) != a(x2), for an array a. Conversely, c may establish x1==x2. and 
	 * this would establish a(x1)== a(x2).
	 * @param e
	 * @param c
	 * @return
	 */
	boolean commutesWith(Effect e, XConstraint c);
	
	/**
	 * Same as commutesWith(e, XTerms.makeTrueConstraint()).
	 * @param x
	 * @return
	 */
	boolean commutesWithForall(XLocal x);
	
	/**
	 * Given constraint c, does this commute with a copy of itself under the assumption that x varies? This is
	 * implemented by replacing x in this with x1 to get e1, and x in this with x1 to get e2, where x1 and x2
	 * are fresh variables, and then checking x1.commutesWith(x2, c.addNotEquals(x1, x2)).
	 * @param x
	 * @param e
	 * @return
	 */
	boolean commutesWithForall(XLocal x, XConstraint c);
	
    /**
     * Like commutesWith(XLocal), but quantifies over a set of variables.
     * @return
     */
    public boolean commutesWithForall(List<XLocal> xs);

    /**
     * Like commutesWith(XLocal, XConstraint), but quantifies over a set of variables.
     * @return
     */
    public boolean commutesWithForall(List<XLocal> xs, XConstraint c);

	/**
	 * Return the Effect obtained by quantifying the current effect for all values of x. Essentially, 
	 * all locations a(f) in read/write/atomic sets which are such that x occurs in f are replaced by
	 * a. (We can do better than that, depending on f.) 
	 * @param x
	 * @return
	 */
	Effect forall(XLocal x);
	
	/**
	 * 
	 * Here is how this should be used. If m is the effect for statement S, 
	 * then m.exists(x,t) is the effect for val x=t; S.
	 * 
	 * <p> Return the Effect obtained by eliminating the final variable x from this. 
	 * Essentially, all Locs in the various sets associated with this whose
	 * underlying term s contains the term underlying x is replaced by a new
	 * Loc whose underlying term is s [ t / x ]. 
	
	 * @param x
	 * @return
	 */
	Effect exists(XLocal x, XTerm t);
	
	/**
	 * Here is how this method should be used. If m is the effect for statement S (and the 
	 * evaluation of the term t), then m.exists(x) is the effect for var x = t; S.
	 * 
	 * <p>Return the Effect obtained by eliminating the variable x from this. 
	 * Should be called to propagate the current effect through var x = ....
	 * (As of 05/18/09 implementation assumes that only rigid terms 
	 * can be used in effect sets, hence the only term in the effect sets
	 * that can be affected is x itself. So this is implemented simply by 
	 * deleting x from all the effect sets.)
	 * @param x
	 * @return
	 */
	Effect exists(LocalLocs x);
	
	/**
	 * Given constraint c, return the effect this; e, using the rules for sequential composition of effect annotations.
	 * Note that there may be no  such annotation -- e.g. an @parfun(read(r)) ; @fun(write(r)) is not
	 * defined .. it does not correspond to a safe parallelization because of the concurrent read/write to the
	 * same location. 
	 * Note that information in constraint c may be used to determine whether this; e is well-defined. 
	 * For instance @parfun(read(a(r))) ; @fun(write(a(s))) is not well-defined unless c establishes r !=s.
	 * @param e
	 * @return this; e
	 * @throws NoSuchEffect -- when this; e is not defined.
	 */
	Effect followedBy(Effect e, XConstraint c) throws XFailure;
	
	/**
	 * If this is @parfun(r,w,a) or @fun(r,w,a), return @parfun(r,w,a)
	 * @return
	 */
	Effect makeParFun();
	
	/**
	 * If this is @parfun(r,w,a) or @fun(r,w,a), return @fun(r,w,a)
	 * @return
	 */
	Effect makeFun();
	
	/**
	 * Add t to the read set for this. Modified in place.
	 * @param t
	 */
	void addRead(Locs t);
	/**
	 * Add t to the read set for this. Modified in place.
	 * @param t
	 */
	void addWrite(Locs t);
	/**
	 * Add t to the read set for this. Modified in place.
	 * @param t
	 */
	void addAtomicInc(Locs t);
	
	Effect substitute(XTerm t, XVar r);
	

}
