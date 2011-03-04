package x10dt.ui.launch.java.builder;


import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jdt.core.compiler.batch.BatchCompiler;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.core.resources.IProject;


import polyglot.frontend.Compiler;
import polyglot.frontend.ForgivingVisitorGoal;
import polyglot.frontend.Goal;
import polyglot.frontend.Job;
import polyglot.frontend.Scheduler;
import polyglot.main.Options;
import polyglot.util.ErrorQueue;
import polyglot.util.InternalCompilerError;
import polyglot.visit.PostCompiled;
import x10dt.ui.launch.core.builder.CheckPackageDeclVisitor;
import x10dt.ui.launch.core.utils.UIUtils;

public class JavaBuilderExtensionInfo extends x10c.ExtensionInfo {
    private final X10JavaBuilder fBuilder;
   
    public JavaBuilderExtensionInfo(X10JavaBuilder builder) {
        this.fBuilder= builder;
    }
    

    @Override
    protected Scheduler createScheduler() {
        return new X10CScheduler(this) {
            @Override
            public List<Goal> goals(Job job) {
                List<Goal> goals = super.goals(job);
                Goal endGoal = goals.get(goals.size() - 1);
                if (!(endGoal.name().equals("End"))) {
                    throw new IllegalStateException("Not an End Goal?");
                }
                List<Goal> newGoals = new ArrayList<Goal>();
                for(Goal goal: goals){
                	if (goal.name().equals("CheckASTForErrors")){ // --- WARNING: FRAGILE CODE HERE!
                		newGoals.add(PackageDeclGoal(job, fBuilder.getProject()));
                	}
                	newGoals.add(goal);
                }
                return newGoals;
            }
            
            protected Goal PackageDeclGoal(Job job, IProject project){
            	return new ForgivingVisitorGoal("PackageDeclarationCheck", job, new CheckPackageDeclVisitor(job, project)).intern(this);
            }
            
            protected Goal PostCompiled() {
                return new PostCompiled(extInfo) {
					protected boolean invokePostCompiler(Options options, Compiler compiler, ErrorQueue eq) {
						if (options.post_compiler != null && !options.output_stdout && !compiler.outputFiles().isEmpty()) {
							Collection<String> commandline = new ArrayList<String>();
							commandline.add("-1.5");
							commandline.add("-nowarn");
							commandline.add("-classpath");
							commandline.add(options.constructPostCompilerClasspath());
							for (Object f : compiler.outputFiles()) {
								commandline.add((String) f);
							}
							final MessageConsole console = UIUtils.findOrCreateX10Console();
							final MessageConsoleStream consoleStream = console.newMessageStream();
							if (!BatchCompiler.compile(commandline.toArray(new String[0]), new PrintWriter(System.out), new PrintWriter(consoleStream), null)) {
								throw new InternalCompilerError(
										"Generated Java file has compilation errors. See Console for details.");
							}
						}
						return true;
					}
                }.intern(this);
            }
           
        };
    }

   
   
}
