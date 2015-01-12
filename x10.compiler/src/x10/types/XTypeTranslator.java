/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

package x10.types;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

import polyglot.ast.Binary;
import polyglot.ast.Call;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.Formal;
import polyglot.ast.Lit;
import polyglot.ast.Local;
import polyglot.ast.Node;
import polyglot.ast.Receiver;
import polyglot.ast.Term;
import polyglot.ast.TypeNode;
import polyglot.ast.Typed;
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
import polyglot.types.MethodDef;
import polyglot.types.Name;
import polyglot.types.QName;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.Types;
import polyglot.types.TypeSystem;
import polyglot.types.Def;
import polyglot.types.Ref;
import polyglot.types.ClassType;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import x10.ast.Closure_c;
import x10.ast.Here;
import x10.ast.Here_c;
import x10.ast.IsRefTest;
import x10.ast.ParExpr;
import x10.ast.SubtypeTest;
import x10.ast.Tuple;
import x10.ast.X10Cast;
import x10.ast.X10Field_c;
import x10.ast.X10Special;
import x10.ast.HasZeroTest;
import x10.constraint.XEQV;
import x10.constraint.XFailure;
import x10.constraint.XLit;
import x10.constraint.XLocal;
import x10.constraint.XUQV;
import x10.constraint.XVar;
import x10.constraint.XTerm;
import x10.constraint.XField;
import x10.errors.Errors;
import x10.errors.Errors.IllegalConstraint;
import x10.extension.X10Ext;
import x10.types.checker.PlaceChecker;
import x10.types.constants.ClosureValue;
import x10.types.constants.ConstantValue;
import x10.types.constants.StringValue;
import x10.types.constraints.CConstraint;
import x10.types.constraints.CLocal;
import x10.types.constraints.ConstraintManager;
import x10.types.constraints.SubtypeConstraint;
import x10.types.constraints.TypeConstraint;
import x10.types.constraints.XConstrainedTerm;
import x10.types.constraints.CAtom;
import x10.types.constraints.CField;
import x10.types.constraints.CThis;
import x10.types.constraints.CSelf;
import x10.types.matcher.Subst;
import x10.util.Synthesizer;
import x10.types.constraints.XTypeLit;
import x10.types.constraints.xnative.CNativeLocal;
import x10.types.constraints.xnative.QualifiedVar;

/**
 * This is the bridge from Expr or TypeNode to a CConstraint. The CConstraint
 * generated may keep a reference to type objects obtained from the Expr or TypeNode.
 *  
 * @author nystrom
 * @author vj
 */
public class XTypeTranslator {
    public static final boolean THIS_VAR = true;

    private final TypeSystem ts;

    public XTypeTranslator(TypeSystem xts) {
        super();
        ts = xts;
    }

    //public static XTerm translate(CConstraint c, Receiver r, TypeSystem xts, Context xc)  {
    //    return xts.xtypeTranslator().translate(c, r, xc);
    //}
    
