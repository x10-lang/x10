/*
 * Created on Feb 23, 2006
 */
package com.ibm.wala.cast.x10.translator.polyglot;

import polyglot.ext.x10.ExtensionInfo.X10Scheduler;
import polyglot.frontend.Job;
import polyglot.frontend.goals.Goal;

import com.ibm.wala.cast.x10.analysis.AsyncAnalysisGoal;
import com.ibm.wala.types.ClassLoaderReference;

public class X10WALAScheduler extends X10Scheduler implements WALAScheduler {	
    public X10WALAScheduler(X10ExtensionInfo extInfo) {
    	super(extInfo);
    }

    public Goal CAstGenerated(Job job) {
	ClassLoaderReference loaderRef= ((X10ExtensionInfo) job.extensionInfo()).getSourceLoaderReference();
	Goal g= internGoal(new X10CASTGoal(job, loaderRef));

	return g;
    }

    public Goal IRGenerated(Job job) {
	X10SourceLoaderImpl loader= ((X10ExtensionInfo) job.extensionInfo()).getSourceLoader();
	Goal g= internGoal(new X10IRGoal(job, loader));

	return g;
    }

    public Goal CAstDumped(Job job) {
	Goal g= internGoal(new CAstDumpedGoal(job));
	return g;
    }

    public Goal AsyncsAnalyzed(Job job) {
	Goal g= internGoal(new AsyncAnalysisGoal(job));
	return g;
    }
}
