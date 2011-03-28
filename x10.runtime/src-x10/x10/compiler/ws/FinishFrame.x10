package x10.compiler.ws;

import x10.compiler.Abort;
import x10.compiler.Header;
import x10.compiler.Inline;
import x10.compiler.Uninitialized;
import x10.util.Stack;

abstract public class FinishFrame extends Frame {
    @Uninitialized public var asyncs:Int;
    @Uninitialized public var redirect:FinishFrame;
    @Uninitialized transient public var stack:Stack[Throwable];

    @Header public def this(up:Frame) {
        super(up);
        this.stack = NULL[Stack[Throwable]]();
        this.redirect = NULL[FinishFrame]();
    }

    public def this(Int, o:FinishFrame) {
        super(o.up.realloc());
        this.asyncs = 1;
        this.stack = NULL[Stack[Throwable]]();
    }

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

    public def wrapResume(worker:Worker) {
        var n:Int;
        Runtime.atomicMonitor.lock(); n = --asyncs; Runtime.atomicMonitor.unlock();
        if (0 != n) throw Abort.ABORT;
        throwable = MultipleExceptions.make(stack);
    }

    @Inline public final def append(s:Stack[Throwable]) {
        if (!isNULL(s)) {
            Runtime.atomicMonitor.lock();
            if (isNULL(stack)) stack = new Stack[Throwable]();
            while (!s.isEmpty()) stack.push(s.pop());
            Runtime.atomicMonitor.unlock();
        }
    }

    @Inline public final def caught(t:Throwable) {
        if (isNULL(stack)) stack = new Stack[Throwable]();
        stack.push(t);
    }

    @Inline public final def rethrow() {
        if (!(isNULL(stack))) throw new MultipleExceptions(stack);
    }

    @Inline public final def check() {
        if (!(isNULL(stack))) {
            while (!stack.isEmpty()) {
                Runtime.pushException(stack.pop());
            }
        }
    }
}
