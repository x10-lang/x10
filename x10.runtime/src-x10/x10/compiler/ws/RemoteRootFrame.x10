package x10.compiler.ws;

import x10.compiler.Header;

public final class RemoteRootFrame extends Frame {
    public val ffRef:GlobalRef[FinishFrame];
    @Header public def this(ffRef:GlobalRef[FinishFrame]) {
        super(NULL[Frame]());
        this.ffRef = ffRef; 
    }

    public def remap():Frame = this;

    public def resume(worker:Worker) {
        //need call back to update the ff's asyncs count
        worker.remoteFinishJoin(ffRef);
    }

    public final def wrapResume(worker:Worker) {}
}
