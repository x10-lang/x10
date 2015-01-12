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

package x10.types.matcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import polyglot.types.Context;
import polyglot.types.Flags;
import polyglot.types.InitializerDef;
import polyglot.types.LazyRef_c;
import polyglot.types.LocalInstance;
import polyglot.types.LocalInstance_c;
import polyglot.types.Name;
import polyglot.types.ProcedureDef;
import polyglot.types.Ref;
import polyglot.types.UnknownType;

import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.Types;
import polyglot.util.Position;
import polyglot.util.Transformation;
import polyglot.util.TransformingList;
import x10.constraint.XFailure;
import x10.constraint.XVar;
import x10.constraint.XTerm;
import x10.types.constraints.ConstraintManager;
import x10.errors.Errors;

import x10.types.ConstrainedType;
import x10.types.ParameterType;
import x10.types.X10ClassDef;
import x10.types.X10CodeDef;
import x10.types.X10LocalDef;
import x10.types.X10LocalDef_c;
import x10.types.X10LocalInstance;
import x10.types.X10LocalInstance_c;
import x10.types.X10ProcedureDef;
import x10.types.X10ProcedureInstance;
import x10.types.MacroType;
import polyglot.types.TypeSystem;
import x10.types.checker.PlaceChecker;
import x10.types.constraints.CConstraint;
import x10.types.constraints.CLocal;
import x10.types.constraints.CNativeRequirement;
import x10.types.constraints.ConstraintMaker;
import x10.types.constraints.SubtypeConstraint;
import x10.types.constraints.TypeConstraint;
import x10.types.constraints.XConstrainedTerm;

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
	    PI newMe = instantiate(context, me, thisTypeArray, typeActuals, actuals,  true);

	    return newMe;
	}

	public static <PI extends X10ProcedureInstance<?>> PI instantiate(Context context, PI me, 
			Type thisType, 
			List<Type> typeActuals, 
			final List<Type> actuals) throws SemanticException {
	   
	    PI me2 = instantiate(context, me, new Type[] {thisType}, typeActuals, actuals, false);
	    return me2;
	}

	/**
	 * This method is the heart of method and constructor call type-checking. This
	 * is a hot spot for X10 compilation.
	 * 
	 * <p> It takes a context, an X10ProcedureInstance (this can represent a new or 
	 * method invocation or closure invocation), the array of types for 
	 * referenced this variables (including :"outer" this variables), the 
	 * actual type parameters to the invocation, and the list of types of 
	 * the actual arguments, and an indication for whether checks are to be done 
	 * or not. 
	 * 
	 * <p> Let the PI (me) be of the form def m[~X](~x:~S){c}:T. Let the 
	 * actual call be of the form e.m[~V](~a), where e:U and ~a:~W.
	 * 
	 * <p> If a PI is returned, then it will be of the form 
	 * (def m[~V](~y:~S'){c'}:T') where S' = S[~y/~x][~V/~X] and with the this's 
	 * replaced as well,and c' and T' are obtained from c and T in a similar
	 * fashion. Thus PI returns an instance of me whose 
	 * 
	 * <p> Note: In order to obtain the parameters of the original 
	 * method/constructor definition, please use mi.def(), where mi is the 
	 * return value of this method.
	 * 
	 * <p> If checking is turned on, a SemanticException is thrown if 
	 * the type of an actual is inconsistent  or is not a subtype of the 
	 * corresponding formal in newMe, or the guard is inconsistent or not 
	 * satisfied.
	 * 
	 * @param <PI>  -- The type of the formal descriptor
	 * @param context -- The context in which the type-checking is being done
	 * @param me -- The formal descriptor for this call
	 * @param thisTypeArray -- An inout parameter containing thisType. 
	 * @param typeActuals -- The actual type parameters to the call
	 * @param actualsIn  -- The types of the actual parameters for the call.
	 * @param checkActuals -- Check actual types are subtypes of the formal
	 * and the guard is satisfied.
	 * @return  -- An instantiated version of me, with actuals substituted for 
	 * formals in actual types and return types. 
	 * @throws SemanticException
	 */
	private static <PI extends X10ProcedureInstance<?>> PI instantiate(
			final Context context, 
			final PI me, 
			final /*inout*/ Type[] thisTypeArray,  
			List<Type> typeActuals, 
			final List<Type> actualsIn, 
			boolean checkActuals) throws SemanticException
			{
		final XVar[] ys = new XVar[actualsIn.size()+1];
	
		final TypeSystem xts = (TypeSystem) me.typeSystem();

		List<Type> formals = new ArrayList<Type>();
		for (Type formal : me.formalTypes()) {
	        	//formal = PlaceChecker.ReplaceHereByPlaceTerm((Type) formal.copy(), context);
	        	formals.add(formal);
		}
		final List<LocalInstance> formalNames = me.formalNames();
		final List<Type> typeFormals = me.typeParameters();
		final boolean isStatic = Types.isStatic(me);
		if (actualsIn.size() != formals.size())
	            throw new SemanticException("Call not valid; incorrect number of actual arguments.", me.position());

		if (typeActuals.size() != typeFormals.size())
	            throw new SemanticException("Call not valid; incorrect number of actual type arguments.", me.position());

		formals = Types.expandTypes(formals, xts);

		final List<Type> actuals = Types.expandTypes(actualsIn, xts);

        // actuals cannot be of void type
        for (Type actual : actuals)
            if (xts.isVoid(actual))
                throw new SemanticException("An actual cannot have a 'void' type.", me.position());


		Type thisType = thisTypeArray[0];

		final XVar ythiseqv =  ys[0] = getSymbolVar(thisType);
		
		XVar st = Types.selfVarBinding(thisType);
		final boolean yeqvIsSymbol = (! isStatic) && (st !=null);
		if (! isStatic) {
			if (st == null)
				thisType = Types.instantiateSelf(ythiseqv, thisType);
		}
		final Type thisTypeFinal = thisType;
		thisTypeArray[0] = thisType;

		XVar[] x = getSymbolicNames(me.formalNames(), xts); 

		// Generate new local variable y's. These are local variables, from which
		// XVar's can be generated as needed.
		// We will need to substitute the symbolic names generated from 
		// these new local variables for the xi.
		// Then, in the newMe that is returned the formal names will be set 
		// to these new local variables so that the newMe will be consistent.
		// i.e. the variables occurring in the constraints of the arg
		// types in newMe will be the formals. 
		// This fixes a major bug in which the variables in constraints
		// got disconnected from the formals, and uqvs would sometimes show
		// up in error messages. This also supports implicit coercions
		// performed using the data in the MI returned from this call.
		// See Converter.tryImplicitConversions.
		final List<LocalInstance> yLocalInstances = getSymbolicNames(actuals);
		
		final XVar[] ySymbols = getSymbolicNames(yLocalInstances, xts);
		System.arraycopy(ySymbols, 0, ys, 1, actuals.size());


		final CConstraint returnEnv = computeNewSigma(thisType, actuals, ythiseqv, ySymbols, isStatic, xts);
		final CConstraint returnEnv2 = computeNewSigma2(thisType, actuals, ythiseqv, ySymbols, isStatic, xts);

		// We'll subst selfVar for THIS.
		XVar xthis = null;

		if (! isStatic ) {
			if (me.def() instanceof X10ProcedureDef)
				xthis = (XVar) ((X10ProcedureDef) me.def()).thisVar();

			if (xthis == null)
				xthis = ConstraintManager.getConstraintSystem().makeThis(); 
		}

		final XVar codePlace = Types.getPlaceTerm(me);
		XConstrainedTerm currentPlaceTerm = context.currentPlaceTerm();
		final XTerm currentPlace = currentPlaceTerm != null ? currentPlaceTerm.term() : ConstraintManager.getConstraintSystem().makeEQV();

		final ParameterType[] X = new ParameterType[typeFormals.size()];
		final Type[] Y = new Type[typeFormals.size()];
		for (int i = 0; i < typeFormals.size(); i++) {
	            Type xtype = xts.expandMacros(typeFormals.get(i));
	            Y[i] = xts.expandMacros(typeActuals.get(i));
	            Y[i] = Subst.subst(Y[i], currentPlace, codePlace);
	           
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
	        				Type rt = me.returnType(); // may be a macrotype
	        				rt = Subst.subst(rt, currentPlace, codePlace);
	        				Type newReturnType = Subst.subst(rt, y2eqv, x2, Y, X);
	        				// Replace all outer#this variables in the constraint
	        				// (if any) by QualifiedVar's.
	        				if (newReturnType instanceof ConstrainedType) {
	        				    if (! isStatic) {
	        				        List<X10ClassDef> outers = Types.outerTypes(thisTypeFinal);
	        				        // Do not replace the lowest level this by qvar's -- only outer this.
	        				        // That will be taken care of by existing code. 
	        				        if (outers != null && outers.size() > 1) {
	        				            XVar[] outerThis = new XVar[outers.size()-1];
	        				            XVar[] outerYs = new XVar[outers.size()-1];
	        				            for (int i=1; i < outers.size(); ++i) {
	        				                outerYs[i-1] = ConstraintManager.getConstraintSystem().makeQualifiedVar(outers.get(i).asType(), (XVar) y2eqv[0]);
	        				                outerThis[i-1]= outers.get(i).thisVar();
	        				            }
	        				            newReturnType = Subst.subst(newReturnType, outerYs, outerThis);
	        				        } 
	        				    }
	        				}
	        				if (! newReturnType.isVoid() && ! xts.isUnknown(newReturnType)) {
	        				    Type nrt = Subst.addIn(newReturnType, returnEnv2);
	        				    if (xts.consistent(nrt, context))
	        				        newReturnType = nrt;
	        				    if ((! isStatic) && (! yeqvIsSymbol) ) {
	        				        nrt = Subst.project(newReturnType, ythiseqv);
	        				        if (xts.consistent(nrt, context))
	        				            newReturnType = nrt;
	        				    }
	        				    for (int i= 1; i < actuals.size()+1; ++i) {
	        				        nrt = Subst.project(newReturnType, (XVar) ys[i]);  
	        				        if (xts.consistent(nrt, context))
	        				            newReturnType = nrt;
	        				        Type t = actualsIn.get(i-1);
	        				        XVar self = t instanceof ConstrainedType ? Types.selfVar((ConstrainedType) t) : null;
	        				        if (self != null) {
	        				            nrt = Subst.project(newReturnType, self);
	        				            if (xts.consistent(nrt, context))
	        				                newReturnType = nrt;
	        				        }
	        				    }
	        				}
	        				if (! xts.consistent(newReturnType, context)) {
	        				    throw new Errors.InconsistentReturnType(newReturnType, me);
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

		{ // set up the new formal types.  These are obtained from the  
			// formal types by replacing x's by y's and this by the yeqv, and 
			// substituting in type parameters. With this normalization, 
			// checkCall will simply have to check that the types of the 
	        // actuals are a subtype of the formals.
	        // substitute in the information about this.
			List<Type> newFormalTypes = new ArrayList<Type>();
			if (checkActuals) {
				for (Type t : formals) {
					t = Subst.subst(t, y2eqv, x2, Y, X); 
					t = Subst.subst(t, currentPlace, codePlace);
					newFormalTypes.add(t);
				}
			} else 	{
				CConstraint env = null; 
				if (! isStatic) {
					env = Types.xclause(thisType);
					if (env != null && ythiseqv != null && ! ((env == null) || env.valid())) {
						env = env.instantiateSelf(ythiseqv);
					}
				}
				for (Type t : formals) {
					t = Subst.subst(t, y2eqv, x2, Y, X); 
					t = Subst.subst(t, currentPlace, codePlace);
					if (! (env == null || env.valid())) {
						if (! isStatic)
							t = Subst.addIn(t, env); 
					}
					if (! isStatic && ! yeqvIsSymbol) {
						t = Subst.project(t, (XVar) ys[0]);
					}

					newFormalTypes.add(t);
				}
			} 
			newMe = (X10ProcedureInstance<?>) newMe.formalTypes(newFormalTypes);
		}

		{ // set up the new formals as well
			// the formal parameters might appear in constraints. 
			// Note that checkActuals may be false, so we are not going to check
			// now simply return a newMe which will be used later in coercions 
			newMe = newMe.formalNames(yLocalInstances);
			// At this point each yLocalInstances(i) has the type of the
			// corresponding actual. This will be replaced after checking with the 
			// formal type. They type cannot be changed to the formal type before
			// checking.
		}
		{ // set up throws types
            List<Type> newThrowTypes = new ArrayList<Type>();
            // [DC] don't think we allow constraints on throws types
            for (Type t :  Types.expandTypes(me.throwTypes(), xts)) {
                t = Subst.subst(t, y2eqv, x2, Y, X); 
                // [DC] no idea what this place stuff is for
                t = Subst.subst(t, currentPlace, codePlace);
                newThrowTypes.add(t);
            } 
            newMe = (X10ProcedureInstance<?>) newMe.throwTypes(newThrowTypes);
		}
		{ // set up the guard.
	        	CConstraint newWhere = Subst.subst(me.guard(), y2eqv, x2, Y, X); 
	        	newWhere = Subst.subst(newWhere, currentPlace, codePlace);
	        	newMe = newMe.guard(newWhere);
		}
		{   // set up the type guard.
	        	TypeConstraint newTWhere = Subst.subst(me.typeGuard(), y2eqv, x2, Y, X);
	        	newTWhere = Subst.subst(newTWhere, currentPlace, codePlace);
	        	newMe = newMe.typeGuard(newTWhere);
		}
		if (! checkActuals) {
			// Update the types to reflect the newly computed formalTypes before returning.
			for (int i=0; i < yLocalInstances.size(); i++) {
				LocalInstance li = yLocalInstances.get(i);
				Ref<Type> ref = (Ref<Type>) li.def().type();
				ref.update(newMe.formalTypes().get(i));
			}
			return (PI) newMe;
		}

		// Now check that the actual types are a subtype of the formal types,
        // and the method guards are satisfied.
		final Context context2 = context.pushAdditionalConstraint(returnEnv, me.position());
		final CConstraint query = newMe.guard();
		X10CompilerOptions opts = (X10CompilerOptions) context.typeSystem().extensionInfo().getOptions();

		// we can do dynamic checks on method calls when using DYNAMIC_CHECKS or VERBOSE_CHECKS
		boolean dynamicChecks = !opts.x10_config.STATIC_CHECKS &&
		!(newMe instanceof MacroType); // MacroType cannot have its guard checked at runtime

		boolean inferGuard = false;
		ProcedureDef procDef = null;
		if (opts.x10_config.CONSTRAINT_INFERENCE) {
			if (context2.currentCode() != null) { // FIXME check why it can be null
				if (context2.currentCode() instanceof ProcedureDef) {
					procDef = (ProcedureDef) context2.currentCode();
					inferGuard = procDef.inferGuard();
				}
				else if (context2.currentCode() instanceof InitializerDef) {
					// FIXME check if we have some thing to do
				}
				else {
					assert false: "context2.currentCode().getClass() = " + context2.currentCode().getClass();
				}
			}
		}

		if ( query != null) {
			if (! query.consistent())
				throw new SemanticException("Call invalid; guard inconsistent for actual parameters of call.");
			if (! returnEnv.entails(query,
					new ConstraintMaker() {
				public CConstraint make() throws XFailure {
					return context2.constraintProjection(returnEnv, query);
				}})) {
				if (inferGuard) {
					try {
						procDef.requirements().add(returnEnv, query, context2);
					} catch (XFailure e) {
						throw new SemanticException("Call invalid; calling environment does not entail the method guard."
								+ "\n\t arg types:" + actualsIn
								+ "\n\t query residue: " + returnEnv.residue(query));
					}
				}
				if (dynamicChecks || inferGuard)
					// TODO we do not have to check constraints at runtime with inferGuard
					newMe = newMe.checkConstraintsAtRuntime(true);
				else
					throw new SemanticException("Call invalid; calling environment does not entail the method guard."
							+ "\n\t arg types:" + actualsIn
							+ "\n\t query residue: " + returnEnv.residue(query));
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
				throw new SemanticException("Type guard " + tQuery 
						+ " cannot be established; inconsistent in calling context.");
			}
			if (! tenv.entails(tQuery, context2)) {
			    throw new SemanticException("Call invalid; calling environment does not entail the method type guard."
			                                + "\n\t Type args:" + typeActuals
			                                + "\n\t Type env:" + tenv
			                                + "\n\t Query residue: " + tQuery);
			}
		}

		final List<Type> myFormals =  new ArrayList<Type>(newMe.formalTypes()); // copy 
		for (int i = 0; i < formals.size(); i++) {
			Type ytype =  Subst.subst(actuals.get(i), y2eqv, x2, Y, X);
			Type xtype = Subst.subst(myFormals.get(i), y2eqv, x2, Y, X); 

			if (! xts.consistent(xtype, context2)) {
				throw new SemanticException("Parameter type " + xtype 
						+ " of call is inconsistent in calling context.");
			}
			if (! xts.isSubtype(ytype, xtype, context2)) {
				if ((dynamicChecks || inferGuard) && xts.isSubtype(Types.baseType(ytype), 
						Types.baseType(xtype), context2)) {
					if (inferGuard) {
						try {
							procDef.requirements().add(ytype, xtype, context2);
						} catch (XFailure e) {
							throw Errors.InvalidParameter.make(i, newMe, ytype, xtype, context2, me.position());
						}
					}
					// TODO we do not have to check constraints at runtime with inferGuard
					newMe = newMe.checkConstraintsAtRuntime(true);
				} else {
					throw Errors.InvalidParameter.make(i, newMe, ytype, xtype, context2, me.position());
				}
			}
		}
		// Update the types to reflect the newly computed formalTypes.
		// These have now been verified, unless dynamicChecks is true
		// in which case Desugarer will generate the checks to 
		// ensure that the actuals meet the constraints of the formalTypes.
		for (int i=0; i < yLocalInstances.size(); i++) {
			LocalInstance li = yLocalInstances.get(i);
			Ref<Type> ref = (Ref<Type>) li.def().type();
			ref.update(newMe.formalTypes().get(i));
		}
		return (PI) newMe;
	}

	
	/**
	 * Return the conjunction of the constraints from the receiver and 
	 * actuals.
	 * @param thisType
	 * @param actuals
	 * @param ythis
	 * @param y
	 * @param isStatic
	 * @param xts
	 * @return
	 * @throws SemanticException
	 */
	private static CConstraint computeNewSigma(Type thisType, List<Type> actuals, 
			XVar ythis, XVar[] y, boolean isStatic, TypeSystem xts) 
	throws SemanticException {
	
		CConstraint env = null; 
		if (! isStatic) {
			env = Types.xclause(thisType);
			if (env != null && ythis != null && ! ((env == null) || env.valid()))
				env = env.instantiateSelf(ythis);
		}
		if (env == null)
			env = ConstraintManager.getConstraintSystem().makeCConstraint();

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
	
	/**
	 * Return thisType's xclause, with ythis substituted for self, and
	 * with each ag y(i)'s constraint added in (with y(i)/self). 
	 * The return type may reference an arg, 
	 * @param thisType
	 * @param actuals
	 * @param ythis
	 * @param y
	 * @param isStatic
	 * @param xts
	 * @return
	 * @throws SemanticException
	 */
	private static CConstraint computeNewSigma2(Type thisType, List<Type> actuals, 
			XVar ythis, XVar[] y,  boolean isStatic, TypeSystem xts) 
	throws SemanticException {
	
		CConstraint env = null; 
		if (! isStatic) {
			env = Types.xclause(thisType);
			if (env != null && ythis != null && ! ((env == null) || env.valid()))
				env = env.instantiateSelf(ythis);
		}
		if (env == null)
			env = ConstraintManager.getConstraintSystem().makeCConstraint();
	
		//To do: Not sure these need to be added to Gamma. Constraint projection will retrieve them
		// from the types of the variables.
	    for (int i = 0; i < actuals.size(); i++) { // update Gamma
	    		Type ytype = actuals.get(i);
	    		final CConstraint yc = Types.realX(ytype);
	    		if (yc != null && ! yc.valid()) {
	    		    env.addIn(y[i], yc);
	    		    if (! env.consistent()) {
	    		        throw new Errors.InconsistentContext(ytype, Position.COMPILER_GENERATED); 
	    		    }
	    		}
	    }
	    return env;
	}
	
	
	/**
	 * If the given type says that self == x, then return x. 
	 * Otherwise make  up a new symbol (with no associated type
	 * information) and return it. 
	 * @param type
	 * @param prefix
	 * @return
	 */
	 private static XVar getSymbolVar(Type type) {
   	  XVar symbol = Types.selfVarBinding(type);
         if (symbol == null) {
       	  symbol = ConstraintManager.getConstraintSystem().makeUQV();  
         }
         return symbol;
   }
    private static X10LocalInstance getSymbol( Type type) {
    	TypeSystem ts = type.typeSystem();
    	Ref<Type> ref = new LazyRef_c<Type>(type);
    	X10LocalDef def = new X10LocalDef_c(ts, 
    			Position.compilerGenerated(type.position()), Flags.FINAL, ref,
    			Name.makeFresh("arg"));
    	X10LocalInstance li = new X10LocalInstance_c(ts, 
    			Position.compilerGenerated(type.position()),
    			new LazyRef_c<X10LocalDef>(def));
    	if (! (type instanceof UnknownType))
    		ref.update(Types.addSelfBinding(type, type.typeSystem().xtypeTranslator().translate(li)));
    	return li;

    }

     private static List<LocalInstance> getSymbolicNames(List<Type> actuals) {
    	  List<LocalInstance> ySymbols = new ArrayList<LocalInstance>(actuals.size());
          for (Type actual : actuals) {
        	  ySymbols.add(getSymbol(actual));
          }
          return ySymbols;
    }
     
    public static XVar[] getSymbolicNames(List<? extends LocalInstance> formalNames, TypeSystem xts) 
    throws SemanticException {
    	 XVar[] x = new XVar[formalNames.size()];
         for (int i = 0; i < formalNames.size(); i++) {
             x[i]=xts.xtypeTranslator().translate(formalNames.get(i));
             assert x[i] != null;
         }
         return x;
    }


}
