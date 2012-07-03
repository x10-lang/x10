package x10.constraint.xsmt;

import java.util.ArrayList;
import java.util.List;

import x10.constraint.XFormula;
import x10.constraint.XTerm;
import x10.constraint.XVar;
import x10.constraint.xsmt.SmtUtil.XSmtKind;

public class XSmtFormula<T> extends XSmtTerm implements XFormula<T>, SmtExpr {
	private static final long serialVersionUID = -1776737061201213218L;

	private final List<XSmtTerm> children; 
	private final T op; 
	
	public XSmtFormula(T op, List<XSmtTerm> children) {
		this.children = children; 
		this.op = op; 
	}
	
	@Override
	public XSmtTerm subst(XTerm y, XVar x) {
		List<XSmtTerm> new_children = new ArrayList<XSmtTerm>(children.size());
		for (XSmtTerm child : children) {
			new_children.add(child.subst(y,x)); 
		}
		return null;
	}

	@Override
	public boolean isAtomicFormula() {
		throw new UnsupportedOperationException("What is an atomic formula?"); 
	}

	@Override
	public boolean hasVar(XVar v) {
		for (XSmtTerm child: children)
			if (child.hasVar(v)) return true; 
		return false;
	}

	@Override
	public XSmtTerm accept(TermVisitor visitor) {
		XSmtTerm res = (XSmtTerm)visitor.visit(this);
		if (res != null)
			return res; 
		boolean changed = false; 
		List<XSmtTerm> new_children = new ArrayList<XSmtTerm>(children.size());
		for (XSmtTerm child : children) {
			XSmtTerm new_child = (XSmtTerm) visitor.visit(child); 
			new_children.add(new_child); 
			changed |= new_child != child; 
		}
		if (changed)
			return new XSmtFormula<T>(op, new_children);
		
		return this;
	}

	@Override
	public boolean hasEQV() {
		for (XSmtTerm child: children)
			if (child.hasEQV()) return true; 
		return false;
	}


	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(); 
		sb.append(op);
		for (XSmtTerm child : children)
			sb.append(" "+ child.toString());
		return sb.toString(); 
	}

	@Override
	public T operator() {
		return op; 
	}

	@Override
	public boolean isUnary() {
		return children.size() == 1; 
	}

	@Override
	public boolean isBinary() {
		return children.size() == 2;
	}

	@Override
	public XSmtTerm unaryArg() {
		return children.get(0); 
	}

	@Override
	public XSmtTerm left() {
		return children.get(0);
	}

	@Override
	public XTerm right() {
		return children.get(1);
	}

	@Override
	public XSmtTerm[] arguments() {
		return children.toArray(new XSmtTerm[0]);
	}

	@Override
	public T asExprOperator() {
		return op;
	}

	@Override
	public XSmtKind getKind() {
		return SmtUtil.toXSmtKind(op.toString());
	}

	@Override
	public SmtType getType() {
		return SmtUtil.toSmtType(op.toString());
	}

	@Override
	public int getNumChildren() {
		return children.size(); 
	}

	@Override
	public SmtTerm get(int i) {
		return children.get(i);
	}

	@Override
	public List<? extends SmtTerm> getChildren() {
		return children; 
	}

}
