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

package x10.compiler.ws;

import x10.compiler.Abort;
import x10.compiler.Header;
import x10.compiler.Ifdef;
import x10.compiler.Inline;

public abstract class TryFrame extends RegularFrame {
    @Header public def this(up:Frame, ff:FinishFrame) {
        super(up, ff);
    }

    @Ifdef("__CPP__")
    public def this(Int, o:TryFrame) {
        super(-1n, o);
    }

    public def wrapResume(worker:Worker) {
        try {
            resume(worker);
        } catch (t:Abort) {
            throw t;
        } catch (t:Exception) {
            worker.throwable = t;
        }
    }
}
