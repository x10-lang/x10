/*
 * Created on Oct 7, 2005
 */
package com.ibm.domo.ast.x10.translator.polyglot;

import polyglot.frontend.AbstractPass;
import polyglot.frontend.Job;
import polyglot.frontend.goals.Goal;

import com.ibm.domo.ast.x10.translator.X10ToIRTranslator;

/**
 * A Pass that creates DOMO IR for the given X10 compilation unit.
 * @author rfuhrer
 */
public class X10IRPass extends AbstractPass {
    private final Job fJob;
    private final X10ToIRTranslator fTranslator;

    public X10IRPass(Goal goal, Job job, X10ToIRTranslator translator) {
	super(goal);
	this.fJob= job;
	this.fTranslator= translator;
    }

    public boolean run() {
	fTranslator.translate(fJob.ast(), fJob.source().name());
	return true;
    }
}
