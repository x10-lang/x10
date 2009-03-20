/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created on Nov 26, 2004
 *
 */
package polyglot.ext.x10.types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import polyglot.ext.x10.types.constr.Constraint;
import polyglot.main.Report;
import polyglot.types.FieldInstance;
import polyglot.types.Resolver;
import polyglot.types.Type;
import polyglot.types.TypeObject;
import polyglot.types.TypeSystem;
import polyglot.util.CodeWriter;
import polyglot.util.Position;


/**
 * Implementation of Future type constructor.
 *
 * <p>This code does not yet implement Termination Equivalence for Future Types, i.e.
 * future future X = future X
 * future nullable future X = future nullable X
 * are not yet implemented.
 *
 * @author vj
 */
public class FutureType_c extends X10ReferenceType_c implements FutureType {
	protected X10NamedType base;
	protected List methods;
	protected X10TypeSystem xts;
	protected FutureType_c() {}

	/** We type base to Type instead of X10Type because we wish to reuse code from
	 * jl which traffics in Type's. We rely on the global invariant, maintained manually
	 * in this version of the code, that at runtime the only Type's created are X10Type's.

	 * @param ts
	 * @param pos
	 * @param base
	 */
	public FutureType_c(TypeSystem ts, Position pos, X10NamedType base) {
		super( ts, pos);
		this.base = base;
		this.methods = new ArrayList(2);
		// [IP] FIXME: is the cast below necessary?
		this.methods.add(((X10TypeSystem) ts).methodInstance(position(),
				this,
				ts.Public(),
				base,
				"force",
				Collections.EMPTY_LIST,
				Collections.EMPTY_LIST));
		this.methods.add(((X10TypeSystem) ts).methodInstance(position(),
				this,
				ts.Public(),
				ts.Boolean(),
				"forced",
				Collections.EMPTY_LIST,
				Collections.EMPTY_LIST));
		xts = (X10TypeSystem) ts;
	}

	public Constraint realClause() {
		return null;
	}
	public boolean isFuture() {
		return true;
	}

	public FutureType toFuture() {
		return this;
	}
	public boolean propertiesElaborated() { return true; }
	public String name() { return base.name();}
	public String fullName() { return base.fullName();}

	/* The only methods are: public <base> force() and public boolean forced().
	 * @see polyglot.types.ReferenceType#methods()
	 */
	public List methods() {
		return this.methods;
	}

	/* This type has no fields.
	 * @see polyglot.types.ReferenceType#fields()
	 */
	public List fields() {
		return Collections.EMPTY_LIST;
	}

	/* The supertype is the base type of the type system.
	 * @see polyglot.types.ReferenceType#superType()
	 */
	public Type superType() {
		return ((X10TypeSystem) ts).X10Object();
	}

	/* This type implements no interfaces.
	 * @see polyglot.types.ReferenceType#interfaces()
	 */
	public List interfaces() {
		return Collections.EMPTY_LIST;
	}

	/*
	 * @see polyglot.types.Type#translate(polyglot.types.Resolver)
	 */
	public String translate(Resolver c) {
		// return "future " + base().translate( c );
		// return "x10.lang.Future /*" + base().translate( c ) + "*/";
		return "x10.lang.Future";
	}

	public void print(CodeWriter w) {
		// [IP] FIXME: is this the right thing to do here?
		w.write("x10.lang.Future");
	}

	/*
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return toStringForDisplay();
	}
	public String toStringForDisplay() {
		return "future<" + base().toStringForDisplay( ) +">";
	}

	/* Return the base type.
	 * @see polyglot.ext.x10.types.FutureType#base()
	 */
	public X10Type base() {
		return this.base;
	}

	/* This type has no fields.
	 * @see polyglot.types.ReferenceType#fieldNamed(java.lang.String)
	 */
	public FieldInstance fieldNamed(String name) {
		return null;
	}

	public boolean isImplicitCastValidImpl(Type origType) {
		if (Report.should_report("debug", 5)) {
			Report.report(5, "[FutureType_c] |" + this + "|.isImplicitCastValidImpl(|" + origType +"|):");
		}

		X10Type toType = (X10Type) origType;
		NullableType targetType = toType.toNullable();
		if (targetType != null) {
			toType = targetType.base();
		}

		FutureType target = toType.toFuture();
		if (target != null) {
			boolean result = base().isImplicitCastValidImpl( target.base() );
			if (Report.should_report("debug", 5)) {
				Report.report(5, "[FutureType_c] ..." + result);
			}
			return result;
		}

		// Otherwise do the default check
		boolean result = super.isImplicitCastValidImpl(toType);
		if (Report.should_report("debug", 5)) {
			Report.report(5, "[FutureType_c] ... "+result);
		}
		return result;
	}

	public boolean isCastValidImpl(Type origType) {
		if (Report.should_report("debug", 5)) {
			Report.report(5, "[FutureType_c] |" + this + "|.isCastValidImpl(|" + origType +"|):");
		}

		X10Type toType = (X10Type) origType;
		NullableType nullType = toType.toNullable();
		if (nullType != null)
			toType = nullType.base();

		FutureType target = X10Type_c.toFuture(toType);
		if (target != null) {
			boolean result = base().isCastValidImpl( target.base() );
			if (Report.should_report("debug", 5)) {
				Report.report(5, "[FutureType_c] ... target=|" + target + "|.");
				Report.report(5, "[FutureType_c] ... returns |" + result + "|.");
			}
			return result;
		}

		// Otherwise do the default check
		boolean result = super.isCastValidImpl(toType);
		if (Report.should_report("debug", 5)) {
			Report.report(5, "[FutureType_c] ... "+result);
		}
		return result;
	}


	public boolean typeEqualsImpl(Type o) {
		return equalsImpl(o);
	}
	public int hashCode() {
		return
		(rootType == this ? base.hashCode() << 2 : rootType.hashCode() )
		+ (depClause != null ? depClause.hashCode() : 0)
		+ ((typeParameters !=null && ! typeParameters.isEmpty()) ? typeParameters.hashCode() :0);
	}
	public boolean equalsImpl(TypeObject t) {
		if (! (t instanceof FutureType))
			return false;
		FutureType other = (FutureType) t;
		return ts.typeEquals(base(), other.base());
	}

	/** Returns true iff the type is canonical. */
	public boolean isCanonical() {
		return base().isCanonical();
	}

	/**
	 * future<T> has no properties.
	 */
	public List<FieldInstance> properties() {
		return Collections.EMPTY_LIST;
	}
	public List<FieldInstance> definedProperties() {
		return Collections.EMPTY_LIST;
	}
	public NullableType toNullable() {
		return X10Type_c.toNullable(this);
	}
	public boolean safe() {
		return false;
	}

	public boolean isValue() {
		return true;
	}
}

