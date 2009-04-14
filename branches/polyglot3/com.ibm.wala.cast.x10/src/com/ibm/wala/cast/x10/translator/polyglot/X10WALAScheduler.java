package com.ibm.wala.cast.x10.translator.polyglot;

import polyglot.ext.x10.ExtensionInfo.X10Scheduler;
import polyglot.frontend.Goal;
import polyglot.frontend.Job;

import com.ibm.wala.cast.java.translator.polyglot.AscriptionGoal;
import com.ibm.wala.cast.x10.analysis.AsyncAnalysisGoal;
import com.ibm.wala.types.ClassLoaderReference;

public class X10WALAScheduler extends X10Scheduler implements WALAScheduler {	
    public X10WALAScheduler(X10ExtensionInfo extInfo) {
    	super(extInfo);
    }

    public Goal AsyncsAnalyzed(Job job) {
        Goal g= intern(new AsyncAnalysisGoal(job));
        return g;
    }

    public Goal Ascription(Job job) {
        Goal g= intern(new AscriptionGoal(job));
        return g;
    }

    public Goal CAstDumped(Job job) {
        Goal g= intern(new CAstDumpedGoal(job));
        return g;
    }

    public Goal CAstGenerated(Job job) {
	ClassLoaderReference loaderRef= ((X10ExtensionInfo) job.extensionInfo()).getSourceLoaderReference();
	Goal g= intern(new X10CASTGoal(job, loaderRef));

	return g;
    }

    public Goal IRGenerated(Job job) {
	X10SourceLoaderImpl loader= ((X10ExtensionInfo) job.extensionInfo()).getSourceLoader();
	Goal g= intern(new X10IRGoal(job, loader));

	return g;
    }
}
