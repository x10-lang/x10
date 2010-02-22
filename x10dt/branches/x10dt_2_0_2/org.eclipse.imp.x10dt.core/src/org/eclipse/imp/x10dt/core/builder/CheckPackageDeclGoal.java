/**
 * 
 */
package org.eclipse.imp.x10dt.core.builder;

import polyglot.frontend.Job;
import polyglot.frontend.VisitorGoal;

public class CheckPackageDeclGoal extends VisitorGoal {
    public CheckPackageDeclGoal(Job job, X10Builder builder) {
        super(job, new CheckPackageDeclVisitor(job, builder.getProject()));
        addPrereq(job.extensionInfo().scheduler().intern(new ComputeDependenciesGoal(job, builder.fDependencyInfo)));
    }
}