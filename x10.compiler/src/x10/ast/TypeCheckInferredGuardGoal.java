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

package x10.ast;

import polyglot.ast.Node;
import polyglot.ast.TypeCheckFragmentGoal;
import polyglot.types.LazyRef;
import polyglot.types.ProcedureDef;
import polyglot.types.Ref;
import polyglot.types.Type;
import polyglot.types.UnknownType;
import polyglot.util.ErrorInfo;
import polyglot.visit.TypeChecker;
import x10.constraint.XFailure;
import x10.errors.Warnings;
import x10.types.X10MethodDef;
import x10.types.constraints.CConstraint;
import x10.types.constraints.ConstraintManager;

public class TypeCheckInferredGuardGoal extends TypeCheckFragmentGoal<CConstraint> {
    private static final long serialVersionUID = -7110597916418023302L;

    Ref<CConstraint> sourceGuard;
    Node x;

    public TypeCheckInferredGuardGoal(Node parent, Node[] prereqs, Node n, TypeChecker v, LazyRef<CConstraint> r, Ref<CConstraint> sourceGuard) {
        super(parent, prereqs, n, v, r, true);
        this.sourceGuard = sourceGuard;
        x=parent;
    }

    @Override
    protected CConstraint defaultRecursiveValue() {
        return ConstraintManager.getConstraintSystem().makeCConstraint(); // ???
    }

    @Override
    protected Node process(Node parent, Node n, TypeChecker v) {
    	Node m = super.process(parent, n, v);
        if (parent instanceof X10MethodDecl || parent instanceof X10ConstructorDecl) {
        	ProcedureDef pdef = null;
          	if (parent instanceof X10MethodDecl) { pdef = ((X10MethodDecl) parent).methodDef(); }
          	if (parent instanceof X10ConstructorDecl) { pdef = ((X10ConstructorDecl) parent).constructorDef(); }
        	if ( pdef.inferGuard() && !pdef.requirements().isEmpty()) {
        		try {
        			CConstraint newGuard = ConstraintManager.getConstraintSystem().makeCConstraint();
        			CConstraint additionalGuard = pdef.requirements().makeGuard(v.context()); // TODO Check v.context is the correct context
        			if (parent instanceof X10MethodDecl) {
        				Warnings.inferredGuard(this.job(), Warnings.GeneratedGuard(((X10MethodDef)pdef).name(), pdef.requirements(), additionalGuard, parent.position()));
        			}
        			if (parent instanceof X10ConstructorDecl) {
        				Warnings.inferredGuard(this.job(), Warnings.GeneratedGuardForConstructor(pdef.requirements(), additionalGuard, parent.position()));
        			}
        			if (sourceGuard != null) newGuard.addIn(sourceGuard.get());
        			newGuard.addIn(additionalGuard);
        			r().update(newGuard);
        		} catch (XFailure e) {
        			v().errorQueue().enqueue(ErrorInfo.SEMANTIC_ERROR, "Could not infer guard: "+e.getMessage()+".", n().position());
        		}
        	}
        }
        if (!r().known()) {
        	if (sourceGuard != null) {
        		r().update(sourceGuard.get());
        	} else {
        		r().update(ConstraintManager.getConstraintSystem().makeCConstraint());
        	}
        }
        return m;

    }
}
