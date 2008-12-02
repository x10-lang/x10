/*
 * Created on Oct 7, 2005
 */
package com.ibm.wala.cast.x10.translator.polyglot;

import polyglot.frontend.CyclicDependencyException;
import polyglot.frontend.ExtensionInfo;
import polyglot.frontend.Job;
import polyglot.frontend.Pass;
import polyglot.frontend.Scheduler;
import polyglot.frontend.goals.AbstractGoal;
import polyglot.frontend.goals.EndGoal;
import polyglot.util.ErrorInfo;

import com.ibm.wala.cast.java.translator.JavaCAst2IRTranslator;

public class X10IRGoal extends AbstractGoal implements EndGoal {
    private X10SourceLoaderImpl fSourceLoader;
    private X10CAst2IRTranslator fTranslator;
    
    public X10IRGoal(Job job, X10SourceLoaderImpl sourceLoader) {
	super(job);
	fSourceLoader = sourceLoader;

	try {
	    WALAScheduler scheduler= (WALAScheduler) job.extensionInfo().scheduler();
	
	    addPrerequisiteGoal(scheduler.CAstGenerated(job), (Scheduler)scheduler);

	} catch (CyclicDependencyException e) {
	    job.compiler().errorQueue().enqueue(ErrorInfo.INTERNAL_ERROR, "Cycle encountered in goal graph?");
	    throw new IllegalStateException(e.getMessage());
	}
    }

    public Pass createPass(ExtensionInfo extInfo) {
    	X10IRPass result = new X10IRPass(this, job(), fSourceLoader);
    	fTranslator = result.getTranslator();
    	return result;
    }

    public String name() {
	return "<WALA IR goal for " + job().source().path() + ">";
    }
    
    public JavaCAst2IRTranslator getJavaCAst2IRTranslator(){
    	return fTranslator.getCAst2IRTranslator();
    }
}
