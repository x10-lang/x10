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
import x10.constraint.XLocal;
import x10.constraint.XVar;
import x10.constraint.XTerm;
import x10.types.constraints.ConstraintManager;
import x10.errors.Errors;
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
import x10.types.matcher.Subst;
import x10.util.Synthesizer;

/**
 * The core routines that implement the place type checker. These are called from various AST nodes. 
 * @author vj
 *
 */
public class PlaceChecker {
	
	/**
	 * Making sure we have only one "synthetic here" variable per TypeSystem. 
	 */
	static final Map<TypeSystem, XVar<Type>> synthHereMap = new HashMap<TypeSystem, XVar<Type>>();
	// lshadare: is there a more reasonable way to do this? 
	public static XVar<Type> here(TypeSystem ts) {
		if (!synthHereMap.containsKey(ts)) {
			XVar<Type> here = ConstraintManager.getConstraintSystem().makeUQV(ts.Place(), "synthetic here");
			synthHereMap.put(ts,  here);
		} 
		return synthHereMap.get(ts);
	}

	private static final String PLACE_HAME = "here"; // temporary XTENLANG-2674 hack

	/**
	 * 
	 * @return a newly constructed UQV representing a fixed but unknown place.
	 */
	public static XTerm<Type> makePlace(TypeSystem ts) {
		XTerm<Type> place = ConstraintManager.getConstraintSystem().makeUQV(ts.Place(), PLACE_HAME);
		return place;
	}

	public static boolean isGlobalPlace(XTerm<Type> term) {
		return (term instanceof XEQV && term.toString().startsWith(PLACE_HAME));
	}

	static XTerm<Type> thisHomeVar(Context xc) {
		return homeVar(((Context) xc).thisVar(), (TypeSystem) xc.typeSystem());
	}
	static FieldInstance GlobalRefHome(TypeSystem xts) {
		return ((ContainerType) xts.GlobalRef()).fieldNamed(xts.homeName());
	}

	public static final String HOME_NAME = "$$here";

	/**
	 * The key  move in adapting the 2.0 place checking system to 2.1 is to continue
	 * to pretend that for each object (other than instances of GlobalRef -- 
	 * for that we have globalRefHomeVar(..)) we track the place where it was created in a fake
	 * field called "$$here". This field does not exist in the object, but it serves as a way
	 * to record the place at which an object was created so that fields that may be initialized 
	 * with the indexical constant "here" can still be tracked through the fake field "this.$$here".
	 * For instance this lets us infer that the following is place safe
	 * class C {
	 *   private val root = GlobalRef[C](this); 
	 *   // this records the type of root as GlobalRef[C]{self.home == C#this.here} .. note C#this.here is a fake field.
	 *   }
	 *   val x = (new C()).root; 
	 *   // this computes the type of new C() as C{this.here == _place269}, where _place269 is a constant standing for "here"
	 *   // so it computes the type of (new C()).root as GlobalRef[C]{self.home == _place269}.
	 *   x(); // now this is legal because _place269 is the current place, hence the guard x.home == here is satisfied.
	 *
	 * todo: Yoav notes that the implementation is broken, see XTENLANG-1905.
	 *   
	 *   Note that the user cannot specify any constraint in the X10 source program to refer to this fake field.
	 *   This field is purely an internal contrivance of the type system to enable us to reuse the machinery 
	 *   we had developed for 2.0 to track where an object is created.
	 *   
	 * @param target
	 * @param xts
	 * @return
	 */
	static XTerm<Type> homeVar(XTerm<Type> target, TypeSystem xts)  {
		return xts.xtypeTranslator().translateFakeField(target, HOME_NAME, xts.Place());
	}
	static XTerm<Type> globalRefHomeVar(XTerm<Type> target, TypeSystem xts)  {
		return xts.xtypeTranslator().translate(target, GlobalRefHome(xts));
	}

	public static XTerm<Type> placeTerm(Type t) {
		TypeSystem xts = (TypeSystem) t.typeSystem();
		CConstraint cc = Types.xclause(t);
		return cc==null ? null : cc.bindingForSelfProjection(GlobalRefHome(xts).def());
	}

	/**
	 * The returned CConstraint has no type information for self, it is
	 * the caller's responsibility to add this information. 
	 * @param thisVar
	 * @param ts
	 * @return
	 */
	public static CConstraint ThisHomeEqualsHere(XTerm<Type> thisVar, TypeSystem ts) {

		XTerm<Type> h =  PlaceChecker.homeVar(thisVar, ts);
		CConstraint c = ConstraintManager.getConstraintSystem().makeCConstraint(ts.Place());
		if (h != null) {
			c.addEquality(h, here(ts));
		}
		return c;
	}

