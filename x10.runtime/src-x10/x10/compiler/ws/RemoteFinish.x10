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
import x10.compiler.Inline;

import x10.util.GrowableRail;

public final class RemoteFinish extends FinishFrame {
    val ffRef:GlobalRef[FinishFrame];

    public def this(ff:FinishFrame) {
        super(null);
        asyncs = 1n;
        ffRef = GlobalRef[FinishFrame](ff);
        Runtime.atomicMonitor.lock(); ff.asyncs++; Runtime.atomicMonitor.unlock();
    }

    @Ifdef("__CPP__")
    public def remap():RemoteFinish = this;

    public def wrapResume(worker:Worker) {
        super.wrapResume(worker);
        update(ffRef, exceptions);
        worker.throwable = null; //The exceptions were sent to source place
        throw Abort.ABORT;
    }

    @Inline public static def update(ffRef:GlobalRef[FinishFrame], exceptions:GrowableRail[CheckedThrowable]) {
        val body = ()=> @x10.compiler.RemoteInvocation {
            val ff = (ffRef as GlobalRef[FinishFrame]{home==here})();
            ff.append(exceptions);
            Runtime.wsFIFO().push(ff);
        };
        Worker.wsRunAsync(ffRef.home.id, body);
    }
}
