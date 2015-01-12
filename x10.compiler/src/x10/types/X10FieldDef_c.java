/**
 * 
 */
/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

package x10.types;

import java.util.Collections;
import java.util.List;

import polyglot.types.FieldDef_c;
import polyglot.types.Flags;
import polyglot.types.Name;
import polyglot.types.QName;
import polyglot.types.Ref;
import polyglot.types.ContainerType;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.Position;
import polyglot.util.TypedList;
import x10.types.constraints.ConstraintManager;
import x10.constraint.XVar;
import x10.types.constraints.TypeConstraint;


public class X10FieldDef_c extends FieldDef_c implements X10FieldDef {
    private static final long serialVersionUID = 6359052056959695361L;

    boolean isProperty;
    
    public X10FieldDef_c(TypeSystem ts, Position pos,
            Ref<? extends ContainerType> container,
            Flags flags, 
            Ref<? extends Type> type,
            Name name, ThisDef thisDef) {
        super(ts, pos, container, flags, type, name);
        this.isProperty = false;
        this.thisDef = thisDef;
    }
    
    public XVar thisVar() {
        if (this.thisDef != null)
            return this.thisDef.thisVar();
        return ConstraintManager.getConstraintSystem().makeThis(); // Why #this instead of this?
    }

    ThisDef thisDef;

    public ThisDef thisDef() {
        return this.thisDef;
    }

    public void setThisDef(ThisDef thisDef) {
        this.thisDef = thisDef;
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
    
    public boolean isProperty() {
        return isProperty;
    }
    
    public void setProperty() {
        isProperty = true;        
    }
    /** A field declaration cannot have a type guard.
     * 
     */
    public Ref<TypeConstraint> typeGuard() {
    	return null;
    }
}