    /**
     * Translate the given AST term to an XTerm using information in the constraint
     * to resolve self, and using the context to determine AST type information 
     * (e.g. for Specials).
     * @param c --- a constraint used for context, e.g. self information. If term
     * is a this (e.g. this, Foo.this etc), then the constraint's this Var is set
     * on return. 
     * @param term -- the term to be translated
     * @param xc -- the context in which the term is to be translated
     * @return null if the translation is not possible. Caller must always check.
     * FIX: Remove the need for the constraint to be passed into translate.
     * 
     * If toplevel is true, then boolean connectives, && are permitted.
     */
    public XTerm translate(CConstraint c, Receiver term, Context xc)  throws IllegalConstraint {
    	return translate(c, term, xc, false);
    }
    public XTerm translate(CConstraint c, Receiver term, Context xc, boolean tl) throws IllegalConstraint {
        if (term == null)
            return null;
        if (term instanceof Lit)
            return translate((Lit) term);
        if (term instanceof Here_c)
            return trans(c, (Here_c) term, xc, tl);
        if (term instanceof Variable)
            return trans(c, (Variable) term, xc, tl);
        if (term instanceof X10Special)
            return trans(c, (X10Special) term, xc, tl);
        if (term instanceof Expr && ts.isUnknown(term.type())) {
            return null;
        }
        if (term instanceof Expr) {
            Expr e = (Expr) term;
            if (e.isConstant()) {
                ConstantValue cv = e.constantValue();
                if (!(cv instanceof ClosureValue)) {
                    return ConstraintManager.getConstraintSystem().makeLit(ConstantValue.toJavaObject(e.constantValue()), e.type());
                }
            }
        }
        if (term instanceof X10Cast) {
            X10Cast cast = ((X10Cast) term);
            return translate(c, cast.expr().type(cast.type()), xc, tl);
        }
        if (term instanceof Call) {
            return trans(c, (Call) term, xc, tl);
        }
        if (term instanceof Tuple) {
            return trans(c, (Tuple) term, xc, tl);
        }
        if (term instanceof Unary) {
            Unary u = (Unary) term;
            Expr t2 = u.expr();
            Unary.Operator op = u.operator();
            if (op == Unary.POS)
                return translate(c, t2, xc, tl);
            return null; // no other unary operator supported
        }
        if (term instanceof Binary)
            return trans(c, (Binary) term, xc, tl);
        if (term instanceof TypeNode)
            return trans(c, (TypeNode) term);
        if (term instanceof ParExpr)
            return translate(c, ((ParExpr) term).expr(), xc, tl);
        return null;
    }


    /**
     * Return the term var.field, where information about the field is obtained from fi.
     * The returned term carries a reference to fi.def(), and field is fi.name().toString().
     * @param var
     * @param fi
     * @return
     */
    public XVar translate(XVar var, FieldInstance fi, boolean ignore) {
        // Warning -- used to have a string that did not contain container()#.
        return ConstraintManager.getConstraintSystem().makeField(var, fi.def());
    }

    /**
     * Return the term target.field, where information about the field is obtained from fi.
     * The returned term carriers a reference to fi.def(), and field is fi.name().toString().
     * Note that if target is a formula f, then the atom field(f) is returned instead.
     * @param target
     * @param fi
     * @return
     */
    public XTerm translate(XTerm target, FieldInstance fi) {
    	return translate(target, fi, false);
    }
    XTerm translate(XTerm target, FieldInstance fi, boolean ignore) {
        if (fi == null)
            return null;
        try {
            if (fi.flags().isStatic()) {
                Type container = Types.get(fi.def().container());
                container = Types.baseType(container);
                if (container instanceof X10ClassType) {
                    target = ConstraintManager.getConstraintSystem().makeLit(((X10ClassType) container).fullName());
                }
                else {
                    throw new Errors.CannotTranslateStaticField(container, fi.position());
                }
            }
            XTerm v;
            if (target instanceof XVar) {
                v = ConstraintManager.getConstraintSystem().makeField((XVar) target, fi.def()); // hmm string was fi.name().toString(0 before.
            }
            else {
                // this is odd....?
                // TODO: Determine under what conditions is this path taken.
              
                v = ConstraintManager.getConstraintSystem().makeAtom(fi.def(), target);
            }
            return v;
        } catch (SemanticException z) {
            return null;
        }
    }


