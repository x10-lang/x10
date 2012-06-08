package x10.constraint;


public interface XConstraintSystem {
	XConstraint mkConstraint(); 
	XConstraint makeTrueConstraint();
/*	public static final String SELF_VAR_PREFIX="self";
    public static final String THIS_VAR_PREFIX="this";
    public static final CThis THIS_THIS = makeThis();
    public static final XLit NULL = XTerms.NULL;
    public static final XLit TRUE = XTerms.TRUE;
    public static final XLit FALSE = XTerms.FALSE;
    public static final XLit OPERATOR = XTerms.OPERATOR; 
    static int selfId = 0;
    public static final CSelf makeSelf() {return new CSelf(selfId++);}
    static int thisId = 1;
    public static final CThis makeThis() {return makeThis(null);}
    public static final CThis makeThis(Type t) {return new CThis(thisId++, t);}
    public static final XVar makeQualifiedThis(Type qualifier, Type base) {
        return makeQualifiedVar(qualifier, makeThis(base)); 
    }
    public static final XVar makeQualifiedVar(Type qualifier, XVar var) {return new QualifiedVar(qualifier, var); }
    public static final CField makeField(XVar var, MethodDef mi) {return new CField(var, mi);}
    public static final CField makeField(XVar var, FieldDef mi) {return new CField(var, mi);}
    public static final CLocal makeLocal(X10LocalDef ld) {return new CLocal(ld);}
    public static final CLocal makeLocal(X10LocalDef ld, String s) {return new CLocal(ld, s);}
    public static final CAtom makeAtom(MethodDef md, XTerm... t ) {return new CAtom(md, md, t);}
    public static final CAtom makeAtom(MethodDef md, MethodDef mdAsExpr, XTerm... t ) {return new CAtom(md, mdAsExpr, t);}
    public static final CAtom makeAtom(MethodDef md, List<XTerm> t ) {return new CAtom(md, md, t);}
    public static final CAtom makeAtom(FieldDef md, XTerm... t ) {return new CAtom(md, md, t);}
    public static final CAtom makeAtom(FieldDef md, FieldDef mdAsExpr, XTerm... t ) {return new CAtom(md, mdAsExpr, t);}
    public static final CAtom makeAtom(FieldDef md, List<XTerm> t ) {return new CAtom(md, md, t);}
    public static XLit makeLit(Object val, Type type) {
        if (val == null) return NULL;
        if (val.equals(true)) return TRUE;
        if (val.equals(false)) return FALSE;
        return new CLit(val, type);
    }
    public static XLit makeZero(Type type) {
        TypeSystem ts = type.typeSystem();
        if (type.isBoolean()) return FALSE;
        else if (type.isChar()) return new CLit(Character.valueOf('\0'), type);
        else if (type.isIntOrLess() || type.isUInt()) return new CLit(0, type);
        else if (type.isLongOrLess()) return new CLit(0L, type);
        else if (type.isFloat()) return new CLit(0.0f, type);
        else if (type.isDouble())return new CLit(0.0, type);
        else if (ts.isObjectOrInterfaceType(type, ts.emptyContext())) return XTerms.NULL;
        else return null;
    }
*/}
