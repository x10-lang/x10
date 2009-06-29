/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package polyglot.ext.x10.types;

import java.util.Collections;
import java.util.List;

import polyglot.types.ArrayType;
import polyglot.types.ClassType;
import polyglot.types.FieldInstance;
import polyglot.types.MethodInstance;
import polyglot.types.Name;
import polyglot.types.Named;
import polyglot.types.NullType;
import polyglot.types.ObjectType;
import polyglot.types.PrimitiveType;
import polyglot.types.QName;
import polyglot.types.Ref;
import polyglot.types.ReferenceType_c;
import polyglot.types.Resolver;
import polyglot.types.SemanticException;
import polyglot.types.StructType;
import polyglot.types.Type;
import polyglot.types.UnknownType;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.util.Transformation;
import polyglot.util.TransformingList;
import x10.constraint.XConstraint;
import x10.constraint.XConstraint_c;
import x10.constraint.XFailure;
import x10.constraint.XRoot;
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
		assert !(baseType.known() && baseType.getCached() instanceof UnknownType) :
			" baseType is " + baseType;
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
		final Type base = baseType.get();
		if (base instanceof ObjectType) {
		    List<Type> l = ((ObjectType) base).interfaces();
		        XConstraint c = constraint.get();
		        final XTerm t = c.bindingForVar(c.self());
		        if (t != null) {
		            return new TransformingList<Type, Type>(l, new Transformation<Type, Type>() {
		                public Type transform(Type o) {
		                    X10TypeSystem xts = (X10TypeSystem) o.typeSystem();
		                    XConstraint c2 = X10TypeMixin.xclause(o);
		                    c2 = c2 != null ? c2.copy() : new XConstraint_c();
		                    try {
		                        c2.addSelfBinding(t);
		                        return X10TypeMixin.xclause(o, c2);
		                    }
		                    catch (XFailure e) {
		                    }
		                    return o;
		                }
		            });
		        }
		        return l;
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
		    Type o = ((ObjectType) base).superClass();
		    if (o != null) {
		    XConstraint c = constraint.get();
		    final XTerm t = c.bindingForVar(c.self());
		    if (t != null) {
		        XConstraint c2 = X10TypeMixin.xclause(o);
		        c2 = c2 != null ? c2.copy() : new XConstraint_c();
		        try {
		            X10TypeSystem xts = (X10TypeSystem) o.typeSystem();
		            c2.addSelfBinding(t);
		            return X10TypeMixin.xclause(o, c2);
		        }
		        catch (XFailure e) {
		        }
		    }
		    }
		    return o;
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
