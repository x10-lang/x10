package x10.types.constraints.xnative;


import polyglot.types.Type;
import x10.constraint.xnative.XNativeUQV;
import x10.types.constraints.CSelf;

/**
 * An optimized representation of self variables.
 * Keeps only an int index. Equality involves only
 * int equality.
 * @author vj
 *
 */
public class CNativeSelf extends XNativeUQV<Type> implements CSelf {
    private static final long serialVersionUID = 7709199402803815649L;
    public static final String SELF_VAR_PREFIX="self";
    
    public CNativeSelf(Type t, int n) { super(t, n); }
    public CNativeSelf(CNativeSelf other) { super(other); }

    //@Override
    //public String toString() {return "self(:" + type() + ")";}
	
    /*
	public void setType(Type t) {
		this.type = t; 
	}
	*/
	
	@Override
	public CNativeSelf copy() {
		return new CNativeSelf(this);
	}

}
