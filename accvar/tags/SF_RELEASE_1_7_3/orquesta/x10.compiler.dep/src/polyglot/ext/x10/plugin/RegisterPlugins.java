package polyglot.ext.x10.plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import polyglot.ext.x10.ExtensionInfo;
import polyglot.ext.x10.ExtensionInfo.X10Scheduler;
import polyglot.frontend.AbstractGoal_c;
import polyglot.frontend.AbstractPass;
import polyglot.frontend.Globals;
import polyglot.frontend.Goal;
import polyglot.frontend.Job;
import polyglot.frontend.Pass;
import polyglot.frontend.Scheduler;
import polyglot.frontend.SourceGoal_c;

public class RegisterPlugins extends SourceGoal_c {

	protected static final class RegisterPluginsPass extends AbstractPass {
		protected ExtensionInfo extInfo;

		protected RegisterPluginsPass(Goal goal, ExtensionInfo extInfo) {
			super(goal);
			this.extInfo = extInfo;
		}

		public boolean run() {
		    for (CompilerPlugin plugin : extInfo.plugins().values()) {
		        plugin.register(extInfo, ((RegisterPlugins) goal).job());
		    }

		    return true;
		}
	}

	public RegisterPlugins(Job job) {
		super("RegisterPlugins", job);
	}

	@Override
	public Pass createPass() {
		return new RegisterPluginsPass(this, (polyglot.ext.x10.ExtensionInfo) Globals.Extension());
	}
//	
//	public List<Goal> prereqs() 	{
//		X10Scheduler x10Sched = (X10Scheduler) Globals.Scheduler();
//		List<Goal> l = new ArrayList<Goal>();
//		l.add(x10Sched.LoadJobPlugins(job));
//		l.addAll(super.prereqs());
//		return l;
//	}
}
