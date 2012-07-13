package x10.constraint.xsmt;

import java.util.ArrayList;
import java.util.List;

import x10.constraint.XField;
import x10.constraint.XTerm;
import x10.constraint.XVar;
import x10.constraint.xsmt.SmtUtil.XSmtKind;

public class XSmtField<T> extends XSmtVar implements XField<T>, SmtExpr {
	private static final long serialVersionUID = 4474065326337967670L;
    public XSmtVar receiver;
    public T field;
    public SmtFuncSymbol uf; // lazily initialized Smt function symbol corresponding to field application
    private boolean hidden;
    
    public XSmtField(XVar receiver, T field, boolean hidden) {
    	this.receiver = (XSmtVar)receiver; 
    	this.field = field;
    	this.uf = null; 
    	this.hidden = hidden; 
    }
    
    public XSmtField(XVar receiver, T field) {
    	this(receiver, field, false);
    }
    
    // memoize the "variables"
    protected XSmtVar[] vars;
    
    @Override
    public XSmtVar[] vars() {
        if (vars == null) initVars();
        return vars;
    }

    @SuppressWarnings("unchecked")
    protected void initVars() {
        int count = 0;
        for (XSmtVar source = this; source instanceof XSmtField; source = ((XSmtField<T>) source).receiver)
            count++;
        vars = new XSmtVar[count + 1];
        XSmtVar f = this;
        for (int i = count; i >= 0; i--) {
            vars[i] = f;
            if (i > 0)
                f = ((XSmtField<T>) f).receiver;
        }
    }	

	@Override
	public boolean hasVar(XVar v) {
		return equals(v) || receiver.hasVar(v); 
	}

	@Override
	public XSmtKind getKind() {
		return XSmtKind.APPLY_UF;
	}

	@Override
	public SmtType getType() {
		SmtType type = get(0).getType(); 
		return type.get(type.arity()); 
	}

	@Override
	public int getNumChildren() {
		return 2;
	}

	@Override
	public SmtTerm get(int i) {
		if (i == 0) {
			if (uf == null)
				uf = SmtUtil.makeFunctionSymbol(field, (SmtBaseType)receiver.getType(), SmtType.USort());
			return uf; 
		}
		if (i == 1) 
			return receiver;
		
		throw new IllegalArgumentException("SmtTerm has only two children. ");
	}

	@Override
	public List<? extends SmtTerm> getChildren() {
		List<SmtTerm> res = new ArrayList<SmtTerm>(2);
		res.add(get(0));
		res.add(receiver);
		return res; 
	}

	@Override
	public T field() {
		return field; 
	}

	@Override
	public XField<T> copyReceiver(XVar newReceiver) {
		if (newReceiver == receiver)
			return this; 
		return new XSmtField<T>((XSmtVar)newReceiver, field);
	}

	@Override
	public XVar receiver() {
		return receiver; 
	}

	@Override
	public XSmtTerm subst(XTerm x, XVar v) {
		XSmtTerm newReceiver = receiver.subst(x, v);
		if (receiver != newReceiver)
			return new XSmtField<T>((XSmtVar)newReceiver, field);
		return this; 
	}

	@Override
	public boolean isHidden() {
		return hidden; 
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((field == null) ? 0 : field.hashCode());
		result = prime * result + (hidden ? 1231 : 1237);
		result = prime * result
				+ ((receiver == null) ? 0 : receiver.hashCode());
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
		XSmtField<?> other = (XSmtField<?>) obj;
		if (field == null) {
			if (other.field != null)
				return false;
		} else if (!field.equals(other.field))
			return false;
		if (hidden != other.hidden)
			return false;
		if (receiver == null) {
			if (other.receiver != null)
				return false;
		} else if (!receiver.equals(other.receiver))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return (receiver == null ? "" : receiver.toString() + ".") + field;
	}
	
	@Override
	public String toSmt2() {
		return "(" + get(0).toSmt2() + " " + get(1).toSmt2() + ")";
	}
	
}
