package x10.constraint.smt;

import java.util.ArrayList;
import java.util.List;

import x10.constraint.XExpr;
import x10.constraint.XOp;
import x10.constraint.XTerm;

public class XSmtExpr<T extends XSmtType> extends XSmtTerm<T> implements XExpr<T> {
	private final XOp<T> op; 
	private final List<XSmtTerm<T>> children; 
	private final boolean hidden; 
	
	public XSmtExpr(XOp<T> op, boolean hidden, List<XSmtTerm<T>> children) {
		super(op.type(children.get(0).type().<T>xTypeSystem()));
		assert children.size() > 0; 
		this.op = op; 
		this.children = children; 
		this.hidden = hidden; 
	}
	
	public XSmtExpr(XOp<T> op, boolean hidden, XSmtTerm<T> ch1, XSmtTerm<T> ch2) {
		super(op.type(ch1.type().<T>xTypeSystem()));

		this.op = op; 
		this.children = new ArrayList<XSmtTerm<T>>(2);
		this.children.add(ch1); 
		this.children.add(ch2); 
		this.hidden = hidden; 
	}

	public XSmtExpr(XOp<T> op, boolean hidden, XSmtTerm<T> ch1) {
		super(op.type(ch1.type().<T>xTypeSystem()));

		this.op = op; 
		this.children = new ArrayList<XSmtTerm<T>>(1);
		this.children.add(ch1); 
		this.hidden = hidden; 
	}

	
	@Override
	public XSmtTerm<T> subst(XTerm<T> t1, XTerm<T> t2) {
		XSmtTerm<T> new_this = super.subst(t1, t2);
		if (new_this != this)
			return new_this;
		
		List<XSmtTerm<T>> new_children = new ArrayList<XSmtTerm<T>>(children.size());
		boolean changed = false;
		for (XSmtTerm<T> ch : children) {
			XSmtTerm<T> new_ch = ch.subst(t1, t2);
			new_children.add(new_ch);
			if (ch!= new_ch)
				changed = true; 
		}
		
		if(changed)
			return new XSmtExpr<T>(op, hidden, new_children);
		return this;
	}

	@Override
	public XOp<T> op() {
		return op; 
	}

	@Override
	public List<? extends XTerm<T>> children() {
		return children; 
	}

	@Override
	public boolean isAtom() {
		return type.isBoolean(); 
	}

	@Override
	public boolean isHidden() {
		return hidden; 
	}

	@Override
	public void print(XPrinter p) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(); 
		sb.append(op.toString());
		for (XSmtTerm<T> ch: children)
			sb.append(" " + ch);
		
		return sb.toString(); 
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((children == null) ? 0 : children.hashCode());
		result = prime * result + (hidden ? 1231 : 1237);
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
		XSmtExpr<?> other = (XSmtExpr<?>) obj;
		if (children == null) {
			if (other.children != null)
				return false;
		} else if (!children.equals(other.children))
			return false;
		if (hidden != other.hidden)
			return false;
		if (op == null) {
			if (other.op != null)
				return false;
		} else if (!op.equals(other.op))
			return false;
		return true;
	}


}
