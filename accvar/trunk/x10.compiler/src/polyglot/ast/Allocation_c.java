/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2010.
 */
package polyglot.ast;

import java.util.List;

import polyglot.ast.Expr_c;
import polyglot.ast.Term;
import polyglot.ast.TypeNode;
import polyglot.util.Position;
import polyglot.visit.CFGBuilder;

/**
 * @author Bowen Alpern
 *
 */
public class Allocation_c extends Expr_c implements Allocation {
    
    List<TypeNode> typeArguments;

    /**
     * @param pos
     */
    public Allocation_c(Position pos) {
        super(pos);
    }

    /* (non-Javadoc)
     * @see polyglot.ast.Term#firstChild()
     */
    public Term firstChild() {
        return null;
    }

    /* (non-Javadoc)
     * @see polyglot.ast.Term_c#acceptCFG(polyglot.visit.CFGBuilder, java.util.List)
     */
    @Override
    public <S> List<S> acceptCFG(CFGBuilder v, List<S> succs) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see x10.ast.X10Allocation#typeArguments()
     */
    public List<TypeNode> typeArguments() {
        return typeArguments;
    }

    /* (non-Javadoc)
     * @see x10.ast.X10Allocation#typeArguments(java.util.List)
     */
    public Allocation typeArguments(List<TypeNode> typeArgs) {
        Allocation_c c = (Allocation_c) copy();
        c.typeArguments = typeArgs;
        return c;
    }

}
