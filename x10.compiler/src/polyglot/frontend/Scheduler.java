/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 * This file was originally derived from the Polyglot extensible compiler framework.
 *
 *  (C) Copyright 2000-2007 Polyglot project group, Cornell University
 *  (C) Copyright IBM Corporation 2007-2015.
 */

package polyglot.frontend;

import java.util.*;

import polyglot.ast.Node;
import polyglot.frontend.Compiler;
import polyglot.frontend.Goal.Status;
import polyglot.main.Options;
import polyglot.main.Reporter;
import polyglot.types.*;
import polyglot.util.InternalCompilerError;
import polyglot.util.Option;
import polyglot.util.CollectionUtil; import x10.types.X10ClassDef;
import x10.util.CollectionFactory;
import polyglot.visit.PostCompiled;


/**
 * The <code>Scheduler</code> manages <code>Goal</code>s and runs
 * <code>Pass</code>es.
 * 
 * The basic idea is to have the scheduler try to satisfy goals. To reach a
 * goal, a pass is run. The pass could modify an AST or it could, for example,
 * initialize the members of a class loaded from a class file.
 * 
 * Goals are processed via a worklist. A goal may have <i>prerequisite</i>
 * dependencies. All prerequisites must be reached before the goal is attempted.
 * The compilation completes when the EndAll goal is reached. A goal will be
 * attempted at most once. If it fails, all goals dependent on it are
 * unreachable.
 * 
 * Passes are allowed to spawn other passes. A <i>reentrant pass</i> is allowed
 * to spawn itself.
 * 
 * Passes are (mostly) transactional. If a pass fails, its effects on the AST
 * and on the system resolver are rolled back.
 * 
 * @author nystrom
 */
public abstract class Scheduler {
    protected ExtensionInfo extInfo;

    /** map used for interning goals. */
    protected Map<Goal,Goal> internCache = CollectionFactory.newHashMap(); // note that is not a cache in the sense that if you clear it the correctness of the compiler is compromised.
    
    // TODO: remove this, we only need to intern the goal status, not the goal itself.
    // Actually, the lazy ref to the goal status is the goal.  The run() method is the resolver for the lazy ref.
    public Goal intern(Goal goal) {
        x10.ExtensionInfo x10Info = (x10.ExtensionInfo) extInfo;
        x10Info.stats.incrFrequency("intern", 1);
        x10Info.stats.incrFrequency("intern:"
                + (goal instanceof VisitorGoal ? ((VisitorGoal) goal).v.getClass().getName() : goal.getClass().getName()), 1);
        Goal g = internCache.get(goal);
        if (g == null) {
            g = goal;
            internCache.put(g, g);
        }
        else {
            assert goal.getClass() == g.getClass();
        }
        return g;
    }
    
    /**
     * A map from <code>Source</code>s to <code>Job</code>s or to
     * the <code>COMPLETED_JOB</code> object if the Job previously
     * existed
     * but has now finished. The map contains entries for all
     * <code>Source</code>s that have had <code>Job</code>s added for them.
     */
    protected Map<Source, Option<Job>> jobs;
    
    protected Collection<Job> commandLineJobs = Collections.emptyList();
    
    /** True if any pass has failed. */
    protected boolean failed;

    /** True if the client requested that this scheduler be canceled */
    private boolean canceled = false;

    /** Return true if this scheduler has been canceled (so any compilation will fail). */
    public boolean canceled() { return canceled; }
    /** Cancel the current compilation. */
    public void cancel() { canceled = true; }

    private static class CompilationAbortedException extends RuntimeException {
        private static final long serialVersionUID = -5353770769730560033L;
    }

    protected static final Option<Job> COMPLETED_JOB = Option.<Job>None();

    /** The currently running pass, or null if no pass is running. */
    protected Goal currentGoal;
    
    public Scheduler(ExtensionInfo extInfo) {
        this.extInfo = extInfo;
        this.jobs = new LinkedHashMap<Source, Option<Job>>();
        this.currentGoal = null;
    }
    
    public ExtensionInfo extensionInfo() {
        return this.extInfo;
    }
    
    public Collection<Job> commandLineJobs() {
        return this.commandLineJobs;
    }

    public Collection<Source> sources;
    public void clearAll(Collection<Source> sources) {
        this.sources = sources;
        setFailed(false);
    }
    
    public void setCommandLineJobs(Collection<Job> c) {
        this.commandLineJobs = Collections.unmodifiableCollection(c);
    }
    
    public boolean reached(Goal goal) {
        return goal.hasBeenReached();
    }

