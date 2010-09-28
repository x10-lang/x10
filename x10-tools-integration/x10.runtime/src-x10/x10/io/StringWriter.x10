/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.io;

import x10.util.StringBuilder;
import x10.compiler.Global;
import x10.compiler.Pinned;

public class StringWriter extends Writer {
	private val root = GlobalRef[StringWriter](this);
    transient val b:StringBuilder;
    public def this() { 
    	this.b = new StringBuilder(); 
    }

    @Global public def write(x:Byte): Void { 
        b.add((x as Byte) as Char);
    }

    @Global public def size() = {
    	if (here == root.home) {
    		return (root as GlobalRef[StringWriter]{self.home==here})().b.length();
    	}
    	return at (root) 
    		(root as GlobalRef[StringWriter]{self.home==here})().b.length();
    	
    }
    @Global public def result() = {
    	if (here == root.home) {
    		return (root as GlobalRef[StringWriter]{self.home==here})().b.result();
    	}
    	return at (root) 
    		(root as GlobalRef[StringWriter]{self.home==here})().b.result();
    }
    
    @Global public def flush(): Void { }
    @Global public def close(): Void { }
}

