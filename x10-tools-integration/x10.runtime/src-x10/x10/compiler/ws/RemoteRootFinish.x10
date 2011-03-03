package x10.compiler.ws;

public final class RemoteRootFinish extends FinishFrame {
    public def this(ffRef:GlobalRef[FinishFrame]) {
        super(upcast[RemoteRootFrame,Frame](new RemoteRootFrame(ffRef)));
        asyncs = 1;
        //move the assign to make
        //redirect = upcast[RemoteRootFinish,FinishFrame](this); 
        this.redirect = NULL[FinishFrame]();
    }

    public def init() {
        redirect = upcast[RemoteRootFinish,FinishFrame](this);
        return this;
    }

    public def remap():FinishFrame = upcast[RemoteRootFinish,FinishFrame](this);
}
