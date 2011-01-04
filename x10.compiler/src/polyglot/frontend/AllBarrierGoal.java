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
