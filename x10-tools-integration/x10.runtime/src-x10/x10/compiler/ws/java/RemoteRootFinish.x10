package x10.compiler.ws.java;

public final class RemoteRootFinish extends FinishFrame {
    public def this(ffRef:GlobalRef[FinishFrame]) {
        super(upcast[RemoteRootFrame,Frame](new RemoteRootFrame(ffRef)));
        asyncs = 1;
    }

    public def init() = this;
}
