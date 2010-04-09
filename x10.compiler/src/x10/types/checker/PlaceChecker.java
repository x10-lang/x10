package x10.types.checker;

import java.util.List;

import polyglot.ast.Call;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.Receiver;
import polyglot.types.ClassDef;
import polyglot.types.Context;
import polyglot.types.FieldDef;
import polyglot.types.FieldInstance;
import polyglot.types.Flags;
import polyglot.types.InitializerDef;
import polyglot.types.MethodDef;
import polyglot.types.MethodInstance;
import polyglot.types.Name;
import polyglot.types.SemanticException;
import polyglot.types.StructType;
import polyglot.types.Type;
import polyglot.util.InternalCompilerError;
import polyglot.visit.ContextVisitor;
import x10.Configuration;
import x10.ast.X10Call;
import x10.ast.X10Call_c;
import x10.ast.X10CanonicalTypeNode_c;
import x10.constraint.XEQV_c;
import x10.constraint.XFailure;
import x10.constraint.XLit;
import x10.constraint.XLit_c;
import x10.constraint.XLocal;
import x10.constraint.XName;
import x10.constraint.XRoot;
import x10.constraint.XTerm;
import x10.constraint.XTerms;
import x10.errors.Errors;
import x10.errors.Errors.PlaceTypeErrorMethodShouldBeLocalOrGlobal;
import x10.types.ClosureType_c;
import x10.types.X10ClassDef;
import x10.types.X10Context;
import x10.types.X10FieldInstance;
import x10.types.X10Flags;
import x10.types.X10MethodInstance;
import x10.types.X10TypeMixin;
import x10.types.X10TypeSystem;
import x10.types.XTypeTranslator;
import x10.types.constraints.CConstraint;
import x10.types.constraints.CConstraint_c;
import x10.types.constraints.XConstrainedTerm;
import x10.types.matcher.Subst;
import x10.util.Synthesizer;

/**
 * The core routines that implement the place type checker. These are called from various AST nodes. 
 * @author vj
 *
 */
public class PlaceChecker {

	static final XLocal HERE = XTerms.makeLocal(XTerms.makeName("here"));
	//public static final XLit GLOBAL_PLACE = new XLit_c("globalPlace");

	public static XLocal here() {
		return HERE;
	}
	/**
	 * 
	 * @return a newly constructed UQV representing a fixed but unknown place.
	 */
	static XTerm makePlace() {
		return XTerms.makeUQV("_place");
	}

	public static boolean isGlobalPlace(XTerm term) {
		return (term instanceof XEQV_c && term.toString().startsWith("_place"));
	}
	/**
	 * Return r.home.
	 * @param r
	 * @param context
	 * @return
	 */
	static XTerm homeVar(XTerm target) {
		return homeVar(target, null);
	}
	
	static XTerm thisHomeVar(Context xc) {
		return homeVar(((X10Context) xc).thisVar(), (X10TypeSystem) xc.typeSystem());
	}
	static FieldInstance AnyHome(X10TypeSystem xts) {
		return ((StructType) xts.Any()).fieldNamed(xts.homeName());
	}
	static XTerm homeVar(XTerm target, X10TypeSystem xts)  {
		return xts.xtypeTranslator().trans(new CConstraint_c(), target, AnyHome(xts));
	}
	
	public static XTerm placeTerm(Type t) {
    	X10TypeSystem xts = (X10TypeSystem) t.typeSystem();
    	CConstraint cc = X10TypeMixin.xclause(t);
    	return cc==null ? null : cc.bindingForSelfField(AnyHome(xts));
    }
	/**
	 * Return {thisVar.home==here}
	 */
	public static CConstraint ThisHomeEqualsPlaceTerm(XTerm thisVar, X10Context cxt) {
		XTerm h =  PlaceChecker.homeVar(thisVar);
		CConstraint c = new CConstraint_c();
		if (h != null) {
			try {
				c.addBinding(h, cxt.currentPlaceTerm());
			} catch (XFailure z) {
			}
		}
		return c;
	}
	
