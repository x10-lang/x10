package x10.constraint.xsmt;

import x10.constraint.XConstraint;
import x10.constraint.XConstraintSystem;
import x10.constraint.XEQV;
import x10.constraint.XField;
import x10.constraint.XFormula;
import x10.constraint.XLit;
import x10.constraint.XLocal;
import x10.constraint.XTerm;
import x10.constraint.XUQV;
import x10.constraint.XVar;



public class XSmtConstraintSystem implements XConstraintSystem {

	public XConstraint makeConstraint() {
		// TODO Auto-generated method stub
		return null;
	}

	public XConstraint makeTrueConstraint() {
		// TODO Auto-generated method stub
		return null;
	}

	public XLit xtrue() {
		// TODO Auto-generated method stub
		return null;
	}

	public XLit xfalse() {
		// TODO Auto-generated method stub
		return null;
	}

	public XLit xnull() {
		// TODO Auto-generated method stub
		return null;
	}

	public XEQV makeEQV() {
		// TODO Auto-generated method stub
		return null;
	}

	public XUQV makeUQV() {
		// TODO Auto-generated method stub
		return null;
	}

	public XUQV makeUQV(String prefix) {
		// TODO Auto-generated method stub
		return null;
	}

	public <T> XField<T> makeField(XVar receiver, T field) {
		// TODO Auto-generated method stub
		return null;
	}

	public XField<Object> makeFakeField(XVar receiver, Object field) {
		// TODO Auto-generated method stub
		return null;
	}

	public XLit makeLit(Object o) {
		// TODO Auto-generated method stub
		return null;
	}

	public XFormula<Object> makeAtom(Object op, boolean isAtomicFormula,
			XTerm... terms) {
		// TODO Auto-generated method stub
		return null;
	}

	public XTerm makeEquals(XTerm left, XTerm right) {
		// TODO Auto-generated method stub
		return null;
	}

	public XTerm makeDisEquals(XTerm left, XTerm right) {
		// TODO Auto-generated method stub
		return null;
	}

	public XTerm makeAnd(XTerm left, XTerm right) {
		// TODO Auto-generated method stub
		return null;
	}

	public XTerm makeNot(XTerm arg) {
		// TODO Auto-generated method stub
		return null;
	}

	public XFormula<Object> makeAtom(Object op, XTerm... terms) {
		// TODO Auto-generated method stub
		return null;
	}

	public <T> XLocal<T> makeLocal(T name) {
		// TODO Auto-generated method stub
		return null;
	}

	public XTerm makeOpaque(Object op, XTerm... terms) {
		// TODO Auto-generated method stub
		return null;
	}

}
