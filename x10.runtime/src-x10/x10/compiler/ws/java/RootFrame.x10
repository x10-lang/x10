package x10.compiler.ws.java;

public final class RootFrame extends Frame {
    public def this() {
        super(null);
    }

    public def resume(worker:Worker) {
        atomic worker.finished.value = true;
    }
}
