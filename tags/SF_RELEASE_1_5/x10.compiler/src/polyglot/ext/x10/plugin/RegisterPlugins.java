package polyglot.ext.x10.plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import polyglot.ext.x10.Configuration;
import polyglot.frontend.AbstractPass;
import polyglot.ext.x10.ExtensionInfo;
import polyglot.ext.x10.ExtensionInfo.X10Scheduler;
import polyglot.frontend.Job;
import polyglot.frontend.Pass;
import polyglot.frontend.Scheduler;
import polyglot.frontend.goals.AbstractGoal;
import polyglot.frontend.goals.Goal;
import polyglot.util.ErrorInfo;
import polyglot.util.ErrorQueue;

public class RegisterPlugins extends AbstractGoal {

	protected static final class RegisterPluginsPass extends AbstractPass {
		private final ExtensionInfo extInfo;

		protected RegisterPluginsPass(Goal goal, ExtensionInfo extInfo) {
			super(goal);
			this.extInfo = extInfo;
		}

		public boolean run() {
			for (Iterator i = extInfo.plugins().values().iterator(); i.hasNext(); ) {
				CompilerPlugin plugin = (CompilerPlugin) i.next();
				plugin.register(extInfo, goal.job());
			}
			
			return true;
		}
	}

	private RegisterPlugins(Job job) {
		super(job, "RegisterPlugins");
	}

	@Override
	public Pass createPass(polyglot.frontend.ExtensionInfo extInfo) {
		return new RegisterPluginsPass(this, (polyglot.ext.x10.ExtensionInfo) extInfo);
	}

	public static Goal create(X10Scheduler scheduler, Job job) {
		return scheduler.internGoal(new RegisterPlugins(job));
	}
	
	public Collection prerequisiteGoals(Scheduler scheduler) {
		X10Scheduler x10Sched = (X10Scheduler) scheduler;
		List<Goal> l = new ArrayList<Goal>();
		l.add(x10Sched.LoadJobPlugins(job));
		l.addAll(super.prerequisiteGoals(scheduler));
		return l;
	}
}
