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

package x10.types.matcher;

import polyglot.types.Context;
import polyglot.types.FieldInstance;
import polyglot.types.Name;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem_c;
import polyglot.types.Types;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import x10.constraint.XFailure;
import x10.constraint.XVar;
import x10.constraint.XTerms;
import x10.constraint.XVar;
import x10.errors.Errors;
import x10.types.ParameterType;
import polyglot.types.Context;
import x10.types.X10FieldInstance;
import polyglot.types.TypeSystem;
import x10.types.constraints.CConstraint;

public class X10FieldMatcher extends TypeSystem_c.FieldMatcher {
	boolean contextKnowsReceiver;
    public X10FieldMatcher(Type container, Name name, Context context) {
       this(container, false, name, context);
    }
    public X10FieldMatcher(Type container, boolean p, Name name, Context context) {
        super(container, name, context);
        this.contextKnowsReceiver = p;
    }


    @Override
    public FieldInstance instantiate(FieldInstance mi) throws SemanticException {          
	    if (! mi.name().equals(name)) {
		return null;
	    }
        X10FieldInstance fi = (X10FieldInstance) mi;
        TypeSystem ts = (TypeSystem) fi.typeSystem();
        Type t = fi.type();
        Type rt = fi.rightType();
        
        // Now need to figure out the type of the field, from the declaration of the field
        // in the container, and the type of the container.
        // The task is to transfer constraints from the target to the field.
        Type ct = container != null ? container : fi.container();
        CConstraint c = Types.xclause(ct);
        
        // Let v be the symbolic name for the target. If there is none, we make one up.
        // Let t = T{tc}, and ct = U{c}. 
        // If c does not have a selfVarBinding, then we want to set t to
        // t = T{exists vv. (tc,this==vv),ct[vv/self]}
        // If c does have a selfVarBinding, v, then we want to set t to
        // t = T{exists v. (tc, this=v, ct)}
        XVar v = Types.selfVarBinding(ct);
        XVar vv = null;
        if (v == null) {
        	v = vv =XTerms.makeUQV();
        }
        XVar oldThis = fi.x10Def().thisVar();
        if (oldThis != null && v == null && vv==null)
        	assert false;
        /*if (c != null) 
        	c = c.copy().instantiateSelf(v);*/

        { // Update t 
            CConstraint tc = Types.realX(t).copy();

            if (! contextKnowsReceiver)
                tc.addIn(v, c);

            t = Types.constrainedType(Types.baseType(t), tc);
            if (! tc.consistent()) {
                throw new Errors.InconsistentType(t, fi.position());
            }
            t = Subst.subst(t, 
                            new XVar[] {v},
                            new XVar[] {oldThis},
                            new Type[] {}, new ParameterType[] {});
            if (vv != null) { // Hide vv, i.e. substitute in an anonymous EQV
                t = Subst.subst(t, 
                                new XVar[] {XTerms.makeEQV()},
                                new XVar[] {vv},
                                new Type[] {}, new ParameterType[] {});
            }
        }

        { // Update rt
            CConstraint tc = Types.realX(rt).copy();

            if (! contextKnowsReceiver)
                tc.addIn(v, c);
            rt = Types.constrainedType(Types.baseType(rt), tc);
            if (! tc.consistent()) {
                throw new Errors.InconsistentType(t, Position.COMPILER_GENERATED);
            }
            XVar w = XTerms.makeEQV();
            rt = Subst.subst(rt, 
                             (v != null ? new XVar[] {v} : new XVar[] { w, w}),
                             (v != null ? new XVar[] {oldThis} : new XVar[] { vv, oldThis}),
                             new Type[] {}, new ParameterType[] {});
            if (vv != null) {
                rt = Subst.subst(rt, 
                                 new XVar[] {XTerms.makeEQV()},
                                 new XVar[] {vv},
                                 new Type[] {}, new ParameterType[] {});
            }
        } 
        
        //rt = Subst.subst(rt, (new XVar[] { w }), (new XVar[] { oldThis }), new Type[] {}, new ParameterType[] {});
        //if (v != null)
        //	rt = X10TypeMixin.setThisVar(rt, v);
        // }

        if (!ts.consistent(t, (Context) context) || !ts.consistent(rt, (Context) context)) {
        	throw new SemanticException("Type of field access is not consistent.");
        }

        return fi.type(t, rt);
    }
}