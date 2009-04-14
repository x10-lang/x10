package com.ibm.wala.cast.x10.translator.polyglot;

import polyglot.frontend.Job;
import polyglot.frontend.SourceGoal_c;

import com.ibm.wala.cast.java.translator.JavaCAst2IRTranslator;
import com.ibm.wala.cast.tree.CAstEntity;
import com.ibm.wala.cast.x10.analysis.AnalysisJobExt;

public class X10IRGoal extends SourceGoal_c /* PORT1.7 removed 'implements EndGoal' */ {
    private X10SourceLoaderImpl fSourceLoader;
    private X10CAst2IRTranslator fTranslator;
    
    public X10IRGoal(Job job, X10SourceLoaderImpl sourceLoader) {
	super(job);
	fSourceLoader = sourceLoader;

	WALAScheduler scheduler= (WALAScheduler) job.extensionInfo().scheduler();
	
	addPrereq(scheduler.CAstGenerated(job));
    }

    @Override
    public boolean runTask() {
        CAstEntity entity= (CAstEntity) ((AnalysisJobExt) job.ext()).get(AnalysisJobExt.CAST_JOBEXT_KEY);

        fTranslator = new X10CAst2IRTranslator(entity, fSourceLoader);
        fTranslator.translate();
        return true;
    }

    public String name() {
	return "<WALA X10IR goal for " + job().source().path() + ">";
    }
    
    public JavaCAst2IRTranslator getJavaCAst2IRTranslator(){
    	return fTranslator.getCAst2IRTranslator();
    }
}
