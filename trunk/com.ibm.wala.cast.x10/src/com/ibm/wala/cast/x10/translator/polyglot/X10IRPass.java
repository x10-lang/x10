/*
 * Created on Oct 7, 2005
 */
package com.ibm.domo.ast.x10.translator.polyglot;

import polyglot.frontend.AbstractPass;
import polyglot.frontend.Job;
import polyglot.frontend.goals.Goal;

import com.ibm.capa.ast.CAstEntity;
import com.ibm.domo.ast.java.loader.JavaSourceLoaderImpl;
import com.ibm.domo.ast.x10.analysis.AnalysisJobExt;
import com.ibm.domo.ast.x10.translator.X10ToIRTranslator;

/**
 * A Pass that creates DOMO IR for the given X10 compilation unit.
 * @author rfuhrer
 */
public class X10IRPass extends AbstractPass {
    private final Job fJob;
    private final JavaSourceLoaderImpl fLoader;

    public X10IRPass(Goal goal, Job job, JavaSourceLoaderImpl loader) {
	super(goal);
	this.fJob= job;
	fLoader= loader;
    }

    public boolean run() {
	CAstEntity entity= (CAstEntity) ((AnalysisJobExt) fJob.ext()).get(AnalysisJobExt.CAST_JOBEXT_KEY);

	X10CAst2IRTranslator translator= new X10CAst2IRTranslator(entity, fLoader);
	translator.translate();
	return true;
    }
}
