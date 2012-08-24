package x10.constraint.smt;

import java.util.HashSet;
import java.util.Set;

import x10.constraint.XConstraintManager;
import x10.constraint.XType;
import x10.constraint.XVar;

public class XSmtQuantifier<T extends XType> extends XSmtTerm<T> {
	Set<XSmtEQV<T>> eqv;
	Set<XSmtVar<T>> uqv;
	final XSmtTerm<T> body;
	
	protected XSmtQuantifier(XSmtTerm<T> body) {
		super(body.type()); 
		this.body = body;
		eqv = new HashSet<XSmtEQV<T>>(); 
		uqv = new HashSet<XSmtVar<T>>();
		collectQuantifiers(this.body); 
	}
	
	protected XSmtQuantifier(XSmtQuantifier<T> other) {
		super(other); 
		this.body = XConstraintManager.<T>getConstraintSystem().copy(other.body); 
		collectQuantifiers(this.body); 
	}
	
	public XSmtTerm<T> body() {
		return body;
	}
	
	public boolean onlyUniversal() {
		return eqv.isEmpty();
	}
	
	public boolean onlyExistential() {
		return uqv.isEmpty();
	}
	
	private void collectQuantifiers(XSmtTerm<T> term) {
		if (term instanceof XSmtEQV) 
			eqv.add((XSmtEQV<T>)term); 
		else
		if (term instanceof XSmtUQV) 
			uqv.add((XSmtUQV<T>)term); 
		else
		if (term instanceof XSmtVar)
			uqv.add((XSmtVar<T>)term);
		else
		if (term instanceof XSmtExpr) {
			XSmtExpr<T> e = (XSmtExpr<T>)term;
			for (XSmtTerm<T> t : e.children())
				collectQuantifiers(t); 
		}
	}
	
	@Override
	public boolean hasVar(XVar<T> var) {
		return body.hasVar(var); 
	}

	@Override
	public XSmtQuantifier<T> copy() {
		return new XSmtQuantifier<T>(this); 
	}

	@Override
	public String prettyPrint() {
		StringBuilder sb = new StringBuilder(); 
		if (!uqv.isEmpty()) {
			sb.append("forall");
			for (XSmtTerm<T> t : uqv) {
				sb.append(" "+t.prettyPrint()); 
			}
			sb.append(".");
		}
		if (!eqv.isEmpty()) {
			sb.append("exists");
			for (XSmtTerm<T> t : eqv) {
				sb.append(" "+t.prettyPrint()); 
			}
			sb.append(".");
		}
		sb.append(body.prettyPrint());
		return sb.toString(); 
	}

	@Override
	protected void print(XPrinter<T> p) {
		int parenCount = 0; 
		if (!uqv.isEmpty()) {
			p.append("(forall (");
			for (XSmtTerm<T> t: uqv) {
				p.append(" (");
				t.print(p);
				p.append(" " + p.printType(t.type()));
				p.append(")");
			}
			p.append(")");
			parenCount++; 
		}
		
		if (!eqv.isEmpty()) {
			p.append("(exists (");
			for (XSmtTerm<T> t: eqv) {
				p.append(" (");
				t.print(p);
				p.append(" " + p.printType(t.type()));
				p.append(")");
			}
			p.append(")");
			parenCount++; 
		}
		
		body.print(p);
		// closing parenthesis
		for (int i = 0; i < parenCount; ++i)
			p.append(")");
	}

	@Override
	protected void declare(XPrinter<T> p) {
		body.declare(p); 
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((body == null) ? 0 : body.hashCode());
		result = prime * result + ((eqv == null) ? 0 : eqv.hashCode());
		result = prime * result + ((uqv == null) ? 0 : uqv.hashCode());
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
		@SuppressWarnings("unchecked")
		XSmtQuantifier<T> other = (XSmtQuantifier<T>) obj;
		if (body == null) {
			if (other.body != null)
				return false;
		} else if (!body.equals(other.body))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return prettyPrint(); 
	}

}
