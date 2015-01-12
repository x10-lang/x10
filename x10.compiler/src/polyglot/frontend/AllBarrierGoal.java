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

import java.util.*;

public abstract class AllBarrierGoal extends AbstractGoal_c {
    private static final long serialVersionUID = 7313267162257728279L;

    protected Scheduler scheduler;
    protected final Collection<Source> sources; // used to determine equality

    public AllBarrierGoal(Scheduler scheduler) {
        this(null, scheduler);
    }
    
    public AllBarrierGoal(String name, Scheduler scheduler) {
        super(name);
        this.scheduler = scheduler;
        sources = scheduler.sources;
    }

    @Override
    public boolean equals(Object o) {
		if (o instanceof AllBarrierGoal) {
			AllBarrierGoal g = (AllBarrierGoal) o;
			return name().equals(g.name()) && (
                    sources.equals(g.sources));
		}
		return false;

    }

    @Override
    public int hashCode() {
        return name.hashCode() ^ sources.hashCode();
    }

    public abstract Goal prereqForJob(Job job);
    
    public List<Goal> prereqs() {
        List<Goal> l = new ArrayList<Goal>();
        for (Job job : scheduler.jobs()) {
            Goal g = prereqForJob(job);
	    if (g != null)
		l.add(g);
        }
        l.addAll(super.prereqs());
        return Collections.unmodifiableList(l);
    }
    
    public boolean runTask() {
    	return true;
    }
}
