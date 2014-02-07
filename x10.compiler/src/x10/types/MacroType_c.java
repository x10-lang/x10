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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import polyglot.types.ConstructorDef;
import polyglot.types.Context;
import polyglot.types.DerefTransform;
import polyglot.types.FieldInstance;
import polyglot.types.Flags;
import polyglot.types.LocalDef;
import polyglot.types.LocalInstance;
import polyglot.types.MemberInstance;

import polyglot.types.ObjectType;
import polyglot.types.Package;
import polyglot.types.ProcedureInstance;
import polyglot.types.Ref;
import polyglot.types.Resolver;
import polyglot.types.SemanticException;
import polyglot.types.Name;
import polyglot.types.ContainerType;
import polyglot.types.Type;
import polyglot.types.TypeObject;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.types.UnknownType;
import polyglot.types.UpcastTransform;
import polyglot.types.TypeSystem_c.TypeEquals;
import polyglot.util.CollectionUtil; import x10.util.CollectionFactory;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.util.Transformation;
import polyglot.util.TransformingList;
import polyglot.util.TypedList;
import x10.types.constraints.ConstraintManager;
import x10.constraint.XVar;
import x10.types.constraints.CConstraint;
import x10.types.constraints.TypeConstraint;


/**
 * Represents a type definition
 * 
 * @author njnystrom
 */
public class MacroType_c extends ParametrizedType_c implements MacroType {
	private static final long serialVersionUID = 2467878434635627679L;
	
	Ref<TypeDef> def;
	
	List<Type> typeParams;
	List<XVar> formals;
	List<Type> formalTypes;
	CConstraint guard;
	Ref<? extends Type> definedType;

	public MacroType_c(TypeSystem ts, Position pos, Ref<TypeDef> def) {
		super(ts, pos, pos);
		this.def = def;
	}
	
	public TypeDef def() {
		return Types.get(def);
	}
	
	public Ref<? extends Type> offerType() {
		return null;
	}
	public boolean isGloballyAccessible() {
	    if (container != null && !container.isGloballyAccessible())
	        return false;
	    return flags().isPublic();
	}

	@Override
	public MacroType container(ContainerType container) {
		return (MacroType) super.container(container);
	}
	
	@Override
	public MacroType flags(Flags flags) {
		return (MacroType) super.flags(flags);
	}

	public Type setFlags(Flags xf) {
		MacroType_c c = (MacroType_c) this.copy();
		if (c.flags == null)
			c.flags = Flags.NONE;
		c.flags = (xf.isStruct()) ? c.flags.Struct() : c.flags;
		return c;
	}
	public Type clearFlags(Flags f) {
		MacroType_c c = (MacroType_c) this.copy();
		if (c.flags == null)
			c.flags = Flags.NONE;
		c.flags = c.flags.clear(f);
		return c;
	}
	@Override
	public boolean equalsImpl(TypeObject t) {
		if (t instanceof MacroType) {
			return super.equalsImpl(t);
		}
		return false;
	}
	
	public String translate(Resolver c) {
		return definedType().translate(c);
	}

	public Name name() {
		if (this.name == null) { 
			return def().name();
		}
		return name;
	}

	public MacroType name(Name name) {
		return (MacroType) super.name(name);
	}

	public List<Type> typeParameters() {
		if (typeParams == null) {
			return new TransformingList<ParameterType, Type>(def().typeParameters(), new UpcastTransform<Type, ParameterType>());
		}
		return typeParams;
	}

	public MacroType typeParameters(List<Type> typeParams) {
		MacroType_c t = (MacroType_c) copy();
		t.typeParams = TypedList.copyAndCheck(typeParams, Type.class, true);
		return (MacroType) t;
	}
	
	public MacroType newTypeParameters(List<Type> typeParams) {
	    return typeParameters(typeParams);
	}
	
	    public List<LocalInstance> formalNames;
	    
	    public List<LocalInstance> formalNames() {
		if (this.formalNames == null) {
		    return new TransformingList<LocalDef, LocalInstance>(def().formalNames(),
		        new Transformation<LocalDef,LocalInstance>() {
		            public LocalInstance transform(LocalDef o) {
		                return o.asInstance();
		            }
		    });
		}
		
		return formalNames;
	    }
	    
