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

package x10c;

import java.util.List;

import polyglot.ast.NodeFactory;
import polyglot.frontend.Goal;
import polyglot.frontend.Job;
import polyglot.frontend.Scheduler;
import polyglot.frontend.VisitorGoal;
import polyglot.types.TypeSystem;
import x10.visit.SharedBoxer;

public class ExtensionInfo extends x10.ExtensionInfo {
    @Override
    protected Scheduler createScheduler() {
        return new X10CScheduler(this);
    }

    static class X10CScheduler extends X10Scheduler {
        public X10CScheduler(ExtensionInfo extInfo) {
            super(extInfo);
        }

        @Override
        public List<Goal> goals(Job job) {
            List<Goal> goals = super.goals(job);
            CodeGenerated(job).addPrereq(Desugarer(job));
            CodeGenerated(job).addPrereq(SharedBoxed(job));
            return goals;
        }

        private Goal SharedBoxed(Job job) {
            TypeSystem ts = extInfo.typeSystem();
            NodeFactory nf = extInfo.nodeFactory();
            return new VisitorGoal("sharedBoxer", job, new SharedBoxer(job, ts, nf)).intern(this);
        }
    }
}
