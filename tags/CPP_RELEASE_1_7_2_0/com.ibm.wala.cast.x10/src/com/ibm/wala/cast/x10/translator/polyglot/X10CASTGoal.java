/*
 * Created on Feb 23, 2006
 */
package com.ibm.wala.cast.x10.translator.polyglot;

import polyglot.frontend.CyclicDependencyException;
import polyglot.frontend.ExtensionInfo;
import polyglot.frontend.Job;
import polyglot.frontend.Pass;
import polyglot.frontend.Scheduler;
import polyglot.frontend.goals.AbstractGoal;
import polyglot.util.ErrorInfo;

import com.ibm.wala.types.ClassLoaderReference;

public class X10CASTGoal extends AbstractGoal {
    private ClassLoaderReference fSourceLoaderRef;

    public X10CASTGoal(Job job, ClassLoaderReference sourceLoader) {
	super(job);
	fSourceLoaderRef= sourceLoader;

	
	try {
	    Scheduler scheduler= job.extensionInfo().scheduler();

	    addPrerequisiteGoal(scheduler.TypeChecked(job), scheduler);
	} catch (CyclicDependencyException e) {
	    job.compiler().errorQueue().enqueue(ErrorInfo.INTERNAL_ERROR, "Cycle encountered in goal graph?");
	    throw new IllegalStateException(e.getMessage());
	}
	
    }

    public Pass createPass(ExtensionInfo extInfo) {
	return new X10CASTPass(this, job(),
		new X10toCAstTranslator(fSourceLoaderRef, extInfo.nodeFactory(),
			(X10ExtensionInfo) extInfo));
    }

    public String name() {
	return "<CAST goal for " + job().source().path() + ">";
    }
}
