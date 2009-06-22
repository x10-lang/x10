/*
 * Created on Feb 23, 2006
 */
package com.ibm.wala.cast.x10.translator.polyglot;

import polyglot.frontend.CyclicDependencyException;
import polyglot.frontend.ExtensionInfo;
import polyglot.frontend.Job;
import polyglot.frontend.Pass;
import polyglot.frontend.Scheduler;
import polyglot.frontend.goals.AbstractGoal;
import polyglot.util.ErrorInfo;

public class CAstDumpedGoal extends AbstractGoal {

    public CAstDumpedGoal(Job job) {
	super(job);
	try {
	    WALAScheduler scheduler= (WALAScheduler) job.extensionInfo().scheduler();

	    addPrerequisiteGoal(scheduler.CAstGenerated(job), (Scheduler)scheduler);
	} catch (CyclicDependencyException e) {
	    job.compiler().errorQueue().enqueue(ErrorInfo.INTERNAL_ERROR, "Cycle encountered in goal graph?");
	    throw new IllegalStateException(e.getMessage());
	}
    }

    public Pass createPass(ExtensionInfo extInfo) {
	return new CAstDumperPass(this);
    }
}