	public static CConstraint ThisHomeEqualsHere(XTerm thisVar, X10TypeSystem ts) {
		
		XTerm h =  PlaceChecker.homeVar(thisVar, ts);
		CConstraint c = new CConstraint_c();
		if (h != null) {
			try {
				c.addBinding(h, here());
			} catch (XFailure z) {
			}
		}
		return c;
	}
	
	public static Type AddIsHereClause(Type type, Context cxt) {
		XTerm selfVar = X10TypeMixin.selfVar(type);
    	XTerm locVar = homeVar(selfVar, (X10TypeSystem) cxt.typeSystem());
    	//XConstrainedTerm pt = ((X10Context) cxt).currentPlaceTerm();
    	// if (pt != null)
    	type = X10TypeMixin.addBinding(type, locVar, here());// here, not pt); // pt, not PlaceChecker.here()
    	return type;
	}
	public static Type ReplaceHereByPlaceTerm(Type type, X10Context xct) {
		if (xct.currentPlaceTerm() == null)
			assert true;
		try {
			if (xct.currentPlaceTerm() !=null) 
				type = Subst.subst(type, xct.currentPlaceTerm().term(), here()); 
		} catch (SemanticException z) {
			throw new InternalError("Unexpectedly inconsistent constraint.");
		}
		return type;
		/*CConstraint xclause = X10TypeMixin.xclause(type);
		if (xclause == null || xclause.valid())
			return type;
		xclause = xclause.copy();
		XConstrainedTerm pt = 
		try {
			if (pt != null) {
				xclause = xclause.substitute(pt.term(), here());
				//xclause.addIn(xclause.self(), pt.constraint());
			}
		} catch (XFailure z) {
			throw new InternalError("Unexpectedly inconsistent constraint.");
		}
		type = X10TypeMixin.constrainedType(X10TypeMixin.baseType(type), xclause);
		return type;*/
	}
	public static Type ReplaceHereByPlaceTerm(Type type, XTerm term) {
		// Do not replace for closure types. The ! in such types is 
		// evaluated dynamically, i.e. at the point of evaluation of the closure.
		if (type instanceof ClosureType_c)
			return type;
		//assert term != null;
		try {
			if (term != null)
				type = Subst.subst(type,  term, here()); 
		} catch (SemanticException z) {
			throw new InternalError("Unexpectedly inconsistent constraint.");
		}
		return type;
	}
	public static void AddThisHomeEqualsPlaceTerm(CConstraint c, XTerm thisVar, X10Context xc) throws XFailure {
		 XTerm locVar = homeVar(thisVar, (X10TypeSystem) xc.typeSystem());
         XConstrainedTerm thisPlace = xc.currentThisPlace();
         if (thisPlace != null) {
        	 assert locVar != null;
        	 c.addBinding(locVar, thisPlace);
         }
	}
	public static void AddHereEqualsPlaceTerm(CConstraint c, X10Context xc) throws XFailure{
		XConstrainedTerm placeTerm = xc.currentPlaceTerm();
		if (placeTerm != null) 
			c.addBinding(here(), placeTerm.term());
	}
	
	static XConstrainedTerm firstPlace = XConstrainedTerm.make(XTerms.makeUQV("FIRST_PLACE"));
	public static XConstrainedTerm firstPlace(X10TypeSystem xts) {
		return firstPlace;
	}
	/*
	private static XConstrainedTerm makePlace(int i, String placeName, X10TypeSystem ts) {
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
			XTerm term = ts.xtypeTranslator().trans(c, ts.xtypeTranslator().trans(ts.Place()), fi, type);
			return XConstrainedTerm.make(term, c2);
		} catch (SemanticException z) {
			// wont happen
		}
		return null;
	}
	*/
	
