package x10.compiler.ws;

import x10.compiler.Abort;

public final class RemoteRootFinish extends FinishFrame {
    val ffRef:GlobalRef[FinishFrame];
    public def this(ffRef:GlobalRef[FinishFrame]) {
        super(NULL[Frame]());
        asyncs = 1;
        this.ffRef = ffRef;
    }

    public def init() {
        redirect = this;
        return this;
    }

    public def remap():FinishFrame = this;

    public def wrapResume(worker:Worker) {
        super.wrapResume(worker);
        worker.remoteFinishJoin(ffRef);
        throw Abort.ABORT;
    }
}

