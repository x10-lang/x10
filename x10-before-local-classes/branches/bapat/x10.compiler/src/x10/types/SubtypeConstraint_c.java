package x10.types;

import java.util.ArrayList;
import java.util.List;

import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.InternalCompilerError;
import x10.constraint.XConstraint;
import x10.constraint.XFailure;
import x10.constraint.XRoot;
import x10.constraint.XTerm;

public class SubtypeConstraint_c implements SubtypeConstraint {
    Type subtype;
    Type supertype;
    boolean equals;

    SubtypeConstraint_c(Type subtype, Type supertype, boolean equals) {
        this.subtype = subtype;
        this.supertype = supertype;
        this.equals = equals;
    }

    /*
     * (non-Javadoc)
     * 
     * @see x10.types.SubtypeConstraint#isEqualityConstraint()
     */
    public boolean isEqualityConstraint() {
        return equals;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see x10.types.SubtypeConstraint#subtype()
     */
    public Type subtype() {
        return subtype;
    }

    /*
     * (non-Javadoc)
     * 
     * @see x10.types.SubtypeConstraint#supertype()
     */
    public Type supertype() {
        return supertype;
    }

    public SubtypeConstraint_c copy() {
        try {
            return (SubtypeConstraint_c) super.clone();
        }
        catch (CloneNotSupportedException e) {
            assert false;
            return this;
        }
    }

    public static Type subst(Type t, XTerm y, XRoot x) {
        try {
            return Subst.subst(t, y, x);
        }
        catch (SemanticException e) {
            throw new InternalCompilerError(e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see x10.types.TypeConstraint#subst(x10.constraint.XTerm,
     * x10.constraint.XRoot, boolean)
     */
    public SubtypeConstraint subst(XTerm y, XRoot x) {
        List<XTerm> newArgs = new ArrayList<XTerm>();
        boolean changed = false;

        Type left = subtype();
        Type l = subst(left, y, x);
        Type right = supertype();
        Type r = subst(right, y, x);

        if (l == left && r == right)
            return this;

        SubtypeConstraint_c n = copy();
        n.subtype = l;
        n.supertype = r;
        return n;
    }

    @Override
    public String toString() {
        return subtype() + (isEqualityConstraint() ? " == " : " <: ") + supertype();
    }

}
