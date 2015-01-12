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

package x10c.ast;

import polyglot.ast.ArrayAccessAssign_c;
import polyglot.ast.Expr;
import polyglot.ast.NodeFactory;
import polyglot.util.Position;

public class X10CBackingArrayAccessAssign_c extends ArrayAccessAssign_c implements BackingArrayAccessAssign {
    public X10CBackingArrayAccessAssign_c(NodeFactory nf, Position pos, Expr array, Expr index, Operator op, Expr right) {
        super(nf, pos, array, index, op, right);
    }
}