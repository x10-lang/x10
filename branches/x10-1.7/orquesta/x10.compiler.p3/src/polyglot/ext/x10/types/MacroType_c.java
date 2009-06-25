package polyglot.ext.x10.types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
	Type definedType;

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
	
	@Override
	public boolean equalsImpl(TypeObject t) {
		if (t instanceof MacroType) {
			return super.equalsImpl(t);
		}
		return false;
	}
	
	public String translate(Resolver c) {
		return definedType.translate(c);
	}

	public Name name() {
		if (this.name == null) { 
			this.name = def().name();
		}
		return name;
	}

	public MacroType name(Name name) {
		return (MacroType) super.name(name);
	}

	public List<Type> typeParameters() {
		if (typeParams == null) {
			typeParams = new TransformingList<Ref<? extends Type>, Type>(def().typeParameters(), new DerefTransform<Type>());
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
		    this.formalNames = new TransformingList(def().formalNames(), new Transformation<LocalDef,LocalInstance>() {
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
	
	public boolean hasFormals(List<Type> formalTypes) {
	        return CollectionUtil.allElementwise(this.formalTypes(), formalTypes, new TypeEquals());
	}
	
	public List<XVar> formals() {
		if (formals == null) {
			List<Integer> indices = new ArrayList<Integer>();
			for (int i = 0; i < def().formalNames().size(); i++) {
				indices.add(i);
			}
			formals = new TransformingList<Integer, XVar>(indices, new FormalToVarTransform(def().formalNames(), def().formalTypes()));
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
			formalTypes = new TransformingList<Ref<? extends Type>, Type>(def().formalTypes(),
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
			guard = Types.get(def().guard());
		return guard;
	}
	
	public MacroType guard(XConstraint guard) {
		MacroType_c t = (MacroType_c) copy();
		t.guard = guard;
		return (MacroType) t;
	}

	public Type definedType() {
		if (definedType == null)
			definedType = Types.get(def().definedType());
		return definedType;
	}

	public MacroType definedType(Type definedType) {
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
			v.setSelfConstraint(new XRef_c<XConstraint>() { public XConstraint compute() { return X10TypeMixin.realX(r.get()); } });
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
	        return signature();
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
			if (p.name().equals(X10TypeSystem.DUMMY_PACKAGE_CLASS_NAME)) {
			    sb.append(p.prefix());
			}
			else {
			    sb.append(p);
			}
			sb.append(".");
		    }
		}
	    }
	    sb.append(d.name());
	    if (typeParams != null) {
		for (int i = 0; i < typeParams.size(); i++) {
		    if (i == 0)
			sb.append("[");
		    if (i != 0)
			sb.append(", ");
		    sb.append(typeParams.get(i));
		    if (i == typeParams.size()-1)
			sb.append("]");
		}
	    }
	    if (formals != null && formalTypes != null) {
		for (int i = 0; i < formals.size(); i++) {
		    if (i == 0)
			sb.append("(");
		    if (i != 0)
			sb.append(", ");
		    if (! formals.get(i).equals("")) {
			sb.append(formals.get(i));
			sb.append(": ");
		    }
		    sb.append(formalTypes.get(i));
		    if (i == formals.size()-1)
			sb.append(")");
		}
	    }
	    if (guard != null)
	    	sb.append(guard);
	    return sb.toString();
	}

	@Override
	public List<FieldInstance> fields() {
		Type base = definedType;
		if (base instanceof StructType) {
			return ((StructType) base).fields();
		}
		return Collections.emptyList();
	}

	@Override
	public List<Type> interfaces() {
		Type base = definedType;
		if (base instanceof ObjectType) {
			return ((ObjectType) base).interfaces();
		}
		return Collections.emptyList();
	}

	@Override
	public List<MethodInstance> methods() {
		Type base = definedType;
		if (base instanceof StructType) {
			return ((StructType) base).methods();
		}
		return Collections.emptyList();
	}

	@Override
	public Type superClass() {
		Type base = definedType;
		if (base instanceof ObjectType) {
			return ((ObjectType) base).superClass();
		}
		return null;
	}
	
	public List<Type> throwTypes() {
	    return Collections.EMPTY_LIST;
	}

	public boolean callValid(Type thisType, List<Type> actualTypes) {
	        return X10MethodInstance_c.callValidImpl(this, thisType, actualTypes);
	}
	
	public boolean moreSpecific(ProcedureInstance<TypeDef> pi) {
	        ProcedureInstance<TypeDef> p1 = this;
	        ProcedureInstance<TypeDef> p2 = pi;

	        // rule 1:
	        Type t1 = null;
	        Type t2 = null;
	        
	        if (p1 instanceof MemberInstance) {
	            t1 = ((MemberInstance<TypeDef>) p1).container();
	        }
	        if (p2 instanceof MemberInstance) {
	            t2 = ((MemberInstance<TypeDef>) p2).container();
	        }
	        
	        if (t1 != null && t2 != null) {
	            if (t1.isClass() && t2.isClass()) {
	                if (! t1.isSubtype(t2) &&
	                        ! t1.toClass().isEnclosed(t2.toClass())) {
	                    return false;
	                }
	            }
	            else {
	                if (! t1.isSubtype(t2)) {
	                    return false;
	                }
	            }
	        }

	        // rule 2:
	        return p2.callValid(t1, p1.formalTypes());
	}
	
	public Type returnType() {
	    return definedType();
	}
	
	public MacroType returnType(Type t) {
	    return definedType(t);
	}
	
	public MacroType throwTypes(List<Type> throwTypes) {
	    return this;
	}

	public boolean throwsSubset(ProcedureInstance<TypeDef> pi) {
	    return true;
	}
}
