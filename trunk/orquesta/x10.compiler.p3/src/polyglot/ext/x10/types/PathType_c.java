package polyglot.ext.x10.types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import polyglot.ext.x10.types.MacroType_c.FormalToVarTransform;
import polyglot.types.Def;
import polyglot.types.DerefTransform;
import polyglot.types.FieldInstance;
import polyglot.types.Flags;
import polyglot.types.MethodInstance;
import polyglot.types.ObjectType;
import polyglot.types.Ref;
import polyglot.types.ReferenceType;
import polyglot.types.Resolver;
import polyglot.types.SemanticException;
import polyglot.types.Name;
import polyglot.types.StructType;
import polyglot.types.Type;
import polyglot.types.TypeObject;
import polyglot.types.TypeSystem;
import polyglot.types.Type_c;
import polyglot.types.Types;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.util.TransformingList;
import polyglot.util.TypedList;
import x10.constraint.XConstraint;
import x10.constraint.XConstraint_c;
import x10.constraint.XFailure;
import x10.constraint.XSelf;
import x10.constraint.XTerms;
import x10.constraint.XVar;

public class PathType_c extends ParametrizedType_c implements PathType {
	TypeProperty prop;
	XVar base;
	Type baseType;

	public PathType_c(TypeSystem ts, Position pos, XVar base, Type baseType, TypeProperty prop) {
		super(ts, pos);
		this.prop = prop;
		this.base = base;
		this.baseType = baseType;
	}

	public boolean isGloballyAccessible() {
	    return false;
	}
	
	@Override
	public PathType container(StructType container) {
		return (PathType) super.container(container);
	}
	
	@Override
	public PathType flags(Flags flags) {
		return (PathType) super.flags(flags);
	}
	
	public Name name() {
		if (this.name == null) { 
			this.name = def().name();
		}
		return this.name;
	}
	
	@Override
	public PathType name(Name name) {
		return (PathType) super.name(name);
	}
	
	@Override
	public boolean equalsImpl(TypeObject t) {
		if (t instanceof PathType) {
			return super.equalsImpl(t);
		}
		return false;
	}

	public XVar base() {
		return base;
	}
	
	public Type baseType() {
		return baseType;
	}
	
	public PathType base(XVar base, Type baseType) {
		PathType_c t = (PathType_c) copy();
		t.base = base;
		return t;
	}
	
	public static XVar pathBase(Type t) {
		t = X10TypeMixin.xclause(t, (XConstraint) null);
		if (t instanceof PathType) {
			PathType pt = (PathType) t;
			return pt.base();
		}
		return null;
	}
	
	public static Type pathBase(Type t, XVar base, Type baseType) {
		XConstraint c = X10TypeMixin.xclause(t);
		t = X10TypeMixin.xclause(t, (XConstraint) null);
		if (t instanceof PathType) {
			PathType pt = (PathType) t;
			return X10TypeMixin.xclause(pt.base(base, baseType), c);
		}
		return t;
	}

	public TypeProperty def() {
		return prop;
	}

	public TypeProperty property() {
		return prop;
	}
	
	public StructType container() {
		return Types.get(prop.container());
	}

	public List<Type> typeParameters() {
		return Collections.EMPTY_LIST;
	}

	public List<XVar> formals() {
		return Collections.<XVar>singletonList(base());
	}

	public List<Type> formalTypes() {
		return Collections.<Type>singletonList(baseType());
	}

	public XConstraint guard() {
		return null;
	}
	
	public PathType typeParameters(List<Type> typeParams) {
		assert typeParams.size() == 0;
		return this;
	}
	public PathType newTypeParameters(List<Type> typeParams) {
	    return typeParameters(typeParams);
	}

	public PathType formals(List<XVar> formals) {
		assert formals.size() == 1;
		PathType_c t = (PathType_c) copy();
		t.base = formals.get(0);
		return t;
	}

	public PathType formalTypes(List<Type> formalTypes) {
		assert formalTypes.size() == 1;
		PathType_c t = (PathType_c) copy();
		t.baseType = formalTypes.get(0);
		return t;
	}
	
	public PathType newFormalTypes(List<Type> formalTypes) {
	    return formalTypes(formalTypes);
	}
	
	@Override
	public String translate(Resolver c) {
		assert false : "Cannot translate " + this;
		return "";
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(base());
		sb.append(".");
		sb.append(name());
		return sb.toString();
	}

	@Override
	public List<FieldInstance> fields() {
		Type base = lowerBound();
		if (base instanceof StructType) {
			return ((StructType) base).fields();
		}
		return Collections.emptyList();
	}

	@Override
	public List<Type> interfaces() {
		Type base = lowerBound();
		if (base instanceof ObjectType) {
			return ((ObjectType) base).interfaces();
		}
		return Collections.emptyList();
	}

	@Override
	public List<MethodInstance> methods() {
		Type base = lowerBound();
		if (base instanceof StructType) {
			return ((StructType) base).methods();
		}
		return Collections.emptyList();
	}

	@Override
	public Type superClass() {
		Type base = lowerBound();
		if (base instanceof ObjectType) {
			return ((ObjectType) base).superClass();
		}
		return null;
	}

	public Type lowerBound() {
		return null;
	}
	
	public Type upperBound() {
		return null;
	}
}
