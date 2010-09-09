package polyglot.frontend;

import java.util.*;

import polyglot.frontend.Goal.Status;
import polyglot.main.Report;
import polyglot.types.LazyRef_c;
import polyglot.util.StringUtil;

public abstract class AbstractGoal_c extends LazyRef_c<Goal.Status> implements Goal {
	String name;
	public List<Goal> prereqs;

	public Goal intern(Scheduler scheduler) {
		return scheduler.intern(this);
	}

	protected AbstractGoal_c() {
		super(Status.NEW);
		this.name = StringUtil.getShortNameComponent(getClass().getName().replace('$', '.'));
		setResolver(this);
	}

	protected AbstractGoal_c(String name) {
		super(Status.NEW);
		this.name = name;
		setResolver(this);
	}

	public void run() {
		AbstractGoal_c goal = this;
		if (Report.should_report(Report.frontend, 2))
			Report.report(2, "Running to goal " + goal);
		
		LinkedList<Goal> worklist = new LinkedList<Goal>(prereqs());

		Set<Goal> prereqs = new LinkedHashSet<Goal>();
		prereqs.addAll(worklist);
		
		while (! worklist.isEmpty()) {
			Goal g = worklist.removeFirst();
			
			if (g.getCached() == Goal.Status.SUCCESS)
			    continue;
			    
			if (Report.should_report(Report.frontend, 4))
				Report.report(4, "running prereq: " + g + "->" + goal);
			
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

		if (Report.should_report(Report.frontend, 4))
			Report.report(4, "running goal " + goal);

		if (Report.should_report(Report.frontend, 5)) {
			if (Globals.Scheduler().currentGoal() != null) {
				Report.report(5, "CURRENT = " + Globals.Scheduler().currentGoal());
				Report.report(5, "SPAWN   = " + goal);
			}
		}

		boolean result = false;
		try {
			result = Globals.Scheduler().runPass(this);
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