	/**
	 * Called when entering scopes of methods (bodies, args, return types). Pushes a placeTerm -- either a new place
	 * (if this is a global method) or this.home, where this is obtained from cxt.
	 */
	public static Context pushHereTerm(MethodDef md, X10Context cxt) {
		return 
			isGlobalCode(md) ?
					cxt.pushPlace(XConstrainedTerm.make(makePlace()))
					: pushHereIsThisHome(cxt);
	
	}
	public static Context pushHereTerm(InitializerDef id, X10Context cxt) {
		X10Flags flags = X10Flags.toX10Flags(id.flags());
		
		// A static initializer executes at place 0.
		if (flags.isStatic()) 
			return cxt.pushPlace(firstPlace((X10TypeSystem) id.typeSystem()));
		
		// A struct instance initializer, executes at the current place, 
		// but we have no way of referring to it, hence we assume it is some new unknown place.
		if (X10TypeMixin.isX10Struct(id.container().get())) 
			return 	cxt.pushPlace(XConstrainedTerm.make(makePlace()));
		
		// An instance initializer, executes at this.home
		return pushHereIsThisHome(cxt);
	}
	public static Context pushHereTerm(FieldDef fd, X10Context c) {
		X10Flags flags = X10Flags.toX10Flags(fd.flags());
		if (flags.isStatic()) 
			return c.pushPlace(firstPlace((X10TypeSystem) fd.typeSystem()));
		if (X10TypeMixin.isX10Struct(fd.container().get())) 
			return 	c.pushPlace(XConstrainedTerm.make(makePlace()));
		X10TypeSystem xts = (X10TypeSystem) c.typeSystem();
		X10Context xc = (X10Context) c;
		ClassDef cd = c.currentClassDef();
		
		// bypass Any to avoid infinite recursion (pushPlace will again look for Any.home..)
		// This means that the types in Any cannot reference here.
		if (cd != null)
		if ( ! xts.isAny(cd.asType())) {
			XTerm h =  homeVar(xc.thisVar(),xts);
			if (h != null)  // null for structs.
				return((X10Context) c).pushPlace(XConstrainedTerm.make(h)); 	
		}
		return c;
	}
	
	public static XTerm methodPT(Flags flags, ClassDef ct) {
		X10Flags xflags = X10Flags.toX10Flags(flags);
    	boolean isGlobal = xflags.isGlobal() || xflags.isStatic() || X10TypeMixin.isX10Struct(ct.asType());
    	return (isGlobal) ? 
    			makePlace() :
    				homeVar(((X10ClassDef) ct).thisVar(), (X10TypeSystem) ct.typeSystem());
	}
	/**
	 * The method is global if it declared with a global flag or if it is a static method, or
	 * if it a method of a struct
	 * @param md
	 * @return
	 */
	static boolean isGlobalCode(MethodDef md) {
    	X10Flags flags = X10Flags.toX10Flags(md.flags());
    	boolean isGlobal = flags.isGlobal() || flags.isStatic() || X10TypeMixin.isX10Struct(md.container().get());
    	return isGlobal;
	}
	
	/**
	 * 
	 * @param xc -- the current context
	 * @return -- the current context (if within a struct), else a context
	 * obtained by setting the new place term to be this.home.
	 */
	public static Context pushHereIsThisHome(X10Context cxt) {
		XTerm h = thisHomeVar(cxt);
		if (h != null)  // null for structs.
			return cxt.pushPlace(XConstrainedTerm.make(h)); 	
		return cxt;
	}
	

	 /**
     * Check that the type system asserts r.home==here.
     * @param r
     * @param context
     * @return
     */
	static boolean isHere(Receiver r, X10Context xc) {
		   XConstrainedTerm h = xc.currentPlaceTerm();
		  // assert h != null;
		   return isAtPlace(r, h==null ? null : h.term(), xc);
	   }
    
