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
import x10.util.CollectionFactory;

/**
 * A <code>CachingResolver</code> memoizes another Resolver
 */
public class CachingResolver implements TopLevelResolver {
    protected TopLevelResolver inner;
    private Map<QName, List<Type>> cache;
    private Map<QName, Package> cachedPackages;
    private boolean cacheNotFound;
    protected Reporter reporter;

    /**
     * Create a caching resolver.
     * @param inner The resolver whose results this resolver caches.
     */
    public CachingResolver(TopLevelResolver inner, boolean cacheNotFound, Reporter reporter) {
        this.inner = inner;
        this.cacheNotFound = cacheNotFound;
        this.cache = new LinkedHashMap<QName, List<Type>>();
        this.cachedPackages = new LinkedHashMap<QName, Package>();
        this.reporter = reporter;
    }

    public CachingResolver(TopLevelResolver inner, Reporter reporter) {
        this(inner, true, reporter);
    }

    protected boolean shouldReport(int level) {
        return (reporter.should_report(Reporter.sysresolver, level) && this instanceof SystemResolver) ||
                reporter.should_report(TOPICS, level);
    }

    public boolean packageExists(QName name) {
        return inner.packageExists(name);
    }

    /*
    public Object copy() {
        try {
            CachingResolver r = (CachingResolver) super.clone();
	        // todo: the inner resolver is not deep cloned. so I removed this copy method. If it is needed, then Resolver should extend Copy and we should implement copy for all Resolvers.
            r.cache = CollectionFactory.newHashMap(this.cache);
            return r;
        }
        catch (CloneNotSupportedException e) {
            throw new InternalCompilerError("clone failed");
        }
    }
    */
    
    /**
     * The resolver whose results this resolver caches.
     */
    public TopLevelResolver inner() {
        return this.inner;
    }

    public String toString() {
        return "(cache " + inner.toString() + ")";
    }

    // FIXME: instead of copying, create an AggregateCollection that will use the actual values
    protected Collection<Type> cachedTypes() {
        ArrayList<Type> r = new ArrayList<Type>();
        for (List<Type> tl : cache.values()) {
            r.addAll(tl);
        }
        return r;
    }

    protected static class ErrorType extends Type_c {
        private static final long serialVersionUID = -6530832412536840944L;
        public final SemanticException error;
        public ErrorType(SemanticException e) { this.error = e; }
        public String translate(Resolver c) { return error.toString(); }
        public String typeToString() { return error.toString(); }
    }

    protected static class ErrorPackage extends Package_c {
        private static final long serialVersionUID = 6490343022821637680L;
        public final SemanticException error;
        public ErrorPackage(SemanticException e) { this.error = e; }
        public String translate(Resolver c) { return error.toString(); }
        public String typeToString() { return error.toString(); }
    }
    
    /**
     * Find a type by name.
     * @param name The name to search for.
     * @return a list of types with that name
     */
    public List<Type> find(QName name) throws SemanticException {
        if (shouldReport(2))
            reporter.report(2, "CachingResolver: find: " + name);

        List<Type> q = cache.get(name);

        if (q == null) {
            if (shouldReport(3))
                reporter.report(3, "CachingResolver: not cached: " + name);

            try {
                q = inner.find(name);
            }
            catch (NoClassException e) {
                if (shouldReport(3)) {
                    reporter.report(3, "CachingResolver: " + e.getMessage());
                    reporter.report(3, "CachingResolver: installing " + name + "-> (not found) in resolver cache");
                }
                if (cacheNotFound) {
                    cache.put(name, CollectionUtil.<Type>list(new ErrorType(e)));
                }
                throw e;
            }

            for (Type t : q) {
                addNamed(name, t);
                addNamed(QName.make(t.fullName()), t);
            }
            cache.put(name, q);

            if (shouldReport(3))
                reporter.report(3, "CachingResolver: loaded: " + name);
        }
        else {
            if (q.size() == 1 && q.get(0) instanceof ErrorType) {
                throw ((ErrorType) q.get(0)).error;
            }

            if (shouldReport(3))
                reporter.report(3, "CachingResolver: cached: " + name);
        }

        return q;
    }

    /**
     * Find a single type by name.
     * @param name The name to search for.
     * @return a type with that name
     */
    public Type findOne(QName name) throws SemanticException {
        List<Type> res = find(name);
        if (res.isEmpty()) {
            throw new SemanticException("Type with name "+name+" not found");
        }
        if (res.size() != 1) {
            throw new SemanticException("Multiple types exist with name "+name);
        }
        Type t = res.get(0);
        if (t instanceof ErrorType) {
            throw ((ErrorType) t).error;
        }
        return t;
    }

    /**
     * Check if a type object is in the cache, returning null if not.
     * @param name The name to search for.
     */
    public List<Type> check(QName name) {
        List<Type> q = cache.get(name);
        if (q != null && q.size() == 1 && q.get(0) instanceof ErrorType)
            return null;
        return (List<Type>) q;
    }

    /**
     * Find a package by name. For most code, this should be called
     * with the Java source name (p.A.B), not the class file name (p.A$B). The
     * exceptions are for resolving names in deserialized types and in types
     * loaded from raw class files.
     */
    public Package findPackage(QName name) throws SemanticException {
        Package p = cachedPackages.get(name);
        if (p == null) {
            if (inner == null) {
                throw new SemanticException("Package with name "+name+" not found");
            }
            return inner.findPackage(name);
        }
        return p;
    }

    /**
     * Install a qualifier in the cache.
     * @param name The name of the qualifier to insert.
     * @param q The qualifier to insert.
     */
    public void install(QName name, Type q) {
        if (shouldReport(3))
            reporter.report(3, "CachingResolver: installing type " + name + "->" + q + " in resolver cache");
        if (shouldReport(5))
            new Exception().printStackTrace();

        List<Type> old = cache.get(name);
        List<Type> res;
        if (old == null) {
            res = CollectionUtil.list(q);
        } else {
            res = new ArrayList<Type>(old);
            if (!contains(res, q)) {
                res.add(q);
            }
        }

        cache.put(name, res);
    }

    private boolean contains(List<Type> l, Type t) {
        if (!(t instanceof ErrorType)) {
            for (Type u : l) {
                if (!(u instanceof ErrorType) && u.equals((Object)t))
                    return true;
            }
        } else {
            ErrorType et = (ErrorType) t;
            for (Type u : l) {
                if ((u instanceof ErrorType) && ((ErrorType) u).error.equals(et.error)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Install a type in the cache.
     * @param name The name of the type to insert.
     * @param q The type to insert.
     * @throws SemanticException
     */
    public void addNamed(QName name, Type q) throws SemanticException {
        install(name, q);
    }

    /**
     * Install a package in the cache.
     * @param name The name of the package to insert.
     * @param q The package to insert.
     */
    public void install(QName name, Package q) {
        if (shouldReport(3))
            reporter.report(3, "CachingResolver: installing package " + name + "->" + q + " in resolver cache");
        if (shouldReport(5))
            new Exception().printStackTrace();

        Package old = cachedPackages.get(name);

        cachedPackages.put(name, q);
    }

    /**
     * Install a package in the cache.
     * @param name The name of the package to insert.
     * @param q The package to insert.
     */
    public void addPackage(QName name, Package q) {
        install(name, q);
    }
    
    public void dump() {
        reporter.report(1, "Dumping " + this);
        for (Map.Entry<QName, List<Type>> e : cache.entrySet()) {
            reporter.report(2, e.toString());
        }
    }

    private static final Collection<String> TOPICS = CollectionUtil.list(Reporter.types, Reporter.resolver);
}
