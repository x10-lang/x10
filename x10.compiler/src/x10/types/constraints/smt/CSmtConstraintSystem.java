package x10.types.constraints.smt;

import java.util.ArrayList;
import java.util.List;

import polyglot.ast.IntLit.Kind;
import polyglot.ast.IntLit;
import polyglot.types.Def;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import x10.constraint.XDef;
import x10.constraint.XExpr;
import x10.constraint.XLit;
import x10.constraint.XOp;
import x10.constraint.XTerm;
import x10.constraint.XVar;
import x10.constraint.smt.XSmtConstraintSystem;
import x10.constraint.smt.XSmtExpr;
import x10.constraint.smt.XSmtField;
import x10.constraint.smt.XSmtLit;
import x10.constraint.smt.XSmtTerm;
import x10.constraint.smt.XSmtVar;
import x10.types.ConstrainedType;
import x10.types.FunctionType;
import x10.types.constraints.CConstraint;
import x10.types.constraints.CConstraintSystem;
import x10.types.constraints.CField;
import x10.types.constraints.CQualifiedVar;
import x10.types.constraints.CSelf;
import x10.types.constraints.CThis;
import x10.types.constraints.ConstraintManager;
import x10.types.constraints.CLocal;
import x10.types.constraints.XTypeLit;

public class CSmtConstraintSystem extends XSmtConstraintSystem<Type> implements CConstraintSystem {
	private static int idCounter = 0;
	
	@Override
	public CSelf makeSelf(Type t) {
		t = Types.baseTypeRec(t); 
		assert ! (t instanceof ConstrainedType);
		return new CSmtSelf(t, idCounter++); 
	}

	@Override
	public CThis makeThis(Type t) {
		t = Types.baseTypeRec(t);
		assert ! (t instanceof ConstrainedType);
		return new CSmtThis(t, idCounter++);
	}

	@Override
	public <D extends XDef<Type>> CSmtLocal<Type, D> makeLocal(D def) {
		assert def != null;
		return new CSmtLocal<Type,D>(def, Types.baseTypeRec(def.resultType()));
	}

	@Override
	public <D extends XDef<Type>> CSmtLocal<Type, D> makeLocal(D def, String name) {
		return new CSmtLocal<Type,D>(def, name, Types.baseTypeRec(def.resultType()));
	}

	@Override
	public <D extends XDef<Type>> XSmtField<Type,D> makeField(XTerm<Type> receiver, D label) {
		assert receiver!= null && label!= null;
		return new XSmtField<Type,D>(label, (XSmtTerm<Type>)receiver, Types.baseTypeRec(label.resultType()));
	}

	@Override
	public <D extends XDef<Type>> XSmtField<Type,D> makeFakeField(XTerm<Type> receiver, D label) {
		assert receiver!= null && label!= null;
		return new XSmtField<Type,D>(label, (XSmtTerm<Type>)receiver, Types.baseTypeRec(label.resultType()), true);
	}

	@Override
	public <D extends XDef<Type>> XSmtExpr<Type> makeMethod(D md, XTerm<Type> receiver, List<? extends XTerm<Type>> terms) {
		XTerm<Type> method = makeField(receiver, md);
		List<XTerm<Type>> args = new ArrayList<XTerm<Type>>(terms.size() +1);
		args.add(method);
		for (XTerm<Type> t : terms)
			args.add(t); 
		return makeExpr(XOp.<Type>APPLY(), terms);
	}
	

	@Override
	public XExpr<Type> makeOpaque(Object op, boolean isatom,
			XTerm<Type> target, List<XTerm<Type>> args) {
		throw new UnsupportedOperationException("unimplemented yet!");
	}

	@Override
	public <D extends XDef<Type>> CField<D> makeCField(XTerm<Type> receiver, D field) {
		if (field.toString().contains("IntRange")) {
			System.out.println("IntRange");
		}
		
		return new CSmtField<D>(field, (XSmtTerm<Type>)receiver, false);
	}

	@Override
	public <D extends XDef<Type>> CField<D> makeFakeCField(XTerm<Type> receiver, D field) {
		return new CSmtField<D>(field, (XSmtTerm<Type>)receiver, true);
	}

	@Override
	public CQualifiedVar makeQualifiedVar(Type qualifier, XTerm<Type> base) {
		assert ! (qualifier instanceof ConstrainedType);
		return new CSmtQualifiedVar(base.type(), qualifier, (XSmtVar<Type>)base);
	}

	@Override
	public XSmtLit<Type, ?> makeZero(Type type) {
		type = Types.baseTypeRec(type);
		assert ! (type instanceof ConstrainedType); 
        TypeSystem ts = type.typeSystem();
        if (type.isBoolean()) return xfalse(ts);
        else if (type.isChar()) return makeLit(Character.valueOf('\0'), type);
        else if (type.isIntOrLess() || type.isUInt()) return makeLit(0, type);
        else if (type.isLongOrLess()) return makeLit(0L, type);
        else if (type.isFloat()) return makeLit(0.0f, type);
        else if (type.isDouble())return makeLit(0.0, type);
        else if (ts.isObjectOrInterfaceType(type, ts.emptyContext())) 
        	return xnull(ts); 
        else return null;		
	}

	@Override
	public XTypeLit makeTypeLit(Type type) {
		type = Types.baseTypeRec(type);
		return new CSmtTypeLit(type, type);
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
	public CConstraint makeCConstraint(Type type) {
		type = Types.baseTypeRec(type); 
		assert ! (type instanceof ConstrainedType);
		return new CSmtConstraint(type);
	}

	@Override
	public CConstraint makeCConstraint(XTerm<Type> self) {
		return new CSmtConstraint((XSmtTerm<Type>)self); 
	}


}
