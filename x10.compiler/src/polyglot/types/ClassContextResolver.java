/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
 */

package polyglot.types;

import java.util.*;

import polyglot.main.Report;
import polyglot.util.*;

/**
 * A <code>ClassContextResolver</code> looks up type names qualified with a class name.
 * For example, if the class is "A.B", the class context will return the class
 * for member class "A.B.C" (if it exists) when asked for "C".
 */
public class ClassContextResolver extends AbstractAccessControlResolver {
    protected Type type;
    
    /**
     * Construct a resolver.
     * @param ts The type system.
     * @param type The type in whose context we search for member types.
     */
    public ClassContextResolver(TypeSystem ts, Type type) {
        super(ts);
        this.type = type;
    }
    
    public String toString() {
        return "(class-context " + type + ")";
    }
    
    /**
     * Find a type object in the context of the class.
     * @param name The name to search for.
     */
    public Named find(Matcher<Named> matcher, Context context) throws SemanticException {
	Name name = matcher.name();
	
        if (Report.should_report(TOPICS, 2))
	    Report.report(2, "Looking for " + name + " in " + this);
        
        if (! (type instanceof ClassType)) {
            throw new NoClassException(name.toString(), type);
        }
        
        ClassType type = (ClassType) this.type;

        Named m = null;

        QName fullName = null;
        QName rawName = null;
        
        if (type.isGloballyAccessible()) {
            fullName = QName.make(type.fullName(), name);
            QName q = ts.getTransformedClassName(type.def());
            rawName = QName.make(q.qualifier(), Name.make(q.name() + "$" + name));
        }
        
        if (fullName != null) {
            // First check the system resolver.
            m = ts.systemResolver().check(fullName);

            // Try the raw class file name.
            if (m == null) {
                m = ts.systemResolver().check(rawName);
            }
            
            if (m == null) {
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
        		m = ts.systemResolver().find(rawName);
        	    }
        	    catch (SemanticException e) {
        		// Not found; will fall through to error handling code
        	    }
        	}
            }
            
            // If we found something, verify that it matches.
            if (m != null) {
        	try {
        	    m = matcher.instantiate(m);
        	}
        	catch (SemanticException e) {
        	    // Doesn't match; try again.
        	    m = null;
        	}
            }
        }
        
        // Check if the member was explicitly declared.
        if (m == null) {
            m = type.memberTypeMatching(matcher);
        }
        
        // If we found something, make sure it's accessible.
        if (m instanceof ClassType) {
            ClassType mt = (ClassType) m;

            if (! mt.isMember()) {
        	throw new SemanticException("Class " + mt +
        	                            " is not a member class, " +
        	                            " but was found in " + type + ".");
            }

            if (! mt.outer().equals((Object) type)) {
        	throw new SemanticException("Class " + mt +
        	                            " is not a member class " +
        	                            " of " + type + ".");
            }

            return mt;
        }

        if (m instanceof MemberInstance) {
            MemberInstance<?> mi = (MemberInstance<?>) m;

            if (! mi.container().equals((Object) type)) {
        	throw new SemanticException("Type " + mi +
        	                            " is not a member " +
        	                            " of " + type + ".");
            }
        }

        if (m != null) {
            if (! canAccess(m, context.currentClassDef(), context)) {
        	throw new SemanticException("Cannot access member type \"" + m + "\".");
            }
            return m;
        }
        
        // If we struck out, try the super types.
        
        // Collect all members of the super types.
        // Use a Set to eliminate duplicates.
        Set<Named> acceptable = new HashSet<Named>();
        
        if (type.superClass() != null) {
            Type sup = type.superClass();
            if (sup instanceof ClassType) {
                Resolver r = ts.classContextResolver((ClassType) sup, context);
                try {
                    Named n = r.find(matcher);
                    acceptable.add(n);
                }
                catch (SemanticException e) {
                }
            }
        }
        
        for (Iterator<Type> i = type.interfaces().iterator(); i.hasNext(); ) {
            Type sup = (Type) i.next();
            if (sup instanceof ClassType) {
                Resolver r = ts.classContextResolver((ClassType) sup, context);
                try {
                    Named n = r.find(matcher);
                    acceptable.add(n);
                }
                catch (SemanticException e) {
                }
            }
        }
        
        if (acceptable.size() == 0) {
            throw new NoClassException(name.toString(), type);
        }
        else if (acceptable.size() > 1) {
            Set<Type> containers = new HashSet<Type>(acceptable.size());
            for (Named n : acceptable) {
                if (n instanceof MemberInstance) {
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
        
        Named t = acceptable.iterator().next();
        
        if (Report.should_report(TOPICS, 2))
            Report.report(2, "Found member type " + t);
        
        return t;
    }

    protected boolean canAccess(Named n, ClassDef accessor, Context context) {
        if (n instanceof MemberInstance) {
            return accessor == null || ts.isAccessible((MemberInstance) n, context);
        }
        return true;
    }
    
    /**
     * The class in whose context we look.
     */
    public Type classType() {
	return type;
    }

    private static final Collection TOPICS = 
            CollectionUtil.list(Report.types, Report.resolver);

}
