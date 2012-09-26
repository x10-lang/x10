/**
 * 
 */
package x10.constraint.xnative;

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
public class XNativeUQV extends XRoot implements XUQV {
    private static final long serialVersionUID = 6919751399957740949L;
    public final int num;
    public final String str;
    public XNativeUQV(int n) {this(null, n);}
    public XNativeUQV(String s, int n) {
        this.num=n;
        this.str=s;
    }
    
    @Override public int hashCode() {return num;}
    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof XNativeUQV) return num == ((XNativeUQV) o).num;
        return false;
    }

    @Override
    public boolean okAsNestedTerm() {return true;}
    @Override
    public boolean hasVar(XVar v) {return equals(v);}
    @Override public String toString() {return str == null ? "uqv#" + num: str;}
}
