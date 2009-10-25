/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
package x10.types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import polyglot.main.Report;
import polyglot.types.ClassType;
import polyglot.types.Context;
import polyglot.types.Def;
import polyglot.types.DerefTransform;
import polyglot.types.LocalInstance;
import polyglot.types.MethodInstance;
import polyglot.types.Name;
import polyglot.types.NoClassException;
import polyglot.types.NoMemberException;
import polyglot.types.NullType;
import polyglot.types.ObjectType;
import polyglot.types.PrimitiveType;
import polyglot.types.ProcedureDef;
import polyglot.types.ProcedureInstance;
import polyglot.types.ProcedureInstance_c;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.StructType;
import polyglot.types.Type;
import polyglot.types.TypeEnv_c;
import polyglot.types.TypeSystem_c;
import polyglot.types.Types;
import polyglot.types.TypeSystem_c.TypeEquals;
import polyglot.util.CollectionUtil;
import polyglot.util.InternalCompilerError;
import polyglot.util.TransformingList;
import x10.ast.X10Special;
import x10.constraint.XConstraint;
import x10.constraint.XConstraint_c;
import x10.constraint.XFailure;
import x10.constraint.XLit;
import x10.constraint.XName;
import x10.constraint.XNameWrapper;
import x10.constraint.XRoot;
import x10.constraint.XTerms;
import x10.constraint.XVar;
import x10.types.ParameterType.Variance;
import x10.types.X10TypeSystem_c.Bound;
import x10.types.X10TypeSystem_c.Kind;

/**
 * A TypeSystem implementation for X10.
 *
 * @author Christian Grothoff
 * @author Christoph von Praun
 * @author vj
 */
public class X10TypeEnv_c extends TypeEnv_c implements X10TypeEnv {
    public X10TypeEnv_c(Context c) {
        super(c);
        this.ts = (X10TypeSystem_c) super.ts;
    }

    public X10TypeEnv_c copy() {
        return (X10TypeEnv_c) super.copy();
    }

    X10TypeSystem_c ts;

    /**
     * Assert that <code>ct</code> implements all abstract methods required;
     * that is, if it is a concrete class, then it must implement all
     * interfaces and abstract methods that it or it's superclasses declare, and if 
     * it is an abstract class then any methods that it overrides are overridden 
     * correctly.
     */
    public void checkClassConformance(ClassType ct) throws SemanticException {
        if (ct.flags().isAbstract()) {
            // don't need to check interfaces or abstract classes           
            return;
        }

        // build up a list of superclasses and interfaces that ct 
        // extends/implements that may contain abstract methods that 
        // ct must define.
        List<Type> superInterfaces = ts.abstractSuperInterfaces(ct);

        // check each abstract method of the classes and interfaces in
        // superInterfaces
        for (Iterator<Type> i = superInterfaces.iterator(); i.hasNext(); ) {
            Type it = i.next();
            if (it instanceof StructType) {
                StructType rt = (StructType) it;
                for (Iterator<MethodInstance> j = rt.methods().iterator(); j.hasNext(); ) {
                    MethodInstance mi = j.next();
                    if (!mi.flags().isAbstract()) {
                        // the method isn't abstract, so ct doesn't have to
                        // implement it.
                        continue;
                    }

                    MethodInstance mj = ts.findImplementingMethod(ct, mi, context);
                    if (mj == null) {
                        if (!ct.flags().isAbstract()) {
                            throw new SemanticException(ct.fullName() + " should be " +
                                                        "declared abstract; it does not define " +
                                                        mi.signature() + ", which is declared in " +
                                                        rt.toClass().fullName(), ct.position());
                        }
                        else { 
                            // no implementation, but that's ok, the class is abstract.
                        }
                    }
                    else if (!typeEquals(ct, mj.container()) && !typeEquals(ct, mi.container())) {
                        try {
                            // check that mj can override mi, which
                            // includes access protection checks.
                            checkOverride((MethodInstance) mj.container(ct), 
                                          (MethodInstance) mi.container(ct));
                            //	                                     checkOverride(ct, mj, mi);
                        }
                        catch (SemanticException e) {
                            // change the position of the semantic
                            // exception to be the class that we
                            // are checking.
                            throw new SemanticException(e.getMessage(),
                                                        ct.position());
                        }
                    }
                    else {
                        // the method implementation mj or mi was
                        // declared in ct. So other checks will take
                        // care of access issues
                	  checkOverride(ct, mj, mi);
                    }
                }
            }
        }
    }

    public void
    checkOverride(ClassType ct, MethodInstance mi0, MethodInstance mj0) throws SemanticException {
        X10MethodInstance mi = (X10MethodInstance) mi0;
        X10MethodInstance mj = (X10MethodInstance) mj0;

        XRoot thisVar = XTerms.makeLocal(XTerms.makeFreshName("this"));

        List<XVar> ys = new ArrayList<XVar>(2);
        List<XRoot> xs = new ArrayList<XRoot>(2);

        X10MethodInstance_c.buildSubst(ct, ys, xs, thisVar);
        X10MethodInstance_c.buildSubst(mi, ys, xs, thisVar);
        X10MethodInstance_c.buildSubst(mj, ys, xs, thisVar);
        final XVar[] y = ys.toArray(new XVar[ys.size()]);
        final XRoot[] x = xs.toArray(new XRoot[ys.size()]);

        mi = X10MethodInstance_c.fixThis(mi, y, x);
        mj = X10MethodInstance_c.fixThis(mj, y, x);

        // Force evaluation to help debugging.
        mi.returnType();
        mj.returnType();

        checkOverride(mi, mj, true);

        X10Flags miF = X10Flags.toX10Flags(mi.flags());
        X10Flags mjF = X10Flags.toX10Flags(mj.flags());

        // Report.report(1, "X10MethodInstance_c: " + this + " canOVerrideImpl " + mj);
        if (! miF.hasAllAnnotationsOf(mjF)) {
            if (Report.should_report(Report.types, 3))
                Report.report(3, mi.flags() + " is more liberal than " +
                              mj.flags());
            throw new SemanticException(mi.signature() + " in " + mi.container() +
                                        " cannot override " + 
                                        mj.signature() + " in " + mj.container() + 
                                        "; attempting to assign weaker " + 
                                        "behavioral annotations", 
                                        mi.position());
        }
    }

    /* (non-Javadoc)
     * @see x10.types.X10TypeEnv#consistent(x10.constraint.XConstraint)
     */
    public boolean consistent(XConstraint c) {
        return c.consistent();
    }

    /* (non-Javadoc)
     * @see x10.types.X10TypeEnv#consistent(polyglot.types.Type)
     */
    public boolean consistent(Type t) {
        if (t instanceof ConstrainedType) {
            ConstrainedType ct = (ConstrainedType) t;
            if (!consistent(Types.get(ct.baseType())))
                return false;
            if (!consistent(Types.get(ct.constraint())))
                return false;
        }
        if (t instanceof MacroType) {
            MacroType mt = (MacroType) t;
            for (Type ti : mt.typeParameters()) {
                if (!consistent(ti))
                    return false;
            }
            for (Type ti : mt.formalTypes()) {
                if (!consistent(ti))
                    return false;
            }
        }
        if (t instanceof X10ClassType) {
            X10ClassType ct = (X10ClassType) t;
            for (Type ti : ct.typeArguments()) {
                if (!consistent(ti))
                    return false;
            }
        }
        //	    if (!consistent(X10TypeMixin.realX(t)))
        //	        return false;
        return true;
    }
    
   
    
    /* (non-Javadoc)
     * @see x10.types.X10TypeEnv#upperBounds(polyglot.types.Type, boolean)
     */
    public List<Type> upperBounds(Type t, boolean includeObject) {
    	List<Type> bounds = bounds(t, Bound.UPPER, includeObject);
        return bounds;
    }
    public List<Type> upperTypeBounds(Type t, boolean includeObject) {
    	List<Type> bounds = typeBounds(t, Bound.UPPER, includeObject);
        return bounds;
    }
    public List<Type> lowerTypeBounds(Type t) {
    	List<Type> bounds = typeBounds(t, Bound.LOWER, false);
        return bounds;
    }
    /* (non-Javadoc)
     * @see x10.types.X10TypeEnv#lowerBounds(polyglot.types.Type)
     */
    public List<Type> lowerBounds(Type t) {
        return bounds(t, Bound.LOWER, false);
    }
    /* (non-Javadoc)
     * @see x10.types.X10TypeEnv#equalBounds(polyglot.types.Type)
     */
    public List<Type> equalBounds(Type t) {
        return bounds(t, Bound.EQUAL, false);
    }

