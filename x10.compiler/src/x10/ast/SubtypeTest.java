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

import polyglot.ast.Expr;
import polyglot.ast.TypeNode;

public interface SubtypeTest extends Expr {
    
    boolean equals();

	public TypeNode supertype();

	public TypeNode subtype();

	public SubtypeTest supertype(TypeNode sup);

	public SubtypeTest subtype(TypeNode sub);

}
