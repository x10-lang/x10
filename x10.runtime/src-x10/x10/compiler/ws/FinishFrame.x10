package x10.compiler.ws;

import x10.compiler.Abort;
import x10.compiler.Header;
import x10.compiler.Inline;
import x10.compiler.Uninitialized;
import x10.util.Stack;

abstract public class FinishFrame extends Frame {
    @Uninitialized public var asyncs:Int;
    @Uninitialized public var redirect:FinishFrame;
    @Uninitialized public var stack:Stack[Throwable];

    // constructor
    @Header public def this(up:Frame) {
        super(up);
//        this.asyncs = 1;
        this.stack = NULL[Stack[Throwable]]();
        this.redirect = NULL[FinishFrame]();
    }
    
    // copy constructor
    public def this(Int, o:FinishFrame) {
        super(o.up.realloc());
        this.asyncs = 1;
        this.stack = NULL[Stack[Throwable]](); // stack is eventually copied by the victim
    }

    // copy methods
    public abstract def remap():FinishFrame;

    public def realloc() {
        if (!isNULL(redirect)) return redirect;
        val tmp = remap();
        tmp.redirect = tmp;
        redirect = tmp;
        return tmp;
    }

    public def wrapBack(worker:Worker, frame:Frame) {
        if (!isNULL(frame.throwable)) {
            Runtime.atomicMonitor.lock();
            caught(frame.throwable);
            Runtime.atomicMonitor.unlock();
        }
    }

    @Inline public final def caught(t:Throwable) {
        if (isNULL(stack)) stack = new Stack[Throwable]();
        stack.push(t);
    }

    @Inline public final def rethrow() {
        if (!(isNULL(stack))) throw new MultipleExceptions(stack);
        return;
    }

    public def wrapResume(worker:Worker) {}
}
