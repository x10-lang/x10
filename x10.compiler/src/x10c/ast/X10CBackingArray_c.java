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

import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.Expr_c;
import polyglot.ast.Id;
import polyglot.ast.Term;
import polyglot.types.Type;
import polyglot.util.Position;
import polyglot.visit.CFGBuilder;

public class X10CBackingArray_c extends Expr_c implements BackingArray {

    private final Expr container;
    
    public X10CBackingArray_c(Position pos, Id id, Type type, Expr container) {
        super(pos);
        super.type = type;
        this.container = container;
    }

    public Expr container() {
        return container;
    }

    @Override
    public <S> List<S> acceptCFG(CFGBuilder v, List<S> succs) {
        return null;
    }
    
    public Term firstChild() {
        return null;
    }
    
    @Override
    public String toString() {
        return container.toString() + ".value";
    }
}