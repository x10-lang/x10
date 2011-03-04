/*
 * Created on Oct 7, 2005
 */
package com.ibm.domo.ast.x10.translator.polyglot;

import polyglot.frontend.CyclicDependencyException;
import polyglot.frontend.ExtensionInfo;
import polyglot.frontend.Job;
import polyglot.frontend.Pass;
import polyglot.frontend.Scheduler;
import polyglot.frontend.goals.AbstractGoal;
import polyglot.frontend.goals.EndGoal;
import polyglot.util.ErrorInfo;

import com.ibm.domo.ast.x10.translator.X10ToIRTranslator;
import com.ibm.wala.cast.java.loader.JavaSourceLoaderImpl;
import com.ibm.wala.cast.java.translator.JavaCAst2IRTranslator;

public class X10IRGoal extends AbstractGoal implements EndGoal {
    private X10SourceLoaderImpl fSourceLoader;
    private X10CAst2IRTranslator fTranslator;
    
    public X10IRGoal(Job job, X10SourceLoaderImpl sourceLoader) {
	super(job);
	fSourceLoader = sourceLoader;

	try {
	    DOMOScheduler scheduler= (DOMOScheduler) job.extensionInfo().scheduler();

	    addPrerequisiteGoal(scheduler.CAstGenerated(job), scheduler);
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
	return "<DOMO IR goal for " + job().source().path() + ">";
    }
    
    public JavaCAst2IRTranslator getJavaCAst2IRTranslator(){
    	return fTranslator.getCAst2IRTranslator();
    }
}