    /**
     * Return target.prop, where information about the property is obtained from fi.
     * It must be the case that fi corresponds to a property that takes no arguments. 
     *  Note that if target is a formula f, then the atom prop(f) is returned instead.
     * TODO: Determine why the name cant be just fi.name().toString().
     * @param target
     * @param fi
     * @return
     */
    public XTerm translate(XTerm target, MethodInstance mi) {
        assert mi.flags().isProperty() && mi.formalTypes().size() == 0;
     
        XTerm v;
        if (target instanceof XVar) {
            v = ConstraintManager.getConstraintSystem().makeField((XVar) target, mi.def());
        }
        else {
            // this is odd....?
            // TODO: Determine under what conditions is this path taken.
           // XName field = ConstraintManager.getConstraintSystem().makeName(mi.def(), Types.get(mi.def().container()) + "#" + mi.name().toString() + "()");
            v = ConstraintManager.getConstraintSystem().makeAtom(mi.def(), target);
        }
        // this creates an unexpanded property method call.
        return v;
    }
    static public XTerm expandSelfPropertyMethod(XTerm term) {
        return expandPropertyMethod(term,false,null,null);
    }
    // todo: merge this code with Checker.expandCall and try to get rid of ts.expandMacros
    static public XTerm expandPropertyMethod(XTerm term, boolean isThisOrSelf,
                        // these three formals help us search for a concrete implementation of the property method
                        // they can be null (then we don't search for an implementation)
                        TypeSystem ts, Context context) {
        Def aDef = null;
        XTerm[] args = null; // the first arg is the this-receiver
        if (term instanceof CAtom) {
            CAtom cAtom = (CAtom) term;
            aDef = cAtom.def();
            args = cAtom.arguments();
        }
        if (term instanceof XField) {
            XField cField = (XField) term;
            Object o = cField.field();
            if (o instanceof Def) {
                aDef = (Def) o;
                args = new XTerm[1];
                args[0] = cField.receiver();
            }
        }
        if (aDef==null || !(aDef instanceof X10MethodDef)) return term;
        XTerm receiver = args[0];
        ClassType classType = null;
        if (receiver instanceof Typed) { // this covers CThis and CNativeLocal and perhaps others
        	Type t = ((Typed)receiver).type();
        	t = Types.baseType(t);
        	if (t instanceof ClassType) {
        		classType = (ClassType) t;
        	} else {
        		assert false;
        	}
        }
        // TODO: the type of self is not available on trunk, this is fixed in the constraint branch.
        X10MethodDef def = (X10MethodDef) aDef;
        if (classType!=null && ts != null) {
            // find the correct def, and return a clone of the XTerm
            final MethodInstance method = ts.findImplementingMethod(classType, def.asInstance(), false, context);
            if (method==null) // the property is abstract in t1
                return term;
            def = (X10MethodDef) method.def();
        }
        final Ref<XTerm> bodyRef = def.body();
        if (bodyRef==null)
            return term;
        XTerm body = bodyRef.get();
        if (body==null)
            return term;
        // currently we only support nullary property methods that are not CAtoms
        List<LocalDef> formals = def.formalNames();
        if (formals.size()!=args.length-1)
            throw new InternalCompilerError("The number of arguments in the property method didn't match the property defintiion.");
        int pos=1;
        for (LocalDef formal : formals) {
            XVar x =  ConstraintManager.getConstraintSystem().makeLocal((X10LocalDef)formal);
            XTerm y = args[pos++];
            body = body.subst(y, x);
        }
        body = body.subst(receiver, def.thisVar());
        return body;
    }

    public static final Object FAKE_KEY = new Object();
    
    /**
     * A fake field is one which exists purely for compilation purposes
     * and has no run-time existence. The main example is "home". It used to exist
     * for all objects, but is now used merely to track the location of the current
     * object statically.
     * @param target
     * @param name
     * @return
     */
    public XTerm translateFakeField(XTerm target, String name)  {
        return ConstraintManager.getConstraintSystem().makeFakeField((XVar) target, Name.make(name));
    }

    /** 
     * Return an XLocal which contains a reference to the type object li.def(),
     * and whose name is li.name().
     * @param li
     * @return
     */
     public CLocal translate(LocalInstance li) {
        return ConstraintManager.getConstraintSystem().makeLocal((X10LocalDef) li.def());
       
    }
     /**
      * Return an XLit representing the literal t.
      * @param t
      * @return
      */
    public XLit translate(Lit t) {
        return ConstraintManager.getConstraintSystem().makeLit(ConstantValue.toJavaObject(t.constantValue()), t.type());
    }
    