	    public MacroType formalNames(List<LocalInstance> formalNames) {
		MacroType_c n = (MacroType_c) copy();
		n.formalNames = formalNames;
		return n;
	    }
	
	public boolean hasFormals(List<Type> formalTypes, Context context) {
	        return CollectionUtil.allElementwise(this.formalTypes(), formalTypes, new TypeEquals(context));
	}
	
	public List<XVar> formals() {
		if (formals == null) {
			List<Integer> indices = new ArrayList<Integer>();
			for (int i = 0; i < def().formalNames().size(); i++) {
				indices.add(i);
			}
			return new TransformingList<Integer, XVar>(indices, new FormalToVarTransform(def().formalNames(), def().formalTypes()));
		}
		return formals;
	}

	public MacroType formals(List<XVar> formals) {
		MacroType_c t = (MacroType_c) copy();
		t.formals = TypedList.copyAndCheck(formals, XVar.class, true);
		return (MacroType) t;
	}

	public List<Type> formalTypes() {
		if (formalTypes == null) {
			return new TransformingList<Ref<? extends Type>, Type>(def().formalTypes(),
										      new DerefTransform<Type>());
		}
		return formalTypes;
	}

	public MacroType formalTypes(List<Type> formalTypes) {
		MacroType_c t = (MacroType_c) copy();
		t.formalTypes = TypedList.copyAndCheck(formalTypes, Type.class, true);
		return (MacroType) t;
	}

	public MacroType newFormalTypes(List<Type> formalTypes) {
	    return formalTypes(formalTypes);
	}

    // todo: this guard&typeGuard is duplicated code similar to ProcedureInstance_c
	public CConstraint guard() {
		if (guard == null)
			return Types.get(def().guard());
		return guard;
	}
	
	public MacroType guard(CConstraint guard) {
		MacroType_c t = (MacroType_c) copy();
		t.guard = guard;
		return (MacroType) t;
	}

    public boolean checkConstraintsAtRuntime() { return false; }
    public MacroType_c checkConstraintsAtRuntime(boolean check) {
        throw new RuntimeException("The guard for a MacroType cannot be dynamically checked (at runtime)");
    }
	
	    /** Constraint on type parameters. */
	    protected TypeConstraint typeGuard;
	    public TypeConstraint typeGuard() { 
	        if (typeGuard == null)
	            return Types.get(def().typeGuard());
	        return typeGuard; }
	    public MacroType typeGuard(TypeConstraint s) { 
	        MacroType_c n = (MacroType_c) copy();
	        n.typeGuard = s; 
	        return n;
	    }


	public Type definedType() {
		if (definedType == null)
			return Types.get(def().definedType());
		Ref<? extends Type> dt = definedType;
		definedType = Types.ref(typeSystem().unknownType(position())); // guard against recursion
		Type t = Types.processFlags(flags(), Types.get(dt));
		definedType = dt;
		return t;
	}
	public MacroType definedType(Type t) {
	    return definedTypeRef(Types.ref(t));
	}
	public Ref<? extends Type> definedTypeRef() {
	    if (definedType == null)
	        return def().definedType();
	    return definedType;
	}

	public MacroType definedTypeRef(Ref<? extends Type> definedType) {
		MacroType_c t = (MacroType_c) copy();
		t.definedType = definedType;
		return t;
	}

    public static class FormalToVarTransform implements Transformation<Integer, XVar> {

		List<LocalDef> formalNames;
		List<Ref<? extends Type>> formalTypes;

		public FormalToVarTransform(List<LocalDef> formalNames, List<Ref<? extends Type>> formalTypes) {
			this.formalNames = formalNames;
			this.formalTypes = formalTypes;
		}

		public XVar transform(Integer i) {
			final Ref<? extends Type> r = formalTypes.get(i);
			X10LocalDef li = (X10LocalDef) formalNames.get(i);
			XVar v = ConstraintManager.getConstraintSystem().makeLocal(li);
			return v;
		}
	}

