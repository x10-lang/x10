package x10.compiler.ws;

import x10.compiler.Abort;
import x10.compiler.Header;
import x10.compiler.Native;
import x10.compiler.Uninitialized;

import x10.util.Random;

public abstract class Frame {
    @Native("java", "((#4) #7)")
    @Native("c++", "static_cast<#U >(#x)")
    public native static def cast[T,U](x:T):U;

    @Native("java", "((#1) null)")
    @Native("c++", "NULL")
    public native static def NULL[T]():T;

    @Native("java", "(null == (#4))")
    @Native("c++", "(NULL == (#4)._val)")
    public native static def isNULL[T](x:T):Boolean;

    @Uninitialized transient public var throwable:Throwable;
    @Uninitialized public val up:Frame;

    @Header public def this(up:Frame) {
        this.up = up;
    }

    public abstract def remap():Frame;

    public def realloc() = remap();

    public def back(worker:Worker, frame:Frame) {}

    public def wrapBack(worker:Worker, frame:Frame) {
        if (!isNULL(frame.throwable)) {
            throwable = frame.throwable;
        } else {
            back(worker, frame);
        }
    }

    public def resume(worker:Worker) {}

    public def wrapResume(worker:Worker) {
        if (!isNULL(throwable)) return;
        try {
            resume(worker);
        } catch (t:Abort) {
            throw t;
        } catch (t:Throwable) {
            throwable = t;
        }
    }
}
