package x10.lang;

import x10.lang.annotations.*;

/**
The interface satisfied by native value rails. 

x10c compiling x10 code to Java "knows" that NativeValRail[T] will be
implemented by Java's T[].

Note: This interface is package protected -- so only code in this
package can reference it.

@see NativeRailMaker

@author vj 06/10/08
*/

interface NativeValRail(length: int) extends Indexable[T](0..length) {}
