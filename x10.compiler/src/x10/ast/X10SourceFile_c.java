/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

package x10.ast;

import java.util.List;

import polyglot.ast.Import;
import polyglot.ast.Node;
import polyglot.ast.PackageNode;
import polyglot.ast.SourceFile_c;
import polyglot.ast.TopLevelDecl;
import polyglot.types.SemanticException;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import polyglot.visit.TypeBuilder;
import polyglot.visit.TypeChecker;
import x10.errors.Errors;

/**
 * An <code>X10SourceFile</code> is an immutable representations of an X10
 * language source file.  It consists of a package name, a list of 
 * <code>Import</code>s, and a list of <code>GlobalDecl</code>s.
 */
public class X10SourceFile_c extends SourceFile_c {

	public X10SourceFile_c(Position pos, PackageNode package_, List<Import> imports, List<TopLevelDecl> decls) {
		super(pos, package_, imports, decls);
		hasBeenTypeChecked = false;
	}

	private boolean hasBeenTypeChecked = false;
	public boolean hasBeenTypeChecked() { return hasBeenTypeChecked; }
	public X10SourceFile_c hasBeenTypeChecked(boolean flag) {
	    if (flag == hasBeenTypeChecked) return this;
	    X10SourceFile_c n = (X10SourceFile_c) copy();
	    n.hasBeenTypeChecked = flag;
	    return n;
	}

    /* (non-Javadoc)
     * @see polyglot.ast.Node_c#typeCheckOverride(polyglot.ast.Node, polyglot.visit.ContextVisitor)
     */
    @Override
    public Node typeCheckOverride(Node parent, ContextVisitor tc) {
        if (hasBeenTypeChecked)
            return this;
        return super.typeCheckOverride(parent, tc);
    }

    /** Type check the source file. */
	public Node typeCheck(ContextVisitor tc) {
		boolean hasPublic = false;

		// Override method to not check for duplicate declarations. This will be
		// caught during type building. But, we need to allow duplicates to handle
		// overloaded typedefs.
		for (TopLevelDecl d : decls) {
			if (d instanceof X10ClassDecl && d.flags().flags().isPublic()) {
				if (hasPublic) {
					Errors.issue(tc.job(),
					        new Errors.SourceContainsMoreThanOnePublicDeclaration(d.position()),
					        this);
				}
				hasPublic = true;
			}
		}

		return this.hasBeenTypeChecked(true);
	}
}
