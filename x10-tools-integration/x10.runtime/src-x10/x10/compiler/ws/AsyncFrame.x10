package x10.compiler.ws;

import x10.compiler.Header;
import x10.compiler.Inline;

public abstract class AsyncFrame extends Frame {
    @Header public def this(up:Frame) {
        super(up);
    }

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
            if (old != ff) {
                move(ff);
                ff.append(old.stack);
            }
            worker.unroll(ff);
        }
    }

    @Inline public final def pollNE(worker:Worker) {
        if (isNULL(worker.deque.poll())) {
            worker.lock.lock();
            worker.lock.unlock();
            val old = cast[Frame,FinishFrame](up);
            val ff = old.redirect;
            if (old != ff) {
                move(ff);
            }
            worker.unroll(ff);
        }
    }

    @Inline public final def caught(t:Throwable) {
        cast[Frame,FinishFrame](up).caught(t);
    }
}
