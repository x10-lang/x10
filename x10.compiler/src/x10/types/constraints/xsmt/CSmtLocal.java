package x10.types.constraints.xsmt;

import polyglot.ast.Typed;
import polyglot.types.Type;
import polyglot.types.Types;
import x10.constraint.xsmt.SmtType;
import x10.constraint.xsmt.SmtVariable;
import x10.constraint.xsmt.XSmtLocal;
import x10.types.constraints.CLocal;
import x10.types.X10LocalDef;

public class CSmtLocal extends XSmtLocal<X10LocalDef> implements CLocal,Typed, SmtVariable {
	private static final long serialVersionUID = -7995799681263229908L;
	String s; // just for documentation

    public CSmtLocal(X10LocalDef ld) {
        super(ld);
        s=ld.name().toString();
    }
    public CSmtLocal(X10LocalDef ld, String s) {
        super(ld);
        this.s=s;
    }
    
    @Override
    public X10LocalDef localDef() {return name;}
    /** Return the type of this variable.
     * 
     */
    @Override
    public Type type() {return Types.get(name.type());}
    
    @Override 
    public String toString() {return s;}
    
    @Override
    public SmtType getType() {
    	return CSmtUtil.toSmtType(type());
    }
    
}

