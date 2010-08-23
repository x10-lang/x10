package polyglot.frontend;

import polyglot.ast.NodeFactory;
import polyglot.main.Options;
import polyglot.types.TypeSystem;

public class Globals {
    /**
     * Thread-local Compiler object. To ensure the compiler is reentrant (and
     * thus can be embedded in Eclipse), this should be the only static field.
     */
    private static ThreadLocal<Compiler> compiler = new ThreadLocal<Compiler>();
    
    public static void initialize(Compiler c) {
        compiler.set(c);
    }
    
    public static Compiler Compiler() { return compiler.get(); }

    public static ExtensionInfo Extension() { return Compiler().sourceExtension(); }

    public static Options Options() { return Extension().getOptions(); }
    public static Stats Stats() { return Extension().getStats(); }

    public static TypeSystem TS() { return Extension().typeSystem(); }

    public static NodeFactory NF() { return Extension().nodeFactory(); }

    public static Scheduler Scheduler() { return Extension().scheduler(); }
    public static Job currentJob() { return Scheduler().currentJob(); }
    public static Goal currentGoal() { return Scheduler().currentGoal(); }
}
