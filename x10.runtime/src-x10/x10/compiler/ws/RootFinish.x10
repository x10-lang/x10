package x10.compiler.ws;

import x10.compiler.Abort;

public final class RootFinish extends FinishFrame {
    public def this() {
        super(null);
        asyncs = 1;
    }

    public def remap():RootFinish = this;

    public def wrapResume(worker:Worker) {
        super.wrapResume(worker);
        Worker.stop();
        throw Abort.ABORT;
    }
}
