package polyglot.ext.x10.types;

import java.util.ArrayList;
import java.util.List;

import polyglot.ast.Binary;
import polyglot.ast.Call;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.Formal;
import polyglot.ast.Lit;
import polyglot.ast.Local;
import polyglot.ast.Receiver;
import polyglot.ast.Term;
import polyglot.ast.TypeNode;
import polyglot.ast.Unary;
import polyglot.ast.Variable;
import polyglot.ext.x10.ast.Contains;
import polyglot.ext.x10.ast.Here;
import polyglot.ext.x10.ast.ParExpr;
import polyglot.ext.x10.ast.SubtypeTest;
import polyglot.ext.x10.ast.Tuple;
import polyglot.ext.x10.ast.X10Special;
import polyglot.types.FieldInstance;
import polyglot.types.Flags;
import polyglot.types.LocalInstance;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import x10.constraint.XConstraint;
import x10.constraint.XConstraint_c;
import x10.constraint.XEQV_c;
import x10.constraint.XFailure;
import x10.constraint.XLit;
import x10.constraint.XLit_c;
import x10.constraint.XLocal;
import x10.constraint.XName;
import x10.constraint.XRef_c;
import x10.constraint.XRoot;
import x10.constraint.XSelf;
import x10.constraint.XTerm;
import x10.constraint.XTerms;
import x10.constraint.XVar;

/**
 * Translate from a Expr or TypeNode to a XConstraint term that can be serialized.
 * @author nystrom
 */
public class XTypeTranslator {
	private final X10TypeSystem ts;

	public XTypeTranslator(X10TypeSystem xts) {
		super();
		ts = xts;
	}

	public void addTypeToEnv(XTerm self, final Type t) throws SemanticException {
	    self.setSelfConstraint(new XRef_c<XConstraint>() {
		public XConstraint compute() {
		    XConstraint c = X10TypeMixin.realX(t);
		    Type base = X10TypeMixin.baseType(t);
		    if (false) {
			if (c != null) {
			    c = c.copy();
			}
			else {
			    c = new XConstraint_c();
			}
			try {
			    c = c.addBinding(XTerms.makeField(XSelf.Self, XTerms.makeName("type")), trans(base));
			}
			catch (XFailure e) {
			    c.setInconsistent();
			}
		    }
		    return c;
		}
	    });
	}

	public XTerm trans(Unary t) throws SemanticException {
		if (t.operator() == Unary.NOT)
			return XTerms.makeNot(trans(t.expr()));
		XTerm v = XTerms.makeAtom(XTerms.makeName(t.operator()), trans(t.expr()));
		addTypeToEnv(v, t.type());
		return v;
	}
	
	public XVar trans(XVar target, FieldInstance fi) throws SemanticException {
		XVar v = XTerms.makeField(target, XTerms.makeName(fi.def(), fi.name().toString()));
		addTypeToEnv(v, fi.type());
		return v;
	}

	public XTerm trans(XTerm target, FieldInstance fi) throws SemanticException {
		return trans(target, fi, fi.type());
	}
	
	
	public XTerm trans(XTerm target, FieldInstance fi, Type t) throws SemanticException {
		XTerm v;
		XName field = XTerms.makeName(fi.def(), fi.name().toString());
		if (target instanceof XVar) {
			v = XTerms.makeField((XVar) target, field);
		}
		else {
			v = XTerms.makeAtom(field, target);
		}
		addTypeToEnv(v, t);
		return v;
	}
	
	public XTerm trans(Field t) throws SemanticException {
		return trans(trans(t.target()), t.fieldInstance(), t.type());
	}

	public XTerm trans(X10Special t) throws SemanticException {
		if (t.kind() == X10Special.SELF) {
			XVar v = XSelf.Self;
			addTypeToEnv(v, t.type());
			return v;
		}
		else {
			return transThis(t.type());
		}
	}

	public XLocal trans(LocalInstance li) throws SemanticException {
		return trans(li, li.type());
	}
	
	public XLocal trans(LocalInstance li, Type t) throws SemanticException {
		XLocal v = XTerms.makeLocal(XTerms.makeName(li.def(), li.name().toString()));
		addTypeToEnv(v, t);
		return v;
	}

	public XLocal trans(Local t) throws SemanticException {
		return trans(t.localInstance(), t.type());
	}

	public XTerm trans(TypeNode t) {
		return trans(t.type());
	}
	
