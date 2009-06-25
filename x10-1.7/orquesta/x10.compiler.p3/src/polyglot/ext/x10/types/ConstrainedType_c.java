package polyglot.ext.x10.types;

import java.util.Collections;
import java.util.List;

import polyglot.types.ArrayType;
import polyglot.types.ClassType;
import polyglot.types.FieldInstance;
import polyglot.types.MethodInstance;
import polyglot.types.Named;
import polyglot.types.NullType;
import polyglot.types.ObjectType;
import polyglot.types.PrimitiveType;
import polyglot.types.QName;
import polyglot.types.Ref;
import polyglot.types.ReferenceType;
import polyglot.types.ReferenceType_c;
import polyglot.types.Resolver;
import polyglot.types.SemanticException;
import polyglot.types.Name;
import polyglot.types.StructType;
import polyglot.types.Type;
import polyglot.types.UnknownType;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import x10.constraint.XConstraint;
import x10.constraint.XSelf;
import x10.constraint.XTerm;
import x10.constraint.XTerms;
import x10.constraint.XVar;

public class ConstrainedType_c extends ReferenceType_c implements ConstrainedType {
	private Ref<x10.constraint.XConstraint> constraint;
	private Ref<? extends Type> baseType;

	public ConstrainedType_c(X10TypeSystem ts, Position pos, Ref<? extends Type> baseType, Ref<x10.constraint.XConstraint> constraint) {
		super(ts, pos);
		assert ts != null;
		this.baseType = baseType;
		assert !(baseType.known() && baseType.getCached() instanceof UnknownType);
		this.constraint = constraint;
	}

	public boolean isGloballyAccessible() {
	    return false;
	}
	
	public Ref<? extends Type> baseType() {
		return baseType;
	}
	
	public ConstrainedType baseType(Ref<? extends Type> baseType) {
		ConstrainedType_c n = (ConstrainedType_c) copy();
		n.baseType = baseType;
		return n;
	}
	
	public Ref<x10.constraint.XConstraint> constraint() {
		return constraint;
	}
	
	public ConstrainedType constraint(Ref<x10.constraint.XConstraint> constraint) {
		ConstrainedType_c n = (ConstrainedType_c) copy();
		n.constraint = constraint;
		return n;
	}
	
	protected XConstraint realXClause;
	protected SemanticException realClauseInvalid;
	
	public XConstraint getRealXClause() { return realXClause; }
	public void setRealXClause(XConstraint c, SemanticException error) {
		this.realXClause = c;
		this.realClauseInvalid = error;
	}
	
	public void checkRealClause() throws SemanticException {
		// Force real clause to be computed.
		X10TypeMixin.realX(this);
		if (realClauseInvalid != null)
			throw realClauseInvalid;
	}
	
	@Override
	public String translate(Resolver c) {
		return baseType().get().translate(c);
	}

	public boolean safe() {
		return ((X10Type) baseType.get()).safe();
	}

	@Override
	public String toString() {
		return "" + baseType.getCached() + constraintString(); // + constraint.getCached();
	}
	
	private String constraintString() {
		StringBuilder sb = new StringBuilder();
		Type base = baseType.getCached();
		XConstraint c = constraint.getCached();
		if (base instanceof X10ClassType) {
		    X10ClassType ct = (X10ClassType) base;
		    if (ct.typeProperties().size() > 0) {
			StringBuilder sb2 = new StringBuilder();
			String sep = "";
			for (TypeProperty p : ct.x10Def().typeProperties()) {
			    XVar v = p.asVar();
			    XTerm b = c.bindingForVar(v);
			    if (b != null) {
				sb2.append(sep);
				sb2.append(b);
				sep = ", ";
				c = c.removeVarBindings(v);
			    }
			}
			if (sb2.length() > 0) {
			    sb.append("[");
			    sb.append(sb2);
			    sb.append("]");
			}
		    }
		    if (ct.definedProperties().size() > 0) {
			StringBuilder sb2 = new StringBuilder();
			String sep = "";
			for (FieldInstance p : ct.definedProperties()) {
			    XVar v = XTerms.makeField(XSelf.Self, XTerms.makeName(p, p.name().toString()));
			    XTerm b = c.bindingForVar(v);
			    if (b != null) {
				sb2.append(sep);
				sb2.append(b);
				sep = ", ";
				c = c.removeVarBindings(v);
			    }
			}
			if (sb2.length() > 0) {
			    sb.append("(");
			    sb.append(sb2);
			    sb.append(")");
			}

		    }
		}
		if (c != null && ! c.valid()) {
			sb.append(c);
		}
		return sb.toString();
	}

	@Override
	public List<FieldInstance> fields() {
		Type base = baseType.get();
		if (base instanceof StructType) {
			return ((StructType) base).fields();
		}
		return Collections.emptyList();
	}

	@Override
	public List<Type> interfaces() {
		Type base = baseType.get();
		if (base instanceof ObjectType) {
			return ((ObjectType) base).interfaces();
		}
		return Collections.emptyList();
	}

	@Override
	public List<MethodInstance> methods() {
		Type base = baseType.get();
		if (base instanceof StructType) {
			return ((StructType) base).methods();
		}
		return Collections.emptyList();
	}

	@Override
	public Type superClass() {
		Type base = baseType.get();
		if (base instanceof ObjectType) {
			return ((ObjectType) base).superClass();
		}
		return null;
	}

	public QName fullName() {
		Type base = baseType.get();
		if (base instanceof Named) {
			return ((Named) base).fullName();
		}
		return null;
	}

	public Name name() {
		Type base = baseType.get();
		if (base instanceof Named) {
			return ((Named) base).name();
		}
		return null;
	}
	
	@Override
	public boolean isPrimitive() {
		Type base = baseType.get();
		return base.isPrimitive();
	}
	@Override
	public boolean isClass() {
		Type base = baseType.get();
		return base.isClass();
	}
	@Override
	public boolean isNull() {
		Type base = baseType.get();
		return base.isNull();
	}
	@Override
	public boolean isArray() {
		Type base = baseType.get();
		return base.isArray();
	}
	@Override
	public boolean isReference() {
		Type base = baseType.get();
		return base.isReference();
	}
	
	@Override
	public PrimitiveType toPrimitive() {
		Type base = baseType.get();
		return base.toPrimitive();
	}
	
	@Override
	public ClassType toClass() {
		Type base = baseType.get();
		return base.toClass();
	}
	
	@Override
	public NullType toNull() {
		Type base = baseType.get();
		return base.toNull();
	}

	@Override
	public ArrayType toArray() {
		Type base = baseType.get();
		return base.toArray();
	}
	
	public void print(CodeWriter w) {
		Type base = baseType.get();
		base.print(w);
	}

}
