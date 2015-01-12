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

import x10.compiler.Header;
import x10.compiler.Ifdef;
import x10.compiler.Inline;
import x10.compiler.NoInline;

public abstract class AsyncFrame extends Frame {
    @Header public def this(up:Frame) {
        super(up);
    }

    @Ifdef("__CPP__")
    public def this(Int, o:AsyncFrame) {
        super(o.ff().redirect);
    }

    @Inline final def ff() = cast[Frame,FinishFrame](up);

    @Ifdef("__CPP__")
    abstract public def move(ff:FinishFrame):void;

    @Inline public final def poll(worker:Worker) {
        if (null == worker.deque.poll()) pollSlow(worker);
    }

    @NoInline public final def pollSlow(worker:Worker) {
        val lock = worker.lock;
        lock.lock();
        lock.unlock();
        var ff:FinishFrame = ff();
        @Ifdef("__CPP__") {
            val ff_redirect = ff.redirect;
            if (ff != ff_redirect) {
                move(ff_redirect);
                ff_redirect.append(ff);
                ff = ff_redirect;
            }
        }
        worker.unroll(ff);
    }

    @NoInline public final def caught(t:Exception) {
        ff().caught(t);
    }
}
