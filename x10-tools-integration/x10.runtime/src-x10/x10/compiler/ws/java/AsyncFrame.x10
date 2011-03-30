package x10.compiler.ws.java;

import x10.compiler.Abort;
import x10.compiler.Inline;

public abstract class AsyncFrame extends Frame {

    public def this(up:Frame) {
        super(up);
    }

    @Inline public final def poll(worker:Worker) {
        if (null == worker.deque.poll()) {
            worker.lock.lock();
            worker.lock.unlock();
            worker.unroll(up);
            throw Abort.ABORT;
        }
        return;
    }
}
