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

package polyglot.ast;

import java.util.*;

import polyglot.frontend.Source;
import polyglot.types.*;
import polyglot.util.*;
import polyglot.visit.*;
import x10.errors.Errors;
import x10.util.CollectionFactory;

/**
 * A <code>SourceFile</code> is an immutable representations of a Java
 * language source file.  It consists of a package name, a list of 
 * <code>Import</code>s, and a list of <code>GlobalDecl</code>s.
 */
public abstract class SourceFile_c extends Node_c implements SourceFile
{
    protected PackageNode package_;
    protected List<Import> imports;
    protected List<TopLevelDecl> decls;
    protected ImportTable importTable;
    protected Source source;

    public SourceFile_c(Position pos, PackageNode package_, List<Import> imports, List<TopLevelDecl> decls) {
	super(pos);
	assert(imports != null && decls != null); // package_ may be null, imports empty
	this.package_ = package_;
	this.imports = TypedList.copyAndCheck(imports, Import.class, true);
	this.decls = TypedList.copyAndCheck(decls, TopLevelDecl.class, true);
    }

    /** Get the source of the source file. */
    public Source source() {
	return this.source;
    }

    /** Set the source of the source file. */
    public SourceFile source(Source source) {
	SourceFile_c n = (SourceFile_c) copy();
	n.source = source;
	return n;
    }

    /** Get the package of the source file. */
    public PackageNode package_() {
	return this.package_;
    }

    /** Set the package of the source file. */
    public SourceFile package_(PackageNode package_) {
	SourceFile_c n = (SourceFile_c) copy();
	n.package_ = package_;
	return n;
    }

    /** Get the imports of the source file. */
    public List<Import> imports() {
	return Collections.unmodifiableList(this.imports);
    }

    /** Set the imports of the source file. */
    public SourceFile imports(List<Import> imports) {
	SourceFile_c n = (SourceFile_c) copy();
	n.imports = TypedList.copyAndCheck(imports, Import.class, true);
	return n;
    }

    /** Get the declarations of the source file. */
    public List<TopLevelDecl> decls() {
	return Collections.unmodifiableList(this.decls);
    }

    /** Set the declarations of the source file. */
    public SourceFile decls(List<TopLevelDecl> decls) {
	SourceFile_c n = (SourceFile_c) copy();
	n.decls = TypedList.copyAndCheck(decls, TopLevelDecl.class, true);
	return n;
    }

    /** Get the declarations of the source file. */
    public ImportTable importTable() {
	return this.importTable;
    }

    /** Set the declarations of the source file. */
    public SourceFile importTable(ImportTable importTable) {
	SourceFile_c n = (SourceFile_c) copy();
	n.importTable = importTable;
	return n;
    }

    /** Reconstruct the source file. */
    protected SourceFile_c reconstruct(PackageNode package_, List<Import> imports, List<TopLevelDecl> decls) {
	if (package_ != this.package_ || ! CollectionUtil.allEqual(imports, this.imports) || ! CollectionUtil.allEqual(decls, this.decls)) {
	    SourceFile_c n = (SourceFile_c) copy();
	    n.package_ = package_;
	    n.imports = TypedList.copyAndCheck(imports, Import.class, true);
	    n.decls = TypedList.copyAndCheck(decls, TopLevelDecl.class, true);
	    return n;
	}

	return this;
    }

    /** Visit the children of the source file. */
    public Node visitChildren(NodeVisitor v) {
        PackageNode package_ = (PackageNode) visitChild(this.package_, v);
	List<Import> imports = visitList(this.imports, v);
	List<TopLevelDecl> decls = visitList(this.decls, v);
	return reconstruct(package_, imports, decls);
    }

    /**
     * Build type objects for the source file.  Set the visitor's import table
     * field before we recurse into the declarations.
     */
    public NodeVisitor buildTypesEnter(TypeBuilder tb) {
        if (package_ != null) {
            return tb.pushPackage(Types.get(package_.package_()));
        }
        return tb;
    }

    /**
     * Build type objects for the source file.  Set the visitor's import table
     * field before we recurse into the declarations.
    public NodeVisitor buildTypesEnter(TypeBuilder tb) throws SemanticException {
        TypeSystem ts = tb.typeSystem();

        ImportTable it;

        if (package_ != null) {
            it = ts.importTable(source.name(), package_.package_());
	}
	else {
            it = ts.importTable(source.name(), null);
	}

        tb.setImportTable(it);

        return tb;
    }
     */

    /**
     * Build type objects for the source file.  Sets the import table field for
     * the source.
    public Node buildTypes(TypeBuilder tb) throws SemanticException {
        ImportTable it = tb.importTable();

        // Clear the import table in case we use the same visitor
        // to visit a different source file.
        tb.setImportTable(null);

        return importTable(it);
    }
     */

    public Context enterScope(Context c) {
        return c.pushSource(importTable);
    }

    /** Type check the source file. */
    public abstract Node typeCheck(ContextVisitor tc);

    public String toString() {
        return "<<<< " + source + " >>>>";
    }

    /** Write the source file to an output file. */
    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
        w.write("<<<< " + source + " >>>>");
        w.newline(0);

	if (package_ != null) {
	    w.write("package ");
	    print(package_, w, tr);
	    w.write(";");
	    w.newline(0);
	    w.newline(0);
	}

	for (Iterator<Import> i = imports.iterator(); i.hasNext(); ) {
	    Import im = (Import) i.next();
	    print(im, w, tr);
	}
	 
	if (! imports.isEmpty()) {
	    w.newline(0);
	}

	for (Iterator<TopLevelDecl> i = decls.iterator(); i.hasNext(); ) {
	    TopLevelDecl d = (TopLevelDecl) i.next();
	    print(d, w, tr);
	}
    }

    public void dump(CodeWriter w) {
        super.dump(w);
        w.begin(0);
        w.allowBreak(4, " ");
        w.write("(import-table " + importTable + ")");
        w.end();
    }
    

}
