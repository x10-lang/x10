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

import com.ibm.domo.ast.java.loader.JavaSourceLoaderImpl;
import com.ibm.domo.ast.x10.translator.X10ToIRTranslator;

public class X10IRGoal extends AbstractGoal implements EndGoal {
    private JavaSourceLoaderImpl fSourceLoader;

    public X10IRGoal(Job job, JavaSourceLoaderImpl sourceLoader) {
	super(job);
	fSourceLoader = sourceLoader;

	try {
	    Scheduler scheduler= job.extensionInfo().scheduler();

	    addPrerequisiteGoal(scheduler.TypeChecked(job), scheduler);
	} catch (CyclicDependencyException e) {
	    job.compiler().errorQueue().enqueue(ErrorInfo.INTERNAL_ERROR, "Cycle encountered in goal graph?");
	    throw new IllegalStateException(e.getMessage());
	}
    }

    public Pass createPass(ExtensionInfo extInfo) {
	return new X10IRPass(this, job(),
	  new X10ToIRTranslator(
	    new X10toCAstTranslator(
	      fSourceLoader.getReference(),
	      extInfo.nodeFactory(),
	      extInfo.typeSystem()),
	    fSourceLoader));
    }

    public String name() {
	return "<DOMO IR goal for " + job().source().path() + ">";
    }
}