    List<Type> getBoundsFromConstraint(Type pt, TypeConstraint c, Bound dir) {
        if (c == null)
            return Collections.EMPTY_LIST;
        
        List<Type> upper = new ArrayList<Type>();
        List<Type> lower = new ArrayList<Type>();

        for (SubtypeConstraint term : c.terms()) {
            Type l = term.subtype();
            Type r = term.supertype();
            if (l != null && r != null) {
                if (term.isEqualityConstraint()) {
                    if (ts.equalsStruct(l, pt)) {
                        upper.add(r);
                        lower.add(r);
                    }
                    if (ts.equalsStruct(r, pt)) {
                        upper.add(l);
                        lower.add(l);
                    }
                }
                else {
                    if (ts.equalsStruct(l, pt))
                        upper.add(r);
                    if (ts.equalsStruct(r, pt))
                        lower.add(l);
                }
            }
        }
        
        switch (dir) {
        case UPPER:
            return upper;
        case LOWER:
            return lower;
        case EQUAL:
            Set<Type> equals = new HashSet<Type>();
            equals.addAll(upper);
            equals.retainAll(lower);
            return new ArrayList<Type>(equals);
        }
        
        return Collections.EMPTY_LIST;
    }
    
    
    public Kind kind(Type t) {
        X10Context  c = (X10Context) this.context;
        t = X10TypeMixin.baseType(t);
        if (t instanceof ClosureType)
            return Kind.VALUE;
        if (t instanceof ClassType) {
            ClassType ct = (ClassType) t;
            if (ct.isAnonymous()) {
                if (ct.superClass() != null)
                    return kind(ct.superClass());
                else if (ct.interfaces().size() > 0)
                    return kind(ct.interfaces().get(0));
                else
                    throw new InternalCompilerError(t + " must have either a superclass or a single superinterface.");
            }
            if (X10Flags.toX10Flags(ct.flags()).isValue())
                return Kind.VALUE;
            if (X10Flags.toX10Flags(ct.flags()).isInterface())
                return Kind.EITHER;

            if (X10Flags.toX10Flags(ct.flags()).isStruct())
                return Kind.STRUCT;
            else
                return Kind.REFERENCE;
        }
        if (t instanceof ParameterType) {
            Kind k = Kind.EITHER;
            for (Type t2 : bounds(t, Bound.UPPER, false)) {
                Kind k2 = kind(t2);
                if (k == Kind.NEITHER)
                    ;
                else if (k == k2)
                    ;
                else if (k2 == Kind.EITHER)
                    ;
                else if (k == Kind.EITHER && k2 == Kind.VALUE)
                    k = Kind.VALUE;
                else if (k == Kind.EITHER && k2 == Kind.STRUCT)
                    k = Kind.STRUCT;
                else if (k == Kind.EITHER && k2 == Kind.REFERENCE)
                    k = Kind.REFERENCE;
                else
                    k = Kind.NEITHER;
            }
            return k;
        }
        return Kind.NEITHER;
    }

    List<Type> typeBounds(Type t, Bound kind, boolean includeObject) {
        List<Type> result = new ArrayList<Type>();
        Set<Type> visited = new HashSet<Type>();
        
        LinkedList<Type> worklist = new LinkedList<Type>();
        worklist.add(t);
        
        while (! worklist.isEmpty()) {
            Type w = worklist.removeFirst();
        
            // Expand macros, remove constraints
            Type expanded = X10TypeMixin.baseType(w);
        
            if (visited.contains(expanded)) {
                continue;
            }
        
            visited.add(expanded);
        
//            // Get constraints from the type's where clause.
//            XConstraint wc = X10TypeMixin.xclause(w);
//            if (wc != null) {
//                List<Type> b = getBoundsFromConstraint(t, wc, kind);
//                worklist.addAll(b);
//            }
        
            if (expanded instanceof ParameterType) {
                ParameterType pt = (ParameterType) expanded;
                X10Def def = (X10Def) Types.get(pt.def());
                Ref<TypeConstraint> ref = def.typeGuard();
                if (ref != null) {
                	 TypeConstraint c = Types.get(def.typeGuard());
                     List<Type> b = getBoundsFromConstraint(pt, c, kind);
                     worklist.addAll(b);
                }
                continue;
            }
            // vj:
            // If U is an upperbound of Ti, then
            // C[T1,..,Ti-1,U,Ti+1,...,Tn] is an upperbound of C[T1,.., Tn]
            if (expanded instanceof X10ClassType && kind == Bound.UPPER) {
            	X10ClassType ct = (X10ClassType) expanded;

            	if (ct.hasParams()) {
            		List<Type> typeArgs = ct.typeArguments();
            		X10ClassDef def = ct.x10Def();
            		List<Variance> variances = def.variances();

            		for (int i=0; i < typeArgs.size(); i++) {
            			ParameterType.Variance v = variances.get(i);
            			switch (v) {
            			case COVARIANT:
            				for (Type type : upperBounds(typeArgs.get(i), true)) {
            					X10ClassType ct1 = (X10ClassType) ct.copy();
            					List<Type> typeArgs1 = new ArrayList<Type>(typeArgs); //copy
            					typeArgs1.set(i,type);
            					ct1 = ct1.typeArguments(typeArgs1);
            					result.add(ct1);
            				}
            				break;
            			case CONTRAVARIANT:
            				for (Type type : lowerBounds(typeArgs.get(i))) {
            					X10ClassType ct1 = (X10ClassType) ct.copy();
            					List<Type> typeArgs1 = new ArrayList<Type>(typeArgs); //copy
            					typeArgs1.set(i,type);
            					ct1 = ct1.typeArguments(typeArgs1);
            					result.add(ct1);
            				}
            				break;

            			case INVARIANT:
            				break;
            			}

            		}
            	}
            }
            result.add(expanded);
        }
        
        if (kind == Bound.UPPER && result.isEmpty())
            if (includeObject)
                return Collections.<Type>singletonList(ts.Object());
            else
                return Collections.<Type>emptyList();
        
        return new ArrayList<Type>(result);
    }

    List<Type> bounds(Type t, Bound kind, boolean includeObject) {
        List<Type> result = new ArrayList<Type>();
        Set<Type> visited = new HashSet<Type>();
        
        LinkedList<Type> worklist = new LinkedList<Type>();
        worklist.add(t);
        
        while (! worklist.isEmpty()) {
            Type w = worklist.removeFirst();
        
           
        
            if (visited.contains(w)) {
                continue;
            }
        
            visited.add(w);
        
//            // Get constraints from the type's where clause.
//            XConstraint wc = X10TypeMixin.xclause(w);
//            if (wc != null) {
//                List<Type> b = getBoundsFromConstraint(t, wc, kind);
//                worklist.addAll(b);
//            }
        
            // Expand macros, remove constraints
            Type expanded = X10TypeMixin.baseType(w);
            if (expanded instanceof ParameterType) {
                ParameterType pt = (ParameterType) expanded;
                X10Def def = (X10Def) Types.get(pt.def());
                Ref<TypeConstraint> ref = def.typeGuard();
                if (ref != null) {
                	 TypeConstraint c = Types.get(def.typeGuard());
                     List<Type> b = getBoundsFromConstraint(pt, c, kind);
                     worklist.addAll(b);
                }
                continue;
            }
            result.add(w);
        }
        
        if (kind == Bound.UPPER && result.isEmpty())
            if (includeObject)
                return Collections.<Type>singletonList(ts.Object());
            else
                return Collections.<Type>emptyList();
        
        return new ArrayList<Type>(result);
    }

    @Override
    public Type findMemberType(Type container, Name name) throws SemanticException {
        // FIXME: check for ambiguities
        for (Type t : upperBounds(container, true)) {
            try {
                return super.findMemberType(t, name);
            }
            catch (SemanticException e) {
            }
            try {
                return ts.findTypeDef(t, ts.TypeDefMatcher(t, name, Collections.EMPTY_LIST, Collections.EMPTY_LIST, context), context);
            }
            catch (SemanticException e) {
            }
        }

        throw new NoClassException(name.toString(), container);
    }

