package x10.constraint.xnative.visitors;

import x10.constraint.XConstraint;
import x10.constraint.XTerm;
import x10.constraint.XType;
import x10.constraint.XTypeSystem;
import x10.constraint.xnative.XNativeConstraintSystem;

public class EntailsVisitor<T extends XType> extends XGraphVisitor<T>{
    XConstraint<T> c1;
    boolean result=true;
    public EntailsVisitor(XNativeConstraintSystem<T> sys, XTypeSystem<T> ts, boolean hideEQV, boolean hideFake, XConstraint <T>c1) {
    	super(sys, ts, hideEQV, hideFake);
        this.c1=c1;
    }
    public boolean visitAtomicFormula(XTerm<T> t) {
        result &= c1.entails(t);
        return result;
    }
    public boolean visitEquals(XTerm<T> t1, XTerm<T> t2) {
        result &= c1.entailsEquality(t1, t2);
        return result;
    }
    public boolean visitDisEquals(XTerm<T> t1, XTerm<T> t2) {
        result &= c1.entailsDisEquality(t1, t2);
        return result;
    }
    public boolean result() {return result;}
}