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
import polyglot.util.CollectionUtil; import x10.util.CollectionFactory;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.util.Transformation;
import polyglot.util.TransformingList;
import x10.types.MethodInstance_c.NoClauseVariant;
import x10.types.constraints.CConstraint;
import x10.types.constraints.TypeConstraint;

public class ClosureInstance_c extends FunctionInstance_c<ClosureDef> implements ClosureInstance {
    private static final long serialVersionUID= 2804222307728697502L;

    public ClosureInstance_c(TypeSystem ts, Position pos, Position errorPos, Ref<? extends ClosureDef> def) {
        super(ts, pos, errorPos, def);
    }
    
    public ClosureDef x10Def() {
	    return def();
    }
    
    public Ref <? extends Type> offerType() {
    	return x10Def().offerType();
    }
    
    FunctionType type;
    
    public FunctionType type() {
	    TypeSystem xts = (TypeSystem) ts;
	    assert false;
	    if (type == null) {
//		type = new ClosureType_c(xts, position(), this);
	    }
	    return type;
    }
    
    protected CodeInstance<?> methodContainer;
    
    public CodeInstance<?> methodContainer() {
        if (methodContainer == null)
            return Types.get(def().methodContainer());
        return methodContainer;
    }
    
    public ClosureInstance methodContainer(CodeInstance<?> methodContainer) {
        if (methodContainer == this.methodContainer) return this;
        ClosureInstance_c ci = (ClosureInstance_c) copy();
        ci.methodContainer = methodContainer;
        return ci;
    }
    
    protected ClassType typeContainer;
    
    public ClassType typeContainer() {
        if (typeContainer == null)
            return Types.get(def().typeContainer());
        return typeContainer;
    }
    
    public ClosureInstance typeContainer(ClassType typeContainer) {
        if (typeContainer == this.typeContainer) return this;
        ClosureInstance_c ci = (ClosureInstance_c) copy();
        ci.typeContainer = typeContainer;
        return ci;
    }
    
    public boolean closureCallValid(List<Type> actualTypes, Context context) {
        return callValid(type(), actualTypes, context);
    }
    
    public boolean callValid(Type thisType, List<Type> actualTypes, Context context) {
    	// me should have been instantiated correctly; if so, the call is valid
    	return true;
    }

    public boolean moreSpecific(Type ct, ProcedureInstance<ClosureDef> p, Context context) {
        return Types.moreSpecificImpl(ct, this, p, context);
    }

    public String signature() {
        StringBuilder sb = new StringBuilder();
        List<String> formals = new ArrayList<String>();
        List<Type> formalTypes = formalTypes();
        if (formalTypes != null) {
            List<LocalInstance> formalNames = formalNames();
            for (int i = 0; i < formalTypes.size(); i++) {
                String s = "";
                String t = formalTypes.get(i).toString();
                if (formalNames != null && i < formalNames.size()) {
                    X10LocalInstance a = (X10LocalInstance) formalNames.get(i);
                    if (a != null && ! a.x10Def().isUnnamed())
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

    public List<Type> typeParameters() {
        return Collections.emptyList();
    }

    public ClosureInstance typeParameters(List<Type> typeParameters) {
        if (typeParameters.size() != 0)
            throw new InternalCompilerError("Attempt to set type parameters of a constructor instance: "+this, this.position());
        return this;
    }

    public List<LocalInstance> formalNames;
    
    public List<LocalInstance> formalNames() {
	if (this.formalNames == null) {
	    return new TransformingList<LocalDef, LocalInstance>(x10Def().formalNames(),
	        new Transformation<LocalDef,LocalInstance>() {
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