	public static class ParamToVarTransform implements Transformation<Ref<? extends Type>, XVar> {
		public XVar transform(Ref<? extends Type> r) {
			Type t = r.get();
			if (t instanceof ParameterType) {
				ParameterType pt = (ParameterType) t;
				// TODO: Replace with XTerms.makeUQV(pt.name().toString());
				return ConstraintManager.getConstraintSystem().makeUQV(pt.name().toString());
				// return XTerms.makeLocal(new XNameWrapper<String>(pt.name().toString()));
			}
			throw new InternalCompilerError("Cannot translate non-parameter type into var.", t.position());
		}

	}
	
	public String designator() {
	    return "type";
	}
	
	public String typeToString() {
	    StringBuilder sb = new StringBuilder();
	    sb.append(signature());
	    if (definedType != null && definedType.known()) {
	        sb.append(" = ");
	        sb.append(definedType);
	    }
	    return sb.toString();
	}

	public String signature() {
	    StringBuffer sb = new StringBuffer();
	    TypeDef d = def.getCached();
	    Ref<? extends ContainerType> cont = d.container();
	    if (cont != null) {
		Type t = cont.getCached();
		if (t != null) {
		    sb.append(t);
		    sb.append(".");
		}
	    }
	    else {
		Ref<? extends Package> pkg = d.package_();
		if (pkg != null) {
		    Package p = pkg.getCached();
		    if (p != null) {
		        sb.append(p);
			sb.append(".");
		    }
		}
	    }
	    sb.append(d.name());
	    if (typeParams != null && typeParams.size() > 0) {
	        sb.append("[");
		for (int i = 0; i < typeParams.size(); i++) {
		    if (i != 0)
			sb.append(", ");
		    sb.append(typeParams.get(i));
		}
		sb.append("]");
	    }
	    if (formals != null && formalTypes != null && formals.size() > 0) {
	        sb.append("(");
		for (int i = 0; i < formals.size(); i++) {
		    if (i != 0)
			sb.append(", ");
		    if (! formals.get(i).toString().equals("")) {
			sb.append(formals.get(i));
			sb.append(": ");
		    }
		    sb.append(formalTypes.get(i));
		}
		sb.append(")");
	    }
	    if (guard != null)
	    	sb.append(guard);
	    return sb.toString();
	}

	@Override
	public List<FieldInstance> fields() {
		Type base = definedType();
		if (base instanceof ContainerType) {
			return ((ContainerType) base).fields();
		}
		return Collections.emptyList();
	}

	@Override
	public List<Type> interfaces() {
		Type base = definedType();
		if (base instanceof ObjectType) {
			return ((ObjectType) base).interfaces();
		}
		return Collections.emptyList();
	}

	@Override
	public List<MethodInstance> methods() {
		Type base = definedType();
		if (base instanceof ContainerType) {
			return ((ContainerType) base).methods();
		}
		return Collections.emptyList();
	}

	@Override
	public Type superClass() {
		Type base = definedType();
		if (base instanceof ObjectType) {
			return ((ObjectType) base).superClass();
		}
		return null;
	}
	
	public List<Type> throwTypes() {
	    return Collections.<Type>emptyList();
	}

	public boolean callValid(Type thisType, List<Type> actualTypes, Context context) {
		// this should have been instantiated correctly; if so, the call is valid
		return true;
	}
	

	public boolean moreSpecific(Type ct, ProcedureInstance<TypeDef> p, Context context) {
	    return Types.moreSpecificImpl(ct, this, p, context);
	}
	
	public Type returnType() {
	    return definedType();
	}
	public Ref<? extends Type> returnTypeRef() {
	    return definedTypeRef();
	}
	
	public MacroType returnType(Type t) {
	    return definedType(t);
	}
	
	public MacroType returnTypeRef(Ref<? extends Type> t) {
	    return definedTypeRef(t);
	}
	
	public MacroType throwTypes(List<Type> throwTypes) {
	    return this;
	}

	public boolean throwsSubset(ProcedureInstance<TypeDef> pi) {
	    return true;
	}
	public boolean equalsNoFlag(Type t2) {
		return false;
	}
}
