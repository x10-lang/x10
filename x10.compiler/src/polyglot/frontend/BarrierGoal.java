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

package polyglot.frontend;

import java.util.*;

public abstract class BarrierGoal extends AbstractGoal_c {
    private static final long serialVersionUID = -8652728509156749456L;

    Collection<Job> jobs;
    
    public BarrierGoal(String name, Collection<Job> jobs) {
        super(name);
        assert jobs != null;
        this.jobs = jobs;
    }

    @Override
    public int hashCode() {
        return name.hashCode()^jobs.hashCode();
    }

    @Override
    public boolean equals(Object o) {
		if (o instanceof BarrierGoal) {
			BarrierGoal g = (BarrierGoal) o;
			return name().equals(g.name()) && jobs.equals(g.jobs);
		}
		return false;
    }

    public BarrierGoal(Collection<Job> jobs) {
        assert jobs != null;
        this.jobs = jobs;
    }
    
    public abstract Goal prereqForJob(Job job);
    
    public List<Goal> prereqs() {
        List<Goal> l = new ArrayList<Goal>();
        for (Job job : jobs) {
            l.add(prereqForJob(job));
        }
        l.addAll(super.prereqs());
        return l;
    }
    
    public boolean runTask() {
    	return true;
    }
}
