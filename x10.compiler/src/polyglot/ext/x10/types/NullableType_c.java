/*
 * Created on Nov 26, 2004
 */
package polyglot.ext.x10.types;

import java.util.List;
import java.util.Collections;

import polyglot.types.ReferenceType;
import polyglot.ext.jl.types.ReferenceType_c;
import polyglot.types.FieldInstance;
import polyglot.types.Resolver;
import polyglot.types.Type;
import polyglot.types.TypeObject;
import polyglot.types.TypeSystem;

import polyglot.util.Position;

/**
 * Implementation of the nullable type constructor.
 *
 * @author vj
 */
public class NullableType_c extends X10ReferenceType_c implements NullableType {

	protected X10Type base;
	// RMF 11/2/2005 --
	// Attempting to initialize these (unused) fields at construction time causes a
	// chicken-and-egg problem for any recursive type with a nullable field of the
	// owner's type (e.g. class Node { nullable Node link; }): can't initialize the
	// type of the nullable field without knowing all the fields/methods first!
//	protected List fields;
//	protected List methods;
	// IP 2/9/2006 -- This is unused
//	protected List interfaces;

	/** Used for deserializing types. */
	protected NullableType_c() { }

	/**
	 * Factory method for producing nullable types. Implements nullable nullable X = nullable X.
	 *
	 * @param ts
	 * @param pos
	 * @param base -- the base type for the nullable type.
	 * @return
	 */
	public static NullableType makeNullable(TypeSystem ts, Position pos, X10Type base) {
		if (base instanceof NullableType)
			return (NullableType) base;
		//System.out.println("NullableType_c:makeNullable: base=" + base);
		return new NullableType_c(ts, pos, base);
	}

	/**
	 * Constructor is made private. Called only in the factory method above.
	 *
	 * @param ts
	 * @param pos
	 * @param base
	 */
	private NullableType_c(TypeSystem ts, Position pos, X10Type base) {
		super(ts, pos);
		this.base = base;
		assert base != null;

//		this.methods = base.methods();
//		this.fields = base.fields();
//		this.interfaces = base.interfaces();
	}

	public boolean isNullable() {
		return true;
	}

	public boolean isX10Array() {
		return base.isX10Array();
	}

	public boolean isFuture() {
		return base.isFuture();
	}

	public NullableType toNullable() {
		return this;
	}

	public X10Type base() {
		return this.base;
	}

	/** Set the base type. */
	public NullableType base(X10Type base) {
		if (base == this.base)
			return this;
		NullableType_c n = (NullableType_c) copy();
		n.base = base;
		return n;
	}

	public String toString() {
		return "nullable<" + base.toString() + ">";
	}

	/**
	 * This needs to be changed so that the right boxed class is produced for
	 * primitive types.
	 * [IP] TODO: Fix this.
	 * @author vj
	 */
	public String translate(Resolver c) {
		return base.translate(c);
	}

	public boolean isCanonical() {
		return base.isCanonical();
	}

	public List methods() {
		if (base instanceof X10ReferenceType)
			return ((X10ReferenceType) base).methods();
		return Collections.EMPTY_LIST;
	}

	public List fields() {
		if (base instanceof X10ReferenceType)
			return ((X10ReferenceType) base).fields();
		return Collections.EMPTY_LIST;
	}

	public FieldInstance fieldNamed(String name) {
		if (base instanceof X10ReferenceType)
			return ((X10ReferenceType) base).fieldNamed(name);
		return null;
	}

	public Type superType() {
		if (base instanceof X10ReferenceType)
			return ((X10ReferenceType) base).superType();
		X10TypeSystem ts = (X10TypeSystem) base.typeSystem();
		return ts.X10Object();
	}

	public List interfaces() {
		if (base instanceof X10ReferenceType)
			return ((X10ReferenceType) base).interfaces();
		return Collections.EMPTY_LIST;
	}

	// vj TODO: check if this is the right thing to do.
	public int hashCode() {
		return base.hashCode() << 2;
	}

	public boolean equalsImpl(TypeObject t) {
		if (t instanceof NullableType) {
			NullableType a = (NullableType) t;
			return ts.equals(base, a.base());
		}
		return false;
	}

	public boolean isImplicitCastValidImpl(Type toType) {
		X10Type targetType = (X10Type) toType;
		if (targetType.isNullable()) {
			NullableType target = targetType.toNullable();
			return base.isImplicitCastValidImpl(target.base());
		}
		return super.isImplicitCastValidImpl(toType);
	}

	// [IP] FIXME: This seems just plain wrong -- we need to check whether we
	// can cast the bases appropriately.  See isImplicitCastValidImpl above.
	// [IP] Also, this is the place to allow casting nullable stuff into
	// non-nullable (by compiling this down to a null check)
	public boolean isCastValidImpl(Type toType) {
		return ((X10Type) toType).isNullable() || base.isCastValidImpl(toType);
	}

	public boolean isValueType() {
		return base.isValueType();
	}
}

