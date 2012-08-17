package x10.constraint.smt;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import x10.constraint.XTerm;
import x10.constraint.XType;
import x10.constraint.XVar;

public class XSmtVar<T extends XType> extends XSmtTerm<T> implements XVar<T> {
	private String name; 
	
	public XSmtVar(T t, String name) {
		super(t);
		this.name = name; 
	}
	
	public XSmtVar(XSmtVar<T> other) {
		super(other);
		this.name = other.name; 
	}

	@Override
	public boolean hasVar(XVar<T> var) {
		return equals(var);
	}
	
	@Override
	public List<XTerm<T>> vars() {
		List<XTerm<T>> res = new ArrayList<XTerm<T>>(1);
		res.add(this); 
		return res;
	}

	@Override
	public String name() {
		return name; 
	}

	@Override
	protected void print(XPrinter<T> p) {
		p.append(p.mangle(toString()));
	}
	
	@Override
	protected void declare(XPrinter<T> p) {
		p.declare(this); 
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		
		if (! (obj instanceof XSmtVar))
			return false;
		
		@SuppressWarnings("unchecked")
		XSmtVar<T> other = (XSmtVar<T>) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return name + ":" + type(); 
	}
	
	@Override
	public String prettyPrint() {
		return name; 
	}

	@Override
	public XSmtVar<T> copy() {
		return new XSmtVar<T>(this); 
	}


}
