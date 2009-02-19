// Licensed Materials - Property of IBM
// (C) Copyright IBM Corporation 2004,2005,2006. All Rights Reserved. 
// Note to U.S. Government Users Restricted Rights:  Use, duplication or disclosure restricted by GSA ADP  Schedule Contract with IBM Corp. 
//                                                                             
// --------------------------------------------------------------------------- 

package polyglot.ext.x10cpp;

import polyglot.ast.NodeFactory;
import polyglot.ext.x10.ast.X10NodeFactory_c;
import polyglot.ext.x10.query.QueryEngine;
import polyglot.ext.x10.visit.CheckNativeAnnotationsVisitor;
import polyglot.ext.x10cpp.ast.X10CPPDelFactory_c;
import polyglot.ext.x10cpp.ast.X10CPPExtFactory_c;
import polyglot.ext.x10cpp.types.X10CPPTypeSystem_c;
import polyglot.ext.x10cpp.visit.X10CPPTranslator;
import polyglot.frontend.Goal;
import polyglot.frontend.Job;
import polyglot.frontend.OutputGoal;
import polyglot.frontend.Scheduler;
import polyglot.frontend.VisitorGoal;
import polyglot.main.Options;
import polyglot.types.TypeSystem;


/**
 * Extension information for x10 extension.
 * @author vj -- Adapted from the Polyglot2 ExtensionsInfo for X10 1.5
 */
public class ExtensionInfo extends polyglot.ext.x10.ExtensionInfo {


	public String compilerName() {
		return "x10cpp";
	}

	public polyglot.main.Version version() {
		return new Version();
	}

	protected NodeFactory createNodeFactory() {
		return new X10NodeFactory_c(this, new X10CPPExtFactory_c(), new X10CPPDelFactory_c()) { };
	}

	protected TypeSystem createTypeSystem() {
		return new X10CPPTypeSystem_c();
	}

	// =================================
	// X10-specific goals and scheduling
	// =================================
	protected Scheduler createScheduler() {
		return new X10CPPScheduler(this);
	}

	public static class X10CPPScheduler extends polyglot.ext.x10.ExtensionInfo.X10Scheduler {
		X10CPPScheduler(ExtensionInfo extInfo) {
			super(extInfo);
		}
		@Override
		public Goal CheckNativeAnnotations(Job job) {
			TypeSystem ts = extInfo.typeSystem();
			NodeFactory nf = extInfo.nodeFactory();
			return new VisitorGoal("CheckNativeAnnotations", job, new CheckNativeAnnotationsVisitor(job, ts, nf, "c++")).intern(this);
		}

		@Override
		public Goal CodeGenerated(Job job) {
			TypeSystem ts = extInfo.typeSystem();
			NodeFactory nf = extInfo.nodeFactory();
			return new OutputGoal(job, new X10CPPTranslator(job, ts, nf, 
						extInfo.targetFactory()));
		}
	}

	// TODO: [IP] Override targetFactory() (rather, add createTargetFactory to polyglot)

	protected Options createOptions() {
		return new X10CPPCompilerOptions(this);
	}
}
