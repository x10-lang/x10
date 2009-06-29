package x10.lang;

import x10.lang.annotations.*;

/**
<p> The interface satisfied by native value rails. 

<p x10c compiling x10 code to Java "knows" that NativeValRail[T] will be
implemented by Java's T[].

<p> Note: This interface is package protected -- so only code in this
package can reference it.

@see NativeRailMaker

@author vj 06/10/08
*/

interface NativeValRail extends NativeAbsRail {
}