    /* (non-Javadoc)
     * @see x10.types.X10TypeEnv#findAcceptableTypeDefs(polyglot.types.Type, x10.types.X10TypeSystem_c.TypeDefMatcher)
     */
    public List<MacroType> findAcceptableTypeDefs(Type container, X10TypeSystem_c.TypeDefMatcher matcher)
    throws SemanticException {

        SemanticException error = null;

        // The list of acceptable methods. These methods are accessible from
        // currClass, the method call is valid, and they are not overridden
        // by an unacceptable method (which can occur with protected methods
        // only).
        List<MacroType> acceptable = new ArrayList<MacroType>();

        // A list of unacceptable methods, where the method call is valid, but
        // the method is not accessible. This list is needed to make sure that
        // the acceptable methods are not overridden by an unacceptable method.
        List<MacroType> unacceptable = new ArrayList<MacroType>();

        Set<Type> visitedTypes = new HashSet<Type>();

        LinkedList<Type> typeQueue = new LinkedList<Type>();

        // Get the upper bound of the container.
        typeQueue.addAll(upperBounds(container, true));

        while (! typeQueue.isEmpty()) {
            Type t = typeQueue.removeFirst();

            if (t instanceof X10ParsedClassType) {
                X10ParsedClassType type = (X10ParsedClassType) t;

                if (visitedTypes.contains(type)) {
                    continue;
                }

                visitedTypes.add(type);

                if (Report.should_report(Report.types, 2))
                    Report.report(2, "Searching type " + type + " for method " + matcher.signature());

                for (Iterator<Type> i = type.typeMembers().iterator(); i.hasNext(); ) {
                    Type ti = i.next();

                    if (!(ti instanceof MacroType)) {
                        continue;	    		
                    }

                    MacroType mi = (MacroType) ti;

                    if (Report.should_report(Report.types, 3))
                        Report.report(3, "Trying " + mi);

                    try {
                        mi = matcher.instantiate(mi);

                        if (mi == null) {
                            continue;
                        }

                        if (isAccessible(mi)) {
                            if (Report.should_report(Report.types, 3)) {
                                Report.report(3, "->acceptable: " + mi + " in " + mi.container());
                            }

                            acceptable.add(mi);
                        }
                        else {
                            // method call is valid, but the method is
                            // unacceptable.
                            unacceptable.add(mi);
                            if (error == null) {
                                error = new NoMemberException(NoMemberException.METHOD,
                                                              "Method " + mi.signature() + " in " + container + " is inaccessible."); 
                            }
                        }

                        continue;
                    }
                    catch (SemanticException e) {
                    }

                    if (error == null) {
                        error = new SemanticException("Type definition " + mi.name() + " in " + container +
                                                      " cannot be instantiated with arguments " + matcher.argumentString() + ".");
                    }
                }
            }

            if (t instanceof ObjectType) {
                ObjectType ot = (ObjectType) t;

                if (ot.superClass() != null) {
                    typeQueue.addLast(ot.superClass());
                }

                typeQueue.addAll(ot.interfaces());
            }
        }

        if (error == null) {
            error = new SemanticException("No type defintion found in " + container + " for " + matcher.signature() + ".");
        }

        if (acceptable.size() == 0) {
            throw error;
        }

        // remove any types in acceptable that are overridden by an
        // unacceptable
        // type.
        // TODO
        //	    for (Iterator<MacroType> i = unacceptable.iterator(); i.hasNext();) {
        //		MacroType mi = i.next();
        //	    	acceptable.removeAll(mi.overrides());
        //	    }

        if (acceptable.size() == 0) {
            throw error;
        }

        return acceptable;
    }

    @Override
    public boolean isSubtype(Type t1, Type t2) {
        return isSubtype(t1, t2, false);
    }

    public boolean behavesLike(Type t1, Type t2) {
    	X10Type xt1 = (X10Type) t1;
    	X10Type xt2 = (X10Type) t2;
    	if (xt1.isX10Struct() || xt1.isX10Struct()) {
    		if (xt1.isX10Struct() != xt2.isX10Struct())
    			return false;
    		return isSubtype(X10TypeMixin.makeRef(xt1), X10TypeMixin.makeRef(xt2), true);
    	}
    	// both are class types
    	return isSubtype(t1, t2, true);
    }
    /* (non-Javadoc)
     * @see x10.types.X10TypeEnv#isSubtypeWithValueInterfaces(polyglot.types.Type, polyglot.types.Type)
     */
    public boolean isSubtypeWithValueInterfaces(Type t1, Type t2) {
        return isSubtype(null, t1, t2, true);
    }
   
    public boolean isSubtype(Type t1, Type t2, boolean allowValueInterfaces) {
    	return isSubtype(null, t1, t2, allowValueInterfaces);
    }