	public XLocal transTypeParam(ParameterType t) {
		return XTerms.makeLocal(XTerms.makeName(t));
	}
	
	public XVar transPathType(PathType t) {
	    XName field = XTerms.makeName(t.property(), t.property().name().toString());
	    return XTerms.makeField(t.formals().get(0), field);
	}
	
	public XTerm trans(Type t) {
		if (t instanceof ParameterType)
			return transTypeParam((ParameterType) t);
//		if (t instanceof X10ClassType)
//		    return transClassType((X10ClassType) t);
//		if (t instanceof ConstrainedType)
//		    return transConstrainedType((ConstrainedType) t);
		if (t instanceof PathType)
			return transPathType((PathType) t);
		if (t instanceof MacroType) {
		    MacroType pt = (MacroType) t;
		    return trans(pt.definedType());
		}
		return new XTypeLit_c(t);
//		return XTerms.makeLit(t);
	}
	
	public static boolean hasVar(Type t, XVar x) {
	    if (t instanceof ConstrainedType) {
		ConstrainedType ct = (ConstrainedType) t;
		Type b = X10TypeMixin.baseType(t);
		XConstraint c = X10TypeMixin.xclause(t);
		if ( hasVar(b, x)) return true;
		for (XTerm term : c.constraints()) {
		    if (term.hasVar(x))
			return true;
		}
	    }
	    if (t instanceof PathType) {
		PathType pt = (PathType) t;
		Type b = pt.baseType();
		XVar v = pt.base();
		return hasVar(b, x) || v.hasVar(x);
	    }
	    if (t instanceof MacroType) {
		MacroType pt = (MacroType) t;
		return hasVar(pt.definedType(), x);
	    }
	    return false;
	}
	
	public static Type subst(Type t, XTerm y, XRoot x) {
		if (t instanceof ConstrainedType) {
		    ConstrainedType ct = (ConstrainedType) t;
		    Type b = X10TypeMixin.baseType(t);
		    XConstraint c = X10TypeMixin.xclause(t);
		    try {
			return X10TypeMixin.xclause(subst(b, y, x), c.substitute(y, x));
		    }
		    catch (XFailure e) {
		    }
		}
		if (t instanceof PathType) {
		    PathType pt = (PathType) t;
		    Type b = pt.baseType();
		    XVar v = pt.base();
		    Type b2 = subst(b, y, x);
		    XTerm z = v.subst(y, x);
		    if (z instanceof XVar) {
			return pt.base((XVar) z, b2);
		    }
		    return pt.base(new XEQV_c(XTerms.makeName(v.toString()), true), b2);
		}
		if (t instanceof MacroType) {
		    MacroType pt = (MacroType) t;
		    return subst(pt.definedType(), y, x);
		}
		return t;
	}

	public static void variables(Type t, List<XVar> result) {
	}

	private XTerm transClassType(X10ClassType t) {
	    X10ClassDef def = t.x10Def();
	    
	    int n = def.typeParameters().size();
	    if (n == 0)
		return XTerms.makeLit(def);
	    
	    List<XTerm> terms = new ArrayList<XTerm>();

	    if (t.isIdentityInstantiation()) {
		for (int i = 0; i < n; i++) {
		    XTerm ti = trans(def.typeParameters().get(i));
		    terms.add(ti);
		}
		return XTerms.makeAtom(XTerms.makeName(def), terms);
	    }
	    
	    List<Type> args = t.typeArguments();
	    for (int i = 0; i < n; i++) {
		XTerm ti = trans(args.get(i));
		terms.add(ti);
	    }
	    return XTerms.makeAtom(XTerms.makeName(def), terms);
	}

	public XLit trans(int t) {
		return XTerms.makeLit(t);
	}
	
	public XLit trans(boolean t) {
		return XTerms.makeLit(t);
	}
	public XLit transNull() {
		return XTerms.makeLit(null);
	}
	
	public class XTypeLit_c extends XLit_c {
	    private XTypeLit_c(Type l) {
		super(l);
	    }

	    public Type type() {
	    return (Type) val;
	    }

	    public boolean hasVar(XVar v) {
	    return XTypeTranslator.hasVar(type(), v);
	    }

