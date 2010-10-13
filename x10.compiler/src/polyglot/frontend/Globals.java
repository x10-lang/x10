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
    
    private static Compiler Compiler() { return compiler.get(); }

    private static ExtensionInfo Extension() { return Compiler().sourceExtension(); }

    public static Options Options() { return Extension().getOptions(); }
}
