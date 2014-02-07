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
import polyglot.ast.Id;
import polyglot.ast.TypeNode;
import polyglot.ast.Assign.Operator;
import polyglot.types.Type;
import polyglot.util.Position;
import x10.ExtensionInfo;
import x10.ast.X10NodeFactory_c;

public class X10CNodeFactory_c extends X10NodeFactory_c {

    public X10CNodeFactory_c(ExtensionInfo extInfo) {
        super(extInfo);
    }

    public BackingArray BackingArray(Position pos, Id id, Type type, Expr rail) {
        BackingArray n = new X10CBackingArray_c(pos, id, type, rail);
        n = (BackingArray) n.ext(extFactory().extExpr());
        n = (BackingArray) n.del(delFactory().delExpr());
        return n;
    }

    public BackingArrayAccess BackingArrayAccess(Position pos, Expr rail, Expr index, Type type) {
        BackingArrayAccess n = new X10CBackingArrayAccess_c(pos, rail, index, type);
        n = (BackingArrayAccess) n.ext(extFactory().extArrayAccess());
        n = (BackingArrayAccess) n.del(delFactory().delArrayAccess());
        return n;
    }

    public BackingArrayAccessAssign BackingArrayAccessAssign(Position pos, Expr rail, Expr index, Operator op, Expr right) {
        BackingArrayAccessAssign n = new X10CBackingArrayAccessAssign_c(this, pos, rail, index, op, right);
        n = (BackingArrayAccessAssign) n.ext(extFactory().extArrayAccessAssign());
        n = (BackingArrayAccessAssign) n.del(delFactory().delArrayAccessAssign());
        return n;
    }

    public BackingArrayNewArray BackingArrayNewArray(Position pos, TypeNode baseType, List<Expr> dims, int addDims, Type type) {
        BackingArrayNewArray n = new X10CBackingArrayNewArray_c(pos, baseType, dims, addDims, type);
        n = (BackingArrayNewArray) n.ext(extFactory().extArrayAccess());
        n = (BackingArrayNewArray) n.del(delFactory().delArrayAccess());
        return n;
    }
}
