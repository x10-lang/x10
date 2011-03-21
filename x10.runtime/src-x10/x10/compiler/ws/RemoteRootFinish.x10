package x10.compiler.ws;

import x10.compiler.Abort;

public final class RemoteRootFinish extends FinishFrame {
    val ffRef:GlobalRef[FinishFrame];
    public def this(ff:FinishFrame) {
        super(NULL[Frame]());
        asyncs = 1;
        ffRef = GlobalRef[FinishFrame](ff);
        Runtime.atomicMonitor.lock(); ff.asyncs++; Runtime.atomicMonitor.unlock();
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
