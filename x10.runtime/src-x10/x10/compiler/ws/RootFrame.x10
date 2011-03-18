package x10.compiler.ws;

import x10.compiler.Header;

public final class RootFrame extends Frame {
    @Header public def this() {
        super(NULL[Frame]());
    }

    public def remap():Frame = this;

    public def wrapResume(worker:Worker) {
        Worker.allStop(worker);
    }
}