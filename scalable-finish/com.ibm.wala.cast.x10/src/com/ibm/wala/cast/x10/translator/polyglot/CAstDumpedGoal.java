package com.ibm.wala.cast.x10.translator.polyglot;

import java.io.PrintWriter;

import polyglot.frontend.Job;
import polyglot.frontend.SourceGoal_c;

import com.ibm.wala.cast.tree.CAstEntity;
import com.ibm.wala.cast.x10.analysis.AnalysisJobExt;
import com.ibm.wala.cast.x10.translator.X10CAstPrinter;

public class CAstDumpedGoal extends SourceGoal_c {

    public CAstDumpedGoal(Job job) {
	super(job);

	WALAScheduler scheduler= (WALAScheduler) job.extensionInfo().scheduler();

	addPrereq(scheduler.CAstGenerated(job));
    }

    @Override
    public boolean runTask() {
        AnalysisJobExt je= (AnalysisJobExt) job.ext();
        PrintWriter pw= new PrintWriter(System.out);

        X10CAstPrinter.printTo((CAstEntity) je.get(AnalysisJobExt.CAST_JOBEXT_KEY), pw);
        pw.close();
        return true;
    }
}
