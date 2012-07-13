package x10.types.constraints.xsmt;

import polyglot.types.Type;
import x10.constraint.XVar;
import x10.constraint.xsmt.SmtType;
import x10.constraint.xsmt.SmtUtil.XSmtKind;
import x10.constraint.xsmt.SmtUtil;
import x10.constraint.xsmt.SmtVariable;
import x10.constraint.xsmt.XSmtVar;
import x10.types.constraints.CSelf;

public class CSmtSelf extends XSmtVar implements CSelf, SmtVariable {
	private static final long serialVersionUID = -3619764732722758L;
	private String smtName; 
	private SmtType smtType; 
	@Override
	public XSmtKind getKind() {
		return XSmtKind.VARIABLE;
	}
	
    public static final String SELF_VAR_PREFIX="self";
    
    public final int num;
    public final Type type; 
    public CSmtSelf(int n, Type t) {this.num=n; smtName = null; smtType = null; this.type = t; }
    @Override
    public int hashCode() {return num;}
    @Override
    public boolean hasVar(XVar v) {return equals(v);}
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof CSmtSelf) return num == ((CSmtSelf) o).num;
        return false;
    }
    @Override
    public String toString() {return SELF_VAR_PREFIX;}

    @Override
	public String getName() {
    	if (smtName == null)
    		smtName = SmtUtil.mangle(toString()+num);
		return smtName;  
	}
	@Override
	public String toSmt2() {
		return getName(); 
	}
	
	@Override
	public SmtType getType() {
		if (smtType == null)
			return smtType = CSmtUtil.toSmtType(type);
		return smtType; 
	}

	public void setType(SmtType t) {
		smtType = t; 
	}
}
