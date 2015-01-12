/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

package x10cuda;

import java.util.ArrayList;
import java.util.List;

import polyglot.ast.NodeFactory;
import polyglot.frontend.Compiler;
import polyglot.frontend.Goal;
import polyglot.frontend.Job;
import polyglot.frontend.Scheduler;
import polyglot.frontend.VisitorGoal;
import polyglot.main.Options;
import polyglot.types.TypeSystem;
import polyglot.util.ErrorQueue;
import polyglot.visit.PostCompiled;
import x10.ast.X10NodeFactory_c;
import x10.optimizations.Optimizer;
import x10.visit.InstanceInvariantChecker;
import x10.visit.PositionInvariantChecker;
import x10cpp.X10CPPCompilerOptions;
import x10cpp.ExtensionInfo.X10CPPScheduler;
import x10cpp.ast.X10CPPExtFactory_c;
import x10cpp.visit.X10CPPTranslator;
import x10cuda.ast.X10CUDADelFactory_c;
import x10cuda.types.X10CUDATypeSystem_c;
import x10cuda.visit.CUDACodeGenerator;
import x10cuda.visit.CUDAPatternMatcher;


/**
 * Extension information for x10 extension.
 * @author vj -- Adapted from the Polyglot2 ExtensionsInfo for X10 1.5
 */
public class ExtensionInfo extends x10cpp.ExtensionInfo {

    @Override
	protected NodeFactory createNodeFactory() {
		return new X10NodeFactory_c(this, new X10CPPExtFactory_c(), new X10CUDADelFactory_c()) { };
	}

    @Override
    protected TypeSystem createTypeSystem() {
        return new X10CUDATypeSystem_c(this);
    }

    @Override
    protected Scheduler createScheduler() {
        return new X10CUDAScheduler(this);
    }

    public static class X10CUDAScheduler extends X10CPPScheduler {
    	private ExtensionInfo extInfo;
    	
        protected X10CUDAScheduler(ExtensionInfo extInfo) {
            super(extInfo);
            this.extInfo = extInfo;
        }
        
        @Override
		public List<Goal> goals(Job job) {
            List<Goal> goals = super.goals(job);
			ArrayList<Goal> newGoals = new ArrayList<Goal>();
            for (Goal g : goals) {
                newGoals.add(g);
                if (g==TypeChecked(job)) {
                    newGoals.add(new VisitorGoal("CUDAPatternMatcher", job, new CUDAPatternMatcher(job, extInfo.ts, extInfo.nf)).intern(this));
                }
            }
            goals = newGoals;
            return goals;
		}

        @Override
		protected Goal PostCompiled() {
            return new PostCompiled(extInfo) {
                private static final long serialVersionUID = -2238021480659240967L;
                @Override
                protected boolean invokePostCompiler(Options options, Compiler compiler, ErrorQueue eq) {
                    if (System.getProperty("x10.postcompile", "TRUE").equals("FALSE"))
                        return true;
                    // use & to avoid short-circuit
                    X10CPPCompilerOptions opts = (X10CPPCompilerOptions)options;
                    return CUDACodeGenerator.postCompile(opts, compiler, eq) & X10CPPTranslator.postCompile(opts, compiler, eq);
                }
            }.intern(this);
        }
    }
}

// vim:tabstop=4:shiftwidth=4:expandtab