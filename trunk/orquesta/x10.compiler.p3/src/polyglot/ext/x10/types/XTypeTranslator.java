package polyglot.ext.x10.types;

import java.util.List;

import polyglot.ast.Binary;
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
import polyglot.ext.x10.ast.X10Special;
import polyglot.types.FieldInstance;
import polyglot.types.LocalInstance;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import x10.constraint.XConstraint;
import x10.constraint.XConstraint_c;
import x10.constraint.XFailure;
import x10.constraint.XLit;
import x10.constraint.XLocal;
import x10.constraint.XName;
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

	public void addTypeToEnv(XTerm self, Type t) throws SemanticException {
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
		    throw new SemanticException(e);
		}
	    }

	    self.setSelfConstraint(c);
	}

	public XTerm trans(Unary t) throws SemanticException {
		if (t.operator() == Unary.NOT)
			return XTerms.makeNot(trans(t.expr()));
		XTerm v = XTerms.makeAtom(XTerms.makeName(t.operator()), trans(t.expr()));
		addTypeToEnv(v, t.type());
		return v;
	}
	
	public XVar trans(XVar target, FieldInstance fi) throws SemanticException {
		XVar v = XTerms.makeField(target, XTerms.makeName(fi.def(), fi.name()));
		addTypeToEnv(v, fi.type());
		return v;
	}

	public XTerm trans(XTerm target, FieldInstance fi) throws SemanticException {
		return trans(target, fi, fi.type());
	}
	
	
	public XTerm trans(XTerm target, FieldInstance fi, Type t) throws SemanticException {
		XTerm v;
		if (target instanceof XVar) {
			v = XTerms.makeField((XVar) target, XTerms.makeName(fi.def(), fi.name()));
		}
		else {
			v = XTerms.makeAtom(XTerms.makeName(fi.def(), fi.name()), target);
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
		XLocal v = XTerms.makeLocal(XTerms.makeName(li.def(), li.name()));
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
	
	// TODO: general macro types, also need to translate back for checking <: later
	public XVar transMacroType(ParametrizedType t) {
		if (t instanceof PathType) {
			PathType pt = (PathType) t;
			XName field = XTerms.makeName(pt.property(), pt.property().name());
			return XTerms.makeField(pt.formals().get(0), field);
		}
		assert false;
		return null;
	}

	public XTerm trans(Type t) {
		if (t instanceof ParameterType)
			return transTypeParam((ParameterType) t);
		if (t instanceof ParametrizedType)
			return transMacroType((ParametrizedType) t);
		return XTerms.makeLit(t);
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
		return transSubtype(left.type(), right.type());
	}

	public XTerm transSubtype(Type ltype, Type rtype) {
		XTerm v = XTerms.makeAtom(XTerms.makeName("<:"), trans(ltype), trans(rtype));
		return v;
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

	public XLocal transThis(Type t) throws SemanticException {
		XLocal v = XTerms.makeLocal(XTerms.makeName("this"));
		addTypeToEnv(v, t);
		return v;
	}

	public XVar genEQV(XConstraint c, Type t) throws SemanticException {
		XVar v = c.genEQV();
		addTypeToEnv(v, t);
		return v;
	}
	
	public XVar genEQV(XConstraint c, Type t, boolean hidden) throws SemanticException {
		XVar v = c.genEQV(hidden);
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
