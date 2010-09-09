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

package x10.ast;

/**
 * Immutable representation of a local variable access. 
 * Introduced to add X10 specific type checks. A local variable accessed
 * in a deptype must be final.
 * 
 * @author vj
 */
import java.util.Set;

import polyglot.ast.Id;
import polyglot.ast.Local_c;
import polyglot.ast.Node;
import polyglot.types.CodeDef;
import polyglot.types.Context;
import polyglot.types.FieldInstance;
import polyglot.types.Flags;
import polyglot.types.LocalInstance;
import polyglot.types.Name;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.Types;
import polyglot.types.UnknownType;
import polyglot.types.VarDef;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;

import x10.constraint.XFailure;
import x10.constraint.XLocal;
import x10.constraint.XTerm;
import x10.constraint.XVar;
import x10.errors.Errors;
import x10.types.X10Context;
import x10.types.X10FieldInstance;
import x10.types.X10Flags;
import x10.types.X10LocalInstance;
import x10.types.X10ProcedureDef;
import x10.types.X10TypeMixin;
import x10.types.X10TypeSystem;
import x10.types.X10TypeSystem_c;
import x10.types.checker.PlaceChecker;
import x10.types.constraints.CConstraint;
import x10.types.constraints.CConstraint;

public class X10Local_c extends Local_c {

	public X10Local_c(Position pos, Id name) {
		super(pos, name);
		
	}
	public Node typeCheck(ContextVisitor tc) {
	    X10Context context = (X10Context) tc.context();
	    LocalInstance li = localInstance();
	    if (!((X10LocalInstance) li).isValid()) {
	        li = findAppropriateLocal(tc, name.id());
	    }

	    // if the local is defined in an outer class, then it must be final
	    if (!context.isLocal(li.name())) {
	        // this local is defined in an outer class
	        if (!li.flags().isFinal() && !X10Flags.toX10Flags(li.flags()).isShared()) {
	            Errors.issue(tc.job(), new SemanticException("Local variable \"" + li.name() + 
	                    "\" is accessed from an inner class or a closure, and must be declared final or shared.",
	                    this.position()));                     
	        }
	    }

	    X10Local_c result = (X10Local_c) localInstance(li).type(li.type());

		try {
			VarDef dli = context.varWhoseTypeIsBeingElaborated();
			if (context.inDepType()) {
				li = result.localInstance();
				if (! (li.def().equals(dli)) && ! li.flags().isFinal()) {
					throw new SemanticError("Local variable " + li.name() 
							+ " must be final in a dependent clause.", 
							position());
				}
			}
			
			// Add in self==x to local variable x.

			Type type = ((X10LocalInstance) li).rightType();
			result = (X10Local_c) result.type(type);
			
			// Fold in the method's guard.
			// %%% FIXME: move method guard into context.currentConstraint
			CodeDef ci = context.currentCode();
			if (ci instanceof X10ProcedureDef) {
			    X10ProcedureDef pi = (X10ProcedureDef) ci;
				CConstraint c = Types.get(pi.guard());
				if (c != null) {
					X10TypeSystem xts = (X10TypeSystem) tc.typeSystem();

					// Substitute self for x (this local) in the guard.
//					C_Var var = new TypeTranslator(xts).trans(localInstance());
//        			Promise p = c.intern(var);
//        			System.out.println("before = " + c);
//        			c = c.substitute(p.term(), C_Special.Self);
//        			System.out.println("after = " + c);

      			    // Add the guard into the constraint for this type. 
        			Type t = result.type();

        			CConstraint dep = X10TypeMixin.xclause(t);
        			if (dep == null) dep = new CConstraint();
        			else dep = dep.copy();
//        			XTerm resultTerm = xts.xtypeTranslator().trans(result);
//        			dep.addSelfBinding((XVar) resultTerm);
        			try {
        			    dep.addIn(c);
        			} catch (XFailure e) {
        			    throw new SemanticException(e.getMessage(), position());
        			}
        			
        			t = X10TypeMixin.xclause(X10TypeMixin.baseType(t), dep);
        			
					return result.type(t);
				}
			}
			
		} catch (SemanticException z) {
			Errors.issue(tc.job(), z, this);
		}
		return result;
	}

    public static X10LocalInstance findAppropriateLocal(ContextVisitor tc, Name name) {
        X10Context context = (X10Context) tc.context();
        SemanticException error = null;
        try {
            return (X10LocalInstance) context.findLocal(name);
        } catch (SemanticException e) {
            error = e;
        }
        // If not returned yet, fake the local instance.
        X10TypeSystem_c xts = (X10TypeSystem_c) tc.typeSystem();
        X10LocalInstance li = xts.createFakeLocal(name, error);
        return li;
    }
}