    /**
     * Translate a type t into an XTerm. A type parameter
     * is translated into an XLocal. Other types are converted
     * into XTypeLit_c.
     * @param t
     * @return
     */
    public XTerm translate(Type t) {
        if (t instanceof ParameterType)
            return translateTypeParam((ParameterType) t);
        //  if (t instanceof X10ClassType)
        //      return transClassType((X10ClassType) t);
        //  if (t instanceof ConstrainedType)
        //      return transConstrainedType((ConstrainedType) t);
        if (t instanceof MacroType) {
            MacroType pt = (MacroType) t;
            return translate(pt.definedType());
        }
        return ConstraintManager.getConstraintSystem().makeTypeLit(t);
        //  return ConstraintManager.getConstraintSystem().makeLit(t);
    }
    
    public XUQV translateTypeParam(ParameterType t) {
        return ConstraintManager.getConstraintSystem().makeUQV(t.toString()); //ConstraintManager.getConstraintSystem().makeLocal(ConstraintManager.getConstraintSystem().makeName(t));
    }

    
    /**
     * Translate into the literal t.
     * @param t
     * @param ts TODO
     * @return
     */
    public static XLit translate(int t, TypeSystem ts) {
        return ConstraintManager.getConstraintSystem().makeLit(t, ts.Int());
    }

    /**
     * Translate into the literal t.
     * @param t
     * @param ts TODO
     * @return
     */
    public static XLit translate(boolean t, TypeSystem ts) {
        return ConstraintManager.getConstraintSystem().makeLit(t, ts.Boolean());
    }

    /**
     * Return the XLit representing null.
     * @param ts TODO
     * @return
     */
    public static XLit transNull(TypeSystem ts) {
        return ConstraintManager.getConstraintSystem().makeLit(null, ts.Null());
    }


  /*  public CConstraint normalize(CConstraint c, Context xc) {
        CConstraint result = ConstraintManager.getConstraintSystem().makeCConstraint();
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
                            result.addBinding(xt.left(), ConstraintManager.getConstraintSystem().TRUE);
                        } else 
                            if (c.disEntails(t1, t2)) {
                                result.addBinding(xt.left(), ConstraintManager.getConstraintSystem().FALSE);
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
*/
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
    public CConstraint constraint(List<Formal> ignore, Expr term, Context xc) throws SemanticException {
        CConstraint c = ConstraintManager.getConstraintSystem().makeCConstraint();
        if (term == null)
            return c;

        if (! term.type().isBoolean())
            throw new SemanticException("Cannot build constraint from expression (1)|" 
            		+ term + "| of type " + term.type() + "; not a boolean.", term.position());

        // TODO: handle the formals.
        XTerm t= translate(c, term, xc, true);
        

        if (t == null)
            throw new SemanticException("Cannot build constraint from expression (2)|" + term + "|.", term.position());

        try {
            c.addTerm(t);
        }
        catch (XFailure e) {
            c.setInconsistent();
        }
        return c;
    }

    public TypeConstraint typeConstraint(List<Formal> ignore, Expr term, Context xc) throws SemanticException {
        TypeConstraint c = new TypeConstraint();
        if (term == null)
            return c;

        if (! term.type().isBoolean())
            throw new SemanticException("Cannot build constraint from expression (3)|"
            		+ term + "| of type " + term.type() + "; not a boolean.",
            		term.position());

        // TODO: handle the formals.

        transType(c, term, xc);

        return c;
    }

    

    public static boolean isPureTerm(Term t) {
        boolean result = false;
        if (t instanceof Variable) {
            Variable v = (Variable) t;
            result = v.flags().isFinal();
        }
        return result;
    }

    public XVar translateThisWithoutTypeConstraint() {
        XVar v = ConstraintManager.getConstraintSystem().makeThis(); // ConstraintManager.getConstraintSystem().makeLocal(ConstraintManager.getConstraintSystem().makeName("this"));
        return v;
    }

    /*public XLocal translateThis(Type t) throws SemanticException {
        XLocal v = translateThisWithoutTypeConstraint();
        return v;
    }*/


    public CConstraint binaryOp(Binary.Operator op, CConstraint cl, CConstraint cr) {
        return null; // none supported
    }

    public CConstraint unaryOp(Unary.Operator op, CConstraint ca) {
        return null; // none supported
    }
    
