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
import polyglot.frontend.ExtensionInfo;
import polyglot.frontend.Goal;
import polyglot.frontend.Job;
import polyglot.frontend.Scheduler;
import polyglot.types.TypeSystem;
import polyglot.visit.NodeVisitor;
import x10.ExtensionInfo.X10Scheduler.ValidatingVisitorGoal;
import x10.visit.DeadVariableEliminator;
import x10.visit.ExpressionFlattener;
import x10.visit.Inliner;

public class Optimizer {

    public static boolean INLINING() {
        if (!x10.Configuration.EXPERIMENTAL)    return false;
        if (x10.Configuration.INLINE_CONSTANTS) return true;
        if (x10.Configuration.INLINE_METHODS)   return true;
        if (x10.Configuration.ALLOW_STATEMENT_EXPRESSIONS && x10.Configuration.INLINE_CLOSURES) return true;
        return false;
    }
    
    public static boolean FLATTENING(boolean javaBackEnd) {
        if (x10.Configuration.FLATTEN_EXPRESSIONS) return true;
        if (javaBackEnd && INLINING())             return true;
        if (!x10.Configuration.ALLOW_STATEMENT_EXPRESSIONS)   return true; // don't let StmtExpr's reach the back end
        return false;
    }

    private final Scheduler     scheduler;
    private final Job           job;
    private final ExtensionInfo extInfo;
    private final TypeSystem    ts;
    private final NodeFactory   nf;
    private final boolean       java;      // Java back-end ???

    private Optimizer(Scheduler s, Job j) {
        scheduler = s;
        job       = j;
        extInfo   = j.extensionInfo();
        ts        = extInfo.typeSystem();
        nf        = extInfo.nodeFactory();
        java      = extInfo instanceof x10c.ExtensionInfo;
    }

    public static List<Goal> goals(Scheduler scheduler, Job job) {
        return new Optimizer(scheduler, job).goals();
    }

    private List<Goal> goals() {
        List<Goal> goals = new ArrayList<Goal>();
        if (x10.Configuration.LOOP_OPTIMIZATIONS) {
            if (x10.Configuration.EXPERIMENTAL) {
                goals.add(ForLoopOptimizations());
                goals.add(LoopUnrolling());
            } else {
                goals.add(LoopUnrolling());
                goals.add(ForLoopOptimizations());
            }
        }
        if (INLINING()) {
            goals.add(Inliner());
        }
        if (FLATTENING(java)) {
            goals.add(ExpressionFlattener());
        }
        if (x10.Configuration.EXPERIMENTAL && x10.Configuration.ELIMINATE_DEAD_VARIABLES) {
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

}
