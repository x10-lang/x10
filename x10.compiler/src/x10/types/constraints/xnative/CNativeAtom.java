package x10.types.constraints.xnative;

import java.util.ArrayList;
import java.util.List;
import polyglot.types.FieldDef;
import polyglot.types.MemberDef;
import polyglot.types.MethodDef;
import x10.constraint.XFormula;
import x10.constraint.XTerm;
import x10.constraint.xnative.XNativeFormula;
import x10.constraint.xnative.XNativeTerm;
import x10.types.constraints.CAtom;

/**
 * An atomic formula built from a property field (this has an associated nullary
 * method) or from a method def.
 * The CAtom knows about the MemberDef it is built form. X10 type information
 * about the CAtom can be obtained from this MemberDef.
 * @author vj
 *
 */
public class CNativeAtom extends XNativeFormula<MemberDef> implements CAtom {
    private static final long serialVersionUID = -1734428949188126121L;
    public CNativeAtom(MethodDef op, MethodDef opAsExpr, XTerm... args) {
    	super(op, opAsExpr, true, args);
    }
    public CNativeAtom(FieldDef op, FieldDef opAsExpr, XTerm... args) {
    	super(op, opAsExpr, true, args);
    }
    /**
     * Return the MemberDef that this CAtom is built on.
     * @return
     */
    @Override
    public MemberDef def() {return op;}
    @Override
    public MemberDef exprDef() {return asExprOp;}
 
}
