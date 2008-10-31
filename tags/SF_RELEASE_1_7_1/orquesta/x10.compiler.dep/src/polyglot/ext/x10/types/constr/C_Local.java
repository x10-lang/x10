/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.types.constr;

import polyglot.ext.x10.types.X10LocalDef;
import polyglot.ext.x10.types.X10LocalInstance;
import polyglot.types.LocalInstance;

public interface C_Local extends C_Root{

	/**
	 * Is this the variable for which the type is being defined?
	 * @return
	 */
	 boolean isSelfVar();
     X10LocalDef localDef();
	
}
