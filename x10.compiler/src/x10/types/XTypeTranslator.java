/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.types;

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
import polyglot.ast.Binary.Operator;
import polyglot.types.ClassDef;
import polyglot.types.CodeDef;
import polyglot.types.Context;
import polyglot.types.FieldInstance;
import polyglot.types.Flags;
import polyglot.types.LocalDef;
import polyglot.types.LocalInstance;
import polyglot.types.Name;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.Types;
import polyglot.types.TypeSystem;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import x10.ast.Here;
import x10.ast.ParExpr;
import x10.ast.SubtypeTest;
import x10.ast.Tuple;
import x10.ast.X10Cast;
import x10.ast.X10Field_c;
import x10.ast.X10Special;
import x10.ast.HasZeroTest;
import x10.constraint.XEQV;
import x10.constraint.XEquals;
import x10.constraint.XFailure;
import x10.constraint.XLit;
import x10.constraint.XLocal;
import x10.constraint.XName;
import x10.constraint.XNameWrapper;
import x10.constraint.XVar;
import x10.constraint.XTerm;
import x10.constraint.XTerms;
import x10.constraint.XVar;
import x10.errors.Errors;
import x10.types.checker.PlaceChecker;
import x10.types.constraints.CConstraint;
import x10.types.constraints.CConstraint;
import x10.types.constraints.SubtypeConstraint;
import x10.types.constraints.TypeConstraint;
import x10.types.constraints.XConstrainedTerm;
import x10.types.matcher.Subst;
import x10.util.Synthesizer;

/**
 * Translate from a Expr or TypeNode to a CConstraint term that can be serialized.
 * @author nystrom
 */
public class XTypeTranslator {
	public static final boolean THIS_VAR = true;
	
    private final TypeSystem ts;

	public XTypeTranslator(TypeSystem xts) {
		super();
		ts = xts;
	}

	// TODO: vj 08/11/09 -- why does this do nothing? 
	public void addTypeToEnv(XTerm self, final Type t) {
	}

	private XTerm trans(CConstraint c, Unary t, Context xc) {
		XTerm v = null;
		if (t.operator() == Unary.NOT) {
		   // v = XTerms.makeNot(trans(c, t.expr(), xc));
		} else {
		    v = XTerms.makeAtom(XTerms.makeName(t.operator()), trans(c, t.expr(), xc));
		    return null;
		}		    
		addTypeToEnv(v, t.type());
		return v;
	}

	public XVar trans(CConstraint c, XVar target, FieldInstance fi) {
		XName field = XTerms.makeName(fi.def(),  fi.name().toString());
		//String string = Types.get(fi.def().container()) + "#" + fi.name().toString();
		XVar v = XTerms.makeField(target,field);
		addTypeToEnv(v, fi.type());
		return v;
	}

	public XTerm trans(XTerm target, FieldInstance fi) {
		return trans(new CConstraint(), target, fi);
	}
	public XTerm trans(CConstraint c, XTerm target, FieldInstance fi)  {
		if (fi == null)
			return null;
		try {
		    return trans(c, target, fi, fi.type());
		} catch (SemanticException z) {
			return null;
		}
	}
	
	public XTerm trans(CConstraint c, XTerm target, MethodInstance fi, Type t) {
	    assert fi.flags().isProperty() && fi.formalTypes().size() == 0;
	    XTerm v;
	    XName field = XTerms.makeName(fi.def(), Types.get(fi.def().container()) + "#" + fi.name().toString() + "()");
	    if (target instanceof XVar) {
	        v = XTerms.makeField((XVar) target, field);
	    }
	    else {
	        v = XTerms.makeAtom(field, target);
	    }
	    addTypeToEnv(v, t);
	    return v;
	}
	
	public XTerm trans(XTerm target, FieldInstance fi, Type t) {
	    try {
	        return trans(new CConstraint(), target, fi, t);
	    } catch (SemanticException z) {
	        return null;
	    }
	}
	