    /* (non-Javadoc)
     * @see x10.types.X10TypeEnv#isSubtype(polyglot.types.Type, polyglot.types.Type, boolean)
     */
    public boolean isSubtype(XVar x, Type t1, Type t2, boolean allowValueInterfaces) {
    	assert t1 != null;
    	assert t2 != null;
    	t1 = ts.expandMacros(t1);
    	t2 = ts.expandMacros(t2);
    	X10Context xcontext = (X10Context) context;

    	X10Type xt1 = (X10Type) t1;
    	X10Type xt2 = (X10Type) t2;

    	/*if (xt2.isRooted() && ! xt1.isRooted()) {
    		if (xt1.isRooted() != xt2.isRooted())
    			return false;
    		// they are both rooted
    		t1 = xt1.clearFlags(X10Flags.ROOTED);
    		t2 = xt2.clearFlags(X10Flags.ROOTED);
    	}*/
    		
    	if (xt1.isProto() || xt2.isProto()) {
    		if (xt1.isProto() != xt2.isProto())
    			return false;
    		// they are both proto
    		t1 = xt1.clearFlags(X10Flags.PROTO);
    		t2 = xt2.clearFlags(X10Flags.PROTO);
    		xt1 = X10TypeMixin.baseForProto(xt1);
        	xt2 = X10TypeMixin.baseForProto(xt2);
    	}
    	
    	if (xt1.isX10Struct() || xt2.isX10Struct()) {
    		if (xt1.isX10Struct() != xt2.isX10Struct())
    			return false;
    		if (! ts.typeEquals(X10TypeMixin.baseType(xt1), X10TypeMixin.baseType(xt2),
    				xcontext))
    			return false;
    		xt1 = X10TypeMixin.makeRef(xt1);
    		xt2 = X10TypeMixin.makeRef(xt2);
    		// now keep going, the clause entailment will be checked by the
    		// logic below.
    	}
    	
    	
    	if (t1 == t2)
    		return true;
   
    	if (t1.isVoid()) 
    		return t2.isVoid();
    	if (t2.isVoid())
    		return false;

    	if (t1.isNull() && (t2.isNull() 
    			|| ts.isReferenceOrInterfaceType(t2, xcontext))) {
    		return true;
    	}

    	if (t2.isNull()) {
    		return false;
    	}

    	// HACK: treat (S) => T as a subtype of Value.
    	if (ts.isFunction(t1, xcontext) 
    			&& ts.typeEquals(t2, ts.Value(), xcontext))
    		return true;

    	if (ts.isValueType(t1, xcontext) 
    			&& ts.isReferenceType(t2, xcontext))
    		return false;

    	if (ts.isValueType(t2, xcontext) 
    			&& ts.isReferenceType(t1, xcontext))
    		return false;

    	if (! allowValueInterfaces) {
    		if (ts.isValueType(t1, xcontext) 
    				&& ts.isReferenceOrInterfaceType(t2, xcontext))
    			return false;

    		if (ts.isValueType(t2, xcontext) 
    				&& ts.isReferenceOrInterfaceType(t1, xcontext))
    			return false;
    	}

    	if (typeEquals(t1, t2))
    		return true;

    	TypeConstraint typeConst = xcontext.currentTypeConstraint();
    	List<SubtypeConstraint> env;
    	if (typeConst != null)
    		env =  typeConst.terms();
    	else 
    		env = Collections.emptyList();
    	
    	
    	// DO NOT check if env.entails(t1 <: t2); it would be recursive
    	// Instead, iterate through the environment.
    	for (int i = 0; i < env.size(); i++) {
    		SubtypeConstraint term = env.get(i);
    		List<SubtypeConstraint> newEnv = new ArrayList<SubtypeConstraint>();
    		if (0 <= i-1 && i-1 < env.size()) 
    			newEnv.addAll(env.subList(0, i-1));
    		if (0 <= i+1 && i+1 < env.size()) 
    			newEnv.addAll(env.subList(i+1, env.size()));
    		//                    newEnv = env;
    		//                    newEnv = Collections.EMPTY_LIST;

    		X10Context xc2 = (X10Context) xcontext.pushBlock();
    		TypeConstraint ec = new TypeConstraint_c();
    		for (SubtypeConstraint tt : newEnv) {
    			ec.addTerm(tt);
    		}
    		xc2.setCurrentTypeConstraint(Types.ref(ec));

    		X10TypeEnv_c tenv = copy();
    		tenv.context = xc2;

    		if (term.isEqualityConstraint()) {
    			SubtypeConstraint eq = term;
    			Type l = eq.subtype();
    			Type r = eq.supertype();
    			if (tenv.isSubtype(t1, l, allowValueInterfaces) 
    					&& tenv.isSubtype(r, t2, allowValueInterfaces)) {
    				return true;
    			}
    			if (tenv.isSubtype(t1, r, allowValueInterfaces) 
    					&& tenv.isSubtype(l, t2, allowValueInterfaces)) {
    				return true;
    			}

    		}
    		else {
    			SubtypeConstraint s = term;
    			Type l = s.subtype();
    			Type r = s.supertype();
    			if (tenv.isSubtype(t1, l, allowValueInterfaces) 
    					&& tenv.isSubtype(r, t2, allowValueInterfaces)) {
    				return true;
    			}
    		}
    	}
    	Type baseType1 = X10TypeMixin.baseType(t1);
    	
    	if (typeEquals(baseType1,t2))
    		return true;
    	
    	Type baseType2 = X10TypeMixin.baseType(t2);
    	XConstraint c1 = X10TypeMixin.realX(t1);
    	XConstraint c2 = X10TypeMixin.xclause(t2);  // NOTE: xclause, not realX
    	if (c2 != null && c2.valid()) { 
    		c2 = null; 
    	}
    	if (c1 != null && c1.valid()) 
    		c1 = null; 
    	
    	if (x == null) {
    		//x = X10TypeMixin.selfVar(c1);
    		//if (x == null) {
    			x = XTerms.makeFreshLocal();
    		//}
    		//XConstraint c = xcontext.currentConstraint();
    			XConstraint c = null;
    			try {
    				c = xcontext.constraintProjection(c1, c2);
    			    c.addBinding(XTerms.HERE, xcontext.currentPlaceTerm().term()); 
    			} catch (XFailure z) {
    				throw new InternalCompilerError("Unexpected inconsistent context " + xcontext);
    			}
    			
    	//	System.err.println("X10TypeEnv_c: currentConstraint is " + c);
    		if (! (c1 == null || entails(c, c1))) {
    			// Now add the real clause of t1 to the context, and proceed.
    			// Must do this even if c2==null. 
    			try {
    			
    				//c1 = c1.substitute(x, c1.self());
    				xcontext = (X10Context) xcontext.pushBlock();	
    				
    				XConstraint r = c.addIn(x, c1);
    				xcontext.setCurrentConstraint(r);
    		
    				X10TypeEnv_c tenv = copy();
    				tenv.context = xcontext;
    			
    				if (c2 != null)
                      t2 = Subst.subst(t2, x, c2.self());
    			
    				return tenv.isSubtype(x, baseType1, t2, allowValueInterfaces);
    			} catch (XFailure z) {
    				throw new InternalCompilerError("Unexpected failure ", z);
    			} catch (SemanticException z) {
    				throw new InternalCompilerError("Unexpected failure ", z);
    			}
    		}
    	}
    	assert x != null;
    	t1=baseType1;
    	
    	
    	if (baseType2 != t2)
    	if (isSubtype(x, t1, baseType2, allowValueInterfaces) && entails(c1,c2))
    		return true;

    	/*if (t1 instanceof PrimitiveType && typeEquals(t2, ts.Value())) {
    		return true;
    	}*/

    	// Handle parametrized types and interfaces
    	if (baseType1 instanceof X10ClassType && baseType2 instanceof X10ClassType) {
    		X10ClassType ct1 = (X10ClassType) baseType1;
    		X10ClassType ct2 = (X10ClassType) baseType2;
    		if (ct1.def() == ct2.def()) { // so the base types are identical
    			X10ClassDef def = ct1.x10Def();
    			int numArgs = def.typeParameters().size();
    			if (numArgs > 0) {
    				if (ct2.typeArguments().size()!= numArgs)
    					return false;
    				if (ct2.typeArguments().size() != numArgs)
    					return false;
    				if (def.variances().size() != numArgs)
    					return false;
    				for (int i = 0; i < numArgs; i++) {
    					Type a1 = ct1.typeArguments().get(i);
    					Type a2 = ct2.typeArguments().get(i);
    					if (a1 == null || a2 == null)
    						assert false;
    					ParameterType.Variance v = def.variances().get(i);
    					switch (v) {
    					case COVARIANT:
    						if (! isSubtype(a1, a2, allowValueInterfaces)) 
    							return false;
    						break;
    					case CONTRAVARIANT:
    						if (! isSubtype(a2, a1, allowValueInterfaces)) 
    							return false;
    						break;
    					case INVARIANT:
    						if (! typeEquals(a1, a2)) 
    							return false;
    						break;
    					}
    				}
    				return true;
    			}
    		}

    		Type child = t1;
    		Type ancestor = t2;

    		if (child instanceof X10ClassType) {
    			X10ClassType childRT = (X10ClassType) child;

    			// Check subclass relation.
    			if (childRT.superClass() != null) {
    				if (this.isSubtype(x, childRT.superClass(), ancestor, allowValueInterfaces)) {
    					return true;
    				}
    			}

    			boolean allowValueInterfacesHere = allowValueInterfaces;
    			if (ts.isReferenceOrInterfaceType(childRT, xcontext))
    				allowValueInterfacesHere = true;

    			// Next check interfaces.
    			List<Type> l = childRT.interfaces();
    			for (Type parentType : l) {
    				boolean tryIt = false;
    				X10Type pt = (X10Type) parentType;
    				XRoot thisVar = childRT.x10Def().thisVar();
    				try {
    					parentType = Subst.subst(parentType, x, thisVar);
    				} catch (SemanticException z) {
    					throw new InternalCompilerError("Unexpected semantic exception " + z);
    				}

    				if (allowValueInterfacesHere 
    						|| ts. isValueType(parentType, xcontext)) {
    					if (isSubtype(x, parentType, ancestor, allowValueInterfaces)) {
    						return true;
    					}
    				}
    			}
    		}
    	}

    	/*
    	 * Do not allowValueInterfaces. A type parameter T is not a subtype of Object.
    	 */
    	if (t1 instanceof ParameterType) {
    		for (Type s1 : upperTypeBounds(t1, false)) {
    			if (isSubtype(x, s1, t2, false))
    				return true;
    		}
    	}
    	if (t2 instanceof ParameterType) {
    		for (Type s2 : lowerTypeBounds(t2)) {
    			if (isSubtype(x, t1, s2, allowValueInterfaces))
    				return true;
    		}
    	}

    	return false;
    }

