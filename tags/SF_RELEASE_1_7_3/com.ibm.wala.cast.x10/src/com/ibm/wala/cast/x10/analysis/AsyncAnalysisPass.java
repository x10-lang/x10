/*
 * Created on Feb 23, 2006
 */
package com.ibm.wala.cast.x10.analysis;

import com.ibm.wala.cast.tree.CAstEntity;
import com.ibm.wala.cast.util.CAstPrinter;

import polyglot.frontend.AbstractPass;
import polyglot.frontend.Job;
import polyglot.frontend.goals.Goal;

public class AsyncAnalysisPass extends AbstractPass {
    private final CAstEntity fEntity;

    public AsyncAnalysisPass(Goal goal, CAstEntity entity) {
	super(goal);
	fEntity= entity;
    }

    public boolean run() {
	AnalysisJobExt je= (AnalysisJobExt) goal().job().ext();

	// Odd: System.out appears to be closed now??? Use System.err...
	System.err.println("Hi Mom");
	return true;
    }
}