    /**
     * Returns true if the receiver r is known statically to be at place.
     * Will always return false if place is global.
     * @param r
     * @param place
     * @param context
     * @return
     */
	public static boolean isAtPlace(Receiver r, Expr place, X10Context xc) {
    	assert place != null;
    	CConstraint_c c = new CConstraint_c();
    	XTerm placeTerm = ((X10TypeSystem) xc.typeSystem()).xtypeTranslator().trans(c, place, xc);
    	if (placeTerm == null) {
    		assert false;  // Maybe should create a UQV? place might be a method call...?
    		return false;
    	}
    	return isAtPlace(r, placeTerm, xc);
    }
    
	
	  /**
     * Returns true if the receiver r is known statically to be at placeTerm.
     * Will always return false if placeTerm is globalPlace().
     * @param r
     * @param placeTerm
     * @param context
     * @return
     */

    // placeTerm may be null. Nothing is known about the current place.
    // In such a case check if r.home == here, otherwise check r.home==placeTerm
    static boolean isAtPlace(Receiver r, XTerm placeTerm, X10Context xc) {
    	X10TypeSystem xts = (X10TypeSystem) xc.typeSystem();
    	// If the code is executing in a global context then
    	// no receiver can be local.
 	  // if (placeTerm != null && isGlobalPlace(placeTerm))
 		//   return false;
 	   
 		   try {
 			   XConstrainedTerm h = xc.currentPlaceTerm();
 			   CConstraint pc = h == null ? new CConstraint_c() : xc.currentPlaceTerm().xconstraint().copy();
 			   
 			   Type rType = r.type();
 			   XTerm target = X10TypeMixin.selfVarBinding(rType); 
 			   if (target != null && target.toString().contains("$dummyAsync#this")) {
 				  XRoot thisVar = xc.thisVar();
 				  for (X10Context outer = (X10Context) xc.pop();
				   outer != null && target != null && target.toString().contains("$dummyAsync#this");
				   outer = (X10Context) outer.pop())
				   {
					   target = outer.thisVar();
				   }
 			   }
 			   if (target == null) {
 				   target = xts.xtypeTranslator().trans(pc, r, xc);
 				   if (target == null)
 					   // The receiver is not named. So make up a new name.
 					   // The only thing we know about the name is that it is of rType,
 					   target = XTerms.makeUQV();
 			   } 
 			   rType = X10TypeMixin.instantiateSelf(target, rType); 
 			  
 			   assert PlaceChecker.homeVar(target, xts) != null;
 			   
 			   pc.addBinding(PlaceChecker.homeVar(target,xts), placeTerm == null ? HERE : placeTerm);
 			   
 			   CConstraint targetConstraint = X10TypeMixin.realX(rType).copy();
 			   CConstraint sigma =  xc.constraintProjection(targetConstraint, pc);
 			   if (h != null) {
 				   sigma.addBinding(HERE, h);
 			   }
 			   // Add this.here = currentThisPlace.
 			   XConstrainedTerm th = xc.currentThisPlace();
 			   if (th != null) {
 				   XRoot thisVar = xc.thisVar();
 				   for (X10Context outer = (X10Context) xc.pop();
 				   outer != null && thisVar == null;
 				   outer = (X10Context) outer.pop())
 				   {
 					   thisVar = outer.thisVar();
 				   }
 				   sigma.addBinding(th, PlaceChecker.homeVar(thisVar,xts));
 			   }
 			   // Now check the constraint.
 			   if (targetConstraint.entails(pc,sigma)) {
 				   // Gamma|- here==r.home
 				   return true;
 			   }	   
 		   } catch (XFailure z) {
 			   // fall through
 		   }
 	   
 	   return false;

    }
    
    /**
     * Check that this field access is place safe.
     * @param field
     * @param xc
     * @throws SemanticException
     */
    
