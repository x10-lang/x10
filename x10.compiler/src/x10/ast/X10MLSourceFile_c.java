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
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;

/** Subclass of SourceFile_c that adds the X10MLSourceFile marker interface.
 * This represents source files read from XML files rather than .x10 files.
 */
public class X10MLSourceFile_c extends SourceFile_c implements X10MLSourceFile {

	public X10MLSourceFile_c(Position pos, PackageNode package_, List<Import> imports,
			List<TopLevelDecl> decls) {
		super(pos, package_, imports, decls);
	}

    @Override
    public Node typeCheck(ContextVisitor tc) {
        return this;
    }

}
