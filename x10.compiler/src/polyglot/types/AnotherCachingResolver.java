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

import java.util.*;

import polyglot.main.Reporter;
import polyglot.types.CachingResolver.ErrorType;
import polyglot.util.*;
import x10.util.CollectionFactory;

/**
 * A <code>CachingResolver</code> memoizes another Resolver
 */
public class AnotherCachingResolver implements Resolver {
    protected Resolver inner;
    private Map<Object, List<Type>> cache;
    private boolean cacheNotFound;
    protected Reporter reporter;

    /**
     * Create a caching resolver.
     * @param inner The resolver whose results this resolver caches.
     */
    public AnotherCachingResolver(Resolver inner, boolean cacheNotFound, Reporter reporter) {
        this.inner = inner;
        this.cacheNotFound = cacheNotFound;
        this.cache = new LinkedHashMap<Object, List<Type>>();
        this.reporter = reporter;
    }

    public AnotherCachingResolver(Resolver inner, Reporter reporter) {
        this(inner, true, reporter);
    }

    protected boolean shouldReport(int level) {
        return reporter.should_report(TOPICS, level);
    }

    /*
    public Object copy() {
	try {
	   AnotherCachingResolver r = (AnotherCachingResolver) super.clone();
	   // todo: the inner resolver is not deep cloned. so I removed this copy method. If it is needed, then Resolver should extend Copy and we should implement copy for all Resolvers. 
	    r.cache = CollectionFactory.newHashMap(this.cache);
	    return r;
	}
	catch (CloneNotSupportedException e) {
	    throw new InternalCompilerError("clone failed");
	}
    }  */

    /**
     * The resolver whose results this resolver caches.
     */
    public Resolver inner() {
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

    /**
     * Find a type by name.
     * @param name The name to search for.
     */
    public List<Type> find(Matcher<Type> matcher) throws SemanticException {
        if (shouldReport(2))
            reporter.report(2, "CachingResolver: find: " + matcher.signature());

        List<Type> q = null;
        if (matcher.key() != null)
            q = cache.get(matcher.key());

        if (q == null) {
            if (shouldReport(3))
                reporter.report(3, "CachingResolver: not cached: " + matcher.signature());

            try {
                q = inner.find(matcher);
            }
            catch (NoClassException e) {
                if (shouldReport(3)) {
                    reporter.report(3, "CachingResolver: " + e.getMessage());
                    reporter.report(3, "CachingResolver: installing " + matcher.signature() + "-> (not found) in resolver cache");
                }
                if (cacheNotFound) {
                    if (matcher.key() != null)
                        cache.put(matcher.key(), CollectionUtil.<Type>list(new ErrorType(e)));
                }
                throw e;
            }

            if (matcher.key() != null)
                cache.put(matcher.key(), q);

            if (shouldReport(3))
                reporter.report(3, "CachingResolver: loaded: " + matcher.signature());
        }
        else {
            if (q.size() == 1 && q.get(0) instanceof ErrorType) {
                throw ((ErrorType) q.get(0)).error;
            }

            if (shouldReport(3))
                reporter.report(3, "CachingResolver: cached: " + matcher.signature());
        }

        return q;
    }

    /**
     * Check if a type object is in the cache, returning null if not.
     * @param name The name to search for.
     */
    public List<Type> check(Matcher<Type> matcher) {
        if (matcher.key() == null)
            return null;
        List<Type> q = cache.get(matcher.key());
        if (q != null && q.size() == 1 && q.get(0) instanceof ErrorType)
            return null;
        return (List<Type>) q;
    }

    public void dump() {
        reporter.report(1, "Dumping " + this);
        for (Map.Entry<Object, List<Type>> e : cache.entrySet()) {
            reporter.report(2, e.toString());
        }
    }

    private static final Collection<String> TOPICS = CollectionUtil.list(Reporter.types, Reporter.resolver);
}
