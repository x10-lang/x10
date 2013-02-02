
We have designed the type system and the Java code generation of X10 programming language very carefully so that X10 programmers can call the existing (i.e. not compiled from X10) Java code as natural as possible. We have successfully exposed most of Java features to X10 in natural ways, therefore X10 programmers can leverage the existing Java libraries without hustle.

However, due to some gaps between X10 and Java, there are some points that X10 programmers need to be aware of.

- Java varargs is not supported.  This is because X10 does not support varargs.
- Java enum is not supported.  This is because X10 does not support enum.
- Static field of Java classes can only be updated with reflection. This is because X10 does not have mutable static field.
- Auto-boxing of Javac is not supported.  X10 programmers need to box primitives explicitly before passing them to Java methods as objects.
- Java generic types are shown as their raw types. This is because X10 generic types preserver runtime time but Java's don't.


This directory includes some examples to illustrate how to call Java from X10 and workaround for the above limitations.

