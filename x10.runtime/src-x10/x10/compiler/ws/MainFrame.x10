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
import x10.compiler.Header;
import x10.compiler.Ifdef;

abstract public class MainFrame extends RegularFrame {
    @Header public def this(up:Frame, ff:FinishFrame) {
        super(up, ff);
    }

    @Ifdef("__CPP__")
    public def this(Int, o:MainFrame) {
        super(-1n, o);
    }

    public abstract def fast(worker:Worker):void;
}
