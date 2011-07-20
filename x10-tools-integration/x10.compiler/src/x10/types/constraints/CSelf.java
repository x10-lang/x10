package x10.types.constraints;


import java.util.Collections;
import java.util.List;

import x10.constraint.XEQV;
import x10.constraint.XRoot;
import x10.constraint.XTerm;
import x10.constraint.XTermKind;
import x10.constraint.XVar;


/**
 * An optimized representation of self variables.
 * Keeps only an int index. Equality involves only
 * int equality.
 * @author vj
 *
 */
public class CSelf extends XRoot {
    private static final long serialVersionUID = 7709199402803815649L;
    
    public final int num;
    
    public CSelf(int n) {
        this.num=n;
    }
    
    public int hashCode() {
        return num;
    }

    public boolean okAsNestedTerm() {
    	return true;
    }
    public boolean hasVar(XVar v) {
        return equals(v);
    }
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o instanceof CSelf) {
            return num == ((CSelf) o).num;
        }
        return false;
    }

    public String toString() {
        return CTerms.SELF_VAR_PREFIX;// + num;
    }
}