    /* (non-Javadoc)
     * @see x10.types.X10TypeEnv#typeEquals(polyglot.types.Type, polyglot.types.Type, java.util.List)
     */
    public boolean typeEquals(Type t1, Type t2) {
        t1 = ts.expandMacros(t1);
        t2 = ts.expandMacros(t2);

        if (t1 == t2)
            return true;

        if (t1.isVoid() && t2.isVoid())
            return true;

        if (t1.isVoid() || t2.isVoid())
            return false;
        
        X10Type xt1 = (X10Type) t1;
        X10Type xt2 = (X10Type) t2;
        if (xt1.isProto() != xt2.isProto()) 
        	return false;
        if (xt1.isX10Struct() != xt2.isX10Struct())
        	return false;
     
        X10Context xc = (X10Context) context;
        List<SubtypeConstraint> env = xc.currentTypeConstraint().terms();

        // DO NOT check if env.entails(t1 == t2); it would be recursive
        // Instead, iterate through the environment.
        for (int i = 0; i < env.size(); i++) {
            SubtypeConstraint term = env.get(i);
            List<SubtypeConstraint> newEnv = new ArrayList<SubtypeConstraint>();
            if (0 <= i-1 && i-1 < env.size()) newEnv.addAll(env.subList(0, i-1));
            if (0 <= i+1 && i+1 < env.size()) newEnv.addAll(env.subList(i+1, env.size()));
            //                    newEnv = env;
            newEnv = Collections.EMPTY_LIST;

            X10Context xc2 = (X10Context) xc.pushBlock();
            TypeConstraint ec = new TypeConstraint_c();
            for (SubtypeConstraint tt : newEnv) {
                ec.addTerm(tt);
            }
            xc2.setCurrentTypeConstraint(Types.ref(ec));

            if (term.isEqualityConstraint()) {
                SubtypeConstraint eq = term;
                Type l = eq.subtype();
                Type r = eq.supertype();
                if (l == null || r == null)
                    continue;
                if (ts.env(xc2).typeEquals(t1, l) && ts.env(xc2).typeEquals(r, t2)) {
                    return true;
                }
                if (ts.env(xc2).typeEquals(t1, r) && ts.env(xc2).typeEquals(l, t2)) {
                    return true;
                }
            }
        }

        Type baseType1 = X10TypeMixin.baseType(t1);
        Type baseType2 = X10TypeMixin.baseType(t2);

        // Don't need the real clause here, since will only be true if the base types are equal.
        XConstraint c1 = X10TypeMixin.xclause(t1);
        XConstraint c2 = X10TypeMixin.xclause(t2);

        if (c1 != null && c1.valid()) { c1 = null; t1 = baseType1; }
        if (c2 != null && c2.valid()) { c2 = null; t2 = baseType2; }

        if (! entails(c1, c2))
            return false;

        if (! entails(c2, c1))
            return false;

        if (t1 instanceof ParameterType && t2 instanceof ParameterType) {
            ParameterType pt1 = (ParameterType) t1;
            ParameterType pt2 = (ParameterType) t2;
            if (TypeParamSubst.isSameParameter(pt1, pt2))
                return true;
        }

        if (t1 != baseType1 || t2 != baseType2)
            if (typeEquals(baseType1, baseType2))
                return true;

        if (t1 instanceof ParameterType) {
            for (Type s1 : equalBounds(t1)) {
                if (typeEquals(s1, t2)) {
                    return true;
                }
            }
        }

        if (t2 instanceof ParameterType) {
            for (Type s2 : equalBounds(t2)) {
                if (typeEquals(t1, s2)) {
                    return true;
                }
            }
        }

        if (t1 instanceof X10ClassType && t2 instanceof X10ClassType) {
            X10ClassType ct1 = (X10ClassType) t1;
            X10ClassType ct2 = (X10ClassType) t2;
            X10ClassDef def1 = ct1.x10Def();
            X10ClassDef def2 = ct2.x10Def();
            if (def1 != def2)
                return false;
            if (ct1.typeArguments().size() == 0 && ct2.typeArguments().size() == 0)
                return true;
            if (! CollectionUtil.allElementwise(ct1.typeArguments(), ct2.typeArguments(), new X10TypeSystem_c.TypeEquals(context))) {
                return false;
            }
            return true;
        }

        return super.typeEquals(t1, t2);
    }

    @Override
    public boolean isCastValid(Type fromType, Type toType) {
        //	    if (isImplicitCastValid(fromType, toType))
        //	        return true;

        fromType = ts.expandMacros(fromType);
        toType =ts. expandMacros(toType);

        // Handle the rooted flag.
    /*    if (((X10Type) toType).isRooted() && ! (((X10Type) fromType).isRooted()))
        	return false;
       */ 
        
        if (fromType == toType)
            return true;

       // if (((X10Type) fromType).equalsNoFlag((X10Type) toType))
        //	return true;
      //  if (typeEquals(fromType, toType))
      //  	return true;
        
        if (fromType instanceof NullType) {
            return toType.isNull() || ! ts.isValueType(toType, (X10Context) context);
        }

        // For now, can always cast to or from a parameter type.
        //		if (fromType instanceof ParameterType || toType instanceof ParameterType) {
        //		    return true;
        //		}

        Type t1 = X10TypeMixin.baseType(fromType);
        Type t2 = X10TypeMixin.baseType(toType);
        XConstraint c1 = X10TypeMixin.realX(fromType);
        XConstraint c2 = X10TypeMixin.realX(toType);


        Type baseType1 = t1;
        Type baseType2 = t2;

        if (c1 != null && c1.valid()) { c1 = null; }
        if (c2 != null && c2.valid()) { c2 = null; }

        if (c1 != null && c2 != null && ! clausesConsistent(c1, c2))
            return false;

        if (baseType1 != fromType || baseType2 != toType)
            return isCastValid(baseType1, baseType2);

        if (ts.isValueType(baseType1, (X10Context) context) && ts.isReferenceType(baseType2, (X10Context) context))
            return false;

        if (ts.isReferenceType(baseType1, (X10Context) context) && ts.isValueType(baseType2, (X10Context) context))
            return false;

        if (ts.isParameterType(baseType1) || ts.isParameterType(baseType2))
            return true;

        return super.isCastValid(baseType1, baseType2);
    }

    public boolean isImplicitNumericCastValid(Type fromType, Type toType) {
        return isPrimitiveConversionValid(fromType, toType);
    }

    @Override
    public boolean isImplicitCastValid(Type fromType, Type toType) {
        return isSubtypeWithValueInterfaces(fromType, toType);
    }

    /* (non-Javadoc)
     * @see x10.types.X10TypeEnv#entails(x10.constraint.XConstraint, x10.constraint.XConstraint)
     */
    public boolean entails(XConstraint c1, XConstraint c2) {
        if (c1 != null || c2 != null) {
            boolean result = true;
 
                try {
                	 X10Context xc = (X10Context) context;
                     XConstraint sigma = xc.constraintProjection(c1,c2);
                     sigma.addIn(c1);
                     result = sigma.entails(c2);
                   /*  result = c1 == null ? 
                    		 (sigma == null ? c2.valid() : sigma.entails(c2))
                    		 : c1.entails(c2, sigma);
                   */
                    
                    if (Report.should_report("sigma", 1)) {
                        System.out.println("c1 = " + c1);
                        System.out.println("c2 = " + c2);
                    }
                //                    result = c1.entails(c2, xc);
                }
                catch (XFailure e) {
                    result = false;
                }
          

            return result;
        }

        return true;
    }

    
    /** Return true if there is a conversion from fromType to toType.  Returns false if the two types are not both value types. */
    public boolean isPrimitiveConversionValid(Type fromType, Type toType) {
        Type baseType1 = X10TypeMixin.baseType(fromType);
        XConstraint c1 = X10TypeMixin.realX(fromType);
        Type baseType2 = X10TypeMixin.baseType(toType);
        XConstraint c2 = X10TypeMixin.realX(toType);

        if (c1 != null && c1.valid()) { c1 = null; }
        if (c2 != null && c2.valid()) { c2 = null; }

        if (! entails(c1, c2))
            return false;

        if (ts.isVoid(baseType1))
            return false;
        if (ts.isVoid(baseType2))
            return false;

        if (ts.isBoolean(baseType1))
            return ts.isBoolean(baseType2);

        // Allow assignment if the fromType's value can be represented as a toType
        if (c1 != null && ts.isNumeric(baseType1) && ts. isNumeric(baseType2)) {
            XVar self = X10TypeMixin.selfVar(c1);
            if (self instanceof XLit) {
                Object val = ((XLit) self).val();
                if (numericConversionValid(baseType2, val)) {
                    return true;
                }
            }
        }

        if (ts.isDouble(baseType1))
            return ts.isDouble(baseType2);
        if (ts.isFloat(baseType1))
            return ts.isFloat(baseType2) || ts.isDouble(baseType2);

        // Do not allow conversions to change signedness.
        if (ts.isLong(baseType1))
            return ts.isLong(baseType2) || ts.isDouble(baseType2);
        if (ts.isInt(baseType1))
            return ts.isInt(baseType2) || ts.isLong(baseType2) || ts.isFloat(baseType2) || ts.isDouble(baseType2);
        if (ts.isShort(baseType1))
            return ts.isShort(baseType2) || ts.isInt(baseType2) || ts.isLong(baseType2) || ts.isFloat(baseType2) || ts.isDouble(baseType2);
        if (ts.isByte(baseType1))
            return ts.isByte(baseType2) || ts.isShort(baseType2) || ts.isInt(baseType2) || ts.isLong(baseType2) || ts.isFloat(baseType2) || ts.isDouble(baseType2);

        if (true) {
            if (ts.isULong(baseType1))
                return ts.isULong(baseType2) || ts.isDouble(baseType2);
            if (ts.isUInt(baseType1))
                return ts.isUInt(baseType2) || ts.isULong(baseType2) || ts.isFloat(baseType2) || ts.isDouble(baseType2);
            if (ts.isUShort(baseType1))
                return ts.isUShort(baseType2) || ts.isUInt(baseType2) || ts.isULong(baseType2) || ts.isFloat(baseType2) || ts.isDouble(baseType2);
            if (ts.isUByte(baseType1))
                return ts.isUByte(baseType2) || ts.isUShort(baseType2) || ts.isUInt(baseType2) || ts.isULong(baseType2) || ts.isFloat(baseType2) || ts.isDouble(baseType2);
        }

        // Note: cannot implicitly coerce a value type to a superclass.
        return false;
    }

