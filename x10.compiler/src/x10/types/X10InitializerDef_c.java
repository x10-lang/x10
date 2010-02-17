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

import polyglot.types.ClassType;
import polyglot.types.Flags;
import polyglot.types.InitializerDef_c;
import polyglot.types.QName;
import polyglot.types.Ref;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.Position;
import polyglot.util.TypedList;
import x10.constraint.XRoot;

public class X10InitializerDef_c extends InitializerDef_c implements X10InitializerDef {
    XRoot thisVar;

    public X10InitializerDef_c(TypeSystem ts, Position pos, Ref<? extends ClassType> container, Flags flags, XRoot thisVar) {
        super(ts, pos, container, flags);
        this.thisVar = thisVar;
    }

    public void setThisVar(XRoot thisVar) {
        this.thisVar = thisVar;
    }

    public XRoot thisVar() {
        return thisVar;
    }

    // BEGIN ANNOTATION MIXIN
    List<Ref<? extends Type>> annotations;

    public List<Ref<? extends Type>> defAnnotations() {
        if (annotations == null)
            return Collections.EMPTY_LIST;
        return Collections.unmodifiableList(annotations);
    }

    public void setDefAnnotations(List<Ref<? extends Type>> annotations) {
        this.annotations = TypedList.<Ref<? extends Type>> copyAndCheck(annotations, Ref.class, true);
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

    /** An X10 Initializer cannot have an explicit typeguard.
     * 
     */
    public Ref<TypeConstraint> typeGuard() {
    	return null;
    }
}
