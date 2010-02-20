/*
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
import polyglot.types.ConstructorDef_c;
import polyglot.types.Flags;
import polyglot.types.LocalDef;
import polyglot.types.QName;
import polyglot.types.Ref;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.CollectionUtil;
import polyglot.util.Position;
import polyglot.util.TypedList;

import x10.constraint.XRoot;
import x10.constraint.XTerm;
import x10.types.constraints.CConstraint;
import x10.types.constraints.TypeConstraint;

/**
 * An X10ConstructorDef_c varies from a ConstructorDef_c only in that it
 * maintains a returnType. 
 * 
 * If an explicit returnType is not declared in the constructor
 * then the returnType is simply a noClause variant of the container.
 * @author vj
 *
 */
public class X10ConstructorDef_c extends ConstructorDef_c implements X10ConstructorDef {
    Ref<? extends ClassType> returnType;
    protected Ref<CConstraint> supClause;
    protected Ref<CConstraint> guard;
    protected Ref<TypeConstraint> typeGuard;
    List<LocalDef> formalNames;

    public X10ConstructorDef_c(TypeSystem ts, Position pos,
            Ref<? extends ClassType> container,
            Flags flags, 
            Ref<? extends ClassType> returnType,
            List<Ref<? extends Type>> typeParameters,
            List<Ref<? extends Type>> formalTypes, 
            XRoot thisVar, List<LocalDef> formalNames,
            Ref<CConstraint> guard, Ref<TypeConstraint> typeGuard, 
            List<Ref<? extends Type>> throwTypes) {
        super(ts, pos, container, flags, formalTypes, throwTypes);
        this.returnType = returnType;
        this.typeParameters = TypedList.copyAndCheck(typeParameters, Ref.class, true);
        this.thisVar = thisVar;
        this.formalNames = TypedList.copyAndCheck(formalNames, LocalDef.class, true);
        this.guard = guard;
        this.typeGuard = typeGuard;
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
    
    public Ref<? extends Type> returnType() {
        return this.returnType;
    }

    public void setReturnType(Ref<? extends ClassType> r) {
        this.returnType = r;
    }

    XRoot thisVar;
    public XRoot thisVar() {
        return this.thisVar;
    }
    
    public void setThisVar(XRoot thisVar) {
        this.thisVar = thisVar;
    }

    public List<LocalDef> formalNames() {
	return Collections.unmodifiableList(formalNames);
    }

    public void setFormalNames(List<LocalDef> formalNames) {
	this.formalNames = TypedList.copyAndCheck(formalNames, LocalDef.class, true);
    }
    
    /** Constraint on superclass constructor call return type. */
    public Ref<CConstraint> supClause() {
	return supClause;
    }
    
    public void setSupClause(Ref<CConstraint> s) {
	this.supClause = s;
    }

    /** Constraint on formal parameters. */
    public Ref<CConstraint> guard() {
        return guard;
    }

    public void setGuard(Ref<CConstraint> s) {
        this.guard = s;
    }

    /** Constraint on type parameters. */
    public Ref<TypeConstraint> typeGuard() {
        return typeGuard;
    }
    
    public void setTypeGuard(Ref<TypeConstraint> s) {
        this.typeGuard = s;
    }
    
    List<Ref<? extends Type>> typeParameters;
    public List<Ref<? extends Type>> typeParameters() {
	        return Collections.unmodifiableList(typeParameters);
    }

    public void setTypeParameters(List<Ref<? extends Type>> typeParameters) {
	    this.typeParameters = TypedList.copyAndCheck(typeParameters, Ref.class, true);
    }

    public String toString() {
	    String s = designator() + " " + flags().translate() + container() + "." + signature() + (guard() != null ? guard() : "") + ": " + returnType();

	    if (!throwTypes().isEmpty()) {
		    s += " throws " + CollectionUtil.listToString(throwTypes());
	    }

	    return s;
    }

    public String signature() {
	    return "this" + (typeParameters.isEmpty() ? "" : typeParameters.toString()) + "(" + CollectionUtil.listToString(formalTypes) + ")";
    }
}
