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
