package x10.interop;

import x10.compiler.Native;
import x10.compiler.NativeRep;
import x10.compiler.NoReturn;

@NativeRep("c++", "#error Undefined Java", "#error Undefined Java", null)
public class Java {
    private def this() { } // no instances
    // Java primitive types
    public static type boolean = x10.lang.Boolean;
    public static type byte = x10.lang.Byte;
    public static type short = x10.lang.Short;
    public static type int = x10.lang.Int;
    public static type long = x10.lang.Long;
    public static type float = x10.lang.Float;
    public static type double = x10.lang.Double;
    public static type char = x10.lang.Char;
    // Java arrays (special)
    // TODO: reject unsigned types for element type
    @NativeRep("java", "#T[]", null, "x10.rtt.Types.getRTT(#T[].class)")
    public static final class array[T](
        @Native("java", "(#this).length")
        length:Java.int
    ) {
        @Native("java", "(#T[])#T$rtt.makeArray(#d0)")
        public native def this(d0:Java.int):array[T]{self.length==d0};
        @Native("java", "(#this)[#i]")
        public final native operator this(i:Java.int):T;
        @Native("java", "(#this)[#i] = #v")
        public final native operator this(i:Java.int) = (v:T):T;
    }
    @Native("java", "(#T[])#T$rtt.makeArray(#d0)")
    public static native def newArray[T](d0:Java.int):array[T]{self.length==d0};
    @Native("java", "(#T[][])#T$rtt.makeArray(#d0,#d1)")
    public static native def newArray[T](d0:Java.int, d1:Java.int):array[array[T]{self.length==d1}]{self.length==d0};
    @Native("java", "(#T[][][])#T$rtt.makeArray(#d0,#d1,#d2)")
    public static native def newArray[T](d0:Java.int, d1:Java.int, d2:Java.int):array[array[array[T]{self.length==d2}]{self.length==d1}]{self.length==d0};
    @Native("java", "(#T[][][][])#T$rtt.makeArray(#d0,#d1,#d2,#d3)")
    public static native def newArray[T](d0:Java.int, d1:Java.int, d2:Java.int, d3:Java.int):array[array[array[array[T]{self.length==d3}]{self.length==d2}]{self.length==d1}]{self.length==d0};
    @Native("java", "((x10.rtt.RuntimeType<?>)#T$rtt).getJavaClass()")
    public static native def javaClass[T]():java.lang.Class;
    @Native("java", "do { throw #e; } while (false)")
    public static native @NoReturn def throwException(e:java.lang.Throwable):void;
    // Java conversions (primitive)
    public static def convert(b:x10.lang.Boolean):Java.boolean = b; // no-op
    //public static def convert(b:Java.boolean):x10.lang.Boolean = b; // no-op
    public static def convert(b:x10.lang.Byte):Java.byte = b; // no-op
    //public static def convert(b:Java.byte):x10.lang.Byte = b; // no-op
    public static def convert(s:x10.lang.Short):Java.short = s; // no-op
    //public static def convert(s:Java.short):x10.lang.Short = s; // no-op
    public static def convert(i:x10.lang.Int):Java.int = i; // no-op
    //public static def convert(i:Java.int):x10.lang.Int = i; // no-op
    public static def convert(l:x10.lang.Long):Java.long = l; // no-op
    //public static def convert(l:Java.long):x10.lang.Long = l; // no-op
    public static def convert(f:x10.lang.Float):Java.float = f; // no-op
    //public static def convert(f:Java.float):x10.lang.Float = f; // no-op
    public static def convert(d:x10.lang.Double):Java.double = d; // no-op
    //public static def convert(d:Java.double):x10.lang.Double = d; // no-op
    public static def convert(c:x10.lang.Char):Java.char = c; // no-op
    //public static def convert(c:Java.char):x10.lang.Char = c; // no-op
    // Java conversions (String)
    @Native("java", "#s")
    public static native def convert(s:x10.lang.String):java.lang.String; // no-op
    //@Native("java", "#s")
    //public static native def convert(s:java.lang.String):x10.lang.String; // no-op
    // Java conversions (array)
    @Native("java", "(#T[])#a.raw.getBackingArray()")
    public static native def convert[T](a:x10.array.Array[T](1)):Java.array[T];
    // XTENLANG-3063
    // @Native("java", "new x10.array.Array((java.lang.System[]) null, #T$rtt).$init(new x10.core.IndexedMemoryChunk(#T$rtt, #a.length, #a), (x10.array.Array.__0$1x10$array$Array$$T$2) null)")
    @Native("java", "new x10.array.Array((java.lang.System[]) null, #T$rtt).x10$array$Array$$init$S(new x10.core.IndexedMemoryChunk(#T$rtt, #a.length, #a), (x10.array.Array.__0$1x10$array$Array$$T$2) null)")
    public static native def convert[T](a:Java.array[T]):x10.array.Array[T](1);
}
