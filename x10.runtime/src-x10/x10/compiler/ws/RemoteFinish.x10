package x10.compiler.ws;

import x10.compiler.Abort;
import x10.compiler.Ifdef;
import x10.compiler.Inline;

import x10.util.Stack;

public final class RemoteFinish extends FinishFrame {
    val ffRef:GlobalRef[FinishFrame];

    public def this(ff:FinishFrame) {
        super(null);
        asyncs = 1;
        ffRef = GlobalRef[FinishFrame](ff);
        Runtime.atomicMonitor.lock(); ff.asyncs++; Runtime.atomicMonitor.unlock();
    }

    @Ifdef("__CPP__")
    public def remap():RemoteFinish = this;

    public def wrapResume(worker:Worker) {
        super.wrapResume(worker);
        update(ffRef, stack);
        worker.throwable = null; //The exceptions were sent to source place
        throw Abort.ABORT;
    }

    @Inline public static def update(ffRef:GlobalRef[FinishFrame], stack:Stack[CheckedThrowable]) {
        val body = ()=> @x10.compiler.RemoteInvocation {
            val ff = (ffRef as GlobalRef[FinishFrame]{home==here})();
            ff.append(stack);
            Runtime.wsFIFO().push(ff);
        };
        Worker.wsRunAsync(ffRef.home.id, body);
    }
}
