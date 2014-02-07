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

import polyglot.ast.ArrayAccess_c;
import polyglot.ast.Expr;
import polyglot.types.Type;
import polyglot.util.Position;

public class X10CBackingArrayAccess_c extends ArrayAccess_c implements BackingArrayAccess {
    public X10CBackingArrayAccess_c(Position pos, Expr array, Expr index, Type type) {
        super(pos, array, index);
        this.type = type;
    }
}