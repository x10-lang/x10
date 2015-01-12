/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 * This file was originally derived from the Polyglot extensible compiler framework.
 *
 *  (C) Copyright 2000-2007 Polyglot project group, Cornell University
 *  (C) Copyright IBM Corporation 2007-2015.
 */

package polyglot.types;

import java.util.*;

import polyglot.main.Reporter;
import polyglot.util.*;
import x10.types.ConstrainedType;
import x10.types.constraints.CConstraint;
import x10.util.CollectionFactory;

/**
 * A <code>ClassContextResolver</code> looks up type names qualified with a class name.
 * For example, if the class is "A.B", the class context will return the class
 * for member class "A.B.C" (if it exists) when asked for "C".
 */
public class ClassContextResolver extends AbstractAccessControlResolver {
    protected Type type;
    protected Reporter reporter;
    
    /**
     * Construct a resolver.
     * @param ts The type system.
     * @param type The type in whose context we search for member types.
     */
    public ClassContextResolver(TypeSystem ts, Type type) {
        super(ts);
        this.type = type;
        this.reporter = ts.extensionInfo().getOptions().reporter;
    }
    
    public String toString() {
        return "(class-context " + type + ")";
    }
    
    /**
     * Find a type object in the context of the class.
     * @param name The name to search for.
     */
    public List<Type> find(Matcher<Type> matcher, Context context) throws SemanticException {
        Name name = matcher.name();
	
        if (reporter.should_report(TOPICS, 2))
	    reporter.report(2, "Looking for " + name + " in " + this);

        if (! (type instanceof ClassType)) {
            throw new NoClassException(name.toString(), type);
        }
        
        ClassType type = (ClassType) this.type;

        Type m = null;

        QName fullName = null;
        QName rawName = null;
        
        if (type.isGloballyAccessible()) {
            fullName = QName.make(type.fullName(), name);
            QName q = ts.getTransformedClassName(type.def());
            rawName = QName.make(q.qualifier(), Name.make(q.name() + "$" + name));
        }
        
        if (fullName != null) {
            List<Type> sr = null;

            // First check the system resolver.
            sr = ts.systemResolver().check(fullName);

            // Try the raw class file name.
            if (sr == null) {
                sr = ts.systemResolver().check(rawName);
            }

            if (sr == null) {
                // Go to disk, but only if there is no job for the type.
                // If there is a job, all members should be in the resolver
                // already.
                boolean useLoadedResolver = true;

                if (type instanceof ParsedTypeObject) {
                    ParsedTypeObject pto = (ParsedTypeObject) type;
                    if (pto.job() != null) {
                        useLoadedResolver = false;
                    }
                }

                if (useLoadedResolver) {
                    try {
                        sr = ts.systemResolver().find(rawName);
                    }
                    catch (SemanticException e) {
                        // Not found; will fall through to error handling code
                    }
                }
            }

            // If we found something, verify that it matches.
            if (sr != null) {
                for (Type q : sr) {
                    try {
                        m = matcher.instantiate(q);
                        break;
                    }
                    catch (SemanticException e) {
                        // Doesn't match; try again.
                    }
                }
            }
        }
        
        // Check if the member was explicitly declared.
        if (m == null) {
            m = type.memberTypeMatching(matcher);
        }
        
        // If we found something, make sure it's accessible.
        Type baseM = m;
        if (Types.isConstrainedType(m)) {
            baseM = Types.baseType(m);
        }
        if (Types.isClass(baseM)) {
            ClassType mt = baseM.toClass();
            if (! mt.isMember()) {
        	throw new SemanticException("Class " + mt +
        	                            " is not a member class, " +
        	                            " but was found in " + type + ".");
            }

            if (mt.outer().def() != type.def()) {
        	throw new SemanticException("Class " + mt +
        	                            " is not a member class " +
        	                            " of " + type + ".");
            }
            return CollectionUtil.<Type>list(Types.container(m, type));
        }

        if (m instanceof MemberInstance<?>) {
            MemberInstance<?> mi = (MemberInstance<?>) m;

            if (! mi.container().equals((Object) type)) {
        	throw new SemanticException("Type " + mi +
        	                            " is not a member " +
        	                            " of " + type + ".");
            }
        }

        if (m != null) {
            if (context!=null && ! canAccess(m, context.currentClassDef(), context)) { // "x10.lang._.Console" result in a null context. See AbstractAccessControlResolver.find(matcher, null);
        	throw new SemanticException("Cannot access member type \"" + m + "\".");
            }
            return CollectionUtil.<Type>list(m);
        }
        
        // If we struck out, try the super types.
        
        // Collect all members of the super types.
        // Use a Set to eliminate duplicates.
        Set<Type> acceptable = CollectionFactory.<Type>newHashSet();
        
        if (type.superClass() != null) {
            Type sup = type.superClass();
            if (sup instanceof ClassType && matcher.visit(sup)) {
                Resolver r = ts.classContextResolver((ClassType) sup, context);
                try {
                    List<Type> n = r.find(matcher);
                    acceptable.addAll(n);
                }
                catch (SemanticException e) {
                }
            }
        }
        
        for (Iterator<Type> i = type.interfaces().iterator(); i.hasNext(); ) {
            Type sup = (Type) i.next();
            if (sup instanceof ClassType && matcher.visit(sup)) {
                Resolver r = ts.classContextResolver((ClassType) sup, context);
                try {
                    List<Type> n = r.find(matcher);
                    acceptable.addAll(n);
                }
                catch (SemanticException e) {
                }
            }
        }
        
        if (acceptable.size() == 0) {
            throw new NoClassException(name.toString(), type);
        }
        else if (acceptable.size() > 1) {
            Set<Type> containers = CollectionFactory.newHashSet(acceptable.size());
            for (Type n : acceptable) {
                if (n instanceof MemberInstance<?>) {
                    MemberInstance<?> mi = (MemberInstance<?>) n;
                    containers.add(mi.container());
                }
            }
            
            if (containers.size() == 2) {
                Iterator<Type> i = containers.iterator();
                Type t1 = (Type) i.next();
                Type t2 = (Type) i.next();
                throw new SemanticException("Member \"" + name +
                                            "\" of " + type + " is ambiguous; it is defined in both " +
                                            t1 + " and " + t2 + ".");
            }
            else if (containers.size() == 0) {
                throw new SemanticException("Member \"" + name +
                                            "\" of " + type + " is ambiguous.");
            }
            else {
                throw new SemanticException("Member \"" + name +
                                            "\" of " + type + " is ambiguous; it is defined in " +
                                            CollectionUtil.listToString(new ArrayList<Type>(containers)) + ".");
            }
        }
        
        assert acceptable.size() == 1;
        
        List<Type> t = new ArrayList<Type>(acceptable);
        
        if (reporter.should_report(TOPICS, 2))
            reporter.report(2, "Found member type " + t);
        
        return t;
    }

    protected boolean canAccess(Type n, ClassDef accessor, Context context) {
        if (n instanceof ConstrainedType) 
            n = Types.baseType(n);
        if (n instanceof MemberInstance<?>) {
            return accessor == null || ts.isAccessible((MemberInstance<?>) n, context);
        }
        return true;
    }
    
    /**
     * The class in whose context we look.
     */
    public Type classType() {
	return type;
    }

    private static final Collection<String> TOPICS = 
            CollectionUtil.list(Reporter.types, Reporter.resolver);

}
