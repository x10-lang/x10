// Licensed Materials - Property of IBM
// (C) Copyright IBM Corporation 2004,2005,2006. All Rights Reserved. 
// Note to U.S. Government Users Restricted Rights:  Use, duplication or disclosure restricted by GSA ADP  Schedule Contract with IBM Corp. 
//                                                                             
// --------------------------------------------------------------------------- 

package polyglot.ext.x10cpp;

import polyglot.ast.NodeFactory;
import polyglot.ext.x10cpp.visit.X10CPPTranslator;
import polyglot.frontend.Goal;
import polyglot.frontend.Job;
import polyglot.frontend.OutputGoal;
import polyglot.frontend.Scheduler;
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

	protected Scheduler createScheduler() {
		return new X10CPPScheduler(this);
	}

	public static class X10CPPScheduler extends polyglot.ext.x10.ExtensionInfo.X10Scheduler {
		X10CPPScheduler(ExtensionInfo extInfo) {
			super(extInfo);
		}
		@Override
		public Goal CodeGenerated(Job job) {
			TypeSystem ts = extInfo.typeSystem();
			NodeFactory nf = extInfo.nodeFactory();
			return new OutputGoal(job, new X10CPPTranslator(job, ts, nf, extInfo.targetFactory()));
		}
	}

	// TODO: [IP] Override targetFactory() (rather, add createTargetFactory to polyglot)

	protected Options createOptions() {
		return new X10CPPCompilerOptions(this);
	}
}
