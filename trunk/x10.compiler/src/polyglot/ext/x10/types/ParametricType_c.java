/*
 * Created by vj on Jan 9, 2005
 *
 * 
 */
package polyglot.ext.x10.types;

import java.util.List;

import polyglot.types.FieldInstance;
import polyglot.types.Resolver;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.Position;
import polyglot.ext.x10.ast.DepParameterExpr;

/**
 * @author vj Jan 9, 2005
 * 
 */
public class ParametricType_c extends X10ReferenceType_c implements
		ParametricType {
	protected X10ReferenceType base;
    protected DepParameterExpr parameters;

	/**
	 * 
	 */
	public ParametricType_c() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param ts
	 */
	public ParametricType_c(TypeSystem ts) {
		super(ts);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param ts
	 * @param pos
	 */
	public ParametricType_c(TypeSystem ts, Position pos, X10ReferenceType base, DepParameterExpr parameters) {
		super(ts, pos);
		this.base = base;
		this.parameters=parameters;
	}

	/* (non-Javadoc)
	 * @see polyglot.types.ReferenceType#methods()
	 */
	public List methods() {
		return base.methods();
	}

	/* (non-Javadoc)
	 * @see polyglot.types.ReferenceType#fields()
	 */
	public List fields() {
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
		return base.toString() + parameters.toString();
	}

	/* (non-Javadoc)
	 * @see polyglot.types.ReferenceType#fieldNamed(java.lang.String)
	 */
	public FieldInstance fieldNamed(String name) {
		// TODO Auto-generated method stub
		return base.fieldNamed( name );
	}

}
