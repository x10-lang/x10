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

package x10.ast;

import x10.types.AtDef;

/**
 * @author vj Jan 5, 2005
 * @author igor Jan 19, 2006
 */
public interface AtEach extends X10ClockedLoop {
    AtDef atDef();
    AtEach atDef(AtDef ci);
}