    // *********************************************************************************************
    // *********************************** private help routines for translation********************
    private XTerm trans(CConstraint c, Here_c h, Context xc, boolean tl) {
        XConstrainedTerm placeTerm = xc.currentPlaceTerm();
        //XConstrainedTerm placeTerm = h.placeTerm();
        if (placeTerm == null) return ConstraintManager.getConstraintSystem().makeEQV();
        return placeTerm.term();
        //return PlaceChecker.here();
    }
    private XLocal trans(Local t) {
        return translate(t.localInstance());
    }

    private XTerm trans(CConstraint c, TypeNode t) {
        return translate(t.type());
    }

    private void transType(TypeConstraint c, Binary t, Context xc) throws SemanticException {
        Expr left = t.left();
        Expr right = t.right();
        XTerm v;

        if (t.operator() == Binary.COND_AND 
                || (t.operator() == Binary.BIT_AND 
                		&& ts.isImplicitCastValid(t.type(), ts.Boolean(), xc))) {
            transType(c, left, xc);
            transType(c, right, xc);
        }
        else {
            throw new SemanticException("Cannot translate " + t 
            		+ " into a type constraint.", t.position());
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
	    } else if (t instanceof IsRefTest) {
	        transType(c, (IsRefTest) t, xc);
	    }
        else {
            throw new SemanticException("Cannot translate " + t 
            		+ " into a type constraint.", t.position());
        }
    }


    private void transType(TypeConstraint c, HasZeroTest t, Context xc) throws SemanticException {
        TypeNode left = t.parameter();
        c.addTerm(new SubtypeConstraint(left.type(), null, SubtypeConstraint.Kind.HASZERO));
    }
    private void transType(TypeConstraint c, IsRefTest t, Context xc) throws SemanticException {
        TypeNode left = t.parameter();
        c.addTerm(new SubtypeConstraint(left.type(), null, SubtypeConstraint.Kind.ISREF));
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
        XVar x = ConstraintManager.getConstraintSystem().makeUQV();
        c1.addSelfBinding(x);
        CConstraint c2 = Types.xclause(x, r2.type()).copy();
        if (rb.operator()== Binary.EQ) {
            c1.addIn(c2);
            if (! c1.consistent())
                result = ConstraintManager.getConstraintSystem().xfalse();
            if (c1.entails(c2) && c2.entails(c1)) {
                result = ConstraintManager.getConstraintSystem().xtrue();
            }
        }
        return result;
    }

    
    private XTerm trans(CConstraint c, Binary t, Context xc, boolean tl) throws IllegalConstraint {
        Expr left = t.left();
        Expr right = t.right();
        XTerm v = null;
      
        Operator op = t.operator();
        XTerm lt = translate(c, left, xc, op==Binary.COND_AND); // Not top-level, unless op==&&
        XTerm rt = translate(c, right, xc,op==Binary.COND_AND); // Not top-level, unless op==&&
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
        	if (! tl)
        		throw new IllegalConstraint(t);
            v = op == Binary.EQ ? ConstraintManager.getConstraintSystem().makeEquals(lt, rt): ConstraintManager.getConstraintSystem().makeDisEquals(lt, rt);
        }
        else if (op == Binary.COND_AND 
                || (op == Binary.BIT_AND && ts.isImplicitCastValid(t.type(), ts.Boolean(), xc))) {
        	if (! tl)
        		throw new IllegalConstraint(t);
        	v = ConstraintManager.getConstraintSystem().makeAnd(lt, rt);
        }
        else  {
            v = ConstraintManager.getConstraintSystem().makeAtom(t.operator(), lt, rt);
            throw new IllegalConstraint(t);
           // return null;
        }
        return v;
    }

    private XTerm trans(CConstraint c, Tuple t, Context xc, boolean tl) throws IllegalConstraint {
        List<XTerm> terms = new ArrayList<XTerm>();
        for (Expr e : t.arguments()) {
            XTerm v = translate(c, e, xc, tl);
            if (v == null)
                return null;
            terms.add(v);
        }
        return ConstraintManager.getConstraintSystem().makeAtom("tuple", terms.toArray(new XTerm[0]));
    }

	private Type getType(TypeSystem ts, String name) throws SemanticException {
		return ts.systemResolver().findOne(QName.make(name));
	}

	private String nodeHasOpaqueAnnotation(TypeSystem ts, MethodDef d) {
		List<Type> anns;
		try {
			anns = d.annotationsMatching(getType(ts, "x10.compiler.Opaque"));
		} catch (SemanticException e) {
			// in case Opaque.x10 does not exist
			return null;
		}
		if (anns.size() == 0) return null;
		Type the_ann = anns.get(0);
		X10ClassType the_ann2 = the_ann.toClass();
	
        Expr e = the_ann2.propertyInitializer(0);
        if (e.isConstant()) {
            return (String) ConstantValue.toJavaObject(e.constantValue());
        }
	    return null;
	}
	
