/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

package x10c.types;

import polyglot.types.JavaArrayType_c;
import polyglot.types.Ref;
import polyglot.types.Type;
import polyglot.util.Position;
import polyglot.types.TypeSystem;

public class BackingArrayType extends JavaArrayType_c {
    private static final long serialVersionUID = 1504080841867595349L;
    public BackingArrayType(TypeSystem xts, Position position, Ref<? extends Type> ref) {
        super(xts, position, ref);
    }
}