package x10.constraint.smt;

import java.util.ArrayList;
import java.util.List;

import x10.constraint.XConstraint;
import x10.constraint.XConstraintSystem;
import x10.constraint.XDef;
import x10.constraint.XEQV;
import x10.constraint.XExpr;
import x10.constraint.XLit;
import x10.constraint.XLocal;
import x10.constraint.XOp;
import x10.constraint.XTerm;
import x10.constraint.XType;
import x10.constraint.XTypeSystem;
import x10.constraint.XUQV;
import x10.constraint.XVar;

public class XSmtConstraintSystem<T extends XType> implements XConstraintSystem<T> {
	private static int idCounter = 0; 
	
	@Override
	public XLit<T, java.lang.Boolean> xtrue(XTypeSystem<T> ts) {
		assert ts != null; 
		return new XSmtLit<T, Boolean>(ts.Boolean(), true);
	}

	@Override
	public XLit<T, java.lang.Boolean> xfalse(XTypeSystem<T> ts) {
		assert ts != null;
		return new XSmtLit<T, Boolean>(ts.Boolean(), false);
	}

	@Override
	public XLit<T, Object> xnull(XTypeSystem<T> ts) {
		assert ts != null;
		return new XSmtLit<T, Object>(ts.Null(), null);
	}

	@Override
	public <V> XLit<T, V> makeLit(T type, V val) {
		assert type != null && val!= null; 
		return new XSmtLit<T, V>(type, val);
	}

	@Override
	public XEQV<T> makeEQV(T type) {
		assert type != null;
		return new XSmtEQV<T>(type, idCounter++);
	}

	@Override
	public XUQV<T> makeUQV(T type) {
		assert type != null;
		return new XSmtUQV<T>(type, idCounter++);
	}

	@Override
	public XUQV<T> makeUQV(T type, String prefix) {
		assert type != null;
		return new XSmtUQV<T>(type, prefix, idCounter++);
	}

	@Override
	public <D extends XDef<T>> XLocal<T, D> makeLocal(D def) {
		assert def != null;
		return new XSmtLocal<T,D>(def);
	}

	@Override
	public XExpr<T> makeExpr(XOp<T> op, List<? extends XTerm<T>> terms) {
		List<XSmtTerm<T>> smt_terms = new ArrayList<XSmtTerm<T>>(terms.size()); 
		for (XTerm<T> t : terms) {
			assert t != null;
			smt_terms.add((XSmtTerm<T>)t); 
		}
		return new XSmtExpr<T>(op, false, smt_terms);
	}

	@Override
	public XExpr<T> makeExpr(XOp<T> op, XTerm<T> t1, XTerm<T> t2) {
		List<XTerm<T>> terms = new ArrayList<XTerm<T>>(2);
		terms.add(t1); 
		terms.add(t2);
		return makeExpr(op, terms);
	}

	@Override
	public XExpr<T> makeExpr(XOp<T> op, XTerm<T> t) {
		List<XTerm<T>> terms = new ArrayList<XTerm<T>>(2);
		terms.add(t); 
		return makeExpr(op, terms);
	}
	
	@Override
	public XExpr<T> makeEquals(XTerm<T> left, XTerm<T> right) {
		assert left!= null && right!= null; 
		return new XSmtExpr<T>(XOp.<T>EQ(), false, (XSmtTerm<T>)left, (XSmtTerm<T>)right);
	}

	@Override
	public XExpr<T> makeDisEquals(XTerm<T> left, XTerm<T> right) {
		assert left!= null && right!= null;
		return makeNot(makeEquals(left, right));
	}
	
	
	@Override
	public XExpr<T> makeAnd(XTerm<T> left, XTerm<T> right) {
		assert left!= null && right!= null; 
		return new XSmtExpr<T>(XOp.<T>AND(), false, (XSmtTerm<T>)left, (XSmtTerm<T>)right);
	}

	@Override
	public XExpr<T> makeNot(XTerm<T> arg) {
		assert arg!= null;
		return new XSmtExpr<T>(XOp.<T>NOT(), false, (XSmtTerm<T>)arg);
	}

	@Override
	public XExpr<T> makeProjection(XTerm<T> receiver, XDef<T> label) {
		assert receiver!= null && label!= null;
		XOp<T> op = XOp.APPLY(label);
		return new XSmtExpr<T>(op, false, (XSmtTerm<T>)receiver);
	}

	@Override
	public XExpr<T> makeFakeProjection(XTerm<T> receiver, XDef<T> label) {
		assert receiver!= null && label!= null;
		XOp<T> op = XOp.APPLY(label);
		return new XSmtExpr<T>(op, false, (XSmtTerm<T>)receiver);
	}

	@Override
	public XConstraint<T> makeConstraint() {
		return new XSmtConstraint<T>(); 
	}

	@Override
	public XVar<T> makeVar(T type, String name) {
		return new XSmtVar<T>(type, name);
	}

	@Override
	public <U extends XTerm<T>> U copy(U term) {
		return term.copy(); 
	}


}
