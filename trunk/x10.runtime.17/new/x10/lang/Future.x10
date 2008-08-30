package x10.lang;

import x10.compiler.NativeRep;

@NativeRep("java", "x10.runtime.Future<#1>")
public interface Future[+T] extends ()=>T {
    // public abstract def start(): void;
    // public abstract def started(): boolean;

    @Native("java", "(#0).force()")
    public def apply(): T;
    
    @Native("java", "(#0).force()")
    public def force(): T;
    
    @Native("java", "(#0).forced()")
    public def forced(): boolean;
}
