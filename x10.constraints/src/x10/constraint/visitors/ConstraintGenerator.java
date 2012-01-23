package x10.constraint.visitors;

import java.util.ArrayList;
import java.util.List;

import x10.constraint.XTerm;
import x10.constraint.XTerms;

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
        result.add( XTerms.makeEquals(t1, t2));
        return true;
    }
    public boolean visitDisEquals(XTerm t1, XTerm t2) {
        result.add(XTerms.makeDisEquals(t1, t2));
        return true;
    }
    public List<XTerm> result() {return result;}
}