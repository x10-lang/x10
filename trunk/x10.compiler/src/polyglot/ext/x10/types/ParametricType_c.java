/*
 * Created by vj on Jan 9, 2005
 *
 * 
 */
package polyglot.ext.x10.types;

import java.util.Iterator;
import java.util.List;

import polyglot.ext.x10.ast.DepParameterExpr;
import polyglot.ext.x10.ast.GenParameterExpr;
import polyglot.types.FieldInstance;
import polyglot.types.Resolver;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.Position;

/**
 * @author vj Jan 9, 2005
 * @author Christian Grothoff
 */
public class ParametricType_c extends X10ReferenceType_c implements
		ParametricType {
	protected X10ReferenceType base;
	protected DepParameterExpr parameters;
	protected GenParameterExpr typeparameters;

	/**
	 * 
	 */
	public ParametricType_c() {
		super();
	}

	/**
	 * @param ts
	 */
	public ParametricType_c(TypeSystem ts) {
		super(ts);
	}
    
	/**
	 * @param ts
	 * @param pos
	 */
	public ParametricType_c(TypeSystem ts,
	        Position pos, 
	        X10ReferenceType base, 
	        GenParameterExpr typeparameters,
	        DepParameterExpr parameters) {
		super(ts, pos);
		assert base != null;
		this.base = base;
		this.typeparameters = typeparameters;
		this.parameters=parameters;
	}

	public boolean isCanonical() {
	    if (! base.isCanonical())
	        return false;
	    if (typeparameters != null) {
	        Iterator it = typeparameters.args().iterator();
	        while (it.hasNext()) {
	            Type t = (Type) it.next();
	            if (!t.isCanonical())
	                return false;
	        }
	    }
	    return true;
	}

    
	/* (non-Javadoc)
	 * @see polyglot.types.ReferenceType#methods()
	 */
	public List methods() {
	    List methods = base.methods();
	    // FIXME: translate methods (change signatures!)
        
	    return base.methods();
	}

	/* (non-Javadoc)
	 * @see polyglot.types.ReferenceType#fields()
	 */
	public List fields() {
	    // FIXME: translate fields (change signatures!)
	    return base.fields();
	}

	/* (non-Javadoc)
	 * @see polyglot.types.ReferenceType#superType()
	 */
	public Type superType() {
		return base.superType();
	}

	/* (non-Javadoc)
	 * @see polyglot.types.ReferenceType#interfaces()
	 */
	public List interfaces() {
	    // FIXME: interfaces may use parameters (implements List<T>)
	    // that we need to instantiate (List<Foo>)
		return base.interfaces();
	}

	/* (non-Javadoc)
	 * @see polyglot.types.Type#translate(polyglot.types.Resolver)
	 */
	public String translate(Resolver c) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return base.toString() 
		+ (typeparameters==null ? "" : typeparameters.toString()) 
		+ (parameters == null ? "" : parameters.toString());
	}

	/* (non-Javadoc)
	 * @see polyglot.types.ReferenceType#fieldNamed(java.lang.String)
	 */
	public FieldInstance fieldNamed(String name) {
		// TODO Auto-generated method stub
		return base.fieldNamed( name );
	}

}
