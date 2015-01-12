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

package polyglot.ast;

import java.util.Collections;
import java.util.List;

import polyglot.frontend.Globals;
import polyglot.types.*;
import polyglot.util.*;
import polyglot.visit.ContextVisitor;
import polyglot.visit.PrettyPrinter;
import x10.errors.Errors;

/**
 * An <code>Import</code> is an immutable representation of a Java
 * <code>import</code> statement.  It consists of the string representing the
 * item being imported and the kind which is either indicating that a class
 * is being imported, or that an entire package is being imported.
 */
public class Import_c extends Node_c implements Import
{
    protected Kind kind;
    protected QName name;
    protected final NodeFactory nf;

    public Import_c(Position pos, Kind kind, QName name, NodeFactory nf) {
	super(pos);
	assert(kind != null && name != null);
	this.name = name;
	this.kind = kind;
	this.nf = nf;
    }

    /** Get the name of the import. */
    public QName name() {
	return this.name;
    }

    /** Set the name of the import. */
    public Import name(QName name) {
	Import_c n = (Import_c) copy();
	n.name = name;
	return n;
    }

    /** Get the kind of the import. */
    public Kind kind() {
	return this.kind;
    }

    /** Set the kind of the import. */
    public Import kind(Kind kind) {
	Import_c n = (Import_c) copy();
	n.kind = kind;
	return n;
    }

    /**
     * Build type objects for the import.
    public Node buildTypes(TypeBuilder tb) throws SemanticException {
	ImportTable it = tb.importTable();

	if (kind == CLASS) {
	    it.addClassImport(name);
	}
	else if (kind == PACKAGE) {
	    it.addPackageImport(name);
	}

	return this;
    }
     */

    /** Check that imported classes and packages exist. */
    public Node typeCheck(ContextVisitor tc) {
        TypeSystem ts = tc.typeSystem();

        // Make sure the imported name exists.
        if (kind == PACKAGE && ts.systemResolver().packageExists(name))
            return this;

        List<Type> tl;
        try {
            tl = ts.systemResolver().find(name);
        }
        catch (SemanticException e) {
            Errors.issue(tc.job(), new Errors.PackageOrClassNameNotFound(name, position));
            tl = Collections.<Type>emptyList();
        }

        for (Type t : tl) {
            if (t.isClass()) {
                ClassType ct = t.toClass();
                if (! ts.classAccessibleFromPackage(ct.def(), tc.context().package_())) {
                    Errors.issue(tc.job(), new Errors.ClassNotAccessible(ct, position));
                }
            }
        }

        return this;
    }

    public String toString() {
	return "import " + name + (kind == PACKAGE ? ".*" : "");
    }

    /** Write the import to an output file. */
    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
	if (! nf.extensionInfo().getOptions().fully_qualified_names) {
	    w.write("import ");
	    w.write(name.toString());

	    if (kind == PACKAGE) {
	        w.write(".*");
	    }

	    w.write(";");
	    w.newline(0);
	}
    }
    

}
