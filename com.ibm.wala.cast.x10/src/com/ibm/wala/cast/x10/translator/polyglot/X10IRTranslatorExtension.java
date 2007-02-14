/*
 * Created on Oct 7, 2005
 */
package com.ibm.domo.ast.x10.translator.polyglot;

import polyglot.frontend.Job;
import polyglot.frontend.goals.Goal;

import com.ibm.wala.cast.java.translator.JavaCAst2IRTranslator;

public class X10IRTranslatorExtension extends X10ExtensionInfo {
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