    @Override
    public boolean numericConversionValid(Type t, java.lang.Object value) {
        assert false;
        return numericConversionValid(t, null, value);
    }
    
    public boolean numericConversionValid(Type toType, Type fromType, java.lang.Object value) {
            if (value == null)
                return false;
            
            if (value instanceof Float || value instanceof Double)
                return false;

            long v;
            
            if (value instanceof Number) {
                v = ((Number) value).longValue();
            }
            else if (value instanceof Character) {
                v = ((Character) value).charValue();
            }
            else {
                return false;
            }
    
            Type base = X10TypeMixin.baseType(toType);
            
            boolean fits = false;
            
            boolean signedFrom = ts.isSigned(fromType);
            boolean unsignedFrom = ts.isUnsigned(fromType);

            if (signedFrom) {
                if (ts.isUByte(toType)) {
                    fits = 0 <= v && v <= 0xffL;
                }
                if (ts.isUShort(toType)) {
                    fits = 0 <= v && v <= 0xffffL;
                }
                if (ts.isUInt(toType)) {
                    fits = 0 <= v && v <= 0xffffffffL;
                }
                if (ts.isULong(toType)) {
                    fits = 0 <= v;
                }

                if (ts.isByte(toType)) {
                    fits = Byte.MIN_VALUE <= v && v <= Byte.MAX_VALUE;
                }
                if (ts.isShort(toType)) {
                    fits = Short.MIN_VALUE <= v && v <= Short.MAX_VALUE;
                }
                if (ts.isInt(toType)) {
                    fits = Integer.MIN_VALUE <= v && v <= Integer.MAX_VALUE;
                }
                if (ts.isLong(toType)) {
                    fits = true;
                }
                
                if (ts.isFloat(toType))
                    // -2^24 .. 2^24
                    fits = -16777216 <= v && v <= 16777216;

                if (ts.isDouble(toType))
                    // -2^53 .. 2^53
                    fits = -9007199254740992L <= v && v <= 9007199254740992L;
            }

            if (unsignedFrom) {
                if (ts.isUByte(toType)) {
                    fits = v <= 0xffL;
                }
                if (ts.isUShort(toType)) {
                    fits = v <= 0xffffL;
                }
                if (ts.isUInt(toType)) {
                    fits = v <= 0xffffffffL;
                }
                if (ts.isULong(toType)) {
                    fits = true;
                }

                if (ts.isByte(toType)) {
                    fits = v <= Byte.MAX_VALUE;
                }
                if (ts.isShort(toType)) {
                    fits = v <= Short.MAX_VALUE;
                }
                if (ts.isInt(toType)) {
                    fits = v <= Integer.MAX_VALUE;
                }
                if (ts.isLong(toType)) {
                    fits = (v & ~0x7fffffffffffffffL) == 0;
                }
                
                if (ts.isFloat(toType))
                    // 0 .. 2^24
                    fits = v <= 16777216;

                if (ts.isDouble(toType))
                    // 0 .. 2^53
                    fits = v <= 9007199254740992L;
            }

            if (! fits)
                return false;
                    
            // Check if adding self==value makes the constraint on t inconsistent.
            
            XLit val = XTerms.makeLit(value);

            try {
                XConstraint c = new XConstraint_c();
                c.addSelfBinding(val);
                return entails(c, X10TypeMixin.realX(toType));
            }
            catch (XFailure f) {
                // Adding binding makes real clause inconsistent.
                return false;
            }
    }

    protected boolean typeRefListEquals(List<Ref<? extends Type>> l1, List<Ref<? extends Type>> l2) {
        return CollectionUtil.<Type>allElementwise(new TransformingList<Ref<? extends Type>, Type>(l1, new DerefTransform<Type>()),
                                                   new TransformingList<Ref<? extends Type>, Type>(l2, new DerefTransform<Type>()),
                                                   new TypeSystem_c.TypeEquals(context));
    }

    protected boolean typeListEquals(List<Type> l1, List<Type> l2) {
        return CollectionUtil.<Type>allElementwise(l1, l2, new X10TypeSystem_c.TypeEquals(context));
    }

    protected boolean listEquals(List<XVar> l1, List<XVar> l2) {
        return CollectionUtil.<XVar>allEqual(l1, l2);
    }

    protected boolean isX10BaseSubtype(Type me, Type sup) {
        Type xme = X10TypeMixin.baseType(me);
        Type xsup = X10TypeMixin.baseType(sup);
        return isSubtype(xme, xsup);
    }

    /* (non-Javadoc)
     * @see x10.types.X10TypeEnv#hasSameClassDef(polyglot.types.Type, polyglot.types.Type)
     */
    public boolean hasSameClassDef(Type t1, Type t2) {
        return ts.hasSameClassDef(t1, t2);
    }

    /* (non-Javadoc)
     * @see x10.types.X10TypeEnv#equivClause(polyglot.types.Type, polyglot.types.Type)
     */
    public boolean equivClause(Type me, Type other) {
        return entailsClause(me, other) && entailsClause(other, me);
    }

    /* (non-Javadoc)
     * @see x10.types.X10TypeEnv#entailsClause(polyglot.types.Type, polyglot.types.Type)
     */
    public boolean entailsClause(Type me, Type other) {
        try {
            XConstraint c1 = X10TypeMixin.realX(me);
            XConstraint c2 = X10TypeMixin.xclause(other);
            return entails(c1, c2);
        }
        catch (InternalCompilerError e) {
            if (e.getCause() instanceof XFailure) {
                return false;
            }
            throw e;
        }
    }

