/*
 * Created on Oct 7, 2005
 */
package com.ibm.wala.cast.x10.translator.polyglot;

import java.util.ArrayList;

import polyglot.frontend.Job;
import polyglot.frontend.goals.Goal;

import com.ibm.wala.cast.java.translator.JavaCAst2IRTranslator;
import com.ibm.wala.cast.tree.CAstEntity;
import com.ibm.wala.cast.x10.analysis.AnalysisJobExt;

public class X10IRTranslatorExtension extends X10ExtensionInfo {
    // Doesn't matter which goal we get here, because they all have the same ExtensionInfo
    // which is what we're interested in
    @Deprecated private X10IRGoal g;
    private ArrayList<Job> jobList = new ArrayList<Job>();

    /**
     * getCompileGoal grabs all X10 related Jobs in order to look up the
     * associated CAstEntities at a later point.
     */
    public Goal getCompileGoal(Job job) {
    	X10IRGoal g = (X10IRGoal) ((WALAScheduler) this.scheduler).IRGenerated(job);
    	jobList.add(job);
    	if (this.g == null) 
    		this.g = g;
    	
	return g;
    }
    
    @Deprecated
    public JavaCAst2IRTranslator getJavaCAst2IRTranslator(String filepath) {
    	return g.getJavaCAst2IRTranslator();
    }
    
    /**
     * Return the CAstEntity for a given source file
     * 
     * @param filepath The full path to the source file
     * @return CAstEntity representing an AST for the file
     */
    public CAstEntity getCAstEntity(String filepath){
    	for (Object oj : jobList){
    		Job j = (Job) oj;
    		if (j.source().path().equals(filepath))
    			return (CAstEntity)((AnalysisJobExt)j.ext()).get(AnalysisJobExt.CAST_JOBEXT_KEY);
    	}
    	throw new IllegalArgumentException("CAstEntity not found for path "+filepath);
    }
}
