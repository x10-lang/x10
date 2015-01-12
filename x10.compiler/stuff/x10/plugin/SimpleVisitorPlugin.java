/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

package x10.plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import x10.ExtensionInfo;
import x10.ExtensionInfo.X10Scheduler;
import polyglot.frontend.Goal;
import polyglot.frontend.Job;
import polyglot.frontend.Scheduler;
import polyglot.visit.NodeVisitor;

public abstract class SimpleVisitorPlugin implements CompilerPlugin {
	public SimpleVisitorPlugin() {
		super();
	}

	public static class TypeCheckPluginGoal extends VisitorGoal {
		public static Goal create(Scheduler scheduler, Job job, NodeVisitor v) {
    		return scheduler.internGoal(new TypeCheckPluginGoal(job, v));
    	}

		private TypeCheckPluginGoal(Job job, NodeVisitor v) {
    		super(job, v);
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
	}

    public abstract NodeVisitor getVisitor(ExtensionInfo extInfo, Job job);
    	
	public Goal register(ExtensionInfo extInfo, Job job) {
		Goal g = TypeCheckPluginGoal.create(extInfo.scheduler(), job,
                                            getVisitor(extInfo, job));
		
		X10Scheduler x10Sched = (X10Scheduler) extInfo.scheduler();
		x10Sched.addDependencyAndEnqueue(x10Sched.X10Boxed(job), g, true);
		
		return g;
	}
}