    @Override
    public Type leastCommonAncestor(Type type1, Type type2)
    throws SemanticException
    {
        try { 
            if (typeEquals(type1, type2)) {
                return type1;
            }

            type1 = X10TypeMixin.baseType(type1);
            type2 = X10TypeMixin.baseType(type2);

            if (typeEquals(type1, type2)) {
                return type1;
            }

            if (type1 instanceof X10ClassType && type2 instanceof X10ClassType) {
                if (hasSameClassDef(type1, type2)) {
                    X10ClassType ct1 = (X10ClassType) type1;
                    X10ClassType ct2 = (X10ClassType) type2;
                    int n = ct1.typeArguments().size();
                    List<Type> newArgs = new ArrayList<Type>(n);
                    for (int i = 0; i < n; i++) {
                        Type a1 = ct1.typeArguments().get(i);
                        Type a2 = ct2.typeArguments().get(i);
                        ParameterType.Variance v = ct1.x10Def().variances().get(i);
                        switch (v) {
                        case INVARIANT:
                            if (typeEquals(a1, a2))
                                newArgs.add(a1);
                            else
                                throw new SemanticException("No least common ancestor found for types \"" + type1 +
                                                            "\" and \"" + type2 + "\".");
                            break;
                        case COVARIANT:
                            newArgs.add(leastCommonAncestor(a1, a2));
                            break;
                        case CONTRAVARIANT:
                            if (isSubtype(a1, a2))
                                newArgs.add(a1);
                            else if (isSubtype(a2, a1))
                                newArgs.add(a2);
                            else
                                throw new SemanticException("No least common ancestor found for types \"" + type1 +
                                                            "\" and \"" + type2 + "\".");
                            break;
                        }
                    }
                    return ct1.typeArguments(newArgs);
                }
            }

            if (type1.isReference() && type2.isNull()) return type1;
            if (type2.isReference() && type1.isNull()) return type2;

            // Don't consider interfaces.
            if (type1.isClass() && type1.toClass().flags().isInterface()) {
                return ts.Object();
            }

            if (type2.isClass() && type2.toClass().flags().isInterface()) {
                return ts.Object();
            }

            if (isSubtype(type1, type2)) return type2;
            if (isSubtype(type2, type1)) return type1;

            if (isSubtype(type1, X10TypeMixin.baseType(type2))) return X10TypeMixin.baseType(type2);
            if (isSubtype(type2, X10TypeMixin.baseType(type1))) return X10TypeMixin.baseType(type1);



            if (type1 instanceof ObjectType && type2 instanceof ObjectType) {
                // Walk up the hierarchy
                Type sup1 = ((ObjectType) type1).superClass();
                Type sup2 = ((ObjectType) type2).superClass();

                if (sup1 == null) return ts.Object();
                if (sup2 == null) return ts.Object();

                Type t1 = leastCommonAncestor(sup1, type2);
                Type t2 = leastCommonAncestor(sup2, type1);

                if (typeEquals(t1, t2)) return t1;

                return ts.Object();
            }
        }
        finally {
            //Report.report(1, "X10TypeSystem_c: The LCA of "  + type1 + " " + type2 + " is " + result + ".");
        }

        throw new SemanticException("No least common ancestor found for types \"" + type1 +
                                    "\" and \"" + type2 + "\".");
    }

    /* (non-Javadoc)
     * @see x10.types.X10TypeEnv#typeBaseEquals(polyglot.types.Type, polyglot.types.Type)
     */
    public boolean typeBaseEquals(Type type1, Type type2) {
        if (type1 == type2) return true;
        if (type1 == null || type2 == null) return false;
        return typeEquals(X10TypeMixin.baseType(type1), X10TypeMixin.baseType(type2));
    }

    /* (non-Javadoc)
     * @see x10.types.X10TypeEnv#typeDeepBaseEquals(polyglot.types.Type, polyglot.types.Type)
     */
    public boolean typeDeepBaseEquals(Type type1, Type type2) {
        if (type1 == type2) return true;
        if (type1 == null || type2 == null) return false;
        return typeEquals(X10TypeMixin.stripConstraints(type1), X10TypeMixin.stripConstraints(type2));
    }
    /* (non-Javadoc)
     * @see x10.types.X10TypeEnv#equalTypeParameters(java.util.List, java.util.List)
     */
    public boolean equalTypeParameters(List<Type> a, List<Type> b) {
        if (a == null || a.isEmpty()) return b==null || b.isEmpty();
        if (b==null || b.isEmpty()) return false;
        int i = a.size(), j=b.size();
        if (i != j) return false;
        boolean result = true;
        for (int k=0; result && k < i; k++) {
            result = typeEquals(a.get(k), b.get(k));
        }
        return result;
    }

    /* (non-Javadoc)
     * @see x10.types.X10TypeEnv#primitiveClausesConsistent(x10.constraint.XConstraint, x10.constraint.XConstraint)
     */
    public boolean primitiveClausesConsistent(x10.constraint.XConstraint c1, x10.constraint.XConstraint c2) {
        //		try {
        //			x10.constraint.Promise p1 = c1.lookup(x10.constraint.C_Self.Self);
        //			x10.constraint.Promise p2 = c2.lookup(x10.constraint.C_Self.Self);
        //			if (p1 != null && p2 != null) {
        //				x10.constraint.C_Term t1 = p1.term();
        //				x10.constraint.C_Term t2 = p2.term();
        //				return t1 == null || t2 == null || t1.equals(t2);
        //			}
        //		}
        //		catch (x10.constraint.Failure e) {
        //			return true;
        //		}
        return true;
    }

    /* (non-Javadoc)
     * @see x10.types.X10TypeEnv#clausesConsistent(x10.constraint.XConstraint, x10.constraint.XConstraint)
     */
    public boolean clausesConsistent(x10.constraint.XConstraint c1, x10.constraint.XConstraint c2) {
        if (primitiveClausesConsistent(c1, c2)) {
            x10.constraint.XConstraint r = c1.copy();
            try {
                r.addIn(c2);
                return r.consistent();
            }
            catch (x10.constraint.XFailure e) {
                return false;
            }
        }
        return false;
    }

    /** Return true if t overrides mi */
    public boolean hasFormals(ProcedureInstance<? extends ProcedureDef> pi, List<Type> formalTypes) {
        return ((ProcedureInstance_c) pi).hasFormals(formalTypes, context);
    }

    public List<MethodInstance> overrides(MethodInstance jmi) {
        X10MethodInstance mi = (X10MethodInstance) jmi;
        List<MethodInstance> l = new ArrayList<MethodInstance>();
        StructType rt = mi.container();

        XRoot thisVar = mi.x10Def().thisVar();
        if (thisVar == null)
            thisVar = XTerms.makeLocal(XTerms.makeFreshName("this"));

        List<XVar> ys = new ArrayList<XVar>(2);
        List<XRoot> xs = new ArrayList<XRoot>(2);
        X10MethodInstance_c.buildSubst(mi, ys, xs, thisVar);
        X10MethodInstance_c.buildSubst(rt, ys, xs, thisVar);
        final XVar[] y = ys.toArray(new XVar[ys.size()]);
        final XRoot[] x = xs.toArray(new XRoot[ys.size()]);

        mi = X10MethodInstance_c.fixThis(mi, y, x);

        while (rt != null) {
            // add any method with the same name and formalTypes from rt
            l.addAll(ts.methods(rt, mi.name(), mi.typeParameters(), mi.formalTypes(), thisVar, context));

            StructType sup = null;

            if (rt instanceof ObjectType) {
                ObjectType ot = (ObjectType) rt;
                if (ot.superClass() instanceof StructType) {
                    sup = (StructType) ot.superClass();
                }
            }

            rt = sup;
        };

        return l;
    }

    public List<MethodInstance> implemented(MethodInstance jmi) {
        X10MethodInstance mi = (X10MethodInstance) jmi;
        XRoot thisVar = mi.x10Def().thisVar();
        if (thisVar == null)
            thisVar = XTerms.makeLocal(XTerms.makeFreshName("this"));
        return implemented(mi, mi.container(), thisVar);
    }

    protected List<MethodInstance> implemented(MethodInstance jmi, StructType st, XRoot thisVar) {
        X10MethodInstance  mi = (X10MethodInstance) jmi;
        if (st == null) {
            return Collections.<MethodInstance>emptyList();
        }

        List<XVar> ys = new ArrayList<XVar>(2);
        List<XRoot> xs = new ArrayList<XRoot>(2);
        X10MethodInstance_c.buildSubst((X10MethodInstance) mi, ys, xs, thisVar);
        X10MethodInstance_c.buildSubst(st, ys, xs, thisVar);
        final XVar[] y = ys.toArray(new XVar[ys.size()]);
        final XRoot[] x = xs.toArray(new XRoot[ys.size()]);

        mi = X10MethodInstance_c.fixThis((X10MethodInstance) mi, y, x);


        List<MethodInstance> l = new LinkedList<MethodInstance>();
        l.addAll(ts.methods(st, mi.name(), mi.typeParameters(), mi.formalTypes(), thisVar, context));

        if (st instanceof ObjectType) {
            ObjectType rt = (ObjectType) st;

            Type superType = rt.superClass();

            if (superType instanceof StructType) {
                l.addAll(implemented(mi, (StructType) superType, thisVar)); 
            }

            List<Type> ints = rt.interfaces();
            for (Type t : ints) {
                if (t instanceof StructType) {
                    StructType rt2 = (StructType) t;
                    l.addAll(implemented(mi, rt2, thisVar));
                }
            }
        }

        return l;
    }

