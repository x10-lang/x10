package polyglot.frontend;

import java.util.*;

public abstract class AllBarrierGoal extends AbstractGoal_c {
    protected Scheduler scheduler;
    
    public AllBarrierGoal(Scheduler scheduler) {
        super();
        this.scheduler = scheduler;
    }
    
    public AllBarrierGoal(String name, Scheduler scheduler) {
        super(name);
        this.scheduler = scheduler;
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
        return l;
    }
    
    public boolean runTask() {
    	return true;
    }
}
