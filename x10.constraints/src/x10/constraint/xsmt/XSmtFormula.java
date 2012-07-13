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
	public final T op; 
	private final boolean atomic;

	public XSmtFormula(T op, boolean atomic, XTerm... children) {
		this.children =  new ArrayList<XSmtTerm>(children.length);
		for (XTerm child : children) {
			this.children.add((XSmtTerm)child); 
		}
		this.op = op; 
		this.atomic = atomic; 
	}

	
	public XSmtFormula(T op, boolean atomic, List<XSmtTerm> children) {
		this.children = children; 
		this.op = op; 
		this.atomic = atomic; 
	}
	
	@Override
	public XSmtTerm subst(XTerm y, XVar x) {
		List<XSmtTerm> new_children = new ArrayList<XSmtTerm>(children.size());
		for (XSmtTerm child : children) {
			new_children.add(child.subst(y,x)); 
		}
		return new XSmtFormula<T>(op, atomic, new_children);
	}

	@Override
	public boolean isAtomicFormula() {
		return atomic; 
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
			return new XSmtFormula<T>(op, atomic, new_children);
		
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
		throw new UnsupportedOperationException("Only the classes that extend XSmtFormula have asExprOperator");
	}

	@Override
	public XSmtKind getKind() {
		return SmtUtil.toXSmtKind(op.toString());
	}

	@Override
	public SmtType getType() {
		if (atomic)
			return SmtType.BoolType();
		
		return SmtType.UnknownType(); 
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


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (atomic ? 1231 : 1237);
		result = prime * result
				+ ((children == null) ? 0 : children.hashCode());
		result = prime * result + ((op == null) ? 0 : op.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		XSmtFormula<?> other = (XSmtFormula<?>) obj;
		if (atomic != other.atomic)
			return false;
		if (children == null) {
			if (other.children != null)
				return false;
		} else if (!children.equals(other.children))
			return false;
		if (op == null) {
			if (other.op != null)
				return false;
		} else if (!op.equals(other.op))
			return false;
		return true;
	}


	@Override
	public String toSmt2() {
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		sb.append(SmtUtil.toSmt2(getKind()));
		for (SmtTerm child: children) {
			sb.append(" " + child.toSmt2());
		}
		sb.append(")"); 

		return sb.toString();
	}

}
