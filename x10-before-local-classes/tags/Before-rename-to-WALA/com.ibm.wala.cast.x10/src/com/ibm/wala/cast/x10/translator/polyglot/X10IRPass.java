/*
 * Created on Oct 7, 2005
 */
package com.ibm.domo.ast.x10.translator.polyglot;

import polyglot.frontend.AbstractPass;
import polyglot.frontend.Job;
import polyglot.frontend.goals.Goal;

import com.ibm.domo.ast.x10.analysis.AnalysisJobExt;
import com.ibm.domo.ast.x10.translator.X10ToIRTranslator;
import com.ibm.wala.cast.java.loader.JavaSourceLoaderImpl;
import com.ibm.wala.cast.tree.CAstEntity;

/**
 * A Pass that creates DOMO IR for the given X10 compilation unit.
 * @author rfuhrer
 */
public class X10IRPass extends AbstractPass {
    private final Job fJob;
    private final X10SourceLoaderImpl fLoader;
	private X10CAst2IRTranslator fTranslator;
    

    public X10IRPass(Goal goal, Job job, X10SourceLoaderImpl loader) {
	super(goal);
	this.fJob= job;
	fLoader= loader;

	CAstEntity entity= (CAstEntity) ((AnalysisJobExt) fJob.ext()).get(AnalysisJobExt.CAST_JOBEXT_KEY);

	fTranslator = new X10CAst2IRTranslator(entity, fLoader);
    }

    public boolean run() {
	fTranslator.translate();
	return true;
    }

	public X10CAst2IRTranslator getTranslator() {
		return fTranslator;
	}
}
