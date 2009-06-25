/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package polyglot.ext.x10.plugin;

import polyglot.ext.x10.ExtensionInfo;
import polyglot.ext.x10.ExtensionInfo.X10Scheduler;
import polyglot.frontend.Goal;
import polyglot.frontend.Job;

public abstract class SimplePlugin implements CompilerPlugin {
	public SimplePlugin() {
		super();
	}

	public Goal register(ExtensionInfo extInfo, Job job) {
		Goal g = createGoal(extInfo, job);
		X10Scheduler x10Sched = (X10Scheduler) extInfo.scheduler();
		x10Sched.addDependencyAndEnqueue(g, x10Sched.TypeChecked(job), true);
		x10Sched.addDependencyAndEnqueue(g, x10Sched.ConstantsChecked(job), true);
		x10Sched.addDependencyAndEnqueue(g, x10Sched.PropagateAnnotations(job), true);
		x10Sched.addDependencyAndEnqueue(x10Sched.X10Boxed(job), g, true);
		return g;
	}

	public abstract Goal createGoal(ExtensionInfo extInfo, Job job);
}
