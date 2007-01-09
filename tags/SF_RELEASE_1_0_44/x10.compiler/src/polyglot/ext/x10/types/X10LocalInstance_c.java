/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/**
 * 
 */
package polyglot.ext.x10.types;

import polyglot.ext.x10.types.constr.C_Local_c;
import polyglot.ext.x10.types.constr.Constraint;
import polyglot.ext.x10.types.constr.Constraint_c;
import polyglot.types.LocalInstance_c;
import polyglot.types.Flags;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.Position;

/**
 * @author vj
 *
 */
public class X10LocalInstance_c extends LocalInstance_c implements X10LocalInstance {

	/**
	 * 
	 */
	public X10LocalInstance_c() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param ts
	 * @param pos
	 * @param flags
	 * @param type
	 * @param name
	 */
	public X10LocalInstance_c(TypeSystem ts, Position pos, Flags flags,
			Type type, String name) {
		super(ts, pos, flags, type, name);
	}
	
	protected int positionInArgList = -1;
	public int positionInArgList() {
		return positionInArgList;
	}
	public void setPositionInArgList(int i) {
		positionInArgList = i;
	}
	public boolean setSelfClauseIfFinal() {
		// If the local variable is final, replace T by T(:self==t), 
		// do this even if depclause==null
		boolean changed = false;
		if ( flags().isFinal()) {
			X10Type t = (X10Type) type();
			Constraint c = Constraint_c.addSelfBinding(C_Local_c.makeSelfVar(this),t.depClause());
			X10Type newType = t.makeVariant(c,t.typeParameters());
			setType(newType);
			changed = true;
		}
		return changed;
		
	}

}
