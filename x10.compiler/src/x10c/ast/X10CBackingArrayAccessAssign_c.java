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

package x10c.ast;

import polyglot.ast.ArrayAccessAssign_c;
import polyglot.ast.Expr;
import polyglot.util.Position;
import x10.ast.X10NodeFactory;

public class X10CBackingArrayAccessAssign_c extends ArrayAccessAssign_c implements BackingArrayAccessAssign {
    public X10CBackingArrayAccessAssign_c(X10NodeFactory nf, Position pos, Expr array, Expr index, Operator op, Expr right) {
        super(nf, pos, array, index, op, right);
    }
}