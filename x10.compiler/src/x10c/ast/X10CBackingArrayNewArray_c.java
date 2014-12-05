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

package x10c.ast;

import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.NewArray_c;
import polyglot.ast.TypeNode;
import polyglot.types.Type;
import polyglot.util.Position;

public class X10CBackingArrayNewArray_c extends NewArray_c implements BackingArrayNewArray {
    public X10CBackingArrayNewArray_c(Position pos, TypeNode baseType, List<Expr> dims, int addDims, Type type) {
        super(pos, baseType, dims, addDims, null);
        this.type = type;
    }
}
