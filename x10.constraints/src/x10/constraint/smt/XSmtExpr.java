package x10.constraint.smt;

import java.util.ArrayList;
import java.util.List;

import x10.constraint.XConstraintManager;
import x10.constraint.XExpr;
import x10.constraint.XOp;
import x10.constraint.XSimpleOp;
import x10.constraint.XTerm;
import x10.constraint.XType;
import x10.constraint.XVar;

public class XSmtExpr<T extends XType> extends XSmtTerm<T> implements XExpr<T> {
	private final XOp<T> op; 
	private final List<XSmtTerm<T>> children; 
	private final boolean hidden; 
	
	public XSmtExpr(XOp<T> op, boolean hidden, List<XSmtTerm<T>> children) {
		super(op.type(children.get(0).type().<T>xTypeSystem()));
		assert op.isArityValid(children.size()); 
		if (op.getKind() == XOp.Kind.EQ && children.size() != 2) {
			throw new IllegalArgumentException("eq size 2"); 
		}
		this.op = op; 
		this.children = children; 
		this.hidden = hidden; 
	}
	
	public XSmtExpr(XOp<T> op, boolean hidden, XSmtTerm<T> ch1, XSmtTerm<T> ch2) {
		super(op.type(ch1.type().<T>xTypeSystem()));
		assert (op.isArityValid(2));
		this.op = op; 
		this.children = new ArrayList<XSmtTerm<T>>(2);
		this.children.add(ch1); 
		this.children.add(ch2); 
		this.hidden = hidden; 
	}

	public XSmtExpr(XOp<T> op, boolean hidden, XSmtTerm<T> ch1) {
		super(op.type(ch1.type().<T>xTypeSystem()));
		assert(op.isArityValid(1));
		this.op = op; 
		this.children = new ArrayList<XSmtTerm<T>>(1);
		this.children.add(ch1); 
		this.hidden = hidden; 
	}
	
	public XSmtExpr(XSmtExpr<T> exp) {
		super(exp);
		assert exp != null;
		this.op = exp.op; 
		this.children = deepCopy(exp.children);
		this.hidden = exp.hidden; 
	}
	
	private List<XSmtTerm<T>> deepCopy (List<XSmtTerm<T>> terms) {
		List<XSmtTerm<T>> res = new ArrayList<XSmtTerm<T>>(terms.size());
		for (XSmtTerm<T> t : terms) {
			res.add(XConstraintManager.<T>getConstraintSystem().copy(t));
		}
		return res; 
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
	public List<? extends XSmtTerm<T>> children() {
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
	protected void print(XPrinter<T> p) {
		p.append("(");
		p.append(op().print(p));
		for (XSmtTerm<T> ch : children()) {
			p.append(" ");
			ch.print(p); 
		}
		p.append(")");
	}
	
	@Override
	protected void declare(XPrinter<T> p) {
		p.declare(this); 
		for (XSmtTerm<T> ch : children()) {
			ch.declare(p); 
		}
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

	@Override
	public boolean hasVar(XVar<T> var) {
		for (XTerm<T> ch :children)
			if (ch.equals(var))
				return true;
		return false;
	}

	@Override
	public XSmtTerm<T> get(int i) {
		return children.get(i);
	}

	@Override
	public boolean isProjection() {
		return 	(op.getKind() == XOp.Kind.APPLY_LABEL || // field or method dereference  a.foo
				 op.getKind() == XOp.Kind.APPLY && children.get(0).isProjection()); // method call a.foo(x,y);
	}
	
	@Override
	public XSmtTerm<T> accept(TermVisitor<T> visitor) {
		XSmtTerm<T> res = (XSmtTerm<T>)visitor.visit(this);
		if (res!=null) return res;
		List<XSmtTerm<T>> newArgs = new ArrayList<XSmtTerm<T>>(children.size());
		boolean wasNew = false;
		for (int i = 0; i < children.size(); ++i ) {
			XSmtTerm<T> xTerm = children.get(i);
			final XSmtTerm<T> newArg = xTerm.accept(visitor);
			wasNew |= newArg!=xTerm;
			newArgs.add(newArg);
		}

		if (!wasNew) return this;
		XSmtExpr<T> newThis = new XSmtExpr<T>(this.op,this.hidden, newArgs); 
		return newThis;
	}

	@Override
	public XSmtExpr<T> copy() {
		return new XSmtExpr<T>(this); 
	}
	
	@Override
	public String prettyPrint() {
		if (children.size() == 1) {
			return op.prettyPrint() + " " + children.get(0).prettyPrint(); 
		}

		if (op() instanceof XSimpleOp && children.size() >= 2) {
			StringBuilder sb = new StringBuilder(); 
			for (int i = 0; i < children.size(); ++i){
				XSmtTerm<T> t = children.get(i); 
				sb.append(i == 0? "" : (" " + op.prettyPrint() + " "));
				sb.append(t.prettyPrint());
			}
			return sb.toString();
		}
		
		StringBuilder sb = new StringBuilder(); 
		sb.append(op.prettyPrint());
		boolean simple = op() instanceof XSimpleOp;
		if (!simple)	
			sb.append("(");
		
		for (XSmtTerm<T> t : children) {
			sb.append((t == children.get(0) && simple ? "" : " ") + t.prettyPrint());
		}
		
		if (!simple)
			sb.append(")");
		
		return sb.toString(); 
	}
	
}
