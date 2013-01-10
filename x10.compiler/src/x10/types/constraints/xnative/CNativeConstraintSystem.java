package x10.types.constraints.xnative;

import java.util.ArrayList;
import java.util.List;

import polyglot.ast.IntLit;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import x10.constraint.XDef;
import x10.constraint.XExpr;
import x10.constraint.XLit;
import x10.constraint.XOp;
import x10.constraint.XTerm;
import x10.constraint.XVar;
import x10.constraint.xnative.XNativeConstraintSystem;
import x10.constraint.xnative.XNativeTerm;
import x10.constraint.xnative.XNativeUQV;
import x10.types.ConstrainedType;
import x10.types.X10LocalDef;
import x10.types.constraints.CConstraint;
import x10.types.constraints.CConstraintSystem;
import x10.types.constraints.CLocal;
import x10.types.constraints.CQualifiedVar;
import x10.types.constraints.QualifierDef;
import x10.types.constraints.XTypeLit;

public class CNativeConstraintSystem extends XNativeConstraintSystem<Type> implements CConstraintSystem {

    @Override
    public XNativeUQV<Type> makeSelf(Type t) {
    	return makeUQV(t, "self");
	}

    @Override
    public XNativeUQV<Type> makeThis(Type t) {
    	return makeUQV(t, "this");
    }

    @Override
    public CLocal makeLocal(X10LocalDef ld) {
    	return new CNativeLocal(ld);
    }
    @Override
    public CLocal makeLocal(X10LocalDef ld, String s) {
    	return new CNativeLocal(ld, s);
    }

	@Override
	public XExpr<Type> makeMethod(XDef<Type> def, XTerm<Type> receiver, List<? extends XTerm<Type>> terms) {
		XTerm<Type> method = makeField(receiver, def);
		List<XTerm<Type>> args = new ArrayList<XTerm<Type>>(terms.size() +1);
		args.add(method);
		for (XTerm<Type> t : terms)
			args.add(t); 
		return makeExpr(XOp.<Type>APPLY(def.resultType()), terms);
	}

	

//    @Override
//    public CAtom makeAtom(MethodDef md, XTerm... t ) {
//    	return new CNativeAtom(md, md, t);
//    }
//    //public CAtom makeAtom(MethodDef md, MethodDef mdAsExpr, XTerm... t ) {
//    //	return new CNativeAtom(md, mdAsExpr, t);
//    //}
//    @Override
//    public CAtom makeAtom(MethodDef md, List<XTerm> t ) {
//    	return new CNativeAtom(md, md, t.toArray(new XTerm[0]));
//    }
//    @Override
//    public CAtom makeAtom(FieldDef md, XTerm... t ) {
//    	return new CNativeAtom(md, md, t);
//    }
//   // public CAtom makeAtom(FieldDef md, FieldDef mdAsExpr, XTerm... t ) {
//   // 	return new CNativeAtom(md, mdAsExpr, t);
//   // }
//    @Override
//    public CAtom makeAtom(FieldDef md, List<XTerm> t ) {
//    	return new CNativeAtom(md, md, t.toArray(new XTerm[0]));
//    }
    /*
    @Override
    public XLit<Type,?> makeLit(Object val, Type type) {
        TypeSystem ts = type.typeSystem();
        if (val == null) return xnull(ts);
        if (val.equals(true)) return xtrue(ts);
        if (val.equals(false)) return xfalse(ts);
        return new CNativeLit(val, type);
    }
    */
	@Override
	public XTypeLit makeTypeLit(Type type) {
		Type kind = type; // [DC] HACK!!! need to use a proper 'kind' type
		return new XNativeTypeLit(kind, Types.baseTypeRec(type));
	}
	@Override
	public XLit<Type,?> makeZero(Type type) {
        TypeSystem ts = type.typeSystem();
        if (type.isBoolean()) return xfalse(ts);
        else if (type.isChar()) return new CNativeLit(Character.valueOf('\0'), type);
        else if (type.isIntOrLess() || type.isUInt()) return new CNativeLit(0, type);
        else if (type.isLongOrLess()) return new CNativeLit(0L, type);
        else if (type.isFloat()) return new CNativeLit(0.0f, type);
        else if (type.isDouble())return new CNativeLit(0.0, type);
        else if (ts.isObjectOrInterfaceType(type, ts.emptyContext())) 
        	return xnull(ts); 
        else return null;
    }
    
    @Override
    public IntLit.Kind getIntLitKind(Type type) {
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
    /**
     *  Creates a field access term corresponding to an @Opaque declaration. Currently 
     *  this is handled in a rather naive way, by modeling it as a field access where
     *  the field consists of the opaque method name and arguments
     */
    @Override
    public XExpr<Type> makeOpaque(Object op, boolean isAtomic, XTerm<Type> target, List<XTerm<Type>> args) { 
		throw new UnsupportedOperationException("unimplemented yet!");
//		args.add(0,target); 
//    	return makeAtom(op, isAtomic, args.toArray(new XTerm[0]));
    }
        
    @Override
    public CConstraint makeCConstraint(Type t, TypeSystem ts) {
    	assert !(t instanceof ConstrainedType);
    	return new CNativeConstraint(this, t==null ? null : makeSelf(t), ts); 
    }
    @Override
    public CConstraint makeCConstraint(XVar<Type> self, TypeSystem ts) {
    	return new CNativeConstraint(this, self, ts); 
    }
    
	@Override
	public CConstraint makeCConstraint(TypeSystem ts) {
    	return new CNativeConstraint(this, (XVar<Type>)null, ts); 
	}
    
    
    /*
    public XVar<Type> makeQualifiedThis(Type qualifier, Type base) {
        return makeQualifiedVar(qualifier, makeThis(base)); 
    }
    */
	@Override
	public CQualifiedVar makeQualifiedVar(Type qualifier, XTerm<Type> base) {
		QualifierDef q = new QualifierDef(qualifier);
    	return new CNativeQualifiedVar(q, (XNativeTerm<Type>)base, false); 
    }

}
