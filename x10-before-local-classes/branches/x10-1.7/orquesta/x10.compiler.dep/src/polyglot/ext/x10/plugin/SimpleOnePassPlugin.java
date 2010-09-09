package polyglot.ext.x10.plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import polyglot.ext.x10.ExtensionInfo;
import polyglot.ext.x10.ExtensionInfo.X10Scheduler;
import polyglot.frontend.AbstractGoal_c;
import polyglot.frontend.Goal;
import polyglot.frontend.Job;
import polyglot.frontend.Pass;
import polyglot.frontend.Scheduler;

public abstract class SimpleOnePassPlugin implements CompilerPlugin {
	public SimpleOnePassPlugin() {
		super();
	}

	public static class SimpleOnePassPluginGoal extends AbstractGoal_c {
		public static Goal create(Scheduler scheduler, Job job, SimpleOnePassPlugin plugin) {
    		return scheduler.internGoal(new SimpleOnePassPluginGoal(job, plugin));
    	}

		private SimpleOnePassPlugin plugin;

		private SimpleOnePassPluginGoal(Job job, SimpleOnePassPlugin plugin) {
    		super(job, plugin.getClass().getName());
    		this.plugin = plugin;
    	}

    	public Collection prerequisiteGoals(Scheduler scheduler) {
    		X10Scheduler x10Sched = (X10Scheduler) scheduler;
    		List<Goal> l = new ArrayList<Goal>();
    		l.add(x10Sched.TypeChecked(job));
    		l.add(x10Sched.ConstantsChecked(job));
    		l.add(x10Sched.PropagateAnnotations(job));
    		l.addAll(super.prerequisiteGoals(scheduler));
    		return l;
    	}
    	
		public Pass createPass(polyglot.frontend.ExtensionInfo extInfo) {
			return plugin.createPass((ExtensionInfo) extInfo, job);
		}
	}

    public abstract Pass createPass(ExtensionInfo extInfo, Job job);
    	
	public Goal register(ExtensionInfo extInfo, Job job) {
		Goal g = SimpleOnePassPluginGoal.create(extInfo.scheduler(), job, this);
		
		X10Scheduler x10Sched = (X10Scheduler) extInfo.scheduler();
		x10Sched.addDependencyAndEnqueue(x10Sched.X10Boxed(job), g, true);
		
		return g;
	}
}
