/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.ast;

import polyglot.ast.Cast;

public interface X10Cast extends Cast {
    public static enum ConversionType {
        UNKNOWN_CONVERSION,
        UNKNOWN_IMPLICIT_CONVERSION,
        PRIMITIVE,
        CHECKED,
        SUBTYPE,
        UNBOXING,
        BOXING,
        UNCHECKED
    }
    
    public ConversionType conversionType();
    public X10Cast conversionType(ConversionType convert);
}
