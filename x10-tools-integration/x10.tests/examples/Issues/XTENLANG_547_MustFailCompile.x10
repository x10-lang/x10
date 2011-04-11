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

import harness.x10Test;

public class XTENLANG_547_MustFailCompile extends x10Test {
    static abstract class Writer {
        public abstract def write(x: Byte): void;

        public def write(buf: GlobalRef[Rail[Byte]]): void {
            val mybuf = buf as GlobalRef[Rail[Byte]]{self.home==here};
            write(mybuf, 0, mybuf().size);
        }

        public def write(buf: GlobalRef[Rail[Byte]]{self.home==here}, off: Int, len: Int) {
            for (var i: Int = off; i < off+len; i++) {
                write(buf()(i));
            }
        }
    }

    static class OutputStreamWriter extends Writer {
        public def write(x: Byte): void { }

        public def write(buf: GlobalRef[Rail[Byte]]): void {
        }

        // This should cause the compiler to issue an error.
        // OutputStreamWriter inherits def write(buf:GlobalRef[Rail[Byte]]{self.home==here}, Int, Int)
        // and its constraint erasure is identical with the method below. But a class cant have two
        // different method definitions whose constraint erasures are identical. And a method
        // can only be overridden by a method which has the same constrained type signature.
        public def write(buf:GlobalRef[Rail[Byte]], off: Int, len: Int): void {
        }
    }

    public static def main(Array[String](1)) {
        new XTENLANG_547_MustFailCompile().execute();
    }

    public def run()=true;
    public def breakit(b:GlobalRef[Rail[byte]], w:OutputStreamWriter) {
        w.write(b, 0, 0); // ERR: Semantic Error: Multiple methods match write(x10.lang.GlobalRef[x10.lang.Rail[x10.lang.Byte]]{self==b}, x10.lang.Int{self==0}, x10.lang.Int{self==0}) [method public XTENLANG_547_MustFailCompile.OutputStreamWriter.write(buf: x10.lang.GlobalRef[x10.lang.Rail[x10.lang.Byte]], off: x10.lang.Int, len: x10.lang.Int), method public XTENLANG_547_MustFailCompile.Writer.write(buf: x10.lang.GlobalRef[x10.lang.Rail[x10.lang.Byte]]{self.x10.lang.GlobalRef#home==here}, off: x10.lang.Int, len: x10.lang.Int)]
    }
}

