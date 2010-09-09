/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.ast;

import java.util.Iterator;
import java.util.List;

import polyglot.ast.Import;
import polyglot.ast.Node;
import polyglot.ast.PackageNode;
import polyglot.ast.SourceFile_c;
import polyglot.ast.TopLevelDecl;
import polyglot.types.SemanticException;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import polyglot.visit.TypeBuilder;

public class X10SourceFile_c extends SourceFile_c {

	public X10SourceFile_c(Position pos, PackageNode package_, List<Import> imports, List<TopLevelDecl> decls) {
		super(pos, package_, imports, decls);
	}

	/** Type check the source file. */
	public Node typeCheck(ContextVisitor tc) throws SemanticException {
		boolean hasPublic = false;

		// Override method to not check for duplicate declarations. This will be
		// caught during type building. But, we need to allow duplicates to handle
		// overloaded typedefs.
		for (Iterator i = decls.iterator(); i.hasNext();) {
			TopLevelDecl d = (TopLevelDecl) i.next();

			if (d.flags().flags().isPublic()) {
				if (hasPublic) {
					throw new SemanticException(
							"The source contains more than one public declaration.",
							d.position());
				}

				hasPublic = true;
			}
		}

		return this;
	}
}