    public static void checkFieldPlaceType( Field field, X10Context xc) 
    throws SemanticException {
    	X10Flags xFlags = X10Flags.toX10Flags(field.fieldInstance().flags());
    	// A global field can be accessed from anywhere.
    	if (xFlags.isGlobal())
    		return;
    	
    	// A static field can be accessed from anywhere.
    	if (xFlags.isStatic())
    		return;
    
    	X10TypeSystem ts = (X10TypeSystem) xc.typeSystem();
    	Receiver target = field.target();
    	if (! ts.isSubtype(target.type(), ts.Object(), xc))
    		return;
    
    	if (PlaceChecker.isHere(target, xc))
    		return;
    
    	XConstrainedTerm h = xc.currentPlaceTerm();
    	if (h != null && isGlobalPlace(h.term())) {
    		throw new Errors.PlaceTypeErrorFieldShouldBeGlobal(field, 
    				field.position());
    	}
    	XTerm placeTerm=null;
    	if (h!= null)
    		placeTerm = h.term();
    	XTerm targetTerm=placeTerm(target.type());
    	throw new Errors.PlaceTypeErrorFieldShouldBeLocalOrGlobal(field, 
    			placeTerm,
    			targetTerm,
    			field.position());
    }
    
    
    /* public CConstraint isHereConstraint(Receiver r, X10Context xc) {
  	   CConstraint pc = new CConstraint_c();

  	   XTerm target = xtypeTranslator().trans(pc, r, xc);
  	   assert r != null;
  	   return isHereConstraint(pc, target, xc);

     }
     public CConstraint isHereConstraint(XTerm target, X10Context xc) {
  	   CConstraint c = new CConstraint_c();
  	   return isHereConstraint(c, target, xc);
     }
     protected CConstraint isHereConstraint(CConstraint pc, XTerm target, X10Context xc) {
  	   try {
  		   XTerm eloc = xtypeTranslator().trans(pc, target, 
  				   ((StructType) Object()).fieldNamed(homeName()));
  		   // XConstrainedTerm h = xc.currentPlaceTerm();
  		   // pc.addBinding(eloc, h==null ? h.term(): XTerms.HERE);
  		   pc.addBinding(eloc, Places.HERE);
  	   } catch (XFailure z) {
  		   pc.setInconsistent();
  	   } 
  	   return pc;
     }
    */
    
    public static Call makeReceiverLocalIfNecessary(X10Call n, ContextVisitor tc) throws SemanticException {
    	try {
			checkLocalReceiver(n, tc);
		} catch (PlaceTypeErrorMethodShouldBeLocalOrGlobal z) {
			// ok, compensate by generating a dynamic cast.
			if (Configuration.STATIC_CALLS)
				throw z;
			X10Call_c result = (X10Call_c) n;
			Receiver r = result.target();
			if (r instanceof Expr) {
				Expr target = (Expr) r;
				Type type = PlaceChecker.AddIsHereClause(target.type(), tc.context());
				target = Converter.attemptCoercion(tc, target, type);
				n = (X10Call) result.reconstruct(target, result.name(), result.arguments());
			}
			
		}
		return n;
    }
    /**
     * Check that this method call is place safe (throwing an error if it isnt, i.e.
     * if the call is to a method that is not global, and the receiver
     * is not at the current place).
     */
    public static void checkLocalReceiver( Call call, ContextVisitor tc) 
	throws SemanticException {
		X10Flags xFlags = X10Flags.toX10Flags(call.methodInstance().flags());
		// A global method can be invoked from anywhere.
		if (xFlags.isGlobal())
			return;
		
		// A static method can be invoked from anywhere.
		if (xFlags.isStatic())
			return;

		X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();
		X10Context xc = (X10Context) tc.context();
		
		Receiver target = call.target();
		
		
		// Method invocations on structs are always permitted
		if (ts.isStructType(target.type()))
			return;


		if (PlaceChecker.isHere(target, xc))
			return;

		XConstrainedTerm h = xc.currentPlaceTerm();
		if (h != null && PlaceChecker.isGlobalPlace(h.term())) {
			throw new Errors.PlaceTypeErrorMethodShouldBeGlobal(call, call.position());
		}
		
		throw new Errors.PlaceTypeErrorMethodShouldBeLocalOrGlobal (call, 
				xc.currentPlaceTerm()==null? null: xc.currentPlaceTerm().term(), 
						placeTerm(target.type()),
						call.position());
	}
    /**
     * Returns true if the placeChecker cannot establish that e is here.
     * @param e
     * @param context
     * @return
     */
	public static boolean needsPlaceCheck(Receiver e, X10Context context) {
	    if (e instanceof X10CanonicalTypeNode_c)
	        return false;
	    Type t = e.type();
	    return !isHere(e, context);
	}
	/*  @Override
	    public NodeVisitor typeCheckEnter(TypeChecker v) throws SemanticException {
	    	if (placeTerm != null) {
	    		v = (TypeChecker) v.context(pushPlaceTerm((X10Context) v.context()));
	    	}
	    	return v;
	    	
	}
	*/
	    //XTerm placeTerm;
	    /**
	     * The type of the place term. May be Ref or Place. May contain a newly generated
	     * var, equated to self. The associated constraint must be considered to be in scope
	     * when examining the body of this PlacedClosure.
	     */
	   // Type placeType;
	    
