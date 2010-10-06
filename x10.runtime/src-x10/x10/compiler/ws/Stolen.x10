package x10.compiler.ws;

import x10.compiler.Native;

public class Stolen extends RuntimeException {
    public static STOLEN = new Stolen();

    private def this() {}
}
