// Licensed Materials - Property of IBM
// (C) Copyright IBM Corporation 2004,2005,2006. All Rights Reserved. 
// Note to U.S. Government Users Restricted Rights:  Use, duplication or disclosure restricted by GSA ADP  Schedule Contract with IBM Corp. 
//                                                                             
// --------------------------------------------------------------------------- 

package polyglot.ext.x10cpp;

import java.util.List;

import polyglot.ast.NodeFactory;
import polyglot.ext.x10.ast.X10NodeFactory_c;
import polyglot.ext.x10.visit.CheckNativeAnnotationsVisitor;
import polyglot.ext.x10.visit.StaticNestedClassRemover;
import polyglot.ext.x10.visit.X10InnerClassRemover;
import polyglot.ext.x10cpp.ast.X10CPPDelFactory_c;
import polyglot.ext.x10cpp.ast.X10CPPExtFactory_c;
import polyglot.ext.x10cpp.types.X10CPPSourceClassResolver;
import polyglot.ext.x10cpp.types.X10CPPTypeSystem_c;
import polyglot.ext.x10cpp.visit.X10CPPTranslator;
import polyglot.frontend.AllBarrierGoal;
import polyglot.frontend.BarrierGoal;
import polyglot.frontend.Compiler;
import polyglot.frontend.Globals;
import polyglot.frontend.Goal;
import polyglot.frontend.Job;
import polyglot.frontend.OutputGoal;
import polyglot.frontend.Scheduler;
import polyglot.frontend.VisitorGoal;
import polyglot.main.Options;
import polyglot.types.MemberClassResolver;
import polyglot.types.SemanticException;
import polyglot.types.TopLevelResolver;
import polyglot.types.TypeSystem;
import polyglot.util.ErrorQueue;
import polyglot.visit.PostCompiled;
import polyglot.util.InternalCompilerError;


/**
 * Extension information for x10 extension.
 * @author vj -- Adapted from the Polyglot2 ExtensionsInfo for X10 1.5
 */
public class ExtensionInfo extends polyglot.ext.x10.ExtensionInfo {


	public String compilerName() {
		return "x10c++";
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

	@Override
    protected void initTypeSystem() {
        // Inline from superclass, replacing SourceClassResolver
        try {
            TopLevelResolver r =
                new X10CPPSourceClassResolver(compiler, this, getOptions().constructFullClasspath(),
                                              getOptions().compile_command_line_only,
                                              getOptions().ignore_mod_times);


            // Resolver to handle lookups of member classes.
            if (true || TypeSystem.SERIALIZE_MEMBERS_WITH_CONTAINER) {
                MemberClassResolver mcr = new MemberClassResolver(ts, r, true);
                r = mcr;
            }

            ts.initialize(r, this);
        }
        catch (SemanticException e) {
            throw new InternalCompilerError(
                "Unable to initialize type system: " + e.getMessage(), e);
        }
    }

    // =================================
	// X10-specific goals and scheduling
	// =================================
	protected Scheduler createScheduler() {
		return new X10CPPScheduler(this);
	}

	public static class X10CPPScheduler extends polyglot.ext.x10.ExtensionInfo.X10Scheduler {
		protected X10CPPScheduler(ExtensionInfo extInfo) {
			super(extInfo);
		}
		@Override
		public Goal CheckNativeAnnotations(Job job) {
			TypeSystem ts = extInfo.typeSystem();
			NodeFactory nf = extInfo.nodeFactory();
			return new VisitorGoal("CheckNativeAnnotations", job, new CheckNativeAnnotationsVisitor(job, ts, nf, "c++")).intern(this);
		}
		public Goal InnerClassesRemoved(Job job) {
			TypeSystem ts = extInfo.typeSystem();
			NodeFactory nf = extInfo.nodeFactory();
			return new VisitorGoal("InnerClassRemover", job, new X10InnerClassRemover(job, ts, nf)).intern(this);
		}
		public Goal StaticNestedClassesRemoved(Job job) {
			TypeSystem ts = extInfo.typeSystem();
			NodeFactory nf = extInfo.nodeFactory();
			return new VisitorGoal("StaticNestedClassRemover", job, new StaticNestedClassRemover(job, ts, nf)).intern(this);
		}
		@Override
		public Goal CodeGenerated(Job job) {
			TypeSystem ts = extInfo.typeSystem();
			NodeFactory nf = extInfo.nodeFactory();
			return new OutputGoal(job, new X10CPPTranslator(job, ts, nf, extInfo.targetFactory())).intern(this);
		}
		@Override
		protected Goal PostCompiled() {
		    return new PostCompiled(extInfo) {
		        protected boolean invokePostCompiler(Options options, Compiler compiler, ErrorQueue eq) {
		            if (System.getProperty("x10.postcompile", "TRUE").equals("FALSE"))
		                return true;
		            return X10CPPTranslator.postCompile(options, compiler, eq);
		        }
		    }.intern(this);
		}
		public Goal NewCodeGenBarrier() {
		    if (Globals.Options().compile_command_line_only) {
		        return new BarrierGoal(commandLineJobs()) {
		            @Override
		            public Goal prereqForJob(Job job) {
		                return StaticNestedClassesRemoved(job);
		            }
		            public String name() { return "CodeGenBarrier"; }
		        };
		    }
		    else {
		        return new AllBarrierGoal("CodeGenBarrier", this) {
		            @Override
		            public Goal prereqForJob(Job job) {
		                return StaticNestedClassesRemoved(job);
		            }
		        };
		    }
		}
		@Override
        public List<Goal> goals(Job job) {
            List<Goal> res = super.goals(job);
            InnerClassesRemoved(job).addPrereq(Serialized(job));
            InnerClassesRemoved(job).addPrereq(CodeGenBarrier());
            StaticNestedClassesRemoved(job).addPrereq(InnerClassesRemoved(job));
            CodeGenerated(job).addPrereq(NewCodeGenBarrier());
            CodeGenerated(job).addPrereq(Desugarer(job));
            return res;
        }
	}

	// TODO: [IP] Override targetFactory() (rather, add createTargetFactory to polyglot)

	protected Options createOptions() {
		return new X10CPPCompilerOptions(this);
	}
}
// vim:tabstop=4:shiftwidth=4:expandtab
