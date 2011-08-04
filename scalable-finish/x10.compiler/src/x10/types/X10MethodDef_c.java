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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import polyglot.ast.TypeNode;
import polyglot.types.Flags;
import polyglot.types.LocalDef;
import polyglot.types.MethodDef_c;
import polyglot.types.MethodInstance;
import polyglot.types.Name;
import polyglot.types.QName;
import polyglot.types.Ref;
import polyglot.types.ReferenceType;
import polyglot.types.SemanticException;
import polyglot.types.Name;
import polyglot.types.StructType;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.types.UnknownType;
import polyglot.util.CollectionUtil;
import polyglot.util.Position;
import polyglot.util.TypedList;
import x10.constraint.XConstraint;
import x10.constraint.XFailure;
import x10.constraint.XVar;
import x10.constraint.XTerm;
import x10.constraint.XVar;
import x10.types.constraints.CConstraint;
import x10.types.constraints.TypeConstraint;

/**
 * An X10ConstructorInstance_c varies from a ConstructorInstance_c only in that it
 * maintains a returnType. If an explicit returnType is not declared in the constructor
 * then the returnType is simply a noClause variant of the container.
 * @author vj
 *
 */
public class X10MethodDef_c extends MethodDef_c implements X10MethodDef {
    Ref<CConstraint> guard;
    Ref<TypeConstraint> typeGuard;
    List<Ref<? extends Type>> typeParameters;
    List<LocalDef> formalNames;
    Ref<XTerm> body;
    Ref<? extends Type> offerType;

    public X10MethodDef_c(TypeSystem ts, Position pos,
            Ref<? extends StructType> container,
            Flags flags, 
            Ref<? extends Type> returnType,
            Name name,
            List<Ref<? extends Type>> typeParams,
            List<Ref<? extends Type>> formalTypes,
            XVar thisVar,
            List<LocalDef> formalNames,
            Ref<CConstraint> guard,
            Ref<TypeConstraint> typeGuard,
            List<Ref<? extends Type>> excTypes, 
            Ref< ? extends Type> offerType,
            Ref<XTerm> body) {
        super(ts, pos, container, flags, returnType, name, formalTypes, excTypes);
        this.typeParameters = TypedList.copyAndCheck(typeParams, Ref.class, true);
        this.thisVar = thisVar;
        this.formalNames = TypedList.copyAndCheck(formalNames, LocalDef.class, true);
        this.guard = guard;
        this.typeGuard = typeGuard;
        this.body = body;
        this.offerType = offerType;
    }

    XVar thisVar;
    public XVar thisVar() {
        return this.thisVar;
    }
    

    public Ref<? extends Type> offerType() {
    	return this.offerType;
    }
    public void setThisVar(XVar thisVar) {
        this.thisVar = thisVar;
    }

    public List<LocalDef> formalNames() {
	return Collections.unmodifiableList(formalNames);
    }

    public void setFormalNames(List<LocalDef> formalNames) {
	this.formalNames = TypedList.copyAndCheck(formalNames, LocalDef.class, true);
    }

    public Ref<XTerm> body() {
        return body;
    }
    
    public void body(Ref<XTerm> body) {
	this.body = body;
    }

    protected boolean inferReturnType;
    public boolean inferReturnType() { return inferReturnType; }
    public void inferReturnType(boolean r) { this.inferReturnType = r; }

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
    
    public void setOfferType(Ref<? extends Type> s) {
        this.offerType = s;
    }
    
    public List<Ref<? extends Type>> typeParameters() {
	        return Collections.unmodifiableList(typeParameters);
    }

    public void setTypeParameters(List<Ref<? extends Type>> typeParameters) {
	    this.typeParameters = TypedList.copyAndCheck(typeParameters, Ref.class, true);
    }

    public String signature() {
        StringBuilder sb = new StringBuilder(name.toString());
        if (! typeParameters.isEmpty()) {
            sb.append("[");
            boolean first = true;
            for (Ref<? extends Type> p : typeParameters) {
                if (!first) {
                    sb.append(",");
                }
                first = false;
                sb.append(p);
            }
            sb.append("]");
        }
        sb.append('(');
        boolean first = true;
        for (LocalDef l : formalNames()) {
            if (!first) {
                sb.append(",");
            }
            first = false;
            sb.append(l.name().toString())
                .append(':')
                .append(l.type().get().toString());
        }
        sb.append(')');
        return sb.toString();
    }

    @Override
    public MethodInstance asInstance() {
        if (asInstance == null) {
            asInstance = new X10MethodInstance_c(ts, position(), Types.<X10MethodDef>ref(this));
        }
        return asInstance;
    }
    
    public static boolean hasVar(Type type, XVar var) {
	    if (type instanceof ConstrainedType) {
		    XConstraint rc = X10TypeMixin.realX(type);
		    if (rc != null && rc.hasVar(var))
			    return true;
		    ConstrainedType ct = (ConstrainedType) type;
		    if (hasVar(Types.get(ct.baseType()), var))
			    return true;
	    }
	    if (type instanceof ParametrizedType) {
		    ParametrizedType mt = (ParametrizedType) type;
		    for (Type t : mt.typeParameters()) {
			    if (hasVar(t, var))
				    return true;
		    }
		    for (XVar v : mt.formals()) {
			    if (v.hasVar(var))
				    return true;
		    }
	    }
	    return false;
    }
    
	public String toString() {
		String s = designator() + " " + X10Flags.toX10Flags(flags()).prettyPrint() + container() + "." + 
		signature() + (guard() != null ? guard() : "") 
		+ ": " + returnType();

		if (!throwTypes().isEmpty()) {
			s += " throws " + CollectionUtil.listToString(throwTypes());
		}
		
		if (body != null && body.getCached() != null)
		    s += " = " + body;

		return s;
	}

	public boolean isProto() {
		return X10Flags.toX10Flags(flags()).isProto();
	}

    public Object copy() {
        X10MethodDef_c copy = (X10MethodDef_c) super.copy();
        copy.asInstance = null;
        return copy;
    }
}