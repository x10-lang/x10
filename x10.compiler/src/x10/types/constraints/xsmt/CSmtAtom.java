package x10.types.constraints.xsmt;

import polyglot.types.FieldDef;
import polyglot.types.MemberDef;
import polyglot.types.MethodDef;
import x10.constraint.XTerm;
import x10.constraint.xsmt.SmtFuncSymbol;
import x10.constraint.xsmt.SmtTerm;
import x10.constraint.xsmt.SmtType;
import x10.constraint.xsmt.SmtVariable;
import x10.constraint.xsmt.XSmtFormula;
import x10.types.constraints.CAtom;

public class CSmtAtom extends XSmtFormula<MemberDef> implements CAtom {
	private static final long serialVersionUID = 4645240272067106952L;
	private SmtVariable var; 

	public CSmtAtom(MethodDef op, XTerm... args) {
    	super(op, true, args);
    	var = null;
    }
    public CSmtAtom(FieldDef op, XTerm... args) {
    	super(op, true, args);
    	var = null;
    }
    /**
     * Return the MemberDef that this CAtom is built on.
     * @return
     */
    @Override
    public MemberDef def() {return op;}
    @Override
    public MemberDef exprDef() {return op;}

	@Override
	public SmtType getType() {
		throw new UnsupportedOperationException("Not implemented yet. ");
	}

	@Override
	public SmtTerm get(int i) {
		throw new UnsupportedOperationException("Not implemented yet. ");
	}
    
}
