package x10.optimizations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import polyglot.ast.NodeFactory;
import polyglot.frontend.AbstractGoal_c;
import polyglot.frontend.CyclicDependencyException;
import polyglot.frontend.ExtensionInfo;
import polyglot.frontend.Globals;
import polyglot.frontend.Goal;
import polyglot.frontend.JLScheduler;
import polyglot.frontend.Job;
import polyglot.frontend.Scheduler;
import polyglot.frontend.VisitorGoal;
import polyglot.frontend.Goal.Status;
import polyglot.main.Report;
import polyglot.types.ClassDef;
import polyglot.types.Flags;
import polyglot.types.LazyRef;
import polyglot.types.QName;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import x10.visit.Inliner;

public class Optimizer extends JLScheduler implements Goal {

    private final Job job;
    private Goal.Status status;
    private boolean known;
    private List<Goal> prereqs;

    public Optimizer(Job job) {
        super(job.extensionInfo());
        this.job = job;
        this.status = Goal.Status.NEW;
        this.known = false;
    }

    public Goal intern(Scheduler scheduler) {
        return scheduler.intern(this);
    }

    public List<Goal> goals(Job job) {
        List<Goal> goals = new ArrayList<Goal>();

        if (x10.Configuration.INLINE_OPTIMIZATIONS)
            goals.add(Inliner(job));
        goals.add(LoopUnrolling(job));
        goals.add(ForLoopOptimizations(job));

        return goals;
    }

    public Goal ForLoopOptimizations(Job job) {
        TypeSystem ts = extInfo.typeSystem();
        NodeFactory nf = extInfo.nodeFactory();
        return new VisitorGoal("For Loop Optimizations", job, new ForLoopOptimizer(job, ts, nf)).intern(this);
    }

    public Goal LoopUnrolling(Job job) {
        TypeSystem ts = extInfo.typeSystem();
        NodeFactory nf = extInfo.nodeFactory();
        return new VisitorGoal("Loop Unrolling", job, new LoopUnroller(job, ts, nf)).intern(this);
    }

    public Goal Inliner(Job job) {
        TypeSystem ts = extInfo.typeSystem();
        NodeFactory nf = extInfo.nodeFactory();
        return new VisitorGoal("Inlined", job, new Inliner(job, ts, nf)).intern(this);
    }

    public boolean runTask() {
        List<Goal> goals = goals(job);
        for (Goal goal : goals) {
            if (!goal.runTask())
                return false;
        }
        return true;
    }

    // FIXME: code copied from AbstractGoal_c
    public void run() {
        Goal goal = this;
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
            status = Status.RUNNING_RECURSIVE;
            break;
        case NEW:
            status = Status.RUNNING;
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
            result = runPass(this);
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

    // FIXME: code copied from AbstractGoal_c
    public List<Goal> prereqs() {
        if (prereqs == null) {
            return Collections.emptyList();
        }
        else {
            return Collections.unmodifiableList(prereqs);
        }
    }

    // FIXME: code copied from AbstractGoal_c
    public void addPrereq(final Goal goal) {
        if (prereqs == null) {
            prereqs = new ArrayList<Goal>();
        }

        prereqs.add(goal);
    }

    // FIXME: code copied from AbstractGoal_c
    public boolean hasBeenReached() {
        return getCached() == Status.SUCCESS;
    }

    // FIXME: code copied from AbstractGoal_c
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
        return "Optimizer";
    }

    // FIXME: code copied from AbstractGoal_c
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

    // FIXME: code copied from AbstractGoal_c
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

    public Status get() {
        if (! known()) {
            run();
            if (! known()) {
                // Should have already reported an error.
            }
            known = true;
        }
        return getCached();
    }

    public Status getCached() {
        return status;
    }

    public boolean known() {
        return known;
    }

    public void update(Status v) {
        status = v;
    }
}
