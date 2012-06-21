package x10.types.constraints.xnative;


import java.util.Collections;
import java.util.List;

import x10.constraint.XEQV;
import x10.constraint.XTerm;
import x10.constraint.XVar;
import x10.constraint.xnative.XRoot;
import x10.types.constraints.CSelf;

/**
 * An optimized representation of self variables.
 * Keeps only an int index. Equality involves only
 * int equality.
 * @author vj
 *
 */
public class CNativeSelf extends XRoot implements CSelf {
    private static final long serialVersionUID = 7709199402803815649L;
    public static final String SELF_VAR_PREFIX="self";
    
    public final int num;
    public CNativeSelf(int n) {this.num=n;}
    @Override
    public int hashCode() {return num;}
    @Override
    public boolean okAsNestedTerm() {return true;}
    @Override
    public boolean hasVar(XVar v) {return equals(v);}
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof CNativeSelf) return num == ((CNativeSelf) o).num;
        return false;
    }
    @Override
    public String toString() {return SELF_VAR_PREFIX;}
}