    private void superCheckOverride(X10MethodInstance mi, X10MethodInstance mj) throws SemanticException {
        boolean allowCovariantReturn = true;

        if (mi == mj)
            return;

        if (! mi.name().equals(mj.name()))
            throw new SemanticException(mi.signature() + " in " + mi.container() +
                                        " cannot override " + 
                                        mj.signature() + " in " + mj.container() + 
                                        "; method names are not equal",
                                        mi.position());

        boolean allEqual = false;


        List<LocalInstance> miFormals = mi.formalNames();
        assert miFormals.size() ==  mj.formalNames().size();
        
        mj = (X10MethodInstance) mj.formalNames(miFormals);
      
     
        if (mi.typeParameters().size() == mj.typeParameters().size() && mi.formalTypes().size() == mj.formalTypes().size()) {
            allEqual = true;
            List<SubtypeConstraint> env = new ArrayList<SubtypeConstraint>();
            for (int j = 0; j < mi.typeParameters().size(); j++) {
                Type p1 = mi.typeParameters().get(j);
                Type p2 = mj.typeParameters().get(j);
                env.add(new SubtypeConstraint_c(p1, p2, true));
            }

            if (!CollectionUtil.allElementwise(mi.formalTypes(), mj.formalTypes(), new TypeEquals(context))) {
                allEqual = false;
            }
        }

        if (!allEqual) {
            throw new SemanticException(mi.signature() + " in " + mi.container() + " cannot override " + mj.signature() + " in " + mj.container()
                                        + "; incompatible parameter types", mi.position());
        }

        if (allowCovariantReturn
                ? ! isSubtype(mi.returnType(), mj.returnType())
                : ! typeEquals(mi.returnType(), mj.returnType())) {
            if (Report.should_report(Report.types, 3))
                Report.report(3, "return type " + mi.returnType() +
                              " != " + mj.returnType());
            throw new SemanticException(mi.signature() + " in " + mi.container() +
                                        " cannot override " + 
                                        mj.signature() + " in " + mj.container() + 
                                        "; attempting to use incompatible " +
                                        "return type\n" +                                        
                                        "found: " + mi.returnType() + "\n" +
                                        "required: " + mj.returnType(), 
                                        mi.position());
        } 

        if (! ts.throwsSubset(mi, mj)) {
            if (Report.should_report(Report.types, 3))
                Report.report(3, mi.throwTypes() + " not subset of " +
                              mj.throwTypes());
            throw new SemanticException(mi.signature() + " in " + mi.container() +
                                        " cannot override " + 
                                        mj.signature() + " in " + mj.container() + 
                                        "; the throw set " + mi.throwTypes() + " is not a subset of the " +
                                        "overridden method's throw set " + mj.throwTypes() + ".", 
                                        mi.position());
        }   

        if (mi.flags().moreRestrictiveThan(mj.flags())) {
            if (Report.should_report(Report.types, 3))
                Report.report(3, mi.flags() + " more restrictive than " +
                              mj.flags());
            throw new SemanticException(mi.signature() + " in " + mi.container() +
                                        " cannot override " + 
                                        mj.signature() + " in " + mj.container() + 
                                        "; attempting to assign weaker " + 
                                        "access privileges", 
                                        mi.position());
        }

        if (mi.flags().isStatic() != mj.flags().isStatic()) {
            if (Report.should_report(Report.types, 3))
                Report.report(3, mi.signature() + " is " + 
                              (mi.flags().isStatic() ? "" : "not") + 
                              " static but " + mj.signature() + " is " +
                              (mj.flags().isStatic() ? "" : "not") + " static");
            throw new SemanticException(mi.signature() + " in " + mi.container() +
                                        " cannot override " + 
                                        mj.signature() + " in " + mj.container() + 
                                        "; overridden method is " + 
                                        (mj.flags().isStatic() ? "" : "not") +
                                        "static", 
                                        mi.position());
        }

        if (! mi.def().equals(mj.def()) && mj.flags().isFinal()) {
            // mi can "override" a final method mj if mi and mj are the same method instance.
            if (Report.should_report(Report.types, 3))
                Report.report(3, mj.flags() + " final");
            throw new SemanticException(mi.signature() + " in " + mi.container() +
                                        " cannot override " + 
                                        mj.signature() + " in " + mj.container() + 
                                        "; overridden method is final", 
                                        mi.position());
        }
    }

    public void checkOverride(MethodInstance r, MethodInstance other) throws SemanticException {
        X10MethodInstance mi = (X10MethodInstance) r;
        X10MethodInstance mj = (X10MethodInstance) other;

        String fullNameWithThis = mi.x10Def().thisVar().toString();
        XName thisName = new XNameWrapper<Object>(new Object(), fullNameWithThis);
        XRoot thisVar = XTerms.makeLocal(thisName);
        thisVar = mi.x10Def().thisVar();

        List<XVar> ys = new ArrayList<XVar>(2);
        List<XRoot> xs = new ArrayList<XRoot>(2);

        X10MethodInstance_c.buildSubst(mi, ys, xs, thisVar);
        X10MethodInstance_c.buildSubst(mj, ys, xs, thisVar);
        final XVar[] y = ys.toArray(new XVar[ys.size()]);
        final XRoot[] x = xs.toArray(new XRoot[ys.size()]);

        mi = X10MethodInstance_c.fixThis(mi, y, x);
        mj = X10MethodInstance_c.fixThis(mj, y, x);

        // Force evaluation to help debugging.
        mi.returnType();
        mj.returnType();

        superCheckOverride(mi, mj);

        X10Flags miF = X10Flags.toX10Flags(mi.flags());
        X10Flags mjF = X10Flags.toX10Flags(mj.flags());

        // Report.report(1, "X10MethodInstance_c: " + this + " canOverrideImpl " + mj);
        if (! miF.hasAllAnnotationsOf(mjF)) {
            if (Report.should_report(Report.types, 3))
                Report.report(3, mi.flags() + " is more liberal than " +
                              mj.flags());
            throw new SemanticException(mi.signature() + " in " + mi.container() +
                                        " cannot override " + 
                                        mj.signature() + " in " + mj.container() + 
                                        "; attempting to assign weaker " + 
                                        "behavioral annotations", 
                                        mi.position());
        }
    }


    public void checkOverride(MethodInstance mi, MethodInstance mj, boolean allowCovariantReturn) throws SemanticException {
        super.checkOverride(mi, mj, allowCovariantReturn);

        X10MethodInstance xmi = (X10MethodInstance) mi;
        X10MethodInstance xmj = (X10MethodInstance) mj;
        boolean entails = true;
        if (xmj.guard() == null) {
            entails = xmi.guard() == null || xmi.guard().valid();
        }
        else {
            try {
                entails = xmi.guard() == null || xmj.guard().entails(xmi.guard(), ((X10Context) context).constraintProjection(xmj.guard(), xmi.guard()));
            }
            catch (XFailure e) {
                entails = false;
            }
        }

        if (! entails) {
            throw new SemanticException(xmi.signature() + " in " + xmi.container() +
                                        " cannot override " + 
                                        xmj.signature() + " in " + xmj.container() + 
                                        "; method guard is not entailed.",
                                        xmi.position());
        }
    }

    /**
     * Returns true iff <m1> is the same method as <m2>
     */
    public boolean isSameMethod(MethodInstance m1, MethodInstance m2) {
        X10MethodInstance mi = (X10MethodInstance) m1;
        X10MethodInstance mj = (X10MethodInstance) m2;

        if (mi.name().equals(mj.name())) {
            String fullNameWithThis = mi.x10Def().thisVar().toString();
            XName thisName = new XNameWrapper<Object>(new Object(), fullNameWithThis);
            XRoot thisVar = XTerms.makeLocal(thisName);

            List<XVar> ys = new ArrayList<XVar>(2);
            List<XRoot> xs = new ArrayList<XRoot>(2);

            X10MethodInstance_c.buildSubst(mi, ys, xs, thisVar);
            X10MethodInstance_c.buildSubst(mj, ys, xs, thisVar);
            final XVar[] y = ys.toArray(new XVar[ys.size()]);
            final XRoot[] x = xs.toArray(new XRoot[ys.size()]);

            mi = X10MethodInstance_c.fixThis(mi, y, x);
            mj = X10MethodInstance_c.fixThis(mj, y, x);

            return ((X10MethodInstance) m1).hasFormals(mj.formalTypes(), context);
        }
        return false;
    }

    public boolean callValid(ProcedureInstance<? extends ProcedureDef> prototype, Type thisType, List<Type> argTypes) {
        return X10MethodInstance_c.callValidImpl((X10ProcedureInstance<?>) prototype, thisType, argTypes, context);
    }
    
  
}
