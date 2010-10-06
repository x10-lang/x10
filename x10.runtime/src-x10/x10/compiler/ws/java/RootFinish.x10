package x10.compiler.ws.java;

public final class RootFinish extends FinishFrame {
    public def this() {
        super(new RootFrame());
    }
    
    public def init() = this;
}
