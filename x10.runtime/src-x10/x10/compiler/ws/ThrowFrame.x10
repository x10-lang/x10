package x10.compiler.ws;

import x10.compiler.Ifdef;

public final class ThrowFrame extends Frame {
    val throwable:Exception;
    
    public def this(up:Frame, throwable:Exception) {
        super(up);
        this.throwable = throwable;
    }

    @Ifdef("__CPP__")
    public def remap():ThrowFrame = this;

    public def wrapResume(worker:Worker) {
        worker.throwable = throwable;
    }
}
