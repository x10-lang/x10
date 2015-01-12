/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 * This file was originally derived from the Polyglot extensible compiler framework.
 *
 *  (C) Copyright 2000-2007 Polyglot project group, Cornell University
 *  (C) Copyright IBM Corporation 2007-2015.
 */

package polyglot.ast;

import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.TypeNode;

/**
 * @author Bowen Alpern
 *
 */
public interface Allocation extends Expr {

    /**
     * @return the type of the object being allocated
     */
    TypeNode objectType();

    /**
     * @param objType the type of the object being allocated
     */
    Allocation objectType(TypeNode objType);

    /**
     * @return the type arguments for the allocation
     */
    List<TypeNode> typeArguments();

    /**
     * @param typeArgs the type arguments for the allocation
     * @return a copy of the allocation with it type arguments set
     */
    Allocation typeArguments(List<TypeNode> typeArgs);

}