	/**
	 * Caller must ensure that self.home==here constraint can be consistently added to type.
	 * @param type
	 * @param cxt
	 * @return
	 */
	public static Type AddIsHereClause(Type type, Context cxt) {
		if (Types.isX10Struct(type))
			return type;
		ConstrainedType type1 = Types.toConstrainedType(type);
		XTerm<Type> selfVar = Types.selfVar(type1);
		assert selfVar != null;
		/*if (selfVar == null) {
		    selfVar = ConstraintManager.getConstraintSystem().makeEQV("self");
		    try {
		        type = X10TypeMixin.setSelfVar(type, selfVar);
		    } catch (SemanticException e) {
		        throw new InternalCompilerError("Cannot set self var for type "+type, e);
		    }
		}*/
		XTerm<Type> locVar = homeVar(selfVar, (TypeSystem) cxt.typeSystem());
		XConstrainedTerm pt = (((Context) cxt).currentPlaceTerm());
		if (locVar != null && pt != null)
			type = Types.addBinding(type1, locVar, pt.term()); // here());// here, not pt); // pt, not PlaceChecker.here()
		return type;
	}

	/**
	 * Replace occurrences of here in type by the context's place term.
	 * @param type
	 * @param xct
	 * @return
	 */
	public static Type ReplaceHereByPlaceTerm(Type type, Context xct) {
		if (xct.currentPlaceTerm() == null)
			assert true;
		try {
			if (xct.currentPlaceTerm() !=null) 
				type = Subst.subst(type, xct.currentPlaceTerm().term(), here(type.typeSystem())); 
		} catch (SemanticException z) {
			throw new InternalCompilerError("Unexpectedly inconsistent constraint.");
		}
		return type;
	}
	
	public static Type ReplaceHereByPlaceTerm(Type type, XConstrainedTerm pt) {
		try {
				type = Subst.subst(type, pt.term(), here(type.typeSystem())); 
		} catch (SemanticException z) {
			throw new InternalCompilerError("Unexpectedly inconsistent constraint.");
		}
		return type;
	}
	/**
	 * Replace occurrences of here in type with term.
	 * @param type
	 * @param term
	 * @return
	 */
	public static Type ReplaceHereByPlaceTerm(Type type, XTerm<Type> term) {
		// Do not replace for closure types. The ! in such types is 
		// evaluated dynamically, i.e. at the point of evaluation of the closure.
		if (type instanceof FunctionType_c)
			return type;
		//assert term != null;
		try {
			if (term != null)
				type = Subst.subst(type,  term, here(type.typeSystem())); 
		} catch (SemanticException z) {
			throw new InternalCompilerError("Unexpectedly inconsistent constraint.");
		}
		return type;
	}

	public static Type ReplacePlaceTermByHere(Type type, Context context) {
		XConstrainedTerm h = ((Context) context).currentPlaceTerm();
		if (h == null)
			return type;
		XTerm<Type> term = h.term();
		try {
			if (term instanceof XVar)
				type = Subst.subst(type,  here(type.typeSystem()), (XVar<Type>)term); 
		} catch (SemanticException z) {
			throw new InternalCompilerError("Unexpectedly inconsistent constraint.");
		}
		return type;
	}


	public static void AddHereEqualsPlaceTerm(CConstraint c, Context xc) {
		if (! c.consistent())
			return;
		XConstrainedTerm placeTerm = xc.currentPlaceTerm();
		if (placeTerm != null)  
			c.addEquality(here(xc.typeSystem()), placeTerm.term());
	}

	public static void setHereTerm(MethodDef md, Context c) {
	    c = c.pushBlock();
	    if (isGlobalCode(md)) {
	        c.setPlace(XConstrainedTerm.make(makePlace(md.typeSystem()), md.typeSystem().Place()));
	    } else {
	        setHereIsThisHome(c);
	    }
	}