	    public XTerm subst(XTerm y, XRoot x, boolean propagate) {
		XTypeLit_c n = (XTypeLit_c) super.subst(y, x, propagate);
		if (n == this) n = (XTypeLit_c) clone();
		n.val = XTypeTranslator.subst(type(), y, x);
		return n;
	    }

	    public void variables(List<XVar> result) {
		XTypeTranslator.variables(type(), result);
	    }
	}

	class HereToken {
		@Override
		public boolean equals(Object o) {
			return o instanceof HereToken;
		}
		@Override
		public String toString() {
			return "here";
		}
		@Override
		public int hashCode() {
			return "here".hashCode();
		}
	}
	
	HereToken here;
	
	public XLit transHere() {
		if (here == null) here = new HereToken();
		return XTerms.makeLit(here);
	}
	
	public XLit trans(Lit t) {
		return XTerms.makeLit(t.constantValue());
	}

	public XTerm trans(Binary t) throws SemanticException {
		Expr left = t.left();
		Expr right = t.right();
		XTerm v;
		if (t.operator() == Binary.EQ) {
			v = XTerms.makeEquals(trans(left), trans(right));
		}
		else if (t.operator() == Binary.COND_AND || (t.operator() == Binary.BIT_AND && ts.isImplicitCastValid(t.type(), ts.Boolean()))) {
			v = XTerms.makeAnd(trans(left), trans(right));
		}
		else {
			v = XTerms.makeAtom(XTerms.makeName(t.operator()), trans(left), trans(right));
		}
		addTypeToEnv(v, t.type());
		return v;
	}
	
	public XTerm trans(Tuple t) throws SemanticException {
	    List<XTerm> terms = new ArrayList<XTerm>();
	    for (Expr e : t.arguments()) {
		terms.add(trans(e));
	    }
	    return XTerms.makeAtom(XTerms.makeName("tuple"), terms);
	}
	
	public XTerm trans(Contains t) throws SemanticException {
	    Expr left = t.item();
	    Expr right = t.collection();
	    boolean containsAll = t.isSubsetTest();
	    if (containsAll)
		return XTerms.makeAtom(XTerms.makeName("subset"), trans(left), trans(right));
	    else
		return XTerms.makeAtom(XTerms.makeName("in"), trans(left), trans(right));
	}

	public XTerm trans(SubtypeTest t) throws SemanticException {
		TypeNode left = t.subtype();
		TypeNode right = t.supertype();
		if (t.equals())
		    return XTerms.makeEquals(trans(left.type()), trans(right.type()));
		else
		    return transSubtype(left.type(), right.type());
	}
	
	public XTerm trans(Call t) throws SemanticException {
	    X10MethodInstance xmi = (X10MethodInstance) t.methodInstance();
	    Flags f = xmi.flags();
	    if (X10Flags.toX10Flags(f).isProperty()) {
		XTerm r = trans(t.target());
		// FIXME: should just return the atom, and add atom==body to the real clause of the class
		// FIXME: fold in class's real clause constraints on parameters into real clause of type parameters
		XTerm body = xmi.body();
		if (body != null) {
		    body = body.subst(r, transThis(xmi.container()));
		    for (int i = 0; i < t.arguments().size(); i++) {
			XRoot x = (XRoot) X10TypeMixin.selfVar(xmi.formalTypes().get(i));
			XTerm y = trans(t.arguments().get(i));
			body = body.subst(y, x);
		    }
		    return body;
		}
		else {
		    if (t.arguments().size() == 0) {
			XName field = XTerms.makeName(xmi.def(), xmi.name() + "()");
			XTerm v;
			if (r instanceof XVar) {
				v = XTerms.makeField((XVar) r, field);
			}
			else {
				v = XTerms.makeAtom(field, r);
			}
			addTypeToEnv(v, xmi.returnType());
			return v;
		    }
		    else {
			List<XTerm> terms = new ArrayList<XTerm>();
			terms.add(r);
			for (Expr e : t.arguments()) {
			    terms.add(trans(e));
			}
			XTerm v = XTerms.makeAtom(XTerms.makeName(xmi, xmi.name().toString()), terms);
			addTypeToEnv(v, xmi.returnType());
			return v;
		    }
		}
	    }
	
	    throw new SemanticException("Cannot translate call |" + t + "| into a constraint; it must be a property method call.");
	}

	public XTerm transSubtype(Type ltype, Type rtype) {
	    return new SubtypeSolver.XSubtype_c(trans(ltype), trans(rtype));
	}

