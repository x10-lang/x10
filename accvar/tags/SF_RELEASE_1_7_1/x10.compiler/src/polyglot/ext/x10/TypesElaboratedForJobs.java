/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/**
 * 
 */
package polyglot.ext.x10;

import polyglot.ext.x10.ExtensionInfo.X10Scheduler;
import polyglot.frontend.Job;
import polyglot.frontend.Scheduler;
import polyglot.frontend.goals.Barrier;
import polyglot.frontend.goals.Goal;
import polyglot.frontend.goals.TypesInitializedForCommandLine;

/**
 * @author VijaySaraswat
 *
 */
public class TypesElaboratedForJobs extends Barrier {

	
	/**
	 * @param name
	 * @param scheduler
	 */
	public TypesElaboratedForJobs( Scheduler scheduler) {
		this("TYPES_ELABORATED_BARRIER", scheduler);
		
	}
	public TypesElaboratedForJobs(String name, Scheduler scheduler) {
		super(name, scheduler);
		
	}

	/* (non-Javadoc)
	 * @see polyglot.frontend.goals.Barrier#goalForJob(polyglot.frontend.Job)
	 */
	@Override
	public Goal goalForJob(Job job) {
		
		return ((X10Scheduler)scheduler).TypeElaborated( job );
	}
	 public static Goal create(Scheduler scheduler) {
	        return scheduler.internGoal(new TypesElaboratedForJobs(scheduler));
	    }

	 
	  
}
