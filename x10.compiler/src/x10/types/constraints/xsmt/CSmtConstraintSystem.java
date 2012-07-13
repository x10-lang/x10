package x10.types.constraints.xsmt;

import java.util.List;

import polyglot.ast.IntLit;
import polyglot.ast.IntLit.Kind;
import polyglot.types.FieldDef;
import polyglot.types.MethodDef;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import x10.constraint.XLit;
import x10.constraint.XTerm;
import x10.constraint.XVar;
import x10.constraint.xsmt.XSmtConstraintSystem;
import x10.types.X10LocalDef;
import x10.types.constraints.CAtom;
import x10.types.constraints.CConstraint;
import x10.types.constraints.CConstraintSystem;
import x10.types.constraints.CField;
import x10.types.constraints.CLocal;
import x10.types.constraints.CSelf;
import x10.types.constraints.CThis;
import x10.types.constraints.ConstraintManager;
import x10.types.constraints.XTypeLit;

public class CSmtConstraintSystem extends XSmtConstraintSystem implements CConstraintSystem {
    static int selfId = 0;
    static int thisId = 1;
    public static final XLit NULL = ConstraintManager.getConstraintSystem().xnull();
    public static final XLit TRUE = ConstraintManager.getConstraintSystem().xtrue();
    public static final XLit FALSE = ConstraintManager.getConstraintSystem().xfalse();
    

	@Override
	public CSelf makeSelf(Type t) {
		return new CSmtSelf(selfId++, t); 
	}

	@Override
	public CThis makeThis() {
		return makeThis(null);
	}

	@Override
	public CThis makeThis(Type t) {
		return new CSmtThis(thisId++, t);
	}

	@Override
	public XVar makeQualifiedThis(Type qualifier, Type base) {
		return makeQualifiedVar(qualifier, makeThis(base));
	}

	@Override
	public XVar makeQualifiedVar(Type qualifier, XVar var) {
		return new QualifiedVar(qualifier, var);
	}

	@Override
	public CField makeField(XVar var, MethodDef mi) {
		return new CSmtField(var, mi);
	}

	@Override
	public CField makeField(XVar var, FieldDef mi) {
		return new CSmtField(var, mi);
	}

	@Override
	public CLocal makeLocal(X10LocalDef ld) {
		return new CSmtLocal(ld);
	}

	@Override
	public CLocal makeLocal(X10LocalDef ld, String s) {
		return new CSmtLocal(ld, s);
	}

	@Override
	public CAtom makeAtom(MethodDef md, XTerm... t) {
		return new CSmtAtom(md, t);
	}

	//@Override
	//public CAtom makeAtom(MethodDef md, MethodDef mdAsExpr, XTerm... t) {
	//	return new CSmtAtom(md, t);
	//}

	@Override
	public CAtom makeAtom(MethodDef md, List<XTerm> t) {
		return new CSmtAtom(md, t.toArray(new XTerm[0])); 
	}

	@Override
	public CAtom makeAtom(FieldDef md, XTerm... t) {
		return new CSmtAtom(md, t); 
	}

	//@Override
	//public CAtom makeAtom(FieldDef md, FieldDef mdAsExpr, XTerm... t) {
	//	// TODO Auto-generated method stub
	//	return null;
	//}

	@Override
	public CAtom makeAtom(FieldDef md, List<XTerm> t) {
		return new CSmtAtom(md, t.toArray(new XTerm[0])); 
	}

	@Override
	public XLit makeLit(Object val, Type type) {
		if (val == null) return NULL;
		if (val.equals(true)) return TRUE; 
		if (val.equals(false)) return FALSE;
		return new CSmtLit(val, type);
	}

	@Override
	public XLit makeZero(Type type) {
        TypeSystem ts = type.typeSystem();
        if (type.isBoolean()) return FALSE;
        else if (type.isChar()) return new CSmtLit(Character.valueOf('\0'), type);
        else if (type.isIntOrLess() || type.isUInt()) return new CSmtLit(0, type);
        else if (type.isLongOrLess()) return new CSmtLit(0L, type);
        else if (type.isFloat()) return new CSmtLit(0.0f, type);
        else if (type.isDouble())return new CSmtLit(0.0, type);
        else if (ts.isObjectOrInterfaceType(type, ts.emptyContext())) 
        	return ConstraintManager.getConstraintSystem().xnull(); 
        else return null;		
	}

	@Override
	public XTypeLit makeTypeLit(Type type) {
		return new XSmtTypeLit(type);
	}

	@Override
	public Kind getIntLitKind(Type type) {
        if (type.isByte())   return IntLit.BYTE;
        if (type.isUByte())  return IntLit.UBYTE;
        if (type.isShort())  return IntLit.SHORT;
        if (type.isUShort()) return IntLit.USHORT;
        if (type.isInt())    return IntLit.INT;
        if (type.isUInt())   return IntLit.UINT;
        if (type.isLong())   return IntLit.LONG;
        if (type.isULong())  return IntLit.ULONG;
        return null;
	}

	@Override
	public CConstraint makeCConstraint(Type t) {
		return new CSmtConstraint(t); 
	}

	@Override
	public CConstraint makeCConstraint(XVar self, Type t) {
		return new CSmtConstraint(self, t); 
	}

	@Override
	public XTerm makeOpaque(Object op, boolean isatom, XTerm target,
			List<XTerm> args) {
		throw new UnsupportedOperationException("unimplemented yet");
	}

}
