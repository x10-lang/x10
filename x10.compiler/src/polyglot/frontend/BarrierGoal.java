package polyglot.frontend;

import java.util.*;

public abstract class BarrierGoal extends AbstractGoal_c {
    Collection<Job> jobs;
    
    public BarrierGoal(String name, Collection<Job> jobs) {
        super(name);
        assert jobs != null;
        this.jobs = jobs;
    }
    
    public BarrierGoal(Collection<Job> jobs) {
        super();
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
