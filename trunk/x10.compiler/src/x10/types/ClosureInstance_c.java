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
import java.util.List;

import polyglot.types.ClassType;
import polyglot.types.CodeInstance;
import polyglot.types.Context;
import polyglot.types.DerefTransform;
import polyglot.types.Flags;
import polyglot.types.FunctionInstance_c;
import polyglot.types.LocalDef;
import polyglot.types.LocalInstance;
import polyglot.types.ProcedureInstance;
import polyglot.types.Ref;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.CollectionUtil;
import polyglot.util.Position;
import polyglot.util.Transformation;
import polyglot.util.TransformingList;
import x10.types.X10MethodInstance_c.NoClauseVariant;
import x10.types.constraints.CConstraint;

public class ClosureInstance_c extends FunctionInstance_c<ClosureDef> implements ClosureInstance {
    private static final long serialVersionUID= 2804222307728697502L;

    public ClosureInstance_c(TypeSystem ts, Position pos, Ref<? extends ClosureDef> def) {
        super(ts, pos, def);
    }
    
    public ClosureDef x10Def() {
	    return def();
    }
    
    FunctionType type;

    public FunctionType type() {
	    X10TypeSystem xts = (X10TypeSystem) ts;
	    assert false;
	    if (type == null) {
//		type = new ClosureType_c(xts, position(), this);
	    }
	    return type;
    }
    
    public CodeInstance<?> methodContainer() {
        return Types.get(def().methodContainer());
    }

    public ClassType typeContainer() {
        return Types.get(def().typeContainer());
    }

    public boolean closureCallValid(List<Type> actualTypes, Context context) {
        return callValid(type(), actualTypes, context);
    }
    
    public boolean callValid(Type thisType, List<Type> actualTypes, Context context) {
        return X10MethodInstance_c.callValidImpl(this, thisType, actualTypes, context);
    }

    public boolean moreSpecific(ProcedureInstance<ClosureDef> p, Context context) {
        return X10MethodInstance_c.moreSpecificImpl(this, p, context);
    }

    public String signature() {
	StringBuilder sb = new StringBuilder();
	List<String> params = new ArrayList<String>();
	if (typeParameters != null) {
	    for (int i = 0; i < typeParameters.size(); i++) {
		params.add(typeParameters.get(i).toString());
	    }
	}
	else {
	    for (int i = 0; i < def().typeParameters().size(); i++) {
		params.add(def().typeParameters().get(i).toString());
	    }
	}
	if (params.size() > 0) {
	    sb.append("[");
	    sb.append(CollectionUtil.listToString(params));
	    sb.append("]");
	}
	List<String> formals = new ArrayList<String>();
	if (formalTypes != null) {
	    for (int i = 0; i < formalTypes.size(); i++) {
		String s = "";
		String t = formalTypes.get(i).toString();
		if (formalNames != null && i < formalNames.size()) {
		    LocalInstance a = formalNames.get(i);
		    if (a != null && ! a.name().toString().equals(""))
			s = a.name() + ": " + t; 
		    else
			s = t;
		}
		else {
		    s = t;
		}
		formals.add(s);
	    }
	}
	else {
	    for (int i = 0; i < def().formalTypes().size(); i++) {
		formals.add(def().formalTypes().get(i).toString());
	    }
	}
	sb.append("(");
	sb.append(CollectionUtil.listToString(formals));
	sb.append(")");
	return sb.toString();
    }

    public String designator() {
        return def().designator();
    }

    public String toString() {
	return designator() + " " + signature() + " => " + returnType();
    }

    @Override
    public ClosureInstance returnType(Type returnType) {
        return (ClosureInstance) super.returnType(returnType);
    }
    
    @Override
    public ClosureInstance returnTypeRef(Ref<? extends Type> returnType) {
        return (ClosureInstance) super.returnTypeRef(returnType);
    }
    
    @Override
    public ClosureInstance formalTypes(List<Type> formalTypes) {
        return (ClosureInstance) super.formalTypes(formalTypes);
    }
    
    @Override
    public ClosureInstance throwTypes(List<Type> throwTypes) {
        return (ClosureInstance) super.throwTypes(throwTypes);
    }

    CConstraint guard;
    
    public CConstraint guard() {
            if (guard == null)
                    return Types.get(def().guard());
            return guard;
    }
    
    public ClosureInstance guard(CConstraint guard) {
        ClosureInstance_c n = (ClosureInstance_c) copy();
        n.guard = guard;
        return n;
    }

    /** Constraint on type parameters. */
    protected TypeConstraint typeGuard;
    public TypeConstraint typeGuard() { 
        if (typeGuard == null)
            return Types.get(def().typeGuard());
        return typeGuard;
    }
    public ClosureInstance typeGuard(TypeConstraint s) { 
        ClosureInstance_c n = (ClosureInstance_c) copy();
        n.typeGuard = s; 
        return n;
    }
    
    
    public List<Type> typeParameters;

    public List<Type> typeParameters() {
	    if (this.typeParameters == null) {
		    return new TransformingList<Ref<? extends Type>, Type>(x10Def().typeParameters(), new DerefTransform<Type>());
	    }

	    return typeParameters;
    }

    public ClosureInstance typeParameters(List<Type> typeParameters) {
	    ClosureInstance_c n = (ClosureInstance_c) copy();
	    n.typeParameters = typeParameters;
	    return n;
    }

    public List<LocalInstance> formalNames;
    
    public List<LocalInstance> formalNames() {
	if (this.formalNames == null) {
	    return new TransformingList(x10Def().formalNames(), new Transformation<LocalDef,LocalInstance>() {
		public LocalInstance transform(LocalDef o) {
		    return o.asInstance();
		}
	    });
	}
	
	return formalNames;
    }
    
    public ClosureInstance formalNames(List<LocalInstance> formalNames) {
	ClosureInstance_c n = (ClosureInstance_c) copy();
	n.formalNames = formalNames;
	return n;
    }
    
    // begin Flagged mixin
    Flags flags;
    public Flags flags() { return flags;}
    public void setFlags(Flags flags) { this.flags = flags;}
    // end Flagged mixin
}

