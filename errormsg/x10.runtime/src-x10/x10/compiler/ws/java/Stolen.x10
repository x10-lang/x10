package x10.compiler.ws.java;

import x10.compiler.Native;

public class Stolen extends RuntimeException {
    public static STOLEN = new Stolen();

    private def this() {}
}
