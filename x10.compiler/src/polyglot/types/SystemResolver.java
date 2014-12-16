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

import polyglot.frontend.ExtensionInfo;
import polyglot.main.Reporter;
import polyglot.util.*;
import x10.util.CollectionFactory;

/**
 * The <code>SystemResolver</code> is the main resolver for
 * fully-qualified names.
 */
public class SystemResolver extends CachingResolver implements TopLevelResolver {
    protected Map<QName,Boolean> packageCache;
    protected ExtensionInfo extInfo;
    
    /**
     * Create a caching resolver.
     * @param inner The resolver whose results this resolver caches.
     */
    public SystemResolver(TopLevelResolver inner, ExtensionInfo extInfo) {
        super(inner, extInfo.getOptions().reporter);
        this.extInfo = extInfo;
        this.packageCache = CollectionFactory.newHashMap();
    }

    /*
    public Object copy() {
        SystemResolver r = (SystemResolver) super.copy();
        // todo: the inner resolver is not deep cloned. so I removed this copy method. If it is needed, then Resolver should extend Copy and we should implement copy for all Resolvers.
        r.packageCache = CollectionFactory.newHashMap(this.packageCache);
        return r;
    }
    */
    
    public void installInAll(QName name, Type n) {
        this.install(name, n);
    }

    public boolean installedInAll(QName name, Type q) {
        if (check(name) != q) {
            return false;
        }
        return true;
    }

    /** Check if a package exists in the resolver cache. */
    protected boolean packageExistsInCache(QName name) {
        for (Type n : cachedTypes()) {
            if (n instanceof Importable) {
                Importable im = (Importable) n;
                if (im.package_() != null &&
                    im.package_().fullName() != null &&
                    (im.package_().fullName().equals(name) ||
                     im.package_().fullName().startsWith(name))) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Check if a package exists.
     */
    @Override
    public boolean packageExists(QName name) {
	Boolean b = packageCache.get(name);
	if (b != null) {
	    return b.booleanValue();
	}
	else {
	    QName prefix = name.qualifier();
	    
            if (prefix != null && packageCache.get(prefix) == Boolean.FALSE) {
                packageCache.put(name, Boolean.FALSE);
                return false;
            }

            boolean exists;
            exists = packageExistsInCache(name);
            if (! exists) {
                exists = ((TopLevelResolver) inner).packageExists(name);
            }

            if (exists) {
                packageCache.put(name, Boolean.TRUE);

                while (prefix != null) {
                    packageCache.put(prefix, Boolean.TRUE);
                    prefix = prefix.qualifier();
                }
            }
            else {
                packageCache.put(name, Boolean.FALSE);
            }

            return exists;
	}
    }

    protected void cachePackage(Package p) {
        if (p != null) {
            packageCache.put(QName.make(p.fullName()), Boolean.TRUE);
            Package prefix = Types.get(p.prefix());
            cachePackage(prefix);
        }
    }

    /**
     * Check if a type is in the cache, returning null if not.
     * @param name The name to search for.
     */
    public Type checkType(QName name) {
        return (Type) check(name);
    }

    /**
     * Return the first element in the given list, or null if the list is null or empty.
     * @param l the given list
     * @return the first element (if any)
     */
    public static <T> T first(List<T> l) {
        if (l != null) {
            for (T n : l) {
                return n;
            }
        }
        return null;
    }

    /**
     * Find a type by name. For most code, this should be called
     * with the Java source name (p.A.B), not the class file name (p.A$B). The
     * exceptions are for resolving names in deserialized types and in types
     * loaded from raw class files.
     */
    @Override
    public List<Type> find(QName name) throws SemanticException {
        List<Type> n = super.find(name);

        if (reporter.should_report(TOPICS, 2))
            reporter.report(2, "Returning from SR.find(" + name + "): " + n);

        return n;
    }

    /**
     * Find a package by name. For most code, this should be called
     * with the Java source name (p.A.B), not the class file name (p.A$B). The
     * exceptions are for resolving names in deserialized types and in types
     * loaded from raw class files.
     */
    @Override
    public Package findPackage(QName name) throws SemanticException {
        Package p = super.findPackage(name);

        if (reporter.should_report(TOPICS, 2))
            reporter.report(2, "Returning from SR.findPackage(" + name + "): " + p);

        return p;
    }

    @Override
    public void install(QName name, Type q) {
        if (reporter.should_report(TOPICS, 2) && check(name) == null)
            reporter.report(2, "SR installing " + name + "->" + q);
        
        super.install(name, q);
    }

    /**
     * Install a qualifier in the cache.
     * @param name The name of the qualifier to insert.
     * @param q The qualifier to insert.
     */
    @Override
    public void addNamed(QName name, Type q) throws SemanticException {
        super.addNamed(name, q);

        if (q instanceof ClassType) {
            ClassType ct = (ClassType) q;
            QName containerName = name.qualifier();
            if (containerName != null) {
            if (ct.isTopLevel()) {
                Package p = ((ClassType) q).package_();
                cachePackage(p);
                if (p != null && containerName.equals(p.fullName())) {
                    addPackage(containerName, p);
                }
            }
            else if (ct.isMember()) {
                if (name.equals(ct.fullName())) {
                    // Check that the names match; we could be installing
                    // a member class under its class file name, not its Java
                    // source full name.
                    addNamed(containerName, ct.outer());
                }
            }
            }
        }
        else if (q instanceof Package) {
            Package p = (Package) q;
            cachePackage(p);
            QName containerName = name.qualifier();
            Package prefix = Types.get(p.prefix());
            if (prefix != null && containerName.equals(prefix.fullName())) {
                addPackage(containerName, prefix);
            }
        }

        if (false && q instanceof Type && packageExists(QName.make(null,name.name()))) {
            throw new SemanticException("Type \"" + name.name() +"\" clashes with package of the same name.", q.position()); // todo: see XTENLANG-2289
        }
    }

    private static final Collection<String> TOPICS =
                    CollectionUtil.list(Reporter.types,
                                        Reporter.resolver,
                                        Reporter.sysresolver);
}
