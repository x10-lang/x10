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

import polyglot.types.ConstructorDef;
import polyglot.types.ConstructorInstance;
import polyglot.types.ConstructorInstance_c;
import polyglot.types.Context;
import polyglot.types.DerefTransform;
import polyglot.types.ErrorRef_c;
import polyglot.types.LocalDef;
import polyglot.types.LocalInstance;
import polyglot.types.MethodDef;
import polyglot.types.ProcedureInstance;
import polyglot.types.Ref;

import polyglot.types.SemanticException;
import polyglot.types.ContainerType;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.types.UpcastTransform;
import polyglot.util.CollectionUtil; import x10.util.CollectionFactory;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.util.Transformation;
import polyglot.util.TransformingList;
import x10.types.constraints.CConstraint;
import x10.types.constraints.TypeConstraint;


/**
 * An X10ConstructorInstance_c varies from a ConstructorInstance_c only in that it
 * maintains a returnType, and a guard. 
 * 
 * <p>If an explicit returnType is not declared in the constructor
 * then the returnType is simply the container together with the constraints 
 * introduced by the property call in the body of the constructor (if the container has
 * properties).
 * 
 * <p> It also has a typeParameteres() method. This currently returns null. 
 * Constructor definitions may not specify type parameters. The type parameters of
 * the container are intended to be in effect in the constructor declaration.
 * 
 *  
 * @author vj
 *
 */
public class X10ConstructorInstance_c extends ConstructorInstance_c implements X10ConstructorInstance {
    private static final long serialVersionUID = 65438556574848648L;

    public X10ConstructorInstance_c(TypeSystem ts, Position pos, Position errorPos,
    		Ref<? extends X10ConstructorDef> def) {
        super(ts, pos, errorPos, def);
    }
    
    @Override
    public boolean moreSpecific(Type ct, ProcedureInstance<ConstructorDef> p, Context context) {
        return Types.moreSpecificImpl(ct, this, p, context);
    }

    public X10ConstructorDef x10Def() {
        return (X10ConstructorDef) def();
    }
    
    public Ref<? extends Type> offerType() {
    	return x10Def().offerType();
    }
    
    public List<Type> annotations() {
        return X10TypeObjectMixin.annotations(this);
    }

    public List<Type> annotationsMatching(Type t) {
        return X10TypeObjectMixin.annotationsMatching(this, t);
    }

    /* (non-Javadoc)
     * @see x10.types.X10ConstructorInstance#depClause()
     */
   // public CConstraint constraint() { return Types.realX(returnType()); }

    public Ref<? extends Type> returnType;
    
    public Ref<? extends Type> returnTypeRef() { 
        if (returnType == null) {
            return x10Def().returnType();
        }
	return returnType;
    }
    
    public Type returnType() { 
        if (returnType == null) {
            return x10Def().returnType().get();
        }
        return Types.get(returnType);
    }
    
    public X10ConstructorInstance returnType(Type retType) {
        return returnTypeRef(Types.ref(retType));
    }
    public X10ConstructorInstance returnTypeRef(Ref<? extends Type> retType) {
        if (retType == this.returnType) return this;
        X10ConstructorInstance_c n = (X10ConstructorInstance_c) copy();
        n.returnType = retType;
        return n;
    }

    @Override
    public X10ConstructorInstance container(ContainerType container) {
        if (container == this.container) return this;
        return (X10ConstructorInstance) super.container(container);
    }
    
    
    @Override
    public boolean callValid(Type thisType, List<Type> argTypes, Context context) {
    	// this should have been instantiated correctly; if so, the call is valid
    	return true;
    }
    
    public List<Type> typeParameters() {
        return Collections.<Type>emptyList();
// [IP] TODO
//        return new TransformingList<ParameterType, Type>(((X10ParsedClassType) this.container()).x10Def().typeParameters(), new UpcastTransform<Type, ParameterType>());
    }

    public X10ConstructorInstance typeParameters(List<Type> typeParameters) {
        if (typeParameters.size() != 0)
            throw new InternalCompilerError("Attempt to set type parameters of a constructor instance: "+this, this.position());
        return this;
// [IP] TODO
//        if (typeParameters.size() != x10Def().typeParameters().size())
//            throw new InternalCompilerError("Attempt to set incorrect number of type parameters of a constructor instance: "+this+" params: "+typeParameters, this.position());
//        // Set the container's type parameters instead
//        return (X10ConstructorInstance) this.container(((X10ParsedClassType) this.container()).typeArguments(typeParameters));
    }

    public List<LocalInstance> formalNames;
    
    public List<LocalInstance> formalNames() {
	if (this.formalNames == null) {
		// Why is this not cached in this.formalNames?
	    return new TransformingList<LocalDef, LocalInstance>(x10Def().formalNames(),
	        new Transformation<LocalDef,LocalInstance>() {
	            public LocalInstance transform(LocalDef o) {
	                return o.asInstance();
	            }
	    });
	}
	
	return formalNames;
    }
    
    public X10ConstructorInstance formalNames(List<LocalInstance> formalNames) {
        if (formalNames == this.formalNames) return this;
        X10ConstructorInstance_c n = (X10ConstructorInstance_c) copy();
        n.formalNames = formalNames;
        return n;
    }

    @Override
    public X10ConstructorInstance formalTypes(List<Type> formalTypes) {
        if (formalTypes == this.formalTypes) return this;
        return (X10ConstructorInstance) super.formalTypes(formalTypes);
    }

    private SemanticException error;

    public SemanticException error() {
        return error;
    }

    public X10ConstructorInstance error(SemanticException e) {
        if (e == this.error) return this;
        X10ConstructorInstance_c n = (X10ConstructorInstance_c) copy();
        n.error = e;
        return n;
    }

    public String toString() {
	    String s = designator() + " " + flags().prettyPrint() + signature();

	
	    return s;
    }
    
    public String signature() {
        StringBuilder sb = new StringBuilder();
        sb.append(container().toString());
        sb.append(".");
        sb.append(TypeSystem.CONSTRUCTOR_NAME);
        // [IP] Constructors don't have type parameters, they inherit them from the container.
        //List<String> params = new ArrayList<String>();
        //List<Type> typeParameters = typeParameters();
        //if (typeParameters != null) {
        //    for (int i = 0; i < typeParameters.size(); i++) {
        //        params.add(typeParameters.get(i).toString());
        //    }
        //}
        //if (params.size() > 0) {
        //    sb.append("[");
        //    sb.append(CollectionUtil.listToString(params));
        //    sb.append("]");
        //}
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
        CConstraint guard = guard();
        if (guard != null)
            sb.append(guard);
        else if (x10Def().guard() != null)
            sb.append(x10Def().guard());
        TypeConstraint typeGuard = this.typeGuard();
        if (typeGuard != null)
            sb.append(typeGuard);
        else if (x10Def().typeGuard() != null)
            sb.append(x10Def().typeGuard());
        Ref<? extends Type> returnType = returnTypeRef();
        if (returnType != null && returnType.known()) {
            sb.append(": ");
            sb.append(returnType);
        }
        return sb.toString();
    }
    
    public boolean isValid() {
        return !(def instanceof ErrorRef_c<?>);
    }
}
