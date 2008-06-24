package polyglot.ext.x10.types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import polyglot.types.DerefTransform;
import polyglot.types.FieldInstance;
import polyglot.types.Flags;
import polyglot.types.MethodInstance;
import polyglot.types.Ref;
import polyglot.types.ReferenceType;
import polyglot.types.Resolver;
import polyglot.types.Type;
import polyglot.types.TypeObject;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.util.Transformation;
import polyglot.util.TransformingList;
import polyglot.util.TypedList;
import x10.constraint.XConstraint;
import x10.constraint.XNameWrapper;
import x10.constraint.XTerms;
import x10.constraint.XVar;

public class MacroType_c extends ParametrizedType_c implements MacroType {
	Ref<TypeDef> def;
	
	List<Type> typeParams;
	List<XVar> formals;
	List<Type> formalTypes;
	XConstraint whereClause;
	Type definedType;

	public MacroType_c(TypeSystem ts, Position pos, Ref<TypeDef> def) {
		super(ts, pos);
	}
	
	public TypeDef def() {
		return Types.get(def);
	}

	@Override
	public MacroType container(ReferenceType container) {
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

	public String name() {
		if (this.name == null) { 
			this.name = def().name();
		}
		return name;
	}

	public MacroType name(String name) {
		return (MacroType) super.name(name);
	}

	public List<Type> typeParams() {
		if (typeParams == null) {
			typeParams = new TransformingList<Ref<? extends Type>, Type>(def().typeParameters(), new DerefTransform<Type>());
		}
		return typeParams;
	}

	public MacroType typeParams(List<Type> typeParams) {
		MacroType_c t = (MacroType_c) copy();
		t.typeParams = TypedList.copyAndCheck(typeParams, Type.class, true);
		return (MacroType) t;
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
	
	public XConstraint whereClause() {
		if (whereClause == null)
			whereClause = Types.get(def().whereClause());
		return whereClause;
	}
	
	public MacroType whereClause(XConstraint whereClause) {
		MacroType_c t = (MacroType_c) copy();
		t.whereClause = whereClause;
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

		List<String> formalNames;
		List<Ref<? extends Type>> formalTypes;

		public FormalToVarTransform(List<String> formalNames, List<Ref<? extends Type>> formalTypes) {
			this.formalNames = formalNames;
			this.formalTypes = formalTypes;
		}

		public XVar transform(Integer i) {
			Ref<? extends Type> r = formalTypes.get(i);
			String name = formalNames.get(i);
			Type t = r.get();
			XVar v = XTerms.makeLocal(new XNameWrapper<String>(name));
			v.setSelfConstraint(X10TypeMixin.realX(t));
			return v;
		}
	}

	public static class ParamToVarTransform implements Transformation<Ref<? extends Type>, XVar> {
		public XVar transform(Ref<? extends Type> r) {
			Type t = r.get();
			if (t instanceof ParameterType) {
				ParameterType pt = (ParameterType) t;
				return XTerms.makeLocal(new XNameWrapper<String>(pt.name()));
			}
			throw new InternalCompilerError("Cannot translate non-parameter type into var.", t.position());
		}

	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(name);
		for (int i = 0; i < typeParams.size(); i++) {
			if (i == 0)
				sb.append("[");
			if (i != 0)
				sb.append(", ");
			sb.append(typeParams.get(i));
			if (i == typeParams.size()-1)
				sb.append("]");
		}
		for (int i = 0; i < formals.size(); i++) {
			if (i == 0)
				sb.append("(");
			if (i != 0)
				sb.append(", ");
			sb.append(formals.get(i));
			sb.append(": ");
			sb.append(formalTypes.get(i));
			if (i == formals.size()-1)
				sb.append(")");
		}
		if (whereClause != null)
			sb.append(whereClause);
		return sb.toString();
	}

	@Override
	public List<FieldInstance> fields() {
		Type base = definedType;
		if (base instanceof ReferenceType) {
			return ((ReferenceType) base).fields();
		}
		return Collections.emptyList();
	}

	@Override
	public List<Type> interfaces() {
		Type base = definedType;
		if (base instanceof ReferenceType) {
			return ((ReferenceType) base).interfaces();
		}
		return Collections.emptyList();
	}

	@Override
	public List<MethodInstance> methods() {
		Type base = definedType;
		if (base instanceof ReferenceType) {
			return ((ReferenceType) base).methods();
		}
		return Collections.emptyList();
	}

	@Override
	public Type superType() {
		Type base = definedType;
		if (base instanceof ReferenceType) {
			return ((ReferenceType) base).superType();
		}
		return null;
	}

}
