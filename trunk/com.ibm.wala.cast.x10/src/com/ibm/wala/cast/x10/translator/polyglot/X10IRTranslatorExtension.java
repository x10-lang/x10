/*
 * Created on Oct 7, 2005
 */
package com.ibm.domo.ast.x10.translator.polyglot;

import polyglot.ext.x10.ExtensionInfo;
import polyglot.frontend.Job;
import polyglot.frontend.Scheduler;
import polyglot.frontend.goals.Goal;

import com.ibm.domo.ast.java.translator.JavaCAst2IRTranslator;
import com.ibm.domo.ast.java.translator.polyglot.IRTranslatorExtension;
import com.ibm.domo.ast.java.translator.polyglot.PolyglotSourceLoaderImpl;

public class X10IRTranslatorExtension extends X10ExtensionInfo implements IRTranslatorExtension {
	// Doesn't matter which goal we get here, because they all have the same ExtensionInfo
	// which is what we're interested in
	private X10IRGoal g;
	
    public Goal getCompileGoal(Job job) {
    	X10IRGoal g = (X10IRGoal) ((DOMOScheduler) this.scheduler).IRGenerated(job);
    	if (this.g == null) 
    		this.g = g;
    	
	return g;
    }
    
    public JavaCAst2IRTranslator getJavaCAst2IRTranslator() {
    	return g.getJavaCAst2IRTranslator();
    }
}
