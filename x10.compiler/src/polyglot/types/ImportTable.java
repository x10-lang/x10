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
import polyglot.util.*;
import x10.util.CollectionFactory;


/**
 * An <code>ImportTable</code> is a type of <code>ClassResolver</code> that
 * corresponds to a particular source file.
 * <p>
 * It has a set of package and class imports, which caches the results of
 * lookups for future reference.
 */
public class ImportTable implements Resolver
{
    protected TypeSystem ts;
    
    protected Reporter reporter;
    
    /** Map from names to classes found, or to the NOT_FOUND object. */
    protected Map<Object,Option<List<Type>>> map;
    
    /** A list of all on-demand imports. */
    protected List<QName> onDemandImports;
    /** Parallel list of the positions of the on-demand imports. */
    protected List<Position> onDemandImportPositions;
    
    /** List of explicit imports. */
    protected List<QName> explicitImports;
    /** Parallel list of positions for lazyImports. */
    protected List<Position> explicitImportPositions;
    
    /** List of explicitly imported names added to the table or pending in
     * the lazyImports list. */
    protected String sourceName;
    /** Position to use for error reporting */
    protected Position sourcePos;
    
    /** Our package */
    protected Ref<? extends Package> pkg;

    private static final Option<List<Type>> NOT_FOUND = Option.<List<Type>>None();
    
    /**
     * Create an import table.
     * @param ts The type system
     * @param pkg The package of the source we are importing types into.
     */
    public ImportTable(TypeSystem ts, Ref<? extends Package> pkg) {
        this(ts, pkg, null);
    }

    /**
     * Create an import table.
     * @param ts The type system
     * @param pkg The package of the source we are importing types into.
     * @param src The name of the source file we are importing into.
     */
    public ImportTable(TypeSystem ts, Ref<? extends Package> pkg, String src) {
        this.ts = ts;
        this.sourceName = src;
        this.sourcePos = src != null ? new Position(null, src) : null;
        this.pkg = pkg;

	this.map = CollectionFactory.newHashMap();
	this.onDemandImports = new ArrayList<QName>();
	this.onDemandImportPositions = new ArrayList<Position>();
	this.explicitImports = new ArrayList<QName>();
	this.explicitImportPositions = new ArrayList<Position>();
    this.reporter = ts.extensionInfo().getOptions().reporter;
    }

    /**
     * The package of the source we are importing types into.
     */
    public Ref<? extends Package> package_() {
        return pkg;
    }

    /**
     * Add a class import.
     */
    public void addExplicitImport(QName name) {
        addExplicitImport(name, null);
    }

    /**
     * Add a class import.
     */
    public void addExplicitImport(QName name, Position pos) {
        if (reporter.should_report(TOPICS, 2))
            reporter.report(2, this + ": lazy import " + name);

	explicitImports.add(name);
	explicitImportPositions.add(pos);
    }

    /**
     * Add a package import.
     */
    public void addOnDemandImport(QName containerName, Position pos) {
	// don't add the import if it is the same as the current package,
	// the same as a default import, or has already been imported
	if ((pkg != null && pkg.get().fullName().equals(containerName)) ||
		ts.defaultOnDemandImports().contains(containerName) ||
		onDemandImports.contains(containerName)) {
	    return;
	}
	
	onDemandImports.add(containerName);
        onDemandImportPositions.add(pos);
    }

    /**
     * Add a package import.
     */
    public void addOnDemandImport(QName containerName) {
	addOnDemandImport(containerName, null);
    }

    /**
     * List the names we import from.
     */
    public List<QName> onDemandImports() {
        return onDemandImports;
    }

    /**
     * List the classes explicitly imported.
     */
    public List<QName> explicitImports() {
        return explicitImports;
    }

    /**
     * The name of the source file we are importing into.
     */
    public String sourceName() {
        return sourceName;
    }