	public static final Object FAKE_KEY = new Object();
	public XTerm transFakeField(CConstraint c, XTerm target, String name)  {
		//XName field = XTerms.makeName(fi.def(), Types.get(fi.def().container()) + "#" + fi.name().toString());
		XName field = XTerms.makeName(FAKE_KEY,  name);
		return XTerms.makeFakeField((XVar) target, field);
	}
	public XTerm trans(CConstraint c, XTerm target, FieldInstance fi, Type t) throws SemanticException {
		XTerm v;
		//XName field = XTerms.makeName(fi.def(), Types.get(fi.def().container()) + "#" + fi.name().toString());
		XName field = XTerms.makeName(fi.def(), fi.name().toString());
		if (fi.flags().isStatic()) {
		    Type container = Types.get(fi.def().container());
		    container = Types.baseType(container);
		    if (container instanceof X10ClassType) {
		        target  = XTerms.makeLit(((X10ClassType) container).fullName());
		    }
		    else {
		        throw new Errors.CannotTranslateStaticField(container, fi.position());
		    }
		}
		if (target instanceof XVar) {
			v = XTerms.makeField((XVar) target, field);
		    }
		    else {
		        v = XTerms.makeAtom(field, target);
		    }
		addTypeToEnv(v, t);
		return v;
	}
	
	private XTerm trans(CConstraint c, Field t, Context xc) {
		XTerm receiver = trans(c, t.target(), xc);
		if (receiver == null)
			return null;
		try {
		    return trans(c, receiver, t.fieldInstance(), t.type());
		} catch (SemanticException z) {
		    return null;
		}
	}

	private XTerm trans(CConstraint c, X10Special t, Context xc0) {
		Context xc = xc0;
		if (t.kind() == X10Special.SELF) {
			if (c == null) {
			    //throw new SemanticException("Cannot refer to self outside a dependent clause.");
			    return null;
			}
			XVar v = (XVar) c.self().clone();
			addTypeToEnv(v, t.type());
			return v;
		}
		else {
		    TypeNode tn = t.qualifier();
		    if (tn != null) {
		        Type q = Types.baseType(tn.type());
		        if (q instanceof X10ClassType) {
		            X10ClassType ct = (X10ClassType) q;
		            while (xc != null) {
		                if (xc.inSuperTypeDeclaration()) {
		                    if (xc.supertypeDeclarationType() == ct.def())
		                        break;
		                }
		                else if (xc.currentClassDef() == ct.def()) {
		                    break;
		                }
		                xc = (Context) xc.pop();
		            }
		        }
		    }
		    // why is this code not in X10Context_c.thisVar()?
		    XVar thisVar = null;
		    for (Context outer = xc; outer != null && thisVar == null; outer = outer.pop()) {
		        thisVar = outer.thisVar();
		    }
		    if (thisVar == null) {
		        SemanticException e = new SemanticException("Cannot refer to |this| from the context " + xc);
		        return null;
		    }
		    // vj: Need to set the thisVar for the constraint.
		    if (c != null)
		    	c.setThisVar(thisVar);
		    return thisVar;
		}
	}

	public CConstraint normalize(CConstraint c, Context xc) {
		CConstraint result = new CConstraint();
		for (XTerm term : c.extConstraints()) {
			try {
			if (term instanceof XEquals) {
				XEquals xt = (XEquals) term;
				XTerm right = xt.right();
				if (right instanceof XEquals) {
					XEquals xright = (XEquals) right;
					XTerm t1 = xright.left();
					XTerm t2 = xright.right();
						if (c.entails(t1, t2)) {
							result.addBinding(xt.left(), XTerms.TRUE);
						} else 
						if (c.disEntails(t1, t2)) {
							result.addBinding(xt.left(), XTerms.FALSE);
						} else
							result.addBinding(xt.left(), xt.right());
				}
			} else 
				result.addTerm(term);
			} catch (XFailure t) {
				
			}
		}
		return result;
	}
	public XLocal trans(LocalInstance li) {
		return trans(li, li.type());
	}
	
	public XLocal trans(LocalInstance li, Type t) {
		XLocal v = XTerms.makeLocal(XTerms.makeName(li.def(), li.name().toString()));
		addTypeToEnv(v, t);
		return v;
	}

	private XLocal trans(CConstraint c, Local t) {
		return trans(t.localInstance(), t.type());
	}

	private XTerm trans(CConstraint c, TypeNode t) {
		return trans(t.type());
	}
	
	public XLocal transTypeParam(ParameterType t) {
		return XTerms.makeLocal(XTerms.makeName(t));
	}
	
	public XTerm trans(Type t) {
		if (t instanceof ParameterType)
			return transTypeParam((ParameterType) t);
//		if (t instanceof X10ClassType)
//		    return transClassType((X10ClassType) t);
//		if (t instanceof ConstrainedType)
//		    return transConstrainedType((ConstrainedType) t);
		if (t instanceof MacroType) {
		    MacroType pt = (MacroType) t;
		    return trans(pt.definedType());
		}
		return new XTypeLit_c(t);
//		return XTerms.makeLit(t);
	}
	
