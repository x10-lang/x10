/*
 * Created on Feb 23, 2006
 */
package com.ibm.domo.ast.x10.translator.polyglot;

import polyglot.ext.jl.ExtensionInfo;
import polyglot.frontend.Job;
import polyglot.frontend.goals.Goal;

import com.ibm.domo.ast.java.loader.JavaSourceLoaderImpl;
import com.ibm.domo.ast.x10.analysis.AsyncAnalysisGoal;
import com.ibm.domo.types.ClassLoaderReference;

public class X10DOMOScheduler extends DOMOScheduler {
    public X10DOMOScheduler(ExtensionInfo extInfo) {
	super(extInfo);
    }

    public Goal CAstGenerated(Job job) {
	ClassLoaderReference loaderRef= ((X10ExtensionInfo) job.extensionInfo()).getSourceLoaderReference();
	Goal g= internGoal(new X10CASTGoal(job, loaderRef));

	return g;
    }

    public Goal IRGenerated(Job job) {
	JavaSourceLoaderImpl loader= ((X10ExtensionInfo) job.extensionInfo()).getSourceLoader();
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
