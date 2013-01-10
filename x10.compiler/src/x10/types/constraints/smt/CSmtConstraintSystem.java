package x10.types.constraints.smt;

import java.util.ArrayList;
import java.util.List;

import polyglot.ast.IntLit;
import polyglot.ast.IntLit.Kind;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import x10.constraint.XDef;
import x10.constraint.XExpr;
import x10.constraint.XOp;
import x10.constraint.XTerm;
import x10.constraint.XUQV;
import x10.constraint.XVar;
import x10.constraint.smt.XSmtConstraintSystem;
import x10.constraint.smt.XSmtExpr;
import x10.constraint.smt.XSmtLit;
import x10.constraint.smt.XSmtVar;
import x10.constraint.xnative.XNativeUQV;
import x10.types.ConstrainedType;
import x10.types.X10LocalDef;
import x10.types.constraints.CConstraint;
import x10.types.constraints.CConstraintSystem;
import x10.types.constraints.CQualifiedVar;
import x10.types.constraints.CSelf;
import x10.types.constraints.CThis;
import x10.types.constraints.QualifierDef;
import x10.types.constraints.XTypeLit;

public class CSmtConstraintSystem extends XSmtConstraintSystem<Type> implements CConstraintSystem {
	private static int idCounter = 0;
	
	@Override
	public XVar<Type> makeSelf(Type t) {
		t = Types.baseTypeRec(t); 
		assert ! (t instanceof ConstrainedType);
		return makeUQV(t); 
	}

	@Override
	public XVar<Type> makeThis(Type t) {
		t = Types.baseTypeRec(t);
		assert ! (t instanceof ConstrainedType);
		return new CSmtThis(t, idCounter++);
	}

	@Override
	public CSmtLocal makeLocal(X10LocalDef def) {
		assert def != null;
		return new CSmtLocal(def, Types.baseTypeRec(Types.get(def.type())));
	}

	@Override
	public CSmtLocal makeLocal(X10LocalDef def, String name) {
		return new CSmtLocal(def, name, Types.baseTypeRec(def.resultType()));
	}

	@Override
	public XSmtExpr<Type> makeMethod(XDef<Type> md, XTerm<Type> receiver, List<? extends XTerm<Type>> terms) {
		XTerm<Type> method = makeField(receiver, md);
		List<XTerm<Type>> args = new ArrayList<XTerm<Type>>(terms.size() +1);
		args.add(method);
		for (XTerm<Type> t : terms)
			args.add(t); 
		return makeExpr(XOp.<Type>APPLY(md.resultType()), terms);
	}
	

	@Override
	public XExpr<Type> makeOpaque(Object op, boolean isatom,
			XTerm<Type> target, List<XTerm<Type>> args) {
		throw new UnsupportedOperationException("unimplemented yet!");
	}

	@Override
	public CQualifiedVar makeQualifiedVar(Type qualifier, XTerm<Type> base) {
		assert ! (qualifier instanceof ConstrainedType);
		QualifierDef q = new QualifierDef(qualifier);
		return new CSmtQualifiedVar(q, (XSmtVar<Type>)base);
	}

	@Override
	public XSmtLit<Type, ?> makeZero(Type type) {
		type = Types.baseTypeRec(type);
		assert ! (type instanceof ConstrainedType); 
        TypeSystem ts = type.typeSystem();
        if (type.isBoolean()) return xfalse(ts);
        else if (type.isChar()) return makeLit(type, Character.valueOf('\0'));
        else if (type.isIntOrLess() || type.isUInt()) return makeLit(type, 0);
        else if (type.isLongOrLess()) return makeLit(type, 0L);
        else if (type.isFloat()) return makeLit(type, 0.0f);
        else if (type.isDouble())return makeLit(type, 0.0);
        else if (ts.isObjectOrInterfaceType(type, ts.emptyContext())) 
        	return xnull(ts); 
        else return null;		
	}

	@Override
	public XTypeLit makeTypeLit(Type type) {
		Type kind = type; // [DC] HACK!!! need to use a proper 'kind' type
		return new CSmtTypeLit(kind, Types.baseTypeRec(type));
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
	public CConstraint makeCConstraint(Type type, TypeSystem ts) {
		type = Types.baseTypeRec(type); 
		assert ! (type instanceof ConstrainedType);
		return new CSmtConstraint(this,type,ts);
	}

	@Override
	public CConstraint makeCConstraint(XVar<Type> self, TypeSystem ts) {
		return new CSmtConstraint(this, (XSmtVar<Type>)self, ts); 
	}

	@Override
	public CConstraint makeCConstraint(TypeSystem ts) {
		return new CSmtConstraint(this, ts); 
	}


}
