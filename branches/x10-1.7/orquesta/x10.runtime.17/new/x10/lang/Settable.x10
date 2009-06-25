package x10.lang;

import x10.compiler.Native;
import x10.compiler.NativeRep;

@NativeRep("java", "x10.core.Settable<#1, #2>")
public interface Settable[-I,V] {
    def set(i: I, v: V): void;
}
