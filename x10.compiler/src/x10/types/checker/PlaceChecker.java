package x10.types.checker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import polyglot.ast.Call;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.Receiver;
import polyglot.types.ClassDef;
import polyglot.types.ConstructorDef;
import polyglot.types.Context;
import polyglot.types.FieldDef;
import polyglot.types.FieldInstance;
import polyglot.types.Flags;
import polyglot.types.InitializerDef;
import polyglot.types.MethodDef;

import polyglot.types.Name;
import polyglot.types.SemanticException;
import polyglot.types.ContainerType;
import polyglot.types.Type;
import polyglot.types.Types;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import x10.Configuration;
import x10.ast.X10Call;
import x10.ast.X10Call_c;
import x10.ast.X10CanonicalTypeNode_c;
import x10.ast.X10Field_c;
import x10.constraint.XEQV;
import x10.constraint.XFailure;
import x10.constraint.XLit;
import x10.constraint.XUQV;
import x10.constraint.XVar;
import x10.constraint.XTerm;
import x10.types.constraints.ConstraintManager;
import x10.errors.Errors;
import x10.errors.Errors.IllegalConstraint;
import x10.errors.Errors.PlaceTypeErrorMethodShouldBeLocalOrGlobal;
import x10.types.FunctionType_c;
import polyglot.types.Context;
import x10.types.ClosureDef;
import x10.types.ConstrainedType;
import x10.types.X10FieldInstance;

import x10.types.MethodInstance;

import polyglot.types.TypeSystem;
import x10.types.XTypeTranslator;
import x10.types.constraints.CConstraint;
import x10.types.constraints.CConstraint;
import x10.types.constraints.XConstrainedTerm;
import x10.types.constraints.CLocal;
import x10.types.matcher.Subst;
import x10.util.Synthesizer;

/**
 * The core routines that implement the place type checker. These are called from various AST nodes. 
 * @author vj
 *
 */
public class PlaceChecker {
	
	/**
	 * Making sure we have only one "here" variable per TypeSystem. 
	 */
	static final Map<TypeSystem, XVar<Type>> synthHereMap = new HashMap<TypeSystem, XVar<Type>>();
	// lshadare: is there a more reasonable way to do this? 
	// [DC] this here variable is used in class guards, field types, method guards, params,
	// and anywhere in a method body that is not inside an 'at'.
	public static XVar<Type> globalHere(TypeSystem ts) {
		if (!synthHereMap.containsKey(ts)) {
			XVar<Type> here = ConstraintManager.getConstraintSystem().makeUQV(ts.Place(), "here");
			synthHereMap.put(ts,  here);
		} 
		return synthHereMap.get(ts);
	}



	
	/*
	private static XTerm<Type> thisHomeVar(Context xc) {
		return homeVar(((Context) xc).thisVar(), (TypeSystem) xc.typeSystem());
	}
	*/

	private static XTerm<Type> homeVar(XTerm<Type> target, TypeSystem xts)  {
		return xts.xtypeTranslator().translateFakeField(target, "$$here", xts.Place());
	}

	/**
	 * The returned CConstraint has no type information for self, it is
	 * the caller's responsibility to add this information. 
	 * @param thisVar
	 * @param ts
	 * @return
	 */
	
	/*
	public static CConstraint ThisHomeEqualsHere(XTerm<Type> thisVar, TypeSystem ts) {

		XTerm<Type> h =  PlaceChecker.homeVar(thisVar, ts);
		CConstraint c = ConstraintManager.getConstraintSystem().makeCConstraint(ts.Place(),ts);
		if (h != null) {
			c.addEquality(h, here(ts));
		}
		return c;
	}
	*/


	/**
	 * Replace occurrences of here in type by the context's place term.
	 * @param type
	 * @param xct
	 * @return
	 */
	public static Type ReplaceHereByPlaceTerm(Type type, Context xct) {
		if (xct.currentPlaceTerm() == null) return type; 
		return ReplaceHereByPlaceTerm(type, xct.currentPlaceTerm());
	}
	
	public static Type ReplaceHereByPlaceTerm(Type type, XConstrainedTerm pt) {
		assert pt != null;
		return ReplaceHereByPlaceTerm(type, pt.term());
	}

	/**
	 * Replace occurrences of here in type with term.
	 * @param type
	 * @param term
	 * @return
	 */
	public static Type ReplaceHereByPlaceTerm(Type type, XTerm<Type> term) {
		// [DC] no idea what the following is talking about, commenting out the code to see if anything breaks...
		// Do not replace for closure types. The ! in such types is 
		// evaluated dynamically, i.e. at the point of evaluation of the closure.
		//if (type instanceof FunctionType_c)
		//	return type;
		
		if (term == null) return type;

		try {
			return Subst.subst(type,  term, globalHere(type.typeSystem())); 
		} catch (SemanticException z) {
			throw new InternalCompilerError("Unexpectedly inconsistent constraint.");
		}
	}

	public static Type ReplacePlaceTermByHere(Type type, Context context) {
		XConstrainedTerm h = ((Context) context).currentPlaceTerm();
		if (h == null)
			return type;
		XTerm<Type> term = h.term();
		try {
			if (term instanceof XVar)
				type = Subst.subst(type,  globalHere(type.typeSystem()), (XVar<Type>)term); 
		} catch (SemanticException z) {
			throw new InternalCompilerError("Unexpectedly inconsistent constraint.");
		}
		return type;
	}

/*
	public static void AddHereEqualsPlaceTerm(CConstraint c, Context xc) {
		if (! c.consistent())
			return;
		XConstrainedTerm placeTerm = xc.currentPlaceTerm();
		if (placeTerm != null)  
			c.addEquality(here(xc.typeSystem()), placeTerm.term());
	}
*/
	
