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

package x10.types.matcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import polyglot.types.Context;
import polyglot.types.LazyRef_c;
import polyglot.types.LocalInstance;

import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.Types;
import polyglot.util.Position;
import x10.constraint.XFailure;
import x10.constraint.XVar;
import x10.constraint.XTerm;
import x10.constraint.XTerms;
import x10.errors.Errors;
import x10.types.ParameterType;
import x10.types.X10ProcedureDef;
import x10.types.X10ProcedureInstance;
import x10.types.MacroType;
import polyglot.types.TypeSystem;
import x10.types.constraints.CConstraint;
import x10.types.constraints.CTerms;
import x10.types.constraints.ConstraintMaker;
import x10.types.constraints.SubtypeConstraint;
import x10.types.constraints.TypeConstraint;
import x10.X10CompilerOptions;


/**
 * A set of static methods used for matching types and type elements (fields, constructors, methods).
 * 
 * @author vj 2/6/2010
 *
 */
public class Matcher {

	// Generic method resolution interacts with type inference.
	// The new (2.1) semantics requires that for instance methods, given a this Type
	// t and a method name m and actual type args, we proceed in two stages.
	// First we determine the set S of applicable and available methods.
	// 
	public static <PI extends X10ProcedureInstance<?>> PI inferAndCheckAndInstantiate(Context context, PI me, 
			Type thisType, 
			List<Type> typeActuals, 
			final List<Type> actuals,
			Position pos) throws SemanticException {
	    final List<Type> typeFormals = me.typeParameters();
	    final List<Type> formals = me.formalTypes();
	
	    if (typeActuals.isEmpty() && ! typeFormals.isEmpty()) {
	        Type[] Y = TypeConstraint.inferTypeArguments(me, thisType, actuals, formals, typeFormals, context);
	        return inferAndCheckAndInstantiate(context, me, thisType, Arrays.asList(Y), actuals, pos);
	    }
	    
	    // Instantiate the proposed PI.
	    Type[] thisTypeArray = new Type[] { thisType };
	    PI newMe = instantiate2(context, me, thisTypeArray, typeActuals, actuals,  true);

	    return newMe;
	}

	public static <PI extends X10ProcedureInstance<?>> PI instantiate(Context context, PI me, 
			Type thisType, 
			List<Type> typeActuals, 
			final List<Type> actuals) throws SemanticException {
	   
	    PI me2 = instantiate2(context, me, new Type[] {thisType}, typeActuals, actuals, false);
	    return me2;
	}

