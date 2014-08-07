/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10.types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

import polyglot.main.Reporter;
import polyglot.types.ClassType;
import polyglot.types.ConstructorInstance;
import polyglot.types.Context;
import polyglot.types.DerefTransform;
import polyglot.types.Flags;
import polyglot.types.LazyRef;
import polyglot.types.LazyRef_c;
import polyglot.types.LocalDef;
import polyglot.types.LocalInstance;
import polyglot.types.Name;
import polyglot.types.NoClassException;
import polyglot.types.NoMemberException;
import polyglot.types.NullType;
import polyglot.types.ObjectType;
import polyglot.types.ProcedureDef;
import polyglot.types.ProcedureInstance;
import polyglot.types.ProcedureInstance_c;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.ContainerType;
import polyglot.types.Type;
import polyglot.types.TypeEnv_c;
import polyglot.types.TypeObject;
import polyglot.types.TypeSystem_c;
import polyglot.types.Types;
import polyglot.types.TypeSystem;
import polyglot.types.Def;
import polyglot.types.MemberDef;
import polyglot.types.TypeSystem_c.ConstructorMatcher;
import polyglot.types.TypeSystem_c.TypeEquals;
import polyglot.util.CodedErrorInfo;
import polyglot.util.CollectionUtil; import x10.util.CollectionFactory;
import polyglot.util.ErrorInfo;
import polyglot.util.ErrorQueue;
import polyglot.util.InternalCompilerError;
import polyglot.util.TransformingList;
import polyglot.util.Transformation;
import x10.constraint.XEQV;
import x10.constraint.XFailure;
import x10.constraint.XLit;
import x10.types.constraints.ConstraintManager;
import x10.constraint.XVar;
import x10.constraint.XTerm;
import x10.errors.Errors;
import x10.types.ParameterType.Variance;
import polyglot.types.TypeSystem_c.Bound;
import polyglot.types.TypeSystem_c.Kind;
import polyglot.ast.Expr;
import x10.types.checker.PlaceChecker;
import x10.types.constraints.CConstraint;
import x10.types.constraints.ConstraintMaker;
import x10.types.constraints.SubtypeConstraint;
import x10.types.constraints.TypeConstraint;
import x10.types.constraints.CField;
import x10.types.constraints.CAtom;
import x10.types.constraints.CThis;
import x10.types.constraints.CSelf;

