package x10.lang;


import x10.lang.annotations.*;

/**
The interface satisfied by mutable native rails. 

x10c compiling x10 code to Java "knows" that NativeRail[T] will be
implemented by a class (say NativeRail_c) wrapping Java's T[] (in a
field f, say), and forwarding get and set methods to this field.
// Java
final class NativeRail_c[T] implements NativeRail[T] {
    final T[] f;
    final length;
    NativeRail_c(T[] x) { f = x; length=x.length;}
    public T get(point p) { return f[p.get(0)];}
    public void set(point p, T v) { f[p.get(0)] = v;}
}

What I would like to see is that the compiler can generate, for o of
type NativeArray[T], and p of type point(0..length-1):

   NativeRailMaker[T].make(l) ==compiled into=> new NativeRail_c(new T[l]);
   o.get(p) ==compiled into=> o.f[p.get(0)]
   o.set(p, v) ==compiled into=> o.f[p.get(0)]=v 
   o.length ==compiled into => o.f.length

The reason for keeping this one level of indirection is that the
serializer will now know to generate a global reference when
serializing a reference to NativeRail_c. If instead we had open coded
NativeRail_c[T] as T[], the serializer would not know at runtime
whether to serialize the contents of the T[] (necessary for a ValRail)
or to serialize a reference to the T[] (necessary for a Rail).

TODO: Check if there is a way in X10 to force an interface to be
implemented only by Value classes.

TODO: Perhaps we really do need parameter-specific compilation of classes?

Note: This interface is package protected. 

@author vj 06/10/08
*/
import x10.lang.TypeDefs.*;

interface NativeRail extends NativeAbsRail, Settable[Int,Range[Int](0,length-1),NativeRailT] {}
