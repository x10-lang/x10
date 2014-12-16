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

package x10.types;

import java.util.Collections;
import java.util.List;

import polyglot.types.Flags;
import polyglot.types.LocalDef_c;
import polyglot.types.LocalInstance;
import polyglot.types.QName;
import polyglot.types.Ref;
import polyglot.types.Name;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.types.VarDef_c.ConstantValue;
import polyglot.util.Position;
import polyglot.util.TypedList;
import x10.constraint.XTerm;
import x10.types.constraints.TypeConstraint;

public class X10LocalDef_c extends LocalDef_c implements X10LocalDef {
    private static final long serialVersionUID = 1790685273653374213L;

    // Added to support fake localdef's introduced into the ast to represent
    // variables that should not be subject to flow analysis.
    boolean hidden = false;
    public boolean hidden() { return hidden; }
    public static X10LocalDef_c makeHidden(TypeSystem ts, Position pos,
            Flags flags, 
            Ref<? extends Type> type,
            Name name)  {
    	X10LocalDef_c x = new X10LocalDef_c(ts, pos, flags, type, name);
    	x.hidden = true;
    	return x;
    }
    public X10LocalDef_c(TypeSystem ts, Position pos,
            Flags flags, 
            Ref<? extends Type> type,
            Name name) {
        super(ts, pos, flags, type, name);
        // TODO: Add the {self==name} constraint to the type
    }

    public static final Name UNNAMED = Name.make("?");
    private boolean isUnnamed = false;
    public void setUnnamed() { isUnnamed = true; }
    public boolean isUnnamed() { return isUnnamed; }
    
    private boolean isAsyncInit = false;
    public void setAsyncInit() { isAsyncInit = true; }
    public boolean isAsyncInit() { return isAsyncInit; }
    
    public String toString() {
	ConstantValue cv = constantRef.getCached();
	String cvStr = "";
	
	if (cv != null && cv.isConstant()) {
		Object v = cv.value();
		if (v instanceof String) {
			String s = (String) v;
	
			if (s.length() > 8) {
				s = s.substring(0, 8) + "...";
			}
	
			v = "\"" + s + "\"";
		}
	
		cvStr = " = " + v;
	}
	
	Name name = isUnnamed ? UNNAMED : this.name;
	return "local " + flags.translate() + name + ": " + type + cvStr;
    }

    private XTerm placeTerm;
    public XTerm placeTerm() { return placeTerm;}
    // FIXME Yoav: keep only the first is a bad strategy because these place terms are used in other types, and other constructs (like AtStmt_c)
    public void setPlaceTerm(XTerm pt) {
    	if (placeTerm == null)
    		placeTerm = pt;
    	//else 
    	//	assert placeTerm == pt;
    }
    
    @Override
    public  Ref<? extends Type> type() {
    	 Ref<? extends Type> result = type;
    	 return result;
    	
    }
    @Override
	 public LocalInstance asInstance() {
	        if (asInstance == null) {
	            asInstance = ts.createLocalInstance(position(), Types.ref(this));
	        }
	        return asInstance;
	    }
    // BEGIN ANNOTATION MIXIN
    List<Ref<? extends Type>> annotations;

    public List<Ref<? extends Type>> defAnnotations() {
	if (annotations == null) return Collections.<Ref<? extends Type>>emptyList();
        return Collections.unmodifiableList(annotations);
    }
    
    public void setDefAnnotations(List<Ref<? extends Type>> annotations) {
        this.annotations = TypedList.<Ref<? extends Type>>copyAndCheck(annotations, Ref.class, true);
    }
    
    public List<Type> annotations() {
        return X10TypeObjectMixin.annotations(this);
    }
    
    public List<Type> annotationsMatching(Type t) {
        return X10TypeObjectMixin.annotationsMatching(this, t);
    }
    
    public List<Type> annotationsNamed(QName fullName) {
        return X10TypeObjectMixin.annotationsNamed(this, fullName);
    }
    // END ANNOTATION MIXIN
    /** An X10 Local definition cannot have a type guard.
     * 
     */
    public Ref<TypeConstraint> typeGuard() {
    	return null;
    }
}