	/**
	 * This is the heart of method and constructor call type-checking.
	 * @param <PI>  -- The type of the formal descriptor
	 * @param context -- The context in which the type-checking is being done
	 * @param me -- The formal descriptor for this call
	 * @param thisTypeArray -- An inout parameter containing thisType. 
	 * @param typeActuals -- The actual type parameters to the call
	 * @param actuals  -- The types of the actual parameters for the call.
	 * @param ys   -- An inout parameter, on completion contains a symbolic name for the target and each parameter
	 * @param hasSymbol -- An inout parameter of the same size as ys. On completion, the ith entry is true iff a symbolic name was generated for ys[i].
	 * @return  -- An instantiated version of me, with actuals substituted for formals in actual types and return types. 
	 * @throws SemanticException
	 */
	private static <PI extends X10ProcedureInstance<?>> PI instantiate2(final Context context, final PI me, 
	    		/*inout*/ Type[] thisTypeArray,  
	    		List<Type> typeActuals, 
	    		List<Type> actuals, 
	    		boolean checkActuals) throws SemanticException
	{
		final XVar[] ys = new XVar[actuals.size()+1];
		final  boolean[] hasSymbol = new boolean[actuals.size()+1];
		final TypeSystem xts = (TypeSystem) me.typeSystem();

		List<Type> formals = new ArrayList<Type>();
		for (Type formal : me.formalTypes()) {
	        	//formal = PlaceChecker.ReplaceHereByPlaceTerm((Type) formal.copy(), context);
	        	formals.add(formal);
		}
		final List<LocalInstance> formalNames = me.formalNames();
		final List<Type> typeFormals = me.typeParameters();
		final boolean isStatic = Types.isStatic(me);
		if (actuals.size() != formals.size())
	            throw new SemanticException("Call not valid; incorrect number of actual arguments.", me.position());

		if (typeActuals.size() != typeFormals.size())
	            throw new SemanticException("Call not valid; incorrect number of actual type arguments.", me.position());

		formals = Types.expandTypes(formals, xts);

		actuals = Types.expandTypes(actuals, xts);

		Type thisType = thisTypeArray[0];

		final XVar ythiseqv =  ys[0] = getSymbol(thisType);
		if (! isStatic) {
	        	XVar st = Types.selfVarBinding(thisType);
	        	hasSymbol[0] = st != null; // if true, a UQV was not generated.
	        	thisTypeArray[0] = thisType = Types.instantiateSelf(ythiseqv, thisType);
		}

		// useful for uniformity. Note some of the formal parameters may not have names.
		// (This does mean that the parameter can therefore not occur in other types, so
		// we should not have to generate a symbolic name for it anyway. Here we do this just for simplicity.)

		XVar[] x = getSymbolicNames(formals, me.formalNames(), xts); 

		// hasSymbol[i] iff actuals[i] already has a symbolic value, and hence does not need a gensym.
		//final boolean[] haveSymbols = haveSymbolicNames(actuals);
		hasSymbolicNames(hasSymbol, 1, actuals);
		final XVar[] ySymbols = getSymbolicNames(actuals);
		System.arraycopy(ySymbols, 0, ys, 1, actuals.size());


		final CConstraint returnEnv = Matcher.computeNewSigma(thisType, actuals, ythiseqv, ySymbols, hasSymbol, isStatic, xts);
		final CConstraint returnEnv2 = Matcher.computeNewSigma2(thisType, actuals, ythiseqv, ySymbols, hasSymbol, isStatic, xts);


		// We'll subst selfVar for THIS.
		XVar xthis = null; // xts.xtypeTranslator().transThis(thisType);

		if (! isStatic ) {
	        	if (me.def() instanceof X10ProcedureDef)
	        		xthis = (XVar) ((X10ProcedureDef) me.def()).thisVar();

	        	if (xthis == null)
	        		xthis = CTerms.makeThis(); // XTerms.makeLocal(XTerms.makeFreshName("this"));
		}
		// update each type in formals, with ythiseqv substituted for xthis (if ! isStatic),
		// and ySymbols substituted for x.
		// Matcher.updateFormalTypes(formals, ythiseqv, xthis, ySymbols, x, isStatic);

		final ParameterType[] X = new ParameterType[typeFormals.size()];
		final Type[] Y = new Type[typeFormals.size()];
		for (int i = 0; i < typeFormals.size(); i++) {
	            Type xtype = xts.expandMacros(typeFormals.get(i));
	            Y[i] = xts.expandMacros(typeActuals.get(i));
	           
	            // TODO: should enforce this statically
	            assert xtype instanceof ParameterType : xtype + " is not a ParameterType, is a " 
	            + (xtype != null ? xtype.getClass().getName() : "null");
	            X[i] = (ParameterType) xtype;
		}

		// Start assembling the pieces of the PI with actual information.
		X10ProcedureInstance<?> newMe = me.typeParameters(Arrays.asList(Y));

		final XVar[] x2 = isStatic ? x : new XVar[x.length+2];
		final XTerm[] y2eqv = isStatic ? ySymbols  : new XTerm[ySymbols.length+2];
		if (! isStatic) {
	        	x2[0] = xthis;
	        	x2[1] = Types.thisVar(xthis, thisType);
	        	System.arraycopy(x, 0, x2, 2, x.length);

	        	y2eqv[0] = ythiseqv;
	        	y2eqv[1] = ythiseqv;
	        	System.arraycopy(ySymbols, 0, y2eqv, 2, ySymbols.length);
		}
		{ // set up the return type.
	        	final LazyRef_c<Type> newReturnTypeRef = new LazyRef_c<Type>(null);
	        	newReturnTypeRef.setResolver(new Runnable() {
	        		public void run() {
	        			try {
	        				Type rt = me.returnType();
	        				// Do not replace here by placeTerm. The return type may be used
	        				// to compute the type of a closure, e.g. () => m(...)
	        				// The type of the closure has to use here, so that 
	        			    // here can get bound to the place at the point of invocation
	        				// (rather than the point of definition). 
	        			
	        				Type newReturnType = Subst.subst(rt, y2eqv, x2, Y, X);
	        				if (! newReturnType.isVoid() && ! xts.isUnknown(newReturnType)) {
	        					try {
	        						
	        						newReturnType = Subst.addIn(newReturnType, returnEnv2);
	        						/*CConstraint c = X10TypeMixin.realX(newReturnType);
	        						c.addIn(returnEnv);
	        						newReturnType = X10TypeMixin.xclause(X10TypeMixin.baseType(newReturnType), c);
	        						*/
	        						for (int i= isStatic ? 1 : 0; i < hasSymbol.length; ++i) {
	        							if (! hasSymbol[i]) {
	        								newReturnType = Subst.project(newReturnType, (XVar) ys[i]);  
	        							}
	        						}
	        					//	XConstrainedTerm placeTerm = ((X10Context) context).currentPlaceTerm();
	        					//	if (placeTerm != null && PlaceChecker.isGlobalPlace(placeTerm.term())) {
	        					//		newReturnType = Subst.project(newReturnType, (XVar) placeTerm.term());  
	        					//	}
	        					} catch (XFailure z) {
	        						throw new Errors.InconsistentReturnType(newReturnType, me);
	        					}
	        				}
	        				if (! xts.consistent(newReturnType, context)) {
	        					throw  new Errors.InconsistentReturnType(newReturnType, me);
	        				}
	        				newReturnTypeRef.update(newReturnType);
	        			}
	        			catch (SemanticException e) {
	        				newReturnTypeRef.update(xts.unknownType(me.position()));
	        			}
	        		} 
	        	});

	        	newMe = (X10ProcedureInstance<?>) newMe.returnTypeRef(newReturnTypeRef);
		}

		{ // set up the new formal types.  These are obtained from the real formal types
	        	// by replacing x's by y's and this by the yeqv, and substituting in type parameters.
	        	// with this normalization, checkCall will simply have to check that the types of the actuals
	        	// are a subtype of the formals.
	        	// substitute in the information about this.
	        	if (! checkActuals) {
	        		List<Type> newFormals = new ArrayList<Type>();
	        		CConstraint env = null; 
	        		if (! isStatic) {
	        			env = Types.xclause(thisType);
	        			if (env != null && ythiseqv != null && ! ((env == null) || env.valid())) {
	        				env = env.copy().instantiateSelf(ythiseqv);
	        			}
	        		}
	        		for (Type t : formals) {
	        			t = Subst.subst(t, y2eqv, x2, Y, X); 
	        			if (! (env == null || env.valid())) {
	        				try {
	        					t = Subst.subst(t, y2eqv, x2, Y, X); 
	        					if (! isStatic)
	        						t = Subst.addIn(t, env); 
	        				} catch (XFailure z) {
	        					t = xts.unknownType(me.position());
	        				}
	        			}
	        			if (! isStatic && ! hasSymbol[0]) {
	        				t = Subst.project(t, (XVar) ys[0]);
	        			}

	        			newFormals.add(t);
	        		}
	        		newMe = (X10ProcedureInstance<?>) newMe.formalTypes(newFormals);

	        	} else {
	        		List<Type> newFormals = new ArrayList<Type>();
	        		for (Type t : formals) {
	        			t = Subst.subst(t, y2eqv, x2, Y, X); 
	        			newFormals.add(t);
	        		}
	        		newMe = (X10ProcedureInstance<?>) newMe.formalTypes(newFormals);
	        	}
		}

		{ // set up the guard.
	        	CConstraint newWhere = Subst.subst(me.guard(), y2eqv, x2, Y, X); 
	        	newMe = newMe.guard(newWhere);
		}
		{   // set up the type guard.
	        	TypeConstraint newTWhere = Subst.subst(me.typeGuard(), y2eqv, x2, Y, X);
	        	newMe = newMe.typeGuard(newTWhere);
		}
		if (checkActuals) {
		    // Now check that the actual types are a subtype of the formal types, and the method guards are satisfied.
		    /*
		    CConstraint newEnv = returnEnv;
		    try {
		        XConstrainedTerm h = context.currentPlaceTerm();
		        if (h != null) {
		            newEnv = newEnv.copy();
		            newEnv.addBinding(PlaceChecker.here(), h.term());
		        }
		    } catch (XFailure z) {
		        throw new SemanticException("Inconsistent place constraints");
		    }
		    */

		    final Context context2 = context.pushAdditionalConstraint(returnEnv, me.position());
		    final CConstraint query = newMe.guard();
            X10CompilerOptions opts = (X10CompilerOptions) context.typeSystem().extensionInfo().getOptions();

            // we can do dynamic checks on method calls when using DYNAMIC_CALLS or VERBOSE_CALLS
            boolean dynamicChecks = !opts.x10_config.STATIC_CALLS &&
		                !(newMe instanceof MacroType); // MacroType cannot have its guard checked at runtime

		    if ( query != null) {
                if (! query.consistent())
                    throw new SemanticException("Call invalid; guard inconsistent for actual parameters of call.");
                if (! returnEnv.entails(query,
                                        new ConstraintMaker() {
                    public CConstraint make() throws XFailure {
                        return context2.constraintProjection(returnEnv, query);
                    }
                })) {
                    if (dynamicChecks)
                        newMe = newMe.checkConstraintsAtRuntime(true);
                    else
                        throw new SemanticException("Call invalid; calling environment does not entail the method guard.");
                }
            }

		    List<Type> typeFormals2 = newMe.typeParameters();
		    TypeConstraint tenv = new TypeConstraint();
		    for (int i = 0; i < typeFormals.size(); i++) {
		        tenv.addTerm(new SubtypeConstraint(typeFormals2.get(i), Y[i], true));
		    }

		    if (! tenv.consistent(context2)) {
		        throw new SemanticException("Call invalid; type environment is inconsistent.");
		    }
		    TypeConstraint tQuery = newMe.typeGuard();

		    if (tQuery != null) {
		        if ( ! xts.consistent(tQuery, context2)) {
		            throw new SemanticException("Type guard " + tQuery + " cannot be established; inconsistent in calling context.");
		        }
		        if (! tenv.entails(tQuery, context2)) {
		            throw new SemanticException("Call invalid; calling environment does not entail the method guard.");
		        }
		    }

		    final List<Type> myFormals =  new ArrayList<Type>(newMe.formalTypes()); // copy 
		    for (int i = 0; i < formals.size(); i++) {
		        Type ytype =  Subst.subst(actuals.get(i), y2eqv, x2, Y, X);
		        Type xtype = Subst.subst(myFormals.get(i), y2eqv, x2, Y, X); 

		        if (! xts.consistent(xtype, context2)) {
		            throw new SemanticException("Parameter type " + xtype + " of call is inconsistent in calling context.");
		        }
		        if (! xts.isSubtype(ytype, xtype, context2)) {                    
                    if (dynamicChecks && xts.isSubtype(Types.baseType(ytype), Types.baseType(xtype), context2))
                        newMe = newMe.checkConstraintsAtRuntime(true);
                    else
                        throw new Errors.InvalidParameter(ytype, xtype, me.position());
		        }
		    }
		}

		return (PI) newMe;
	}

	
	public static CConstraint computeNewSigma(Type thisType, List<Type> actuals, 
			XVar ythis, XVar[] y, boolean[] hasSymbol, boolean isStatic, TypeSystem xts) 
	throws SemanticException {
	
		CConstraint env = null; 
		if (! isStatic) {
			env = Types.xclause(thisType);
			if (env != null && ythis != null && ! ((env == null) || env.valid()))
				env = env.copy().instantiateSelf(ythis);
		}
		if (env == null)
			env = new CConstraint();

	    for (int i = 0; i < actuals.size(); i++) { // conjoin ytype's realX
	    		Type ytype = actuals.get(i);
	    		final CConstraint yc = Types.realX(ytype);
	    		if (yc != null && ! yc.valid()) {
	    		    env.addIn(y[i], yc);
	    		    if (! env.consistent())
	    		        throw new Errors.InconsistentContext(ytype, Position.COMPILER_GENERATED);
	    		}	    	
	    }
	    return env;
	}
	
