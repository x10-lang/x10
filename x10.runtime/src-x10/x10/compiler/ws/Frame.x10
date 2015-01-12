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

import x10.xrx.*;
import x10.compiler.Abort;
import x10.compiler.Header;
import x10.compiler.Ifdef;
import x10.compiler.Native;
import x10.compiler.Uninitialized;

import x10.util.Random;

public abstract class Frame {
    @Native("java", "((#U$box) #x)")
    @Native("c++", "reinterpret_cast< #U >(#x)")
    public native static def cast[T,U](x:T):U;

    @Uninitialized public val up:Frame;

    @Header public def this(up:Frame) {
        this.up = up;
    }

    @Ifdef("__CPP__")
    public abstract def remap():Frame;

    @Ifdef("__CPP__")
    public def realloc() = remap();

    public def back(worker:Worker, frame:Frame) {}

    public def wrapBack(worker:Worker, frame:Frame) {
        if (null != worker.throwable) return;
        back(worker, frame);
    }

    public def resume(worker:Worker) {}

    public def wrapResume(worker:Worker) {
        if (null != worker.throwable) return;
        try {
            resume(worker);
        } catch (t:Abort) {
            throw t;
        } catch (t:Exception) {
            worker.throwable = t;
        }
    }
}
