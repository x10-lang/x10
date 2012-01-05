/**
 * 
 */
package x10.constraint;

/**
 * An efficient representation of a universally
 * quantified variable that is permitted to occur in multiple constraints.
 * 
 * Such a variable may optionally have a string name.
 * @author vj
 * @see XEQV
 *
 */
public class XUQV extends XRoot {
    private static final long serialVersionUID = 6919751399957740949L;
    public final int num;
    public final String str;
    public XUQV(int n) {this(null, n);}
    public XUQV(String s, int n) {
        this.num=n;
        this.str=s;
    }
    
    @Override public int hashCode() {return num;}
    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof XUQV) return num == ((XUQV) o).num;
        return false;
    }

    public boolean okAsNestedTerm() {return true;}
    public boolean hasVar(XVar v) {return equals(v);}
    @Override public String toString() {return str == null ? "uqv#" + num: str;}
}