	public static CConstraint computeNewSigma2(Type thisType, List<Type> actuals, 
			XVar ythis, XVar[] y, boolean[] hasSymbol, boolean isStatic, TypeSystem xts) 
	throws SemanticException {
	
		CConstraint env = null; 
		if (! isStatic) {
			env = Types.xclause(thisType);
			if (env != null && ythis != null && ! ((env == null) || env.valid()))
				env = env.copy().instantiateSelf(ythis);
		}
		if (env == null)
			env = new CConstraint();
	
	    for (int i = 0; i < actuals.size(); i++) { // update Gamma
	    	if (! hasSymbol[i+1]) {
	    		Type ytype = actuals.get(i);
	    		final CConstraint yc = Types.realX(ytype);
	    		if (yc != null && ! yc.valid()) {
	    		    env.addIn(y[i], yc);
	    		    if (! env.consistent()) {
	    		        throw new Errors.InconsistentContext(ytype, Position.COMPILER_GENERATED); 
	    		    }
	    		}
	    	}
	    }
	    return env;
	}
	
	public static XVar getSymbol(Type type) {
    	return getSymbol(type, "arg");
    }
	/**
	 * If the given type says that self == x, then return x. 
	 * Otherwise make  up a new symbol (with no associated type
	 * information) and return it. 
	 * @param type
	 * @param prefix
	 * @return
	 */
    private static XVar getSymbol(Type type, String prefix) {
    	  XVar symbol = Types.selfVarBinding(type);
          if (symbol == null) {
        	  symbol = XTerms.makeUQV(); // XTerms.makeLocal(XTerms.makeFreshName("arg"));
              // symbol = XTerms.makeUQV(XTerms.makeFreshName(prefix));
          }
          return symbol;
    }
    static void hasSymbolicNames(boolean[] hasSymbol, int start, List<Type> actuals) {
      for (int i = 0; i < actuals.size(); i++) {
    	  XVar symbol = Types.selfVarBinding(actuals.get(i));
           hasSymbol[i+start] = symbol != null;
      }
    }
     static XVar[] getSymbolicNames(List<Type> actuals) {
    	  XVar[] ySymbols = new XVar[actuals.size()];
          for (int i = 0; i < actuals.size(); i++) {
               ySymbols[i] = getSymbol(actuals.get(i)); 
          }
          return ySymbols;
    }
    public static XVar[] getSymbolicNames(List<Type> formals, List<LocalInstance> formalNames, TypeSystem xts) 
    throws SemanticException {
    	 XVar[] x = new XVar[formals.size()];
         for (int i = 0; i < formals.size(); i++) {
             x[i]=xts.xtypeTranslator().translate(formalNames.get(i));
             assert x[i] != null;
         }
         return x;
    }

	/**
	 * Update the types in formals by subsituting ythis/xthis and y/x.
	 * @param formals
	 * @param ythis
	 * @param xthis
	 * @param y
	 * @param x
	 */
	public static void updateFormalTypes(List<Type> formals, XVar ythis, XVar xthis, XVar[] y, XVar[] x, 
			boolean isStatic)
	throws SemanticException {
		for (int i=0; i < formals.size(); ++i) {
			CConstraint formalC = Types.xclause(formals.get(i));
			if (formalC != null) {
				try {
					formalC = formalC.substitute(y, x);
					// No, do not substitute y[i] for self.
					// formalC = formalC.instantiateSelf(y[i]);
					if ((! isStatic) && xthis != null)
						formalC = formalC.substitute(ythis, xthis);
					formals.set(i, Types.constrainedType(Types.baseType(formals.get(i)), 
							formalC));
				} catch (XFailure z) {
					throw new SemanticException("Call invalid; calling environment is inconsistent.");
				}
			}
		}
	}

}
