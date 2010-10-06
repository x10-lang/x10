package x10.compiler.ws.java;

public abstract class FinishFrame extends Frame {
    public var asyncs:Int;

    public def this(up:Frame) {
        super(up);
        this.asyncs = 1;
    }
}
