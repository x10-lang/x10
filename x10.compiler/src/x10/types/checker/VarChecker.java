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

import polyglot.ast.Field;
import polyglot.ast.NamedVariable;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.frontend.Job;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.visit.ContextVisitor;
import x10.errors.Errors;
import x10.types.X10TypeEnv_c;

/**
 * Flags an error if visited node contains a mutable variable or field.
 * @author vj
 *
 */
public class VarChecker extends ContextVisitor {
    public VarChecker(Job job) {
        super(job, job.extensionInfo().typeSystem(), job.extensionInfo().nodeFactory());
    }
    public SemanticException error = null;
    @Override
    public Node override(Node n) {
        if (n instanceof NamedVariable) {
            NamedVariable e = (NamedVariable) n;
            // We typecheck constraint expressions in constraint (deptype) context.  So the check
            // below is not needed.
            //if (! e.flags().isFinal())
            //    error = new Errors.VarMustBeFinalInTypeDef(e.name().toString(), e.position()); 

            if (n instanceof Field) {
                Field l = (Field) n;
                if (! new X10TypeEnv_c(context).isAccessible(l.fieldInstance())) {
                    error = new Errors.VarMustBeAccessibleInTypeDef(l.fieldInstance(), e.position()); 
                }
            }
            return n;
        }

        return null;
    }
}