    protected void completeJob(Job job) {
        if (job != null) {
            job.setCompleted(true);
            jobs.put(job.source(), COMPLETED_JOB);
            Reporter reporter = extInfo.getOptions().reporter;
            if (reporter.should_report(Reporter.frontend, 1)) {
                reporter.report(1, "Completed job " + job);
            }
        }
    }

    public List<Goal> prerequisites(Goal goal) {
        return goal.prereqs();
    }
    
    Goal EndAll;
    
    public Goal End(Job job) {
        return new SourceGoal_c("End", job) {
            private static final long serialVersionUID = 8093041848879587834L;
            public boolean runTask() {
                // The job has finished.  Let's remove it from the job map
                // so it can be garbage collected, and free up the AST.
                completeJob(job);
                return true;
            }
        }.intern(this);
    }
    
    Collection<Job> shouldCompile = new LinkedHashSet<Job>();
    
    public boolean shouldCompile(Job job) {
	if (commandLineJobs().contains(job))
	    return true;
        if (extInfo.getOptions().compile_command_line_only)
            return false;
        return shouldCompile.contains(job);
    }
    
    protected Goal PostCompiled() {
	return new PostCompiled(extInfo).intern(this);
    }

    protected Goal EndAll() {
	if (EndAll == null)
	    EndAll = PostCompiled();
	return EndAll;
    }

    protected Goal EndCommandLine() {
	return EndAll();
    }

    public boolean runToCompletion() {
        boolean okay;
        okay = runToCompletion(EndAll());
        return okay;
    }

    /**
     * Attempt to complete all goals in the worklist (and any subgoals they
     * have). This method returns <code>true</code> if all passes were
     * successfully run and all goals in the worklist were reached. The worklist
     * should be empty at return.
     */ 
    public boolean runToCompletion(Goal endGoal) {
    	boolean okay = false;

    	try {
    		okay = attempt(endGoal);
    	}
    	catch (CyclicDependencyException e) {
    	}
    	catch (CompilationAbortedException e) {
    	}

    	Reporter reporter = extInfo.getOptions().reporter;
        if (reporter.should_report(Reporter.frontend, 1))
            reporter.report(1, "Finished all passes for " + this.getClass().getName() + " -- " +
                        (okay ? "okay" : "failed"));

        return okay;
    }
    
    /**         
     * Load a source file and create a job for it.  Optionally add a goal
     * to compile the job to Java.
     * 
     * @param source The source file to load.
     * @param compile True if the compile goal should be added for the new job.
     * @return The new job or null if the job has already completed.
     */         
    public Job loadSource(FileSource source, boolean compile) {
        // Add a new Job for the given source. If a Job for the source
        // already exists, then we will be given the existing job.
        Job job = addJob(source);

        if (job == null) {
            // addJob returns null if the job has already been completed, in
            // which case we can just ignore the request to read in the
            // source.
            return null;
        }               
        
        // Create a goal for the job; this will set up dependencies for
        // the goal, even if the goal isn't to be added to the work list.
        addDependenciesForJob(job, compile);
        
        return job;
    }
    
    public boolean sourceHasJob(Source s) {
        return jobs.get(s) != null;
    }
    
    public Goal currentGoal() {
    	return currentGoal;
    }
    
    public Job currentJob() {
    	if (currentGoal instanceof SourceGoal_c)
    		return ((SourceGoal_c) currentGoal).job();
    	return null;
    }
    
    /**
     * Run passes until the <code>goal</code> is attempted.  Returns true iff the goal is reached.
     * @throws CyclicDependencyException 
     */ 
    public boolean attempt(Goal goal) throws CyclicDependencyException {
        assert currentGoal() == null
        || currentGoal().getCached() == Goal.Status.RUNNING
        || currentGoal().getCached() == Goal.Status.RUNNING_RECURSIVE
        || currentGoal().getCached() == Goal.Status.RUNNING_WILL_FAIL : "goal " + currentGoal() + " state " + currentGoal().state();

        Status state = goal.get();
        
        return state == Goal.Status.SUCCESS;
    }

    public static class State {
        SystemResolver resolver;
        State(SystemResolver resolver) {
            this.resolver = resolver;
        }
    }
    
    public State pushGlobalState(Goal goal) {
        TypeSystem ts = extInfo.typeSystem();
//        SystemResolver resolver = ts.saveSystemResolver();
        SystemResolver resolver = ts.systemResolver();
        return new State(resolver);
    }

    public void popGlobalState(Goal goal, State s) {
        //TypeSystem ts = Globals.TS();
        if (reached(goal)) {
//            try {
//                s.resolver.putAll(ts.systemResolver());
//            }
//            catch (SemanticException e) {
//                ts.restoreSystemResolver(s.resolver);
//                goal.setState(Goal.Status.FAIL);
//            }
        }
        else {
//            ts.restoreSystemResolver(s.resolver);
        }
    }
    
