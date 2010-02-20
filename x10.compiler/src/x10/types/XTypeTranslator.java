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
import polyglot.types.LocalInstance;
import polyglot.types.MethodInstance;
import polyglot.types.Name;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.Types;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import x10.ast.Contains;
import x10.ast.Here;
import x10.ast.ParExpr;
import x10.ast.SubtypeTest;
import x10.ast.Tuple;
import x10.ast.X10Field_c;
import x10.ast.X10Special;
import x10.constraint.XEQV_c;
import x10.constraint.XEquals;
import x10.constraint.XFailure;
import x10.constraint.XLit;
import x10.constraint.XLit_c;
import x10.constraint.XLocal;
import x10.constraint.XName;
import x10.constraint.XNameWrapper;
import x10.constraint.XRef_c;
import x10.constraint.XRoot;
import x10.constraint.XTerm;
import x10.constraint.XTerms;
import x10.constraint.XVar;
import x10.types.constraints.CConstraint;
import x10.types.constraints.CConstraint_c;
import x10.types.constraints.SubtypeConstraint_c;
import x10.types.constraints.TypeConstraint;
import x10.types.constraints.TypeConstraint_c;
import x10.types.constraints.XConstrainedTerm;
import x10.types.matcher.Subst;
import x10.util.Synthesizer;

/**
 * Translate from a Expr or TypeNode to a CConstraint term that can be serialized.
 * @author nystrom
 */
public class XTypeTranslator {
	public static final boolean THIS_VAR = true;
	
    private final X10TypeSystem ts;

	public XTypeTranslator(X10TypeSystem xts) {
		super();
		ts = xts;
	}

	XConstrainedTerm firstPlace;
	public XConstrainedTerm firstPlace() {
		if (firstPlace == null)
			firstPlace = makePlace(0, "FIRST_PLACE");
		return firstPlace;
	}
	/**
	 * We should think of a place term as representing a set of places.
	 * firstPlace represents {Place.places(0)}.
	 * globalPlace represents the set of all places.
	 * From the type checkers point of view code is typechecked in a context in which the
	 * current place is constrained to be any place in this set.
	 */
	XConstrainedTerm globalPlace;
	public XConstrainedTerm globalPlace() {
		if (globalPlace == null)
			globalPlace = XConstrainedTerm.make(XTerms.GLOBAL_PLACE);
		return globalPlace;
	}
	private XConstrainedTerm makePlace(int i, String placeName) {
		CConstraint c = new CConstraint_c();
		X10FieldInstance fi = null;
		Type type = ts.Place();
		 CConstraint c2 = new CConstraint_c();
		 try {
			 XTerm id = Synthesizer.makeProperty(ts.Place(), c2.self(), "id");
			 if (id == null)
			     return null;
			 c.addBinding(id, XTerms.makeLit(i));
			 type = X10TypeMixin.xclause(type, c);
		 } catch (XFailure z) {
			 // wont happen
		 }
		try {
			Context con = ts.emptyContext();
			fi = (X10FieldInstance) ts.findField(ts.Place(), 
					ts.FieldMatcher(ts.Place(), Name.make(placeName), con));
		}
		catch (SemanticException e) {
			// ignore
		}

		try {
			XTerm term = trans(c, trans(ts.Place()), fi, type);
			return XConstrainedTerm.make(term, c2);
		} catch (SemanticException z) {
			// wont happen
		}
		return null;
	}
	// TODO: vj 08/11/09 -- why does this do nothing? 
	public void addTypeToEnv(XTerm self, final Type t) /*throws SemanticException*/ {
	}

	private XTerm trans(CConstraint c, Unary t, X10Context xc) throws SemanticException {
		if (t.operator() == Unary.NOT)
			return XTerms.makeNot(trans(c, t.expr(), xc));
		XTerm v = XTerms.makeAtom(XTerms.makeName(t.operator()), trans(c, t.expr(), xc));
		addTypeToEnv(v, t.type());
		return v;
	}
	
