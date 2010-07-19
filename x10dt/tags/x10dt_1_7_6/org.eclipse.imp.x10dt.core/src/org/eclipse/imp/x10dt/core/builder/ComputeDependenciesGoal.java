/**
 * 
 */
package org.eclipse.imp.x10dt.core.builder;

import polyglot.frontend.Job;
import polyglot.frontend.VisitorGoal;

public class ComputeDependenciesGoal extends VisitorGoal {
    public ComputeDependenciesGoal(Job job, PolyglotDependencyInfo dependencyInfo) {
        super(job, new ComputeDependenciesVisitor(job, job.extensionInfo().typeSystem(), dependencyInfo));
        addPrereq(job.extensionInfo().scheduler().TypeChecked(job));
    }
}