    protected static class Complete extends RuntimeException {
        private static final long serialVersionUID = 868126407122946251L;

        protected Goal goal;

        Complete(Goal goal) {
            this.goal = goal;
        }
    }
    
    /**         
     * Run the pass <code>pass</code>.  All subgoals of the pass's goal
     * required to start the pass should be satisfied.  Running the pass
     * may not satisfy the goal, forcing it to be retried later with new
     * subgoals.
     */
    protected boolean runPass(Goal goal) throws CyclicDependencyException {
        Job job = goal instanceof SourceGoal ? ((SourceGoal) goal).job() : null;
                
        Options options = extInfo.getOptions();
        Reporter reporter = options.reporter;
        if (canceled) {
            if (reporter.should_report(Reporter.frontend, 1))
                reporter.report(1, "Compilation canceled; skipping pass " + goal);
            
            goal.update(Goal.Status.FAIL);
            throw new CompilationAbortedException();
        }
        
        if (options.disable_passes.contains(goal.name())) {
            if (reporter.should_report(Reporter.frontend, 1))
                reporter.report(1, "Skipping pass " + goal);
            
            goal.update(Goal.Status.SUCCESS);
            return true;
        }
        
        if (reporter.should_report(Reporter.frontend, 1))
            reporter.report(1, "Running pass for " + goal);

        if (reached(goal)) {
            throw new InternalCompilerError("Cannot run a pass for completed goal " + goal);
        }
        
        boolean result = false;

        if (true || job == null || job.status()) {
            reporter.start_reporting(goal.name());

            if (job != null) {
				    // We're starting to run the pass. 
				    // Record the initial error count.
				    job.initialErrorCount = job.compiler().errorQueue().errorCount();
            }
            
            Goal oldGoal = currentGoal;
            currentGoal = goal;
            String key = goal.toString();
            x10.ExtensionInfo x10Info = (x10.ExtensionInfo) extInfo;
            x10Info.stats.startTiming(goal.name(), key);

            x10Info.stats.incrFrequency(key + " attempts", 1);
            x10Info.stats.incrFrequency("total goal attempts", 1);
            
            try {
                result = goal.runTask();

                if (result && goal.getCached() == Goal.Status.RUNNING) {
                    x10Info.stats.incrFrequency(key + " reached", 1);
                    x10Info.stats.incrFrequency("total goal reached", 1);

                    goal.update(Status.SUCCESS);

                    if (reporter.should_report(Reporter.frontend, 1))
                        reporter.report(1, "Completed pass for " + goal);
                }
                else {
                    x10Info.stats.incrFrequency(key + " unreached", 1);
                    x10Info.stats.incrFrequency("total goal unreached", 1);

                    if (reporter.should_report(Reporter.frontend, 1))
                        reporter.report(1, "Completed (unreached) pass for " + goal);
                }
            }
            finally {
                currentGoal = oldGoal;
                
                if (job != null) {
				    // We've stopped running a pass. 
				    // Check if the error count changed.
				    int errorCount = job.compiler().errorQueue().errorCount();
				
				    if (errorCount > job.initialErrorCount) {
				        job.reportedErrors = true;
				    }
				}

                reporter.stop_reporting(goal.name());

                x10Info.stats.stopTiming();
            }

            // pretty-print this pass if we need to.
            if (job != null &&
                (extInfo.getOptions().print_ast.contains(goal.name()) ||
                 extInfo.getOptions().print_ast.contains("printall"))) {
                System.err.println("--------------------------------" +
                                   "--------------------------------");
                System.err.println("Pretty-printing AST for " + job +
                                   " after " + goal.name());

                job.ast().prettyPrint(System.err);
            }

            // dump this pass if we need to.
            if (job != null && 
                (extInfo.getOptions().dump_ast.contains(goal.name()) ||
                 extInfo.getOptions().dump_ast.contains("dumpall"))) {
                System.err.println("--------------------------------" +
                                   "--------------------------------");
                System.err.println("Dumping AST for " + job +
                                   " after " + goal.name());
                
                job.ast().dump(System.err);
            }
        }   

        if (! result) {
            failed = true;
        }
        
        // Record the progress made before running the pass and then update
        // the current progress.
        if (reporter.should_report(Reporter.frontend, 1)) {
            reporter.report(1, "Finished " + goal +
                          " status=" + statusString(result));
        }
        
        if (job != null) {
            job.updateStatus(result);
        }
                
        return result;             
    }
    
    /** FIXME: TEMPRORARY Inliner hack: Errors in speculative compilation for inlining should not be fatal
     * @depricated DO NOT USE
     */
    public boolean getFailed() {
        return failed;
    }
    
