package x10.util;

import x10.compiler.Native;
import x10.compiler.NativeRep;

@NativeRep("java", "java.util.Stack<#1>")
public class Stack[T] {
    @Native("java", "#0.iterator()")
    native public def iterator(): Iterator[T];

}
