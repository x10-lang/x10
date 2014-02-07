/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10cuda.types;

import polyglot.frontend.ExtensionInfo;
import polyglot.types.Context;
import polyglot.types.Context;
import polyglot.types.TypeSystem_c;


public class X10CUDATypeSystem_c extends TypeSystem_c {
    public X10CUDATypeSystem_c(ExtensionInfo extInfo) {
        super(extInfo);
    }

    public Context emptyContext() {
        return new X10CUDAContext_c(this);
    }
}