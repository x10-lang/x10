package x10.compiler.ws;

public class RemoteAtFrame extends Frame {
    val upRef:GlobalRef[Frame];

    // constructor
    public def this(up:Frame, ff:FinishFrame) {
        super(ff);
        upRef = GlobalRef[Frame](up);
    }

    public def remap():RemoteAtFrame = this;

    public def wrapResume(worker:Worker) {
        worker.remoteAtNotify(upRef, throwable);
        throwable = null;
    }
}
