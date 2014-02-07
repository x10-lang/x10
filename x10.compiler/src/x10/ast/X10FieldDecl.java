/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10.ast;

import polyglot.ast.Expr;
import polyglot.ast.FieldDecl;
import polyglot.ast.FlagsNode;
import polyglot.ast.Id;
import polyglot.ast.TypeNode;
import polyglot.types.FieldDef;
import polyglot.types.Type;
import x10.types.X10FieldDef;

public interface X10FieldDecl extends FieldDecl {
	
	Type hasType();

	X10FieldDecl flags(FlagsNode flags);
	X10FieldDecl type(TypeNode type);
	X10FieldDecl name(Id name);
	X10FieldDecl init(Expr init);
	X10FieldDecl fieldDef(FieldDef mi);
	X10FieldDef fieldDef();
}
