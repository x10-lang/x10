package x10.compiler;

/** 
 * A class that is used to implement continuations (bypass finally blocks).
 * 
 * NOT INTENDED FOR USE BY X10 PROGRAMMERS
 * 
 * @author tardieu
 */
public class Abort extends x10.lang.Throwable {
    public static ABORT = new Abort();

    private def this() {}
}