import x10.types.matcher.Matcher;
import x10.types.matcher.Subst;
import x10.visit.Desugarer;

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
    }


    public static final Name ANONYMOUS = Name.make("<anonymous>");

    /**
     * Assert that <code>ct</code> implements all abstract methods required;
     * that is, if it is a concrete class, then it must implement all
     * interfaces and abstract methods that it or it's superclasses declare, and if 
     * it is an abstract class then any methods that it overrides are overridden 
     * correctly.
     */
    @Override
    public void checkClassConformance(ClassType ct) throws SemanticException {
        if (ct.flags().isAbstract()) {
            // don't need to check interfaces of abstract classes
            return;
        }

        // build up a list of superclasses and interfaces that ct 
        // extends/implements that may contain abstract methods that 
        // ct must define.
        List<Type> superInterfaces = ts.abstractSuperInterfaces(ct);
        
        // check each abstract method of the classes and interfaces in superInterfaces
        for (Type it : superInterfaces) {
        	// Everything from Any you get 'for free'
        	// [DC] perhaps it == ts.Any() is more appropriate here?
        	if (ts.typeEquals(it, ts.Any(), context)) continue;
            if (it instanceof ContainerType) {
                ContainerType rt = (ContainerType) it;
                for (MethodInstance mi : rt.methods()) {
                    if (!mi.flags().isAbstract()) {
                        // the method isn't abstract, so ct doesn't have to implement it.
                        continue;
                    }

                    mi = expandPropertyInMethod(mi);
                    MethodInstance mj = ts.findImplementingMethod(ct, mi, context);
                    if (mj == null) {
                    	if (Types.isX10Struct(ct)) {
                    		// [DC] what about toString and typeName() ???
                    		// Ignore checking requirement if the method is equals(Any), and ct is a struct.
                    		if (mi.name().toString().equals("equals")) {
                    			List<Type> argTypes = mi.formalTypes();
                    			if (argTypes.size() == 1 && ts.typeEquals(argTypes.get(0), ts.Any(), ts.emptyContext())) {
                    				continue;
                    			}
                    		}
                    		// Ignore checking requirement if the method is hashCode(), and ct is a struct.
                    		if (mi.name().toString().equals("hashCode")) {
                    			List<Type> argTypes = mi.formalTypes();
                    			if (argTypes.size() == 0) {
                    				continue;
                    			}
                    		}
                    	}
                    	// [DC] hack: need to figure out what to do about toString in the absence of x10.lang.Object
                    	if (mi.name().toString().equals("hashCode"))
                    		continue;
                    	if (mi.name().toString().equals("equals"))
                    		continue;
                    	if (mi.name().toString().equals("toString"))
                    		continue;
                    	if (mi.name().toString().equals("typeName"))
                    		continue;

                    	// workaround for XTENLANG-3348
                    	boolean isManaged = ((x10.ExtensionInfo) ts.extensionInfo()).isManagedX10();
                    	if (isManaged && mi.name().toString().equals("compareTo") && rt.fullName().toString().equals("x10.lang.Comparable")) {
                    		ErrorQueue eq = ts.extensionInfo().compiler().errorQueue();
                    		eq.enqueue(ErrorInfo.WARNING, ct.fullName()+" does not define "+mi.signature()+", which is declared in "+rt.fullName()+".  This is usually an error, but skipping this for Java interop.", ct.errorPosition());
                    		continue;
                    	}

                    	if (!ct.flags().isAbstract()) {
                            SemanticException e = new SemanticException(ct.fullName()
                                    + " should be declared abstract; it does not define "
                                    + mi.signature()
                                    + ", which is declared in "
                                    + rt.toClass().fullName(), ct.errorPosition());
                            Map<String, Object> map = CollectionFactory.newHashMap();
                            map.put(CodedErrorInfo.ERROR_CODE_KEY, CodedErrorInfo.ERROR_CODE_METHOD_NOT_IMPLEMENTED);
                            Name name = ct.name();
                            if (name == null) {
                                name = ANONYMOUS;
                            }
                            map.put("CLASS", name.toString());
                            map.put("METHOD", mi.name().toString());
                            map.put("SUPER_CLASS", rt.toClass().name().toString());
                            e.setAttributes(map);
                            throw e;
                        }
                        else { 
                            // no implementation, but that's ok, the class is abstract.
                        }
                    }
                    // the checks below will be done by the MethodDecl's conformance check
                    /*
                    else if (!typeEquals(ct, mj.container()) && !typeEquals(ct, mi.container())) {
                        try {
                            // check that mj can override mi, which
                            // includes access protection checks.
                            checkOverride((MethodInstance) mj.container(ct), (MethodInstance) mi.container(ct));
                            //checkOverride(ct, mj, mi);
                        }
                        catch (SemanticException e) {
                            // change the position of the semantic
                            // exception to be the class that we
                            // are checking.
                            throw new SemanticException(e.getMessage(), ct.position());
                        }
                    }
                    else {
                        // the method implementation mj or mi was
                        // declared in ct. So other checks will take
                        // care of access issues
                        checkOverride(ct, mj, mi);
                    }
                    */
                }
            }
        }
    }

    
 /*   public void checkOverride(ClassType ct, MethodInstance mi, MethodInstance mj) throws SemanticException {
         XVar thisVar =  ConstraintManager.getConstraintSystem().makeUQV(); // ConstraintManager.getConstraintSystem().makeUQV(ConstraintManager.getConstraintSystem().makeFreshName("this")); // ConstraintManager.getConstraintSystem().makeLocal(ConstraintManager.getConstraintSystem().makeFreshName("this"));

         List<XVar> ys = new ArrayList<XVar>(2);
         List<XVar> xs = new ArrayList<XVar>(2);

         MethodInstance_c.buildSubst(ct, ys, xs, thisVar);
         MethodInstance_c.buildSubst(mi, ys, xs, thisVar);
         MethodInstance_c.buildSubst(mj, ys, xs, thisVar);
         final XVar[] y = ys.toArray(new XVar[ys.size()]);
         final XVar[] x = xs.toArray(new XVar[ys.size()]);

         Context cxt = context; // PlaceChecker.pushHereTerm(mi.def(), (X10Context) context);
         X10TypeEnv_c newEnv = new X10TypeEnv_c(cxt);
         mi = newEnv.fixThis(mi, y, x);
         mj = newEnv.fixThis(mj, y, x);

         // Force evaluation to help debugging.
         mi.returnType();
         mj.returnType();

        newEnv.checkOverride(mi, mj, true);
    }
*/
    /* (non-Javadoc)
     * @see x10.types.X10TypeEnv#consistent(x10.types.constraints.CConstraint)
     */
    public boolean consistent(CConstraint c) { // todo: this is a horrible (!!!) signature because we do not use "this"! Make is static, or rename it, or use the context!
        return c.consistent(); // todo: why is a TypeConstraint behaviour below different from a CConstraint here?
    }

    /* (non-Javadoc)
     * @see x10.types.X10TypeEnv#consistent(x10.types.constraints.TypeConstraint)
     */
    public boolean consistent(TypeConstraint c) {
        return c.consistent(context);
    }

    /* (non-Javadoc)
     * @see x10.types.X10TypeEnv#consistent(polyglot.types.Type)
     */
    public boolean consistent(Type t) {
        if (t instanceof ConstrainedType) {
            ConstrainedType ct = (ConstrainedType) t;
            if (!consistent(Types.get(ct.baseType()))) {
            	//System.out.println("INCONSISTENT: base type "+t);
                return false;
            }
            if (!consistent(Types.get(ct.constraint()))) {
            	//System.out.println("INCONSISTENT: constraint "+t);
                return false;
            }
        }
        if (t instanceof MacroType) {
            MacroType mt = (MacroType) t;
            for (Type ti : mt.typeParameters()) {
                if (!consistent(ti)) {
                	//System.out.println("INCONSISTENT: macrotype param "+t);
                    return false;
                }
            }
            for (Type ti : mt.formalTypes()) {
                if (!consistent(ti)) {
                	//System.out.println("INCONSISTENT: macrotype formal "+t);
                    return false;
                }
            }
        }
        if (t instanceof X10ParsedClassType) {
        	X10ParsedClassType ct = (X10ParsedClassType) t;
        	if (ct.typeArguments() != null) {
        		for (Type ti : ct.typeArguments()) {
        			if (!consistent(ti)) {
                    	//System.out.println("INCONSISTENT: type argument "+t);
        				return false;
        			}
        		}
        		final X10ClassDef def = ct.x10Def();
        		TypeConstraint c = Types.get(def.typeBounds());
        		if (c != null) { // We need to prove the context entails "c" (the class invariant) after we substituted the type arguments
        			TypeConstraint equals = ct.subst().reinstantiate(c);
        			if (!new X10TypeEnv_c(context).consistent(equals)) {
                    	//System.out.println("INCONSISTENT: entailment "+t+" => "+c+"      "+equals);
                    	//System.out.println(context);
        				return false;
        			}
        		}
        	}
        }
        //if (!consistent(X10TypeMixin.realX(t)))
        //    return false;
        return true;
    }
   
    
    /* (non-Javadoc)
     * @see x10.types.X10TypeEnv#upperBounds(polyglot.types.Type, boolean)
     */
    public List<Type> upperBounds(Type t, boolean includeObject) {
    	List<Type> bounds = bounds(t, Bound.UPPER, includeObject);
        return bounds;
    }
    public List<Type> upperBounds(Type t) {
    	return upperBounds(t, false);
    }
  
    public List<Type> upperTypeBounds(Type t) {
    	List<Type> bounds = typeBounds(t, Bound.UPPER);
        return bounds;
    }
    public List<Type> lowerTypeBounds(Type t) {
    	List<Type> bounds = typeBounds(t, Bound.LOWER);
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
            return Collections.<Type>emptyList();
        
        List<Type> upper = new ArrayList<Type>();
        List<Type> lower = new ArrayList<Type>();

        for (SubtypeConstraint term : c.terms()) {
            if (term.isHaszero()) continue;
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
                else if (term.isSubtypeConstraint()) {
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
            Set<Type> equals = CollectionFactory.newHashSet();
            equals.addAll(upper);
            equals.retainAll(lower);
            return new ArrayList<Type>(equals);
        }
        
        return Collections.<Type>emptyList();
    }
    
    
    public Kind kind(Type t) {
        Context  c = (Context) this.context;
        t = Types.baseType(t);
        if (t instanceof FunctionType)
            return Kind.INTERFACE;
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
            if (ct.flags().isInterface())
                return Kind.INTERFACE;

            if (ct.flags().isStruct())
                return Kind.STRUCT;
            else
                return Kind.OBJECT;
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
                else if (k == Kind.EITHER && k2 == Kind.STRUCT)
                    k = Kind.STRUCT;
                else if (k == Kind.EITHER && k2 == Kind.OBJECT)
                    k = Kind.OBJECT;
                else
                    k = Kind.NEITHER;
            }
            return k;
        }
        return Kind.NEITHER;
    }

    List<Type> typeBounds(Type t, Bound kind) {
        List<Type> result = new ArrayList<Type>();
        Set<Type> visited = CollectionFactory.newHashSet();
        
        LinkedList<Type> worklist = new LinkedList<Type>();
        worklist.add(t);
        
        while (! worklist.isEmpty()) {
            Type w = worklist.removeFirst();
        
            // Expand macros, remove constraints
            Type expanded = Types.baseType(w);
        
            if (visited.contains(expanded)) {
                continue;
            }
        
            visited.add(expanded);
        
//            // Get constraints from the type's where clause.
//            CConstraint wc = X10TypeMixin.xclause(w);
//            if (wc != null) {
//                List<Type> b = getBoundsFromConstraint(t, wc, kind);
//                worklist.addAll(b);
//            }
        
            if (expanded instanceof ParameterType) {
            	/* [DC] this code cannot handle more general type constraints 
            	 * like == haszero and isref let us remove it and implement 
            	 * the same functionality via appending to the context
            	 * vj: Removing this code lead to breakage of F-bounded types.
            	 * See XTENLANG-3265. Restoring it.*/
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

            	// FIXME: [IP] hasParams() does not check for parameters accessible from the outer class 
            	if (ct.hasParams()) {
            		List<Type> typeArgs = ct.typeArguments();
            		X10ClassDef def = ct.x10Def();
            		List<Variance> variances = def.variances();
            		if (typeArgs != null && typeArgs.size() == def.typeParameters().size()) {
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
            }
            result.add(expanded);
        }
        
        if (kind == Bound.UPPER && result.isEmpty())
            return Collections.<Type>singletonList(ts.Any());
        return new ArrayList<Type>(result);
    }

    List<Type> bounds(Type t, Bound kind, boolean  includeObject) {
        List<Type> result = new ArrayList<Type>();
        Set<Type> visited = CollectionFactory.newHashSet();
        
        LinkedList<Type> worklist = new LinkedList<Type>();
        worklist.add(t);
        
        while (! worklist.isEmpty()) {
            Type w = worklist.removeFirst();
        
           
        
            if (visited.contains(w)) {
                continue;
            }
        
            visited.add(w);
        
//            // Get constraints from the type's where clause.
//            CConstraint wc = X10TypeMixin.xclause(w);
//            if (wc != null) {
//                List<Type> b = getBoundsFromConstraint(t, wc, kind);
//                worklist.addAll(b);
//            }
        
            // Expand macros
            Type expanded = Types.baseType(w);
            if (expanded instanceof ParameterType) {
                ParameterType pt = (ParameterType) expanded;
                X10Def def = (X10Def) Types.get(pt.def());
                TypeConstraint c = context.currentTypeConstraint();
                if (c != null) {
                     List<Type> b = getBoundsFromConstraint(pt, c, kind);
                     worklist.addAll(b);
                }
                continue;
            }
            result.add(w);
        }
        
        if (kind == Bound.UPPER && result.isEmpty())
            if (includeObject)
                return Collections.<Type>singletonList(ts.Any());
            else
                return Collections.<Type>emptyList();
        
        return new ArrayList<Type>(result);
    }

    @Override
    public Type findMemberType(Type container, Name name) throws SemanticException {
        // FIXME: check for ambiguities
        for (Type t : upperBounds(container, true)) {
            try {
                List<Type> tl = ts.classContextResolver(container, context)
                .find(ts.MemberTypeMatcher(container, name, context));

                for (Type n : tl) {
                    if (n instanceof ClassType) {
                        return (ClassType) n;
                    }
                }
            }
            catch (SemanticException e) {
            }
            try {
                return ts.findTypeDef(t, name, Collections.<Type>emptyList(), Collections.<Type>emptyList(), context);
            }
            catch (SemanticException e) {
            }
        }

        throw new NoClassException(name.toString(), container);
    }

    /* (non-Javadoc)
     * @see x10.types.X10TypeEnv#findAcceptableTypeDefs(polyglot.types.Type, x10.types.X10TypeSystem_c.TypeDefMatcher)
     */
    public List<MacroType> findAcceptableTypeDefs(Type container, TypeDefMatcher matcher)
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

        Set<Type> visitedTypes = CollectionFactory.newHashSet();

        LinkedList<Type> typeQueue = new LinkedList<Type>();
        Set<Type> preventInfiniteRecursion = CollectionFactory.newHashSet(); // e.g., class CircularityTests { property i() = 5; class R extends R {i()==5} {} }

        // Get the upper bound of the container.
        typeQueue.addAll(upperBounds(container, true));

        while (! typeQueue.isEmpty()) {
            Type t = typeQueue.removeFirst();
            
            { // preventing infinite recursion
                Type baseType = Types.baseType(t);
                if (preventInfiniteRecursion.contains(baseType)) continue;
                preventInfiniteRecursion.add(baseType);
            }

            if (t instanceof X10ParsedClassType) {
                X10ParsedClassType type = (X10ParsedClassType) t;

                if (visitedTypes.contains(type)) {
                    continue;
                }

                visitedTypes.add(type);

                if (reporter.should_report(Reporter.types, 2))
                    reporter.report(2, "Searching type " + type + " for method " + matcher.signature());

                for (Iterator<Type> i = type.typeMembers().iterator(); i.hasNext(); ) {
                    Type ti = i.next();

                    if (!(ti instanceof MacroType)) {
                        continue;	    		
                    }

                    MacroType mi = (MacroType) ti;

                    if (reporter.should_report(Reporter.types, 3))
                        reporter.report(3, "Trying " + mi);

                    try {
                        mi = matcher.instantiate(mi);

                        if (mi == null) {
                            continue;
                        }

                        if (isAccessible(mi)) {
                            if (reporter.should_report(Reporter.types, 3)) {
                                reporter.report(3, "->acceptable: " + mi + " in " + mi.container());
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

                final Type superClass = ot.superClass();
                if (superClass != null) {
                    typeQueue.addLast(superClass);
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
        return isSubtype(null, t1, t2);
    }
     
    public boolean isSubtype(XVar x, Type t1, Type t2) {
        boolean res = isOldSubtype(x, t1,t2);
 /*       if (res == false && x != null && t1 != null && t2 != null) {
        	System.out.println("XVar "+x.toString());
        	System.out.println("type1 "+t1.toString());
        	System.out.println("type2 "+t2.toString());
        }*/
        return res; 
        /*
        boolean old = isOldSubtype(x, t1,t2);
        boolean newEq = isNewSubtype(x, t1,t2);
        if (old != newEq) {
            System.out.println("isSubtype: now " + (old ? "not " : "") + " a subtype " 
                               + "\n\t: t1=" + t1 
                               + "\n\t: t2=" + t2);
        }
        return old;*/
    }

    private boolean isInterface(ClassType t) {
        return t.flags().isInterface();
    }
    public MethodInstance expandPropertyInMethod(MethodInstance mi) {
        // expand property methods in all formals and guard
        mi = (MethodInstance) mi.formalNames(new TransformingList<LocalInstance,LocalInstance>(mi.formalNames(), new Transformation<LocalInstance, LocalInstance>() {
            public LocalInstance transform(LocalInstance o) {
                return o.type(expandPropertyInMethodNonNull(o.type()));
            }
        }));
        if (mi.guard()!=null)
            mi = (MethodInstance) mi.guard( ifNull(expandProperty(true,mi.guard()),mi.guard()) );
        mi = (MethodInstance) mi.formalTypes(new TransformingList<Type,Type>(mi.formalTypes(), new Transformation<Type, Type>() {
            public Type transform(Type o) {
                return expandPropertyInMethodNonNull(o);
            }
        }));
        if (mi.returnType()!=null) {
        	mi = mi.returnType(expandPropertyInMethodNonNull(mi.returnType()));
        }
        return mi;
    }
    public Type expandPropertyInSubtype(Type t1, Type t2) {
        // we expand properties in t2 based on the property definitions in t1.
        // e.g., val i:I{self.p()==2} = c;
        // where I is an interface with abstract property p, and C is a class with concrete definition for p.
        return expandProperty(false, t2);
    }
    public static <T> T ifNull(T t, T def) {
        return t==null ? def : t;
    }
    public Type expandPropertyInMethodNonNull(Type t2) {
        return ifNull(expandProperty(true,t2), t2);
    }
    public Type expandProperty(final boolean isMethod, Type t2) {
    	if (t2 instanceof X10ClassType) {
    		X10ClassType ct = (X10ClassType) t2;
    		List<Type> old_ta = ct.typeArguments();
    		if (old_ta != null) {
	            List<Type> new_ta = new TransformingList<Type,Type>(old_ta, new Transformation<Type, Type>() {
	                public Type transform(Type o) {
	                    return expandPropertyInMethodNonNull(o);
	                }
	            });
	            t2 = ct.typeArguments(new_ta);
    		}
    	}
        if (!(t2 instanceof ConstrainedType)) return t2;
        ConstrainedType t2c = (ConstrainedType) t2;
        CConstraint originalConst = Types.get(t2c.constraint());
        CConstraint newConstraint = expandProperty(isMethod, originalConst);
        if (newConstraint==null) return null;
        if (newConstraint!=originalConst)
            t2 = Types.xclause(Types.baseType(t2), newConstraint);
        return t2;
    }
    public CConstraint expandProperty(final boolean isMethod, CConstraint originalConst) {
        final List<? extends XTerm> terms = originalConst.constraints();
        final ArrayList<XTerm> newTerms = new ArrayList<XTerm>(terms.size());
        boolean wasNew = false;
        for (XTerm xTerm : terms) {
            final XTerm.TermVisitor visitor = new XTerm.TermVisitor() {
                public XTerm visit(XTerm term) {
                    XTerm res = XTypeTranslator.expandPropertyMethod(term,isMethod,ts,context);
                    return res==term ? null : res;
                }
            };
            final XTerm newXterm = xTerm.accept(visitor);
            wasNew |= newXterm!=xTerm;
            newTerms.add(newXterm);
        }
        if (wasNew) {
            final CConstraint newConstraint = ConstraintManager.getConstraintSystem().makeCConstraint(originalConst.self());
            newConstraint.setThisVar(originalConst.thisVar());
            for (XTerm xTerm : newTerms) {
                try {
                    newConstraint.addTerm(xTerm);
                } catch (XFailure xFailure) {
                    return null;
                }
            }
            if (!newConstraint.consistent())
                return null;
            return newConstraint;
        }
        return originalConst;
    }
    /* (non-Javadoc)
     * @see x10.types.X10TypeEnv#isSubtype(polyglot.types.Type, polyglot.types.Type, boolean)
     */
    boolean isOldSubtype(XVar x, Type t1, Type t2) {
    	assert t1 != null;
    	assert t2 != null;

    	if (t1 == t2) 
    		return true;

    	if (ts.isUnknown(t1) || ts.isUnknown(t2)) return true; // used to be hasUnknown, which checks whether any part of the type is unknown, but it is inefficient (it is enough to check just the base type for unknown).

    	if (t1.isVoid())
    		return t2.isVoid();
    	if (t2.isVoid())
    		return false;

    	t1 = ts.expandMacros(t1);
    	t2 = ts.expandMacros(t2);
        t1 = expandProperty(false, t1);
        t2 = expandProperty(false, t2);
        if (t2==null) return false;
        assert !t1.isVoid() && !t2.isVoid();
        
    	if (ts.isAny(t2))
    		return true;
    	Context xcontext = context;

    	{
    		boolean isStruct1 = Types.isX10Struct(t1);
    		boolean isStruct2 = Types.isX10Struct(t2);


    		if (isStruct2) {
    			// t1 must be a struct, and the bases must be the same.
    			// No -- it could be a type parameter.
    			if (isStruct1 && ! (ts.typeEquals(Types.baseType(t1), Types.baseType(t2),
    					xcontext)))
    				return false;

    			// now keep going, the clause entailment will be checked by the
    			// logic below.
    		} else if (isStruct1) {
    			if (! ts.isInterfaceType(t2))
    				return false;
    			// t1 is a struct, and t2 is an interface
    		}
    	}
    	
    	if (t1.isNull())
    		return Types.permitsNull(xcontext, t2);
    	

    	if (t2.isNull()) 
    		return false;
    	
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

    		Context xc2 = xcontext.pushBlock();
    		//Context xc2 = ((X10Context_c) xcontext).pushTypeConstraint(newEnv);
    		xc2.setTypeConstraint(newEnv);
    		X10TypeEnv_c tenv = shallowCopy();
    		tenv.context = xc2;

    		if (term.isEqualityConstraint()) {
    			SubtypeConstraint eq = term;
    			Type l = eq.subtype();
    			Type r = eq.supertype();
    			if (tenv.isSubtype(t1, l) 
    					&& tenv.isSubtype(r, t2)) {
    				return true;
    			}
    			if (tenv.isSubtype(t1, r) 
    					&& tenv.isSubtype(l, t2)) {
    				return true;
    			}
    		}
    		else if (term.isSubtypeConstraint()) {
    			SubtypeConstraint s = term;
    			Type l = s.subtype();
    			Type r = s.supertype();
    			if (tenv.isSubtype(t1, l) 
    					&& tenv.isSubtype(r, t2)) {
    				return true;
    			}
    		}
    	}
    	Type baseType1 = Types.baseType(t1);
    	
    	if (typeEquals(baseType1,t2))
    		return true;
    	
    	Type baseType2 = Types.baseType(t2);
    	CConstraint c1 = Types.realX(t1);
    	if (c1!= null && x != null) {
    		c1 = c1.instantiateSelf(x);
    	}
    	
    	CConstraint c2 = Types.xclause(t2);  // NOTE: xclause, not realX (you want "c2" to have as few constraints as possible).
    
    	if (c2 != null && c2.valid()) { 
    		c2 = null; 
    	}
    	if (c2 != null && x != null) {
    		c2 = c2.instantiateSelf(x);
    	}
    	if (c1 != null && c1.valid()) 
    		c1 = null; 

    	if (x == null) {
    		// Now generate x and substitute it for self in both t1 and t2.

    		/*if (c1 != null) {
    			x = c1.selfVarBinding();
    		}
    		if (x == null && c2 != null) {
    			x = c2.selfVarBinding();
    		}*/
    		if (x == null) {
    			x =  ConstraintManager.getConstraintSystem().makeUQV(); 
    		}
    		t2 = Types.instantiateSelf(x, t2);
    		c2 = Types.xclause(t2);
    		if (c2 != null && c2.valid()) {
    			c2 = null;
    			
    		}
    		if (c1 != null)
    			c1 = c1.copy().instantiateSelf(x);

    		CConstraint c = null;
    		c = xcontext.constraintProjection(c1, c2);
    		
//  		c1 = xcontext.constraintProjection(c1);
//  		c2 = xcontext.constraintProjection(c2);

    		if (c1 != null && ! c.entails(c1)) {
    			// Update the context, by adding the
    			// real clause of t1 to the current constraint, 
    			// and proceed with x, baseType1 and t2.
    			// Must do this even if c2==null since t1 and t2 may be parametric.
    			try {
    				xcontext = (Context) xcontext.pushBlock();	

    				c.addIn(c1);
    				if (! c.consistent()) {
    					return false;
    				}
    				xcontext.setCurrentConstraint(c);

    				X10TypeEnv_c tenv = shallowCopy();
    				tenv.context = xcontext;

    				if (c2 != null)
    					t2 = Subst.subst(t2, x, c2.self());
    				
    				return tenv.isSubtype(x, baseType1, t2);
    			} 	 catch (SemanticException z) {
    				throw new InternalCompilerError("Unexpected failure ", z);
    			}
    		}

    		if (! c.entails(c2))
    			return false;

    		return isSubtype(x, baseType1, baseType2);
    		
    	} 
    	
    	t1=baseType1;
    	
    	if (baseType2 != t2) {
    		if (! entails(c1, c2))
    			return false;
    		if (isSubtype(x, baseType1, baseType2 ))
    			return true;
    	}
    	// At this point the constraint has been checked and baseType2 == t2.

    	// Handle function types
    	if (baseType1 instanceof FunctionType && baseType2 instanceof FunctionType) {
            FunctionType ft1 = (FunctionType) baseType1;
            FunctionType ft2 = (FunctionType) baseType2;
            // (x1:S1,..., xn:Sn){c}=>S <: (y1:T1,..., yn:Tn){d}=>T provided that
            // 1. Ti <: Si, for i in 1..n
            // 2. c entails d
            // 3. S <: T
            List<Type> Sl = ft1.argumentTypes();
            Type S = ft1.returnType();
            CConstraint c = ft1.guard();
            List<Type> Tl = ft2.argumentTypes();
            Type T = ft2.returnType();
            CConstraint d = ft2.guard();
            if (Sl.size() != Tl.size())
                return false;
            XVar[] ys = Types.toVarArray(Types.toLocalDefList(ft2.formalNames()));
            XVar[] xs = Types.toVarArray(Types.toLocalDefList(ft1.formalNames()));
            try {
                Sl = Subst.subst(Sl, ys, xs);
                if (c != null) {
                    c = Subst.subst(c, ys, xs);
                }
                S = Subst.subst(S, ys, xs);
            } catch (SemanticException e) {
                throw new InternalCompilerError("Unexpected exception comparing function types", ft1.position(), e);
            }
            for (int i = 0; i < Sl.size(); i++) {
                Type Si = Sl.get(i);
                Type Ti = Tl.get(i);
                if (!isSubtype(x, Ti, Si))
                    return false;
            }
            if (!entails(d, c))
                return false;
            if (!isSubtype(x, S, T))
                return false;
            return true;
    	}

    	// Handle parameterized types and interfaces
    	if (baseType1 instanceof X10ClassType && baseType2 instanceof X10ClassType) {
    		X10ClassType ct1 = (X10ClassType) baseType1;
    		X10ClassType ct2 = (X10ClassType) baseType2;
    		if (ct1.def() == ct2.def()) { // so the base types are identical
    			X10ClassDef def = ct1.x10Def();
    			int numParams = def.typeParameters().size();
    			if (numParams > 0) {
    				if (ct1.typeArguments() == null && ct2.typeArguments() == null)
    				    return true;
    				if (ct1.typeArguments() == null || ct2.typeArguments() == null)
    				    return false;
    				if (ct1.typeArguments().size() != numParams)
    					return false;
    				if (ct2.typeArguments().size() != numParams)
    					return false;
        			// XTENLANG-2118 Java array hack
        			if (ts.isJavaArray(ct1)) {
        				assert (numParams == 1);
        				Type a1 = ct1.typeArguments().get(0);
        				Type a2 = ct2.typeArguments().get(0);
        				if (a1.isNumeric() || a1.isChar() || a2.isNumeric() || a2.isChar()) {
        					// The equality case should have been caught by the typeEquals() call above
        					return false;
        				}
        				// For non-numeric types, Java arrays are covariant
        				return isSubtype(a1, a2);
        			}
    				if (def.variances().size() != numParams)
    					return false; // FIXME: throw an InternalCompilerError
    				for (int i = 0; i < numParams; i++) {
    					Type a1 = ct1.typeArguments().get(i);
    					Type a2 = ct2.typeArguments().get(i);
    					if (a1 == null || a2 == null)
    						assert false;
    					ParameterType.Variance v = def.variances().get(i);
    					switch (v) {
    					case COVARIANT:
    						if (! isSubtype(a1, a2)) 
    							return false;
    						break;
    					case CONTRAVARIANT:
    						if (! isSubtype(a2, a1)) 
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
    				if (this.isSubtype(x, childRT.superClass(), ancestor)) {
    					return true;
    				}
    			}

    			// Next check interfaces.
    			List<Type> l = childRT.interfaces();
    			for (Type parentType : l) {
    				boolean tryIt = false;
    				Type pt =  parentType;
    				XVar thisVar = childRT.x10Def().thisVar();
    				try {
    					parentType = Subst.subst(parentType, x, thisVar);
    				} catch (SemanticException z) {
    					throw new InternalCompilerError("Unexpected semantic exception " + z);
    				}


    				if (isSubtype(x, parentType, ancestor)) {
    					return true;
    				}
    				
    			}
    		}
    	}

    	if (t1 instanceof ParameterType) {
    		for (Type s1 : upperTypeBounds(t1)) {
    			if (isSubtype(x, s1, t2))
    				return true;
    		}
    	}
    	if (t2 instanceof ParameterType) {
    		for (Type s2 : lowerTypeBounds(t2)) {
    			if (isSubtype(x, t1, s2))
    				return true;
    		}
    	}

    	return false;
    }

    /* (non-Javadoc)
     * @see x10.types.X10TypeEnv#typeEquals(polyglot.types.Type, polyglot.types.Type, java.util.List)
     */
  
   /* @Override
    public boolean typeEquals(Type t1, Type t2) {
        return newTypeEquals(t1,t2);
        boolean old = oldTypeEquals(t1,t2);
        boolean newEq = newTypeEquals(t1,t2);
        if (old != newEq) {
            System.out.println("TypeEquals: now " + (old ? "not " : "") + "equal " 
                               + "\n\t: t1=" + t1 
                               + "\n\t: t2=" + t2);
        }
        return old;
    }*/
    
    @Override
    public boolean typeEquals(Type t1, Type t2) {    
        t1 = ts.expandMacros(t1);
        t2 = ts.expandMacros(t2);

        if (t1 == t2)
            return true;
        if (t1 == null || t2 == null)
            return false;
        
        if (t1.isVoid())
            return t2.isVoid();
        if (t2.isVoid())
            return false;

    	if (ts.isUnknown(t1) || ts.isUnknown(t2)) return true;
          
        if (((TypeObject) t1).equalsImpl((TypeObject) t2))
            return true;
       
        
        // A type parameter T might still be equal to Int if there is a type constraint in the context saying T==Int
        //if (X10TypeMixin.isX10Struct(t1) != X10TypeMixin.isX10Struct(t2)) return false;

        Context xc = (Context) context;
        List<SubtypeConstraint> env = xc.currentTypeConstraint().terms();

        // DO NOT check if env.entails(t1 == t2); it would be recursive
        // Instead, iterate through the environment.
        for (int i = 0; i < env.size(); i++) {
            SubtypeConstraint term = env.get(i);
            List<SubtypeConstraint> newEnv = new ArrayList<SubtypeConstraint>();
            if (0 <= i-1 && i-1 < env.size()) newEnv.addAll(env.subList(0, i-1));
            if (0 <= i+1 && i+1 < env.size()) newEnv.addAll(env.subList(i+1, env.size()));
            //                    newEnv = env;
            newEnv = Collections.<SubtypeConstraint>emptyList();

            Context xc2 = xc.pushBlock();
            xc2.setTypeConstraint(newEnv);
            	//((X10Context_c) xc).pushTypeConstraint(newEnv);

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

        
        
        if (t1 instanceof ParameterType && t2 instanceof ParameterType) {
            ParameterType pt1 = (ParameterType) t1;
            ParameterType pt2 = (ParameterType) t2;
            if (TypeParamSubst.isSameParameter(pt1, pt2))
                return true;
        }
        
       

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

        if (t1 instanceof FunctionType && t2 instanceof FunctionType) {
            FunctionType ft1 = (FunctionType) t1;
            FunctionType ft2 = (FunctionType) t2;
            if (ft1.formalNames().size() == ft2.formalNames().size()) {
                // Rename formals
                XTerm[] ys = new XTerm[ft1.formalNames().size()];
                for (int i = 0; i < ys.length; i++) {
                    ys[i] = ConstraintManager.getConstraintSystem().makeUQV();
                }
                try {
                    XVar[] xs1 = Types.toVarArray(Types.toLocalDefList(ft1.formalNames()));
                    t1 = Subst.subst(ft1, ys, xs1, new Type[] { }, new ParameterType[] { });
                    XVar[] xs2 = Types.toVarArray(Types.toLocalDefList(ft2.formalNames()));
                    t2 = Subst.subst(ft2, ys, xs2, new Type[] { }, new ParameterType[] { });
                } catch (SemanticException e) {
                    throw new InternalCompilerError("Unexpected exception while transforming function types", e);
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
            List<Type> ta1 = ct1.typeArguments();
            if (ta1 == null) ta1 = Collections.<Type>emptyList();
            List<Type> ta2 = ct2.typeArguments();
            if (ta2 == null) ta2 = Collections.<Type>emptyList();
            if (ta1.size() == 0 && ta2.size() == 0)
                return true;
            if (! CollectionUtil.allElementwise(ta1, ta2, new TypeSystem_c.TypeEquals(context))) {
                return false;
            }
            return true;
        }
        Type baseType1 = Types.baseType(t1);
        Type baseType2 = Types.baseType(t2);
        if (baseType1 == t1 && baseType2 == t2)
            return false; // they dont have constraints; i have no way left of showing they are equal.
      
        if (! typeEquals(baseType1, baseType2))
            return false;
        // We must take the realX because if I have a definition:
        // class A(i:Int) {i==1} {}
        // then the types A and A{self.i==1} are equal!
        CConstraint c1 = Types.realX(t1);
        CConstraint c2 = Types.realX(t2);

        if (c1 != null && c1.valid()) { c1 = null; t1 = baseType1; }
        if (c2 != null && c2.valid()) { c2 = null; t2 = baseType2; }
        XVar temp = ConstraintManager.getConstraintSystem().makeUQV();
        // instantiateSelf ensures that Int{self123==3} and A{self456==3} are equal.
        if (c1 != null) {
            c1 = c1.instantiateSelf(temp);
        }
        if (c2 != null) {
            c2 = c2.instantiateSelf(temp);
        }
            
        if (! entails( c1, c2))
            return false;

        if (! entails(c2, c1))
            return false;
        return true;
    }
    @Override
    public boolean isCastValid(Type fromType, Type toType) {
        //	    if (isImplicitCastValid(fromType, toType))
        //	        return true;

        fromType = ts.expandMacros(fromType);
        toType =ts. expandMacros(toType);

        if (isSubtype(fromType, toType) || isSubtype(toType, fromType))
            return true;

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
            return toType.isNull() ||  Types.permitsNull(context, toType);
          
        }

        // For now, can always cast to or from a parameter type.
        //		if (fromType instanceof ParameterType || toType instanceof ParameterType) {
        //		    return true;
        //		}

        Type t1 = Types.baseType(fromType);
        Type t2 = Types.baseType(toType);
        CConstraint c1 = Types.realX(fromType);
        CConstraint c2 = Types.realX(toType);


        Type baseType1 = t1;
        Type baseType2 = t2;

        if (c1 != null && c1.valid()) { c1 = null; }
        if (c2 != null && c2.valid()) { c2 = null; }

        if (c1 != null && c2 != null && ! clausesConsistent(c1, c2))
            return false;

        if (baseType1 != fromType || baseType2 != toType)
            return isCastValid(baseType1, baseType2);

        if (ts.isStructType(baseType1) && ts.isObjectType(baseType2, (Context) context))
            return false;

        if (ts.isObjectType(baseType1, (Context) context) && ts.isStructType(baseType2))
            return false;

        if (ts.isParameterType(baseType1) || ts.isParameterType(baseType2))
            return true;

        if (ts.hasUnknown(baseType1) || ts.hasUnknown(baseType2))
            return true;

        return super.isCastValid(baseType1, baseType2);
    }

    public boolean isImplicitNumericCastValid(Type fromType, Type toType) {
        return isPrimitiveConversionValid(fromType, toType);
    }

    @Override
    public boolean isImplicitCastValid(Type fromType, Type toType) {
        return isSubtype(fromType, toType);
    }

    /* (non-Javadoc)
     * @see x10.types.X10TypeEnv#entails(x10.constraint.CConstraint, x10.constraint.CConstraint)
     */
    public boolean entails(final CConstraint c1, final CConstraint c2) {
        if (c2 == null || c2.valid())
            return true;
        if (c1 != null && ! c1.consistent())
            return false;

        final Context xc = context;
        if (c1 == null) {
            CConstraint sigma = xc.constraintProjection(c2);
            return sigma.entails(c2);
        }
        return c1.entails(c2, new ConstraintMaker() {
                public CConstraint make() throws XFailure {
                    return xc.constraintProjection(c1, c2);
                }
            });
        
    }

    
    /** Return true if there is a conversion from fromType to toType.  Returns false if the two types are not both value types. */
    public boolean isPrimitiveConversionValid(Type fromType, Type toType) {
        Type baseType1 = Types.baseType(fromType);
        CConstraint c1 = Types.realX(fromType);
        Type baseType2 = Types.baseType(toType);
        CConstraint c2 = Types.realX(toType);

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
            XVar self = Types.selfVar(c1);
            if (self instanceof XLit) {
                Object val = ((XLit) self).val();
                if (numericConversionValid(baseType2, baseType1, val)) {
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
            
            // [DC] seems this code is now redundant since we have user-defined operators
            if (true) return false;
            
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
    
            Type base = Types.baseType(toType);
            
            boolean fits = false;
            
            boolean signedFrom = ts.isSignedNumeric(fromType);
            boolean unsignedFrom = ts.isUnsignedNumeric(fromType);

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
            
            XLit val = ConstraintManager.getConstraintSystem().makeLit(value, base);

            CConstraint c = ConstraintManager.getConstraintSystem().makeCConstraint();
            c.addSelfBinding(val);
            return entails(c, Types.realX(toType));
    }

    protected boolean typeRefListEquals(List<Ref<? extends Type>> l1, List<Ref<? extends Type>> l2) {
        return CollectionUtil.<Type>allElementwise(new TransformingList<Ref<? extends Type>, Type>(l1, new DerefTransform<Type>()),
                                                   new TransformingList<Ref<? extends Type>, Type>(l2, new DerefTransform<Type>()),
                                                   new TypeSystem_c.TypeEquals(context));
    }

    protected boolean typeListEquals(List<Type> l1, List<Type> l2) {
        return CollectionUtil.<Type>allElementwise(l1, l2, new TypeSystem_c.TypeEquals(context));
    }

    protected boolean listEquals(List<XVar> l1, List<XVar> l2) {
        return CollectionUtil.<XVar>allEqual(l1, l2);
    }

    protected boolean isX10BaseSubtype(Type me, Type sup) {
        Type xme = Types.baseType(me);
        Type xsup = Types.baseType(sup);
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
            CConstraint c1 = Types.realX(me);
            CConstraint c2 = Types.xclause(other);
            return entails(c1, c2);
        }
        catch (InternalCompilerError e) {
            if (e.getCause() instanceof XFailure) {
                return false;
            }
            throw e;
        }
    }

    /**
     * Return the least common ancestor of the two types. This must always exist.
     */
    @Override
    public Type leastCommonAncestor(Type type1, Type type2)
  
    {

        Type t;
        IF: if (type1.isNull() || type2.isNull()) {
            t = type1.isNull() ? type2 : type1;
            if (Types.permitsNull(context, t))
                return t;
            // Maybe there is a constraint {self!=null} that we can remove from "t", and then null will be allowed.
            // e.g., true ? null : new Test()
            //      	 T1: type(null)
     	    //      	 T2: Test{self.home==here, self!=null}
            // The lub should be:  Test{self.home==here}

            Type baseType = Types.baseType(t);
            if (!Types.permitsNull(context, baseType)) {
                // ok, so climb and find the least common ancestor via interfaces.
                break IF; // return ts.Any();
              /*  throw new SemanticException("No least common ancestor found for types \"" + type1 +
    								"\" and \"" + type2 + "\", because one is null and the other cannot contain null.");*/
            }
            // we need to keep all the constraints except the one that says the type is not null
            Type res = baseType;
            // todo: this is an attempt to keep as many constraints as possible:
            // res = Types.addConstraint(baseType, Types.allowNull(Types.realX(t)));
            // It is useful if we do LCA(null, Point(2))  because it should be Point(2)
            // However   LCA(null, Bla{self==a})  is complicated because "a" might be of type Bla{self!=null}, so we need to remove the constraint self==a.
            // See XTENLANG_2622 and XTENLANG_1380
            assert Types.consistent(res);
            assert Types.permitsNull(context, res);
            return res;
        } 
            
        t = leastCommonAncestorBase(Types.baseType(type1), Types.baseType(type2));
        
    	
    	CConstraint c1 = Types.realX(type1), c2 = Types.realX(type2);
    	CConstraint c = c1.leastUpperBound(c2);
    	if (! c.valid())
    		t = Types.addConstraint(t, c);
    	assert Types.consistent(t);
    	return t;
    }

    private FunctionType changeReturnType(FunctionType ft, Type nrt) {
        Type rt = ft.returnType();
        if (nrt == rt || typeEquals(nrt, rt))
            return ft;
        List<Ref<? extends Type>> formalTypes = new ArrayList<Ref<? extends Type>>();
        List<ParameterType> typeParameters = Collections.<ParameterType>emptyList(); // FIXME
        for (Type t : ft.argumentTypes()) {
            formalTypes.add(Types.ref(t));
        }
        List<LocalDef> formalNames = new ArrayList<LocalDef>();
        for (LocalInstance li : ft.formalNames()) {
            formalNames.add(li.def());
        }
        return ts.functionType(ft.position(), Types.ref(nrt), typeParameters, formalTypes, formalNames, Types.ref(ft.guard()));
    }

    // Assumes type1 and type2 are base types, no constraint clauses.
    private Type leastCommonAncestorBase(Type type1, Type type2)  {
        Type res = leastCommonAncestorBaseOld(type1, type2);
        // try to find something better with interfaces:
        // let's intersect all the interfaces type1 and type2 implement,
        // then let's return the one that is a subtype of all those in the intersection.
        // see XTENLANG-2635
        if (ts.Any()!=res)
            return res;
        if (ts.isAny(type1) || ts.isAny(type2)) // optimization
            return res;
        if (type1 instanceof X10ParsedClassType_c && type2 instanceof X10ParsedClassType_c) {
            X10ParsedClassType_c ct1 = (X10ParsedClassType_c) type1;
            X10ParsedClassType_c ct2 = (X10ParsedClassType_c) type2;
            Set<Type> in1 = getStrippedInterfaces(ct1);
            Set<Type> in2 = getStrippedInterfaces(ct2);
            Set<Type> intersection = new HashSet<Type>();
            Context empty = ts.emptyContext();
            for (Type t1 : in1) {
                for (Type t2 : in2) {
                    if (ts.typeEquals(t1,t2, empty))
                        intersection.add(t1);
                }
            }
            int size = intersection.size();
            // Note that Object doesn't implement Any (a bug in our compiler)
            if (size>1) {
                // let's find the most specific one
                Type mostSpecific = null;
                for (Type candidate : intersection) {
                    boolean ok = true;
                    for (Type t : intersection)
                        if (t!=candidate && !ts.isSubtype(candidate, t,empty)) {
                            ok = false;
                            break;
                        }
                    if (!ok) continue;
                    mostSpecific = candidate;
                    break;
                }
                if (mostSpecific!=null)
                    return mostSpecific;
            }
        }
        return res;
    }
    // returns all the interfaces implemented by ct, without any constraint info
    private Set<Type> getStrippedInterfaces(X10ParsedClassType_c ct) {
        Set<X10ParsedClassType_c> supertypes = ct.allSuperTypes(true);
        Set<Type> res = new HashSet<Type>();
        for (X10ParsedClassType_c s : supertypes) {
            if (s.def().flags().isInterface())
                res.add(Types.stripConstraints(s));
        }
        return res;
    }
    private Type leastCommonAncestorBaseOld(Type type1, Type type2)  {
    	if (typeEquals(type1, type2)) {
    		return type1;
    	}

    	IF: if (type1 instanceof X10ClassType && type2 instanceof X10ClassType) {
    	    if (type1 instanceof FunctionType && type2 instanceof FunctionType) {
    	        FunctionType ft1 = (FunctionType) type1;
    	        FunctionType ft2 = (FunctionType) type2;
    	        if (isSubtype(ft1, ft2))
    	            return ft2;
    	        if (isSubtype(ft2, ft1))
    	            return ft1;
    	        if (ft1 instanceof ClosureType)
    	            return leastCommonAncestor(((ClosureType) ft1).functionInterface(), ft2);
    	        if (ft2 instanceof ClosureType)
    	            return leastCommonAncestor(ft1, ((ClosureType) ft2).functionInterface());
    	        // Found two function interfaces, neither of which is a subtype of each other.
    	        // First try finding a common (covariant) return type.
    	        Type rt1 = ft1.returnType();
    	        Type rt2 = ft2.returnType();
    	        Type nrt = leastCommonAncestor(rt1, rt2);
    	        Type nft1 = changeReturnType(ft1, nrt);
    	        Type nft2 = changeReturnType(ft2, nrt);
    	        if (nft1 != ft1 || nft2 != ft2)
    	            return leastCommonAncestor(nft1, nft2);
    	        // TODO: try contravariant arguments
    	        return ts.Any(); // Two unrelated functions still extend Any
    	    }
    		if (hasSameClassDef(type1, type2)) {
    			X10ClassType ct1 = (X10ClassType) type1;
    			X10ClassType ct2 = (X10ClassType) type2;
    			if (ct1.typeArguments() == null || ct2.typeArguments() == null)
    			    return ct1.typeArguments(null);
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
    					    // OK, so you have no chance of returning an LCA based 
    					    // on analyzing the type arguments.Return to climbing
    					    // the hierarchy.
    					    break IF; // return ts.Object();
    						/*throw new SemanticException("No least common ancestor found for types \"" + type1 +
    								"\" and \"" + type2 + "\".");*/
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
    					    break IF; 
    						/*throw new SemanticException("No least common ancestor found for types \"" + type1 +
    								"\" and \"" + type2 + "\".");*/
    					break;
    				}
    			}
    			return ct1.typeArguments(newArgs);
    		}
    	}

    	if (isSubtype(type1, type2))
    		return type2;
    	if (isSubtype(type2, type1))
    		return type1;

    	// Don't consider interfaces.
    	if ((type1.isClass() && ts.isInterfaceType(type2)) ||
            (type2.isClass() && ts.isInterfaceType(type1))) {
    		return ts.Any(); // an interface may be implemented by a struct
    	}

    	// Since they are not equal, and one is not a subtype of another
    	// and one of them is a struct, the lub has to be Any.
    	if (Types.isX10Struct(type1) || Types.isX10Struct(type2)) {
    		return ts.Any();
    	}
    	// XTENLANG-2118: Since they are not equal, and one is not a subtype of another
    	// and one of them is not a subtype of Object, the lub has to be Any.
    	// [DC] hmm... this code assumed that the only hierarchy was under Object?  The only other types were function types?
    	// Commenting it out, let the code underneath do the work...
    	//if (!ts.isSubtype(type1, ts.Object()) || !ts.isSubtype(type2, ts.Object())) {
    	//	return ts.Any();
    	//}
    	// Now neither is a struct. Neither is null.
    	if (type1 instanceof ObjectType && type2 instanceof ObjectType) {
    		// Walk up the hierarchy
    		Type sup1 = ((ObjectType) type1).superClass();
    		Type sup2 = ((ObjectType) type2).superClass();

    		if (sup1 == null) return ts.Any();
    		if (sup2 == null) return ts.Any();

    		Type t1 = leastCommonAncestor(sup1, type2);
    		Type t2 = leastCommonAncestor(sup2, type1);

    		if (typeEquals(t1, t2)) 
    			return t1;
    	}
    	return ts.Any();
    }

    /* (non-Javadoc)
     * @see x10.types.X10TypeEnv#typeBaseEquals(polyglot.types.Type, polyglot.types.Type)
     */
    public boolean typeBaseEquals(Type type1, Type type2) {
        if (type1 == type2) return true;
        if (type1 == null || type2 == null) return false;
        return typeEquals(Types.baseType(type1), Types.baseType(type2));
    }

    /* (non-Javadoc)
     * @see x10.types.X10TypeEnv#typeDeepBaseEquals(polyglot.types.Type, polyglot.types.Type)
     */
    public boolean typeDeepBaseEquals(Type type1, Type type2) {
        if (type1 == type2) return true;
        if (type1 == null || type2 == null) return false;
        return typeEquals(Types.stripConstraints(type1), Types.stripConstraints(type2));
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
     * @see x10.types.X10TypeEnv#primitiveClausesConsistent(CConstraint, x10.constraint.CConstraint)
     */
    public boolean primitiveClausesConsistent(CConstraint c1, CConstraint c2) {
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
     * @see x10.types.X10TypeEnv#clausesConsistent(x10.constraint.CConstraint, x10.constraint.CConstraint)
     */
    public boolean clausesConsistent(CConstraint c1, CConstraint c2) {
        if (primitiveClausesConsistent(c1, c2)) {
            CConstraint r = c1.copy();
            r.addIn(c2);
            return r.consistent();
        }
        return false;
    }

    /** Return true if t overrides mi */
    public boolean hasFormals(ProcedureInstance<? extends ProcedureDef> pi, List<Type> formalTypes) {
        return ((ProcedureInstance_c<?>) pi).hasFormals(formalTypes, context);
    }

    @Override
    public List<MethodInstance> overrides(MethodInstance jmi) {
        MethodInstance mi = (MethodInstance) jmi;
        List<MethodInstance> l = new ArrayList<MethodInstance>();
        ContainerType rt = mi.container();

        XVar thisVar = mi.x10Def().thisVar();
        if (thisVar == null)
            thisVar = ConstraintManager.getConstraintSystem().makeThis();  

        List<XVar> ys = new ArrayList<XVar>(2);
        List<XVar> xs = new ArrayList<XVar>(2);
        MethodInstance_c.buildSubst(mi, ys, xs, thisVar);
        MethodInstance_c.buildSubst(rt, ys, xs, thisVar);
        final XVar[] y = ys.toArray(new XVar[ys.size()]);
        final XVar[] x = xs.toArray(new XVar[ys.size()]);

        mi = fixThis(mi, y, x);

        XVar placeTerm = Types.getPlaceTerm(mi);

        while (rt != null) {
            // add any method with the same name and formalTypes from rt
            l.addAll(ts.methods(rt, mi.name(), mi.typeParameters(), mi.formalNames(), thisVar, placeTerm, context));

            ContainerType sup = null;

            if (rt instanceof ObjectType) {
                ObjectType ot = (ObjectType) rt;
                if (ot.superClass() instanceof ContainerType) {
                    sup = (ContainerType) ot.superClass();
                }
            }

            rt = sup;
        };

        return l;
    }

    @Override
    public List<MethodInstance> implemented(MethodInstance jmi) {
        MethodInstance mi = (MethodInstance) jmi;
        XVar thisVar = mi.x10Def().thisVar();
        if (thisVar == null)
            thisVar = ConstraintManager.getConstraintSystem().makeThis();  
        return implemented(mi, mi.container(), thisVar);
    }

    protected List<MethodInstance> implemented(MethodInstance jmi, ContainerType st, XVar thisVar) {
        MethodInstance  mi = (MethodInstance) jmi;
        if (st == null) {
            return Collections.<MethodInstance>emptyList();
        }

        List<XVar> ys = new ArrayList<XVar>(2);
        List<XVar> xs = new ArrayList<XVar>(2);
        MethodInstance_c.buildSubst((MethodInstance) mi, ys, xs, thisVar);
        MethodInstance_c.buildSubst(st, ys, xs, thisVar);
        final XVar[] y = ys.toArray(new XVar[ys.size()]);
        final XVar[] x = xs.toArray(new XVar[ys.size()]);

        mi = fixThis((MethodInstance) mi, y, x);

        XVar placeTerm = Types.getPlaceTerm(mi);

        List<MethodInstance> l = new LinkedList<MethodInstance>();
        l.addAll(ts.methods(st, mi.name(), mi.typeParameters(), mi.formalNames(), thisVar, placeTerm, context));

        if (st instanceof ObjectType) {
            ObjectType rt = (ObjectType) st;

            Type superType = rt.superClass();

            if (superType instanceof ContainerType) {
                l.addAll(implemented(mi, (ContainerType) superType, thisVar)); 
            }

            List<Type> ints = rt.interfaces();
            for (Type t : ints) {
                if (t instanceof ContainerType) {
                    ContainerType rt2 = (ContainerType) t;
                    l.addAll(implemented(mi, rt2, thisVar));
                }
            }
        }

        return l;
    }
    
    XVar[] genSymbolicVars(int n) {
    	XVar[] result = new XVar[n];
    	//String prefix = "arg";
    	for (int i =0; i < n; ++i) {
    		result[i] = ConstraintManager.getConstraintSystem().makeUQV();
    	}
    	return result;
    }

    // FIXME: unify with TypeEnv_c.checkOverride()
    private void superCheckOverride(MethodInstance mi, MethodInstance mj, boolean allowCovariantReturn) throws SemanticException {
        if (mi == mj)
            return;

        if (!mi.name().equals(mj.name())) {
            throw new SemanticException(mi.signature() + " in " + mi.container() 
                                        +" cannot override " 
                                        +mj.signature() + " in " + mj.container() 
                                        + "; method names are not equal",mi.errorPosition());
        }
        if (mi.formalNames().size() != mj.formalNames().size()) {
            throw new SemanticException(mi.signature() + " in " + mi.container() 
                                        + " cannot override " + mj.signature() + " in " + mj.container() 
                                        +"; different number of arguments",mi.errorPosition());
        }
        if (mi.typeParameters().size() != mj.typeParameters().size()) {
            throw new SemanticException(mi.signature() + " in " + mi.container() 
                                        +" cannot override " + mj.signature() + " in " + mj.container()
                                        + "; different number of type parameters",mi.errorPosition());
        }

        XVar[] newSymbols = genSymbolicVars(mj.formalNames().size()+1);
        TypeSystem xts = (TypeSystem) mi.typeSystem();
        XVar[] miFormals = Matcher.getSymbolicNames(mi.formalNames(),xts);
        XVar mipt = Types.getPlaceTerm(mi);
        XVar[] mjFormals = Matcher.getSymbolicNames(mj.formalNames(),xts);
        XVar mjpt = Types.getPlaceTerm(mj);
        XVar[] miSymbols = new XVar[miFormals.length+1];
        miSymbols[0] = mipt;
        System.arraycopy(miFormals, 0, miSymbols, 1, miFormals.length);
        XVar[] mjSymbols = new XVar[mjFormals.length+1];
        mjSymbols[0] = mjpt;
        System.arraycopy(mjFormals, 0, mjSymbols, 1, mjFormals.length);
        
        TypeParamSubst tps = new TypeParamSubst(xts, mi.typeParameters(), mj.x10Def().typeParameters());
        assert (mi.typeParameters().size() == mj.typeParameters().size() &&
                mi.formalTypes().size() == mj.formalTypes().size());
        boolean allEqual = true;
//        List<SubtypeConstraint> env = new ArrayList<SubtypeConstraint>();
//        for (int j = 0; j < mi.typeParameters().size(); j++) {
//            Type p1 = mi.typeParameters().get(j);
//            Type p2 = mj.typeParameters().get(j);
//            env.add(new SubtypeConstraint(p1, p2, true));
//        }
        List<Type> miTypes = Subst.subst(mi.formalTypes(), newSymbols, miSymbols);
        List<Type> mjTypes = Subst.subst(mj.formalTypes(), newSymbols, mjSymbols);
        if (!CollectionUtil.allElementwise(miTypes, tps.reinstantiate(mjTypes), new TypeEquals(context))) {
            allEqual = false;
        }

        if (!allEqual) {
            throw new SemanticException(mi.signature() + " in " + mi.container() + " cannot override " 
                                        +mj.signature() + " in " + mj.container() 
                                        +"; incompatible parameter types",mi.errorPosition());
        }

        Type miRet = Subst.subst(mi.returnType(), newSymbols, miSymbols);
        Type mjRet = Subst.subst(mj.returnType(), newSymbols, mjSymbols);
        if (! isSubtype(miRet, tps.reinstantiate(mjRet))) {
            if (reporter.should_report(Reporter.types, 3))
                reporter.report(3, "return type " + mi.returnType() + " != " + mj.returnType());
            throw new Errors.IncompatibleReturnType(mi, mj);
        } 

        // FIXME: is this the same as entails(CConstraint, CConstraint)?
        boolean entails = true;

        final CConstraint mig = Subst.subst(mi.guard(), newSymbols, miSymbols);
        final CConstraint mjg = Subst.subst(mj.guard(), newSymbols, mjSymbols);
        if (mjg == null) {
            entails &= mig == null || mig.valid();
        }
        else {
            entails &= mig == null 
            || mjg.entails(mig, new ConstraintMaker() {
                public CConstraint make() throws XFailure {
                    return context.constraintProjection(mjg, mig);
                }
            });          
        }

        TypeConstraint mitg = Subst.subst(mi.typeGuard(), newSymbols, miSymbols);
        TypeConstraint mjtg = Subst.subst(mj.typeGuard(), newSymbols, mjSymbols);
        if (mjtg == null) {
            entails &= mitg == null;
        } else {
        	mjtg = (TypeConstraint)tps.reinstantiate(mjtg);
            entails &= mitg == null || mjtg.entails(mitg, context);          
        }

        if (! entails) {
            throw new SemanticException(mi.name() + " in " + mi.container()
             +" cannot override method in " + mj.container() 
             +"; overriding method guard is not entailed."
             + "\n\t Overiding Method in " + mi.container() + ":" + mi.signature()
             + "\n\t Method in " + mj.container()+":" + mj.signature()
             ,
             mi.errorPosition());
        }

        if (mi.flags().moreRestrictiveThan(mj.flags())) {
            if (reporter.should_report(Reporter.types, 3))
                reporter.report(3, mi.flags() + " more restrictive than " +
                              mj.flags());
            throw new SemanticException(mi.signature() + " in " 
            		+ mi.container() +" cannot override " +mj.signature() 
            		+ " in " + mj.container() +"; attempting to assign weaker " +"access privileges",mi.errorPosition());
        }

        if (mi.flags().isStatic() != mj.flags().isStatic()) {
            if (reporter.should_report(Reporter.types, 3))
                reporter.report(3, mi.signature() + " is " + 
                              (mi.flags().isStatic() ? "" : "not") + 
                              " static but " + mj.signature() + " is " +
                              (mj.flags().isStatic() ? "" : "not") + " static");
            throw new SemanticException(mi.signature() + " in " + mi.container()
            		+" cannot override " +mj.signature() + " in " + mj.container()
            		+"; overridden method is " +(mj.flags().isStatic() ? "" : "not") +"static",mi.errorPosition());
        }

        if (! mi.def().equals(mj.def()) && mj.flags().isFinal()) {
            // mi can "override" a final method mj if mi and mj are the same method instance.
            if (reporter.should_report(Reporter.types, 3))
                reporter.report(3, mj.flags() + " final");
            throw new SemanticException(mi.signature() + " in " + mi.container() 
            		+" cannot override " +mj.signature() + " in " + mj.container()
            		+"; overridden method is final",mi.errorPosition());
        }
    }

    @Override
    public void checkOverride(MethodInstance r, MethodInstance other) throws SemanticException {
        checkOverride(r, other, true);
    }

    @Override
    public void checkOverride(MethodInstance r, MethodInstance other, boolean allowCovariantReturn) throws SemanticException {

        MethodInstance mi = (MethodInstance) r;
        MethodInstance mj = (MethodInstance) other;
        XVar thisVar = mi.x10Def().thisVar(); 

        List<XVar> ys = new ArrayList<XVar>(2);
        List<XVar> xs = new ArrayList<XVar>(2);

        MethodInstance_c.buildSubst(mi, ys, xs, thisVar);
        MethodInstance_c.buildSubst(mj, ys, xs, thisVar);
        final XVar[] y = ys.toArray(new XVar[ys.size()]);
        final XVar[] x = xs.toArray(new XVar[ys.size()]);

        // Force evaluation to help debugging.

        mi = fixThis(mi, y, x);
        mi.returnType();
        mi = expandPropertyInMethod(mi);

        mj = fixThis(mj, y, x);
        mj.returnType();
        mj = expandPropertyInMethod(mj);


        superCheckOverride(mi, mj, allowCovariantReturn);

        if (!ts.throwsSubset(mi, mj)) {
            if (reporter.should_report(Reporter.types, 3))
                reporter.report(3, mi.throwTypes() + " not subset of " + mj.throwTypes());
            throw new SemanticException(mi.signature() + " in " + mi.container() + " cannot override " + mj.signature() + " in " + mj.container()
                    + "; the throw set " + mi.throwTypes() + " is not a subset of the " + "overridden method's throw set " + mj.throwTypes() + ".",
                    mi.position());
        }

        Flags miF = mi.flags();
        Flags mjF = mj.flags();

        // Report.report(1, "X10MethodInstance_c: " + this + " canOverrideImpl " + mj);
        if (! miF.hasAllAnnotationsOf(mjF)) {
            if (reporter.should_report(Reporter.types, 3))
                reporter.report(3, mi.flags() + " is more liberal than " + mj.flags());
            throw new SemanticException(mi.flags() + " " + mi.signature() 
                                        + " in " + mi.container() 
                                        +" cannot override " +mj.flags() 
                                        + " " + mj.signature() 
                                        + " in " + mj.container() 
                                        +"; attempting to assign weaker behavioral annotations",
                                        mi.errorPosition());
        }
    }

    /**
     * Returns true iff <m1> is the same method as <m2>
     */
    @Override
    public boolean isSameMethod(MethodInstance mi, MethodInstance mj) {
        if (mi.name().equals(mj.name())) {
           // String fullNameWithThis = mi.x10Def().thisVar().toString();
          //  XName thisName = new XNameWrapper<Object>(new Object(), fullNameWithThis);
            XVar thisVar = mi.x10Def().thisVar(); // ConstraintManager.getConstraintSystem().makeThis(fullNameWithThis); // ConstraintManager.getConstraintSystem().makeLocal(thisName);

            List<XVar> ys = new ArrayList<XVar>(2);
            List<XVar> xs = new ArrayList<XVar>(2);

            MethodInstance_c.buildSubst(mi, ys, xs, thisVar);
            MethodInstance_c.buildSubst(mj, ys, xs, thisVar);
            final XVar[] y = ys.toArray(new XVar[ys.size()]);
            final XVar[] x = xs.toArray(new XVar[ys.size()]);

            mi = fixThis(mi, y, x);
            mj = fixThis(mj, y, x);

            return mi.hasFormals(mj.formalTypes(), context);
        }
        return false;
    }

    public boolean callValid(ProcedureInstance<? extends ProcedureDef> prototype, Type thisType, List<Type> argTypes) {
    	// this should have been instantiated correctly; if so, the call is valid
    	return true;
    }
    
    /**
     * Populates the list acceptable with those MethodInstances which are
     * Applicable and Accessible as defined by JLS 15.11.2.1
     * 
     * @param container
     *            TODO
     * @param matcher
     *            TODO
     */
    public List<ConstructorInstance> findAcceptableConstructors(Type container, ConstructorMatcher matcher) throws SemanticException {
	SemanticException error = null;

	List<ConstructorInstance> acceptable = new ArrayList<ConstructorInstance>();

	if (reporter.should_report(reporter.types, 2))
	    reporter.report(2, "Searching type " + container + " for constructor " + matcher.signature());

	if (!(container instanceof ClassType)) {
	    return Collections.<ConstructorInstance>emptyList();
	}

	X10ParsedClassType containerClass = (X10ParsedClassType) container.toClass();
	List<Type> tas = containerClass.typeArguments();
	TypeConstraint tb = Types.get(containerClass.x10Def().typeBounds());
	if (tas != null && tb != null) {
	    TypeConstraint ntb = containerClass.subst().reinstantiate(tb);
	    if (!ntb.consistent(context))
	        error = new Errors.TypeGuardNotEntailed(tb, container);
	}

	List<ConstructorInstance> list = containerClass.constructors();
	if (error != null) list = Collections.<ConstructorInstance>emptyList();
    //When we write:  new A[T](...)
    // then matcher.typeArgs is [T]
    // but the ctor instance is not like a generic method (it already has the right substitution)
    List<Type> typeArgs = Collections.<Type>emptyList(); //matcher.typeArgs;
        boolean isDumb = matcher.isDumbMatcher;
    boolean shouldTryCoercions = !isDumb && typeArgs.size() == 0 && container != null && (container instanceof X10ParsedClassType) && ((X10ParsedClassType) container).def().typeParameters().size() > 0;
    List<ConstructorInstance> resolved =
        TypeSystem_c.resolveProcedure(container, context, list, typeArgs, matcher.argTypes, isDumb);
	for (ConstructorInstance ci : resolved) {
	    if (reporter.should_report(Reporter.types, 3))
		reporter.report(3, "Trying " + ci);

	    try {
	    	ConstructorInstance oldCI = ci;
	    	ci = matcher.instantiate(ci);

	    	if (ci == null) {
	    		continue;
	    	}
	    	ci.setOrigMI(oldCI);
	    	if (isAccessible(ci)) {
	    		if (reporter.should_report(Reporter.types, 3))
	    			reporter.report(3, "->acceptable: " + ci);
	    		acceptable.add(ci);
	    	}
	    	else {
	    		if (error == null) {
	    			error = new NoMemberException(NoMemberException.CONSTRUCTOR,
	    			                              "Constructor " + ci.signature() + "\n is inaccessible.");
	    		}
	    	}

	    	continue;
	    }
	    catch (SemanticException e) {
	    	// Treat any instantiation errors as call invalid errors.
	        if (!shouldTryCoercions) error = e;
	    }

	    if (error == null) {
		error = new NoMemberException(NoMemberException.CONSTRUCTOR, "Constructor cannot be invoked with given arguments."
		        + "\n Signature:" + ci.signature()
				+ "\n Arguments:" + matcher.argumentString() + ".");

	    }
	}

	if (acceptable.size() == 0) {
	    if (error == null) {
	    	error = new NoMemberException(NoMemberException.CONSTRUCTOR, "No valid constructor found for " + matcher.signature() + ".");
	    	Map<String, Object> map = CollectionFactory.newHashMap();
            map.put(CodedErrorInfo.ERROR_CODE_KEY, CodedErrorInfo.ERROR_CODE_CONSTRUCTOR_NOT_FOUND);
            map.put("CONSTRUCTOR", matcher.name().toString());
            map.put("ARGUMENTS", matcher.argumentString());
            error.setAttributes(map);
	    }

	    throw error;
	}

	return acceptable;
    }

	public  MethodInstance fixThis(final MethodInstance mi, final XVar[] y, final XVar[] x) {
	    MethodInstance mj = mi;
	    final TypeSystem ts = (TypeSystem) mi.typeSystem();
	
	    final MethodInstance zmj = mj;
	    final LazyRef<Type> tref = new LazyRef_c<Type>(null);
	    tref.setResolver(new Runnable() { 
	        public void run() {
	            try {
	                Type newRetType = Subst.subst(zmj.returnType(), y, x, new Type[] { }, new ParameterType[] { });
	                newRetType = PlaceChecker.ReplaceHereByPlaceTerm(newRetType, (Context) context);
	                final boolean isStatic =  zmj.flags().isStatic();
	                // add in this.home=here clause.
	                if (! isStatic  && ! Types.isX10Struct(mi.container())) {
	                    if (y.length > 0 && y[0] instanceof XEQV)
	                        newRetType = Subst.addIn(newRetType, PlaceChecker.ThisHomeEqualsHere(y[0], ts));
	                }
	                if (y.length > 0 && y[0] instanceof XEQV) // this is a synthetic variable
	                    newRetType = Subst.project(newRetType, (XVar) y[0]);  			
	                tref.update(newRetType);
	            }
	            catch (SemanticException e) {
	                tref.update(zmj.returnType());
	            }
	        }
	    });
	    mj =  (MethodInstance) mj.returnTypeRef(tref);
	
	    List<Type> newFormals = new ArrayList<Type>();
	    for (Type t : mj.formalTypes()) {
	        try {
	            Type newT;
	            newT = Subst.subst(t, y, x, new Type[] { }, new ParameterType[] { });
	            newFormals.add(newT);
	        }
	        catch (SemanticException e) {
	            newFormals.add(t);
	        }
	    }
	    mj = (MethodInstance) mj.formalTypes(newFormals);
        
        List<Type> newThrows = new ArrayList<Type>();
        for (Type t : mj.throwTypes()) {
            // DC i copied this code from the newFormals case above.  Hope it works.
            try {
                Type newT;
                newT = Subst.subst(t, y, x, new Type[] { }, new ParameterType[] { });
                newThrows.add(newT);
            }
            catch (SemanticException e) {
                newThrows.add(t);
            }
        }
        mj = (MethodInstance) mj.throwTypes(newThrows);
	
	    List<LocalInstance> newFormalNames = new ArrayList<LocalInstance>();
	    for (LocalInstance li : mj.formalNames()) {
	        try {
	            LocalInstance newLI;
	            newLI = Subst.subst((X10LocalInstance) li, y, x);
	            newFormalNames.add(newLI);
	        }
	        catch (SemanticException e) {
	            newFormalNames.add(li);
	        }
	    }
	    
	    mj = (MethodInstance) mj.formalNames(newFormalNames);
	
	    if (mj.guard() != null) {
	        try {
	            CConstraint newGuard = mj.guard().substitute(y, x);
	            mj = (MethodInstance) mj.guard(newGuard);
	        }
	        catch (XFailure e) {
	        }
	    }
	    if (mj.typeGuard() != null) {
	        TypeConstraint newGuard = mj.typeGuard();
	        for (int i = 0; i < x.length; i++)
	            newGuard = newGuard.subst(y[i], x[i]);
	        mj = (MethodInstance) mj.typeGuard(newGuard);
	    }
	
	    return mj;
	}

}
