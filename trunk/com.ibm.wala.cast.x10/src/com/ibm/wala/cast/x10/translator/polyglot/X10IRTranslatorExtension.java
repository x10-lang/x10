/*
 * Created on Oct 7, 2005
 */
package com.ibm.domo.ast.x10.translator.polyglot;

import polyglot.ext.x10.ExtensionInfo;
import polyglot.frontend.Job;
import polyglot.frontend.Scheduler;
import polyglot.frontend.goals.Goal;

import com.ibm.domo.ast.java.translator.polyglot.IRTranslatorExtension;
import com.ibm.domo.ast.java.translator.polyglot.PolyglotSourceLoaderImpl;

public class X10IRTranslatorExtension extends X10ExtensionInfo implements IRTranslatorExtension {
    public Goal getCompileGoal(Job job) {
	return ((DOMOScheduler) this.scheduler).IRGenerated(job);
    }
}
