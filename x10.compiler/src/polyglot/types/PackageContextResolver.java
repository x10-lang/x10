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
 *  (C) Copyright IBM Corporation 2007-2014.
 */

package polyglot.types;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import polyglot.util.InternalCompilerError;
import polyglot.util.StringUtil;

/**
 * A <code>PackageContextResolver</code> is responsible for looking up types
 * and packages in a package by name.
 */
public class PackageContextResolver extends AbstractAccessControlResolver
{
    protected Package p;

    /**
     * Create a package context resolver.
     * @param ts The type system.
     * @param p The package in whose context to search.
     */
    public PackageContextResolver(TypeSystem ts, Package p) {
        super(ts);
        this.p = p;
    }

    /**
     * The package in whose context to search.
     */
    public Package package_() {
        return p;
    }

    /**
     * Find a type object by name.
     * @param name Name of the class or package to find.
     * 
     */
    public List<Type> find(Matcher<Type> matcher, Context context) throws SemanticException {
        Name name = matcher.name();

        List<Type> tl = null;

        try {
            tl = ts.systemResolver().find(QName.make(p.fullName(), name));
        }
        catch (NoClassException e) {
            // Rethrow if some _other_ class or package was not found.
            if (!e.getClassName().equals(p.fullName() + "." + name)) {
                throw e;
            }
        }

        if (tl != null) {
            List<Type> newTL = new ArrayList<Type>();
            for (Type n : tl) {
                if (! canAccess(n, context)) {
                    throw new SemanticException("Cannot access " + n + " from " + context.currentClassDef() + ".");
                }
                
                try {
                	// [DC] what is this for?  some sort of validity check?
                    n = matcher.instantiate(n);
                } catch (SemanticException e) {
                    n = null;
                }
                
                if (n != null)
                    newTL.add(n);
            }
            if (newTL.isEmpty()) {
                tl = null;
            } else {
                tl = newTL;
            }
        }
   
        return tl;
    }

    protected boolean canAccess(Type n, Context context) {
        if (n instanceof ClassType) {
            return context == null || ts.classAccessible(((ClassType) n).def(), context);
        }
        return true;
    }

    public String toString() {
        return "(package-context " + p.toString() + ")";
    }
}
