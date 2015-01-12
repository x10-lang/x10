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

package x10.compiler.ws;

import x10.compiler.Abort;
import x10.compiler.Ifdef;

public final class RootFinish extends FinishFrame {
    public def this() {
        super(null);
        asyncs = 1n;
    }

    @Ifdef("__CPP__")
    public def remap():RootFinish = this;

    public def wrapResume(worker:Worker) {
        super.wrapResume(worker);
        Worker.stop();
        throw Abort.ABORT;
    }
}