/**
     * This used to be a key routine that contained special code for handling at constraints.
     * It translates a call t into what the body of the called method would translate to,
     * assuming that the method represents a property.
     * @param c
     * @param t
     * @param xc
     * @return
     */
    private XTerm trans(CConstraint c, Call t, Context xc, boolean tl) throws IllegalConstraint {
        MethodInstance xmi = (MethodInstance) t.methodInstance();
        Flags f = xmi.flags();
        if (f.isProperty()) {
            XTerm r = translate(c, t.target(), xc, tl);
            if (r == null)
                return null;
            // FIXME: should just return the atom, and add atom==body to the real clause of the class
            // FIXME: fold in class's real clause constraints on parameters into real clause of type parameters
            XTerm body = xmi.body();
            if (body != null) {
                if (xmi.x10Def().thisVar() != null && t.target() instanceof Expr) {
                    //XName This = ConstraintManager.getConstraintSystem().makeName(new Object(), Types.get(xmi.def().container()) + "#this");
                    //body = body.subst(r, ConstraintManager.getConstraintSystem().makeLocal(This));
                    body = body.subst(r, xmi.x10Def().thisVar());
                }
                if ((! tl) && ! (body.okAsNestedTerm()))
                	throw new IllegalConstraint(t, body, t.position());
                for (int i = 0; i < t.arguments().size(); i++) {
                    //XVar x = (XVar) X10TypeMixin.selfVarBinding(xmi.formalTypes().get(i));
                    //XVar x = (XVar) xmi.formalTypes().get(i);
                    XVar x =  ConstraintManager.getConstraintSystem().makeLocal((X10LocalDef) xmi.def().formalNames().get(i));  // we get the def first because the formalNames were renamed in Matcher.instantiate, see XTENLANG-2582
                    XTerm y = translate(c, t.arguments().get(i), xc, tl);
                    if (y == null)
                        assert y != null : "XTypeTranslator: translation of arg " + i + " of " + t + " yields null (pos=" 
                        + t.position() + ")";
                    body = body.subst(y, x);
                }
                return body;
            } else {
            	String n = nodeHasOpaqueAnnotation(xc.typeSystem(), xmi.def());
            	if (n != null) {
	            	System.out.println("making atom \""+n+"\" for "+t);
	            	// translating arguments
	            	List<XTerm> args = new ArrayList<XTerm>(); 
	            	for (Expr arg : t.arguments()) {
	            		XTerm x = translate(c, arg, xc, tl); 
	            		args.add(x);
	            	}
	            	return ConstraintManager.getConstraintSystem().makeOpaque(xmi.def(), r, args);
            	}
            }

            if (t.arguments().size() == 0) {
              
                XTerm v;
                if (r instanceof XVar) {
                    v = ConstraintManager.getConstraintSystem().makeField((XVar) r, xmi.def());
                  
                }
                else {
                	  if ((! tl))
                       	throw new IllegalConstraint(t);
                    v = ConstraintManager.getConstraintSystem().makeAtom(xmi.def(), r);
                }
                return v;
            }
            if ((! tl))
               	throw new IllegalConstraint(t);
            List<XTerm> terms = new ArrayList<XTerm>();
            terms.add(r);
            for (Expr e : t.arguments()) {
                XTerm v = translate(c, e, xc, tl);
                if (v == null)
                    return null;
                terms.add(v);
            }
            XTerm v = ConstraintManager.getConstraintSystem().makeAtom(xmi.def(), terms);
            return v;
        }
        Type type = t.type();
        return Types.selfVarBinding(type); // maybe null.
    }

    private XTerm trans(CConstraint c, Variable term, Context xc, boolean tl) throws IllegalConstraint {
        if (term instanceof Field)
            return trans(c, (Field) term, xc, tl);
        if (term instanceof X10Special)
            return trans(c, (X10Special) term, xc, tl);
        if (term instanceof Local)
            return trans((Local) term);
        return null;
    }

    private XTerm trans(CConstraint c, X10Special term, Context xc, boolean tl) {
        return trans(c, term, xc);
    }
    public XTerm trans(CConstraint c, X10Special t, Context xc0) {
        if (true || xc0.specialAsQualifiedVar()) {
            return translateSpecialAsQualified(c,t,xc0);
        }
        Context xc = xc0;
        if (t.kind() == X10Special.SELF) {
            if (c == null) {
                //throw new SemanticException("Cannot refer to self outside a dependent clause.");
                return null;
            }
            XVar v = (XVar) c.self().clone();
         // Need to deal with qualified guy as well.
            TypeNode tn = t.qualifier();
            if (tn != null) {
                Type q = Types.baseType(tn.type());
                v = ConstraintManager.getConstraintSystem().makeQualifiedVar(q, v);
            }
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
            XVar thisVar = null;
            for (Context outer = xc; outer != null && thisVar == null; outer = outer.pop()) {
                thisVar = outer.thisVar();
            }
            if (thisVar == null) {
                SemanticException e = new SemanticException("Cannot refer to |this| from the context " + xc);
                return null;
            }
            if (c != null)
                c.setThisVar(thisVar);
            return thisVar;
        }
    }
    public XTerm translateSpecialAsQualified(CConstraint c, X10Special t, Context xc0) {
        Context xc = xc0;
        if (t.kind() == X10Special.SELF) {
            if (c == null) {
                //throw new SemanticException("Cannot refer to self outside a dependent clause.");
                return null;
            }
            XVar v = (XVar) c.self().clone();
            // Need to deal with qualified guy as well.
            TypeNode tn = t.qualifier();
            if (tn != null) {
                Type q = Types.baseType(tn.type());
                v = ConstraintManager.getConstraintSystem().makeQualifiedVar(q, v);
            }
            return v;
        }
        // this. why are we doing nothing about super..?
        XVar baseThisVar = null;
        for (Context outer = xc; outer != null && baseThisVar == null; outer = outer.pop()) {
            baseThisVar = outer.thisVar();
        }
        if (baseThisVar == null) {
            SemanticException e = new SemanticException("Cannot refer to |this| from the context " + xc);
            return null;
        }
        if (baseThisVar instanceof QualifiedVar) {
            QualifiedVar qVar = (QualifiedVar) baseThisVar;
            if (qVar.type() == ((Typed) qVar.receiver()).type())
                baseThisVar = qVar.receiver();
        }
        assert (baseThisVar instanceof CThis);
        XVar thisVar = baseThisVar;
        try {
            TypeNode tn = t.qualifier();
            if (tn == null) {
                return thisVar;
            }
            Type q = Types.baseType(tn.type());
            // So we need to translate A.this in a deptype.
            // The result should not be the this-associated-with-
            // the-outer-A in the context.
            // Rather it needs to be a QualifiedVar capturing
            // A as a qualifier. 
            // Return the qualified version of the base this.
            thisVar = (((CThis)baseThisVar).type()==q)
            ? baseThisVar : ConstraintManager.getConstraintSystem().makeQualifiedVar(q, baseThisVar);
            return thisVar;
        } finally {
            if (c != null)
                c.setThisVar(baseThisVar);
        }
    }
    private XTerm trans(CConstraint c, Field t, Context xc, boolean tl)  throws IllegalConstraint {
        XTerm receiver = translate(c, t.target(), xc, tl);
        if (receiver == null)
            return null;
        return translate(receiver, t.fieldInstance(), tl);
    }
}
