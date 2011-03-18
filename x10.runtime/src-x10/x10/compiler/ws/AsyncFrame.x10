package x10.compiler.ws;

import x10.compiler.Abort;
import x10.compiler.Header;
import x10.compiler.Inline;
import x10.util.Stack;

public abstract class AsyncFrame extends Frame {
    // constructor
    @Header public def this(up:Frame) {
        super(up);
    }

    // copy constructor
    public def this(Int, o:AsyncFrame) {
        super(cast[Frame,FinishFrame](o.up).redirect);
    }

    abstract public def move(ff:FinishFrame):void;

    @Inline public final def poll(worker:Worker) {
        if (isNULL(worker.deque.poll())) {
            worker.lock.lock();
            worker.lock.unlock();
            val old = cast[Frame,FinishFrame](up);
            val ff = old.redirect;
            if (!eq(old, ff)) {
                move(ff);
                if (!isNULL(old.stack)) {
                    Runtime.atomicMonitor.lock();
                    if (isNULL(ff.stack)) ff.stack = new Stack[Throwable]();
                    while (!old.stack.isEmpty()) ff.stack.push(old.stack.pop());
                    Runtime.atomicMonitor.unlock();
                }
            }
            worker.unroll(ff);
            throw Abort.ABORT;
        }
        return;
    }

    @Inline public final def caught(t:Throwable) {
        cast[Frame,FinishFrame](up).caught(t);
    }

    public def wrapResume(worker:Worker) {
        try {
            if (!isNULL(throwable)) throw throwable;
            resume(worker);
        } catch (t:Abort) {
            throw t;
        } catch (t:Throwable) {
            throwable = t;
        }
    }
}
