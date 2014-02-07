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

package x10.ast;

import polyglot.ast.Node;
import polyglot.ast.TypeCheckFragmentGoal;
import polyglot.ast.TypeCheckTypeGoal;
import polyglot.frontend.Job;
import polyglot.frontend.SourceGoal;
import polyglot.types.LazyRef;
import polyglot.types.Type;
import polyglot.types.UnknownType;
import polyglot.util.ErrorInfo;
import polyglot.visit.TypeChecker;

public class X10TypeCheckTypeGoal extends TypeCheckTypeGoal {
    private static final long serialVersionUID = 7359324021851338683L;

    public X10TypeCheckTypeGoal(Node parent, Node n, TypeChecker v, LazyRef<Type> r) {
        super(parent, n, v, r, false);
    }
}
