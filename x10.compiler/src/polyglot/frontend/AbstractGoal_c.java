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

import polyglot.frontend.Goal.Status;
import polyglot.main.Reporter;
import polyglot.types.LazyRef_c;
import polyglot.util.StringUtil;

public abstract class AbstractGoal_c extends LazyRef_c<Goal.Status> implements Goal {
	private static final long serialVersionUID = 39827248332800427L;

	String name;
	private List<Goal> prereqs;
	protected Scheduler scheduler = null;
	private List<GoalListener> listeners;

	public final Goal intern(Scheduler scheduler) {
		this.scheduler = scheduler;
		return scheduler.intern(this);
	}

	protected AbstractGoal_c() {
        this(null);
	}
	protected AbstractGoal_c(String name) {
		super(Status.NEW);
		this.name = name==null ? StringUtil.getShortNameComponent(getClass().getName().replace('$', '.')) : name;
		setResolver(this);
	}

	public boolean addListener(GoalListener listener) {
		Goal g = this;
		if (scheduler != null)
			g = this.intern(scheduler);
		if (g != this)
			return g.addListener(listener);

		boolean adding = true;
		if (listeners == null) {
			listeners = new ArrayList<GoalListener>();
		} else {
			for (GoalListener l : listeners) {
				if (l == listener) {
					adding = false;
					break;
				}
			}
		}
		if (adding)
			listeners.add(listener);
		return adding;
	}

	public void run() {
		AbstractGoal_c goal = this;
		Reporter reporter = scheduler.extensionInfo().getOptions().reporter;
		if (reporter.should_report(reporter.frontend, 2))
			reporter.report(2, "Running to goal " + goal);
		
		LinkedList<Goal> worklist = new LinkedList<Goal>(prereqs());

		Set<Goal> prereqs = new LinkedHashSet<Goal>();
		prereqs.addAll(worklist);
		
		while (! worklist.isEmpty()) {
			Goal g = worklist.removeFirst();
			
			if (g.getCached() == Goal.Status.SUCCESS)
			    continue;
			    
			if (reporter.should_report(reporter.frontend, 4))
				reporter.report(4, "running prereq: " + g + "->" + goal);
			
			Status s = g.get();

			// Make sure any new prereqs added during the recursion are in the queue.
			for (Goal g2 : prereqs()) {
			    if (! prereqs.contains(g2)) {
				prereqs.add(g2);
				worklist.add(g2);
			    }
			}
			
			switch (s) {
			case NEW:
			case FAIL:
			case RUNNING:
			case RUNNING_RECURSIVE:
			case RUNNING_WILL_FAIL:
			case UNREACHABLE:
				update(Status.UNREACHABLE);
				continue;
			case SUCCESS:
				break;
			}
		}
		
		Status oldStatus = getCached();

		switch (oldStatus) {
		case RUNNING:
		case RUNNING_RECURSIVE:
		    updateCache(Status.RUNNING_RECURSIVE);
		    break;
		case NEW:
		    updateCache(Status.RUNNING);
		    break;		  
		case RUNNING_WILL_FAIL:
		default:
		    return;
		}
		
		boolean recursive = oldStatus == Status.RUNNING_RECURSIVE;

		if (reporter.should_report(reporter.frontend, 4))
			reporter.report(4, "running goal " + goal);

		if (reporter.should_report(reporter.frontend, 5)) {
			if (scheduler.currentGoal() != null) {
				reporter.report(5, "CURRENT = " + scheduler.currentGoal());
				reporter.report(5, "SPAWN   = " + goal);
			}
		}

		boolean result = false;
		try {
			if (listeners != null) {
			    for (GoalListener l : listeners) {
			        l.taskStarted(this);
			    }
			}
			result = scheduler.runPass(this);
			if (state() == Goal.Status.RUNNING_WILL_FAIL)
			    result = false;
		}
		catch (CyclicDependencyException e) {
		}

		if (result) {
		    switch (oldStatus) {
		    case RUNNING:
		    case RUNNING_RECURSIVE:
			update(oldStatus);
			break;
		    case NEW:
			update(Status.SUCCESS);
			break;		  
		    default:
			break;
		    }
		}
		else {
		    switch (oldStatus) {
		    case RUNNING:
		    case RUNNING_RECURSIVE:
			update(Status.RUNNING_WILL_FAIL);
			break;
		    case NEW:
			update(Status.FAIL);
			break;		  
		    default:
			break;
		    }
		}
	}

	public abstract boolean runTask();

	public List<Goal> prereqs() {
		if (prereqs == null) {
			return Collections.emptyList();
		}
		else {
			return Collections.unmodifiableList(prereqs);
		}
	}

	public void addPrereq(final Goal goal) {
		if (prereqs == null) {
			prereqs = new ArrayList<Goal>();
		}

		prereqs.add(goal);
	}

	public boolean hasBeenReached() {
		return getCached() == Status.SUCCESS;
	}

	public boolean isReachable() {
		Status state = getCached();
		switch (state) {
		case NEW:
		case RUNNING:
		case RUNNING_RECURSIVE:
		case SUCCESS:
			return true;
		case RUNNING_WILL_FAIL:
		case FAIL:
		case UNREACHABLE:
			return false;
		default:
			return false;
		}
	}

	public String name() {
		return name;
	}

	public Status state() {
		return getCached();
	}

	public int hashCode() {
		return name().hashCode();
	}

	public boolean equals(Object o) {
		if (o instanceof Goal) {
			Goal g = (Goal) o;
			return name().equals(g.name());
		}
		return false;
	}

	public boolean isRunning() {
	    switch (state()) {
	    case RUNNING:
	    case RUNNING_RECURSIVE:
	    case RUNNING_WILL_FAIL:
		return true;
	    default:
		return false;
	    }
	}

	public void fail() {
	    switch (state()) {
	    case SUCCESS:
		assert false;
		break;
	    case RUNNING:
	    case RUNNING_RECURSIVE:
		update(Goal.Status.RUNNING_WILL_FAIL);
		break;
	    case NEW:
		update(Goal.Status.UNREACHABLE);
		break;
	    case RUNNING_WILL_FAIL:
	    case FAIL:
	    case UNREACHABLE:
		break;
	    }
	}

	protected String stateString() {
		Status state = state();
		switch (state) {
		case NEW:
			return "new";
		case RUNNING:
			return "running";
		case RUNNING_RECURSIVE:
			return "running-recursive";
		case RUNNING_WILL_FAIL:
		    return "running-will-fail";
		case SUCCESS:
			return "success";
		case FAIL:
			return "failed";
		case UNREACHABLE:
			return "unreachable";
		}
		return "unknown-goal-state";
	}

	public String toString() {
		return name() + " (" + stateString() + ")";
	}
}
