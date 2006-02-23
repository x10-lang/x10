/*
 * Created on Feb 23, 2006
 */
package com.ibm.domo.ast.x10.translator.polyglot;

import com.ibm.domo.ast.java.ipa.callgraph.JavaSourceAnalysisScope;
import com.ibm.domo.ast.java.translator.polyglot.PolyglotSourceLoaderImpl;
import com.ibm.domo.ast.x10.analysis.AnalysisJobExt;
import com.ibm.domo.types.ClassLoaderReference;

import polyglot.ext.x10.ExtensionInfo;
import polyglot.frontend.Job;
import polyglot.frontend.JobExt;
import polyglot.frontend.Scheduler;
import polyglot.frontend.goals.Goal;

public class X10AnalysisExtension extends ExtensionInfo {
    protected PolyglotSourceLoaderImpl fSourceLoader;

    public void setSourceLoader(PolyglotSourceLoaderImpl sourceLoader) {
	fSourceLoader= sourceLoader;
    }

    public Goal getCompileGoal(Job job) {
	return ((DOMOScheduler) this.scheduler).AsyncsAnalyzed(job);
    }

    public JobExt jobExt() {
	return new AnalysisJobExt();
    }

    protected Scheduler createScheduler() {
	return new X10DOMOScheduler(this);
    }

    public PolyglotSourceLoaderImpl getSourceLoader() {
	return fSourceLoader;
    }

    public ClassLoaderReference getSourceLoaderReference() {
	return JavaSourceAnalysisScope.SOURCE_REF;
    }
}
