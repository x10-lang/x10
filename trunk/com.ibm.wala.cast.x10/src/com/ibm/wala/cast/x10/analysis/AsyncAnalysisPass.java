/*
 * Created on Feb 23, 2006
 */
package com.ibm.domo.ast.x10.analysis;

import com.ibm.capa.ast.CAstEntity;
import com.ibm.capa.ast.impl.CAstPrinter;

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

	System.out.println("Hi Mom");
	return true;
    }
}
