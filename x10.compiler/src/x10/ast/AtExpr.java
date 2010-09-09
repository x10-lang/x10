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

import java.util.List;

import polyglot.ast.CodeBlock;
import polyglot.ast.Expr;
import polyglot.ast.Formal;
import polyglot.ast.TypeNode;
import polyglot.types.Type;
import x10.types.ClosureDef;
import x10.visit.ExprFlattener;

/** The AST node for the X10 construct at (P) {e}
 *
 */
public interface AtExpr extends  PlacedClosure {
	
   
  
}
