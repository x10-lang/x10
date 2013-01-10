package x10.constraint.xnative.visitors;

import x10.constraint.XFailure;
import x10.constraint.XTerm;
import x10.constraint.XType;
import x10.constraint.XTypeSystem;
import x10.constraint.XVar;
import x10.constraint.xnative.XNativeConstraint;
import x10.constraint.xnative.XNativeConstraintSystem;


public class AddInVisitor<T extends XType> extends XGraphVisitor<T> {
    XNativeConstraint<T> c2;
    XTerm<T> newSelf;
    XVar<T> cSelf;
    public AddInVisitor(XNativeConstraintSystem<T> sys, XTypeSystem<T> ts, boolean hideEQV, boolean hideFake, XNativeConstraint<T> c2) {
    	super(sys, ts, hideEQV, hideFake);
        this.c2=c2;
    }
    public boolean visitAtomicFormula(XTerm<T> t) {
        try {
            c2.addTerm(t);
            return true;
        } catch (XFailure z) {
            c2.setInconsistent();
            return false;
        }
    }
    public boolean visitEquals(XTerm<T> t1, XTerm<T> t2) {
        c2.addEquality(t1, t2);
        return c2.consistent();     
    }
    public boolean visitDisEquals(XTerm<T> t1, XTerm<T> t2) {
        c2.addDisEquality(t1, t2);
        return c2.consistent();
    }
}