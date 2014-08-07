/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10c;

import java.util.ArrayList;
import java.util.List;

import polyglot.ast.NodeFactory;
import polyglot.frontend.Compiler;
import polyglot.frontend.Goal;
import polyglot.frontend.Job;
import polyglot.frontend.Scheduler;
import polyglot.frontend.SourceGoal_c;
import polyglot.main.Options;
import polyglot.types.TypeSystem;
import polyglot.util.ErrorQueue;
import polyglot.visit.PostCompiled;
import x10.X10CompilerOptions;
import x10.ExtensionInfo.X10Scheduler.ValidatingVisitorGoal;
import x10.visit.X10Translator;
import x10c.ast.X10CNodeFactory_c;
import x10c.types.X10CTypeSystem_c;
import x10c.visit.AsyncInitializer;
import x10c.visit.BoxingDetector;
import x10c.visit.CastRemover;
import x10c.visit.ClosureRemover;
import x10c.visit.ClosuresToStaticMethods;
import x10c.visit.Desugarer;
import x10c.visit.ExpressionFlattenerForAtExpr;
import x10c.visit.InlineHelper;
import x10c.visit.JavaCaster;
import x10c.visit.NativeClassVisitor;
import x10c.visit.RailInLoopOptimizer;
import x10c.visit.StaticInitializer;
import x10c.visit.VarsBoxer;

public class ExtensionInfo extends x10.ExtensionInfo {
    
    public boolean isNativeX10() { return false; }
    public boolean isManagedX10() { return true; }
    
    @Override
    protected Scheduler createScheduler() {
        return new X10CScheduler(this);
    }

    @Override
    protected NodeFactory createNodeFactory() {
        return new X10CNodeFactory_c(this);
    }

    @Override
    protected TypeSystem createTypeSystem() {
        return new X10CTypeSystem_c(this);
    }

    @Override
    protected X10CCompilerOptions createOptions() {
        return new X10CCompilerOptions(this);
    }

    @Override
    public X10CCompilerOptions getOptions() {
        return (X10CCompilerOptions) super.getOptions();
    }

    public static class X10CScheduler extends X10Scheduler {
        public X10CScheduler(ExtensionInfo extInfo) {
            super(extInfo);
        }

        @Override
        public ExtensionInfo extensionInfo() {
            return (ExtensionInfo) this.extInfo;
        }

        @Override
        public List<Goal> goals(Job job) {
            List<Goal> superGoals = super.goals(job);
            ArrayList<Goal> goals = new ArrayList<Goal>(superGoals.size()+10);
            for (Goal g : superGoals) {
                if (g == Desugarer(job)) {
                    goals.add(ExpressionFlattenerForAtExpr(job));
                    goals.add(VarsBoxer(job));
                }
                if (g == CodeGenerated(job)) {
                    goals.add(JavaCodeGenStart(job));
//                    goals.add(ClosuresToStaticMethods(job));
                    goals.add(StaticInitializer(job));
                    goals.add(AsyncInitializer(job));
                    goals.add(ClosureRemoved(job));
                    goals.add(RailInLoopOptimizer(job));
                    goals.add(CastsRemoved(job));
                    goals.add(JavaCaster(job));
                    goals.add(InlineHelped(job));
                    goals.add(BoxingDetector(job));
                    goals.add(PreCodegenASTInvariantChecker(job));
                }
                goals.add(g);
            }
            return goals;
        }

        @Override
        protected Goal codegenPrereq(Job job) {
            return InlineHelped(job);
        }

        @Override
        public Goal Desugarer(Job job) {
            TypeSystem ts = extInfo.typeSystem();
            NodeFactory nf = extInfo.nodeFactory();
            return new ValidatingVisitorGoal("Desugarer", job, new Desugarer(job, ts, nf)).intern(this);
        }

        @Override
        public Goal NativeClassVisitor(Job job) {
            TypeSystem ts = extInfo.typeSystem();
            NodeFactory nf = extInfo.nodeFactory();
            return new ValidatingVisitorGoal("NativeClassVisitor", job, new NativeClassVisitor(job, ts, nf, nativeAnnotationLanguage())).intern(this);
        }

