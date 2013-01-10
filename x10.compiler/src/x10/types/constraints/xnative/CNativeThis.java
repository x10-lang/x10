package x10.types.constraints.xnative;


import polyglot.ast.Typed;
import polyglot.types.Type;
import x10.constraint.XTerm;
import x10.constraint.XVar;
import x10.constraint.xnative.XNativeTerm;
import x10.constraint.xnative.XNativeUQV;
import x10.types.constraints.CThis;


/**
 * An optimized representation of this variables.
 * Keeps an int index and a type. Equality involves only
 * int equality.
 * 
 * <p>Code is essentially a copy of CSelf. Do not want to
 * combine, for that will mean extra state needs to be kept
 * with each object.
 * @author vj
 *
 */
public class CNativeThis extends XNativeUQV<Type> implements CThis, Typed {
    private static final long serialVersionUID = -2033423584924662939L;
    public static final String THIS_VAR_PREFIX="this";
    
    public CNativeThis(int n, Type type) {
    	super(type, n);
    }

    //@Override public String toString() {
    //	return THIS_VAR_PREFIX + (type() != null ? "(:" + type() + ")" : "");
    //}
    
}