    /** FIXME: TEMPRORARY Inliner hack: Errors in speculative compilation for inlining should not be fatal
     * @depricated DO NOT USE
     */
    
    public void setFailed (boolean b) {
        failed = b;
    }
    
    /** FIXME: TEMPRORARY Inliner hack: Errors in speculative compilation for inlining should not be fatal
     * @depricated DO NOT USE
     */
    
    public void clearFailed () {
        if (failed)
            setFailed(false);
    }
                                   
    protected static String statusString(boolean okay) {
        if (okay) {
            return "done";
        }
        else {
            return "failed";
        }
    }
    
    /** Return all compilation units currently being compiled. */
    public Collection<Job> jobs() {
        ArrayList<Job> l = new ArrayList<Job>(jobs.size());
        
        for (Iterator<Option<Job>> i = jobs.values().iterator(); i.hasNext(); ) {
            Option<Job> o = i.next();
            if (o != COMPLETED_JOB) {
                l.add(o.get());
            }
        }
        
        return l;
    }

    /**
     * Add a new <code>Job</code> for the <code>Source source</code>.
     * A new job will be created if
     * needed. If the <code>Source source</code> has already been processed,
     * and its job discarded to release resources, then <code>null</code>
     * will be returned.
     */
    public Job addJob(Source source) {
        return addJob(source, null);
    }

    /**
     * Add a new <code>Job</code> for the <code>Source source</code>,
     * with AST <code>ast</code>.
     * A new job will be created if
     * needed. If the <code>Source source</code> has already been processed,
     * and its job discarded to release resources, then <code>null</code>
     * will be returned.
     */
    public Job addJob(Source source, Node ast) {
        Option<Job> o = jobs.get(source);
        Job job = null;
        
        if (o == COMPLETED_JOB) {
            // the job has already been completed.
            // We don't need to add a job
            return null;
        }
        else if (o == null) {
            // No appropriate job yet exists, we will create one.
            job = this.createSourceJob(source, ast);

            // record the job in the map and the worklist.
            jobs.put(source, new Option.Some<Job>(job));

            Reporter reporter = extInfo.getOptions().reporter;
            if (reporter.should_report(Reporter.frontend, 4)) {
                reporter.report(4, "Adding job for " + source + " at the " +
                    "request of goal " + currentGoal);
            }
        }
        else {
            job = o.get();
        }
        
        return job;
    }

    /** Get the goals for a particular job.  This creates the dependencies between them.  The list must include End(job). */
    public abstract List<Goal> goals(Job job);
    
    public void addDependenciesForJob(Job job, boolean compile) {
        ExtensionInfo extInfo = this.extInfo;

        List<Goal> goals = goals(job);

        Goal prev = null;

        // Be careful: the list might include goals already run.
        for (Goal goal : goals) {
//            assert goal instanceof SourceGoal;
            if (prev != null) {
                goal.addPrereq(prev);
            }
            if (! goal.hasBeenReached()) {
                prev = goal;
            }
        }
        
        assert prev == End(job);
        
        if (compile) {
            shouldCompile.add(job);
            EndAll().addPrereq(prev);
        }
    }

    /**
     * Create a new <code>Job</code> for the given source and AST.
     * In general, this method should only be called by <code>addJob</code>.
     */
    protected Job createSourceJob(Source source, Node ast) {
        return new Job(extInfo, extInfo.jobExt(), source, ast);
    }

    public String toString() {
        return getClass().getName();
    }   
    
    public abstract Goal Parsed(Job job);
    public abstract Goal ImportTableInitialized(Job job);
    public abstract Goal TypesInitialized(Job job);
    protected abstract Goal constructTypesInitialized(Job job);
    public abstract Goal TypesInitializedForCommandLineBarrier();
    public abstract Goal PreTypeCheck(Job job);
    public abstract Goal TypeChecked(Job job);
    public abstract Goal ReachabilityChecked(Job job);
    public abstract Goal ExceptionsChecked(Job job);
    public abstract Goal ExitPathsChecked(Job job);
    public abstract Goal InitializationsChecked(Job job);
    public abstract Goal ConstructorCallsChecked(Job job);
    public abstract Goal ForwardReferencesChecked(Job job);
    public abstract Goal Serialized(Job job);
    public abstract Goal CodeGenerated(Job job);

    public abstract Goal LookupGlobalType(LazyRef<Type> sym);
    public abstract Goal LookupGlobalTypeDef(LazyRef<X10ClassDef> sym, QName name);
    public abstract Goal LookupGlobalTypeDefAndSetFlags(LazyRef<X10ClassDef> sym, QName name, Flags flags);
}


