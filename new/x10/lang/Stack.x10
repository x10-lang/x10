/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.lang;

import x10.compiler.Native;
import x10.compiler.NativeRep;

@NativeRep("java", "x10.runtime.NativeStack<#1>")
public class Stack[T] {
	public native def this();
	
	@Native("java", "#0.push(#1)")
	public native def push(t: T): void;

	@Native("java", "#0.pop()")
	public native def pop(): T;

	@Native("java", "#0.empty()")
	public native def empty(): boolean;

	@Native("java", "#0.size()")
	public native def size(): int;
}
