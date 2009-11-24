package x10.effects.constraints;

import x10.constraint.XLocal;
import x10.constraint.XName;
import x10.constraint.XTerm;

/**
 * A Factory class for creating effects.
 * 
 * @author vj 05/13/09
 *
 */
public class Effects {


	/**
	 * The code walker will create effects for sequential leaf statements by calling 
	 * Factory.makeEffect(Factory.FUN),and will then add the XTerms picked up from the statement
	 * to the readSet, writeSet and atomicIncSet of the effect.
	 * 
	 * 
	 * @param isFun
	 * @return
	 */
	public static Effect makeEffect(Safety s) {
		return new Effect_c(s);
	}
	public static Effect makeSafe() {
		return makeEffect(Safety.SAFE);
	}
	public static Effect makeParSafe() {
		return makeEffect(Safety.PAR_SAFE);
	}
	public static Effect makeUnsafe() {
		return makeEffect(Safety.UNSAFE);
	}
	/**
	 * Return an Obj designated by x. The caller must ensure
	 * that x is a rigid term and its type is an object type.
	 * 
	 * @param x
	 * @return
	 */
	public static ObjLocs makeObj(XTerm x) {
		return new ObjLocs_c(x);
	}
	/**
	 * Return a Locs corresponding to the object designated by
	 * the term x. 
	 * <p> Caller must ensure x is a rigid designator, that is, all
	 * variables occurring in it must be final. Caller must
	 * ensure that the type of x is an Object (with mutable fields); in
	 * particular it must not be an array.
	 * @param x
	 * @return
	 */
	public static ObjLocs makeObjLocs(XTerm x) {
		return new ObjLocs_c(x);
	}
	
	/**
	 * Return a Locs corresponding to the array designated by
	 * the term x. 
	 * <p> Caller must ensure x is a rigid designator, that is, all
	 * variables occurring in it must be final. Caller must
	 * ensure that the type of x is an Array whose members are
	 * mutable; in particular it must not be an object.
	 * @param x
	 * @return
	 */
	public static ArrayLocs makeArrayLocs(XTerm x) {
		return new ArrayLocs_c(x);
	}
	/**
	 * Returns a Locs corresponding to the local variable
	 * designated by x. 
	 * 
	 * <p> Caller must ensure that x is mutable.
	 * @param x
	 * @return
	 */
	public static LocalLocs makeLocalLocs(XLocal x) {
		return new LocalLocs_c(x);
	}
	

	
	/**
	 * Returns a Locs corresponding to the local variable
	 * designated by x. 
	 * 
	 * <p> Caller must ensure that x is mutable.
	 * @param x
	 * @return
	 */
	public static FieldLocs makeFieldLocs(XTerm o, XName field) {
		return new FieldLocs_c(o, field);
	}
	
	public static ArrayElementLocs makeArrayElementLocs(XTerm a, XTerm t) {
		return new ArrayElementLocs_c(a, t);
	}


}
