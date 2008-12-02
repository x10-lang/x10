/*
 * Created on Feb 23, 2006
 */
package com.ibm.wala.cast.x10.translator.polyglot;

import polyglot.frontend.ExtensionInfo;
import polyglot.frontend.JLScheduler;
import polyglot.frontend.Job;
import polyglot.frontend.goals.Goal;

public abstract class WALAScheduler extends JLScheduler {

    public WALAScheduler(ExtensionInfo extInfo) {
	super(extInfo);
    }

    public abstract Goal CAstDumped(Job job);

    public abstract Goal CAstGenerated(Job job);

    public abstract Goal IRGenerated(Job job);

    public abstract Goal AsyncsAnalyzed(Job job);
}
