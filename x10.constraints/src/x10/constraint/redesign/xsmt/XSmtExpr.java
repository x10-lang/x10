package x10.constraint.redesign.xsmt;

import java.util.ArrayList;
import java.util.List;

import x10.constraint.redesign.XExpr;
import x10.constraint.redesign.XOp;
import x10.constraint.redesign.XTerm;

public class XSmtExpr<T extends XSmtType> extends XSmtTerm<T> implements XExpr<T> {
	private final XOp<T> op; 
	private final List<XSmtTerm<T>> children; 
	private final boolean hidden; 
	
	public XSmtExpr(XOp<T> op, List<XSmtTerm<T>> children, boolean hidden) {
		super(op.type());
		assert children.size() > 0; 
		
		this.op = op; 
		this.children = children; 
		this.hidden = hidden; 
	}
	
	public XSmtExpr(XOp<T> op, XSmtTerm<T>[] children, boolean hidden) {
		super(op.type());
		assert children.length > 0; 
		
		this.op = op; 
		this.children = new ArrayList<XSmtTerm<T>>(children.length);
		for (XSmtTerm<T> ch: children) {
			assert ch != null;
			this.children.add(ch);
		}
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
			return new XSmtExpr<T>(op, new_children, hidden);
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

}
