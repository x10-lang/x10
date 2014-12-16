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
 *  (C) Copyright IBM Corporation 2007-2014.
 */

package polyglot.ast;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import polyglot.frontend.*;
import polyglot.types.LazyRef;
import polyglot.util.CollectionUtil; import x10.util.CollectionFactory;
import polyglot.visit.TypeChecker;

public class TypeCheckFragmentGoal<T> extends AbstractGoal_c implements SourceGoal{
    private static final long serialVersionUID = -843644476867221586L;

    private Node parent;
    private Node[] prereqs;
    private Node n;
    private TypeChecker v;
    private LazyRef<T> r;
    private boolean mightFail;

    public TypeCheckFragmentGoal(Node parent, Node n, TypeChecker v, LazyRef<T> r, boolean mightFail) {
        this(parent, new Node[0], n, v, r, mightFail);
    }

    public TypeCheckFragmentGoal(Node parent, Node[] prereqs, Node n, TypeChecker v, LazyRef<T> r, boolean mightFail) {
        this.parent = parent;
        this.prereqs = prereqs;
        this.n = n;
        this.v = v;
        this.r = r;
        this.mightFail = mightFail;
        this.scheduler = v.job().extensionInfo().scheduler();
    }

    protected Node n() {
        return n;
    }

    public List<Goal> prereqs() {
        List<Goal> l = super.prereqs();
        List<Goal> l2 = Collections.singletonList(v.job().extensionInfo().scheduler().PreTypeCheck(v.job()));
        if (l.isEmpty())
            return l2;
        else
            return CollectionUtil.<Goal> append(l, l2);
    }

    protected LazyRef<T> r() {
        return r;
    }

    protected TypeChecker v() {
        return v;
    }
    
    protected Node processPrereq(Node parent, Node n, TypeChecker v) {
        if (n != null) {
            v = (TypeChecker) v.enter(parent, n); // uses v.context
        }
        return parent.visitChild(n, v);
    }

    protected Node process(Node parent, Node n, TypeChecker v) {
        return processPrereq(parent, n, v);
    }
    
    protected T defaultRecursiveValue() {
        return r().getCached();
    }

    public Job job() {
        return v.job();
    }


    public boolean runTask() {
        Goal g = v.job().extensionInfo().scheduler().PreTypeCheck(v.job());
        assert g.hasBeenReached();

        if (state() == Goal.Status.RUNNING_RECURSIVE) {
            r().update(defaultRecursiveValue()); // marks r known
            return mightFail;
        }

        try {
            if (prereqs != null) {
                for (int i = 0; i < prereqs.length; i++) {
                    Node n = prereqs[i];
                    Node m = processPrereq(parent, n, v);
                    v.job().nodeMemo().put(n, m);
                    v.job().nodeMemo().put(m, m);
                }
            }
            Node n = n();
            Node m = process(parent, n, v);
            v.job().nodeMemo().put(n, m);
            v.job().nodeMemo().put(m, m);
            return mightFail || r.known();
        }
        catch (SchedulerException e) {
            return false;
        }
    }

    public String toString() {
        return v.job() + ":" + v.job().extensionInfo() + ":" + name() + " (" + stateString() + ") " + parent + "->" + n + " via " + Arrays.toString(prereqs);
    }
}