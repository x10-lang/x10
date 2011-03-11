package x10.compiler.ws.java;


public final class RemoteRootFrame extends Frame {
    public val ffRef:GlobalRef[FinishFrame];
    public def this(ffRef:GlobalRef[FinishFrame]) {
        super(null);
        this.ffRef = ffRef; 
    }

    public def resume(worker:Worker) {
        //need call back to update the ff's asyncs count
        worker.remoteFinishJoin(ffRef);
    }
}
