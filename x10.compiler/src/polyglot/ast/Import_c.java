/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2007 Polyglot project group, Cornell University
 * Copyright (c) 2006-2007 IBM Corporation
 * 
 */

package polyglot.ast;

import polyglot.frontend.Globals;
import polyglot.types.*;
import polyglot.util.*;
import polyglot.visit.ContextVisitor;
import polyglot.visit.PrettyPrinter;

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

    public Import_c(Position pos, Kind kind, QName name) {
	super(pos);
	assert(kind != null && name != null);
	this.name = name;
	this.kind = kind;
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
    public Node typeCheck(ContextVisitor tc) throws SemanticException {
        TypeSystem ts = tc.typeSystem();

        // Make sure the imported name exists.
        if (kind == PACKAGE && ts.systemResolver().packageExists(name))
            return this;
        
        Named n;
        try {
            n = ts.systemResolver().find(name);
        }
        catch (SemanticException e) {
            throw new SemanticException("Package or class " + name + " not found.");
        }

        if (n instanceof Type) {
            Type t = (Type) n;
            if (t.isClass()) {
        	ClassType ct = t.toClass();
        	if (! ts.classAccessibleFromPackage(ct.def(), tc.context().package_())) {
        	    throw new SemanticException("Class " + ct + " is not accessible.");
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
	if (! Globals.Options().fully_qualified_names) {
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
