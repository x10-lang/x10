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

package x10.io;

import x10.compiler.NativeRep;
import x10.compiler.Native;

public class OutputStreamWriter extends Writer {
    @NativeRep("java", "x10.core.io.OutputStream", null, "x10.core.io.OutputStream.$RTT")
    @NativeRep("c++", "x10::io::OutputStreamWriter__OutputStream*", "x10::io::OutputStreamWriter__OutputStream", null)
    protected abstract static class OutputStream {
        @Native("java", "#this.close()")
        public native def close():void;

        @Native("java", "#this.flush()")
        public native def flush():void;
        
        @Native("java", "#this.write(#v)")
        public native def write(v:Int):void;
        
        @Native("java", "#this.write(#s)")
        public native def write(s:String):void;
        
        @Native("java", "#this.write((#r).getByteArray(), #off, #len)")
        public native def write(r:Rail[Byte], off:Long, len:Long):void;
    }

    val out: OutputStream;
    
    def stream():OutputStream = out;
    
    public def this(out: OutputStream) {
        this.out = out;
    }
    
    public def flush():void { 
        out.flush(); 
    }

    public def close():void {
        out.close(); 
    }
    
    public def write(x:Byte):void {
        out.write(x); 
    }

    public def write(s:String):void {
        out.write(s); 
    }
    
    public def write(buf:Rail[Byte], off:Long, len:Long):void {
        out.write(buf, off, len);
    }
}