	public XTerm trans(Variable term) throws SemanticException {
		//Report.report(1, "TypeTranslator: translating Variable " + term);
		if (term instanceof Field)
			return trans((Field) term);
		if (term instanceof X10Special)
			return trans((X10Special) term);
		if (term instanceof Local)
			return trans((Local) term);

		throw new SemanticException("Cannot translate variable |" + term + "| into a constraint; it must be a field, local, this, or self.");
	}

	public XTerm trans(Receiver term) throws SemanticException {
		// Report.report(1, "TypeTranslator: translating Receiver " + term);
		if (term == null)
			return null;
		if (term instanceof Lit)
			return trans((Lit) term);
		if (term instanceof Here)
			return transHere();
		if (term instanceof Variable)
			return trans((Variable) term);
		if (term instanceof X10Special)
			return trans((X10Special) term);
		if (term instanceof Expr) {
			Expr e = (Expr) term;
			if (e.isConstant())
				return XTerms.makeLit(e.constantValue());
		}
		if (term instanceof Call) {
		    Call c = (Call) term;
		    return trans((Call) term);
		}
		if (term instanceof Tuple) {
		    Tuple c = (Tuple) term;
		    return trans((Tuple) term);
		}
		if (term instanceof Unary) {
			Unary u = (Unary) term;
			Expr t2 = u.expr();
			Unary.Operator op = u.operator();
			if (op == Unary.POS)
				return trans(t2);
			return trans((Unary) term);
		}
		if (term instanceof Binary)
			return trans((Binary) term);
		if (term instanceof SubtypeTest)
		    return trans((SubtypeTest) term);
		if (term instanceof Contains)
		    return trans((Contains) term);
		if (term instanceof TypeNode)
			return trans((TypeNode) term);
		if (term instanceof ParExpr)
			return trans(((ParExpr) term).expr());

		throw new SemanticException("Cannot translate type or expression |" + term + "| (" + term.getClass().getName() + ")" + " to a term.");
	}

	/**
	 * Translate an expression into a XConstraint, throwing SemanticExceptions 
	 * if this is not possible.
	 * This must be called after type-checking of Expr.
	 * @param formals TODO
	 * @param term
	 * @param c
	 * @return
	 * @throws SemanticException
	 */
	public XConstraint constraint(List<Formal> formals, Expr term) throws SemanticException {
		XConstraint c = new XConstraint_c();
		if (term == null)
			return c;
		
		if (!ts.isImplicitCastValid(term.type(), ts.Boolean()))
			throw new SemanticException("Cannot build constraint from expression |" + term + "| of type " + term.type() + "; not a boolean.");
		
		// TODO: handle the formals.
		
		XTerm t = trans(term);
		t.setSelfConstraint(null);
		
		try {
			c.addTerm(t);
		}
		catch (XFailure e) {
			throw new SemanticException(e.getMessage());
		}
		return c;
	}

	public static XTerm translate(Receiver r, X10TypeSystem xts) throws SemanticException {
		return xts.xtypeTranslator().trans(r);
	}

	public static boolean isPureTerm(Term t) {
		boolean result = false;
		if (t instanceof Variable) {
			Variable v = (Variable) t;
			result = v.flags().isFinal();
		}
		return result;
	}

	public XLocal transThisWithoutTypeConstraint() throws SemanticException {
	    XLocal v = XTerms.makeLocal(XTerms.makeName("this"));
	    return v;
	}
	
	public XLocal transThis(Type t) throws SemanticException {
	    XLocal v = transThisWithoutTypeConstraint();
		addTypeToEnv(v, t);
		return v;
	}

	public XRoot genEQV(XConstraint c, Type t) throws SemanticException {
		XRoot v = c.genEQV();
		addTypeToEnv(v, t);
		return v;
	}
	
	public XRoot genEQV(XConstraint c, Type t, boolean hidden) throws SemanticException {
		XRoot v = c.genEQV(hidden);
		addTypeToEnv(v, t);
		return v;
	}
	
	public XVar genEQV(XConstraint c, boolean hidden) throws SemanticException {
		XVar v = c.genEQV(hidden);

		return v;
	}

	public XConstraint binaryOp(Binary.Operator op, XConstraint cl, XConstraint cr) {
		return null;
	}
	
	public XConstraint unaryOp(Unary.Operator op, XConstraint ca) {
		return null;
	}
}
