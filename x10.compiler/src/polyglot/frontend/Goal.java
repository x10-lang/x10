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

package polyglot.frontend;

import java.util.List;

import polyglot.types.Ref;


public interface Goal extends Ref<Goal.Status>, Runnable {
    public static enum Status {
        NEW, RUNNING, SUCCESS, FAIL, UNREACHABLE, RUNNING_RECURSIVE, RUNNING_WILL_FAIL;
    };

    Status state();

    Goal intern(Scheduler s);

    String name();

    /** Return true if this goal is reachable. */
    boolean isReachable();

    /** Return true if this goal has been reached. */
    boolean hasBeenReached();

    /** Mark this pass as failed. */
    void fail();

    List<Goal> prereqs();
    void addPrereq(Goal goal);

    boolean runTask();

    /**
     * Add a listener object to the goal.
     * @return true if the listener was added, false if the listener was already present
     */
    boolean addListener(GoalListener listener);
}
