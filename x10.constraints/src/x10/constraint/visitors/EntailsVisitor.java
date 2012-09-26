package x10.constraint.visitors;

import x10.constraint.XConstraint;
import x10.constraint.XTerm;

public class EntailsVisitor extends XGraphVisitor{
    XConstraint c1;
    boolean result=true;
    public EntailsVisitor(boolean hideEQV, boolean hideFake, XConstraint c1) {
    	super(hideEQV, hideFake);
        this.c1=c1;
    }
    public boolean visitAtomicFormula(XTerm t) {
        result &= c1.entails(t);
        return result;
    }
    public boolean visitEquals(XTerm t1, XTerm t2) {
        result &= c1.entails(t1, t2);
        return result;
    }
    public boolean visitDisEquals(XTerm t1, XTerm t2) {
        result &= c1.disEntails(t1, t2);
        return result;
    }
    public boolean result() {return result;}
}