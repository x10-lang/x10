package x10bc;

import polyglot.ast.NodeFactory;
import polyglot.ast.SourceFile;
import polyglot.frontend.AbstractGoal_c;
import polyglot.frontend.Compiler;
import polyglot.frontend.Goal;
import polyglot.frontend.JLScheduler;
import polyglot.frontend.Job;
import polyglot.frontend.Scheduler;
import polyglot.types.TypeSystem;

/**
 * Extension information for ibex extension.
 */
public class ExtensionInfo extends x10.ExtensionInfo {
    @Override
    protected Scheduler createScheduler() {
        return new X10BCScheduler(this);
    }
    
    @Override
    public void initCompiler(Compiler compiler) {
        getOptions().output_ext = "class";
        super.initCompiler(compiler);
    }

    public String compilerName() {
        return "x10bc";
    }
    
    static class X10BCScheduler extends X10Scheduler {
        public X10BCScheduler(ExtensionInfo extInfo) {
            super(extInfo);
        }
        
        @Override
        protected Goal PostCompiled() {
            return new AbstractGoal_c("PostCompiled") {
                public boolean runTask() {
                    return true;
                }
            }.intern(this);
        }

        @Override
        public Goal CodeGenerated(final Job job) {
            final TypeSystem ts = extInfo.typeSystem();
            final NodeFactory nf = extInfo.nodeFactory();
            return new AbstractGoal_c("BCCodeGenerated") {
                @Override
                public boolean runTask() {
                    try {
                        new X10BytecodeTranslator(job, ts, nf).visit((SourceFile) job.ast());
                    }
                    catch (Exception e) {
                        return false;
                    }
                    return true;
                }
            }.intern(this);
        }
    }
}
