package x10.constraint.redesign.xsmt;

import x10.constraint.redesign.XConstraintSystem;
import x10.constraint.redesign.XDef;
import x10.constraint.redesign.XEQV;
import x10.constraint.redesign.XExpr;
import x10.constraint.redesign.XLit;
import x10.constraint.redesign.XLocal;
import x10.constraint.redesign.XOp;
import x10.constraint.redesign.XTerm;
import x10.constraint.redesign.XUQV;

public class XSmtConstraintSystem<T extends XSmtType> implements XConstraintSystem<T> {

	@Override
	public XLit<T, java.lang.Boolean> xtrue() {
		return new XSmtLit<T, Boolean>(Boolean(), true);
	}

	@Override
	public XLit<T, java.lang.Boolean> xfalse() {
		return new XSmtLit<T, Boolean>(Boolean(), false);
	}

	@Override
	public XLit<T, Object> xnull() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <V> XLit<T, V> makeLit(T type, V v) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public XEQV<T> makeEQV(T type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public XUQV<T> makeUQV(T type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public XUQV<T> makeUQV(T type, String prefix) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <D extends XDef<T>> XLocal<T, D> makeLocal(T type, D def) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public XExpr<T> makeExpr(XOp<T> op, XTerm<T>... terms) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public XTerm<T> makeEquals(XTerm<T> left, XTerm<T> right) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public XTerm<T> makeAnd(XTerm<T> left, XTerm<T> right) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public XTerm<T> makeNot(XTerm<T> arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public XExpr<T> makeProjection(XTerm<T> receiver, XDef<T> label) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public XExpr<T> makeFakeProjection(XTerm<T> receiver, XDef<T> label) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T Boolean() {
		// TODO Auto-generated method stub
		return null;
	}
 
}
