/*
 * Created on Feb 23, 2006
 */
package com.ibm.domo.ast.x10.translator.polyglot;

import polyglot.ext.jl.JLScheduler;
import polyglot.ext.jl.ExtensionInfo;
import polyglot.frontend.Job;
import polyglot.frontend.Scheduler;
import polyglot.frontend.goals.Goal;
import polyglot.types.FieldInstance;
import polyglot.types.ParsedClassType;

public abstract class DOMOScheduler extends JLScheduler {

    public DOMOScheduler(ExtensionInfo extInfo) {
	super(extInfo);
    }

    public abstract Goal CAstDumped(Job job);

    public abstract Goal CAstGenerated(Job job);

    public abstract Goal IRGenerated(Job job);

    public abstract Goal AsyncsAnalyzed(Job job);
}
