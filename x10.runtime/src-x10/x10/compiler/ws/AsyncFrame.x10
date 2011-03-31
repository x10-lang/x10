package x10.compiler.ws;

import x10.compiler.Header;
import x10.compiler.Inline;
import x10.compiler.NoInline;

public abstract class AsyncFrame extends Frame {
    @Header public def this(up:Frame) {
        super(up);
    }

    public def this(Int, o:AsyncFrame) {
        super(o.ff().redirect);
    }

    @Inline final def ff() = cast[Frame,FinishFrame](up);

    abstract public def move(ff:FinishFrame):void;

    @Inline public final def poll(worker:Worker) {
        if (null == worker.deque.poll()) pollSlow(worker);
    }

    @NoInline public final def pollSlow(worker:Worker) {
        val lock = worker.lock;
        lock.lock();
        lock.unlock();
        val ff = ff();
        val ff_redirect = ff.redirect;
        if (ff != ff_redirect) {
            move(ff_redirect);
            ff_redirect.append(ff.stack);
        }
        worker.unroll(ff_redirect);
    }

    @Inline public final def caught(t:Throwable) {
        ff().caught(t);
    }
}
