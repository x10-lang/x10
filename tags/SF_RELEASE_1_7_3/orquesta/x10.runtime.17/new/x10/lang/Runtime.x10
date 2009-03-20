package x10.lang;

import x10.compiler.Native;
import x10.compiler.NativeRep;

@NativeRep("java", "x10.runtime.Runtime")
public class Runtime {
    @Native("java", "x10.runtime.Runtime.sleep(#1)")
    public native static def sleep(millis: long): boolean;

    @Native("java", "x10.runtime.Runtime.exit(#1)")
    public native static def exit(code: int): void;

    @Native("java", "x10.runtime.Runtime.setExitCode(#1)")
	public native static def setExitCode(code: int): void;

    @Native("java", "x10.runtime.Runtime.here()")
	public native static def here():Place;

	
}
