/*
 * Created on Feb 23, 2006
 */
package com.ibm.domo.ast.x10.translator.polyglot;

import polyglot.frontend.AbstractPass;
import polyglot.frontend.Job;
import polyglot.frontend.goals.Goal;

import com.ibm.capa.ast.CAstEntity;
import com.ibm.capa.impl.debug.Assertions;
import com.ibm.domo.ast.x10.analysis.AnalysisJobExt;
import com.ibm.domo.ast.x10.translator.X10ToIRTranslator;

public class X10CASTPass extends AbstractPass {
    private final Job fJob;
    private final X10toCAstTranslator fTranslator;

    public X10CASTPass(Goal goal, Job job, X10toCAstTranslator translator) {
	super(goal);
	this.fJob= job;
	this.fTranslator= translator;
    }

    public boolean run() {
	((AnalysisJobExt) fJob.ext()).put(AnalysisJobExt.CAST_JOBEXT_KEY,
		fTranslator.translate(fJob.ast(), fJob.source().name()));
	return true;
    }
}
