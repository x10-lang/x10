/*
 * Created on Feb 23, 2006
 */
package com.ibm.wala.cast.x10.translator.polyglot;

import polyglot.frontend.Job;
import polyglot.frontend.goals.Goal;

public interface WALAScheduler {

    public Goal CAstDumped(Job job);

    public Goal CAstGenerated(Job job);

    public Goal IRGenerated(Job job);

    public Goal AsyncsAnalyzed(Job job);
}
