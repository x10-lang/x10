package com.ibm.wala.cast.x10.translator.polyglot;

import polyglot.frontend.ExtensionInfo;
import polyglot.frontend.Job;
import polyglot.frontend.Scheduler;
import polyglot.frontend.SourceGoal_c;

import com.ibm.wala.cast.x10.analysis.AnalysisJobExt;
import com.ibm.wala.types.ClassLoaderReference;

public class X10CASTGoal extends SourceGoal_c {
    private ClassLoaderReference fSourceLoaderRef;
    private X10toCAstTranslator fTranslator;

    public X10CASTGoal(Job job, ClassLoaderReference sourceLoader) {
	super(job);
	fSourceLoaderRef= sourceLoader;

	Scheduler scheduler= job.extensionInfo().scheduler();

	addPrereq(scheduler.TypeChecked(job));
    }

    @Override
    public boolean runTask() {
        ExtensionInfo extInfo = job.extensionInfo();

        fTranslator= new X10toCAstTranslator(fSourceLoaderRef, extInfo.nodeFactory(),
                (X10ExtensionInfo) extInfo);
        ((AnalysisJobExt) job.ext()).put(AnalysisJobExt.CAST_JOBEXT_KEY,
                fTranslator.translate(job.ast(), job.source().name()));
        return true;
    }

    public String name() {
	return "<CAST goal for " + job().source().path() + ">";
    }
}
