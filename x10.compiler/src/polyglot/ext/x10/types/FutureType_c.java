/*
 * Created on Nov 26, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package polyglot.ext.x10.types;

import java.util.List;

import polyglot.ext.jl.types.ReferenceType_c;
import polyglot.types.FieldInstance;
import polyglot.types.NullableType;
import polyglot.types.ReferenceType;
import polyglot.types.Resolver;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.FutureType;

import polyglot.util.Position;
import java.util.Collections;
import java.util.ArrayList;


/** Implementation of Future types.
 * <p>This code does not yet implement Termination Equivalence for Future Types, i.e.  
 * future future X = future X 
 * future nullable future X = future nullable X 
 * are not yet implemented.
 * 
 * @author vj
 *
 */
public class FutureType_c extends ReferenceType_c implements FutureType {
	protected Type base;
	protected List methods;
	
	protected FutureType_c() {}
	
	public FutureType_c( TypeSystem ts, Position pos, Type base ) {
		super( ts, pos);
		this.base = base;
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
		return ts.Object();
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
		return "future " + base().translate( c );
	}

	/* 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "future " + base().toString( );
	}

	/* Return the base type.
	 * @see polyglot.ext.x10.types.FutureType#base()
	 */
	public Type base() {
		return this.base;
	}

	/* This type has no fields.
	 * @see polyglot.types.ReferenceType#fieldNamed(java.lang.String)
	 */
	public FieldInstance fieldNamed(String name) {
		return null;
	}
	
	 public boolean isImplicitCastValidImpl( Type toType ) {
	 	// System.out.println(this + ".isImplicitCastValidImpl(" + toType +")");
    	if (toType.isFuture()) {
    		
    		FutureType target = toType.toFuture();
    		// System.out.println(base()+ ".isImplicitCastValidImpl(" + target.base() +")");
    		return base().isImplicitCastValidImpl( target.base() );
    	}
     	// System.out.println(this + ".isImplicitCastValidImpl(" + toType +") returns false!");	 
        return false;
	 }
	 
	 public boolean isCastValidImpl( Type toType) {
	 	System.out.println(this + ".isCastValidImpl(" + toType +")");
	 	if (toType.isFuture()) {
	 		FutureType target = toType.toFuture();
	 		return base().isCastValidImpl( target.base() );
	 	}
	 	return false;
	 }
	 
	 

}
