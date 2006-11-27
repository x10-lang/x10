package polyglot.ext.x10.ast;

import polyglot.ast.Special;
import polyglot.ast.Special.Kind;

public interface X10Special extends Special {
    public static final Kind SELF = new Kind("self");
    boolean isSelf();
}
