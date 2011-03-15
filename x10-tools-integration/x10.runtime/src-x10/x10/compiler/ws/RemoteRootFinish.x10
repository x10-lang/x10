package x10.compiler.ws;

public final class RemoteRootFinish extends FinishFrame {
    public def this(ffRef:GlobalRef[FinishFrame]) {
        super(new RemoteRootFrame(ffRef));
        asyncs = 1;
        //move the assign to make
        //redirect = this; 
        this.redirect = NULL[FinishFrame]();
    }

    public def init() {
        redirect = this;
        return this;
    }

    public def remap():FinishFrame = this;
}
