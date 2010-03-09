/*
 * Created on Feb 23, 2006
 */
package com.ibm.wala.cast.x10.analysis;

import polyglot.frontend.CyclicDependencyException;
import polyglot.frontend.ExtensionInfo;
import polyglot.frontend.Job;
import polyglot.frontend.Pass;
import polyglot.frontend.Scheduler;
import polyglot.frontend.goals.AbstractGoal;
import polyglot.util.ErrorInfo;

import com.ibm.wala.cast.x10.translator.polyglot.WALAScheduler;
import com.ibm.wala.cast.tree.CAstEntity;

public class AsyncAnalysisGoal extends AbstractGoal {
    public AsyncAnalysisGoal(Job job) {
	super(job);
	try {
	    WALAScheduler scheduler= (WALAScheduler) job.extensionInfo().scheduler();

	    addPrerequisiteGoal(scheduler.CAstGenerated(job), (Scheduler)scheduler);
	    addPrerequisiteGoal(scheduler.CAstDumped(job), (Scheduler)scheduler);
} catch (CyclicDependencyException e) {
	    job.compiler().errorQueue().enqueue(ErrorInfo.INTERNAL_ERROR, "Cycle encountered in goal graph?");
	    throw new IllegalStateException(e.getMessage());
	}
    }

    public Pass createPass(ExtensionInfo extInfo) {
	CAstEntity entity= (CAstEntity) ((AnalysisJobExt) this.job().ext()).get(AnalysisJobExt.CAST_JOBEXT_KEY);

	return new AsyncAnalysisPass(this, entity);
    }
}
