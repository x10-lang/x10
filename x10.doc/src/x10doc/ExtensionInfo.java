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

package x10doc;

import java.util.ArrayList;
import java.util.List;

import polyglot.frontend.AllBarrierGoal;
import polyglot.frontend.Goal;
import polyglot.frontend.Job;
import polyglot.frontend.Scheduler;
import x10.Version;
import x10doc.doc.X10ClassDoc;
import x10doc.doc.X10RootDoc;
import x10doc.goals.ASTTraversalGoal;
import x10doc.visit.X10DocGenerator;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.RootDoc;
import com.sun.tools.doclets.standard.Standard;

public class ExtensionInfo extends x10.ExtensionInfo {

    public RootDoc root;

    public void setRoot(RootDoc root) {
        this.root = root;
    }

    @Override
    public polyglot.main.Version version() {
        return new Version() {
            @Override
            public String name() { return "x10doc"; }
        };
    }

    @Override
    public String compilerName() {
        return "x10doc";
    }

    @Override
    protected X10DocOptions createOptions() {
        return new X10DocOptions(this);
    }

    @Override
    public X10DocOptions getOptions() {
        return (X10DocOptions) super.getOptions();
    }

    @Override
    protected Scheduler createScheduler() {
        System.setErr(System.out);
        return new X10DocScheduler(this);
    }

    public static class X10DocScheduler extends X10Scheduler {

        public X10DocScheduler(ExtensionInfo extInfo) {
            super(extInfo);
        }

        @Override
        public ExtensionInfo extensionInfo() {
            return (ExtensionInfo) this.extInfo;
        }

        @Override
        public List<Goal> goals(Job job) {
            List<Goal> goals = new ArrayList<Goal>(typecheckSourceGoals(job));
            Goal endGoal = goals.remove(goals.size() - 1);
            goals.add(EnsureNoErrors(job));

            goals.add(X10DocGenerated(job));
            goals.add(endGoal);

            // the barrier will handle prereqs on its own
            X10DocGenerated(job).addPrereq(TypeCheckBarrier());

            return goals;
        }

        public Goal X10DocGenerated(Job job) {
            // TypeSystem ts = extInfo.typeSystem();
            // NodeFactory nf = extInfo.nodeFactory();
            return new ASTTraversalGoal("X10DocGenerated", job, new X10DocGenerator(job)).intern(this);
        }

        @SuppressWarnings("serial")
        public Goal EndAll() {
            return new AllBarrierGoal("DocletInvoked", this) {
                @Override
                public boolean runTask() {
                    // for all specified classes, add comments displaying
                    // constraints
                    for (ClassDoc c : X10RootDoc.getRootDoc().specifiedClasses()) {
                        X10ClassDoc cd = (X10ClassDoc) c;
                        cd.addDeclTag(cd.declString());
                        cd.addDeclsToMemberComments();
                    }

                    Standard.start(((X10DocScheduler) scheduler).extensionInfo().root);
                    return true;
                }

                @Override
                public Goal prereqForJob(Job job) {
                    if (((X10DocScheduler) scheduler).commandLineJobs().contains(job)) return scheduler.End(job);
                    return null;
                }
            }.intern(this);
        }
    }
}
