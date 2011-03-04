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

package x10.ast;

import polyglot.frontend.Job;
import polyglot.frontend.SourceGoal;
import polyglot.util.ErrorInfo;
import polyglot.visit.TypeChecker;
import x10.types.LazyRef;
import x10.types.Type;
import x10.types.UnknownType;

public class X10TypeCheckTypeGoal extends X10TypeCheckFragmentGoal<Type> {
    private static final long serialVersionUID = 7359324021851338683L;

    public X10TypeCheckTypeGoal(Node parent, Node n, TypeChecker v, LazyRef<Type> r) {
        super(parent, n, v, r, false);
    }

    @Override
    public boolean runTask() {
        boolean result = super.runTask();
        if (result) {
            if (r().getCached() instanceof UnknownType) {
                v.errorQueue().enqueue(ErrorInfo.SEMANTIC_ERROR, "Could not compute type.", n.position());
                return false;
            }
        }
        return result;
    }
}
