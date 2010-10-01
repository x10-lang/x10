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

import polyglot.ast.Node;
import polyglot.frontend.Goal;
import polyglot.frontend.Job;
import polyglot.frontend.SchedulerException;
import polyglot.frontend.SourceGoal;
import polyglot.types.LazyRef;
import polyglot.visit.TypeChecker;

public class TypeCheckFragmentGoal<T> extends polyglot.ast.TypeCheckFragmentGoal<T> implements SourceGoal {
    private static final long serialVersionUID = -7690455598516526228L;

    public TypeCheckFragmentGoal(Node parent, Node n, TypeChecker v, LazyRef<T> r, boolean mightFail) {
        super(parent, n, v, r, mightFail);
    }

    protected LazyRef<T> r() {
        return r;
    }

    public boolean runTask() {
        Goal g = v.job().extensionInfo().scheduler().PreTypeCheck(v.job());
        assert g.hasBeenReached();

        if (state() == Goal.Status.RUNNING_RECURSIVE) {
            r().update(defaultRecursiveValue()); // marks r known
            return mightFail;
        }

        try {
            Node m = process(parent, n, v);
            v.job().nodeMemo().put(n, m);
            v.job().nodeMemo().put(m, m);
            return mightFail || r.known();
        }
        catch (SchedulerException e) {
            return false;
        }
    }

    protected Node process(Node parent, Node n, TypeChecker v) {
        return parent.visitChild(n, v);
    }

    protected T defaultRecursiveValue() {
        return r().getCached();
    }

    public Job job() {
        return v.job();
    }
}
