package polyglot.ext.x10.types;

import java.util.ArrayList;
import java.util.List;

import x10.constraint.XRoot;
import x10.constraint.XTerm;

public class TypeConstraint_c implements TypeConstraint {

    List<SubtypeConstraint> terms;
    boolean consistent;

    public TypeConstraint_c() {
        terms = new ArrayList<SubtypeConstraint>();
        consistent = true;
    }
    
    public boolean entails(TypeConstraint c, X10Context xc) {
        X10TypeSystem xts = (X10TypeSystem) xc.typeSystem();
        for (SubtypeConstraint t : c.terms()) {
            if (t.isEqualityConstraint()) {
                if (!xts.typeEquals(t.subtype(), t.supertype(), xc)) {
                    return false;
                }
            }
            else {
                if (!xts.isSubtype(t.subtype(), t.supertype(), xc)) {
                    return false;
                }
            }
        }
        return true;
    }

    public List<SubtypeConstraint> terms() {
        return terms;
    }

    public TypeConstraint_c copy() {
        try {
            return (TypeConstraint_c) super.clone();
        }
        catch (CloneNotSupportedException e) {
            assert false;
            return this;
        }
    }
    
    public TypeConstraint addIn(TypeConstraint c) {
        terms.addAll(c.terms());
        return this;
    }
    
    public void addTerm(SubtypeConstraint c) {
        terms.add(c);
    }

    public void addTerms(List<SubtypeConstraint> terms) {
        this.terms.addAll(terms);
    }
    
    public boolean consistent(X10Context context) {
        if (consistent) {
            X10Context xc = (X10Context) context;
            X10TypeSystem ts = (X10TypeSystem) context.typeSystem();
            for (SubtypeConstraint t : terms()) {
                if (t.isEqualityConstraint()) {
                    if (! ts.typeEquals(t.subtype(), t.supertype(), xc)) {
                        consistent = false;
                        return false;
                    }
                }
                else {
                    if (! ts.isSubtype(t.subtype(), t.supertype(), xc)) {
                        consistent = false;
                        return false;
                    }
                }
            }
        }
        return consistent;
    }

    public void setInconsistent() {
        this.consistent = false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see polyglot.ext.x10.types.TypeConstraint#subst(x10.constraint.XTerm,
     * x10.constraint.XRoot, boolean)
     */
    public TypeConstraint subst(XTerm y, XRoot x) {
        TypeConstraint_c c = new TypeConstraint_c();
        List<SubtypeConstraint> l = c.terms;
        for (SubtypeConstraint s : terms) {
            l.add(s.subst(y, x));
        }
        return c;
    }

    @Override
    public String toString() {
        return terms.toString();
    }

    
}
