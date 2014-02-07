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

import polyglot.types.ConstructorDef_c;
import polyglot.types.ContainerType;
import polyglot.types.Flags;
import polyglot.types.LocalDef;
import polyglot.types.QName;
import polyglot.types.Ref;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.CollectionUtil;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.util.TypedList;
import x10.constraint.XVar;
import x10.types.constraints.CConstraint;
import x10.types.constraints.ConstraintManager;
import x10.types.constraints.TypeConstraint;
import x10.types.constraints.XConstrainedTerm;


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
    private static final long serialVersionUID = -8014698525564801656L;

    Ref<? extends Type> returnType;
    protected Type supType;
    protected Ref<CConstraint> sourceGuard;
    protected Ref<CConstraint> guard;
    protected Ref<TypeConstraint> typeGuard;
    List<LocalDef> formalNames;
    Ref<? extends Type> offerType;

    public X10ConstructorDef_c(TypeSystem ts, Position pos, Position errorPos,
            Ref<? extends ContainerType> container,
            Flags flags,
            Ref<? extends Type> returnType,
            List<Ref<? extends Type>> formalTypes,
            List<Ref<? extends Type>> throwTypes,
            ThisDef thisDef,
            List<LocalDef> formalNames, Ref<CConstraint> guard,
            Ref<TypeConstraint> typeGuard, 
            Ref<? extends Type> offerType) {
        super(ts, pos, errorPos, container, flags, formalTypes, throwTypes);
        this.returnType = returnType;
        this.formalNames = TypedList.copyAndCheck(formalNames, LocalDef.class, true);
        this.sourceGuard = guard;
        this.guard = guard; // assume no guard inference for now
        this.typeGuard = typeGuard;
        this.thisDef = thisDef;
        this.offerType = offerType;
    }

    public Ref<? extends Type> offerType() {
        return offerType;
    }

    protected boolean inferReturnType;
    public boolean inferReturnType() { return inferReturnType; }
    public void inferReturnType(boolean r) { this.inferReturnType = r; }

    protected boolean derivedReturnType;
    public boolean derivedReturnType() { return derivedReturnType; }
    public void derivedReturnType(boolean r) { this.derivedReturnType = r; }

    protected boolean inferGuard;
    @Override
    public boolean inferGuard() { return inferGuard; }
    @Override
    public void inferGuard(boolean r) { this.inferGuard = r; }

    // BEGIN ANNOTATION MIXIN
    List<Ref<? extends Type>> annotations;

    public List<Ref<? extends Type>> defAnnotations() {
        if (annotations == null) return Collections.emptyList();
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

    public void setReturnType(Ref<? extends Type> r) {
        this.returnType = r;
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
    
    protected XConstrainedTerm placeTerm;
    public XConstrainedTerm placeTerm() { return placeTerm; }
    public void setPlaceTerm(XConstrainedTerm pt) {
        if (placeTerm != null)
            assert (placeTerm == null);
        placeTerm = pt;
    }

    public List<LocalDef> formalNames() {
	return Collections.unmodifiableList(formalNames);
    }

    public void setFormalNames(List<LocalDef> formalNames) {
	this.formalNames = TypedList.copyAndCheck(formalNames, LocalDef.class, true);
    }
    
    /** Constraint on superclass constructor call return type. */
    public Type supType() {
	    return supType;
    }

    public void setSupType(Type t) {
	    this.supType = t;
    }

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
    
    public List<ParameterType> typeParameters() {
        return ((X10ParsedClassType) Types.get(container)).x10Def().typeParameters();
    }

    public void setTypeParameters(List<ParameterType> typeParameters) {
        throw new InternalCompilerError("Attempt to set type parameters on a constructor def: "+this, position());
    }
    
    public String toString() {
	    String s = designator() + " " + flags().translate() + container() + "." + signature() + (guard() != null ? guard() : "") + ": " + returnType();

	    return s;
    }

    public String signature() {
    	StringBuilder sb = new StringBuilder(TypeSystem.CONSTRUCTOR_NAME);
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
}
