package x10.compiler.ws;

import x10.compiler.Abort;
import x10.compiler.NoReturn;

public final class RootFinish extends FinishFrame {
    public def this() {
        super(null);
        asyncs = 1;
    }

    public def init() {
        redirect = this;
        return this;
    }

    public def remap():FinishFrame = this;

    @NoReturn public def wrapResume(worker:Worker) {
        super.wrapResume(worker);
        Worker.stop();
        throw Abort.ABORT;
    }
}
