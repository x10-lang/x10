/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
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
import polyglot.types.MethodInstance;
import polyglot.types.ObjectType;
import polyglot.types.Package;
import polyglot.types.ProcedureInstance;
import polyglot.types.Ref;
import polyglot.types.Resolver;
import polyglot.types.SemanticException;
import polyglot.types.Name;
import polyglot.types.StructType;
import polyglot.types.Type;
import polyglot.types.TypeObject;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.types.UnknownType;
import polyglot.types.TypeSystem_c.TypeEquals;
import polyglot.util.CollectionUtil;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.util.Transformation;
import polyglot.util.TransformingList;
import polyglot.util.TypedList;
import x10.constraint.XConstraint;
import x10.constraint.XNameWrapper;
import x10.constraint.XRef_c;
import x10.constraint.XTerms;
import x10.constraint.XVar;

public class MacroType_c extends ParametrizedType_c implements MacroType {
	Ref<TypeDef> def;
	
	List<Type> typeParams;
	List<XVar> formals;
	List<Type> formalTypes;
	XConstraint guard;
	Ref<? extends Type> definedType;

	public MacroType_c(TypeSystem ts, Position pos, Ref<TypeDef> def) {
		super(ts, pos);
		this.def = def;
	}
	
	public TypeDef def() {
		return Types.get(def);
	}
	
	public boolean isGloballyAccessible() {
	    return false;
	}

	@Override
	public MacroType container(StructType container) {
		return (MacroType) super.container(container);
	}
	
	@Override
	public MacroType flags(Flags flags) {
		return (MacroType) super.flags(flags);
	}
	
	public boolean isGlobal() {
		return flags() == null ? false : ((X10Flags) flags()).isGlobal();
	}
	public boolean isProto() {
		return flags() == null ? false : ((X10Flags) flags()).isProto();
	}
	public boolean isX10Struct() {
		return ((X10TypeSystem) typeSystem()).isStructType(this);
	}
	
	public X10Type setFlags(Flags f) {
		X10Flags xf = (X10Flags) f;
		MacroType_c c = (MacroType_c) this.copy();
		if (c.flags == null)
			c.flags = X10Flags.toX10Flags(Flags.NONE);
		c.flags = xf.isGlobal() 
				? (xf.isStruct() ? ((X10Flags) c.flags).Global().Struct() : ((X10Flags) c.flags).Global())
						: ((xf.isStruct()) ? ((X10Flags) c.flags).Struct() : c.flags);
		return c;
	}
	public X10Type clearFlags(Flags f) {
		MacroType_c c = (MacroType_c) this.copy();
		if (c.flags == null)
			c.flags = X10Flags.toX10Flags(Flags.NONE);
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
			return new TransformingList<Ref<? extends Type>, Type>(def().typeParameters(), new DerefTransform<Type>());
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
		    return new TransformingList(def().formalNames(), new Transformation<LocalDef,LocalInstance>() {
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
	
	public XConstraint guard() {
		if (guard == null)
			return Types.get(def().guard());
		return guard;
	}
	
	public MacroType guard(XConstraint guard) {
		MacroType_c t = (MacroType_c) copy();
		t.guard = guard;
		return (MacroType) t;
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
		return ((X10Type) Types.get(definedType)).setFlags(flags());
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
			LocalDef li = formalNames.get(i);
			XVar v = XTerms.makeLocal(new XNameWrapper<LocalDef>(li, li.name().toString()));
			return v;
		}
	}

	public static class ParamToVarTransform implements Transformation<Ref<? extends Type>, XVar> {
		public XVar transform(Ref<? extends Type> r) {
			Type t = r.get();
			if (t instanceof ParameterType) {
				ParameterType pt = (ParameterType) t;
				return XTerms.makeLocal(new XNameWrapper<String>(pt.name().toString()));
			}
			throw new InternalCompilerError("Cannot translate non-parameter type into var.", t.position());
		}

	}
	
	public String designator() {
	    return "type";
	}
	
	public String toString() {
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
	    Ref<? extends StructType> cont = d.container();
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
		    if (! formals.get(i).equals("")) {
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
		if (base instanceof StructType) {
			return ((StructType) base).fields();
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
		if (base instanceof StructType) {
			return ((StructType) base).methods();
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
	    return Collections.EMPTY_LIST;
	}

	public boolean callValid(Type thisType, List<Type> actualTypes, Context context) {
	        return X10MethodInstance_c.callValidImpl(this, thisType, actualTypes, context);
	}
	

	public boolean moreSpecific(ProcedureInstance<TypeDef> p, Context context) {
	    return X10MethodInstance_c.moreSpecificImpl(this, p, context);
	}
	
	public Type returnType() {
	    return definedType();
	}
	public Ref<? extends Type> returnTypeRef() {
	    return definedTypeRef();
	}
	
	public MacroType returnType(Type t) {
	    return definedTypeRef(Types.ref(t));
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
	public boolean equalsNoFlag(X10Type t2) {
		return false;
	}
}
