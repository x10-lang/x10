/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.types;

import java.util.Collections;
import java.util.List;

import polyglot.types.ArrayType;
import polyglot.types.ClassType;
import polyglot.types.FieldInstance;
import polyglot.types.Flags;
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
import polyglot.types.Types;
import polyglot.types.UnknownType;
import polyglot.util.CodeWriter;
import polyglot.util.InternalCompilerError;
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

/**
 * 09/11/09
 * A ConstrainedType_c represents the type T{c}. It has a basetype (of type Ref<? extends Type>)
 * and a constraint (of type Ref<XConstraint>).
 * 
 * @author njnystrom
 * @author vj
 *
 */
public class ConstrainedType_c extends ReferenceType_c implements ConstrainedType {
	private Ref<x10.constraint.XConstraint> constraint;
	private Ref<? extends Type> baseType;

	public ConstrainedType_c(X10TypeSystem ts, Position pos, 
			Ref<? extends Type> baseType, Ref<x10.constraint.XConstraint> constraint) {
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
	
	/**
	 * Check that the basetype and constraint agree on thisVar.
	 */
	public XVar thisVar() {
		if (realXClause == null)
			realXClause = realX();
		if (! realXClause.consistent())
			return null;
		return realXClause.thisVar();
	}
	
	public Ref<? extends Type> baseType() {
		return baseType;
	}
	
	public X10Type clearFlags(Flags f) {
		ConstrainedType_c c = (ConstrainedType_c) this.copy();
		X10Type t = (X10Type) Types.get(c.baseType);
		if (t==null)
			throw new InternalCompilerError("Cannot remove flags " + f + " from null type.");
		t = t.clearFlags(f);
		c.baseType = Types.ref(t);
	//	((Ref<Type>)c.baseType).update(t);
		return c;
	}
	
	public X10Type setFlags(Flags f) {
		ConstrainedType_c c = (ConstrainedType_c) this.copy();
		X10Type t = (X10Type) Types.get(c.baseType);
		if (t==null)
			throw new InternalCompilerError("Cannot set flags " + f + " on null type.");
		t = t.setFlags(f);
		//((Ref<Type>)c.baseType).update(t);
		return c;
	}
	
	public Flags flags() {
		X10Type t = (X10Type) Types.get(this.baseType);
		assert t != null : "Cannot get flags on null type.";
		if (t==null)
			throw new InternalCompilerError("Cannot get flags on null type.");
		return t.flags();
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
	
	public XConstraint getRealXClause() { 
		if (realXClause == null) {
			realXClause = realX();
		}
		return realXClause; 
	}
	/*public void setRealXClause(XConstraint c, SemanticException error) {
		this.realXClause = c;
		this.realClauseInvalid = error;
	}*/
	
	protected XConstraint realX() {
		// Now get the root clause and join it with the dep clause.
		XConstraint rootClause = X10TypeMixin.realX(Types.get(this.baseType()));
		if (rootClause == null)
			assert rootClause != null;

		XConstraint depClause = X10TypeMixin.xclause(this);

		try {
			X10TypeMixin.getThisVar(rootClause, depClause);
		} catch (XFailure z) {
			try {
				rootClause = rootClause.copy().addIn(depClause);
			} catch (XFailure z1) {
			}
			rootClause.setInconsistent();
			return rootClause;
		}
		if (depClause == null) 
			return rootClause;

		XConstraint realClause = rootClause.copy();

		try {
			realClause.addIn(depClause);
			realClause.setThisVar(XConstraint_c.getThisVar(rootClause, depClause));
		}
		catch (XFailure f) {
			realClause.setInconsistent();
		}
	
		return realClause;

	}
	
	public void checkRealClause() throws SemanticException {
		// Force real clause to be computed.
		if (realXClause == null)
			realXClause = realX();
		if (! realXClause.consistent()) {
			if (realClauseInvalid != null) {
				realClauseInvalid = new SemanticException(this 
						+ " has an inconsistent real clause " + realXClause);

			}
			throw realClauseInvalid;
		}
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
		return baseType.getCached() + constraintString(); // + constraint.getCached();
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

	// vj 08/11/09
	// For each FieldInstance fi of baseType, need to return a new FieldInstance fi' obtained
	// by adding this: this.constraint.
	@Override
	public List<FieldInstance> fields() {
		Type base = baseType.get();
		XConstraint c = getRealXClause();
		final XVar thisVar = thisVar();
		/*try {
		c = c.substitute(thisVar, c.self());
		} catch (XFailure f) {
		    throw new InternalCompilerError("Unexpected failure when substituting thisVar() for self in " + c);
		}*/
		final XConstraint cc = c;
		if (base instanceof StructType) {
			final List<FieldInstance> fis = ((StructType) base).fields();
			return fis;
			/*return new TransformingList<FieldInstance, FieldInstance>(fis, new Transformation<FieldInstance, FieldInstance>() {
				public FieldInstance transform(FieldInstance o) {
					assert o instanceof X10FieldInstance;
					X10FieldInstance xo = (X10FieldInstance) o;
					Type t = xo.rightType();
					// Now need to add the constainer's constraint.
					t = X10TypeMixin.addConstraint(t, cc);
					
					FieldInstance o1 = o.type(t);
					return o1;
				}		
			});*/

		}
		return Collections.emptyList();
	}
	
	public void ensureSelfBound() {
		assert constraint != null;
		XVar self = X10TypeMixin.selfVarBinding(this);
		if (self == null) {
			self = XConstraint_c.genEQV();
			XConstraint c = constraint.get();
			try {
			c.addSelfBinding(self);
			} catch (XFailure z) {
				// cannot happen
			}
			constraint.update(c);
		}
	}

	// vj: Revised substantially 08/11/09
	
	@Override
	public List<Type> interfaces() {
		final Type base = baseType.get();
		if (! (base instanceof ObjectType))
			return Collections.emptyList();

		List<Type> l = ((ObjectType) base).interfaces();
		XConstraint c = constraint.get();
		// Get or make a name tt for self.
		XTerm t = c.bindingForVar(c.self());
		if (t == null) {
			t = XConstraint_c.genEQV(true);
			
		}
		final XTerm tt = t;

		return new TransformingList<Type, Type>(l, new Transformation<Type, Type>() {
			public Type transform(Type o) {
				X10TypeSystem xts = (X10TypeSystem) o.typeSystem();
				XConstraint c2 = X10TypeMixin.xclause(o);
				c2 = c2 != null ? c2.copy() : new XConstraint_c();
				try {
					if (c2.thisVar() != null)
						c2.addBinding(c2.thisVar(), tt);
					//c2.addSelfBinding(tt);
					//  c2.substitute(tt, XTerms.)
					return X10TypeMixin.xclause(o, c2);
				}
				catch (XFailure e) {
				}
				return o;
			}
		});
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
	
	
	public boolean isX10Struct() {
		X10Type base = (X10Type) baseType.get();
		return base.isX10Struct();
	}
	
	/*public boolean isRooted() {
		X10Type base = (X10Type) baseType.get();
		return base.isRooted();
	}*/
	public boolean isProto() {
		X10Type base = (X10Type) baseType.get();
		return base.isProto();
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
	public boolean equalsNoFlag(X10Type t2) {
		return false;
	}

}
