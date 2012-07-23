package x10.types.constraints.xsmt;

import polyglot.types.Type;
import x10.constraint.XVar;
import x10.constraint.xsmt.SmtType;
import x10.constraint.xsmt.SmtUtil;
import x10.constraint.xsmt.SmtUtil.XSmtKind;
import x10.constraint.xsmt.SmtVariable;
import x10.constraint.xsmt.XSmtVar;
import x10.types.constraints.CThis;

public class CSmtThis extends XSmtVar implements CThis, SmtVariable {
	private static final long serialVersionUID = -6426206886218986965L;
    public static final String THIS_VAR_PREFIX="this";
    private String smtName; 
    public final int num;
    public final Type type;

    public CSmtThis(int n, Type type) {
        this.num = n;
        this.type = type;
        smtName = null; 
    }
    @Override 
    public int hashCode() {return num;}

	@Override
	public XSmtKind getKind() {
		return XSmtKind.VARIABLE;
	}
	
	@Override
	public Type type() {return type;}
	
	@Override
	public boolean hasVar(XVar<Type> v) {
		return equals(v); 
	}
	
	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof CSmtThis) return num == ((CSmtThis) o).num;
        return false;
    }
    @Override 
    public String toString() {
    	return THIS_VAR_PREFIX + (type != null ? "(:" + type + ")" : "");
    }
    
	@Override
	public String getName() {
		if (smtName == null)
			smtName = SmtUtil.mangle(toString()+num);
		return smtName;
	}
	
	@Override
	public SmtType getType() {
		return CSmtUtil.toSmtType(type);
	}
	
	@Override
	public String toSmt2() {
		return getName(); 
	}


}
