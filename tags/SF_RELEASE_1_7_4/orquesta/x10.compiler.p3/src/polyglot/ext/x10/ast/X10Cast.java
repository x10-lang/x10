/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.ast;

import polyglot.ast.Cast;

public interface X10Cast extends Cast {
    
    public boolean isConversion();

	public boolean notNullRequired();
	public boolean isToTypeNullable();
}
