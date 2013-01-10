package x10.types.constraints.xnative.visitors;

import polyglot.types.Type;
import x10.constraint.XFailure;
import x10.constraint.XTerm;
import x10.constraint.XTypeSystem;
import x10.constraint.XVar;
import x10.constraint.xnative.XNativeConstraintSystem;
import x10.constraint.xnative.visitors.XGraphVisitor;
import x10.types.constraints.CConstraint;

public class AddInVisitor extends XGraphVisitor<Type> {
    final CConstraint c2;
    final XTerm<Type> newSelf;
    final XVar<Type> cSelf;
    public AddInVisitor(XNativeConstraintSystem<Type> sys, XTypeSystem<Type> ts, boolean hideEQV, boolean hideFake, CConstraint c2, XTerm<Type> newSelf, XVar<Type> cSelf) {
    	super(sys, ts, hideEQV, hideFake);
        this.c2=c2;
        this.newSelf = newSelf;
        this.cSelf = cSelf;
    }
    public boolean visitAtomicFormula(XTerm<Type> t) {
        try {
            t = t.subst(sys, newSelf, cSelf);
            c2.addTerm(t);
            return true;
        } catch (XFailure z) {
            c2.setInconsistent();
            return false;
        }
    }
    public boolean visitEquals(XTerm<Type> t1, XTerm<Type> t2) {
        t1 = t1.subst(sys, newSelf, cSelf);
        t2 = t2.subst(sys, newSelf, cSelf);
        c2.addEquality(t1, t2);
        return c2.consistent();     
    }
    public boolean visitDisEquals(XTerm<Type> t1, XTerm<Type> t2) {
        t1 = t1.subst(sys, newSelf, cSelf);
        t2 = t2.subst(sys, newSelf, cSelf);
        c2.addDisEquality(t1, t2);
        return c2.consistent();
    }
}
