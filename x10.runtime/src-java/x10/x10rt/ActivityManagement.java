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

package x10.x10rt;

import x10.lang.FinishState;

public class ActivityManagement {
    
    public static FinishState activityCreationBookkeeping() {
        FinishState fs = x10.lang.Runtime.activity().finishState();
        fs.notifySubActivitySpawn(x10.lang.Runtime.home());
        fs.notifyActivityCreation$O(x10.lang.Runtime.home());
        return fs;
    }

    // Invoked from native code.
    public static void activityTerminationBookkeeping(FinishState fs) {
        fs.notifyActivityTermination();
    }
}
