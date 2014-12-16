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

package x10.types.checker;

import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.frontend.Globals;
import polyglot.frontend.Job;
import polyglot.visit.ContextVisitor;
import x10.ast.X10Special;


public class PlaceTermChecker extends ContextVisitor {
	public PlaceTermChecker(Job job) {
        super(job, job.extensionInfo().typeSystem(), job.extensionInfo().nodeFactory());
    }
    @Override
    public Node override(Node n) {
        if (n instanceof Expr) {
            Expr e = (Expr) n;
            error = error || e.toString().startsWith("_place");
            return n;
        }
        return null;
    }
    public boolean error() { return error;}
}
