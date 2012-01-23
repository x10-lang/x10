package x10.compiler.ws;

import x10.compiler.Ifdef;
import x10.compiler.Inline;

public final class AtFrame extends Frame {
    val upRef:GlobalRef[Frame];

    public def this(up:Frame, ff:FinishFrame) {
        super(ff);
        upRef = GlobalRef[Frame](up);
    }

    @Ifdef("__CPP__")
    public def remap():AtFrame = this;

    public def wrapResume(worker:Worker) {
        update(upRef, worker.throwable);
        worker.throwable = null;
    }

    @Inline public static def update(upRef:GlobalRef[Frame], throwable:Throwable) {
        val body = ()=> @x10.compiler.RemoteInvocation {
            var up:Frame = (upRef as GlobalRef[Frame]{home==here})();
            if (null != throwable) {
                up = new ThrowFrame(up, throwable);
            }
            Runtime.wsFIFO().push(up);
        };
        Worker.wsRunAsync(upRef.home.id, body);
    }
}
