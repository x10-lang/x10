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
 * A <code>CachingResolver</code> memoizes another Resolver
 */
public class AnotherCachingResolver implements Resolver, Copy {

    protected Resolver inner;
    private Map<Object,Object> cache;
    private boolean cacheNotFound;

    /**
     * Create a caching resolver.
     * @param inner The resolver whose results this resolver caches.
     */
    public AnotherCachingResolver(Resolver inner, boolean cacheNotFound) {
	this.inner = inner;
	this.cacheNotFound = cacheNotFound;
	this.cache = new LinkedHashMap<Object, Object>();
    }

    public AnotherCachingResolver(Resolver inner) {
	this(inner, true);
    }

    protected boolean shouldReport(int level) {
	return Report.should_report(TOPICS, level);
    }

    public Object copy() {
	try {
	   AnotherCachingResolver r = (AnotherCachingResolver) super.clone();
	    r.cache = new HashMap<Object, Object>(this.cache);
	    return r;
	}
	catch (CloneNotSupportedException e) {
	    throw new InternalCompilerError("clone failed");
	}
    }

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
	     Report.report(2, "CachingResolver: find: " + matcher.signature());

	 Object o = null;
	 if (matcher.key() != null)
	     o = cache.get(matcher.key());

	 if (o instanceof SemanticException) throw ((SemanticException) o);

	 Named q = (Named) o;

	 if (q == null) {
	     if (shouldReport(3))
		 Report.report(3, "CachingResolver: not cached: " + matcher.signature());

	     try {
		 q = inner.find(matcher);
	     }
	     catch (NoClassException e) {
		 if (shouldReport(3)) {
		     Report.report(3, "CachingResolver: " + e.getMessage());
		     Report.report(3, "CachingResolver: installing " + matcher.signature() + "-> (not found) in resolver cache");
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
		 Report.report(3, "CachingResolver: loaded: " + matcher.signature());
	 }
	 else {
	     if (shouldReport(3))
		 Report.report(3, "CachingResolver: cached: " + matcher.signature());
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
	 Report.report(1, "Dumping " + this);
	 for (Iterator i = cache.entrySet().iterator(); i.hasNext(); ) {
	     Map.Entry e = (Map.Entry) i.next();
	     Report.report(2, e.toString());
	 }
     }

     private static final Collection TOPICS =
	 CollectionUtil.list(Report.types,
	                     Report.resolver);
}
