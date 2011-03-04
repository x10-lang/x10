package x10.compiler.ws;

public final class RootFinish extends FinishFrame {
    public def this() {
        super(upcast[RootFrame,Frame](new RootFrame()));
        asyncs = 1;
        //move the assign to make
        //redirect = upcast[RootFinish,FinishFrame](this); 
        this.redirect = NULL[FinishFrame]();
    }

    public def init() {
        redirect = upcast[RootFinish,FinishFrame](this);
        return this;
    }

    public def remap():FinishFrame = upcast[RootFinish,FinishFrame](this);
}