	public static void setHereTerm(FieldDef fd, Context c) {
		Flags flags = fd.flags();
		if (flags.isStatic()) {
			c.setPlace(fd.typeSystem().FIRST_PLACE());
			return;
		}
		if (Types.isX10Struct(fd.container().get())) {
			c.setPlace(XConstrainedTerm.make(makePlace(fd.typeSystem()), fd.typeSystem().Place()));
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

	public static XConstrainedTerm methodPlaceTerm(MethodDef md) {
	    // XTENLANG-2725: in X10 2.2, all methods are "global"
	    boolean isGlobal = true; // || md.flags().isStatic() || Types.isX10Struct(ct.asType());
	    XTerm<Type> term = isGlobal ? makePlace(md.typeSystem()) : homeVar(md.thisVar(), md.typeSystem());
	    CConstraint d = ConstraintManager.getConstraintSystem().makeCConstraint(md.typeSystem().Place());
	    try {
	        return XConstrainedTerm.instantiate(d, term);
	    } catch (XFailure z) {
	        throw new InternalCompilerError("Cannot construct placeTerm from term and constraint.");
	    }
	}

	public static XConstrainedTerm constructorPlaceTerm(ConstructorDef cd) {
	    CConstraint d = ConstraintManager.getConstraintSystem().makeCConstraint(cd.typeSystem().Place());
	    XTerm<Type> term = homeVar(cd.thisVar(), cd.typeSystem());
	    try {
	        return XConstrainedTerm.instantiate(d, term);
	    } catch (XFailure z) {
	        throw new InternalCompilerError("Cannot construct placeTerm from term and constraint.");
	    }
	}
	
	public static XConstrainedTerm closurePlaceTerm(ClosureDef cd) {
	    CConstraint d = ConstraintManager.getConstraintSystem().makeCConstraint(cd.typeSystem().Place());
	    XTerm<Type> term = makePlace(cd.typeSystem());
	    try {
	        return XConstrainedTerm.instantiate(d, term);
	    } catch (XFailure z) {
	        throw new InternalCompilerError("Cannot construct placeTerm from term and constraint.");
	    }
	}

	/**
	 * The method is global if it declared with a global flag or if it is a static method, or
	 * if it a method of a struct
	 * @param md
	 * @return
	 */
	static boolean isGlobalCode(MethodDef md) {
		Flags flags = md.flags();
		// XTENLANG-2725: in X10 2.2, all methods are "global"
		boolean isGlobal = true || flags.isStatic() || Types.isX10Struct(md.container().get());
		return isGlobal;
	}

	/**
	 * 
	 * @param xc -- the current context
	 * @return -- the current context (if within a struct), else a context
	 * obtained by setting the new place term to be this.home.
	 */
	public static void setHereIsThisHome(Context c) {
		XTerm<Type> h = thisHomeVar(c);
		if (h != null)  // null for structs.
			c.setPlace(XConstrainedTerm.make(h, c.typeSystem().Place())); 	
	}


	public static Receiver makeReceiverLocalIfNecessary(ContextVisitor tc, Receiver target, Flags flags) {
		return target;
	}

	public static X10Call makeReceiverLocalIfNecessary(X10Call n, ContextVisitor tc) throws SemanticException {
		Receiver res =
			makeReceiverLocalIfNecessary(tc, n.target(), n.methodInstance().flags());
		if (res != null) {
			if (res != n.target()) n = (X10Call) n.target(res).targetImplicit(false);
			return n;
		}
		XConstrainedTerm h = ((Context) tc.context()).currentPlaceTerm();
		if (h != null && PlaceChecker.isGlobalPlace(h.term())) {
			throw new Errors.PlaceTypeErrorMethodShouldBeGlobal(n, n.position());
		} else {
			throw new Errors.PlaceTypeErrorMethodShouldBeLocalOrGlobal(n, 
					h == null ? null : h.term(),
							placeTerm(n.target().type()),
							n.position());
		}
	}

	public static X10Field_c makeFieldAccessLocalIfNecessary(X10Field_c n, ContextVisitor tc) throws SemanticException {
		Receiver res =
			makeReceiverLocalIfNecessary(tc, n.target(), n.fieldInstance().flags());
		if (res != null) {
			if (res != n.target()) n = (X10Field_c) n.target(res).targetImplicit(false);
			return n;
		}
		XConstrainedTerm h = ((Context) tc.context()).currentPlaceTerm();
		if (h != null && PlaceChecker.isGlobalPlace(h.term())) {
			throw new Errors.PlaceTypeErrorFieldShouldBeGlobal(n, n.position());
		} else {
			throw new Errors.PlaceTypeErrorFieldShouldBeLocalOrGlobal(n, 
					h == null ? null : h.term(),
							placeTerm(n.target().type()),
							n.position());
		}
	}


	public static XConstrainedTerm computePlaceTerm(Expr place, Context xc, TypeSystem ts) throws SemanticException {
		Type placeType = place.type();
		CConstraint d = Types.xclause(placeType);
		d = (d==null) ? ConstraintManager.getConstraintSystem().makeCConstraint(Types.baseType(placeType)) : d.copy();
		CConstraint pc = null;
		XTerm<Type> term = null;
		XConstrainedTerm pt = null;
		boolean placeIsPlace = ts.isImplicitCastValid(placeType, ts.Place(), xc);
		if (placeIsPlace)  {
			term = ts.xtypeTranslator().translate(pc, place, xc);
			if (term == null) {
				term = makePlace(ts);
			}
			try {
				pt = XConstrainedTerm.instantiate(d, term);
			} catch (XFailure z) {
				throw new InternalCompilerError("Cannot construct placeTerm from " + 
						term + " and constraint " + d + ".");
			}
		} else {
			throw new InternalCompilerError("The place argument of an \"at\" must be of type Place", place.position());
		}

		return pt;
	}
}