	    public static XConstrainedTerm computePlaceTerm( Expr place, X10Context xc, 
	    		X10TypeSystem  ts
	    		) throws SemanticException {
	 		Type placeType = place.type();
			CConstraint d = X10TypeMixin.xclause(placeType);
			d = (d==null) ? new CConstraint_c() : d.copy();
			CConstraint pc = null;
			XTerm term = null;
			XConstrainedTerm pt = null;
	    	boolean placeIsPlace = ts.isImplicitCastValid(placeType, ts.Place(), xc);
	    	if (placeIsPlace)  {
	    		term = ts.xtypeTranslator().trans(pc, place, xc);
	    		if (term == null) {
	    			term = XTerms.makeUQV();
	    		}
	    		try {
	    			pt = XConstrainedTerm.instantiate(d, term);
				} catch (XFailure z) {
	
					throw new InternalCompilerError("Cannot construct placeTerm from " + 
							term + " and constraint " + d + ".");
				}
	    	} else {
	    		boolean placeIsRef = true; // ts.isImplicitCastValid(placeType, ts.Object(), xc);
	    		if (placeIsRef) {
	    			XTerm src = ts.xtypeTranslator().trans(pc, place, xc);
	    			if (src == null) {
	    				src = XTerms.makeUQV();
	    			}
	    			try {
	    				d= d.substitute(src, d.self());
	    				pt = XConstrainedTerm.make(homeVar(src,ts), d);
	    			} catch (XFailure z) {
	    				assert false;
	    				throw new InternalCompilerError("Cannot construct placeTerm from " + 
	    					 place + " and constraint " + d + ".");
	    			}
	    		} else 
	    			throw new SemanticException(
	    					"Place expression |" + place + "| must be of type \"" +
	    					ts.Place() + "\", or " + ts.Object() + ", not \"" + place.type() + "\".",
	    					place.position());
	    	}
	    
	    	return pt;
	    }

	    public static XTerm rewriteAtClause(CConstraint c, X10MethodInstance xmi, Call t, XTerm r, X10Context xc) throws SemanticException {
	    	X10TypeSystem  ts = (X10TypeSystem) xc.typeSystem();
	    	XTypeTranslator tr = ts.xtypeTranslator();
	    	if (xmi.name().equals(Name.make("at"))
	    			&& ts.typeEquals(xmi.def().container().get(), ts.Any(), xc)
	    			&& t.arguments().size()==1) {
	    		FieldInstance fi = ts.findField(ts.Any(), ts.FieldMatcher(ts.Object(), 
	    				ts.homeName(), xc));
	    		XTerm lhs =  tr.trans(c, r, fi, ts.Place());

	    		// replace by r.home == arg0 or r.home == arg0.home
	    		Expr arg = t.arguments().get(0);
	    		XTerm y = tr.trans(c, arg, xc);
	    		if (y == null)
	    			throw new SemanticException("Cannot translate " + arg + " to a constraint term.",
	    					arg.position()
	    			);
	    		y = ts.isSubtype(xmi.formalTypes().get(0), ts.Place(), xc) 
	    		?   y : tr.trans(c, y, fi, ts.Place());

	    		return XTerms.makeEquals(lhs, y);
	    	}
	    	return xmi.body();
	    }
	    
	    
    
}
