/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.optimizations;

import java.util.ArrayList;
import java.util.List;

import polyglot.ast.NodeFactory;
import polyglot.frontend.Goal;
import polyglot.frontend.Job;
import polyglot.frontend.Scheduler;
import polyglot.types.TypeSystem;
import polyglot.visit.NodeVisitor;
import x10.Configuration;
import x10.ExtensionInfo;
import x10.ExtensionInfo.X10Scheduler.ValidatingVisitorGoal;
import x10.X10CompilerOptions;
import x10.optimizations.inlining.InlineDeclHarvester;
import x10.optimizations.inlining.Inliner;
import x10.visit.CodeCleanUp;
import x10.visit.ConstantPropagator;
import x10.visit.ConstructorSplitterVisitor;
import x10.visit.DeadVariableEliminator;
import x10.visit.ExpressionFlattener;

public class Optimizer {

    public static boolean INLINING(ExtensionInfo extInfo) {
        Configuration config = extInfo.getOptions().x10_config;
        if (!config.OPTIMIZE)        return false;
        if (config.INLINE_CONSTANTS) return true;
        if (config.INLINE_METHODS)   return true;
        if (config.INLINE_CLOSURES)  return true;
        if (config.INLINE_METHODS_IMPLICIT) return true;
        return false;
    }

    public static boolean FLATTENING(ExtensionInfo extInfo) {
        Configuration config = extInfo.getOptions().x10_config;
        if (config.FLATTEN_EXPRESSIONS) return true;
        if (extInfo instanceof x10c.ExtensionInfo && INLINING(extInfo)) return true;
        if (!config.ALLOW_STATEMENT_EXPRESSIONS) return true; // don't let StmtExpr's reach the back end
        return false;
    }

    public static boolean CONSTRUCTOR_SPLITTING(polyglot.frontend.ExtensionInfo extensionInfo) {
        Configuration config =((ExtensionInfo) extensionInfo).getOptions().x10_config;
        if (!config.OPTIMIZE) return false;
        if (!config.SPLIT_CONSTRUCTORS) return false;
        return true;
    }
    
    private final Scheduler     scheduler;
    private final Job           job;
    private final ExtensionInfo extInfo;
    private final Configuration config;
    private final TypeSystem    ts;
    private final NodeFactory   nf;

    public Optimizer(Scheduler s, Job j) {
        scheduler = s;
        job       = j;
        extInfo   = (ExtensionInfo) j.extensionInfo();
        config    = ((X10CompilerOptions) extInfo.getOptions()).x10_config;
        ts        = extInfo.typeSystem();
        nf        = extInfo.nodeFactory();
    }

    public static List<Goal> preInlinerGoals(Scheduler scheduler, Job job) {
        return new Optimizer(scheduler, job).preInlinerGoals();
    }

    public static List<Goal> goals(Scheduler scheduler, Job job) {
        return new Optimizer(scheduler, job).goals();
    }

    private List<Goal> preInlinerGoals() {
        List<Goal> goals = new ArrayList<Goal>();
        if (CONSTRUCTOR_SPLITTING(extInfo)) {
            goals.add(ConstructorSplitter());
        }
        if (config.LOOP_OPTIMIZATIONS) {
            goals.add(LoopUnrolling());
            goals.add(ForLoopOptimizations());
        }
        return goals;
    }

    private List<Goal> goals() {
        List<Goal> goals = preInlinerGoals();
        if (INLINING(extInfo)) {
            goals.add(Harvester());
            goals.add(Inliner());
        }
        if (FLATTENING(extInfo)) {
            goals.add(ExpressionFlattener());
        }
        if (config.CODE_CLEAN_UP && !config.DEBUG) {
            goals.add(CodeCleanUp());
        }
        // workaround for XTENLANG-2705
        if (config.OPTIMIZE) {
            goals.add(ConstantProp());
        }
        if (config.EXPERIMENTAL && config.ELIMINATE_DEAD_VARIABLES) {
            goals.add(DeadVariableEliminator());
        }
            
        // TODO: add an empty goal that prereqs the above
        return goals;
    }

    public Goal LoopUnrolling() {
        NodeVisitor visitor = new LoopUnroller(job, ts, nf);
        Goal goal = new ValidatingVisitorGoal("Loop Unrolling", job, visitor);
        return goal.intern(scheduler);
    }

    public Goal ForLoopOptimizations() {
        NodeVisitor visitor = new ForLoopOptimizer(job, ts, nf);
        Goal goal = new ValidatingVisitorGoal("For Loop Optimizations", job, visitor);
        return goal.intern(scheduler);
    }

    public Goal Harvester() {
        NodeVisitor visitor = new InlineDeclHarvester(job, ts, nf);
        Goal goal = new ValidatingVisitorGoal("Harvested", job, visitor);
        return goal.intern(scheduler);
    }

    public Goal Inliner() {
        NodeVisitor visitor = new Inliner(job, ts, nf);
        Goal goal = new ValidatingVisitorGoal("Inlined", job, visitor);
        return goal.intern(scheduler);
    }

    public Goal ExpressionFlattener() {
        NodeVisitor visitor = new ExpressionFlattener(job, ts, nf);
        Goal goal = new ValidatingVisitorGoal("ExpressionFlattener", job, visitor);
        return goal.intern(scheduler);
    }

    public Goal DeadVariableEliminator() {
        NodeVisitor visitor = new DeadVariableEliminator(job, ts, nf);
        Goal goal = new ValidatingVisitorGoal("Dead Variable Elimination", job, visitor);
        return goal.intern(scheduler);
    }

    public Goal ConstructorSplitter() {
        NodeVisitor visitor = new ConstructorSplitterVisitor(job, ts, nf);
        Goal goal = new ValidatingVisitorGoal("Constuctor Splitter", job, visitor);
        return goal.intern(scheduler);
    }
    
    public Goal CodeCleanUp() {
        NodeVisitor visitor = new CodeCleanUp(job, ts, nf);
        Goal goal = new ValidatingVisitorGoal("CodeCleanUp", job, visitor);
        return goal.intern(scheduler);
    }
    
    public Goal ConstantProp() {
        NodeVisitor visitor = new ConstantPropagator(job, ts, nf);
        Goal goal = new ValidatingVisitorGoal("ConstantPropagation", job, visitor);
        return goal.intern(scheduler);
    }

}
