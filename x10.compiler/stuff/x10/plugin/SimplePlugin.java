/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10.plugin;

import x10.ExtensionInfo;
import x10.ExtensionInfo.X10Scheduler;
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
