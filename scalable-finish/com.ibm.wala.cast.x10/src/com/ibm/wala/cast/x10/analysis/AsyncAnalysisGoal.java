package com.ibm.wala.cast.x10.analysis;

import polyglot.frontend.Job;
import polyglot.frontend.SourceGoal_c;

import com.ibm.wala.cast.tree.CAstEntity;
import com.ibm.wala.cast.x10.translator.polyglot.WALAScheduler;

public class AsyncAnalysisGoal extends SourceGoal_c {
    public AsyncAnalysisGoal(Job job) {
	super(job);

	WALAScheduler scheduler= (WALAScheduler) job.extensionInfo().scheduler();

	addPrereq(scheduler.CAstGenerated(job));
	addPrereq(scheduler.CAstDumped(job));
    }

    @Override
    public boolean runTask() {
        AnalysisJobExt je= (AnalysisJobExt) job().ext();
        CAstEntity entity= (CAstEntity) je.get(AnalysisJobExt.CAST_JOBEXT_KEY);

        // Odd: System.out appears to be closed now??? Use System.err...
        System.err.println("Hi Mom");
        return true;
    }
}