    /**
     * Find a type by name, using the cache and the outer resolver,
     * but not the import table.
     */
    protected List<Type> cachedFind(Name name) throws SemanticException {
        Option<List<Type>> res = map.get(name);

        if (res != null && res != NOT_FOUND) {
            return res.get();
        }

        List<Type> t = ts.systemResolver().find(QName.make(null, name)); // NOTE: short name
        map.put(name, Option.<List<Type>>Some(t));
        return t;
    }

    /**
     * Find a type by name, searching the import table.
     */
    public List<Type> find(Matcher<Type> matcher) throws SemanticException {
        if (reporter.should_report(TOPICS, 2))
            reporter.report(2, this + ".find(" + matcher.signature() + ")");

        // The class name is short.
        // First see if we have a mapping already.
        Option<List<Type>> res = matcher.key() != null ? map.get(matcher.key()) : null;

        if (res != null) {
            if (res == NOT_FOUND) {
                throw new NoClassException(matcher.name().toString(), sourcePos);
            }
            return res.get();
        }

        SemanticException ex = null;
        List<Type> resolved = null;

        try {
            resolved = lookupExplicit(matcher);
        }
        catch (NoClassException e) {
            ex = e;
        }

        if (resolved == null) {
            Package p = Types.get(pkg);

            // Check if the current package defines it.
            // If so, this takes priority over the package imports (or 
            // "type-import-on-demand" declarations as they are called in
            // the JLS), so even if another package defines the same name,
            // there is no conflict. See Section 6.5.2 of JLS, 2nd Ed.

            QName containerName = p != null ? p.fullName() : null;
            Position pos = null;

            resolved = findInContainer(matcher, containerName, pos);
        }

        // It wasn't an explicit import.  Maybe it was on-demand?
        if (resolved == null) {
            try {
                resolved = lookupOnDemand(matcher);
            }
            catch (NoClassException e) {
                ex = e;
            }
        }

        if (matcher.key() != null) {
            if (resolved != null) {
                map.put(matcher.key(), Option.<List<Type>>Some(resolved));
            }
            else {
                map.put(matcher.key(), NOT_FOUND);
            }
        }

        if (resolved != null)
            return resolved;

        if (ex != null)
            throw ex;

        throw new NoClassException(matcher.name().toString(), sourcePos);
    }

    protected List<Type> lookupOnDemand(Matcher<Type> matcher) throws SemanticException, NoClassException {
        List<QName> imports = new ArrayList<QName>(onDemandImports.size() + 5);
        List<Position> positions = new ArrayList<Position>(onDemandImports.size() + 5);

        // Next search the default imports (e.g., java.lang)
        imports.addAll(ts.defaultOnDemandImports());
        positions.addAll(Arrays.asList(new Position[imports.size()]));

        // Then search the explicit p.* imports.
        imports.addAll(onDemandImports);
        positions.addAll(onDemandImportPositions);

        assert imports.size() == positions.size();

        List<Type> resolved = null;

        Set<QName> tried = CollectionFactory.newHashSet();

        for (int i = 0; i < imports.size(); i++) {
            QName containerName = imports.get(i);
            Position pos = positions.get(i);

            if (tried.contains(containerName))
                continue;
            tried.add(containerName);

            List<Type> n = findInContainer(matcher, containerName, pos);

            if (n != null) {
                if (resolved == null) {
                    // This is the first occurrence of name we've found
                    // in a package import.
                    // Record it, and keep going, to see if there
                    // are any conflicts.
                    resolved = n;
                }
                else {
                    // This is the 2nd occurrence of name we've found
                    // in an imported package.
                    // That's bad.
                    throw new SemanticException("Reference to " + 
                            matcher.signature() + " is ambiguous; both " + 
                            resolved + " and " + n + " match.");
                }
            }
        }

//        // Search the empty package only if not already found.
//        if (resolved == null) {
//            QName containerName = null;
//            Position pos = null;
//
//            if (! tried.contains(containerName)) {
//
//                Named n = findInContainer(matcher, containerName, pos);
//
//                if (n != null) {
//                    resolved = n;
//                }
//            }
//        }

        return resolved;
    }
    
