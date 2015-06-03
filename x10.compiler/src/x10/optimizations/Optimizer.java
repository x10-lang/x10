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

package x10.optimizations;

import java.util.ArrayList;
import java.util.List;

import polyglot.ast.NodeFactory;
import polyglot.frontend.Goal;
import polyglot.frontend.Job;
import polyglot.frontend.Scheduler;
import polyglot.types.TypeSystem;
import polyglot.visit.NodeVisitor;
import polyglot.visit.DeadCodeEliminator;
import x10.Configuration;
import x10.ExtensionInfo;
import x10.ExtensionInfo.X10Scheduler.ValidatingVisitorGoal;
import x10.X10CompilerOptions;
import x10.optimizations.inlining.DeclPackager;
import x10.optimizations.inlining.Inliner;
import x10.visit.CodeCleanUp;
import x10.visit.ConstantPropagator;
import x10.visit.ConstructorSplitterVisitor;
import x10.visit.DeadVariableEliminator;
import x10.visit.UnusedVariableEliminator;
import x10.visit.ExpressionFlattener;
import x10.visit.X10CopyPropagator;
import x10cpp.visit.TupleRemover;

public class Optimizer {
    
    public static boolean INLINING(ExtensionInfo extInfo) {
        Configuration config = extInfo.getOptions().x10_config;
        return config.OPTIMIZE && (config.INLINE || 0 < config.INLINE_SIZE);
    }

    public static boolean FLATTENING(ExtensionInfo extInfo) {
        Configuration config = extInfo.getOptions().x10_config;
        if (config.FLATTEN_EXPRESSIONS) return true;
        if (extInfo.isManagedX10()) return true;
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
            if (!(extInfo.isManagedX10())) {
                goals.add(LoopUnrolling());
            }
            goals.add(ForLoopOptimizations());
        }
        return goals;
    }

    private List<Goal> goals() {
        List<Goal> goals = preInlinerGoals();
        if (INLINING(extInfo)) {
            goals.add(Packager());
            goals.add(PreInlineConstantProp());
            goals.add(Inliner(false));
        } else {
            // Even when inlining is not enabled, we're still going to inline
            // closure calls on closure literals.
            goals.add(Inliner(true));
        }
        if (FLATTENING(extInfo)) {
            goals.add(ExpressionFlattener());
        }
        if (!config.DEBUG_ENABLE_LINEMAPS) {
            goals.add(CodeCleanUp());
        }
        if (config.OPTIMIZE) {
            goals.add(ConstantProp());
            goals.add(CopyPropagation());
            goals.add(UnusedVariableEliminator());
            if (!config.DEBUG_ENABLE_LINEMAPS) {
                goals.add(CodeCleanUp2());
            }
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

    public Goal Packager() {
        NodeVisitor visitor = new DeclPackager(job, ts, nf);
        Goal goal = new ValidatingVisitorGoal("Packaged decl's", job, visitor);
        return goal.intern(scheduler);
    }

    public Goal Inliner(boolean closuresOnly) {
        NodeVisitor visitor = new Inliner(job, ts, nf, closuresOnly);
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
    
    public Goal UnusedVariableEliminator() {
        NodeVisitor visitor = new UnusedVariableEliminator(job, ts, nf);
        Goal goal = new ValidatingVisitorGoal("Unused Variable Elimination", job, visitor);
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
    
    public Goal CodeCleanUp2() {
        NodeVisitor visitor = new CodeCleanUp(job, ts, nf);
        Goal goal = new ValidatingVisitorGoal("CodeCleanUp Redux", job, visitor);
        return goal.intern(scheduler);
    }

    public Goal PreInlineConstantProp() {
        NodeVisitor visitor = new ConstantPropagator(job, ts, nf, true);
        Goal goal = new ValidatingVisitorGoal("Pre-inlining ConstantPropagation", job, visitor);
        return goal.intern(scheduler);
    }
    
    public Goal ConstantProp() {
        NodeVisitor visitor = new ConstantPropagator(job, ts, nf, false);
        Goal goal = new ValidatingVisitorGoal("ConstantPropagation", job, visitor);
        return goal.intern(scheduler);
    }
    
    public Goal CopyPropagation() {
        NodeVisitor visitor = new X10CopyPropagator(job, ts, nf);
        Goal goal = new ValidatingVisitorGoal("CopyPropagation", job, visitor);
        return goal.intern(scheduler);
    }

}
