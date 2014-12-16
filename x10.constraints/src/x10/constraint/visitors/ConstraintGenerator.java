package x10.constraint.visitors;

import java.util.ArrayList;
import java.util.List;

import x10.constraint.XConstraintManager;
import x10.constraint.XTerm;

public  final class ConstraintGenerator extends XGraphVisitor {
    public List<XTerm> result = new ArrayList<XTerm>(5);
    public ConstraintGenerator(boolean hideEQV, boolean hideFake) {
    	super(hideEQV, hideFake);
    }
    public boolean visitAtomicFormula(XTerm t) {
        result.add(t);
        return true;
    }
    public boolean visitEquals(XTerm t1, XTerm t2) {
        result.add(XConstraintManager.getConstraintSystem().makeEquals(t1, t2));
        return true;
    }
    public boolean visitDisEquals(XTerm t1, XTerm t2) {
        result.add(XConstraintManager.getConstraintSystem().makeDisEquals(t1, t2));
        return true;
    }
    public List<XTerm> result() {return result;}
}