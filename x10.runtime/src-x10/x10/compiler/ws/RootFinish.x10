package x10.compiler.ws;

import x10.util.Stack;

public final class RootFinish extends FinishFrame {
    public def this() {
        super(new RootFrame());
        asyncs = 1;
    }

    public def init() {
        redirect = this;
        return this;
    }

    public def remap():FinishFrame = this;
}