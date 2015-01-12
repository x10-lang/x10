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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.HashSet;

import polyglot.ast.TypeNode;
import polyglot.ast.Node;
import polyglot.ast.Expr;
import polyglot.types.Flags;
import polyglot.types.LocalDef;
import polyglot.types.MethodDef_c;

import polyglot.types.Name;
import polyglot.types.QName;
import polyglot.types.Ref;

import polyglot.types.SemanticException;
import polyglot.types.Name;
import polyglot.types.ContainerType;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.types.UnknownType;
import polyglot.util.CollectionUtil; import x10.util.CollectionFactory;
import polyglot.util.Position;
import polyglot.util.TypedList;
import polyglot.visit.NodeVisitor;
import x10.constraint.XConstraint;
import x10.constraint.XFailure;
import x10.types.constraints.ConstraintManager;
import x10.constraint.XVar;
import x10.constraint.XTerm;
import x10.constraint.XVar;
import x10.types.constraints.CConstraint;
import x10.types.constraints.TypeConstraint;
import x10.types.constraints.XConstrainedTerm;

import x10.ast.X10Call_c;

public class X10MethodDef_c extends MethodDef_c implements X10MethodDef {
    private static final long serialVersionUID = -9049001281152283179L;

    Ref<CConstraint> sourceGuard; // added to support type-inferencing of guards
    Ref<CConstraint> guard;
    Ref<TypeConstraint> typeGuard;
    List<ParameterType> typeParameters;
    List<LocalDef> formalNames;
    Ref<XTerm> body;
    Ref<? extends Type> offerType;

    private HashSet<X10MethodDef_c> propertyMethodTransitivelyCalls = null; //null - haven't calculated it
    public void calcPropertyMethodTransitivelyCalls(Expr expr) {
        if (propertyMethodTransitivelyCalls==null) propertyMethodTransitivelyCalls=new HashSet<X10MethodDef_c>();
        expr.visit( new NodeVisitor() {
            @Override
            public Node override(Node n) {
                if (n instanceof X10Call_c) {
                    X10Call_c call = (X10Call_c) n;
                    X10MethodDef_c callingDef = (X10MethodDef_c) call.methodInstance().def();
                    propertyMethodTransitivelyCalls.add(callingDef);
                    if (callingDef.propertyMethodTransitivelyCalls!=null)
                        propertyMethodTransitivelyCalls.addAll(callingDef.propertyMethodTransitivelyCalls);
                }
                return null;
            }
        });
    }
    public boolean isCircularPropertyMethod(Expr expr) {
        calcPropertyMethodTransitivelyCalls(expr);
        return propertyMethodTransitivelyCalls.contains(this);
    }

    public X10MethodDef_c(TypeSystem ts, Position pos, Position errorPos,
            Ref<? extends ContainerType> container,
            Flags flags, 
            Ref<? extends Type> returnType,
            Name name,
            List<ParameterType> typeParams,
            List<Ref<? extends Type>> formalTypes,
            List<Ref<? extends Type>> throwTypes,
            ThisDef thisDef,
            List<LocalDef> formalNames,
            Ref<CConstraint> guard,
            Ref<TypeConstraint> typeGuard,
            Ref< ? extends Type> offerType,
            Ref<XTerm> body) {
        super(ts, pos, errorPos, container, flags, returnType, name, formalTypes, throwTypes);
        this.typeParameters = TypedList.copyAndCheck(typeParams, ParameterType.class, true);
        this.formalNames = TypedList.copyAndCheck(formalNames, LocalDef.class, true);
        this.sourceGuard = guard;
        this.guard = guard; // assume no guard inference for now
        this.typeGuard = typeGuard;
        this.thisDef = thisDef;
        this.body = body;
        this.offerType = offerType;
    }

    public XVar thisVar() {
        if (this.thisDef != null)
            return this.thisDef.thisVar();
        return ConstraintManager.getConstraintSystem().makeThis();
    }

    ThisDef thisDef;

    public ThisDef thisDef() {
        return this.thisDef;
    }

    public void setThisDef(ThisDef thisDef) {
        this.thisDef = thisDef;
    }

    protected XConstrainedTerm placeTerm;
    public XConstrainedTerm placeTerm() { return placeTerm; }
    public void setPlaceTerm(XConstrainedTerm pt) {
        if (placeTerm != null)
            assert (placeTerm == null);
        placeTerm = pt;
    }
    
    public Ref<? extends Type> offerType() {
    	return this.offerType;
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

    protected boolean inferGuard;
    @Override
    public boolean inferGuard() { return inferGuard; }
    @Override
    public void inferGuard(boolean r) { this.inferGuard = r; }

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
    
    /** Constraint on formal parameters. */
    public Ref<CConstraint> guard() {
        return guard;
    }

    public void setGuard(Ref<CConstraint> s) {
    	this.guard = s;
    }

    public Ref<CConstraint> sourceGuard() {
    	return sourceGuard;
    }

    public void setSourceGuard(Ref<CConstraint> s) {
    	this.sourceGuard = s;
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
    
    public List<ParameterType> typeParameters() {
	        return Collections.unmodifiableList(typeParameters);
    }

    public void setTypeParameters(List<ParameterType> typeParameters) {
	    this.typeParameters = TypedList.copyAndCheck(typeParameters, ParameterType.class, true);
    }

    public String signature() {
        StringBuilder sb = new StringBuilder(name.toString());
        if (! typeParameters.isEmpty()) {
            sb.append("[");
            boolean first = true;
            for (ParameterType p : typeParameters) {
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
                sb.append(", ");
            }
            first = false;
            if (! ((X10LocalDef) l).isUnnamed()) {
                sb.append(l.name().toString())
                    .append(":");
            }
            sb.append(l.type().get().toString());
        }
        sb.append(')');
        return sb.toString();
    }

/*    public static boolean hasVar(Type type, XVar var) {
	    if (type instanceof ConstrainedType) {
		    XConstraint rc = Types.realX(type);
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
    */
	public String toString() {
		String s = designator() + " " + flags().prettyPrint() + container() + "." + 
		signature() + (guard() != null ? guard() : "") 
		+ ":" + returnType();

		if (!throwTypes().isEmpty()) {
		    s += " throws " + CollectionUtil.listToString(throwTypes());
		}
		
		if (body != null && body.getCached() != null)
		    s += " = " + body;

		return s;
	}
}
