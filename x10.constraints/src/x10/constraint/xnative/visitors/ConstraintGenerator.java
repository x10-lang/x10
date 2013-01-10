package x10.constraint.xnative.visitors;

import java.util.ArrayList;
import java.util.List;

import x10.constraint.XTerm;
import x10.constraint.XType;
import x10.constraint.XTypeSystem;
import x10.constraint.xnative.XNativeConstraintSystem;
import x10.constraint.xnative.XNativeTerm;

public  final class ConstraintGenerator<T extends XType> extends XGraphVisitor<T> {
    public List<XNativeTerm<T>> result = new ArrayList<XNativeTerm<T>>(5);
    public ConstraintGenerator(XNativeConstraintSystem<T> sys, XTypeSystem<T> ts, boolean hideEQV, boolean hideFake) {
    	super(sys, ts, hideEQV, hideFake);
    }
    @Override
    public boolean visitAtomicFormula(XTerm<T> t) {
        result.add((XNativeTerm<T>)t);
        return true;
    }
    @Override
	public boolean visitEquals(XTerm<T> t1, XTerm<T> t2) {
        result.add(sys.makeEquals(ts, t1, t2));
        return true;
    }
    @Override
	public boolean visitDisEquals(XTerm<T> t1, XTerm<T> t2) {
        result.add(sys.makeDisEquals(ts, t1, t2));
        return true;
    }
    public List<XNativeTerm<T>> result() {return result;}
}