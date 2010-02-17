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

package x10.types;

import java.util.Collections;
import java.util.List;

import polyglot.types.FieldDef_c;
import polyglot.types.Flags;
import polyglot.types.Name;
import polyglot.types.QName;
import polyglot.types.Ref;
import polyglot.types.StructType;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.Position;
import polyglot.util.TypedList;
import x10.constraint.XRoot;

/**
 * An X10ConstructorInstance_c varies from a ConstructorInstance_c only in that it
 * maintains a returnType. If an explicit returnType is not declared in the constructor
 * then the returnType is simply a noClause variant of the container.
 * @author vj
 *
 */
public class X10FieldDef_c extends FieldDef_c implements X10FieldDef {
    boolean isProperty;
    XRoot thisVar;
    
    public X10FieldDef_c(TypeSystem ts, Position pos,
            Ref<? extends StructType> container,
            Flags flags, 
            Ref<? extends Type> type,
            Name name, XRoot thisVar) {
        super(ts, pos, container, flags, type, name);
        this.isProperty = false;
        this.thisVar = thisVar;
    }
    
    public XRoot thisVar() {
        return this.thisVar;
    }

    public void setThisVar(XRoot thisVar) {
        this.thisVar = thisVar;
    }

    // BEGIN ANNOTATION MIXIN
    List<Ref<? extends Type>> annotations;

    public List<Ref<? extends Type>> defAnnotations() {
	if (annotations == null) return Collections.EMPTY_LIST;
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
