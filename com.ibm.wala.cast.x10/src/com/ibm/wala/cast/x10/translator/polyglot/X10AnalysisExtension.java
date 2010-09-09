/*
 * Created on Feb 23, 2006
 */
package com.ibm.wala.cast.x10.translator.polyglot;

import polyglot.frontend.Goal;
import polyglot.frontend.Job;

public class X10AnalysisExtension extends X10ExtensionInfo {
    public Goal getCompileGoal(Job job) {
	return ((WALAScheduler) this.scheduler).AsyncsAnalyzed(job);
    }
}
