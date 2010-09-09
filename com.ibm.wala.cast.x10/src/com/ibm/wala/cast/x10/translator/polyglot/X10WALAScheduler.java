package com.ibm.wala.cast.x10.translator.polyglot;

import java.util.ArrayList;

import java.util.List;

import polyglot.ast.NodeFactory;
import polyglot.frontend.Goal;
import polyglot.frontend.Job;
import polyglot.frontend.SourceGoal_c;
import polyglot.frontend.VisitorGoal;
import polyglot.types.TypeSystem;
import x10.ExtensionInfo.X10Scheduler;
import x10.visit.SharedBoxer;

import com.ibm.wala.cast.java.translator.polyglot.AscriptionGoal;
import com.ibm.wala.cast.x10.analysis.AsyncAnalysisGoal;
import com.ibm.wala.types.ClassLoaderReference;

public class X10WALAScheduler extends X10Scheduler implements WALAScheduler {	
    public X10WALAScheduler(X10ExtensionInfo extInfo) {
    	super(extInfo);
    }

    public Goal AsyncsAnalyzed(Job job) {
        Goal g= intern(new AsyncAnalysisGoal(job));
        return g;
    }

    public Goal Ascription(Job job) {
        Goal g= intern(new AscriptionGoal(job));
        return g;
    }

    public Goal CAstDumped(Job job) {
        Goal g= intern(new CAstDumpedGoal(job));
        return g;
    }

    public Goal CAstGenerated(Job job) {
	ClassLoaderReference loaderRef= ((X10ExtensionInfo) job.extensionInfo()).getSourceLoaderReference();
	Goal g= intern(new X10CASTGoal(job, loaderRef));

	return g;
    }

    public Goal IRGenerated(Job job) {
	X10SourceLoaderImpl loader= ((X10ExtensionInfo) job.extensionInfo()).getSourceLoader();
	Goal g= intern(new X10IRGoal(job, loader));

	return g;
    }
    
    public Goal SharedBoxer(Job job) {
        TypeSystem ts = extInfo.typeSystem();
        NodeFactory nf = extInfo.nodeFactory();
        return new VisitorGoal("SharedBoxed", job, new SharedBoxer(job, ts, nf)).intern(this);
    }

    @Override
    public List<Goal> goals(Job job) {
        List<Goal> goals = new ArrayList<Goal>();

        goals.add(Parsed(job));
        goals.add(TypesInitialized(job));
        goals.add(ImportTableInitialized(job));

//      goals.add(CastRewritten(job)); // RMF This pass is apparently now a no-op.

        goals.add(PropagateAnnotations(job));
//      goals.add(LoadJobPlugins(job)); // RMF Do we need plugins for analysis?
//      goals.add(RegisterPlugins(job));
        
        goals.add(PreTypeCheck(job));
//      goals.add(TypesInitializedForCommandLine()); // RMF Do we need this for analysis?
        goals.add(TypeChecked(job));
        goals.add(SharedBoxer(job));
        goals.add(IRGenerated(job));
        goals.add(End(job));
        return goals;
    }
}