        @Override
        protected Goal PostCompiled() {
            return new PostCompiled(extInfo) {
                private static final long serialVersionUID = 1L;
                @Override
                protected boolean invokePostCompiler(Options options, Compiler compiler, ErrorQueue eq) {
                    if (System.getProperty("x10.postcompile", "TRUE").equals("FALSE"))
                        return true;
                    
                    // Ensure that there is no post compilation for ONLY_TYPE_CHECKING jobs
                    X10CompilerOptions opts = extensionInfo().getOptions();
                    if (opts.x10_config.ONLY_TYPE_CHECKING) return true;
                    
                    return X10Translator.postCompile((X10CompilerOptions)options, compiler, eq);
                }
            }.intern(this);
        }

        public Goal JavaCodeGenStart(Job job) {
            Goal cg = new SourceGoal_c("JavaCodeGenStart", job) { // Is this still necessary?
                private static final long serialVersionUID = 1L;
                @Override
                public boolean runTask() { return true; }
            };
            return cg.intern(this);
        }

        public Goal ClosuresToStaticMethods(Job job) {
            TypeSystem ts = extInfo.typeSystem();
            NodeFactory nf = extInfo.nodeFactory();
            return new ValidatingVisitorGoal("ClosuresToStaticMethods", job, new ClosuresToStaticMethods(job, ts, nf)).intern(this);
        }

        public Goal ClosureRemoved(Job job) {
            TypeSystem ts = extInfo.typeSystem();
            NodeFactory nf = extInfo.nodeFactory();
            return new ValidatingVisitorGoal("ClosureRemoved", job, new ClosureRemover(job, ts, nf)).intern(this);
        }
        
        private Goal RailInLoopOptimizer(Job job) {
            TypeSystem ts = extInfo.typeSystem();
            NodeFactory nf = extInfo.nodeFactory();
            return new ValidatingVisitorGoal("RailInLoopOptimized", job, new RailInLoopOptimizer(job, ts, nf)).intern(this);
        }
        
        private Goal JavaCaster(Job job) {
            TypeSystem ts = extInfo.typeSystem();
            NodeFactory nf = extInfo.nodeFactory();
            return new ValidatingVisitorGoal("JavaCasted", job, new JavaCaster(job, ts, nf)).intern(this);
        }

        private Goal CastsRemoved(Job job) {
            TypeSystem ts = extInfo.typeSystem();
            NodeFactory nf = extInfo.nodeFactory();
            return new ValidatingVisitorGoal("CastsRemoved", job, new CastRemover(job, ts, nf)).intern(this);
        }
        
        private Goal InlineHelped(Job job) {
            TypeSystem ts = extInfo.typeSystem();
            NodeFactory nf = extInfo.nodeFactory();
            return new ValidatingVisitorGoal("InlineHelped", job, new InlineHelper(job, ts, nf)).intern(this);
        }
        
        private Goal BoxingDetector(Job job) {
            TypeSystem ts = extInfo.typeSystem();
            NodeFactory nf = extInfo.nodeFactory();
            return new ValidatingVisitorGoal("BoxingDetector", job, new BoxingDetector(ts, nf)).intern(this);
        }

        private Goal AsyncInitializer(Job job) {
            TypeSystem ts = extInfo.typeSystem();
            NodeFactory nf = extInfo.nodeFactory();
            return new ValidatingVisitorGoal("AsyncInitialized", job, new AsyncInitializer(job, ts, nf)).intern(this);
        }

        private Goal StaticInitializer(Job job) {
            TypeSystem ts = extInfo.typeSystem();
            NodeFactory nf = extInfo.nodeFactory();
            return new ValidatingVisitorGoal("StaticInitialized", job, new StaticInitializer(job, ts, nf)).intern(this);
        }

        private Goal VarsBoxer(Job job) {
            TypeSystem ts = extInfo.typeSystem();
            NodeFactory nf = extInfo.nodeFactory();
            return new ValidatingVisitorGoal("VarsBoxed", job, new VarsBoxer(job, ts, nf)).intern(this);
        }
        
        private Goal ExpressionFlattenerForAtExpr(Job job) {
            TypeSystem ts = extInfo.typeSystem();
            NodeFactory nf = extInfo.nodeFactory();
            return new ValidatingVisitorGoal("ExpressionFlattenerForAtExpr", job, new ExpressionFlattenerForAtExpr(job, ts, nf)).intern(this);
        }
        
        public Goal PreCodegenASTInvariantChecker(Job job) {
            return new ValidatingVisitorGoal("CodegenASTInvariantChecker", job, new PreCodeGenASTChecker(job)).intern(this);
        }
    }

    @Override
    public Desugarer makeDesugarer(Job job) {
        return new Desugarer(job, job.extensionInfo().typeSystem(), job.extensionInfo().nodeFactory());
    }
}
