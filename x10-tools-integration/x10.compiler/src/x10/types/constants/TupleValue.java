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

import polyglot.types.QName;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;

/**
 * @author Bowen Alpern
 *
 */
public class TupleValue extends ConstantValue {
    
    TupleValue (Type type, Object... values) throws SemanticException {
        super(values, type.typeSystem().arrayOf(type));
        // TODO: check that each value in values has type type
    }
    
    public String toString() {
        String result = "";
        String cont = "[";
        for (Object v : (Object[]) value) {
            result += cont + v;
            cont = ", ";
        }
        result += "]";
        return result;
    }
    
}
