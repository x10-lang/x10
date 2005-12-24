/*
 * Created on Nov 26, 2004
 *
 */
package polyglot.ext.x10.types;

import java.util.List;

import polyglot.types.FieldInstance;
import polyglot.types.Resolver;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.TypeObject;

import polyglot.util.Position;
import java.util.Collections;
import java.util.ArrayList;

import polyglot.main.Report;


/** Implementation of Future type constructor.
 * 
 * <p>This code does not yet implement Termination Equivalence for Future Types, i.e.  
 * future future X = future X 
 * future nullable future X = future nullable X 
 * are not yet implemented.
 * 
 * @author vj
 *
 */
public class FutureType_c extends X10ReferenceType_c implements FutureType {
	protected X10Type base;
	protected List methods;
	
	protected FutureType_c() {}
	
	/** We type base to Type instead of X10Type because we wish to reuse code from
	 * jl which traffics in Type's. We rely on the global invariant, maintained manually 
	 * in this version of the code, that at runtime the only Type's created are X10Type's.

	 * @param ts
	 * @param pos
	 * @param base
	 */
	public FutureType_c( TypeSystem ts, Position pos, Type base ) {
		super( ts, pos);
		this.base = (X10Type) base;
		this.methods = new ArrayList(1);
		this.methods.add(ts.methodInstance( position(),
				this,
				ts.Public(),
				base,
				"force",
				Collections.EMPTY_LIST,
				Collections.EMPTY_LIST	));				
	}
	
	public boolean isFuture() {
		return true;
	}

	public FutureType toFuture() {
		return this;
	}
	
	/* The only method is: public <base> force().
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

	/* 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		// return "future " + base().toString( );
		return "x10.lang.Future";
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
		if ( Report.should_report("debug", 5) )  {
			Report.report(5, "[FutureType_c] |" + this + "|.isImplicitCastValidImpl(|" + origType +"|):");
		}

		X10Type toType = (X10Type) origType;
		if (toType.isNullable()) {
			NullableType targetType = toType.toNullable();
			toType = targetType.base();
		}

		if (toType.isFuture()) {
			FutureType target = toType.toFuture();
			boolean result = base().isImplicitCastValidImpl( target.base() );
			if ( Report.should_report("debug", 5) )  {
				Report.report(5, "[FutureType_c] ..." + result);
			}
			return result;
		}

		// Otherwise do the default check
		boolean result = super.isImplicitCastValidImpl(toType);
		if ( Report.should_report("debug", 5) )  {
			Report.report(5, "[FutureType_c] ... "+result);
		}
		return result;
	}

	public boolean isCastValidImpl(Type origType) {
		if (Report.should_report("debug", 5)) {
			Report.report(5, "[FutureType_c] |" +  this + "|.isCastValidImpl(|" + origType +"|):");
		}

		X10Type toType = (X10Type) origType;
		if (toType.isNullable()) {
			NullableType nullType = toType.toNullable();
			toType = nullType.base();
		}

		if (toType.isFuture()) {
			FutureType target = toType.toFuture();
			boolean result = base().isCastValidImpl( target.base() );
			if (Report.should_report("debug", 5)) {
				Report.report(5, "[FutureType_c] ... target=|" +  target + "|.");
				Report.report(5, "[FutureType_c] ... returns |" +  result + "|.");
			}
			return result;
		}

		// Otherwise do the default check
		boolean result = super.isCastValidImpl(toType);
		if ( Report.should_report("debug", 5) )  {
			Report.report(5, "[FutureType_c] ... "+result);
		}
		return result;
	}

	/*
	 * [IP] Cannot use the default pointer equality, since we're creating too many
	 * of these.  This makes all Futures the same -- may be wrong.
	 * [IP] FIXME: make sure only one instance at a time is created instead.
	 */
	public boolean equalsImpl(TypeObject t) {
		return t instanceof FutureType_c;
	}

	/** Returns true iff the type is canonical. */
	public boolean isCanonical() {
		return base().isCanonical();
	}

	public boolean isValueType() {
		return true;
	}
}

