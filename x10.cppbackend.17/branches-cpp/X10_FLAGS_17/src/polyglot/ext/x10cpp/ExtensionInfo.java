// Licensed Materials - Property of IBM
// (C) Copyright IBM Corporation 2004,2005,2006. All Rights Reserved. 
// Note to U.S. Government Users Restricted Rights:  Use, duplication or disclosure restricted by GSA ADP  Schedule Contract with IBM Corp. 
//                                                                             
// --------------------------------------------------------------------------- 

package polyglot.ext.x10cpp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import polyglot.ast.NodeFactory;
import polyglot.ext.x10.ast.X10NodeFactory_c;
import polyglot.ext.x10.query.QueryEngine;
import polyglot.ext.x10cpp.ast.X10CPPDelFactory_c;
import polyglot.ext.x10cpp.ast.X10CPPExtFactory_c;
import polyglot.ext.x10cpp.types.X10CPPTypeSystem_c;
import polyglot.ext.x10cpp.visit.X10CPPTranslator;
import polyglot.frontend.Compiler;
import polyglot.frontend.Job;
import polyglot.frontend.OutputPass;
import polyglot.frontend.Pass;
import polyglot.frontend.Scheduler;
import polyglot.frontend.goals.CodeGenerated;
import polyglot.frontend.goals.Goal;
import polyglot.main.Options;
import polyglot.types.TypeSystem;

/**
 * Extension information for x10 extension.
 */
public class ExtensionInfo extends polyglot.ext.x10.ExtensionInfo {
	public polyglot.main.Version version() {
		return new Version();
	}

	public String compilerName() {
		return "x10c++";
	}

	protected NodeFactory createNodeFactory() {
		return new X10NodeFactory_c(this, new X10CPPExtFactory_c(), new X10CPPDelFactory_c()) { };
	}

	protected TypeSystem createTypeSystem() {
		return new X10CPPTypeSystem_c();
	}

	public void initCompiler(Compiler compiler) {
		super.initCompiler(compiler);
		QueryEngine.init(this);
	}

	// =================================
	// X10-specific goals and scheduling
	// =================================
	protected Scheduler createScheduler() {
		return new X10Scheduler(this);
	}

	public static class X10Scheduler extends polyglot.ext.x10.ExtensionInfo.X10Scheduler {
		X10Scheduler(ExtensionInfo extInfo) {
			super(extInfo);
		}
		public Goal CodeGenerated(final Job job) {
			return X10CodeGenerated.create(this, job);
		}
		public Goal X10Casted(final Job job) {
			return new EmptyGoal(job) {
				{ setState(REACHED); }
		    	public Collection prerequisiteGoals(Scheduler scheduler) {
		    		List<Goal> l = new ArrayList<Goal>();
		    		l.add(scheduler.TypeChecked(job));
		    		l.add(scheduler.ConstantsChecked(job));
		    		l.add(((X10Scheduler)scheduler).X10Boxed(job));
		    		l.addAll(super.prerequisiteGoals(scheduler));
		    		return l;
		    	}
			};
		}
	}

	static class X10CodeGenerated extends CodeGenerated {
		public static Goal create(Scheduler scheduler, Job job) {
			return scheduler.internGoal(new X10CodeGenerated(job));
		}
		private X10CodeGenerated(Job job) {
			super(job);
		}
		public Collection prerequisiteGoals(Scheduler scheduler) {
			X10Scheduler x10Sched= (X10Scheduler) scheduler;
			List<Goal> l = new ArrayList<Goal>();
			l.add(x10Sched.X10Boxed(job));
			l.add(x10Sched.X10Casted(job));    		
			//	l.add(x10Sched.X10ExprFlattened(job));
			l.add(x10Sched.TypeElaborated(job));
			l.add(x10Sched.X10Expanded(job));
			l.add(x10Sched.PropertyAssignmentsChecked(job));
			l.addAll(super.prerequisiteGoals(scheduler));
			return l;
		}
		public Pass createPass(polyglot.frontend.ExtensionInfo extInfo) {
			TypeSystem ts = extInfo.typeSystem();
			NodeFactory nf = extInfo.nodeFactory();
			return new OutputPass(this, new X10CPPTranslator(job(), ts, nf,
						extInfo.targetFactory()));
		}
	}

	// TODO: [IP] Override targetFactory() (rather, add createTargetFactory to polyglot)

	protected Options createOptions() {
		return new X10CPPCompilerOptions(this);
	}
}
