/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2010.
 */
package x10.types.constants;

import polyglot.types.TypeSystem;

/**
 * @author Bowen Alpern
 *
 */
public class StringValue extends ConstantValue {
    
    public StringValue(TypeSystem ts, String s) {
        super(s, ts.String());
    }

	@Override
	public String value() {
		return (String) value;
	}

}