	public XVar trans(CConstraint c, XVar target, FieldInstance fi) throws SemanticException {
	    String string = Types.get(fi.def().container()) + "#" + fi.name().toString();
	    XVar v = XTerms.makeField(target, XTerms.makeName(fi.def(), string));
	    addTypeToEnv(v, fi.type());
	    return v;
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
	
	public XTerm trans(CConstraint c, XTerm target, MethodInstance fi, Type t) throws SemanticException {
	    assert X10Flags.toX10Flags(fi.flags()).isProperty() && fi.formalTypes().size() == 0;
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
	
	public XTerm trans(CConstraint c, XTerm target, FieldInstance fi, Type t) throws SemanticException {
		XTerm v;
		//XName field = XTerms.makeName(fi.def(), Types.get(fi.def().container()) + "#" + fi.name().toString());
		XName field = XTerms.makeName(fi.def(),  fi.name().toString());
		if (fi.flags().isStatic()) {
		    Type container = Types.get(fi.def().container());
		    container = X10TypeMixin.baseType(container);
		    if (container instanceof X10ClassType) {
		        target  = XTerms.makeLit(((X10ClassType) container).fullName());
		    }
		    else {
		        throw new SemanticException("Cannot translate a static field of non-class type " + container + ".");
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
	
	private XTerm trans(CConstraint c, Field t, X10Context xc) throws SemanticException {
		XTerm receiver = trans(c, t.target(), xc);
		if (receiver == null)
			return null;
		return trans(c, receiver, t.fieldInstance(), t.type());
	}

	private XTerm trans(CConstraint c, X10Special t, X10Context xc0) throws SemanticException {
	    X10Context xc = xc0;
		if (t.kind() == X10Special.SELF) {
		        if (c == null)
		            throw new SemanticException("Cannot refer to self outside a dependent clause.");
			XVar v = (XVar) c.self().clone();
			addTypeToEnv(v, t.type());
			return v;
		}
		else {
		    TypeNode tn = t.qualifier();
		    if (tn != null) {
		        Type q = X10TypeMixin.baseType(tn.type());
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
		                xc = (X10Context) xc.pop();
		            }
		        }
		    }
		    XRoot thisVar = xc == null ? null : xc.thisVar();
		    for (X10Context outer = (X10Context) xc.pop();
		    outer != null && thisVar == null;
		    outer = (X10Context) outer.pop())
		    {
		        thisVar = outer.thisVar();
		    }
		    if (thisVar == null) {
		        SemanticException e = new SemanticException("Cannot refer to |this| from the context "
		        		+ xc);
		        if (true)
		        throw new InternalCompilerError(e.getMessage());
		        throw e;
		    }
		    // vj: Need to set the thisVar for the constraint.
		    if (c != null)
		    	c.setThisVar(thisVar);
		    return thisVar;
		}
	}

	public CConstraint normalize(CConstraint c, X10Context xc) {
		CConstraint result = new CConstraint_c();
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
	public XLocal trans(LocalInstance li) throws SemanticException {
		return trans(li, li.type());
	}
	
	public XLocal trans(LocalInstance li, Type t) throws SemanticException {
		XLocal v = XTerms.makeLocal(XTerms.makeName(li.def(), li.name().toString()));
		addTypeToEnv(v, t);
		return v;
	}

	private XLocal trans(CConstraint c, Local t) throws SemanticException {
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
	
	public static Type subst(Type t, XTerm y, XRoot x) throws SemanticException {
	    return Subst.subst(t, y, x);
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
	
	public static class XTypeLit_c extends XLit_c {
	    private XTypeLit_c(Type l) {
		super(l);
	    }

	    public Type type() {
	    return (Type) val;
	    }

	    public boolean hasVar(XVar v) {
	    return X10TypeMixin.hasVar(type(), v);
	    }

	    public XTerm subst(XTerm y, XRoot x, boolean propagate) {
		XTypeLit_c n = (XTypeLit_c) super.subst(y, x, propagate);
		if (n == this) n = (XTypeLit_c) clone();
		try {
		    n.val = Subst.subst(type(), y, x);
		}
		catch (SemanticException e) {
		    return n;
		}
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
	public XTerm transHere(X10Context xc) {
	//	if (here == null) here = new HereToken();
	//	return XTerms.makeLit(here);
		// return xc.currentPlaceTerm().term();
		return XTerms.HERE;
	}
	
	public XLit trans(Lit t) {
		return XTerms.makeLit(t.constantValue());
	}

	private void transType(TypeConstraint c, Binary t, X10Context xc) throws SemanticException {
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

	private void transType(TypeConstraint c, Expr t, X10Context xc) throws SemanticException {
	    if (t instanceof Binary) {
	        transType(c, (Binary) t, xc);
	    }
	    else if (t instanceof ParExpr) {
	        transType(c, ((ParExpr) t).expr(), xc);
	    }
	    else if (t instanceof SubtypeTest) {
	        transType(c, (SubtypeTest) t, xc);
	    }
	    else {
	        throw new SemanticException("Cannot translate " + t + " into a type constraint.", t.position());
	    }
	}
	

        private void transType(TypeConstraint c, SubtypeTest t, X10Context xc) throws SemanticException {
                TypeNode left = t.subtype();
                TypeNode right = t.supertype();
                c.addTerm(new SubtypeConstraint_c(left.type(), right.type(), t.equals()));
        }

        private XTerm simplify(Binary rb, XTerm v) {
        	XTerm result = v;
        	Expr r1 = rb.left();
        	Expr r2  = rb.right();

        	// Determine if their types force them to be equal or disequal.
        	
        	CConstraint c1 = X10TypeMixin.xclause(r1.type()).copy();
        	XVar x = XTerms.makeUQV();
        	try {
        		c1.addSelfBinding(x);
        	} catch (XFailure z) {
        		// cant happen
        	}
        	CConstraint c2 = X10TypeMixin.xclause(x, r2.type()).copy();
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
	
	private XTerm trans(CConstraint c, Binary t, X10Context xc) throws SemanticException {
	    Expr left = t.left();
	    Expr right = t.right();
	    XTerm v = null;
	    XTerm lt = trans(c, left, xc);
	    XTerm rt = trans(c, right, xc);
	    Operator op = t.operator();
	    if (lt == null || rt == null)
	        throw new SemanticException("Cannot translate " + t + " to constraint term.");
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
	    else  {
			v = XTerms.makeAtom(XTerms.makeName(t.operator()), lt, rt);
		}
		addTypeToEnv(v, t.type());
		return v;
	}
	
	private XTerm trans(CConstraint c, Tuple t, X10Context xc) throws SemanticException {
	    List<XTerm> terms = new ArrayList<XTerm>();
	    for (Expr e : t.arguments()) {
		terms.add(trans(c, e, xc));
	    }
	    return XTerms.makeAtom(XTerms.makeName("tuple"), terms);
	}
	
	private XTerm trans(CConstraint c, Contains t, X10Context xc) throws SemanticException {
	    Expr left = t.item();
	    Expr right = t.collection();
	    boolean containsAll = t.isSubsetTest();
	    if (containsAll)
		return XTerms.makeAtom(XTerms.makeName("subset"), trans(c, left, xc), trans(c, right, xc));
	    else
		return XTerms.makeAtom(XTerms.makeName("in"), trans(c, left, xc), trans(c, right, xc));
	}
	
	private XTerm trans(CConstraint c, Call t, X10Context xc) throws SemanticException {
		X10MethodInstance xmi = (X10MethodInstance) t.methodInstance();
		Flags f = xmi.flags();
		if (X10Flags.toX10Flags(f).isProperty()) {
			XTerm r = trans(c, t.target(), xc);
			// FIXME: should just return the atom, and add atom==body to the real clause of the class
			// FIXME: fold in class's real clause constraints on parameters into real clause of type parameters
			XTerm body = xmi.body();

			if (body == null) {
				// hardwire s.at(t) for an interface
				// return s.home = t is Place ? t : t.home

				if (xmi.name().equals(Name.make("at"))
						&& ts.typeEquals(xmi.def().container().get(), ts.Any(), xc)
						&& t.arguments().size()==1) {
					FieldInstance fi = ts.findField(ts.Object(), ts.FieldMatcher(ts.Object(), 
							ts.homeName(), xc));
					XTerm lhs =  trans(c, r, fi, ts.Place());

					// replace by r.home == arg0 or r.home == arg0.home
					Expr arg = t.arguments().get(0);
					XTerm y = trans(c, arg, xc);
					if (y == null)
						throw new SemanticException("Cannot translate " + arg + " to a constraint term.",
								arg.position()
								);
					y = ts.isSubtype(xmi.formalTypes().get(0), ts.Place(), xc) 
					?   y : trans(c, y, fi, ts.Place());

					body = XTerms.makeEquals(lhs, y);

				}
				//System.err.println("Golden...XTypeTranslator: translated " + t + " to " + body);
			}
			if (body != null) {
				if (xmi.x10Def().thisVar() != null && t.target() instanceof Expr) {
					//XName This = XTerms.makeName(new Object(), Types.get(xmi.def().container()) + "#this");
					//body = body.subst(r, XTerms.makeLocal(This));
					body = body.subst(r, xmi.x10Def().thisVar());
				}
				for (int i = 0; i < t.arguments().size(); i++) {
					//XRoot x = (XRoot) X10TypeMixin.selfVarBinding(xmi.formalTypes().get(i));
					//XRoot x = (XRoot) xmi.formalTypes().get(i);
					XRoot x = (XRoot) XTerms.makeLocal(new XNameWrapper(xmi.formalNames().get(i).def()));
					XTerm y = trans(c, t.arguments().get(i), xc);
					if (y == null)
						assert y != null : "XTypeTranslator: translation of arg " + i + " of " + t + " yields null (pos=" 
						+ t.position() + ")";
					body = body.subst(y, x);
				}
				//System.err.println("Golden...XTypeTranslator 2: translating" + t + " to " + body);
				addTypeToEnv(body, xmi.returnType());
				return body;
			}
			else {

				//System.err.println("Golden...XTypeTranslator 3: translating" + t);
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
				else {

					List<XTerm> terms = new ArrayList<XTerm>();
					terms.add(r);
					for (Expr e : t.arguments()) {
						terms.add(trans(c, e, xc));
					}
					XTerm v = XTerms.makeAtom(XTerms.makeName(xmi, xmi.name().toString()), terms);
					addTypeToEnv(v, xmi.returnType());
					return v;
				}
			}
		}
		Type type =   t.type();
		return X10TypeMixin.selfVarBinding(type); // maybe null.

//	    throw new SemanticException("Cannot translate call |" + t + "| into a constraint; it must be a property method call.");
	}

	private XTerm trans(CConstraint c, Variable term, X10Context xc) throws SemanticException {
		//Report.report(1, "TypeTranslator: translating Variable " + term);
		if (term instanceof Field)
			return trans(c, (Field) term, xc);
		if (term instanceof X10Special)
			return trans(c, (X10Special) term, xc);
		if (term instanceof Local)
			return trans(c, (Local) term);

		return null;
//		throw new SemanticException("Cannot translate variable |" + term + "| into a constraint; it must be a field, local, this, or self.");
	}
	
	public XTerm trans(CConstraint c, Receiver term, X10Context xc)  {
		// Report.report(1, "TypeTranslator: translating Receiver " + term);
		try {
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
		if (term instanceof Expr) {
			Expr e = (Expr) term;
			if (e.isConstant())
				return XTerms.makeLit(e.constantValue());
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
		if (term instanceof Contains)
		    return trans(c, (Contains) term, xc);
		if (term instanceof TypeNode)
		    return trans(c, (TypeNode) term);
		if (term instanceof ParExpr)
			return trans(c, ((ParExpr) term).expr(), xc);
		} catch (SemanticException z) {
			// fall through
		}
		return null;
//		throw new SemanticException("Cannot translate type or expression |" + term + "| (" + term.getClass().getName() + ")" + " to a term.");
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
	public CConstraint constraint(List<Formal> formals, Expr term, X10Context xc) throws SemanticException {
            CConstraint c = new CConstraint_c();
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
                throw new SemanticException(e.getMessage());
            }
            return c;
	}
	
	public TypeConstraint typeConstraint(List<Formal> formals, Expr term, X10Context xc) throws SemanticException {
	    TypeConstraint c = new TypeConstraint_c();
        if (term == null)
        	return c;
        
        if (! term.type().isBoolean())
        	throw new SemanticException("Cannot build constraint from expression |" + term + "| of type " + term.type() + "; not a boolean.");
        
        // TODO: handle the formals.
        
        transType(c, term, xc);

        return c;
	}
	
	public static XTerm translate(CConstraint c, Receiver r, X10TypeSystem xts, X10Context xc) throws SemanticException {
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
