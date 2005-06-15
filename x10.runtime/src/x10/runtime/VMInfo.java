package x10.runtime;

/**
 * VMInfo records information about all JVMs participating in the
 * execution of the X10 program.  It is mainly used by the LAPI support
 * library.
 * 
 * @author Allan Kielstra
 */
public final class VMInfo {
    public static native void init(VMInfo[] VM_, x10.lang.Runtime r);
    public static native void term();
    public static VMInfo[] VM_;         // copied from Configuration.VM_
    public static int      THIS_IS_VM = 0;
    public String          hostName;
    public int             portNumber;
    public int             numberOfPlaces;
    public boolean         hasShutdownMsgBeenSent;
}
