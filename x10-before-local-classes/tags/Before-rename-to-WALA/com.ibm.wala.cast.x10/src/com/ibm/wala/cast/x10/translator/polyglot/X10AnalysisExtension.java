/*
 * Created on Feb 23, 2006
 */
package com.ibm.domo.ast.x10.translator.polyglot;

import polyglot.frontend.Job;
import polyglot.frontend.goals.Goal;

public class X10AnalysisExtension extends X10ExtensionInfo {
    public Goal getCompileGoal(Job job) {
	return ((DOMOScheduler) this.scheduler).AsyncsAnalyzed(job);
    }
}
