package x10.lang;

import x10.compiler.NativeRep;

@NativeRep("java", "x10.runtime.Future<#1>")
public interface Future[+T] extends ()=>T {
    // public abstract def start(): void;
    // public abstract def started(): boolean;

    public def force(): T;
    public def forced(): boolean;
}
