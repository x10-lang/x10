package x10.compiler.ws;

import x10.compiler.Header;

public final class RootFrame extends Frame {
    @Header public def this() {
        super(null);
    }

    public def remap():Frame = this;

    public def wrapResume(worker:Worker) {
        //atomic worker.finished.value = true;
        //Runtime.println(here+":Fire all stop from root's slow");
        Worker.allStop(worker);
        if (null != throwable) throw throwable;
    }
}