package x10.types.constraints;


import java.util.Collections;
import java.util.List;

import polyglot.ast.Typed;
import polyglot.types.Type;

import x10.constraint.XEQV;
import x10.constraint.XRoot;
import x10.constraint.XTerm;
import x10.constraint.XTermKind;
import x10.constraint.XVar;


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
public class CThis extends XRoot implements Typed {
    private static final long serialVersionUID = -2033423584924662939L;
    
    public final int num;
    //CHECK: The outer qualifier information is now carried in an
    // enclosing QualifiedVar.
    public final Type type;
 
    public CThis(int n, Type type) {
        this.num = n;
        this.type = type;
    }
    
    @Override
    public int hashCode() {
        return num;
    }
    
    /**
     * Return the type of this.
     * Note: A Qualified this, A.this, is represented as a CThis (this) wrapped inside
     * a QualifiedVar (with qualifier A). A.this's type is the type of the QualifiedVar,
     * namely, A.
     */
    public Type type() {
        return type;
    }
    @Override
    public XTerm subst(XTerm y, XVar x, boolean propagate) {
        XTerm r = super.subst(y, x, propagate);
            return r;
    }
    public boolean okAsNestedTerm() {
    	return true;
    }
    public boolean hasVar(XVar v) {
        return equals(v);
    }
    
  
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o instanceof CThis) {
            return num == ((CThis) o).num;
        }
        return false;
    }

    @Override
    public String toString() {
        return CTerms.THIS_VAR_PREFIX + (type != null ? "(:" + type + ")" : "");
    }
}
