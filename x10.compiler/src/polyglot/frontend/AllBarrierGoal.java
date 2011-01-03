package polyglot.frontend;

import java.util.*;

public abstract class AllBarrierGoal extends AbstractGoal_c {
    private static final long serialVersionUID = 7313267162257728279L;

    protected Scheduler scheduler;
    private List<Goal> prereqs = null; // this is not just a cache! we use it for equality and hashCode, and when the compiler is set with different commandLineJobs, then this set shouldn't change
    
    public AllBarrierGoal(Scheduler scheduler) {
        super();
        this.scheduler = scheduler;
    }
    
    public AllBarrierGoal(String name, Scheduler scheduler) {
        super(name);
        this.scheduler = scheduler;
    }

    @Override
    public boolean equals(Object o) {
		if (o instanceof AllBarrierGoal) {
			AllBarrierGoal g = (AllBarrierGoal) o;
			return name().equals(g.name()) && (
                    prereqs==g.prereqs || // either both are null, or both are non-null and equal
                    (prereqs!=null && g.prereqs!=null && prereqs.equals(g.prereqs)));
		}
		return false;

    }

    @Override
    public int hashCode() {
        return name.hashCode() ^ (prereqs==null ? 0 : prereqs.hashCode());
    }

    public abstract Goal prereqForJob(Job job);
    
    public List<Goal> prereqs() {
        if (prereqs!=null) return prereqs;

        List<Goal> l = new ArrayList<Goal>();
        for (Job job : scheduler.jobs()) {
            Goal g = prereqForJob(job);
	    if (g != null)
		l.add(g);
        }
        l.addAll(super.prereqs());
        prereqs = Collections.unmodifiableList(l);
        return prereqs;
    }
    
    public boolean runTask() {
    	return true;
    }
}