    protected List<Type> findInContainer(Matcher<Type> matcher, QName containerName, Position containerPos) throws SemanticException  {
        List<Type> resolved = null;

        if (containerName != null) {
            // Find the container, then search the container.
            Resolver r = null;

            // We don't allow packages and classes to have the same name.  So, only look
            // for a class if a package is not found.
            try {
                Package pkgContainer = ts.systemResolver().findPackage(containerName);
                r = ts.packageContextResolver(pkgContainer);
            }
            catch (SemanticException e) {
                try {
                    List<Type> containers = ts.systemResolver().find(containerName);
                    if (containers != null) {
                        for (Type container : containers) {
                            if (container instanceof ClassType) {
                                r = ts.classContextResolver(container);
                                break;
                            }
                        }
                    }
                }
                catch (SemanticException z) {
                }
            }

            if (r == null)
                return null;

            try {
                resolved = r.find(matcher);
            }
            catch (SemanticException e) {
            }
            
        }

        if (resolved == null) {
            Name name = matcher.name();

            try {
                resolved = ts.systemResolver().find(QName.make(containerName, name)); 

                // Now verify that what we found actually matches.
                if (resolved != null) {
                    List<Type> newResolved = new ArrayList<Type>();
                    for (Type t : resolved) {
                        t = matcher.instantiate(t);
                        if (t != null)
                            newResolved.add(t);
                    }
                    if (newResolved.isEmpty()) {
                        resolved = null;
                    } else {
                        resolved = newResolved;
                    }
                }
            }
            catch (SemanticException e) {
                return null;
            }
        }

        // Found something.  Now make sure we can actually see it.
        if (resolved != null) {
            List<Type> newResolved = new ArrayList<Type>();
            for (Type t : resolved) {
                if (isVisibleFromThisPackage(t, containerName))
                    newResolved.add(t);
            }
            if (!newResolved.isEmpty())
                return newResolved;
        }

        return null;
    }

    /**
     * Return whether <code>n</code> in package <code>pkgName</code> is visible from within
     * package <code>pkg</code>.  The empty string may
     * be passed in to represent the default package.
     */
    protected boolean isVisibleFromThisPackage(Type t, QName containerName) {
        boolean isVisible = false;
        
        Package p = Types.get(this.pkg);
        boolean inSamePackage =
               p != null && p.fullName().equals(containerName)
            || p == null && containerName == null;
        
        if (inSamePackage) {
            isVisible = true;
        }
        else {
        	if (t.isClass()) {
        	    ClassType ct = t.toClass();
        	    isVisible = ts.classAccessibleFromPackage(ct.def(), Types.get(pkg));
        	}
        	else {
        	    // Assume non-class types are always visible.
        	    isVisible = true;
        	}
        }

        return isVisible;
    }

    protected List<Type> lookupExplicit(Matcher<Type> matcher) throws SemanticException {
        Set<QName> tried = CollectionFactory.newHashSet();

        for (int i = 0; i < explicitImports.size(); i++) {
            QName longName = explicitImports.get(i);
            Position pos = explicitImportPositions.get(i);

            if (tried.contains(longName))
                continue;
            tried.add(longName);

            if (reporter.should_report(TOPICS, 2))
                reporter.report(2, this + ": import " + longName);

            if (longName.name().equals(matcher.name()))
                return findInContainer(matcher, longName.qualifier(), pos);
        }

        return null;
    }

    public String toString() {
        if (sourceName != null) {
            return "(import " + sourceName + ")";
        }
        else {
            return "(import)";
        }
    }

    private static final Collection<String> TOPICS = 
        CollectionUtil.list(Reporter.types, Reporter.resolver, Reporter.imports);

}
