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

package x10doc.goals;

import polyglot.ast.Node;
import polyglot.frontend.Job;
import polyglot.frontend.SourceGoal_c;
import x10.visit.X10DelegatingVisitor;

@SuppressWarnings("serial")
public class ASTTraversalGoal extends SourceGoal_c {

    private final X10DelegatingVisitor v;

    public ASTTraversalGoal(String name, Job job, X10DelegatingVisitor v) {
        super(name, job);
        this.v = v;
    }

    public ASTTraversalGoal(Job job, X10DelegatingVisitor v) {
        super(job);
        this.v = v;
    }

    @Override
    public boolean runTask() {
        Node n = job().ast();
        // System.out.println("n.getClass() = " + n.getClass());
        v.visitAppropriate(n);
        return true;
    }

}
