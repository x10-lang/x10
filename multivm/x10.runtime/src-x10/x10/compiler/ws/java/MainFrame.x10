package x10.compiler.ws.java;

public abstract class MainFrame extends RegularFrame {
    public def this(up:Frame, ff:FinishFrame) {
        super(up, ff);
    }

    public abstract def fast(worker:Worker):Void;

    public def run() {
        Worker.main(this);
    }
}
