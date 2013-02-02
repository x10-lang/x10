/**
 * 
 */
package x10.constraint.xnative;

import x10.constraint.XType;
import x10.constraint.XUQV;
import x10.constraint.XVar;

/**
 * An efficient representation of a universally
 * quantified variable that is permitted to occur in multiple constraints.
 * 
 * Such a variable may optionally have a string name.
 * @author vj
 * @see XNativeEQV
 *
 */
public class XNativeUQV<T extends XType> extends XNativeVar<T> implements XUQV<T> {
    private static final long serialVersionUID = 6919751399957740949L;
    public final int num;
    
    public XNativeUQV(String str, T type, int num) {
    	super(type, str == null ? "uqv#" + num: str);
        this.num = num;
    }
    public XNativeUQV(T type, int num) {
    	this(null,type,num);
    }
    public XNativeUQV(XNativeUQV<T> other) {
    	super(other.type(), other.name());
    	this.num = other.num;
    }
    
    @Override public int hashCode() {return num;}
    
    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof XNativeUQV<?>) return num == ((XNativeUQV<?>) o).num;
        return false;
    }

    @Override
    public boolean hasVar(XVar<T> v) {return equals(v);}

    @Override
	public XNativeUQV<T> copy() {
    	return new XNativeUQV<T>(this);
	}
	
}
