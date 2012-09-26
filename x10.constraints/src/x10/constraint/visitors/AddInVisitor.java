package x10.constraint.visitors;

import x10.constraint.XFailure;
import x10.constraint.XTerm;
import x10.constraint.XVar;
import x10.constraint.xnative.XNativeConstraint;


public class AddInVisitor extends XGraphVisitor {
    XNativeConstraint c2;
    XTerm newSelf;
    XVar cSelf;
    public AddInVisitor(boolean hideEQV, boolean hideFake, XNativeConstraint c2) {
    	super(hideEQV, hideFake);
        this.c2=c2;
    }
    public boolean visitAtomicFormula(XTerm t) {
        try {
            c2.addTerm(t);
            return true;
        } catch (XFailure z) {
            c2.setInconsistent();
            return false;
        }
    }
    public boolean visitEquals(XTerm t1, XTerm t2) {
        c2.addBinding(t1, t2);
        return c2.consistent();     
    }
    public boolean visitDisEquals(XTerm t1, XTerm t2) {
        c2.addDisBinding(t1, t2);
        return c2.consistent();
    }
}