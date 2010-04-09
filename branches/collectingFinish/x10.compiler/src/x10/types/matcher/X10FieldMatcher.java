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
import polyglot.util.InternalCompilerError;
import x10.constraint.XFailure;
import x10.constraint.XRoot;
import x10.constraint.XTerms;
import x10.constraint.XVar;
import x10.types.ParameterType;
import x10.types.X10Context;
import x10.types.X10FieldInstance;
import x10.types.X10TypeMixin;
import x10.types.X10TypeSystem;
import x10.types.constraints.CConstraint;

public class X10FieldMatcher extends TypeSystem_c.FieldMatcher {
    public X10FieldMatcher(Type container, Name name, Context context) {
        super(container, name, context);
    }

    @Override
    public FieldInstance instantiate(FieldInstance mi) throws SemanticException {
        X10FieldInstance fi = (X10FieldInstance) super.instantiate(mi);
        if (fi == null)
            return null;

        Type ct = container != null ? container : fi.container();
        CConstraint c = X10TypeMixin.xclause(ct);
        XVar v = X10TypeMixin.selfVarBinding(ct);
        XRoot vv = null;
        if (v == null) {
        	v = vv =XTerms.makeUQV();
        	//ct = X10TypeMixin.instantiateSelf(v,ct);
        }
        
        if (c != null) 
        	c = c.copy().instantiateSelf(v);


        X10TypeSystem ts = (X10TypeSystem) fi.typeSystem();
        XRoot oldThis = fi.x10Def().thisVar();
        if (oldThis != null && v == null && vv==null)
        	assert false;
        // TODO: vj: 08/11/09 
        // Shouldnt we be setting thisVar on the type?
        Type t = fi.type();
        Type rt = fi.rightType();
       // if (c != null && ! c.valid()) {
        	XRoot w = XTerms.makeEQV();
        	CConstraint tc = X10TypeMixin.realX(t).copy();

        	try {
        		tc.addIn(c);
        		t = X10TypeMixin.constrainedType(X10TypeMixin.baseType(t), tc);
        		t = Subst.subst(t, 
        				new XVar[] {v},
        				new XRoot[] {oldThis},
        				new Type[] {}, new ParameterType[] {});
        		if (vv != null) {
        			t = Subst.subst(t, 
            				new XVar[] {XTerms.makeEQV()},
            				new XRoot[] {vv},
            				new Type[] {}, new ParameterType[] {});
        		}
        	 } catch (XFailure z) {
        		throw new InternalCompilerError("Unexpected inconsistency.");
        	 }

        	 tc = X10TypeMixin.realX(rt).copy();
        	 try {
        		tc.addIn(c);
        		rt = X10TypeMixin.constrainedType(X10TypeMixin.baseType(rt), tc);
        		rt = Subst.subst(rt, 
        				(v != null ? new XVar[] {v} : new XVar[] { w, w}),
        				(v != null ? new XRoot[] {oldThis} : new XRoot[] { vv, oldThis}),
        				new Type[] {}, new ParameterType[] {});
        		if (vv != null) {
        			rt = Subst.subst(rt, 
            				new XVar[] {XTerms.makeEQV()},
            				new XRoot[] {vv},
            				new Type[] {}, new ParameterType[] {});
        		}
        	 } catch (XFailure z) {
        		throw new InternalCompilerError("Unexpected inconsistency.");
        	 }   
        	//rt = Subst.subst(rt, (new XVar[] { w }), (new XRoot[] { oldThis }), new Type[] {}, new ParameterType[] {});
        	//if (v != null)
        	//	rt = X10TypeMixin.setThisVar(rt, v);
       // }

        if (!ts.consistent(t, (X10Context) context) || !ts.consistent(rt, (X10Context) context)) {
        	throw new SemanticException("Type of field access is not consistent.");
        }

        return fi.type(t, rt);
    }
}