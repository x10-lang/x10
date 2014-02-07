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

import polyglot.types.ClassType;
import polyglot.types.Flags;
import polyglot.types.LocalInstance;
import polyglot.types.Name;
import polyglot.types.QName;
import polyglot.types.Ref;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.types.VarDef_c;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.util.TypedList;
import x10.constraint.XTerm;
import x10.types.constraints.ConstraintManager;
import x10.constraint.XVar;
import x10.types.constraints.TypeConstraint;


public class ThisDef_c extends VarDef_c implements ThisDef {
    private static final long serialVersionUID = 8939235355633300017L;

    public ThisDef_c(TypeSystem ts, Position pos, 
                     Ref<? extends ClassType> qType,
                     Ref<? extends ClassType> baseType) {
        super(ts, pos, Flags.FINAL, baseType, ThisDef.THIS);
        ClassType bt = Types.get(baseType);
        XVar baseVar = ConstraintManager.getConstraintSystem().makeThis(bt);  
        thisVar =  qType==null?  baseVar
                : ConstraintManager.getConstraintSystem().makeQualifiedVar(qType.get(), baseVar);
    }

    public String toString() {
        return "this : " + type;
    }

    @Override
    public ThisDef_c copy() {
        ThisDef_c res = (ThisDef_c) super.copy();
        res.asInstance = null;
        return res;
    }

    @Override
    public void setConstantValue(x10.types.constants.ConstantValue constantValue) {
        super.setConstantValue(constantValue);
        this.asInstance = null;
    }

    @Override
    public void setFlags(Flags flags) {
        super.setFlags(flags);
        this.asInstance = null;
    }

    @Override
    public void setName(Name name) {
        super.setName(name);
        this.asInstance = null;
    }

    @Override
    public void setNotConstant() {
        super.setNotConstant();
        this.asInstance = null;
    }

    @Override
    public void setType(Ref<? extends Type> type) {
        super.setType(type);
        this.asInstance = null;
    }

    private XVar thisVar;
    public XVar thisVar() {
        return this.thisVar;
    }
    public void setThisVar(XVar thisVar) {
        this.thisVar = thisVar;
    }

    @Override
    public  Ref<? extends Type> type() {
    	 Ref<? extends Type> result = type;
    	 return result;
    }

    private ThisInstance asInstance;

    public ThisInstance asInstance() {
        if (asInstance == null) {
            asInstance = ts.createThisInstance(position(), Types.ref(this));
        }
        return asInstance;
    }

    // BEGIN ANNOTATION MIXIN
    public List<Ref<? extends Type>> defAnnotations() {
        return Collections.<Ref<? extends Type>>emptyList();
    }
    
    public void setDefAnnotations(List<Ref<? extends Type>> annotations) {
        if (!annotations.isEmpty())
            throw new InternalCompilerError("Adding annotations to 'this'");
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

    /**
     * 'this' cannot have a type guard.
     */
    public Ref<TypeConstraint> typeGuard() {
    	return null;
    }
}