	public static Type subst(Type t, XTerm y, XVar x) throws SemanticException {
	    return Subst.subst(t, y, x);
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
	
	public static class XTypeLit_c extends XLit {
	    private static final long serialVersionUID = -1222245257474719757L;

	    private XTypeLit_c(Type l) {
		super(l);
	    }

	    public Type type() {
	    return (Type) val;
	    }

	    public boolean hasVar(XVar v) {
	    return Types.hasVar(type(), v);
	    }

	    public XTerm subst(XTerm y, XVar x, boolean propagate) {
		XTypeLit_c n = (XTypeLit_c) super.subst(y, x, propagate);
		Type newVal = n.type();
		try {
		    newVal = Subst.subst(type(), y, x);
		} catch (SemanticException e) { }
		if (newVal == n.type())
		    return n;
		if (n == this) n = (XTypeLit_c) clone();
		n.val = newVal;
		return n;
	    }
	}

/*	static class HereToken {
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
	*/
	public XTerm transHere(Context xc) {
	//	if (here == null) here = new HereToken();
	//	return XTerms.makeLit(here);
		// return xc.currentPlaceTerm().term();
		return PlaceChecker.here();
	}
	
	public XLit trans(Lit t) {
		return XTerms.makeLit(t.constantValue());
	}

	private void transType(TypeConstraint c, Binary t, Context xc) throws SemanticException {
	    Expr left = t.left();
	    Expr right = t.right();
	    XTerm v;
	    
	    if (t.operator() == Binary.COND_AND 
	    		|| (t.operator() == Binary.BIT_AND && ts.isImplicitCastValid(t.type(), ts.Boolean(), xc))) {
	        transType(c, left, xc);
	        transType(c, right, xc);
	    }
	    else {
	        throw new SemanticException("Cannot translate " + t + " into a type constraint.", t.position());
	    }
	}

	private void transType(TypeConstraint c, Expr t, Context xc) throws SemanticException {
	    if (t instanceof Binary) {
	        transType(c, (Binary) t, xc);
	    }
	    else if (t instanceof ParExpr) {
	        transType(c, ((ParExpr) t).expr(), xc);
	    }
	    else if (t instanceof SubtypeTest) {
	        transType(c, (SubtypeTest) t, xc);
        } else if (t instanceof HasZeroTest) {
            transType(c, (HasZeroTest) t, xc);
	    }
	    else {
	        throw new SemanticException("Cannot translate " + t + " into a type constraint.", t.position());
	    }
	}
	

	private void transType(TypeConstraint c, HasZeroTest t, Context xc) throws SemanticException {
	    TypeNode left = t.parameter();
	    c.addTerm(new SubtypeConstraint(left.type(), null, SubtypeConstraint.Kind.HASZERO));
	}
	private void transType(TypeConstraint c, SubtypeTest t, Context xc) throws SemanticException {
	    TypeNode left = t.subtype();
	    TypeNode right = t.supertype();
	    c.addTerm(new SubtypeConstraint(left.type(), right.type(), t.equals()));
	}

	private XTerm simplify(Binary rb, XTerm v) {
	    XTerm result = v;
	    Expr r1 = rb.left();
	    Expr r2  = rb.right();

	    // Determine if their types force them to be equal or disequal.

	    CConstraint c1 = Types.xclause(r1.type()).copy();
	    XVar x = XTerms.makeUQV();
	    try {
	        c1.addSelfBinding(x);
	    } catch (XFailure z) {
	        throw new InternalCompilerError("Unexpected failure", z); // can't happen
	    }
	    CConstraint c2 = Types.xclause(x, r2.type()).copy();
	    if (rb.operator()== Binary.EQ) {
	        try {
	            if (! c1.addIn(c2).consistent())
	                result = XTerms.FALSE;
	            if (c1.entails(c2) && c2.entails(c1)) {
	                result = XTerms.TRUE;
	            }
	        } catch (XFailure z) {
	            result = XTerms.FALSE;
	        }
	    }
	    return result;
	}

	private XTerm trans(CConstraint c, Binary t, Context xc) {
	    Expr left = t.left();
	    Expr right = t.right();
	    XTerm v = null;
	    XTerm lt = trans(c, left, xc);
	    XTerm rt = trans(c, right, xc);
	    Operator op = t.operator();
	    if (lt == null || rt == null)
	        return null;
	    if (op == Binary.EQ || op == Binary.NE) {
	    	if (right instanceof ParExpr) {
	    		right = ((ParExpr)right).expr();
	    	}
	    	if (right instanceof Binary && ((Binary) right).operator() == Binary.EQ) {
	    		rt = simplify((Binary) right, rt);
	    	}
	    	if (left instanceof Binary && ((Binary) right).operator() == Binary.EQ) {
	    		lt = simplify((Binary) left, lt);
	    	}
	    	
	    		v = op == Binary.EQ ? XTerms.makeEquals(lt, rt): XTerms.makeDisEquals(lt, rt);
	    }
	    else if (t.operator() == Binary.COND_AND 
	    		|| (t.operator() == Binary.BIT_AND && ts.isImplicitCastValid(t.type(), ts.Boolean(), xc))) {
	        v = XTerms.makeAnd(lt, rt);
	    }
	    else if (t.operator() == Binary.IN) {
	        v = XTerms.makeAtom(XTerms.makeName(t.operator()), lt, rt);
	    }
	    else  {
	        v = XTerms.makeAtom(XTerms.makeName(t.operator()), lt, rt);
	        return null;
	    }
	    addTypeToEnv(v, t.type());
	    return v;
	}

	private XTerm trans(CConstraint c, Tuple t, Context xc) {
	    List<XTerm> terms = new ArrayList<XTerm>();
	    for (Expr e : t.arguments()) {
	        XTerm v = trans(c, e, xc);
	        if (v == null)
	            return null;
	        terms.add(v);
	    }
	    return XTerms.makeAtom(XTerms.makeName("tuple"), terms);
	}
	
	private XTerm trans(CConstraint c, Call t, Context xc) {
		MethodInstance xmi = (MethodInstance) t.methodInstance();
		Flags f = xmi.flags();
		if (f.isProperty()) {
			XTerm r = trans(c, t.target(), xc);
			if (r == null)
				return null;
			// FIXME: should just return the atom, and add atom==body to the real clause of the class
			// FIXME: fold in class's real clause constraints on parameters into real clause of type parameters
			XTerm body = xmi.body();

			if (body == null) {
				// hardwire s.at(t) for an interface
				// return s.home = t is Place ? t : t.home
				// stub out for orthogonal locality
				//body  = PlaceChecker.rewriteAtClause(c, xmi, t, r, xc);
			}
			if (body != null) {
				if (xmi.x10Def().thisVar() != null && t.target() instanceof Expr) {
					//XName This = XTerms.makeName(new Object(), Types.get(xmi.def().container()) + "#this");
					//body = body.subst(r, XTerms.makeLocal(This));
					body = body.subst(r, xmi.x10Def().thisVar());
				}
				for (int i = 0; i < t.arguments().size(); i++) {
					//XVar x = (XVar) X10TypeMixin.selfVarBinding(xmi.formalTypes().get(i));
					//XVar x = (XVar) xmi.formalTypes().get(i);
					XVar x = (XVar) XTerms.makeLocal(new XNameWrapper<LocalDef>(xmi.formalNames().get(i).def()));
					XTerm y = trans(c, t.arguments().get(i), xc);
					if (y == null)
						assert y != null : "XTypeTranslator: translation of arg " + i + " of " + t + " yields null (pos=" 
						+ t.position() + ")";
					body = body.subst(y, x);
				}
				addTypeToEnv(body, xmi.returnType());
				return body;
			}

			if (t.arguments().size() == 0) {
				XName field = XTerms.makeName(xmi.def(), Types.get(xmi.def().container()) + "#" + xmi.name() + "()");
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
			List<XTerm> terms = new ArrayList<XTerm>();
			terms.add(r);
			for (Expr e : t.arguments()) {
				XTerm v = trans(c, e, xc);
				if (v == null)
				    return null;
				terms.add(v);
			}
			XTerm v = XTerms.makeAtom(XTerms.makeName(xmi, xmi.name().toString()), terms);
			addTypeToEnv(v, xmi.returnType());
			return v;
		}
		Type type = t.type();
		return Types.selfVarBinding(type); // maybe null.
	    //throw new SemanticException("Cannot translate call |" + t + "| into a constraint; it must be a property method call.");
	}

	private XTerm trans(CConstraint c, Variable term, Context xc) {
		//Report.report(1, "TypeTranslator: translating Variable " + term);
		if (term instanceof Field)
			return trans(c, (Field) term, xc);
		if (term instanceof X10Special)
			return trans(c, (X10Special) term, xc);
		if (term instanceof Local)
			return trans(c, (Local) term);
		return null;
		//throw new SemanticException("Cannot translate variable |" + term + "| into a constraint; it must be a field, local, this, or self.");
	}
	
	public XTerm trans(CConstraint c, Receiver term, Context xc)  {
		// Report.report(1, "TypeTranslator: translating Receiver " + term);
		if (term == null)
			return null;
		if (term instanceof Lit)
			return trans((Lit) term);
		if (term instanceof Here)
			return transHere(xc);
		if (term instanceof Variable)
			return trans(c, (Variable) term, xc);
		if (term instanceof X10Special)
			return trans(c, (X10Special) term, xc);
		if (term instanceof Expr && ts.isUnknown(term.type()))
			return null;
		if (term instanceof Expr) {
			Expr e = (Expr) term;
			if (e.isConstant())
				return XTerms.makeLit(e.constantValue());
		}
		if (term instanceof X10Cast) {
			X10Cast cast = ((X10Cast) term);
			return trans(c, cast.expr().type(cast.type()), xc);
		}
		if (term instanceof Call) {
		    return trans(c, (Call) term, xc);
		}
		if (term instanceof Tuple) {
		    return trans(c, (Tuple) term, xc);
		}
		if (term instanceof Unary) {
			Unary u = (Unary) term;
			Expr t2 = u.expr();
			Unary.Operator op = u.operator();
			if (op == Unary.POS)
				return trans(c, t2, xc);
			return trans(c, (Unary) term, xc);
		}
		if (term instanceof Binary)
			return trans(c, (Binary) term, xc);
		if (term instanceof TypeNode)
		    return trans(c, (TypeNode) term);
		if (term instanceof ParExpr)
			return trans(c, ((ParExpr) term).expr(), xc);
		return null;
		//throw new SemanticException("Cannot translate type or expression |" + term + "| (" + term.getClass().getName() + ")" + " to a term.");
	}
	
	/**
	 * Translate an expression into a CConstraint, throwing SemanticExceptions 
	 * if this is not possible.
	 * This must be called after type-checking of Expr.
	 * @param formals TODO
	 * @param term
	 * @param xc TODO
	 * @param c
	 * @return
	 * @throws SemanticException
	 */
	public CConstraint constraint(List<Formal> formals, Expr term, Context xc) throws SemanticException {
            CConstraint c = new CConstraint();
            if (term == null)
                return c;
            
            if (! term.type().isBoolean())
                throw new SemanticException("Cannot build constraint from expression |" + term + "| of type " + term.type() + "; not a boolean.");
            
            // TODO: handle the formals.
            XTerm t = trans(c, term, xc);
            
            if (t == null)
                throw new SemanticException("Cannot build constraint from expression |" + term + "|.");
            
            try {
                c.addTerm(t);
            }
            catch (XFailure e) {
                c.setInconsistent();
               // throw new SemanticException(e.getMessage());
            }
            return c;
	}
	
	public TypeConstraint typeConstraint(List<Formal> formals, Expr term, Context xc) throws SemanticException {
	    TypeConstraint c = new TypeConstraint();
        if (term == null)
        	return c;
        
        if (! term.type().isBoolean())
        	throw new SemanticException("Cannot build constraint from expression |" + term + "| of type " + term.type() + "; not a boolean.");
        
        // TODO: handle the formals.
        
        transType(c, term, xc);

        return c;
	}
	
	public static XTerm translate(CConstraint c, Receiver r, TypeSystem xts, Context xc) throws SemanticException {
		return xts.xtypeTranslator().trans(c, r, xc);
	}

	public static boolean isPureTerm(Term t) {
		boolean result = false;
		if (t instanceof Variable) {
			Variable v = (Variable) t;
			result = v.flags().isFinal();
		}
		return result;
	}

	public XLocal transThisWithoutTypeConstraint() {
	    XLocal v = XTerms.makeLocal(XTerms.makeName("this"));
	    return v;
	}
	
	public XLocal transThis(Type t) throws SemanticException {
	    XLocal v = transThisWithoutTypeConstraint();
		addTypeToEnv(v, t);
		return v;
	}


	public CConstraint binaryOp(Binary.Operator op, CConstraint cl, CConstraint cr) {
		return null;
	}
	
	public CConstraint unaryOp(Unary.Operator op, CConstraint ca) {
		return null;
	}
}
