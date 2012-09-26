package x10.types.constraints.visitors;

import x10.constraint.XFailure;
import x10.constraint.XTerm;
import x10.constraint.XVar;
import x10.constraint.visitors.XGraphVisitor;
import x10.types.constraints.CConstraint;

public class AddInVisitor extends XGraphVisitor {
    CConstraint c2;
    XTerm newSelf;
    XVar cSelf;
    public AddInVisitor(boolean hideEQV, boolean hideFake, CConstraint c2, XTerm newSelf, XVar cSelf) {
    	super(hideEQV, hideFake);
        this.c2=c2;
        this.newSelf = newSelf;
        this.cSelf = cSelf;
    }
    public boolean visitAtomicFormula(XTerm t) {
        try {
            t = t.subst(newSelf, cSelf);
            c2.addTerm(t);
            return true;
        } catch (XFailure z) {
            c2.setInconsistent();
            return false;
        }
    }
    public boolean visitEquals(XTerm t1, XTerm t2) {
        t1 = t1.subst(newSelf, cSelf);
        t2 = t2.subst(newSelf, cSelf);
        c2.addBinding(t1, t2);
        return c2.consistent();     
    }
    public boolean visitDisEquals(XTerm t1, XTerm t2) {
        t1 = t1.subst(newSelf, cSelf);
        t2 = t2.subst(newSelf, cSelf);
        c2.addDisBinding(t1, t2);
        return c2.consistent();
    }
}