	/*
	public static void setHereTerm(MethodDef md, Context c) {
	    c = c.pushBlock();
        c.setPlace(XConstrainedTerm.make(makeHereUQV(md.typeSystem()), md.typeSystem().Place()));
	}
	*/

	/*
	public static void setHereTerm(FieldDef fd, Context c) {
		Flags flags = fd.flags();
		if (flags.isStatic()) {
			c.setPlace(fd.typeSystem().FIRST_PLACE());
			return;
		}
		if (Types.isX10Struct(fd.container().get())) {
			c.setPlace(XConstrainedTerm.make(makeHereUQV(fd.typeSystem()), fd.typeSystem().Place()));
			return;
		}
		TypeSystem xts = (TypeSystem) c.typeSystem();
		Context xc = (Context) c;
		ClassDef cd = c.currentClassDef();

		// bypass GlobalRef to avoid infinite recursion (pushPlace will again look for GlobalRef.home..)
		// This means that the types in GlobalRef cannot reference here.
		if (cd != null)
			if ( ! xts.hasSameClassDef(Types.baseType(cd.asType()), xts.GlobalRef())) {
				XTerm<Type> h =  homeVar(xc.thisVar(),xts);
				if (h != null)  // null for structs.
					c.setPlace(XConstrainedTerm.make(h, xts.Place()));
			}
	}
	*/

	/*
	public static XConstrainedTerm methodPlaceTerm(MethodDef md) {
	    // XTENLANG-2725: in X10 2.2, all methods are "global"
	    boolean isGlobal = true; // || md.flags().isStatic() || Types.isX10Struct(ct.asType());
	    XTerm<Type> term = makeHereUQV(md.typeSystem());
	    CConstraint d = ConstraintManager.getConstraintSystem().makeCConstraint(md.typeSystem().Place(),md.typeSystem());
        return XConstrainedTerm.instantiate(d, term);
	}

	public static XConstrainedTerm constructorPlaceTerm(ConstructorDef cd) {
	    CConstraint d = ConstraintManager.getConstraintSystem().makeCConstraint(cd.typeSystem().Place(),cd.typeSystem());
	    //XTerm<Type> term = homeVar(cd.thisVar(), cd.typeSystem());
	    XTerm<Type> term = makeHereUQV(cd.typeSystem());
        return XConstrainedTerm.instantiate(d, term);
	}
	
	public static XConstrainedTerm closurePlaceTerm(ClosureDef cd) {
	    CConstraint d = ConstraintManager.getConstraintSystem().makeCConstraint(cd.typeSystem().Place(),cd.typeSystem());
	    XTerm<Type> term = makeHereUQV(cd.typeSystem());
        return XConstrainedTerm.instantiate(d, term);
	}
	*/

	public static XConstrainedTerm globalPlaceTerm(TypeSystem ts) {
	    CConstraint d = ConstraintManager.getConstraintSystem().makeCConstraint(ts.Place(), ts);
        return XConstrainedTerm.instantiate(d, globalHere(ts));
	}

	/**
	 * 
	 * @param xc -- the current context
	 * @return -- the current context (if within a struct), else a context
	 * obtained by setting the new place term to be this.home.
	 */
	/*
	public static void setHereIsThisHome(Context c) {
		XTerm<Type> h = thisHomeVar(c);
		if (h != null)  // null for structs.
			c.setPlace(XConstrainedTerm.make(h, c.typeSystem().Place())); 	
	}
	*/


	static int counter = 0;

	/** Return a place term where the current place is derived statically from expr if possible. */
	public static XConstrainedTerm computePlaceTerm(Expr expr, Context xc, TypeSystem ts) {
		if (expr == null) {
			
		}
		Type exprType = expr.type();
		if (!ts.isImplicitCastValid(exprType, ts.Place(), xc)) {
			throw new InternalCompilerError("The place argument of an \"at\" must be of type Place", expr.position());
		}

		CConstraint d = Types.xclause(exprType);
		if (d == null) {
			d = ConstraintManager.getConstraintSystem().makeCConstraint(Types.baseType(exprType),ts);
		}

		XTerm<Type> term;
		try {
			CConstraint pc = null;
			term = ts.xtypeTranslator().translate(pc, expr, xc);
		} catch (IllegalConstraint e) {
			term = null;
		}

		if (term == null) {
			// [DC] this means that p in at (p) { ... } is not something the type system can understand
			// e.g. a mutable var or an m() that is not a property method.  Use an eqv.
			// OR we got the exception above, which I think means the same thing but not sure.
			term = ConstraintManager.getConstraintSystem().makeUQV(ts.Place(), "unknown_place"+(counter++));
		}

		// [DC] note, adds to d: d.self() == term
		return XConstrainedTerm.instantiate(d, term);
	}

	/** Return a place term that has no information about the target place. */
	public static XConstrainedTerm computeUnknownPlaceTerm(Context xc, TypeSystem ts) {

		CConstraint d = ConstraintManager.getConstraintSystem().makeCConstraint(ts.Place(), ts);

		XTerm<Type> term = ConstraintManager.getConstraintSystem().makeUQV(ts.Place(), "unknown_ateach_place"+(counter++));

		// [DC] note, adds to d: d.self() == term
		return XConstrainedTerm.instantiate(d, term);
	}
}
