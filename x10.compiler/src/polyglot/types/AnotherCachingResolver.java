/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
 */

package polyglot.types;

import java.util.*;

import polyglot.main.Reporter;
import polyglot.util.*;
import x10.util.CollectionFactory;

/**
 * A <code>CachingResolver</code> memoizes another Resolver
 */
public class AnotherCachingResolver implements Resolver {

    protected Resolver inner;
    private Map<Object,Object> cache;
    private boolean cacheNotFound;
    protected Reporter reporter;

    /**
     * Create a caching resolver.
     * @param inner The resolver whose results this resolver caches.
     */
 
    public AnotherCachingResolver(Resolver inner, Reporter reporter) {
        this.inner = inner;
        this.cacheNotFound = true;
        this.cache = new LinkedHashMap<Object, Object>();
        this.reporter = reporter;
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

     protected Collection<Named> cachedObjects() {
	 ArrayList<Named> l = new ArrayList<Named>();
	 for (Object o : cache.values()) {
	     if (o instanceof Named)
		 l.add((Named) o);
	 }
	 return l;
     }

     /**
      * Find a type object by name.
      * @param name The name to search for.
      */
     public Named find(Matcher<Named> matcher) throws SemanticException {
	 if (shouldReport(2))
	     reporter.report(2, "CachingResolver: find: " + matcher.signature());

	 Object o = null;
	 if (matcher.key() != null)
	     o = cache.get(matcher.key());

	 if (o instanceof SemanticException) throw ((SemanticException) o);

	 Named q = (Named) o;

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
			 cache.put(matcher.key(), e);
		 }
		 throw e;
	     }

	     if (matcher.key() != null)
		 cache.put(matcher.key(), q);

	     if (shouldReport(3))
		 reporter.report(3, "CachingResolver: loaded: " + matcher.signature());
	 }
	 else {
	     if (shouldReport(3))
		 reporter.report(3, "CachingResolver: cached: " + matcher.signature());
	 }

	 return q;
     }

     /**
      * Check if a type object is in the cache, returning null if not.
      * @param name The name to search for.
      */
     public Named check(Matcher<Named> matcher) {
	 if (matcher.key() == null)
	     return null;
	 Object o = cache.get(matcher.key());
	 if (o instanceof Throwable)
	     return null;
	 return (Named) o;
     }

     public void dump() {
	 reporter.report(1, "Dumping " + this);
	 for (Map.Entry<Object, Object> e : cache.entrySet()) {
	     reporter.report(2, e.toString());
	 }
     }

     private static final Collection<String> TOPICS =
	 CollectionUtil.list(Reporter.types,
	                     Reporter.resolver);
}
